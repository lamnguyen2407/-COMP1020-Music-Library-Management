package com.musicapp.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for WelcomeView.fxml
 * Handles the initial entry points into the application.
 */
public class WelcomeViewController {

    /**
     * Handles the "Try logging in" or "Start" button action.
     * Directs the user to the Main application shell.
     */
    @FXML
    private void handleStart(ActionEvent event) {
        navigateToMain(event);
    }

    /**
     * Handles the "Sign Up" button action.
     * For Interim Report, this also directs to the Main shell.
     */
    @FXML
    private void handleSignUp(ActionEvent event) {
        System.out.println("[Welcome] Sign Up clicked - Redirecting to MainView");
        navigateToMain(event);
    }

    /**
     * Core logic to switch from Welcome Scene to Main Scene.
     */
    private void navigateToMain(ActionEvent event) {
        try {
            // FIX: Ensure the path matches your resource root folder
            URL resource = getClass().getResource("/MainView.fxml");
            if (resource == null) {
                System.err.println("[Welcome] CRITICAL: MainView.fxml not found!");
                return;
            }

            // Load the Main UI
            FXMLLoader loader = new FXMLLoader(resource);
            Parent mainView = loader.load();
            
            // Get the current window (Stage) from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set the scene to standard desktop resolution
            Scene mainScene = new Scene(mainView, 1280, 800);
            stage.setScene(mainScene);
            stage.setTitle("Music Library Management");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            System.err.println("[Welcome] Failed to transition to MainView");
            e.printStackTrace();
        }
    }
}