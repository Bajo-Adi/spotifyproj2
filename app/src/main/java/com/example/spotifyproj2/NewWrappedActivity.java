package com.example.spotifyproj2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
    private Button btnDownloadPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wrapped);

        mAccessToken = getAccessToken();
        if (mAccessToken != null) {
            fetchSpotifyData("https://api.spotify.com/v1/me/top/tracks?time_range=long_term", "song");
            fetchSpotifyData("https://api.spotify.com/v1/me/top/artists?time_range=long_term", "artist");
        } else {
            Toast.makeText(this, "Please get a token first", Toast.LENGTH_SHORT).show();
        }

        btnDownloadPDF = findViewById(R.id.download_button);
        btnDownloadPDF.setOnClickListener(v -> convertLayoutToPDF());
    }

    private void convertLayoutToPDF() {
        View content = findViewById(android.R.id.content).getRootView();
        content.setDrawingCacheEnabled(true);

        try {
            File file = new File(getExternalFilesDir(null), "NewWrapped.pdf");
            FileOutputStream fos = new FileOutputStream(file);
            content.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            content.setDrawingCacheEnabled(false);
            Toast.makeText(this, "PDF Downloaded: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAccessToken() {
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
                Log.e("SpotifyData", "Failed to fetch data: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray items = json.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String name = item.getString("name");

                            if (type.equals("song")) {
                                topTracks.add(name);
                            } else {
                                topArtists.add(name);
                            }
                        }

                        runOnUiThread(() -> {
                            if (type.equals("song")) {
                                // Update UI for songs
                            } else {
                                // Update UI for artists
                            }
                            storeDataInFirestore();
                        });
                    } catch (Exception e) {
                        Log.e("SpotifyData", "Error parsing data", e);
                    }
                } else {
                    Log.e("SpotifyData", "Unsuccessful response" + response);
                }
            }
        });
    }

    private void storeDataInFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("topTracks", topTracks);
            data.put("topArtists", topArtists);
            data.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

            db.collection("users")
                    .document(user.getUid())
                    .collection("wrappeds")
                    .document(data.get("date").toString())
                    .set(data)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Data successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
        } else {
            Log.w("Firestore", "User not logged in");
        }
    }
}
