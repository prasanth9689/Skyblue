package com.skyblue.skybluea.registration;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.MainActivity;
import com.skyblue.skybluea.R;

public class InitProfileActivity extends AppCompatActivity {
    Button skipBtn;
    ImageView addPhotoCenterIcon, addPhotoCenterThumbnail;
    RelativeLayout relativeLayoutThumbnailContainer;
    TextView MobileNumberText;
    private ProgressDialog pDialog;
    private Context context = this;
    private static final int PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(InitProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        //pDialog.setMessage("Please wait...");
        pDialog.setMessage(getResources().getString(R.string.please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        pDialog.dismiss();

        setContentView(R.layout.activity_init_profile_activity);

        addPhotoCenterIcon = findViewById(R.id.add_icon_center);
        addPhotoCenterThumbnail = findViewById(R.id.thumbnail);
        relativeLayoutThumbnailContainer = findViewById(R.id.thumbnail_container);
        skipBtn = findViewById(R.id.id_skip_button);
        MobileNumberText = findViewById(R.id.id_mobile_number_text);

        addPhotoCenterIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PictureChooser();
            }
        });

        addPhotoCenterThumbnail.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // if system os is >= marshmallow , request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED ){
                        // permission not enabled , request it
                        String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        // permission already granded
                        PictureChooser();
                    }
                }
            }
        });

        relativeLayoutThumbnailContainer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // if system os is >= marshmallow , request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED ){
                        // permission not enabled , request it
                        String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        // permission already granded
                        PictureChooser();
                    }
                }
                //PictureChooser();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void PictureChooser() {

        pDialog.show();

        String uploadProfilePictureId = "2";

        Intent intent = new Intent(InitProfileActivity.this , MainActivity.class);
        intent.putExtra("image_id", uploadProfilePictureId);
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(context, Home.class);
        startActivity(intent);

//        String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
//        String get_country_name = getIntent().getStringExtra("countryname");
//        String get_country_phone_code = getIntent().getStringExtra("phonecode");
//
//        Intent intent = new Intent(InitProfileActivity.this , InitAddFriendsActivity.class);
//        intent.putExtra("phonenumberonly" , get_mobile_no);
//        intent.putExtra("countryname" , get_country_name);
//        intent.putExtra("phonecode" , get_country_phone_code);
//        overridePendingTransition(0,0);
//        startActivity(intent);
//        finish();
    }
}