package com.skyblue.skybluea.model;

public class Login {
    String mobile;
    String mobile_no_full;
    String email;
    String email_person_id;
    String message;
    String user_name;
    String user_id;
    String profile_image;
    String cover_image;
    String gender;
    String dob;
    String firebase_token;

    public void setMobile_no_full(String mobile_no_full) {
        this.mobile_no_full = mobile_no_full;
    }

    public String getMobile_no_full() {
        return mobile_no_full;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail_person_id() {
        return email_person_id;
    }

    public void setEmail_person_id(String email_person_id) {
        this.email_person_id = email_person_id;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getCover_image() {
        return cover_image;
    }

    public String getDob() {
        return dob;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public String getGender() {
        return gender;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getUser_id() {
        return user_id;
    }
}
