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

public class FavoriteSongViewController implements Initializable, MainViewController.MainViewAware {

    @FXML private ImageView coverArtView;
    @FXML private Label songCountLabel;
    @FXML private VBox songListContainer;
    @FXML private Button playAllBtn;
    @FXML private Button deleteBtn; 

    private final List<Song> favoriteSongs = new ArrayList<>();
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private boolean deleteMode = false;
    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playAllBtn.setOnAction(e -> playAll());
        deleteBtn.setOnAction(e -> handleDelete());
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

    @FXML
    private void handleDelete() {
        if (!deleteMode) {
            deleteMode = true;
            deleteBtn.setText("CONFIRM");
            deleteBtn.setStyle("-fx-background-color: #CC3300; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 32 8 32; -fx-background-radius: 4; -fx-cursor: hand;");
            buildRows();
        } else {
            List<Song> toRemove = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    toRemove.add(favoriteSongs.get(i));
                }
            }
            if (!toRemove.isEmpty()) {
                favoriteSongs.removeAll(toRemove);
                if (SessionManager.currentUser != null) {
                    new Thread(() -> {
                        try {
                            for (Song s : toRemove) {
                                DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
            deleteMode = false;
            deleteBtn.setText("DELETE");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #c07840; -fx-text-fill: #c07840; -fx-font-weight: bold; -fx-padding: 7 32 7 32; -fx-background-radius: 4; -fx-border-radius: 4; -fx-cursor: hand;");
            updateCount();
            buildRows();
        }
    }

    private void buildRows() {
        songListContainer.getChildren().clear();
        checkBoxes.clear();
        for (int i = 0; i < favoriteSongs.size(); i++) {
            songListContainer.getChildren().add(buildRow(favoriteSongs.get(i), i));
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
        HBox.setMargin(cb, new Insets(0, 0, 0, 40));
        checkBoxes.add(cb);

        Label numberLabel = new Label(String.valueOf(index + 1));
        numberLabel.setPrefWidth(40);
        numberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        if (!deleteMode) {
            HBox.setMargin(numberLabel, new Insets(0, 0, 0, 40));
        }

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

        Button heartBtn = new Button("♥");
        heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 14px; -fx-border-width: 0;");

        heartBtn.setOnAction(e -> handleAddToFavorites(song, heartBtn));
        
        Label artistLabel = new Label(song.getArtist());
        artistLabel.setPrefWidth(180);
        artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Label genreLabel = new Label(song.getGenre());
        genreLabel.setPrefWidth(160);
        genreLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label durationLabel = new Label(formatDuration(song.getDuration()));
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40;");

        row.getChildren().addAll(cb, numberLabel, thumb, titleLabel, heartBtn, artistLabel, genreLabel, spacer, durationLabel);
        HBox.setMargin(durationLabel, new Insets(0, 40, 0, 0));

        row.setOnMouseClicked(e -> {
            if (e.getTarget() != cb) {
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
    
    private void handleAddToFavorites(Song song, Button heartBtn) {
        if (SessionManager.currentUser == null) return;
        
        boolean currentlyFav = heartBtn.getText().equals("♥");
        if (currentlyFav) {
            heartBtn.setText("♡");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-font-size: 14px; -fx-border-width: 0;");
        } else {
            heartBtn.setText("♥");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 14px; -fx-border-width: 0;");
        }
        
        DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), song);
    }
}