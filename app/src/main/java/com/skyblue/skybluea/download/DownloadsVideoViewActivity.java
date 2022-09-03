package com.skyblue.skybluea.download;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.skyblue.skybluea.R;

import java.io.UnsupportedEncodingException;

public class DownloadsVideoViewActivity extends AppCompatActivity {
    PlayerView playerView;
    private SimpleExoPlayer player;

    String data , video_name_string;

    Context context = this;
    TextView videoNameTv;
    ImageView backBtn;

    String newStringEmojidecooded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads_video_view);
        playerView = findViewById(R.id.player_view);
        videoNameTv = findViewById(R.id.video_name_tv);
        backBtn = findViewById(R.id.back);

        data = getIntent().getExtras().getString("url");
        String url = String.valueOf(data);

        video_name_string = getIntent().getStringExtra("name");

        try {
            byte[] data = Base64.decode(video_name_string, Base64.DEFAULT);
            newStringEmojidecooded = new String(data, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        videoNameTv.setText(newStringEmojidecooded);

        SimpleExoPlayer player;

        player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultTrackSelector());

        playerView.setPlayer(player);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Skyblue"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(getIntent().getExtras().getString("url")));

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}