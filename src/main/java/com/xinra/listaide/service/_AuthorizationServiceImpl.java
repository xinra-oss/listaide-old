package com.xinra.listaide.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.xinra.listaide.entity.Session;
import com.xinra.listaide.entity.SessionRepository;

@Service
public class _AuthorizationServiceImpl implements AuthorizationService {

	@Value("${listaide.spotify.clientid}")
	private String clientId;
	
	@Value("${listaide.spotify.secret}")
	private String clientSecret;
	
	@Autowired
	private DTOFactory dtoFactory;
	
	@Autowired
	private SessionRepository sessionRepo;
	
	@Override
	public AuthorizationDTO beginAuthorization(String redirectUri) {
		
		//Generate a random session id that is not used yet.
		String sessionId = null;
		do {
			sessionId = BCrypt.gensalt();
		} while(sessionRepo.findOne(sessionId) != null);
		
		//Hash the session id to get the state
		String state = BCrypt.hashpw(sessionId, BCrypt.gensalt());
		
		//Generate the authorization URL using the Spotify API wrapper
		Api api = Api.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.redirectURI(redirectUri)
				.build();
		
		//All required scopes
		List<String> scopes = Arrays.asList(
				"playlist-read-private", 
				"playlist-read-collaborative",
				"playlist-modify-public",
				"playlist-modify-private"
		);
		
		String url = api.createAuthorizeURL(scopes, state);
		
		//Write data into a DTO
		AuthorizationDTO dto = dtoFactory.createDTO(AuthorizationDTO.class);
		dto.setSessionId(sessionId);
		dto.setUrl(url);
		
		return dto;
	}

	@Override
	public void completeAuthorization(String sessionId, String state, String code, String redirectUri) throws ServiceException {
		//Validate the state
		try {
			if(!BCrypt.checkpw(sessionId, state)) {
				throw new Exception();
			}
		} catch(Exception e) {
			throw new ServiceException("Invalid State");
		}
		
		//Obtain OAuth tokens using the Spotify API wrapper
		Api api = Api.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.redirectURI(redirectUri) //otherwise throws NPE, no idea why
				.build();
		
		try {
			AuthorizationCodeCredentials creds = api.authorizationCodeGrant(code).build().get();
			
			//Save session to database
			Session session = new Session(sessionId, creds.getAccessToken(), creds.getRefreshToken());
			sessionRepo.save(session);
		} catch (IOException | WebApiException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void destroySession(String sessionId) {
		try {
			sessionRepo.delete(sessionId);
		} catch (EmptyResultDataAccessException e) {
			//ignore if session id does not exist
		}
	}

}
