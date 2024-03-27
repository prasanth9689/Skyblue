package com.skyblue.skybluea.helper.custom_media_picker.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.UploadActivity;
import com.skyblue.skybluea.helper.custom_media_picker.ImageDisplay;

import java.util.ArrayList;

/**
 *
 *
 * An adapter for populating RecyclerView with items representing folders that contain images
 */
public class pictureFolderAdapter extends RecyclerView.Adapter<pictureFolderAdapter.FolderHolder>{

    private ArrayList<imageFolder> folders;
    private Context folderContx;
    private itemClickListener listenToClick;

    /**
     *
     * @param folders An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param folderContx The Activity or fragment Context
     * @param listen interFace for communication between adapter and fragment or activity
     */
    public pictureFolderAdapter(ArrayList<imageFolder> folders, Context folderContx, itemClickListener listen) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.picture_folder_item, parent, false);
        return new FolderHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        final imageFolder folder = folders.get(position);

        folderContx = holder.itemView.getContext();

        Glide.with(folderContx)
                .load(folder.getFirstPic())
                .apply(new RequestOptions().centerCrop())
                .into(holder.folderPic);


        //Log.e("hdd", folder.getFirstPic());

        //setting the number of images

        /*
        Modification

        From :
        Example

            Downloads (15)

            To:

            Downloads

            Original code
        String text = ""+folder.getFolderName() + "("+folder.getNumberOfPics()+")";
         */
        String text = ""+folder.getFolderName();
        String folderSizeString=""+folder.getNumberOfPics()+" Media";
      //  holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);

        holder.folderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   listenToClick.onPicClicked(folder.getPath(),folder.getFolderName());
                Intent intent = new Intent(folderContx, ImageDisplay.class);
                intent.putExtra("folderPath", folder.getPath());
                intent.putExtra("folderName",folder.getFolderName());
                folderContx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


    public class FolderHolder extends RecyclerView.ViewHolder{
        ImageView folderPic;
        TextView folderName;
        //set textview for foldersize
     //   TextView folderSize;

        CardView folderCard;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
         //   folderSize=itemView.findViewById(R.id.folderSize);
            folderCard = itemView.findViewById(R.id.folderCard);
        }
    }

}
