package com.skyblue.skybluea.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.skyblue.skybluea.R;

import java.io.File;
import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.viewHolder> {
    Context context;
    Activity activity;
    ArrayList<DownloadModel> arrayList;

    private View emptyView;

    public DownloadAdapter(Context context,Activity activity, ArrayList<DownloadModel> arrayList) {
        this.context = context;
        this.activity  = activity ;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public DownloadAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_row_download_list, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {

        String video_url = String.valueOf(arrayList.get(position).getFilePath());

        File file = new File(video_url);
        if(file.exists()){
            MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
            mMMR.setDataSource(context, Uri.parse(video_url));
            Bitmap bitmapThumbnail = mMMR.getFrameAtTime();

            Glide
                    .with( context )
                    .load(bitmapThumbnail)
                    //  .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.color_bg_gray)
                    .into( holder.thumbnail );

        } else
        {
            Glide
                    .with( context )
                    .load(R.drawable.default_placeholder)
                    //  .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.color_bg_gray)
                    .into( holder.thumbnail );
        }



        Toast.makeText(context, arrayList.get(position).getFilePath() , Toast.LENGTH_SHORT).show();

        holder.txtFileName.setText(arrayList.get(position).getFileName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(video_url);
                if(file.exists()){
                    Intent intent = new Intent(context , DownloadsVideoViewActivity.class);
                    intent.putExtra("url", arrayList.get(position).getFilePath());
                    intent.putExtra("name", arrayList.get(position).getFileName());
                    activity.startActivity(intent);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtFileName , txtNoVideoAvailable;
        ImageButton optionMenuBtn;
        ImageView thumbnail;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txtFileName = itemView.findViewById(R.id.id_file_name);
            thumbnail = itemView.findViewById(R.id.thumbnail);
          //  txtNoVideoAvailable = itemView.findViewById(R.id.no_video_file);
            // optionMenuBtn = itemView.findViewById(R.id.id_download_popup_menu);
        }
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

}
