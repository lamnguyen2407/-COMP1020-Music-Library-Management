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
import javafx.scene.Node;

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

    // ✅ THÊM 3 BIẾN NÀY ĐỂ CHỐNG LỖI "BÓNG MA" (RÒ RỈ BỘ NHỚ)
    private javafx.scene.media.MediaPlayer currentPlayer;
    private javafx.beans.value.ChangeListener<Number> volumeListener;
    private javafx.beans.value.ChangeListener<javafx.util.Duration> timeListener;

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    public void showBar() {
        if (playerBar != null) {
            playerBar.setVisible(true);
            playerBar.setManaged(true);
        }
    }

    // ✅ HÀM CẮM DÂY ĐÃ ĐƯỢC CHỐNG ĐẠN 100%
    public void bindMediaPlayer(javafx.scene.media.MediaPlayer player) {
        // 1. NGẮT DÂY ĐIỆN VỚI CÁI LOA CŨ (RẤT QUAN TRỌNG)
        if (this.currentPlayer != null) {
            if (volumeListener != null && volumeSlider != null) {
                volumeSlider.valueProperty().removeListener(volumeListener);
            }
            if (timeListener != null) {
                this.currentPlayer.currentTimeProperty().removeListener(timeListener);
            }
        }

        this.currentPlayer = player;
        if (player == null) return;

        // 2. CẮM DÂY CHO LOA MỚI VÀ GẮN ÁO GIÁP BẢO VỆ
        if (volumeSlider != null) {
            volumeListener = (obs, oldVal, newVal) -> {
                try {
                    // Chỉ chỉnh âm lượng khi loa đang hoạt động, không chạm vào loa đã bị Hủy (DISPOSED)
                    if (this.currentPlayer.getStatus() != javafx.scene.media.MediaPlayer.Status.DISPOSED 
                        && this.currentPlayer.getStatus() != javafx.scene.media.MediaPlayer.Status.UNKNOWN) {
                        this.currentPlayer.setVolume(newVal.doubleValue() / 100.0);
                    }
                } catch (Exception e) { /* Bỏ qua lỗi vặt nếu loa đang giật lag */ }
            };
            volumeSlider.valueProperty().addListener(volumeListener);
        }

        // Cắm dây thanh chạy tiến độ nhạc
        timeListener = (obs, oldTime, newTime) -> {
            if (progressSlider != null && !progressSlider.isPressed()) {
                progressSlider.setValue(newTime.toSeconds());
            }
            if (labelCurrentTime != null) {
                labelCurrentTime.setText(formatDuration((int) newTime.toSeconds()));
            }
        };
        player.currentTimeProperty().addListener(timeListener);

        // 3. KHI LOA ĐÃ NẠP XONG NHẠC THÌ MỚI LẤY THÔNG SỐ
        player.setOnReady(() -> {
            try {
                if (volumeSlider != null) {
                    player.setVolume(volumeSlider.getValue() / 100.0);
                }
                double totalSecs = player.getTotalDuration().toSeconds();
                if (progressSlider != null) progressSlider.setMax(totalSecs);
                if (labelTotalTime != null) labelTotalTime.setText(formatDuration((int) totalSecs));
            } catch (Exception e) {}
        });
    }

    @FXML 
    public void onSeek() { 
        if (mainController != null && progressSlider != null) {
            mainController.seekAudio(progressSlider.getValue());
        }
    }

    private String formatDuration(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    public void setSongData(Song song) {
        if (song == null) return;
        
        this.isFavorite = false; 
        this.currentSongModel = song;
        updateHeartUI(); 

        if (playerSongTitle != null) playerSongTitle.setText(song.getTitle());
        if (playerArtistName != null) playerArtistName.setText(song.getArtist());
        if (btnPlayPause != null) btnPlayPause.setText("⏸"); 

        if (song.getImageURL() != null && !song.getImageURL().isEmpty() && playerArtImage != null) {
            try {
                if (song.getImageURL().startsWith("/")) {
                    playerArtImage.setImage(new Image(getClass().getResourceAsStream(song.getImageURL())));
                } else {
                    playerArtImage.setImage(new Image(song.getImageURL()));
                }
            } catch (Exception e) {}
        }

        if (SessionManager.currentUser != null && btnLike != null && song.getSongId() != null) {
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("playlists")
                .child("fav_" + SessionManager.currentUser.getUserId())
                .child("songIds")
                .child(song.getSongId())
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                        isFavorite = snapshot.exists();
                        Platform.runLater(() -> updateHeartUI());
                    }
                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError error) {}
                });
        }
    }

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

    @FXML 
    public void onNext() { 
        Song nextSong = PlaybackService.getInstance().next();
        if (nextSong != null && mainController != null) {
            mainController.playSongFromService(nextSong); 
        }
    }

    @FXML 
    public void onPrev() { 
        Song prevSong = PlaybackService.getInstance().previous();
        if (prevSong != null && mainController != null) {
            mainController.playSongFromService(prevSong);
        }
    }

    @FXML public void onShuffle() { System.out.println("🔀 Shuffle mode"); }
    @FXML public void onRepeat() { System.out.println("↻ Repeat mode"); }
}