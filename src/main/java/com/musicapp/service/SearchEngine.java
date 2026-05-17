package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class SearchEngine {
	private SearchStrategy strategy;
	private LibraryManager libraryManager;
	
	public SearchEngine(LibraryManager libraryManager) {
		this.libraryManager = libraryManager;
		//this.strategy = new LinearSearchStrategy();
		this.strategy = new AdvancedSearchStrategy();
	}
	
	public void setStrategy(SearchStrategy s) {
		this.strategy = s;
	}
	public void buildIndex() {
		strategy.resetIndex();
		List<Song> allSongs = libraryManager.getAllSong();
		for(Song song : allSongs) {
			strategy.indexSong(song);
		}
		System.out.println("Search Engine index built successfully.");
	}

	// Call this from MusicController if a user creates/adds a new song to the app.
	public void indexSingleSong(Song song) {
		strategy.indexSong(song);
	}
	public List<Song> search(String kw) {
		//List<Song> allSongs = libraryManager.getAllSong();
		Map<String, Song> cache = libraryManager.getSongCache();
		return strategy.search(cache, kw);
	}
}
