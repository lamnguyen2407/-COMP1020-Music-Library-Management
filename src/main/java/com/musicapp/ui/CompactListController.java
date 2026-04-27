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

public class CompactListController implements Initializable {

    // ═══════════════════════════════════════════
    // FXML FIELDS
    // ═══════════════════════════════════════════
    @FXML private Label titleLabel;
    @FXML private ListView<SongItem> songListView;

    // Content area để navigate tiếp
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ═══════════════════════════════════════════
    // MODEL
    // ═══════════════════════════════════════════
    public static class SongItem {
        public String songName;
        public String artist;
        public String album;
        public String time;
        public String coverPath;
        public boolean isFavorite;

        public SongItem(String songName, String artist, String album,
                        String time, String coverPath) {
            this.songName  = songName;
            this.artist    = artist;
            this.album     = album;
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
        new SongItem("Going Bad (feat. Drake)",                  "Meek Mill",    "Championships",                 "3:01", null),
        new SongItem("HIGHEST IN THE ROOM",                     "Travis Scott", "HIGHEST IN THE ROOM – Single",  "2:56", null),
        new SongItem("Praise The Lord (Da Shine) [feat. Skepta]","A$AP Rocky",  "TESTING",                       "3:26", null),
        new SongItem("Taste (feat. Offset)",                    "Tyga",         "Taste (feat. Offset) – Single", "3:53", null),
        new SongItem("Wow.",                                    "Post Malone",  "Wow. – Single",                 "2:30", null),
        new SongItem("679 (feat. Morty)",                       "Fetty Wap",    "Fetty Wap (Deluxe Edition)",    "3:07", null),
        new SongItem("Funky Friday",                            "Dave & Fredo", "Funky Friday – Single",         "3:03", null),
        new SongItem("Sicko Mode",                              "Travis Scott", "ASTROWORLD",                    "5:12", null),
        new SongItem("Rockstar",                                "Post Malone",  "beerbongs & bentleys",          "3:38", null),
        new SongItem("God's Plan",                              "Drake",        "Scorpion",                      "3:18", null)
    );

    // ═══════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Latest Song");
        songListView.setItems(sampleSongs);
        songListView.setCellFactory(lv -> new CompactSongCell());
    }

    // ═══════════════════════════════════════════
    // PUBLIC — trang khác gọi để truyền data vào
    // ═══════════════════════════════════════════
    public void setData(String title, ObservableList<SongItem> songs) {
        titleLabel.setText(title);
        if (songs != null) {
            songListView.setItems(songs);
        }
    }

    // ═══════════════════════════════════════════
    // COMPACT SONG CELL
    // ═══════════════════════════════════════════
    private class CompactSongCell extends ListCell<SongItem> {

        private final HBox      root        = new HBox(12);
        private final ImageView thumb       = new ImageView();
        private final Label     nameLabel   = new Label();
        private final Button    heartBtn    = new Button("♡");
        private final Region    spacer      = new Region();
        private final Label     artistLabel = new Label();
        private final Label     albumLabel  = new Label();
        private final Button    addBtn      = new Button("+");
        private final Label     timeLabel   = new Label();

        CompactSongCell() {
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(0, 0, 0, 0));
            root.setStyle("-fx-background-color: transparent;");

            // Thumbnail
            thumb.setFitWidth(40);
            thumb.setFitHeight(40);
            thumb.setPreserveRatio(false);

            // Tên bài hát
            nameLabel.setPrefWidth(300);
            nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C1810; -fx-font-weight: bold;");

            // Trái tim
            heartBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #C0C0C0;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 8 0 8;"
            );
            heartBtn.setPrefWidth(30);

            // Spacer
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Artist
            artistLabel.setPrefWidth(200);
            artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            // Album
            albumLabel.setPrefWidth(220);
            albumLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

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
                thumb, nameLabel, heartBtn,
                spacer, artistLabel, albumLabel, addBtn, timeLabel
            );

            // Sự kiện trái tim
            heartBtn.setOnAction(e -> {
                SongItem item = getItem();
                if (item == null) return;
                item.isFavorite = !item.isFavorite;
                if (item.isFavorite) {
                    heartBtn.setText("♥");
                    heartBtn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #C0703A;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 8 0 8;"
                    );
                    // TODO: backend gọi FavoriteService.add(item)
                    System.out.println("❤ Added to Favorites: " + item.songName);
                } else {
                    heartBtn.setText("♡");
                    heartBtn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #C0C0C0;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 8 0 8;"
                    );
                    // TODO: backend gọi FavoriteService.remove(item)
                    System.out.println("🤍 Removed from Favorites: " + item.songName);
                }
            });

            // Sự kiện dấu +
            addBtn.setOnAction(e -> {
                SongItem item = getItem();
                if (item == null) return;
                showAddMenu(item, addBtn);
            });
        }

        // Popup menu khi bấm dấu +
        private void showAddMenu(SongItem item, Button anchor) {
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-border-color: #E0D8D0;");

            MenuItem deleteFromLibrary  = new MenuItem("Delete from Library");
            MenuItem addToFavorite      = new MenuItem("Add to your favorite songs");
            MenuItem addToPlaylist      = new MenuItem("Add to Playlist....");
            MenuItem removeFromPlaylist = new MenuItem("Remove from PlayList");
            MenuItem playNext           = new MenuItem("Play next");

            String menuItemStyle = "-fx-font-size: 13px; -fx-text-fill: #2C1810;";
            deleteFromLibrary.setStyle(menuItemStyle);
            addToFavorite.setStyle(menuItemStyle);
            addToPlaylist.setStyle(menuItemStyle);
            removeFromPlaylist.setStyle(menuItemStyle);
            playNext.setStyle(menuItemStyle);

            deleteFromLibrary.setOnAction(e -> {
                // TODO: backend gọi LibraryService.delete(item)
                System.out.println("🗑 Delete from Library: " + item.songName);
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
                System.out.println("❤ Add to Favorite: " + item.songName);
            });

            addToPlaylist.setOnAction(e -> {
                // TODO: mở dialog chọn playlist
                System.out.println("➕ Add to Playlist: " + item.songName);
            });

            removeFromPlaylist.setOnAction(e -> {
                // TODO: backend gọi PlaylistService.remove(item)
                System.out.println("➖ Remove from Playlist: " + item.songName);
            });

            playNext.setOnAction(e -> {
                // TODO: backend gọi PlaybackService.playNext(item)
                System.out.println("⏭ Play Next: " + item.songName);
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

        @Override
        protected void updateItem(SongItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

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

            nameLabel.setText(item.songName);
            artistLabel.setText(item.artist);
            albumLabel.setText(item.album);
            timeLabel.setText(item.time);

            // Trạng thái trái tim
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

            // Hover effect
            root.setOnMouseEntered(e ->
                root.setStyle("-fx-background-color: #F0EAE4; -fx-background-radius: 6;"));
            root.setOnMouseExited(e ->
                root.setStyle("-fx-background-color: transparent;"));

            setGraphic(root);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");
        }
    }
}