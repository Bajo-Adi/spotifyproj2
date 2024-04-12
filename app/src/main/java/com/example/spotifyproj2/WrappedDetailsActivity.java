package com.example.spotifyproj2;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WrappedDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout topSongsLayout;
    private LinearLayout topArtistsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapped_details);
        Log.d("Wrapped", "Received request");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        topSongsLayout = findViewById(R.id.layoutTopSongs);
        topArtistsLayout = findViewById(R.id.layoutTopArtists);

        String date = getIntent().getStringExtra("DATE");
        if (date != null) {
            // Fetch the top artists and tracks for this date
            fetchWrappedDetails(date);
        }
    }

    private void fetchWrappedDetails(String date) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference wrappedRef = db.collection("users")
                    .document(uid)
                    .collection("wrappeds")
                    .document(date);

            wrappedRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> topTracks = (List<String>) documentSnapshot.get("topTracks");
                    List<String> topArtists = (List<String>) documentSnapshot.get("topArtists");

                    // Update UI with the fetched data
                    updateTopItemsUI(topTracks, topSongsLayout);
                    updateTopItemsUI(topArtists, topArtistsLayout);
                } else {
                    Toast.makeText(this, "No details available for this date", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error fetching wrapped details", e);
                Toast.makeText(this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateTopItemsUI(List<String> items, LinearLayout layout) {
        if (items != null) {
            for (int i = 0; i < items.size() && i < 5; i++) {
                TextView textView = (TextView) layout.getChildAt(i);
                if (textView != null) {
                    textView.setText(items.get(i));
                }
            }
        }
    }
}
