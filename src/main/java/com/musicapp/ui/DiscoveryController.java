package com.musicapp.ui;
import com.musicapp.ui.SongListController;
import com.musicapp.ui.SongListController.SongItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscoveryController implements Initializable {

    // ═══════════════════════════════════════════
    // FXML FIELDS — Trending Song cards
    // ═══════════════════════════════════════════
    @FXML private StackPane trendingCard1;
    @FXML private StackPane trendingCard2;
    @FXML private StackPane trendingCard3;
    @FXML private StackPane trendingCard4;

    @FXML private ImageView trending1AlbumArt;
    @FXML private ImageView trending2AlbumArt;
    @FXML private ImageView trending3AlbumArt;
    @FXML private ImageView trending4AlbumArt;

    @FXML private Label trending1Title;
    @FXML private Label trending1Artist;
    @FXML private Label trending2Title;
    @FXML private Label trending2Artist;
    @FXML private Label trending3Title;
    @FXML private Label trending3Artist;
    @FXML private Label trending4Title;
    @FXML private Label trending4Artist;

    // ═══════════════════════════════════════════
    // FXML FIELDS — Top Artist rows
    // ═══════════════════════════════════════════
    @FXML private ImageView artist1Avatar;
    @FXML private ImageView artist2Avatar;
    @FXML private ImageView artist3Avatar;
    @FXML private ImageView artist4Avatar;
    @FXML private ImageView artist5Avatar;

    @FXML private Label artist1Name;
    @FXML private Label artist1Plays;
    @FXML private Label artist2Name;
    @FXML private Label artist2Plays;
    @FXML private Label artist3Name;
    @FXML private Label artist3Plays;
    @FXML private Label artist4Name;
    @FXML private Label artist4Plays;
    @FXML private Label artist5Name;
    @FXML private Label artist5Plays;

    // ═══════════════════════════════════════════
    // FXML FIELDS — Recent Played rows
    // ═══════════════════════════════════════════
    @FXML private ImageView recent1AlbumArt;
    @FXML private ImageView recent2AlbumArt;
    @FXML private ImageView recent3AlbumArt;
    @FXML private ImageView recent4AlbumArt;
    @FXML private ImageView recent5AlbumArt;

    @FXML private Label recent1Title;
    @FXML private Label recent1Artist;
    @FXML private Label recent2Title;
    @FXML private Label recent2Artist;
    @FXML private Label recent3Title;
    @FXML private Label recent3Artist;
    @FXML private Label recent4Title;
    @FXML private Label recent4Artist;
    @FXML private Label recent5Title;
    @FXML private Label recent5Artist;

    @FXML private Label recent1Like;
    @FXML private Label recent2Like;
    @FXML private Label recent3Like;
    @FXML private Label recent4Like;
    @FXML private Label recent5Like;

    // ═══════════════════════════════════════════
    // FXML FIELDS — Go-To Song player
    // ═══════════════════════════════════════════
    @FXML private ImageView gotoAlbumArt;
    @FXML private Label     gotoTitle;
    @FXML private Label     gotoArtist;
    @FXML private Label     prevBtn;
    @FXML private Label     nextBtn;

    // ═══════════════════════════════════════════
    // FXML FIELDS — New Album Release cards
    // ═══════════════════════════════════════════
    @FXML private ImageView newAlbum1AlbumArt;
    @FXML private ImageView newAlbum2AlbumArt;
    @FXML private ImageView newAlbum3AlbumArt;
    @FXML private ImageView newAlbum4AlbumArt;
    @FXML private ImageView newAlbum5AlbumArt;
    @FXML private ImageView newAlbum6AlbumArt;
    @FXML private ImageView newAlbum7AlbumArt;
    @FXML private ImageView newAlbum8AlbumArt;

    @FXML private Label newAlbum1Title;  @FXML private Label newAlbum1Artist;
    @FXML private Label newAlbum2Title;  @FXML private Label newAlbum2Artist;
    @FXML private Label newAlbum3Title;  @FXML private Label newAlbum3Artist;
    @FXML private Label newAlbum4Title;  @FXML private Label newAlbum4Artist;
    @FXML private Label newAlbum5Title;  @FXML private Label newAlbum5Artist;
    @FXML private Label newAlbum6Title;  @FXML private Label newAlbum6Artist;
    @FXML private Label newAlbum7Title;  @FXML private Label newAlbum7Artist;
    @FXML private Label newAlbum8Title;  @FXML private Label newAlbum8Artist;

    // ═══════════════════════════════════════════
    // FXML FIELDS — Latest Song grid
    // ═══════════════════════════════════════════
    @FXML private ImageView latest1AlbumArt;  @FXML private Label latest1Title;  @FXML private Label latest1Artist;
    @FXML private ImageView latest2AlbumArt;  @FXML private Label latest2Title;  @FXML private Label latest2Artist;
    @FXML private ImageView latest3AlbumArt;  @FXML private Label latest3Title;  @FXML private Label latest3Artist;
    @FXML private ImageView latest4AlbumArt;  @FXML private Label latest4Title;  @FXML private Label latest4Artist;
    @FXML private ImageView latest5AlbumArt;  @FXML private Label latest5Title;  @FXML private Label latest5Artist;
    @FXML private ImageView latest6AlbumArt;  @FXML private Label latest6Title;  @FXML private Label latest6Artist;
    @FXML private ImageView latest7AlbumArt;  @FXML private Label latest7Title;  @FXML private Label latest7Artist;
    @FXML private ImageView latest8AlbumArt;  @FXML private Label latest8Title;  @FXML private Label latest8Artist;
    @FXML private ImageView latest9AlbumArt;  @FXML private Label latest9Title;  @FXML private Label latest9Artist;
    @FXML private ImageView latest10AlbumArt; @FXML private Label latest10Title; @FXML private Label latest10Artist;
    @FXML private ImageView latest11AlbumArt; @FXML private Label latest11Title; @FXML private Label latest11Artist;
    @FXML private ImageView latest12AlbumArt; @FXML private Label latest12Title; @FXML private Label latest12Artist;

    // ═══════════════════════════════════════════
    // SAMPLE DATA — backend thay bằng DB thật
    // ═══════════════════════════════════════════

    /** Dữ liệu dùng chung cho Trending Song list */
    private static final ObservableList<SongItem> TRENDING_SONGS =
        FXCollections.observableArrayList(
            new SongItem("Going Bad (feat. Drake)",                      "Meek Mill",    "Championships",                "3:01", null),
            new SongItem("HIGHEST IN THE ROOM",                         "Travis Scott", "HIGHEST IN THE ROOM – Single", "2:56", null),
            new SongItem("Praise The Lord (Da Shine) [feat. Skepta]",   "A$AP Rocky",  "TESTING",                      "3:26", null),
            new SongItem("Taste (feat. Offset)",                        "Tyga",         "Taste (feat. Offset) – Single","3:53", null),
            new SongItem("Wow.",                                         "Post Malone", "Wow. – Single",                "2:30", null),
            new SongItem("679 (feat. Morty)",                           "Fetty Wap",   "Fetty Wap (Deluxe Edition)",   "3:07", null),
            new SongItem("Funky Friday",                                "Dave & Fredo", "Funky Friday – Single",        "3:03", null),
            new SongItem("Sicko Mode",                                  "Travis Scott", "ASTROWORLD",                   "5:12", null),
            new SongItem("Rockstar",                                    "Post Malone",  "beerbongs & bentleys",         "3:38", null),
            new SongItem("God's Plan",                                  "Drake",        "Scorpion",                     "3:18", null)
        );

    /** Dữ liệu Top Artist — col1=Tên, col2=Số follower, col3=Top song */
    private static final ObservableList<SongItem> TOP_ARTISTS =
        FXCollections.observableArrayList(
            new SongItem("KENDRICK LAMAR", "2.6M", "NOT LIKE US",              "3:01", null),
            new SongItem("BILLIE EILISH",  "2.0M", "HIGHEST IN THE ROOM",     "2:56", null),
            new SongItem("TAYLOR SWIFT",   "1.7M", "Anti-Hero",               "3:26", null),
            new SongItem("BEYONCE",        "1.5M", "TEXAS HOLD 'EM",          "3:53", null),
            new SongItem("The Weeknd",     "1.5M", "Blinding Lights",         "2:30", null),
            new SongItem("Drake",          "1.4M", "God's Plan",              "3:18", null),
            new SongItem("Bad Bunny",      "1.3M", "Titi Me Pregunto",        "3:45", null),
            new SongItem("Post Malone",    "1.2M", "Wow.",                    "2:30", null),
            new SongItem("Travis Scott",   "1.1M", "Sicko Mode",              "5:12", null),
            new SongItem("SZA",            "1.0M", "Kill Bill",               "2:33", null)
        );

    /** Dữ liệu New Album Release — dùng chung cấu trúc song/artist/album */
    private static final ObservableList<SongItem> ALBUM_SONGS =
        FXCollections.observableArrayList(
            new SongItem("Going Bad (feat. Drake)",                    "Meek Mill",   "Championships",                "3:01", null),
            new SongItem("HIGHEST IN THE ROOM",                       "Travis Scott","HIGHEST IN THE ROOM – Single", "2:56", null),
            new SongItem("Praise The Lord (Da Shine) [feat. Skepta]", "A$AP Rocky", "TESTING",                      "3:26", null),
            new SongItem("Taste (feat. Offset)",                      "Tyga",        "Taste (feat. Offset) – Single","3:53", null),
            new SongItem("Wow.",                                       "Post Malone","Wow. – Single",                "2:30", null),
            new SongItem("679 (feat. Morty)",                         "Fetty Wap",  "Fetty Wap (Deluxe Edition)",   "3:07", null),
            new SongItem("Funky Friday",                              "Dave & Fredo","Funky Friday – Single",        "3:03", null),
            new SongItem("Sicko Mode",                                "Travis Scott","ASTROWORLD",                   "5:12", null),
            new SongItem("Rockstar",                                  "Post Malone", "beerbongs & bentleys",         "3:38", null),
            new SongItem("God's Plan",                                "Drake",       "Scorpion",                     "3:18", null)
        );

    /** Dữ liệu Latest Songs */
    private static final ObservableList<SongItem> LATEST_SONGS =
        FXCollections.observableArrayList(
            new SongItem("Going Bad (feat. Drake)",                    "Meek Mill",   "Championships",                "3:01", null),
            new SongItem("HIGHEST IN THE ROOM",                       "Travis Scott","HIGHEST IN THE ROOM – Single", "2:56", null),
            new SongItem("Praise The Lord (Da Shine) [feat. Skepta]", "A$AP Rocky", "TESTING",                      "3:26", null),
            new SongItem("Taste (feat. Offset)",                      "Tyga",        "Taste (feat. Offset) – Single","3:53", null),
            new SongItem("Wow.",                                       "Post Malone","Wow. – Single",                "2:30", null),
            new SongItem("679 (feat. Morty)",                         "Fetty Wap",  "Fetty Wap (Deluxe Edition)",   "3:07", null),
            new SongItem("Funky Friday",                              "Dave & Fredo","Funky Friday – Single",        "3:03", null),
            new SongItem("Sicko Mode",                                "Travis Scott","ASTROWORLD",                   "5:12", null),
            new SongItem("Rockstar",                                  "Post Malone", "beerbongs & bentleys",         "3:38", null),
            new SongItem("God's Plan",                                "Drake",       "Scorpion",                     "3:18", null),
            new SongItem("Blinding Lights",                           "The Weeknd",  "After Hours",                  "3:20", null),
            new SongItem("Save Your Tears",                           "The Weeknd",  "After Hours",                  "3:35", null)
        );

    // ═══════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSampleData();
        setupClickHandlers();
    }

    // ═══════════════════════════════════════════
    // POPULATE SAMPLE DATA vào UI cards
    // ═══════════════════════════════════════════
    private void populateSampleData() {

        // ── Trending cards (4 bài đầu) ──
        setCardData(trending1Title, trending1Artist, "Mr.Morale & The Big Steppers", "Kendrick Lamar");
        setCardData(trending2Title, trending2Artist, "Grandfather Ballet",            "Jerome Bell");
        setCardData(trending3Title, trending3Artist, "Boyfriend",                     "Jerome Bell ft. Anlene");
        setCardData(trending4Title, trending4Artist, "Boyfriend",                     "Jerome Bell ft. Anlene");

        // ── Top Artist rows ──
        setCardData(artist1Name, artist1Plays, "Devon Lane",     "122M Plays");
        setCardData(artist2Name, artist2Plays, "Jerome Bell",    "192M Plays");
        setCardData(artist3Name, artist3Plays, "Jane Cooper",    "106M Plays");
        setCardData(artist4Name, artist4Plays, "Floyd Miles",    "200M Plays");
        setCardData(artist5Name, artist5Plays, "Darrell Steward","96M Plays");

        // ── Recent Played rows ──
        setCardData(recent1Title, recent1Artist, "Grandfather Ballet",   "Jerome Bell");
        setCardData(recent2Title, recent2Artist, "Into You",             "Ariana Grande");
        setCardData(recent3Title, recent3Artist, "Love On The Weekend",  "Wade Warren");
        setCardData(recent4Title, recent4Artist, "Souvenir",             "Kathryn Murphy");
        setCardData(recent5Title, recent5Artist, "It Will Be Okay",      "Cameron Williamson");

        // ── Go-To Song player ──
        gotoTitle.setText("It Will Be Okay");
        gotoArtist.setText("Cameron Williamson");

        // ── New Album Release (8 cards) ──
        setCardData(newAlbum1Title, newAlbum1Artist, "Mr.Morale & The ...", "Cameron Williamson");
        setCardData(newAlbum2Title, newAlbum2Artist, "Grandfather Ballet",  "Jerome Bell");
        setCardData(newAlbum3Title, newAlbum3Artist, "Boyfriend",           "Jerome Bell ft. Anlene");
        setCardData(newAlbum4Title, newAlbum4Artist, "Boyfriend",           "Jerome Bell ft. Anlene");
        setCardData(newAlbum5Title, newAlbum5Artist, "Mr.Morale & The ...", "Cameron Williamson");
        setCardData(newAlbum6Title, newAlbum6Artist, "Grandfather Ballet",  "Jerome Bell");
        setCardData(newAlbum7Title, newAlbum7Artist, "Boyfriend",           "Jerome Bell ft. Anlene");
        setCardData(newAlbum8Title, newAlbum8Artist, "Boyfriend",           "Jerome Bell ft. Anlene");

        // ── Latest Song grid (12 ô) ──
        Label[] latestTitles  = { latest1Title,  latest2Title,  latest3Title,
                                   latest4Title,  latest5Title,  latest6Title,
                                   latest7Title,  latest8Title,  latest9Title,
                                   latest10Title, latest11Title, latest12Title };
        Label[] latestArtists = { latest1Artist,  latest2Artist,  latest3Artist,
                                   latest4Artist,  latest5Artist,  latest6Artist,
                                   latest7Artist,  latest8Artist,  latest9Artist,
                                   latest10Artist, latest11Artist, latest12Artist };

        for (int i = 0; i < LATEST_SONGS.size() && i < latestTitles.length; i++) {
            SongItem s = LATEST_SONGS.get(i);
            latestTitles[i].setText(s.col1);
            latestArtists[i].setText(s.col2);
        }
    }

    /** Helper: set text cho cặp title/subtitle label */
    private void setCardData(Label titleLbl, Label subtitleLbl, String title, String subtitle) {
        if (titleLbl   != null) titleLbl.setText(title);
        if (subtitleLbl != null) subtitleLbl.setText(subtitle);
    }

    // ═══════════════════════════════════════════
    // CLICK HANDLERS
    // ═══════════════════════════════════════════
    private void setupClickHandlers() {

        // ── Trending Song cards → SongListView với header SONG / ARTIST / ALBUM ──
        for (StackPane card : new StackPane[]{ trendingCard1, trendingCard2, trendingCard3, trendingCard4 }) {
            if (card != null) {
                card.setOnMouseClicked(e -> openTrendingList());
            }
        }

        // ── Top Artist rows ──
        // (gắn vào HBox chứa từng dòng — nếu muốn click toàn dòng,
        //  gắn listener ở đây; nếu chỉ click avatar thì đổi sang artist*Avatar)
        bindArtistRowClick();

        // ── New Album Release cards → SongListView với header SONG / ARTIST / ALBUM ──
        bindAlbumCardClicks();

        // ── Latest Song grid cells → CompactListView ──
        bindLatestSongClicks();
    }

    // ── Trending Song ──────────────────────────────────────────────────────────
    private void openTrendingList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SongListView.fxml"));
            Parent root = loader.load();
            SongListController ctrl = loader.getController();

            ctrl.setColumnHeaders("SONG", "ARTIST", "ALBUM");
            ctrl.setData(
                "Top 50: Global hits, updated regularly.",
                "Best Song of the Day",
                "Discover the Top 50 songs trending worldwide. The biggest hits,\nupdated to keep you in sync with global music trends.",
                "/images/top50_cover.jpg",
                FXCollections.observableArrayList(TRENDING_SONGS)
            );

            switchScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ── Top Artist ─────────────────────────────────────────────────────────────
    private void bindArtistRowClick() {
        // Tất cả avatar click đều navigate sang Artist list
        ImageView[] avatars = { artist1Avatar, artist2Avatar, artist3Avatar,
                                artist4Avatar, artist5Avatar };
        for (ImageView av : avatars) {
            if (av != null) av.setOnMouseClicked(e -> openTopArtistList());
        }
    }

    private void openTopArtistList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SongListView.fxml"));
            Parent root = loader.load();
            SongListController ctrl = loader.getController();

            ctrl.setColumnHeaders("ARTIST", "NUMBER OF FOLLOWER", "TOP SONG");
            ctrl.setData(
                "Top Artist of the Month",
                "Viral Artist by Monthly Listener",
                "Discover the top artists trending worldwide. The biggest names shaping\ntoday's music scene.",
                "/images/top_artist_cover.jpg",
                FXCollections.observableArrayList(TOP_ARTISTS)
            );

            switchScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ── New Album Release ──────────────────────────────────────────────────────
    private void bindAlbumCardClicks() {
        // Lấy title/artist từ từng card để hiện đúng tên album
        record AlbumCard(StackPane pane, String albumName, String description) {}

        // Vì FXML không có fx:id cho StackPane của newAlbum,
        // backend cần wrap chúng — tạm thời dùng trendingCard pattern.
        // TODO: thêm fx:id="newAlbumCard1..8" vào FXML nếu muốn click riêng từng card.
        // Hiện tại chỉ minh hoạ với newAlbum1Title onMouseClicked.
        Label[] titleLabels = { newAlbum1Title, newAlbum2Title, newAlbum3Title, newAlbum4Title,
                                newAlbum5Title, newAlbum6Title, newAlbum7Title, newAlbum8Title };
        Label[] artistLabels = { newAlbum1Artist, newAlbum2Artist, newAlbum3Artist, newAlbum4Artist,
                                 newAlbum5Artist, newAlbum6Artist, newAlbum7Artist, newAlbum8Artist };

        for (int i = 0; i < titleLabels.length; i++) {
            final Label tLbl = titleLabels[i];
            final Label aLbl = artistLabels[i];
            if (tLbl != null) {
                tLbl.setOnMouseClicked(e -> openAlbumDetail(tLbl.getText(), aLbl.getText()));
            }
        }
    }

    private void openAlbumDetail(String albumName, String artistName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SongListView.fxml"));
            Parent root = loader.load();
            SongListController ctrl = loader.getController();

            ctrl.setColumnHeaders("SONG", "ARTIST", "ALBUM");
            ctrl.setData(
                albumName,
                "DESCRIPTION",
                "DESCRIPTION",
                null,   // TODO: truyền cover path thật từ DB
                FXCollections.observableArrayList(ALBUM_SONGS)
            );

            switchScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ── Latest Song ────────────────────────────────────────────────────────────
    private void bindLatestSongClicks() {
        Label[] titleLabels = { latest1Title,  latest2Title,  latest3Title,
                                latest4Title,  latest5Title,  latest6Title,
                                latest7Title,  latest8Title,  latest9Title,
                                latest10Title, latest11Title, latest12Title };
        for (Label lbl : titleLabels) {
            if (lbl != null) lbl.setOnMouseClicked(e -> openLatestSongList());
        }

        // Click vào thumbnail ảnh cũng navigate
        ImageView[] arts = { latest1AlbumArt,  latest2AlbumArt,  latest3AlbumArt,
                             latest4AlbumArt,  latest5AlbumArt,  latest6AlbumArt,
                             latest7AlbumArt,  latest8AlbumArt,  latest9AlbumArt,
                             latest10AlbumArt, latest11AlbumArt, latest12AlbumArt };
        for (ImageView iv : arts) {
            if (iv != null) iv.setOnMouseClicked(e -> openLatestSongList());
        }
    }

    private void openLatestSongList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/CompactListView.fxml"));
            Parent root = loader.load();

            // CompactListView chỉ cần tiêu đề + list bài hát
            // Giả sử CompactListController có setData(title, songs)
            try {
                Object ctrl = loader.getController();
                ctrl.getClass()
                    .getMethod("setData", String.class, ObservableList.class)
                    .invoke(ctrl, "Latest Songs",
                            FXCollections.observableArrayList(LATEST_SONGS));
            } catch (Exception ignored) {
                // Nếu CompactListController chưa có setData thì bỏ qua
            }

            switchScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════
    // HELPER — đổi scene trong cùng Stage
    // ═══════════════════════════════════════════
    private void switchScene(Parent newRoot) {
        // Lấy Stage hiện tại từ bất kỳ node nào đã render
        Stage stage = null;
        if (trendingCard1 != null && trendingCard1.getScene() != null) {
            stage = (Stage) trendingCard1.getScene().getWindow();
        } else if (gotoTitle != null && gotoTitle.getScene() != null) {
            stage = (Stage) gotoTitle.getScene().getWindow();
        }

        if (stage != null) {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.setRoot(newRoot);
            } else {
                stage.setScene(new Scene(newRoot));
            }
        }
    }
}