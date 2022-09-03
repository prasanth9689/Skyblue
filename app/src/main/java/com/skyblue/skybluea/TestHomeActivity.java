package com.skyblue.skybluea;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TestHomeActivity extends AppCompatActivity {
    private static final String KEY_NULL = "null";
    private SessionHandler session;


    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final String KEY_PREFE_PROFILE_URL = "profile_url";

    //private static final String GET_POST_RECYCLER = "https://www.skyblue-watch.xyz/web/handle/pages/page_post/picture/stocl/get_by_page_id.php";
    private static final String GET_POST_RECYCLER = "https://www.skyblue-watch.xyz/web/handle/pages/page_post/picture/stocl/test.php";

    Context context = this;

    String page_id, common_id;




    private static final String KEY_EMPTY = "";

    Button postBtn;
    EmojiEditText postTxt;
    ImageView emojiIcon , emojiKeyBod , cmaeraIcon;
    ViewGroup rootView;

    String postTextHolder;

    String toServerUnicodeEncoded;

    private RecyclerView pList;
    private LinearLayoutManager linearLayoutManager;
    private List<Post> postList;
    private RecyclerView.Adapter adapter;

    private String user_profile_url;
    private String user_id_holder;
    private String user_name_logged;
    private String newStringEmojidecooded;
    private String newEmojidecodedPostText;
    private String newEmojidecodedPostUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());

        setContentView(R.layout.activity_test_home);
        //getSupportActionBar().hide();


        postBtn = findViewById(R.id.post_btn);

        postTxt = findViewById(R.id.id_post_text);
        emojiIcon = findViewById(R.id.emoji);
        emojiKeyBod = findViewById(R.id.emoji_keyboard);
        rootView = findViewById(R.id.main_activity_root_view);


        pList = findViewById(R.id.recycler_view);


        postList = new ArrayList<>();

        adapter = new PostAdapter(context.getApplicationContext(), postList);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pList.setHasFixedSize(true);
        pList.setItemViewCacheSize(20);
        pList.setLayoutManager(linearLayoutManager);
        pList.setAdapter(adapter);


        postList.clear();
        getData();


        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(postTxt);





        emojiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emojiPopup.toggle();

                emojiIcon.setVisibility(View.INVISIBLE);
                emojiKeyBod.setVisibility(View.VISIBLE);
            }
        });
        emojiKeyBod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.dismiss();
                emojiIcon.setVisibility(View.VISIBLE);
                emojiKeyBod.setVisibility(View.INVISIBLE);

            }
        });

        page_id = getIntent().getStringExtra("page_id");
        common_id = getIntent().getStringExtra("common_id");

         /*

         common_name = getIntent().getStringExtra("common_name");
         page_name = getIntent().getStringExtra("name");
         page_description = getIntent().getStringExtra("des");

          */

        // Toast.makeText(context, page_id+common_id+common_name+page_name+page_description,Toast.LENGTH_SHORT).show();

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postTextHolder = Objects.requireNonNull(postTxt.getText()).toString().trim();


                if (KEY_EMPTY.equals(postTextHolder) || postTextHolder.length() < 2)
                {
                    postTxt.setError("Please write something");
                    postTxt.requestFocus();

                }else{
                    PostNow();
                }


            }
        });



    }

    private void PostNow() {

        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
        mPlayer.start();

        String dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String timeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String timeDateString  = dateString +" "+timeString;


        try {
            byte[] data = postTxt.getText().toString().getBytes("UTF-8");
            toServerUnicodeEncoded = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String user_id = sp.getString(KEY_PREFE_ID, null);

        String page_id = getIntent().getStringExtra("page_id");
        String common_id = getIntent().getStringExtra("common_id");

        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/upload.php")
                .addBodyParameter("user_id", user_id)
                .addBodyParameter("page_id", page_id)
                .addBodyParameter("common_id", common_id)
                .addBodyParameter("image_about", toServerUnicodeEncoded)
                .addBodyParameter("time_date", timeDateString)

                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1"))
                        {
                            Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();

                    }
                });

    }



    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POST_RECYCLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    Toast.makeText(context , response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                Post post = new Post();

                                post.setPost_id(jsonObject2.getString("id"));
                                post.setProfile_url(jsonObject2.getString("profile_url"));
                                post.setUser_name(jsonObject2.getString("user_name"));
                                post.setPost_text(jsonObject2.getString("post_text"));
                                post.setPiture_url(jsonObject2.getString("url"));
                                post.setLike_status(jsonObject2.getString("status"));
                                post.setLikes(jsonObject2.getString("likes"));
                                post.setComments(jsonObject2.getString("comments"));
                                post.setDate_added(jsonObject2.getString("date_added"));
                                post.setPost_user_id(jsonObject2.getString("post_user_id"));

                                postList.add(post);
                                //       Toast.makeText(SearchActivity.this, jsonObject2.getString("id"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        // adapter.clear();


                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //     getData();


                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String user_id_holder_in = sp.getString(KEY_PREFE_ID, String.valueOf(1));

                String page_id = getIntent().getStringExtra("page_id");

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
              // params.put("user_id", "2523641");
              //  params.put("page_id", "69");

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        Context context;
        private List<Post> list;
        View v;

        public PostAdapter(Context context, List<Post> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            v = LayoutInflater.from(context).inflate(R.layout.row_model_fragment_home, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Post post = list.get(position);



            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            user_id_holder = sp.getString(KEY_PREFE_ID, null);
            user_profile_url = sp.getString(KEY_PREFE_PROFILE_URL, null);
            user_name_logged = sp.getString(KEY_PREFE_NAME, null);

            // changes to hide



            session = new SessionHandler(context);


            holder.txtPostId.setText(post.getPost_id());


/*
            try {
                byte[] data = Base64.decode(post.getUser_name(), Base64.DEFAULT);
                newEmojidecodedPostUserName = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

 */
            holder.txtPostUserName.setText(post.getUser_name());


            holder.txtStatus.setText(post.getLike_status());
            holder.txtTotalLikes.setText(post.getLikes());
            holder.txtTotalComments.setText(post.getComments());

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date past = null;
            try {
                past = format.parse(post.getDate_added());

                Date now = new Date();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
                    // System.out.println(seconds+" seconds ago");
                    //   Toast.makeText(PrimaryHomeHomeActivity.this, seconds+"seconds ago",Toast.LENGTH_SHORT).show();
                    holder.txtTimeDate.setText(seconds + " seconds ago");
                } else if (minutes < 60) {
                    // System.out.println(minutes+" minutes ago");
                    //  Toast.makeText(PrimaryHomeHomeActivity.this, minutes+"minutes ago",Toast.LENGTH_SHORT).show();
                    holder.txtTimeDate.setText(minutes + " minutes ago");
                } else if (hours < 24) {
                    //System.out.println(hours+" hours ago");
                    //   Toast.makeText(PrimaryHomeHomeActivity.this, hours+"hours ago",Toast.LENGTH_SHORT).show();
                    holder.txtTimeDate.setText(hours + " hours ago");
                } else {
                    //System.out.println(days+" days ago");
                    //  Toast.makeText(PrimaryHomeHomeActivity.this, days+"days ago",Toast.LENGTH_SHORT).show();
                    holder.txtTimeDate.setText(days + " days ago");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }



            if (post.getLike_status().equals("1")) {
                holder.ImageLikeBtn.setChecked(true);
            } else {
                holder.ImageLikeBtn.setChecked(false);
            }



            Glide
                    .with(context)
                    .load(post.getProfile_url())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(holder.user_small_thumbnail);


            if (KEY_EMPTY.equals(post.getPiture_url()) || KEY_NULL.equals(post.getPiture_url() ))
            {
                holder.postImage.setVisibility(View.INVISIBLE);
                //    Toast.makeText(context, "EMPTY", Toast.LENGTH_SHORT).show();
            }else {

                Glide
                        .with(context)
                        .load(post.getPiture_url())
                        .placeholder(R.drawable.common_placeholder_image_sharp)
                        .into(holder.postImage);

                //   Toast.makeText(context, "NOT EMPTY", Toast.LENGTH_SHORT).show();
            }



            try {
                byte[] data = Base64.decode(post.getPost_text(), Base64.DEFAULT);
                newEmojidecodedPostText = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            holder.txtPostText.setText(newEmojidecodedPostText);


            holder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // changes to hide
                    session = new SessionHandler(context);

                    if (session.isLoggedIn()) {

                        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                        mPlayer.start();


                        holder.ImageLikeBtn.setChecked(true);

                        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/like_default_insert.php")
                                .addBodyParameter("u", user_id_holder)
                                .addBodyParameter("user_profile_url", user_profile_url)
                                .addBodyParameter("user_name_logged", user_name_logged)
                                .addBodyParameter("post_id", post.getPost_id())
                                .addBodyParameter("post_user_id", post.getPost_user_id())
                                .setTag("test")
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                        ////////////////////////////////////////////////////////////////
                                        //    GET TOTAL LIKES

                                        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/likes_total_get.php")
                                                .addBodyParameter("user_id", user_id_holder)
                                                .addBodyParameter("post_id", post.getPost_id())
                                                .setTag("test")
                                                .setPriority(Priority.HIGH)
                                                .build()
                                                .getAsString(new StringRequestListener() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        //   Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                                        holder.txtTotalLikes.setText("");
                                                        holder.txtTotalLikes.setText(response);
                                                    }

                                                    @Override
                                                    public void onError(ANError anError) {
                                                    }
                                                });

                                        //   END TOTAL LIKES
                                        ///////////////////////////////////////////////////////////////
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                    }
                                });





                    } else {
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                    }
                }
            });

            holder.imageViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // changes to hide

                    session = new SessionHandler(context);


                    if (session.isLoggedIn()) {
                         /*
                        String post_user_name_string = post.getPost_user_name();
                        String post_text_string = post.getPost_text();
                        String post_image_url_string = post.getPost_image_url();
                        String post_id_string = post.getPost_id();
                        String post_like_status_string = post.getLikes();

                        //Toast.makeText(c, user.getName(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, ImageViewActivity.class);

                        i.putExtra("post_user_name", post_user_name_string);
                        i.putExtra("post_user_profile_image_url", post.getProfileurl());
                        i.putExtra("post_text", post_text_string);
                        i.putExtra("post_image_url", post_image_url_string);
                        i.putExtra("post_id", post_id_string);
                        i.putExtra("post_like_status", post_like_status_string);
                        i.putExtra("post_user_id", post.getPost_user_id());
                        i.putExtra("user_cover", post.getUser_cover());
                        startActivity(i);

                     */
                    } else {
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                    }


                }
            });

            holder.user_small_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Not implemented to ProfileViewActivity

                    if (session.isLoggedIn()) {

                        /*
                        String post_user_id_string = post.getPost_user_id();


                        Intent i = new Intent(context, AccountActivity.class);
                        i.putExtra("user_id", post_user_id_string);
                        startActivity(i);
                         */


                    } else {
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                    }


                }
            });

            holder.txtPostUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (session.isLoggedIn()) {
                        /*
                        String post_user_id_string = post.getPost_user_id();


                        Intent i = new Intent(context, ProfileViewActivity.class);
                        i.putExtra("user_id", post_user_id_string);

                        startActivity(i);

                         */
                    } else {
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                    }
                }
            });


            holder.ImageLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                    user_id_holder = sp.getString(KEY_PREFE_ID, null);
                    user_profile_url = sp.getString(KEY_PREFE_PROFILE_URL, null);
                    user_name_logged = sp.getString(KEY_PREFE_NAME, null);

                    if (session.isLoggedIn()) {
                        boolean checked = ((CheckBox) v).isChecked();
                        if (checked) {

                            MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
                            mPlayer.start();
                            //  Toast.makeText(context,"Liked",Toast.LENGTH_SHORT).show();


                            AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/like_default_insert.php")
                                    .addBodyParameter("u", user_id_holder)
                                    .addBodyParameter("user_profile_url", user_profile_url)
                                    .addBodyParameter("user_name_logged", user_name_logged)
                                    .addBodyParameter("post_id", post.getPost_id())
                                    .addBodyParameter("post_user_id", post.getPost_user_id())
                                    .setTag("test")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                            ////////////////////////////////////////////////////////////////
                                            //    GET TOTAL LIKES

                                            AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/likes_total_get.php")
                                                    .addBodyParameter("user_id", user_id_holder)
                                                    .addBodyParameter("post_id", post.getPost_id())
                                                    .setTag("test")
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .getAsString(new StringRequestListener() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            //   Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                                            holder.txtTotalLikes.setText("");
                                                            holder.txtTotalLikes.setText(response);
                                                        }

                                                        @Override
                                                        public void onError(ANError anError) {
                                                        }
                                                    });

                                            //   END TOTAL LIKES
                                            ///////////////////////////////////////////////////////////////
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                        }
                                    });

                        } else {
                            //  Toast.makeText(context,"Un Liked",Toast.LENGTH_SHORT).show();


                            AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/like_insert_false_update.php")
                                    .addBodyParameter("user_id", user_id_holder)
                                    .addBodyParameter("post_id", post.getPost_id())
                                    .setTag("test")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            //  Toast.makeText(context,response,Toast.LENGTH_SHORT).show();

                                            ////////////////////////////////////////////////////////////////
                                            //    GET TOTAL LIKES

                                            AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/likes_total_get.php")
                                                    .addBodyParameter("user_id", user_id_holder)
                                                    .addBodyParameter("post_id", post.getPost_id())
                                                    .setTag("test")
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .getAsString(new StringRequestListener() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            //  Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                                                            holder.txtTotalLikes.setText("");
                                                            holder.txtTotalLikes.setText(response);
                                                        }

                                                        @Override
                                                        public void onError(ANError anError) {
                                                        }
                                                    });

                                            //   END TOTAL LIKES
                                            ///////////////////////////////////////////////////////////////
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                        }
                                    });


                        }
                    } else {
                        holder.ImageLikeBtn.setChecked(false);
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                        holder.ImageLikeBtn.setChecked(false);
                    }
                }


            });



        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txtPostUserName, txtPostId, txtPostTexttxtUserName, txtPostImageUrl, txtPostUserId, txtProfileUrl, txtStatus, txtTotalLikes, txtTotalComments;
            EmojiTextView txtPostText;
            ImageView postImage, user_small_thumbnail, ic_share;
            CheckBox ImageDownloadBtn, ImageLikeBtn;
            CheckBox ImageCommentBtn;
            private TextView txtTimeDate;
            RelativeLayout imageViewButton;

            public ViewHolder(final View itemView) {
                super(itemView);
                postImage = itemView.findViewById(R.id.id_thumbnail);
                txtPostUserName = itemView.findViewById(R.id.user_name);
                txtPostId = itemView.findViewById(R.id.post_id);
                txtPostImageUrl = itemView.findViewById(R.id.post_image_url);
                txtPostUserId = itemView.findViewById(R.id.post_user_id);
                txtProfileUrl = itemView.findViewById(R.id.post_user_profile);
                txtTotalLikes = itemView.findViewById(R.id.load_likes_main);
                txtStatus = itemView.findViewById(R.id.id_status);
                txtTotalComments = itemView.findViewById(R.id.load_total_comments_main);
                user_small_thumbnail = itemView.findViewById(R.id.id_user_rounded_thumbnail);

                ImageCommentBtn = itemView.findViewById(R.id.id_ic_cmd);
                ImageLikeBtn = itemView.findViewById(R.id.id_like_checkbox);
                txtTimeDate = itemView.findViewById(R.id.time_date);


                txtPostText = itemView.findViewById(R.id.post_text);

                // Below code added july 2021
                imageViewButton = itemView.findViewById(R.id.imageview_btn);


            }

        }

    }




    public class Post {

        public String post_id , profile_url , user_name , post_text , piture_url;
        public String like_status , likes , comments , date_added , post_user_id;


        public Post() {
            this.post_id = post_id;
            this.profile_url = profile_url;
            this.user_name = user_name;
            this.post_text = post_text;
            this.piture_url = piture_url;
            this.like_status = like_status;
            this.likes = likes;
            this.comments = comments;
            this.date_added = date_added;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public String getProfile_url() {
            return profile_url;
        }

        public void setProfile_url(String profile_url) {
            this.profile_url = profile_url;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getPost_text() {
            return post_text;
        }

        public void setPost_text(String post_text) {
            this.post_text = post_text;
        }

        public String getPiture_url() {
            return piture_url;
        }

        public void setPiture_url(String piture_url) {
            this.piture_url = piture_url;
        }

        public String getLike_status() {
            return like_status;
        }

        public void setLike_status(String like_status) {
            this.like_status = like_status;
        }

        public String getLikes() {
            return likes;
        }

        public void setLikes(String likes) {
            this.likes = likes;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getDate_added() {
            return date_added;
        }

        public void setDate_added(String date_added) {
            this.date_added = date_added;
        }

        public String getPost_user_id() {
            return post_user_id;
        }

        public void setPost_user_id(String post_user_id) {
            this.post_user_id = post_user_id;
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here


        getData();

        //  Toast.makeText(context, "Refreshed" ,Toast.LENGTH_SHORT).show();
    }
}