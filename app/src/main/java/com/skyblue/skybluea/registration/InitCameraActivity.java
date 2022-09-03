package com.skyblue.skybluea.registration;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.R;

import java.io.File;

public class InitCameraActivity extends AppCompatActivity {
    // Create pictures folder : camera difault caputre store path
    // Redmi devices not contains " Profiles " folder
    // must need check and create picture folder

    final Context context = this;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    String CAMERA = "cam";

    ImageView cameraBtn , cameraImageView;
    TextView photosTextView;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_camera);
        photosTextView = findViewById(R.id.textPhotos);
        cameraBtn = findViewById(R.id.camera_image_view);
        cameraImageView = findViewById(R.id.cameraImageView);


        // if system os is >= marshmallow , request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ){
                // permission not enabled , request it
                String [] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE);
            }
            else {
                // permission already granded
                openCamera();
            }
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if system os is >= marshmallow , request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.CAMERA) ==
                                    PackageManager.PERMISSION_DENIED ){
                        // permission not enabled , request it
                        String [] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {

                        // Create pictures folder : camera difault caputre store path
                        // Redmi devices not contains " Profiles " folder
                        // must need check and create picture folder
                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/Pictures");
                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }
                        // permission already granded
                        openCamera();
                    }
                }

            }
        });
        photosTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(context, InitPhotoSelectActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();

                 */
            }
        });

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // Camera intent
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    // handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // this method is called , when user press allow or deny
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup
                    openCamera();
                }
                else {
                    // permission was popup was denied
                    Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //set the image captured to imageView

             cameraImageView.setImageURI(image_uri);

/*
            Intent intent = new Intent(context, InitImageViewActivity.class);
            intent.putExtra("img" , image_uri.toString());
            intent.putExtra("cam" , CAMERA);
            startActivity(intent);

 */

        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
