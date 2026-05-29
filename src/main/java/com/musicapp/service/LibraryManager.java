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
        loadDataToCache(); 
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
    
    public List<Song> getSongsByIds(List<String> songIds) {
        loadDataToCache(); // BỔ SUNG DÒNG NÀY: Đảm bảo kho có hàng
        List<Song> results = new ArrayList<>();
        if (songIds == null || songIds.isEmpty()) {
            return results;
        }
        
        for (String id : songIds) {
            Song s = songCache.get(id); 
            if (s != null) {
                results.add(s);
            }
        }
        return results;
    }
    
    private void loadDataToCache() {
        if (songCache.isEmpty()) {
            List<Song> fetchedSongs = firebaseService.fetchSongs(); // Gọi mạng lấy toàn bộ bài hát
            if (fetchedSongs != null) {
                for (Song s : fetchedSongs) {
                    songCache.put(s.getSongId(), s);
                }
            }
            System.out.println("LibraryManager: Đã nạp " + songCache.size() + " bài hát từ mây vào RAM!");
        }
    }
    
    
}