package com.musicapp.model;

import java.util.*;

public class ListenerUser extends User {
	private List<String> playlistIds;

	public ListenerUser(String userId, String email, String name, String password) {
		super(userId, email, name, password);
		this.playlistIds = new ArrayList<>();
	}

	public List<String> getPlaylistIds() {
		return playlistIds;
	}

	public void addPlaylistId(String playlistId) {
		playlistIds.add(playlistId);
	}

	public void removePlaylistId(String playlistId) {
		playlistIds.remove(playlistId);
	}
}
