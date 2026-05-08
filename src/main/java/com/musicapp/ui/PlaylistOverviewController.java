package com.musicapp.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.musicapp.model.Playlist;
import com.musicapp.model.SessionManager;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlaylistOverviewController implements Initializable, MainViewController.MainViewAware {

    public static PlaylistOverviewController instance;

    @FXML private VBox playlistListContainer;
    @FXML private HBox newPlaylistRow; 
    @FXML private StackPane contentArea;
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> audioColumn;

    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
        if (playlistListContainer != null && playlistListContainer.getScene() != null) {
            this.contentArea = (StackPane) playlistListContainer.getScene().lookup("#contentArea");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        if (titleColumn != null) titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (artistColumn != null) artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        if (audioColumn != null) audioColumn.setCellValueFactory(new PropertyValueFactory<>("audioURL"));

        refreshData();
        setupRoleBasedView();
    }

    public void addNewPlaylist(String playlistName, File coverImageFile) {
        HBox newRow = buildPlaylistRow(playlistName, "♫");
        
        int insertIndex = playlistListContainer.getChildren().indexOf(newPlaylistRow);
        if (insertIndex == -1) insertIndex = playlistListContainer.getChildren().size();

        playlistListContainer.getChildren().add(insertIndex, newRow);
    }

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Song> updatedSongs = DatabaseManager.getInstance().getService().fetchSongs();
                Platform.runLater(() -> {
                    if (tableView != null) {
                        tableView.getItems().setAll(updatedSongs);
                    }
                });
            } catch (Exception e) {
                System.err.println("[Error] Lỗi khi load nhạc: " + e.getMessage());
            }
        }).start();
    }

    private void setupRoleBasedView() {
        if (playlistListContainer == null) return;
        
        if (SessionManager.isAdmin) {
            Node tableNode = tableView;
            playlistListContainer.getChildren().clear(); 
            
            if (tableNode != null) playlistListContainer.getChildren().add(tableNode);

            // FIX: Thêm chữ 's' cho Today's Hits
            addAdminSystemRow("Today's Hits", "♫");
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
            } else if (name.equals("All Albums")) { 
                loadNewAlbumReleaseView();
            } else if (name.equals("Today's Hits")) { // FIX: Phải khớp với tên bên trên
                loadTodaysHitsView();
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
            updateMainContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAdminManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
            }
            
            ctrl.setData(
                "ADMIN_ALL_SONGS", 
                "All Songs", 
                "Library Management",
                "Admin can add or remove songs from the global library here.",
                "/images/allsong.jpg", // Đã sửa thành chữ thường khớp với file
                0, 
                "Various", 
                new java.util.ArrayList<>()
            );
            
            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNewAlbumReleaseView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewAlbumReleaseView.fxml"));
            Node view = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
            }
            updateMainContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTodaysHitsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
            }
            
            // TRUYỀN ĐÚNG ID MÀ MAIN VIEW ĐANG DÙNG
            ctrl.setData(
                "SYSTEM_TODAY'S_HITS",  // <--- ĐỔI DÒNG NÀY LÀ XONG (thay vì "pl_system_todays_hits")
                "Today's Hits", 
                "System Playlist",
                "The biggest tracks on everyone's mind right now.",
                "/images/todayhit.jpg", 
                2026,
                "Various Artists",
                new java.util.ArrayList<>()
            );
            
            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCompactView(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) ctrl).setMainController(this.mainController);
            }
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