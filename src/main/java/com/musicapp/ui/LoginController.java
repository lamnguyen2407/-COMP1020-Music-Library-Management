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
    
    private boolean isLoggingIn = false;
     
    @FXML
    public void handleLogin(ActionEvent event) {
        // 2. Nếu đang trong quá trình login thì thoát luôn, không chạy dòng dưới
        if (isLoggingIn) return; 

        String identifier = emailField.getText().trim();
        String password = passwordField.getText();

        if(identifier.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Điền thông tin đã chứ!");
            return;
        }

        // 3. Bắt đầu xử lý: Khóa trạng thái lại
        isLoggingIn = true; 
        System.out.println("Checking credentials on Firebase...");
        
        // Vô hiệu hóa nút bấm để user khỏi "spam" click
        Node source = (Node) event.getSource();
        source.setDisable(true);

        DatabaseManager.getInstance().getService().authenticateUser(identifier, password, new LoginCallback() {
            @Override 
            public void onSuccess(User user, String role) {
                // Đăng nhập xong thì không cần reset vì đã chuyển trang
                SessionManager.currentUser = user;
                SessionManager.isAdmin = "admin".equalsIgnoreCase(role);
                Platform.runLater(() -> goToMainView(event));
            }
            
            @Override 
            public void onError(String errorMessage) {
                // QUAN TRỌNG: Nếu lỗi thì phải mở khóa để user thử lại
                isLoggingIn = false; 
                Platform.runLater(() -> {
                    source.setDisable(false); // Mở lại nút
                    System.out.println("Login Failed: " + errorMessage);
                    
                    // HIỂN THỊ ALERT CHO USER BIẾT
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
