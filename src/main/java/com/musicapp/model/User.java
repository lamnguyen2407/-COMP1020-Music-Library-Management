package com.musicapp.model;

public abstract class User {
	private String userId;
	private String email;
	private String name;
	private String password;
	
	public User() {};

	public User(String userId, String email, String name, String password) {
		this.userId = userId;
		this.email = email;
		this.name = name;
		this.password = password;
	}

	public String getUserId() {
		return this.userId;
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
	
	public void setUserId(String userId) { 
		this.userId = userId; 
	}
	
    public void setEmail(String email) { 
    	this.email = email; 
    }
    
    public void setName(String name) { 
    	this.name = name; 
    }
    
    public void setPassword(String password) { 
    	this.password = password; 
    }
}
