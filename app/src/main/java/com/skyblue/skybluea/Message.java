package com.skyblue.skybluea;

public class Message {
    public String messageId;
    public String message;
    public String imageUrl;
    public String sender_id;
    public String buddy_id;
    public String buddy_profile_url;

    public Message()
    {
        this.messageId = messageId;
        this.message = message;
        this.imageUrl = imageUrl;
        this.sender_id = sender_id;
        this.buddy_id = buddy_id;
        this.buddy_profile_url = buddy_profile_url;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setBuddy_id(String buddy_id) {
        this.buddy_id = buddy_id;
    }

    public String getBuddy_id() {
        return buddy_id;
    }


}
