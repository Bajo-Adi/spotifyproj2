package com.example.spotifyproj2;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNewAccount extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth

        // Initialize UI components
        EditText emailEditText = findViewById(R.id.editTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        EditText retypePasswordEditText = findViewById(R.id.editTextReTypePassword);
        Button createAccountButton = findViewById(R.id.buttonCreateAccount);

        createAccountButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String retypePassword = retypePasswordEditText.getText().toString().trim();

            if (!password.equals(retypePassword)) {
                Toast.makeText(CreateNewAccount.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new user
            createNewUser(email, password);
        });
    }

    private void createNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(CreateNewAccount.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                        // Redirect to login or main activity as needed
                        // startActivity(new Intent(CreateNewAccount.this, LoginActivity.class));
                        // finish();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(CreateNewAccount.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}