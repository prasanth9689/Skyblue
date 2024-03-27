package com.skyblue.skybluea.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.channels.ManageChannelActivity;
import com.skyblue.skybluea.model.ChannelsModel;

import java.util.ArrayList;

public class ChannelsAdatpter extends RecyclerView.Adapter<ChannelsAdatpter.ViewHolder> {
    private Context context;
    private Activity activity;
    private ArrayList<ChannelsModel> arrayList;

    public ChannelsAdatpter(Context context, Activity activity, ArrayList<ChannelsModel> arrayList){
        this.context = context;
        this.activity  = activity ;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ChannelsAdatpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.model_channel_list, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelsAdatpter.ViewHolder holder, int position) {

        final ChannelsModel channelsModel = arrayList.get(position);
        holder.channelName.setText(channelsModel.getChannelName());
        holder.main.setOnClickListener(view -> {
            Intent intent = new Intent(context, ManageChannelActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        Glide
                .with(HomeActivity.context)
                .load(R.drawable.channel_bg)
                .placeholder(R.color.image_placeholder_bg)
                .into(holder.channelImage);

        String channelName = channelsModel.getChannelName();

        holder.channelLetter.setText(channelName.substring(0,1));


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView channelName, channelLetter;
        private CardView main;
        private ImageView channelImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channelName = itemView.findViewById(R.id.channel_name);
            main = itemView.findViewById(R.id.main);
            channelImage = itemView.findViewById(R.id.channel_image);
            channelLetter = itemView.findViewById(R.id.channel_first_letter);
        }
    }
}
