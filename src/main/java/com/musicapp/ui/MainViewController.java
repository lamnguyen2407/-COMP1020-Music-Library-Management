package com.musicapp.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * MainViewController.java
 *
 * Controller cho MainView.fxml — khung shell chính của ứng dụng.
 *
 * Chức năng:
 *  - Quản lý điều hướng sidebar (Home, Account, Search, Playlists, Settings)
 *  - Load/inject các View con vào contentArea (StackPane)
 *  - Điều khiển PlayerBar (play/pause, prev, next, shuffle, repeat, seek, volume, like)
 *  - Toggle giữa TopBar và PlayerBar
 *
 * package com.musicapp.ui;
 * FXML:    resources/fxml/MainView.fxml
 */
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
    // FXML paths cho các View con
    // ══════════════════════════════════════════
    private static final String FXML_DISCOVERY = "/com/music/ui/DiscoveryView.fxml";
    private static final String FXML_ACCOUNT   = "/com/music/ui/AccountView.fxml";
    private static final String FXML_PLAYLIST  = "/com/music/ui/PlaylistOverview.fxml";
    private static final String FXML_SETTINGS  = "/com/music/ui/SettingsView.fxml";

    // ══════════════════════════════════════════
    // Initializable
    // ══════════════════════════════════════════

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load trang Home mặc định khi app khởi động
        loadView(FXML_DISCOVERY);

        // Gắn listener cho search field:
        // Khi user bắt đầu gõ → focus vào search field và highlight nav-btn Search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isBlank()) {
                setActiveNav(btnSearch);
            }
        });

        // Focus vào search field khi click vào nó
        searchField.setOnMouseClicked(e -> setActiveNav(btnSearch));

        // Volume mặc định
        volumeSlider.setValue(70);
    }

    // ══════════════════════════════════════════
    // NAVIGATION HANDLERS
    // ══════════════════════════════════════════

    /**
     * Bấm "Home" → load DiscoveryView vào contentArea
     */
    @FXML
    private void onNavHome() {
        setActiveNav(btnHome);
        loadView(FXML_DISCOVERY);
    }

    /**
     * Bấm "Account" → load AccountView vào contentArea
     */
    @FXML
    private void onNavAccount() {
        setActiveNav(btnAccount);
        loadView(FXML_ACCOUNT);
    }

    /**
     * Bấm "Search" → focus vào search bar, không chuyển trang
     * (User gõ từ khóa trực tiếp vào searchField trên TopBar)
     */
    @FXML
    private void onNavSearch() {
        setActiveNav(btnSearch);
        searchField.requestFocus();
        searchField.selectAll();
    }

    /**
     * Bấm "Playlists" → load PlaylistOverview vào contentArea
     */
    @FXML
    private void onNavPlaylists() {
        setActiveNav(btnPlaylists);
        loadView(FXML_PLAYLIST);
    }

    /**
     * Bấm "Settings" → load SettingsView vào contentArea (nếu có)
     */
    @FXML
    private void onNavSettings() {
        loadView(FXML_SETTINGS);
    }

    // ══════════════════════════════════════════
    // PLAYER BAR HANDLERS
    // ══════════════════════════════════════════

    /**
     * Toggle Play / Pause
     */
    @FXML
    private void onPlayPause() {
        isPlaying = !isPlaying;
        btnPlayPause.setText(isPlaying ? "⏸" : "▶");
        // TODO: gọi MediaPlayer.play() / MediaPlayer.pause() ở đây
    }

    /**
     * Bài trước
     */
    @FXML
    private void onPrev() {
        // TODO: PlayerService.previous()
        System.out.println("[Player] Previous track");
    }

    /**
     * Bài tiếp theo
     */
    @FXML
    private void onNext() {
        // TODO: PlayerService.next()
        System.out.println("[Player] Next track");
    }

    /**
     * Toggle Shuffle
     */
    @FXML
    private void onShuffle() {
        isShuffled = !isShuffled;
        btnShuffle.setStyle(isShuffled ? "-fx-opacity: 1;" : "-fx-opacity: 0.5;");
        // TODO: PlayerService.setShuffle(isShuffled)
    }

    /**
     * Toggle Repeat
     */
    @FXML
    private void onRepeat() {
        isRepeating = !isRepeating;
        btnRepeat.setStyle(isRepeating ? "-fx-opacity: 1;" : "-fx-opacity: 0.5;");
        // TODO: PlayerService.setRepeat(isRepeating)
    }

    /**
     * Seek khi user thả chuột khỏi progress slider
     */
    @FXML
    private void onSeek() {
        double seekTo = progressSlider.getValue(); // 0–100
        System.out.println("[Player] Seek to: " + seekTo + "%");
        // TODO: MediaPlayer.seek(Duration.seconds(totalDuration * seekTo / 100))
    }

    /**
     * Toggle Like / Unlike bài hát
     */
    @FXML
    private void onToggleLike() {
        isLiked = !isLiked;
        btnLike.setText(isLiked ? "♥" : "♡");
        // TODO: gọi API thêm/xóa khỏi danh sách yêu thích
    }

    // ══════════════════════════════════════════
    // PUBLIC API — gọi từ các controller con
    // ══════════════════════════════════════════

    /**
     * Hiện PlayerBar, ẩn TopBar (gọi khi user nhấn Play một bài)
     * Dùng FadeTransition để transition mượt.
     */
    public void showPlayerBar(String songTitle, String artistName) {
        playerSongTitle.setText(songTitle);
        playerArtistName.setText(artistName);

        topBar.setVisible(false);
        topBar.setManaged(false);

        playerBar.setManaged(true);
        playerBar.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(300), playerBar);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        isPlaying = true;
        btnPlayPause.setText("⏸");
    }

    /**
     * Ẩn PlayerBar, hiện lại TopBar (gọi khi dừng phát hoàn toàn)
     */
    public void hidePlayerBar() {
        playerBar.setVisible(false);
        playerBar.setManaged(false);

        topBar.setManaged(true);
        topBar.setVisible(true);

        isPlaying = false;
        btnPlayPause.setText("▶");
    }

    /**
     * Cập nhật thời gian hiển thị trên progress bar.
     *
     * @param currentSeconds thời gian hiện tại (giây)
     * @param totalSeconds   tổng thời lượng (giây)
     */
    public void updateProgress(int currentSeconds, int totalSeconds) {
        labelCurrentTime.setText(formatTime(currentSeconds));
        labelTotalTime.setText(formatTime(totalSeconds));

        if (totalSeconds > 0) {
            progressSlider.setValue((double) currentSeconds / totalSeconds * 100);
        }
    }

    // ══════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════

    /**
     * Load một FXML view con vào contentArea với hiệu ứng fade.
     */
    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("[MainViewController] FXML not found: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();

            // Truyền tham chiếu MainViewController sang controller con
            // (nếu controller con implement MainViewAware)
            Object childController = loader.getController();
            if (childController instanceof MainViewAware) {
                ((MainViewAware) childController).setMainController(this);
            }

            // Fade transition khi chuyển trang
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

    /**
     * Đặt trạng thái active cho nav button được chọn,
     * xóa active khỏi các button còn lại.
     */
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

    /**
     * Format giây thành chuỗi "m:ss"
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }

    // ══════════════════════════════════════════
    // INNER INTERFACE — để controller con giao tiếp ngược lại
    // ══════════════════════════════════════════

    /**
     * Các controller con (Discovery, Playlist…) implement interface này
     * để nhận tham chiếu MainViewController và gọi showPlayerBar() khi cần.
     *
     * Ví dụ trong DiscoveryViewController:
     *
     *   public class DiscoveryViewController implements MainViewAware {
     *       private MainViewController mainController;
     *
     *       @Override
     *       public void setMainController(MainViewController mc) {
     *           this.mainController = mc;
     *       }
     *
     *       private void playSong(Song song) {
     *           mainController.showPlayerBar(song.getTitle(), song.getArtist());
     *       }
     *   }
     */
    public interface MainViewAware {
        void setMainController(MainViewController mainController);
    }
}