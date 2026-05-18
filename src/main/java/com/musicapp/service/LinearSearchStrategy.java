package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class LinearSearchStrategy implements SearchStrategy {
    
    @Override
    public List<Song> search(List<Song> sourceList, String keyword) {
        List<Song> result = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        
        for (Song song : sourceList) {
            String title = song.getTitle().toLowerCase();
            String artist = song.getArtist().toLowerCase();
            
            if (artist.contains(lowerKeyword) || title.contains(lowerKeyword)) {
                result.add(song);
            }
        }
        return result;
    }
}