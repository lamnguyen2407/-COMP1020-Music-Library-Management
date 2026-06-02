package com.musicapp.ui;

import com.musicapp.service.DatabaseManager;
import com.musicapp.model.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    @FXML private Label roleLabel;
    @FXML private Label usernameDisplay;
    @FXML private TextField usernameField;
    @FXML private Label emailDisplay;
    @FXML private TextField emailField;
    @FXML private Label nameDisplay;
    @FXML private TextField nameField;

    @FXML private Button editBtn, cancelBtn, saveBtn;

    private String savedUsername, savedEmail, savedName;
    private String currentUserId;
    private String currentUserRole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (SessionManager.currentUser != null) {
            this.currentUserId = SessionManager.currentUser.getUserId();
            this.currentUserRole = SessionManager.isAdmin ? "admin" : "user";
            loadUserData();
        } else {
            System.err.println("Session not found");
        }
    }

    private void loadUserData() {
        System.out.println("Loading data for User ID: " + currentUserId);

        DatabaseManager.getInstance().getService().getDbRef()
                .child("users")
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Data found on Firebase: " + snapshot.exists());

                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);
                    
                    String fullName = snapshot.hasChild("fullname") 
                                    ? snapshot.child("fullname").getValue(String.class) 
                                    : snapshot.child("name").getValue(String.class);

                    Platform.runLater(() -> {
                        savedUsername = username;
                        savedEmail = email;
                        savedName = fullName;

                        if (role != null && !role.isEmpty()) {
                            roleLabel.setText(role.substring(0, 1).toUpperCase() + role.substring(1));
                        }
                        
                        usernameDisplay.setText(savedUsername);
                        emailDisplay.setText(savedEmail);
                        nameDisplay.setText(savedName);

                        usernameField.setText(savedUsername);
                        emailField.setText(savedEmail);
                        nameField.setText(savedName);

                        setEditMode(false);
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Data load failed: " + error.getMessage());
            }
        });
    }

    @FXML
    private void handleSave() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newName = nameField.getText().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty() || newName.isEmpty()) {
            showAlert("Input Required", "All fields must be filled.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newUsername);
        updates.put("email", newEmail);
        updates.put("fullname", newName);

        DatabaseManager.getInstance().getService().getDbRef()
                .child("users")
                .child(currentUserId)
                .updateChildren(updates, (error, ref) -> {
                    if (error == null) {
                        Platform.runLater(() -> {
                            savedUsername = newUsername;
                            savedEmail = newEmail;
                            savedName = newName;
                            updateLabels();
                            setEditMode(false);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Update failed: " + error.getMessage()));
                    }
                });
    }

    private void updateLabels() {
        usernameDisplay.setText(savedUsername);
        emailDisplay.setText(savedEmail);
        nameDisplay.setText(savedName);
    }

    @FXML 
    private void handleEdit() { 
        setEditMode(true); 
    }
    
    @FXML 
    private void handleCancel() { 
        usernameField.setText(savedUsername);
        emailField.setText(savedEmail);
        nameField.setText(savedName);
        setEditMode(false); 
    }

    private void setEditMode(boolean editing) {
        usernameDisplay.setVisible(!editing);
        usernameDisplay.setManaged(!editing);
        emailDisplay.setVisible(!editing);
        emailDisplay.setManaged(!editing);
        nameDisplay.setVisible(!editing);
        nameDisplay.setManaged(!editing);

        usernameField.setVisible(editing);
        usernameField.setManaged(editing);
        emailField.setVisible(editing);
        emailField.setManaged(editing);
        nameField.setVisible(editing);
        nameField.setManaged(editing);

        editBtn.setVisible(!editing);
        editBtn.setManaged(!editing);
        cancelBtn.setVisible(editing);
        cancelBtn.setManaged(editing);
        saveBtn.setVisible(editing);
        saveBtn.setManaged(editing);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}