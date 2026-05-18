package com.musicapp.service;

import com.musicapp.model.*;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class FirebaseServiceImpl implements FirebaseService {

    private DatabaseReference dbRef;
    private String currentUserId;
    private String currentUserRole;
    private final String BUCKET_NAME = "music-library-management-59ce4.appspot.com";
    
    @Override
    public String getCurrentUserId() { return currentUserId; }
    
    @Override
    public String getCurrentUserRole() { return currentUserRole; }

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

    @Override
    public String uploadFileToStorage(File file, String folderName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket();
        String blobName = folderName + "/" + UUID.randomUUID().toString() + "_" + file.getName();
        String contentType = file.getName().endsWith(".mp3") ? "audio/mpeg" : "image/jpeg";

        Blob blob = bucket.create(blobName, new FileInputStream(file), contentType);

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
                    if (s != null) {
                        s.setSongId(c.getKey()); 
                        songList.add(s);
                    }
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
    public DatabaseReference getDbRef() {
        return this.dbRef;
    }
    
    @Override
    public void saveSong(Song song) {
        if (song == null || song.getSongId() == null) return;
        this.dbRef.child("songs").child(song.getSongId()).setValueAsync(song);
    }

    @Override
    public void deleteSong(String id) {
        this.dbRef.child("songs").child(id).removeValueAsync();
    }
    
    @Override 
    public List<Song> fetchSongsByIds(List<String> songIds) {
        List<Song> songList = new ArrayList<>();
        if(songIds == null || songIds.isEmpty()) return songList;
        
        CountDownLatch latch = new CountDownLatch(songIds.size());
        
        for (String id : songIds) {
            this.dbRef.child("songs").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Song song = snapshot.getValue(Song.class);
                        if (song != null) {
                            song.setSongId(snapshot.getKey());
                            synchronized (songList) {
                                songList.add(song);
                            }
                        }
                    }
                    latch.countDown(); 
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown(); 
                }
            });
        }

        try {
            latch.await(); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return songList;
    }

    @Override
    public List<Album> fetchAlbums() {
        List<Album> albumList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        this.dbRef.child("albums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot c : snapshot.getChildren()) {
                    Album a = c.getValue(Album.class);
                    if (a != null) {
                        a.setAlbumId(c.getKey());
                        albumList.add(a);
                    }
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

    @Override
    public void saveAlbum(Album album) {
        if (album == null || album.getAlbumId() == null) return;
        this.dbRef.child("albums").child(album.getAlbumId()).setValueAsync(album);
        System.out.println("Successfully saved album: " + album.getTitle());
    }

    @Override
    public void saveUserPlaylist(String userId, Playlist playlist) {
        this.dbRef.child("playlists").child(playlist.getPlaylistId()).setValueAsync(playlist);
        this.dbRef.child("users")
                  .child(userId)
                  .child("playlistIds")
                  .child(playlist.getPlaylistId())
                  .setValueAsync(true); 
    }

    @Override
    public Playlist fetchPlaylist(String playlistId) {
        final Playlist[] result = new Playlist[1];
        CountDownLatch latch = new CountDownLatch(1);

        this.dbRef.child("playlists").child(playlistId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    result[0] = snapshot.getValue(Playlist.class);
                    if(result[0] != null) {
                        result[0].setPlaylistId(snapshot.getKey());
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error fetching playlist: " + error.getMessage());
                latch.countDown();
            }
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        return result[0];
    }

    @Override
    public void deleteAlbum(String albumId) {
        if (albumId == null || albumId.isEmpty()) return;
        this.dbRef.child("albums").child(albumId).removeValueAsync();
        System.out.println("Album deleted successfully: " + albumId);
    }

    @Override
    public void deleteUserPlaylist(String userId, String playlistId) {
        if (userId == null || playlistId == null) return;
        this.dbRef.child("users").child(userId).child("playlists")
                  .child(playlistId).removeValueAsync();
        System.out.println("Playlist " + playlistId + " deleted for user: " + userId);
    }
    
    @Override
    public void addSongToAlbum(String albumId, String songId) {
        if (albumId == null || songId == null) return;
        this.dbRef.child("albums").child(albumId)
                  .child("songIds").child(songId).setValueAsync(true);
        System.out.println("Song " + songId + " added to album " + albumId);
    }
    
    @Override
    public void addSongToPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        this.dbRef.child("playlists")
                  .child(playlistId)
                  .child("songIds")
                  .child(songId)
                  .setValueAsync(true);
        System.out.println("Song " + songId + " added to playlist " + playlistId);
    }
    
    @Override
    public List<String> fetchSongIdsFromPlaylist(String playlistId) {
        List<String> ids = new ArrayList<>();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        this.dbRef.child("playlists").child(playlistId).child("songIds")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    future.complete(snapshot);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });

        try {
            DataSnapshot snapshot = future.get(10, java.util.concurrent.TimeUnit.SECONDS);
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Object value = child.getValue();
                    if (value instanceof Boolean) {
                        ids.add(child.getKey());
                    } 
                    else {
                        ids.add(String.valueOf(value)); 
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching IDs from playlist: " + e.getMessage());
        }
        return ids;
    }
    
    @Override
    public List<String> fetchSongIdsFromAlbum(String albumId) {
        List<String> ids = new ArrayList<>();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        this.dbRef.child("albums").child(albumId).child("songIds")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    future.complete(snapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });

        try {
            DataSnapshot snapshot = future.get(10, java.util.concurrent.TimeUnit.SECONDS);
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey()); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching IDs from album: " + e.getMessage());
        }
        return ids;
    }
    
    @Override 
    public void saveUser(User user) {
        DatabaseReference usersRef = dbRef.child("users");
        usersRef.child(user.getUserId()).setValueAsync(user);
    }
    
    @Override 
    public void authenticateUser(String loginIdentifier, String password, LoginCallback callback) {
        DatabaseReference usersRef = dbRef.child("users");
        String identifer = loginIdentifier.contains("@") ? "email" : "name";
        usersRef.orderByChild(identifer).equalTo(loginIdentifier).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot userSnap: snapshot.getChildren()) {
                        String pw = userSnap.child("password").getValue(String.class);
                        if(pw != null && pw.equals(password)) {
                            String role = userSnap.child("role").getValue(String.class);
                            User loginUser = null;
                            if("admin".equals(role)) {
                                loginUser = userSnap.getValue(Admin.class);
                            }
                            else {
                                loginUser = userSnap.getValue(ListenerUser.class);
                            }
                            
                            FirebaseServiceImpl.this.currentUserId = loginUser.getUserId();
                            FirebaseServiceImpl.this.currentUserRole = role;

                            callback.onSuccess(loginUser, role);
                            return;
                        }
                    }
                    callback.onError("Incorrect Password!");
                }
                else {
                    callback.onError("Account not found!");
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Database Error: " + error.getMessage());
            }
        });
    }
    
    @Override
    public List<Playlist> fetchUserPlaylists(String userId) {
        List<Playlist> playlists = new ArrayList<>();
        CompletableFuture<DataSnapshot> userFuture = new CompletableFuture<>();

        this.dbRef.child("users").child(userId).child("playlistIds")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userFuture.complete(snapshot);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    userFuture.completeExceptionally(error.toException());
                }
            });

        try {
            DataSnapshot playlistIdsSnapshot = userFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
            
            if (playlistIdsSnapshot.exists()) {
                for (DataSnapshot idSnapshot : playlistIdsSnapshot.getChildren()) {
                    String pId = idSnapshot.getKey(); 
                    Playlist p = fetchPlaylistById(pId); 
                    if (p != null) {
                        playlists.add(p);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Info: User has no playlists or connection error: " + e.getMessage());
        }

        return playlists; 
    }

    private Playlist fetchPlaylistById(String playlistId) throws Exception {
        CompletableFuture<DataSnapshot> pFuture = new CompletableFuture<>();
        this.dbRef.child("playlists").child(playlistId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) { pFuture.complete(snapshot); }
            @Override
            public void onCancelled(DatabaseError error) { pFuture.completeExceptionally(error.toException()); }
        });
        
        DataSnapshot ds = pFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
        if (ds.exists()) {
            Playlist p = ds.getValue(Playlist.class);
            if(p != null) {
                p.setPlaylistId(ds.getKey());
            }
            return p;
        }
        return null;
    }
    
    @Override
    public void saveNewUserPlaylist(String uid, String name) {
        String playlistId = dbRef.child("playlists").push().getKey();
        if (playlistId == null) return;

        Playlist newPlaylist = new Playlist();
        newPlaylist.setPlaylistId(playlistId);
        newPlaylist.setName(name);
        newPlaylist.setOwnerId(uid);
        newPlaylist.setSongIds(new java.util.HashMap<>()); 

        dbRef.child("playlists").child(playlistId).setValueAsync(newPlaylist);
        dbRef.child("users").child(uid).child("playlistIds").child(playlistId).setValueAsync(true);
        
        System.out.println("Successfully created playlist: " + name);
    }
    
    @Override
    public void toggleFavoriteSong(String userId, Song song) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference("playlists")
            .child("fav_" + userId)
            .child("songIds")
            .child(song.getSongId()); 

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ref.removeValueAsync();
                    System.out.println("Removed from favorites: " + song.getTitle());
                } else {
                    ref.setValueAsync(true); 
                    System.out.println("Added to favorites: " + song.getTitle());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error toggling favorite: " + error.getMessage());
            }
        });
    }
}