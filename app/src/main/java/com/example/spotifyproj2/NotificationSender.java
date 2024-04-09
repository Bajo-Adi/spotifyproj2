package com.example.spotifyproj2;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
// Add other necessary imports

public class NotificationSender {

    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public static void sendNotification(String toUserId, String message) {
        // Logic to send "notification" data to Firebase Realtime Database
        Map<String, String> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("fromUserId", "yourUserId");

        dbRef.child("notifications").child(toUserId).push().setValue(notification);
    }
}

