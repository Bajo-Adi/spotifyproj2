package com.example.spotifyproj2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        Button SettingsBtn = (Button) findViewById(R.id.settings_btn);
        Button newBtn = (Button) findViewById(R.id.new_wrapped);
        Button pastBtn = (Button) findViewById(R.id.past_wrappeds);

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
                Intent intent = new Intent(HomeScreen.this, NewWrappedActivity.class);
                startActivity(intent);
            }
        });

        pastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace MainActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(HomeScreen.this, Past_Wrapped.class);
                startActivity(intent);
            }
        });
    }
}