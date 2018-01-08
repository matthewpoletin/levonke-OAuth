package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.App;
import com.levonke.OAuth.web.model.AppRequest;

public interface AppService {
	
	App findById(Integer id);
	
	App save(AppRequest appRequest);
	
}
