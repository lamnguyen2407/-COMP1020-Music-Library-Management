package com.musicapp.ui;

import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    // BIẾN MỚI: Để giữ ID của album hiện tại
    private String currentAlbumId;
    
    private Song createdSong = null; 
    
    public Song getCreatedSong() { 
        return createdSong; 
    }
    
    // HÀM MỚI: Để SongListController truyền AlbumID sang
    public void setTargetAlbumId(String albumId) {
        this.currentAlbumId = albumId;
    }

    @FXML
    private void onSave() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String artist = artistField.getText().trim();
            String genre = genreField.getText().trim();
            
            int year = Integer.parseInt(yearField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            String rawAudioUrl = audioUrlField.getText().trim();
            String rawImageUrl = imageUrlField.getText().trim();
            
            String directAudioUrl = convertToDirectLink(rawAudioUrl);
            String directImageUrl = convertToDirectLink(rawImageUrl);

            // 1. Lưu bài hát vào kho /songs chung
            Song newSong = new Song(id, title, artist, genre, duration, year, directAudioUrl, directImageUrl);
            DatabaseManager.getInstance().getService().saveSong(newSong);
            
            // 2. NỐI DÂY: Nếu đang ở trong một Album cụ thể, lưu ID bài hát vào Album đó luôn
            if (currentAlbumId != null && !currentAlbumId.isEmpty()) {
                if (currentAlbumId.startsWith("pl_") || currentAlbumId.contains("SYSTEM")) {
                    // Nếu là Playlist (Today's Hits hoặc Playlist tự tạo)
                    DatabaseManager.getInstance().getService().addSongToPlaylist(currentAlbumId, id);
                } else {
                    // Nếu là Album thật sự
                    DatabaseManager.getInstance().getService().addSongToAlbum(currentAlbumId, id);
                }
            }
            
            this.createdSong = newSong;
            closeModal();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Vui lòng kiểm tra lại dữ liệu nhập vào (Năm, Thời lượng phải là số).");
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

    public void setPredefinedData(String artist, String genre, int year, String imageUrl) {
        if (artistField != null) artistField.setText(artist);
        if (genreField != null) genreField.setText(genre);
        if (yearField != null) yearField.setText(String.valueOf(year));
        
        // Nó TỰ ĐỘNG dán Link ảnh của Album vào TextField Ảnh bìa bài hát!
        if (imageUrlField != null) imageUrlField.setText(imageUrl); 
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String convertToDirectLink(String driveUrl) {
        if (driveUrl == null || !driveUrl.contains("drive.google.com")) return driveUrl;
        try {
            String fileId = "";
            if (driveUrl.contains("/d/")) fileId = driveUrl.split("/d/")[1].split("/")[0];
            else if (driveUrl.contains("id=")) fileId = driveUrl.split("id=")[1].split("&")[0];
            if (!fileId.isEmpty()) return "https://drive.google.com/uc?export=download&id=" + fileId;
        } catch (Exception e) {}
        return driveUrl;
    }
}