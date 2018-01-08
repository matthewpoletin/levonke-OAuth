package com.levonke.OAuth.web;

import com.levonke.OAuth.exception.InvalidTokenException;
import com.levonke.OAuth.exception.TokenExpireException;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.web.model.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntityNotFoundException.class)
	public ErrorResponse notFound(EntityNotFoundException exception) {
		logger.error("EntityNotFound exception:" + exception.getMessage());
		return new ErrorResponse("EntityNotFound", exception.getMessage());
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UserCredentialsException.class)
	public ErrorResponse userCredentials(UserCredentialsException exception) {
		logger.error("UserCredentialsException exception:" + exception.getMessage());
		return new ErrorResponse("UserCredentialsException", exception.getMessage());
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(TokenExpireException.class)
	public ErrorResponse tokenExpire(TokenExpireException exception) {
		logger.error("TokenExpireException exception:" + exception.getMessage());
		return new ErrorResponse("TokenExpireException", exception.getMessage());
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(InvalidTokenException.class)
	public ErrorResponse invalidToken(InvalidTokenException exception) {
		logger.error("InvalidUserToken exception:" + exception.getMessage());
		return new ErrorResponse("InvalidUserToken ", exception.getMessage());
	}
	
}
