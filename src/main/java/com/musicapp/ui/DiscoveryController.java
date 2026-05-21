package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.MusicService;
import com.musicapp.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DiscoveryController implements Initializable, MainViewController.MainViewAware {

    @FXML private StackPane trendingCard1, trendingCard2, trendingCard3, trendingCard4, trendingCard5, trendingCard6;
    @FXML private StackPane todaysHitBanner;
    @FXML private StackPane newAlbumCard1, newAlbumCard2, newAlbumCard3, newAlbumCard4, newAlbumCard5;
    
    @FXML private Label trending1Title, trending2Title, trending3Title, trending4Title, trending5Title;
    @FXML private Label trending1Artist, trending2Artist, trending3Artist, trending4Artist, trending5Artist;
    
    @FXML private Label newAlbum1Title, newAlbum2Title, newAlbum3Title, newAlbum4Title, newAlbum5Title;
    @FXML private Label newAlbum1Artist, newAlbum2Artist, newAlbum3Artist, newAlbum4Artist, newAlbum5Artist;
    
    @FXML private Label gotoTitle, gotoArtist;
    
    @FXML private ImageView trending1AlbumArt, trending2AlbumArt, trending3AlbumArt, trending4AlbumArt, trending5AlbumArt;
    @FXML private ImageView newAlbum1Art, newAlbum2Art, newAlbum3Art, newAlbum4Art, newAlbum5Art;
    
    @FXML private Label latest1Title, latest1Artist, latest2Title, latest2Artist, latest3Title, latest3Artist;
    @FXML private Label latest4Title, latest4Artist, latest5Title, latest5Artist, latest6Title, latest6Artist;
    @FXML private Label latest7Title, latest7Artist, latest8Title, latest8Artist, latest9Title, latest9Artist;
    
    @FXML private ImageView latest1AlbumArt, latest2AlbumArt, latest3AlbumArt, latest4AlbumArt, latest5AlbumArt,
            latest6AlbumArt, latest7AlbumArt, latest8AlbumArt, latest9AlbumArt;
            
    @FXML private HBox latestCard1, latestCard2, latestCard3, latestCard4, latestCard5, latestCard6, latestCard7, latestCard8, latestCard9;
    @FXML private ImageView bannerImage;

    private final ObservableList<Song> trendingSongs = FXCollections.observableArrayList();
    private final ObservableList<Album> newAlbums = FXCollections.observableArrayList();
    private final ObservableList<Song> latestSongs = FXCollections.observableArrayList();
    
    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (bannerImage != null && todaysHitBanner != null) {
            bannerImage.fitWidthProperty().bind(todaysHitBanner.widthProperty());
        }
        
        setupClickHandlers();
        fetchTrendingFromAdminPlaylist();
        fetchAlbumsFromFirebase();
        fetchLatestSongsFromFirebase(); 
    }
    
    private void fetchTrendingFromAdminPlaylist() {
        System.out.println("Fetching trending songs...");
        new Thread(() -> {
            try {
                List<Song> songs = MusicService.getInstance().getTrendingSongs();
                Platform.runLater(() -> {
                    trendingSongs.clear();
                    trendingSongs.addAll(songs);
                    populateTrendingData();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchAlbumsFromFirebase() {
        System.out.println("Fetching newest albums...");
        new Thread(() -> {
            List<Album> processedAlbums = MusicService.getInstance().getNewestAlbums();
            Platform.runLater(() -> {
                newAlbums.clear();
                newAlbums.addAll(processedAlbums);
                updateNewAlbumUI();
            });
        }).start();
    }
    
    private void fetchLatestSongsFromFirebase() {
        System.out.println("Fetching latest songs...");
        new Thread(() -> {
            List<Song> processedSongs = MusicService.getInstance().getLatestSongs();
            Platform.runLater(() -> {
                latestSongs.clear();
                latestSongs.addAll(processedSongs);
                updateLatestSongsUI();
            });
        }).start();
    }

    private void populateTrendingData() {
        if (trendingSongs == null || trendingSongs.isEmpty()) return;

        Label[] titles = {trending1Title, trending2Title, trending3Title, trending4Title, trending5Title};
        Label[] artists = {trending1Artist, trending2Artist, trending3Artist, trending4Artist, trending5Artist};
        ImageView[] arts = {trending1AlbumArt, trending2AlbumArt, trending3AlbumArt, trending4AlbumArt, trending5AlbumArt};

        for (int i = 0; i < Math.min(trendingSongs.size(), titles.length); i++) {
            Song song = trendingSongs.get(i);
            if (titles[i] != null) titles[i].setText(song.getTitle());
            if (artists[i] != null) artists[i].setText(song.getArtist());
            if (arts[i] != null && song.getImageURL() != null) {
                try { arts[i].setImage(new Image(song.getImageURL(), true)); } catch (Exception e) {}
            }
        }
        
        if (trendingSongs.size() > 5 && gotoTitle != null) {
            gotoTitle.setText(trendingSongs.get(5).getTitle());
            if (gotoArtist != null) gotoArtist.setText(trendingSongs.get(5).getArtist());
        }
    }

    private void updateNewAlbumUI() {
        Label[] titles = {newAlbum1Title, newAlbum2Title, newAlbum3Title, newAlbum4Title, newAlbum5Title};
        Label[] artists = {newAlbum1Artist, newAlbum2Artist, newAlbum3Artist, newAlbum4Artist, newAlbum5Artist};
        ImageView[] arts = {newAlbum1Art, newAlbum2Art, newAlbum3Art, newAlbum4Art, newAlbum5Art};

        for (int i = 0; i < Math.min(newAlbums.size(), titles.length); i++) {
            Album album = newAlbums.get(i);
            if (titles[i] != null) titles[i].setText(album.getTitle());
            if (artists[i] != null) artists[i].setText(album.getArtist());
            if (arts[i] != null && album.getImageURL() != null) {
                try { arts[i].setImage(new Image(album.getImageURL(), true)); } catch (Exception e) {}
            }
        }
    }

    private void updateLatestSongsUI() {
        Label[] titles = {latest1Title, latest2Title, latest3Title, latest4Title, latest5Title, latest6Title, latest7Title, latest8Title, latest9Title};
        Label[] artists = {latest1Artist, latest2Artist, latest3Artist, latest4Artist, latest5Artist, latest6Artist, latest7Artist, latest8Artist, latest9Artist};
        ImageView[] arts = {latest1AlbumArt, latest2AlbumArt, latest3AlbumArt, latest4AlbumArt, latest5AlbumArt, latest6AlbumArt, latest7AlbumArt, latest8AlbumArt, latest9AlbumArt};

        for (int i = 0; i < Math.min(latestSongs.size(), titles.length); i++) {
            Song song = latestSongs.get(i);
            if (titles[i] != null) titles[i].setText(song.getTitle());
            if (artists[i] != null) artists[i].setText(song.getArtist());
            if (arts[i] != null && song.getImageURL() != null) {
                try { arts[i].setImage(new Image(song.getImageURL(), true)); } catch (Exception e) {}
            }
        }
    }

    private void setupClickHandlers() {
        StackPane[] trendingCards = {trendingCard1, trendingCard2, trendingCard3, trendingCard4, trendingCard5, trendingCard6};
        for (int i = 0; i < trendingCards.length; i++) {
            final int index = i;
            if (trendingCards[i] != null) {
                trendingCards[i].setOnMouseClicked(e -> {
                    if (mainController != null && trendingSongs.size() > index) {
                        mainController.showPlayerBar(trendingSongs.get(index), null, 0);
                    }
                });
            }
        }

        StackPane[] albumCards = {newAlbumCard1, newAlbumCard2, newAlbumCard3, newAlbumCard4, newAlbumCard5};
        for (int i = 0; i < albumCards.length; i++) {
            final int index = i;
            if (albumCards[i] != null) {
                albumCards[i].setOnMouseClicked(e -> {
                    if (newAlbums.size() > index) {
                        fetchSongsAndOpenAlbum(newAlbums.get(index));
                    }
                });
            }
        }

        HBox[] latestCards = {latestCard1, latestCard2, latestCard3, latestCard4, latestCard5, latestCard6, latestCard7, latestCard8, latestCard9};
        for (int i = 0; i < latestCards.length; i++) {
            final int index = i;
            if (latestCards[i] != null) {
                latestCards[i].setOnMouseClicked(e -> {
                    if (mainController != null && latestSongs.size() > index) {
                        mainController.showPlayerBar(latestSongs.get(index), null, 0);
                    }
                });
            }
        }

        if (todaysHitBanner != null) todaysHitBanner.setOnMouseClicked(e -> openDetailedSongList("Today's Hits"));
        if (latest1Title != null) latest1Title.setOnMouseClicked(e -> openCompactList());
    }

    private void fetchSongsAndOpenAlbum(Album album) {
        System.out.println("Downloading songs for album: " + album.getTitle());
        new Thread(() -> {
            List<Song> realSongs = DatabaseManager.getInstance().getService().fetchSongsByIds(album.getSongIdList());
            Platform.runLater(() -> {
                if (mainController != null) {
                    mainController.loadSongDetail(album.getTitle(), album.getArtist(), album.getGenre(),
                            album.getReleaseYear(), album.getImageURL(), realSongs);
                }
            });
        }).start();
    }

    private void openDetailedSongList(String listTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            ctrl.setMainController(this.mainController);

            String finalId = "SYSTEM_" + listTitle.toUpperCase().replace(" ", "_");
            String coverPath = "/images/playlist_cover.png"; 

            if (listTitle.equals("Today's Hits")) {
                coverPath = "/images/todayhit.jpg";
            } else if (listTitle.equals("All Songs")) {
                coverPath = "/images/allsong.jpg";
            }

            ctrl.setData(finalId, listTitle, "Curated for you", "System Playlist", coverPath, 2026, "Various", new ArrayList<>());

            if (mainController != null) {
                mainController.navigateToView(view);
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
                mainController.navigateToView(view);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}