package com.skyblue.skybluea.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.account.add_mobile.AddMobileNumberActivity;
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
        onClick();
    }

    private void onClick() {
        binding.addMobile.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddMobileNumberActivity.class);
            intent.putExtra("event_name", "Add mobile number");
            startActivity(intent);
        });
    }

    private void loadUser() {
        binding.gender.setText(user.getUser_gender());
        binding.dob.setText(user.getUser_dob());
        if (user.getMobile().isEmpty() | user.getMobile().equals("") | user.getMobile().equals("null")){
            binding.mobile.setVisibility(View.GONE);
            binding.addMobile.setVisibility(View.VISIBLE);
        }

//        if (user.getEmail().isEmpty() | user.getEmail().equals("") | user.getEmail().equals("null")){
//            binding.email.setVisibility(View.GONE);
//        }
        binding.email.setText(user.getEmail());
        binding.mobile.setText(user.getMobile());
        binding.idBack.setOnClickListener(view -> finish());
    }
}