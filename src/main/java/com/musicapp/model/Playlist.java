package com.musicapp.model;

import java.util.*;

public class Playlist {
	private String playlistId;
	private String ownerId;
	private String name;
	private boolean isPublic;
	private ArrayList<String> songIds;
	
	public Playlist() {
		this.songIds = new ArrayList<>();
	}

	public Playlist(String name) {
		this.playlistId = UUID.randomUUID().toString();
		this.name = name;
		this.isPublic = false;
		this.songIds = new ArrayList<>();
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	

	public String getName() {
		return name;
	}

	public boolean isPublic() {
		return isPublic;
	}


	public List<String> getsongsId() { 
		return this.songIds; 
	}
	
	public void setPlaylistId(String playlistId) { 
		this.playlistId = playlistId; 
	}
  
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
    public void setName(String name) { 
    	this.name = name; 
    }
    
    public void setPublic(boolean isPublic) { 
    	this.isPublic = isPublic; 
    }
    
    public void setSongIds(ArrayList<String> songIds) { 
    	this.songIds = songIds; 
    }
    

	public void addSongToPlaylist(String songId) {
		songIds.add(songId);
	}

	public void removeSongFromPlaylist(String songId) {
        songIds.remove(songId);
    }

	
}
