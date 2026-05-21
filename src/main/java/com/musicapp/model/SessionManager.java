package com.musicapp.model;

public class SessionManager {
    public static User currentUser;
    public static boolean isAdmin;
    
    public static void clearSession() {
        currentUser = null;
        isAdmin = false;
    }
}