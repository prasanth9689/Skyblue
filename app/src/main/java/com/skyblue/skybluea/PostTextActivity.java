package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.UnsupportedEncodingException;

public class PostTextActivity extends AppCompatActivity {

    EmojiEditText postTextView;
    RelativeLayout color_container , color_1_Rel , color_2_Rel , color_3_Rel , color_4_Rel , color_5_Rel;
    RelativeLayout color_6_Rel , color_7_Rel , color_8_Rel , color_9_Rel , color_10_Rel , color_11_Rel;


    ImageView emojiIcon , emojiKeyBod ;
    ViewGroup rootView;

    String newStringEmojidecooded1;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());

        setContentView(R.layout.activity_post_text);

        postTextView = findViewById(R.id.id_post_text);
        color_container = findViewById(R.id.id_text_container);

        color_1_Rel = findViewById(R.id.id_color_1);
        color_2_Rel = findViewById(R.id.id_colour_2);
        color_3_Rel = findViewById(R.id.id_color_3);
        color_4_Rel = findViewById(R.id.id_color_4);
        color_5_Rel = findViewById(R.id.id_color_5);
        color_6_Rel = findViewById(R.id.id_color_6);
        color_7_Rel = findViewById(R.id.id_color_7);
        color_8_Rel = findViewById(R.id.id_color_8);
        color_9_Rel = findViewById(R.id.id_color_9);
        color_10_Rel = findViewById(R.id.id_color_10);
        color_11_Rel = findViewById(R.id.id_color_11);

        emojiIcon = findViewById(R.id.emoji);
        emojiKeyBod = findViewById(R.id.emoji_keyboard);
        rootView = findViewById(R.id.main_activity_root_view);
        backBtn = findViewById(R.id.backBtn);




        String post_text = getIntent().getStringExtra("post_text");

        postTextView.setText(post_text);

           final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(postTextView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
        emojiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emojiPopup.toggle();

                emojiIcon.setVisibility(View.INVISIBLE);
                emojiKeyBod.setVisibility(View.VISIBLE);
            }
        });
        emojiKeyBod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.dismiss();
                emojiIcon.setVisibility(View.VISIBLE);
                emojiKeyBod.setVisibility(View.INVISIBLE);

            }
        });
        color_1_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_1));

            }
        });

        color_2_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_2));
            }
        });

        color_3_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_3));

            }
        });

        color_4_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_4));

            }
        });

        color_5_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_5));

            }
        });

        color_6_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_6));

            }
        });

        color_7_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_7));

            }
        });

        color_8_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_8));

            }
        });

        color_9_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_9));

            }
        });

        color_10_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_10));

            }
        });

        color_11_Rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_container.setBackgroundColor(getResources().getColor(R.color.post_text_color_11));

            }
        });
    }

    public void ClickTagFriends(View view){

        Intent intent = new Intent(this, PostTagFriendsActivity.class);
        startActivity(intent);

    }

    public void ClickAddLocation(View view){

        Intent intent = new Intent(this, PostAddLocationActivity.class);
        startActivity(intent);

    }

    public void ClickButton(View view){

        Intent intent = new Intent(this, PostAccessSelectActivity.class);
        startActivity(intent);

    }




}