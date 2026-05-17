package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {

    private Context context;
    private List<UserModel> userList;

    public ChatsListAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.usernameText.setText(user.getUsername());


        if (user.getUnreadCount() > 0) {
            holder.unreadCountText.setVisibility(View.VISIBLE);
            holder.unreadCountText.setText(String.valueOf(user.getUnreadCount()));
        } else {
            holder.unreadCountText.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("username", user.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, unreadCountText;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            unreadCountText = itemView.findViewById(R.id.unread_count_text);
        }
    }
}
