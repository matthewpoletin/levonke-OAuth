package com.levonke.OAuth.repository;

import com.levonke.OAuth.domain.App;
import org.springframework.data.repository.CrudRepository;

public interface AppRepository extends
		CrudRepository<App, Integer> {
}
