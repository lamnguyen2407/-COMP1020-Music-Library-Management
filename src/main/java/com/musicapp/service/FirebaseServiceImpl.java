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
                // THAY CÁI LINK BÊN DƯỚI BẰNG LINK DATABASE CỦA NHÓM MÀY (Lấy ở Giai đoạn 2)
                .setDatabaseUrl("https://music-library-management-59ce4-default-rtdb.firebaseio.com/") 
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            dbRef = FirebaseDatabase.getInstance().getReference();
            System.out.println("✅ Kết nối Firebase thành công rực rỡ!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối Firebase: " + e.getMessage());
        }
    }
    @Override
    public List<Song> fetchSongs() {
    	return null; //temporarily return null value
    }
    
    @Override
    public void saveSong(Song song) {
    	
    }
    
    @Override
    public void deleteSong(String id) {
    	
    }
    
}