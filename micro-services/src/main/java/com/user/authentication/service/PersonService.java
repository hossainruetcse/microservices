package com.user.authentication.service;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.user.authentication.Iservice.IPersonService;
import com.user.authentication.dao.PersonDAO;
import com.user.authentication.entity.Person;
import com.user.authentication.security.JWTAuthorizationFilter;
import com.user.authentication.entity.JWTToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import static com.user.authentication.constant.SecurityConstants.*;

@Service
public class PersonService implements IPersonService {

	@Autowired
	PersonDAO personDao;
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Override
	public Person getByUsername(String username) {
		return personDao.findByUsername(username);
	}

	@Override
	public Person getByEmail(String email) {
		return personDao.findByEmail(email);
	}

	@Override
	public void save(Person person) {
		personDao.save(person);
	}

	@Override
	public void activate(Person person) {
		person.setActive(true);
		personDao.save(person);

	}

	@Override
	public void changePassword(Person person, String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changePassword(Person person, String newPassword) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean isEmailExist(String email) {
		return personDao.findByEmail(email)!=null;
	}

	@Override
	public boolean isUsernameExist(String username) {
		return personDao.findByUsername(username)!=null;
	}
	
	@Override
	public  JWTToken generateTokenAfterAuthentication(Person person) {
		checkAuthentication(person) ;
		return new JWTToken(buildToken(getByUsername(person.getUsername()), LOGIN_TOKEN_KEY, LOGIN_TOKEN_EXPIRATION_TIME), TOKEN_PREFIX);
	}
	
	@Override
	public Person getRequestedPerson(HttpServletRequest request) {
		String username=getRequestedUsername(request) ;
		Person person=getByUsername(username);
		return person;
	}
	@Override
	public String getRequestedUsername(HttpServletRequest request) {
		String token= JWTAuthorizationFilter.getRequestedToken(request);
		return JWTAuthorizationFilter.getUsernameByToken(token);
	}
	@Override
	public void checkAuthentication(Person person) {
		final Authentication authentication=authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(person.getUsername(), person.getPassword(), new ArrayList<>()));
	}
	
	private String buildToken(Person person, String key, long expirationTime) {
		return Jwts.builder().setSubject(person.getUsername()).setHeaderParam("User", person)
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime ))
				.signWith(SignatureAlgorithm.HS512, key.getBytes()).compact();
	}
	public Person testEmail(String email) {
		return personDao.findByEmail(email);
	}
	public Person testUsername(String username) {
		return personDao.findByUsername(username);
	}
	public String getActivationMailMessage(String baseUrl, String username, String key) {
		String message="Hi "+username+",\n\nYour account has created successfully. Now you need to activate your account to login. "
				+ "To activate your account please click on the link bellow.\n\n"+
				baseUrl+"/api/user/activate?username="+username+"&key="+key;
		return message;
	}

	@Override
	public void varifyEmail(Person person) {
		person.setEmailVarified(true);
		activate(person);
		
	}
}
