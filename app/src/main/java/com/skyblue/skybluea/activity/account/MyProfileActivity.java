package com.skyblue.skybluea.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityMyProfileBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;

public class MyProfileActivity extends AppCompatActivity {
    private ActivityMyProfileBinding binding;
    private SessionHandler session;
    private Context context = this;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        loadUser();
    }

    private void loadUser() {
        binding.gender.setText(user.getUser_gender());
        binding.dob.setText(user.getUser_dob());
        binding.mobile.setText(user.getMobile());
        binding.idBack.setOnClickListener(view -> finish());
    }
}