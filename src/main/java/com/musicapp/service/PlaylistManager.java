package com.musicapp.service;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PlaylistManager {
    
    private HashMap<String, Playlist> playlistMap;
    private Playlist currentPlaylist;
    private String currentUserId; 

    public PlaylistManager(String userId) {
        this.playlistMap = new HashMap<>();
        this.currentUserId = userId;
    }

    public Playlist createPlaylist(String name) {
        Playlist newPlaylist = new Playlist(name, currentUserId, "user", "/images/default_playlist.png");
        
        playlistMap.put(newPlaylist.getPlaylistId(), newPlaylist);
        this.currentPlaylist = newPlaylist;
        
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, newPlaylist);
        
        return newPlaylist;
    }

    public Playlist createPlaylist(String name, String coverImage) {
        Playlist newPlaylist = new Playlist(name, currentUserId, "user", coverImage);
        
        playlistMap.put(newPlaylist.getPlaylistId(), newPlaylist);
        this.currentPlaylist = newPlaylist;
        
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, newPlaylist);
        
        return newPlaylist;
    }

    public void addToPlaylist(Song song) {
        if (currentPlaylist != null && song != null) {
            currentPlaylist.addSongToPlaylist(song.getSongId());
            
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("Song added and synced to Firebase successfully.");
        } else {
            System.err.println("Error: Please select a valid playlist and song.");
        }
    }

    public void removeFromPlaylist(String songId) {
        if (currentPlaylist != null && songId != null) {
            currentPlaylist.removeSongFromPlaylist(songId);
            
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("Song removed and synced to Firebase successfully.");
        } else {
            System.err.println("Error: Cannot remove. Playlist or Song ID is null.");
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
    
    public void addSongToSpecificPlaylist(String playlistId, Song song) {
        Playlist target = playlistMap.get(playlistId);
        if (target == null) {
            System.err.println("Cannot find playlist ID: " + playlistId);
            return;
        }

        target.addSongToPlaylist(song.getSongId());
        
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, target);
        System.out.println("Added " + song.getTitle() + " to playlist " + target.getName());
    }

    public List<Playlist> getAllUserPlaylists() {
        if (playlistMap.isEmpty()) {
            List<Playlist> fetched = DatabaseManager.getInstance().getService().fetchUserPlaylists(currentUserId);
            if (fetched != null) {
                for (Playlist p : fetched) {
                    playlistMap.put(p.getPlaylistId(), p);
                }
            }
        }
        return new ArrayList<>(playlistMap.values());
    }

    public void deletePlaylist(String playlistId) {
        if (playlistId != null) {
            playlistMap.remove(playlistId);
            
            DatabaseManager.getInstance().getService().deletePlaylist(currentUserId, playlistId);
            
            System.out.println("Playlist " + playlistId + " has been deleted.");
        }
    }
    
    
}