package com.musicapp.ui;

import javafx.application.Platform;
import com.musicapp.Main;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlaylistOverviewController implements Initializable, MainViewController.MainViewAware {

    // Singleton để các Modal gọi refreshData()
    public static PlaylistOverviewController instance;

    @FXML private VBox playlistListContainer;
    @FXML private HBox newPlaylistRow; // Cái hàng để bấm tạo Playlist mới
    @FXML private StackPane contentArea;
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> audioColumn;

    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
        // Cố gắng lấy contentArea từ scene nếu chưa có
        if (playlistListContainer != null && playlistListContainer.getScene() != null) {
            this.contentArea = (StackPane) playlistListContainer.getScene().lookup("#contentArea");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        // Map Column cho TableView (Dùng cho Admin hoặc các list nhạc)
        if (titleColumn != null) titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (artistColumn != null) artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        if (audioColumn != null) audioColumn.setCellValueFactory(new PropertyValueFactory<>("audioURL"));

        refreshData();
        setupRoleBasedView();
    }

    // --- HÀM FIX LỖI: Thêm Playlist mới vào giao diện (Gọi từ CreatePlaylistModal) ---
    public void addNewPlaylist(String playlistName, File coverImageFile) {
        HBox newRow = buildPlaylistRow(playlistName, "♫");
        
        // Chèn vào ngay trước cái nút "New Playlist"
        int insertIndex = playlistListContainer.getChildren().indexOf(newPlaylistRow);
        if (insertIndex == -1) insertIndex = playlistListContainer.getChildren().size();

        playlistListContainer.getChildren().add(insertIndex, newRow);
        System.out.println("[Success] Đã thêm playlist mới vào UI: " + playlistName);
    }

    // --- HÀM FIX LỖI: Load nhạc từ Firebase ---
    public void refreshData() {
        new Thread(() -> {
            try {
                List<Song> updatedSongs = DatabaseManager.getInstance().getService().fetchSongs();
                Platform.runLater(() -> {
                    if (tableView != null) {
                        tableView.getItems().setAll(updatedSongs);
                        System.out.println("[Success] Đã nạp " + updatedSongs.size() + " bài hát từ Firebase.");
                    }
                });
            } catch (Exception e) {
                System.err.println("[Error] Lỗi khi load nhạc: " + e.getMessage());
            }
        }).start();
    }

    private void setupRoleBasedView() {
        if (playlistListContainer == null) return;
        
        if (Main.isAdmin) {
            // Lưu lại cái bảng trước khi clear nếu cần hiện nó cho Admin
            Node tableNode = tableView;
            playlistListContainer.getChildren().clear(); 
            
            if (tableNode != null) playlistListContainer.getChildren().add(tableNode);

            addAdminSystemRow("Today's Hit", "♫");
            addSeparator();
            addAdminSystemRow("All Songs", "≡");
            addSeparator();
            addAdminSystemRow("All Albums", "◎");
        }
    }

    private void addAdminSystemRow(String name, String iconSymbol) {
        HBox row = buildPlaylistRow(name, iconSymbol);
        row.setOnMouseClicked(e -> {
            if (name.equals("All Songs")) {
                loadAdminManagementView();
            } else if (name.equals("All Albums")) { // BẮT SỰ KIỆN CLICK VÀO ALL ALBUMS Ở ĐÂY
                loadNewAlbumReleaseView();
            } else {
                loadCompactView(name);
            }
        });
        playlistListContainer.getChildren().add(row);
    }

    private void addSeparator() {
        Region sep = new Region();
        sep.setMinHeight(1);
        sep.setStyle("-fx-background-color: #E0D8D0;");
        VBox.setMargin(sep, new Insets(0, 32, 0, 32));
        playlistListContainer.getChildren().add(sep);
    }

    @FXML
    private void onNewPlaylistClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreatePlaylistModal.fxml"));
            Node view = loader.load();
            
            // CreatePlaylistModalController ctrl = loader.getController(); // Lấy controller nếu cần thiết lập data
            updateMainContent(view);
        } catch (IOException e) {
            System.err.println("[Lỗi] Không load được CreatePlaylistModal.fxml");
            e.printStackTrace();
        }
    }

    private void loadAdminManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();

            SongListController ctrl = loader.getController();
            ctrl.setData("All Songs", "Library Management",
                    "Admin can add or remove songs from the global library here.",
                    null, 0, null, null);
            
            updateMainContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HÀM MỚI: DÙNG ĐỂ LOAD GIAO DIỆN NEW ALBUM RELEASE
    private void loadNewAlbumReleaseView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewAlbumReleaseView.fxml"));
            Node view = loader.load();

            // Nếu sau này cần truyền data cho NewAlbumReleaseController thì lấy ra ở đây
            // NewAlbumReleaseController ctrl = loader.getController();

            updateMainContent(view);
        } catch (IOException e) {
            System.err.println("[Lỗi] Không load được NewAlbumReleaseView.fxml");
            e.printStackTrace();
        }
    }

    private void loadCompactView(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();
            updateMainContent(view); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMainContent(Node view) {
        if (contentArea == null && playlistListContainer.getScene() != null) {
            contentArea = (StackPane) playlistListContainer.getScene().lookup("#contentArea");
        }
        if (contentArea != null) {
            contentArea.getChildren().setAll(view);
        }
    }

    private HBox buildPlaylistRow(String name, String icon) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(80);
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-cursor: hand;");

        StackPane iconBox = new StackPane();
        iconBox.setMinSize(56, 56);
        iconBox.setStyle("-fx-background-color: #D2B48C; -fx-background-radius: 8;");
        Label lblIcon = new Label(icon);
        lblIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        iconBox.getChildren().add(lblIcon);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label chevron = new Label("›");
        chevron.setStyle("-fx-font-size: 20px; -fx-text-fill: #9E8E84;");

        row.getChildren().addAll(iconBox, nameLabel, chevron);
        row.setOnMouseEntered(ev -> row.setStyle("-fx-background-color: #F0EAE4; -fx-cursor: hand;"));
        row.setOnMouseExited(ev -> row.setStyle("-fx-background-color: transparent;"));

        return row;
    }
}