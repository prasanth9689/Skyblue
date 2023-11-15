package com.skyblue.skybluea.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityVideoViewBinding;

import java.util.Objects;

public class VideoViewActivity extends AppCompatActivity {
    private ActivityVideoViewBinding binding;
    String videoUrl;
    private Context context;
    ImageView btFullScreen;
    boolean flag = false;

    ProgressBar progressBar;
    SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.btFullScreen = (ImageView) this.binding.playerView.findViewById(R.id.bt_fullscreen);
        getWindow().setFlags(1024, 1024);

        videoUrl = getIntent().getStringExtra("url");

        // we are parsing a video url
        // and parsing its video uri.
        Uri videouri = Uri.parse(videoUrl);
        binding.videoName.setText(getIntent().getStringExtra("video_name"));
        binding.userName.setText(getIntent().getStringExtra("user_name"));
        Glide
                .with(HomeActivity.context)
                .load(getIntent().getStringExtra("profile_image"))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder_person)
                .into(binding.profileImage);

        this.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance((Context) this, new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())), new DefaultLoadControl());
        MediaSource mediaSource = new ExtractorMediaSource(videouri, new DefaultHttpDataSourceFactory("exoplayer_video"), new DefaultExtractorsFactory(), (Handler) null, (ExtractorMediaSource.EventListener) null);
        binding.playerView.setPlayer(this.simpleExoPlayer);
        binding.playerView.setKeepScreenOn(true);
        this.simpleExoPlayer.prepare(mediaSource);
        this.simpleExoPlayer.setPlayWhenReady(true);
        this.simpleExoPlayer.addListener(new Player.EventListener() {
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }

            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            public void onLoadingChanged(boolean isLoading) {
            }

            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == 2) {
                    VideoViewActivity.this.progressBar.setVisibility(0);
                } else if (playbackState == 3) {
                    VideoViewActivity.this.progressBar.setVisibility(8);
                }
            }

            public void onRepeatModeChanged(int repeatMode) {
            }

            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            public void onPlayerError(ExoPlaybackException error) {
            }

            public void onPositionDiscontinuity(int reason) {
            }

            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            public void onSeekProcessed() {
            }
        });
        this.btFullScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VideoViewActivity.this.flag) {
                    VideoViewActivity.this.btFullScreen.setImageDrawable(VideoViewActivity.this.getResources().getDrawable(R.drawable.ic_fullscreen));
                    VideoViewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    VideoViewActivity.this.flag = false;
                    return;
                }
                VideoViewActivity.this.btFullScreen.setImageDrawable(VideoViewActivity.this.getResources().getDrawable(R.drawable.ic_fullscreen_exit));
                VideoViewActivity.this.setRequestedOrientation(0);
                VideoViewActivity.this.flag = true;
            }
        });

    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.simpleExoPlayer.setPlayWhenReady(false);
        this.simpleExoPlayer.getPlaybackState();
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        this.simpleExoPlayer.setPlayWhenReady(true);
        this.simpleExoPlayer.getPlaybackState();
    }
}
