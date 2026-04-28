package com.musicapp.service;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import java.util.HashMap;

public class PlaylistManager {
    
    private HashMap<String, Playlist> playlistMap;
    private Playlist currentPlaylist;
    private String currentUserId; 

    public PlaylistManager(String userId) {
        this.playlistMap = new HashMap<>();
        this.currentUserId = userId;
    }

    public Playlist createPlaylist(String name) {
        Playlist newPlaylist = new Playlist(name, currentUserId);
        
        playlistMap.put(newPlaylist.getPlaylistId(), newPlaylist);
        this.currentPlaylist = newPlaylist;
        
        // Sync with Firebase immediately
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, newPlaylist);
        
        return newPlaylist;
    }

    public void addToPlaylist(Song song) {
        if (currentPlaylist != null && song != null) {
            // Extract the ID from the Song object and add it
            currentPlaylist.addSongToPlaylist(song.getSongId());
            
            // Sync the updated playlist to Firebase
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("Song added and playlist synced to Firebase.");
        } else {
            System.err.println("Error: Please select a valid playlist and song.");
        }
    }

    public void removeFromPlaylist(String songId) {
        if (currentPlaylist != null && songId != null) {
            currentPlaylist.removeSongFromPlaylist(songId);
            
            // Sync the updated playlist to Firebase
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("Song removed and playlist synced to Firebase.");
        } else {
            System.err.println("Error: Cannot remove song. Playlist or Song ID is null.");
        }
    }

    public Playlist getPlaylist(String id) {
        Playlist p = playlistMap.get(id);
        if (p != null) {
            this.currentPlaylist = p;
        }
        return p;
    }
    
    public Playlist getCurrentPlaylist() {
        return this.currentPlaylist;
    }
}