package com.example.spotifyproj2;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyproj2.PW_RecyclerViewAdapter;
import com.example.spotifyproj2.WrappedModel;
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
    private RecyclerView datesRecyclerView;
    private PW_RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_wrapped);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        RecyclerView datesRecyclerView = findViewById(R.id.datesContainer);
        datesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PW_RecyclerViewAdapter(this, new ArrayList<>());
        datesRecyclerView.setAdapter(adapter);

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
                    // Pass the list to the RecyclerView Adapter
                    adapter.updateData(wrappedDates);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        }
    }
}
