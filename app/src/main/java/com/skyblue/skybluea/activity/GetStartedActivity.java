package com.skyblue.skybluea.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityGetStartedBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;

public class GetStartedActivity extends AppCompatActivity {
    private ActivityGetStartedBinding binding;
    private SessionHandler session;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_GET_STARTED = "get_started";
    private Dialog permissionDialog;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int MY_STORAGE_REQUEST_CODE = 2;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initPermissionDialog();
        binding.getStartBtn.setOnClickListener(view1 -> {permissionDialog.show();});

        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHome();
        }
    }

    private void initPermissionDialog() {
        permissionDialog = new Dialog(this);
        permissionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        permissionDialog.setContentView(R.layout.model_permission);
        permissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        permissionDialog.setCancelable(true);

          Button continueBtn = permissionDialog.findViewById(R.id.continue_btn);
          continueBtn.setEnabled(true);
          continueBtn.setOnClickListener(view -> initPermission());
    }

    private void initPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    private void storagePermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_CAMERA_REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    storagePermission();
                }
                else
                {
                    Toast.makeText(this,getString( R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
                }
            };
            case MY_STORAGE_REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                  //  redirect();
                   // Toast.makeText(this, "Storage Permission granted", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                                    SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(KEY_PREFE_GET_STARTED, "true");
                                    editor.apply();
                        redirect();
                    }
                }
                else
                {
                    //Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    redirect();
                }
            }
        }
    }

    private void redirect(){
        Intent intent = new Intent(context, HomeActivity2.class);
        startActivity(intent);
    }

    private void loadHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}