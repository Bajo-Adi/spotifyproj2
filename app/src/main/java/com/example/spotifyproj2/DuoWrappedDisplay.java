package com.example.spotifyproj2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DuoWrappedDisplay extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duo_wrapped_display);

        // Make API requests to Spotify to get user's top items
        // Process API responses and extract top songs and artists for each user

        ArrayList<String> user1TopSongs = getUserTopSongs("user1AccessToken");
        ArrayList<String> user1TopArtists = getUserTopArtists("user1AccessToken");


        ArrayList<String> user2TopSongs = getUserTopSongs("user2AccessToken");
        ArrayList<String> user2TopArtists = getUserTopArtists("user2AccessToken");

        // Calculate similarity score
        double similarityScore = calculateSimilarityScore(user1TopSongs, user2TopSongs, user1TopArtists, user2TopArtists);

        // Update UI with shared top items and similarity score
        updateUI(user1TopSongs, user2TopSongs, user1TopArtists, user2TopArtists,similarityScore);
    }

    // Method to get user's top songs from Spotify API
    private ArrayList<String> getUserTopSongs(String accessToken) {
        // Implement logic to make API request and parse response
        return new ArrayList<>();
    }

    // Method to get user's top artists from Spotify API
    private ArrayList<String> getUserTopArtists(String accessToken) {
        // Implement logic to make API request and parse response
        return new ArrayList<>();
    }

    // Method to calculate similarity score
    private double calculateSimilarityScore(ArrayList<String> user1TopSongs, ArrayList<String> user2TopSongs,
                                            ArrayList<String> user1TopArtists, ArrayList<String> user2TopArtists) {
        // Implement logic to calculate similarity score
        // Convert ArrayLists to Sets for efficient comparison
        Set<String> user1SongsSet = new HashSet<>(user1TopSongs);
        Set<String> user2SongsSet = new HashSet<>(user2TopSongs);
        Set<String> user1ArtistsSet = new HashSet<>(user1TopArtists);
        Set<String> user2ArtistsSet = new HashSet<>(user2TopArtists);

        // Find common songs and artists
        Set<String> commonSongs = new HashSet<>(user1SongsSet);
        commonSongs.retainAll(user2SongsSet); // Retain only common songs
        Set<String> commonArtists = new HashSet<>(user1ArtistsSet);
        commonArtists.retainAll(user2ArtistsSet); // Retain only common artists

        // Calculate similarity score
        int commonSongsCount = commonSongs.size();
        int commonArtistsCount = commonArtists.size();
        double similarityScore = (commonSongsCount + commonArtistsCount) / 100.0;

        return similarityScore;
    }

    // Method to update UI with shared top items and similarity score
    private void updateUI(ArrayList<String> user1TopSongs, ArrayList<String> user2TopSongs,
                          ArrayList<String> user1TopArtists, ArrayList<String> user2TopArtists,
                          double similarityScore) {
        // Update TextViews in the layout to display shared top items and similarity score
        //TextView sharedSongsTextView = findViewById(R.id.shared_songs_text_view);
        // we can update other TextViews as well...
        // for example:
        // sharedSongsTextView.setText("Shared Top Songs: " + sharedTopSongs.toString());

    }
}
