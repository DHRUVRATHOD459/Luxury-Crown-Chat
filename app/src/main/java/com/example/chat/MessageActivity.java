package com.example.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    private TextView username;
    private EditText textSend;
    private Button btnSend;
    private RecyclerView recyclerView;

    private FirebaseUser fUser;
    private DatabaseReference userRef;

    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        username = findViewById(R.id.user_name);
        textSend = findViewById(R.id.text_send);
        btnSend = findViewById(R.id.send_button);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = getIntent().getStringExtra("userId");

        if (fUser == null || userId == null) {
            Log.e(TAG, "fUser or userId is null. Exiting.");
            finish();
            return;
        }

        // Get the user's username from Realtime DB
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
                    username.setText(user.getUsername());
                } else {
                    Log.w(TAG, "Username is missing in database. Data: " + snapshot);
                    username.setText("Unknown");
                }

                // Load messages and mark as seen
                readMessages(fUser.getUid(), userId);
                markMessagesAsSeen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "userRef error: " + error.getMessage());
            }
        });

        btnSend.setOnClickListener(v -> {
            String message = textSend.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(fUser.getUid(), userId, message);
                textSend.setText("");
            }
        });

        chatAdapter = new ChatAdapter(this, chatList, fUser.getUid());
        recyclerView.setAdapter(chatAdapter);
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        String messageId = ref.push().getKey();

        Chat chat = new Chat(sender, receiver, message, false, System.currentTimeMillis());
        if (messageId != null) {
            ref.child(messageId).setValue(chat);
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(sender).child("lastMessageTimestamp").setValue(chat.getTimestamp());
        userRef.child(receiver).child("lastMessageTimestamp").setValue(chat.getTimestamp());
    }

    private void readMessages(String myId, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat != null &&
                            ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                                    (chat.getReceiver().equals(userId) && chat.getSender().equals(myId)))) {
                        chatList.add(chat);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "readMessages error: " + error.getMessage());
            }
        });
    }

    private void markMessagesAsSeen() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat != null &&
                            chat.getReceiver().equals(fUser.getUid()) &&
                            chat.getSender().equals(userId) &&
                            !chat.isSeen()) {
                        ds.getRef().child("seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "markMessagesAsSeen error: " + error.getMessage());
            }
        });
    }
}
