package com.skyblue.skybluea.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.skyblue.skybluea.ChooseLanguageActivity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivitySplashBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_GET_STARTED = "get_started";
    private static final String KEY_PREFE_LANG = "lang";
    private SharedPreferences sp;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String status = sp.getString(KEY_PREFE_GET_STARTED, "");

        SessionHandler session = new SessionHandler(getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            String LANG_ID = sp.getString(KEY_PREFE_LANG, String.valueOf(3));
            if (LANG_ID.equals("1")){
                if (status.equals("true")){
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
            }else {
                    Intent intent = new Intent(context, GetStartedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
            }
            }else{
                Intent intent = new Intent(context, ChooseLanguageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        },1500);
    }
}