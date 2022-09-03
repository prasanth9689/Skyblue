package com.skyblue.skybluea.account;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jsibbold.zoomage.ZoomageView;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.Utils;

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

public class AccountProfileImageView extends AppCompatActivity {
    private SessionHandler session;

    private static final int MAX_LENGTH = 20 ;
    TextView topNaveTextView;
    private ZoomageView zoomageView;
    ImageView backBtn;
    Button saveBtn;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    public static String id3 = "test_channel_03";
    NotificationManager nm;
    private ProgressDialog pDialog;
    ImageView threeDots;
    LinearLayout backgroundLayout;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    Button saveBtnBottonSheet , deleteBottomSheet;
    String delete_cover_photo_from_photo_view = "https://www.skyblue-watch.xyz/web/handle/delete_photo_from_upload_cover_activity.php";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile_image_view);

        topNaveTextView = findViewById(R.id.id_top_nav_text);
        zoomageView = (ZoomageView) findViewById(R.id.id_zoom_image_view);
        backBtn = findViewById(R.id.id_back);
        saveBtn = findViewById(R.id.id_save_btn);
        threeDots = findViewById(R.id.id_three_dots);


        bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetdialog_profile_image_view_save, null);
        bottomSheetDialog = new BottomSheetDialog(AccountProfileImageView.this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        topNaveTextView.setText(getIntent().getStringExtra("picture_title"));
        String received_uri = (getIntent().getStringExtra("user_profile"));
        String id = getIntent().getStringExtra("image_r_id");

        AccountProfileImageView context = this;

        Glide
                .with( context )
                .load(received_uri)
                //  .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.common_placeholder_image_sharp)
                .into( zoomageView );

        saveBtnBottonSheet = bottomSheetView.findViewById(R.id.id_save_button);
        deleteBottomSheet = bottomSheetView.findViewById(R.id.id_remove_button);

        saveBtnBottonSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/Skyblue");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                MediaPlayer mPlayer = MediaPlayer.create(AccountProfileImageView.this, R.raw.juntos);
                mPlayer.start();

                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(AccountProfileImageView.this,id3);
                mBuilder.setContentTitle("Picture download")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.drawable.ic_launcher_foreground);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                Uri uri = Uri.parse(received_uri);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                String fileName = received_uri.substring(received_uri.lastIndexOf("/") + 1);
                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle("Skyblue")
                        .setDescription("Downloading picture.")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Skyblue/"+fileName);// Your Custom directory name/Your Image file name

                Toast.makeText(context, getResources().getString(R.string.download_started), Toast.LENGTH_SHORT).show();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                bottomSheetDialog.dismiss();
            }
        });

        deleteBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer mPlayer = MediaPlayer.create(AccountProfileImageView.this, R.raw.juntos);
                mPlayer.start();

                String id = getIntent().getStringExtra("image_r_id");

                switch (Integer.parseInt(id )) {

                    case 0:

                    //    Toast.makeText(AccountProfileImageView.this, " 0", Toast.LENGTH_SHORT).show();
                        break;

                    case 11:

                    //    Toast.makeText(AccountProfileImageView.this, " 11 - Delete Profile Picture ", Toast.LENGTH_SHORT).show();
                        DeleteProfilePhoto();
                        break;

                    case 22:
                    //    Toast.makeText(AccountProfileImageView.this, " 22 - Delete Cover Picture", Toast.LENGTH_SHORT).show();
                        DeleteCoverPhoto();
                        break;

                    case 33:
                     //   Toast.makeText(AccountProfileImageView.this, "", Toast.LENGTH_SHORT).show();

                    case 4:

                        break;



                    default:
                        Toast.makeText(AccountProfileImageView.this, " Try again", Toast.LENGTH_SHORT).show();
                        break;

                }


            }
        });

        /*
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/Skyblue");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                MediaPlayer mPlayer = MediaPlayer.create(AccountProfileImageView.this, R.raw.juntos);
                mPlayer.start();

                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(AccountProfileImageView.this,id3);
                mBuilder.setContentTitle("Picture download")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.drawable.ic_launcher_foreground);

                new DownloadFileFromURL().execute(received_uri);
            }
        });

         */

        threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(AccountProfileImageView.this, "Clicked", Toast.LENGTH_SHORT).show();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        finish();
        overridePendingTransition(0,0);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog = new ProgressDialog(AccountProfileImageView.this);
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

           Toast.makeText(AccountProfileImageView.this, "Saved success", Toast.LENGTH_SHORT).show();

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

    private void DeleteProfilePhoto(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.PROFILE_PICTURE_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                    //    Utils.showMessage(context, ServerResponse);

                        switch (Integer.parseInt(ServerResponse )) {

                            case 0:
                                Toast.makeText(AccountProfileImageView.this , "unable to delete try again", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:
                                String profileEmty = "";
                                session.loginUserUpdateProfile(profileEmty);

                                Glide
                                        .with( context )
                                        .load("https://www.skyblue-watch.xyz/web/handle/delete_photo_from_photo_view.php")
                                        //  .apply(RequestOptions.circleCropTransform())
                                        .placeholder(R.drawable.common_placeholder_image_sharp)
                                        .into( zoomageView );

                                Toast.makeText(AccountProfileImageView.this , "Deleted success", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                                break;

                            default:
                                Toast.makeText(AccountProfileImageView.this , "", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Showing error message if something goes wrong.
                        Toast.makeText(AccountProfileImageView.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                session = new SessionHandler(getApplicationContext());
                User user = session.getUserDetails();
                String userId = user.getUser_id();

                // Adding All values to Params.
                params.put("user_id", userId);

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(AccountProfileImageView.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }
    private void DeleteCoverPhoto(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.COVER_PICTURE_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        Utils.showMessage(context, ServerResponse);

                        switch (Integer.parseInt(ServerResponse )) {

                            case 0:
                                Toast.makeText(AccountProfileImageView.this , "unable to delete try again", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:

                                String coverEmty = "";
                                session.loginUserAddCover(coverEmty);

                                Glide
                                        .with( context )
                                        .load("https://www.skyblue-watch.xyz/web/handle/delete_photo_from_photo_view.php")
                                        //  .apply(RequestOptions.circleCropTransform())
                                        .placeholder(R.drawable.common_placeholder_image_sharp)
                                        .into( zoomageView );

                                Toast.makeText(AccountProfileImageView.this , "Deleted success", Toast.LENGTH_SHORT).show();

                                bottomSheetDialog.dismiss();
                                break;

                            default:
                                Toast.makeText(AccountProfileImageView.this , "SERVER ERROR!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AccountProfileImageView.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                session = new SessionHandler(getApplicationContext());
                User user = session.getUserDetails();
                String userId = user.getUser_id();

                // Adding All values to Params.
                params.put("user_id", userId);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(AccountProfileImageView.this);
        requestQueue.add(stringRequest);
    }
}