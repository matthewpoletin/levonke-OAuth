package com.levonke.OAuth.repository;

import com.levonke.OAuth.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends
		CrudRepository<User, Integer> {
	
	Optional<User> findUserByUsername(String username);
}
