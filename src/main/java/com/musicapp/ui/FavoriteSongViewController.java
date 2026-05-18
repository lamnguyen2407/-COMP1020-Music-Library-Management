package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

public class FavoriteSongViewController implements Initializable, MainViewController.MainViewAware {

    @FXML private ImageView coverArtView;
    @FXML private Label songCountLabel;
    @FXML private VBox songListContainer;
    @FXML private Button playAllBtn;

    private final List<Song> favoriteSongs = new ArrayList<>();
    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playAllBtn.setOnAction(e -> playAll());
        loadFavoriteSongs();
    }

    public void loadFavoriteSongs() {
        if (SessionManager.currentUser == null) return;

        new Thread(() -> {
            try {
                String favId = "fav_" + SessionManager.currentUser.getUserId();
                List<String> songIds = DatabaseManager.getInstance().getService().fetchSongIdsFromPlaylist(favId);

                if (songIds != null && !songIds.isEmpty()) {
                    List<Song> songs = DatabaseManager.getInstance().getService().fetchSongsByIds(songIds);
                    Platform.runLater(() -> setFavoriteSongs(songs));
                } else {
                    Platform.runLater(() -> setFavoriteSongs(new ArrayList<>()));
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch favorite songs payload: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void setFavoriteSongs(List<Song> songs) {
        favoriteSongs.clear();
        favoriteSongs.addAll(songs);
        buildRows();
        updateCount();
    }

    private void buildRows() {
        songListContainer.getChildren().clear();
        for (int i = 0; i < favoriteSongs.size(); i++) {
            songListContainer.getChildren().add(buildRow(favoriteSongs.get(i), i));
        }
    }

    private HBox buildRow(Song song, int index) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(rowStyle(false));

        Label numberLabel = new Label(String.valueOf(index + 1));
        numberLabel.setPrefWidth(40);
        numberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        ImageView thumb = new ImageView();
        thumb.setFitWidth(40);
        thumb.setFitHeight(40);
        thumb.setPreserveRatio(true);
        if (song.getImageURL() != null && !song.getImageURL().isEmpty()) {
            try { 
                if (song.getImageURL().startsWith("http")) {
                    thumb.setImage(new Image(song.getImageURL(), true)); 
                } else {
                    URL resource = getClass().getResource(song.getImageURL());
                    if (resource != null) thumb.setImage(new Image(resource.toExternalForm(), true));
                }
            } catch (Exception ignored) {}
        }

        Label titleLabel = new Label(song.getTitle());
        titleLabel.setPrefWidth(220);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a; -fx-padding: 0 0 0 12;");

        // The Heart Icon ("tim") stays clean and intact here
        Button heartBtn = new Button("♥");
        heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 14px; -fx-border-width: 0;");

        Label artistLabel = new Label(song.getArtist());
        artistLabel.setPrefWidth(180);
        artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Label genreLabel = new Label(song.getGenre());
        genreLabel.setPrefWidth(160);
        genreLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // A proper, dedicated rectangular Delete Button positioned safely near the end of the row
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> removeFromFavorites(song));

        Label durationLabel = new Label(formatDuration(song.getDuration()));
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40; -fx-alignment: center-right;");

        row.getChildren().addAll(numberLabel, thumb, titleLabel, heartBtn, artistLabel, genreLabel, spacer, deleteBtn, durationLabel);
        
        HBox.setMargin(deleteBtn, new Insets(0, 20, 0, 0));
        HBox.setMargin(durationLabel, new Insets(0, 40, 0, 0));
        HBox.setMargin(numberLabel,   new Insets(0, 0, 0, 40));

        row.setOnMouseClicked(e -> {
            if (e.getTarget() != deleteBtn) {
                playSong(song, index);
            }
        });

        row.setOnMouseEntered(e -> row.setStyle(rowStyle(true)));
        row.setOnMouseExited(e ->  row.setStyle(rowStyle(false)));

        return row;
    }

    private void playAll() {
        if (mainController != null && !favoriteSongs.isEmpty()) {
            mainController.showPlayerBar(favoriteSongs.get(0), favoriteSongs, 0);
        }
    }

    private void playSong(Song song, int index) {
        if (mainController != null) {
            mainController.showPlayerBar(song, favoriteSongs, index);
        }
    }

    private void removeFromFavorites(Song song) {
        favoriteSongs.remove(song);
        buildRows();
        updateCount();

        if (SessionManager.currentUser != null) {
            DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), song);
        }
    }

    private void updateCount() {
        songCountLabel.setText(favoriteSongs.size() + " songs");
    }

    private String rowStyle(boolean hovered) {
        return "-fx-padding: 8 0 8 0; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0;" +
               (hovered ? " -fx-background-color: #f5efe9;" : "") + " -fx-cursor: hand;";
    }

    private String formatDuration(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}