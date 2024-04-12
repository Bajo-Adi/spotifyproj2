package com.example.spotifyproj2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuoWrapped extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText editTextUser2Email;
    private String editTextUser1Email;
    private Button buttonCompare;
    private TextView duoSongs, duoArtists, duoSongTogether, duoSimilarityPercent;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duo_wrapped);

        db = FirebaseFirestore.getInstance();
//        editTextUser1Email = findViewById(R.id.friend_username_edit_text);  // Update ID as per your layout
        editTextUser1Email = auth.getCurrentUser().getEmail().toString();
        editTextUser2Email = findViewById(R.id.editTextFriendEmail);  // Assume another ID for the second email
        buttonCompare = findViewById(R.id.buttonDuoWrapped);  // Update ID as per your layout
        duoSongs = findViewById(R.id.duoSongs);
        duoArtists = findViewById(R.id.duoArtists);
        duoSongTogether = findViewById(R.id.duoSongTogether);
        duoSimilarityPercent = findViewById(R.id.duoSimilarityPercent);

        buttonCompare.setOnClickListener(v -> {
            String email1 = editTextUser1Email;
            String email2 = editTextUser2Email.getText().toString().trim();
            fetchUserDetailsAndCompare(email1, email2);
        });
    }

    private void fetchUserDetailsAndCompare(String email1, String email2) {
        db.collection("users").whereEqualTo("email", email1).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                DocumentSnapshot user1Doc = task1.getResult().getDocuments().get(0);
                List<String> user1TopSongs = (List<String>) user1Doc.get("topTracks");
                List<String> user1TopArtists = (List<String>) user1Doc.get("topArtists");

                db.collection("users").whereEqualTo("email", email2).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                        DocumentSnapshot user2Doc = task2.getResult().getDocuments().get(0);
                        List<String> user2TopSongs = (List<String>) user2Doc.get("topTracks");
                        List<String> user2TopArtists = (List<String>) user2Doc.get("topArtists");

                        updateUIWithComparisonResults(user1TopSongs, user2TopSongs, user1TopArtists, user2TopArtists);
                    } else {
                        Toast.makeText(this, "Second user's data not found.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "First user's data not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithComparisonResults(List<String> user1TopSongs, List<String> user2TopSongs, List<String> user1TopArtists, List<String> user2TopArtists) {
        Set<String> commonSongs = new HashSet<>(user1TopSongs);
        commonSongs.retainAll(user2TopSongs);

        Set<String> commonArtists = new HashSet<>(user1TopArtists);
        commonArtists.retainAll(user2TopArtists);

        double similarityScore = calculateSimilarityScore(commonSongs.size(), commonArtists.size(), user1TopSongs.size(), user2TopSongs.size(), user1TopArtists.size(), user2TopArtists.size());

        duoSongs.setText("Shared songs: " + commonSongs.toString());
        duoArtists.setText("Shared artists: " + commonArtists.toString());
        duoSongTogether.setText("The song that brings you together is: " + (commonSongs.isEmpty() ? "None" : commonSongs.iterator().next()));
        duoSimilarityPercent.setText("You Have a " + similarityScore + "% Similarity Score in Music Taste!");
    }

    private double calculateSimilarityScore(int commonSongsCount, int commonArtistsCount, int totalSongsUser1, int totalSongsUser2, int totalArtistsUser1, int totalArtistsUser2) {
        double totalItems = (totalSongsUser1 + totalSongsUser2 + totalArtistsUser1 + totalArtistsUser2);
        return ((double) (commonSongsCount + commonArtistsCount) / totalItems) * 100;
    }
}