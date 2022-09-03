package com.skyblue.skybluea;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class PostPictureActivity extends AppCompatActivity {
    private SessionHandler session;
    private static final int MAX_IMAGE_SIZE = 10;
    EmojiEditText MessageTextBox;
    ImageView emojiIcon , emojiKeyBod , cmaeraIcon;
    ViewGroup rootView;
    ImageView postImageView;
    final Context context = this;
    String data;
    Button postSendButton;
    private static final String SERVER_PATH = "https://www.skyblue-watch.xyz/web/upload_post_image/upload.php";
    Bitmap bitmap;
    RelativeLayout relativeLayout, relativeLayoutCancel;
    String GetSendString;
    String  toServerUnicodeEncoded;
    String timeString , dateString , timeDateString;
    ImageView back;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    public static String id3 = "test_channel_03";
    NotificationManager nm;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EmojiManager.install(new IosEmojiProvider());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_picture);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        postImageView = findViewById(R.id.final_post_image_view);
        postSendButton = findViewById(R.id.button_post_now);
        MessageTextBox = findViewById(R.id.message_text_box);
        emojiIcon = findViewById(R.id.emoji);
        emojiKeyBod = findViewById(R.id.emoji_keyboard);
        rootView = findViewById(R.id.main_activity_root_view);

        EditText uriPath = findViewById(R.id.uriPath);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(MessageTextBox);
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

        // hide21062021
           String data = getIntent().getExtras().getString("img");
        //String data = "file:///storage/emulated/0/Download/raul-najera-TAqspfWom04-unsplash.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();

           uriPath.setText(data);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data));
            // bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));
            postImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        postSendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                MediaPlayer mPlayer = MediaPlayer.create(PostPictureActivity.this, R.raw.juntos);
                mPlayer.start();

                Intent intent = new Intent(PostPictureActivity.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(PostPictureActivity.this,id3);
                mBuilder.setContentTitle("Picture Upload")
                        .setContentText("Upload in progress")
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

                UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(PostPictureActivity.this);
                uploadAsyncTask.execute();
            }
        });

        createchannel();
    }


    private void createchannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level


            //a urgent level channel
            mChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name2),  //name of the channel
                    NotificationManager.IMPORTANCE_HIGH);   //importance level
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description3));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);
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
                HttpPost httpPost = new HttpPost(AppConstants.POST_IMAGE_UPLOAD);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                String filename = "hiiii";
                File f = new File(context.getCacheDir(), filename);
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);

                // Below Code Added July 18 2021 - dont delete

                FileBody fileBody = new FileBody(f);

                String fileNameImagePlaceholder = "placeHolderImage";
                File filePlaceHolderImage = new File(context.getCacheDir(), fileNameImagePlaceholder);
                filePlaceHolderImage.createNewFile();

                Bitmap scaledBitmapPlaceholder = scaleDown(bitmap, MAX_IMAGE_SIZE, true);

                //Convert bitmap to byte array
                // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                ByteArrayOutputStream bosPlaceholder = new ByteArrayOutputStream();
                scaledBitmapPlaceholder.compress(Bitmap.CompressFormat.JPEG, 1 /*ignored for PNG*/, bosPlaceholder);
                byte[] bitmapdataPlaceholder = bosPlaceholder.toByteArray();

                //write the bytes in file
                FileOutputStream fosPlaceholder = new FileOutputStream(filePlaceHolderImage);
                fosPlaceholder.write(bitmapdataPlaceholder);

                FileBody fileBodyPlaceholder = new FileBody(filePlaceHolderImage);

                String MEDIA_TYPE_IMAGE = "image";

                multipartEntityBuilder.addPart("file_image", fileBody);
                multipartEntityBuilder.addPart("file_image_placeholder", fileBodyPlaceholder);
                multipartEntityBuilder.addTextBody("user_id", user.getUser_id());
                multipartEntityBuilder.addTextBody("user_name", user.getName());
                multipartEntityBuilder.addTextBody("image_about", toServerUnicodeEncoded);
                multipartEntityBuilder.addTextBody("media_type", MEDIA_TYPE_IMAGE);
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
}