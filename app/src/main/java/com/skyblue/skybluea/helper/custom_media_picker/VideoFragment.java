package com.skyblue.skybluea.helper.custom_media_picker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.helper.custom_media_picker.util.MarginDecoration;
import com.skyblue.skybluea.helper.custom_media_picker.util.PicHolder;
import com.skyblue.skybluea.helper.custom_media_picker.util.imageFolder;
import com.skyblue.skybluea.helper.custom_media_picker.util.itemClickListener;
import com.skyblue.skybluea.helper.custom_media_picker.util.pictureFacer;
import com.skyblue.skybluea.helper.custom_media_picker.util.pictureFolderAdapter;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private Context folderContx;
    private itemClickListener listenToClick;
    private TextView empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video, container, false);
        empty = view.findViewById(R.id.empty);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.hasFixedSize();
        ArrayList<imageFolder> folds = getPicturePaths();


        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            // RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,MainActivity.this,this);
            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,folderContx, listenToClick);
            recyclerView.setAdapter(folderAdapter);
        }

        //changeStatusBarColor();
        return view;
    } /**1
     * @return
     * gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */
    private ArrayList<imageFolder> getPicturePaths(){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media.BUCKET_ID};
        Cursor cursor = requireActivity().getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                }else{
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" "+picFolders.get(i).getNumberOfPics());
        }

        //reverse order ArrayList
       /* ArrayList<imageFolder> reverseFolders = new ArrayList<>();

        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }*/

        return picFolders;
    }



    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param pictureFolderPath a String corresponding to a folder path on the device external storage
     */

    public void onPicClicked(String pictureFolderPath,String folderName) {
//        Intent move = new Intent(MainActivity.this,ImageDisplay.class);
//        move.putExtra("folderPath",pictureFolderPath);
//        move.putExtra("folderName",folderName);
//
//        //  Important
//        move.putExtra("image_id", getIntent().getStringExtra("image_id"));
//        move.putExtra("common_id", get_common_id);
//        move.putExtra("phonenumberonly" , get_mobile_no);
//
//
//        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
//        startActivity(move);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenToClick = (itemClickListener) getParentFragment();
    }

    /**
     * Default status bar height 24dp,with code API level 24
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor()
    {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(folderContx.getApplicationContext(),R.color.black));

    }
}
