package com.musicapp.service;

import com.musicapp.model.Song;
import com.musicapp.model.Album;
import com.musicapp.model.Playlist;

import java.io.File;
import java.util.List;

public interface FirebaseService {

    // Storage 
    String uploadFileToStorage(File file, String folderName) throws Exception;

    // Songs
    List<Song> fetchSongs();
    void saveSong(Song song);
    void deleteSong(String id);

    // Albums
    List<Album> fetchAlbums();
    void saveAlbum(Album album);

    // Playlists
    void saveUserPlaylist(String userId, Playlist playlist);
}