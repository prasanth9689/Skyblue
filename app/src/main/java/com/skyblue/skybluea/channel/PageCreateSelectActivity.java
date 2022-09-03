package com.skyblue.skybluea.channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skyblue.skybluea.R;

public class PageCreateSelectActivity extends AppCompatActivity  {

    MyRecyclerViewAdapter adapter;

    Context context = this;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_create_select);

        backButton = findViewById(R.id.id_back);

        // data to populate the RecyclerView with
        int[] images = {R.drawable.page_entertainment, R.drawable.page_celebrity, R.drawable.page_business, R.drawable.page_education , R.drawable.page_organization, R.drawable.page_service};

        String[] data = {"1", "2", "3", "4"};
        String[] name = {"Entertainment", "Celebrity", "Business", "Educational"};

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(this, data , name , images);

        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /*
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private String[] mData;
        private String[] mName;
        private LayoutInflater mInflater;


        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, String[] data , String[] name) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mName = name;
        }

        // inflates the cell layout from xml when needed
        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_model_page_create, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each cell
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.myTextView.setText(mData[position]);
            holder.myTextName.setText(mName[position]);


            holder.myTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, PageHomeActivity.class);
                    startActivity(intent);


                }
            });
        }

        // total number of cells
        @Override
        public int getItemCount() {
            return mData.length;
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView myTextView , myTextName , click;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.info_text);
                myTextName = itemView.findViewById(R.id.id_name);
                click = itemView.findViewById(R.id.click);

            }


        }






    }
     */

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private String[] mData;
        private String[] mName;
        private int[] mImages;
        private LayoutInflater mInflater;


        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, String[] data, String[] name, int[] images) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mName = name;
            this.mImages = images;
        }

        // inflates the cell layout from xml when needed
        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_model_page_create, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each cell
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.myTextView.setText(mData[position]);
            holder.myTextName.setText(mName[position]);

            holder.imageView.setImageResource(mImages[position]);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String pageId = mData[position];



               //     Toast.makeText(context, "Clicked"+mData[position], Toast.LENGTH_SHORT).show();



//                    switch (Integer.parseInt(mData[position])) {
//
//                        case 1:
//
//                            //  Toast.makeText(HomeProfileMainActivity.this , "111111111111111", Toast.LENGTH_SHORT).show();
//
//
//                            Intent intent = new Intent(PageCreateSelectActivity.this, PageCreateEntertainmentActivity.class);
//                            intent.putExtra("page_id", pageId);
//                            startActivity(intent);
//
//
//
//
//                            break;
//                        case 2:
//                            Intent intent1 = new Intent(PageCreateSelectActivity.this, PageCreateCeleNameActivity.class);
//                            intent1.putExtra("page_id", pageId);
//                            startActivity(intent1);
//
//                            break;
//
//                        case 3:
//                            Intent intent2 = new Intent(PageCreateSelectActivity.this, PageCreateBusNameActivity.class);
//                            intent2.putExtra("page_id", pageId);
//                            startActivity(intent2);
//                            break;
//
//                        case 4:
//                            Intent intent3 = new Intent(PageCreateSelectActivity.this, PageCreateEduNameActivity.class);
//                            intent3.putExtra("page_id", pageId);
//                            startActivity(intent3);
//                            break;
//
//                        case 5:
//                            //       Toast.makeText(HomeProfileMainActivity.this, "Logout" , Toast.LENGTH_SHORT).show();
//
//
//
//                            break;
//
//                        default:
//                            Toast.makeText(context , "default", Toast.LENGTH_SHORT).show();
//                            break;
//                    }





                }
            });
        }

        // total number of cells
        @Override
        public int getItemCount() {
            return mData.length;
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView myTextView , myTextName , click;
            CardView cardView;
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.info_text);
                myTextName = itemView.findViewById(R.id.id_name);
                click = itemView.findViewById(R.id.click);
                cardView = itemView.findViewById(R.id.id_card);
                imageView = itemView.findViewById(R.id.imageView);

            }


        }






    }
}