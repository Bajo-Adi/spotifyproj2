//package com.example.spotifyproj2;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class NewWrappedActivity extends AppCompatActivity {
//
//    // I have commented the code for now.
////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_new_wrapped);
//////        fetchWrappedData();
//////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_wrapped);
//
//
//        ArrayList<String> songs = new ArrayList<>();
//        songs.add("Song 1");
//        songs.add("Song 2");
//        songs.add("Song 3");
//        songs.add("Song 4");
//        songs.add("Song 5");
//
//        ArrayList<String> artists = new ArrayList<>();
//        artists.add("Artist 1");
//        artists.add("Artist 2");
//        artists.add("Artist 3");
//        artists.add("Artist 4");
//        artists.add("Artist 5");
//
//        ArrayList<String> albums = new ArrayList<>();
//        albums.add("Album 1");
//        albums.add("Album 2");
//        albums.add("Album 3");
//        albums.add("Album 4");
//        albums.add("Album 5");
//
//        // Set text for TextViews representing songs
//        for (int i = 0; i < songs.size(); i++) {
//            String songId = "song" + (i + 1); // Generate TextView id dynamically
//            int resId = getResources().getIdentifier(songId, "id", getPackageName()); // Get resource id
//            TextView songTextView = findViewById(resId);
//            songTextView.setText(songs.get(i));
//        }
//
//        // Set text for TextViews representing artists
//        for (int i = 0; i < artists.size(); i++) {
//            String artistId = "artist" + (i + 1); // Generate TextView id dynamically
//            int resId = getResources().getIdentifier(artistId, "id", getPackageName()); // Get resource id
//            TextView artistTextView = findViewById(resId);
//            artistTextView.setText(artists.get(i));
//        }
//
//        // Set text for TextViews representing albums
//        for (int i = 0; i < albums.size(); i++) {
//            String albumId = "album" + (i + 1); // Generate TextView id dynamically
//            int resId = getResources().getIdentifier(albumId, "id", getPackageName()); // Get resource id
//            TextView albumTextView = findViewById(resId);
//            albumTextView.setText(albums.get(i));
//        }
//    }
////    public void fetchWrappedData() {
////        WrappedFirebaseRepository wrappedRepo = new WrappedFirebaseRepository();
////        wrappedRepo.getWrappedData("userId", wrappedData -> {
////            // TODO: Update UI with the wrappedData
////            // TODO: Update UI with the wrappedData
////        });
////    }
//}











