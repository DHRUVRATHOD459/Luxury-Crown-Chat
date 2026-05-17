package com.example.chat;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText editTextLogEmail, editTextLogPassword;
    private Button logbutton;
    private TextView textViewSignUp;
    private FirebaseAuth mAuth;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        editTextLogEmail = findViewById(R.id.editTextLogEmail);
        editTextLogPassword = findViewById(R.id.editTextLogPassword);
        logbutton = findViewById(R.id.logbutton);
        textViewSignUp = findViewById(R.id.textView8_2);

        logbutton.setOnClickListener(v -> {
            String email = editTextLogEmail.getText().toString().trim();
            String password = editTextLogPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please enter email and password!", Toast.LENGTH_SHORT).show();
            } else {
                showLoadingDialog();
                loginUser(email, password);
            }
        });

        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Registration.class));
            finish();
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        // ✅ Save username if not exists
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists() || !snapshot.hasChild("username")) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("email", firebaseUser.getEmail());
                                    map.put("username", "Dhruv-001"); // 🔁 Replace with dynamic username if needed
                                    reference.setValue(map);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error if needed
                            }
                        });

                        new Handler().postDelayed(() -> {
                            hideLoadingDialog();
                            Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        }, 1000);
                    } else {
                        hideLoadingDialog();
                        Toast.makeText(Login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
