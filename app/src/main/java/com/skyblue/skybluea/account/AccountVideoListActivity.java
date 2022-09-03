package com.skyblue.skybluea.account;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.CheckNetwork;
import com.skyblue.skybluea.Post;
import com.skyblue.skybluea.PostAdapter;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.account.adapter.UserVideoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountVideoListActivity extends AppCompatActivity {
    private SessionHandler session;

    ImageView backButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Post> postList;
    private RecyclerView.Adapter adapter;

    User user;

    Context context = this;

    RelativeLayout errorLoad;
    ProgressBar progressBar;

    private boolean isLoading;
    private int visibleThreshold = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video_account);

        initVariable();
        initSetOnClickListener();

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        postList = new ArrayList<>();

        adapter = new UserVideoListAdapter(this, postList);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

        getUserVideos();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = getLastVisibleItem(new int[]{linearLayoutManager.findLastVisibleItemPosition()});;

                if (!isLoading && totalItemCount > 1 &&
                        totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    //Loading more data from server
                    postList.add(null);
                    adapter.notifyItemInserted(postList.size() - 1);
                    getUserVideosLoadMore();
                    setLoading(true);
                }
            }
        });
    }

    private void getUserVideos() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.AC_GET_MEDIA_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Utils.showMessage(context, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                Post post = new Post();
                                post.setPost_id(jsonObject2.getString("id"));
                                post.setPost_text(jsonObject2.getString("image_about"));
                                post.setPost_user_name(jsonObject2.getString("user_name"));
                                post.setPost_image_url(jsonObject2.getString("url"));
                                post.setPost_user_id(jsonObject2.getString("user_id"));
                                post.setStatus(jsonObject2.getString("status"));
                                post.setLikes(jsonObject2.getString("likes"));
                                post.setComments(jsonObject2.getString("comments"));
                                post.setProfileurl(jsonObject2.getString("profile_url"));
                                post.setTime_date(jsonObject2.getString("time_date"));
                                post.setMedia_type(jsonObject2.getString("media_type"));
                                post.setVideo_url(jsonObject2.getString("video_url"));
//                                post.setTotal_views(jsonObject2.getString("total_views"));

                                postList.add(post);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                if(CheckNetwork.isInternetAvailable(context)) //returns true if internet available
                {
                    //   Toast.makeText(context,"Internet Connected",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    errorLoad.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    // Toast.makeText(context,"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", user.getUser_id());
                params.put("media_type", "video");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void getUserVideosLoadMore() {

    }

    private void initSetOnClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initVariable() {
        backButton = (ImageView) findViewById(R.id.video_grid_back_btn);
        errorLoad = findViewById(R.id.error_load_home);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
    }

    public void setLoading(boolean status) {
        isLoading = status;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }
}
