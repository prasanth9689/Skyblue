package com.skyblue.skybluea.helper.session;

import java.util.Date;

public class User {
    String mobile , name , user_id , user_profile , user_cover , user_gender , user_dob , firebase_token;
    String channel_primary_id, channel_primary_name;
    Date sessionExpiryDate;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public void setUser_cover(String user_cover) {
        this.user_cover = user_cover;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public void setUser_dob(String user_dob) {
        this.user_dob = user_dob;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public void setChannel_primary_id(String channel_primary_id) {
        this.channel_primary_id = channel_primary_id;
    }

    public void setChannel_primary_name(String channel_primary_name) {
        this.channel_primary_name = channel_primary_name;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public String getUser_dob() {
        return user_dob;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getUser_cover() {
        return user_cover;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getChannel_primary_id() {
        return channel_primary_id;
    }

    public String getChannel_primary_name() {
        return channel_primary_name;
    }
}
