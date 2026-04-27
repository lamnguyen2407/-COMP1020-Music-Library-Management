package com.musicapp.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SongListController implements Initializable {

    // ═══════════════════════════════════════════
    // FXML FIELDS — khớp với fx:id trong FXML
    // ═══════════════════════════════════════════
    @FXML private ImageView coverImageView;
    @FXML private Label     titleLabel;
    @FXML private Label     subtitleLabel;
    @FXML private Label     descriptionLabel;
    @FXML private Button    playButton;
    @FXML private Button    shuffleButton;
    @FXML private Label     col1Header;
    @FXML private Label     col2Header;
    @FXML private Label     col3Header;
    @FXML private ListView<SongItem> songListView;

    // ═══════════════════════════════════════════
    // MODEL — dữ liệu 1 bài hát/nghệ sĩ
    // ═══════════════════════════════════════════
    public static class SongItem {
        public String col1;   // Tên bài / Tên nghệ sĩ
        public String col2;   // Artist / Số follower
        public String col3;   // Album / Top song
        public String time;
        public String coverPath;
        public boolean isFavorite;

        public SongItem(String col1, String col2, String col3,
                        String time, String coverPath) {
            this.col1      = col1;
            this.col2      = col2;
            this.col3      = col3;
            this.time      = time;
            this.coverPath = coverPath;
            this.isFavorite = false;
        }
    }

    // ═══════════════════════════════════════════
    // DATA MẪU — backend thay bằng DB thật
    // ═══════════════════════════════════════════
    private final ObservableList<SongItem> sampleSongs =
            FXCollections.observableArrayList(
        new SongItem("Going Bad (feat. Drake)",                 "Meek Mill",    "Championships",                  "3:01", null),
        new SongItem("HIGHEST IN THE ROOM",                    "Travis Scott", "HIGHEST IN THE ROOM – Single",   "2:56", null),
        new SongItem("Praise The Lord (Da Shine) [feat. Skepta]","A$AP Rocky", "TESTING",                        "3:26", null),
        new SongItem("Taste (feat. Offset)",                   "Tyga",         "Taste (feat. Offset) – Single",  "3:53", null),
        new SongItem("Wow.",                                   "Post Malone",  "Wow. – Single",                  "2:30", null),
        new SongItem("679 (feat. Morty)",                      "Fetty Wap",    "Fetty Wap (Deluxe Edition)",     "3:07", null),
        new SongItem("Funky Friday",                           "Dave & Fredo", "Funky Friday – Single",          "3:03", null),
        new SongItem("Sicko Mode",                             "Travis Scott", "ASTROWORLD",                     "5:12", null),
        new SongItem("Rockstar",                               "Post Malone",  "beerbongs & bentleys",           "3:38", null),
        new SongItem("God's Plan",                             "Drake",        "Scorpion",                       "3:18", null)
    );

    // ═══════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
   

        // Load data mẫu
        songListView.setItems(sampleSongs);

        // Gắn cell factory — mỗi row là 1 SongCell
        songListView.setCellFactory(lv -> new SongCell());

        // Nút Play
        playButton.setOnAction(e -> onPlayClicked());

        // Nút Shuffle
        shuffleButton.setOnAction(e -> onShuffleClicked());
    }

    // ═══════════════════════════════════════════
    // PUBLIC — các trang khác gọi hàm này để
    // truyền data riêng vào (title, ảnh, songs)
    // ═══════════════════════════════════════════
    public void setData(String title, String subtitle, String description,
                        String coverImagePath, ObservableList<SongItem> songs) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
        descriptionLabel.setText(description);

        if (coverImagePath != null && !coverImagePath.isEmpty()) {
            try {
                coverImageView.setImage(new Image(
                        getClass().getResourceAsStream(coverImagePath),
                        230, 230, false, true));
            } catch (Exception ignored) {}
        }

        if (songs != null) {
            songListView.setItems(songs);
        }
    }

    // Đổi header cột tuỳ trang
    // Trending/Album/Playlist: ("SONG","ARTIST","ALBUM")
    // Top Artist:              ("ARTIST","NUMBER OF FOLLOWER","TOP SONG")
    public void setColumnHeaders(String col1, String col2, String col3) {
        col1Header.setText(col1);
        col2Header.setText(col2);
        col3Header.setText(col3);
    }

    // ═══════════════════════════════════════════
    // PLAY / SHUFFLE
    // ═══════════════════════════════════════════
    private void onPlayClicked() {
        // TODO: backend kết nối PlaybackService để phát nhạc
        System.out.println("▶ PLAY clicked");
    }

    private void onShuffleClicked() {
        // TODO: backend kết nối PlaybackService để phát ngẫu nhiên
        FXCollections.shuffle(sampleSongs);
        System.out.println("🔀 SHUFFLE clicked");
    }

    // ═══════════════════════════════════════════
    // SONG CELL — mỗi dòng trong ListView
    // ═══════════════════════════════════════════
    private class SongCell extends ListCell<SongItem> {

        private final HBox  root        = new HBox(12);
        private final Label indexLabel  = new Label();
        private final ImageView thumb   = new ImageView();
        private final Label nameLabel   = new Label();
        private final Button heartBtn   = new Button("♡");
        private final Region spacer1    = new Region();
        private final Label col2Label   = new Label();
        private final Label col3Label   = new Label();
        private final Button addBtn     = new Button("+");
        private final Label timeLabel   = new Label();

        SongCell() {
            // Layout
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(0, 40, 0, 40));
            root.setStyle("-fx-background-color: transparent;");

            // Index
            indexLabel.setPrefWidth(36);
            indexLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9E8E84;");

            // Thumbnail
            thumb.setFitWidth(40);
            thumb.setFitHeight(40);
            thumb.setPreserveRatio(false);

            // Tên bài hát
            nameLabel.setPrefWidth(240);
            nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C1810; -fx-font-weight: bold;");

            // Nút trái tim
            heartBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #C0C0C0;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 8 0 8;"
            );
            heartBtn.setPrefWidth(30);

            // Spacer giữa tên và col2
            HBox.setHgrow(spacer1, Priority.ALWAYS);

            // Col2 (Artist / Follower)
            col2Label.setPrefWidth(180);
            col2Label.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            // Col3 (Album / Top Song)
            col3Label.setPrefWidth(200);
            col3Label.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            // Nút dấu +
            addBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #C0703A;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 4 0 4;"
            );

            // Time
            timeLabel.setPrefWidth(45);
            timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9E8E84;");

            root.getChildren().addAll(
                indexLabel, thumb, nameLabel, heartBtn,
                spacer1, col2Label, col3Label, addBtn, timeLabel
            );

            // Sự kiện nút trái tim
            heartBtn.setOnAction(e -> {
                SongItem item = getItem();
                if (item == null) return;
                item.isFavorite = !item.isFavorite;
                if (item.isFavorite) {
                    // Đổi sang trái tim đầy — màu đỏ/cam
                    heartBtn.setText("♥");
                    heartBtn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #C0703A;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 8 0 8;"
                    );
                    // TODO: backend gọi FavoriteService.add(item)
                    System.out.println("❤ Added to Favorites: " + item.col1);
                } else {
                    // Đổi lại trái tim rỗng
                    heartBtn.setText("♡");
                    heartBtn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #C0C0C0;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 8 0 8;"
                    );
                    // TODO: backend gọi FavoriteService.remove(item)
                    System.out.println("🤍 Removed from Favorites: " + item.col1);
                }
            });

            // Sự kiện nút dấu + → hiện ContextMenu
            addBtn.setOnAction(e -> {
                SongItem item = getItem();
                if (item == null) return;
                showAddMenu(item, addBtn);
            });
        }

        // Hiện popup menu khi bấm dấu +
        private void showAddMenu(SongItem item, Button anchor) {
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-border-color: #E0D8D0;");

            MenuItem deleteFromLibrary    = new MenuItem("Delete from Library");
            MenuItem addToFavorite        = new MenuItem("Add to your favorite songs");
            MenuItem addToPlaylist        = new MenuItem("Add to Playlist....");
            MenuItem removeFromPlaylist   = new MenuItem("Remove from PlayList");
            MenuItem playNext             = new MenuItem("Play next");

            // Style từng item
            String menuItemStyle = "-fx-font-size: 13px; -fx-text-fill: #2C1810;";
            deleteFromLibrary.setStyle(menuItemStyle);
            addToFavorite.setStyle(menuItemStyle);
            addToPlaylist.setStyle(menuItemStyle);
            removeFromPlaylist.setStyle(menuItemStyle);
            playNext.setStyle(menuItemStyle);

            // Xử lý từng option
            deleteFromLibrary.setOnAction(e -> {
                // TODO: backend gọi LibraryService.delete(item)
                System.out.println("🗑 Delete from Library: " + item.col1);
            });

            addToFavorite.setOnAction(e -> {
                item.isFavorite = true;
                heartBtn.setText("♥");
                heartBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #C0703A;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0 8 0 8;"
                );
                // TODO: backend gọi FavoriteService.add(item)
                System.out.println("❤ Add to Favorite: " + item.col1);
            });

            addToPlaylist.setOnAction(e -> {
                // TODO: mở dialog chọn playlist
                System.out.println("➕ Add to Playlist: " + item.col1);
            });

            removeFromPlaylist.setOnAction(e -> {
                // TODO: backend gọi PlaylistService.remove(item)
                System.out.println("➖ Remove from Playlist: " + item.col1);
            });

            playNext.setOnAction(e -> {
                // TODO: backend gọi PlaybackService.playNext(item)
                System.out.println("⏭ Play Next: " + item.col1);
            });

            menu.getItems().addAll(
                deleteFromLibrary,
                addToFavorite,
                addToPlaylist,
                removeFromPlaylist,
                playNext
            );

            menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
        }

        // ═══════════════════════════════════════
        // Update cell mỗi khi data thay đổi
        // ═══════════════════════════════════════
        @Override
        protected void updateItem(SongItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            // Index (số thứ tự, bắt đầu từ 1)
            indexLabel.setText(String.valueOf(getIndex() + 1));

            // Thumbnail
            if (item.coverPath != null && !item.coverPath.isEmpty()) {
                try {
                    thumb.setImage(new Image(
                            getClass().getResourceAsStream(item.coverPath),
                            40, 40, false, true));
                } catch (Exception ex) {
                    thumb.setImage(null);
                }
            } else {
                thumb.setImage(null);
            }

            // Text
            nameLabel.setText(item.col1);
            col2Label.setText(item.col2);
            col3Label.setText(item.col3);
            timeLabel.setText(item.time);

            // Trái tim đúng trạng thái
            if (item.isFavorite) {
                heartBtn.setText("♥");
                heartBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #C0703A;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0 8 0 8;"
                );
            } else {
                heartBtn.setText("♡");
                heartBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #C0C0C0;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0 8 0 8;"
                );
            }

            // Hover effect — highlight dòng
            root.setOnMouseEntered(e ->
                root.setStyle("-fx-background-color: #F0EAE4; -fx-background-radius: 6;"));
            root.setOnMouseExited(e ->
                root.setStyle("-fx-background-color: transparent;"));

            setGraphic(root);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");
        }
    }
}
