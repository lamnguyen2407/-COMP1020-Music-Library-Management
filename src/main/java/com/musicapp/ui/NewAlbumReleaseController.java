//package com.musicapp.ui;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Node;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.TilePane;
//import javafx.scene.layout.VBox;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ResourceBundle;
//
//public class NewAlbumReleaseController implements Initializable {
//
//    @FXML
//    private TilePane albumTilePane;
//
//    // ---------------------------------------------------------------
//    // Dữ liệu mẫu — controller thật sẽ thay bằng gọi API/database
//    // ---------------------------------------------------------------
//    private static final List<String[]> SAMPLE_ALBUMS = new ArrayList<>();
//
//    static {
//        // Format: { "Album Title", "Artist Name", "coverImagePath" }
//        SAMPLE_ALBUMS.add(new String[]{"POMPEII // UTILITY",           "Earl Sweatshirt, MIKE, SURF GANG", null});
//        SAMPLE_ALBUMS.add(new String[]{"Distracted",                   "Thundercat",                       null});
//        SAMPLE_ALBUMS.add(new String[]{"Ambiguous Desire",             "Arlo Parks",                       null});
//        SAMPLE_ALBUMS.add(new String[]{"LOL : SLUTTY BASS",            "Tory Lanez",                       null});
//        SAMPLE_ALBUMS.add(new String[]{"EMOTIONS",                     "Nine Vicious",                     null});
//        SAMPLE_ALBUMS.add(new String[]{"Broken View",                  "Sam Barber",                       null});
//        SAMPLE_ALBUMS.add(new String[]{"SAME DIFFERENCE",              "Swae Lee",                         null});
//        SAMPLE_ALBUMS.add(new String[]{"Easter Lily EP",               "U2",                               null});
//        SAMPLE_ALBUMS.add(new String[]{"VOLUMES: ONE",                 "Bon Iver",                         null});
//        SAMPLE_ALBUMS.add(new String[]{"Live at Apple Music Radio",    "Black Label Society",              null});
//        SAMPLE_ALBUMS.add(new String[]{"Vol.II",                       "Angine de Poitrine",               null});
//        SAMPLE_ALBUMS.add(new String[]{"AREA 41",                      "41, Kyle Richh, Jenn Carter, TaTa",null});
//        SAMPLE_ALBUMS.add(new String[]{"Unfold",                       "MONSTA X",                         null});
//        SAMPLE_ALBUMS.add(new String[]{"Alexandra Palace",             "Fred again.., Thomas Bangalter",   null});
//        SAMPLE_ALBUMS.add(new String[]{"HALO",                         "Tiffany Day",                      null});
//    }
//
//    // Dùng để lấy content area từ MainController để swap view
//    // Nếu dùng pattern MainController, inject vào đây trước khi load
//    private StackPane contentArea;
//
//    public void setContentArea(StackPane contentArea) {
//        this.contentArea = contentArea;
//    }
//
//    // ---------------------------------------------------------------
//    // Khởi tạo: xóa placeholder trong FXML, load card thật từ data
//    // ---------------------------------------------------------------
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        // Xóa các card placeholder đã có sẵn trong FXML (10 card mẫu)
//        albumTilePane.getChildren().clear();
//
//        // Load card từ dữ liệu mẫu
//        for (String[] albumData : SAMPLE_ALBUMS) {
//            VBox card = createAlbumCard(albumData[0], albumData[1], albumData[2]);
//            albumTilePane.getChildren().add(card);
//        }
//    }
//
//    // ---------------------------------------------------------------
//    // Tạo một album card (ImageView + Title + Artist)
//    // ---------------------------------------------------------------
//    private VBox createAlbumCard(String title, String artist, String imagePath) {
//        VBox card = new VBox(6);
//        card.getStyleClass().add("album-card");
//
//        // --- Ảnh bìa ---
//        if (imagePath != null && !imagePath.isEmpty()) {
//            // Trường hợp có ảnh thật
//            try {
//                Image img = new Image(
//                        getClass().getResourceAsStream(imagePath),
//                        170, 170, false, true
//                );
//                ImageView imageView = new ImageView(img);
//                imageView.setFitWidth(170);
//                imageView.setFitHeight(170);
//                imageView.setPreserveRatio(false);
//                imageView.getStyleClass().add("album-cover-image");
//                card.getChildren().add(imageView);
//            } catch (Exception e) {
//                // Fallback sang placeholder nếu load ảnh lỗi
//                card.getChildren().add(buildPlaceholder());
//            }
//        } else {
//            // Placeholder khi chưa có ảnh
//            card.getChildren().add(buildPlaceholder());
//        }
//
//        // --- Tên album ---
//        Label titleLabel = new Label(title);
//        titleLabel.getStyleClass().add("album-title-label");
//        titleLabel.setMaxWidth(170);
//        titleLabel.setWrapText(false);
//
//        // --- Tên nghệ sĩ ---
//        Label artistLabel = new Label(artist);
//        artistLabel.getStyleClass().add("album-artist-label");
//        artistLabel.setMaxWidth(170);
//        artistLabel.setWrapText(false);
//
//        card.getChildren().addAll(titleLabel, artistLabel);
//
//        // --- Sự kiện click: chuyển sang SongListView ---
//        card.setOnMouseClicked(event -> onAlbumClicked(title, artist, imagePath));
//        card.setStyle("-fx-cursor: hand;");
//
//        return card;
//    }
//
//    // ---------------------------------------------------------------
//    // Placeholder Region khi chưa có ảnh
//    // ---------------------------------------------------------------
//    private Region buildPlaceholder() {
//        Region placeholder = new Region();
//        placeholder.getStyleClass().add("album-cover-placeholder");
//        placeholder.setPrefWidth(170);
//        placeholder.setPrefHeight(170);
//        placeholder.setMinWidth(170);
//        placeholder.setMinHeight(170);
//        return placeholder;
//    }
//
//    // ---------------------------------------------------------------
//    // Xử lý click vào album → load SongListView.fxml
//    // ---------------------------------------------------------------
//    private void onAlbumClicked(String albumTitle, String artistName, String imagePath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/SongListView.fxml")
//            );
//            Node songListView = loader.load();
//
//            // Truyền thông tin album sang SongListController
//            SongListController songListController = loader.getController();
//            songListController.setData(albumTitle, artistName, imagePath);
//
//            // Swap nội dung trong content area của MainView
//            if (contentArea != null) {
//                contentArea.getChildren().setAll(songListView);
//            }
//
////        } catch(IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
package com.musicapp.ui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NewAlbumReleaseController implements Initializable {

    @FXML
    private TilePane albumTilePane;

    // ---------------------------------------------------------------
    // Dữ liệu mẫu — controller thật sẽ thay bằng gọi API/database
    // ---------------------------------------------------------------
    private static final List<String[]> SAMPLE_ALBUMS = new ArrayList<>();

    static {
        // Format: { "Album Title", "Artist Name", "coverImagePath" }
        SAMPLE_ALBUMS.add(new String[]{"POMPEII // UTILITY",           "Earl Sweatshirt, MIKE, SURF GANG", null});
        SAMPLE_ALBUMS.add(new String[]{"Distracted",                   "Thundercat",                       null});
        SAMPLE_ALBUMS.add(new String[]{"Ambiguous Desire",             "Arlo Parks",                       null});
        SAMPLE_ALBUMS.add(new String[]{"LOL : SLUTTY BASS",            "Tory Lanez",                       null});
        SAMPLE_ALBUMS.add(new String[]{"EMOTIONS",                     "Nine Vicious",                     null});
        SAMPLE_ALBUMS.add(new String[]{"Broken View",                  "Sam Barber",                       null});
        SAMPLE_ALBUMS.add(new String[]{"SAME DIFFERENCE",              "Swae Lee",                         null});
        SAMPLE_ALBUMS.add(new String[]{"Easter Lily EP",               "U2",                               null});
        SAMPLE_ALBUMS.add(new String[]{"VOLUMES: ONE",                 "Bon Iver",                         null});
        SAMPLE_ALBUMS.add(new String[]{"Live at Apple Music Radio",    "Black Label Society",              null});
        SAMPLE_ALBUMS.add(new String[]{"Vol.II",                       "Angine de Poitrine",               null});
        SAMPLE_ALBUMS.add(new String[]{"AREA 41",                      "41, Kyle Richh, Jenn Carter, TaTa",null});
        SAMPLE_ALBUMS.add(new String[]{"Unfold",                       "MONSTA X",                         null});
        SAMPLE_ALBUMS.add(new String[]{"Alexandra Palace",             "Fred again.., Thomas Bangalter",   null});
        SAMPLE_ALBUMS.add(new String[]{"HALO",                         "Tiffany Day",                      null});
    }

    // Dùng để lấy content area từ MainController để swap view
    // Nếu dùng pattern MainController, inject vào đây trước khi load
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ---------------------------------------------------------------
    // Khởi tạo: xóa placeholder trong FXML, load card thật từ data
    // ---------------------------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Xóa các card placeholder đã có sẵn trong FXML (10 card mẫu)
        albumTilePane.getChildren().clear();

        // Load card từ dữ liệu mẫu
        for (String[] albumData : SAMPLE_ALBUMS) {
            VBox card = createAlbumCard(albumData[0], albumData[1], albumData[2]);
            albumTilePane.getChildren().add(card);
        }
    }

    // ---------------------------------------------------------------
    // Tạo một album card (ImageView + Title + Artist)
    // ---------------------------------------------------------------
    private VBox createAlbumCard(String title, String artist, String imagePath) {
        VBox card = new VBox(6);
        card.getStyleClass().add("album-card");

        // --- Ảnh bìa ---
        if (imagePath != null && !imagePath.isEmpty()) {
            // Trường hợp có ảnh thật
            try {
                Image img = new Image(
                        getClass().getResourceAsStream(imagePath),
                        170, 170, false, true
                );
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(170);
                imageView.setFitHeight(170);
                imageView.setPreserveRatio(false);
                imageView.getStyleClass().add("album-cover-image");
                card.getChildren().add(imageView);
            } catch (Exception e) {
                // Fallback sang placeholder nếu load ảnh lỗi
                card.getChildren().add(buildPlaceholder());
            }
        } else {
            // Placeholder khi chưa có ảnh
            card.getChildren().add(buildPlaceholder());
        }

        // --- Tên album ---
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("album-title-label");
        titleLabel.setMaxWidth(170);
        titleLabel.setWrapText(false);

        // --- Tên nghệ sĩ ---
        Label artistLabel = new Label(artist);
        artistLabel.getStyleClass().add("album-artist-label");
        artistLabel.setMaxWidth(170);
        artistLabel.setWrapText(false);

        card.getChildren().addAll(titleLabel, artistLabel);

        // --- Sự kiện click: chuyển sang SongListView ---
        card.setOnMouseClicked(event -> onAlbumClicked(title, artist, imagePath));
        card.setStyle("-fx-cursor: hand;");

        return card;
    }

    // ---------------------------------------------------------------
    // Placeholder Region khi chưa có ảnh
    // ---------------------------------------------------------------
    private Region buildPlaceholder() {
        Region placeholder = new Region();
        placeholder.getStyleClass().add("album-cover-placeholder");
        placeholder.setPrefWidth(170);
        placeholder.setPrefHeight(170);
        placeholder.setMinWidth(170);
        placeholder.setMinHeight(170);
        return placeholder;
    }

    // ---------------------------------------------------------------
    // Xử lý click vào album → load SongListView.fxml
    // ---------------------------------------------------------------
    private void onAlbumClicked(String albumTitle, String artistName, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/SongListView.fxml")
            );
            Node songListView = loader.load();

            // Truyền thông tin album sang SongListController
            SongListController songListController = loader.getController();
            songListController.setData(albumTitle, artistName, "", imagePath, null);
            // Swap nội dung trong content area của MainView
            if (contentArea != null) {
                contentArea.getChildren().setAll(songListView);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}