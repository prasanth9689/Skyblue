package com.skyblue.skybluea.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
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
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleSignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();
        initAccount();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

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
            signOut();
            Intent intent = new Intent(context, HomeActivity.class);
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

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Google Sign out successfully");
                    }
                });
    }
}