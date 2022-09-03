package com.skyblue.skybluea.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.registration.PrivacyPolicyActivity;

public class SettingsActivity extends AppCompatActivity {
    ImageView backButton;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = (ImageView) findViewById(R.id.back_settings);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClickContactUs(View view){
        Intent intent = new Intent(context, ContactUsActivity.class);
        startActivity(intent);
    }

    public void onClickAboutUs(View view){
        Intent intent = new Intent(context, AboutUsActivity.class);
        startActivity(intent);
    }

    public void onClickPrivacyPolicy(View view){
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    public void onClickTermsAndConditions(View view){
        Intent intent = new Intent(context, TermsAndConditionsActivity.class);
        startActivity(intent);
    }

    public void onClickChooseLanguage(View view){
        Intent intent = new Intent(context, LanguageSettingsActivity.class);
        startActivity(intent);
    }
}