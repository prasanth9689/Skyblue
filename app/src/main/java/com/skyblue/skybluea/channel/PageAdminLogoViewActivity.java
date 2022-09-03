package com.skyblue.skybluea.channel;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jsibbold.zoomage.ZoomageView;
import com.skyblue.skybluea.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PageAdminLogoViewActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 20 ;
    TextView topNaveTextView;
    private ZoomageView zoomageView;
    ImageView backBtn;
    Button saveBtn;

    private ProgressDialog pDialog;

    ImageView threeDots;

    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;

    Button saveBtnBottonSheet , deleteBottomSheet;

    private static final String SERVER_URL_REMOVE_LOGO = "https://www.skyblue-watch.xyz/web/handle/pages/delete/delete_logo.php";
    String SERVER_URL_REMOVE_COVER = "https://www.skyblue-watch.xyz/web/handle/pages/delete/delete_cover.php";

    final Context context = this;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    String received_uri;


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;


    public static String id3 = "test_channel_03";
    NotificationManager nm;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_admin_logo_view);


        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        topNaveTextView = findViewById(R.id.id_top_nav_text);
        zoomageView = (ZoomageView) findViewById(R.id.id_zoom_image_view);
        backBtn = findViewById(R.id.id_back);
        saveBtn = findViewById(R.id.id_save_btn);
        threeDots = findViewById(R.id.id_three_dots);


        bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetdialog_logo_image_view_save, null);
        bottomSheetDialog = new BottomSheetDialog(PageAdminLogoViewActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());




        topNaveTextView.setText(getIntent().getStringExtra("picture_name"));

         received_uri = (getIntent().getStringExtra("image_url"));

        String id = getIntent().getStringExtra("image_r_id");

      //  Toast.makeText(context, received_uri,Toast.LENGTH_SHORT).show();

        //profileImageView.setImageURI(Uri.parse(received_uri));

        CheckStoragePermission();

        createchannel();

        PageAdminLogoViewActivity context = this;

        Glide
                .with( context )
                .load(received_uri)
                //  .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.common_placeholder_image_sharp)
                .into( zoomageView );


        saveBtnBottonSheet = bottomSheetView.findViewById(R.id.id_save_button);
        deleteBottomSheet = bottomSheetView.findViewById(R.id.id_remove_button);



        threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();

            }
        });


        saveBtnBottonSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AccountProfileImageView.this, "Clicked" , Toast.LENGTH_SHORT).show();

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/Skyblue");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                mPlayer.start();

                startDownload();

                bottomSheetDialog.dismiss();
            }
        });


        deleteBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                mPlayer.start();

                String id = getIntent().getStringExtra("image_r_id");

                switch (Integer.parseInt(id )) {

                    case 0:

                        Toast.makeText(context, " 0", Toast.LENGTH_SHORT).show();
                        break;

                    case 11:

                 //       Toast.makeText(context, " 11 - Delete Profile Picture ", Toast.LENGTH_SHORT).show();
                        DeleteLogoPhoto();
                        break;

                    case 22:
                  //      Toast.makeText(context, " 22 - Delete Cover Picture", Toast.LENGTH_SHORT).show();
                        DeleteCoverPhoto();
                        break;

                    case 33:
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

                    case 4:

                        break;



                    default:
                        Toast.makeText(context, " Try again", Toast.LENGTH_SHORT).show();
                        break;

                }


            }
        });

    }

    private void DeleteLogoPhoto() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL_REMOVE_LOGO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //   TextViewMobileNumberStatus.setText(ServerResponse);
                        //        Toast.makeText(ImageViewActivity.this, ServerResponse , Toast.LENGTH_SHORT).show();

                        switch (Integer.parseInt(ServerResponse )) {

                            case 0:
                                Toast.makeText(context , "unable to delete try again", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:

                                Glide
                                        .with( context )
                                        .load("https://www.skyblue-watch.xyz/web/handle/")
                                        //  .apply(RequestOptions.circleCropTransform())
                                        .placeholder(R.drawable.common_placeholder_image_sharp)
                                        .into( zoomageView );

                                Toast.makeText(context , "Deleted success", Toast.LENGTH_SHORT).show();

                                bottomSheetDialog.dismiss();
                                break;

                            default:
                                Toast.makeText(context , "", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        // Showing error message if something goes wrong.
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {

                String page_id = getIntent().getStringExtra("page_id");

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("page_id", page_id);
                //       params.put("photo_id", getIntent().getStringExtra("post_id"));


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    private void startDownload() {

        String root = Environment.getExternalStorageDirectory().toString()+ "/Skyblue/";

        String fileName = random()+".JPG";

        AndroidNetworking.download(received_uri,root,fileName)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress

                        final int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        final int progressUnknownSize = ((int)((100 * bytesDownloaded) / totalBytes));

/*
                        if (totalBytes == -1)
                        {
                            Toast.makeText(context, "Unknown size", Toast.LENGTH_SHORT).show();

                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("unknown size");
                            progressDialog.setProgress(0);
                            progressDialog.setMax(100);
                            progressDialog.setProgressNumberFormat((bytes2String(bytesDownloaded)) + "/" + (bytes2String(filesize)));

                        }else {
                            progressDialog.setProgress(0);
                            progressDialog.setMax(100);
                            progressDialog.setProgress(progress);
                        }


 */

                        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mBuilder = new NotificationCompat.Builder(context,id3);
                        mBuilder.setContentTitle("Download")
                                .setContentText("Download in progress")
                                .setSmallIcon(R.drawable.ic_launcher_foreground);


                        mBuilder.setProgress(100, progress, true);
                        mNotifyManager.notify(id, mBuilder.build());






                        //Toast.makeText(context, String.valueOf(progress), Toast.LENGTH_LONG).show();
                        /*
                        progressTxt.setText(String.valueOf(progress));
                        bytesDownloadTxt.setText(String.valueOf(bytesDownloaded));
                        totalBytesTxt.setText(String.valueOf(totalBytes));

                         */



                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion

                        Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show();
                        //     progressDialog.dismiss();


                        mBuilder.setProgress(100,100,false);
                        mBuilder.setContentText("Download completed");
                        mNotifyManager.notify(id, mBuilder.build());
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });


    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();




            pDialog = new ProgressDialog(PageAdminLogoViewActivity.this, R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Downloading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString()+ "/Skyblue";;

                System.out.println("Downloading");
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(root+"/"+random()+".jpg");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {

            Toast.makeText(PageAdminLogoViewActivity.this, "Saved success", Toast.LENGTH_SHORT).show();

            pDialog.dismiss();
        }

    }

    protected String random() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private void DeleteCoverPhoto(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL_REMOVE_COVER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //   TextViewMobileNumberStatus.setText(ServerResponse);
                        //        Toast.makeText(ImageViewActivity.this, ServerResponse , Toast.LENGTH_SHORT).show();

                        switch (Integer.parseInt(ServerResponse )) {

                            case 0:
                                Toast.makeText(context , "unable to delete try again", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:

                                Glide
                                        .with( context )
                                        .load("https://www.skyblue-watch.xyz/web/handle/")
                                        //  .apply(RequestOptions.circleCropTransform())
                                        .placeholder(R.drawable.common_placeholder_image_sharp)
                                        .into( zoomageView );

                                Toast.makeText(context , "Deleted success", Toast.LENGTH_SHORT).show();

                                bottomSheetDialog.dismiss();
                                break;

                            default:
                                Toast.makeText(context , "", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        // Showing error message if something goes wrong.
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                String page_id = getIntent().getStringExtra("page_id");

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("page_id", page_id);
                //       params.put("photo_id", getIntent().getStringExtra("post_id"));


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }



    private void CheckStoragePermission() {
        if(ContextCompat.checkSelfPermission(PageAdminLogoViewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(PageAdminLogoViewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        if(ContextCompat.checkSelfPermission(PageAdminLogoViewActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(PageAdminLogoViewActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void createchannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level


            //a urgent level channel
            mChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name2),  //name of the channel
                    NotificationManager.IMPORTANCE_LOW);   //importance level
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description3));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);

            mChannel.setShowBadge(true);
            nm.createNotificationChannel(mChannel);

            // mChannel.enableVibration(true);
            //  mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
    }



}