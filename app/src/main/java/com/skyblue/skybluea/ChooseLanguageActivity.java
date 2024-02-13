package com.skyblue.skybluea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.skyblue.skybluea.activity.SplashActivity;
import com.skyblue.skybluea.databinding.ActivityChooseLanguageBinding;
import com.skyblue.skybluea.helper.LocaleHelper;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {
    private ActivityChooseLanguageBinding binding;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_LANG = "lang";
    private final Context context = this;
    private Locale myLocale;
    private String currentLanguage = "en", currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_language);

        initSetOnClickListner();
        currentLanguage = getIntent().getStringExtra(currentLang);
    }
    private void initSetOnClickListner() {
        binding.eng.setOnClickListener(view -> {
            setLocale("en");
            SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_PREFE_LANG, "1");
            editor.apply();
        });

        binding.tamil.setOnClickListener(view -> {
            setLocale("ta");
            SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_PREFE_LANG, "1");
            editor.apply();
        });

        binding.hi.setOnClickListener(view -> {
            setLocale("hi");
            SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_PREFE_LANG, "1");
            editor.apply();
        });
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
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}