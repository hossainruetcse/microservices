package com.user.authentication.Iservice;

import javax.servlet.http.HttpServletRequest;

import com.user.authentication.entity.JWTToken;
import com.user.authentication.entity.Person;

public interface IPersonService {
	public Person getByUsername(String username);
	public Person getByEmail(String email);
	public void save(Person person);
	public void activate(Person person);
	public void varifyEmail(Person person);
	public boolean isEmailExist(String email);
	public boolean isUsernameExist(String username);
	public void changePassword(Person person, String oldPassword, String newPassword);
	public void changePassword(Person person, String newPassword);
	public JWTToken generateTokenAfterAuthentication(Person person);
	public Person getRequestedPerson(HttpServletRequest request);
	public void checkAuthentication(Person person) ;
	public String getRequestedUsername(HttpServletRequest request);
}
