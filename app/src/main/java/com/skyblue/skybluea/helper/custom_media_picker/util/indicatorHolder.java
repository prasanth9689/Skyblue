package com.skyblue.skybluea.helper.custom_media_picker.util;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.skyblue.skybluea.R;


/**
 *
 *
 * recyclerViewPagerImageIndicator's ViewHolder
 */
public class indicatorHolder extends RecyclerView.ViewHolder{

    public ImageView image;
    private CardView card;
    View positionController;

    indicatorHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageIndicator);
        card = itemView.findViewById(R.id.indicatorCard);
        positionController = itemView.findViewById(R.id.activeImage);
    }
}
