package com.bridgelabz.userregistration.model;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Entity
@Table(name="user_registration")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userid;
	private String FirstName;
	private String LastName;
	private String DataOfBirth;
	private String Email;
	private String Password;
	private LocalDateTime RegisteredDate;
	private LocalDateTime UpdatedDate;
	@Column(name ="is_verify_email ", columnDefinition = "boolean default false")
	private boolean verifyEmail;
	
}


