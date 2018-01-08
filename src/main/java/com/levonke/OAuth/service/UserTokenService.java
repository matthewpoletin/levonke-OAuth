package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.domain.UserToken;
import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.web.model.UserTokenInitialRequest;
import com.levonke.OAuth.web.model.UserTokenRefreshRequest;
import com.levonke.OAuth.web.model.UserTokenRequest;

public interface UserTokenService {
	
	UserToken create(UserTokenInitialRequest userTokenInitialRequest) throws UserCredentialsException;
	
	UserToken refresh(UserTokenRefreshRequest userTokenRefreshRequest) throws TokenExpireException, InvalidTokenException;
	
	User checkAccessToken(UserTokenRequest userTokenRequest) throws TokenExpireException, InvalidTokenException;
	
	void delete(UserTokenRequest userTokenRequest);
	
	void removeExpiredToken();
}
