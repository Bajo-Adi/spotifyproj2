package com.example.spotifyproj2;
import com.example.spotifyproj2.SpotifyAuthorization;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

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

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private TextView tokenTextView, codeTextView, profileTextView;

    SpotifyAuthorization authorize = new SpotifyAuthorization();

    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
//        codeTextView = (TextView) findViewById(R.id.code_text_view);
//        profileTextView = (TextView) findViewById(R.id.response_text_view);

        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
//        Button codeBtn = (Button) findViewById(R.id.code_btn);
//        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        Button homeBtn = (Button) findViewById(R.id.login_button);
        Button newAccBtn = (Button) findViewById(R.id.new_account_button);

        // Set the click listeners for the buttons
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        newAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(MainActivity.this, CreateNewAccount.class);
                startActivity(intent);
            }
        });


        tokenBtn.setOnClickListener((v) -> {
            authorize.getToken(this, new SpotifyAuthorization.AuthorizationCallback() {
                @Override
                public void onAuthorizationStarted() {

                }

                @Override
                public void onAuthorizationCompleted(String accessToken) {
                    Log.d("LOGIN","Login Successful");
                }

                @Override
                public void onAuthorizationFailed(String errorMessage) {
                    Log.e("LOGIN",errorMessage);
                }
            });
        });
    }
}

//        codeBtn.setOnClickListener((v) -> {
//            getCode();
//        });
//
//        profileBtn.setOnClickListener((v) -> {
//            onGetUserProfileClicked();
//        });



