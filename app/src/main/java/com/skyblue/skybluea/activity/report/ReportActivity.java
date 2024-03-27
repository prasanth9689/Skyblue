package com.skyblue.skybluea.activity.report;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityReportBinding;

public class ReportActivity extends AppCompatActivity {
    private ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}