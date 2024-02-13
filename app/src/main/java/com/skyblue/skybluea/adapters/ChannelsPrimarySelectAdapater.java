package com.skyblue.skybluea.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.databinding.ModelChannelListPrimarySelBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.model.ChannelsModel;

import java.util.ArrayList;

public class ChannelsPrimarySelectAdapater extends RecyclerView.Adapter<ChannelsPrimarySelectAdapater.ViewHolder> {
    private SessionHandler session;
    private final Context context;
    private Activity activity;
    private final ArrayList<ChannelsModel> arrayList;

    public ChannelsPrimarySelectAdapater(Context context, Activity activity, ArrayList<ChannelsModel> arrayList){
        this.context = context;
        this.activity  = activity ;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ChannelsPrimarySelectAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelChannelListPrimarySelBinding binding = ModelChannelListPrimarySelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelsPrimarySelectAdapater.ViewHolder holder, int position) {
        session = new SessionHandler(context);
        final ChannelsModel channelsModel = arrayList.get(position);
        holder.binding.channelName.setText(channelsModel.getChannelName());

        Glide
                .with(HomeActivity.context)
                .load(R.drawable.channel_bg)
                .placeholder(R.color.image_placeholder_bg)
                .into(holder.binding.channelImage);

        String channelName = channelsModel.getChannelName();

        holder.binding.channelFirstLetter.setText(channelName.substring(0,1).toUpperCase());

        holder.binding.main.setOnClickListener(view -> {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
            holder.binding.radioButton.setEnabled(true);
            session.saveChannelPrimary(channelsModel.getChannelId(), channelsModel.getChannelName());
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });

        holder.binding.radioButton.setOnClickListener(view -> {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
            holder.binding.radioButton.setEnabled(true);
            session.saveChannelPrimary(channelsModel.getChannelId(), channelsModel.getChannelName());
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ModelChannelListPrimarySelBinding binding;

        public ViewHolder(ModelChannelListPrimarySelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}