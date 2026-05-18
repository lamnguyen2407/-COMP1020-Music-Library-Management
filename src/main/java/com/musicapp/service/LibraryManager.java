package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class LibraryManager {
    private HashMap<String, Song> songCache = new HashMap<>();
    private FirebaseService firebaseService;
    
    public LibraryManager(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    public void loadFromFirebase() {
        List<Song> fetchedSongs = firebaseService.fetchSongs();
        
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
            System.err.println("Error: Song ID already exists in cache.");
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
}