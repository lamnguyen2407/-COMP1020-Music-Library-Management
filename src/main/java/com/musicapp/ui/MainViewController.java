package com.musicapp.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.google.firebase.database.*;

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
    // FXML — TopBar
    // ══════════════════════════════════════════
    @FXML private HBox topBar;
    @FXML private TextField searchField;
    @FXML private Label userNameLabel;
    @FXML private ImageView userImageView;

    // ══════════════════════════════════════════
    // FXML — Sidebar & Content
    // ══════════════════════════════════════════
    @FXML private Button btnHome, btnAccount, btnSearch, btnPlaylists, btnSettings;
    @FXML private StackPane contentArea;

    // ══════════════════════════════════════════
    // FXML — Nhúng PlaybackView (fx:id="playback")
    // ══════════════════════════════════════════
    @FXML private PlaybackViewController playbackController;

    // ══════════════════════════════════════════
    // State
    // ══════════════════════════════════════════
    private MediaPlayer mediaPlayer; 

    private static final String FXML_DISCOVERY = "/DiscoveryView.fxml";
    private static final String FXML_ACCOUNT = "/AccountView.fxml";
    private static final String FXML_PLAYLIST = "/PlaylistOverview.fxml";
    private static final String FXML_SETTINGS = "/SettingsView.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userNameLabel.setText(SessionManager.isAdmin ? "Admin View" : "User View");
        loadView(FXML_DISCOVERY);

        // Khởi tạo tham chiếu cho Controller con
        if (playbackController != null) {
            playbackController.setMainController(this);
        } else {
            System.err.println("⚠️ CẢNH BÁO: Không tìm thấy playbackController!");
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isBlank()) setActiveNav(btnSearch);
            });
            searchField.setOnMouseClicked(e -> setActiveNav(btnSearch));
            searchField.setOnAction(e -> handleSearchRequest());
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
    // AUDIO ENGINE (ĐIỀU KHIỂN NHẠC)
    // ══════════════════════════════════════════

    public void setMediaPlayer(MediaPlayer player) {
        if (this.mediaPlayer != null) this.mediaPlayer.stop();
        this.mediaPlayer = player;
        
        // Tự động chuyển bài khi hát xong
        this.mediaPlayer.setOnEndOfMedia(() -> {
            if (playbackController != null) playbackController.onNext();
        });
    }

    public void pauseAudio() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
        }
    }

    public void resumeAudio() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.play();
        }
    }

    // ══════════════════════════════════════════
    // HIỂN THỊ THANH NHẠC
    // ══════════════════════════════════════════

    public void showPlayerBar(Song currentSong, List<Song> queue, int index) {
        try {
            // 1. Mồi Playlist cho Backend
            if (queue != null && !queue.isEmpty()) {
                com.musicapp.service.PlaybackService.getInstance().setPlaylist(queue, index);
            }
            com.musicapp.service.PlaybackService.getInstance().play(currentSong);

            // 2. Chơi nhạc thật
            if (currentSong.getAudioURL() != null && !currentSong.getAudioURL().isEmpty()) {
                String uriString = currentSong.getAudioURL().trim().replace(" ", "%20");
                Media hit = new Media(uriString);
                setMediaPlayer(new MediaPlayer(hit));
                mediaPlayer.play();
            }

            // 3. Đá UI sang cho PlaybackViewController
            if (playbackController != null) {
                playbackController.showBar();
                playbackController.setSongData(currentSong);
            }

        } catch (Exception e) { 
            System.err.println("❌ LỖI KHỞI TẠO MEDIA: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    // CÁC HÀM TƯƠNG THÍCH NGƯỢC
    // ══════════════════════════════════════════

    public void showPlayerBar(String songTitle, String artistName, String imagePath) {
        if (playbackController != null) {
            Song tempSong = new Song("temp_id", songTitle, artistName, "", 0, 2026, "", imagePath);
            playbackController.showBar();
            playbackController.setSongData(tempSong);
        }
    }

    public void showPlayerBar(String songTitle, String artistName, String imagePath, MediaPlayer player) {
        setMediaPlayer(player);
        showPlayerBar(songTitle, artistName, imagePath);
    }

    public void showPlayerBar(String songTitle, String artistName, String imagePath, String audioUrl) {
        try {
            if (audioUrl != null && !audioUrl.isEmpty()) {
                String uriString = audioUrl.trim().replace(" ", "%20");
                Media hit = new Media(uriString);
                setMediaPlayer(new MediaPlayer(hit));
                mediaPlayer.play();
            }
            
            if (playbackController != null) {
                Song tempSong = new Song("temp_id", songTitle, artistName, "", 0, 2026, audioUrl, imagePath);
                playbackController.showBar();
                playbackController.setSongData(tempSong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ctrl.setData("SONG_LIST_VIEW", title, subtitle, desc, "/images/allsong.jpg", 0, "", new java.util.ArrayList<>());            
            ctrl.setSongsList(data);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public void openAllAlbumsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewAlbumReleaseView.fxml"));
            Node view = loader.load();
            Object childController = loader.getController();
            if (childController instanceof MainViewAware) ((MainViewAware) childController).setMainController(this);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void fetchAndLoadAlbum(String albumName, String artist, String genre, int year, String imageURL, List<String> songIds) {
        List<Song> realSongs = new ArrayList<>();
        if (songIds == null || songIds.isEmpty()) {
            loadSongDetail(albumName, artist, genre, year, imageURL, realSongs);
            return;
        }

        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("ADMIN_ALL_SONGS");
        AtomicInteger loadedCount = new AtomicInteger(0);

        for (String id : songIds) {
            songsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Song song = snapshot.getValue(Song.class);
                        if (song != null) realSongs.add(song);
                    }
                    checkIfFinished();
                }
                @Override public void onCancelled(DatabaseError error) { checkIfFinished(); }

                private void checkIfFinished() {
                    if (loadedCount.incrementAndGet() == songIds.size()) {
                        Platform.runLater(() -> loadSongDetail(albumName, artist, genre, year, imageURL, realSongs));
                    }
                }
            });
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
    // SEARCH LOGIC
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
            
            ctrl.setData("SEARCH_VIEW", "Search Results", "Results for: \"" + query + "\"", "Searching...", null, 0, "Various", new java.util.ArrayList<>());
            ctrl.setColumnHeaders("SONG", "ARTIST", "GENRE"); 
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
                        ctrl.setData("SEARCH_VIEW", "Search Results", "Results for: \"" + query + "\"", results.size() + " found", null, 0, "Various", new java.util.ArrayList<>());
                        ctrl.setSongsList(results); 
                    });
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
    public javafx.scene.layout.StackPane getContentArea() { return contentArea; }
}