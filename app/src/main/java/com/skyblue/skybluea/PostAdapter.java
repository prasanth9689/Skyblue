package com.skyblue.skybluea;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.skyblue.skybluea.SearchActivity.progress_bar_type;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.skyblue.skybluea.account.AccountActivity;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SessionHandler session;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    // After API 23 the permission request for accessing external storage is changed
    // Before API 23 permission request is asked by the user during installation of app
    // After API 23 permission request is asked at runtime
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private ProgressDialog pDialog;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_ITEM = 0;

    private List<Post> mSnapFeedList;
    private Activity mContext;

    private Dialog loadingProgressDialog;
    final Handler handler = new Handler();

    ImageView snapFeedImageView;
    User user;

    public PostAdapter(Activity context, List<Post>mSnapFeedList) {
        this.mContext = context;
        this.mSnapFeedList = mSnapFeedList;
        //  initLoadingProgressDialog();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_model_fragment_home, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.model_dialog_loading_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof PostViewHolder) {

            PostViewHolder postHolder = (PostViewHolder) holder;

            final Post post = mSnapFeedList.get(position);

            session = new SessionHandler(mContext.getApplicationContext());
            user = session.getUserDetails();

            postHolder.userNameText.setText(post.getPost_user_name());
            postHolder.txtTotalLikes.setText(post.getLikes());
            postHolder.txtTotalComments.setText(post.getComments());
            postHolder.txtStatus.setText(post.getStatus());

            String newStringEmojidecooded = "";

            try {
                byte[] data = Base64.decode(post.getPost_text(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            postHolder.descriptionText.setText(newStringEmojidecooded);

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
                    postHolder.txtTimeDate.setText(seconds + " seconds ago");
                } else if (minutes < 60) {
                    postHolder.txtTimeDate.setText(minutes + " minutes ago");
                } else if (hours < 24) {
                    postHolder.txtTimeDate.setText(hours + " hours ago");
                } else {
                    postHolder.txtTimeDate.setText(days + " days ago");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            Glide
//                    .with(mContext)
//                    .load(post.getPost_image_url())
//                    .placeholder(R.color.image_placeholder_bg)
//                    .into(postHolder.imgPostImage);

            Picasso.get().load(post.getPost_image_url()).placeholder(R.color.image_placeholder_bg).into(postHolder.imgPostImage);

            Glide
                    .with(mContext)
                    .load(post.getProfileurl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(postHolder.imgUserImage);


            postHolder.checkBoxImageLikeBtn.setChecked(post.getStatus().equals("1"));

            if (post.getMedia_type().equals("video")) {

                postHolder.imgPlayIcon.setVisibility(View.VISIBLE);
            } else {
                postHolder.imgPlayIcon.setVisibility(View.INVISIBLE);
            }

            postHolder.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Skyblue");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }

                    File myDirSec = new File(root + "/Skyblue/.share");
                    if (!myDirSec.exists()) {
                        //  Toast.makeText(context, ".share created",Toast.LENGTH_SHORT).show();
                        myDirSec.mkdirs();
                    }

                    new DownloadFileFromURL().execute(post.getPost_image_url());
                }
            });

//            postHolder.downloadVideoBtn.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void onClick(View view) {
//
//                    String root = Environment.getExternalStorageDirectory().toString();
//                    File myDir = new File(root + "/Skyblue");
//                    if (!myDir.exists()) {
//                        myDir.mkdirs();
//                        Utils.showMessage(mContext, "created");
//                    }
//
//
//
//                     Requesting Permission to access External Storage
//                    ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            EXTERNAL_STORAGE_PERMISSION_CODE);
//
//                    if (SDK_INT >= Build.VERSION_CODES.M) {
//                        if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
//                                mContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                            //                showMessageInSnackbar("Permission already granded");
//                            downloadVideoDefaultNotificationBar(post.getPost_image_url());
//                        }
//                    }
//                }
//            });
            postHolder.checkBoxDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {


                    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

                    Uri uri = Uri.parse(post.getPost_image_url());

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    String fileName = post.getPost_image_url().substring(post.getPost_image_url().lastIndexOf("/") + 1);
                    request.setAllowedNetworkTypes(
                                    DownloadManager.Request.NETWORK_WIFI
                                            | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false).setTitle("Skyblue")
                            .setDescription("Downloading picture.")
                            .setDestinationInExternalPublicDir("/Skyblue", fileName);

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //  Long reference = downloadManager .enqueue(request);
                }
            });
            postHolder.imgPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getMedia_type().equals("video")) {
                        Intent intent = new Intent(mContext, VideoViewActivity.class);
                        intent.putExtra("video_url", post.getVideo_url());
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra("image_url", post.getPost_image_url());
                        intent.putExtra("post_user_name", post.getPost_user_name());
                        intent.putExtra("post_user_profile_image_url", post.getProfileurl());
                        mContext.startActivity(intent);
                    }
                }
            });
            postHolder.imgUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Not implemented to ProfileViewActivity
                    if (session.isLoggedIn()) {

                        String post_user_id_string = post.getPost_user_id();

                        if (user.user_id.equals(post_user_id_string)) {
                            Intent i = new Intent(mContext, AccountActivity.class);
                            i.putExtra("user_id", post_user_id_string);
                            mContext.startActivity(i);
                        } else {
                          //  if (!"".equals(post.getProfileurl()) && !"null".equals(post.getProfileurl())) {
                                Intent i = new Intent(mContext, ProfileViewActivity.class);
                                i.putExtra("user_id", post_user_id_string);
                                i.putExtra("user_profile", post.getProfileurl());
                                i.putExtra("user_name", post.getPost_user_name());
                                mContext.startActivity(i);
                          //  }
                        }
                    } else {
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                    }
                }
            });
            postHolder.userNameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (session.isLoggedIn()) {
                        String post_user_id_string = post.getPost_user_id();
                    } else {
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                    }
                }
            });
            postHolder.checkBoxImageLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (session.isLoggedIn()) {
                        boolean checked = ((CheckBox) v).isChecked();
                        if (checked) {

                            int getTotalLikes = Integer.parseInt(post.getLikes());
                            int resultLikes = getTotalLikes +1;
                            postHolder.txtTotalLikes.setText(String.valueOf(resultLikes));

                            AndroidNetworking.post(AppConstants.POST_LIKED)
                                    .addBodyParameter("user_id", user.getUser_id())
                                    .addBodyParameter("user_profile", user.getUser_profile())
                                    .addBodyParameter("user_name", user.getName())
                                    .addBodyParameter("post_id", post.getPost_id())
                                    .addBodyParameter("post_user_id", post.getPost_user_id())
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {

                                            Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                        }
                                    });

                        } else {

                            String getTotalLikesTextView = postHolder.txtTotalLikes.getText().toString();
                            int getTotalLikes = Integer.parseInt(getTotalLikesTextView);
                            int one = 1;
                            int resultLike = getTotalLikes - one;


                            postHolder.txtTotalLikes.setText(String.valueOf(resultLike));

                            AndroidNetworking.post(AppConstants.POST_UNLIKED)
                                    .addBodyParameter("user_id", user.getUser_id())
                                    .addBodyParameter("post_id", post.getPost_id())
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            //  Toast.makeText(context,response,Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                        }
                                    });


                        }
                    } else {
                        postHolder.checkBoxImageLikeBtn.setChecked(false);
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                        postHolder.checkBoxImageLikeBtn.setChecked(false);
                    }
                }


            });
            postHolder.likeRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    postHolder.checkBoxImageLikeBtn.performClick();
                }
            });
            postHolder.ImageCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (session.isLoggedIn()) {

                        String post_image_url_string = post.getPost_image_url();
                        String post_id_string = post.getPost_id();

                        Intent intent = new Intent(mContext, ImageCommentActivity.class);
                        intent.putExtra("post_image_url", post_image_url_string);
                        intent.putExtra("post_id", post_id_string);
                        intent.putExtra("post_user_id", post.getPost_user_id());
                        mContext.startActivity(intent);


                    } else {
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                    }
                }
            });
            postHolder.commentRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (session.isLoggedIn()) {


                        String post_image_url_string = post.getPost_image_url();
                        String post_id_string = post.getPost_id();

                        Intent intent = new Intent(mContext, ImageCommentActivity.class);
                        intent.putExtra("post_image_url", post_image_url_string);
                        intent.putExtra("post_id", post_id_string);
                        intent.putExtra("post_user_id", post.getPost_user_id());
                        mContext.startActivity(intent);


                    } else {
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                    }
                }
            });

            postHolder.checkBoxDownloadBtn.setOnClickListener(new View.OnClickListener() {
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


                        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

                        Uri uri = Uri.parse(post.getPost_image_url());

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String fileName = post.getPost_image_url().substring(post.getPost_image_url().lastIndexOf("/") + 1);
                        request.setAllowedNetworkTypes(
                                        DownloadManager.Request.NETWORK_WIFI
                                                | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false).setTitle("Skyblue")
                                .setDescription("Downloading picture.")
                                .setDestinationInExternalPublicDir("/Skyblue", fileName);

                        Toast.makeText(mContext, mContext.getResources().getString(R.string.download_started), Toast.LENGTH_SHORT).show();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        downloadManager.enqueue(request);


                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        //  Long reference = downloadManager .enqueue(request);


                    } else {
                        Intent ii = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(ii);
                    }
                }
            });


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void downloadVideoDefaultNotificationBar(String mediaUrl) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(mediaUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
        request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Skyblue")
                .setDescription("Downloading picture.")
                .setDestinationInExternalPublicDir("/Skyblue", fileName);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //  Long reference = downloadManager .enqueue(request);
    }

    @Override
    public int getItemViewType(int position) {
        return mSnapFeedList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarloading);
        }
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView txtStatus, txtTotalLikes, txtTotalComments;
        public EmojiTextView descriptionText;
        public ImageView imgPostImage, imgUserImage, imgShare, imgPlayIcon;
        public CheckBox checkBoxDownloadBtn, checkBoxImageLikeBtn;
        public CheckBox ImageCommentBtn;
        public TextView txtTimeDate , userNameText;
        public RelativeLayout relExtraBtn , commentRelativeLayout , likeRelativeLayout;
        public RelativeLayout downloadVideoBtn;

        public PostViewHolder(View itemView) {
            super(itemView);
            imgUserImage = itemView.findViewById(R.id.id_user_rounded_thumbnail);
            imgPostImage = itemView.findViewById(R.id.id_thumbnail);
            descriptionText = itemView.findViewById(R.id.id_image_text);
            userNameText = itemView.findViewById(R.id.id_user_name);

            txtTotalLikes = itemView.findViewById(R.id.id_load_likes_main);
            txtStatus = itemView.findViewById(R.id.id_status);
            txtTotalComments = itemView.findViewById(R.id.id_load_total_comments_main);

            checkBoxDownloadBtn = itemView.findViewById(R.id.id_ic_download);
            ImageCommentBtn = itemView.findViewById(R.id.id_ic_cmd);
            checkBoxImageLikeBtn = itemView.findViewById(R.id.id_like_checkbox);
            txtTimeDate = itemView.findViewById(R.id.time_date);
            imgShare = itemView.findViewById(R.id.ic_share);

            // Below code added july 2021
            relExtraBtn = itemView.findViewById(R.id.id_rel_extra_btn);

            //Below code added oct 2021
            imgPlayIcon = itemView.findViewById(R.id.id_play_icon);
            commentRelativeLayout = itemView.findViewById(R.id.comment_round_box);
            likeRelativeLayout = itemView.findViewById(R.id.like_round_box);

//            downloadVideoBtn = itemView.findViewById(R.id.download2_round_box);
        }
    }

    @Override
    public int getItemCount() {
        return mSnapFeedList == null ? 0 : mSnapFeedList.size();
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

            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage(mContext.getString(R.string.please_wait_dot_dot));
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.download_from_skyblue_app));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share image using"));
        }

        protected Dialog onCreateDialog(int id) {
            switch (id) {
                case progress_bar_type:
                    pDialog = new ProgressDialog(mContext);
                    pDialog.setMessage(mContext.getString(R.string.please_wait_dot_dot));
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
