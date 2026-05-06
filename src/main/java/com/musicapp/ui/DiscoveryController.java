package com.musicapp.ui;

import com.musicapp.Main;
import com.musicapp.model.SessionManager;
import com.musicapp.service.DatabaseManager;
import com.musicapp.model.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for DiscoveryView.fxml
 * Handles the home screen logic and role-based data display.
 */
public class DiscoveryController implements Initializable, MainViewController.MainViewAware {

    @FXML private StackPane trendingCard1, trendingCard2, trendingCard3, trendingCard4;
    @FXML private StackPane trending1PlayBtn;
    @FXML private StackPane todaysHitBanner;
    @FXML private StackPane newAlbumCard1, newAlbumCard2, newAlbumCard3, newAlbumCard4;
    @FXML private HBox newAlbumsHeader;
    @FXML private Label trending1Title, trending1Artist, trending2Title, trending2Artist;
    @FXML private Label gotoTitle, gotoArtist;
    @FXML private Label latest1Title, latest1Artist; 
    @FXML private Label heartBtn1, heartBtn2, heartBtn3, heartBtn4, heartBtn5, heartBtn6, heartBtn7, heartBtn8, heartBtn9;

    private MainViewController mainController;

    // Dữ liệu mẫu cho Trending
    private final ObservableList<SongListController.SongItem> TRENDING_SONGS = FXCollections.observableArrayList(
        new SongListController.SongItem("t1", "Going Bad (feat. Drake)", "Meek Mill", "Hip-hop", 181, 2026, "url", "/images/song1.png"),
        new SongListController.SongItem("t2", "HIGHEST IN THE ROOM", "Travis Scott", "Trap", 176, 2026, "url", "/images/song2.png")
    );

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSampleData();
        setupClickHandlers();

        // Ẩn trái tim ở phần Latest Song nếu role là admin
        if (SessionManager.isAdmin) {
            Label[] hearts = {heartBtn1, heartBtn2, heartBtn3, heartBtn4, heartBtn5, heartBtn6, heartBtn7, heartBtn8, heartBtn9};
            for (Label heart : hearts) {
                if (heart != null) {
                    heart.setVisible(false);
                    heart.setManaged(false);
                }
            }
        }
    }

    private void populateSampleData() {
        if(trending1Title != null) trending1Title.setText(TRENDING_SONGS.get(0).title);
        if(trending1Artist != null) trending1Artist.setText(TRENDING_SONGS.get(0).artist);
        if(gotoTitle != null) gotoTitle.setText("It Will Be Okay (2026 Remix)");
    }

    private void setupClickHandlers() {
        // Click vào Trending
        if (trendingCard1 != null) {
            trendingCard1.setOnMouseClicked(e -> openDetailedSongList("Trending Hits"));
        }
        
        // Click vào Today's Hits Banner
        if(todaysHitBanner != null) {
            todaysHitBanner.setOnMouseClicked(e -> openDetailedSongList("Today's Hits"));
        }
        
        // Click vào New Album Header (NEW!)
        if (newAlbumsHeader != null) {
            newAlbumsHeader.setOnMouseClicked(e -> {
                if (mainController != null) mainController.openAllAlbumsView();
            });
        }
        
        // Nút Play nhanh ở Trending (FIXED NULL CRASH)
        if(trending1PlayBtn != null) {
            trending1PlayBtn.setOnMouseClicked(e -> {
                e.consume(); // Prevent the card behind it from being clicked
                if(mainController != null) {
                    SongListController.SongItem song = TRENDING_SONGS.get(0);
                    // FIXED: Use the 3-argument version so it doesn't crash trying to load a null audio file
                    mainController.showPlayerBar(song.title, song.artist, song.imageURL);
                }
            });
        }
        
        if (latest1Title != null) {
            latest1Title.setOnMouseClicked(e -> openCompactList());
        }

        // Click New Album Cards to go to AlbumDetail
        if (newAlbumCard1 != null) {
            newAlbumCard1.setOnMouseClicked(e -> {
                if (mainController != null) {
                    List<Song> dummySongs = new ArrayList<>();
                    dummySongs.add(new Song("1", "Shape of You", "Ed Sheeran", "Pop", 233, 2017, "", ""));
                    dummySongs.add(new Song("2", "Castle on the Hill", "Ed Sheeran", "Pop", 261, 2017, "", ""));
                    mainController.loadSongDetail("Divide", "Ed Sheeran", "Pop", 2017, "", dummySongs);
                }
            });
        }
        
        if (newAlbumCard2 != null) {
            newAlbumCard2.setOnMouseClicked(e -> {
                if (mainController != null) {
                    List<Song> dummySongs = new ArrayList<>();
                    dummySongs.add(new Song("3", "Blinding Lights", "The Weeknd", "R&B", 200, 2020, "", ""));
                    mainController.loadSongDetail("After Hours", "The Weeknd", "R&B", 2020, "", dummySongs);
                }
            });
        }

        // Connect the "New Album Release >" header to view ALL albums
        if (newAlbumsHeader != null) {
            newAlbumsHeader.setOnMouseClicked(e -> {
                if (mainController != null) mainController.openAllAlbumsView();
            });
        }

        setupHeartToggle(heartBtn1);
        setupHeartToggle(heartBtn2);
        setupHeartToggle(heartBtn3);
        setupHeartToggle(heartBtn4);
        setupHeartToggle(heartBtn5);
        setupHeartToggle(heartBtn6);
        setupHeartToggle(heartBtn7);
        setupHeartToggle(heartBtn8);
        setupHeartToggle(heartBtn9);
    }

    private void openDetailedSongList(String listTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            // 1. Gắn MainViewController
            ctrl.setMainController(this.mainController);
            
            // 2. XÁC ĐỊNH ID CHUẨN (Lấy luôn cái SYSTEM_TODAY'S_HITS)
            // Logic biến "Today's Hits" thành "SYSTEM_TODAY'S_HITS"
            String finalId = "SYSTEM_" + listTitle.toUpperCase().replace(" ", "_");
            
            // 3. TRUYỀN DỮ LIỆU SANG SONG_LIST_CONTROLLER
            // Truyền 1 list rỗng (new ArrayList<>()) vào tham số ID
            // TẠI SAO? Vì hàm refreshData() trong SongListController thấy list rỗng
            // sẽ tự động lên Firebase kéo node finalId về!
            ctrl.setData(
                finalId, 
                listTitle, 
                "Curated for you", 
                "System Playlist", 
                "/images/playlist_cover.png", 
                2026, 
                "Various", 
                new java.util.ArrayList<>() 
            );

            // 4. Đẩy giao diện lên
            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openCompactList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();
            
            if (loader.getController() instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) loader.getController()).setMainController(this.mainController);
            }

            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setupHeartToggle(Label heartBtn) {
        if (heartBtn == null) return;
        
        heartBtn.setOnMouseClicked(e -> {
            e.consume(); // Prevent the click from triggering the song row click
            
            String currentFill = heartBtn.getStyle();
            if (currentFill.contains("#D32F2F")) {
                // It's currently red, switch it back to brown (un-like)
                heartBtn.setStyle("-fx-text-fill: #B08D6A; -fx-font-size: 16px; -fx-cursor: hand;");
            } else {
                // It's currently brown, switch it to red (like)
                heartBtn.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 16px; -fx-cursor: hand;");
                System.out.println("Added song to favorites!"); 
            }
        });
    }
}