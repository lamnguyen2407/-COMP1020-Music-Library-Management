package com.musicapp.ui;

import com.musicapp.Main;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
        userNameLabel.setText(Main.isAdmin ? "Admin View" : "User View");
        loadView(FXML_DISCOVERY);

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
        // Gộp logic: Nếu team mày muốn nút Settings mở ra WelcomeView (Đăng xuất)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WelcomeView.fxml"));
            Parent root = loader.load();
            // Dùng btnHome để lấy window an toàn, phòng trường hợp btnSettings chưa được gán
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            // Nếu lỗi, fallback về loadView nội bộ như cũ
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
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setText("▶");
        } else {
            mediaPlayer.play();
            isPlaying = true;
            btnPlayPause.setText("⏸");
        }
    }

    @FXML 
    private void onNext() { 
        if (SongListController.instance != null) SongListController.instance.playNext(); 
    }

    @FXML 
    private void onPrev() { 
        if (SongListController.instance != null) SongListController.instance.playPrevious(); 
    }

    @FXML 
    private void onSeek() { 
        if (mediaPlayer != null) mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
    }

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
            // Chỉ update slider nếu user không đang cầm chuột kéo nó
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

    // 1. Hàm cũ (Cập nhật UI chay)
    public void showPlayerBar(String songTitle, String artistName, String imagePath) {
        updatePlayerUI(songTitle, artistName, imagePath);
    }

    // 2. Hàm gọi từ SongListController (Có gắn MediaPlayer)
    public void showPlayerBar(String songTitle, String artistName, String imagePath, MediaPlayer player) {
        setMediaPlayer(player);
        updatePlayerUI(songTitle, artistName, imagePath);
    }

    // 3. Hàm gọi từ AlbumViewController
    public void showPlayerBar(Song currentSong, List<Song> queue, int index) {
        try {
            if (currentSong.getAudioURL() != null && !currentSong.getAudioURL().isEmpty()) {
                Media hit = new Media(currentSong.getAudioURL());
                setMediaPlayer(new MediaPlayer(hit));
                mediaPlayer.play();
            }
            updatePlayerUI(currentSong.getTitle(), currentSong.getArtist(), currentSong.getImageURL());
            labelTotalTime.setText(formatDuration(currentSong.getDuration()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm support ẩn/hiện UI Player Bar chung
    private void updatePlayerUI(String title, String artist, String img) {
        if (playerSongTitle != null) playerSongTitle.setText(title);
        if (playerArtistName != null) playerArtistName.setText(artist);
        
        if (playerArtImage != null && img != null && !img.isEmpty()) {
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
            ctrl.setData(title, subtitle, desc, null, 0, "", data);            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadSongDetail(String albumName, String artist, String genre, int year, String imageURL, List<Song> songs) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlbumDetailView.fxml"));
            Parent view = loader.load();
            AlbumViewController controller = loader.getController();
            controller.setMainController(this);
            controller.setAlbumData(albumName, artist, genre, year, imageURL, songs);
            contentArea.getChildren().setAll(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════
    // SEARCH LOGIC (Chọc thẳng Firebase)
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
            
            // Set initial loading state with 7 arguments
            ctrl.setData("Search Results", "Results for: \"" + query + "\"", "Searching...", null, 0, "", null);
            ctrl.setColumnHeaders("ARTIST", "GENRE", "TIME"); 
            contentArea.getChildren().setAll(view);

            new Thread(() -> {
                try {
                    List<Song> allSongs = DatabaseManager.getInstance().getService().fetchSongs();
                    var results = javafx.collections.FXCollections.<SongListController.SongItem>observableArrayList();
                    String lowerQuery = query.toLowerCase(); // No longer redeclared
                    
                    for (Song song : allSongs) {
                        if (song.getTitle().toLowerCase().contains(lowerQuery) || 
                            song.getArtist().toLowerCase().contains(lowerQuery)) {
                            results.add(new SongListController.SongItem(
                                song.getSongId(), song.getTitle(), song.getArtist(), 
                                song.getGenre(), song.getDuration(), song.getReleaseYear(), 
                                song.getAudioURL(), song.getImageURL()
                            ));
                        }
                    }
                    
                    // Update UI with the final results using 7 arguments
                    Platform.runLater(() -> ctrl.setData("Search Results", "Results for: \"" + query + "\"", results.size() + " found", null, 0, "", results));
                } catch (Exception e) { e.printStackTrace(); }
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

    // ══════════════════════════════════════════
    // Đã gói lại để sửa lỗi cú pháp tơ hơ ở cuối file
    // ══════════════════════════════════════════
    private void testLoadAlbum() {
        List<Song> testSongs = new java.util.ArrayList<>();
        testSongs.add(new Song("1", "Going Bad", "Meek Mill", "Hip-Hop", 181, 2018, "", ""));
        testSongs.add(new Song("2", "Amen", "Meek Mill", "Hip-Hop", 196, 2018, "", ""));
        loadSongDetail("Championship", "Meek Mill", "Hip-Hop", 2018, "", testSongs);
    }
}