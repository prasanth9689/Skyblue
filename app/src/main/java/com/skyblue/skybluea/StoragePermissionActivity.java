package com.skyblue.skybluea;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class StoragePermissionActivity extends AppCompatActivity {

    Button allowButton;
    Context context = this;
    private int PERMISSION_REQUEST_CODE = 11;

    class YourAbstractClass{

        // variable to hold context
        private Context context;

//save the context recievied via constructor in a local variable

        public YourAbstractClass(Context context){  //constructor
            this.context=context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_permission);

        allowButton = (Button) findViewById(R.id.id_allow_button);

        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDevice();
            }
        });
    }

    private void checkDevice() {
        // First check device Android 11 or higher
        if (SDK_INT >= Build.VERSION_CODES.R) {

        //    showMessageInSnackbar("This device latest Android 11 and higher");
            storagePermissionSelfCheck();

        } else {
            //below android 11=======
          //  showMessageInSnackbar("his device low version below ");
            storagePermissionBelowVersions();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void storagePermissionSelfCheck() {

        /*
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            // permission not enabled , request it
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            // Requesting the permission
            //show popup to request permission
            requestPermissions(permission, PERMISSION_REQUEST_CODE);
        }

         */

        storagePermissionLatest();
    }

    private void storagePermissionLatest() {

        // latest devices permission

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                startActivity(new Intent(this,  VideoViewActivity.class));
            } else { //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void storagePermissionBelowVersions() {

        //below android 11=======
      //  showMessageInSnackbar("his device low version below ");


        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, VideoViewActivity.class));
            } else { //request for the permission
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permission
                requestPermissions(permission, PERMISSION_REQUEST_CODE);
                storagePermissionBelowVersions();
            }

        }
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