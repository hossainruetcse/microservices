package com.user.authentication.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.authentication.entity.Person;

public interface PersonDAO extends JpaRepository<Person, String> {
	Person findByUsername(String username);
	Person findByEmail(String email);

}
