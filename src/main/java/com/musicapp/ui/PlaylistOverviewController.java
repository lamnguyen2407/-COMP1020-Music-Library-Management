package com.musicapp.ui;

import com.musicapp.Main;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlaylistOverviewController implements Initializable, MainViewController.MainViewAware {

    @FXML private VBox playlistListContainer;
    @FXML private HBox newPlaylistRow;

    private StackPane contentArea;

    @Override
    public void setMainController(MainViewController mainController) {
        // Fix: Lấy contentArea từ main shell thông qua node thực tế
        if (playlistListContainer != null && playlistListContainer.getScene() != null) {
            this.contentArea = (StackPane) playlistListContainer.getScene().lookup("#contentArea");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoleBasedView();
    }

    private void setupRoleBasedView() {
        if (Main.isAdmin) {
            // --- LOGIC CHO ADMIN (Ảnh 2 - Nhánh Admin) ---
            playlistListContainer.getChildren().clear(); // Xóa sạch các row User tĩnh

            addAdminSystemRow("Today's Hit", "♫");
            addSeparator();
            addAdminSystemRow("All Songs", "≡"); // Mục tiêu chính của Ảnh 3
            addSeparator();
            addAdminSystemRow("All Albums", "◎");
            
        } else {
            // --- LOGIC CHO USER ---
            // Gắn sự kiện cho row "Your favorite songs" (row đầu tiên trong FXML)
            if (!playlistListContainer.getChildren().isEmpty()) {
                Node favoriteRow = playlistListContainer.getChildren().get(0);
                favoriteRow.setOnMouseClicked(e -> loadCompactView("Your favorite songs"));
            }
        }
    }

    /**
     * Chèn các dòng đặc quyền của Admin vào danh sách
     */
    private void addAdminSystemRow(String name, String iconSymbol) {
        HBox row = buildPlaylistRow(name, iconSymbol);
        
        row.setOnMouseClicked(e -> {
            if (name.equals("All Songs")) {
                loadAdminManagementView(); // Mở màn hình Quản lý (Ảnh 3)
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

    // ==========================================
    // NAVIGATION HANDLERS
    // ==========================================

    @FXML
    private void onNewPlaylistClicked() {
        loadView("/CreatePlaylistModal.fxml", "Create Modal");
    }

    /**
     * Mở màn hình Quản lý kho nhạc của Admin (Ảnh 3)
     * Đây là nơi sẽ có 2 nút đỏ ADD và DELETE
     */
    private void loadAdminManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();

            SongListController ctrl = loader.getController();
            // Thiết lập giao diện quản lý cho Admin
            ctrl.setData("All Songs", "Library Management", 
                         "Admin can add or remove songs from the global library here.", 
                         null, null); 
            
            updateMainContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCompactView(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();

            CompactListController ctrl = loader.getController();
            ctrl.setData(title, null);

            updateMainContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMainContent(Node view) {
        // Lấy trực tiếp từ Scene ngay tại thời điểm click để không bao giờ bị null
        if (playlistListContainer.getScene() != null) {
            StackPane area = (StackPane) playlistListContainer.getScene().lookup("#contentArea");
            if (area != null) {
                area.getChildren().setAll(view);
            } else {
                System.err.println("[Lỗi UI] Không tìm thấy #contentArea để chuyển trang!");
            }
        } else {
            System.err.println("[Lỗi UI] Scene hiện tại đang null!");
        }
    }

    // ==========================================
    // UI BUILDER
    // ==========================================

    public void addNewPlaylist(String playlistName, File coverImageFile) {
        HBox newRow = buildPlaylistRow(playlistName, "♫");
        int insertIndex = playlistListContainer.getChildren().indexOf(newPlaylistRow);
        if (insertIndex == -1) insertIndex = playlistListContainer.getChildren().size();

        playlistListContainer.getChildren().add(insertIndex, newRow);
        System.out.println("[Playlist] User added: " + playlistName);
    }

    private HBox buildPlaylistRow(String name, String icon) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(80);
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-cursor: hand;");

        // Icon Box
        StackPane iconBox = new StackPane();
        iconBox.setMinSize(56, 56);
        iconBox.setStyle("-fx-background-color: #D2B48C; -fx-background-radius: 8;");
        Label lblIcon = new Label(icon);
        lblIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        iconBox.getChildren().add(lblIcon);

        // Name
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label chevron = new Label("›");
        chevron.setStyle("-fx-font-size: 20px; -fx-text-fill: #9E8E84;");

        row.getChildren().addAll(iconBox, nameLabel, chevron);
        
        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #F0EAE4; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        return row;
    }

    private void loadView(String fxmlPath, String errorTag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            updateMainContent(view);
        } catch (IOException e) {
            System.err.println("Error loading " + errorTag);
            e.printStackTrace();
        }
    }
}