package com.musicapp.ui;

import com.musicapp.Main; // IMPORTING THE GLOBAL STATE
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
    @FXML private Label     userNameLabel;
    @FXML private ImageView userImageView;

    // ══════════════════════════════════════════
    // FXML — PlayerBar
    // ══════════════════════════════════════════
    @FXML private HBox      playerBar;
    @FXML private ImageView playerArtImage;
    @FXML private Label     playerSongTitle;
    @FXML private Label     playerArtistName;
    @FXML private Button    btnLike;
    @FXML private Button    btnShuffle;
    @FXML private Button    btnPrev;
    @FXML private Button    btnPlayPause;
    @FXML private Button    btnNext;
    @FXML private Button    btnRepeat;
    @FXML private Label     labelCurrentTime;
    @FXML private Label     labelTotalTime;
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
    private boolean isLiked     = false;
    private boolean isShuffled  = false;
    private boolean isRepeating = false;

    // ══════════════════════════════════════════
    // FXML paths
    // ══════════════════════════════════════════
    // FIX: Updated paths to match standard Maven resource structure (removed /com/music/ui/)
    private static final String FXML_DISCOVERY = "/DiscoveryView.fxml";
    private static final String FXML_ACCOUNT   = "/AccountView.fxml";
    private static final String FXML_PLAYLIST  = "/PlaylistOverview.fxml";
    private static final String FXML_SETTINGS  = "/SettingsView.fxml";

    // ══════════════════════════════════════════
    // Initializable
    // ══════════════════════════════════════════

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // --- ADMIN/USER DISGUISE LOGIC ---
        if (Main.isAdmin) {
            userNameLabel.setText("Admin View");
            // Optional: You could change the avatar image here to an admin icon
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
            
            // 👉 CHÈN THÊM DÒNG NÀY ĐỂ BẮT SỰ KIỆN NÚT ENTER
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

    // ... (Player Bar Handlers remain unchanged as they are perfectly stubbed) ...

    @FXML private void onPlayPause() { /* Stub */ }
    @FXML private void onPrev() { /* Stub */ }
    @FXML private void onNext() { /* Stub */ }
    @FXML private void onShuffle() { /* Stub */ }
    @FXML private void onRepeat() { /* Stub */ }
    @FXML private void onSeek() { /* Stub */ }
    @FXML private void onToggleLike() { /* Stub */ }

    // ══════════════════════════════════════════
    // PUBLIC API
    // ══════════════════════════════════════════

    public void showPlayerBar(String songTitle, String artistName, String imagePath) {
        // 1. Cập nhật thông tin bài hát
        if (playerSongTitle != null) playerSongTitle.setText(songTitle);
        if (playerArtistName != null) playerArtistName.setText(artistName);
        if (playerArtImage != null && imagePath != null && !imagePath.isEmpty()) {
            try {
                // Thử load ảnh từ resources
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    playerArtImage.setImage(new Image(imageUrl.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Không load được ảnh bìa trên thanh Bar");
            }
        }
        // 2. Hiển thị thanh Player Bar (Nếu nó đang ẩn)
        if (playerBar != null && !playerBar.isVisible()) {
            playerBar.setManaged(true);
            playerBar.setVisible(true);

            // Hiệu ứng hiện hình trong 0.3 giây
            FadeTransition ft = new FadeTransition(Duration.millis(300), playerBar);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }

        // 3. Đổi trạng thái nút Play sang Pause
        isPlaying = true;
        if (btnPlayPause != null) btnPlayPause.setText("⏸");
        
        // 4. (Mở rộng) Cập nhật ảnh nhỏ trên thanh Player nếu muốn
        // if (playerArtImage != null) playerArtImage.setImage(new Image("..."));
    }

    public void hidePlayerBar() {
        if (playerBar != null) {
            playerBar.setVisible(false);
            playerBar.setManaged(false);
        }

        if (topBar != null) {
            topBar.setManaged(true);
            topBar.setVisible(true);
        }

        isPlaying = false;
        if (btnPlayPause != null) btnPlayPause.setText("▶");
    }

    public void updateProgress(int currentSeconds, int totalSeconds) {
        if (labelCurrentTime != null) labelCurrentTime.setText(formatTime(currentSeconds));
        if (labelTotalTime != null) labelTotalTime.setText(formatTime(totalSeconds));

        if (totalSeconds > 0 && progressSlider != null) {
            progressSlider.setValue((double) currentSeconds / totalSeconds * 100);
        }
    }

    // ══════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════

    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("[MainViewController] CRITICAL FXML not found: " + fxmlPath + ". Check resources folder.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();

            Object childController = loader.getController();
            if (childController instanceof MainViewAware) {
                ((MainViewAware) childController).setMainController(this);
            }

            view.setOpacity(0);
            contentArea.getChildren().setAll(view);

            FadeTransition ft = new FadeTransition(Duration.millis(250), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            System.err.println("[MainViewController] Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveNav(Button selected) {
        Button[] navButtons = { btnHome, btnAccount, btnSearch, btnPlaylists };
        for (Button btn : navButtons) {
            if (btn == null) continue;
            btn.getStyleClass().removeAll("nav-btn-active");
        }
        if (selected != null && !selected.getStyleClass().contains("nav-btn-active")) {
            selected.getStyleClass().add("nav-btn-active");
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }
 // ══════════════════════════════════════════
    // LOGIC XỬ LÝ TÌM KIẾM (SEARCH)
    // ══════════════════════════════════════════

    private void handleSearchRequest() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) return;

        System.out.println("[MainView] Đang tìm kiếm: " + query);
        navigateToSearchResult(query);
    }

    private void navigateToSearchResult(String query) {
        try {
            // Dùng lại SongListView.fxml để làm trang hiển thị kết quả
            URL resource = getClass().getResource("/SongListView.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();

            SongListController ctrl = loader.getController();

            if (ctrl instanceof MainViewAware) {
                ((MainViewAware) ctrl).setMainController(this);
                System.out.println("✅ Đã nối dây cho trang kết quả tìm kiếm!");
            }
            
            // 1. Lấy kho nhạc tổng từ MusicService
            javafx.collections.ObservableList<SongListController.SongItem> allSongs = MusicService.getGlobalLibrary();
            
            // 2. Lọc ra các bài có chứa từ khóa (Title hoặc Artist)
            javafx.collections.ObservableList<SongListController.SongItem> results = javafx.collections.FXCollections.observableArrayList();
            
            String lowerQuery = query.toLowerCase();
            for (SongListController.SongItem song : allSongs) {
                if (song.title.toLowerCase().contains(lowerQuery) || 
                    song.artist.toLowerCase().contains(lowerQuery)) {
                    results.add(song);
                }
            }

            // 3. Đổ dữ liệu vào giao diện SongListView
            ctrl.setData(
                "Search Results", 
                "Showing matches for: \"" + query + "\"", 
                results.size() + " songs found.", 
                null, 
                results
            );
            
            ctrl.setColumnHeaders("SONG", "ARTIST", "ALBUM");

            // Hiển thị lên màn hình (kèm hiệu ứng mờ ảo)
            view.setOpacity(0);
            contentArea.getChildren().setAll(view);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(250), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            System.err.println("[MainView] Lỗi load trang Search");
            e.printStackTrace();
        }
    }
    
    public interface MainViewAware {
        void setMainController(MainViewController mainController);
    }
    
  
}