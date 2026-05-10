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
                    if (s != null) {
                        // QUAN TRỌNG: Phải lấy cái Key gán vào ID thì app mới biết nó là ai!
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
        // Lưu vào nhánh songs
        this.dbRef.child("songs").child(song.getSongId()).setValueAsync(song);
    }

    @Override
    public void deleteSong(String id) {
        this.dbRef.child("songs").child(id).removeValueAsync();
    }
    @Override 
    public List<Song> fetchAlbumSongsByIds(List<String> songIds) {
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
                            // Dùng synchronized để đảm bảo an toàn khi add từ nhiều luồng Firebase
                            synchronized (songList) {
                                songList.add(song);
                            }
                        }
                    }
                    latch.countDown(); // Tải xong 1 bài, đếm ngược Latch
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown(); // Lỗi cũng đếm ngược để không bị treo
                }
            });
        }

        try {
            latch.await(); // Đóng băng luồng hiện tại cho đến khi tải đủ số bài hát
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
        // 1. Lưu thông tin chi tiết playlist vào nhánh chung
        this.dbRef.child("playlists").child(playlist.getPlaylistId()).setValueAsync(playlist);
        
        // 2. Lưu ID này vào danh sách playlist của User đó
        this.dbRef.child("users")
                  .child(userId)
                  .child("playlistIds")
                  .child(playlist.getPlaylistId())
                  .setValueAsync(true); // Chỉ cần lưu ID là key, value là true cho nhẹ
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
                    
                    // NẾU LÀ BOOLEAN: Dành cho nhạc được add bằng app ("ID": true)
                    if (value instanceof Boolean) {
                        ids.add(child.getKey());
                    } 
                    // NẾU LÀ SỐ HOẶC CHUỖI: Dành cho list nhập tay trên Firebase (0: 3333001)
                    else {
                        ids.add(String.valueOf(value)); 
                    }
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
    
    // For dealing with users database
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
    						callback.onSuccess(loginUser, role);
    						return;
    					}
    				}
    				callback.onError("Incorrect Password!!!");
    			}
    			else {
    				callback.onError("No account are found !!!");
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

        // 1. Tìm xem User này có danh sách ID playlist nào không
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
                // 2. Nếu có ID, duyệt qua từng ID để lấy Object Playlist thật sự
                for (DataSnapshot idSnapshot : playlistIdsSnapshot.getChildren()) {
                    String pId = idSnapshot.getKey(); 
                    
                    // Chỗ này mình fetch nốt thông tin Playlist từ nhánh /playlists
                    Playlist p = fetchPlaylistById(pId); // Mày nên có hàm phụ này
                    if (p != null) {
                        playlists.add(p);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ℹ️ User chưa có playlist hoặc lỗi kết nối: " + e.getMessage());
        }

        return playlists; // Trả về list (có thể rỗng nhưng không được null)
    }

    // Hàm hỗ trợ lấy 1 playlist theo ID
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
            return ds.getValue(Playlist.class);
        }
        return null;
    }
    
    @Override
    public void saveNewUserPlaylist(String uid, String name) {
        // 1. Tạo một ID ngẫu nhiên cho Playlist mới trên Firebase
        String playlistId = dbRef.child("playlists").push().getKey();
        
        if (playlistId == null) return;

        // 2. Tạo Object Playlist (Đảm bảo Class Playlist của mày có các setter này)
        Playlist newPlaylist = new Playlist();
        newPlaylist.setPlaylistId(playlistId);
        newPlaylist.setName(name);
        newPlaylist.setOwnerId(uid);
        // Khởi tạo một Map rỗng để tránh lỗi Null sau này khi add bài hát
        newPlaylist.setSongIds(new java.util.HashMap<>()); 

        // 3. Thực hiện lưu đồng thời vào 2 nhánh (Atomic Update)
        // Lưu thông tin chi tiết của Playlist
        dbRef.child("playlists").child(playlistId).setValueAsync(newPlaylist);
        
        // Lưu ID của Playlist này vào danh sách quản lý của User
        dbRef.child("users").child(uid).child("playlistIds").child(playlistId).setValueAsync(true);
        
        System.out.println("✅ Firebase: Đã tạo playlist " + name + " thành công!");
    }
    
    @Override
    public void toggleFavoriteSong(String userId, Song song) {
        // Trỏ thẳng vào nhánh của bài hát cụ thể, KHÔNG trỏ vào node cha
        com.google.firebase.database.DatabaseReference ref = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("playlists")
            .child("fav_" + userId)
            .child("songIds")
            .child(song.getSongId()); 

        ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Nếu đã có tim -> Xóa bài này (Bỏ tim)
                    ref.removeValueAsync();
                    System.out.println("Đã xóa khỏi Favorite: " + song.getTitle());
                } else {
                    // Nếu chưa có tim -> Thêm bài này (Thả tim) bằng setValue(true)
                    ref.setValueAsync(true); 
                    System.out.println("Đã thêm vào Favorite: " + song.getTitle());
                }
            }
            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                System.err.println("Lỗi thả tim: " + error.getMessage());
            }
        });
    }
    
}