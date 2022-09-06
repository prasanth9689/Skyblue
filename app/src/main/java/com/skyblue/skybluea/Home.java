package com.skyblue.skybluea;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.rvadapter.AdmobNativeAdAdapter;
import com.skyblue.skybluea.account.AccountActivity;
import com.skyblue.skybluea.helper.LocaleHelper;
import com.skyblue.skybluea.utils.AppVersion;
import com.skyblue.skybluea.utils.ImageConstants;
import com.skyblue.skybluea.utils.VideoConstants;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {
    private SessionHandler session;

    public String newStringEmojidecooded , userNameStringEmojidecooded;
    RelativeLayout UserAccountBtn, addPhotoFloat, addPhotoRel, addVideoRel, pageRel;
    final Context context = this;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int REQUEST_READ_PERMISSION = 687;

    private String TempStatus = "0";
    BottomNavigationView bottomNavigationView;
    User user;
    ImageView userprofileIV;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Post> postList;
    private RecyclerView.Adapter adapter;

    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private int visibleItemCount , pastVisiblesItems;

    private boolean isLoading;
    private int visibleThreshold = 5;

    RelativeLayout errorLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_home);

        initVariable();
        initSetOnClickListener();
        //  CheckAppVersion();

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        if (session.isLoggedIn()) {
            Glide
                    .with(context)
                    .load(user.getUser_profile())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(userprofileIV);
        }

        postList = new ArrayList<>();

        adapter = new PostAdapter(this, postList);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(getString(R.string.admob_app_native_ad_id), adapter,
                "medium").adItemInterval(10).build();
        recyclerView.setAdapter(admobNativeAdAdapter);

        //   getFeedDetails(0);
        getVideoDataUserNotLogged();

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
                    getVideoDataUserNotLoggedgetLoadMore();
                    setLoading(true);
                }
            }
        });
    }

    private void initSetOnClickListener() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.item_home:
                        startActivity(new Intent(Home.this
                                , Home.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.item_media:
                        Intent ii2 = new Intent(Home.this, HomeVideoActivity.class);
                        startActivity(ii2);
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.item_add:
                        session = new SessionHandler(Home.this);
                        if (session.isLoggedIn()) {
                            if(ContextCompat.checkSelfPermission(context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                            if (SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                                        checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    //                showMessageInSnackbar("Permission already granded");
                                    openPhotoChooser();
                                }
                            }
                        } else {
                            Intent ii = new Intent(Home.this, LoginActivity.class);
                            startActivity(ii);
                        }

                        return true;

                    case R.id.item_notification:
                        session = new SessionHandler(Home.this);
                        if (session.isLoggedIn()) {
                            Intent ii = new Intent(Home.this, AccountActivity.class);
                            startActivity(ii);
                        } else {
                            Intent ii = new Intent(Home.this, LoginActivity.class);
                            startActivity(ii);
                        }

                        return true;

                    case R.id.item_chat:
                        session = new SessionHandler(Home.this);
                        Intent ii = new Intent(Home.this, SearchActivity.class);
                        startActivity(ii);

                        return true;
                }
                return false;
            }
        });

        addPhotoFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new SessionHandler(Home.this);
                if (session.isLoggedIn()) {
                    if(ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(Home.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                                checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            //                showMessageInSnackbar("Permission already granded");
                            openVideoChooser();
                        }
                    }
                } else {
                    Intent ii = new Intent(Home.this, LoginActivity.class);
                    startActivity(ii);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.clear();
                //getVideoDataUserLogged();
                getVideoDataUserNotLogged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        UserAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new SessionHandler(Home.this);
                if (session.isLoggedIn()) {
                    Intent intent = new Intent(Home.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    Intent ii = new Intent(Home.this, LoginActivity.class);
                    startActivity(ii);
                }
            }
        });
    }

    private void initVariable() {
        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        UserAccountBtn = findViewById(R.id.ic_user);
        addPhotoFloat = findViewById(R.id.add_photo_cam);
        addPhotoRel = findViewById(R.id.rel_photo);
        progressBar = findViewById(R.id.progress_bar);
        userprofileIV = findViewById(R.id.home_user_profile);
        errorLoad = findViewById(R.id.error_load_home);
    }

    private void getVideoDataUserNotLoggedgetLoadMore() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.HOME_GET_COMMON_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Utils.showMessage(context, response);
                        setLoading(false);

                        postList.remove(postList.size() - 1);
                        adapter.notifyItemRemoved(postList.size());

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
//                                post.setUser_cover(jsonObject2.getString("user_cover"));
//                                post.setPlaceholder(jsonObject2.getString("placeholder_url"));
                                post.setMedia_type(jsonObject2.getString("media_type"));
                                post.setVideo_url(jsonObject2.getString("video_url"));
//                                post.setTotal_views(jsonObject2.getString("total_views"));

                                postList.add(post);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // adapter.clear();
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
                    recyclerView.setVisibility(View.INVISIBLE);
                    // Toast.makeText(context,"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
    private void getVideoDataUserNotLogged() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.HOME_GET_COMMON_DATA,
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
//                                post.setUser_cover(jsonObject2.getString("user_cover"));
//                                post.setPlaceholder(jsonObject2.getString("placeholder_url"));
                                post.setMedia_type(jsonObject2.getString("media_type"));
                                post.setVideo_url(jsonObject2.getString("video_url"));
//                                post.setTotal_views(jsonObject2.getString("total_views"));

                                postList.add(post);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        // adapter.clear();
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
                params.put("user_id", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        }else {
            if(ContextCompat.checkSelfPermission(Home.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                showMessageInSnackbar("Permission False or True");
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        openVideoChooser();
    }

    private void openVideoChooser() {

        Intent ii = new Intent(Home.this, VideoSelectorDisplay.class);
        ii.putExtra("video_r_id", VideoConstants.POST_VIDEO);
        startActivity(ii);
    }

    private void openPhotoChooser() {

        Intent ii = new Intent(Home.this, MainActivity.class);
        ii.putExtra("image_id", ImageConstants.POST_PICTURE);
        startActivity(ii);
    }

    private void checkInternetConnecton() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            getVideoDataUserNotLogged();
        } else {
            connected = false;
            checkInternetConnecton();
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
      /*      client.getHomeTimeline(new JsonHttpResponseHandler() {
                public void onSuccess(JSONArray json) {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    adapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    adapter.addAll(...);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                }

                public void onFailure(Throwable e) {
                    Log.d("DEBUG", "Fetch timeline error: " + e.toString());
                }
            });
       */
        //    }
    }

    private void CheckAppVersion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.CHECK_APP_VERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (TempStatus.equals(response)) {
                            // Latest version
                        } else {
                            //    Deferent version - available new version to update
                            Intent i = new Intent(context, NewVersionUpdateAvailableActivity.class);
                            startActivity(i);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (CheckNetwork.isInternetAvailable(Home.this)) //returns true if internet available
                        {
                            // Internet Connected
                        } else {
                            //  No Internet Connection
                            checkInternetConnecton();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("app_version", AppVersion.APP_VERSION);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void showMessageInSnackbar(String message) {
        Snackbar snack = Snackbar.make(
                (((Activity) context).findViewById(android.R.id.content)),
                message, Snackbar.LENGTH_SHORT);
        snack.setDuration(Snackbar.LENGTH_SHORT);//change Duration as you need
        //snack.setAction(actionButton, new View.OnClickListener());//add your own listener
        View view = snack.getView();
        TextView tv = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);//change textColor

        TextView tvAction = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_action);
        tvAction.setTextSize(16);
        tvAction.setTextColor(Color.WHITE);

        snack.show();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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
