package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.PlaybackService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;

public class PlaybackViewController {

    @FXML private HBox playerBar;
    @FXML private ImageView playerArtImage;
    @FXML private Label playerSongTitle, playerArtistName, labelCurrentTime, labelTotalTime;
    @FXML private Button btnLike, btnPlayPause;
    @FXML private Slider progressSlider, volumeSlider;

    private MainViewController mainController;
    private Song currentSongModel;
    private boolean isFavorite = false;

    // 1. Nhận tham chiếu từ MainView
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    // 2. Hàm hiển thị thanh nhạc (Hàm mày đang báo lỗi thiếu đây)
    public void showBar() {
        if (playerBar != null) {
            playerBar.setVisible(true);
            playerBar.setManaged(true);
        } else {
            System.err.println("⚠️ Chưa tìm thấy playerBar trong FXML!");
        }
    }

    // 3. Hàm mồi dữ liệu và check tim
    public void setSongData(Song song) {
        if (song == null) return;
        this.currentSongModel = song;

        // Cập nhật text
        if (playerSongTitle != null) playerSongTitle.setText(song.getTitle());
        if (playerArtistName != null) playerArtistName.setText(song.getArtist());
        if (btnPlayPause != null) btnPlayPause.setText("⏸"); 

        // Cập nhật ảnh
        if (song.getImageURL() != null && !song.getImageURL().isEmpty() && playerArtImage != null) {
            try {
                if (song.getImageURL().startsWith("/")) {
                    playerArtImage.setImage(new Image(getClass().getResourceAsStream(song.getImageURL())));
                } else {
                    playerArtImage.setImage(new Image(song.getImageURL()));
                }
            } catch (Exception e) {}
        }

        // Check tim ngầm từ Firebase
        if (SessionManager.currentUser != null && btnLike != null) {
            new Thread(() -> {
                try {
                    String favId = "fav_" + SessionManager.currentUser.getUserId();
                    List<String> favIds = DatabaseManager.getInstance().getService().fetchSongIdsFromPlaylist(favId);
                    isFavorite = favIds.contains(song.getSongId());
                    Platform.runLater(this::updateHeartUI);
                } catch (Exception e) {}
            }).start();
        }
    }

    // 4. Logic thả tim
    @FXML 
    public void onToggleLike() {
        if (currentSongModel == null || SessionManager.currentUser == null) return;
        isFavorite = !isFavorite;
        updateHeartUI();
        DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), currentSongModel);
    }

    private void updateHeartUI() {
        if (btnLike == null) return;
        btnLike.setText(isFavorite ? "♥" : "♡");
        btnLike.setStyle(isFavorite ? "-fx-text-fill: #C0703A; -fx-background-color: transparent;" : "-fx-text-fill: #C0C0C0; -fx-background-color: transparent;");
    }

    // 5. Nút Play/Pause (Đã sửa thành public)
    @FXML 
    public void onPlayPause() { 
        if (mainController == null || btnPlayPause == null) return;
        
        if (btnPlayPause.getText().equals("▶")) {
            btnPlayPause.setText("⏸");
            mainController.resumeAudio(); 
        } else {
            btnPlayPause.setText("▶");
            mainController.pauseAudio();
        }
    }

    // 6. Nút Next (Đã sửa thành public để MainView gọi khi hết bài)
    @FXML 
    public void onNext() { 
        Song nextSong = PlaybackService.getInstance().next();
        if (nextSong != null && !nextSong.equals(currentSongModel) && mainController != null) {
            mainController.showPlayerBar(nextSong, null, 0); 
        }
    }

    // 7. Nút Prev (Đã sửa thành public)
    @FXML 
    public void onPrev() { 
        Song prevSong = PlaybackService.getInstance().previous();
        if (prevSong != null && !prevSong.equals(currentSongModel) && mainController != null) {
            mainController.showPlayerBar(prevSong, null, 0);
        }
    }

    // Các hàm phụ (cũng public cho an toàn)
    @FXML public void onShuffle() { System.out.println("🔀 Shuffle mode"); }
    @FXML public void onRepeat() { System.out.println("↻ Repeat mode"); }
    @FXML public void onSeek() { /* Chỗ này sau sẽ nhét logic tua nhạc */ }
}