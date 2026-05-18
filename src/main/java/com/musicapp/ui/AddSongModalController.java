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
    @FXML private TextField durationField; 
    @FXML private TextField audioUrlField; 
    @FXML private TextField imageUrlField;

    private String currentAlbumId;
    private Song createdSong = null; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onSave() {
        try {
            String title = titleField.getText().trim();
            String audioUrl = audioUrlField.getText().trim();
            
            if (title.isEmpty() || audioUrl.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", "Title and Audio URL fields are required.");
                return;
            }

            String id = Song.generateAutoId(); 
            int year = Integer.parseInt(yearField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            String directAudioUrl = convertToDirectLink(audioUrl);
            String directImageUrl = convertToDirectLink(imageUrlField.getText().trim());

            Song newSong = new Song(id, title, artistField.getText().trim(), genreField.getText().trim(), 
                                    duration, year, directAudioUrl, directImageUrl);
            
            DatabaseManager.getInstance().getService().saveSong(newSong);
            
            if (currentAlbumId != null && !currentAlbumId.isEmpty()) {
                if (currentAlbumId.startsWith("pl_") || currentAlbumId.contains("SYSTEM")) {
                    DatabaseManager.getInstance().getService().addSongToPlaylist(currentAlbumId, id);
                } else {
                    DatabaseManager.getInstance().getService().addSongToAlbum(currentAlbumId, id);
                }
            }
            
            this.createdSong = newSong;
            System.out.println("Song saved successfully.");
            closeModal();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Year and Duration fields must contain valid integers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Could not complete song insertion transaction.");
        }
    }

    @FXML private void onCancel() { closeModal(); }

    private void closeModal() {
        if (titleField.getScene() != null) {
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
        }
    }

    public void setPredefinedData(String artist, String genre, int year, String imageUrl) {
        if (artistField != null) artistField.setText(artist != null ? artist : "");
        if (genreField != null) genreField.setText(genre != null ? genre : "");
        if (yearField != null) yearField.setText(year > 0 ? String.valueOf(year) : "");
        if (imageUrlField != null) imageUrlField.setText(imageUrl != null ? imageUrl : ""); 
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
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([\\w-]{25,})");
            java.util.regex.Matcher matcher = pattern.matcher(driveUrl);
            
            if (matcher.find()) {
                fileId = matcher.group(1);
            }

            if (!fileId.isEmpty()) {
                return "https://drive.google.com/uc?export=view&id=" + fileId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driveUrl;
    }
}