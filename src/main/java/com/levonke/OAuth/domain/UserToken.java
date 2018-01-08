package com.levonke.OAuth.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "user_token")
public class UserToken {
	
	@Id
	@Column(name = "access_token")
	private String accessToken;
	
	@Column(name ="access_expires")
	private Date accessExpires;
	
	@Column(name ="refresh_token", unique = true)
	private String refreshToken;
	
	@Column(name = "refresh_expires")
	private Date refreshExpires;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_sql")
	private User user;
	
}
