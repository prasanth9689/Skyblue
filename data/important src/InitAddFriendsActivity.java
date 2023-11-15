package com.skyblue.skybluea.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.LoginActivity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitAddFriendsActivity extends AppCompatActivity {
    private RecyclerView vList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Video> videoList;
    private RecyclerView.Adapter adapter;


    private Context context = this;


    private String url = "https://www.skyblue-watch.xyz/web/handle/initialize_add_friends.php";

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBarLoading;

    Button searchBtn ;
    EditText searchEditText;
    String QueryHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_add_friends);

        searchEditText =(EditText) findViewById(R.id.editTextSearch);
        searchBtn = findViewById(R.id.id_add_friend_button);
       // searchEditText.requestFocus();

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    videoList.clear();
                    getData();
                    return true;
                }
                return false;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoList.clear();
                getData();
            }
        });


        String getStringIntent = getIntent().getStringExtra("id");

    //    Toast.makeText(InitAddFriendsActivity.this,getStringIntent,Toast.LENGTH_SHORT).show();

        String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
        String get_country_name = getIntent().getStringExtra("countryname");
        String get_country_phone_code = getIntent().getStringExtra("phonecode");

     //   Toast.makeText(InitAddFriendsActivity.this,get_mobile_no+get_country_name+get_country_phone_code,Toast.LENGTH_SHORT).show();


/*
        ImageView ceoPhoto = findViewById(R.id.ceo_thumbnail);
        ImageView skybluePhoto = findViewById(R.id.skyblue_offcial_thumbnail);

 */
        Button nextBtn = findViewById(R.id.next_btn);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        progressBarLoading = findViewById(R.id.progress_bar_loading);

/*

        Glide
                .with( this.context )
                .load("https://www.skyblue-watch.xyz/web/uprofile_picture/picture/temp/prasanth.jpg")
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder_person)
                .into( ceoPhoto );

        Glide
                .with( this.context )
                .load("https://www.skyblue-watch.xyz/web/uprofile_picture/picture/temp/skyblue.jpg")
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder_person)
                .into( skybluePhoto );

 */

        //getSupportActionBar().hide();

        vList = findViewById(R.id.list);
        videoList = new ArrayList<>();

        adapter = new VideoAdapter(getApplicationContext(), videoList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(vList.getContext(), 0);
       // vList.addItemDecoration(new DividerItemDecoration(context, 0));
        vList.setHasFixedSize(true);
        vList.setLayoutManager(linearLayoutManager);
        vList.addItemDecoration(dividerItemDecoration);
        vList.setAdapter(adapter);

        videoList.clear();
        getData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

             //   videoList.clear();
              //  getData();

                 swipeRefreshLayout.setRefreshing(false);
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitAddFriendsActivity.this, Home.class);
                intent.putExtra("phonenumberonly",getIntent().getStringExtra("phonenumberonly"));
                startActivity(intent);
            }
        });
    }

    private void getData() {
      //  final ProgressDialog progressDialog = new ProgressDialog(this);
       // progressDialog.setMessage("Loading...");
        progressBarLoading.setVisibility(View.VISIBLE);


        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++ )
                            {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                Video video = new Video();
                                video.setName(jsonObject2.getString("name"));
                                video.setImageUrl(jsonObject2.getString("profile_url"));
                                video.setVideoUri(jsonObject2.getString("url"));
                                video.setVideoId(jsonObject2.getString("id"));


                                videoList.add(video);
                                //       Toast.makeText(SearchActivity.this, jsonObject2.getString("id"), Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            progressBarLoading.setVisibility(View.INVISIBLE);
                        }
                        // adapter.clear();


                        adapter.notifyDataSetChanged();
                        progressBarLoading.setVisibility(View.INVISIBLE);

                    }

                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressBarLoading.setVisibility(View.INVISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                QueryHolder = searchEditText.getText().toString();
                String cases = "%";
                String casess = "%";
                String query = cases + QueryHolder + casess;
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("query", query);
                params.put("query_text", QueryHolder);



                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public class Video
    {
        public String Name;
        public String ImageUrl;
        public String VideoUri;
        public String VideoId;

        public Video()
        {
            this.Name = Name;
            this.ImageUrl = ImageUrl;
            this.VideoUri = VideoUri;
            this.VideoId = VideoId;
        }

        public String getName()
        {
            return Name;
        }
        public void setName(String Name)
        {
            this.Name = Name;
        }

        public String getImageUrl()
        {
            return ImageUrl;
        }
        public void setImageUrl(String ImageUrl)
        {
            this.ImageUrl = ImageUrl;
        }


        public String getVideoUri(){return VideoUri; }
        public void setVideoUri(String VideoUri){this.VideoUri = VideoUri;}

        public String getVideoId(){return VideoId;}
        public void setVideoId(String VideoId){this.VideoId = VideoId;}

    }

    public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
        private SessionHandler session;
        private String url;
        private Context context;
        private List<Video> list;
        private static final String SHARED_PREFE_ID = "mypref";
        private static final String KEY_PREFE_ID = "myid";
        private static final String KEY_PREFE_NAME = "user_name";

        private String check_request = "https://www.skyblue-watch.xyz/web/handle/check_request.php";
        private String cancel_request = "https://www.skyblue-watch.xyz/web/handle/cancel_request.php";
        private static final String SEND_REQUEST= "1";
        private static final String TempStatus = "1";

        public VideoAdapter(Context context, List<Video> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.model_row_init_add_friends, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Video video = list.get(position);
            session = new SessionHandler(context.getApplicationContext());



            holder.textName.setText(video.getName());


            //  Picasso.get().load(video.getImageUrl()).placeholder(R.drawable.placeholder2).into(holder.thumbnail);

            Glide
                    .with( this.context )
                    .load(video.getImageUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into( holder.thumbnail );



            SharedPreferences sp = holder.itemView.getContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            final String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);
            final String userNameHolder = sp.getString(KEY_PREFE_NAME, null);

            String testPurposeUserId = "191";

            if(testPurposeUserId.equals(video.getVideoId()))         // your condition to hide dropdown
            {
                holder.SendRequestCardView.setVisibility(View.INVISIBLE);
                holder.SearchProgressbar.setVisibility(View.INVISIBLE);

            } else

            {
                //     holder.SendRequestButtonSearch.setVisibility(View.VISIBLE);
            }
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                    mPlayer.start();


                    holder.SendRequestCardView.setVisibility(View.INVISIBLE);

                    sendRequset();



                }

                private void sendRequset() {

                    holder.addFriendProgressBar.setVisibility(View.VISIBLE);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, check_request,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {

                                    Context context = null;
//                                Toast.makeText(context , ServerResponse,Toast.LENGTH_SHORT).show();

                                    if (ServerResponse.equals(TempStatus))
                                    {

                                        holder.addFriendProgressBar.setVisibility(View.INVISIBLE);
                                        holder.RequestHasBeenSendBtnCardView.setVisibility(View.VISIBLE);
                                        holder.cancelRequestBtnCardView.setVisibility(View.VISIBLE);
                                    }else
                                    {
//                                   Toast.makeText(context, "Request sended ", Toast.LENGTH_SHORT).show();
                                    }
                                    // Showing response message coming from server.
                                    //  Toast.makeText(RegisterMobileActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {



                                    // Showing error message if something goes wrong.
                                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {

                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();

                            // Adding All values to Params.
                            params.put("from_id", userIdHolderShared);
                            params.put("from_name",userNameHolder);
                            params.put("to_name",video.getName());
                            params.put("to_id",video.getVideoId());


                            return params;
                        }

                    };

                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);



                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Create intent getting the context of your View and the class where you want to go

                    session = new SessionHandler(context);

                    if(session.isLoggedIn()){
                        /*
                        Intent intent = new Intent(context, ProfileViewActivity.class);
                        intent.putExtra("user_id", video.getVideoId());
                        startActivity(intent ); //If you are inside activity, otherwise pass context to this funtion

                         */
                    }else{
                        Intent ii = new Intent( context, LoginActivity.class);
                        startActivity(ii);
                    }

                }
            });

            holder.cancelReqest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cancelRequest();
                }
                private void cancelRequest() {


                    holder.cancelRequestProgressBar.setVisibility(View.VISIBLE);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, cancel_request,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {

                                    Context context = null;
//                                Toast.makeText(context , ServerResponse,Toast.LENGTH_SHORT).show();

                                    if (ServerResponse.equals(TempStatus))
                                    {

                                        holder.cancelRequestBtnCardView.setVisibility(View.INVISIBLE);
                                        holder.cancelRequestProgressBar.setVisibility(View.INVISIBLE);

                                        holder.RequestHasBeenSendBtnCardView.setVisibility(View.INVISIBLE);
                                        holder.SendRequestCardView.setVisibility(View.VISIBLE);

                                    }else
                                    {
//                                   Toast.makeText(context, "Request sended ", Toast.LENGTH_SHORT).show();
                                    }
                                    // Showing response message coming from server.
                                    //  Toast.makeText(RegisterMobileActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {



                                    // Showing error message if something goes wrong.
                                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {

                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();

                            // Adding All values to Params.
                            params.put("from_id", userIdHolderShared);
                            params.put("from_name",userNameHolder);
                            params.put("to_name",video.getName());
                            params.put("to_id",video.getVideoId());


                            return params;
                        }

                    };

                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);



                }

            });

        }



        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textName, textImageUrl,videoUri, textVideoId ;
            public ImageView thumbnail;
            public CardView SendRequestCardView ,RequestHasBeenSendBtnCardView , cancelRequestBtnCardView;
            public Button addFriend , cancelReqest;
            public ProgressBar SearchProgressbar , addFriendProgressBar , cancelRequestProgressBar;

            public ViewHolder(final View itemView) {
                super(itemView);

                textName = itemView.findViewById(R.id.text_name);
                thumbnail = itemView.findViewById(R.id.thumbnail_view);

                SendRequestCardView = itemView.findViewById(R.id.id_card_view_add_friend_button);
                RequestHasBeenSendBtnCardView = itemView.findViewById(R.id.id_card_view_add_friend_has_send_button);
                cancelRequestBtnCardView = itemView.findViewById(R.id.id_card_view_cancel_request_button);

                addFriendProgressBar = itemView.findViewById(R.id.progress_bar);

                addFriend = itemView.findViewById(R.id.id_add_friend);
                cancelReqest = itemView.findViewById(R.id.cancel_request);
                cancelRequestProgressBar = itemView.findViewById(R.id.cancel_req_progress_bar);



            }


        }






    }

    // Disable back press button
    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}

