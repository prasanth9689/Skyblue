package com.skyblue.skybluea;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;
import com.skyblue.skybluea.account.adapter.UserVideoListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {
    private SessionHandler session;

    ImageView backBtn , userImageView , imageViewBackBtn;
    ZoomageView expandedImage;
    TextView userNameTxt;
    ProgressBar progressBar , progressBarMediaLoad;
    Context context = this;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<UserMediaModel> mediaList;
    private RecyclerView.Adapter adapter;

    private boolean isLoading;
    private int visibleThreshold = 5;

    User user;

    RelativeLayout imageViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        initVariable();
        setIntent();
        setOnClickListener();
        setUpRecyclerView();

        getUserMedia();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = getLastVisibleItem(new int[]{linearLayoutManager.findLastVisibleItemPosition()});;

                if (!isLoading && totalItemCount > 1 &&
                        totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    //Loading more data from server
                    mediaList.add(null);
                    adapter.notifyItemInserted(mediaList.size() - 1);
                    getUserMedia();
                    setLoading(true);
                }
            }
        });
    }

    private void getUserMedia() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.PRF_GET_COMMON_MEDIA_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Utils.showMessage(context, response);
                        progressBarMediaLoad.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                UserMediaModel userMediaModel = new UserMediaModel();
                                userMediaModel.setPost_id(jsonObject2.getString("id"));
                                userMediaModel.setPost_text(jsonObject2.getString("image_about"));
                                userMediaModel.setPost_user_name(jsonObject2.getString("user_name"));
                                userMediaModel.setPost_image_url(jsonObject2.getString("url"));
                                userMediaModel.setPost_user_id(jsonObject2.getString("user_id"));
                                userMediaModel.setStatus(jsonObject2.getString("status"));
                                userMediaModel.setLikes(jsonObject2.getString("likes"));
                                userMediaModel.setComments(jsonObject2.getString("comments"));
                                userMediaModel.setProfileurl(jsonObject2.getString("profile_url"));
                                userMediaModel.setTime_date(jsonObject2.getString("time_date"));
                                userMediaModel.setMedia_type(jsonObject2.getString("media_type"));
                                userMediaModel.setVideo_url(jsonObject2.getString("video_url"));
//                                post.setTotal_views(jsonObject2.getString("total_views"));

                                mediaList.add(userMediaModel);
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
                   // errorLoad.setVisibility(View.VISIBLE);
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
                params.put("user_id", getIntent().getStringExtra("user_id"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void setUpRecyclerView() {
        mediaList = new ArrayList<>();

        adapter = new UserMediaAdapter(this, mediaList);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewContainer.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.GONE);

                Glide.with(context)
                        .load(getIntent().getStringExtra("user_profile"))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.VISIBLE);
                                //    placeHolderRelativeLayout.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                //  placeHolderRelativeLayout.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(expandedImage);
            }
        });

        imageViewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewContainer.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setIntent() {
        userNameTxt.setText(getIntent().getStringExtra("user_name"));

        String profile_url = getIntent().getStringExtra("user_profile");

        if (!"".equals(profile_url) && !"null".equals(profile_url)) {
            Glide.with(context)
                    .load(getIntent().getStringExtra("user_profile"))
                    .apply(RequestOptions.circleCropTransform())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.VISIBLE);
                            //    placeHolderRelativeLayout.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            //  placeHolderRelativeLayout.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(userImageView);
        } else{

            progressBar.setVisibility(View.GONE);
            userImageView.setImageResource(R.drawable.placeholder_person);
        }
    }

    private void initVariable() {
        recyclerView = findViewById(R.id.recycler_view);
        backBtn =  findViewById(R.id.back_button);
        userImageView = findViewById(R.id.user_image_view);
        progressBar = findViewById(R.id.progressBar3);
        progressBarMediaLoad = findViewById(R.id.progress_bar_media);
        userNameTxt = findViewById(R.id.id_user_name);
        imageViewContainer = findViewById(R.id.image_view_container);
        expandedImage = findViewById(R.id.expanded_image);
        imageViewBackBtn = findViewById(R.id.back_button_image_view);
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

    @Override
    public void onBackPressed(){
      //  super.onBackPressed();
        if (imageViewContainer.getVisibility() == View.VISIBLE) {
            // Its visible
            imageViewContainer.setVisibility(View.GONE);
            backBtn.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}
