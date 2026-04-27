package com.musicapp.control;
import com.musicapp.model.*;
import com.musicapp.service.LibraryManager;
public class AdminController {
	private Admin currentAdmin;
	private LibraryManager libraryManager;
	public AdminController(Admin admin, LibraryManager manager) {
		this.currentAdmin = admin;
		this.libraryManager = manager;
	}
	public void handleAddSong(Song data) {
		if(this.currentAdmin == null) {
			System.out.println("Access denied ! You must be an admin to add song.");
			return;
		}
		this.libraryManager.addSong(data);
	}
	public void handleDeleteSong(String id) {
		if(this.currentAdmin == null) {
			System.out.println("Access denied ! You must be an admin to delete song.");
			return;
		}
		this.libraryManager.removeSong(id);
	}
}
