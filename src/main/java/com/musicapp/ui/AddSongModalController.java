package com.musicapp.ui;

import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.FirebaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSongModalController {

    @FXML private TextField idField; // Giữ lại ID theo yêu cầu FXML của mày
    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField durationField;
    
    // Hai ô TextField mới để dán link trực tiếp từ Drive
    @FXML private TextField audioUrlField; 
    @FXML private TextField imageUrlField;

    @FXML private Button saveBtn;

    @FXML
    private void onSave() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String artist = artistField.getText().trim();
            String genre = genreField.getText().trim();
            
            // Nhớ ép kiểu cẩn thận chỗ này
            int year = Integer.parseInt(yearField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            String rawAudioUrl = audioUrlField.getText().trim();
            String rawImageUrl = imageUrlField.getText().trim();
            
            String directAudioUrl = convertToDirectLink(rawAudioUrl);
            String directImageUrl = convertToDirectLink(rawImageUrl);

            Song newSong = new Song(id, title, artist, genre, duration, year, directAudioUrl, directImageUrl);
            DatabaseManager.getInstance().getService().saveSong(newSong);
            
       
            // THÊM DÒNG NÀY:
            if (SongListController.instance != null) {
                SongListController.instance.refreshData();
            }

            closeModal();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Năm và thời lượng phải là số!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
 // Hàm này giúp biến link Drive "xem" thành link "tải/phát nhạc"
    private String convertToDirectLink(String driveUrl) {
        if (driveUrl == null || !driveUrl.contains("drive.google.com")) {
            return driveUrl; // Nếu không phải link Drive thì giữ nguyên
        }
        
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
            System.err.println("Lỗi chuyển đổi link Drive: " + e.getMessage());
        }
        
        return driveUrl;
    }
}