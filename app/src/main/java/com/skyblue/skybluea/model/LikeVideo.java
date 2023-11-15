package com.skyblue.skybluea.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LikeVideo {

    @SerializedName("status")
    public String status;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("id")
    public String id;


    public class data {
        public void setUser_id(String user_id){
        }

    }
}
