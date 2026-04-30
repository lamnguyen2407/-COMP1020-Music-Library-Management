package com.musicapp.model;

import java.util.*;

public class Playlist {
    private String playlistId;
    private String ownerId;
    private String name;
    private boolean isPublic;
    
    private Map<String, Boolean> songIds;
    
    private String coverImage; 
    
    private String type; 

    public Playlist() {
        this.songIds = new HashMap<>();
    }

    public Playlist(String name, String ownerId, String type, String coverImage) {
        this.playlistId = "PL_" + UUID.randomUUID().toString(); // Thêm tiền tố PL_ cho dễ quản lý
        this.name = name;
        this.ownerId = ownerId;
        this.isPublic = false;
        this.type = type;
        this.coverImage = coverImage;
        this.songIds = new HashMap<>();
    }

   
    public String getPlaylistId() { return playlistId; }
    public void setPlaylistId(String playlistId) { this.playlistId = playlistId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Boolean> getSongIds() { return songIds; }
    public void setSongIds(Map<String, Boolean> songIds) { this.songIds = songIds; }

   
    public List<String> getSongIdList() {
        if (this.songIds == null) return new ArrayList<>();
        return new ArrayList<>(this.songIds.keySet());
    }

    public void addSongToPlaylist(String songId) {
        if (this.songIds == null) this.songIds = new HashMap<>();
        this.songIds.put(songId, true);
    }

    public void removeSongFromPlaylist(String songId) {
        if (this.songIds != null) {
            this.songIds.remove(songId);
        }
    }
}