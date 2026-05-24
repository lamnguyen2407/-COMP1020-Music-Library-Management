package com.musicapp.ui;

import java.io.IOException;

import com.musicapp.model.SessionManager;
import com.musicapp.model.User;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.RegisterCallback;

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

        if(username.equals("admin") || username.equals("admin1")) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed", "Username is already taken");
            return;
        }
        
        // Vô hiệu hóa nút Đăng ký để tránh user click 2 lần liên tục
        Node source = (Node) event.getSource();
        source.setDisable(true);

        // GIAO VIỆC CHO SERVICE XỬ LÝ DATABASE
        DatabaseManager.getInstance().getService().registerNewUser(email, username, password, fullname, new RegisterCallback() {
            @Override
            public void onSuccess(User user) {
                Platform.runLater(() -> {
                    SessionManager.currentUser = user;
                    SessionManager.isAdmin = false;
                    goToMainView(event);
                });
            }

            @Override
            public void onError(String errorMessage) {
                Platform.runLater(() -> {
                    source.setDisable(false); // Bật lại nút nếu lỗi
                    showAlert(Alert.AlertType.WARNING, "Registration Failed", errorMessage);
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