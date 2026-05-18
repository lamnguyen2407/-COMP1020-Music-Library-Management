package com.musicapp.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import com.musicapp.model.ListenerUser;
import com.musicapp.model.SessionManager;
import com.musicapp.service.DatabaseManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField fullnameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleSignUp(ActionEvent event) {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String fullname = fullnameField.getText().trim();
        
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill out all fields to register.");
            return;
        }

        String newUserId = UUID.randomUUID().toString();
        ListenerUser newUser = new ListenerUser(newUserId, fullname, email, username, password);
        
        try {
            System.out.println("Saving new profile to database...");
            DatabaseManager.getInstance().getService().saveUser(newUser);
            System.out.println("User profile successfully synchronized with database.");
            
            // Set session states to prevent null pointer crashes in downstream view contexts
            SessionManager.currentUser = newUser;
            SessionManager.isAdmin = false;
            
        } catch (Exception e) {
            System.err.println("Database transaction failure: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Could not synchronize credentials with network node.");
            return;
        }
        
        goToMainView(event);
    }
    
    @FXML 
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/WelcomeView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToMainView(ActionEvent event) {
        System.out.println("Switching to Main Dashboard");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}