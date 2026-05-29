package com.musicapp.ui;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import com.musicapp.model.SessionManager; 
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.List;
import java.util.ArrayList;

public class AddToPlaylistController {
    
    @FXML private ListView<Playlist> playlistListView;
    @FXML private Button addBtn;

    private Song songToAdd;
    private MainViewController mainController; 

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
    }

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
        loadUserPlaylists();
    }

    private void loadUserPlaylists() {
        if (mainController != null && mainController.getPlaylistManager() != null) {
            List<Playlist> userPlaylists = mainController.getPlaylistManager().getAllUserPlaylists();
            
            List<Playlist> displayList = new ArrayList<>();
            if (userPlaylists != null) {
                for (Playlist p : userPlaylists) {
                    if (!p.getPlaylistId().startsWith("fav_")) {
                        displayList.add(p);
                    }
                }
            }
            playlistListView.setItems(FXCollections.observableArrayList(displayList));
        }
    }

    @FXML
    private void handleAdd() {
        Playlist selected = playlistListView.getSelectionModel().getSelectedItem();
        
        if (selected != null && songToAdd != null && mainController != null) {
            System.out.println("Adding song: " + songToAdd.getSongId() + " to playlist: " + selected.getPlaylistId());
            
            mainController.getPlaylistManager().addSongToSpecificPlaylist(selected.getPlaylistId(), songToAdd);
            
            if (PlaylistOverviewController.instance != null) {
                PlaylistOverviewController.instance.refreshData();
            }

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