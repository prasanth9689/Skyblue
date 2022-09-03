package com.skyblue.skybluea;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PostVideoActivity extends AppCompatActivity {
    private SessionHandler session;
    EmojiEditText MessageTextBox;
    ImageView emojiIcon , emojiKeyBod ;
    ViewGroup rootView;
    final Context context = this;
    String video_url , videoName , data , GetSendString , toServerUnicodeEncoded , timeString , dateString , timeDateString;
    Button postSendButton;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    public static String id3 = "test_channel_03";
    NotificationManager notificationManager;
    PlayerView playerView;
    private SimpleExoPlayer player;
    Bitmap bitmapThumbnail;
    EmojiPopup emojiPopup;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EmojiManager.install(new IosEmojiProvider());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initVariable();
        initSetOnClickListener();

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(MessageTextBox);
        data = getIntent().getExtras().getString("img");
        video_url = String.valueOf(data);
        videoName = getIntent().getStringExtra("video_name");
      //  MessageTextBox.setText(videoName.substring(0, videoName.length() - 4));

        CreateFolder();
        createThumbnail();
        createchannel();
    }

    private void initSetOnClickListener() {
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

        postSendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                MediaPlayer mPlayer = MediaPlayer.create(PostVideoActivity.this, R.raw.juntos);
                mPlayer.start();

                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(PostVideoActivity.this,id3);
                mBuilder.setContentTitle("Skyblue")
                        .setContentText("Video uploading please wait")
                        .setSmallIcon(R.drawable.ic_launcher_foreground);

                GetSendString = Objects.requireNonNull(MessageTextBox.getText()).toString().trim();

                dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                timeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                timeDateString  = dateString +" "+timeString;

                //     toServerUnicodeEncoded = org.apache.commons.text.StringEscapeUtils.escapeJava(GetSendString);
                try {
                    byte[] data = MessageTextBox.getText().toString().getBytes("UTF-8");
                    toServerUnicodeEncoded = Base64.encodeToString(data, Base64.DEFAULT);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(PostVideoActivity.this);
                uploadAsyncTask.execute();

                Intent intent = new Intent(PostVideoActivity.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

//                if (!toServerUnicodeEncoded.isEmpty()){
//                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(PostVideoActivity.this);
//                    uploadAsyncTask.execute();
//
//                    Intent intent = new Intent(PostVideoActivity.this, Home.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }else{
//                    showMessageInSnackbar("Please type video name");
//                }
            }
        });
    }

    private void initVariable() {
        playerView = findViewById(R.id.player_view);
        postSendButton = findViewById(R.id.button_post_now);
        MessageTextBox = findViewById(R.id.message_text_box);
        emojiIcon = findViewById(R.id.emoji);
        emojiKeyBod = findViewById(R.id.emoji_keyboard);
        rootView = findViewById(R.id.main_activity_root_view);
    }

    private void CreateFolder(){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Skyblue");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something else on success
            //     Toast.makeText(MainActivity.this, "Directory Created", Toast.LENGTH_SHORT).show();
        } else {
            // Do something else on failure
            //   Toast.makeText(MainActivity.this, "Failed - Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void createchannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level

            //a urgent level channel
            notificationChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name2),  //name of the channel
                    NotificationManager.IMPORTANCE_HIGH);   //importance level
            // Configure the notification channel.
            notificationChannel.setDescription(getString(R.string.channel_description3));
            notificationChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadAsyncTask extends AsyncTask<Void, Integer, String> {

        HttpClient httpClient = new DefaultHttpClient();
        private final Context context;
        private Exception exception;

        private UploadAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            String responseString = null;

            try {
                HttpPost httpPost = new HttpPost(AppConstants.POST_VIDEO_UPLOAD);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                String videoNameTextHolder = Objects.requireNonNull(MessageTextBox.getText()).toString();

                String data = getIntent().getExtras().getString("img");
                video_url = String.valueOf(data);

                Uri fff = Uri.parse(data);
                File file_video = new File(fff.getPath());

                String filename = "hiiii";
                File f = new File(context.getCacheDir(), filename);
                f.createNewFile();

                //Convert bitmap to byte array
                // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmapThumbnail.compress(Bitmap.CompressFormat.JPEG, 60 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);

                FileBody fileBody = new FileBody(f);

                String media_type = "video";

                multipartEntityBuilder.addPart("file_video", new FileBody(file_video));
                multipartEntityBuilder.addPart("file_image", fileBody);
          //      multipartEntityBuilder.addTextBody("video_name", videoNameTextHolder);
                multipartEntityBuilder.addTextBody("media_type", media_type);
                multipartEntityBuilder.addTextBody("user_id", user.getUser_id());
                multipartEntityBuilder.addTextBody("user_name", user.getName());
                multipartEntityBuilder.addTextBody("image_about", toServerUnicodeEncoded);
                multipartEntityBuilder.addTextBody("time_date", timeDateString);

                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        new MyHttpEntity.ProgressListener() {
                            @Override
                            public void transferred(float progress) {
                                publishProgress((int) progress);
                            }
                        };

                // POST
                httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));

                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(httpEntity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
                this.exception = e;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {

            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());

        }
        @Override
        protected void onProgressUpdate(Integer... progress) {

            mBuilder.setProgress(100, progress[0], false);
            mNotifyManager.notify(id, mBuilder.build());
            super.onProgressUpdate(progress);
        }
        @Override
        protected void onPostExecute(String result) {
            mBuilder.setProgress(0,0,false);
            mBuilder.setContentText("Upload completed");
            mNotifyManager.notify(id, mBuilder.build());

            Toast.makeText(getApplicationContext(),
                    result, Toast.LENGTH_LONG).show();
        }
    }

    private void createThumbnail() {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(this, Uri.parse(video_url));
        bitmapThumbnail = mMMR.getFrameAtTime();
    }


    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultTrackSelector());

        playerView.setPlayer(player);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Skyblue"));

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
    private void showMessageInSnackbar(String message) {
        Snackbar snack = Snackbar.make(
                (((Activity) context).findViewById(android.R.id.content)),
                message, Snackbar.LENGTH_SHORT);
        snack.setDuration(Snackbar.LENGTH_SHORT);//change Duration as you need
        //snack.setAction(actionButton, new View.OnClickListener());//add your own listener
        View view = snack.getView();
        TextView tv = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);//change textColor

        TextView tvAction = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_action);
        tvAction.setTextSize(16);
        tvAction.setTextColor(Color.WHITE);

        snack.show();
    }
}