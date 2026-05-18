package com.musicapp.ui;

import com.musicapp.model.SessionManager;
import com.musicapp.service.DatabaseManager;
import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatePlaylistModalController implements Initializable {

    @FXML private TextField playlistNameField;
    @FXML private TextField descriptionField;
    @FXML private VBox playlistCoverBox;
    @FXML private ImageView uploadedImageView; 
    
    private File selectedImageFile;
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (uploadedImageView == null) {
            uploadedImageView = new ImageView();
        }
        uploadedImageView.setFitWidth(300);
        uploadedImageView.setFitHeight(300);
        uploadedImageView.setPreserveRatio(false);

        playlistCoverBox.setOnMouseClicked(e -> onCoverClicked());
        playlistCoverBox.setStyle("-fx-cursor: hand;");
    }

    private void onCoverClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Playlist Cover Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) playlistCoverBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            try {
                Image image = new Image(file.toURI().toString(), 300, 300, false, true);
                uploadedImageView.setImage(image);
                playlistCoverBox.getChildren().clear();
                playlistCoverBox.getChildren().add(uploadedImageView);
            } catch (Exception e) {
                System.err.println("Error loading image asset: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSavePlaylist() {
        if (SessionManager.currentUser == null) {
            System.err.println("Session Error: User context identity missing.");
            return;
        }
        
        String uid = SessionManager.currentUser.getUserId();
        String name = playlistNameField.getText().trim();

        if (name.isEmpty()) {
            playlistNameField.setStyle("-fx-border-color: #C0703A; -fx-border-width: 0 0 2 0;");
            return;
        }

        DatabaseManager.getInstance().getService().saveNewUserPlaylist(uid, name);
        System.out.println("Playlist transaction pushed to Firebase: " + name);
        
        navigateToPlaylistOverview(name, selectedImageFile);
    }

    @FXML
    private void onCancelClicked() {
        navigateToPlaylistOverview(null, null);
    }

    private void navigateToPlaylistOverview(String newName, File cover) {
        try {
            String fxmlPath = "/PlaylistOverview.fxml"; 
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            PlaylistOverviewController ctrl = loader.getController();
            if (newName != null) {
                ctrl.addNewPlaylist(newName, cover);
            }

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Interface routing navigation failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void onAddMusicClicked(MouseEvent event) { 
        System.out.println("Add music tracking execution event triggered.");
    }
}