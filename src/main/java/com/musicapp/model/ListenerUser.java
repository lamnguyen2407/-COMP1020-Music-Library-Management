package com.musicapp.model;

import java.util.*;

public class ListenerUser extends User {
    private List<String> playlistIds;
    private String fullname;
    
    public ListenerUser() {
        super(); 
        this.playlistIds = new ArrayList<>();
        this.setRole("listener");
    }

    public ListenerUser(String userId, String fullname, String email, String username, String password) {
        super(userId, email, username, password);
        this.fullname = fullname;
        this.playlistIds = new ArrayList<>();
        this.setRole("listener");
    }

    public List<String> getPlaylistIds() { 
        return playlistIds; 
    }
    public String getFullname() {
    	return this.fullname;
    }
    public void setFullname(String fullname) {
    	this.fullname = fullname;
    }
    public void setPlaylistIds(List<String> playlistIds) { 
        this.playlistIds = playlistIds; 
    }

    public void addPlaylistId(String playlistId) {
        if (!this.playlistIds.contains(playlistId)) {
            this.playlistIds.add(playlistId);
        }
    }

    public void removePlaylistId(String playlistId) {
        this.playlistIds.remove(playlistId);
    }
}