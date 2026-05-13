package com.musicapp.ui;

import com.google.firebase.database.FirebaseDatabase;
import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserPlaylistViewController implements Initializable, MainViewController.MainViewAware {

    @FXML private ImageView coverArtView;
    @FXML private Label playlistNameLabel;
    @FXML private Label songCountLabel;
    @FXML private Label checkboxHeaderSpacer;
    @FXML private Button playBtn;
    @FXML private Button deleteBtn;
    @FXML private VBox songListContainer;

    private final List<Song> songs = new ArrayList<>();
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private boolean deleteMode = false;
    private MainViewController mainController;
    
    private String currentPlaylistId; // ✅ CẦN LƯU ID ĐỂ BIẾT ĐƯỜNG XÓA TRÊN FIREBASE
    private List<String> favSongIds = new ArrayList<>(); // LƯU TRẠNG THÁI TIM

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    // ✅ ĐÃ SỬA: Thêm tham số playlistId
    public void setPlaylistData(String playlistId, String name, String coverURL, List<Song> songList) {
        this.currentPlaylistId = playlistId;
        playlistNameLabel.setText(name);
        songs.clear();
        songs.addAll(songList);

        if (coverURL != null && !coverURL.isEmpty()) {
            try { coverArtView.setImage(new Image(coverURL, true)); }
            catch (Exception ignored) {}
        }
        updateCount();

        // ✅ TẢI TRẠNG THÁI TIM TRƯỚC KHI VẼ GIAO DIỆN
        new Thread(() -> {
            try {
                if (SessionManager.currentUser != null) {
                    String favId = "fav_" + SessionManager.currentUser.getUserId();
                    favSongIds = DatabaseManager.getInstance().getService().fetchSongIdsFromPlaylist(favId);
                }
                Platform.runLater(this::buildRows);
            } catch (Exception e) {
                Platform.runLater(this::buildRows);
            }
        }).start();
    }

    @FXML
    private void handlePlay() {
        if (songs.isEmpty()) return;
        playSong(songs.get(0), 0);
    }

    @FXML
    private void handleDelete() {
        if (!deleteMode) {
            deleteMode = true;
            deleteBtn.setText("CONFIRM");
            buildRows();
        } else {
            List<Song> toRemove = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    toRemove.add(songs.get(i));
                }
            }
            songs.removeAll(toRemove);

            // ✅ XÓA KHỎI FIREBASE
            if (currentPlaylistId != null && !toRemove.isEmpty()) {
                new Thread(() -> {
                    try {
                        for (Song s : toRemove) {
                            FirebaseDatabase.getInstance().getReference("playlists")
                                .child(currentPlaylistId).child("songIds").child(s.getSongId()).removeValueAsync();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            deleteMode = false;
            deleteBtn.setText("DELETE");
            updateCount();
            buildRows();
        }
    }

    private void buildRows() {
        songListContainer.getChildren().clear();
        checkBoxes.clear();

        checkboxHeaderSpacer.setVisible(deleteMode);
        checkboxHeaderSpacer.setManaged(deleteMode);

        for (int i = 0; i < songs.size(); i++) {
            songListContainer.getChildren().add(buildRow(songs.get(i), i));
        }
    }

    private HBox buildRow(Song song, int index) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(rowStyle(false));

        CheckBox cb = new CheckBox();
        cb.setVisible(deleteMode);
        cb.setManaged(deleteMode);
        cb.setPrefWidth(32);
        HBox.setMargin(cb, new javafx.geometry.Insets(0, 0, 0, 40));
        checkBoxes.add(cb);

        Label numberLabel = new Label(String.valueOf(index + 1));
        numberLabel.setPrefWidth(40);
        numberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        if (!deleteMode) HBox.setMargin(numberLabel, new javafx.geometry.Insets(0, 0, 0, 40));

        ImageView thumb = new ImageView();
        thumb.setFitWidth(44);
        thumb.setFitHeight(44);
        thumb.setPreserveRatio(true);
        if (song.getImageURL() != null && !song.getImageURL().isEmpty()) {
            try { thumb.setImage(new Image(song.getImageURL(), true)); }
            catch (Exception ignored) {}
        }

        Label titleLabel = new Label(song.getTitle());
        titleLabel.setPrefWidth(220);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a; -fx-padding: 0 0 0 12;");
        titleLabel.setOnMouseClicked(e -> playSong(song, index));

        // ✅ LOGIC ĐỒNG BỘ TIM
        boolean isFav = favSongIds.contains(song.getSongId());
        Button heartBtn = new Button(isFav ? "♥" : "♡");
        String heartColor = isFav ? "#C0703A" : "#C0C0C0";
        heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + heartColor + "; -fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
        heartBtn.setOnAction(e -> handleAddToFavorites(song, heartBtn));

        Label artistLabel = new Label(song.getArtist());
        artistLabel.setPrefWidth(160);
        artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Label genreLabel = new Label(song.getGenre());
        genreLabel.setPrefWidth(120);
        genreLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label durationLabel = new Label(formatDuration(song.getDuration()));
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40;");
        HBox.setMargin(durationLabel, new javafx.geometry.Insets(0, 40, 0, 0));

        row.getChildren().addAll(cb, numberLabel, thumb, titleLabel, heartBtn, artistLabel, genreLabel, spacer, durationLabel);

        row.setOnMouseClicked(e -> {
            if (e.getTarget() != cb && e.getTarget() != heartBtn) {
                playSong(song, index);
            }
        });

        row.setOnMouseEntered(e -> row.setStyle(rowStyle(true)));
        row.setOnMouseExited(e ->  row.setStyle(rowStyle(false)));

        return row;
    }

    private void playSong(Song song, int index) {
        if (mainController != null) {
            mainController.showPlayerBar(song, songs, index);
        }
    }

    // ✅ GỌI API TOGGLE TIM
    private void handleAddToFavorites(Song song, Button heartBtn) {
        if (SessionManager.currentUser == null) return;
        
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
        
        DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), song);
    }

    private void updateCount() {
        songCountLabel.setText(songs.size() + " songs");
    }

    private String rowStyle(boolean hovered) {
        return "-fx-padding: 8 0 8 0; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0;" +
               (hovered ? " -fx-background-color: #f5efe9;" : "") + " -fx-cursor: hand;";
    }

    private String formatDuration(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}