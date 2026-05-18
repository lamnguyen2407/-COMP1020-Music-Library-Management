package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CompactListController implements Initializable, MainViewController.MainViewAware {

    @FXML private Label titleLabel;
    @FXML private ListView<SongListController.SongItem> songListView;

    private StackPane contentArea;
    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Latest Songs");
        songListView.setItems(FXCollections.observableArrayList());
        songListView.setCellFactory(lv -> new CompactSongCell());
    }

    public void setData(String title, ObservableList<SongListController.SongItem> songs) {
        titleLabel.setText(title);
        if (songs != null) {
            songListView.setItems(songs);
        }
    }

    private class CompactSongCell extends ListCell<SongListController.SongItem> {

        private final HBox root = new HBox(12);
        private final ImageView thumb = new ImageView();
        private final Label nameLabel = new Label();
        private final Button heartBtn = new Button();
        private final Region spacer = new Region();
        private final Label artistLabel = new Label();
        private final Label albumLabel = new Label();
        private final Button addBtn = new Button("+");
        private final Label timeLabel = new Label();

        CompactSongCell() {
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(0));
            root.setStyle("-fx-background-color: transparent;");

            thumb.setFitWidth(40);
            thumb.setFitHeight(40);
            thumb.setPreserveRatio(false);

            nameLabel.setPrefWidth(300);
            nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C1810; -fx-font-weight: bold;");

            heartBtn.setPrefWidth(50);

            HBox.setHgrow(spacer, Priority.ALWAYS);

            artistLabel.setPrefWidth(200);
            artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            albumLabel.setPrefWidth(220);
            albumLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            addBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-text-fill: #C0703A; -fx-cursor: hand; -fx-padding: 0 4 0 4;");

            timeLabel.setPrefWidth(45);
            timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9E8E84;");

            root.getChildren().addAll(thumb, nameLabel, heartBtn, spacer, artistLabel, albumLabel, addBtn, timeLabel);

            if (SessionManager.isAdmin) {
                heartBtn.setVisible(false);
                heartBtn.setManaged(false);
            }

            root.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    SongListController.SongItem item = getItem();
                    if (item != null && mainController != null) {
                        System.out.println("Loading audio track: " + item.title);
                        
                        List<Song> queue = new ArrayList<>();
                        int targetIndex = 0;
                        int counter = 0;
                        
                        for (SongListController.SongItem sItem : songListView.getItems()) {
                            Song s = new Song(sItem.songId, sItem.title, sItem.artist, sItem.genre, sItem.duration, sItem.releaseYear, sItem.audioURL, sItem.imageURL);
                            queue.add(s);
                            if (sItem.songId != null && sItem.songId.equals(item.songId)) {
                                targetIndex = counter;
                            }
                            counter++;
                        }
                        
                        if (!queue.isEmpty()) {
                            mainController.showPlayerBar(queue.get(targetIndex), queue, targetIndex);
                        }
                    }
                }
            });

            heartBtn.setOnAction(e -> {
                SongListController.SongItem item = getItem();
                if (item == null || SessionManager.currentUser == null) return;
                
                item.isFavorite = !item.isFavorite;
                updateHeartUI(item.isFavorite);
                
                Song song = new Song(item.getSongId(), item.getTitle(), item.getArtist(), 
                                     item.getGenre(), item.getDuration(), item.getReleaseYear(), 
                                     item.getAudioURL(), item.getImageURL());
                DatabaseManager.getInstance().getService().toggleFavoriteSong(SessionManager.currentUser.getUserId(), song);
            });

            addBtn.setOnAction(e -> {
                SongListController.SongItem item = getItem();
                if (item == null) return;
                showAddMenu(item, addBtn);
            });
        }

        private void updateHeartUI(boolean isFav) {
            heartBtn.setText(isFav ? "Liked" : "Like");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 12px; -fx-text-fill: " + (isFav ? "#C0703A" : "#C0C0C0") + "; -fx-cursor: hand; -fx-padding: 0 4 0 4;");
        }

        private void showAddMenu(SongListController.SongItem item, Button anchor) {
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-border-color: #E0D8D0;");
            String menuItemStyle = "-fx-font-size: 13px; -fx-text-fill: #2C1810;";

            if (SessionManager.isAdmin) {
                MenuItem deleteFromLibrary = new MenuItem("Delete from System");
                deleteFromLibrary.setStyle("-fx-text-fill: #CC3300; -fx-font-weight: bold;");
                deleteFromLibrary.setOnAction(e -> {
                    DatabaseManager.getInstance().getService().deleteSong(item.songId);
                    songListView.getItems().remove(item);
                    System.out.println("Deleted from system: " + item.title);
                });
                menu.getItems().add(deleteFromLibrary);
            } else {
                MenuItem openAddModal = new MenuItem("Add to Playlist");
                openAddModal.setStyle(menuItemStyle);
                openAddModal.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddToPlaylistModal.fxml"));
                        Parent rootNode = loader.load();
                        AddToPlaylistController modalCtrl = loader.getController();
                        
                        Song song = new Song(item.getSongId(), item.getTitle(), item.getArtist(), 
                                             item.getGenre(), item.getDuration(), item.getReleaseYear(), 
                                             item.getAudioURL(), item.getImageURL());
                        modalCtrl.initData(song);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(rootNode));
                        stage.setTitle("Add to Playlist");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        if (root.getScene() != null) {
                            stage.initOwner(root.getScene().getWindow());
                        }
                        stage.showAndWait();
                    } catch (IOException ex) {
                        System.err.println("Failed to open add to playlist modal: " + ex.getMessage());
                    }
                });

                menu.getItems().add(openAddModal);
            }

            menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
        }

        @Override
        protected void updateItem(SongListController.SongItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            nameLabel.setText(item.title);
            artistLabel.setText(item.artist);
            albumLabel.setText(item.genre); 
            timeLabel.setText(item.getDurationString());
            updateHeartUI(item.isFavorite);

            if (item.imageURL != null && !item.imageURL.isEmpty()) {
                try {
                    if (item.imageURL.startsWith("http")) {
                        thumb.setImage(new Image(item.imageURL, 40, 40, false, true));
                    } else {
                        URL resource = getClass().getResource(item.imageURL);
                        if (resource != null) {
                            thumb.setImage(new Image(resource.toExternalForm(), 40, 40, false, true));
                        } else {
                            thumb.setImage(null);
                        }
                    }
                } catch (Exception ex) {
                    thumb.setImage(null);
                }
            } else {
                thumb.setImage(null);
            }

            root.setOnMouseEntered(e -> root.setStyle("-fx-background-color: #F0EAE4; -fx-background-radius: 6;"));
            root.setOnMouseExited(e -> root.setStyle("-fx-background-color: transparent;"));

            setGraphic(root);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");
        }
    }
}