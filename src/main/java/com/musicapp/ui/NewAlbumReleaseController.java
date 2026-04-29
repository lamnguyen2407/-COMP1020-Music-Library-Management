package com.musicapp.ui;

import com.musicapp.Main; 
import com.musicapp.model.Album;
import com.musicapp.ui.SongListController.SongItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane; 
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NewAlbumReleaseController implements Initializable {

    @FXML private FlowPane albumContainer;
    @FXML private ImageView headerImageView;
    @FXML private Button addAlbumBtn;
    @FXML private Button deleteAlbumBtn;

    private StackPane contentArea;

    // --- CÁC BIẾN MỚI THÊM CHO CHỨC NĂNG XOÁ ---
    private List<Album> currentAlbums = new ArrayList<>(); 
    private List<Album> selectedForDeletion = new ArrayList<>();
    private boolean isDeleteMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // PHÂN QUYỀN: Ẩn/Hiện nút Add và Delete dựa vào quyền Admin
        if (Main.isAdmin) {
            addAlbumBtn.setVisible(true); addAlbumBtn.setManaged(true);
            deleteAlbumBtn.setVisible(true); deleteAlbumBtn.setManaged(true);
            
            // GẮN SỰ KIỆN CLICK CHO NÚT ADD VÀ DELETE
            addAlbumBtn.setOnAction(event -> openAddAlbumModal());
            deleteAlbumBtn.setOnAction(event -> toggleDeleteMode()); // Gắn sự kiện xoá
            
        } else {
            addAlbumBtn.setVisible(false); addAlbumBtn.setManaged(false);
            deleteAlbumBtn.setVisible(false); deleteAlbumBtn.setManaged(false);
        }

        // TẠO 4 ALBUM GIẢ ĐỂ TEST GIAO DIỆN VÀ LƯU VÀO currentAlbums
        currentAlbums.add(new Album("a1", "1989 (Taylor's Version)", "Taylor Swift", 2023, "/images/Taylor_Swift_1989.png", "Pop"));
        currentAlbums.add(new Album("a2", "Divide", "Ed Sheeran", 2017, "/images/shapeofyou.jpg", "Pop"));
        currentAlbums.add(new Album("a3", "Midnights", "Taylor Swift", 2022, "", "Pop"));
        currentAlbums.add(new Album("a4", "Starboy", "The Weeknd", 2016, "đường_dẫn_sai.png", "R&B"));

        displayAlbums(currentAlbums);
    }

    // --- HÀM XỬ LÝ BẬT/TẮT CHẾ ĐỘ XOÁ ---
    private void toggleDeleteMode() {
        if (!isDeleteMode) {
            // 1. BẬT CHẾ ĐỘ XOÁ
            isDeleteMode = true;
            selectedForDeletion.clear();
            
            // Đổi style nút Delete cho nổi bật (Màu đỏ)
            deleteAlbumBtn.setText("CONFIRM DELETE");
            deleteAlbumBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 35 10 35; -fx-background-radius: 25; -fx-cursor: hand;");
            
            // Load lại UI để thẻ album thay đổi click event
            displayAlbums(currentAlbums); 
        } else {
            // 2. TẮT CHẾ ĐỘ XOÁ & THỰC HIỆN XOÁ
            isDeleteMode = false;
            
            if (!selectedForDeletion.isEmpty()) {
                // Xoá khỏi danh sách hiển thị
                currentAlbums.removeAll(selectedForDeletion);
                
                // LƯU Ý: NẾU BẠN CÓ DATABASE, GỌI HÀM XOÁ Ở ĐÂY
                // Ví dụ: MusicService.deleteAlbums(selectedForDeletion);
                
                System.out.println("Đã xoá " + selectedForDeletion.size() + " albums.");
            }
            
            selectedForDeletion.clear();
            
            // Trả lại style ban đầu cho nút Delete
            deleteAlbumBtn.setText("DELETE");
            deleteAlbumBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #2C1810; -fx-border-radius: 25; -fx-text-fill: #2C1810; -fx-font-weight: bold; -fx-padding: 10 35 10 35; -fx-border-width: 2; -fx-cursor: hand;");
            
            // Load lại UI bình thường
            displayAlbums(currentAlbums);
        }
    }

 // HÀM MỞ CỬA SỔ ADD ALBUM MODAL
    private void openAddAlbumModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAlbumModal.fxml"));
            Parent root = loader.load();

            // LẤY CONTROLLER CỦA CỬA SỔ MODAL
            AddAlbumModalController modalController = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Album");
            modalStage.setScene(new Scene(root));
            
            // Thiết lập chế độ Modal: Bắt buộc người dùng thao tác xong mới được click lại về cửa sổ chính
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            // Gắn cửa sổ cha là giao diện hiện tại
            if (addAlbumBtn.getScene() != null) {
                modalStage.initOwner(addAlbumBtn.getScene().getWindow());
            }

            // DỪNG CODE TẠI ĐÂY VÀ CHỜ: Hiển thị cửa sổ và chờ cho đến khi nó bị đóng (ấn Save hoặc Cancel)
            modalStage.showAndWait();
            
            // SAU KHI MODAL ĐÓNG, CODE SẼ CHẠY TIẾP XUỐNG ĐÂY
            // Lấy album vừa được tạo từ Modal
            Album newlyCreatedAlbum = modalController.getNewAlbum();
            
            // Nếu người dùng ấn Save (album không bị null)
            if (newlyCreatedAlbum != null) {
                // Thêm vào danh sách hiển thị hiện tại
                currentAlbums.add(newlyCreatedAlbum);
                
                // Vẽ lại giao diện với danh sách mới
                displayAlbums(currentAlbums);
                
                System.out.println("Đã thêm album mới vào giao diện: " + newlyCreatedAlbum.getTitle());
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi mở cửa sổ AddAlbumModal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void displayAlbums(List<Album> albumsFromDatabase) {
        if (albumContainer == null) {
            System.err.println("Lỗi: Không tìm thấy albumContainer. Kiểm tra lại fx:id trong file FXML!");
            return;
        }
        
        albumContainer.getChildren().clear();
        for (Album album : albumsFromDatabase) {
            try {
                Node albumCard = createAlbumCard(album);
                albumContainer.getChildren().add(albumCard);
            } catch (Exception e) {
                System.err.println("Lỗi khi vẽ card cho album: " + album.getTitle());
            }
        }
    }

    private Node createAlbumCard(Album album) {
        VBox card = new VBox(10); 
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160); 
        
        String defaultStyle = "-fx-background-color: transparent; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #EFEFEF; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);";
        String selectedDeleteStyle = "-fx-background-color: #FFEBEE; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #D32F2F; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand; -fx-opacity: 0.8;";

        // Thiết lập giao diện tùy thuộc vào chế độ (Normal hay Delete mode)
        if (isDeleteMode && selectedForDeletion.contains(album)) {
            card.setStyle(selectedDeleteStyle);
        } else {
            card.setStyle(defaultStyle);
        }

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(140, 140);
        imageContainer.setMinSize(140, 140);
        imageContainer.setMaxSize(140, 140);
        imageContainer.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 8;");
        
        Label placeholderIcon = new Label("🖼️"); 
        placeholderIcon.setStyle("-fx-text-fill: #A0A0A0; -fx-font-size: 40px;");
        imageContainer.getChildren().add(placeholderIcon);

        ImageView coverImage = new ImageView();
        coverImage.setFitWidth(140);
        coverImage.setFitHeight(140);
        coverImage.setPreserveRatio(false); 
        
        Rectangle clip = new Rectangle(140, 140);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        coverImage.setClip(clip);

        boolean hasImage = false;
        if (album.imageURL() != null && !album.imageURL().trim().isEmpty()) {
            try {
                URL imageURL = getClass().getResource(album.imageURL());
                if (imageURL != null) {
                    coverImage.setImage(new Image(imageURL.toExternalForm()));
                    hasImage = true;
                }
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh: " + album.imageURL());
            }
        }
        
        if (hasImage) {
            imageContainer.getChildren().add(coverImage);
        }

        VBox textContainer = new VBox(3);
        textContainer.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(album.getTitle());
        titleLabel.setStyle("-fx-text-fill: #1A1A1A; -fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setPrefWidth(140);
        titleLabel.setAlignment(Pos.CENTER);

        Label artistLabel = new Label(album.getArtist() + " • " + album.releaseYear());
        artistLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        textContainer.getChildren().addAll(titleLabel, artistLabel);
        card.getChildren().addAll(imageContainer, textContainer);

        // XỬ LÝ SỰ KIỆN CLICK (Phân nhánh dựa vào chế độ hiện tại)
        if (isDeleteMode) {
            // TRONG CHẾ ĐỘ XOÁ: Click để thêm/bớt khỏi danh sách xoá
            card.setOnMouseClicked(e -> {
                if (selectedForDeletion.contains(album)) {
                    selectedForDeletion.remove(album);
                    card.setStyle(defaultStyle);
                } else {
                    selectedForDeletion.add(album);
                    card.setStyle(selectedDeleteStyle);
                }
            });
        } else {
            // TRONG CHẾ ĐỘ BÌNH THƯỜNG: Có hiệu ứng hover và click để xem chi tiết
            card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
            card.setOnMouseExited(e -> card.setStyle(defaultStyle));
            card.setOnMouseClicked(event -> loadAlbumDetailView(album));
        }

        return card;
    }

    private void loadAlbumDetailView(Album selectedAlbum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();

            SongListController ctrl = loader.getController();

            ObservableList<SongItem> albumSongs = FXCollections.observableArrayList();
            // Đảm bảo MusicService.getGlobalLibrary() hoạt động đúng ở dự án của bạn
            if(MusicService.getGlobalLibrary() != null) {
                for (SongItem song : MusicService.getGlobalLibrary()) {
                    if (song.artist.equals(selectedAlbum.getArtist())) { 
                        albumSongs.add(song);
                    }
                }
            }

            ctrl.setData(
                    selectedAlbum.getTitle(), 
                    selectedAlbum.getArtist(), 
                    "Album detail view", 
                    selectedAlbum.imageURL(), 
                    selectedAlbum.releaseYear(), 
                    selectedAlbum.getGenre(),   // Lưu ý: Nếu class Album của bạn không có hàm getGenre() thì hãy đổi thành selectedAlbum.genre() tuỳ vào code của bạn
                    albumSongs
                );
            if (contentArea == null && albumContainer.getScene() != null) {
                contentArea = (StackPane) albumContainer.getScene().lookup("#contentArea");
            }

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}