package com.skyblue.skybluea;

public class Post {

    public String post_id;
    public String post_user_name;
    public String post_image_url;
    public String post_text;
    public String post_user_id;
    public String profileurl;
    public String status;
    public String likes;
    public String comments;
    public String time_date;
    public String user_cover;
    public String placeholder;
    public String media_type;
    public String video_url;
    public String total_views;

    public Post() {
        this.post_id = post_id;
        this.post_user_name = post_user_name;
        this.post_image_url = post_image_url;
        this.post_text = post_text;
        this.post_user_id = post_user_id;
        this.profileurl = profileurl;
        this.status = status;
        this.likes = likes;
        this.comments = comments;
        this.time_date = time_date;
        this.user_cover = user_cover;
        this.placeholder = placeholder;
        this.media_type = media_type;
        this.video_url = video_url;
        this.total_views = total_views;
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

    public String getStatus() {
        return status;
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
    public String getMedia_type() {
        return media_type;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }
}
