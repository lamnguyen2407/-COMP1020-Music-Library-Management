package com.musicapp.model;

public class SessionManager {

	public static User currentUser;
	public static boolean isAdmin;
	
	// Call this method when user logs out 
	public static void clearSession() {
		currentUser = null;
		isAdmin = false;
	}
}
