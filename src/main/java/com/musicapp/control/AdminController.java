package com.musicapp.control;
import com.musicapp.model.Admin;
import com.musicapp.service.LibraryManager;
public class AdminController {
	private Admin currentAdmin;
	private LibraryManager libraryManager;
	public AdminController(Admin admin, LibraryManager manager) {
		this.currentAdmin = admin;
		this.libraryManager = manager;
	}
	public void handleAddSong(Song data) {
		
	}
	public void handleDeleteSong(Song id) {
		
	}
}
