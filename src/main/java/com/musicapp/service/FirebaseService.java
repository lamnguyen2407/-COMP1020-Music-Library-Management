package com.musicapp.service;
import com.musicapp.model.Song;
import java.util.*;

public interface FirebaseService {
	List<Song> fetchSongs();
	void saveSong(Song song);
	void deleteSong(String id);
}
