package com.skyblue.skybluea;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.account.AccountUpdateCoverActivity;
import com.skyblue.skybluea.account.AccountUpdateProfileActivity;
import com.skyblue.skybluea.channel.PageAdminUpdateCoverActivity;
import com.skyblue.skybluea.channel.PageAdminUpdateLogoActivity;
import com.skyblue.skybluea.fragments.pictureBrowserFragment;
import com.skyblue.skybluea.registration.InitImageViewActivity;
import com.skyblue.skybluea.utils.MarginDecoration;
import com.skyblue.skybluea.utils.PicHolder;
import com.skyblue.skybluea.utils.itemClickListener;
import com.skyblue.skybluea.utils.pictureFacer;

import java.io.File;
import java.util.ArrayList;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_display);
        getSupportActionBar().hide();

        folderName = findViewById(R.id.foldername);
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
        Uri allVideosuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
        try {
            cursor.moveToFirst();
            do{
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

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
                    //   picListerner.onPicClicked(holder,position, pictureList);

                    //Toast.makeText(v.getContext(), "Clicked",Toast.LENGTH_SHORT).show();


                    String id = getIntent().getStringExtra("image_id");

                    //  int myId = 0;
                    Uri sendUri = Uri.fromFile(new File(image.getPicturePath()));

                    switch (Integer.parseInt(id )) {

                        case 0:

                            Toast.makeText(ImageDisplay.this, " 0", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:

                             //  Toast.makeText(ImageDisplay.this, " 1 - PostPictureActivity", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(v.getContext(), PostPictureActivity.class);
                            intent.putExtra("img", sendUri.toString());
                            v.getContext().startActivity(intent);


                            break;

                        case 2:
                            //   Toast.makeText(ImageDisplay.this, " 2 - Update profile picture - Registration stage init", Toast.LENGTH_SHORT).show();


                            String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
                            String get_country_name = getIntent().getStringExtra("countryname");
                            String get_country_phone_code = getIntent().getStringExtra("phonecode");

                            Intent intent2 = new Intent(v.getContext(), InitImageViewActivity.class);
                            intent2.putExtra("img", sendUri.toString());

                            intent2.putExtra("phonenumberonly" , get_mobile_no);
                            intent2.putExtra("countryname" , get_country_name);
                            intent2.putExtra("phonecode" , get_country_phone_code);
                            v.getContext().startActivity(intent2);




                            break;

                        case 3:
                            Intent intent3 = new Intent(v.getContext(), AccountUpdateProfileActivity.class);
                            intent3.putExtra("img", sendUri.toString());
                            v.getContext().startActivity(intent3);
                            break;

                        case 4:
                            Intent intent21 = new Intent(v.getContext(), AccountUpdateCoverActivity.class);
                            intent21.putExtra("img", sendUri.toString());
                            v.getContext().startActivity(intent21);


                            break;
                        case 5:
                            Intent intent25 = new Intent(v.getContext(), PageAdminUpdateLogoActivity.class);
                            intent25.putExtra("img", sendUri.toString());
                            v.getContext().startActivity(intent25);

                            break;

                        case 6:
                            String get_common_id = getIntent().getStringExtra("common_id");
                            Intent intent6 = new Intent(v.getContext(), PageAdminUpdateCoverActivity.class);
                            intent6.putExtra("img", sendUri.toString());
                            intent6.putExtra("common_id", get_common_id);
                            v.getContext().startActivity(intent6);

                            break;


                        default:
                            Toast.makeText(ImageDisplay.this, " Try again", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return pictureList.size();
        }
    }

}
