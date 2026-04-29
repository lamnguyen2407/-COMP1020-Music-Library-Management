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
        public boolean isSelected = false; // Used forcheckbox

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
            
            // LẤY CONTROLLER CỦA MODAL
            AddSongModalController modalCtrl = loader.getController();
            
            // BƠM 4 DỮ LIỆU VÀO MODAL
            if (!"All Songs".equals(currentAlbumTitle)) {
                // Truyền: Artist, Genre, Year, Cover
                modalCtrl.setPredefinedData(currentArtist, currentGenre, currentYear, currentCover);
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Admin - Add New Track to " + currentAlbumTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            stage.showAndWait(); 
            refreshListData();
            
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
            // Xoá bài hát đã chọn
            songs.removeIf(s -> {
                if (s.isSelected) {
                    MusicService.removeSong(s);
                    return true;
                }
                return false;
            });
            
            // Sau khi xoá xong, gọi lại refresh để đảm bảo dữ liệu đồng bộ
            refreshListData();
            
            deleteBtn.setText("DELETE");
            deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
        }
    }
    private String currentAlbumTitle;
    private String currentArtist;
    private int currentYear;       // <-- Thêm dòng này
    private String currentGenre;   // <-- Thêm dòng này
    private String currentCover;   // <-- Thêm dòng này
    
    public void setData(String title, String sub, String desc, String cover, int year, String genre, ObservableList<SongItem> data) {
        this.currentAlbumTitle = title;
        this.currentArtist = sub; 
        this.currentYear = year;     // <-- Lưu năm
        this.currentGenre = genre;   // <-- Lưu thể loại
        this.currentCover = cover;   // <-- Lưu link ảnh

        titleLabel.setText(title);
        subtitleLabel.setText(sub);
        descriptionLabel.setText(desc);
        
        // Nạp ảnh bìa (giữ nguyên logic cũ của bạn)
        if (coverImageView != null && cover != null && !cover.trim().isEmpty()) {
            try {
                if (cover.startsWith("http")) {
                    coverImageView.setImage(new Image(cover, true)); 
                } else {
                    URL imageURL = getClass().getResource(cover);
                    if (imageURL != null) coverImageView.setImage(new Image(imageURL.toExternalForm()));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (data != null) {
            songs.setAll(data);
        }

        // Hiện nút Add/Delete cho Admin
        if (Main.isAdmin) {
            boolean canEdit = "All Songs".equals(title) || "Album detail view".equals(desc);
            addBtn.setVisible(canEdit); addBtn.setManaged(canEdit);
            deleteBtn.setVisible(canEdit); deleteBtn.setManaged(canEdit);
        }
    }
    private void refreshListData() {
        if ("All Songs".equals(currentAlbumTitle)) {
            // Nếu là trang All Songs thì load tất cả
            songs.setAll(MusicService.getGlobalLibrary());
        } else {
            // Nếu là trang Album, lọc lại toàn bộ kho nhạc lấy đúng bài của Album này
            ObservableList<SongItem> filteredSongs = FXCollections.observableArrayList();
            for (SongItem s : MusicService.getGlobalLibrary()) {
                // Lọc theo Artist (hoặc theo Album title nếu bạn có trường đó)
                if (s.artist.equals(currentArtist)) {
                    filteredSongs.add(s);
                }
            }
            songs.setAll(filteredSongs);
        }
    }
    
    
    public void setColumnHeaders(String c1, String c2, String c3) {
        col1Header.setText(c1);
        col2Header.setText(c2);
        col3Header.setText(c3);
    }

    // =========================================
    // CUSTOM CELL (Inner Class)
    // ==========================================
 // =========================================
    // CUSTOM CELL (Inner Class)
    // ==========================================
    private class SongCell extends ListCell<SongItem> {
        private final HBox root = new HBox(0); // BẮT BUỘC KHOẢNG CÁCH = 0
        private final CheckBox checkBox = new CheckBox();
        private final Label indexLabel = new Label();
        private final ImageView thumb = new ImageView();
        private final Label nameLabel = new Label();
        private final Button heartBtn = new Button("♡");
        private final Label artistLabel = new Label();
        private final Label albumLabel = new Label();
        private final Button addBtnRow = new Button("+");
        private final Label timeLabel = new Label();

        SongCell() {
            // 1. THÊM DÒNG NÀY: Ép JavaFX xoá bỏ lề ảo (default padding) của ListCell
            this.setPadding(new Insets(0)); 
            
            root.setAlignment(Pos.CENTER_LEFT);
            // 2. SỬA DÒNG NÀY: Trả lại lề trái/phải 40px để HBox này thẳng tắp với Header HBox
            root.setPadding(new Insets(0, 40, 0, 40)); 

            // CỘT 0: Checkbox (Khóa cứng 35px)
            checkBox.setPrefWidth(35); checkBox.setMinWidth(35); checkBox.setMaxWidth(35);
            
            // ... (Giữ nguyên toàn bộ phần khóa cứng kích thước các cột ở bên dưới)
            // CỘT 1: Index (#) (Khóa cứng 35px)
            indexLabel.setPrefWidth(35); indexLabel.setMinWidth(35); indexLabel.setMaxWidth(35);

            // CỘT 2: SONG (Gom Ảnh + Tên bài + Nút tim vào 1 HBox 300px)
            thumb.setFitWidth(40); thumb.setFitHeight(40);
            nameLabel.setPrefWidth(210); nameLabel.setStyle("-fx-font-weight: bold;");
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-cursor: hand;");
            
            HBox songInfoBox = new HBox(12, thumb, nameLabel, heartBtn);
            songInfoBox.setAlignment(Pos.CENTER_LEFT);
            songInfoBox.setPrefWidth(300); songInfoBox.setMinWidth(300); songInfoBox.setMaxWidth(300);

            // CỘT 3: ARTIST (Khóa cứng 180px)
            artistLabel.setPrefWidth(180); artistLabel.setMinWidth(180); artistLabel.setMaxWidth(180);

            // CỘT 4: ALBUM (Khóa cứng 200px)
            albumLabel.setPrefWidth(200); albumLabel.setMinWidth(200); albumLabel.setMaxWidth(200);

            // CỘT 5: TIME (Khóa cứng 80px)
            addBtnRow.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0703A; -fx-font-size: 18px; -fx-cursor: hand;");
            HBox timeBox = new HBox(15, addBtnRow, timeLabel);
            timeBox.setAlignment(Pos.CENTER_RIGHT); 
            timeBox.setPrefWidth(80); timeBox.setMinWidth(80); timeBox.setMaxWidth(80);

            // Gom tất cả vào root (KHÔNG CÒN BIẾN SPACER NỮA)
            root.getChildren().addAll(checkBox, indexLabel, songInfoBox, artistLabel, albumLabel, timeBox);

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

                        if (mainController != null) {
                            mainController.showPlayerBar(item.title, item.artist, item.imageURL);
                        } else {
                            System.err.println("⚠️ mainController đang null, không thể hiện Bar!");
                        }
                    } catch (Exception ex) {
                        System.err.println("❌ Lỗi MediaPlayer!");
                        ex.printStackTrace();
                    }
                }
            });

            // Context Menu
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
                fav.setStyle(itemStyle); playlistSubMenu.setStyle(itemStyle); remove.setStyle(itemStyle);

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
                checkBox.setVisible(isDeleteMode); checkBox.setManaged(isDeleteMode);
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
    } }