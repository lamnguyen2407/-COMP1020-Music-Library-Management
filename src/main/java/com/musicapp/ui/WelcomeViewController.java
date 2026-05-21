package com.musicapp.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class WelcomeViewController {

    @FXML 
    public void handleStart(ActionEvent event) {
        System.out.println("Switching to Login Page...");
        switchScene(event, "/LoginView.fxml");
    }

    @FXML 
    public void goToLogin(ActionEvent event) {
        System.out.println("Switching to Login Page...");
        switchScene(event, "/LoginView.fxml");
    }
    
    @FXML 
    public void goToSignUp(ActionEvent event) {
        System.out.println("Switching to Sign Up page...");
        switchScene(event, "/SignUpView.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            java.net.URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("Routing Exception: Unable to resolve interface configuration at target descriptor: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resourceUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800)); 
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}