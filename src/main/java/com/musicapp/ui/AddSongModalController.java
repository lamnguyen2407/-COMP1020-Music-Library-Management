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

    @FXML private TextField idField; 
    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField durationField;
    
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
            
            // Lấy code ép kiểu và xử lý link Drive của nhóm
            int year = Integer.parseInt(yearField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            String rawAudioUrl = audioUrlField.getText().trim();
            String rawImageUrl = imageUrlField.getText().trim();
            
            String directAudioUrl = convertToDirectLink(rawAudioUrl);
            String directImageUrl = convertToDirectLink(rawImageUrl);

            // Lưu thẳng vào Database thật (thay cho dòng Mock của bạn)
            Song newSong = new Song(id, title, artist, genre, duration, year, directAudioUrl, directImageUrl);
            DatabaseManager.getInstance().getService().saveSong(newSong);
            
            // Refresh lại danh sách hiển thị
            if (SongListController.instance != null) {
                SongListController.instance.refreshData();
            }

            // Lấy code của bạn: Đóng popup sau khi lưu xong cho mượt UX
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

    // ====================================================
    // HÀM CỦA BẠN: TỰ ĐỘNG ĐIỀN DATA TỪ ALBUM TRUYỀN SANG
    // ====================================================
    public void setPredefinedData(String artist, String genre, int year, String imageUrl) {
        if (artistField != null) artistField.setText(artist);
        if (genreField != null) genreField.setText(genre);
        if (yearField != null) yearField.setText(String.valueOf(year)); 
        if (imageUrlField != null) imageUrlField.setText(imageUrl);
        
        // (Tuỳ chọn) Khoá các ô này lại không cho Admin sửa
        // artistField.setEditable(false);
        // imageUrlField.setEditable(false);
    }

    // ====================================================
    // HÀM CỦA NHÓM: XỬ LÝ THÔNG BÁO VÀ CONVERT LINK DRIVE
    // ====================================================
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
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