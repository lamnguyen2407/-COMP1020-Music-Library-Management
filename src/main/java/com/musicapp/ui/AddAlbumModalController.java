package com.musicapp.ui;

import com.musicapp.model.Album;
import com.musicapp.service.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddAlbumModalController {

    @FXML private TextField albumTitleField;
    @FXML private TextField artistField;
    @FXML private TextField releaseYearField;
    @FXML private TextField genreField;
    @FXML private TextField imageUrlField;

    private Album newAlbum = null;

    @FXML
    private void onSave() {
        try {
            String title = albumTitleField.getText().trim();
            String artist = artistField.getText().trim();
            String yearStr = releaseYearField.getText().trim();
            String genre = genreField.getText().trim();
            
            // Lấy link gốc người dùng nhập
            String rawImage = imageUrlField.getText().trim();

            if (title.isBlank() || artist.isBlank() || yearStr.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Title, Artist và Release Year!");
                return;
            }

            int year = Integer.parseInt(yearStr);

            // BÍ QUYẾT Ở ĐÂY: Chuyển đổi link Drive /view thành link tải trực tiếp
            String directImage = convertToDirectLink(rawImage);

            // Tạo Album với link đã được convert
            newAlbum = new Album(title, artist, year, directImage, genre);
            System.out.println("--- ADMIN ACTION: PUSHING TO FIREBASE ---");
            
            DatabaseManager.getInstance().getService().saveAlbum(newAlbum);
            System.out.println("✅ Đã lưu thành công: " + newAlbum.getTitle());

            closeModal();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Release Year (Năm) phải là một con số!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể lưu vào Firebase: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        newAlbum = null; 
        closeModal();
    }

    private void closeModal() {
        if (albumTitleField.getScene() != null) {
            Stage stage = (Stage) albumTitleField.getScene().getWindow();
            stage.close();
        }
    }

    public Album getNewAlbum() {
        return newAlbum;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // THÊM HÀM NÀY: Hàm thần thánh chuyển link Drive thành link ảnh trực tiếp
    private String convertToDirectLink(String driveUrl) {
        if (driveUrl == null || !driveUrl.contains("drive.google.com")) return driveUrl;
        try {
            String fileId = "";
            if (driveUrl.contains("/d/")) {
                fileId = driveUrl.split("/d/")[1].split("/")[0];
            } else if (driveUrl.contains("id=")) {
                fileId = driveUrl.split("id=")[1].split("&")[0];
            }
            if (!fileId.isEmpty()) {
                return "https://drive.google.com/uc?export=download&id=" + fileId;
            }
        } catch (Exception e) {
            System.err.println("Lỗi convert link: " + e.getMessage());
        }
        return driveUrl;
    }
}