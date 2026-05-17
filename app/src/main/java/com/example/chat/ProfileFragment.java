package com.example.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {
    private TextView nameText, emailText, usernameText;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // UI Components
        nameText = view.findViewById(R.id.profile_name);
        emailText = view.findViewById(R.id.profile_email);
        usernameText = view.findViewById(R.id.profile_username);

        // Firebase Initialization
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            emailText.setText("Email: " + user.getEmail());
            loadUserProfile(user.getUid());  // Firestore से यूजर डेटा लाना
        }

        return view;
    }

    private void loadUserProfile(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String username = document.getString("username");

                    nameText.setText("Name: " + name);
                    usernameText.setText("Username: " + username);
                } else {
                    Toast.makeText(getContext(), "User data not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
