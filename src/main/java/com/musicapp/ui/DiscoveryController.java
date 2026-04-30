package com.musicapp.ui;

import com.musicapp.Main;
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
    @FXML private Label trending1Title, trending1Artist, trending2Title, trending2Artist;
    @FXML private Label gotoTitle, gotoArtist;
    @FXML private Label latest1Title, latest1Artist; 

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
        
        // Nút Play nhanh ở Trending
        if(trending1PlayBtn != null) {
            trending1PlayBtn.setOnMouseClicked(e -> {
                e.consume();
                if(mainController != null) {
                    SongListController.SongItem song = TRENDING_SONGS.get(0);
                    mainController.showPlayerBar(song.title, song.artist, song.imageURL, null);
                }
            });
        }
        
        if (latest1Title != null) {
            latest1Title.setOnMouseClicked(e -> openCompactList());
        }
    }

    private void openDetailedSongList(String listTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            // 1. Kết nối với MainViewController
            ctrl.setMainController(this.mainController);
            
            // 2. XÁC ĐỊNH ID CHUẨN (Lấy đúng cái SYSTEM_TODAY'S_HITS)
            // Logic này sẽ biến "Today's Hits" thành "SYSTEM_TODAY'S_HITS"
            String finalId = "SYSTEM_" + listTitle.toUpperCase().replace(" ", "_");
            
            // 3. TRUYỀN DỮ LIỆU SANG SONG_LIST_CONTROLLER
            // Truyền một list rỗng (new ArrayList<>()) vào tham số thứ 8.
            // TẠI SAO? Để hàm refreshData() trong SongListController thấy list rỗng 
            // và tự động lên Firebase kéo bài hát từ node finalId về.
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

            // 4. Đưa giao diện lên màn hình
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
}