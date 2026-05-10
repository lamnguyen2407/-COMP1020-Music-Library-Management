package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumViewController {

    // ── FXML bindings ──────────────────────────────────────────────────────────
    @FXML private ImageView albumArtView;
    @FXML private Label albumNameLabel;
    @FXML private Label artistLabel;
    @FXML private Label metaLabel;
    @FXML private Button playBtn;
    @FXML private VBox songListContainer;

    // ── State ──────────────────────────────────────────────────────────────────
    private List<Song> songs;
    private MainViewController mainController;
    private List<String> favSongIds = new ArrayList<>(); // BIẾN MỚI: Lưu danh sách tim

    // ── Setter: reference to MainViewController (for showing player bar) ───────
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    // ── Setter: album data ─────────────────────────────────────────────────────
    public void setAlbumData(String albumName, String artist, String genre, int year, String imageURL, List<Song> songs) {
        this.songs = songs;

        albumNameLabel.setText(albumName);
        artistLabel.setText(artist);
        metaLabel.setText(genre + " • " + year);

        if (imageURL != null && !imageURL.isEmpty()) {
            try {
                albumArtView.setImage(new Image(imageURL, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ✅ LẤY DỮ LIỆU TIM TRƯỚC KHI VẼ GIAO DIỆN
        new Thread(() -> {
            try {
                if (SessionManager.currentUser != null) {
                    String favId = "fav_" + SessionManager.currentUser.getUserId();
                    favSongIds = DatabaseManager.getInstance().getService().fetchSongIdsFromPlaylist(favId);
                }
                // Lấy xong mới bắt đầu vẽ danh sách bài hát
                Platform.runLater(this::buildSongRows);
            } catch (Exception e) {
                System.err.println("Lỗi load tim trong Album: " + e.getMessage());
                Platform.runLater(this::buildSongRows);
            }
        }).start();
    }

    // ── Build song rows dynamically ────────────────────────────────────────────
    private void buildSongRows() {
        songListContainer.getChildren().clear();

        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            HBox row = buildRow(song, i);
            songListContainer.getChildren().add(row);
        }
    }

    private HBox buildRow(Song song, int index) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

        // ── Title (clickable) ──
        Label titleLabel = new Label(song.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");
        titleLabel.setOnMouseClicked(e -> handleSongClick(song, index));

        // ── Heart button ──
        // ✅ KIỂM TRA TRẠNG THÁI TIM NGAY TỪ ĐẦU
        boolean isFav = favSongIds.contains(song.getSongId());
        Button heartBtn = new Button(isFav ? "♥" : "♡");
        
        if (SessionManager.isAdmin) {
            heartBtn.setVisible(false);
            heartBtn.setManaged(false);
        }
        
        // Setup màu chuẩn như bên SongListController
        String heartColor = isFav ? "#C0703A" : "#C0C0C0";
        heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + heartColor + "; -fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
        
        // Truyền cả nút bấm vào hàm để đổi màu ngay lập tức
        heartBtn.setOnAction(e -> handleAddToFavorites(song, heartBtn));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Plus button with dropdown ──
        Button plusBtn = new Button("+");
        if (SessionManager.isAdmin) {
            plusBtn.setVisible(false);
            plusBtn.setManaged(false);
        }
        plusBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-font-size: 16px; -fx-cursor: hand; -fx-border-width: 0;");

        ContextMenu dropdown = new ContextMenu();
        MenuItem addToPlaylist = new MenuItem("Add to playlist");
        addToPlaylist.setOnAction(e -> handleAddToPlaylist(song));
        dropdown.getItems().add(addToPlaylist);

        plusBtn.setOnAction(e -> dropdown.show(plusBtn, plusBtn.localToScreen(0, plusBtn.getHeight()).getX(), plusBtn.localToScreen(0, plusBtn.getHeight()).getY()));

        // ── Duration ──
        Label durationLabel = new Label(formatDuration(song.getDuration()));
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40;");

        row.getChildren().addAll(titleLabel, heartBtn, spacer, plusBtn, durationLabel);

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0; -fx-background-color: #f5efe9; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));

        return row;
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    @FXML
    private void handlePlay() {
        if (songs == null || songs.isEmpty()) return;
        playSong(songs.get(0), 0);
    }

    private void handleSongClick(Song song, int index) {
        playSong(song, index);
    }

    private void playSong(Song song, int index) {
        if (mainController != null) {
            mainController.showPlayerBar(song, songs, index);
        }
    }

    // ✅ ĐÃ FIX: Xử lý logic Thả Tim
    private void handleAddToFavorites(Song song, Button heartBtn) {
        if (SessionManager.currentUser == null) return;

        // Đảo trạng thái UI ngay lập tức
        boolean currentlyFav = heartBtn.getText().equals("♥");
        if (currentlyFav) {
            heartBtn.setText("♡");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
            favSongIds.remove(song.getSongId());
        } else {
            heartBtn.setText("♥");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
            favSongIds.add(song.getSongId());
        }

        // Gọi API lên Firebase
        DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), song);
    }

    // ✅ ĐÃ FIX: Xử lý logic Add To Playlist Modal
    private void handleAddToPlaylist(Song song) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddToPlaylistModal.fxml"));
            Parent root = loader.load();

            AddToPlaylistController controller = loader.getController();
            controller.initData(song);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add to Playlist");
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Gán owner để modal hiện đè lên trên app thay vì cửa sổ rời
            if (songListContainer != null && songListContainer.getScene() != null) {
                stage.initOwner(songListContainer.getScene().getWindow());
            }
            
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("❌ Lỗi mở AddToPlaylistModal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatDuration(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return m + ":" + String.format("%02d", s);
    }
}