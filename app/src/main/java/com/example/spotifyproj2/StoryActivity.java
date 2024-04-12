package com.example.spotifyproj2;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.shts.android.storiesprogressview.StoriesProgressView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import com.example.spotifyproj2.WrappedData;
import okhttp3.Response;
import android.media.MediaPlayer;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, Serializable {

    private MediaPlayer mediaPlayer;
    private StoriesProgressView storiesProgressView;
    private LinearLayout layout_fav_artist, layout_fav_track, layout_fav_albums;
    private ImageView iv_background, iv_artist_1, iv_artist_2, iv_artist_3, iv_artist_4, iv_artist_5, iv_track_1, iv_track_2, iv_track_3, iv_track_4, iv_track_5, iv_album_1,iv_album_2, iv_album_3, iv_album_4, iv_album_5;
    private static final String TAG = "StoryActivity";
    private OkHttpClient httpClient = new OkHttpClient();
    private int content;
    private ApiClient client;
    private String accessToken;
    private WrappedData wrapped_info;
    private Gson gson;
    private Boolean toBeSaved;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView tv_artist_1, tv_artist_2, tv_artist_3, tv_artist_4, tv_artist_5, tv_track_name_1, tv_track_name_2, tv_track_name_3, tv_track_name_4, tv_track_name_5, tv_track_artist_1, tv_track_artist_2, tv_track_artist_3, tv_track_artist_4, tv_track_artist_5, tv_popular_song_name, tv_popular_song_album, tv_popular_song_artist, tv_album_name_1, tv_album_name_2, tv_album_name_3, tv_album_name_4, tv_album_name_5;

    long pressTime = 0L;

    private final int[] resources = new int[]{
            R.drawable.blue_back,
            R.drawable.red_back,
            R.drawable.green_back
    };

    private ArrayList<JsonArray> string_resources;
    private ArrayList<String> previewURL;
    long limit = 5000L;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };
    private void changeStory() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();

        try {
            String songUrl = previewURL.get(content); // Calculate next story index
            mediaPlayer.setDataSource(this, Uri.parse(songUrl));
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        mediaPlayer = new MediaPlayer();
        wrapped_info =  null;

        Intent intent = getIntent();
        ArrayList<String> wrappedInfoJsonStrings = intent.getStringArrayListExtra("wrapped_info");
        if (wrappedInfoJsonStrings != null && !wrappedInfoJsonStrings.isEmpty()) {
            try {
                JsonObject root = JsonParser.parseString(wrappedInfoJsonStrings.get(0)).getAsJsonObject();
                if (root != null && root.has("items")) {
                    JsonArray items = root.getAsJsonArray("items");

                    JsonArray favoriteArtists = new JsonArray();
                    JsonArray previewTracks = new JsonArray();
                    JsonArray albums = new JsonArray();

                    for (JsonElement item : items) {
                        JsonObject track = item.getAsJsonObject().getAsJsonObject("track");
                        if (track != null) {
                            previewTracks.add(track.get("name").getAsString());

                            JsonObject album = track.getAsJsonObject("album");
                            if (album != null) {
                                albums.add(album.get("name").getAsString());
                            }

                            JsonArray artists = track.getAsJsonArray("artists");
                            for (JsonElement artistElement : artists) {
                                JsonObject artist = artistElement.getAsJsonObject();
                                if (artist != null) {
                                    favoriteArtists.add(artist.get("name").getAsString());
                                }
                            }
                        }
                    }


                    wrapped_info.setFavoriteArtists(favoriteArtists);
                    wrapped_info.setPreviewTracks(previewTracks);
                    wrapped_info.setAlbums(albums);
                }
            } catch (Exception e) {
                Log.e("StoryActivity", "Error parsing JSON data: " + e.getMessage());
                finish();
            }
        } else {
            Log.e("StoryActivity", "No WrappedData found in intent extras");
            finish();
        }


        toBeSaved = intent.getBooleanExtra("toBeSaved", false);
        //Log.d(TAG, "onCreate: Wrapped Info" + wrapped_info.getFavoriteTracks().toString());

        try {
            string_resources.add(wrapped_info.getFavoriteArtists());
            string_resources.add(wrapped_info.getFavoriteTracks());


            JsonArray temp = new JsonArray();
            temp.add(wrapped_info.getTracksSaved());
            string_resources.add(temp);

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getLocalizedMessage() );
        }



        for(int i = 0; i < wrapped_info.getPreviewTracks().size(); i++) {
            previewURL.add(i, wrapped_info.getPreviewTracks().get(i).getAsString());
        }

        Log.d(TAG, "onCreate: Preview Tracks" + previewURL.toString());


        iv_background = findViewById(R.id.image);

        layout_fav_track = findViewById(R.id.layout_fav_tracks);
        layout_fav_artist = findViewById(R.id.layout_fav_artists);
        layout_fav_albums = findViewById(R.id.layout_fav_albums);

        iv_artist_1 = findViewById(R.id.iv_artist_1);
        iv_artist_2 = findViewById(R.id.iv_artist_2);
        iv_artist_3 = findViewById(R.id.iv_artist_3);
        iv_artist_4 = findViewById(R.id.iv_artist_4);
        iv_artist_5 = findViewById(R.id.iv_artist_5);

        tv_artist_1 = findViewById(R.id.tv_artist_1);
        tv_artist_2 = findViewById(R.id.tv_artist_2);
        tv_artist_3 = findViewById(R.id.tv_artist_3);
        tv_artist_4 = findViewById(R.id.tv_artist_4);
        tv_artist_5 = findViewById(R.id.tv_artist_5);

        iv_track_1 = findViewById(R.id.iv_track_1);
        iv_track_2 = findViewById(R.id.iv_track_2);
        iv_track_3 = findViewById(R.id.iv_track_3);
        iv_track_4 = findViewById(R.id.iv_track_4);
        iv_track_5 = findViewById(R.id.iv_track_5);

        iv_album_1 = findViewById(R.id.iv_album_1);
        iv_album_2 = findViewById(R.id.iv_album_2);
        iv_album_3 = findViewById(R.id.iv_album_3);
        iv_album_4 = findViewById(R.id.iv_album_4);
        iv_album_5 = findViewById(R.id.iv_album_5);

        tv_track_artist_1 = findViewById(R.id.tv_track_artist_1);
        tv_track_artist_2 = findViewById(R.id.tv_track_artist_2);
        tv_track_artist_3 = findViewById(R.id.tv_track_artist_3);
        tv_track_artist_4 = findViewById(R.id.tv_track_artist_4);
        tv_track_artist_5 = findViewById(R.id.tv_track_artist_5);

        tv_track_name_1 = findViewById(R.id.tv_track_name_1);
        tv_track_name_2 = findViewById(R.id.tv_track_name_2);
        tv_track_name_3 = findViewById(R.id.tv_track_name_3);
        tv_track_name_4 = findViewById(R.id.tv_track_name_4);
        tv_track_name_5 = findViewById(R.id.tv_track_name_5);


        tv_album_name_1 = findViewById(R.id.tv_album_1);
        tv_album_name_2 = findViewById(R.id.tv_album_2);
        tv_album_name_3 = findViewById(R.id.tv_album_3);
        tv_album_name_4 = findViewById(R.id.tv_album_4);
        tv_album_name_5 = findViewById(R.id.tv_album_5);

//        iv_popular_track = findViewById(R.id.iv_popular_track);
//        tv_popular_song_name = findViewById(R.id.tv_popular_song_name);
//        tv_popular_song_album = findViewById(R.id.tv_popular_song_album);
//        tv_popular_song_artist = findViewById(R.id.tv_popular_song_artist);

        iv_background.setImageResource(resources[content]);
        displayUserInfo(string_resources.get(content));
        storiesProgressView = findViewById(R.id.stories);
        Log.d(TAG, "onCreate: " + storiesProgressView);
        storiesProgressView.setStoriesCount(resources.length); // <- set stories
        storiesProgressView.setStoryDuration(5000L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories();

        content = 0;

        try {
            mediaPlayer.setDataSource(this, Uri.parse(previewURL.get(content)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediaPlayer.start();

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
        iv_background.setImageResource(resources[++content]);
        displayUserInfo(string_resources.get(content));
        changeStory();



    }

    private void displayUserInfo(JsonArray jsonArray){

        try {
            Log.d(TAG, "displayUserInfo: " + jsonArray);
            switch (content) {
                case 0:
                    displayFavArtistInfo(jsonArray);
                    break;
                case 1:
                    displayFavTrackInfo(jsonArray);
                    break;
                case 2:
                    displayFavAlbumInfo(jsonArray);
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

//    private void displayPopularTrack(JsonArray jsonArray) {
//        layout_fav_albums.setVisibility(View.VISIBLE);
//        layout_fav_track.setVisibility(View.INVISIBLE);
//
//        Log.d(TAG, "displayPopularTrack: " + jsonArray.get(0));
//
//        Picasso.get().load(jsonArray.get(0).getAsJsonObject().get("url").getAsString()).into(iv_popular_track);
//        tv_popular_song_name.setText(jsonArray.get(0).getAsJsonObject().get("name").getAsString());
//        tv_popular_song_artist.setText(jsonArray.get(0).getAsJsonObject().get("artist").getAsString());
//        tv_popular_song_album.setText(jsonArray.get(0).getAsJsonObject().get("album").getAsString());
//    }

    private void displayFavTrackInfo(JsonArray items) {
        layout_fav_artist.setVisibility(View.INVISIBLE);
        layout_fav_track.setVisibility(View.VISIBLE);

        tv_track_name_1.setText(items.get(0).getAsJsonObject().get("name").getAsString());
        tv_track_name_2.setText(items.get(1).getAsJsonObject().get("name").getAsString());
        tv_track_name_3.setText(items.get(2).getAsJsonObject().get("name").getAsString());
        tv_track_name_4.setText(items.get(3).getAsJsonObject().get("name").getAsString());
        tv_track_name_5.setText(items.get(4).getAsJsonObject().get("name").getAsString());

        tv_track_artist_1.setText(items.get(0).getAsJsonObject().get("artist").getAsString());
        tv_track_artist_2.setText(items.get(1).getAsJsonObject().get("artist").getAsString());
        tv_track_artist_3.setText(items.get(2).getAsJsonObject().get("artist").getAsString());
        tv_track_artist_4.setText(items.get(3).getAsJsonObject().get("artist").getAsString());
        tv_track_artist_5.setText(items.get(4).getAsJsonObject().get("artist").getAsString());

        Log.d(TAG, "displayFavTrackInfo: " + items.get(0).getAsJsonObject().get("url").toString());

        Picasso.get().load(items.get(0).getAsJsonObject().get("url").getAsString()).into(iv_track_1);
        Picasso.get().load(items.get(1).getAsJsonObject().get("url").getAsString()).into(iv_track_2);
        Picasso.get().load(items.get(2).getAsJsonObject().get("url").getAsString()).into(iv_track_3);
        Picasso.get().load(items.get(3).getAsJsonObject().get("url").getAsString()).into(iv_track_4);
        Picasso.get().load(items.get(4).getAsJsonObject().get("url").getAsString()).into(iv_track_5);

    }

    private void displayFavArtistInfo(JsonArray array) throws JSONException {
        tv_artist_1.setText(array.get(0).getAsJsonObject().get("name").getAsString());
        tv_artist_2.setText(array.get(1).getAsJsonObject().get("name").getAsString());
        tv_artist_3.setText(array.get(2).getAsJsonObject().get("name").getAsString());
        tv_artist_4.setText(array.get(3).getAsJsonObject().get("name").getAsString());
        tv_artist_5.setText(array.get(4).getAsJsonObject().get("name").getAsString());

        Picasso.get().load(array.get(0).getAsJsonObject().get("url").getAsString()).into(iv_artist_1);
        Picasso.get().load(array.get(1).getAsJsonObject().get("url").getAsString()).into(iv_artist_2);
        Picasso.get().load(array.get(2).getAsJsonObject().get("url").getAsString()).into(iv_artist_3);
        Picasso.get().load(array.get(3).getAsJsonObject().get("url").getAsString()).into(iv_artist_4);
        Picasso.get().load(array.get(4).getAsJsonObject().get("url").getAsString()).into(iv_artist_5);

    }
    private void displayFavAlbumInfo(JsonArray array) throws JSONException {
        tv_album_name_1.setText(array.get(0).getAsJsonObject().get("name").getAsString());
        tv_album_name_2.setText(array.get(1).getAsJsonObject().get("name").getAsString());
        tv_album_name_3.setText(array.get(2).getAsJsonObject().get("name").getAsString());
        tv_album_name_4.setText(array.get(3).getAsJsonObject().get("name").getAsString());
        tv_album_name_5.setText(array.get(4).getAsJsonObject().get("name").getAsString());

        Picasso.get().load(array.get(0).getAsJsonObject().get("url").getAsString()).into(iv_album_1);
        Picasso.get().load(array.get(1).getAsJsonObject().get("url").getAsString()).into(iv_album_2);
        Picasso.get().load(array.get(2).getAsJsonObject().get("url").getAsString()).into(iv_album_3);
        Picasso.get().load(array.get(3).getAsJsonObject().get("url").getAsString()).into(iv_album_4);
        Picasso.get().load(array.get(4).getAsJsonObject().get("url").getAsString()).into(iv_album_5);

    }


    @Override
    public void onPrev() {
        iv_background.setImageResource(resources[--content]);
        displayUserInfo(string_resources.get(content));
        changeStory();

    }

    @Override
    public void onComplete() {
        if(toBeSaved) {
            showSaveDialog();
        } else {
            finish();
        }
    }

    private void showSaveDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics.
        builder.setMessage("Do you want to save this wrapped in your history?")
                .setTitle("Save Wrapped?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
                saveWrapped();
                Intent newWrappedIntent = new Intent(StoryActivity.this, NewWrappedActivity.class);
                startActivity(newWrappedIntent);
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
                dialog.dismiss();
                finish();
            }
        });

// 3. Get the AlertDialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveWrapped() {
        String uid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "saveWrapped: UID: " + uid);

        wrapped_info.setSavedDate(Calendar.getInstance().getTime());

        String wrapped = gson.toJson(wrapped_info);


        db.collection("users").document(uid).update("wrapped_info", FieldValue.arrayUnion(wrapped)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Wrapped saved");
                Toast.makeText(StoryActivity.this, "Wrapped has been saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing document" + e.toString());
            }
        });

        DocumentReference ref = db.collection("users").document(uid);
        ref.update("wrapped_info", FieldValue.arrayUnion(wrapped_info));
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}