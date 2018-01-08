package com.levonke.OAuth.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserCredentialsException extends Exception {
	
	public UserCredentialsException(String message) {
		super(message);
	}
	
}
