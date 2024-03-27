package com.skyblue.skybluea.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Search {

    // Response
    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("channel_id")
    public String channel_id;

    @SerializedName("channel_name")
    public String channel_name;

    @SerializedName("thumbnail_url")
    public String thumbnail_url;

    @SerializedName("video_url")
    public String video_url;

    @SerializedName("video_name")
    public String video_name;

    @SerializedName("description")
    public String description;

    @SerializedName("upload_date")
    public String upload_date;

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_name() {
        return video_name;
    }

    public String getDescription() {
        return description;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    @SerializedName("data")
    public List<Data> data = null;

    public class Data{
        // Request
        @SerializedName("request_access")
        public String request_access;

        @SerializedName("request_query_base64")
        public String request_query_base64;

        @SerializedName("request_user_id")
        public String request_user_id;

        @SerializedName("request_time_date")
        public String request_time_date;

        // Response
        @SerializedName("id")
        public String id;

        @SerializedName("user_id")
        public String user_id;

        @SerializedName("channel_id")
        public String channel_id;

        @SerializedName("channel_name")
        public String channel_name;

        @SerializedName("thumbnail_url")
        public String thumbnail_url;

        @SerializedName("video_url")
        public String video_url;

        @SerializedName("video_name")
        public String video_name;

        @SerializedName("description")
        public String description;

        @SerializedName("upload_date")
        public String upload_date;

    }
}
