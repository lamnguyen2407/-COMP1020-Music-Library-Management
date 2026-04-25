package com.musicapp.service;
import com.musicapp.model.Song;
import java.util.*;

public class LinearSearchStrategy implements SearchStrategy{
	@Override
	public List<Song> search(List<Song> sourceList, String kw) {
		List<Song> res = new ArrayList<>();
		for(Song song: sourceList) {
			//if(song.getaudioURL().equals(kw)) res.add(song);
		}
		return res;
	}
}
