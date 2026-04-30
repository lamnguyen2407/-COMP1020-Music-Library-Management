package com.musicapp.model;

import java.util.*;

public class Album {
    private String albumId;
    private String title;
    private String artist;
    private int releaseYear;
    private String genre;
    private String imageURL;
    
    // CÚ FIX QUAN TRỌNG: Đổi List thành Map để chuẩn NoSQL Firebase
    private Map<String, Boolean> songIds;
    
    // 1. Constructor rỗng cho Firebase
    public Album() {
        this.songIds = new HashMap<>();
    }
    
    // 2. Constructor lấy từ Firebase về
    public Album(String albumId, String title, String artist, int releaseYear, String imageURL, String genre) {
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.genre = genre;
        this.songIds = new HashMap<>();
    }

    // 3. Constructor tạo mới từ UI
    public Album(String title, String artist, int releaseYear, String imageURL, String genre) {
        this.albumId = "ALBUM_" + UUID.randomUUID().toString(); 
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.genre = genre;
        this.songIds = new HashMap<>();
    }
    
    // ==========================================
    // GETTERS & SETTERS CƠ BẢN
    // ==========================================
    public String getAlbumId() { return this.albumId; }
    public void setAlbumId(String albumId) { this.albumId = albumId; }
    
    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return this.artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public int getReleaseYear() { return this.releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    
    public String getImageURL() { return this.imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    
    public String getGenre() { return this.genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    // ==========================================
    // XỬ LÝ DANH SÁCH BÀI HÁT (Map)
    // ==========================================
    public Map<String, Boolean> getSongIds() {
        return this.songIds;
    }

    public void setSongIds(Map<String, Boolean> songIds) {
        this.songIds = songIds;
    }

    // Hàm hỗ trợ UI lấy List String ID
    public List<String> getSongIdList() {
        if (this.songIds == null) return new ArrayList<>();
        return new ArrayList<>(this.songIds.keySet());
    }
    
    // Hàm thêm bài hát chuẩn NoSQL
    public void addSongId(String songId) {
        if (this.songIds == null) this.songIds = new HashMap<>();
        this.songIds.put(songId, true);
    }

    // Hàm xóa bài hát khỏi Album
    public void removeSongId(String songId) {
        if (this.songIds != null) {
            this.songIds.remove(songId);
        }
    }
}