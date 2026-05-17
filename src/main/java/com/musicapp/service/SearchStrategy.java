package com.musicapp.service;
import com.musicapp.model.Song;
import java.util.*;

public interface SearchStrategy {
	List<Song> search(List<Song> sourceList, String kw);
	default List<Song> search(Map<String, Song> songCache, String kw) {
        return search(new ArrayList<>(songCache.values()), kw);
    }
	default void indexSong(Song song) {
		
	}
	default void resetIndex() {
		
	}
}
