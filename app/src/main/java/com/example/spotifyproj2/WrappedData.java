package com.example.spotifyproj2;


import java.util.List;
import java.util.Map;

// This class should mirror the structure of the Wrapped data in your Firebase Realtime Database
public class WrappedData {
    private List<String> topSongs;
    private List<String> topArtists;
    private Map<String, Integer> genreCounts;
    private String userId; // or any other fields that represent the data you store for each Wrapped

    // Default constructor required for calls to DataSnapshot.getValue(WrappedData.class)
    public WrappedData() {
    }

    // Constructor to set all fields when creating a new WrappedData object
    public WrappedData(List<String> topSongs, List<String> topArtists, Map<String, Integer> genreCounts, String userId) {
        this.topSongs = topSongs;
        this.topArtists = topArtists;
        this.genreCounts = genreCounts;
        this.userId = userId;
    }

    // Getters and setters for all fields
    public List<String> getTopSongs() {
        return topSongs;
    }

    public void setTopSongs(List<String> topSongs) {
        this.topSongs = topSongs;
    }

    public List<String> getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(List<String> topArtists) {
        this.topArtists = topArtists;
    }

    public Map<String, Integer> getGenreCounts() {
        return genreCounts;
    }

    public void setGenreCounts(Map<String, Integer> genreCounts) {
        this.genreCounts = genreCounts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // You may add other methods, such as a toString(), if needed
}


