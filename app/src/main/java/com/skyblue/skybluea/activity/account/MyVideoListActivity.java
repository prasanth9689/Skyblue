package com.skyblue.skybluea.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skyblue.skybluea.adapters.MyVideosAdapter;
import com.skyblue.skybluea.databinding.ActivityMyVideoListBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVideoListActivity extends AppCompatActivity {
    private ActivityMyVideoListBinding binding;
    private Context context = this;
    private SessionHandler session;
    private LinearLayoutManager linearLayoutManager;
    private List<Post> postList;
    private RecyclerView.Adapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyVideoListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        postList = new ArrayList<>();

        adapter = new MyVideosAdapter(this, postList);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        //binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}