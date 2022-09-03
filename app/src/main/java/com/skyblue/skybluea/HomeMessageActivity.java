package com.skyblue.skybluea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeMessageActivity extends AppCompatActivity {
   private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final String SHARED_PREFE_PROFILE_ID = "myprofile";
    private static final String KEY_PREFE_PROFILE_ID = "myprofileid";

    ListView myGridView;
    ProgressBar myDataLoaderProgressBar;

    final Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_message);

        myGridView = findViewById(R.id.myListView);
        myDataLoaderProgressBar = findViewById(R.id.myDataLoaderProgressBar);

        new DataRetriever(this.getApplication()).retrieve(myGridView, myDataLoaderProgressBar);




         }

    class ListViewAdapter extends BaseAdapter
    {
        Context c;
        ArrayList<Notification> notifications;

        public ListViewAdapter(Context c, ArrayList<Notification>notifications)
        {
            this.c = c;
            this.notifications = notifications;
        }

        @Override
        public int getCount() {return notifications.size();}

        @Override
        public Object getItem(int i){return notifications.get(i);}

        @Override
        public long getItemId(int i){return i;}


        @Override
        public View getView(int i, View view , ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = LayoutInflater.from(c).inflate(R.layout.row_model_nav_home_message_list, viewGroup, false);
            }

            TextView txtId = view.findViewById(R.id.id_model_notification);
            TextView txtSenderId = view.findViewById(R.id.sender_id);
            TextView txtFollowingName = view.findViewById(R.id.sender_name);
            TextView txtFollowingId = view.findViewById(R.id.following_id);
            TextView txtStatus = view.findViewById(R.id.status_model_notification);
            TextView txtProfile_Url = view.findViewById(R.id.profile_url_model_notification);


            ImageView user_small_thumbnail = view.findViewById(R.id.id_user_rounded_thumbnail_notification);


            final Notification notification = (Notification) this.getItem(i);

            txtId.setText(notification.getId());

            /*
            txtSenderId.setText(notification.getSender_Id());
            txtSenderName.setText(notification.getSender_Name());
            txtStatus.setText(notification.getStatus());
            txtProfile_Url.setText(notification.getProfile_Url());
            */

            txtFollowingName.setText(notification.getFollowing_name());
            txtFollowingId.setText(notification.getFollowing_id());

            if(notification.getProfile_url() != null && notification.getProfile_url().length() > 0) {

                //  Picasso.get().load(notification.getProfile_Url()).placeholder(R.drawable.placeholder).into(user_small_thumbnail);

                Glide
                        .with(context)
                        .load(notification.getProfile_url())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.placeholder_person)
                        .into(user_small_thumbnail);
            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("buddy_id_key", notification.getFollowing_id());
                    intent.putExtra("profile_id_key" , notification.getProfile_url());
                    startActivity(intent);



                }
            });
            return view;
        }
    }

    class DataRetriever
    {
        private static final String get_buddy_list = "https://www.skyblue-watch.xyz/web/handle/get_follow_list.php";


        SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolder = sp.getString(KEY_PREFE_ID, null);

        private final Context c;
        private ListViewAdapter adapter;

        public DataRetriever(Context c)
        {
            this.c = c;
        }

        public void retrieve(final ListView gv, final ProgressBar myProgressBar)
        {
            final ArrayList<Notification> notifications = new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);



            AndroidNetworking.post(get_buddy_list)
                    .addBodyParameter("follower_id" ,userIdHolder)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //    Toast.makeText(c, ""+response, Toast.LENGTH_LONG).show();
                            JSONObject jsonObject;
                            Notification notification;

                            try
                            {
                                for (int i = 0; i<response.length(); i++)
                                {
                                    jsonObject = response.getJSONObject(i);

                                    String id = jsonObject.getString("id");
                                    String profile_url = jsonObject.getString("profile_url");
                                    String following_name = jsonObject.getString("following_name");
                                    String following_id = jsonObject.getString("following_id");

                                    notification = new Notification(id,profile_url,following_name,following_id);

                                    notifications.add(notification);

                                }

                                adapter = new ListViewAdapter(c, notifications);
                                gv.setAdapter(adapter);
                                myProgressBar.setVisibility(View.GONE);
                            }catch (JSONException e)
                            {
                                myProgressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);

                        }
                    });

        }
    }

    class Notification {
        private String id , profile_url , following_name , following_id;


        public Notification(String id , String profile_url , String following_name , String following_id){
            this.id = id;
            this.profile_url = profile_url;
            this.following_name = following_name;
            this.following_id = following_id;
        }
        public String getId() {return id;}
        public String getProfile_url() {
            return profile_url;
        }
        public String getFollowing_name() {
            return following_name;
        }
        public String getFollowing_id() {
            return following_id;
        }
    }

}
