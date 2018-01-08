package com.levonke.OAuth.web.model;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class AppRequest {
	
	@NotEmpty(message = "Property name is not set")
	private String name;
	
}
