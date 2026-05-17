package com.example.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;

import java.util.*;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatsListAdapter chatsListAdapter;
    private List<UserModel> userList;
    private FirebaseUser firebaseUser;
    private DatabaseReference chatsRef;
    private FirebaseFirestore firestore;

    private Map<String, Integer> unreadCounts;
    private Map<String, Long> latestMessageMap;
    private Set<String> allChatUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        unreadCounts = new HashMap<>();
        latestMessageMap = new HashMap<>();
        allChatUsers = new HashSet<>();

        chatsListAdapter = new ChatsListAdapter(getContext(), userList);
        recyclerView.setAdapter(chatsListAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        firestore = FirebaseFirestore.getInstance();

        loadChats();

        return view;
    }

    private void loadChats() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allChatUsers.clear();
                unreadCounts.clear();
                latestMessageMap.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);
                    if (chat == null) continue;

                    String myId = firebaseUser.getUid();
                    String userId = null;

                    if (chat.getSender().equals(myId)) {
                        userId = chat.getReceiver();
                    } else if (chat.getReceiver().equals(myId)) {
                        userId = chat.getSender();

                        // Count unread messages
                        if (!chat.isSeen()) {
                            int count = unreadCounts.getOrDefault(userId, 0);
                            unreadCounts.put(userId, count + 1);
                        }
                    }

                    if (userId != null) {
                        allChatUsers.add(userId);
                        long lastTime = latestMessageMap.getOrDefault(userId, 0L);
                        latestMessageMap.put(userId, Math.max(lastTime, chat.getTimestamp()));
                    }
                }

                fetchUserDetailsFromFirestore(new ArrayList<>(allChatUsers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchUserDetailsFromFirestore(List<String> userIds) {
        userList.clear();

        for (String userId : userIds) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            if (user != null) {
                                user.setUserId(userId);
                                user.setUnreadCount(unreadCounts.getOrDefault(userId, 0));
                                user.setLastMessageTimestamp(latestMessageMap.getOrDefault(userId, 0L));
                                userList.add(user);

                                // Sort latest message first
                                Collections.sort(userList, (u1, u2) ->
                                        Long.compare(u2.getLastMessageTimestamp(), u1.getLastMessageTimestamp()));

                                chatsListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }
}
