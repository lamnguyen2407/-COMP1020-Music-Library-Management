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
        String[] tokens = lowerKeyword.split(" ");
        String pivot = tokens[0];
        int id = 0;
        for(int i = 0; i < tokens.length; ++i) {
        	if(tokens[i].length() > pivot.length()) {
        		pivot = tokens[i];  
        		id = i;
        	}
        }
        for (Song song : sourceList) {
            String title = song.getTitle().toLowerCase();
            String artist = song.getArtist().toLowerCase();
            
            if (artist.contains(pivot) || title.contains(pivot)) {
                result.add(song);
            }
        }
        List<Song> finalResult = new ArrayList<>();
        for(Song s: result) {
        	boolean isMatched = true;
        	for(int i = 0; i < tokens.length; ++i) {
        		if(i != id) {
        			if(!s.getArtist().contains(tokens[i]) && !s.getTitle().contains(tokens[i])) {
        				isMatched = false;
        				break;
        			}
        		}
        	}
        	if(isMatched) finalResult.add(s);
        }
        return finalResult;
    }

    @Override
    public List<Song> search(Map<String, Song> songCache, String kw) {
        List<Song> result = new ArrayList<>();
        if (kw == null || kw.trim().isEmpty()) return result;
        
        String lowerKeyword = kw.toLowerCase().trim();
        String[] tokens = lowerKeyword.split(" ");
        String pivot = tokens[0];
        int id = 0;
        for(int i = 0; i < tokens.length; ++i) {
        	if(tokens[i].length() > pivot.length()) {
        		pivot = tokens[i];  
        		id = i;
        	}
        }
        for (Song song : songCache.values()) {
            String title = song.getTitle().toLowerCase();
            String artist = song.getArtist().toLowerCase();
            
            if (artist.contains(pivot) || title.contains(pivot)) {
                result.add(song);
            }
        }
        List<Song> finalResult = new ArrayList<>();
        for(Song s: result) {
        	boolean isMatched = true;
        	for(int i = 0; i < tokens.length; ++i) {
        		if(i != id) {
        			if(!s.getArtist().contains(tokens[i]) && !s.getTitle().contains(tokens[i])) {
        				isMatched = false;
        				break;
        			}
        		}
        	}
        	if(isMatched) finalResult.add(s);
        }
        return finalResult;
    }
}