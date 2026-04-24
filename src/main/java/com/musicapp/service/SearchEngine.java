package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class SearchEngine {
	private SearchStrategy strategy;
	private LibraryManager libraryManager;
	
	public SearchEngine(LibraryManager libraryManager) {
		this.libraryManager = libraryManager;
		this.strategy = new LinearSearchStrategy();
	}
	
	public void setStrategy(SearchStrategy s) {
		this.strategy = s;
	}
	
	public List<Song> search(String kw) {
		List<Song> allSogs = libraryManager.getAllSong();
		
		return strategy.search(all Songs, kw);
	}
}
