package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.App;
import com.levonke.OAuth.domain.OAuthToken;
import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.exception.AppCredentialsException;
import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.repository.OAuthTokenRepository;
import com.levonke.OAuth.web.model.OAuthTokenCodeRequest;
import com.levonke.OAuth.web.model.OAuthTokenInitialRequest;
import com.levonke.OAuth.web.model.OAuthTokenRefreshRequest;
import com.levonke.OAuth.web.model.OAuthTokenRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.List;

@Service
public class OAuthTokenServiceImpl implements OAuthTokenService {
	
	private final String accessTokenKey = "oauth";
	private final String refreshTokenKey = "topSecret";
	
	private final AppService appService;
	private final UserService userService;
	private final OAuthTokenRepository oAuthTokenRepository;
	
	@Autowired
	public OAuthTokenServiceImpl(AppService appService, UserService userService, OAuthTokenRepository oAuthTokenRepository) {
		this.appService = appService;
		this.userService = userService;
		this.oAuthTokenRepository = oAuthTokenRepository;
	}
	
	@Override
	public OAuthToken create(OAuthTokenInitialRequest oAuthTokenRequest) throws UserCredentialsException {
		App app = appService.findById(oAuthTokenRequest.getAppId());
		if (app == null) { throw new EntityNotFoundException("App '{" + oAuthTokenRequest.getAppId() + "}' not found"); }
		User user = userService.checkCredentials(oAuthTokenRequest.getUsername(), oAuthTokenRequest.getPassword());
		OAuthToken token = generateToken(app, user);
		return oAuthTokenRepository.save(token);
	}
	
	@Override
	public OAuthToken findByAppCode(OAuthTokenCodeRequest oAuthTokenCodeRequest) throws AppCredentialsException {
		App app = appService.findById(oAuthTokenCodeRequest.getAppId());
		if (!app.getSecret().equals(oAuthTokenCodeRequest.getAppSecret())) {
			throw new AppCredentialsException("App '{" + app.getId() + "}' secret not valid");
		}
		OAuthToken token = oAuthTokenRepository.findByAppCode(oAuthTokenCodeRequest.getCode())
				.orElseThrow(() -> new EntityNotFoundException("Token with code '{" + oAuthTokenCodeRequest.getCode() + "}' not found"));
		oAuthTokenRepository.delete(token);
		return token;
	}
	
	@Override
	public OAuthToken refresh(OAuthTokenRefreshRequest refreshRequest) throws TokenExpireException {
		App app = appService.findById(Integer.parseInt(refreshRequest.getRefreshToken().substring(
			refreshRequest.getRefreshToken().lastIndexOf(":") + 1)));
		Jws<Claims> jws = decryptAuthToken(refreshTokenKey, refreshRequest.getRefreshToken());
		if ((new Date()).after(jws.getBody().getExpiration())) {
			oAuthTokenRepository.deleteById(refreshRequest.getRefreshToken());
			throw new TokenExpireException("ExpirationDate");
		}
		User user = userService.findByUsername(jws.getBody().getSubject());
		return generateToken(app, user);
	}
	
	@Override
	public User checkAccessToken(OAuthTokenRequest tokenRequest) throws TokenExpireException, InvalidTokenException {
		OAuthToken token = oAuthTokenRepository.findById(tokenRequest.getAccessToken())
				.orElse(null);
		if (token != null) {
			throw new InvalidTokenException("Token '{" + tokenRequest.getAccessToken() + "}' in stop list");
		}
		Jws<Claims> jws = decryptAuthToken(accessTokenKey, tokenRequest.getAccessToken());
		if ((new Date()).after(jws.getBody().getExpiration())) {
			oAuthTokenRepository.deleteById(tokenRequest.getAccessToken());
			throw new TokenExpireException("ExpirationDate");
		}
		return userService.findByUsername(jws.getBody().getSubject());
	}
	
	@Override
	public void toStopList(OAuthTokenRequest tokenRequest) throws TokenExpireException, InvalidTokenException {
		String accessToken = tokenRequest.getAccessToken();
		App app = appService.findById(Integer.parseInt(accessToken.substring(accessToken.lastIndexOf(":") + 1)));
		User user = checkAccessToken(tokenRequest);
		Jws<Claims> jws = decryptAuthToken(accessTokenKey, accessToken);
		OAuthToken token = new OAuthToken()
			.setAccessToken(accessToken)
			.setAccessExpires(jws.getBody().getExpiration())
			.setApp(app)
			.setUser(user);
		oAuthTokenRepository.save(token);
	}
	
	@Override
	public void removeExpiredToken() {
		Date now = new Date();
		List<OAuthToken> expiredTokens = oAuthTokenRepository.findAllByAccessExpiresBeforeOrRefreshExpiresBefore(now, now);
		oAuthTokenRepository.deleteAll(expiredTokens);
	}
	
	private OAuthToken generateToken(App app, User user) {
		String accessTokenKey = app.getSecret() + this.accessTokenKey;
		String refreshTokenKey = app.getSecret() + this.refreshTokenKey;
		
		Date accessExpires = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
		Date refreshExpires = new Date(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L));
		
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
		
		String code = RandomStringUtils.randomAlphanumeric(10);
		
		return new OAuthToken()
			.setUser(user)
			.setApp(app)
			.setAppCode(code)
			.setAccessToken(accessToken.concat(":" + app.getId()))
			.setRefreshToken(refreshToken.concat(":" + app.getId()))
			.setAccessExpires(accessExpires)
			.setRefreshExpires(refreshExpires);
	}
	
	private Jws<Claims> decryptAuthToken(String key, String token) {
		Integer appId = Integer.parseInt(token.substring(token.lastIndexOf(":") + 1));
		String tokenKey = appService.findById(appId).getSecret() + key;
		return Jwts.parser()
			.setSigningKey(DatatypeConverter.printBase64Binary(tokenKey.getBytes()))
			.parseClaimsJws(token.substring(0, token.lastIndexOf(":")));
	}
}
