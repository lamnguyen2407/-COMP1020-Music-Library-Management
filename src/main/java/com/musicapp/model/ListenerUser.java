package com.musicapp.model;

import java.util.*;

public class ListenerUser extends User {
    private List<String> playlistIds;

    public ListenerUser() {
        super(); 
        this.playlistIds = new ArrayList<>();
    }

    public ListenerUser(String userId, String email, String name, String password) {
        super(userId, email, name, password);
        this.playlistIds = new ArrayList<>();
    }

    public List<String> getPlaylistIds() { 
        return playlistIds; 
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