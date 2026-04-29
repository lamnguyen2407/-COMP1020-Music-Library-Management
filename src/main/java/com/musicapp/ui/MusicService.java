package com.musicapp.ui;

import com.musicapp.model.Album; // Sửa lỗi Album cannot be resolved
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Service to manage global song and album data.
 * Acts as a centralized "Mock Database".
 */
public class MusicService {

    // Kho nhạc tổng của hệ thống
    private static final ObservableList<SongListController.SongItem> globalLibrary = FXCollections.observableArrayList();

    // 1. Khai báo danh sách album tĩnh (Đã đưa vào trong class)
    private static final ObservableList<Album> albums = FXCollections.observableArrayList();

    /**
     * Khởi tạo dữ liệu mẫu cho bài hát và album.
     */
    static {
        // --- Dữ liệu bài hát ---
        globalLibrary.add(new SongListController.SongItem("s1", "Going Bad (feat. Drake)", "Meek Mill", "Hip-hop", 181, 2026, "mp3_url", "/images/song1.png"));
        globalLibrary.add(new SongListController.SongItem("s2", "HIGHEST IN THE ROOM", "Travis Scott", "Trap", 176, 2026, "mp3_url", "/images/song2.png"));
        globalLibrary.add(new SongListController.SongItem("s3", "Young And Beautiful", "Lana Del Rey", "Pop", 236, 2013, "mp3_url", "/images/song3.png"));
        globalLibrary.add(new SongListController.SongItem("s4", "Locked Out of Heaven", "Bruno Mars", "Pop Rock", 233, 2012, "mp3_url", "/images/bruno.png"));

        // --- Dữ liệu Album mẫu ---
        albums.add(new Album("a1", "1989 (Taylor's Version)", "Taylor Swift", 2023, "/images/Taylor_Swift_1989.png", "Pop"));
        albums.add(new Album("a2", "Divide", "Ed Sheeran", 2017, "/images/shapeofyou.jpg", "Pop"));
        albums.add(new Album("a3", "Midnights", "Taylor Swift", 2022, "", "Pop"));
        albums.add(new Album("a4", "Starboy", "The Weeknd", 2016, "", "R&B"));
    }

    /**
     * Lấy danh sách Album (Dùng cho AddAlbumModalController)
     */
    public static ObservableList<Album> getAlbums() {
        return albums;
    }

    /**
     * Lấy toàn bộ kho nhạc
     */
    public static ObservableList<SongListController.SongItem> getGlobalLibrary() {
        return globalLibrary;
    }

    /**
     * Thêm bài hát mới vào kho nhạc
     */
    public static void addSong(SongListController.SongItem newSong) {
        globalLibrary.add(newSong);
        System.out.println("[MusicService] Success! Added to global library: " + newSong.title);
    }
    
    /**
     * Xóa bài hát khỏi kho nhạc
     */
    public static void removeSong(SongListController.SongItem song) {
        globalLibrary.remove(song);
        System.out.println("[MusicService] Success! Removed from global library: " + song.title);
    }
}