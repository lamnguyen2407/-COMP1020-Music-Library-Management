package com.musicapp.control;
import com.musicapp.service.*;
import com.musicapp.model.User;
import java.util.*;
public class MusicController {
	private User currentUser;
	private SearchEngine searchEngine;
	private PlaybackService playbackService;
	private LibraryManager libraryManager;
	private PlaylistManager playlistManager;
	public MusicController(LibraryManager libMana, PlaybackService playback, SearchEngine search, PlaylistManager playMana) {
		this.libraryManager = libMana;
		this.playbackService = playback;
		this.searchEngine = search;
		this.playlistManager = playMana;
	}
	public void setCurrentUser(User user) {
		this.currentUser = user;
	}
	public List<Song> handleSearch(String kw) {
		return null;
	}
	public void handlePlay(String id) {
		
	}
	public void Next() {
		
	}
	public void handlePrevious() {
		
	}
	public List<Song> getFeaturedSongs() {
		return null;
	}
	public List<Playlist> getUserPlaylists() {
		return null;
	}
}
