package com.skyblue.skybluea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.account.MyVideoListActivity;
import com.skyblue.skybluea.activity.registration.NameActivity;
import com.skyblue.skybluea.helper.custom_media_picker.MediaPickerActivity;
import com.skyblue.skybluea.helper.session.SessionHandler;

public class SplashActivity extends AppCompatActivity {
    private SessionHandler session;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_GET_STARTED = "get_started";
    Handler handler;
    Context context = this;

    String test2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionHandler(getApplicationContext());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String status = sp.getString(KEY_PREFE_GET_STARTED, "");

                if (status.equals("true")){
                    loadHome();
                }else {
                    Intent i = new Intent(getApplicationContext(), GetStartedActivity.class);
                    startActivity(i);
                    finish();
                }

                /*
                SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String LANG_ID = sp.getString(KEY_PREFE_LANG, String.valueOf(3));

                if (LANG_ID.equals("1")){
                    Intent intent=new Intent(context,Home.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(context, ChooseLanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
                 */
            }
        },1500); // 1500
    }

    private void loadHome() {
        Intent i = new Intent(getApplicationContext(), HomeActivity2.class);
        startActivity(i);
        finish();
    }
}