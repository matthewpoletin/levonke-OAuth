package com.levonke.OAuth.web;

import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.domain.UserToken;
import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.service.UserTokenService;
import com.levonke.OAuth.web.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserTokenController {
	
	private final UserTokenService userTokenService;
	
	@Autowired
	public UserTokenController(UserTokenService userTokenService) {
		this.userTokenService = userTokenService;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public UserTokenResponse createToken(@Valid @RequestBody UserTokenInitialRequest tokenRequest) throws UserCredentialsException {
		UserToken userToken = this.userTokenService.create(tokenRequest);
		return new UserTokenResponse(userToken);
	}
	
	@RequestMapping(value = "/token", method = RequestMethod.DELETE)
	public void deleteToken(@Valid @RequestBody UserTokenRequest userTokenRequest) {
		userTokenService.delete(userTokenRequest);
	}
	
	@RequestMapping(value = "/token/check", method = RequestMethod.POST)
	public UserResponse checkToken(@Valid @RequestBody UserTokenRequest userTokenRequest) throws TokenExpireException, InvalidTokenException {
		User user = this.userTokenService.checkAccessToken(userTokenRequest);
		return new UserResponse(user);
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/token/refresh", method = RequestMethod.POST)
	public UserTokenResponse refreshToken(@Valid @RequestBody UserTokenRefreshRequest refreshTokenRequest) throws TokenExpireException, InvalidTokenException {
		UserToken userToken = this.userTokenService.refresh(refreshTokenRequest);
		return new UserTokenResponse(userToken);
	}
}
