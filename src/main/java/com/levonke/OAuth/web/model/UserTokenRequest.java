package com.levonke.OAuth.web.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserTokenRequest {
	
    @NotEmpty(message = "Property accessToken is not set")
    String accessToken;
    
}
