package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<UserModel> userList;

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
        sortUsersByLastMessage();
    }

    private void sortUsersByLastMessage() {
        Collections.sort(userList, (u1, u2) ->
                Long.compare(u2.getLastMessageTimestamp(), u1.getLastMessageTimestamp()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        if (user == null) return;

        holder.username.setText(user.getUsername());

        // 🕒 Last message time
        if (user.getLastMessageTimestamp() > 0) {
            String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date(user.getLastMessageTimestamp()));
            holder.lastMessageTime.setText(formattedTime);
            holder.lastMessageTime.setVisibility(View.VISIBLE);
        } else {
            holder.lastMessageTime.setVisibility(View.GONE);
        }

        // 🔴 Unread count
        if (user.getUnreadCount() > 0) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(String.valueOf(user.getUnreadCount()));
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            if (user.getUserId() != null && !user.getUserId().isEmpty()) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, unreadCount, lastMessageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            unreadCount = itemView.findViewById(R.id.unread_count);
            lastMessageTime = itemView.findViewById(R.id.last_message_time);
        }
    }
}
