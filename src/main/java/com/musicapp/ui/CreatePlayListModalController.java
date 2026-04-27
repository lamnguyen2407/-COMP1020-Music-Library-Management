package com.musicapp.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatePlaylistController implements Initializable {

    // ═══════════════════════════════════════════
    // FXML FIELDS
    // ═══════════════════════════════════════════
    @FXML private TextField  playlistNameField;
    @FXML private TextField  descriptionField;
    @FXML private VBox       playlistCoverBox;      // ô vuông màu nâu
    @FXML private StackPane  playlistCoverContainer;// StackPane bọc ô vuông

    // ImageView để hiển thị ảnh sau khi upload
    private ImageView uploadedImageView;

    // Lưu file ảnh người dùng chọn
    private File selectedImageFile;

    // Content area từ MainController để swap view khi Cancel
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ═══════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Chuẩn bị ImageView để hiển thị ảnh upload
        uploadedImageView = new ImageView();
        uploadedImageView.setFitWidth(300);
        uploadedImageView.setFitHeight(300);
        uploadedImageView.setPreserveRatio(false);

        // Bấm vào ô vuông ảnh bìa → mở file chooser
        playlistCoverBox.setOnMouseClicked(e -> onCoverClicked());
        playlistCoverContainer.setOnMouseClicked(e -> onCoverClicked());
        playlistCoverBox.setStyle("-fx-cursor: hand;");
    }

    // ═══════════════════════════════════════════
    // CLICK VÀO Ô VUÔNG → UPLOAD ẢNH
    // ═══════════════════════════════════════════
    private void onCoverClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh bìa Playlist");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp")
        );

        // Lấy Stage hiện tại
        Stage stage = (Stage) playlistCoverBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            try {
                Image image = new Image(file.toURI().toString(), 300, 300, false, true);
                uploadedImageView.setImage(image);

                // Xóa nội dung cũ trong ô vuông và thay bằng ảnh mới
                playlistCoverBox.getChildren().clear();
                playlistCoverBox.getChildren().add(uploadedImageView);

                System.out.println("✅ Ảnh đã chọn: " + file.getName());
            } catch (Exception e) {
                System.out.println("❌ Lỗi load ảnh: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════
    // FXML HANDLER — bấm Add Music
    // ═══════════════════════════════════════════
    @FXML
    private void onAddMusicClicked() {
        // TODO: mở dialog chọn bài hát thêm vào playlist
        System.out.println("➕ Add Music clicked");
    }

    // ═══════════════════════════════════════════
    // FXML HANDLER — bấm Done
    // ═══════════════════════════════════════════
    @FXML
    private void onDoneClicked() {
        String name        = playlistNameField.getText().trim();
        String description = descriptionField.getText().trim();

        // Kiểm tra tên playlist không được để trống
        if (name.isEmpty()) {
            playlistNameField.setStyle(
                "-fx-border-color: #C0703A; -fx-border-width: 0 0 2 0;"
            );
            System.out.println("⚠ Vui lòng nhập tên playlist!");
            return;
        }

        // TODO: backend gọi PlaylistService.create(name, description, selectedImageFile)
        System.out.println("✅ Tạo playlist: " + name);
        System.out.println("   Mô tả: " + description);
        System.out.println("   Ảnh: " + (selectedImageFile != null ? selectedImageFile.getName() : "Không có"));

        // Sau khi tạo xong → quay về PlaylistOverview
        navigateToPlaylistOverview();
    }

    // ═══════════════════════════════════════════
    // FXML HANDLER — bấm Cancel → quay về PlaylistOverview
    // ═══════════════════════════════════════════
    @FXML
    private void onCancelClicked() {
        navigateToPlaylistOverview();
    }

    // ═══════════════════════════════════════════
    // NAVIGATE VỀ PlaylistOverview
    // ═══════════════════════════════════════════
    private void navigateToPlaylistOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/PlaylistOverview.fxml")
            );
            Node view = loader.load();

            // Truyền playlist mới sang PlaylistOverviewController
            PlaylistOverviewController ctrl = loader.getController();
            ctrl.addNewPlaylist(
                playlistNameField.getText().trim(),
                selectedImageFile  // ảnh bìa nếu có
            );

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}