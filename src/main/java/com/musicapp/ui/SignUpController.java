package com.musicapp.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class SignUpController {
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField fullnameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleSignUp(ActionEvent event) {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullname = fullnameField.getText();
        
        if(email.isEmpty() || username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
            System.out.println("Error: Please input again !");
            return;
        }
        
        System.out.println("New User Registered");
        System.out.println("Fullname: " + fullname + " | Email: " + email + " | Username: " + username);
        goToMainView(event);
    }
    
    @FXML 
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/WelcomeView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.show();
        }
        catch(IOException e) {
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
        }
        catch(IOException e) {
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
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
