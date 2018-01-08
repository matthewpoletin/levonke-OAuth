package com.levonke.OAuth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class ExpirationTimer {
	
	@Autowired
	public ExpirationTimer(OAuthTokenService oAuthTokenService, UserTokenService userTokenService) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run()
			{
				oAuthTokenService.removeExpiredToken();
				userTokenService.removeExpiredToken();
			}
		};
		timer.schedule( task, 0L, 60000 );
	}
	
}
