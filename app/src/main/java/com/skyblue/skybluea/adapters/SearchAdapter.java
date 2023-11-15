package com.skyblue.skybluea.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.skyblue.skybluea.activity.LoginActivity;
import com.skyblue.skybluea.activity.SearchActivity;
import com.skyblue.skybluea.activity.VideoViewActivity;
import com.skyblue.skybluea.activity.VideoViewActivity2;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.model.Search;
import com.vanniktech.emoji.EmojiTextView;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    List<Search> postList;
    private Context context;
    public SearchAdapter(Context context, List<Search>list){
        this.postList = list;
        this.context = context;
    }

    public void updateUserList(List<Search> list){
        this.postList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_row_search, parent, false);
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
            Search post = postList.get(position);

            postViewHolder.txtPostText.setText(post.getChannel_name());

            String newStringEmojidecooded = "";
            try {
                byte[] data = Base64.decode(post.getVideo_name(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            postViewHolder.txtVideoName.setText(newStringEmojidecooded);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date past = null;
            try {
                past = format.parse(post.getUpload_date());

                Date now = new Date();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
                    // System.out.println(seconds+" seconds ago");
                    //   Toast.makeText(PrimaryHomeHomeActivity.this, seconds+"seconds ago",Toast.LENGTH_SHORT).show();
                    postViewHolder.txtTimeDate.setText(seconds + " seconds ago");
                } else if (minutes < 60) {
                    // System.out.println(minutes+" minutes ago");
                    //  Toast.makeText(PrimaryHomeHomeActivity.this, minutes+"minutes ago",Toast.LENGTH_SHORT).show();
                    postViewHolder.txtTimeDate.setText(minutes + " minutes ago");
                } else if (hours < 24) {
                    //System.out.println(hours+" hours ago");
                    //   Toast.makeText(PrimaryHomeHomeActivity.this, hours+"hours ago",Toast.LENGTH_SHORT).show();
                    postViewHolder.txtTimeDate.setText(hours + " hours ago");
                } else {
                    //System.out.println(days+" days ago");
                    //  Toast.makeText(PrimaryHomeHomeActivity.this, days+"days ago",Toast.LENGTH_SHORT).show();
                    postViewHolder.txtTimeDate.setText(days + " days ago");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Glide
                    .with(context)
                    .load(post.getThumbnail_url())
                    .placeholder(R.color.image_placeholder_bg)
                    .into(postViewHolder.imgPostImage);

//            Glide
//                    .with(context)
//                    .load(post.getProfile_url())
//                    .apply(RequestOptions.circleCropTransform())
//                    .placeholder(R.drawable.placeholder_person)
//                    .into(postViewHolder.imgUserImage);

//
//            postViewHolder.imgPostImage.setOnClickListener(v -> {
//                Intent intent = new Intent(context, VideoViewActivity2.class);
//                intent.putExtra("url", postList.get(position).getVideo_url());
//                intent.putExtra("video_name", postList.get(position).getVideo_name());
//                intent.putExtra("profile_image", postList.get(position).getProfile_url());
//                intent.putExtra("channel_name", postList.get(position).getChannel_name());
//                intent.putExtra("time_date", postList.get(position).getTime_date());
//                context.startActivity(intent);
//            });
//
//            holder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(context, VideoViewActivity2.class);
//                intent.putExtra("url", postList.get(position).getVideo_url());
//                intent.putExtra("video_name", postList.get(position).getVideo_name());
//                intent.putExtra("profile_image", postList.get(position).getProfile_url());
//                intent.putExtra("channel_name", postList.get(position).getChannel_name());
//                intent.putExtra("time_date", postList.get(position).getTime_date());
//                context.startActivity(intent);
//            });

            String finalNewStringEmojidecooded = newStringEmojidecooded;
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), VideoViewActivity2.class);
                intent.putExtra("url", postList.get(position).getVideo_url());
                intent.putExtra("video_name", finalNewStringEmojidecooded);
              //  intent.putExtra("profile_image", postList.get(position).getProfile_url());
                intent.putExtra("channel_name", postList.get(position).getChannel_name());
                intent.putExtra("time_date", postList.get(position).getUpload_date());
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
        private TextView txtVideoName, txtStatus;
        EmojiTextView txtPostText;
        ImageView imgPostImage, imgUserImage, imgPlayIcon;
        private TextView txtTimeDate;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserImage = itemView.findViewById(R.id.id_user_rounded_thumbnail);
            imgPostImage = itemView.findViewById(R.id.id_thumbnail);
            txtPostText = itemView.findViewById(R.id.id_user_name_text);
            txtVideoName = itemView.findViewById(R.id.id_video_name);
            txtTimeDate = itemView.findViewById(R.id.time_date);
            imgPlayIcon = itemView.findViewById(R.id.id_play_icon);
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