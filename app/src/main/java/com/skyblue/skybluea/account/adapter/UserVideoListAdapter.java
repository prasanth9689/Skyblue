package com.skyblue.skybluea.account.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skyblue.skybluea.ImageViewActivity;
import com.skyblue.skybluea.Post;
import com.skyblue.skybluea.PostAdapter;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.VideoViewActivity;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.List;

public class UserVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SessionHandler session;
    private ProgressDialog pDialog;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_ITEM = 0;

    private List<Post> postList;
    private Activity context;

    private Dialog loadingProgressDialog;
    final Handler handler = new Handler();

    User user;

    public UserVideoListAdapter(Activity context, List<Post>postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_model_account_user_video_list, parent, false);
            return new VideoListViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.model_dialog_loading_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoListViewHolder) {

            VideoListViewHolder videoListViewHolder = (VideoListViewHolder) holder;

            final Post postVideoList = postList.get(position);

            Picasso.get().load(postVideoList.getPost_image_url()).placeholder(R.color.image_placeholder_bg).into(videoListViewHolder.thumbnailImageView);

            if (postVideoList.getMedia_type().equals("video")) {

                videoListViewHolder.imgPlayIcon.setVisibility(View.VISIBLE);
            } else {
                videoListViewHolder.imgPlayIcon.setVisibility(View.INVISIBLE);
            }

            videoListViewHolder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (postVideoList.getMedia_type().equals("video")) {
                        Intent intent = new Intent(context, VideoViewActivity.class);
                        intent.putExtra("video_url", postVideoList.getVideo_url());
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(context, ImageViewActivity.class);
                        intent.putExtra("image_url", postVideoList.getPost_image_url());
                        intent.putExtra("post_user_name", postVideoList.getPost_user_name());
                        intent.putExtra("post_user_profile_image_url", postVideoList.getProfileurl());
                        context.startActivity(intent);
                    }
                }
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

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
        }
    }

    static class VideoListViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailImageView , imgPlayIcon;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.id_thumbnail);
            imgPlayIcon = itemView.findViewById(R.id.id_play_icon_ac);
        }
    }

}
