package com.musicapp.ui;

import com.musicapp.model.Album;
import com.musicapp.service.DatabaseManager; // Cần cái này để gọi Firebase
import javafx.fxml.FXML;
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
            // 1. Thu thập dữ liệu từ UI
            String title = albumTitleField.getText();
            String artist = artistField.getText();
            String yearStr = releaseYearField.getText();
            String genre = genreField.getText();
            String image = imageUrlField.getText();

            // Kiểm tra dữ liệu trống (Validation cơ bản)
            if (title.isBlank() || artist.isBlank() || yearStr.isBlank()) {
                System.err.println("Error: Please fill in Title, Artist and Year!");
                return;
            }

            int year = Integer.parseInt(yearStr);

            // 2. Tạo đối tượng Album mới bằng Constructor tự sinh ID (UUID)
            // Constructor này mày vừa thêm vào class Album: title, artist, year, image, genre
            newAlbum = new Album(title, artist, year, image, genre);

            System.out.println("--- ADMIN ACTION: PUSHING TO FIREBASE ---");
            
            // 3. ĐẨY DỮ LIỆU LÊN FIREBASE REALTIME DATABASE
            // Hàm này sẽ chọc vào node "albums" và lưu theo albumId đã tự sinh
            DatabaseManager.getInstance().getService().saveAlbum(newAlbum);

            System.out.println("✅ Đã lưu thành công lên Firebase: " + newAlbum.getTitle());

            // Đóng Modal
            closeModal();
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Release Year must be a valid number!");
        } catch (Exception e) {
            System.err.println("Error saving to Firebase: " + e.getMessage());
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
}