package com.skyblue.skybluea.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.adapters.SearchAdapter;
import com.skyblue.skybluea.databinding.ActivitySearchBinding;
import com.skyblue.skybluea.helper.AppConstants;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.ChannelCreation;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.model.Search;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private List<Search> postList;
    private RecyclerView.Adapter adapter;
    String QueryHolder;
    public static Context context;
    public SearchActivity activity;
    ProgressBar progressDialog;
    public static final int progress_bar_type = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editText.requestFocus();

        SessionHandler session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        postList = new ArrayList<>();

        adapter = new SearchAdapter(getApplicationContext(), postList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);

        binding.back.setOnClickListener((View.OnClickListener) v -> finish());

        binding.editText.setOnEditorActionListener((TextView.OnEditorActionListener) (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                postList.clear();
                getData();
                return true;
            }
            return false;
        });

        binding.actionSearch.setOnClickListener((View.OnClickListener) v -> {
            postList.clear();
            getData();
        });
    }

    private void getData() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        String userId = user.getUser_id();
        String access = AppConstants.ACCESS_SEARCH_GET;
        String queryBase64 = binding.editText.getText().toString().trim();

        String mDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String mTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String timeDate  = mDate +" "+mTime;

//        try {
//            byte[] data = binding.editText.getText().toString().getBytes("UTF-8");
//            queryBase64 = Base64.encodeToString(data, Base64.DEFAULT);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        RequestBody mAccess = RequestBody.create(MediaType.parse("multipart/form-data"), access);
        RequestBody mQueryBase64 = RequestBody.create(MediaType.parse("multipart/form-data"), queryBase64);
        RequestBody mUserId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody mTimeDate = RequestBody.create(MediaType.parse("multipart/form-data"), timeDate);

        Call<Search> call=apiInterface.getSearch(mAccess,
                mQueryBase64,
                mUserId,
                mTimeDate
                );
        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Search search = response.body();

                    List<Search.Data> dataList = search.data;

                    for (Search.Data data: dataList){
                        Log.e("test_", data.thumbnail_url);
                        Search search1 = new Search();

                        search1.setId(data.id);
                        search1.setUser_id(data.user_id);
                        search1.setChannel_id(data.channel_id);
                        search1.setChannel_name(data.channel_name);
                        search1.setThumbnail_url(data.thumbnail_url);
                        search1.setVideo_url(data.video_url);
                        search1.setVideo_name(data.video_name);
                        search1.setDescription(data.description);
                        search1.setUpload_date(data.upload_date);
                        postList.add(search1);
                    }

//                    Search search1 = new Search();
//
//                   List<Search.Data> dataList = search.data;
//                    for (Search.Data data: dataList){
//                        Log.e("se_", data.thumbnail_url);
//
//                        search1.setThumbnail_url(data.thumbnail_url);
//                        search1.setChannel_name(data.channel_name);
//                        search1.setVideo_name(data.video_name);
//                    }
//                    postList.add(search1);

//                    for (int i =0; i < dataList.size(); i++){
//
//                        Search search1 = new Search();
//                        for (Search.Data data: dataList) {
//                            search1.setThumbnail_url(data.thumbnail_url);
//                            postList.add(search1);
//                        }
//                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {

            }
        });
    }
}


