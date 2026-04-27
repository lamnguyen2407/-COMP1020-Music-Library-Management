package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public class PlaybackService {
	private LinkedList<Song> nextqueue; // play next song
	private Stack<Song> history; // play prev song
	
	private Song currentSong; // current playing song
	
	public PlaybackService() {
		this.nextqueue = new LinkedList<>();
		this.history = new Stack<>();
	}
	
	public void play(Song song) {
		// if currently playing a new song, put it in the history stack before playing the next
		if (this.currentSong != null) {
			history.push(this.currentSong);
		}
		
		this.currentSong = song;
		System.out.println("Currently playing: " + currentSong.getTitle());
	}
	
	public Song next() {
		if (nextqueue.isEmpty()) {
			System.out.println("Off the waiting list");
			return currentSong;
		}
		
		currentSong = nextqueue.poll();
		System.out.println("Move to the next song " + currentSong.getTitle());
		return currentSong;
	}
	
	public Song previous() {
		if (history.isEmpty()) {
			System.out.println("No previous song played");
			return currentSong;
		}
		
		// push current song to the top of waiting list
		if (currentSong != null) {
			nextqueue.addFirst(currentSong);
		}
		
		currentSong = history.pop();
		System.out.println("Move to the previous song " + currentSong.getTitle());
		return currentSong;
	}
	
	// add a song into waiting list (queue)
	public void enqueue(Song song) {
		nextqueue.addLast(song);
	}
	
	//  support function for UI to get information to show in menu bar
	public Song getCurrentSong() {
		return currentSong;
	}
}
