package com.musicapp.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.musicapp.model.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaybackViewController implements Initializable {

    @FXML private ImageView albumArtView;
    @FXML private Label songTitleLabel, artistLabel, genreLabel, yearLabel, playBtnIcon;
    @FXML private ProgressBar progressBar;
    @FXML private Button editSongBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- PHÂN QUYỀN ADMIN (Theo đúng định hướng Ảnh 3) ---
        // Nếu không phải Admin, ẩn nút chỉnh sửa thông tin bài hát
        if (editSongBtn != null) {
            if (!SessionManager.isAdmin) {
                editSongBtn.setVisible(false);
                editSongBtn.setManaged(false);
            } else {
                editSongBtn.setVisible(true);
                editSongBtn.setManaged(true);
            }
        }
    }

    /**
     * CẬP NHẬT: Đã sửa tên các field để khớp với SongListController.SongItem (8 attributes)
     */
    public void setSongData(SongListController.SongItem song) {
        if (song == null) return;

        // Gán dữ liệu vào các Label
        songTitleLabel.setText(song.title);  // Trước đây là col1
        artistLabel.setText(song.artist);    // Trước đây là col2
        genreLabel.setText(song.genre);      // Thuộc tính mới
        yearLabel.setText(String.valueOf(song.releaseYear)); // Sử dụng releaseYear thật thay vì hardcode 2026

        // Xử lý hiển thị ảnh bìa
        if (song.imageURL != null && !song.imageURL.isEmpty()) {
            try {
                // Kiểm tra nếu là đường dẫn resource bên trong app
                if (song.imageURL.startsWith("/")) {
                    albumArtView.setImage(new Image(getClass().getResourceAsStream(song.imageURL)));
                } else {
                    // Nếu là URL bên ngoài (Firebase/Web)
                    albumArtView.setImage(new Image(song.imageURL));
                }
            } catch (Exception e) {
                System.err.println("[Playback] Error loading image: " + song.imageURL);
                // Có thể set 1 ảnh mặc định ở đây nếu lỗi
            }
        }
    }

    @FXML 
    private void onPlayPause() { 
        if (playBtnIcon == null) return;
        
        if (playBtnIcon.getText().equals("▶")) {
            playBtnIcon.setText("⏸");
            System.out.println("[Player] Music Resumed");
        } else {
            playBtnIcon.setText("▶");
            System.out.println("[Player] Music Paused");
        }
    }

    @FXML private void onNext() { System.out.println("⏭ Skipping to Next Song"); }
    @FXML private void onPrev() { System.out.println("⏮ Returning to Previous Song"); }
    @FXML private void onShuffle() { System.out.println("🔀 Shuffle mode toggled"); }
    @FXML private void onRepeat() { System.out.println("↻ Repeat mode toggled"); }

    @FXML 
    private void onEditSong() {
        System.out.println("[Admin] Opening AddSongModal in 'Edit Mode' for: " + songTitleLabel.getText());
        // Sau này chỗ này sẽ gọi AddSongModalController và truyền data vào để Admin sửa
    }
}