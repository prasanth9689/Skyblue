package com.skyblue.skybluea;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FragmentHome extends Fragment {

    // changes to hide
    private SessionHandler session;

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 100;

    // String CHECK_APP_VERSION = "http://103.50.162.147/~skyblqxl/web/check_app_versions.php";
    String CHECK_APP_VERSION = "https://www.skyblue-watch.xyz/web/handle/check_app_versions.php";
    private String TempStatus = "0";
    //private static final String APP_VERSION = "1907104";
    //private static final String APP_VERSION = "2004091";
    //private static final String APP_VERSION = "2004201";
    //private static final String APP_VERSION = "2004211";
    //private static final String APP_VERSION = "2004262";
    //private static final String APP_VERSION = "2004281";
    //private static final String APP_VERSION = "2005011";
    //private static final String APP_VERSION = "2005012";
    // private static final String APP_VERSION = "2005021";
    //private static final String APP_VERSION = "2005041";
    // private static final String APP_VERSION = "200621";
    //private static final String APP_VERSION = "200904";
    //private static final String APP_VERSION = "2009041";
    private static final String APP_VERSION = "2103161";


    // private static final String GET_POST_RECYCLER = "http://103.50.162.147/~skyblqxl/web/upload_post_image/images/stocl/";
    private static final String GET_POST_RECYCLER = "https://www.skyblue-watch.xyz/web/upload_post_image/images/stocl/";

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final String KEY_PREFE_PROFILE_URL = "profile_url";

    private RecyclerView pList;
    private LinearLayoutManager linearLayoutManager;
    private List<Post> postList;
    private RecyclerView.Adapter adapter;

    String user_profile_url;
    String user_id_holder;
    String user_name_logged;
    // Progress Dialog
    private ProgressDialog pDialog;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    String newStringEmojidecooded;

    public static final String TITLE = "HOME";

    public static FragmentHome newInstance() {

        return new FragmentHome();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragment_home, container, false);
        EmojiManager.install(new IosEmojiProvider());

        CheckStoragePermission();



        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        user_id_holder = sp.getString(KEY_PREFE_ID, null);
        user_profile_url = sp.getString(KEY_PREFE_PROFILE_URL, null);
        user_name_logged = sp.getString(KEY_PREFE_NAME, null);


        pList = view.findViewById(R.id.recycler_view);

        pList.setItemViewCacheSize(20);
        postList = new ArrayList<>();

        adapter = new PostAdapter(getContext().getApplicationContext(), postList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //       dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
        pList.setHasFixedSize(true);
        pList.setLayoutManager(linearLayoutManager);
        //     mList.addItemDecoration(dividerItemDecoration);
        pList.setAdapter(adapter);
        //   linearLayoutManager.setReverseLayout(true);

        postList.clear();
        getData();

        CheckAppVersion();


        return view;
    }


    private void CheckAppVersion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_APP_VERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(HomeHomeFragmentActivity.this.getActivity(), response,Toast.LENGTH_SHORT).show();


                        if (TempStatus.equals(response)) {
                            //  Toast.makeText(HomeHomeFragmentActivity.this.getActivity(), "Latest version",Toast.LENGTH_SHORT).show();
                        } else {
                            //    Toast.makeText(HomeHomeFragmentActivity.this.getActivity(), "Deferent version",Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(getContext(), NewVersionUpdateAvailableActivity.class);

                            startActivity(i);


                        }
                        // Showing response message coming from server.
                        //  Toast.makeText(RegisterMobileActivity.this, ServerResponse, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Try again", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("app_version", APP_VERSION);


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POST_RECYCLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    Toast.makeText(getContext() , response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                Post post = new Post();

                                post.setPost_id(jsonObject2.getString("id"));
                                post.setPost_text(jsonObject2.getString("image_about"));
                                post.setPost_user_name(jsonObject2.getString("user_name"));
                                post.setPost_image_url(jsonObject2.getString("url"));
                                post.setPost_user_id(jsonObject2.getString("user_id"));
                                post.setStatus(jsonObject2.getString("status"));
                                post.setLikes(jsonObject2.getString("likes"));
                                post.setComments(jsonObject2.getString("comments"));
                                post.setProfileurl(jsonObject2.getString("profile_url"));
                                post.setTime_date(jsonObject2.getString("time_date"));
                                post.setUser_cover(jsonObject2.getString("user_cover"));

                                postList.add(post);

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

                getData();

                Log.e("Volley", error.toString());
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String user_id_holder_in = sp.getString(KEY_PREFE_ID, String.valueOf(1));

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", user_id_holder_in);


                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

            SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            user_id_holder = sp.getString(KEY_PREFE_ID, null);
            user_profile_url = sp.getString(KEY_PREFE_PROFILE_URL, null);
            user_name_logged = sp.getString(KEY_PREFE_NAME, null);

            // changes to hide
            session = new SessionHandler(getContext());


            holder.txtPostId.setText(post.getPost_id());

            holder.txtPostUserName.setText(post.getPost_user_name());
            holder.txtTotalLikes.setText(post.getLikes());
            holder.txtTotalComments.setText(post.getComments());
            holder.txtStatus.setText(post.getStatus());
            holder.txtPostUserId.setText(post.getPost_user_id());

            try {
                byte[] data = Base64.decode(post.getPost_text(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            holder.txtPostText.setText(newStringEmojidecooded);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date past = null;
            try {
                past = format.parse(post.getTime_date());

                Date now = new Date();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
                    // System.out.println(seconds+" seconds ago");

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


            Glide
                    .with(getContext())
                    .load(post.getPost_image_url())
                    .placeholder(R.drawable.splash_gradient)
                    .into(holder.postImage);

            Glide
                    .with(getContext())
                    .load(post.getProfileurl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(holder.user_small_thumbnail);


            if (post.getStatus().equals("1")) {
                holder.ImageLikeBtn.setChecked(true);
            } else {
                holder.ImageLikeBtn.setChecked(false);
            }


            holder.ic_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Skyblue");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }

                    File myDirSec = new File(root + "/Skyblue/.share");
                    if (!myDirSec.exists()) {
                        //  Toast.makeText(getContext(), ".share created",Toast.LENGTH_SHORT).show();
                        myDirSec.mkdirs();
                    }

                    new DownloadFileFromURL().execute(post.getPost_image_url());
                }
            });

            holder.ImageDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {


                    //Toast.makeText(PrimaryHomeHomeActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();

                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "Skyblue");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    if (success) {
                        // Do something else on success
                        //     Toast.makeText(MainActivity.this, "Directory Created", Toast.LENGTH_SHORT).show();
                    }
                    DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                    Uri uri = Uri.parse(post.getPost_image_url());

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    String fileName = post.getPost_image_url().substring(post.getPost_image_url().lastIndexOf("/") + 1);
                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false).setTitle("Skyblue")
                            .setDescription("Downloading picture.")
                            .setDestinationInExternalPublicDir("/Skyblue", fileName);

                    Toast.makeText(context, getResources().getString(R.string.download_started), Toast.LENGTH_SHORT).show();

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadManager.enqueue(request);


                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //  Long reference = downloadManager .enqueue(request);

                }
            });


            holder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // changes to hide
                    session = new SessionHandler(getContext());

                    if (session.isLoggedIn()) {
                        String post_user_name_string = post.getPost_user_name();
                        String post_text_string = post.getPost_text();
                        String post_image_url_string = post.getPost_image_url();
                        String post_id_string = post.getPost_id();
                        String post_like_status_string = post.getLikes();

                        //Toast.makeText(c, user.getName(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getContext(), ImageViewActivity.class);

                        i.putExtra("post_user_name", post_user_name_string);
                        i.putExtra("post_user_profile_image_url", post.getProfileurl());
                        i.putExtra("post_text", post_text_string);
                        i.putExtra("post_image_url", post_image_url_string);
                        i.putExtra("post_id", post_id_string);
                        i.putExtra("post_like_status", post_like_status_string);
                        i.putExtra("post_user_id", post.getPost_user_id());
                        i.putExtra("user_cover", post.getUser_cover());
                        startActivity(i);
                    } else {
                        Intent ii = new Intent(getContext(), LoginActivity.class);
                        startActivity(ii);
                    }


                }
            });

            holder.user_small_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Not implemented to ProfileViewActivity

                    if (session.isLoggedIn()) {
                        String post_user_id_string = post.getPost_user_id();


                  //      Intent i = new Intent(getContext(), ProfileViewActivity.class);
                  //      i.putExtra("user_id", post_user_id_string);

                   //     startActivity(i);
                    } else {
                        Intent ii = new Intent(getContext(), LoginActivity.class);
                        startActivity(ii);
                    }


                }
            });

            holder.txtPostUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (session.isLoggedIn()) {
                        String post_user_id_string = post.getPost_user_id();


                //        Intent i = new Intent(getContext(), ProfileViewActivity.class);
                 //       i.putExtra("user_id", post_user_id_string);

                  //      startActivity(i);
                    } else {
                        Intent ii = new Intent(getContext(), LoginActivity.class);
                        startActivity(ii);
                    }
                }
            });


            holder.ImageLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                    user_id_holder = sp.getString(KEY_PREFE_ID, null);
                    user_profile_url = sp.getString(KEY_PREFE_PROFILE_URL, null);
                    user_name_logged = sp.getString(KEY_PREFE_NAME, null);

                    if (session.isLoggedIn()) {
                        boolean checked = ((CheckBox) v).isChecked();
                        if (checked) {
                            //  Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();


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
                                            // Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
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
                                                            //   Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
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
                            //  Toast.makeText(getContext(),"Un Liked",Toast.LENGTH_SHORT).show();


                            AndroidNetworking.post("https://www.skyblue-watch.xyz/web/upload_post_image/likes/like_insert_false_update.php")
                                    .addBodyParameter("user_id", user_id_holder)
                                    .addBodyParameter("post_id", post.getPost_id())
                                    .setTag("test")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            //  Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();

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
                                                            //  Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
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
                        Intent ii = new Intent(getContext(), LoginActivity.class);
                        startActivity(ii);
                        holder.ImageLikeBtn.setChecked(false);
                    }
                }


            });
            holder.ImageCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (session.isLoggedIn()) {
                        String post_image_url_string = post.getPost_image_url();
                        String post_id_string = post.getPost_id();
/*
                        Intent intent = new Intent(getContext(), ImageCommentActivity.class);
                        intent.putExtra("post_image_url", post_image_url_string);
                        intent.putExtra("post_id", post_id_string);
                        intent.putExtra("post_user_id", post.getPost_user_id());
                        startActivity(intent);

 */
                    } else {
                        Intent ii = new Intent(context, LoginActivity.class);
                        startActivity(ii);
                    }


                }
            });
            holder.ImageDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {

                    if (session.isLoggedIn()) {


                        //Toast.makeText(PrimaryHomeHomeActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();

                        File folder = new File(Environment.getExternalStorageDirectory() +
                                File.separator + "Skyblue");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            // Do something else on success
                            //     Toast.makeText(MainActivity.this, "Directory Created", Toast.LENGTH_SHORT).show();
                        } else {
                            // Do something else on failure
                            //   Toast.makeText(MainActivity.this, "Failed - Error", Toast.LENGTH_SHORT).show();
                        }


                        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                        Uri uri = Uri.parse(post.getPost_image_url());

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String fileName = post.getPost_image_url().substring(post.getPost_image_url().lastIndexOf("/") + 1);
                        request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI
                                        | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false).setTitle("Skyblue")
                                .setDescription("Downloading picture.")
                                .setDestinationInExternalPublicDir("/Skyblue", fileName);

                        Toast.makeText(context, getResources().getString(R.string.download_started), Toast.LENGTH_SHORT).show();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        downloadManager.enqueue(request);


                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        //  Long reference = downloadManager .enqueue(request);


                    } else {
                        Intent ii = new Intent(getContext(), LoginActivity.class);
                        startActivity(ii);
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
            CheckBox ImageDownloadBtn, ImageCommentBtn, ImageLikeBtn;
            private TextView txtTimeDate;

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
                ImageDownloadBtn = itemView.findViewById(R.id.id_ic_download);
                ImageCommentBtn = itemView.findViewById(R.id.id_ic_cmd);
                ImageLikeBtn = itemView.findViewById(R.id.id_like_checkbox);
                txtTimeDate = itemView.findViewById(R.id.time_date);
                ic_share = itemView.findViewById(R.id.ic_share);

                txtPostText = itemView.findViewById(R.id.post_text);


            }

        }

        /*
          l̥* Background Async Task to download file
         * */
        class DownloadFileFromURL extends AsyncTask<String, String, String> {

            /**
             * Before starting background thread
             * Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // showDialog(progress_bar_type);

                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            /**
             * Downloading file in background thread
             */
            @Override
            protected String doInBackground(String... f_url) {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream("/sdcard/Skyblue/.share/thumbnail.jpg");

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                return null;
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                pDialog.setProgress(Integer.parseInt(progress[0]));
            }

            /**
             * After completing background task
             * Dismiss the progress dialog
             **/
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            protected void onPostExecute(String file_url) {
                // dismiss the dialog after the file was downloaded
                // dismissDialog(progress_bar_type);
                pDialog.dismiss();
                saveImageCanvas();



           /*

                // Displaying downloaded image into image view
                // Reading image path from sdcard
                String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
                // setting downloaded into image view
           //     my_image.setImageDrawable(Drawable.createFromPath(imagePath));
               imgBuddyUserProfilePic.setImageDrawable(Drawable.createFromPath(imagePath));
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                // final File photoFile = new File(getFilesDir(), "foo.jpg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Skyblue App");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share image using"));



            */


            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            private void saveImageCanvas() {
                /*
        File myDir=new File("/sdcard/Skyblue/.share/thumbnail.jpg");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();

 */


                File dir = Environment.getExternalStorageDirectory();
                File file2 = new File(dir, "/Skyblue/.share/thumbnail.jpg");

                Bitmap bitmap = BitmapFactory.decodeFile(file2.getAbsolutePath());


                int textSize = 20;
                String textToDraw = "Skyblue App";

                Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);


                Canvas newCanvas = new Canvas(newBitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.WHITE);
                paint.setTextSize(textSize);


                Rect bounds = new Rect();
                paint.getTextBounds(textToDraw, 0, textToDraw.length(), bounds);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                //  int x = (newCanvas.getWidth() / 4);
                // int y = newBitmap.getHeight() /9;

                newCanvas.drawText(textToDraw, 10f, newBitmap.getHeight() - 20, paint);
                //   testImageView.setImageBitmap(newBitmap);


                try {
                    FileOutputStream out = new FileOutputStream(file2);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                // final File photoFile = new File(getFilesDir(), "foo.jpg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(String.valueOf(file2))));
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Downloaded from Skyblue app");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share image using"));

            }

            protected Dialog onCreateDialog(int id) {
                switch (id) {
                    case progress_bar_type:
                        pDialog = new ProgressDialog(getContext());
                        pDialog.setMessage("Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setMax(100);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialog.setCancelable(true);
                        pDialog.show();
                        return pDialog;
                    default:
                        return null;
                }
            }


        }

    }
    private void CheckStoragePermission() {

        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);
    }

    private void CheckCameraPermission() {


        checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE);
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{permission},
                    requestCode);
        } else {
             /*
            Toast.makeText(MainActivity.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
             */
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getContext(),
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();

                CheckCameraPermission();
            } else {
                Toast.makeText(getContext(),
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

                CheckCameraPermission();
            }

        }
    }

}


