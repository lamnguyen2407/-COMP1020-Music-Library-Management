package com.musicapp.ui;

import com.musicapp.service.LoginCallback;
import com.musicapp.model.*;
import com.musicapp.service.DatabaseManager;
import com.musicapp.model.SessionManager;

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
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    
    private boolean isLoggingIn = false;
     
    @FXML
    public void handleLogin(ActionEvent event) {
        if (isLoggingIn) return; 

        String identifier = emailField.getText().trim();
        String password = passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            System.out.println("Validation Error: Fields cannot be empty.");
            return;
        }

        isLoggingIn = true; 
        System.out.println("Checking credentials on Firebase...");
        
        Node source = (Node) event.getSource();
        source.setDisable(true);

        DatabaseManager.getInstance().getService().authenticateUser(identifier, password, new LoginCallback() {
            @Override 
            public void onSuccess(User user, String role) {
                SessionManager.currentUser = user;
                SessionManager.isAdmin = "admin".equalsIgnoreCase(role);
                Platform.runLater(() -> goToMainView(event));
            }
            
            @Override 
            public void onError(String errorMessage) {
                isLoggingIn = false; 
                Platform.runLater(() -> {
                    source.setDisable(false); 
                    System.err.println("Login Failed: " + errorMessage);
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText(null);
                    alert.setContentText(errorMessage);
                    alert.showAndWait();
                });
            }
        });
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
    public void goToSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUpView.fxml"));
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
}