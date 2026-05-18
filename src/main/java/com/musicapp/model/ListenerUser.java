package com.musicapp.model;

import java.util.*;

public class ListenerUser extends User {
    private Map<String, Boolean> playlistIds = new HashMap<>();
    private String fullname;
    
    public ListenerUser() {
        super(); 
        this.playlistIds = new HashMap<>();
        this.setRole("listener");
    }

    public ListenerUser(String userId, String fullname, String email, String username, String password) {
        super(userId, email, username, password);
        this.fullname = fullname;
        this.playlistIds = new HashMap<>();
        this.setRole("listener");
    }

    public Map<String, Boolean> getPlaylistIds() { 
        if (this.playlistIds == null) {
            this.playlistIds = new HashMap<>();
        }
        return this.playlistIds;
    }

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPlaylistIds(Map<String, Boolean> playlistIds) { 
        this.playlistIds = playlistIds; 
    }

    public void addPlaylistId(String playlistId) {
        this.playlistIds.put(playlistId, true);
    }

    public void removePlaylistId(String playlistId) {
        if (this.playlistIds != null) {
            this.playlistIds.remove(playlistId);
        }
    }
}