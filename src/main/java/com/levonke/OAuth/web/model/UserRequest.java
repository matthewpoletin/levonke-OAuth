package com.levonke.OAuth.web.model;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UserRequest {
	
	@NotNull(message = "Property id is not set")
	@Positive(message = "Property id must be positive value")
	Integer id;
	
	@NotEmpty(message = "Property username is not set")
	String username;
	
	@NotEmpty(message = "Property password is not set")
	String password;
	
}
