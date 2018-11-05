package com.user.authentication.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="person_token")
public class ActivationToken {
	
	@Id
	private String id;
	private String emailKey;
	private String passwordKey;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id",nullable = false)
	private Person person;
	
	public ActivationToken() {
		setId(UUID.randomUUID().toString());
	}

	public ActivationToken(String emailKey, String passwordKey) {
		this();
		this.emailKey=emailKey;
		this.passwordKey=passwordKey;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmailKey() {
		return emailKey;
	}

	public void setEmailKey(String emailKey) {
		this.emailKey = emailKey;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	@Override
	public String toString() {
		return "ActivationToken [id=" + id + ", emailKey=" + emailKey + ", passwordKey=" + passwordKey + ", person="
				+ person + "]";
	}	
}
