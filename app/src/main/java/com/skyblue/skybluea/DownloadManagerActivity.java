package com.skyblue.skybluea;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class DownloadManagerActivity extends AppCompatActivity {

    Button downloadBtn , asyncTaskBtn;
    String root, fileName;

    ProgressDialog progressDialog;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    final Handler handler = new Handler();
    final int delay = 100; // 1000 milliseconds == 1 second

    TextView progressTxt , bytesDownloadTxt , totalBytesTxt , downloadSizeTxt;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    private static final String url = "https://www.skyblue-watch.xyz/web/uvideo/videos/t6eFOy9O6wFarZTtEsHtJm9hd.MP4";

    long  bytesDownloaded, filesize;
    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    public static final double SPACE_TB = 1024 * SPACE_GB;

    Button downloadWithNotificationBtn;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        downloadBtn = findViewById(R.id.id_download);
        progressTxt = findViewById(R.id.id_progress);
        bytesDownloadTxt = findViewById(R.id.id_bytes_downloaded);
        totalBytesTxt = findViewById(R.id.id_total_bytes);
        downloadSizeTxt = findViewById(R.id.id_download_size);
        asyncTaskBtn = findViewById(R.id.id_async_download);
        downloadWithNotificationBtn = findViewById(R.id.id_download_notification);

        CheckStoragePermission();

        downloadWithNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           //     Intent intent = new Intent(context, DownloadWithNotificationManger.class);
             //   startActivity(intent);
            }
        });

        asyncTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DownloadFileFromURL().execute("https://www.skyblue-watch.xyz/web/uvideo/videos/Gaj3mK2wXRDkIO8wIq3ZbpnMH.MP4");

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                mPlayer.start();

                startDownload();

            }
        });
    }

    private void CheckStoragePermission() {
        if(ContextCompat.checkSelfPermission(DownloadManagerActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(DownloadManagerActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        if(ContextCompat.checkSelfPermission(DownloadManagerActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(DownloadManagerActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void startDownload() {
        progressDialog = new ProgressDialog(context, R.style.DialogCustom);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Please wait");
        progressDialog.setProgress(0);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog
            }
        });
        progressDialog.show();

        /*

               image : https://www.gstatic.com/webp/gallery/4.sm.jpg
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/CL944tFJdMNsdwnJzE0zSj6GT.MP4      5 mb
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/t6eFOy9O6wFarZTtEsHtJm9hd.MP4      2 mb
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/ItKpvcDNQcKUlORiCKRJtuGhc.MP4
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/CaxzF0bort8qY6q5sDtLGEw1d.MP4

               image : https://www.skyblue-watch.xyz/web/upload_post_image/images/test/GOgdtGCGQK4KjTRzQUiq1xWxj.JPG
        */


        String root = Environment.getExternalStorageDirectory().toString()+ "/Skyblue/";

        String fileName = random()+".MP4";



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL myUrl = new URL(url);
            URLConnection urlConnection = myUrl.openConnection();
            urlConnection.connect();
            int file_size = urlConnection.getContentLength();
            Toast.makeText(context, String.valueOf(file_size),Toast.LENGTH_LONG).show();
            downloadSizeTxt.setText(String.valueOf(file_size));
            // Log.i("sasa", "file_size = " + file_size);
        } catch (IOException e) {
            e.printStackTrace();
        }






        AndroidNetworking.download(url,root,fileName)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress

                        final int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        final int progressUnknownSize = ((int)((100 * bytesDownloaded) / totalBytes));


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





                        //Toast.makeText(context, String.valueOf(progress), Toast.LENGTH_LONG).show();
                        progressTxt.setText(String.valueOf(progress));
                        bytesDownloadTxt.setText(String.valueOf(bytesDownloaded));
                        totalBytesTxt.setText(String.valueOf(totalBytes));



                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion

                        Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });

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


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showDialog(progress_bar_type);

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/Skyblue/thumbnail.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

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
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            // dismissDialog(progress_bar_type);
            progressDialog.dismiss();
            saveImageCanvas();



           /*

                // Displaying downloaded image into image view
                // Reading image path from sdcard
                String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
                // setting downloaded into image view
           //     my_image.setImageDrawable(Drawable.createFromPath(imagePath));
               imgBuddyUserProfilePic.setImageDrawable(Drawable.createFromPath(imagePath));
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                // final File photoFile = new File(getFilesDir(), "foo.jpg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Skyblue App");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share image using"));



            */


        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        private void saveImageCanvas() {
                /*
        File myDir=new File("/sdcard/Skyblue/.share/thumbnail.jpg");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();

 */


            File dir = Environment.getExternalStorageDirectory();
            File file2 = new File(dir, "/Skyblue/.share/thumbnail.jpg");

            Bitmap bitmap = BitmapFactory.decodeFile(file2.getAbsolutePath());


            int textSize = 20;
            String textToDraw = "Skyblue App";

            Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);


            Canvas newCanvas = new Canvas(newBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);


            Rect bounds = new Rect();
            paint.getTextBounds(textToDraw, 0, textToDraw.length(), bounds);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            //  int x = (newCanvas.getWidth() / 4);
            // int y = newBitmap.getHeight() /9;

            newCanvas.drawText(textToDraw, 10f, newBitmap.getHeight() - 20, paint);
            //   testImageView.setImageBitmap(newBitmap);


            try {
                FileOutputStream out = new FileOutputStream(file2);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            // final File photoFile = new File(getFilesDir(), "foo.jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(String.valueOf(file2))));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Downloaded from Skyblue app");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share image using"));

        }

        protected Dialog onCreateDialog(int id) {
            switch (id) {
                case progress_bar_type:
                    progressDialog = new ProgressDialog(DownloadManagerActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setMax(100);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    return progressDialog;
                default:
                    return null;
            }
        }
    }


    public static String bytes2String(long sizeInBytes) {

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if ( sizeInBytes < SPACE_KB ) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if ( sizeInBytes < SPACE_MB ) {
                return nf.format(sizeInBytes/SPACE_KB) + " KB";
            } else if ( sizeInBytes < SPACE_GB ) {
                return nf.format(sizeInBytes/SPACE_MB) + " MB";
            } else if ( sizeInBytes < SPACE_TB ) {
                return nf.format(sizeInBytes/SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes/SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }

}