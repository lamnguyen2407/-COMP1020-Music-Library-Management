package com.musicapp.ui;

import com.musicapp.Main;
import com.musicapp.service.DatabaseManager;
import com.musicapp.service.FirebaseServiceImpl;
import com.musicapp.model.*;
import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Controller for DiscoveryView.fxml
 * Handles the home screen logic and role-based data display.
 */
public class DiscoveryController implements Initializable, MainViewController.MainViewAware {

    @FXML private StackPane trendingCard1, trendingCard2, trendingCard3, trendingCard4, trendingCard5;
    @FXML private StackPane todaysHitBanner;
    @FXML private StackPane newAlbumCard1, newAlbumCard2, newAlbumCard3, newAlbumCard4, newAlbumCard5;
    @FXML private Label trending1Title, trending2Title, trending3Title, trending4Title, trending5Title;
    @FXML private Label trending1Artist, trending2Artist, trending3Artist, trending4Artist, trending5Artist;    
    @FXML private Label newAlbum1Title, newAlbum2Title, newAlbum3Title, newAlbum4Title, newAlbum5Title;
    @FXML private Label newAlbum1Artist, newAlbum2Artist, newAlbum3Artist, newAlbum4Artist, newAlbum5Artist;    
    @FXML private Label gotoTitle, gotoArtist;
    @FXML private ImageView trending1AlbumArt, trending2AlbumArt, trending3AlbumArt, trending4AlbumArt, trending5AlbumArt;
    @FXML private ImageView newAlbum1Art, newAlbum2Art, newAlbum3Art, newAlbum4Art, newAlbum5Art;
    @FXML private Label latest1Title, latest1Artist, latest2Title, latest2Artist, latest3Title, latest3Artist;
    @FXML private Label latest4Title, latest4Artist, latest5Title, latest5Artist, latest6Title, latest6Artist;
    @FXML private Label latest7Title, latest7Artist, latest8Title, latest8Artist, latest9Title, latest9Artist;
    @FXML private ImageView latest1AlbumArt, latest2AlbumArt, latest3AlbumArt, latest4AlbumArt, latest5AlbumArt, latest6AlbumArt, latest7AlbumArt, latest8AlbumArt, latest9AlbumArt;
    @FXML private HBox latestCard1, latestCard2, latestCard3, latestCard4, latestCard5, latestCard6, latestCard7, latestCard8, latestCard9;
    private MainViewController mainController;

    @Override
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSampleData();
        setupClickHandlers();
        fetchAlbumsFromFirebase();
        fetchLatestSongsFromFirebase();
    }

    // Dữ liệu cho Trending Songs
    private final ObservableList<SongListController.SongItem> TRENDING_SONGS = FXCollections.observableArrayList(
	    // Song 1
	    new SongListController.SongItem(
	        "S1002", "Ex-Factor", "Lauryn Hill", "R&B", 327, 1998, 
	        "https://drive.google.com/uc?export=download&id=1N7-tdKi3f31lNBqyToDHGl83pFFlkUlr", 
	        "https://drive.google.com/uc?export=download&id=1LHb8cjGKcw64o2wfDiJqCuUZYIwtq3ts"
	    ),
	    // Song 2
	    new SongListController.SongItem(
	        "S1086", "Stay", "The Kid LAROI & Justin Bieber", "Pop", 143, 2024, 
	        "https://drive.google.com/uc?export=download&id=1FwaBz_P2QnSGB9vjDdRLze3X7nLaVszU",
	        "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/89/59/6a/89596ab9-fa3c-8d08-4d95-a6450fa2013c/886449400515.jpg/600x600bb.jpg"
	    ),
	    // Song 3 
	    new SongListController.SongItem(
	        "S1088", "That Girl", "Olly Murs", "Pop", 176, 2024, 
	        "https://drive.google.com/uc?export=download&id=1ep119l9P4AFnfoCn9BghAsZVVokChIU6", 
	        "https://is1-ssl.mzstatic.com/image/thumb/Music114/v4/29/94/b2/2994b2cd-5936-9081-4311-88dfc4d08e96/dj.sxjwuycl.jpg/600x600bb.jpg"
	    ),
	    // Song 4 
	    new SongListController.SongItem(
	        "S1046", "Be Great", "Jill Scott", "R&B", 193, 2026, 
	        "https://drive.google.com/uc?export=download&id=1NqLTs89MHQRmgac7WFG18xpLiDwYGhS7", 
	        "https://drive.google.com/uc?export=download&id=1cD4KxUpK4YZZ2vFRrnxUQQts_CfH1KiI"
	    ),
	    // Song 5 
	    new SongListController.SongItem(
	        "S1021", "Institutionalized", "Kendrick Lamar", "Rap", 272, 2015, 
	        "https://drive.google.com/uc?export=download&id=1WKZR5KiO_zsCJYSuEYy-Ubh-51Lh-guZ", 
	        "https://drive.google.com/uc?export=download&id=1KDyfIkWcBhy-PIgPtvQxm8Ja8JGlAhjS"
	    )
    	);
       
    private void populateSampleData() {
        // Song 1
        SongListController.SongItem firstSong = TRENDING_SONGS.get(0);
        if(trending1Title != null) trending1Title.setText(firstSong.title);
        if(trending1Artist != null) trending1Artist.setText(firstSong.artist);
        if(trending1AlbumArt != null && firstSong.imageURL != null) {
            trending1AlbumArt.setImage(new javafx.scene.image.Image(firstSong.imageURL, true));
        }
        
        // Song 2
        SongListController.SongItem secondSong = TRENDING_SONGS.get(1);
        if(trending2Title != null) trending2Title.setText(secondSong.title);
        if(trending2Artist != null) trending2Artist.setText(secondSong.artist);
        if(trending2AlbumArt != null && secondSong.imageURL != null) {
            trending2AlbumArt.setImage(new javafx.scene.image.Image(secondSong.imageURL, true));
        }

        // Song 3
        SongListController.SongItem thirdSong = TRENDING_SONGS.get(2);
        if(trending3Title != null) trending3Title.setText(thirdSong.title);
        if(trending3Artist != null) trending3Artist.setText(thirdSong.artist);
        if(trending3AlbumArt != null && thirdSong.imageURL != null) {
            try { trending3AlbumArt.setImage(new javafx.scene.image.Image(thirdSong.imageURL, true)); } catch(Exception e){}
        }

        // Song 4
        SongListController.SongItem fourthSong = TRENDING_SONGS.get(3);
        if(trending4Title != null) trending4Title.setText(fourthSong.title);
        if(trending4Artist != null) trending4Artist.setText(fourthSong.artist);
        if(trending4AlbumArt != null && fourthSong.imageURL != null) {
            try { trending4AlbumArt.setImage(new javafx.scene.image.Image(fourthSong.imageURL, true)); } catch(Exception e){}
        }

        // Song 5
        SongListController.SongItem fifthSong = TRENDING_SONGS.get(4);
        if(trending5Title != null) trending5Title.setText(fifthSong.title);
        if(trending5Artist != null) trending5Artist.setText(fifthSong.artist);
        if(trending5AlbumArt != null && fifthSong.imageURL != null) {
            try { trending5AlbumArt.setImage(new javafx.scene.image.Image(fifthSong.imageURL, true)); } catch(Exception e){}
        }
        
        if(gotoTitle != null) gotoTitle.setText("It Will Be Okay (2026 Remix)");
    }

    // New album release list
    private final ObservableList<Album> NEW_ALBUMS = FXCollections.observableArrayList();   
    public void processNewestAlbums(List<Album> allAlbumFromDatabase) {
    	System.out.println("Số lượng tải về từ Firebase: " + allAlbumFromDatabase.size());
    	NEW_ALBUMS.clear();
    	allAlbumFromDatabase.sort((a, b) -> Integer.compare(b.getReleaseYear(), a.getReleaseYear()));
    	int lim = Math.min(5, allAlbumFromDatabase.size());
    	for(int i = 0; i < lim; ++i) {
    		NEW_ALBUMS.add(allAlbumFromDatabase.get(i));
    	}
    	updateNewAlbumUI();
    }
    private void updateNewAlbumUI() {
        // Card 1
        if (NEW_ALBUMS.size() > 0) {
            Album a1 = NEW_ALBUMS.get(0);
            if(newAlbum1Title != null) newAlbum1Title.setText(a1.getTitle());
            if(newAlbum1Artist != null) newAlbum1Artist.setText(a1.getArtist());
            if(newAlbum1Art != null && a1.getImageURL() != null) { 
                try { newAlbum1Art.setImage(new Image(a1.getImageURL(), true)); } catch(Exception e){} 
            }
        }

        // Card 2
        if (NEW_ALBUMS.size() > 1) {
            Album a2 = NEW_ALBUMS.get(1);
            if(newAlbum2Title != null) newAlbum2Title.setText(a2.getTitle());
            if(newAlbum2Artist != null) newAlbum2Artist.setText(a2.getArtist());
            if(newAlbum2Art != null && a2.getImageURL() != null) { 
                try { newAlbum2Art.setImage(new Image(a2.getImageURL(), true)); } catch(Exception e){} 
            }
        }

        // Card 3
        if (NEW_ALBUMS.size() > 2) {
            Album a3 = NEW_ALBUMS.get(2);
            if(newAlbum3Title != null) newAlbum3Title.setText(a3.getTitle());
            if(newAlbum3Artist != null) newAlbum3Artist.setText(a3.getArtist());
            if(newAlbum3Art != null && a3.getImageURL() != null) { 
                try { newAlbum3Art.setImage(new Image(a3.getImageURL(), true)); } catch(Exception e){} 
            }
        }

        // Card 4
        if (NEW_ALBUMS.size() > 3) {
            Album a4 = NEW_ALBUMS.get(3);
            if(newAlbum4Title != null) newAlbum4Title.setText(a4.getTitle());
            if(newAlbum4Artist != null) newAlbum4Artist.setText(a4.getArtist());
            if(newAlbum4Art != null && a4.getImageURL() != null) { 
                try { newAlbum4Art.setImage(new Image(a4.getImageURL(), true)); } catch(Exception e){} 
            }
        }

        // Card 5
        if (NEW_ALBUMS.size() > 4) {
            Album a5 = NEW_ALBUMS.get(4);
            if(newAlbum5Title != null) newAlbum5Title.setText(a5.getTitle());
            if(newAlbum5Artist != null) newAlbum5Artist.setText(a5.getArtist());
            if(newAlbum5Art != null && a5.getImageURL() != null) { 
                try { newAlbum5Art.setImage(new Image(a5.getImageURL(), true)); } catch(Exception e){} 
            }
        }
    }
    private void setupClickHandlers() {
        // Click vào Trending
    	// Card 1
        if (trendingCard1 != null) {
            trendingCard1.setOnMouseClicked(e -> {
            	if(mainController != null && TRENDING_SONGS != null && TRENDING_SONGS.size() > 0) {
            		SongListController.SongItem song = TRENDING_SONGS.get(0);
            		mainController.showPlayerBar(song.title, song.artist, song.imageURL, song.audioURL);
            	}
            });
        }
        // Card 2
        if (trendingCard2 != null) {
            trendingCard2.setOnMouseClicked(e -> {
            	if(mainController != null && TRENDING_SONGS != null && TRENDING_SONGS.size() > 1) {
            		SongListController.SongItem song = TRENDING_SONGS.get(1);
            		mainController.showPlayerBar(song.title, song.artist, song.imageURL, song.audioURL);
            	}
            });
        }
        // Card 3
        if (trendingCard3 != null) {
            trendingCard3.setOnMouseClicked(e -> {
                if(mainController != null && TRENDING_SONGS != null && TRENDING_SONGS.size() > 2) {
                    SongListController.SongItem song = TRENDING_SONGS.get(2);
                    mainController.showPlayerBar(song.title, song.artist, song.imageURL, song.audioURL);
                }
            });
        }
        // Card 4
        if (trendingCard4 != null) {
            trendingCard4.setOnMouseClicked(e -> {
                if(mainController != null && TRENDING_SONGS != null && TRENDING_SONGS.size() > 3) {
                    SongListController.SongItem song = TRENDING_SONGS.get(3);
                    mainController.showPlayerBar(song.title, song.artist, song.imageURL, song.audioURL);
                }
            });
        }
        // Card 5
        if (trendingCard5 != null) {
            trendingCard5.setOnMouseClicked(e -> {
                if(mainController != null && TRENDING_SONGS != null && TRENDING_SONGS.size() > 4) {
                    SongListController.SongItem song = TRENDING_SONGS.get(4);
                    mainController.showPlayerBar(song.title, song.artist, song.imageURL, song.audioURL);
                }
            });
        }
        // Click vào Today's Hits Banner
        if(todaysHitBanner != null) {
            todaysHitBanner.setOnMouseClicked(e -> openDetailedSongList("Today's Hits"));
        }
        
        if (latest1Title != null) {
            latest1Title.setOnMouseClicked(e -> openCompactList());
        }
      
        // Click New Album Cards to go to AlbumView
        if (newAlbumCard1 != null) {
            newAlbumCard1.setOnMouseClicked(e -> {
                if (NEW_ALBUMS != null && NEW_ALBUMS.size() > 0) {
                    fetchSongsAndOpenAlbum(NEW_ALBUMS.get(0));
                }
            });
        }

        if (newAlbumCard2 != null) {
            newAlbumCard2.setOnMouseClicked(e -> {
                if (NEW_ALBUMS != null && NEW_ALBUMS.size() > 1) {
                    fetchSongsAndOpenAlbum(NEW_ALBUMS.get(1));
                }
            });
        }

        if (newAlbumCard3 != null) {
            newAlbumCard3.setOnMouseClicked(e -> {
                if (NEW_ALBUMS != null && NEW_ALBUMS.size() > 2) {
                    fetchSongsAndOpenAlbum(NEW_ALBUMS.get(2));
                }
            });
        }

        if (newAlbumCard4 != null) {
            newAlbumCard4.setOnMouseClicked(e -> {
                if (NEW_ALBUMS != null && NEW_ALBUMS.size() > 3) {
                    fetchSongsAndOpenAlbum(NEW_ALBUMS.get(3));
                }
            });
        }

        if (newAlbumCard5 != null) {
            newAlbumCard5.setOnMouseClicked(e -> {
                if (NEW_ALBUMS != null && NEW_ALBUMS.size() > 4) {
                    fetchSongsAndOpenAlbum(NEW_ALBUMS.get(4));
                }
            });
        }
        
        // Click latest songs cards to play the songs
        // Latest song 1
        if (latestCard1 != null) {
            latestCard1.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 0) {
            		Song song = LATEST_SONGS.get(0);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 2
        if (latestCard2 != null) {
            latestCard2.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 1) {
            		Song song = LATEST_SONGS.get(1);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 3
        if (latestCard3 != null) {
            latestCard3.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 2) {
            		Song song = LATEST_SONGS.get(2);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 4
        if (latestCard4 != null) {
            latestCard4.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 3) {
            		Song song = LATEST_SONGS.get(3);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 5
        if (latestCard5 != null) {
            latestCard5.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 4) {
            		Song song = LATEST_SONGS.get(4);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 6
        if (latestCard6 != null) {
            latestCard6.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 5) {
            		Song song = LATEST_SONGS.get(5);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 7
        if (latestCard7 != null) {
            latestCard7.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 6) {
            		Song song = LATEST_SONGS.get(6);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 8
        if (latestCard8 != null) {
            latestCard8.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 7) {
            		Song song = LATEST_SONGS.get(7);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }

        // Latest song 9
        if (latestCard9 != null) {
            latestCard9.setOnMouseClicked(e -> {
            	if(mainController != null && LATEST_SONGS != null && LATEST_SONGS.size() > 8) {
            		Song song = LATEST_SONGS.get(8);
            		mainController.showPlayerBar(song.getTitle(), song.getArtist(), song.getImageURL(), song.getAudioURL());
            	}
            });
        }   
    }
    private void fetchSongsAndOpenAlbum(Album album) {
        System.out.println("Downloading songs through FirebaseServiceImpl...");
        System.out.println(album.getSongIdList());
        // Run in background 
        new Thread(() -> {
        	FirebaseServiceImpl firebaseService = new FirebaseServiceImpl();
        	List<Song> realSongs = firebaseService.fetchAlbumSongsByIds(album.getSongIdList());
        	System.out.println("Successfully download " + realSongs.size() + " songs.");
        	Platform.runLater(() -> {
        		if(mainController != null) {
        			mainController.loadSongDetail(
        				album.getTitle(),
        				album.getArtist(),
        				album.getGenre(),
        				album.getReleaseYear(),
        				album.getImageURL(),
        				realSongs
        			);
        		}
        	});
        }).start();
    }
    private void fetchAlbumsFromFirebase() {
    	System.out.println("Loading albums list...");
    	new Thread(()-> {
    		FirebaseServiceImpl firebaseService = new FirebaseServiceImpl();
    		List<Album> allAlbums = firebaseService.fetchAlbums();
    		System.out.println("Download " + allAlbums.size() + " albums");
    		Platform.runLater(() -> {
    			processNewestAlbums(allAlbums);
    		});
    	}).start();
    }
    private void openDetailedSongList(String listTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongListView.fxml"));
            Node view = loader.load();
            SongListController ctrl = loader.getController();
            
            // 1. Kết nối với MainViewController
            ctrl.setMainController(this.mainController);
            
            // 2. XÁC ĐỊNH ID CHUẨN (Lấy đúng cái SYSTEM_TODAY'S_HITS)
            // Logic này sẽ biến "Today's Hits" thành "SYSTEM_TODAY'S_HITS"
            String finalId = "SYSTEM_" + listTitle.toUpperCase().replace(" ", "_");
            
            // 3. TRUYỀN DỮ LIỆU SANG SONG_LIST_CONTROLLER
            // Truyền một list rỗng (new ArrayList<>()) vào tham số thứ 8.
            // TẠI SAO? Để hàm refreshData() trong SongListController thấy list rỗng 
            // và tự động lên Firebase kéo bài hát từ node finalId về.
            ctrl.setData(
                finalId, 
                listTitle, 
                "Curated for you", 
                "System Playlist", 
                "/images/playlist_cover.png", 
                2026, 
                "Various", 
                new java.util.ArrayList<>() 
            );

            // 4. Đưa giao diện lên màn hình
            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openCompactList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompactListView.fxml"));
            Node view = loader.load();
            
            if (loader.getController() instanceof MainViewController.MainViewAware) {
                ((MainViewController.MainViewAware) loader.getController()).setMainController(this.mainController);
            }

            if (mainController != null) {
                mainController.getContentArea().getChildren().setAll(view);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // Latest songs list
    private final ObservableList<Song> LATEST_SONGS = FXCollections.observableArrayList();   
    public void processLatestSongs(List<Song> allSongsFromDatabase) {
    	System.out.println("Số lượng tải về từ Firebase: " + allSongsFromDatabase.size());
    	NEW_ALBUMS.clear();
    	allSongsFromDatabase.sort((a, b) -> Integer.compare(b.getReleaseYear(), a.getReleaseYear()));
    	int lim = Math.min(9, allSongsFromDatabase.size());
    	for(int i = 0; i < lim; ++i) {
    		LATEST_SONGS.add(allSongsFromDatabase.get(i));
    	}
    	updateLatestSongsUI();
    }
    private void updateLatestSongsUI() {
        // Latest song 1
        if (LATEST_SONGS.size() > 0) {
            Song s1 = LATEST_SONGS.get(0);
            if(latest1Title != null) latest1Title.setText(s1.getTitle());
            if(latest1Artist != null) latest1Artist.setText(s1.getArtist());
            if(latest1AlbumArt != null && s1.getImageURL() != null) { 
                try { latest1AlbumArt.setImage(new Image(s1.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 2
        if (LATEST_SONGS.size() > 1) {
            Song s2 = LATEST_SONGS.get(1);
            if(latest2Title != null) latest2Title.setText(s2.getTitle());
            if(latest2Artist != null) latest2Artist.setText(s2.getArtist());
            if(latest2AlbumArt != null && s2.getImageURL() != null) { 
                try { latest2AlbumArt.setImage(new Image(s2.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 3
        if (LATEST_SONGS.size() > 2) {
            Song s3 = LATEST_SONGS.get(2);
            if(latest3Title != null) latest3Title.setText(s3.getTitle());
            if(latest3Artist != null) latest3Artist.setText(s3.getArtist());
            if(latest3AlbumArt != null && s3.getImageURL() != null) { 
                try { latest3AlbumArt.setImage(new Image(s3.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 4
        if (LATEST_SONGS.size() > 3) {
            Song s4 = LATEST_SONGS.get(3);
            if(latest4Title != null) latest4Title.setText(s4.getTitle());
            if(latest4Artist != null) latest4Artist.setText(s4.getArtist());
            if(latest4AlbumArt != null && s4.getImageURL() != null) { 
                try { latest4AlbumArt.setImage(new Image(s4.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 5
        if (LATEST_SONGS.size() > 4) {
            Song s5 = LATEST_SONGS.get(4);
            if(latest5Title != null) latest5Title.setText(s5.getTitle());
            if(latest5Artist != null) latest5Artist.setText(s5.getArtist());
            if(latest5AlbumArt != null && s5.getImageURL() != null) { 
                try { latest5AlbumArt.setImage(new Image(s5.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 6
        if (LATEST_SONGS.size() > 5) {
            Song s6 = LATEST_SONGS.get(5);
            if(latest6Title != null) latest6Title.setText(s6.getTitle());
            if(latest6Artist != null) latest6Artist.setText(s6.getArtist());
            if(latest6AlbumArt != null && s6.getImageURL() != null) { 
                try { latest6AlbumArt.setImage(new Image(s6.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 7
        if (LATEST_SONGS.size() > 6) {
            Song s7 = LATEST_SONGS.get(6);
            if(latest7Title != null) latest7Title.setText(s7.getTitle());
            if(latest7Artist != null) latest7Artist.setText(s7.getArtist());
            if(latest7AlbumArt != null && s7.getImageURL() != null) { 
                try { latest7AlbumArt.setImage(new Image(s7.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 8
        if (LATEST_SONGS.size() > 7) {
            Song s8 = LATEST_SONGS.get(7);
            if(latest8Title != null) latest8Title.setText(s8.getTitle());
            if(latest8Artist != null) latest8Artist.setText(s8.getArtist());
            if(latest8AlbumArt != null && s8.getImageURL() != null) { 
                try { latest8AlbumArt.setImage(new Image(s8.getImageURL(), true)); } catch(Exception e){} 
            }
        }
        // Latest song 9
        if (LATEST_SONGS.size() > 8) {
            Song s9 = LATEST_SONGS.get(8);
            if(latest9Title != null) latest9Title.setText(s9.getTitle());
            if(latest9Artist != null) latest9Artist.setText(s9.getArtist());
            if(latest9AlbumArt != null && s9.getImageURL() != null) { 
                try { latest9AlbumArt.setImage(new Image(s9.getImageURL(), true)); } catch(Exception e){} 
            }
        }   
    }
    private void fetchLatestSongsFromFirebase() {
    	System.out.println("Loading songs list...");
    	new Thread(()-> {
    		FirebaseServiceImpl firebaseService = new FirebaseServiceImpl();
    		List<Song> allSongs = firebaseService.fetchSongs();
    		System.out.println("Download " + allSongs.size() + " Songs");
    		Platform.runLater(() -> {
    			processLatestSongs(allSongs);
    		});
    	}).start();
    }
}

