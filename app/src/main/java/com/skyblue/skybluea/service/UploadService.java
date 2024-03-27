package com.skyblue.skybluea.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.UploadActivity;
import com.skyblue.skybluea.model.UploadVideo;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service {
    // File upload
    private long totalFileUploaded = 0;
    private long totalFileLength = 0;
    private UploadService.FileUploaderCallback fileUploaderCallback;
    public static final String CHANNEL_ID = "ForegroundSrviceChannel";
    Context context = this;
    Notification notification;

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String videoNameIntent = intent.getStringExtra("video_name");
        String userIdIntent = intent.getStringExtra("user_id");
        String videoUrlIntent = intent.getStringExtra("video_url");
        String thumnailUrlIntent = intent.getStringExtra("thumbnail_url");
        String descriptionIntent = intent.getStringExtra("description");
        String durationIntent = intent.getStringExtra("duration");
        String channelIdIntent = intent.getStringExtra("channel_id");
        String channelNameIntent = intent.getStringExtra("channel_name");

        Intent notificationIntent = new Intent(this, UploadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOnlyAlertOnce(true)
                .setContentTitle("Video Uploading Foreground Service")
                .setContentText(videoNameIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // do heavy work on background thread
        // upload date
        String mDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String mTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String mTimeDate  = mDate +" "+mTime;

        // text
        assert videoNameIntent != null;
        RequestBody video_name = RequestBody.create(MediaType.parse("multipart/form-data"), videoNameIntent);
        assert userIdIntent != null;
        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), userIdIntent);
        assert durationIntent != null;
        RequestBody duration = RequestBody.create(MediaType.parse("multipart/form-data"), durationIntent);
        RequestBody date = RequestBody.create(MediaType.parse("multipart/form-data"), mDate);
        RequestBody time = RequestBody.create(MediaType.parse("multipart/form-data"), mTime);
        RequestBody timeDate = RequestBody.create(MediaType.parse("multipart/form-data"), mTimeDate);
        assert descriptionIntent != null;
        RequestBody descriptionFinal = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionIntent);
        assert channelIdIntent != null;
        RequestBody channelId = RequestBody.create(MediaType.parse("multipart/form-data"), channelIdIntent);
        assert channelNameIntent != null;
        RequestBody channelName = RequestBody.create(MediaType.parse("multipart/form-data"), channelNameIntent);

        // video file
        Uri VideoUri = Uri.parse(videoUrlIntent);
        File file_video = new File(VideoUri.getPath());
        //  RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file_video);

        ProgressRequestBody progressRequestBody = new ProgressRequestBody(file_video);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",file_video.getName(),progressRequestBody);

        // thumbnail
        File file_thumbnail = new File(thumnailUrlIntent, "thumbnail" + ".jpg");
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"),file_thumbnail);
        MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData("thumbnail",file_thumbnail.getName(),requestBody2);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<UploadVideo> call = apiInterface.uploadVideo(video_name,
                user_id ,
                duration ,
                date,
                time,
                timeDate,
                descriptionFinal,
                videoPart,
                thumbnailPart,
                channelId,
                channelName);

        call.enqueue(new Callback<UploadVideo>() {
            @Override
            public void onResponse(@NonNull Call<UploadVideo> call, @NonNull Response<UploadVideo> response) {
                UploadVideo uploadVideo = response.body();
                if (response.isSuccessful()){
                    UploadVideo uploadVideoq = response.body();
                    if (uploadVideo.message.equals("1")){
                        showUploadCompleteNotification();
                        stopForeground(true);
                        Toast.makeText(getBaseContext(), "Upload success", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadVideo> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                stopForeground(true);
            }
        });

        // stopself
        return START_NOT_STICKY;
    }

    public class ProgressRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public ProgressRequestBody(final File file){
            mFile = file;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("video/*");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLenth = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1){

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLenth));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            }finally {
                in.close();
            }
        }
    }

    private class ProgressUpdater implements Runnable{
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total){
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int) (100 * mUploaded/mTotal);
            int total_percent = (int) (100 * (totalFileUploaded + mUploaded));
            // fileUploaderCallback.onProgressUpdate(current_percent, total_percent,uploadIndex+1 );
            Log.e("upload_" , String.valueOf(current_percent));

            // Notification progressbar
            updateNotification(current_percent);
        }
    }

    private void updateNotification(int progress) {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOnlyAlertOnce(true)
                .setContentTitle("Video Uploading")
                .setContentText("Uploading: " + progress + "%")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        startForeground(1, notification);
    }

    private void showUploadCompleteNotification() {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Upload Complete")
                .setContentText("Upload has been completed successfully.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    public interface FileUploaderCallback{
        void onProgressUpdate(int currentpercent, int totalpercent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
