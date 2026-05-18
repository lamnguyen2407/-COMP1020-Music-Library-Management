package com.musicapp.model;

public class Admin extends User {
    
    public Admin() {
        super(); 
        this.setRole("admin");
    }

    public Admin(String userId, String email, String name, String password) {
        super(userId, email, name, password);
        this.setRole("admin");
    }
}