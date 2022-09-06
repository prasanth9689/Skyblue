package com.skyblue.skybluea;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skyblue.skybluea.account.adapter.UserVideoListAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_ITEM = 0;

    private List<UserMediaModel> mediaList;
    private Activity context;

    public UserMediaAdapter(Activity context, List<UserMediaModel> mediaList){
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_model_profile_common_user_media, parent, false);
            return new MediaViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.model_dialog_loading_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mediaList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MediaViewHolder){
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
            final UserMediaModel userMediaList = mediaList.get(position);

            Picasso.get().load(userMediaList.getPost_image_url()).placeholder(R.color.image_placeholder_bg).into(mediaViewHolder.thumbnailImageView);

            if (userMediaList.getMedia_type().equals("video")) {
                mediaViewHolder.imgPlayIcon.setVisibility(View.VISIBLE);
            } else {
                mediaViewHolder.imgPlayIcon.setVisibility(View.INVISIBLE);
            }

            mediaViewHolder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMediaList.getMedia_type().equals("video")) {
                        Intent intent = new Intent(context, VideoViewActivity.class);
                        intent.putExtra("video_url", userMediaList.getVideo_url());
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(context, ImageViewActivity.class);
                        intent.putExtra("image_url", userMediaList.getPost_image_url());
                        intent.putExtra("post_user_name", userMediaList.getPost_user_name());
                        intent.putExtra("post_user_profile_image_url", userMediaList.getProfileurl());
                        context.startActivity(intent);
                    }
                }
            });

        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mediaList == null ? 0 : mediaList.size();
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailImageView , imgPlayIcon;

        public MediaViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.id_thumbnail);
            imgPlayIcon = itemView.findViewById(R.id.id_play_icon_ac);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
        }
    }
}
