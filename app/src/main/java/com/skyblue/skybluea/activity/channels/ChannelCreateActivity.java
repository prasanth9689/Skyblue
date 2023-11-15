package com.skyblue.skybluea.activity.channels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.HomeActivity2;
import com.skyblue.skybluea.database.DatabaseManager;
import com.skyblue.skybluea.databinding.ActivityChannelCreateBinding;
import com.skyblue.skybluea.helper.AppConstants;
import com.skyblue.skybluea.helper.Utils;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.ChannelCreation;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelCreateActivity extends AppCompatActivity {
    private ActivityChannelCreateBinding binding;
    private final Context context = this;
    private String channelName;
    private User user;
    private DatabaseManager databaseManager;
    private Dialog customProgressDialog;
    private Dialog successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseManager = new DatabaseManager(this);

        SessionHandler session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();
        onClick();
        initProgressDialog();
        initSuccessDialog();

    }

    private void initSuccessDialog() {
        successDialog = new Dialog(context);

        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.model_dialog_channel_create);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCancelable(false);

        Button cancelButton = successDialog.findViewById(R.id.back_button);
        cancelButton.setEnabled(true);

        cancelButton.setOnClickListener(view -> {
            successDialog.dismiss();
            Intent intent = new Intent(context, HomeActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initProgressDialog() {
        customProgressDialog = new Dialog(context);
        customProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customProgressDialog.setContentView(R.layout.model_dialog_custom_progress);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        TextView textView = customProgressDialog.findViewById(R.id.message1);
        textView.setEnabled(true);
        textView.setText(R.string.creating_channel_please_wait);
       // customProgressDialog.show();
    }

    private void onClick() {
       binding.create.setOnClickListener(view -> {
           channelName = binding.channelName.getText().toString().trim();
           if ("".equals(channelName)){
               binding.channelName.setError(getString(R.string.enter_channel_name)); // continue okmm
               binding.channelName.requestFocus();
               return;
           }
           createChannel();
       });

       binding.back.setOnClickListener(v -> finish());
    }

    private void createChannel() {
        customProgressDialog.show();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        String userId = user.getUser_id();
        String access = AppConstants.ACCESS_CHANNEL_CREATION;

        String mDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String mTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String timeDate  = mDate +" "+mTime;

        RequestBody mChannelName = RequestBody.create(MediaType.parse("multipart/form-data"), channelName);
        RequestBody mUserId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody mAccess = RequestBody.create(MediaType.parse("multipart/form-data"), access);
        RequestBody mTimeDate = RequestBody.create(MediaType.parse("multipart/form-data"), timeDate);

        Call<ChannelCreation> call = apiInterface.channelCreation(mAccess,
                mChannelName,
                mUserId,
                mTimeDate);

        call.enqueue(new Callback<ChannelCreation>() {
            @Override
            public void onResponse(@NonNull Call<ChannelCreation> call, @NonNull Response<ChannelCreation> response) {
            customProgressDialog.dismiss();
                if (response.isSuccessful()){
                    ChannelCreation channelCreation = response.body();

                    assert channelCreation != null;
                    Log.e("channel_", channelCreation.status);

                    ArrayList<String> channelNameStringArray = new ArrayList<>();
                    ArrayList<String> channelIdStringArray = new ArrayList<>();
                    ArrayList<String> channelCreatedDateStringArray = new ArrayList<>();

                    List<ChannelCreation.Data> dataList = channelCreation.data;
                    for (ChannelCreation.Data data: dataList){
                        Log.e("channel_", data.channel_name);
                        channelNameStringArray.add(data.channel_name);
                        channelIdStringArray.add(data.channel_id);
                        channelCreatedDateStringArray.add(data.created_date);

                        Utils.showMessage(context, getString(R.string.channel_created_success));
                        successDialog.show();
                    }

                    databaseManager.open();
                    databaseManager.deleteAllChannel();
                    databaseManager.saveChannelListFromServer(channelNameStringArray,
                            channelIdStringArray,
                            channelCreatedDateStringArray);
                }
            }

            @Override
            public void onFailure(Call<ChannelCreation> call, Throwable t) {
                customProgressDialog.dismiss();            }
        });
    }
}