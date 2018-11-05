package com.user.authentication.controller;



import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.email.service.MailSendingService;
import com.user.authentication.Iservice.IPersonService;
import com.user.authentication.entity.Person;
import com.user.authentication.service.PersonService;




@RestController
@RequestMapping("/api/user")
@Configuration
@PropertySource("file:./src/main/resources/error/messages/message.properties")
public class UserController {

	@Autowired
	IPersonService personService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
//	@Autowired
//	private MailSendingService mailSendingService;
	
	@Value("${email.exits}")
	private String emailExistMessage;
	@Value("${username.exits}")
	private String usernameExistMessage;
	@Value("${wrong.userNmae.password}")
	private String wrongUsernameOrPasswordMessage;
	@Value("${confirm.password.error}")
	private String confirmPasswordDoesNotMatchMessage;
	@Value("${wrong.password}")
	private String passwordWrongMessage;
	@Value("${password.changed.successful}")
	private String passwordChangeSuccessfulMessage;
	@Value("${server.baseUrl}")
	private String baseUrl;
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public ResponseEntity  signUp(HttpServletRequest request,@RequestBody Person person) {
		if(personService.isEmailExist(person.getEmail())) {
			
			return ResponseEntity.badRequest().body(emailExistMessage);
		}
		if(personService.isUsernameExist(person.getUsername())) {
			return ResponseEntity.badRequest().body(usernameExistMessage);
		}
		person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
		String emailKey=UUID.randomUUID().toString()+UUID.randomUUID().toString();
		person.getActivationToken().setEmailKey(emailKey);
		System.out.println(person.getActivationToken());
		personService.save(person);
		String emailBody=((PersonService) personService).getActivationMailMessage(baseUrl, person.getUsername(), emailKey);
		MailSendingService mailSendingService= new MailSendingService();
		mailSendingService.send("Account activation confirm.", person.getEmail(), emailBody);
		return ResponseEntity.ok(person);
	}
	
	@RequestMapping(value="/signin", method=RequestMethod.POST)
	public ResponseEntity signIn(@RequestBody Person person) {
		try {
			return ResponseEntity.ok(personService.generateTokenAfterAuthentication(person));
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(wrongUsernameOrPasswordMessage);
		}	
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public ResponseEntity getProfile(HttpServletRequest request) {
		Person person=null;
		try {
			person=personService.getRequestedPerson(request);
		}catch(Exception e) {
			ResponseEntity.badRequest();
		}
		return ResponseEntity.ok(person);
	}

	@RequestMapping(value="/edit/profile", method=RequestMethod.POST)
	public ResponseEntity editProfile(HttpServletRequest request, Person requestedPerson) {
		Person person=null;
		try {
			person=personService.getRequestedPerson(request);
			if(requestedPerson.getFirstName()!=null && requestedPerson.getFirstName().length()>0) {
				person.setFirstName(requestedPerson.getFirstName());
			}
			if(requestedPerson.getLastName()!=null && requestedPerson.getLastName().length()>0) {
				person.setLastName(requestedPerson.getLastName());
			}
			personService.save(person);
		}catch(Exception e) {
			ResponseEntity.badRequest();
		}
		return ResponseEntity.ok(person);
	}
	
	@RequestMapping(value = "/activate", method = RequestMethod.GET)
	public ResponseEntity activateAccount(@RequestParam String username, @RequestParam String key) {
		try {
			Person person = personService.getByUsername(username);
			if (key != null && key.equals(person.getActivationToken().getEmailKey())) {
				person.getActivationToken().setEmailKey(null);
				personService.varifyEmail(person);
				return ResponseEntity.ok("Account activated successfully.");
			} else {
				return ResponseEntity.badRequest().body("The key is not valid.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("The key or the username is not valid.");
		}
	}
	
	@RequestMapping(value="/changepassword", method=RequestMethod.POST)
	public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody  Map<String, String> jsonObject){
		String oldPassword= jsonObject.get("oldPassword");
		String newPassword=jsonObject.get("newPassword");
		String confirmNewPassword=jsonObject.get("confirmNewPassword");
		if(!newPassword.equals(confirmNewPassword)) {
			return ResponseEntity.badRequest().body(confirmPasswordDoesNotMatchMessage);
		}
		String username=personService.getRequestedUsername(request);
		try {
			personService.checkAuthentication(new Person(username,oldPassword));
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(passwordWrongMessage);
		}
		Person person=personService.getByUsername(username);
		person.setPassword(bCryptPasswordEncoder.encode(newPassword));
		personService.save(person);
		return ResponseEntity.ok(passwordChangeSuccessfulMessage);
	}
	
	@RequestMapping(value="/forgotpassword", method=RequestMethod.POST)
	public ResponseEntity forgotPassword(@RequestBody Person requestedPerson) {
		if (!personService.isEmailExist(requestedPerson.getEmail())) {
			return ResponseEntity.badRequest().body(emailExistMessage);
		}
		Person person=personService.getByEmail(requestedPerson.getEmail());
		String passwordKey=UUID.randomUUID().toString()+UUID.randomUUID().toString();
		person.getActivationToken().setPasswordKey(passwordKey);
		MailSendingService mailSendingService= new MailSendingService();
		//######## send mail..................
		return ResponseEntity.ok("A link has been sent to your email. Please check your mail to change password.");
	}
	
	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public ResponseEntity resetPassword( @RequestBody  Map<String, String> jsonObject) {
		String email= jsonObject.get("email");
		String key=jsonObject.get("key");
		String password=jsonObject.get("password");
		Person person = personService.getByEmail(email);
		if (key != null && person != null && key.equals(person.getActivationToken().getPasswordKey())) {
			person.setPassword(bCryptPasswordEncoder.encode(password));
			person.getActivationToken().setPasswordKey(null);
			return ResponseEntity.ok(passwordChangeSuccessfulMessage);
		} else {
			return ResponseEntity.badRequest().body("Password reset unsuccessfull.");
		}
	}
}
