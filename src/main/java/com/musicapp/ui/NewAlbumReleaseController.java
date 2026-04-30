package com.musicapp.ui;

import com.musicapp.Main; 
import com.musicapp.model.Album;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.ui.SongListController.SongItem;
import javafx.application.Platform;
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

public class NewAlbumReleaseController implements Initializable, MainViewController.MainViewAware {

    @FXML private FlowPane albumContainer;
    @FXML private ImageView headerImageView;
    @FXML private Button addAlbumBtn;
    @FXML private Button deleteAlbumBtn;

    private StackPane contentArea;
    private MainViewController mainController; 

    private List<Album> currentAlbums = new ArrayList<>(); 
    private List<Album> selectedForDeletion = new ArrayList<>();
    private boolean isDeleteMode = false;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Main.isAdmin) {
            addAlbumBtn.setVisible(true); addAlbumBtn.setManaged(true);
            deleteAlbumBtn.setVisible(true); deleteAlbumBtn.setManaged(true);
            
            addAlbumBtn.setOnAction(event -> openAddAlbumModal());
            deleteAlbumBtn.setOnAction(event -> toggleDeleteMode()); 
        } else {
            addAlbumBtn.setVisible(false); addAlbumBtn.setManaged(false);
            deleteAlbumBtn.setVisible(false); deleteAlbumBtn.setManaged(false);
        }

        refreshData();
    }

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Album> dbAlbums = DatabaseManager.getInstance().getService().fetchAlbums();
                Platform.runLater(() -> {
                    currentAlbums.clear();
                    currentAlbums.addAll(dbAlbums);
                    displayAlbums(currentAlbums);
                    System.out.println("✅ Đã load " + currentAlbums.size() + " albums từ Firebase.");
                });
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi load Albums từ Firebase: " + e.getMessage());
            }
        }).start();
    }

    private void toggleDeleteMode() {
        if (!isDeleteMode) {
            isDeleteMode = true;
            selectedForDeletion.clear();
            deleteAlbumBtn.setText("CONFIRM DELETE");
            deleteAlbumBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 35 10 35; -fx-background-radius: 25; -fx-cursor: hand;");
            displayAlbums(currentAlbums); 
        } else {
            if (!selectedForDeletion.isEmpty()) {
                // THỰC HIỆN XÓA THẬT TRÊN FIREBASE
                new Thread(() -> {
                    try {
                        for (Album album : selectedForDeletion) {
                            DatabaseManager.getInstance().getService().deleteAlbum(album.getAlbumId());
                        }
                        Platform.runLater(() -> {
                            currentAlbums.removeAll(selectedForDeletion);
                            selectedForDeletion.clear();
                            finishDeleteMode();
                        });
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi xóa album: " + e.getMessage());
                    }
                }).start();
            } else {
                finishDeleteMode();
            }
        }
    }

    private void finishDeleteMode() {
        isDeleteMode = false;
        deleteAlbumBtn.setText("DELETE");
        deleteAlbumBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #2C1810; -fx-border-radius: 25; -fx-text-fill: #2C1810; -fx-font-weight: bold; -fx-padding: 10 35 10 35; -fx-border-width: 2; -fx-cursor: hand;");
        displayAlbums(currentAlbums);
    }

    private void openAddAlbumModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAlbumModal.fxml"));
            Parent root = loader.load();
            
            // Lấy controller của modal để hứng dữ liệu
            AddAlbumModalController modalController = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Album");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            if (addAlbumBtn.getScene() != null) modalStage.initOwner(addAlbumBtn.getScene().getWindow());
            
            // Mở bảng lên và code sẽ dừng ở đây chờ bạn bấm Save/Cancel
            modalStage.showAndWait();
            
            // ---- XỬ LÝ LÀM MỚI GIAO DIỆN NGAY LẬP TỨC ----
            Album createdAlbum = modalController.getNewAlbum();
            
            if (createdAlbum != null) {
                // Nếu có Album mới tạo -> Chèn thẳng vào giao diện mà không cần chờ mạng!
                currentAlbums.add(createdAlbum);
                
                // Ép giao diện vẽ lại danh sách Album ngay lập tức
                Platform.runLater(() -> {
                    displayAlbums(currentAlbums);
                });
                
                System.out.println("✅ Đã ép UI hiển thị ngay Album: " + createdAlbum.getTitle());
            } else {
                // Trường hợp người dùng bấm Cancel, không làm gì cả
                System.out.println("Đã đóng bảng, không có Album nào được tạo.");
            }
            
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    public void displayAlbums(List<Album> albumsFromDatabase) {
        if (albumContainer == null) return;
        albumContainer.getChildren().clear();
        for (Album album : albumsFromDatabase) {
            albumContainer.getChildren().add(createAlbumCard(album));
        }
    }

    private Node createAlbumCard(Album album) {
        VBox card = new VBox(10); 
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160); 
        
        String defaultStyle = "-fx-background-color: transparent; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;";
        String selectedDeleteStyle = "-fx-background-color: #FFEBEE; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #D32F2F; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;";

        card.setStyle(isDeleteMode && selectedForDeletion.contains(album) ? selectedDeleteStyle : defaultStyle);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(140, 140);
        imageContainer.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 8;");
        
        ImageView coverImage = new ImageView();
        coverImage.setFitWidth(140); coverImage.setFitHeight(140);
        Rectangle clip = new Rectangle(140, 140); clip.setArcWidth(16); clip.setArcHeight(16);
        coverImage.setClip(clip);

        if (album.getImageURL() != null && !album.getImageURL().isEmpty()) {
            try {
                String imgUrl = album.getImageURL().trim();
                
                // Kiểm tra xem là link local (trong máy) hay link web online
                if (imgUrl.startsWith("/")) {
                    // Đọc ảnh từ thư mục resources (ví dụ: /images/shapeofyou.jpg)
                    coverImage.setImage(new Image(getClass().getResourceAsStream(imgUrl)));
                } else {
                    // Đọc ảnh từ web (http...)
                    coverImage.setImage(new Image(imgUrl, true));
                }
                
                imageContainer.getChildren().add(coverImage);
            } catch (Exception e) {
                // NẾU LỖI: Chỉ in ra console chứ không làm sập giao diện!
                System.err.println("⚠️ Bỏ qua ảnh lỗi của Album '" + album.getTitle() + "': " + e.getMessage());
            }
        }

        VBox textContainer = new VBox(3); textContainer.setAlignment(Pos.CENTER);
        Label titleLabel = new Label(album.getTitle());
        titleLabel.setStyle("-fx-text-fill: #1A1A1A; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label artistLabel = new Label(album.getArtist() + " • " + album.getReleaseYear());
        artistLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        textContainer.getChildren().addAll(titleLabel, artistLabel);
        card.getChildren().addAll(imageContainer, textContainer);

        if (isDeleteMode) {
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
            card.setOnMouseClicked(event -> loadAlbumDetailView(album));
        }
        return card;
    }

 // Tìm chỗ này trong NewAlbumReleaseController
 // Ví dụ trong hàm xử lý Click vào Album
    private void loadAlbumDetailView(Album selectedAlbum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Parent view = loader.load();
            
            SongListController controller = loader.getController();
            controller.setMainController(this.mainController); // Truyền sếp sang cho thằng con tiếp theo

            // Đổ data (8 tham số chuẩn Cách 1)
            controller.setData(
                selectedAlbum.getAlbumId(),
                selectedAlbum.getTitle(),
                selectedAlbum.getArtist(),
                "Album detail view",
                selectedAlbum.getImageURL(),
                selectedAlbum.getReleaseYear(),
                selectedAlbum.getGenre(),
                selectedAlbum.getSongIdList()
            );

            // DÙNG CÁI NÀY: Gọi qua getter của MainViewController
            mainController.getContentArea().getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}