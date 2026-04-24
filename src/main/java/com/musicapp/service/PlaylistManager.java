package com.musicapp.service;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import java.util.*;

public class PlaylistManager {
	private HashMap<String, Playlist> playlistMap;
	private Playlist currenPlaylist;
	
	public PlaylistManager() {
		this.playlistMap = new HashMap<>();
	}
	
	public Playlist createPlaylist(String name) {
		Playlist newPlaylist = new Playlist(name);
		playlistMap.put(newPlaylist.getPlaylistId(), new Playlist());
		this.currentPlaylist = newPlaylist;
		return newPlaylist;
	}
	
	public void addToPlaylist(Song song) {
		if (currentPlaylist != null) {
			currentPlaylist.addSongToPlaylist(song);
		}
		
		else {
			System.out.println("Error: Please choose your playlist to add new songs");
		}
		
	}
	
	public void removeFromPlaylist(String songId) {
		if (currentPlaylist != null) {
			currentPlaylist.removeSongToPlaylist(songId);
		}
	}
	
	public Playlist getPlaylist(String id) {
		Playlist p = playlistMap.get(id);
		if (p!= null) this.currentPlaylist = p;
		return p;
	}
}


