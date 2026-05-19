package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public interface SearchStrategy {
    default List<Song> search(List<Song> sourceList, String keyword) {
    	return null;
    }
    default List<Song> search(Map<String, Song> songCache, String kw) {
    	return null;
    }
    default void resetIndex() {
    	
    }
    default void indexSong(Song song) {
    	
    }
    
}