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
        List<Song> allSongs = DatabaseManager.getInstance().getService().fetchSongs();
        List<Song> result = new ArrayList<>();

        if (allSongs == null || allSongs.isEmpty()) return result;


        Map<String, Song> uniqueArtistSongs = new HashMap<>();
        for (Song s : allSongs) {
            String artistName = s.getArtist(); 
      
            if (!uniqueArtistSongs.containsKey(artistName)) {
                uniqueArtistSongs.put(artistName, s);
            }
        }

        PriorityQueue<Song> pq = new PriorityQueue<>((a, b) -> {
            if(a.getReleaseYear() == b.getReleaseYear()) {
                return b.getSongId().compareTo(a.getSongId());
            }
            return Integer.compare(a.getReleaseYear(), b.getReleaseYear());
        });

        for (Song s : uniqueArtistSongs.values()) {
            pq.offer(s);
            while(pq.size() > 9) {
                pq.poll();
            }
        }

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }
        
        Collections.reverse(result);
        
        return result;
    }
}