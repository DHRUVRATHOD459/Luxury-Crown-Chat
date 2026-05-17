package com.example.chat;
public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean seen;
    private long timestamp;
    public Chat() {}

    public Chat(String sender, String receiver, String message, boolean seen, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.seen = seen;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
