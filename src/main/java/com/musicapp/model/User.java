package com.musicapp.model;

public abstract class User {
	private String userId;
	private String email;
	private String name;
	private String password;

	public User(String userId, String email, String name, String password) {
		this.userId = userId;
		this.email = email;
		this.name = name;
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}
