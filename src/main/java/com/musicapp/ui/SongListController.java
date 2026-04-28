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

    // ==========================================
    // DATA MODEL (Giữ nguyên)
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
        public boolean isSelected = false;

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
    // 🟢 CÁC HÀM PHÁT NHẠC (NẰM Ở CONTROLLER LEVEL)
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
                // Gửi MediaPlayer sang Main để điều khiển thanh Slider/Play/Pause
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
                    convertedList.add(new SongItem(s.getSongId(), s.getTitle(), s.getArtist(), 
                        s.getGenre(), s.getDuration(), s.getReleaseYear(), s.getAudioURL(), s.getImageURL()));
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

    @FXML private void onAddSongClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddSongModal.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setScene(new Scene(root));
            stage.showAndWait(); 
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onDeleteToggleClicked() {
        isDeleteMode = !isDeleteMode;
        songListView.refresh();
        if (!isDeleteMode) {
            songs.removeIf(s -> {
                if (s.isSelected) {
                    DatabaseManager.getInstance().getService().deleteSong(s.songId);
                    return true;
                }
                return false;
            });
        }
        deleteBtn.setText(isDeleteMode ? "CONFIRM" : "DELETE");
    }

    public void setData(String title, String sub, String desc, String cover, ObservableList<SongItem> data) {
        titleLabel.setText(title); subtitleLabel.setText(sub); descriptionLabel.setText(desc);
        if (data != null) songs.setAll(data);
    }

    // ==========================================
    // CUSTOM CELL (CHỈ CHỨA LOGIC VẼ GIAO DIỆN)
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
            thumb.setFitWidth(40); thumb.setFitHeight(40);
            nameLabel.setPrefWidth(240); nameLabel.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(spacer, Priority.ALWAYS);
            artistLabel.setPrefWidth(180); albumLabel.setPrefWidth(200);

            root.getChildren().addAll(checkBox, indexLabel, thumb, nameLabel, heartBtn, spacer, artistLabel, albumLabel, addBtnRow, timeLabel);

            // Double Click để phát nhạc
            root.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && getItem() != null) {
                    // Gọi hàm startPlaying của lớp bên ngoài
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
                albumLabel.setText(item.genre); 
                timeLabel.setText(item.getDurationString());
                
                if(item.imageURL != null && !item.imageURL.isEmpty()){
                    thumb.setImage(new Image(item.imageURL, true));
                }
                setGraphic(root);
            }
        }
        
     // Thêm hàm này vào để MainViewController không báo lỗi nữa
        
    }
    
    public void setColumnHeaders(String c1, String c2, String c3) {
        if (col1Header != null) col1Header.setText(c1);
        if (col2Header != null) col2Header.setText(c2);
        if (col3Header != null) col3Header.setText(c3);
    }
}