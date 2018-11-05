package com.user.authentication.service;

import static java.util.Collections.emptyList;

import org.dom4j.dom.DOMNodeHelper.EmptyNodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.authentication.dao.PersonDAO;
import com.user.authentication.entity.Person;

@Service
public class PersonDetailsService implements UserDetailsService {

	@Autowired
	PersonDAO personDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Person person =personDao.findByUsername(username);
		if(person==null || !person.isActive() || !person.isEmailVarified()) {
			throw new UsernameNotFoundException(username);
		}
		return new User(person.getUsername(),person.getPassword(),emptyList());
	}

}
