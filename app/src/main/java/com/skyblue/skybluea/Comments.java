package com.skyblue.skybluea;

public class Comments {

    public String notification_id;
    public String user_id;
    public String user_name;
    public String comments;
    public String profileurl;

    public Comments() {
        this.notification_id = notification_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.comments = comments;
        this.profileurl = profileurl;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getComments() {
        return comments;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
}
