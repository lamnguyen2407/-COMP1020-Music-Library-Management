package com.musicapp.model;

public class Admin extends User {
	
	public Admin() {};

	public Admin(String userId, String email, String name, String password) {
		super(userId, email, name, password);
	}

}
