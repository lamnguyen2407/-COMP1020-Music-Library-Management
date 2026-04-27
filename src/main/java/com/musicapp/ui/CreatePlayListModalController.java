package com.musicapp.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

public class CreatePlaylistModalController implements Initializable {

    // ==========================================
    // FXML FIELDS
    // ==========================================
    @FXML private TextField  playlistNameField;
    @FXML private TextField  descriptionField;
    @FXML private VBox       playlistCoverBox;      // The brown square
    @FXML private StackPane  playlistCoverContainer;// Container for the square

    private ImageView uploadedImageView;
    private File selectedImageFile;

    // Reference to Main content area for navigation
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ==========================================
    // INITIALIZATION
    // ==========================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Prepare ImageView for the uploaded cover
        uploadedImageView = new ImageView();
        uploadedImageView.setFitWidth(300);
        uploadedImageView.setFitHeight(300);
        uploadedImageView.setPreserveRatio(false);

        // Wiring the click event for cover upload
        playlistCoverBox.setOnMouseClicked(e -> onCoverClicked());
        playlistCoverBox.setStyle("-fx-cursor: hand;");
    }

    // ==========================================
    // COVER UPLOAD LOGIC
    // ==========================================
    private void onCoverClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Playlist Cover Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) playlistCoverBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            try {
                Image image = new Image(file.toURI().toString(), 300, 300, false, true);
                uploadedImageView.setImage(image);

                // Replace the placeholder content with the actual image
                playlistCoverBox.getChildren().clear();
                playlistCoverBox.getChildren().add(uploadedImageView);

                System.out.println("[CreatePlaylist] Image selected: " + file.getName());
            } catch (Exception e) {
                System.err.println("[CreatePlaylist] Error loading image: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // NAVIGATION & ACTIONS
    // ==========================================

    @FXML
    private void onAddMusicClicked() {
        // TODO: Future Implementation - Open song picker dialog
        System.out.println("[CreatePlaylist] Add Music triggered");
    }

    @FXML
    private void onDoneClicked() {
        String name = playlistNameField.getText().trim();
        String description = descriptionField.getText().trim();

        // Simple validation to prevent empty playlists
        if (name.isEmpty()) {
            playlistNameField.setStyle("-fx-border-color: #C0703A; -fx-border-width: 0 0 2 0;");
            System.out.println("[CreatePlaylist] Validation failed: Name is empty.");
            return;
        }

        // --- BACKEND HANDSHAKE ---
        // TODO: PlaylistService.getInstance().createPlaylist(name, description, selectedImageFile);
        System.out.println("[CreatePlaylist] Success! Created: " + name);

        // Navigate back to overview to show the results
        navigateToPlaylistOverview(name, selectedImageFile);
    }

    @FXML
    private void onCancelClicked() {
        // Navigate back without saving
        navigateToPlaylistOverview(null, null);
    }

    private void navigateToPlaylistOverview(String newName, File cover) {
        try {
            // FIX: Ensure path matches your resources structure
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlaylistOverview.fxml"));
            Node view = loader.load();

            // Link the new data to the overview controller
            PlaylistOverviewController ctrl = loader.getController();
            
            // Only add if we actually created something
            if (newName != null) {
                ctrl.addNewPlaylist(newName, cover);
            }

            // Swap view in the main content area
            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            } else {
                // Fallback: If contentArea wasn't injected, try to find it from the scene
                StackPane mainContent = (StackPane) playlistCoverBox.getScene().lookup("#contentArea");
                if (mainContent != null) {
                    mainContent.getChildren().setAll(view);
                }
            }
        } catch (IOException e) {
            System.err.println("[CreatePlaylist] Navigation failed!");
            e.printStackTrace();
        }
    }
}