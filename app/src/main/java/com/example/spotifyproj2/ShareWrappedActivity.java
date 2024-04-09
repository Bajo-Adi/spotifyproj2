package com.example.spotifyproj2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ShareWrappedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_wrapped);
        // TODO: Implement UI to select friends and share Wrapped data
    }

    public void shareWrappedWithFriend(String friendUserId) {
        WrappedFirebaseRepository wrappedRepo = new WrappedFirebaseRepository();
        wrappedRepo.shareWrappedWithFriend(FirebaseAuth.getInstance().getCurrentUser().getUid(), friendUserId, new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean isSuccess) {
                if (isSuccess) {
                    NotificationSender.sendNotification(friendUserId, "You've been granted access to view Wrapped data!");
                    Toast.makeText(ShareWrappedActivity.this, "Data shared successfully!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ShareWrappedActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void inviteForDuoWrapped(String friendUserId) {
        //TODO: Add entry to Database, and send a notification as well
    }
}



