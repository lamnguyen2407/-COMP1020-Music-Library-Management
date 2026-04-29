package com.musicapp.service;

import com.musicapp.model.Song;
import com.musicapp.model.Album;
import com.musicapp.model.Playlist;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class FirebaseServiceImpl implements FirebaseService {

    private DatabaseReference dbRef;
    private final String BUCKET_NAME = "music-library-management-59ce4.appspot.com";

    public FirebaseServiceImpl() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-config.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://music-library-management-59ce4-default-rtdb.firebaseio.com/") 
                .setStorageBucket(BUCKET_NAME)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            dbRef = FirebaseDatabase.getInstance().getReference();
            System.out.println("Connected to Firebase Database and Storage successfully");

        } catch (Exception e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
        }
    }

    // Uploads file (mp3 or image) to Firebase Storage and returns the public download URL
    public String uploadFileToStorage(File file, String folderName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        
        // Generate a unique file name to avoid overwriting existing files
        String blobName = folderName + "/" + UUID.randomUUID().toString() + "_" + file.getName();
        
        // Determine MIME type based on file extension
        String contentType = file.getName().endsWith(".mp3") ? "audio/mpeg" : "image/jpeg";

        Blob blob = bucket.create(blobName, new FileInputStream(file), contentType);

        // Encode the file path to create a valid public download URL for JavaFX streaming
        String encodedBlobName = URLEncoder.encode(blobName, StandardCharsets.UTF_8.toString());
        return "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + encodedBlobName + "?alt=media";
    }

    @Override
    public List<Song> fetchSongs() {
        List<Song> songList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        this.dbRef.child("songs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot c : snapshot.getChildren()) {
                    Song s = c.getValue(Song.class);
                    if (s != null) songList.add(s);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Database error: " + error.getMessage());
                latch.countDown();
            }
        });
        
        try { 
            latch.await(); 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        }
        
        return songList;
    }

    @Override
    public void saveSong(Song song) {
        if (song == null || song.getSongId() == null) return;
        
        this.dbRef.child("songs").child(song.getSongId()).setValueAsync(song);
        System.out.println("Successfully saved song: " + song.getTitle());
    }

    @Override
    public void deleteSong(String id) {
        this.dbRef.child("songs").child(id).removeValueAsync();
    }

    public List<Album> fetchAlbums() {
        List<Album> albumList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        this.dbRef.child("albums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot c : snapshot.getChildren()) {
                    Album a = c.getValue(Album.class);
                    if (a != null) albumList.add(a);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) { 
                latch.countDown(); 
            }
        });
        
        try { 
            latch.await(); 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        }
        
        return albumList;
    }

    public void saveAlbum(Album album) {
        if (album == null || album.getAlbumId() == null) return;
        
        this.dbRef.child("albums").child(album.getAlbumId()).setValueAsync(album);
        System.out.println("Successfully saved album: " + album.getTitle());
    }

    public void saveUserPlaylist(String userId, Playlist playlist) {
        if (userId == null || playlist == null) return;
        
        // Save to path: /users/{userId}/playlists/{playlistId}
        this.dbRef.child("users").child(userId).child("playlists")
                  .child(playlist.getPlaylistId()).setValueAsync(playlist);
        System.out.println("Saved playlist for user: " + userId);
    }
}