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
        return result;
    }

    public List<Song> getLatestSongs() {
        List<Song> allSongs = firebaseService.fetchSongs();
        List<Song> result = new ArrayList<>();
        
        if (allSongs == null || allSongs.isEmpty()) return result;

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
        return result;
    }
}