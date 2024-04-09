package com.example.spotifyproj2;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class SpotifyDataFetch {

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Fetch user profile data from Spotify.
     * @param accessToken The access token required for Spotify API requests.
     * @param callback Callback to handle the response.
     */
    public void fetchUserProfile(String accessToken, SpotifyApiResponseCallback callback) {
        String url = "https://api.spotify.com/v1/me"; // Spotify API endpoint for user profile
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        callback.onSuccess(jsonObject);
                    } else {
                        callback.onError(new IOException("Unexpected response code: " + response.code()));
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Callback interface for Spotify API responses.
     */
    public interface SpotifyApiResponseCallback {
        void onSuccess(JSONObject response);
        void onError(Exception e);
    }
}

