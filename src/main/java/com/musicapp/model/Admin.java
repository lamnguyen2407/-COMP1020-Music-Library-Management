package com.musicapp.model;

public class Admin extends User {
	private String adminRole;

	public Admin(String userId, String email, String name, String password, String adminRole) {
		super(userId, email, name, password);
		this.adminRole = adminRole;
	}

	public String getAdminRole() {
		return adminRole;
	}
}
