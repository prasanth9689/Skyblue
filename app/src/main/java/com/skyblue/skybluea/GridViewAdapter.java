package com.skyblue.skybluea;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.skyblue.skybluea.account.AccountActivity;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    //Imageloader to load images
    private ImageLoader imageLoader;

    //Context
    private Context context;

    //Array List that would contain the urls and the titles for the images
    private ArrayList<String> images;
    private ArrayList<String> id;


    public GridViewAdapter (Context context, ArrayList<String> images , ArrayList<String> id){
        //Getting all the values
        this.context = context;
        this.images = images;
        this.id = id;

    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Creating a linear layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //NetworkImageView
        NetworkImageView networkImageView = new NetworkImageView(context);

        //Initializing ImageLoader
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(images.get(position), ImageLoader.getImageListener(networkImageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        //Setting the image url to load
        networkImageView.setImageUrl(images.get(position),imageLoader);
        //Creating a textview to show the title
        TextView textView = new TextView(context);
        textView.setText(id.get(position));
        textView.setVisibility(View.INVISIBLE);

networkImageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
     //   Toast.makeText(context, "Url address"+images.get(position), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(v.getContext(), AccountActivity.class);
        intent.putExtra("post_image_url",images.get(position));
        intent.putExtra("post_id" , id.get(position));
     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

     context.startActivity(intent );


    }
});



        //Scaling the imageview
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setLayoutParams(new GridView.LayoutParams(200,200));

        //Adding views to the layout
      //  linearLayout.addView(textView);
        linearLayout.addView(networkImageView);

        //Returnint the layout

        return linearLayout;
    }
}