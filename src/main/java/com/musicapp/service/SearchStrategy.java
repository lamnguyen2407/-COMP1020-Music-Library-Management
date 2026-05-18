package com.musicapp.service;

import com.musicapp.model.Song;
import java.util.*;

public interface SearchStrategy {
    List<Song> search(List<Song> sourceList, String keyword);
}