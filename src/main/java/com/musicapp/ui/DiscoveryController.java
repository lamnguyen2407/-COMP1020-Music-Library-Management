package com.musicapp.ui;

import com.musicapp.Main;
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

    // ==========================================
    // DATA: Using the unified 8-attribute SongItem
    // ==========================================
    private final ObservableList<SongListController.SongItem> TRENDING_SONGS = FXCollections.observableArrayList(
        // Cấu trúc mới: id, title, artist, genre, duration(s), year, audioURL, imageURL
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
        // Đổ dữ liệu 2026 vào UI
        if(trending1Title != null) trending1Title.setText(TRENDING_SONGS.get(0).title);
        if(trending1Artist != null) trending1Artist.setText(TRENDING_SONGS.get(0).artist);
        if(gotoTitle != null) gotoTitle.setText("It Will Be Okay (2026 Remix)");
    }

    private void setupClickHandlers() {
        if (trendingCard1 != null) {
            trendingCard1.setOnMouseClicked(e -> openDetailedSongList("Trending Hits"));
        }
        // The transition to hit songs when clicking today's hit banner
        if(todaysHitBanner != null) {
        	todaysHitBanner.setOnMouseClicked(e -> {
        		if(mainController != null) {
        			mainController.openSongListView("Today's Hits", "Top tracks", "The biggest track on everyone's mind", null);
        		}
        	});
        }
        // The play button to show ONLY the bottom bar
        if(trending1PlayBtn != null) {
        	trending1PlayBtn.setOnMouseClicked(e -> {
        		e.consume();
        		if(mainController != null) {
        			// Get 1st song (demo)
        			SongListController.SongItem song = TRENDING_SONGS.get(0);
        			// Call the API to show play bar
        			mainController.showPlayerBar(song.title, song.artist, song.imageURL);
        		}
        	});
        }
        // Admin click vào bài hát để quản lý, User click để nghe
        if (latest1Title != null) {
            latest1Title.setOnMouseClicked(e -> openCompactList());
        }
    }

    private void openDetailedSongList(String listTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();

            SongListController ctrl = loader.getController();
            
            // 👉 BƯỚC QUAN TRỌNG: Bàn giao mainController cho thằng con mới
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
                System.out.println("✅ Discovery đã truyền mainController sang SongList!");
            }
            
            ctrl.setData(listTitle, "2026 Global Chart", "Top trending songs worldwide.", null, TRENDING_SONGS);
            updateMainContent(view);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openCompactList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();
            
            // 👉 CŨNG PHẢI NỐI DÂY Ở ĐÂY LUÔN
            Object ctrl = loader.getController();
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
                System.out.println("✅ Discovery đã truyền mainController sang CompactList!");
            }

            updateMainContent(view);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Logic tráo đổi content mượt mà trong MainView
     */
    private void updateMainContent(Node newView) {
        // Lấy contentArea thông qua trendingCard1 (Node thực tế) để tránh lỗi getScene()
        if (trendingCard1 != null && trendingCard1.getScene() != null) {
            StackPane contentArea = (StackPane) trendingCard1.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(newView);
            }
        }
    }
}