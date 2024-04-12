package com.example.spotifyproj2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ShareWrappedActivity extends AppCompatActivity {
    private EditText editTextFriendEmail;
    private Button buttonShareWrapped;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_wrapped);

        editTextFriendEmail = findViewById(R.id.editTextFriendEmail);
        buttonShareWrapped = findViewById(R.id.buttonShareWrapped);
        db = FirebaseFirestore.getInstance();

        buttonShareWrapped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWrappedWithFriend(editTextFriendEmail.getText().toString().trim());
            }
        });
    }

    private void shareWrappedWithFriend(String friendEmail) {
        db.collection("users").whereEqualTo("email", friendEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                String friendId = task.getResult().getDocuments().get(0).getId();
                shareWrappedData(friendId);
            } else {
                Toast.makeText(ShareWrappedActivity.this, "Invite friend to our app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareWrappedData(String friendId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to share wrappeds.", Toast.LENGTH_LONG).show();
            return;
        }

        // Fetch the current user's latest wrapped data and share it
        fetchCurrentUserWrappedData(currentUser.getUid(), new Callback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> wrappedData) {
                // Add current user's ID or username as 'sharedBy'
                wrappedData.put("sharedBy", currentUser.getEmail());  // or currentUser.getEmail()

                // Now share this wrappedData with the specified friendId
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(friendId)
                        .collection("friendsWrapped")
                        .add(wrappedData)
                        .addOnSuccessListener(documentReference -> showSuccessNotification())
                        .addOnFailureListener(e -> Toast.makeText(ShareWrappedActivity.this, "Failed to share wrapped", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ShareWrappedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This is a placeholder, implement according to how you manage wrapped data
    private void fetchCurrentUserWrappedData(String userId, Callback<Map<String, Object>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("wrappeds")
                // Assuming you want the latest one or specify how to pick which wrapped to share
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastWrapped = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Object> wrappedData = lastWrapped.getData();
                        callback.onSuccess(wrappedData);
                    } else {
                        Toast.makeText(this, "No wrapped data available to share.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve your wrapped data.", Toast.LENGTH_SHORT).show());
    }

    interface Callback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    private void showSuccessNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "wrapped_sharing_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Wrapped Sharing Notifications",
                    NotificationManager.IMPORTANCE_HIGH // Use HIGH to ensure notifications pop up
            );

            notificationChannel.setDescription("Notifications for when a wrapped is shared");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 100, 500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationManager.areNotificationsEnabled()) {
                // Open notification settings if not enabled
                Intent intent = new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
                return;
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher) // Ensure you have a drawable with this name
                .setContentTitle("Success")
                .setContentText("Wrapped shared successfully with your friend!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, notificationBuilder.build());
    }
}