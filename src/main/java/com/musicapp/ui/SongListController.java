package com.musicapp.ui;

import com.musicapp.Main;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SongListController implements Initializable, MainViewController.MainViewAware {
    
    // Biến quản lý vị trí bài hát đang phát
    private int currentIndex = -1; 
    public static SongListController instance; 
    
    private MainViewController mainController;
    private static MediaPlayer currentPlayer;
    
    @FXML private ImageView coverImageView;
    @FXML private Label titleLabel, subtitleLabel, descriptionLabel;
    @FXML private Label col1Header, col2Header, col3Header, checkHeader;
    @FXML private Button playButton, shuffleButton, addBtn, deleteBtn;
    @FXML private ListView<SongItem> songListView;

    private boolean isDeleteMode = false;
    private final ObservableList<SongItem> songs = FXCollections.observableArrayList();

    private String currentAlbumTitle;
    private String currentArtist;
    private int currentYear;       
    private String currentGenre;   
    private String currentCover;   

    // ==========================================
    // DATA MODEL
    // ==========================================
    public static class SongItem {
        public String songId;
        public String title;
        public String artist;
        public String genre;
        public int duration;
        public int releaseYear;
        public String audioURL;
        public String imageURL;
        public boolean isFavorite = false;
        public boolean isSelected = false; // Used for checkbox

        public SongItem(String id, String title, String artist, String genre, int duration, int year, String audio, String image) {
            this.songId = id; this.title = title; this.artist = artist;
            this.genre = genre; this.duration = duration; this.releaseYear = year;
            this.audioURL = audio; this.imageURL = image;
        }

        public String getDurationString() {
            int mins = duration / 60;
            int secs = duration % 60;
            return String.format("%d:%02d", mins, secs);
        }
    }
    
    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        setupRoleBasedUI();
        refreshData();
        songListView.setItems(songs);
        songListView.setCellFactory(lv -> new SongCell());
    }

    // ==========================================
    // CÁC HÀM PHÁT NHẠC
    // ==========================================
    
    public void playNext() {
        if (songs.isEmpty()) return;
        currentIndex++;
        if (currentIndex >= songs.size()) currentIndex = 0; 
        playSongByIndex(currentIndex);
    }

    public void playPrevious() {
        if (songs.isEmpty()) return;
        currentIndex--;
        if (currentIndex < 0) currentIndex = songs.size() - 1;
        playSongByIndex(currentIndex);
    }

    private void playSongByIndex(int index) {
        if (index >= 0 && index < songs.size()) {
            this.currentIndex = index;
            startPlaying(songs.get(index));
        }
    }

    public void startPlaying(SongItem item) {
        try {
            if (currentPlayer != null) currentPlayer.stop();

            String path = item.audioURL;
            if (path == null || path.isEmpty()) {
                System.err.println("❌ Link nhạc trống!");
                return;
            }

            Media hit = new Media(path);
            currentPlayer = new MediaPlayer(hit);
            currentPlayer.play();

            if (mainController != null) {
                mainController.showPlayerBar(item.title, item.artist, item.imageURL, currentPlayer);
            }
            System.out.println("▶ Đang phát: " + item.title);
        } catch (Exception ex) {
            System.err.println("❌ Lỗi MediaPlayer: " + ex.getMessage());
        }
    }

    // ==========================================
    // LOGIC DỮ LIỆU & UI
    // ==========================================

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Song> firebaseSongs = DatabaseManager.getInstance().getService().fetchSongs();
                ObservableList<SongItem> convertedList = FXCollections.observableArrayList();
                for (Song s : firebaseSongs) {
                    // Filter handling if it's an album view
                    if ("All Songs".equals(currentAlbumTitle) || currentAlbumTitle == null) {
                        convertedList.add(new SongItem(s.getSongId(), s.getTitle(), s.getArtist(), 
                            s.getGenre(), s.getDuration(), s.getReleaseYear(), s.getAudioURL(), s.getImageURL()));
                    } else if (s.getArtist().equals(currentArtist)) {
                        convertedList.add(new SongItem(s.getSongId(), s.getTitle(), s.getArtist(), 
                            s.getGenre(), s.getDuration(), s.getReleaseYear(), s.getAudioURL(), s.getImageURL()));
                    }
                }
                Platform.runLater(() -> songs.setAll(convertedList));
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void setupRoleBasedUI() {
        if (Main.isAdmin) {
            playButton.setVisible(false); playButton.setManaged(false);
            shuffleButton.setVisible(false); shuffleButton.setManaged(false);
            addBtn.setVisible(true); addBtn.setManaged(true);
            deleteBtn.setVisible(true); deleteBtn.setManaged(true);
        } else {
            addBtn.setVisible(false); addBtn.setManaged(false);
            deleteBtn.setVisible(false); deleteBtn.setManaged(false);
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
            
            // LẤY CONTROLLER CỦA MODAL (HEAD logic)
            AddSongModalController modalCtrl = loader.getController();
            
            // BƠM 4 DỮ LIỆU VÀO MODAL
            if (!"All Songs".equals(currentAlbumTitle)) {
                modalCtrl.setPredefinedData(currentArtist, currentGenre, currentYear, currentCover);
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Admin - Add New Track to " + (currentAlbumTitle != null ? currentAlbumTitle : "Library"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            // Tạm dừng thread UI ở đây cho đến khi cửa sổ AddSong đóng lại
            stage.showAndWait(); 
            
            // Cửa sổ đã đóng -> Đợi 500ms cho Firebase đồng bộ rồi tải lại danh sách
            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ex) {}
                Platform.runLater(this::refreshData);
            }).start();
            
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 🔴 HÀM XÓA ĐÃ ĐƯỢC FIX CHỐNG TREO APP
    @FXML 
    private void onDeleteToggleClicked() {
        isDeleteMode = !isDeleteMode;
        songListView.refresh();

        if (isDeleteMode) {
            deleteBtn.setText("CONFIRM");
            deleteBtn.setStyle("-fx-background-color: #CC3300; -fx-text-fill: white;");
        } else {
            deleteBtn.setText("DELETING..."); 
            deleteBtn.setDisable(true); 
            
            // Lọc ra các bài cần xóa
            List<SongItem> itemsToRemove = songs.stream()
                .filter(s -> s.isSelected)
                .toList();
                
            // Nếu không có bài nào được chọn thì thoát luôn
            if (itemsToRemove.isEmpty()) {
                deleteBtn.setText("DELETE");
                deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
                deleteBtn.setDisable(false);
                return;
            }

            // Chạy ngầm để không làm đơ giao diện
            new Thread(() -> {
                try {
                    for (SongItem item : itemsToRemove) {
                        DatabaseManager.getInstance().getService().deleteSong(item.songId);
                    }
                    
                    // Cập nhật lại giao diện sau khi Firebase báo thành công
                    Platform.runLater(() -> {
                        songs.removeAll(itemsToRemove);
                        deleteBtn.setText("DELETE");
                        deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
                        deleteBtn.setDisable(false);
                        System.out.println("[Success] Đã xóa nhạc khỏi Firebase an toàn!");
                    });
                } catch (Exception e) {
                    System.err.println("[Error] Lỗi khi xóa nhạc: " + e.getMessage());
                    Platform.runLater(() -> {
                        deleteBtn.setText("DELETE");
                        deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
                        deleteBtn.setDisable(false);
                    });
                }
            }).start();
        }
    }
    
    public void setData(String title, String sub, String desc, String cover, int year, String genre, ObservableList<SongItem> data) {
        this.currentAlbumTitle = title;
        this.currentArtist = sub; 
        this.currentYear = year;     
        this.currentGenre = genre;   
        this.currentCover = cover;   

        titleLabel.setText(title);
        subtitleLabel.setText(sub);
        descriptionLabel.setText(desc);
        
        // Nạp ảnh bìa
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
    
    public void setColumnHeaders(String c1, String c2, String c3) {
        if (col1Header != null) col1Header.setText(c1);
        if (col2Header != null) col2Header.setText(c2);
        if (col3Header != null) col3Header.setText(c3);
    }

    // ==========================================
    // CUSTOM CELL (ĐÃ NUDGE - ÉP LÙI SANG TRÁI 20PX)
    // ==========================================
    private class SongCell extends ListCell<SongItem> {
        private final HBox root = new HBox(0); // Không khoảng cách tổng
        private final CheckBox checkBox = new CheckBox();
        private final Label indexLabel = new Label();
        private final ImageView thumb = new ImageView();
        private final Label nameLabel = new Label();
        private final Button heartBtn = new Button("♡");
        private final Label artistLabel = new Label();
        private final Label genreLabel = new Label();
        private final Region spacer = new Region();
        private final Button addBtnRow = new Button("+");
        private final Label timeLabel = new Label();

        SongCell() {
            // Ép JavaFX xoá bỏ lề ảo của ListCell
            this.setPadding(new Insets(0)); 
            
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(8, 40, 8, 40));

            // 1. Cột # (Cho căn trái để thẳng hàng với dấu # ở trên)
            indexLabel.setPrefWidth(40);
            indexLabel.setAlignment(Pos.CENTER_LEFT); 
            
            // 2. Cột SONG (Ép tổng chiều rộng từ 300px xuống 280px)
            thumb.setFitWidth(40); thumb.setFitHeight(40);
            
            nameLabel.setPrefWidth(200); // Cắt 20px ở đây
            nameLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;"); 
            
            heartBtn.setPrefWidth(40);
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-cursor: hand; -fx-padding: 0;"); // Ép bỏ padding mặc định
            
            HBox songColumn = new HBox(0, thumb, nameLabel, heartBtn);
            songColumn.setAlignment(Pos.CENTER_LEFT);
            songColumn.setPrefWidth(280); // Cắt 20px ở đây để kéo Artist sang trái
            songColumn.setMinWidth(280);
            songColumn.setMaxWidth(280);

            // 3. Cột ARTIST
            artistLabel.setPrefWidth(200);
            artistLabel.setMinWidth(200);
            artistLabel.setMaxWidth(200);

            // 4. Cột GENRE
            genreLabel.setPrefWidth(150);
            genreLabel.setMinWidth(150);
            genreLabel.setMaxWidth(150);

            // 5. Khoảng trắng đẩy Time sang phải
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // 6. Cột TIME
            addBtnRow.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #C0703A; -fx-font-size: 16px; -fx-cursor: hand;");
            timeLabel.setPrefWidth(40);
            timeLabel.setAlignment(Pos.CENTER_RIGHT);
            
            HBox timeColumn = new HBox(5, addBtnRow, timeLabel);
            timeColumn.setAlignment(Pos.CENTER_RIGHT);

            root.getChildren().addAll(checkBox, indexLabel, songColumn, artistLabel, genreLabel, spacer, timeColumn);

            root.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && getItem() != null) {
                    SongListController.this.currentIndex = getIndex();
                    SongListController.this.startPlaying(getItem());
                }
            });
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
                genreLabel.setText(item.genre); 
                timeLabel.setText(item.getDurationString());
                
                heartBtn.setVisible(!Main.isAdmin); 
                
                if(item.imageURL != null && !item.imageURL.isEmpty()){
                    thumb.setImage(new Image(item.imageURL, true));
                }
                setGraphic(root);
            }
        }
    }
}