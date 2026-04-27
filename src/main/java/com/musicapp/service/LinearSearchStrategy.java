package com.musicapp.service;
import com.musicapp.model.Song;
import java.util.List;
import java.util.ArrayList;
public class LinearSearchStrategy implements SearchStrategy{
	@Override
	public List<Song> search(List<Song> sourceList, String kw) {
		List<Song> res = new ArrayList<>();
		kw = kw.toLowerCase();
		for(Song song: sourceList) {
			String title = song.getTitle().toLowerCase();
			String artist = song.getArtist().toLowerCase();
			if((artist.contains(kw)) || (title.contains(kw))) {
				res.add(song);
			}
		}
		return res;
	}
}
