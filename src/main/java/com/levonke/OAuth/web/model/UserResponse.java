package com.levonke.OAuth.web.model;

import com.levonke.OAuth.domain.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
	
	private Integer id;
	
	private String username;
	
	public UserResponse(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
	}
	
}
