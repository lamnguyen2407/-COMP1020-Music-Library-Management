package com.musicapp.service;

import com.musicapp.model.Song;
import com.musicapp.service.FirebaseService;
import java.util.*;


public class LibraryManager {
	private HashMap<String, Song> songCache = new HashMap<>();
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
		if (fetchedSongs != null) {
			for (Song song: fetchedSongs) {
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
	
	public boolean addSong(Song song) {
		
		if (songCache.containsKey(song.getSongId())) {
	        System.out.println("Error: song ID exists");
	        return false;
	    }
		
		firebaseService.saveSong(song);
		songCache.put(song.getSongId(), song);
		return true;
	}
	
	public void removeSong(String id) {
		firebaseService.deleteSong(id);
		songCache.remove(id);
	}
	
	public void reloadCache() {
		songCache.clear();
		loadFromFirebase();
	}
	public Map<String, Song> getSongCache() {
		return this.songCache;
	}
}
