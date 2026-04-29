package com.musicapp.model;
import java.util.*;
public class Album {
	private String albumId;
	private String title;
	private String artist;
	private int releaseYear;
	private String genre;
	private String imageURL;
	private List<Song> songs;
	
	public Album(String albumId, String title, String artist, int releaseYear, String imageURL, String genre)  {
		this.albumId = albumId;
		this.title = title;
		this.artist = artist;
		this.releaseYear = releaseYear;
		this.imageURL = imageURL;
		this.genre = genre;
		this.songs = new ArrayList<>();
	}
	
	public String getAlbumId() {
		return this.albumId;
	}
	public String getTitle() {
		return this.title;
	}
	public String getArtist() {
		return this.artist;
	}
	public int releaseYear() {
		return this.releaseYear;
	}
	public String imageURL() {
		return this.imageURL;
	}
	public List<Song> getAlbumSongs() {
		return this.songs;
	}
	public String getGenre() {
		return this.genre;
	}
}
