package com.skyblue.skybluea.model;

public class ChannelsModel {
    String id, userId, channelId, channelName, channelCreateDate;

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setChannelCreateDate(String channelCreateDate) {
        this.channelCreateDate = channelCreateDate;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelCreateDate() {
        return channelCreateDate;
    }
}
