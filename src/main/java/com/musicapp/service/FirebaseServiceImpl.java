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
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CompletableFuture;

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

    @Override
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

    @Override
    public void saveAlbum(Album album) {
        if (album == null || album.getAlbumId() == null) return;
        
        this.dbRef.child("albums").child(album.getAlbumId()).setValueAsync(album);
        System.out.println("Successfully saved album: " + album.getTitle());
    }

    @Override
    public void saveUserPlaylist(String userId, Playlist playlist) {
        if (userId == null || playlist == null) return;
        
        // Save to path: /users/{userId}/playlists/{playlistId}
        this.dbRef.child("users").child(userId).child("playlists")
                  .child(playlist.getPlaylistId()).setValueAsync(playlist);
        System.out.println("Saved playlist for user: " + userId);
    }

    // ==========================================
    // 2 HÀM MỚI THÊM CHO TÍNH NĂNG TODAY'S HITS
    // ==========================================

    @Override
    public Playlist fetchPlaylist(String playlistId) {
        final Playlist[] result = new Playlist[1];
        CountDownLatch latch = new CountDownLatch(1);

        this.dbRef.child("playlists").child(playlistId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    result[0] = snapshot.getValue(Playlist.class);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Lỗi tải playlist: " + error.getMessage());
                latch.countDown();
            }
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        return result[0];
    }

    @Override
    public List<Song> fetchSongsByIds(List<String> songIds) {
        List<Song> result = new ArrayList<>();
        if (songIds == null || songIds.isEmpty()) return result;

        // Tận dụng hàm fetchSongs() lấy toàn bộ nhạc, sau đó lọc tại Local
        // Cách này đảm bảo đồng bộ với cấu trúc dữ liệu hiện tại của mày
        List<Song> allSongs = fetchSongs();
        for (Song s : allSongs) {
            if (songIds.contains(s.getSongId())) {
                result.add(s);
            }
        }
        return result;
    }
    
    @Override
    public void deleteAlbum(String albumId) {
        if (albumId == null || albumId.isEmpty()) return;
        
        this.dbRef.child("albums").child(albumId).removeValueAsync();
        System.out.println("✅ Đã xóa Album khỏi Firebase: " + albumId);
    }

    @Override
    public void deleteUserPlaylist(String userId, String playlistId) {
        if (userId == null || playlistId == null) return;
        
        // Trỏ đúng đường dẫn: /users/{userId}/playlists/{playlistId}
        this.dbRef.child("users").child(userId).child("playlists")
                  .child(playlistId).removeValueAsync();
        System.out.println("✅ Đã xóa Playlist: " + playlistId + " của user: " + userId);
    }
    
    @Override
    public void addSongToAlbum(String albumId, String songId) {
        if (albumId == null || songId == null) return;
        
        // Đường dẫn: /albums/{albumId}/songIds/{songId} = true
        this.dbRef.child("albums").child(albumId)
                  .child("songIds").child(songId).setValueAsync(true);
                  
        System.out.println("✅ Firebase: Đã nối bài hát " + songId + " vào Album " + albumId);
    }
    
 // Đảm bảo mày đã có @Override để Java biết đây là hàm triển khai từ Interface
    @Override
    public void addSongToPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        
        // Lưu vào nhánh 'playlists' để không bị lẫn với 'albums'
        this.dbRef.child("playlists")
                  .child(playlistId)
                  .child("songIds")
                  .child(songId)
                  .setValueAsync(true);
                  
        System.out.println("✅ [Firebase] Đã thêm bài hát " + songId + " vào playlist " + playlistId);
    }
    
    @Override
    public List<String> fetchSongIdsFromPlaylist(String playlistId) {
        List<String> ids = new ArrayList<>();
        // Tạo một "lời hứa" (Future) để đợi dữ liệu từ Firebase
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        // Dùng Listener để lấy dữ liệu một lần duy nhất
        this.dbRef.child("playlists").child(playlistId).child("songIds")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // Khi có dữ liệu, hoàn thành "lời hứa"
                    future.complete(snapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Nếu lỗi, báo lỗi cho "lời hứa"
                    future.completeExceptionally(error.toException());
                }
            });

        try {
            // Đợi tối đa 10 giây để lấy dữ liệu (tránh treo luồng vĩnh viễn nếu mạng lag)
            DataSnapshot snapshot = future.get(10, java.util.concurrent.TimeUnit.SECONDS);
            
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey()); // Lấy cái ID bài hát (ví dụ: 3333000)
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi fetch ID từ Firebase: " + e.getMessage());
        }

        return ids;
    }
    
    @Override
    public List<String> fetchSongIdsFromAlbum(String albumId) {
        List<String> ids = new ArrayList<>();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        // Soi đúng vào nhánh 'albums' -> {albumId} -> 'songIds'
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
            // Đợi tối đa 10 giây cho dữ liệu load về
            DataSnapshot snapshot = future.get(10, java.util.concurrent.TimeUnit.SECONDS);
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey()); // Lấy ID bài hát ra
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy ID từ Album: " + e.getMessage());
        }
        return ids;
    }
}