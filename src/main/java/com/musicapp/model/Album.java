package com.musicapp.model;

import java.util.*;

public class Album {
    private String albumId;
    private String title;
    private String artist;
    private int releaseYear;
    private String genre;
    private String imageURL;
    private Map<String, Boolean> songIds;
    
    public Album() {
        this.songIds = new HashMap<>();
    }
    
    public Album(String albumId, String title, String artist, int releaseYear, String imageURL, String genre) {
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.genre = genre;
        this.songIds = new HashMap<>();
    }

    public Album(String title, String artist, int releaseYear, String imageURL, String genre) {
        this.albumId = "ALBUM_" + UUID.randomUUID().toString(); 
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.genre = genre;
        this.songIds = new HashMap<>();
    }
    
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
    
    public Map<String, Boolean> getSongIds() {
        return this.songIds;
    }

    public void setSongIds(Map<String, Boolean> songIds) {
        this.songIds = songIds;
    }

    public List<String> getSongIdList() {
        if (this.songIds == null) return new ArrayList<>();
        return new ArrayList<>(this.songIds.keySet());
    }
    
    public void addSongId(String songId) {
        if (this.songIds == null) this.songIds = new HashMap<>();
        this.songIds.put(songId, true);
    }

    public void removeSongId(String songId) {
        if (this.songIds != null) {
            this.songIds.remove(songId);
        }
    }
}