package com.example.spotifyproj2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

public class SettingsUpdateInfo extends AppCompatActivity {
    Spinner spinner;
    private Button submitButton, delButton;
    private TextInputEditText name, email,password, password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_update_info);

        spinner = findViewById(R.id.spinnerOptions);
        name = findViewById(R.id.etName);
        email = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        password2 = findViewById(R.id.etRetypePassword);
        submitButton = findViewById(R.id.btnSubmit);
        delButton = findViewById(R.id.btnDelete);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the newly selected value
                String selectedItem = parent.getItemAtPosition(position).toString();
                saveSelectedTime(selectedItem);
                // Do something with the selected item
                Log.d("Spinner", "Selected item: " + selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve text from TextInputLayouts
                String sname = name.getText().toString().trim();
                String semail = email.getText().toString().trim();
                String spassword = password.getText().toString().trim();
                String spassword2 = password2.getText().toString().trim();

                // Do something with the retrieved text
                Log.d("TextInput", "Text 1: " + sname);
                Log.d("TextInput", "Text 2: " + semail);
                Log.d("TextInput", "Text 3: " + spassword);
                Log.d("TextInput", "Text 4: " + spassword2);

                if (sname.isEmpty()){
                    sname = null;
                }
                if (semail.isEmpty()){
                    semail = null;
                }
                if (spassword.isEmpty()){
                    spassword = null;
                }
                if (spassword2.isEmpty()){
                    spassword2 = null;
                }
                // You can also perform validation or other operations here
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(userId);


// Update email
                if (semail != null) {
                    String newEmail = semail;
                    user.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("test", "Email address updated successfully.");
                                        Toast.makeText(SettingsUpdateInfo.this, "Email ID updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("test", "Failed to update email address: " + task.getException().getMessage());
                                        Toast.makeText(SettingsUpdateInfo.this, "Failed to update email ID: ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    docRef.update("email", semail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Handle successful update
                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                    Log.w("Firestore", "Error updating document", e);
                                }
                            });
                }
                if (spassword != null && spassword2 != null && spassword2.equals(spassword)){
                    String newPasswrod = spassword;
                    Log.d("value",newPasswrod);
                    user.updatePassword(newPasswrod)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("test", "Password updated successfully.");
                                        Toast.makeText(SettingsUpdateInfo.this, "Password successfully updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("test", "Failed to update Password: " + task.getException().getMessage());
                                        Toast.makeText(SettingsUpdateInfo.this, "Failed to update Password: ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                if (sname != null){
                    docRef.update("displayName", sname)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Handle successful update
                                    Log.d("Firestore", "Name successfully updated!");
                                    Toast.makeText(SettingsUpdateInfo.this, "Name successfully updated!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                    Log.w("Firestore", "Error updating document", e);
                                    Toast.makeText(SettingsUpdateInfo.this, "Name was not updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
    private void saveSelectedTime(String selectedItem) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time_key", selectedItem);
        editor.apply();
    }
    public static String getTime(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("time_key", "default_value");
    }

    private void showConfirmationDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to do this?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("del", "User account deleted.");
                                }
                            }
                        });
                db.collection("users").document(uid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                Toast.makeText(SettingsUpdateInfo.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error
                                Toast.makeText(SettingsUpdateInfo.this, "Error deleting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to execute when the user cancels the operation
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}