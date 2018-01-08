package com.levonke.OAuth.repository;

import com.levonke.OAuth.domain.OAuthToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OAuthTokenRepository extends
		CrudRepository<OAuthToken, String> {
	
	Optional<OAuthToken> findByAppCode(String appCode);
	
	List<OAuthToken> findAllByAccessExpiresBeforeOrRefreshExpiresBefore(Date accessExpired, Date refreshExpired);
}
