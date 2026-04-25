package com.musicapp.service;

import com.musicapp.model.Song;
import com.musicapp.interfaces.FirebaseService;
import java.util.*;


public class LibraryManager {
	private HashMap<String, Song> songCache = new HashMap<>;
	private FirebaseService firebaseService;
	
	public LibraryManager(FirebaseService firebaseService) {
		this.firebaseService = firebaseService;
	}
	
	// Call music from Firebase to store in Cache (call only once when opening app)
	public void loadFromFirebase() {
		// get songs from firebases
		List<Song> fetchedSongs = firebaseService.fetchSongs();
		
		// put songs to Hash Map for quick searching (O(1) complexity)
		songCache.clear();
		if (fetchSongs != null) {
			for (Song song: fetchSongs) {
				songCache.put(song.getSongId(), song);
			}
		}
		
		System.out.println("Loaded " + songCache.size() + " songs to cache.");
	}
	
	
	public Song getSong(String songId) {
		return songCache.get(songId);
	}
	
	public List<Song> getAllSong() {
		return new ArrayList<>(songCache.values());
	}
	
	public void addSong(Song song) {
		firebaseService.saveSong(song);
		songCache.put(song.getSongId(), song);
	}
	
	public void removeSong(String id) {
		firebaseService.deleteSong(id);
		songCache.remove(id);
	}
	
	public void reloadCache() {
		songCache.clear();
		loadFromFirebase();
	}
}
