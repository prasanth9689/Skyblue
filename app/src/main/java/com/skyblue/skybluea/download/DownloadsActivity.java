package com.skyblue.skybluea.download;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.google.android.material.snackbar.Snackbar;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.database.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class DownloadsActivity extends AppCompatActivity {

    private static final int FILE_READY_TO_DOWNLOAD = 0;
    private static final int FILE_DOWNLOAD_COMPLETED = 1;
    private static final int FILE_DOWNLOAD_PAUSED = 2;
    private static final int FILE_DOWNLOAD_ERROR = 3;

    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    public static final double SPACE_TB = 1024 * SPACE_GB;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String url = "https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4";
    private static final String FILE_NAME = "Prasanth";
    private static final String FILE_URL = "s";

    TextView progressTxt , bytesDownloadTxt , totalBytesTxt , downloadSizeTxt;
    private TextView textFileName , textDownloadSizeStatic , textDownloadProgressSize , textDownloadPercentage;
    ImageView backButton;
    ProgressBar progressBar;
    ImageButton popupMenu;
    String timeString , dateString , timeDateString;
    int file_size;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    long  bytesDownloaded, filesize;
    Context context = this;

    private DatabaseManager databaseManager;

    ArrayList<DownloadModel> arrayList;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    String TIME_DATE;

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        backButton = findViewById(R.id.back);
        linearLayout = findViewById(R.id.id_ll_notify_no_downloads);
        relativeLayout = findViewById(R.id.id_rel_main);

        databaseManager = new DatabaseManager(DownloadsActivity.this);

        //  saveDownloadsToDatabase(url, FILE_NAME , FILE_READY_TO_DOWNLOAD);
             startDownload(url, FILE_NAME);

        displayDownloads();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }





    //display notes list
    public void displayDownloads() {
        arrayList = new ArrayList<>(databaseManager.getDownloads());
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(20);

        DownloadAdapter adapter = new DownloadAdapter(getApplicationContext(), this, arrayList);
        recyclerView.setAdapter(adapter);

        if (arrayList.isEmpty())
        {
            linearLayout.setVisibility(View.VISIBLE);
            relativeLayout.setBackgroundColor((getResources().getColor(R.color.white)));
        }
    }

    private void startDownload( String url, String fileName) {


        /*

               image : https://www.gstatic.com/webp/gallery/4.sm.jpg
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/CL944tFJdMNsdwnJzE0zSj6GT.MP4      5 mb
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/t6eFOy9O6wFarZTtEsHtJm9hd.MP4      2 mb
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/ItKpvcDNQcKUlORiCKRJtuGhc.MP4
               video : https://www.skyblue-watch.xyz/web/uvideo/videos/CaxzF0bort8qY6q5sDtLGEw1d.MP4

               image : https://www.skyblue-watch.xyz/web/upload_post_image/images/test/GOgdtGCGQK4KjTRzQUiq1xWxj.JPG
        */


        String root = Environment.getExternalStorageDirectory().toString()+ "/Skyblue/";

        String fileNameFinal = fileName+".MP4";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL myUrl = new URL(url);
            URLConnection urlConnection = myUrl.openConnection();
            urlConnection.connect();
            file_size = urlConnection.getContentLength();
            Toast.makeText(context, String.valueOf(file_size),Toast.LENGTH_LONG).show();
            // downloadSizeTxt.setText(String.valueOf(file_size));
            // Log.i("sasa", "file_size = " + file_size);
        } catch (IOException e) {
            e.printStackTrace();
        }




        AndroidNetworking.download(url,root,fileNameFinal)
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress

                        final int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        final int progressUnknownSize = ((int)((100 * bytesDownloaded) / totalBytes));

                        textFileName.setText(fileNameFinal);
                        if (totalBytes == -1)
                        {
                            Toast.makeText(context, "Unknown size", Toast.LENGTH_SHORT).show();

                            //  progressDialog.setProgressNumberFormat((bytes2String(bytesDownloaded)) + "/" + (bytes2String(filesize)));

                        }else {
                            Toast.makeText(context, "Know size", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setProgress(progress);
                        downloadSizeTxt.setText(bytes2String(file_size));

                        textDownloadSizeStatic.setText(bytes2String(file_size));
                        textDownloadProgressSize.setText((bytes2String(bytesDownloaded)));
                        textDownloadPercentage.setText(String.valueOf(progress));


                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
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