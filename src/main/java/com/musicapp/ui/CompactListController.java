package com.musicapp.ui;

import com.musicapp.Main;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ResourceBundle;

public class CompactListController implements Initializable {

    @FXML private Label titleLabel;
    private static MediaPlayer currentPlayer;
    
    // ĐỔI SANG DÙNG MODEL CHUẨN 8 THUỘC TÍNH
    @FXML private ListView<SongListController.SongItem> songListView;

    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Latest Song");
        
        // Lấy data "sống" từ MusicService thay vì list hardcode
        songListView.setItems(MusicService.getGlobalLibrary());
        songListView.setCellFactory(lv -> new CompactSongCell());
    }

    // ==========================================
    // PUBLIC API (Data Injection)
    // ==========================================
    public void setData(String title, ObservableList<SongListController.SongItem> songs) {
        titleLabel.setText(title);
        if (songs != null) {
            songListView.setItems(songs);
        }
    }

    // ==========================================
    // CUSTOM LIST CELL RENDERER
    // ==========================================
    private class CompactSongCell extends ListCell<SongListController.SongItem> {

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

            thumb.setFitWidth(40);
            thumb.setFitHeight(40);
            thumb.setPreserveRatio(false);

            nameLabel.setPrefWidth(300);
            nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C1810; -fx-font-weight: bold;");

            heartBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #C0C0C0; -fx-cursor: hand; -fx-padding: 0 8 0 8;");
            heartBtn.setPrefWidth(30);

            HBox.setHgrow(spacer, Priority.ALWAYS);

            artistLabel.setPrefWidth(200);
            artistLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            albumLabel.setPrefWidth(220);
            albumLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A6A60;");

            addBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-text-fill: #C0703A; -fx-cursor: hand; -fx-padding: 0 4 0 4;");

            timeLabel.setPrefWidth(45);
            timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9E8E84;");

            root.getChildren().addAll(thumb, nameLabel, heartBtn, spacer, artistLabel, albumLabel, addBtn, timeLabel);

            if (Main.isAdmin) {
                heartBtn.setVisible(false);
                heartBtn.setManaged(false);
            }

            // Double Click to Play
            root.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    SongListController.SongItem item = getItem();
                    if (item != null) {
                        System.out.println("▶ Đang nạp nhạc: " + item.title);
                        
                        try {
                            // Tắt bài cũ nếu đang phát
                            if (currentPlayer != null) {
                                currentPlayer.stop();
                            }

                            // Chạy bài mới
                            String path = item.audioURL;
                            // Nếu là URL web (bắt đầu bằng http)
                            if (path.startsWith("http")) {
                                 Media hit = new Media(path);
                                 currentPlayer = new MediaPlayer(hit);
                                 currentPlayer.play();
                            } 
                            // Nếu là file Local (như /audio/BlankSpace.mp3)
                            else {
                                 URL resourceUrl = getClass().getResource(path);
                                 if (resourceUrl != null) {
                                     Media hit = new Media(resourceUrl.toString());
                                     currentPlayer = new MediaPlayer(hit);
                                     currentPlayer.play();
                                     System.out.println("🎵 Nhạc đang phát Ting Ting!");
                                 } else {
                                     System.err.println("Lỗi: Không tìm thấy file nhạc tại " + path);
                                 }
                            }

                            // TODO Mở rộng: Gọi qua MainViewController để hiện cái thanh Player ở dưới cùng
                            // Main.mainViewController.showPlayerBar(item.title, item.artist);

                        } catch (Exception ex) {
                            System.err.println("Lỗi phát nhạc!");
                            ex.printStackTrace();
                        }
                    }
                }
            });

            heartBtn.setOnAction(e -> {
                SongListController.SongItem item = getItem();
                if (item == null) return;
                item.isFavorite = !item.isFavorite;
                updateHeartUI(item.isFavorite);
            });

            addBtn.setOnAction(e -> {
                SongListController.SongItem item = getItem();
                if (item == null) return;
                showAddMenu(item, addBtn);
            });
        }

        private void updateHeartUI(boolean isFav) {
            if (isFav) {
                heartBtn.setText("♥");
                heartBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #C0703A; -fx-cursor: hand; -fx-padding: 0 8 0 8;");
            } else {
                heartBtn.setText("♡");
                heartBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #C0C0C0; -fx-cursor: hand; -fx-padding: 0 8 0 8;");
            }
        }

        // ==========================================
        // CONTEXT MENU LOGIC (TÍCH HỢP DROPBOX)
        // ==========================================
        private void showAddMenu(SongListController.SongItem item, Button anchor) {
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-border-color: #E0D8D0;");
            String menuItemStyle = "-fx-font-size: 13px; -fx-text-fill: #2C1810;";

            if (Main.isAdmin) {
                MenuItem deleteFromLibrary = new MenuItem("Delete from System");
                deleteFromLibrary.setStyle("-fx-text-fill: #CC3300; -fx-font-weight: bold;");
                deleteFromLibrary.setOnAction(e -> {
                    MusicService.removeSong(item);
                    System.out.println("🗑 Deleted: " + item.title);
                });
                menu.getItems().add(deleteFromLibrary);
            } else {
                MenuItem addToFavorite = new MenuItem("Add to your favorite songs");
                
                // DROPBOX TẠI ĐÂY
                Menu playlistSubMenu = new Menu("Add to Playlist..."); 
                String[] userPlaylists = {"My Chill Mix", "Workout 2026", "Roadtrip"};
                for (String plName : userPlaylists) {
                    MenuItem plItem = new MenuItem(plName);
                    plItem.setStyle("-fx-font-size: 12px;");
                    plItem.setOnAction(e -> System.out.println("[User] Added to: " + plName));
                    playlistSubMenu.getItems().add(plItem);
                }

                MenuItem removeFromPlaylist = new MenuItem("Remove from PlayList");
                MenuItem playNext = new MenuItem("Play next");

                addToFavorite.setStyle(menuItemStyle);
                playlistSubMenu.setStyle(menuItemStyle);
                removeFromPlaylist.setStyle(menuItemStyle);
                playNext.setStyle(menuItemStyle);

                addToFavorite.setOnAction(e -> {
                    item.isFavorite = true;
                    updateHeartUI(true);
                });

                menu.getItems().addAll(addToFavorite, playlistSubMenu, new SeparatorMenuItem(), removeFromPlaylist, playNext);
            }

            menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
        }

        @Override
        protected void updateItem(SongListController.SongItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            // Map đúng 8 thuộc tính
            nameLabel.setText(item.title);
            artistLabel.setText(item.artist);
            albumLabel.setText(item.genre); // Dùng genre thay cho album tạm
            timeLabel.setText(item.getDurationString());
            updateHeartUI(item.isFavorite);

            if (item.imageURL != null && !item.imageURL.isEmpty()) {
                try {
                    thumb.setImage(new Image(getClass().getResourceAsStream(item.imageURL), 40, 40, false, true));
                } catch (Exception ex) {
                    thumb.setImage(null);
                }
            } else {
                thumb.setImage(null);
            }

            root.setOnMouseEntered(e -> root.setStyle("-fx-background-color: #F0EAE4; -fx-background-radius: 6;"));
            root.setOnMouseExited(e -> root.setStyle("-fx-background-color: transparent;"));

            setGraphic(root);
            setStyle("-fx-background-color: transparent; -fx-padding: 2 0 2 0;");
        }
    }
}