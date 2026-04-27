package com.musicapp.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistOverviewController implements Initializable {

    // ═══════════════════════════════════════════
    // FXML FIELDS
    // ═══════════════════════════════════════════
    @FXML private VBox  playlistListContainer;
    @FXML private HBox  newPlaylistRow;

    // Content area từ MainController để swap view
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ═══════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gắn sự kiện click vào row "Your favorite songs" (row đầu tiên — static trong FXML)
        // Row này là children index 0 trong playlistListContainer
        Node favoriteRow = playlistListContainer.getChildren().get(0);
        favoriteRow.setOnMouseClicked(e -> onFavoriteSongsClicked());
        favoriteRow.setStyle("-fx-cursor: hand;");
    }

    // ═══════════════════════════════════════════
    // CLICK VÀO "Your favorite songs"
    // → Load CompactListView với title "Your favorite songs"
    // ═══════════════════════════════════════════
    private void onFavoriteSongsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CompactListView.fxml")
            );
            Node view = loader.load();

            CompactListController ctrl = loader.getController();
            ctrl.setContentArea(contentArea);
            ctrl.setData("Your favorite songs", null); // null → dùng data mẫu, backend thay bằng list thật

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════
    // CLICK VÀO DẤU "+" → Mở CreatePlaylistModal
    // ═══════════════════════════════════════════
    @FXML
    private void onNewPlaylistClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CreatePlaylistModal.fxml")
            );
            Node view = loader.load();

            CreatePlaylistController ctrl = loader.getController();
            ctrl.setContentArea(contentArea);

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════
    // ĐƯỢC GỌI TỪ CreatePlaylistController
    // sau khi bấm Done → thêm playlist mới vào danh sách
    // ═══════════════════════════════════════════
    public void addNewPlaylist(String playlistName, File coverImageFile) {
        // Tạo row mới cho playlist vừa tạo
        HBox newRow = buildPlaylistRow(playlistName);

        // Insert trước row "New PlayList" (luôn cuối cùng)
        int insertIndex = playlistListContainer.getChildren().indexOf(newPlaylistRow);
        playlistListContainer.getChildren().add(insertIndex, newRow);

        // Thêm separator phía trên row mới
        Region separator = new Region();
        separator.getStyleClass().add("playlist-separator");
        separator.setMinHeight(1);
        separator.setMaxHeight(1);
        separator.setPrefHeight(1);
        playlistListContainer.getChildren().add(insertIndex, separator);

        System.out.println("✅ Playlist mới đã thêm: " + playlistName);
    }

    // ═══════════════════════════════════════════
    // TẠO MỘT PLAYLIST ROW MỚI
    // ═══════════════════════════════════════════
    private HBox buildPlaylistRow(String playlistName) {
        HBox row = new HBox(16);
        row.getStyleClass().add("playlist-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setMinHeight(80);
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-cursor: hand;");

        // Icon box (ô vuông nhạc)
        HBox iconBox = new HBox();
        iconBox.getStyleClass().add("playlist-icon-box");
        iconBox.setAlignment(javafx.geometry.Pos.CENTER);
        iconBox.setMinWidth(56);  iconBox.setMinHeight(56);
        iconBox.setMaxWidth(56);  iconBox.setMaxHeight(56);

        Label iconLabel = new Label("♫");
        iconLabel.getStyleClass().add("playlist-icon-label");
        iconBox.getChildren().add(iconLabel);

        // Tên playlist
        Label nameLabel = new Label(playlistName);
        nameLabel.getStyleClass().add("playlist-name-label");
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        // Chevron
        Label chevron = new Label("›");
        chevron.getStyleClass().add("playlist-chevron-label");

        row.getChildren().addAll(iconBox, nameLabel, chevron);

        // Click vào playlist mới → load CompactListView với tên playlist đó
        row.setOnMouseClicked(e -> onPlaylistClicked(playlistName));

        return row;
    }

    // ═══════════════════════════════════════════
    // CLICK VÀO MỘT PLAYLIST BẤT KỲ
    // ═══════════════════════════════════════════
    private void onPlaylistClicked(String playlistName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CompactListView.fxml")
            );
            Node view = loader.load();

            CompactListController ctrl = loader.getController();
            ctrl.setContentArea(contentArea);
            ctrl.setData(playlistName, null); // backend thay null bằng list thật

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}