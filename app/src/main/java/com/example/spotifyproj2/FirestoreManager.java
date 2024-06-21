package com.example.spotifyproj2;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Save Spotify data to Firestore.
     *
     * @param username The Spotify username to use as the document ID.
     * @param topArtists List of the user's top artists.
     * @param topSongs List of the user's top songs.
     * /@param additionalInfo Other Spotify information you might want to store.
     */
    public void saveSpotifyDataToFirestore(String username, List<String> topArtists, List<String> topSongs) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("topArtists", topArtists);
        userData.put("topSongs", topSongs);
//        userData.addAll(additionalInfo);

        db.collection("spotifyUsers").document(username)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error writing document", e);
                });
    }
}
