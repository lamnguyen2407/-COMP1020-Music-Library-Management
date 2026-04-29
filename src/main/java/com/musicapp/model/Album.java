package com.musicapp.model;
import java.util.*;

public class Album {
	private String albumId;
	private String title;
	private String artist;
	private int releaseYear;
	private String genre;
	private String imageURL;
	private List<String> songIds;
	
	// Constructor rỗng của bạn kia thêm vào
	public Album() {
		this.songIds = new ArrayList<>();
	}
	
	// Constructor có tham số (đã sửa this.songs thành this.songIds cho khớp khai báo)
	public Album(String albumId, String title, String artist, int releaseYear, String imageURL, String genre) {
		this.albumId = albumId;
		this.title = title;
		this.artist = artist;
		this.releaseYear = releaseYear;
		this.imageURL = imageURL;
		this.genre = genre;
		this.songIds = new ArrayList<>();
	}
	
	// Các hàm Getter cơ bản
	public String getAlbumId() {
		return this.albumId;
	}
	public String getTitle() {
		return this.title;
	}
	public String getArtist() {
		return this.artist;
	}
	public int getReleaseYear() {
		return this.releaseYear;
	}
	public String getImageURL() {
		return this.imageURL;
	}
	public String getGenre() {
		return this.genre;
	}
	
	// Các hàm Setter và xử lý List của bạn kia
	public List<String> getAlbumSongs() {
		return this.songIds;
	}
	
	public void setAlbumId(String albumId) { 
		this.albumId = albumId; 
	}
	
    public void setTitle(String title) { 
    	this.title = title; 
    }
    
    public void setArtist(String artist) { 
    	this.artist = artist; 
    }
    
    public void setReleaseYear(int releaseYear) { 
    	this.releaseYear = releaseYear; 
    }
    
    public void setImageURL(String imageURL) { 
    	this.imageURL = imageURL; 
    }
    
    public void setGenre(String genre) { 
    	this.genre = genre; 
    }
    
    public void setSongIds(List<String> songIds) { 
    	this.songIds = songIds; 
    }
	
    public void addSongId(String songId) {
        if (!this.songIds.contains(songId)) {
            this.songIds.add(songId);
        }
    }
}