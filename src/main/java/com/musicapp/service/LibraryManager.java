package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class LibraryManager {
    private Map<String, Song> songCache = new HashMap<>();
    private FirebaseService firebaseService;
    
    public LibraryManager(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    public void addSongToCache(Song song) {
        songCache.put(song.getSongId(), song);
    }

    public void clearCache() {
        songCache.clear();
    }
    
    public Song getSong(String songId) {
        return songCache.get(songId);
    }
    
    public List<Song> getAllSong() {
        return new ArrayList<>(songCache.values());
    }

    public Map<String, Song> getSongCache() {
        return Collections.unmodifiableMap(songCache);
    }
    
    public boolean addSong(Song song) {
        if (songCache.containsKey(song.getSongId())) {
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
}