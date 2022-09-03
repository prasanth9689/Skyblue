package com.skyblue.skybluea.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SplashActivity;
import com.skyblue.skybluea.helper.LocaleHelper;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_LANG = "lang";

    Button engBtn , taBtn, hiBtn;
    Context context = this;

    Locale myLocale;
    String currentLanguage = "en", currentLang;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        initSetOnClickListner();

        currentLanguage = getIntent().getStringExtra(currentLang);

        checkCurrentLanguage();

    }
    private void initSetOnClickListner() {
        engBtn = findViewById(R.id.id_eng);
        taBtn = findViewById(R.id.id_tamil);
        hiBtn = findViewById(R.id.id_hi);
        backBtn = findViewById(R.id.back_settings_choose_language);

        engBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_PREFE_LANG, "1");
                editor.apply();
            }
        });

        taBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ta");

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_PREFE_LANG, "1");
                editor.apply();
            }
        });

        hiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("hi");

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_PREFE_LANG, "1");
                editor.apply();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkCurrentLanguage() {

        //  Toast.makeText(context, currentLang, Toast.LENGTH_SHORT).show();
    }

    public void onClickTamilLanguage(View view){
        setLocale("ta");
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, SplashActivity.class);
            refresh.putExtra(currentLang, localeName);

            // after on CLick we are using finish to close and then just after that
            // we are calling startactivity(getIntent()) to open our application
            finish();
            Intent intent = new Intent(context, SplashActivity.class);
            startActivity(intent);

            // this basically provides animation
            overridePendingTransition(0, 0);
        } else {
            // showMessageInSnackbar(getResources().getString(R.string.language_already_selected));
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}