package com.example.chat;

public class UserModel {
    private String userId;
    private String username;
    private int unreadCount;
    private long lastMessageTimestamp;


    public UserModel() {}

    public UserModel(String userId, String username, int unreadCount, long lastMessageTimestamp) {
        this.userId = userId;
        this.username = username;
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() {
        return (username != null && !username.isEmpty()) ? username : "Unknown";
    }
    public void setUsername(String username) { this.username = username; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public long getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(long lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }
}
