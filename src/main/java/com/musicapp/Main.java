package com.musicapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/WelcomeView.fxml");
        if (fxmlLocation == null) {
            System.err.println("CRITICAL ERROR: MainView.fxml not found in resources!");
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        primaryStage.setTitle("Music Library Management - Testing Mode");
        primaryStage.setScene(new Scene(root, 1280, 800)); 
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}