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

        if(username.equals("admin") || username.equals("admin1")) {
        	showAlert(Alert.AlertType.WARNING, "Registration Failed", "Username is already taken");
            return;
        }
        attemptRegistration(email, username, password, fullname, 0, event);
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
    private int getIntegerKey(String text) {
    	int key = 0, prime = 29;
    	for(int i = 0; i < text.length(); ++i) {
    		key = key * prime + text.charAt(i);
    	}
    	return key & Integer.MAX_VALUE; // avoid exceeding the limitation of Integer datatype
    }
    private void attemptRegistration(String email, String username, String password, String fullname, int i, ActionEvent event) {
        // Check globally if the user name is taken 
        DatabaseManager.getInstance().getService().getDbRef().child("users")
            .orderByChild("name").equalTo(username)
            .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot usernameSnapshot) {
                    if (usernameSnapshot.exists()) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Registration Failed", "Username is already taken."));
                        return;
                    }

                    // If user name is free, check globally if the email is taken 
                    DatabaseManager.getInstance().getService().getDbRef().child("users")
                        .orderByChild("email").equalTo(email)
                        .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot emailSnapshot) {
                                if (emailSnapshot.exists()) {
                                    Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Registration Failed", "Email is already taken."));
                                    return;
                                }

                                // If both text fields are globally unique, proceed with ID generation
                                proceedWithIdGeneration(email, username, password, fullname, i, event);
                            }

                            @Override
                            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", error.getMessage()));
                            }
                        });
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", error.getMessage()));
                }
            });
    }

    // Process new user data
    private void proceedWithIdGeneration(String email, String username, String password, String fullname, int i, ActionEvent event) {
        int keyUser = getIntegerKey(username);
        int keyEmail = getIntegerKey(email);

        int indexUser = ( (keyUser % 997) + i * (991 - (keyUser % 991)) ) % 997;
        int indexEmail = ( (keyEmail % 997) + i * (991 - (keyEmail % 991))) % 997;
        String generatedUserId = String.format("L%03d%03d", Math.abs(indexUser), Math.abs(indexEmail));

        DatabaseManager.getInstance().getService().getDbRef().child("users").child(generatedUserId)
            .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        ListenerUser newUser = new ListenerUser(generatedUserId, fullname, email, username, password);
                        try {
                            DatabaseManager.getInstance().getService().saveUser(newUser);
                            DatabaseManager.getInstance().getService().setSession(newUser.getUserId(), newUser.getRole());
                            Platform.runLater(() -> {
                                SessionManager.currentUser = newUser;
                                SessionManager.isAdmin = false;
                                goToMainView(event);
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to save user data."));
                        }
                    } else {
                        // Hash Collision (Different user entirely landed on same hash indices)
                        proceedWithIdGeneration(email, username, password, fullname, i + 1, event);
                    }
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", error.getMessage()));
                }
            });
    }
}