package com.skyblue.skybluea.activity.channels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.adapters.ChannelsAdatpter;
import com.skyblue.skybluea.database.DatabaseManager;
import com.skyblue.skybluea.databinding.ActivityChannelsDashboardBinding;
import com.skyblue.skybluea.model.ChannelsModel;

import java.util.ArrayList;

public class ChannelsDashboard extends AppCompatActivity {
    private ActivityChannelsDashboardBinding binding;
    private DatabaseManager databaseManager;
    private ArrayList<ChannelsModel> channelsArrayList;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelsDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        onClick();

        channelsArrayList = new ArrayList<>(databaseManager.loadCahnnels());

        if (channelsArrayList.size() == 0){
            Log.e("cl_", "mNo channels created");
            binding.emptyChannel.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            Log.e("cl_", "mchannels availabe");
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyChannel.setVisibility(View.GONE);

            displayChannelsList();
        }
    }

    private void displayChannelsList() {
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setItemViewCacheSize(20);
        ChannelsAdatpter channelsAdatpter = new ChannelsAdatpter(getApplicationContext(),this, channelsArrayList);
        binding.recyclerView.setAdapter(channelsAdatpter);
    }

    private void onClick() {
        binding.create.setOnClickListener(view -> startActivity(new Intent(context, ChannelCreateActivity.class)));
        binding.createNew.setOnClickListener(view -> startActivity(new Intent(context, ChannelCreateActivity.class)));
        binding.back.setOnClickListener(v -> finish());
    }
}