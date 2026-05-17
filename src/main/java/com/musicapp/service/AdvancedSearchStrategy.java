package com.musicapp.service;
import com.musicapp.model.Song;
import java.util.*;

public class AdvancedSearchStrategy implements SearchStrategy {
    
    private AVLtree searchIndex;

    public AdvancedSearchStrategy() {
        this.searchIndex = new AVLtree();
    }

    @Override
    public void resetIndex() {
        searchIndex.root = null;
    }

    @Override
    public void indexSong(Song song) {
        String fullSearchText = song.getTitle() + " - " + song.getArtist();
        String[] tokens = fullSearchText.split("\\s+");
        for(String token : tokens) {
            token = token.toLowerCase().trim();
            if(!token.isEmpty() && !token.equals("-")) {
                searchIndex.root = searchIndex.insertToTree(searchIndex.root, token, song);
            }
        }
    }
    @Override
    public List<Song> search(List<Song> sourceList, String kw) {
        // If something accidentally calls this slower List version, convert it to a Map and send it to your fast version above!
        Map<String, Song> tempMap = new HashMap<>();
        for(Song s : sourceList) {
            tempMap.put(s.getSongId(), s);
        }
        return search(tempMap, kw); 
    }
    @Override
    public List<Song> search(Map<String, Song> songCache, String kw) {
        if (kw == null || kw.trim().isEmpty()) return new ArrayList<>();
        
        kw = kw.toLowerCase().trim();
        String[] kwTokens = kw.split("\\s+");
        int len = kwTokens.length;
        int pivot = 0;
        
        for(int i = 1; i < len; ++i) {
            if(kwTokens[i].length() > kwTokens[pivot].length()) {
                pivot = i;
            }
        }
        
        HashSet<Song> set = new HashSet<>(searchIndex.find(searchIndex.root, kwTokens[pivot]));
        
        for(int i = 0; i < len; ++i) {
            if(i != pivot) {
                final String currentToken = kwTokens[i];
                set.removeIf(song -> {
                    String searchableText = (song.getTitle() + " " + song.getArtist()).toLowerCase();
                    return !searchableText.contains(currentToken);
                });
            }
        }
        	
        List<Song> finalResults = new ArrayList<>();
        for(Song result : set) {
            // Direct Hash lookup
            if (songCache.containsKey(result.getSongId())) {
                finalResults.add(result);
            }
        }
        return finalResults;
    }

}

class AVLtree {
    class Node {
        String val;
        Node left, right;
        int height = 0;
        ArrayList<Song> songs = new ArrayList<>();
        
        Node(String val) {
            this.val = val;
        }
    }
    
    Node root;
    
    int getHeight(Node current) {
        if(current == null) return -1;
        return current.height;
    }
    
    Node leftRotate(Node X) {
        Node Y = X.right;
        Node Z = Y.left;
        Y.left = X;
        X.right = Z;
        X.height = Math.max(getHeight(X.left), getHeight(X.right)) + 1;
        Y.height = Math.max(getHeight(Y.left), getHeight(Y.right)) + 1;
        return Y;
    }
    
    Node rightRotate(Node Y) {
        Node X = Y.left;
        Node Z = X.right;
        X.right = Y;
        Y.left = Z;
        Y.height = Math.max(getHeight(Y.left), getHeight(Y.right)) + 1;
        X.height = Math.max(getHeight(X.left), getHeight(X.right)) + 1;
        return X;
    }
    
    Node insertToTree(Node current, String token, Song fullSong) {
        if(current == null) {
            Node newNode = new Node(token);
            newNode.songs.add(fullSong);
            return newNode;
        }
        if(token.compareTo(current.val) < 0) { 
            current.left = insertToTree(current.left, token, fullSong);
        }
        else if(token.compareTo(current.val) > 0) {
            current.right = insertToTree(current.right, token, fullSong);
        }
        else {
            current.songs.add(fullSong);
        }
        
        int leftHeight = getHeight(current.left);
        int rightHeight = getHeight(current.right);
        current.height = Math.max(leftHeight, rightHeight) + 1;
        int heightDiff = leftHeight - rightHeight;
        
        if(heightDiff > 1) {
            if(current.left.val.compareTo(token) < 0) current.left = leftRotate(current.left);
            return rightRotate(current);
        }
        if(heightDiff < -1) {
            if(current.right.val.compareTo(token) > 0) current.right = rightRotate(current.right);
            return leftRotate(current);
        }
        return current;
    }
    
    ArrayList<Song> find(Node current, String kw) {
        ArrayList<Song> res = new ArrayList<>();
        if(current == null) return res;
        
        if(current.val.startsWith(kw)) {
            res.addAll(find(current.left, kw));
            res.addAll(find(current.right, kw));
            res.addAll(current.songs);
            return res;
        }
        
        if(current.val.compareTo(kw) < 0) return find(current.right, kw);
        else return find(current.left, kw);
    }
}