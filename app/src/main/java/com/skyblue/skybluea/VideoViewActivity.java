package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

public class VideoViewActivity extends AppCompatActivity {
    Context context = this;
    TextView textTitle, textDiscription;
    ImageView backBtn;

    PlayerView playerView;
    private SimpleExoPlayer player;

    String video_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_video_view);

        textTitle = findViewById(R.id.id_text_title);
        textDiscription = findViewById(R.id.id_text_discription);
        backBtn = findViewById(R.id.id_back_arrow_ps);
        playerView = findViewById(R.id.player_view);

        video_url = getIntent().getStringExtra("video_url");
        String title = getIntent().getStringExtra("title");
        String discription = getIntent().getStringExtra("discription");

    //    textTitle.setText(title);
     //   textDiscription.setText(discription);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultTrackSelector());

        playerView.setPlayer(player);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Machine Task"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(video_url));

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        playerView.setPlayer(null);
        player.release();
        player = null;
    }

//    @Override
//    public void onBackPressed(){
//        if (player != null) {
//            player.setPlayWhenReady(false);
//            player.stop();
//            player.seekTo(0);
//        }
//}

}