package com.example.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button signupButton;
    private ProgressDialog loadingDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // Make sure XML file name is correct

        auth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.rgusername);
        emailInput = findViewById(R.id.rgemail);
        passwordInput = findViewById(R.id.rgpassword);
        signupButton = findViewById(R.id.signupbutton);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Registering...");
        loadingDialog.setCancelable(false);

        signupButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(username, email, password);
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        loadingDialog.show();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fUser = auth.getCurrentUser();
                if (fUser != null) {
                    saveUserToDatabase(fUser.getUid(), username);
                }
            } else {
                loadingDialog.dismiss();
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserToDatabase(String userId, String username) {
        UserModel user = new UserModel(userId, username, 0, System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .setValue(user)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registration.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
