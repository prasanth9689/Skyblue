package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoViewManager extends AppCompatActivity {
    PlayerView playerView ;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view_manager);
    }


    public void playerView(String url) {
        SimpleExoPlayer player;

        player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultTrackSelector());

        playerView.setPlayer(player);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Skyblue"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url));

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    public void testMethod(String test) {

        Toast.makeText(context, test , Toast.LENGTH_SHORT ).show();
    }


}