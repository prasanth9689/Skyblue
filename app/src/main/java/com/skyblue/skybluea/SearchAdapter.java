package com.skyblue.skybluea;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
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

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SessionHandler session;

    private ProgressDialog pDialog;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_ITEM = 0;

    private List<Search> searchListList;
    private Activity mContext;

    private Dialog loadingProgressDialog;
    final Handler handler = new Handler();

    User user;

    public SearchAdapter(Activity context, List<Search>searchListList) {
        this.mContext = context;
        this.searchListList = searchListList;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.model_row_search, parent, false);
            return new SearchViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.model_dialog_loading_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof SearchViewHolder) {

            SearchViewHolder searchHolder = (SearchViewHolder) holder;

            final Search search = searchListList.get(position);

            session = new SessionHandler(mContext.getApplicationContext());
            user = session.getUserDetails();

            searchHolder.userNameText.setText(search.getUserName());;

            Glide
                    .with(mContext)
                    .load(search.getUserProfile())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(searchHolder.userProfileImage);



        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return searchListList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return searchListList == null ? 0 : searchListList.size();
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
        }
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameText;
        public ImageView userProfileImage;

        public SearchViewHolder(View itemView) {
            super(itemView);

            userNameText = itemView.findViewById(R.id.id_user_name_search);
            userProfileImage = itemView.findViewById(R.id.id_user_image_search);
        }
    }
}
