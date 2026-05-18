package com.musicapp.model;

public class Song implements Comparable<Song> {
    private String songId;
    private String title;
    private String artist;
    private String genre;
    private int duration;
    private int releaseYear;
    private String audioURL;
    private String imageURL;
    
    public Song() {
    }
    
    public Song(String songId, String title, String artist, String genre, int duration, int releaseYear, String audioURL, String imageURL) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.audioURL = audioURL;
        this.imageURL = imageURL;
    }

    public String getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getAudioURL() {
        return audioURL;
    }
    
    public String getImageURL() {
        return imageURL;
    }
    
    public void setSongId(String songId) { 
        this.songId = songId; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public void setArtist(String artist) { 
        this.artist = artist; 
    }
    
    public void setGenre(String genre) { 
        this.genre = genre; 
    }
    
    public void setDuration(int duration) { 
        this.duration = duration; 
    }
    
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear; 
    }
    
    public void setAudioURL(String audioURL) { 
        this.audioURL = audioURL; 
    }
    
    public void setImageURL(String imageURL) { 
        this.imageURL = imageURL; 
    }
    
    @Override
    public String toString() {
        return title + " - " + artist + " (" + releaseYear + ")";
    }

    @Override
    public int compareTo(Song other) {
        return this.title.compareTo(other.title);
    }
    
    public static String generateAutoId() {
        return "S" + System.currentTimeMillis();
    }
}