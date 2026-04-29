package com.musicapp.ui;

import com.musicapp.Main;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    // ══════════════════════════════════════════
    // FXML — TopBar
    // ══════════════════════════════════════════
    @FXML private HBox      topBar;
    @FXML private TextField searchField;
    @FXML private Label      userNameLabel;
    @FXML private ImageView userImageView;

    // ══════════════════════════════════════════
    // FXML — PlayerBar
    // ══════════════════════════════════════════
    @FXML private HBox      playerBar;
    @FXML private ImageView playerArtImage;
    @FXML private Label      playerSongTitle;
    @FXML private Label      playerArtistName;
    @FXML private Button    btnLike;
    @FXML private Button    btnShuffle;
    @FXML private Button    btnPrev;
    @FXML private Button    btnPlayPause;
    @FXML private Button    btnNext;
    @FXML private Button    btnRepeat;
    @FXML private Label      labelCurrentTime;
    @FXML private Label      labelTotalTime;
    @FXML private Slider    progressSlider;
    @FXML private Slider    volumeSlider;

    // ══════════════════════════════════════════
    // FXML — Sidebar nav buttons
    // ══════════════════════════════════════════
    @FXML private Button    btnHome;
    @FXML private Button    btnAccount;
    @FXML private Button    btnSearch;
    @FXML private Button    btnPlaylists;

    // ══════════════════════════════════════════
    // FXML — Content area
    // ══════════════════════════════════════════
    @FXML private StackPane contentArea;

    // ══════════════════════════════════════════
    // State
    // ══════════════════════════════════════════
    private boolean isPlaying   = false;
    private boolean isLiked      = false;
    private boolean isShuffled  = false;
    private boolean isRepeating = false;

    // ══════════════════════════════════════════
    // FXML paths (Đã dọn dẹp các dòng trùng lặp)
    // ══════════════════════════════════════════
    private static final String FXML_DISCOVERY = "/DiscoveryView.fxml";
    private static final String FXML_ACCOUNT   = "/AccountView.fxml";
    private static final String FXML_PLAYLIST  = "/PlaylistOverview.fxml";
    private static final String FXML_SETTINGS  = "/SettingsView.fxml";

    // ══════════════════════════════════════════
    // Initializable
    // ══════════════════════════════════════════

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        if (Main.isAdmin) {
            userNameLabel.setText("Admin View");
        } else {
            userNameLabel.setText("User View");
        }
        
        loadView(FXML_DISCOVERY);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isBlank()) {
                    setActiveNav(btnSearch);
                }
            });
            searchField.setOnMouseClicked(e -> setActiveNav(btnSearch));
            searchField.setOnAction(e -> handleSearchRequest());
        }
        
        if (volumeSlider != null) volumeSlider.setValue(70);
    }

    // ══════════════════════════════════════════
    // NAVIGATION HANDLERS
    // ══════════════════════════════════════════

    @FXML
    private void onNavHome() {
        setActiveNav(btnHome);
        loadView(FXML_DISCOVERY);
    }

    @FXML
    private void onNavAccount() {
        setActiveNav(btnAccount);
        loadView(FXML_ACCOUNT);
    }

    @FXML
    private void onNavSearch() {
        setActiveNav(btnSearch);
        if (searchField != null) {
            searchField.requestFocus();
            searchField.selectAll();
        }
    }

    @FXML
    private void onNavPlaylists() {
        setActiveNav(btnPlaylists);
        loadView(FXML_PLAYLIST);
    }

    @FXML
    private void onNavSettings() {
        loadView(FXML_SETTINGS);
    }

    // ══════════════════════════════════════════
    // PLAYER BAR HANDLERS (Stubs)
    // ══════════════════════════════════════════
    @FXML private void onPlayPause() { isPlaying = !isPlaying; btnPlayPause.setText(isPlaying ? "⏸" : "▶"); }
    @FXML private void onPrev() { System.out.println("Prev clicked"); }
    @FXML private void onNext() { System.out.println("Next clicked"); }
    @FXML private void onShuffle() { isShuffled = !isShuffled; System.out.println("Shuffle: " + isShuffled); }
    @FXML private void onRepeat() { isRepeating = !isRepeating; System.out.println("Repeat: " + isRepeating); }
    @FXML private void onSeek() { System.out.println("Seeking to: " + progressSlider.getValue()); }
    @FXML private void onToggleLike() { isLiked = !isLiked; btnLike.setText(isLiked ? "❤️" : "♡"); }

    // ══════════════════════════════════════════
    // PUBLIC API
    // ══════════════════════════════════════════

    public void showPlayerBar(String songTitle, String artistName, String imagePath) {
        if (playerSongTitle != null) playerSongTitle.setText(songTitle);
        if (playerArtistName != null) playerArtistName.setText(artistName);
        
        if (playerArtImage != null && imagePath != null && !imagePath.isEmpty()) {
            try {
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    playerArtImage.setImage(new Image(imageUrl.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Không load được ảnh bìa trên thanh Bar");
            }
        }

        if (playerBar != null && !playerBar.isVisible()) {
            playerBar.setManaged(true);
            playerBar.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(300), playerBar);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }

        isPlaying = true;
        if (btnPlayPause != null) btnPlayPause.setText("⏸");
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
            if (ctrl instanceof MainViewAware) {
                ((MainViewAware) ctrl).setMainController(this);
            }
            
            // Logic lọc nhạc (Sử dụng MusicService mày đã viết)
            var allSongs = MusicService.getGlobalLibrary();
            var results = javafx.collections.FXCollections.<SongListController.SongItem>observableArrayList();
            
            String lowerQuery = query.toLowerCase();
            for (var song : allSongs) {
                if (song.title.toLowerCase().contains(lowerQuery) || 
                    song.artist.toLowerCase().contains(lowerQuery)) {
                    results.add(song);
                }
            }

            ctrl.setData("Search Results", "Results for: \"" + query + "\"", results.size() + " found", null, 0, "", results);            ctrl.setColumnHeaders("SONG", "ARTIST", "GENRE");

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════

    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) return;

            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();

            Object childController = loader.getController();
            if (childController instanceof MainViewAware) {
                ((MainViewAware) childController).setMainController(this);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveNav(Button selected) {
        Button[] navButtons = { btnHome, btnAccount, btnSearch, btnPlaylists };
        for (Button btn : navButtons) {
            if (btn != null) btn.getStyleClass().removeAll("nav-btn-active");
        }
        if (selected != null) selected.getStyleClass().add("nav-btn-active");
    }

    public interface MainViewAware {
        void setMainController(MainViewController mainController);
    }
}