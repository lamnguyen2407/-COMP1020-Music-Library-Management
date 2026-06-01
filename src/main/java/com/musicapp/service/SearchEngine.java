package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class SearchEngine {
    private LibraryManager libraryManager;
    private AdvancedSearchStrategy advancedStrategy;
    private LinearSearchStrategy linearStrategy;
    

    public SearchEngine() {
        FirebaseService firebaseService = DatabaseManager.getInstance().getService();
        this.libraryManager = new LibraryManager(firebaseService);
        this.advancedStrategy = new AdvancedSearchStrategy();
        this.linearStrategy = new LinearSearchStrategy();
        
        // Single pass: Fetch once, add to cache and index simultaneously
        List<Song> allSongs = firebaseService.fetchSongs();
        for (Song s : allSongs) {
            libraryManager.addSongToCache(s);
            advancedStrategy.indexSong(s);
        }
        System.out.println("SearchEngine: Single-pass initialization complete. Songs: " + allSongs.size());
    }
    public List<Song> search(String query) {
        Map<String, Song> cache = libraryManager.getSongCache();
        List<Song> advancedResults = advancedStrategy.search(cache, query);
        return advancedResults;
    }
 
}