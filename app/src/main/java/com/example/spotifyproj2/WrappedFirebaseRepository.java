package com.example.spotifyproj2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
// Add other necessary imports

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

// Add other necessary imports

public class WrappedFirebaseRepository {

    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public void getWrappedData(String userId, Callback<WrappedData> callback) {
        dbRef.child("wrappedData").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        WrappedData wrappedData = dataSnapshot.getValue(WrappedData.class);
                        if (wrappedData != null) {
                            callback.onSuccess(wrappedData);
                        } else {
                            callback.onError(new Exception("No Wrapped Data found for user: " + userId));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    public void shareWrappedWithFriend(String userId, String friendUserId, Callback<Boolean> callback) {

        // Logic to share Wrapped data with a friend in Firebase Realtime Database, I will complete it soon
    }
}


