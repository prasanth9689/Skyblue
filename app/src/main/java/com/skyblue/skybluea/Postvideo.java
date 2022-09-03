package com.skyblue.skybluea;

public class Postvideo {

    public String post_id;
    public String post_user_name;
    public String post_image_url;
    public String post_text;
    public String post_user_id;
    public String profileurl;
    public String getStatus;
    public String likes;
    public String comments;
    public String time_date;
    public String user_cover;
    public String video_url;

    public Postvideo() {
        this.post_id = post_id;
        this.post_user_name = post_user_name;
        this.post_image_url = post_image_url;
        this.post_text = post_text;
        this.post_user_id = post_user_id;
        this.profileurl = profileurl;
        this.getStatus = getStatus;
        this.likes = likes;
        this.comments = comments;
        this.time_date = time_date;
        this.user_cover = user_cover;
        this.video_url = video_url;
    }

    public String getPost_id() {
        return post_id;
    }
    public String getPost_user_name() {
        return post_user_name;
    }
    public String getPost_image_url() {
        return post_image_url;
    }
    public String getPost_text() {
        return post_text;
    }
    public String getPost_user_id() {
        return post_user_id;
    }
    public String getComments() {
        return comments;
    }
    public String getLikes() {
        return likes;
    }
    public String getGetStatus() {
        return getStatus;
    }
    public String getProfileurl() {
        return profileurl;
    }
    public String getTime_date() {
        return time_date;
    }
    public String getUser_cover() {
        return user_cover;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public void setGetStatus(String getStatus) {
        this.getStatus = getStatus;
    }
    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
    public void setPost_user_name(String post_user_name) {
        this.post_user_name = post_user_name;
    }
    public void setPost_image_url(String post_image_url) {
        this.post_image_url = post_image_url;
    }
    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }
    public void setLikes(String likes) {
        this.likes = likes;
    }
    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
    public void setPost_user_id(String post_user_id) {
        this.post_user_id = post_user_id;
    }
    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }
    public void setUser_cover(String user_cover) {
        this.user_cover = user_cover;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
