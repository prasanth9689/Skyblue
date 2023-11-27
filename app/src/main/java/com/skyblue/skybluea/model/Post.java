package com.skyblue.skybluea.model;

import com.google.gson.annotations.SerializedName;

public class Post {
    String video_name, profile_url, user_name, thumbnail_url, video_url, user_id, duration, post_id, time_date;
    String channel_id, channel_name, likes, like_status, comments, total_views;

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLikes() {
        return likes;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getTime_date() {
        return time_date;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getDuration() {
        return duration;
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
}
