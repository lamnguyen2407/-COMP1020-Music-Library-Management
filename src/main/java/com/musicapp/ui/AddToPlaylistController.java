package com.musicapp.ui;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.PlaylistManager;
import com.musicapp.model.SessionManager; // Import cho gọn
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.List;

public class AddToPlaylistController {
    @FXML private ListView<Playlist> playlistListView;
    @FXML private Button addBtn;

    private Song songToAdd;
    private PlaylistManager playlistManager;
    private String currentUserId; // Chỉ khai báo, không gán cứng ở đây

    @FXML
    public void initialize() {
        // Custom hiển thị tên Playlist
        playlistListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        
        // Chỉ cho bấm Add khi đã chọn 1 dòng
        addBtn.disableProperty().bind(playlistListView.getSelectionModel().selectedItemProperty().isNull());
    }

    public void initData(Song song) {
        this.songToAdd = song;

        // Lấy User ID an toàn từ Session
        if (SessionManager.currentUser != null) {
            this.currentUserId = SessionManager.currentUser.getUserId();
        } else {
            // Backup nếu session lỗi (để app không crash)
            this.currentUserId = "tester01"; 
        }

        this.playlistManager = new PlaylistManager(currentUserId);
        
        // Load playlist từ Firebase
        loadUserPlaylists();
    }

    private void loadUserPlaylists() {
        // Nên chạy cái này để đảm bảo lấy list mới nhất
        List<Playlist> userPlaylists = DatabaseManager.getInstance().getService().fetchUserPlaylists(currentUserId);
        
        if (userPlaylists != null) {
            playlistListView.setItems(FXCollections.observableArrayList(userPlaylists));
        }
    }

    @FXML
    private void handleAdd() {
        Playlist selected = playlistListView.getSelectionModel().getSelectedItem();
        
        if (selected != null && songToAdd != null) {
            // Kiểm tra xem ID bài hát có chuẩn không
            System.out.println("Adding song: " + songToAdd.getSongId() + " to playlist: " + selected.getPlaylistId());
            
            // Thực hiện add thông qua Service/Manager
            DatabaseManager.getInstance().getService().addSongToPlaylist(selected.getPlaylistId(), songToAdd.getSongId());
            
            closeModal();
        } else {
            System.err.println("❌ Lỗi: Chưa chọn playlist hoặc dữ liệu bài hát bị trống!");
        }
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        if (playlistListView.getScene() != null) {
            Stage stage = (Stage) playlistListView.getScene().getWindow();
            if (stage != null) stage.close();
        }
    }
}