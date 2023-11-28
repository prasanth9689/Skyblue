package com.skyblue.skybluea.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityCommonAccountDashboardBinding;

public class CommonAccountDashboard extends AppCompatActivity {
    private ActivityCommonAccountDashboardBinding binding;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommonAccountDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}