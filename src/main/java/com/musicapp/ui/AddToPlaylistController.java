package com.musicapp.ui;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.PlaylistManager;
import com.musicapp.model.SessionManager; 
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
    private String currentUserId; 

    @FXML
    public void initialize() {
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
        
        addBtn.disableProperty().bind(playlistListView.getSelectionModel().selectedItemProperty().isNull());
    }

    public void initData(Song song) {
        this.songToAdd = song;

        if (SessionManager.currentUser != null) {
            this.currentUserId = SessionManager.currentUser.getUserId();
        } else {
            this.currentUserId = "tester01"; 
        }

        this.playlistManager = new PlaylistManager(currentUserId);
        loadUserPlaylists();
    }

    private void loadUserPlaylists() {
        List<Playlist> userPlaylists = DatabaseManager.getInstance().getService().fetchUserPlaylists(currentUserId);
        if (userPlaylists != null) {
            playlistListView.setItems(FXCollections.observableArrayList(userPlaylists));
        }
    }

    @FXML
    private void handleAdd() {
        Playlist selected = playlistListView.getSelectionModel().getSelectedItem();
        
        if (selected != null && songToAdd != null) {
            System.out.println("Adding song: " + songToAdd.getSongId() + " to playlist: " + selected.getPlaylistId());
            DatabaseManager.getInstance().getService().addSongToPlaylist(selected.getPlaylistId(), songToAdd.getSongId());
            closeModal();
        } else {
            System.err.println("Error: Playlist selection or song payload target is missing.");
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