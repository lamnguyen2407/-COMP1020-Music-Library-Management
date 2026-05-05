package com.musicapp.ui;
import com.musicapp.service.LoginCallback;
import com.musicapp.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

import com.musicapp.model.SessionManager;
import com.musicapp.service.DatabaseManager;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String identifier = emailField.getText().trim();
        String password = passwordField.getText();
        if(identifier.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Please login again !");
            return;
        }
        
        System.out.println("Checking credentials on Firebase...");
        
        DatabaseManager.getInstance().getService().authenticateUser(identifier, password, new LoginCallback() {
        	@Override 
        	public void onSuccess(User user, String role) {
        		SessionManager.currentUser = user;
        		SessionManager.isAdmin = "admin".equals(role);
        		Platform.runLater(() -> {
        			System.out.println("Login successful! Welcome " + user.getName());
        			goToMainView(event);
        		});
        	}
        	
        	@Override 
        	public void onError(String errorMessage) {
        		Platform.runLater(() -> {
        			System.out.println("Login Failed " + errorMessage);
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
