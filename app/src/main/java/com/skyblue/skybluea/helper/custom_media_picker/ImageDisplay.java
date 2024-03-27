package com.skyblue.skybluea.helper.custom_media_picker;

import static androidx.core.view.ViewCompat.setTransitionName;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;

import java.io.File;
import java.util.ArrayList;

import com.skyblue.skybluea.activity.UploadActivity;
import com.skyblue.skybluea.helper.custom_media_picker.util.itemClickListener;
import com.skyblue.skybluea.helper.custom_media_picker.util.pictureFacer;
import com.skyblue.skybluea.helper.custom_media_picker.util.PicHolder;
import com.skyblue.skybluea.helper.custom_media_picker.util.MarginDecoration;
import com.skyblue.skybluea.helper.custom_media_picker.util.pictureBrowserFragment;

/**
 *
 *
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String foldePath;
    TextView folderName;
    ImageView back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_display);
//        getSupportActionBar().hide();

        folderName = findViewById(R.id.foldername);
        back = findViewById(R.id.back);
        folderName.setText(getIntent().getStringExtra("folderName"));

        foldePath =  getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if(allpictures.isEmpty()){
            load.setVisibility(View.VISIBLE);
            allpictures = getAllImagesByFolder(foldePath);
            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
            load.setVisibility(View.GONE);
        }else{

        }

        back.setOnClickListener(v -> finish());
    }

    /**
     *
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {



        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
            }
        });



        /*

        Modified by Prasanth Julai 17

        hided below lines
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();
         */


        /* Hided below by prasanth*/

         pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)

        /* Hided below by prasanth*/

        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }

         */

        /*

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();

         */




    }

    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {

    }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     * @param path a String corresponding to a folder path on the device external storage
     */
    public ArrayList<pictureFacer> getAllImagesByFolder(String path){
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE};
        Cursor cursor = getContentResolver().query( allVideosuri, projection, MediaStore.Video.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
        try {
            cursor.moveToFirst();
            do{
                pictureFacer pic = new pictureFacer();
                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));

                images.add(pic);
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for(int i = images.size()-1;i > -1;i--){
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
    public class picture_Adapter extends RecyclerView.Adapter<PicHolder> {


        private ArrayList<pictureFacer> pictureList;
        private Context pictureContx;
        private final itemClickListener picListerner;

        /**
         * @param pictureList  ArrayList of pictureFacer objects
         * @param pictureContx The Activities Context
         * @param picListerner An interface for listening to clicks on the RecyclerView's items
         */
        public picture_Adapter(ArrayList<pictureFacer> pictureList, Context pictureContx, itemClickListener picListerner) {
            this.pictureList = pictureList;
            this.pictureContx = pictureContx;
            this.picListerner = picListerner;
        }

        @NonNull
        @Override
        public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View cell = inflater.inflate(R.layout.pic_holder_item, container, false);
            return new PicHolder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {

            final pictureFacer image = pictureList.get(position);

            Glide.with(pictureContx)
                    .load(image.getPicturePath())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.picture);

            setTransitionName(holder.picture, String.valueOf(position) + "_image");

            holder.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       picListerner.onPicClicked(holder,position, pictureList);

                    Uri sendUri = Uri.fromFile(new File(image.getPicturePath()));

                    Intent intent = new Intent(v.getContext(), UploadActivity.class);
                    intent.putExtra("img", sendUri.toString());
                    v.getContext().startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return pictureList.size();
        }
    }

}
