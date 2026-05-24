package com.musicapp.service;

import com.google.firebase.database.DatabaseReference;
import com.musicapp.model.*;
import java.io.File;
import java.util.List;

public interface FirebaseService {

    String uploadFileToStorage(File file, String folderName) throws Exception;

    List<Song> fetchSongs();
    void saveSong(Song song);
    void deleteSong(String id);
    void addSongToAlbum(String albumId, String songId);
    
    List<Song> fetchSongsByIds(List<String> songIds);

    List<Album> fetchAlbums();
    void saveAlbum(Album album);
    void deleteAlbum(String albumId); 

    void saveUserPlaylist(String userId, Playlist playlist);
    Playlist fetchPlaylist(String playlistId);
    void deleteUserPlaylist(String userId, String playlistId);
    void addSongToPlaylist(String playlistId, String songId);
    
    List<String> fetchSongIdsFromPlaylist(String playlistId);
    List<String> fetchSongIdsFromAlbum(String albumId);
    
    void saveUser(User user);
    void authenticateUser(String identifier, String password, LoginCallback callback);
    List<Playlist> fetchUserPlaylists(String userId);
    void saveNewUserPlaylist(String uid, String name);
    
    DatabaseReference getDbRef();
    void toggleFavoriteSong(String userId, Song song);
    
    String getCurrentUserId();
    String getCurrentUserRole();
    void setSession(String userId, String role);
    
    void removeSongFromAlbum(String albumId, String songId);
    void removeSongFromPlaylist(String playlistId, String songId);
    
    void removeSongContextually(String contextId, String songId);
    List<Song> fetchSongsContextually(String contextId, String contextTitle);
}