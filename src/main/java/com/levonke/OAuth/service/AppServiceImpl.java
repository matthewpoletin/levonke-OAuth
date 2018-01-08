package com.levonke.OAuth.service;

import com.levonke.OAuth.domain.App;
import com.levonke.OAuth.repository.AppRepository;
import com.levonke.OAuth.web.model.AppRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

@Service
public class AppServiceImpl implements AppService {
	
	private final AppRepository appRepository;
	
	@Autowired
	public AppServiceImpl(AppRepository appRepository) {
		this.appRepository = appRepository;
	}
	
	@Override
	public App findById(Integer id) {
		return appRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("App '{ " + id + " }' not found"));
	}
	
	@Override
	public App save(AppRequest appRequest) {
		App app = new App()
			.setName(appRequest.getName())
			.setSecret(RandomStringUtils.randomAlphanumeric(50));
		return appRepository.save(app);
	}
}
