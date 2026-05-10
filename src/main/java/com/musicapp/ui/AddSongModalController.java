package com.musicapp.ui;

import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSongModalController implements Initializable {

    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField durationField; // Quay trở lại làm FXML field
    @FXML private TextField audioUrlField; 
    @FXML private TextField imageUrlField;

    private String currentAlbumId;
    private Song createdSong = null; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vẫn giữ Auto ID vì nó giúp mày không bị trùng dữ liệu trên Firebase
        // Nếu mày muốn nhập ID tay nốt thì xóa dòng này và bỏ disable trong FXML
    }

    @FXML
    private void onSave() {
        try {
            // 1. Lấy dữ liệu và kiểm tra trống
            String title = titleField.getText().trim();
            String audioUrl = audioUrlField.getText().trim();
            
            if (title.isEmpty() || audioUrl.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập ít nhất là Tên bài và Link nhạc.");
                return;
            }

            // 2. Chuyển đổi dữ liệu số
            String id = Song.generateAutoId(); // Hệ thống tự sinh ID ngầm
            int year = Integer.parseInt(yearField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            // 3. Convert Link Drive
            String directAudioUrl = convertToDirectLink(audioUrl);
            String directImageUrl = convertToDirectLink(imageUrlField.getText().trim());

            // 4. Tạo Object bài hát
            Song newSong = new Song(id, title, artistField.getText().trim(), genreField.getText().trim(), 
                                    duration, year, directAudioUrl, directImageUrl);
            
            // 5. Lưu vào nhánh "songs"
            DatabaseManager.getInstance().getService().getDbRef().child("songs").child(id)
                .setValueAsync(newSong);
            
            // 6. Nối dây vào Album/Playlist (nếu có)
            if (currentAlbumId != null && !currentAlbumId.isEmpty()) {
                String folder = (currentAlbumId.startsWith("pl_") || currentAlbumId.contains("SYSTEM")) ? "playlists" : "albums";
                DatabaseManager.getInstance().getService().getDbRef()
                    .child(folder).child(currentAlbumId).child("songIdList").push().setValueAsync(id);
            }
            
            this.createdSong = newSong;
            System.out.println("✅ Đã lưu bài hát thành công!");
            closeModal();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Năm và Thời lượng (giây) phải là con số!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu bài hát.");
        }
    }

    @FXML private void onCancel() { closeModal(); }

    private void closeModal() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    // Các hàm Helper giữ nguyên
    public void setPredefinedData(String artist, String genre, int year, String imageUrl) {
        if (artistField != null) artistField.setText("");
        if (genreField != null) genreField.setText("");
        if (yearField != null) yearField.setText(String.valueOf(""));
        if (imageUrlField != null) imageUrlField.setText(""); 
    }

    public void setTargetAlbumId(String albumId) { this.currentAlbumId = albumId; }
    public Song getCreatedSong() { return createdSong; }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String convertToDirectLink(String driveUrl) {
        if (driveUrl == null || driveUrl.isEmpty() || !driveUrl.contains("drive.google.com")) {
            return driveUrl;
        }

        try {
            String fileId = "";
            // Dùng Regex để tìm ID (chuỗi khoảng 33 ký tự nằm sau d/ hoặc id=)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([\\w-]{25,})");
            java.util.regex.Matcher matcher = pattern.matcher(driveUrl);
            
            if (matcher.find()) {
                fileId = matcher.group(1);
            }

            if (!fileId.isEmpty()) {
                // Trả về đúng định dạng Lâm test thành công
                return "https://drive.google.com/uc?export=view&id=" + fileId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driveUrl;
    }
}