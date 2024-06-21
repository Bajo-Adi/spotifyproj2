package com.example.spotifyproj2;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API_call {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    public void onGetUserProfileClicked(String mAccessToken, Callback callback) {
        /**if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // Create a request to get the user profile
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(callback);
    }

    //fetching spotify user id
    public void fetchAndStoreSpotifyUserId(String token, Callback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(callback);
    }

    private void storeSpotifyUserIdInFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("spotifyUserId", userId);

        db.collection("spotifyUsers").document(userId) // Use userID as document ID
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User ID successfully written!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error writing user ID", e));
    }


    //artist data
    public void artistRequest(String token, Callback callback) {
        //OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/artists/41X1TR6hrK8Q2ZCpp2EqCz")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        /**client.newCall(request).enqueue(new com.example.spotifyproj2.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        // Process artist data, then store in Firestore
                        storeArtistDataInFirestore(jsonObject);
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                    }
                }
            }
        });*/
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(callback);
    }

    private void storeArtistDataInFirestore(JSONObject artistData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = artistData.optString("id", ""); // Assume you have user ID here

        db.collection("spotifyUsers").document(userId)
                .update("artistData", artistData.toString())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Artist data successfully written!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error writing artist data", e));
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    /**@Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }*/
}
