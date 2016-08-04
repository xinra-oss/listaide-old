package com.xinra.listaide.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.User;
import com.xinra.listaide.entity.Session;
import com.xinra.listaide.entity.SessionRepository;

@Service
public class _SpotifyServiceImpl implements SpotifyService {
	
	@Autowired
	SessionRepository sessionRepo;
	
	@Autowired
	DTOFactory dtoFactory;
	
	private Api api;
	private User user;
	
	protected void validateSession() throws ServiceException {
		if(api == null) throw new ServiceException("No session has been attached to the SpotifyService!");
	}

	@Override
	public UserDTO attachSession(String sessionId) throws ServiceException {
		//load session
		Session session = sessionRepo.findOne(sessionId);
		if(session == null) throw new ServiceException("Invalid session id!");
		
		//Initialize API
		api = Api.builder()
				.accessToken(session.getAccessToken())
				.refreshToken(session.getRefreshToken())
				.build();
		
	    //Update the session
	    session.setLastUsed(new Date());
	    sessionRepo.save(session);
	    
	    //Retrieve user info
	    try {
			user = api.getMe().build().get();
		} catch (IOException | WebApiException e) {
			throw new ServiceException(e);
		}
	    
	    //return DTO
	    UserDTO dto = dtoFactory.createDTO(UserDTO.class);
	    dto.setId(user.getId());
	    return dto;
	}
}
