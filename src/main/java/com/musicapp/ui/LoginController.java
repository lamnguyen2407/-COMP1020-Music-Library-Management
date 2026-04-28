package com.musicapp.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String identifier = emailField.getText();
        String password = passwordField.getText();
        if(identifier.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Please login again !");
            return;
        }
        if(identifier.contains("@gmail")) {
            System.out.println("Attempting to login using email " + identifier);
        }
        else {
            System.out.println("Attempting to login using username " + identifier);
        }
        
        System.out.println("Dummy User Authorized successfully");
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
    public void goToSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUpView.fxml"));
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
