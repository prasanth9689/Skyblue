package com.skyblue.skybluea.activity;

import static java.lang.String.format;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityUploadBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.service.UploadService;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UploadActivity extends AppCompatActivity implements AdsMediaSource.MediaSourceFactory  {
    private ActivityUploadBinding binding;
    public static final String APP_DATA = "/.skyblue";
    private final Context context = this;
    private User user;
    private String videoName, video_url, video_duration_min, video_duration_sec, video_duration_final;
    private EmojiPopup emojiPopup;
    private ProgressDialog pDialog;
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private PlayerView playerView;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    private  DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private int mResumeWindow;
    private long mResumePosition;
    String unicodeEncodedVideoName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SessionHandler session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        checkChannel();

        emojiPopup = EmojiPopup.Builder.fromRootView(binding.rootView).build(binding.videoName);

        dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, getString(R.string.app_name)));

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        video_url = getIntent().getStringExtra("img");

        createFolder();
        video_duration_final = getVideoDuration();
        createThumbnail(video_url);
        OnClickListener();

        String primaryChannelName = user.getChannel_primary_name();

        if (!primaryChannelName.equals("")){
            binding.primaryChannel.setText(primaryChannelName);
        }
    }

    private void checkChannel() {

    }

    private void OnClickListener() {
        binding.emoji.setOnClickListener(v -> {
            emojiPopup.toggle();
            binding.emoji.setVisibility(View.INVISIBLE);
            binding.emojiKeyboard.setVisibility(View.VISIBLE);
        });
        binding.emojiKeyboard.setOnClickListener(v -> {
            emojiPopup.dismiss();
            binding.emoji.setVisibility(View.VISIBLE);
            binding.emojiKeyboard.setVisibility(View.INVISIBLE);
        });
        binding.upload.setOnClickListener(view -> {
            videoName = binding.videoName.getText().toString();
            if (videoName.equals("")) {
                Toast.makeText(UploadActivity.this, getString(R.string.please_enter_video_name), Toast.LENGTH_SHORT).show();
            }else{
                try {
                    uploadNow();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        binding.back.setOnClickListener(v -> finish());
    }

    private String getVideoDuration() {
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(video_url));
        int duration = mp.getDuration();
        mp.release();
        /*convert millis to appropriate time*/
//        String time =   format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        video_duration_min = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(duration));
//        video_duration_sec = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        Log.e("time_duration", time);
//        if (video_duration_min.equals("0")){
//            video_duration_final = video_duration_sec;
//        }else {
//            video_duration_final = video_duration_min;
//        }


        Uri VideoUri = Uri.parse(video_url);
        File file_video = new File(VideoUri.getPath());

        MediaPlayer mp1 = MediaPlayer.create(this, Uri.parse(video_url));
        int duration1 = mp1.getDuration();
        mp.release();
        /*convert millis to appropriate time*/
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    private void uploadNow() throws IOException {
        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
        mPlayer.start();

        // video name
        videoName = Objects.requireNonNull(binding.videoName.getText()).toString().trim();

        try {
            byte[] data = binding.videoName.getText().toString().getBytes("UTF-8");
            unicodeEncodedVideoName = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String dir = getExternalFilesDir("/").getPath() + "/" + ".skyblue/";

        Toast.makeText(context, getString(R.string.video_uploading_started), Toast.LENGTH_SHORT).show();
        startService();
        Intent intent = new Intent(context, HomeActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

//        UploadService uploadService = new UploadService();
//
//        Intent serviceIntent = new Intent(this, UploadService.class);
//        uploadService.uploadNow(context, unicodeEncodedVideoName, String.valueOf(user.getUser_id()), video_duration_final, mDescription, video_url, dir, new UploadService.FileUploaderCallback() {
//            @Override
//            public void onProgressUpdate(int currentpercent, int totalpercent) {
//                Log.e("upload_", "From: Home" + currentpercent);
//            }
//        });
//        startService(serviceIntent);
    }

    public void startService(){

//        String videoName = intent.getStringExtra("video_name");
//        String userId = intent.getStringExtra("user_id");
//        String videoDuration = intent.getStringExtra("video_duration");
//        String description = intent.getStringExtra("duration");
//        String videoUrl = intent.getStringExtra("video_url");
//        String thumnailUrl = intent.getStringExtra("thumbnail_url");

        String dir = getExternalFilesDir("/").getPath() + "/" + ".skyblue/";
        String mDescription = binding.description.getText().toString();

        Intent serviceIntent = new Intent(this, UploadService.class);
        serviceIntent.putExtra("video_name", unicodeEncodedVideoName);
        serviceIntent.putExtra("user_id", user.getUser_id());
        serviceIntent.putExtra("video_duration", video_duration_final);
        serviceIntent.putExtra("video_url", video_url);
        serviceIntent.putExtra("thumbnail_url", dir);
        serviceIntent.putExtra("description", mDescription);
        serviceIntent.putExtra("duration", video_duration_final);
        serviceIntent.putExtra("channel_id", user.getChannel_primary_id());
        serviceIntent.putExtra("channel_name", user.getChannel_primary_name());

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void createThumbnail(String video_url) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(this, Uri.parse(video_url));
        Bitmap videoThumbnail = mMMR.getFrameAtTime();
        saveThumbnail(videoThumbnail);
    }

    private void saveThumbnail(Bitmap videoThumbnail) {
        File dir = getExternalFilesDir(APP_DATA);
        if(!dir.exists())
        {
            if (!dir.mkdir())
            {
                Toast.makeText(getApplicationContext(), getString(R.string.the_folder) + dir.getPath() + getString(R.string.was_not_created), Toast.LENGTH_SHORT).show();
            }
        }
        File f = new File(dir, "thumbnail" + ".jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        videoThumbnail.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //write the bytes in file
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(UploadActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(UploadActivity.this, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            Log.i("DEBUG"," haveResumePosition ");
            player.seekTo(mResumeWindow, mResumePosition);
        }
        // Uri videouri = Uri.parse(videoUrl);
        // String contentUrl = getString(videouri);
        MediaSource mVideoSource = buildMediaSource(Uri.parse(video_url));
        Log.i("DEBUG"," mVideoSource "+ mVideoSource);
        player.prepare(mVideoSource);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView == null) {
            playerView =  findViewById(R.id.exoplayer);
            initFullscreenDialog();
            initFullscreenButton();
        }
        initExoPlayer();
        if (mExoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(UploadActivity.this, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException(getString(R.string.unsupporte_type) + type);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null && player != null) {
            mResumeWindow = player.getCurrentWindowIndex();
            mResumePosition = Math.max(0, player.getContentPosition());

            player.release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    @Override
    public int[] getSupportedTypes() {
        return new int[] {C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
    }

    private void createFolder() {
        File dir = getExternalFilesDir(APP_DATA);
        if(!dir.exists())
        {
            if (!dir.mkdir())
            {
                Toast.makeText(getApplicationContext(), getString(R.string.the_folder) + dir.getPath() + getString(R.string.was_not_created), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgressBar() {
        pDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getString(R.string.uploading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}