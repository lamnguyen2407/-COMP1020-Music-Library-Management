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
            String rawImage = imageUrlField.getText().trim();

            if (title.isBlank() || artist.isBlank() || yearStr.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in Title, Artist, and Release Year.");
                return;
            }

            int year = Integer.parseInt(yearStr);
            String directImage = convertToDirectLink(rawImage);

            newAlbum = new Album(title, artist, year, directImage, genre);
            
            System.out.println("Admin Action: Pushing album to Firebase");
            DatabaseManager.getInstance().getService().saveAlbum(newAlbum);
            System.out.println("Album saved successfully: " + newAlbum.getTitle());

            closeModal();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Release Year must be a valid numerical value.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Unable to save data to Firebase: " + e.getMessage());
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
                return "https://drive.google.com/uc?export=view&id=" + fileId;
            }
        } catch (Exception e) {
            System.err.println("Link conversion error: " + e.getMessage());
        }
        return driveUrl;
    }
}