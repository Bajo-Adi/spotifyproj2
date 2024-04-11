package com.example.spotifyproj2;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpotifyAuthorization extends AppCompatActivity {

    public static final String CLIENT_ID = "d9dda103f124418d9ef669778cd7dda8";
    public static final String REDIRECT_URI = "spotifyproj2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String mAccessToken, mAccessCode;


    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";

    public SpotifyAuthorization() {
        //Default constructor
    }
    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken(Activity activity, AuthorizationCallback callback) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request);

        callback.onAuthorizationStarted();
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode(Activity activity, AuthorizationCallback callback) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { SCOPES }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }*/

    /**
     * This function retrieves the result from the authorization request and passes on the token
     * if successful
     * @param requestCode
     * @param resultCode
     * @param data
     * @param callback
     */
    public void handleTokenAuthorizationResult(int requestCode, int resultCode, Intent data, AuthorizationCallback callback) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            // Handle the access token, for example, pass it to the callback.
            callback.onAuthorizationCompleted(mAccessToken);
        } else {
            // Handle other cases if needed
            callback.onAuthorizationFailed(response.getError());
        }
    }

    /**
     * This function would ideally handle the authorization request for the access code and pass on
     * the code appropriately
     * @param requestCode
     * @param resultCode
     * @param data
     * @param callback
     */
    public void handleCodeAuthorizationResult(int requestCode, int resultCode, Intent data, AuthorizationCallback callback) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            // Handle the access code
            callback.onAuthorizationCompleted(mAccessCode);
        } else {
            // Handle other cases if needed
            callback.onAuthorizationFailed(response.getError());
        }
    }

    /**
     * Callback interface to handle authorization results
     */
    public interface AuthorizationCallback {
        void onAuthorizationStarted();
        void onAuthorizationCompleted(String accessToken);
        void onAuthorizationFailed(String errorMessage);
    }


    /**
     * Get user profile
     * This method will get the user profile using the token
     */

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }
}
