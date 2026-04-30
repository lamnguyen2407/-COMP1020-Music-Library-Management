package com.musicapp.service;

import com.musicapp.model.Album;
import com.musicapp.model.Playlist;
import com.musicapp.model.Song;

import java.io.File;
import java.util.List;

public interface FirebaseService {

    // =========================================
    // 1. STORAGE (Files & Images)
    // =========================================
    String uploadFileToStorage(File file, String folderName) throws Exception;

    // =========================================
    // 2. SONGS
    // =========================================
    List<Song> fetchSongs();
    void saveSong(Song song);
    void deleteSong(String id);
    void addSongToAlbum(String albumId, String songId);
    
    // HÀM MỚI: Lấy danh sách nhạc theo một list ID cho trước
    List<Song> fetchSongsByIds(List<String> songIds);

    // =========================================
    // 3. ALBUMS
    // =========================================
    List<Album> fetchAlbums();
    void saveAlbum(Album album);

    // =========================================
    // 4. PLAYLISTS (User & System)
    // =========================================
    void saveUserPlaylist(String userId, Playlist playlist);
    
    // HÀM MỚI: Kéo 1 playlist cụ thể từ Firebase về
    Playlist fetchPlaylist(String playlistId);
    
    void deleteUserPlaylist(String userId, String playlistId);

    void deleteAlbum(String albumId); 
    
    void addSongToPlaylist(String playlistId, String songId);
    
    public List<String> fetchSongIdsFromPlaylist(String playlistId);
    
    List<String> fetchSongIdsFromAlbum(String albumId);
    
}