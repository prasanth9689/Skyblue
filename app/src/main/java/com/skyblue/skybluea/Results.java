package com.skyblue.skybluea;

import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("title")
    private String title;


    public Results(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
