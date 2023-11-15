package com.skyblue.skybluea.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;

import com.skyblue.skybluea.activity.HomeActivity2;
import com.skyblue.skybluea.activity.VideoViewActivity2;
import com.skyblue.skybluea.model.Post;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            postViewHolder.imgThumbnail.setOnClickListener(view -> {
              Intent intent = new Intent(view.getContext(), VideoViewActivity2.class);
                intent.putExtra("post_id", postList.get(position).getPost_id());
                intent.putExtra("url", postList.get(position).getVideo_url());
                intent.putExtra("video_name", finalNewStringEmojidecooded);
                intent.putExtra("profile_image", postList.get(position).getProfile_url());
                intent.putExtra("channel_name", postList.get(position).getChannel_name());
                intent.putExtra("time_date", postList.get(position).getTime_date());
                intent.putExtra("likes", postList.get(position).getLikes());
                intent.putExtra("like_status", postList.get(position).getLike_status());
                // send like status to vv activity use putextra do now
                view.getContext().startActivity(intent);
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

    public static class PostViewHolder extends RecyclerView.ViewHolder{
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

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
        }
    }
}