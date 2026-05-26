package com.musicapp.service;

import com.musicapp.model.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FirebaseServiceImpl implements FirebaseService {

    private DatabaseReference dbRef;
    private String currentUserId;
    private String currentUserRole;
    
    @Override
    public String getCurrentUserId() { return currentUserId; }
    
    @Override
    public String getCurrentUserRole() { return currentUserRole; }

    @Override
    public void setSession(String userId, String role) {
        this.currentUserId = userId;
        this.currentUserRole = role;
    }

    public FirebaseServiceImpl() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-config.json");
            
            if (serviceAccount == null) {
                throw new RuntimeException("Firebase config file not found in resources!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://music-library-management-59ce4-default-rtdb.firebaseio.com/") 
                .build(); 

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            dbRef = FirebaseDatabase.getInstance().getReference();
            System.out.println("Connected to Firebase Realtime Database successfully");

        } catch (Exception e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
        }
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
            latch.await(10, TimeUnit.SECONDS); 
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
        Song[] tempArray = new Song[songIds.size()];
        
        for (int i = 0; i < songIds.size(); i++) {
            final int index = i;
            String id = songIds.get(i);
            
            this.dbRef.child("songs").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Song song = snapshot.getValue(Song.class);
                        if (song != null) {
                            song.setSongId(snapshot.getKey());
                            tempArray[index] = song; 
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
            latch.await(10, TimeUnit.SECONDS); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Song s : tempArray) {
            if (s != null) songList.add(s);
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
            latch.await(10, TimeUnit.SECONDS); 
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
        try {
            return fetchPlaylistByIdAsync(playlistId).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public void removeSongFromAlbum(String albumId, String songId) {
        if (albumId == null || songId == null) return;
        this.dbRef.child("albums")
                  .child(albumId)
                  .child("songIds")
                  .child(songId)
                  .removeValueAsync();
        System.out.println("Song " + songId + " removed from album " + albumId);
    }

    @Override
    public void removeSongFromPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        this.dbRef.child("playlists")
                  .child(playlistId)
                  .child("songIds")
                  .child(songId)
                  .removeValueAsync();
        System.out.println("Song " + songId + " removed from playlist " + playlistId);
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
            DataSnapshot snapshot = future.get(10, TimeUnit.SECONDS);
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
            DataSnapshot snapshot = future.get(10, TimeUnit.SECONDS);
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
    
    private int getIntegerKey(String text) {
        int key = 0, prime = 29;
        for(int i = 0; i < text.length(); ++i) {
            key = key * prime + text.charAt(i);
        }
        return key & Integer.MAX_VALUE;
    }

    @Override
    public void registerNewUser(String email, String username, String password, String fullname, RegisterCallback callback) {
        dbRef.child("users").orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot usernameSnapshot) {
                    if (usernameSnapshot.exists()) {
                        callback.onError("Username is already taken.");
                        return;
                    }
                    proceedWithEmailHash(email, username, password, fullname, 0, callback);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callback.onError("Database Error: " + error.getMessage());
                }
            });
    }

    private void proceedWithEmailHash(String email, String username, String password, String fullname, int i, RegisterCallback callback) {
        int keyEmail = getIntegerKey(email);
        int indexEmail = ( (keyEmail % 997) + i * (991 - (keyEmail % 991))) % 997;
        String generatedUserId = String.format("U%08d", Math.abs(indexEmail));

        dbRef.child("users").child(generatedUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        ListenerUser newUser = new ListenerUser(generatedUserId, fullname, email, username, password);
                        try {
                            saveUser(newUser); 
                            setSession(newUser.getUserId(), newUser.getRole()); 
                            callback.onSuccess(newUser);
                        } catch (Exception e) {
                            callback.onError("Failed to save user data.");
                        }
                    } else {
                        String dbEmail = snapshot.child("email").getValue(String.class);
                        if (email.equalsIgnoreCase(dbEmail)) {
                            callback.onError("Email is already taken.");
                        } else {
                            proceedWithEmailHash(email, username, password, fullname, i + 1, callback);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    callback.onError("Database Error: " + error.getMessage());
                }
            });
    }
    
    @Override
    public void authenticateUser(String email, String password, LoginCallback callback) {
        String targetId;

        if (email.equalsIgnoreCase("admin1@musicapp.com")) { 
            targetId = "admin1"; 
        } 
        
        else if (email.equalsIgnoreCase("admin2@musicapp.com")) { 
            targetId = "admin2"; 
        } 
        else {
            int keyEmail = getIntegerKey(email);
            int indexEmail = ( (keyEmail % 997) + 0 * (991 - (keyEmail % 991))) % 997;
            targetId = String.format("U%08d", Math.abs(indexEmail));
        }

        dbRef.child("users").child(targetId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    
                    if (password.equals(dbPassword)) {
                        User user = snapshot.getValue(ListenerUser.class); 
                        callback.onSuccess(user, snapshot.child("role").getValue(String.class));
                    } else {
                        callback.onError("Incorrect password. Please try again.");
                    }
                } else {
                      callback.onError("Account does not exist.");
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
            DataSnapshot playlistIdsSnapshot = userFuture.get(5, TimeUnit.SECONDS);
            
            if (playlistIdsSnapshot.exists()) {
                List<CompletableFuture<Playlist>> fetchFutures = new ArrayList<>();
                
                for (DataSnapshot idSnapshot : playlistIdsSnapshot.getChildren()) {
                    String pId = idSnapshot.getKey(); 
                    fetchFutures.add(fetchPlaylistByIdAsync(pId)); 
                }
                
                CompletableFuture.allOf(fetchFutures.toArray(new CompletableFuture[0])).join();
                
                for (CompletableFuture<Playlist> future : fetchFutures) {
                    Playlist p = future.get();
                    if (p != null) playlists.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Info: User has no playlists or connection error: " + e.getMessage());
        }

        return playlists; 
    }

    private CompletableFuture<Playlist> fetchPlaylistByIdAsync(String playlistId) {
        CompletableFuture<Playlist> pFuture = new CompletableFuture<>();
        this.dbRef.child("playlists").child(playlistId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Playlist p = snapshot.getValue(Playlist.class);
                    if (p != null) p.setPlaylistId(snapshot.getKey());
                    pFuture.complete(p);
                } else {
                    pFuture.complete(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                pFuture.completeExceptionally(error.toException());
            }
        });
        return pFuture;
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
    
    @Override
    public void removeSongContextually(String contextId, String songId) {
        if (songId == null) return;
        
        boolean isLibraryView = (contextId == null || contextId.toLowerCase().contains("all") || contextId.toLowerCase().contains("library"));

        if (isLibraryView) {
            deleteSong(songId); 
        } else if (contextId.startsWith("SYSTEM_") || contextId.startsWith("pl_") || contextId.startsWith("fav_")) {
            removeSongFromPlaylist(contextId, songId);
        } else {
            removeSongFromAlbum(contextId, songId);
        }
    }
    
    @Override
    public List<Song> fetchSongsContextually(String contextId, String contextTitle) {
        List<Song> targetSongs = new ArrayList<>();
        boolean isLibraryView = (contextTitle != null && 
             (contextTitle.toLowerCase().contains("all") || contextTitle.toLowerCase().contains("library")));
        
        if (isLibraryView) {
            return fetchSongs();
        } else if (contextId != null) {
            List<String> songIds;
            if (contextId.startsWith("SYSTEM_") || contextId.startsWith("pl_") || contextId.startsWith("fav_")) {
                songIds = fetchSongIdsFromPlaylist(contextId);
            } else {
                songIds = fetchSongIdsFromAlbum(contextId);
            }
            
            if (songIds != null && !songIds.isEmpty()) {
                return fetchSongsByIds(songIds);
            }
        }
        return targetSongs;
    }
}