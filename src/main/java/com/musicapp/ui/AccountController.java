package com.musicapp.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    // ── FXML bindings ──────────────────────────────────────────────────────────
    @FXML private Label     roleLabel;

    @FXML private Label     usernameDisplay;
    @FXML private TextField usernameField;

    @FXML private Label     emailDisplay;
    @FXML private TextField emailField;

    @FXML private Label     nameDisplay;
    @FXML private TextField nameField;

    @FXML private Button    editBtn;
    @FXML private Button    cancelBtn;
    @FXML private Button    saveBtn;

    // ── Snapshot for cancel ────────────────────────────────────────────────────
    private String savedUsername;
    private String savedEmail;
    private String savedName;

    // ══════════════════════════════════════════════════════════════════════════
    // Initialize
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
    }

    // ── Load user data ─────────────────────────────────────────────────────────
    private void loadUserData() {
        // TODO: Replace with real Firebase fetch
        // Example:
        //   User user = FirebaseService.getCurrentUser();
        //   setUserData(user.getUserId(), user.getEmail(), user.getName(), "Listener");

        // Placeholder data
        setUserData("Username", "abc@gmail.com", "Rachelly Proovra", "Listener");
    }

    public void setUserData(String username, String email, String name, String role) {
        savedUsername = username;
        savedEmail    = email;
        savedName     = name;

        roleLabel.setText(role);
        usernameDisplay.setText(username);
        emailDisplay.setText(email);
        nameDisplay.setText(name);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Handlers
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleEdit() {
        // Populate fields with current values
        usernameField.setText(savedUsername);
        emailField.setText(savedEmail);
        nameField.setText(savedName);

        setEditMode(true);
    }

    @FXML
    private void handleCancel() {
        // Discard changes — restore display labels from snapshot
        usernameDisplay.setText(savedUsername);
        emailDisplay.setText(savedEmail);
        nameDisplay.setText(savedName);

        setEditMode(false);
    }

    @FXML
    private void handleSave() {
        String newUsername = usernameField.getText().trim();
        String newEmail    = emailField.getText().trim();
        String newName     = nameField.getText().trim();

        // Keep old value if field left empty
        if (!newUsername.isEmpty()) savedUsername = newUsername;
        if (!newEmail.isEmpty())    savedEmail    = newEmail;
        if (!newName.isEmpty())     savedName     = newName;

        // Update display labels
        usernameDisplay.setText(savedUsername);
        emailDisplay.setText(savedEmail);
        nameDisplay.setText(savedName);

        // TODO: Push updated data to Firebase
        // Example:
        //   FirebaseService.updateUser(savedUsername, savedEmail, savedName);

        setEditMode(false);
    }

    // ── Toggle between view / edit mode ───────────────────────────────────────
    private void setEditMode(boolean editing) {
        // Display labels
        usernameDisplay.setVisible(!editing);
        usernameDisplay.setManaged(!editing);
        emailDisplay.setVisible(!editing);
        emailDisplay.setManaged(!editing);
        nameDisplay.setVisible(!editing);
        nameDisplay.setManaged(!editing);

        // Edit fields
        usernameField.setVisible(editing);
        usernameField.setManaged(editing);
        emailField.setVisible(editing);
        emailField.setManaged(editing);
        nameField.setVisible(editing);
        nameField.setManaged(editing);

        // Buttons
        editBtn.setVisible(!editing);
        editBtn.setManaged(!editing);
        cancelBtn.setVisible(editing);
        cancelBtn.setManaged(editing);
        saveBtn.setVisible(editing);
        saveBtn.setManaged(editing);
    }
}