package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

public class MessagePhotoViewActivity extends AppCompatActivity {

    ZoomageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_photo_view);

        imageView = findViewById(R.id.imageView);

        Glide
                .with(MessagePhotoViewActivity.this)
                .load(getIntent().getStringExtra("image"))
                .placeholder(R.drawable.placeholder_commen)
                .into(imageView);
    }
}
