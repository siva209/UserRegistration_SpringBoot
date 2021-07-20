package com.bridgelabz.userregistration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.userregistration.dto.ForgotPwdDto;
import com.bridgelabz.userregistration.dto.LoginDto;
import com.bridgelabz.userregistration.dto.UserDto;
import com.bridgelabz.userregistration.exception.CustomException;
import com.bridgelabz.userregistration.jwtoperations.Jms;
import com.bridgelabz.userregistration.jwtoperations.JwtOperations;
import com.bridgelabz.userregistration.model.UserEntity;
import com.bridgelabz.userregistration.repository.UserRepository;
@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userrepo;
	@Autowired
	private BCryptPasswordEncoder pwdencoder;
	
	@Autowired
	private JwtOperations jwt=new JwtOperations();
	@Autowired
	private Jms jms;
	@Autowired
	private ModelMapper modelmapper;

	
	@Override
	public UserEntity registerUser(UserDto dto) {
		
		isEmailExists(dto.getEmail());
		//UserEntity entity=new UserEntity();
//		BeanUtils.copyProperties(dto, entity);
		UserEntity entity=modelmapper.map(dto, UserEntity.class);
		entity.setRegisteredDate(LocalDateTime.now());
		entity.setUpdatedDate(LocalDateTime.now());
	   // entity.setPassword(pwdencoder.encode(entity.getPassword()));
		entity.setPassword(pwdencoder.encode(entity.getPassword()));
		userrepo.save(entity);
		String body="http://localhost:8080/verifyemail/"+jwt.jwtToken(entity.getUserid());
		System.out.println(body);
		jms.sendEmail(entity.getEmail(),"verification email",body);
		return entity;
	}
	
	@Override
	public boolean isEmailExists(String email) {
		if(userrepo.isEmailExists(email).isPresent())
			throw new CustomException("email already exists",HttpStatus.OK,null,"false");
		return false;
	}

	@Override
	public UserEntity verify(String token) {
		long id=jwt.parseJWT(token);
		UserEntity user=userrepo.isIdExists(id).orElseThrow(() -> new CustomException("user not exists",HttpStatus.OK,id,"false"));
		user.setVerifyEmail(true);
		userrepo.save(user);
		return user;
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		System.out.println(email);
		UserEntity user=userrepo.getUserByEmail(email).orElseThrow(() -> new CustomException("email not exists",HttpStatus.OK,null,"false"));
		return user;
	}


	@Override
	public UserEntity loginUser(LoginDto dto) {
		UserEntity  user=userrepo.getUserByEmail(dto.getEmail()).orElseThrow(() -> new CustomException("login failed",HttpStatus.OK,null,"false"));
		boolean ispwd=pwdencoder.matches(dto.getPassword(),user.getPassword());
		if(ispwd==false) {
			throw new CustomException("login failed",HttpStatus.OK,null,"false");
		} else {
			String token=jwt.jwtToken(user.getUserid());
			return user;
			
		}
	}
	public UserEntity getUser(String token) {
		long id=jwt.parseJWT(token);
		return userrepo.getUserById(id).orElseThrow(() -> new CustomException("user not exists",HttpStatus.OK,id,"false"));
	}
	@Override
	public List<UserEntity> getall() {
		return userrepo.getAllUsers().orElseThrow(() -> new CustomException("no users present", HttpStatus.OK,null,"false"));
	}

	@Override
	public UserEntity getUserById(long userId) {
		return userrepo.getUserById(userId).orElseThrow(() -> new CustomException("user not exists",HttpStatus.OK,userId,"false"));
	}

	@Override
	public String forgotPwd(ForgotPwdDto forgotdto) {
		UserEntity user=getUserByEmail(forgotdto.getEmail());
		String body="http://localhost:4200/resetpassword/"+jwt.jwtToken(user.getUserid());
		jms.sendEmail(user.getEmail(),"Reset Password",body);
		return "success";
	}
	}
