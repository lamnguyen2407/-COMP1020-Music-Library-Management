package com.musicapp.control;
import com.musicapp.service.*;
import com.musicapp.model.*;
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
		return this.searchEngine.search(kw);
	}
	public void handlePlay(String id) {
		Song currentSong = this.libraryManager.getSong(id);
		this.playbackService.play(currentSong);
	}
	public void handleNext() {
		Song nextSong = this.playbackService.next();
		if(nextSong != null) this.playbackService.play(nextSong);
		else System.out.println("No valid next song!");
	}
	public void handlePrevious() {
		Song prevSong = this.playbackService.previous();
		if(prevSong != null) this.playbackService.play(prevSong);
		else System.out.println("No valid previous song!");
	}
	public List<Song> getFeaturedSongs() {
		return this.libraryManager.getAllSong();
	}
	public List<Playlist> getUserPlaylists() {
		// Return empty list if not find the currentUser
		if(this.currentUser == null) return List.of(); 
		List<Playlist> res = new ArrayList<>();
		if(this.currentUser instanceof ListenerUser) {
			ListenerUser listener = (ListenerUser) this.currentUser;
			for(String listenerPlaylistIds: listener.getPlaylistIds()) {
				res.add(this.playlistManager.getPlaylist(listenerPlaylistIds));
			}
		}
		if(res.isEmpty()) { // If user does not create any Playlists
			System.out.println("You do not create any playlists. Let's create a new one !");
			return List.of();
		}
		else return res;
	}
}
