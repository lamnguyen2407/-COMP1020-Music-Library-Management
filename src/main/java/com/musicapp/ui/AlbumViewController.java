package com.musicapp.ui;

import com.musicapp.model.Song;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class AlbumViewController {

	// ── FXML bindings ──────────────────────────────────────────────────────────
	@FXML
	private ImageView albumArtView;
	@FXML
	private Label albumNameLabel;
	@FXML
	private Label artistLabel;
	@FXML
	private Label metaLabel;
	@FXML
	private Button playBtn;
	@FXML
	private VBox songListContainer;

	// ── State ──────────────────────────────────────────────────────────────────
	private List<Song> songs;
	private MainViewController mainController;

	// ── Setter: reference to MainViewController (for showing player bar) ───────
	public void setMainController(MainViewController mainController) {
		this.mainController = mainController;
	}

	// ── Setter: album data ─────────────────────────────────────────────────────
	public void setAlbumData(String albumName, String artist, String genre, int year, String imageURL,
			List<Song> songs) {
		this.songs = songs;

		albumNameLabel.setText(albumName);
		artistLabel.setText(artist);
		metaLabel.setText(genre + " • " + year);

		if (imageURL != null && !imageURL.isEmpty()) {
			try {
				albumArtView.setImage(new Image(imageURL, true));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		buildSongRows();
	}

	// ── Build song rows dynamically ────────────────────────────────────────────
	private void buildSongRows() {
		songListContainer.getChildren().clear();

		for (int i = 0; i < songs.size(); i++) {
			Song song = songs.get(i);
			HBox row = buildRow(song, i);
			songListContainer.getChildren().add(row);
		}
	}

	private HBox buildRow(Song song, int index) {
		HBox row = new HBox(12);
		row.setAlignment(Pos.CENTER_LEFT);
		row.setStyle(
				"-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

		// ── Title (clickable) ──
		Label titleLabel = new Label(song.getTitle());
		titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");
		titleLabel.setOnMouseClicked(e -> handleSongClick(song, index));

		// ── Heart button ──
		Button heartBtn = new Button("♡");
		if (com.musicapp.model.SessionManager.isAdmin) {
		    heartBtn.setVisible(false);
		    heartBtn.setManaged(false);
		}
		heartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #c07840; "
				+ "-fx-font-size: 14px; -fx-cursor: hand; -fx-border-width: 0;");
		heartBtn.setOnAction(e -> handleAddToFavorites(song));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// ── Plus button with dropdown ──
		Button plusBtn = new Button("+");
		plusBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #c07840; "
				+ "-fx-font-size: 16px; -fx-cursor: hand; -fx-border-width: 0;");

		ContextMenu dropdown = new ContextMenu();
		MenuItem addToPlaylist = new MenuItem("Add to playlist");
		addToPlaylist.setOnAction(e -> handleAddToPlaylist(song));
		dropdown.getItems().add(addToPlaylist);

		plusBtn.setOnAction(e -> dropdown.show(plusBtn, plusBtn.localToScreen(0, plusBtn.getHeight()).getX(),
				plusBtn.localToScreen(0, plusBtn.getHeight()).getY()));

		// ── Duration ──
		Label durationLabel = new Label(formatDuration(song.getDuration()));
		durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-min-width: 40;");

		row.getChildren().addAll(titleLabel, heartBtn, spacer, plusBtn, durationLabel);

		// Hover effect
		row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; "
				+ "-fx-border-width: 0 0 1 0; -fx-background-color: #f5efe9; -fx-cursor: hand;"));
		row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10 40 10 40; -fx-border-color: #f0ebe5; "
				+ "-fx-border-width: 0 0 1 0; -fx-cursor: hand;"));

		return row;
	}

	// ── Handlers ──────────────────────────────────────────────────────────────

	@FXML
	private void handlePlay() {
		if (songs == null || songs.isEmpty())
			return;
		playSong(songs.get(0), 0);
	}

	private void handleSongClick(Song song, int index) {
		playSong(song, index);
	}

	private void playSong(Song song, int index) {
		if (mainController != null) {
			mainController.showPlayerBar(song, songs, index);
		}
	}

	private void handleAddToFavorites(Song song) {
		// TODO: Add song to "Your Favorite Songs" playlist
	}

	private void handleAddToPlaylist(Song song) {
		// TODO: Show playlist picker using Playlist.java
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private String formatDuration(int seconds) {
		int m = seconds / 60;
		int s = seconds % 60;
		return m + ":" + String.format("%02d", s);
	}
}