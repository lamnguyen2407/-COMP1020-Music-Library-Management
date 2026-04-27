package com.musicapp.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


// Nếu file MusicService của mày nằm khác thư mục với SongListController thì PHẢI CÓ dòng này:
import com.musicapp.ui.SongListController;
/**
 * Service to manage global song data for Interim Report demo.
 * Acts as a centralized "Mock Database".
 */
public class MusicService {

    // Kho nhạc tổng của hệ thống
    private static final ObservableList<SongListController.SongItem> globalLibrary = FXCollections.observableArrayList();

    /**
     * Khởi tạo một số bài hát có sẵn để demo tính năng Search và Playback.
     * Đủ 8 thuộc tính: id, title, artist, genre, duration(s), year, audioURL, imageURL
     */
    static {
        // Những bài Hits 2026
        globalLibrary.add(new SongListController.SongItem("s1", "Going Bad (feat. Drake)", "Meek Mill", "Hip-hop", 181, 2026, "mp3_url", "/images/song1.png"));
        globalLibrary.add(new SongListController.SongItem("s2", "HIGHEST IN THE ROOM", "Travis Scott", "Trap", 176, 2026, "mp3_url", "/images/song2.png"));
        
        // Vài bài kinh điển để test tính năng tìm kiếm
        globalLibrary.add(new SongListController.SongItem("s3", "Young And Beautiful", "Lana Del Rey", "Pop", 236, 2013, "mp3_url", "/images/song3.png"));
        globalLibrary.add(new SongListController.SongItem("s4", "Locked Out of Heaven", "Bruno Mars", "Pop Rock", 233, 2012, "mp3_url", "/images/bruno.png"));
    }

    /**
     * Lấy toàn bộ kho nhạc (Dùng cho màn hình "All Songs" của Admin và tìm kiếm)
     */
    public static ObservableList<SongListController.SongItem> getGlobalLibrary() {
        return globalLibrary;
    }

    /**
     * Thêm bài hát mới vào kho nhạc (Dùng khi Admin bấm SAVE ở AddSongModal)
     */
    public static void addSong(SongListController.SongItem newSong) {
        globalLibrary.add(newSong);
        System.out.println("[MusicService] Success! Added to global library: " + newSong.title);
    }
    
    /**
     * Xóa bài hát khỏi kho nhạc (Dùng khi Admin bấm DELETE ở SongList)
     */
    public static void removeSong(SongListController.SongItem song) {
        globalLibrary.remove(song);
        System.out.println("[MusicService] Success! Removed from global library: " + song.title);
    }
}