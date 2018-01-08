package com.levonke.OAuth.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "app")
public class App {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@Column(name = "secret")
	private String secret;
	
	@Column(name = "name", unique = true)
	private String name;
	
}
