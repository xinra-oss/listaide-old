package com.xinra.listaide.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.NotModifiedException;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.User;
import com.xinra.listaide.entity.ListaideUser;
import com.xinra.listaide.entity.PlaylistRepository;
import com.xinra.listaide.entity.Session;
import com.xinra.listaide.entity.SessionRepository;
import com.xinra.listaide.entity.SpotifyAlbum;
import com.xinra.listaide.entity.SpotifyArtist;
import com.xinra.listaide.entity.SpotifyPlaylist;
import com.xinra.listaide.entity.SpotifyTrack;
import com.xinra.listaide.entity.SpotifyUser;
import com.xinra.listaide.entity.TrackRepository;
import com.xinra.listaide.entity.UserRepository;

@Service
public class _SpotifyServiceImpl implements SpotifyService {
	
	@Autowired
	SessionRepository sessionRepo;
	
	@Autowired
	PlaylistRepository playlistRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TrackRepository trackRepo;
	
	@Autowired
	DTOFactory dtoFactory;
	
	private Api api;
	private User user;
	private String playlistsEtag;
	
	protected void validateSession() throws ServiceException {
		if(api == null) throw new ServiceException("No session has been attached to the SpotifyService!");
	}

	@Override
	public UserDTO attachSession(String sessionId, Boolean login) throws ServiceException {
		//load session
		Session session = sessionRepo.findOne(sessionId);
		if(session == null) throw new ServiceException("Invalid session id!");
		
		//Initialize API
		api = Api.builder()
				.accessToken(session.getUser().getAccessToken())
				.refreshToken(session.getUser().getRefreshToken())
				.build();
		
		//Refresh access token
		//currently not working, see https://github.com/thelinmichael/spotify-web-api-java/issues/35
//		try {
//			RefreshAccessTokenCredentials creds = api.refreshAccessToken().build().get();
//			api.setAccessToken(creds.getAccessToken());
//			session.getUser().setAccessToken(creds.getAccessToken());
//		} catch (IOException | WebApiException e) {
//			throw new ServiceException(e);
//		}
		
		//If applicable, bump last login timestamp
		if(login) {
			session.getUser().setLastLogin(new Date());
		}
		
	    //Update the session
	    session.setLastUsed(new Date());
	    session = sessionRepo.save(session);
	    
	    //Retrieve user info
	    try {
			user = api.getMe().build().get();
		} catch (IOException | WebApiException e) {
			throw new ServiceException(e);
		}
	    
	    playlistsEtag = session.getUser().getPlaylistsEtag();
	    
	    //return DTO
	    UserDTO dto = dtoFactory.createDTO(UserDTO.class);
	    dto.setId(user.getId());
	    dto.setUrl(user.getExternalUrls().get("spotify"));
	    if(user.getImages().size() > 0) {
	    	dto.setImageUrl(user.getImages().get(0).getUrl());
	    }
	    return dto;
	}
	
	private void requireSession() throws ServiceException {
		if(api == null) throw new ServiceException("No session has been attached!");
	}

	@Override
	@Transactional
	public void synchronize() throws ServiceException {
		requireSession();
		
		try {
			synchronizePlaylists();
			synchronizeTracks();
		} catch (IOException | WebApiException e) {
			throw new ServiceException(e);
		} 
	}
	
	private void synchronizePlaylists() throws IOException, WebApiException {
		//fetch web API
		List<SimplePlaylist> playlists = new ArrayList<>();
		try {
			int offset = 0;
			while(true) {
				UserPlaylistsRequest.Builder builder = api.getPlaylistsForUser(user.getId())
						.offset(offset);
				if(playlistsEtag != null) builder.header("If-None-Match", playlistsEtag);
				
				Page<SimplePlaylist> page = builder.build().get();
				playlists.addAll(page.getItems());
				offset += page.getItems().size();
				if(offset >=  page.getTotal()) {
					//update etag after last iteration
					playlistsEtag = page.getEtag();
					break;
				}
			}
		} catch (NotModifiedException e) {
			return; //cache is still valid
		}
		
		//update etag in entity
		ListaideUser listaideUser = userRepo.findOne(user.getId());
		listaideUser.setPlaylistsEtag(playlistsEtag);
		final ListaideUser luser = userRepo.save(listaideUser);
		
		//update playlist cache
		List<SimplePlaylist> processed = new ArrayList<>(playlists.size());
		playlistRepo.findByUserId(user.getId()).forEach(p -> {
			try {
				SimplePlaylist current = playlists.stream()
						.filter(sp -> sp.getId().equals(p.getId()))
						.findFirst().get();
				//update
				toEntity(current, p);
				playlistRepo.save(p);
				processed.add(current);
			} catch(NoSuchElementException e) {
				//Playlist has been deleted
				playlistRepo.delete(p);
			}
		});
		
		//create cache for newly created playlists
		playlists.stream().filter(p -> !processed.contains(p)).forEach(p -> {
			SpotifyPlaylist playlist = toEntity(p, null);
			playlist.setUser(luser);
			playlistRepo.save(playlist);
		});
	}
	
	private void synchronizeTracks() throws IOException, WebApiException {
		for(SpotifyPlaylist playlist : playlistRepo.findByUserId(user.getId())) {
			List<PlaylistTrack> tracks = new ArrayList<>();
			try {
				int offset = 0;
				while(true) {
					PlaylistTracksRequest.Builder builder = api.getPlaylistTracks(user.getId(), playlist.getId())
							.offset(offset);
					if(playlist.getTracksEtag() != null) builder.header("If-None-Match", playlist.getTracksEtag());
					
					Page<PlaylistTrack> page = builder.build().get();
					tracks.addAll(page.getItems());
					offset += page.getItems().size();
					if(offset >=  page.getTotal()) {
						//update etag after last iteration
						playlist.setTracksEtag(page.getEtag());
						playlist = playlistRepo.save(playlist);
						break;
					}
				}
			} catch (NotModifiedException e) {
				return; //cache is still valid
			}
			
			//update track cache
			List<SpotifyTrack> processed = new ArrayList<>(tracks.size());
			int i = 1;
			for(PlaylistTrack track : tracks) {
				try {
					SpotifyTrack current = playlist.getTracks().stream()
							.filter(st -> st.getTrackId().equals(track.getTrack().getId())
									&& track.getAddedAt().equals(st.getAddedAt()) //other way round doesn't work...
									&& st.getAddedBy().getId().equals(track.getAddedBy().getId()))
							.findFirst().get();
					//If the same user added the same track at the same time, only the first occurrence
					//is updated, subsequent ones are dropped and recreated
					if(processed.contains(current)) throw new NoSuchElementException();
					//Don't actually update the entity, only update ordering
					current.setNumber(i);
					processed.add(current);
				} catch(NoSuchElementException e) {
					//there is no entity for this track -> create one
					SpotifyTrack newTrack = toEntity(track, null);
					newTrack.setNumber(i);
					trackRepo.save(newTrack);
					playlist.getTracks().add(newTrack);
					processed.add(newTrack);
				}
				i++;
			}
			
			//remove deleted tracks from cache
			//playlist.setTracks(processed); doesn't work because Hibernate's 
			//orphan-removal wouldn't recognize this correctly
			playlist.getTracks().removeIf(t -> !processed.contains(t));
			
			playlistRepo.save(playlist);
		}
	}
	
	private SpotifyPlaylist toEntity(SimplePlaylist source, SpotifyPlaylist target) {
		if(target == null) {
			target = new SpotifyPlaylist();
			target.setTracks(new HashSet<>());
		}
		target.setId(source.getId());
		target.setCollaborative(source.isCollaborative());
		target.setName(source.getName());
		target.setPublicAccess(source.isPublicAccess());
		target.setUrl(source.getExternalUrls().get("spotify"));
		target.setImageUrl(source.getImages().get(0).getUrl());
		if(target.getOwner() == null) target.setOwner(new SpotifyUser());
		target.getOwner().setUrl(source.getOwner().getExternalUrls().get("spotify"));
		if(source.getOwner().getImages() != null && !source.getOwner().getImages().isEmpty()) {
			target.getOwner().setImageUrl(source.getOwner().getImages().get(0).getUrl());
		}
		return target;
	}
	
	private SpotifyTrack toEntity(PlaylistTrack source, SpotifyTrack target) {
		if(target == null) target = new SpotifyTrack();
		target.setAddedAt(source.getAddedAt());
		if(target.getAddedBy() == null) target.setAddedBy(new SpotifyUser());
		target.getAddedBy().setId(source.getAddedBy().getId());
		target.getAddedBy().setUrl(source.getAddedBy().getExternalUrls().get("spotify"));
		if(source.getAddedBy().getImages() != null) {
			target.getAddedBy().setImageUrl(source.getAddedBy().getImages().get(0).getUrl());
		}
		target.setTrackId(source.getTrack().getId());
		if(target.getAlbum() == null) target.setAlbum(new SpotifyAlbum());
		target.getAlbum().setName(source.getTrack().getAlbum().getName());
		target.getAlbum().setUrl(source.getTrack().getAlbum().getExternalUrls().get("spotify"));
		if(target.getArtists() == null) target.setArtists(new HashSet<>());
		else target.getArtists().clear();
		int i = 1;
		for(SimpleArtist a : source.getTrack().getArtists()) {
			SpotifyArtist artist = new SpotifyArtist();
			artist.setName(a.getName());
			artist.setUrl(a.getExternalUrls().get("spotify"));
			artist.setNumber(i);
			target.getArtists().add(artist);
			i++;
		}
		target.setDuration(source.getTrack().getDuration());
		target.setName(source.getTrack().getName());
		return target;
	}
}
