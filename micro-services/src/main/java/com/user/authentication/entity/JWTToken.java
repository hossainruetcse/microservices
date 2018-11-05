package com.user.authentication.entity;

public class JWTToken {
	private String key;
	private String tokenType;

	public JWTToken(String key, String tokenType) {
		this.key = key;
		this.tokenType = tokenType;
	}

	public String getKey() {
		return key;
	}

	public String getTokenType() {
		return tokenType;
	}

}
