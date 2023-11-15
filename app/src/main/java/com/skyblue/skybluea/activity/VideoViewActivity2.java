package com.skyblue.skybluea.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityVideoView2Binding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.LikeVideo;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import com.skyblue.skybluea.viewmodels.PostListViewModel;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoViewActivity2 extends AppCompatActivity implements AdsMediaSource.MediaSourceFactory  {
    private ActivityVideoView2Binding binding;
    private SessionHandler session;
    private final Context context = this;
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private PlayerView playerView;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    private  DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private int mResumeWindow;
    private long mResumePosition;
    String videoUrl;
    List<Post> postList;
    PostListViewModel postListViewModel;
    PostAdapter adapter;
    User user;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoView2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, getString(R.string.app_name)));

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
        videoUrl = getIntent().getStringExtra("url");
        binding.videoName.setText(getIntent().getStringExtra("video_name"));
        binding.userName.setText(getIntent().getStringExtra("channel_name"));

        String totalLikes = getIntent().getStringExtra("likes");

        assert totalLikes != null;
        if (totalLikes.equals("null")){
            binding.totalLikes.setText("0");
        }else {
            binding.totalLikes.setText(totalLikes);
        }

        loadUploadTime();
        loadVideos();
        onClick();
    }

    private void onClick() {
        binding.likeCheckbox.setOnClickListener(v -> {
            if (session.isLoggedIn()){
                boolean isChecked = ((CheckBox)v).isChecked();

                if (isChecked){
                    // insert like

                    String totalLikes = binding.totalLikes.getText().toString();

                    Integer mTotalLikes = Integer.parseInt(totalLikes);
                    mTotalLikes++;

                    binding.totalLikes.setText(String.valueOf(mTotalLikes));
                    insertLike();
                }else {
                    // delete aleady liked
                    String totalLikes = binding.totalLikes.getText().toString();

                    Integer mTotalLikes = Integer.parseInt(totalLikes);
                    mTotalLikes--;

                    binding.totalLikes.setText(String.valueOf(mTotalLikes));
                    insertUnlike();
                }
            }else {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.commentRoundBox.setOnClickListener(v -> {
            if(session.isLoggedIn());

        });
    }

    private void insertUnlike() {
        String userId = user.getUser_id();
        String postId = getIntent().getStringExtra("post_id");

        RequestBody mUserid = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        assert postId != null;
        RequestBody mPostId = RequestBody.create(MediaType.parse("multipart/form-data"), postId);

        Call<LikeVideo> call = apiInterface.insertUnLike(mUserid, mPostId);

        call.enqueue(new Callback<LikeVideo>() {
            @Override
            public void onResponse(Call<LikeVideo> call, Response<LikeVideo> response) {
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
            public void onFailure(Call<LikeVideo> call, Throwable t) {

            }
        });
    }

    private void insertLike() {

        String userId = user.getUser_id();
        String postId = getIntent().getStringExtra("post_id");

        RequestBody mUserid = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        assert postId != null;
        RequestBody mPostId = RequestBody.create(MediaType.parse("multipart/form-data"), postId);

        Call<LikeVideo> call = apiInterface.insertLike(mUserid, mPostId);

        call.enqueue(new Callback<LikeVideo>() {
            @Override
            public void onResponse(Call<LikeVideo> call, Response<LikeVideo> response) {
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
            public void onFailure(Call<LikeVideo> call, Throwable t) {

            }
        });

    }

    private void loadVideos() {
        // responsible for measuring and positioning item,
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        // adding array list to our adapter class.
        adapter = new PostAdapter(postList);
        // setting adapter to our recycler view.
        binding.recyclerView.setAdapter(adapter);
        // framework provides the ViewModels, a special mechanism is required to create instances of them
        postListViewModel = new ViewModelProvider(this).get(PostListViewModel.class);

             /*
         Attach the Observer object to the LiveData object using the observe() method. The observe() method takes a LifecycleOwner object.
         This subscribes the Observer object to the LiveData object so that it is notified of changes. You usually attach the Observer object
         in a UI controller, such as an activity or fragment.
         */
        postListViewModel.getUserListObserver().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> list) {
                // check user array list empty or null
                if (list!=null){
                    postList = list;
                    adapter.updateUserList(list);
                   // binding.errorLoadHome.setVisibility(View.GONE);
                }
                if (list == null){
                    binding.recyclerView.setVisibility(View.GONE);
                   // binding.errorLoadHome.setVisibility(View.VISIBLE);
                }
            }
        });
        // call retrofit api
        postListViewModel.makeApiCall();
    }

    private void loadUploadTime() {
        String uploadTime = getIntent().getStringExtra("time_date");

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date past = null;
        try {
            past = format.parse(uploadTime);

            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                binding.uploadDate.setText(seconds + " seconds ago");
            } else if (minutes < 60) {
                binding.uploadDate.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                binding.uploadDate.setText(hours + " hours ago");
            } else {
                binding.uploadDate.setText(days + " days ago");
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
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(VideoViewActivity2.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(VideoViewActivity2.this, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
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
        mVideoSource = buildMediaSource(Uri.parse(videoUrl));
        Log.i("DEBUG"," mVideoSource "+mVideoSource);
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
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(VideoViewActivity2.this, R.drawable.ic_fullscreen_skrink));
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

    public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    List<Post> postList;
    private Activity context;
    public PostAdapter(List<Post>list){
        this.postList = list;
    }

    public void updateUserList(List<Post> list){
        this.postList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_fragment_home, parent, false);
            return new PostViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_dialog_loading_progress, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof PostViewHolder) {
            PostViewHolder postViewHolder = (PostViewHolder) holder;

            Glide
                    .with(HomeActivity2.context)
                    .load(postList.get(position).getThumbnail_url())
                    .placeholder(R.color.image_placeholder_bg)
                    .into(postViewHolder.imgThumbnail);
            int b=9;
            Glide
                    .with(HomeActivity2.context)
                    .load(postList.get(position).getProfile_url())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(postViewHolder.imgProfile);

               String newStringEmojidecooded = "";

            try {
                byte[] data = Base64.decode(postList.get(position).getVideo_name(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            postViewHolder.txtVideoName.setText(newStringEmojidecooded);
            //postViewHolder.txtUserName.setText(postList.get(position).getChannel_name());
            postViewHolder.txtDuration.setText(postList.get(position).getDuration());
            postViewHolder.txtChannelName.setText(postList.get(position).getChannel_name());

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date past = null;
            try {
                past = format.parse(postList.get(position).getTime_date());

                Date now = new Date();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
                    postViewHolder.txtUploadDate.setText(seconds + " seconds ago");
                } else if (minutes < 60) {
                    postViewHolder.txtUploadDate.setText(minutes + " minutes ago");
                } else if (hours < 24) {
                    postViewHolder.txtUploadDate.setText(hours + " hours ago");
                } else {
                    postViewHolder.txtUploadDate.setText(days + " days ago");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String finalNewStringEmojidecooded = newStringEmojidecooded;
            postViewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent intent = new Intent(view.getContext(), VideoViewActivity2.class);
//                    intent.putExtra("url", postList.get(position).getVideo_url());
//                    intent.putExtra("video_name", finalNewStringEmojidecooded);
//                    intent.putExtra("profile_image", postList.get(position).getProfile_url());
//                    intent.putExtra("channel_name", postList.get(position).getChannel_name());
//                    intent.putExtra("time_date", postList.get(position).getTime_date());
//                    view.getContext().startActivity(intent);

                        videoUrl = postList.get(position).getVideo_url();
                        binding.videoName.setText(finalNewStringEmojidecooded);
                        binding.userName.setText(postList.get(position).getChannel_name());
                        player.stop();

                        initExoPlayer();
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        TextView txtVideoName, txtUploadDateName, txtDuration, txtChannelName, txtUploadDate;
        ImageView imgProfile, imgThumbnail;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVideoName = itemView.findViewById(R.id.video_name);
            txtUploadDateName = itemView.findViewById(R.id.user_name);
            txtDuration = itemView.findViewById(R.id.duration);
            imgProfile = itemView.findViewById(R.id.profile_image);
            imgThumbnail = itemView.findViewById(R.id.thumbnail);
            txtChannelName = itemView.findViewById(R.id.channel_name);
            txtUploadDate = itemView.findViewById(R.id.upload_date);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);

        }
    }
}

//    public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//        private final int VIEW_TYPE_ITEM = 0;
//        private final int VIEW_TYPE_LOADING = 1;
//        List<Post> postList;
//        private Activity activity;
//
//        public PostAdapter (List<Post> list){
//            this.postList = list;
//        }
//
//        public void updateUserList(List<Post>list){
//            this.postList = list;
//            notifyDataSetChanged();
//        }
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            if (viewType == VIEW_TYPE_ITEM){
//                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_fragment_home_shimmer, parent,false);
//                return new PostViewHolder(view);
//            }else {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_dialog_loading_progress, parent, false);
//                return new LoadingViewHolder(view);
//            }
//        }
//
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            if (holder instanceof PostViewHolder){
//                PostViewHolder postViewHolder = (PostViewHolder) holder;
////                Glide
////                        .with(VideoViewActivity2.this)
////                        .load(postList.get(position).getProfile_url())
////                        .apply(RequestOptions.circleCropTransform())
////                        .placeholder(R.drawable.placeholder_person2)
////                        .into(postViewHolder.imgProfile);
////
////                Glide
////                        .with(VideoViewActivity2.this)
////                        .load(postList.get(position).getThumbnail())
////                        .placeholder(R.color.image_placeholder_bg)
////                        .into(postViewHolder.imgThumbnail);
////
////                String newStringEmojidecooded = "";
////                try {
////                    byte[] data = Base64.decode(postList.get(position).getVideo_name(), Base64.DEFAULT);
////                    newStringEmojidecooded = new String(data, "UTF-8");
////
////                } catch (UnsupportedEncodingException e) {
////                    e.printStackTrace();
////                }
////
////                postViewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        videoUrl = postList.get(position).getVideo_url();
////                        binding.videoName.setText(postList.get(position).getVideo_name());
////                        binding.userName.setText(postList.get(position).getUser_name());
////                        initExoPlayer();
////                    }
////                });
////                postViewHolder.txtVideoName.setText(newStringEmojidecooded);
////                postViewHolder.txtUserName.setText(postList.get(position).getUser_name());
////                postViewHolder.txtDuration.setText(postList.get(position).getDuration());
////
////                String finalNewStringEmojidecooded = newStringEmojidecooded;
////
//
//            }else if (holder instanceof LoadingViewHolder) {
//                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
//                loadingViewHolder.progressBar.setIndeterminate(true);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return postList == null ? 0 : postList.size();
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
//        }
//
//        public class PostViewHolder extends RecyclerView.ViewHolder{
//            TextView txtVideoName, txtUserName, txtDuration;
//            ImageView imgProfile, imgThumbnail;
//            public PostViewHolder(@NonNull View itemView) {
//                super(itemView);
//                txtVideoName = itemView.findViewById(R.id.video_name);
//                txtUserName = itemView.findViewById(R.id.user_name);
//                txtDuration = itemView.findViewById(R.id.duration);
//                imgProfile = itemView.findViewById(R.id.profile_image);
//                imgThumbnail = itemView.findViewById(R.id.thumbnail);
//            }
//        }
//
//        public  class LoadingViewHolder extends RecyclerView.ViewHolder {
//            public ProgressBar progressBar;
//            public LoadingViewHolder(View itemView) {
//                super(itemView);
//                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
//            }
//        }
//    }

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