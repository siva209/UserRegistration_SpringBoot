package com.bridgelabz.userregistration.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgelabz.userregistration.dto.ForgotPwdDto;
import com.bridgelabz.userregistration.dto.LoginDto;
import com.bridgelabz.userregistration.dto.UserDto;
import com.bridgelabz.userregistration.model.UserEntity;



@Service
public interface UserService {
	public UserEntity registerUser(UserDto dto);
	public List<UserEntity> getall();
	public boolean isEmailExists(String email);
	public UserEntity verify(String token);
	public UserEntity loginUser(LoginDto dto);
	public UserEntity getUserByEmail(String email);
    public UserEntity getUserById(long userId);
    public String forgotPwd(ForgotPwdDto forgotdto);
}



