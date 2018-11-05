package com.user.authentication.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {
	
	@RequestMapping("/home")
	public String getHome(){
		return "This is a test Home response............token is valid.";
	}

}
