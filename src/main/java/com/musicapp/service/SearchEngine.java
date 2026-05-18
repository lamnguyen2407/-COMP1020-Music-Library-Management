package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.List;

public class SearchEngine {
    private SearchStrategy strategy;
    private LibraryManager libraryManager;
    
    public SearchEngine(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        this.strategy = new LinearSearchStrategy();
    }
    
    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }
    
    public List<Song> search(String keyword) {
        List<Song> allSongs = libraryManager.getAllSong();
        return strategy.search(allSongs, keyword);
    }
}