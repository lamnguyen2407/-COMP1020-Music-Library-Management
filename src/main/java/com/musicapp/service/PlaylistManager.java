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

    // ==========================================
    // CÚ FIX: CẬP NHẬT CONSTRUCTOR CHO PLAYLIST
    // ==========================================
    
    // Cách 1: UI chỉ truyền Tên Playlist (Tự động gán ảnh mặc định)
    public Playlist createPlaylist(String name) {
        // Tham số: name, ownerId, type ("user"), coverImage (link mặc định)
        Playlist newPlaylist = new Playlist(name, currentUserId, "user", "/images/default_playlist.png");
        
        playlistMap.put(newPlaylist.getPlaylistId(), newPlaylist);
        this.currentPlaylist = newPlaylist;
        
        // Đẩy thẳng lên Firebase
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, newPlaylist);
        
        return newPlaylist;
    }

    // Cách 2: Dự phòng cho tương lai nếu User upload được ảnh bìa
    public Playlist createPlaylist(String name, String coverImage) {
        Playlist newPlaylist = new Playlist(name, currentUserId, "user", coverImage);
        
        playlistMap.put(newPlaylist.getPlaylistId(), newPlaylist);
        this.currentPlaylist = newPlaylist;
        
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, newPlaylist);
        
        return newPlaylist;
    }

    // ==========================================
    // LOGIC ADD/REMOVE (Giữ nguyên, đã chuẩn)
    // ==========================================

    public void addToPlaylist(Song song) {
        if (currentPlaylist != null && song != null) {
            // Lấy ID từ Song và đưa vào Map của Playlist
            currentPlaylist.addSongToPlaylist(song.getSongId());
            
            // Cập nhật lại toàn bộ Playlist đó lên Firebase
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("✅ Đã thêm bài hát và đồng bộ lên Firebase.");
        } else {
            System.err.println("❌ Lỗi: Hãy chọn một playlist và bài hát hợp lệ.");
        }
    }

    public void removeFromPlaylist(String songId) {
        if (currentPlaylist != null && songId != null) {
            // Xóa ID khỏi Map của Playlist
            currentPlaylist.removeSongFromPlaylist(songId);
            
            // Cập nhật lại Firebase
            DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, currentPlaylist);
            System.out.println("✅ Đã xóa bài hát và đồng bộ lên Firebase.");
        } else {
            System.err.println("❌ Lỗi: Không thể xóa. Playlist hoặc Song ID bị null.");
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
            System.err.println("Cannot find playlist ID " + playlistId);
            return;
        }

        target.addSongToPlaylist(song.getSongId());
        
        DatabaseManager.getInstance().getService().saveUserPlaylist(currentUserId, target);
        System.out.println("Added " + song.getTitle() + " to playlist " + target.getName());
    }
}