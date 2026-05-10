package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class PlaybackService {
    private static PlaybackService instance;

    private LinkedList<Song> nextqueue; // play next song
    private Stack<Song> history;        // play prev song
    private Song currentSong;           // current playing song
    
    private PlaybackService() {
        this.nextqueue = new LinkedList<>();
        this.history = new Stack<>();
    }
    
    public static PlaybackService getInstance() {
        if (instance == null) {
            instance = new PlaybackService();
        }
        return instance;
    }
    
    public void setPlaylist(List<Song> allSongs, int startIndex) {
        nextqueue.clear(); 
        
        if (allSongs != null && !allSongs.isEmpty() && startIndex >= 0 && startIndex < allSongs.size()) {
            for (int i = startIndex + 1; i < allSongs.size(); i++) {
                nextqueue.addLast(allSongs.get(i));
            }
        }
    }
    
    public void play(Song song) {
        if (song == null) return;
        
     
        if (this.currentSong != null && !this.currentSong.getSongId().equals(song.getSongId())) {
            history.push(this.currentSong);
        }
        this.currentSong = song;
        System.out.println("Currently playing: " + currentSong.getTitle());
    }
    
    public Song next() {
        if (nextqueue.isEmpty()) {
            System.out.println("Off the waiting list");
            return null; 
        }
        
        if (currentSong != null) {
            history.push(currentSong); 
        }
        
        currentSong = nextqueue.poll(); 
        System.out.println("Move to the next song: " + currentSong.getTitle());
        return currentSong;
    }
    
    public Song previous() {
        if (history.isEmpty()) {
            System.out.println("No previous song played");
            return null; 
        }
        
        if (currentSong != null) {
            nextqueue.addFirst(currentSong); 
        }
        
        currentSong = history.pop(); 
        System.out.println("Move to the previous song: " + currentSong.getTitle());
        return currentSong;
    }
    
    public void enqueue(Song song) {
        nextqueue.addLast(song);
    }
    
    public Song getCurrentSong() {
        return currentSong;
    }
    
    public void clearQueue() {
        this.nextqueue.clear();
        System.out.println("Clear next queue");
    }
}