package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.OAuthToken;
import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.exception.AppCredentialsException;
import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.web.model.OAuthTokenCodeRequest;
import com.levonke.OAuth.web.model.OAuthTokenInitialRequest;
import com.levonke.OAuth.web.model.OAuthTokenRefreshRequest;
import com.levonke.OAuth.web.model.OAuthTokenRequest;

public interface OAuthTokenService {
	
	OAuthToken create(OAuthTokenInitialRequest oAuthTokenRequest) throws UserCredentialsException;
	
	OAuthToken findByAppCode(OAuthTokenCodeRequest oAuthTokenCodeRequest) throws AppCredentialsException;
	
	OAuthToken refresh(OAuthTokenRefreshRequest authTokenRefreshRequest) throws Exception;
	
	User checkAccessToken(OAuthTokenRequest oAuthTokenRequest) throws TokenExpireException, InvalidTokenException;
	
	void toStopList(OAuthTokenRequest oAuthTokenRequest) throws TokenExpireException, InvalidTokenException;
	
	void removeExpiredToken();
}
