package com.musicapp.service;

public class DatabaseManager {
    
    private static DatabaseManager instance;
    private FirebaseService firebaseService;

    private DatabaseManager() {
        this.firebaseService = new FirebaseServiceImpl();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public FirebaseService getService() {
        return this.firebaseService;
    }
}