package com.levonke.OAuth.web;

import com.levonke.OAuth.domain.User;
import com.levonke.OAuth.service.UserService;
import com.levonke.OAuth.web.model.UserRequest;
import com.levonke.OAuth.web.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	private final UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public UserResponse createUser(@Valid @RequestBody UserRequest userRequest, HttpServletResponse response) throws Exception {
		User user = this.userService.save(userRequest);
		response.addHeader(HttpHeaders.LOCATION, "/api/auth/user/" + user.getId());
		return new UserResponse(user);
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable Integer id) {
		this.userService.delete(id);
	}
	
}
