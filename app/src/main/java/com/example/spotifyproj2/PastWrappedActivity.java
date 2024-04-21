package com.example.spotifyproj2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PastWrappedActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button Homebtn;
    private RecyclerView datesRecyclerView;
    private PW_RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_wrapped);

        // Initialize Firestore and FirebaseAuth instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Homebtn  = findViewById(R.id.home_button);
        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(PastWrappedActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        // Setup the RecyclerView and its adapter
        datesRecyclerView = findViewById(R.id.datesContainer);
        datesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PW_RecyclerViewAdapter(this, new ArrayList<>());
        datesRecyclerView.setAdapter(adapter);

        // Get current user and fetch their "wrappeds"
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            CollectionReference wrappedsRef = db.collection("users").document(uid).collection("wrappeds");

            wrappedsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<WrappedModel> wrappedDates = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String date = document.getString("date");
                        wrappedDates.add(new WrappedModel(date));
                    }
                    // Pass the fetched dates to the RecyclerView Adapter
                    adapter.updateData(wrappedDates);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        } else {
            Log.d(TAG, "No current user found.");
        }
    }
}
