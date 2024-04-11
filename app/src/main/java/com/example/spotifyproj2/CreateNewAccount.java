package com.example.spotifyproj2;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.LoginActivity;

public class CreateNewAccount extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elements
        EditText nameEditText = findViewById(R.id.etCreateName);
        EditText usernameEditText = findViewById(R.id.etCreateUsername);
        EditText emailEditText = findViewById(R.id.etCreateEmail);
        EditText passwordEditText = findViewById(R.id.etCreatePassword);
        EditText retypePasswordEditText = findViewById(R.id.etRetypePassword);
        Button createAccountButton = findViewById(R.id.btnCreateAccount);

        createAccountButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String retypePassword = retypePasswordEditText.getText().toString().trim();

            if (validateInput(name, username, email, password, retypePassword)) {
                createNewUser(email, password, name, username);
            }
        });
    }

    private boolean validateInput(String name, String username, String email, String password, String retypePassword) {
        if (email.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields marked with *", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(retypePassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createNewUser(String email, String password, String name, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Created New Account successfully.", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        User user = new User();
                        user.setDisplayName(name);
                        user.setId(username);
                        user.setEmail(email);
                        // Additional user information
                        assert firebaseUser != null;
                        addUserToFirestore(firebaseUser.getUid(), user);

                        sendEmailVerification();
                        updateUI(firebaseUser);
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateNewAccount.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private void addUserToFirestore(String userId, User user) {
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
    }
}
