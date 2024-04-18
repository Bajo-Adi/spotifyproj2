package com.example.spotifyproj2;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        apiClient = new ApiClient(this);
        Button SettingsBtn = (Button) findViewById(R.id.settings_btn);
        Button newBtn = (Button) findViewById(R.id.new_wrapped);
        Button pastBtn = (Button) findViewById(R.id.past_wrappeds);
        Button newDuoWrappedBtn = (Button) findViewById(R.id.new_duo_wrapped);
        Button sharewrapped = (Button) findViewById(R.id.buttonShareWrapped);
        Button loginIntoSpotify = (Button) findViewById(R.id.token_btn);

        // Set the click listeners for the buttons
        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(HomeScreen.this, SettingsUpdateInfo.class);
                startActivity(intent);
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
//                Intent intent = new Intent(HomeScreen.this, NewWrappedActivity.class);
//                startActivity(intent);
                fetchDataAndShowStory(v);

            }
        });
        sharewrapped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(HomeScreen.this, ShareWrappedActivity.class);
                startActivity(intent);
            }
        });

        pastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(HomeScreen.this, PastWrappedActivity.class);
                startActivity(intent);
            }
        });
        newDuoWrappedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(HomeScreen.this, DuoWrapped.class);
                startActivity(intent);
            }
        });
        loginIntoSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, SpotifyAuthorization.class);
                startActivity(intent);
            }
        });
    }
    private void fetchDataAndShowStory(View view) {
        showLoading();
        String[] endpoints = new String[]{"me/top/tracks?limit=5", "me/top/artists?limit=5", "me/tracks?limit=5", "me/albums?limit=5"};
        ArrayList<String> results = new ArrayList<>();

        fetchSequentialData(endpoints, 0, results);
    }
    private void fetchSequentialData(String[] endpoints, int index, ArrayList<String> results) {
        if (index >= endpoints.length) {
            // All data fetched
            Log.d("fetching over", "fetchSequentialData: " + results.toString());
            Intent intent = new Intent(HomeScreen.this, StoryActivity.class);
            intent.putStringArrayListExtra("wrapped_info", results);
            startActivity(intent);
            hideLoading();
            return;
        }
        apiClient.fetchData(endpoints[index], new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("APIFAIL", "onFailure: " + e.getMessage());

            }


        @Override
        public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                results.add(response.body().string());
                Log.d("APISTUFF", "onResponse:" + response);
                fetchSequentialData(endpoints, index + 1, results);

            }
        });
    }

    private void showLoading() {
        progressDialog = ProgressDialog.show(this, "", "Loading...", true);
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}