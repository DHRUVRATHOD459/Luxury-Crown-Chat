package com.example.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> userList;
    private EditText searchInput;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        searchInput = view.findViewById(R.id.search_input);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(), userList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // TextWatcher for search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    searchUsers(s.toString().trim());
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchUsers(String query) {
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    UserModel user = doc.toObject(UserModel.class);

                    if (user != null && user.getUsername() != null) {
                        user.setUserId(doc.getId());

                        if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            } else {
                System.out.println("❌ Firestore Query Failed: " + task.getException());
            }

            if (userList.isEmpty()) {
                Toast.makeText(getContext(), "कोई यूजर नहीं मिला!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
