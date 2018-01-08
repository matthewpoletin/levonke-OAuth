package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.domain.UserToken;
import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.repository.UserTokenRepository;
import com.levonke.OAuth.web.model.UserTokenInitialRequest;
import com.levonke.OAuth.web.model.UserTokenRefreshRequest;
import com.levonke.OAuth.web.model.UserTokenRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.List;

@Service
public class UserTokenServiceImpl implements UserTokenService {
	
	private final String accessTokenKey = "race2earn";
	private final String refreshTokenKey = "topSecret";
	
	private final UserService userService;
	private final UserTokenRepository userTokenRepository;
	
	@Autowired
	public UserTokenServiceImpl(UserService userService, UserTokenRepository userTokenRepository) {
		this.userService = userService;
		this.userTokenRepository = userTokenRepository;
	}
	
	@Override
	public UserToken create(UserTokenInitialRequest userTokenInitialRequest) throws UserCredentialsException {
		User user = userService.checkCredentials(userTokenInitialRequest.getUsername(), userTokenInitialRequest.getPassword());
		if (user != null) {
			UserToken userToken = generateToken(user);
			return userTokenRepository.save(userToken);
		} else {
			throw new UserCredentialsException("User credentials not valid");
		}
	}
	
	@Override
	public UserToken refresh(UserTokenRefreshRequest userTokenRefreshRequest) throws TokenExpireException, InvalidTokenException {
		Jws<Claims> jws = Jwts.parser()
			.setSigningKey(DatatypeConverter.printBase64Binary(refreshTokenKey.getBytes()))
			.parseClaimsJws(userTokenRefreshRequest.getRefreshToken());
		UserToken userTokenByRefresh = userTokenRepository.findByRefreshToken(userTokenRefreshRequest.getRefreshToken())
			.orElseThrow(() -> new InvalidTokenException("Refresh token '{" + userTokenRefreshRequest.getRefreshToken() + "}' not found"));
		userTokenRepository.delete(userTokenByRefresh);
		if ((new Date()).after(jws.getBody().getExpiration())) {
			throw new TokenExpireException("ExpirationDate");
		}
		User user = userService.findByUsername(jws.getBody().getSubject());
		UserToken userToken = generateToken(user);
		return userTokenRepository.save(userToken);
	}
	
	@Override
	public User checkAccessToken(UserTokenRequest userTokenRequest) throws TokenExpireException, InvalidTokenException {
		UserToken userToken = userTokenRepository.findById(userTokenRequest.getAccessToken())
				.orElseThrow(() -> new InvalidTokenException("Access token '{" + userTokenRequest.getAccessToken() + "}' not found"));
		Jws<Claims> jws = Jwts.parser()
				.setSigningKey(DatatypeConverter.printBase64Binary(accessTokenKey.getBytes()))
				.parseClaimsJws(userTokenRequest.getAccessToken());
		if ((new Date()).after(jws.getBody().getExpiration())) {
			throw new TokenExpireException("ExpirationDate");
		}
		return userService.findByUsername(jws.getBody().getSubject());
	}
	
	@Override
	public void delete(UserTokenRequest userTokenDeleteRequest) {
		UserToken token = userTokenRepository.findById(userTokenDeleteRequest.getAccessToken())
				.orElseThrow(() -> new EntityNotFoundException("UserToken '{" + userTokenDeleteRequest.getAccessToken() + "}' not found"));
		userTokenRepository.delete(token);
	}
	
	@Override
	public void removeExpiredToken() {
		Date now = new Date();
		List<UserToken> expiredTokens = userTokenRepository.findAllByAccessExpiresBeforeAndRefreshExpiresBefore(now, now);
		userTokenRepository.deleteAll(expiredTokens);
	}
	
	private UserToken generateToken(User user) {
		Date accessExpires = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000L));
		Date refreshExpires = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L));
		
		String accessToken = Jwts.builder()
			.setSubject(user.getUsername())
			.setExpiration(accessExpires)
			.signWith(SignatureAlgorithm.HS512, DatatypeConverter.printBase64Binary(accessTokenKey.getBytes()))
			.compact();
		String refreshToken = Jwts.builder()
			.setSubject(user.getUsername())
			.setExpiration(refreshExpires)
			.signWith(SignatureAlgorithm.HS512, DatatypeConverter.printBase64Binary(refreshTokenKey.getBytes()))
			.compact();
		
		return new UserToken()
			.setUser(user)
			.setAccessToken(accessToken)
			.setRefreshToken(refreshToken)
			.setAccessExpires(accessExpires)
			.setRefreshExpires(refreshExpires);
	}
	
}
