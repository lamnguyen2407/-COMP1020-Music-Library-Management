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

	@Override
	public String toString() {
		return title + " - " + artist + " (" + releaseYear + ")";
	}

	@Override
	public int compareTo(Song other) {
		return this.title.compareTo(other.title);
	}
}
