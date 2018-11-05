package com.user.authentication.entity;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="person")
public class Person  {
	
	@Id
	private String id;
	private String username;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String displayName;
	private boolean isActive;
	private boolean isEmailVarified;
	@OneToOne(mappedBy = "person", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private ActivationToken activationToken;


	public Person() {
		setId(UUID.randomUUID().toString());
		setActivationToken(new ActivationToken());
		setFirstName("");
		setLastName("");
		setActive(false);
		setEmailVarified(false);
	}
	public Person(String username, String password) {
		this();
		this.username=username;
		this.password=password;
	}
	public String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		setDisplayName();
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
		setDisplayName();
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName() {
		this.displayName = this.firstName+" "+this.lastName;
	}
	
	 @JsonIgnore
	 @JsonProperty(value = "active")
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	 @JsonIgnore
	 @JsonProperty(value = "password")
	public String getPassword() {
		return password;
	}

	 @JsonIgnore
	 @JsonProperty(value = "emailVarified")
	public boolean isEmailVarified() {
		return isEmailVarified;
	}

	public void setEmailVarified(boolean isEmailVarified) {
		this.isEmailVarified = isEmailVarified;
	}
	
	public void setActivationToken(ActivationToken activationToken) {
		this.activationToken = activationToken;
		this.activationToken.setPerson(this);
	}
	 @JsonIgnore
	 @JsonProperty(value = "activationToken")
	public ActivationToken getActivationToken() {
		return activationToken;
	}
	@Override
	public String toString() {
		return "[Id : "+this.id+", name : "+this.displayName+", Email : "+this.email+", isActive : "+this.isActive+"]";
	}
	
}
