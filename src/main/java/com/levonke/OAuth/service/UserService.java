package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.exception.UserCredentialsException;
import com.levonke.OAuth.web.model.UserRequest;

public interface UserService {
	
	User findByUsername(String username);
	
	User checkCredentials(String username, String password) throws UserCredentialsException;
	
	User save(UserRequest userRequest) throws Exception;
	
	void delete(Integer id);
}
