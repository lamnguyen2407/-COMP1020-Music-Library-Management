package com.musicapp.ui;

import com.musicapp.model.Album; // NHỚ IMPORT CLASS ALBUM
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.UUID;

public class AddAlbumModalController {

    @FXML private TextField albumTitleField;
    @FXML private TextField artistField;
    @FXML private TextField releaseYearField;
    @FXML private TextField genreField;
    @FXML private TextField imageUrlField;

    // Biến để lưu trữ album mới được tạo
    private Album newAlbum = null;

    @FXML
    private void onSave() {
        try {
            // 1. Thu thập dữ liệu từ UI
            String title = albumTitleField.getText();
            String artist = artistField.getText();
            int year = Integer.parseInt(releaseYearField.getText());
            String genre = genreField.getText();
            String image = imageUrlField.getText();

            // Tạo một ID ngẫu nhiên cho album (Hoặc lấy từ DB sau này)
            String id = UUID.randomUUID().toString();

            // 2. Tạo đối tượng Album mới và gán vào biến newAlbum
            // Lưu ý: Thứ tự tham số phụ thuộc vào constructor của class Album của bạn
            newAlbum = new Album(id, title, artist, year, image, genre);

            System.out.println("--- ADMIN ACTION: ADDING NEW ALBUM ---");
            System.out.println("Đã lưu tạm Album: " + newAlbum.getTitle());

            // 3. Xử lý lưu dữ liệu Database ở đây (nếu có)
            // Ví dụ: Database.save(newAlbum);

            // Đóng Modal sau khi thêm thành công
            closeModal();
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Release Year must be a valid number!");
            // Bạn có thể thêm code hiện thông báo lỗi (Alert) ở đây cho Admin biết
        }
    }

    @FXML
    private void onCancel() {
        newAlbum = null; // Huỷ bỏ thì không trả về album nào
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) albumTitleField.getScene().getWindow();
        stage.close();
    }

    // Hàm GETTER để NewAlbumReleaseController có thể lấy dữ liệu album vừa tạo
    public Album getNewAlbum() {
        return newAlbum;
    }
}