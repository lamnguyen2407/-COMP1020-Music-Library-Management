package com.musicapp.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SongListController implements Initializable, MainViewController.MainViewAware {
    
    private int currentIndex = -1; 
    public static SongListController instance; 
    
    private MainViewController mainController;
    private static MediaPlayer currentPlayer;
    
    @FXML private ImageView coverImageView;
    @FXML private Label titleLabel, subtitleLabel, descriptionLabel;
    @FXML private Label col1Header, col2Header, col3Header, checkHeader;
    @FXML private Button playButton, shuffleButton, addBtn, deleteBtn;
    @FXML private ListView<SongItem> songListView;
    @FXML private HBox headerBox; // Thêm dòng này
    private boolean isDeleteMode = false;
    private final ObservableList<SongItem> songs = FXCollections.observableArrayList();

    private String currentAlbumId; // BIẾN MỚI: Để định danh Album đang xem
    private String currentAlbumTitle;
    private String currentArtist;
    private int currentYear;        
    private String currentGenre;   
    private String currentCover;   
    private List<String> currentSongIdList = new ArrayList<>(); // BIẾN MỚI: Danh sách ID bài hát của Album

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
        songs.addListener((javafx.collections.ListChangeListener.Change<? extends SongItem> c) -> {
            if (headerBox != null) {
                if (songs.size() > 6) { 
                    headerBox.setStyle("-fx-background-color: #FAF7F4; -fx-padding: 12 55 8 40;");
                } else {
                    headerBox.setStyle("-fx-background-color: #FAF7F4; -fx-padding: 12 40 8 40;");
                }
            }
        });
    }

    // ==========================================
    // CÁC HÀM PHÁT NHẠC (ĐÃ FIX AN TOÀN)
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
            if (currentPlayer != null) {
                MediaPlayer.Status oldStatus = currentPlayer.getStatus();
                if (oldStatus != MediaPlayer.Status.UNKNOWN && oldStatus != MediaPlayer.Status.HALTED) {
                    try { currentPlayer.stop(); } catch (Exception ignored) {}
                }
                currentPlayer.dispose();
            }

            String path = item.audioURL;
            if (path == null || path.isEmpty()) return;
            
            String uriString = path.trim().replace(" ", "%20");
            Media hit = new Media(uriString);
            currentPlayer = new MediaPlayer(hit);
            
            if (mainController != null) {
                mainController.showPlayerBar(item.title, item.artist, item.imageURL, currentPlayer);
            }
            
            currentPlayer.setOnReady(() -> currentPlayer.play());
        } catch (Exception ex) {
            System.err.println("❌ Lỗi MediaPlayer: " + ex.getMessage());
        }
    }

    // ==========================================
    // LOGIC DỮ LIỆU & UI (NÂNG CẤP ĐỢT 2)
    // ==========================================
    public void refreshData() {
        // Kh ng fetch l nh Search
        if (currentAlbumTitle != null && currentAlbumTitle.contains("Search Results")) return;
        new Thread(() -> {
            try {
                List<SongItem> convertedList = new ArrayList<>();
                boolean isLibraryView = (currentAlbumTitle != null && 
                     (currentAlbumTitle.toLowerCase().contains("all") || currentAlbumTitle.toLowerCase().contains("library")));
                
                if (isLibraryView) {
                    // 1. M nh Library: Load t
                    List<Song> firebaseSongs = DatabaseManager.getInstance().getService().fetchSongs();
                    for (Song s : firebaseSongs) {
                        convertedList.add(new SongItem(s.getSongId(), s.getTitle(), s.getArtist(), 
                             s.getGenre(), s.getDuration(), s.getReleaseYear(), s.getAudioURL(), s.getImageURL()));
                    }
                } else if (currentAlbumId != null) {
                    // 2. M nh Album/Playlist c
                    
                    // --- ĐÃ BỎ LỆNH IF KIỂM TRA RỖNG Ở ĐÂY ĐỂ LUÔN FETCH TỪ FIREBASE ---
                    System.out.println("[DEBUG] Đang lấy danh sách ID cho: " + currentAlbumId);
                    
                    // Ki m tra xem l  Playlist hay Album  ng ch
                    if (currentAlbumId.startsWith("SYSTEM_") || currentAlbumId.startsWith("pl_")) {
                        currentSongIdList = DatabaseManager.getInstance().getService().fetchSongIdsFromPlaylist(currentAlbumId);
                    } else {
                        currentSongIdList = DatabaseManager.getInstance().getService().fetchSongIdsFromAlbum(currentAlbumId);
                    }
                    
                    // Sau khi c  danh s ch ID (d  truy n sang hay v a fetch), k o th ng tin b
                    if (currentSongIdList != null && !currentSongIdList.isEmpty()) {
                        List<Song> songsFromDb = DatabaseManager.getInstance().getService().fetchSongsByIds(currentSongIdList);
                        for (Song s : songsFromDb) {
                            convertedList.add(new SongItem(s.getSongId(), s.getTitle(), s.getArtist(), 
                                 s.getGenre(), s.getDuration(), s.getReleaseYear(), s.getAudioURL(), s.getImageURL()));
                        }
                    }
                }
                
                Platform.runLater(() -> songs.setAll(convertedList));
            } catch (Exception e) { 
                 System.err.println("[ERROR] L i refreshData: " + e.getMessage()); 
            }
        }).start();
    }

    private void setupRoleBasedUI() {
        try {
            if (SessionManager.isAdmin) {
                if (playButton != null) { playButton.setVisible(false); playButton.setManaged(false); }
                if (shuffleButton != null) { shuffleButton.setVisible(false); shuffleButton.setManaged(false); }
            } else {
                if (addBtn != null) { addBtn.setVisible(false); addBtn.setManaged(false); }
                if (deleteBtn != null) { deleteBtn.setVisible(false); deleteBtn.setManaged(false); }
            }
        } catch (Exception e) {}
    }

    // ==========================================
    // ACTION HANDLERS
    // ==========================================
    @FXML 
    private void onAddSongClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddSongModal.fxml"));
            Parent root = loader.load();
            
            AddSongModalController modalCtrl = loader.getController();
            
            if (currentAlbumId != null) {
                modalCtrl.setTargetAlbumId(currentAlbumId);
                modalCtrl.setPredefinedData(currentArtist, currentGenre, currentYear, currentCover);
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Code sẽ tạm dừng ở đây cho đến khi bấm Save và đóng Modal
            
            // --- ĐOẠN CODE FIX LỖI (Thay thế đoạn Thread.sleep 300ms cũ) ---
            // Lấy ngay bài hát vừa được tạo thành công từ modal
            Song addedSong = modalCtrl.getCreatedSong();
            
            if (addedSong != null) {
                // Đóng gói thành SongItem
                SongItem newItem = new SongItem(
                    addedSong.getSongId(), addedSong.getTitle(), addedSong.getArtist(),
                    addedSong.getGenre(), addedSong.getDuration(), addedSong.getReleaseYear(),
                    addedSong.getAudioURL(), addedSong.getImageURL()
                );
                
                // Add thẳng vào List hiển thị trên giao diện (UI sẽ tự động chèn thêm dòng nhạc này luôn)
                songs.add(newItem);
                
                // Cập nhật danh sách ID nội bộ phòng trường hợp logic khác cần dùng
                if (currentSongIdList != null) {
                    currentSongIdList.add(addedSong.getSongId());
                }
            }
            // ---------------------------------------------------------------

        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML 
    private void onDeleteToggleClicked() {
        isDeleteMode = !isDeleteMode;
        songListView.refresh();
        if (isDeleteMode) {
            deleteBtn.setText("CONFIRM");
            deleteBtn.setStyle("-fx-background-color: #CC3300; -fx-text-fill: white;");
        } else {
            List<SongItem> itemsToRemove = songs.stream().filter(s -> s.isSelected).toList();
            if (itemsToRemove.isEmpty()) {
                resetDeleteBtn();
                return;
            }
            new Thread(() -> {
                try {
                    for (SongItem item : itemsToRemove) {
                        DatabaseManager.getInstance().getService().deleteSong(item.songId);
                    }
                    Platform.runLater(() -> {
                        songs.removeAll(itemsToRemove);
                        resetDeleteBtn();
                    });
                } catch (Exception e) { Platform.runLater(this::resetDeleteBtn); }
            }).start();
        }
    }

    private void resetDeleteBtn() {
        deleteBtn.setText("DELETE");
        deleteBtn.setStyle("-fx-background-color: #C0703A; -fx-text-fill: white;");
        deleteBtn.setDisable(false);
    }
    
    // HÀM QUAN TRỌNG: Nơi nhận dữ liệu từ Album truyền sang
    public void setData(String id, String title, String sub, String desc, String cover, int year, String genre, List<String> songIds) {
        this.currentAlbumId = id;
        this.currentAlbumTitle = title;
        this.currentArtist = sub; 
        this.currentYear = year;     
        this.currentGenre = genre;   
        this.currentCover = cover;   
        this.currentSongIdList = (songIds != null) ? songIds : new ArrayList<>();

        if (titleLabel != null) titleLabel.setText(title);
        if (subtitleLabel != null) subtitleLabel.setText(sub);
        if (descriptionLabel != null) descriptionLabel.setText(desc);
        
        if (coverImageView != null && cover != null && !cover.trim().isEmpty()) {
            try {
                if (cover.startsWith("http")) {
                    coverImageView.setImage(new Image(cover, true)); 
                } else {
                    URL url = getClass().getResource(cover);
                    if (url != null) {
                        coverImageView.setImage(new Image(url.toExternalForm()));
                    } else {
                        // NẾU SAI ĐƯỜNG DẪN ẢNH HOẶC CHỮ HOA/THƯỜNG, NÓ SẼ BÁO LỖI RA CONSOLE NGAY!
                        System.err.println("❌ BÁO ĐỘNG: KHÔNG TÌM THẤY ẢNH TẠI ĐƯỜNG DẪN -> " + cover);
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ LỖI LOAD ẢNH: " + e.getMessage());
            }
        }

        refreshData(); 

        if (SessionManager.isAdmin) {
            boolean canEdit = (title != null && title.toLowerCase().contains("all"))
                               || "Album detail view".equals(desc)
                               || "System Playlist".equals(desc)
                               || "System Playlist".equals(sub)
                               || (id != null && id.contains("system"))
                               || (id != null && id.startsWith("pl_system"));
            
            if (addBtn != null) { addBtn.setVisible(canEdit); addBtn.setManaged(canEdit); }
            if (deleteBtn != null) { deleteBtn.setVisible(canEdit); deleteBtn.setManaged(canEdit); }
        }
    }
    
    public void setColumnHeaders(String c1, String c2, String c3) {
        if (col1Header != null) col1Header.setText(c1);
        if (col2Header != null) col2Header.setText(c2);
        if (col3Header != null) col3Header.setText(c3);
    }

    private class SongCell extends ListCell<SongItem> {
        private final HBox root = new HBox(0); 
        private final CheckBox checkBox = new CheckBox();
        private final Label indexLabel = new Label();
        private final ImageView thumb = new ImageView();
        private final Label nameLabel = new Label();
        private final Button heartBtn = new Button("♡");
        
        private final Label artistLabel = new Label();
        private final Label genreLabel = new Label();
        private final Region spacer = new Region();
        private final Label timeLabel = new Label();

        SongCell() {
            this.setPadding(new Insets(0)); 
            
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(0, 40, 0, 40));
            root.setMinHeight(62);

            checkBox.setMinWidth(35);
            checkBox.setPrefWidth(35);

            indexLabel.setPrefWidth(40);
            indexLabel.setMinWidth(40);
            
            thumb.setFitWidth(40); thumb.setFitHeight(40);
            
            nameLabel.setPrefWidth(190); 
            nameLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;"); 
            
            heartBtn.setPrefWidth(40);
            heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C0C0C0; -fx-cursor: hand; -fx-padding: 0;"); 
            
            // MANG ĐOẠN IF VÀO TRONG CONSTRUCTOR NÀY:
            if (SessionManager.isAdmin) {
                heartBtn.setVisible(false);
                heartBtn.setManaged(false);
            }
            
            HBox songCol = new HBox(0, thumb, nameLabel, heartBtn);
            songCol.setAlignment(Pos.CENTER_LEFT);
            songCol.setPrefWidth(280); 
            songCol.setMinWidth(280);

            artistLabel.setPrefWidth(180);
            artistLabel.setMinWidth(180);

            genreLabel.setPrefWidth(130);
            genreLabel.setMinWidth(130);

            // TÁCH RIÊNG DÒNG NÀY: Nó trả về void nên phải đứng một mình
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            timeLabel.setPrefWidth(60);
            timeLabel.setAlignment(Pos.CENTER_RIGHT);
            
            // CHỈ TRUYỀN TÊN BIẾN VÀO ĐÂY:
            root.getChildren().addAll(checkBox, indexLabel, songCol, artistLabel, genreLabel, spacer, timeLabel);

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
            if (empty || item == null) setGraphic(null);
            else {
                checkBox.setVisible(isDeleteMode); checkBox.setManaged(isDeleteMode);
                checkBox.setSelected(item.isSelected);
                checkBox.setOnAction(e -> item.isSelected = checkBox.isSelected());
                indexLabel.setText(String.valueOf(getIndex() + 1));
                nameLabel.setText(item.title);
                artistLabel.setText(item.artist);
                genreLabel.setText(item.genre); 
                timeLabel.setText(item.getDurationString());
                if (item.imageURL != null && !item.imageURL.isEmpty()) {
                    try { thumb.setImage(new Image(item.imageURL, true)); } catch (Exception e) {}
                }
                setGraphic(root);
            }
        }
    }
    
 // Hàm bổ trợ để hiển thị dữ liệu Search hoặc dữ liệu ép từ bên ngoài vào
    public void setSongsList(ObservableList<SongItem> manualData) {
        if (manualData != null) {
            // Ngăn thằng refreshData() tự động fetch và ghi đè
            this.currentAlbumId = null; 
            this.songs.setAll(manualData);
        }
    }
    }