package com.skyblue.skybluea.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.VideoViewActivity;
import com.skyblue.skybluea.databinding.MDialogLoadingProgressBinding;
import com.skyblue.skybluea.databinding.MFragmentHomeBinding;
import com.skyblue.skybluea.model.Post;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private List<Post> postList;
    private Context context;

    public PostAdapter(List<Post>list, Context context){
        this.postList = list;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUserList(List<Post> list, Context context){
        this.postList = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM){
            MFragmentHomeBinding binding = MFragmentHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new PostViewHolder(binding);
        }else {
            MDialogLoadingProgressBinding binding = MDialogLoadingProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(binding);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder instanceof PostViewHolder) {
            PostViewHolder postViewHolder = (PostViewHolder) holder;

            Glide
                    .with(HomeActivity.context)
                    .load(postList.get(position).getThumbnail_url())
                    .placeholder(R.color.image_placeholder_bg)
                    .into(postViewHolder.binding.thumbnail);

            Glide
                    .with(HomeActivity.context)
                    .load(postList.get(position).getProfile_url())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(postViewHolder.binding.profileImage);

            String newStringEmojidecooded = "";

            byte[] data = Base64.decode(postList.get(position).getVideo_name(), Base64.DEFAULT);
            newStringEmojidecooded = new String(data, StandardCharsets.UTF_8);

            postViewHolder.binding.videoName.setText(newStringEmojidecooded);
            postViewHolder.binding.duration.setText(postList.get(position).getDuration());
            postViewHolder.binding.channelName.setText(postList.get(position).getChannel_name());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date past;
            try {
                past = format.parse(postList.get(position).getTime_date());

                Date now = new Date();
                assert past != null;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
                    String sec = seconds + context.getString(R.string.seconds_ago);
                    postViewHolder.binding.uploadDate.setText(sec);
                } else if (minutes < 60) {
                    String min = minutes + context.getString(R.string.minutes_ago);
                    postViewHolder.binding.uploadDate.setText(min);
                } else if (hours < 24) {
                    String hrs = hours + context.getString(R.string.hours_ago);
                    postViewHolder.binding.uploadDate.setText(hrs);
                } else {
                    String day = days + context.getString(R.string.days_ago);
                    postViewHolder.binding.uploadDate.setText(day);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String finalNewStringEmojidecooded = newStringEmojidecooded;
            postViewHolder.binding.thumbnail.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), VideoViewActivity.class);
                intent.putExtra("post_id", postList.get(position).getPost_id());
                intent.putExtra("post_user_id", postList.get(position).getUser_id());
                intent.putExtra("url", postList.get(position).getVideo_url());
                intent.putExtra("video_name", finalNewStringEmojidecooded);
                intent.putExtra("profile_image", postList.get(position).getProfile_url());
                intent.putExtra("channel_id", postList.get(position).getChannel_id());
                intent.putExtra("channel_name", postList.get(position).getChannel_name());
                intent.putExtra("time_date", postList.get(position).getTime_date());
                intent.putExtra("likes", postList.get(position).getLikes());
                intent.putExtra("like_status", postList.get(position).getLike_status());
                intent.putExtra("comments", postList.get(position).getComments());
                intent.putExtra("total_views", postList.get(position).getTotal_views());
                view.getContext().startActivity(intent);
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.binding.progressBarloading.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     */
    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private final MFragmentHomeBinding binding;

        public PostViewHolder(MFragmentHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private final MDialogLoadingProgressBinding binding;

        public LoadingViewHolder(MDialogLoadingProgressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}