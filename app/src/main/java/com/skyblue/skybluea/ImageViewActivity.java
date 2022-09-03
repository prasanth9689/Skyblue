package com.skyblue.skybluea;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.view.View.INVISIBLE;

public class ImageViewActivity extends AppCompatActivity {
    // changes to hide
    private SessionHandler session;

    ImageView imageViewPicture , downloadbtn, BackButton;

    final Context context = this;

    TextView postUserName;
    ImageView postUserProfileImageView;
    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_view);

        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();

        imageViewPicture  =  (ImageView) findViewById(R.id.imageView2);
        BackButton = findViewById(R.id.id_back_arrow);
        postUserName = findViewById(R.id.post_user_name);
        postUserProfileImageView = findViewById(R.id.icon_user_profile_image);
        final ProgressBar progressBar = findViewById(R.id.progressBar2);


        postUserName.setText(getIntent().getStringExtra("post_user_name"));
        Glide
                .with(ImageViewActivity.this)
                .load(getIntent().getStringExtra("post_user_profile_image_url"))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder_person)
                .into(postUserProfileImageView);


        Glide.with(context)
                .load(getIntent().getStringExtra("image_url"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.VISIBLE);
                        //    placeHolderRelativeLayout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        //  placeHolderRelativeLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageViewPicture);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        postUserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Intent intent = new Intent(context, ProfileViewActivity.class);
                intent.putExtra("user_id", getIntent().getStringExtra("post_user_id"));
                startActivity(intent);

 */
            }
        });
        postUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(context, ProfileViewActivity.class);
                intent.putExtra("user_id", getIntent().getStringExtra("post_user_id"));
                startActivity(intent);

                 */
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
