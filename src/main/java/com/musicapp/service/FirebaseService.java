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

    // =========================================
    // 3. ALBUMS
    // =========================================
    List<Album> fetchAlbums();
    void saveAlbum(Album album);

    // =========================================
    // 4. PLAYLISTS (User Specific)
    // =========================================
    void saveUserPlaylist(String userId, Playlist playlist);
}