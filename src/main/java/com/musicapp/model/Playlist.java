package com.musicapp.model;
import com.musicapp.model.Song;
import java.util.*;

public class Playlist {
	private String playlistId;
	private String ownerId;
	private String name;
	private boolean isPublic;
	private ArrayList<Song> songs;

	public Playlist(String name) {
		this.playlistId = UUID.randomUUID().toString();
		this.name = name;
		this.isPublic = false;
		this.songs = new ArrayList<>();
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void addSongToPlaylist(Song song) {
		songs.add(song);
	}

	public void removeSongToPlaylist(String id) {
		songs.removeIf(song -> song.getSongId().equals(id));
	}

	public ArrayList<Song> getSongs() {
		return songs;
	}
}
