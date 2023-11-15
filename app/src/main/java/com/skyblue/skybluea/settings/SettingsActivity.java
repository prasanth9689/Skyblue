package com.skyblue.skybluea.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.PrivacyPolicyActivity;
import com.skyblue.skybluea.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setOnClickListener();
    }

    private void setOnClickListener() {
        binding.privacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(context, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        binding.termsCons.setOnClickListener(view -> {
            Intent intent = new Intent(context, TermsAndConActivity.class);
            startActivity(intent);
        });

        binding.back.setOnClickListener(view -> {
            finish();
        });
    }
}