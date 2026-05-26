package com.musicapp;

import com.musicapp.model.*;
import com.musicapp.service.*;

import java.util.*;

/**
 * Manual System Test Runner
 * Validates core data structures and algorithms without Firebase dependency.
 * Run this class to produce test evidence for presentation.
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        MUSIC LIBRARY MANAGEMENT - SYSTEM TEST REPORT        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        testAVLTreeSearch();
        testPlaybackServiceQueue();
        testPlaybackServiceHistory();
        testLinearSearchStrategy();
        testSongComparable();
        testPlaylistManagement();
        testAlbumSongManagement();
        testUserRoleHierarchy();
        testSessionManager();
        testPriorityQueueTopK();
        testDoubleHashingCollisionResolution();

        System.out.println();
        System.out.println("══════════════════════════════════════════════════════════════");
        System.out.printf("  TOTAL: %d tests  |  ✅ PASSED: %d  |  ❌ FAILED: %d%n", passed + failed, passed, failed);
        System.out.println("══════════════════════════════════════════════════════════════");
        
        if (failed == 0) {
            System.out.println("  🎉 ALL TESTS PASSED SUCCESSFULLY");
        } else {
            System.out.println("  ⚠️  SOME TESTS FAILED — REVIEW OUTPUT ABOVE");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 1: AVL Tree Search Engine — Prefix Matching
    // ─────────────────────────────────────────────────────────────
    private static void testAVLTreeSearch() {
        printHeader("AVL Tree Search Engine");

        AdvancedSearchStrategy search = new AdvancedSearchStrategy();

        Song s1 = new Song("S001", "Blinding Lights", "The Weeknd", "Pop", 200, 2020, "", "");
        Song s2 = new Song("S002", "Shape of You", "Ed Sheeran", "Pop", 234, 2017, "", "");
        Song s3 = new Song("S003", "Bohemian Rhapsody", "Queen", "Rock", 355, 1975, "", "");
        Song s4 = new Song("S004", "Blank Space", "Taylor Swift", "Pop", 231, 2014, "", "");
        Song s5 = new Song("S005", "Bliss", "Muse", "Alternative", 250, 2001, "", "");

        // Build index
        search.indexSong(s1);
        search.indexSong(s2);
        search.indexSong(s3);
        search.indexSong(s4);
        search.indexSong(s5);

        // Build cache
        Map<String, Song> cache = new HashMap<>();
        cache.put("S001", s1); cache.put("S002", s2); cache.put("S003", s3);
        cache.put("S004", s4); cache.put("S005", s5);

        // Test 1a: Exact word match
        List<Song> r1 = search.search(cache, "queen");
        assertTest("Exact word search 'queen'", r1.size() == 1 && r1.get(0).getTitle().equals("Bohemian Rhapsody"));

        // Test 1b: Prefix match — "bli" should match "Blinding Lights" and "Bliss"
        List<Song> r2 = search.search(cache, "bli");
        assertTest("Prefix search 'bli' → 2 results (Blinding Lights, Bliss)", r2.size() == 2);

        // Test 1c: Multi-token search — "blinding weeknd"
        List<Song> r3 = search.search(cache, "blinding weeknd");
        assertTest("Multi-token 'blinding weeknd' → 1 result", r3.size() == 1 && r3.get(0).getSongId().equals("S001"));

        // Test 1d: Empty query returns nothing
        List<Song> r4 = search.search(cache, "");
        assertTest("Empty query returns 0 results", r4.isEmpty());

        // Test 1e: No match
        List<Song> r5 = search.search(cache, "zzzzz");
        assertTest("Non-existent query returns 0 results", r5.isEmpty());
        
        // Test 1f: Artist search
        List<Song> r6 = search.search(cache, "ed");
        assertTest("Artist prefix search 'ed' → finds Ed Sheeran", r6.size() >= 1);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 2: PlaybackService — Queue (LinkedList) Operations
    // ─────────────────────────────────────────────────────────────
    private static void testPlaybackServiceQueue() {
        printHeader("Playback Queue (LinkedList)");

        PlaybackService ps = PlaybackService.getInstance();

        Song a = new Song("A", "Song A", "Artist1", "Pop", 180, 2023, "", "");
        Song b = new Song("B", "Song B", "Artist2", "Pop", 200, 2023, "", "");
        Song c = new Song("C", "Song C", "Artist3", "Pop", 210, 2023, "", "");

        List<Song> playlist = List.of(a, b, c);
        ps.setPlaylist(playlist, 0);  // Start at index 0, queue = [B, C]
        ps.play(a);

        assertTest("Current song is A after play(A)", ps.getCurrentSong().getSongId().equals("A"));

        // Next → should go to B
        Song next1 = ps.next();
        assertTest("next() returns Song B", next1 != null && next1.getSongId().equals("B"));

        // Next → should go to C
        Song next2 = ps.next();
        assertTest("next() returns Song C", next2 != null && next2.getSongId().equals("C"));

        // Next → queue is empty now
        Song next3 = ps.next();
        assertTest("next() returns null at end of queue", next3 == null);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 3: PlaybackService — History (Stack) Operations
    // ─────────────────────────────────────────────────────────────
    private static void testPlaybackServiceHistory() {
        printHeader("Playback History (Stack)");

        // Reset by creating fresh instance logic
        PlaybackService ps = PlaybackService.getInstance();

        Song x = new Song("X", "Song X", "ArtistX", "Rock", 300, 2020, "", "");
        Song y = new Song("Y", "Song Y", "ArtistY", "Rock", 280, 2021, "", "");

        ps.clearQueue();
        ps.play(x);
        ps.play(y);  // x goes to history

        // Previous → should go back to X
        Song prev = ps.previous();
        assertTest("previous() returns Song X from history stack", prev != null && prev.getSongId().equals("X"));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 4: Linear Search Strategy — Baseline Comparison
    // ─────────────────────────────────────────────────────────────
    private static void testLinearSearchStrategy() {
        printHeader("Linear Search Strategy (Baseline)");

        LinearSearchStrategy linear = new LinearSearchStrategy();
        
        List<Song> songs = List.of(
            new Song("L1", "Levitating", "Dua Lipa", "Pop", 203, 2020, "", ""),
            new Song("L2", "Lovely", "Billie Eilish", "Indie", 200, 2018, "", ""),
            new Song("L3", "Lose Yourself", "Eminem", "Hip Hop", 326, 2002, "", "")
        );

        List<Song> r1 = linear.search(songs, "l");
        assertTest("Linear search 'l' matches all 3 songs starting with L", r1.size() == 3);

        List<Song> r2 = linear.search(songs, "dua");
        assertTest("Linear search 'dua' matches Dua Lipa", r2.size() == 1);

        List<Song> r3 = linear.search(songs, "xyz");
        assertTest("Linear search 'xyz' returns 0", r3.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 5: Song Comparable — Natural Ordering
    // ─────────────────────────────────────────────────────────────
    private static void testSongComparable() {
        printHeader("Song Comparable (Natural Ordering)");

        Song alpha = new Song("1", "Alpha", "A", "Pop", 100, 2020, "", "");
        Song beta = new Song("2", "Beta", "B", "Pop", 100, 2020, "", "");
        Song gamma = new Song("3", "Gamma", "C", "Pop", 100, 2020, "", "");

        List<Song> list = new ArrayList<>(List.of(gamma, alpha, beta));
        Collections.sort(list);

        assertTest("Songs sort alphabetically by title: Alpha < Beta < Gamma",
                list.get(0).getTitle().equals("Alpha") &&
                list.get(1).getTitle().equals("Beta") &&
                list.get(2).getTitle().equals("Gamma"));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 6: Playlist Model — Add/Remove Songs
    // ─────────────────────────────────────────────────────────────
    private static void testPlaylistManagement() {
        printHeader("Playlist Model (Add/Remove)");

        Playlist pl = new Playlist("My Playlist", "user1", "user", "/images/cover.png");
        
        assertTest("New playlist starts with 0 songs", pl.getSongIdList().isEmpty());

        pl.addSongToPlaylist("S001");
        pl.addSongToPlaylist("S002");
        assertTest("After adding 2 songs, size = 2", pl.getSongIdList().size() == 2);

        pl.removeSongFromPlaylist("S001");
        assertTest("After removing 1 song, size = 1", pl.getSongIdList().size() == 1);

        assertTest("Remaining song is S002", pl.getSongIdList().contains("S002"));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 7: Album Model — Song ID Management
    // ─────────────────────────────────────────────────────────────
    private static void testAlbumSongManagement() {
        printHeader("Album Model (Song IDs)");

        Album album = new Album("Test Album", "Test Artist", 2024, "/img.jpg", "Pop");

        album.addSongId("S100");
        album.addSongId("S200");
        album.addSongId("S300");
        assertTest("Album has 3 songs after adding", album.getSongIdList().size() == 3);

        album.removeSongId("S200");
        assertTest("Album has 2 songs after removing one", album.getSongIdList().size() == 2);
        assertTest("Removed song S200 is no longer present", !album.getSongIdList().contains("S200"));
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 8: User Role Hierarchy (Inheritance)
    // ─────────────────────────────────────────────────────────────
    private static void testUserRoleHierarchy() {
        printHeader("User Role Hierarchy (Polymorphism)");

        ListenerUser listener = new ListenerUser("U001", "John Doe", "john@test.com", "johndoe", "pass123");
        Admin admin = new Admin("U999", "admin@test.com", "admin", "admin123");

        assertTest("ListenerUser role = 'listener'", "listener".equals(listener.getRole()));
        assertTest("Admin role = 'admin'", "admin".equals(admin.getRole()));
        assertTest("Both are instances of User (polymorphism)", listener instanceof User && admin instanceof User);
        assertTest("ListenerUser has playlist map initialized", listener.getPlaylistIds() != null);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 9: Session Manager — State Management
    // ─────────────────────────────────────────────────────────────
    private static void testSessionManager() {
        printHeader("Session Manager");

        ListenerUser user = new ListenerUser("U050", "Test User", "test@test.com", "testuser", "pw");
        SessionManager.currentUser = user;
        SessionManager.isAdmin = false;

        assertTest("Session stores current user", SessionManager.currentUser != null);
        assertTest("isAdmin = false for listener", !SessionManager.isAdmin);

        SessionManager.clearSession();
        assertTest("clearSession() nullifies user", SessionManager.currentUser == null);
        assertTest("clearSession() resets isAdmin to false", !SessionManager.isAdmin);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 10: PriorityQueue Top-K Selection (Album ranking logic)
    // ─────────────────────────────────────────────────────────────
    private static void testPriorityQueueTopK() {
        printHeader("PriorityQueue Top-K Selection");

        List<Song> allSongs = List.of(
            new Song("1", "Old Song",    "A", "Pop", 100, 2000, "", ""),
            new Song("2", "Mid Song",    "B", "Pop", 100, 2015, "", ""),
            new Song("3", "New Song",    "C", "Pop", 100, 2023, "", ""),
            new Song("4", "Newest Song", "D", "Pop", 100, 2024, "", ""),
            new Song("5", "Another Old", "E", "Pop", 100, 2005, "", ""),
            new Song("6", "Very New",    "F", "Pop", 100, 2022, "", ""),
            new Song("7", "Ancient",     "G", "Pop", 100, 1990, "", "")
        );

        // Replicate MusicService.getLatestSongs() top-K logic
        PriorityQueue<Song> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.getReleaseYear(), b.getReleaseYear()));
        int k = 3;
        for (Song s : allSongs) {
            pq.offer(s);
            while (pq.size() > k) pq.poll(); // evict smallest year
        }

        List<Song> topK = new ArrayList<>();
        while (!pq.isEmpty()) topK.add(pq.poll());

        assertTest("PriorityQueue selects top-" + k + " newest songs", topK.size() == k);

        // All results should be from 2022, 2023, or 2024
        boolean allRecent = topK.stream().allMatch(s -> s.getReleaseYear() >= 2022);
        assertTest("All top-3 songs are from 2022+", allRecent);
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 11: Double Hashing — Collision Resolution
    // ─────────────────────────────────────────────────────────────
    private static void testDoubleHashingCollisionResolution() {
        printHeader("Double Hashing (User ID Generation)");

        // Replicate the hashing logic from FirebaseServiceImpl
        String email1 = "alice@example.com";
        String email2 = "bob@example.com";
        String email3 = "charlie@example.com";

        String id1 = generateUserId(email1, 0);
        String id2 = generateUserId(email2, 0);
        String id3 = generateUserId(email3, 0);

        assertTest("Hash produces formatted ID (e.g. U00000XXX)", id1.startsWith("U") && id1.length() == 9);
        assertTest("Different emails produce different IDs", !id1.equals(id2) && !id2.equals(id3));

        // Test collision resolution (probing with i=1)
        String id1_probe1 = generateUserId(email1, 1);
        assertTest("Probing (i=1) produces different ID than (i=0)", !id1.equals(id1_probe1));
    }

    // Replicated from FirebaseServiceImpl
    private static String generateUserId(String email, int i) {
        int key = 0, prime = 29;
        for (int j = 0; j < email.length(); j++) {
            key = key * prime + email.charAt(j);
        }
        key = key & Integer.MAX_VALUE;
        int index = ((key % 997) + i * (991 - (key % 991))) % 997;
        return String.format("U%03d", Math.abs(index));
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────
    private static void assertTest(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  ✅ PASS: " + name);
        } else {
            failed++;
            System.out.println("  ❌ FAIL: " + name);
        }
    }

    private static void printHeader(String section) {
        System.out.println();
        System.out.println("── " + section + " ──────────────────────────────────────");
    }
}
