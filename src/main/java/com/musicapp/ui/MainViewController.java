package com.musicapp.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainViewController implements Initializable {

    // ══════════════════════════════════════════
    // FXML — TopBar & PlayerBar
    // ══════════════════════════════════════════
    @FXML private HBox topBar, playerBar;
    @FXML private TextField searchField;
    @FXML private Label userNameLabel, playerSongTitle, playerArtistName, labelCurrentTime, labelTotalTime;
    @FXML private ImageView userImageView, playerArtImage;
    @FXML private Button btnLike, btnShuffle, btnPrev, btnPlayPause, btnNext, btnRepeat;
    @FXML private Slider progressSlider, volumeSlider;

    // ══════════════════════════════════════════
    // FXML — Sidebar & Content
    // ══════════════════════════════════════════
    @FXML private Button btnHome, btnAccount, btnSearch, btnPlaylists, btnSettings;
    @FXML private StackPane contentArea;

    // ══════════════════════════════════════════
    // State
    // ══════════════════════════════════════════
    private boolean isPlaying = false;
    private boolean isLiked = false;
    private boolean isShuffled = false;
    private boolean isRepeating = false;
    private MediaPlayer mediaPlayer;

    private static final String FXML_DISCOVERY = "/DiscoveryView.fxml";
    private static final String FXML_ACCOUNT = "/AccountView.fxml";
    private static final String FXML_PLAYLIST = "/PlaylistOverview.fxml";
    private static final String FXML_SETTINGS = "/SettingsView.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userNameLabel.setText(SessionManager.isAdmin ? "Admin View" : "User View");
        loadView(FXML_DISCOVERY);
        if (SessionManager.isAdmin) {
            if (btnLike != null) {
                btnLike.setVisible(false);
                btnLike.setManaged(false);
            }
        }
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isBlank()) setActiveNav(btnSearch);
            });
            searchField.setOnMouseClicked(e -> setActiveNav(btnSearch));
            searchField.setOnAction(e -> handleSearchRequest());
        }

        if (volumeSlider != null) {
            volumeSlider.setValue(70);
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) mediaPlayer.setVolume(newVal.doubleValue() / 100);
            });
        }
    }

    // ══════════════════════════════════════════
    // NAVIGATION HANDLERS
    // ══════════════════════════════════════════

    @FXML private void onNavHome() { setActiveNav(btnHome); loadView(FXML_DISCOVERY); }
    @FXML private void onNavAccount() { setActiveNav(btnAccount); loadView(FXML_ACCOUNT); }
    
    @FXML 
    private void onNavPlaylists() { 
        setActiveNav(btnPlaylists); 
        loadView(FXML_PLAYLIST); 
    }
    
    @FXML 
    private void onNavSettings() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WelcomeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            loadView(FXML_SETTINGS);
        }
    }

    @FXML 
    private void onNavSearch() {
        setActiveNav(btnSearch);
        if (searchField != null) {
            searchField.requestFocus();
            searchField.selectAll();
        }
    }

    // ══════════════════════════════════════════
    // PLAYER BAR HANDLERS 
    // ══════════════════════════════════════════

    @FXML private void onShuffle() { isShuffled = !isShuffled; }
    @FXML private void onRepeat() { isRepeating = !isRepeating; }
    @FXML private void onToggleLike() { isLiked = !isLiked; btnLike.setText(isLiked ? "❤️" : "♡"); }

    @FXML 
    private void onPlayPause() { 
        if (mediaPlayer == null) return;

        MediaPlayer.Status status = mediaPlayer.getStatus();

        // CÚ FIX: Ngăn chặn thao tác khi Media chưa tải xong lõi (jfxPlayer) hoặc bị lỗi link
        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            System.out.println("⏳ Nhạc đang tải hoặc link bị lỗi, chưa thể phát/dừng lúc này...");
            return;
        }

        try {
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                isPlaying = false;
                btnPlayPause.setText("▶");
            } else {
                mediaPlayer.play();
                isPlaying = true;
                btnPlayPause.setText("⏸");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi từ nút Play/Pause: " + e.getMessage());
        }
    }

    @FXML private void onNext() { if (SongListController.instance != null) SongListController.instance.playNext(); }
    @FXML private void onPrev() { if (SongListController.instance != null) SongListController.instance.playPrevious(); }
    @FXML private void onSeek() { if (mediaPlayer != null) mediaPlayer.seek(Duration.seconds(progressSlider.getValue())); }

    // ══════════════════════════════════════════
    // GẮN MEDIAPLAYER VÀ UPDATE SLIDER
    // ══════════════════════════════════════════

    public void setMediaPlayer(MediaPlayer player) {
        if (this.mediaPlayer != null) this.mediaPlayer.stop();
        this.mediaPlayer = player;
        if (volumeSlider != null) this.mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        this.mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> updateProgress(newTime));
        this.mediaPlayer.setOnEndOfMedia(this::onNext);
        isPlaying = true;
        btnPlayPause.setText("⏸");
    }

    private void updateProgress(Duration currentTime) {
        if (mediaPlayer == null) return;
        double current = currentTime.toSeconds();
        double total = mediaPlayer.getTotalDuration().toSeconds();
        if (total > 0) {
            progressSlider.setMax(total);
            if (!progressSlider.isPressed()) progressSlider.setValue(current);
            labelCurrentTime.setText(formatDuration((int)current));
            labelTotalTime.setText(formatDuration((int)total));
        }
    }

    private String formatDuration(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    // ══════════════════════════════════════════
    // CÁC HÀM SHOW PLAYER BAR 
    // ══════════════════════════════════════════

    public void showPlayerBar(String songTitle, String artistName, String imagePath) {
        updatePlayerUI(songTitle, artistName, imagePath);
    }

    public void showPlayerBar(String songTitle, String artistName, String imagePath, MediaPlayer player) {
        setMediaPlayer(player);
        updatePlayerUI(songTitle, artistName, imagePath);
    }

    public void showPlayerBar(Song currentSong, List<Song> queue, int index) {
        try {
            if (currentSong.getAudioURL() != null && !currentSong.getAudioURL().isEmpty()) {
                // CÚ FIX: Xử lý khoảng trắng trong link nhạc y như SongListController
                String uriString = currentSong.getAudioURL().trim().replace(" ", "%20");
                Media hit = new Media(uriString);
                
                setMediaPlayer(new MediaPlayer(hit));
                mediaPlayer.play();
            }
            updatePlayerUI(currentSong.getTitle(), currentSong.getArtist(), currentSong.getImageURL());
            labelTotalTime.setText(formatDuration(currentSong.getDuration()));
        } catch (Exception e) { 
            System.err.println("❌ LỖI KHỞI TẠO MEDIA TỪ QUEUE: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void updatePlayerUI(String title, String artist, String img) {
        if (playerSongTitle != null) playerSongTitle.setText(title != null ? title : "Unknown");
        if (playerArtistName != null) playerArtistName.setText(artist != null ? artist : "Unknown");
        
        if (playerArtImage != null && img != null && !img.trim().isEmpty()) {
            try { playerArtImage.setImage(new Image(img, true)); } 
            catch (Exception e) { System.err.println("Lỗi load ảnh"); }
        }

        if (playerBar != null && !playerBar.isVisible()) {
            playerBar.setVisible(true);
            playerBar.setManaged(true);
            FadeTransition ft = new FadeTransition(Duration.millis(300), playerBar);
            ft.setFromValue(0); ft.setToValue(1); ft.play();
        }
    }

    public void hidePlayerBar() {
        if (playerBar != null) {
            playerBar.setVisible(false);
            playerBar.setManaged(false);
        }
        isPlaying = false;
        if (btnPlayPause != null) btnPlayPause.setText("▶");
    }

    // ══════════════════════════════════════════
    // GIAO DIỆN CON (Songs & Albums)
    // ══════════════════════════════════════════

    public void openSongListView(String title, String subtitle, String desc, javafx.collections.ObservableList<SongListController.SongItem> data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            ctrl.setMainController(this); 

            // CÚ FIX: 
            // 1. Thêm ID "SONG_LIST_VIEW" vào đầu cho đủ 8 tham số.
            // 2. Tham số cuối truyền một ArrayList rỗng vì chúng ta đã có sẵn 'data' (SongItem) rồi.
            ctrl.setData("SONG_LIST_VIEW", title, subtitle, desc, null, 0, "", new java.util.ArrayList<>());            
            
            // 3. Bơm trực tiếp danh sách SongItem vào giao diện
            ctrl.setSongsList(data);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public void openAllAlbumsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewAlbumReleaseView.fxml"));
            Node view = loader.load();
            
            Object childController = loader.getController();
            if (childController instanceof MainViewAware) {
                ((MainViewAware) childController).setMainController(this);
            }
            
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("❌ Could not load NewAlbumReleaseView.fxml");
            e.printStackTrace();
        }
    }
    
    public void loadSongDetail(String albumName, String artist, String genre, int year, String imageURL, List<Song> songs) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlbumView.fxml"));
            Parent view = loader.load();
            AlbumViewController controller = loader.getController();
            controller.setMainController(this);
            controller.setAlbumData(albumName, artist, genre, year, imageURL, songs);
            contentArea.getChildren().setAll(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════
    // SEARCH LOGIC (Chọc thẳng Firebase - ĐÃ FIX CHỐNG LỖI NULL)
    // ══════════════════════════════════════════

    private void handleSearchRequest() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) return;
        navigateToSearchResult(query);
    }

    private void navigateToSearchResult(String query) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            if (ctrl instanceof MainViewAware) ((MainViewAware) ctrl).setMainController(this);
            
            // CÚ FIX 1: Truyền đủ 8 tham số (Thêm ID "SEARCH" vào đầu và null vào cuối)
         // Trong hàm navigateToSearchResult(String query)
            ctrl.setData("SEARCH_VIEW", "Search Results", "Results for: \"" + query + "\"", "Searching...", null, 0, "Various", new java.util.ArrayList<>());

            ctrl.setColumnHeaders("SONG", "ARTIST", "GENRE"); // <- Sửa lại cho đúng thứ tựuserNameLabel.setText(SessionManager.isAdmin ? "Admin View" : "User View");
            contentArea.getChildren().setAll(view);

            new Thread(() -> {
                try {
                    List<Song> allSongs = DatabaseManager.getInstance().getService().fetchSongs();
                    var results = javafx.collections.FXCollections.<SongListController.SongItem>observableArrayList();
                    String lowerQuery = query.toLowerCase(); 
                    
                    for (Song song : allSongs) {
                        String title = song.getTitle() != null ? song.getTitle().toLowerCase() : "";
                        String artist = song.getArtist() != null ? song.getArtist().toLowerCase() : "";
                        
                        if (title.contains(lowerQuery) || artist.contains(lowerQuery)) {
                            results.add(new SongListController.SongItem(
                                song.getSongId(), song.getTitle(), song.getArtist(), 
                                song.getGenre(), song.getDuration(), song.getReleaseYear(), 
                                song.getAudioURL(), song.getImageURL()
                            ));
                        }
                    }
                    
                    Platform.runLater(() -> {
                        // CÚ FIX 2: Cập nhật lại kết quả tìm kiếm (Cũng phải đủ 8 tham số)
                        ctrl.setData("SEARCH_VIEW", "Search Results", "Results for: \"" + query + "\"", results.size() + " found", null, 0, "Various", new java.util.ArrayList<>());
                        // Đẩy list kết quả tìm kiếm vào thẳng ListView
                        ctrl.setSongsList(results); 
                    });
                } catch (Exception e) { 
                    e.printStackTrace(); 
                }
            }).start();

        } catch (IOException e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════

    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) return;
            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();
            Object childController = loader.getController();
            if (childController instanceof MainViewAware) ((MainViewAware) childController).setMainController(this);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setActiveNav(Button selected) {
        Button[] navButtons = { btnHome, btnAccount, btnSearch, btnPlaylists };
        for (Button btn : navButtons) if (btn != null) btn.getStyleClass().removeAll("nav-btn-active");
        if (selected != null) selected.getStyleClass().add("nav-btn-active");
    }

    public interface MainViewAware { void setMainController(MainViewController mainController); }

    private void testLoadAlbum() {
        List<Song> testSongs = new java.util.ArrayList<>();
        testSongs.add(new Song("1", "Going Bad", "Meek Mill", "Hip-Hop", 181, 2018, "", ""));
        testSongs.add(new Song("2", "Amen", "Meek Mill", "Hip-Hop", 196, 2018, "", ""));
        loadSongDetail("Championship", "Meek Mill", "Hip-Hop", 2018, "", testSongs);
    }
    
 // Thêm hàm này để các Controller khác có thể lấy contentArea ra dùng
    public javafx.scene.layout.StackPane getContentArea() {
        return contentArea;
    }
}