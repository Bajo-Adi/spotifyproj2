package com.example.spotifyproj2;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyproj2.SettingsUpdateInfo;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewWrappedActivity extends AppCompatActivity {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken; // Spotify Access Token
    private List<String> topTracks = new ArrayList<>();
    private List<String> topArtists = new ArrayList<>();
    //private Spinner spinner_house;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wrapped);
        //LayoutInflater inflater = getLayoutInflater();
        //View settingsView = inflater.inflate(R.layout.activity_settings_update_info, null);
        //setContentView(settingsView);
        //SettingsUpdateInfo settings = new SettingsUpdateInfo();
        String time = SettingsUpdateInfo.getTime(this);
        //spinner_house = findViewById(R.id.spinnerOptions);
        //String spinner_data = spinner_house.getSelectedItem().toString();
        String s1 = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term";
        String s2 = "https://api.spotify.com/v1/me/top/artists?time_range=short_term";
        Log.d("test",time);
         switch (time){
         case "1 month":
         s1 ="https://api.spotify.com/v1/me/top/tracks?time_range=short_term";
         s2 ="https://api.spotify.com/v1/me/top/artists?time_range=short_term";
         Log.d("t","entered here");
         break;
         case "6 months":
         s1 ="https://api.spotify.com/v1/me/top/tracks?time_range=medium_term";
         s2 ="https://api.spotify.com/v1/me/top/artists?time_range=medium_term";
         break;
         case "1 year":
         s1 ="https://api.spotify.com/v1/me/top/tracks?time_range=long_term";
         s2 ="https://api.spotify.com/v1/me/top/artists?time_range=long_term";
         break;
         }

        mAccessToken = getAccessToken();
        if (mAccessToken != null) {
            fetchSpotifyData(s1, "song");
            fetchSpotifyData(s2, "artist");
        }
    }

    private String getAccessToken() {
        // Retrieve the Spotify Access Token from SharedPreferences
        return getSharedPreferences("SpotifyPreferences", MODE_PRIVATE).getString("accessToken", null);
    }

    private void fetchSpotifyData(String url, final String type) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray items = json.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            String name = items.getJSONObject(i).getString("name");
                            int finalI = i;
                            runOnUiThread(() -> updateUI(type + (finalI + 1), name));
                            if (type.equals("song")) {
                                topTracks.add(name);
                            } else if (type.equals("artist")) {
                                topArtists.add(name);
                            }
                        }

                        if (type.equals("artist")) { // Store data after fetching both tracks and artists
                            storeDataInFirestore();
                        }
                    } catch (Exception e) {
                        Log.e("SpotifyData", "Failed parsing", e);
                    }
                } else {
                    Log.e("SpotifyData", "API" + responseBody);
                }
            }
        });
    }

    private void updateUI(String viewId, String text) {
        int resId = getResources().getIdentifier(viewId, "id", getPackageName());
        TextView textView = findViewById(resId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void storeDataInFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Preparing data
            Map<String, Object> data = new HashMap<>();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Dynamically retrieve the text from TextViews for songs and artists
            ArrayList<String> songs = new ArrayList<>();
            ArrayList<String> artists = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                int songResId = getResources().getIdentifier("song" + i, "id", getPackageName());
                int artistResId = getResources().getIdentifier("artist" + i, "id", getPackageName());

                TextView songTextView = findViewById(songResId);
                TextView artistTextView = findViewById(artistResId);

                if (songTextView != null) {
                    songs.add(songTextView.getText().toString());
                }
                if (artistTextView != null) {
                    artists.add(artistTextView.getText().toString());
                }
            }

            // Add the retrieved text to the data map
            data.put("topTracks", songs);
            data.put("topArtists", artists);
            data.put("date", currentDate);

            // Document reference: users/{userId}/wrappeds/{currentDate}
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("wrappeds")
                    .document(currentDate)
                    .set(data)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Wrapped data successfully written for date: " + currentDate))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing wrapped data", e));
        } else {
            Log.w("Firestore", "No user logged in");
        }
    }
}
