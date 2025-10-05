package com.quinpoint.model;

public class Message {
    private final String userId;      // the user this message belongs to (client or advisor id)
    private final String timestamp;   // simple string timestamp
    private final String senderLabel; // "Advisor" or "You"
    private final String text;

    public Message(String userId, String timestamp, String senderLabel, String text) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.senderLabel = senderLabel;
        this.text = text;
    }

    // convenience constructor with now timestamp
    public Message(String userId, String senderLabel, String text) {
        this(userId, java.time.LocalDateTime.now().toString(), senderLabel, text);
    }

    public String getUserId() { return userId; }
    public String getTimestamp() { return timestamp; }
    public String getSenderLabel() { return senderLabel; }
    public String getText() { return text; }
}
