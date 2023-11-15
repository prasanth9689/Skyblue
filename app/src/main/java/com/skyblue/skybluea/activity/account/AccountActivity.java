package com.skyblue.skybluea.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.HomeActivity2;
import com.skyblue.skybluea.activity.channels.ChannelsDashboard;
import com.skyblue.skybluea.databinding.ActivityAccountBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.settings.SettingsActivity;

public class AccountActivity extends AppCompatActivity {
    private ActivityAccountBinding binding;
    private SessionHandler session;
    private final Context context = this;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();
        initAccount();

        setOnClickListener();
    }

    private void initAccount() {
        String primaryChannelName = user.getChannel_primary_name();
        binding.channelName.setText(primaryChannelName);
    }

    private void setOnClickListener() {
        binding.profileCon.setOnClickListener(view -> {

        });
        binding.logout.setOnClickListener(view -> {
            session = new SessionHandler(getApplicationContext());
            User user = session.getUserDetails();
            session.logoutUser();
            Intent intent = new Intent(context, HomeActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.myVideos.setOnClickListener(view -> {
            Intent intent = new Intent(context, MyVideoListActivity.class);
            startActivity(intent);
        });

        binding.myProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, MyProfileActivity.class);
            startActivity(intent);
        });

        binding.settings.setOnClickListener(view -> {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
        });

        binding.myChannel.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChannelsDashboard.class);
            startActivity(intent);
        });

        binding.idBack.setOnClickListener(view -> finish());
    }
}