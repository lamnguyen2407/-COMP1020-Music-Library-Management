package com.musicapp.service;
import com.musicapp.model.Song;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.*;
public class FirebaseServiceImpl implements FirebaseService{

    private DatabaseReference dbRef;

    public FirebaseServiceImpl() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-config.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://music-library-management-59ce4-default-rtdb.firebaseio.com/") 
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            dbRef = FirebaseDatabase.getInstance().getReference();
            System.out.println("Connected database successfully");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    @Override
    public List<Song> fetchSongs() {
    	List<Song> songList = new ArrayList<>();
    	// This acts as a stop point, "1" means waiting for 1 event to finish
    	java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
    	this.dbRef.child("songs").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
    		@Override
    		public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
    			for(com.google.firebase.database.DataSnapshot c: snapshot.getChildren()) {
    				Song s = c.getValue(Song.class);
    				songList.add(s);
    			}
    			// Once the loop is finished, pass the stop point
    			latch.countDown();
    		}
    		
    		@Override
    		public void onCancelled(com.google.firebase.database.DatabaseError error) {
    			System.err.println("Database error: " + error.getMessage());
    			latch.countDown();
    		}
    	});
    	// Java will proceed until return fully loaded list
    	try {
    		latch.await();
    	} 
    	catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	return songList;
    	
    }

    @Override
    public void saveSong(Song song) {
    	if(song == null || song.getSongId() == null) {
    		System.out.println("Cannot save this song!");
    		return;
    	}
    	this.dbRef.child("songs").child(song.getSongId()).setValueAsync(song);
    	System.out.println("Successfully save " + song.getTitle() + " to Firebase.");
    }
    
    @Override
    public void deleteSong(String id) {
    	this.dbRef.child("songs").child(id).removeValueAsync();
    }
    
}