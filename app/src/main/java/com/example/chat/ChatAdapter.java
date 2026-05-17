package com.example.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final Context context;
    private final List<Chat> chatList;
    private final String myUid;

    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;

    public ChatAdapter(Context context, List<Chat> chatList, String myUid) {
        this.context = context;
        this.chatList = chatList;
        this.myUid = myUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == MSG_TYPE_RIGHT)
                ? R.layout.chat_item_right
                : R.layout.chat_item_left;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.messageText.setText(chat.getMessage());

        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            // Show sender name for received messages
            holder.senderName.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(chat.getSender())
                    .child("username")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String name = snapshot.getValue(String.class);
                        holder.senderName.setText((name != null && !name.isEmpty()) ? name : "Unknown");
                    })
                    .addOnFailureListener(e -> holder.senderName.setText("Unknown"));
        } else {
            // Hide sender name for sent messages
            holder.senderName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getSender().equals(myUid) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderName;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderName = itemView.findViewById(R.id.sender_name);
        }
    }
}
