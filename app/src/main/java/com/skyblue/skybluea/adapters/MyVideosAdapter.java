package com.skyblue.skybluea.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.model.Post;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class MyVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SessionHandler session;
    private ProgressDialog progressDialog;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_ITEM = 0;

    private List<Post> postList;
    private Activity context;

    private Dialog loadingProgressDialog;
    final Handler handler = new ConsoleHandler();

    User user;

    public MyVideosAdapter(Activity context, List<Post>postList){
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.m_my_videos, parent, false);
            return new VideoListViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.m_loading_progress, parent, false);
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

//            final Post postVideoList = postList.get(position);
//
//            Picasso.get().load(postVideoList.getThumbnail()).placeholder(R.color.image_placeholder_bg).into(videoListViewHolder.thumbnailImageView);
//
//            String newStringEmojidecooded = "";
//
//            try {
//                byte[] data = Base64.decode(postList.get(position).getVideo_name(), Base64.DEFAULT);
//                newStringEmojidecooded = new String(data, "UTF-8");
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            videoListViewHolder.videoName.setText(newStringEmojidecooded);
//
////            if (postVideoList.getMedia_type().equals("video")) {
//
//                videoListViewHolder.imgPlayIcon.setVisibility(View.VISIBLE);
////            } else {
////                videoListViewHolder.imgPlayIcon.setVisibility(View.INVISIBLE);
////            }
//
//            String finalNewStringEmojidecooded = newStringEmojidecooded;
//            videoListViewHolder.thumbnailImageView.setOnClickListener(view -> {
//                Intent intent = new Intent(view.getContext(), VideoViewActivity2.class);
//                intent.putExtra("url", postList.get(position).getVideo_url());
//                intent.putExtra("video_name", finalNewStringEmojidecooded);
//                intent.putExtra("profile_image", postList.get(position).getProfile_url());
//                intent.putExtra("user_name", postList.get(position).getUser_name());
//                intent.putExtra("time_date", postList.get(position).getTime_date());
//                view.getContext().startActivity(intent);
//            });
//
//            videoListViewHolder.videoContainer.setOnClickListener(view -> {
//                Intent intent = new Intent(view.getContext(), VideoViewActivity2.class);
//                intent.putExtra("url", postList.get(position).getVideo_url());
//                intent.putExtra("video_name", finalNewStringEmojidecooded);
//                intent.putExtra("profile_image", postList.get(position).getProfile_url());
//                intent.putExtra("user_name", postList.get(position).getUser_name());
//                intent.putExtra("time_date", postList.get(position).getTime_date());
//                view.getContext().startActivity(intent);
//            });

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
        public TextView videoName;
        public CardView videoContainer;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            imgPlayIcon = itemView.findViewById(R.id.play_icon);
            videoName = itemView.findViewById(R.id.video_name);
            videoContainer = itemView.findViewById(R.id.video_con);
        }
    }
}
