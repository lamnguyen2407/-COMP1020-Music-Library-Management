package com.musicapp.service;

import com.musicapp.model.Album;
import com.musicapp.model.Song;
import java.util.*;

public class MusicService {
    
    private static MusicService instance;
    private FirebaseService firebaseService;
    private List<Song> trendingCache;

    private MusicService() {
        this.firebaseService = DatabaseManager.getInstance().getService();
    }

    public static MusicService getInstance() {
        if (instance == null) {
            instance = new MusicService();
        }
        return instance;
    }

    public List<Song> getTrendingSongs() {
        if (trendingCache != null && !trendingCache.isEmpty()) {
            return trendingCache;
        }

        List<String> todayHitIds = firebaseService.fetchSongIdsFromPlaylist("SYSTEM_TODAY'S_HITS");
        List<Song> songs = new ArrayList<>();
        
        if (todayHitIds != null && !todayHitIds.isEmpty()) {
            songs = firebaseService.fetchSongsByIds(todayHitIds);
            Collections.shuffle(songs);
            
            int limit = Math.min(6, songs.size());
            songs = new ArrayList<>(songs.subList(0, limit));
            trendingCache = songs; 
        }
        return songs;
    }

    public List<Album> getNewestAlbums() {
        List<Album> allAlbums = firebaseService.fetchAlbums();
        List<Album> result = new ArrayList<>();
        if (allAlbums == null || allAlbums.isEmpty()) return result;
        /*
         * Sorting array list of albums to : 

        allAlbums.sort((a, b) -> Integer.compare(b.getReleaseYear(), a.getReleaseYear()));
        Set<String> seenArtists = new HashSet<>();
        
        for (Album a : allAlbums) {
            String artist = a.getArtist();
            if (!seenArtists.contains(artist)) { 
                result.add(a);
                seenArtists.add(artist);
            }
            if (result.size() == 5) break; 
        }
        */
        
        // Using priority queue
        PriorityQueue<Album> pq = new PriorityQueue<>(new Comparator<Album>() {
        	@Override
        	public int compare(Album a, Album b) {
        		if(a.getReleaseYear() == b.getReleaseYear()) {
        			return b.getAlbumId().compareTo(a.getAlbumId());
        		}
        		return Integer.compare(a.getReleaseYear(), b.getReleaseYear());
        	}
        });
        for(Album alb: allAlbums) {
        	pq.offer(alb);
        	while(pq.size() > 5) pq.poll();
        }
        while(!pq.isEmpty()) {
        	result.add(pq.poll());
        }
        return result;
    }

    public List<Song> getLatestSongs() {
        List<Song> allSongs = firebaseService.fetchSongs();
        List<Song> result = new ArrayList<>();
        
        if (allSongs == null || allSongs.isEmpty()) return result;
        /*
         * Sorting array list of songs to get latest songs
        allSongs.sort((a, b) -> Integer.compare(b.getReleaseYear(), a.getReleaseYear()));
        Map<String, Integer> artistCount = new HashMap<>();
        
        for (Song s : allSongs) {
            String artist = s.getArtist();
            int count = artistCount.getOrDefault(artist, 0);
            
            if (count < 1) { 
                result.add(s);
                artistCount.put(artist, count + 1);
            }
            if (result.size() == 9) break; 
        }
        */
        
        // Using priority queue 
        PriorityQueue<Song> pq = new PriorityQueue<>((a, b) -> {
        	if(a.getReleaseYear() == b.getReleaseYear()) {
        		return b.getSongId().compareTo(a.getSongId());
        	}
        	return Integer.compare(a.getReleaseYear(), b.getReleaseYear());
        });
        
        for(Song s: allSongs) {
        	pq.offer(s);
        	while(pq.size() > 9) pq.poll();
        }
        while(!pq.isEmpty()) {
        	result.add(pq.poll());
        }
        return result;
    }
}