package com.skyblue.skybluea.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelCreation {
    // request
    @SerializedName("channel_name")
    public String channel_name;

    @SerializedName("user_id")
    public String user_id;

    // response
    @SerializedName("message")
    public String message;

    @SerializedName("channel_id")
    public String channel_id;

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public List<Data> data = null;

    public class Data{
        @SerializedName("channel_id")
        public String channel_id;

        @SerializedName("channel_name")
        public String channel_name;

        @SerializedName("created_date")
        public String created_date;
    }

}
