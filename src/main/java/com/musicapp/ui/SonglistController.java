package com.musicapp.ui;

import com.musicapp.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

import com.musicapp.ui.MainViewController;



public class SongListController implements Initializable, MainViewController.MainViewAware{
	private MainViewController mainController;
	// Thêm biến này vào trong class SongListController
	private static MediaPlayer currentPlayer;
	
	
    // ==========================================
    // FXML FIELDS
    // ==========================================
    @FXML private ImageView coverImageView;
    @FXML private Label titleLabel, subtitleLabel, descriptionLabel;
    @FXML private Label col1Header, col2Header, col3Header, checkHeader;
    @FXML private Button playButton, shuffleButton, addBtn, deleteBtn;
    @FXML private ListView<SongItem> songListView;

    private boolean isDeleteMode = false;

    // ==========================================
    // DATA MODEL (Exhaustive 8 Attributes)
    // ==========================================
    public static class SongItem {
        public String songId;       // 1
        public String title;        // 2
        public String artist;       // 3
        public String genre;        // 4
        public int duration;        // 5
        public int releaseYear;     // 6
        public String audioURL;     // 7
        public String imageURL;     // 8
        
        public boolean isFavorite = false;
        public boolean isSelected = false; // Used for Delete checkbox

        public SongItem(String id, String title, String artist, String genre, int duration, int year, String audio, String image) {
            this.songId = id; this.title = title; this.artist = artist;
            this.genre = genre; this.duration = duration; this.releaseYear = year;
            this.audioURL = audio; this.imageURL = image;
        }

        // Helper to format mm:ss for UI
        public String getDurationString() {
            int mins = duration / 60;
            int secs = duration % 60;
            return String.format("%d:%02d", mins, secs);
        }
    }
    
    @Override // Bây giờ nó sẽ hiểu cái này là từ MainViewController.MainViewAware mà ra
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
        System.out.println("SongListController: Connected to MainController sucessfully");
    }
    
    private final ObservableList<SongItem> songs = FXCollections.observableArrayList();

    // ==========================================
    // CONTROLLER INITIALIZATION
    // ==========================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoleBasedUI();
        
        // If no data was injected via setData, pull from the global MusicService
        if (songs.isEmpty()) {
            songs.setAll(MusicService.getGlobalLibrary());
        }
        
        songListView.setItems(songs);
        songListView.setCellFactory(lv -> new SongCell());
    }

    private void setupRoleBasedUI() {
        if (Main.isAdmin) {
            // Admin: Show Add/Delete, Hide Play/Shuffle
            playButton.setVisible(false); playButton.setManaged(false);
            shuffleButton.setVisible(false); shuffleButton.setManaged(false);
            addBtn.setVisible(true); addBtn.setManaged(true);
            deleteBtn.setVisible(true); deleteBtn.setManaged(true);
        } else {
            // User: Show Play/Shuffle, Hide Add/Delete
            addBtn.setVisible(false); addBtn.setManaged(false);
            deleteBtn.setVisible(false); deleteBtn.setManaged(false);
            if(checkHeader != null) { checkHeader.setVisible(false); checkHeader.setManaged(false); }
        }
    }

    // ==========================================
    // ACTION HANDLERS
    // ==========================================

    @FXML
    private void onAddSongClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddSongModal.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Admin - Add New Track");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait(); 
            
            // Refresh list after modal closes (in case MusicService was updated)
            songs.setAll(MusicService.getGlobalLibrary());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteToggleClicked() {
        isDeleteMode = !isDeleteMode;
        checkHeader.setVisible(isDeleteMode);
        checkHeader.setManaged(isDeleteMode);
        songListView.refresh();

        if (isDeleteMode) {
            deleteBtn.setText("CONFIRM");
            deleteBtn.setStyle("-fx-background-color: #CC3300; -fx-text-fill: white;");
        } else {
            // Remove selected songs from both the local view and the global service
            songs.removeIf(s -> {
                if (s.isSelected) {
                    MusicService.removeSong(s);
                    return true;
                }
                return false;
            });
            deleteBtn.setText("DELETE");
            deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
        }
    }

    public void setData(String title, String sub, String desc, String cover, ObservableList<SongItem> data) {
        titleLabel.setText(title);
        subtitleLabel.setText(sub);
        descriptionLabel.setText(desc);
        if (data != null) songs.setAll(data);
    }
    
    public void setColumnHeaders(String c1, String c2, String c3) {
        col1Header.setText(c1);
        col2Header.setText(c2);
        col3Header.setText(c3);
    }

    // ==========================================
    // CUSTOM CELL (Inner Class)
    // ==========================================
    private class SongCell extends ListCell<SongItem> {
        private final HBox root = new HBox(12);
        private final CheckBox checkBox = new CheckBox();
        private final Label indexLabel = new Label();
        private final ImageView thumb = new ImageView();
        private final Label nameLabel = new Label();
        private final Button heartBtn = new Button("♡");
        private final Region spacer = new Region();
        private final Label artistLabel = new Label();
        private final Label albumLabel = new Label();
        private final Button addBtnRow = new Button("+");
        private final Label timeLabel = new Label();

        SongCell() {
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(0, 40, 0, 40));
            thumb.setFitWidth(40);
            thumb.setFitHeight(40);
            nameLabel.setPrefWidth(240); nameLabel.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(spacer, Priority.ALWAYS);
            artistLabel.setPrefWidth(180); albumLabel.setPrefWidth(200);
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0;");
            addBtnRow.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 18px; -fx-cursor: hand;");

            root.getChildren().addAll(checkBox, indexLabel, thumb, nameLabel, heartBtn, spacer, artistLabel, albumLabel, addBtnRow, timeLabel);

            // Double click to play
         // Bơm máy hát vào sự kiện Double Click
         // Double click to play
            root.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && getItem() != null) {
                    SongItem item = getItem();
                    System.out.println("▶ Bắt đầu nạp nhạc từ kho: " + item.title);
                    
                    try {
                        if (currentPlayer != null) {
                            currentPlayer.stop();
                        }

                        String path = item.audioURL;
                        if (path == null || path.isEmpty()) {
                            System.err.println("❌ Bài này chưa có link nhạc!");
                            return;
                        }

                        if (path.startsWith("http")) {
                             Media hit = new Media(path);
                             currentPlayer = new MediaPlayer(hit);
                             currentPlayer.play();
                        } 
                        else {
                             if (!path.startsWith("/")) path = "/" + path;
                             URL resourceUrl = getClass().getResource(path);
                             
                             if (resourceUrl != null) {
                                 Media hit = new Media(resourceUrl.toString());
                                 currentPlayer = new MediaPlayer(hit);
                                 currentPlayer.play();
                             } else {
                                 System.err.println("❌ Lỗi: Không tìm thấy file nhạc tại src/main/resources" + path);
                             }
                        }

                        // ==========================================
                        // 👉 THÊM ĐOẠN NÀY ĐỂ HIỆN THANH PLAYER BAR
                        // ==========================================
                        if (mainController != null) {
                            mainController.showPlayerBar(item.title, item.artist, item.imageURL);
                        } else {
                            System.err.println("⚠️ mainController đang null, không thể hiện Bar!");
                        }
                        // ==========================================

                    } catch (Exception ex) {
                        System.err.println("❌ Lỗi MediaPlayer!");
                        ex.printStackTrace();
                    }
                }
            });

            // Context Menu (Image 5 logic)
            addBtnRow.setOnAction(e -> showActionMenu(getItem(), addBtnRow));
        }

        private void showActionMenu(SongItem item, Button anchor) {
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-border-color: #E0D8D0; -fx-padding: 5;");

            if (Main.isAdmin) {
                MenuItem delete = new MenuItem("Delete from Library");
                delete.setStyle("-fx-text-fill: #CC3300; -fx-font-weight: bold;");
                delete.setOnAction(e -> {
                    MusicService.removeSong(item);
                    songs.remove(item);
                });
                menu.getItems().add(delete);
            } else {
                MenuItem fav = new MenuItem("Add to your favorite songs");
                Menu playlistSubMenu = new Menu("Add to Playlist..."); 
                
                String[] userPlaylists = {"My Chill Mix", "Workout 2026", "Roadtrip", "Late Night Vibes"};
                for (String plName : userPlaylists) {
                    MenuItem plItem = new MenuItem(plName);
                    plItem.setStyle("-fx-font-size: 12px;");
                    plItem.setOnAction(e -> System.out.println("[User] Added '" + item.title + "' to playlist: " + plName));
                    playlistSubMenu.getItems().add(plItem);
                }

                MenuItem remove = new MenuItem("Remove from this PlayList");
                
                String itemStyle = "-fx-font-size: 13px; -fx-text-fill: #2C1810;";
                fav.setStyle(itemStyle);
                playlistSubMenu.setStyle(itemStyle);
                remove.setStyle(itemStyle);

                menu.getItems().addAll(fav, playlistSubMenu, new SeparatorMenuItem(), remove);
            }

            menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
        }

        @Override
        protected void updateItem(SongItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox.setVisible(isDeleteMode);
                checkBox.setManaged(isDeleteMode);
                checkBox.setSelected(item.isSelected);
                checkBox.setOnAction(e -> item.isSelected = checkBox.isSelected());

                indexLabel.setText(String.valueOf(getIndex() + 1));
                nameLabel.setText(item.title);
                artistLabel.setText(item.artist);
                albumLabel.setText(item.genre); 
                timeLabel.setText(item.getDurationString());
                
                heartBtn.setVisible(!Main.isAdmin); 
                
                setGraphic(root);
            }
        }
    }
}