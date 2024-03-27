package com.skyblue.skybluea.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.skyblue.skybluea.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("UPDATE TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null)
        {
            final String title = remoteMessage.getNotification().getTitle();
            final String body = remoteMessage.getNotification().getBody();
            String url = "";

            if (remoteMessage.getData() != null)

                url = remoteMessage.getData().get("image");
            if (!TextUtils.isEmpty(url))
            {
                final String finalUrl = url;

                new Handler(Looper.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get()
                                        .load(finalUrl)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                showNotification(MyFirebaseService.this,
                                                        title,body,null,bitmap);
                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });
                            }
                        });
            }

            else
                showNotification(MyFirebaseService.this,title,body,null,null);
        }
        else
        {
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("body");
            String url = remoteMessage.getData().get("image");
            if (!TextUtils.isEmpty(url))
            {
                final String finalUrl = url;

                new Handler(Looper.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get()
                                        .load(finalUrl)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                showNotification(MyFirebaseService.this,
                                                        title,body,null,bitmap);
                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });
                            }
                        });
            }
            else
                showNotification(MyFirebaseService.this, title,body,null,null);
        }
    }

    private void showNotification(Context context,
                                  String title,
                                  String body,
                                  Intent pendingIntent,
                                  Bitmap bitmap)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();
        String channelId = "skyblue-911";
        String channelName = "SKYBLUE";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder;
        if (bitmap != null)
            builder = new NotificationCompat.Builder(context , channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle(title)
                    .setContentText(body);
        else
            builder = new NotificationCompat.Builder(context , channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body);
        if (pendingIntent != null)
        {
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntent(pendingIntent);
            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        }
        notificationManager.notify(notificationId,builder.build());
    }
}
