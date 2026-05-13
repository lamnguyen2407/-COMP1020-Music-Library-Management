package com.musicapp.ui;

import com.musicapp.model.Song;
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

public class UserPlaylistViewController implements Initializable,
        MainViewController.MainViewAware {

    // ── FXML bindings ──────────────────────────────────────────────────────────
    @FXML private ImageView coverArtView;
    @FXML private Label     playlistNameLabel;
    @FXML private Label     songCountLabel;
    @FXML private Label     checkboxHeaderSpacer;
    @FXML private Button    playBtn;
    @FXML private Button    deleteBtn;
    @FXML private VBox      songListContainer;

    // ── State ──────────────────────────────────────────────────────────────────
    private final List<Song>     songs        = new ArrayList<>();
    private final List<CheckBox> checkBoxes   = new ArrayList<>();
    private boolean              deleteMode   = false;
    private MainViewController   mainController;

    // ── MainViewAware ──────────────────────────────────────────────────────────
    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Data is injected via setPlaylistData()
    }

    // ── Public API ─────────────────────────────────────────────────────────────
    public void setPlaylistData(String name, String coverURL, List<Song> songList) {
        playlistNameLabel.setText(name);
        songs.clear();
        songs.addAll(songList);

        if (coverURL != null && !coverURL.isEmpty()) {
            try { coverArtView.setImage(new Image(coverURL, true)); }
            catch (Exception ignored) {}
        }

        updateCount();
        buildRows();
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    @FXML
    private void handlePlay() {
        if (songs.isEmpty()) return;
        playSong(songs.get(0), 0);
    }

    @FXML
    private void handleDelete() {
        if (!deleteMode) {
            // Enter delete mode
            deleteMode = true;
            deleteBtn.setText("CONFIRM");
            buildRows();
        } else {
            // Confirm — remove checked songs
            List<Song> toRemove = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    toRemove.add(songs.get(i));
                }
            }
            songs.removeAll(toRemove);

            // TODO: Remove from Firebase
            // Example:
            //   FirebaseService.removeSongsFromPlaylist(playlistId, toRemove);

            deleteMode = false;
            deleteBtn.setText("DELETE");
            updateCount();
            buildRows();
        }
    }

    // ── Build rows ─────────────────────────────────────────────────────────────
    private void buildRows() {
        songListContainer.getChildren().clear();
        checkBoxes.clear();

        // Show/hide checkbox header spacer
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

        // ── Checkbox (delete mode only) ──
        CheckBox cb = new CheckBox();
        cb.setVisible(deleteMode);
        cb.setManaged(deleteMode);
        cb.setPrefWidth(32);
        HBox.setMargin(cb, new javafx.geometry.Insets(0, 0, 0, 40));
        checkBoxes.add(cb);

        // ── # number ──
        Label numberLabel = new Label(String.valueOf(index + 1));
        numberLabel.setPrefWidth(40);
        numberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        if (!deleteMode) HBox.setMargin(numberLabel, new javafx.geometry.Insets(0, 0, 0, 40));

        // ── Thumbnail ──
        ImageView thumb = new ImageView();
        thumb.setFitWidth(44);
        thumb.setFitHeight(44);
        thumb.setPreserveRatio(true);
        if (song.getImageURL() != null && !song.getImageURL().isEmpty()) {
            try { thumb.setImage(new Image(song.getImageURL(), true)); }
            catch (Exception ignored) {}
        }

        // ── Title (clickable) ──
        Label titleLabel = new Label(song.getTitle());
        titleLabel.setPrefWidth(220);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                            "-fx-text-fill: #1a1a1a; -fx-padding: 0 0 0 12;");
        titleLabel.setOnMouseClicked(e -> playSong(song, index));

        // ── Heart button ──
        Button heartBtn = new Button("♡");
        heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #c07840; " +
                          "-fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
        heartBtn.setOnAction(e -> handleAddToFavorites(song, heartBtn));

        // ── Artist ──
        Label artistLabel = new Label(song.getArtist());
        artistLabel.setPrefWidth(160);
        artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        // ── Genre ──
        Label genreLabel = new Label(song.getGenre());
        genreLabel.setPrefWidth(120);
        genreLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Duration ──
        Label durationLabel = new Label(formatDuration(song.getDuration()));
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40;");
        HBox.setMargin(durationLabel, new javafx.geometry.Insets(0, 40, 0, 0));

        row.getChildren().addAll(cb, numberLabel, thumb, titleLabel, heartBtn,
                                 artistLabel, genreLabel, spacer, durationLabel);

        // Click row → play (ignore clicks on checkbox and heart)
        row.setOnMouseClicked(e -> {
            if (e.getTarget() != cb && e.getTarget() != heartBtn) {
                playSong(song, index);
            }
        });

        row.setOnMouseEntered(e -> row.setStyle(rowStyle(true)));
        row.setOnMouseExited(e ->  row.setStyle(rowStyle(false)));

        return row;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void playSong(Song song, int index) {
        if (mainController != null) {
            mainController.showPlayerBar(song, songs, index);
        }
    }

    private void handleAddToFavorites(Song song, Button heartBtn) {
        heartBtn.setText("❤️");
        heartBtn.setDisable(true);

        // TODO: Add to Firebase favorite playlist
        // Example:
        //   FirebaseService.addToFavorites(SessionManager.getCurrentUserId(), song.getSongId());
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