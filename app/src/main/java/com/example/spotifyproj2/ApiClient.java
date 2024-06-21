//package com.example.spotifyproj2;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import androidx.annotation.NonNull;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.HttpUrl;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import java.io.IOException;
//
//public class ApiClient {
//
//    private static final String TAG = "ApiClient";
//    private OkHttpClient client;
//    private Context context;
//
//    public ApiClient(Context context) {
//        this.client = new OkHttpClient();
//        this.context = context;
//    }
//
//    private String getAccessToken() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("SpotifyPreferences", Context.MODE_PRIVATE);
//        return sharedPreferences.getString("accessToken", null);
//    }
//
//    public void fetchSpotifyData(String endpoint, FetchDataCallback callback) {
//        String accessToken = getAccessToken();
//        if (accessToken == null) {
//            callback.onError(new Exception("Access Token not available."));
//            return;
//        }
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/me/" + endpoint).newBuilder();
//        String url = urlBuilder.build().toString();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + accessToken)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e(TAG, "Failed to fetch data: ", e);
//                callback.onError(e);
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    callback.onError(new IOException("Unexpected code " + response));
//                } else {
//                    callback.onSuccess(response.body().string());
//                }
//            }
//        });
//    }
//
//    public interface FetchDataCallback {
//        void onSuccess(String data);
//        void onError(Exception e);
//    }
//}
package com.example.spotifyproj2;

import android.content.Context;
import android.util.Log;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import java.io.IOException;

public class ApiClient {
    private OkHttpClient httpClient;
    private String baseUrl = "https://api.spotify.com/v1/";
    private Context context;

    public ApiClient(Context context) {
        this.httpClient = new OkHttpClient();
        this.context = context;
    }

    public void fetchData(String endpoint, Callback callback) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            Log.e("ApiClient", "Access Token is null");
            callback.onFailure(null, new IOException("Access token is unavailable. Please re-authenticate."));
            return;
        }

        HttpUrl url = HttpUrl.parse(baseUrl + endpoint).newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        httpClient.newCall(request).enqueue(callback);
    }

    private String getAccessToken() {
        return context.getSharedPreferences("SpotifyPreferences", Context.MODE_PRIVATE).getString("accessToken", null);
    }
}

