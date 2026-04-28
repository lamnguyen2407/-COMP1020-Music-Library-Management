package com.musicapp.ui;

import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.FirebaseService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.UUID;

public class AddSongModalController {

    // Note: idField is removed because UUID generates the ID automatically
    @FXML private TextField titleField, artistField, genreField, yearField, durationField;
    
    // Ensure you add these fx:id to the buttons in your FXML file
    @FXML private Button selectAudioBtn, selectImageBtn, saveBtn;

    private File selectedAudioFile;
    private File selectedImageFile;

    @FXML
    private void onSelectAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        selectedAudioFile = fileChooser.showOpenDialog(titleField.getScene().getWindow());
        
        if (selectedAudioFile != null) {
            selectAudioBtn.setText(selectedAudioFile.getName());
        }
    }

    @FXML
    private void onSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(titleField.getScene().getWindow());
        
        if (selectedImageFile != null) {
            selectImageBtn.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void onSave() {
        try {
            String title = titleField.getText();
            String artist = artistField.getText();
            String genre = genreField.getText();
            int year = Integer.parseInt(yearField.getText());
            int duration = Integer.parseInt(durationField.getText());

            if (selectedAudioFile == null || selectedImageFile == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Files", "Please select both an audio file and a cover image.");
                return;
            }

            // Disable the save button to prevent duplicate uploads
            saveBtn.setDisable(true);
            saveBtn.setText("Uploading...");

            // Run network operations in the background to keep the UI responsive
            Task<Void> uploadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    FirebaseService dbService = DatabaseManager.getInstance().getService();

                    // 1. Upload files to Storage and get public URLs
                    String audioUrl = dbService.uploadFileToStorage(selectedAudioFile, "songs");
                    String imageUrl = dbService.uploadFileToStorage(selectedImageFile, "covers");

                    // 2. Create the Song object with auto-generated UUID
                    Song newSong = new Song(
                        UUID.randomUUID().toString(),
                        title,
                        artist,
                        genre,
                        duration,
                        year,
                        audioUrl,
                        imageUrl
                    );

                    // 3. Save metadata to Realtime Database
                    dbService.saveSong(newSong);
                    return null;
                }
            };

            uploadTask.setOnSucceeded(e -> {
                System.out.println("Song saved successfully!");
                // Use Platform.runLater to update the UI from the background thread
                Platform.runLater(this::closeModal);
            });

            uploadTask.setOnFailed(e -> {
                Throwable error = uploadTask.getException();
                System.err.println("Upload failed: " + error.getMessage());
                
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Upload Error", error.getMessage());
                    saveBtn.setDisable(false);
                    saveBtn.setText("Save");
                });
            });

            // Start the background process
            new Thread(uploadTask).start();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Year and Duration must be valid numbers.");
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
}