package com.skyblue.skybluea.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityVideoViewBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.LikeVideo;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoViewActivity extends AppCompatActivity implements AdsMediaSource.MediaSourceFactory  {
    private ActivityVideoViewBinding binding;
    private SessionHandler session;
    private final Context context = this;
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private PlayerView playerView;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    private  DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private int mResumeWindow;
    private long mResumePosition;
    private String videoUrl;
    private User user;
    private APIInterface apiInterface;
    private BottomSheetDialog bottomSheetDialog;
    private String loggedUserId, loggedUserName, postId, postUserId, channelId;
    private RecyclerViewAdapter recyclerViewAdapter;
    private final List<Post> rowsArrayList = new ArrayList<>();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        postId = getIntent().getStringExtra("post_id");

        loadVideo(postId);

        dataSourceFactory =
                new DefaultDataSourceFactory(
                        context, Util.getUserAgent(context, getString(R.string.app_name)));

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        initBottomSheet();
        onClick();

       new Thread(() -> {
           initRecyclerView();
           loadList();
           initScrollListener();
       }).start();
    }

    private void loadVideo(String postId) {
        postUserId = getIntent().getStringExtra("post_user_id");
        videoUrl = getIntent().getStringExtra("url");
        binding.videoName.setText(getIntent().getStringExtra("video_name"));
        binding.userName.setText(getIntent().getStringExtra("channel_name"));
        channelId = getIntent().getStringExtra("channel_id");
        String totalLikes = getIntent().getStringExtra("likes");
        String likeStatus = getIntent().getStringExtra("like_status");
        String totalComments = getIntent().getStringExtra("comments");
        String totalViews = getIntent().getStringExtra("total_views");
        String uploadTime = getIntent().getStringExtra("time_date");

        loadLikeFunction(likeStatus, totalLikes);
        loadComments(totalComments);
        loadUploadTime(uploadTime);
        assert totalViews != null;
        loadTotalViews(totalViews);

        viewsInit();
    }

    private void initScrollListener() {

        binding.loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
//        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
//                        //bottom of list!
//                        loadMore();
//                        Log.e("ll_", "isLoading = true");
//                        isLoading = true;
//                    }
//                }
//            }
//        });
    }

    private void loadMore() {
        // add more progressbar
        rowsArrayList.add(null);
        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call=apiInterface.getCommonPosts2("1");

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    String postList = "";

                    try {
                        assert response.body() != null;
                        postList = response.body().string();
                        JSONArray jsonArray = new JSONArray(postList);

                        // remove progress bar
                        rowsArrayList.remove(rowsArrayList.size() - 1);
                        int scrollPosition = rowsArrayList.size();
                        recyclerViewAdapter.notifyItemRemoved(scrollPosition);

                        for (int i = 0; i<jsonArray.length(); i++) {
                            Post post = new Post();
                            JSONObject object = jsonArray.getJSONObject(i);
                            post.setUser_name(object.getString("user_name"));
                            post.setThumbnail_url(object.getString("thumbnail_url"));
                            post.setVideo_name(object.getString("video_name"));
                            post.setLikes(object.getString("likes"));
                            post.setComments(object.getString("comments"));
                            post.setUser_id(object.getString("user_id"));
                            post.setProfile_url(object.getString("profile_url"));
                            post.setLike_status(object.getString("like_status"));
                            post.setPost_id(object.getString("post_id"));
                            post.setTime_date(object.getString("time_date"));
                            post.setVideo_url(object.getString("video_url"));
                            post.setDuration(object.getString("duration"));
                            post.setChannel_id(object.getString("channel_id"));
                            post.setTotal_views(object.getString("total_views"));
                            post.setChannel_name(object.getString("channel_name"));
                            rowsArrayList.add(post);
                        }

                        int arrayListSize = rowsArrayList.size();
                        Log.e("vv_", "Load more : arrayListSize " + arrayListSize);

                        recyclerViewAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } catch (JSONException | IOException e) {
                        // throw new RuntimeException(e);
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadList() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call=apiInterface.getCommonPosts2("1");

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    String postList = "";
                    binding.shimmerLayout.setVisibility(View.GONE);
                    try {
                        assert response.body() != null;
                        postList = response.body().string();
                        JSONArray jsonArray = new JSONArray(postList);

                        for (int i = 0; i<jsonArray.length(); i++) {
                            Post post = new Post();
                            JSONObject object = jsonArray.getJSONObject(i);
                            post.setUser_name(object.getString("user_name"));
                            post.setThumbnail_url(object.getString("thumbnail_url"));
                            post.setVideo_name(object.getString("video_name"));
                            post.setLikes(object.getString("likes"));
                            post.setComments(object.getString("comments"));
                            post.setUser_id(object.getString("user_id"));
                            post.setProfile_url(object.getString("profile_url"));
                            post.setLike_status(object.getString("like_status"));
                            post.setPost_id(object.getString("post_id"));
                            post.setTime_date(object.getString("time_date"));
                            post.setVideo_url(object.getString("video_url"));
                            post.setDuration(object.getString("duration"));
                            post.setChannel_id(object.getString("channel_id"));
                            post.setTotal_views(object.getString("total_views"));
                            post.setChannel_name(object.getString("channel_name"));
                            rowsArrayList.add(post);
                        }

                        recyclerViewAdapter.notifyDataSetChanged();
                    } catch (JSONException | IOException e) {
                        // throw new RuntimeException(e);
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setHasFixedSize(false);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setItemViewCacheSize(20);
        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList);
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        public List<Post> postList;

        public RecyclerViewAdapter(List<Post> postList) {
            this.postList = postList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_fragment_home, parent, false);
                return new PostViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_loading_progress, parent, false);
                return new LoadingViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PostViewHolder) {
                PostViewHolder holder1 = (PostViewHolder) holder;

                Post post = postList.get(position);

                /*
                Glide
                        .with(VideoViewActivity.this)
                        .load(postList.get(position).getThumbnail_url())
                        .placeholder(R.color.image_placeholder_bg)
                        .into(holder1.imgThumbnail);
                */

                Glide
                        .with(context.getApplicationContext())
                        .load(postList.get(position).getProfile_url())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.placeholder_person)
                        .into(holder1.imgProfile);

                Picasso.get().load(postList.get(position).getThumbnail_url()).into(holder1.imgThumbnail);
                Picasso.get().load(postList.get(position).getProfile_url()).into(holder1.imgProfile);

                holder1.txtChannelName.setText(post.getUser_name());
                String newStringEmojidecooded = "";
                byte[] data = Base64.decode(postList.get(position).getVideo_name(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, StandardCharsets.UTF_8);
                holder1.txtVideoName.setText(newStringEmojidecooded);
                holder1.txtDuration.setText(postList.get(position).getDuration());

                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date past = null;
                try {
                    past = format.parse(postList.get(position).getTime_date());

                    Date now = new Date();
                    assert past != null;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                    if (seconds < 60) {
                        String sec = seconds + getString(R.string.seconds_ago);
                        holder1.txtUploadDate.setText(sec);
                    } else if (minutes < 60) {
                        String min = minutes + getString(R.string.minutes_ago);
                        holder1.txtUploadDate.setText(min);
                    } else if (hours < 24) {
                        String hrs = hours + getString(R.string.hours_ago);
                        holder1.txtUploadDate.setText(hrs);
                    } else {
                        String day = days + getString(R.string.days_ago);
                        holder1.txtUploadDate.setText(day);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String finalNewStringEmojidecooded = newStringEmojidecooded;
                holder1.imgThumbnail.setOnClickListener(view -> {
                    postId = postList.get(position).getPost_id();
                    postUserId = postList.get(position).getUser_id();
                    videoUrl = postList.get(position).getVideo_url();
                    binding.videoName.setText(finalNewStringEmojidecooded);
                    binding.userName.setText(postList.get(position).getChannel_name());
                    loadUploadTime(postList.get(position).getTime_date());
                    binding.likeCheckbox.setChecked(false);
                    loadLikeFunction(postList.get(position).getLike_status(), postList.get(position).getLikes());
                    loadComments(postList.get(position).getComments());
                    loadTotalViews(postList.get(position).getTotal_views());
                    player.stop();
                    initExoPlayer();
                });

            }else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return postList == null ? 0 : postList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public class PostViewHolder extends RecyclerView.ViewHolder{
            private final TextView txtChannelName, txtVideoName, txtDuration, txtUploadDate;
            private final ImageView imgThumbnail, imgProfile;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                txtVideoName = itemView.findViewById(R.id.video_name);
                txtChannelName = itemView.findViewById(R.id.channel_name);
                txtDuration = itemView.findViewById(R.id.duration);
                imgThumbnail = itemView.findViewById(R.id.thumbnail);
                imgProfile = itemView.findViewById(R.id.profile_image);
                txtUploadDate = itemView.findViewById(R.id.upload_date);
            }
        }

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View itemView) {
                super(itemView);
                progressBar = itemView.findViewById(R.id.progressBarloading);
            }
        }
    }

    private void loadTotalViews(String totalViews) {
        if (!totalViews.equals("")) {
            binding.totalViews.setText(totalViews);
        }else {
            binding.totalComments.setText("0");
        }
    }

    private void loadLikeFunction(String likeStatus, String totalLikes) {
        if (session.isLoggedIn()){
            loggedUserId = user.getUser_id();
            loggedUserName = user.getName();

            if (likeStatus != null) {

                if (likeStatus.equals("null")){
                    return;
                }

                int mLikeStatus = Integer.parseInt(likeStatus);

                if (mLikeStatus == 1){
                    binding.likeCheckbox.setChecked(true);
                }
            }
        }

        if (totalLikes != null && totalLikes.equals("null")) {
            binding.totalLikes.setText("0");
        }else {
            binding.totalLikes.setText(totalLikes);
        }
    }

    private void viewsInit() {

        String mDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String mTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String mTimeDate  = mDate +" "+mTime;

        String userId;
        if (session.isLoggedIn()){
            userId = user.getUser_id();
        }else {
            userId = "1";
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("post_id", postId)
                .addFormDataPart("user_id", userId)
                .addFormDataPart("device", "Android")
                .addFormDataPart("date", mDate)
                .addFormDataPart("time", mTime)
                .addFormDataPart("time_date", mTimeDate)
                .build();

        Call<ResponseBody> call = apiInterface.sendViews(requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res;
                    try {
                        res = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.e("views count: ", res);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initBottomSheet() {
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetdialog_comment, null);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.AppBottomSheetCommentDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);
    }

    private void loadComments(String totalComments) {
        if (totalComments != null && totalComments.equals("null")) {
            binding.totalComments.setText("0");
        }else {
            assert totalComments != null;
            int check = Integer.parseInt(totalComments);
            if (check == 0){
                binding.totalComments.setText("0");
            }else {
                binding.totalComments.setText(totalComments);
            }
        }
    }

    private void onClick() {
        binding.likeCheckbox.setOnClickListener(v -> {
            if (session.isLoggedIn()){
                boolean isChecked = ((CheckBox)v).isChecked();

                String totalLikes = binding.totalLikes.getText().toString();
                Integer mTotalLikes = Integer.parseInt(totalLikes);
                if (isChecked){
                    // insert like
                    mTotalLikes++;
                    binding.totalLikes.setText(String.valueOf(mTotalLikes));
                    insertLike();
                }else {
                    // delete already liked
                    mTotalLikes--;
                    binding.totalLikes.setText(String.valueOf(mTotalLikes));
                    insertUnlike();
                }
            }else {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        binding.emptyCommentsLayout.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                openPostCommentDialog();
            } else {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        binding.commentRoundBox.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                openPostCommentDialog();
            } else {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        binding.postUserProfileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommonAccountDashboard.class);
            intent.putExtra("user_id", postUserId);
            startActivity(intent);
        });

        binding.subscription.setOnClickListener(v -> {
            RequestBody mUserId = RequestBody.create(MediaType.parse("multipart/form-data"), loggedUserId);
            RequestBody mChannelId = RequestBody.create(MediaType.parse("multipart/form-data"), channelId);
            Call<ResponseBody> call = apiInterface.subscriptionNew(mUserId, mChannelId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        String res;
                        try {
                            res = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                         Log.e("subscription", res);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.likeCheckbox.performClick();
            }
        });

        binding.likeTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.likeCheckbox.performClick();
            }
        });

        binding.commentCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.commentRoundBox.performClick();
            }
        });
    }

    private void openPostCommentDialog() {
        EditText edText = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btSubmit = bottomSheetDialog.findViewById(R.id.submit);
        assert edText != null;
        edText.setFocusableInTouchMode(true);
        edText.requestFocus();

        bottomSheetDialog.show();

        if (btSubmit != null) {
            btSubmit.setOnClickListener(v1 -> {
                String commentText = edText.getText().toString().trim();

                if (commentText.isEmpty()){
                    edText.requestFocus();
                    Toast.makeText(context, getString(R.string.please_write_something), Toast.LENGTH_SHORT).show();
                    return;
                }

                edText.setText("");
                String totalComments = binding.totalComments.getText().toString();
                int mTotalComments = Integer.parseInt(totalComments);
                mTotalComments++;
                binding.totalComments.setText(String.valueOf(mTotalComments));
                saveComment(commentText);
            });
        }
    }

    private void saveComment(String commentText) {
        bottomSheetDialog.dismiss();
        RequestBody mLoggedUserId = RequestBody.create(MediaType.parse("multipart/form-data"), loggedUserId);
        RequestBody mLoggedUserName = RequestBody.create(MediaType.parse("multipart/form-data"), loggedUserName);
        RequestBody mCommentText = RequestBody.create(MediaType.parse("multipart/form-data"), commentText);
        RequestBody mPostId = RequestBody.create(MediaType.parse("multipart/form-data"), postId);
        RequestBody mPostUserId = RequestBody.create(MediaType.parse("multipart/form-data"), postUserId);
        Call<ResponseBody> call = apiInterface.saveComment(mLoggedUserId, mLoggedUserName, mCommentText, mPostId, mPostUserId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    String res;
                    try {
                        res = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertUnlike() {
        RequestBody mUserid = RequestBody.create(MediaType.parse("multipart/form-data"), loggedUserId);
        assert postId != null;
        RequestBody mPostId = RequestBody.create(MediaType.parse("multipart/form-data"), postId);
        Call<LikeVideo> call = apiInterface.insertUnLike(mUserid, mPostId);

        call.enqueue(new Callback<LikeVideo>() {
            @Override
            public void onResponse(@NonNull Call<LikeVideo> call, @NonNull Response<LikeVideo> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    LikeVideo likeVideo = response.body();
                    if (likeVideo.status.equals("true")){
                        Log.e("unlike_", "un liked success and removed in database");
                    }else {
                        Log.e("unlike_", "failed");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeVideo> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertLike() {

        RequestBody mUserid = RequestBody.create(MediaType.parse("multipart/form-data"), loggedUserId);
        assert postId != null;
        RequestBody mPostId = RequestBody.create(MediaType.parse("multipart/form-data"), postId);

        Call<LikeVideo> call = apiInterface.insertLike(mUserid, mPostId);

        call.enqueue(new Callback<LikeVideo>() {
            @Override
            public void onResponse(@NonNull Call<LikeVideo> call, @NonNull Response<LikeVideo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    LikeVideo likeVideo = response.body();

                    if (likeVideo.status.equals("true")){
                        Log.e("like_", "liked success");
                    }else {
                        Log.e("like_", "already exists");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeVideo> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void loadUploadTime(String uploadTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date past;
        try {
            past = format.parse(uploadTime);
            Date now = new Date();
            assert past != null;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                binding.uploadDate.setText(seconds + getString(R.string.seconds_ago));
            } else if (minutes < 60) {
                binding.uploadDate.setText(minutes + getString(R.string.minutes_ago));
            } else if (hours < 24) {
                binding.uploadDate.setText(hours + getString(R.string.hours_ago));
            } else {
                binding.uploadDate.setText(days + getString(R.string.days_ago));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }


    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }


    private void openFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(v -> {
            if (!mExoPlayerFullscreen)
                openFullscreenDialog();
            else
                closeFullscreenDialog();
        });
    }

    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            Log.i("DEBUG"," haveResumePosition ");
            player.seekTo(mResumeWindow, mResumePosition);
        }
        MediaSource mVideoSource = buildMediaSource(Uri.parse(videoUrl));
        Log.i("DEBUG"," mVideoSource "+ mVideoSource);
        player.prepare(mVideoSource);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView == null) {
            playerView =  findViewById(R.id.exoplayer);
            initFullscreenDialog();
            initFullscreenButton();
        }
        initExoPlayer();
        if (mExoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException(getString(R.string.unsupporte_type) + type);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null && player != null) {
            mResumeWindow = player.getCurrentWindowIndex();
            mResumePosition = Math.max(0, player.getContentPosition());
            player.release();
        }
        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    @Override
    public int[] getSupportedTypes() {
        return new int[] {C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
    }
}
