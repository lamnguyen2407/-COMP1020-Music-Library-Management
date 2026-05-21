package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class SearchEngine {
    private LibraryManager libraryManager;
    private AdvancedSearchStrategy advancedStrategy;
    private LinearSearchStrategy linearStrategy;
    
    // Manual toggle for strategy choice
    // private boolean useAdvanced = true;

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
    /*
    public List<Song> search(String query) {
        Map<String, Song> cache = libraryManager.getSongCache();
        
        // Performance comparison in terminal
        long startL = System.nanoTime();
        List<Song> linearResults = linearStrategy.search(cache, query);
        long endL = System.nanoTime();
        
        long startA = System.nanoTime();
        List<Song> advancedResults = advancedStrategy.search(cache, query);
        long endA = System.nanoTime();
        
        double timeL = (endL - startL) / 1_000_000.0;
        double timeA = (endA - startA) / 1_000_000.0;
        
        System.out.printf("[BENCHMARK] Query: '%s' | Linear: %.3fms | Advanced: %.3fms | Mode: %s\n", 
                          query, timeL, timeA, (useAdvanced ? "ADVANCED" : "LINEAR"));
        
        return useAdvanced ? advancedResults : linearResults;
    }
    */
}