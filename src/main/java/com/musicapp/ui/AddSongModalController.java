package com.musicapp.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSongModalController {

    @FXML private TextField idField, titleField, artistField, genreField, yearField, durationField, audioUrlField, imageUrlField;

    @FXML
    private void onSave() {
        try {
            // 1. Thu thập trọn bộ 8 thuộc tính từ UI
            String id = idField.getText();
            String title = titleField.getText();
            String artist = artistField.getText();
            String genre = genreField.getText();
            int year = Integer.parseInt(yearField.getText());
            int duration = Integer.parseInt(durationField.getText());
            String audio = audioUrlField.getText();
            String image = imageUrlField.getText();

            // 2. LOGIC DEMO: In ra console để thầy cô thấy data đã được xử lý
            System.out.println("--- ADMIN ACTION: ADDING NEW SONG ---");
            System.out.println("ID: " + id);
            System.out.println("Title: " + title);
            System.out.println("Artist: " + artist);
            System.out.println("Genre: " + genre);
            System.out.println("Year: " + year);
            System.out.println("Duration: " + duration + "s");
            System.out.println("Audio: " + audio);
            System.out.println("Image: " + image);
            System.out.println("-------------------------------------");
            
         // ... lấy 8 thuộc tính từ TextField ...
            SongListController.SongItem newSong = new SongListController.SongItem(
                id, title, artist, genre, duration, year, audio, image
            );

            // Lưu vào kho nhạc chung
            MusicService.addSong(newSong);
	            
	         // Refresh màn hình CompactList đang mở
	         // Lấy lại danh sách mới nhất
	         ObservableList<SongListController.SongItem> updatedList = MusicService.getGlobalLibrary();
	
	         // (Cách dễ nhất) Mày đóng cái Modal lại, 
	         // Khi Admin quay ra nhấn "All Albums" hoặc "Latest Song", nó sẽ load lại list mới.
	         closeModal();
	            
            // 3. Sau này mày sẽ gọi Firebase hoặc DB ở đây:
            // Database.save(new Song(id, title, artist, ...));

            closeModal();
        } catch (NumberFormatException e) {
            // Xử lý nếu Admin nhập chữ vào ô Year hoặc Duration
            System.err.println("Error: Year and Duration must be numbers!");
        }
    }

    @FXML
    private void onCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}