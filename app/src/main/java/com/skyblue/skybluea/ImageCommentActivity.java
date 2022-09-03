package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ImageCommentActivity extends AppCompatActivity {
    private SessionHandler session;

    TextView imageIdTextView , userIdTextView;
    ImageView imageViewPicture , backArrow;
    EditText CommentEditText;
    ImageButton CommentSendButton;
    String GetCommentString , post_id , post_user_id;
    User user;
    ListView myGridView;
    ProgressBar myDataLoaderProgressBar;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comment);

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        initVariable();
        setOnClickListener();

        CommentEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        imageIdTextView.setText(getIntent().getStringExtra("post_id"));

        post_id =imageIdTextView.getText().toString().trim();
        post_user_id = getIntent().getStringExtra("post_user_id");

        userIdTextView.setText(user.getUser_id());
        new DataRetriever(ImageCommentActivity.this).retrieve(myGridView, myDataLoaderProgressBar);
    }


    private void setOnClickListener() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CommentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCommentString = CommentEditText.getText().toString().trim();

                if (GetCommentString.isEmpty() || GetCommentString.length() < 1) {
                    Utils.showMessageInSnackbar(context, "write something");
                    return;
                }else
                {
                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(ImageCommentActivity.this);
                    uploadAsyncTask.execute();
                }
            }
        });
    }

    private void initVariable() {
        myGridView = findViewById(R.id.myListView);
        myDataLoaderProgressBar = findViewById(R.id.myDataLoaderProgressBar);
        userIdTextView = (TextView) findViewById(R.id.id_imageview_userid);
        imageViewPicture  =  (ImageView) findViewById(R.id.imageView2);
        imageIdTextView = (TextView) findViewById(R.id.id_imageview_image_id);
        CommentEditText = (EditText) findViewById(R.id.comment_text_box);
        CommentSendButton = (ImageButton)findViewById(R.id.comment_send_button);
        backArrow = findViewById(R.id.backArrow);
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
                view = LayoutInflater.from(c).inflate(R.layout.row_model_comments, viewGroup, false);
            }

            TextView txtId = view.findViewById(R.id.id_model_notification);
            TextView txtSenderId = view.findViewById(R.id.sender_id);
            TextView txtSenderName = view.findViewById(R.id.sender_name);
            TextView txtStatus = view.findViewById(R.id.status_model_notification);
            TextView txtProfile_Url = view.findViewById(R.id.profile_url_model_notification);
            TextView txtBuddyMessage = view.findViewById(R.id.buddy_message);
            ImageView user_small_thumbnail = view.findViewById(R.id.id_user_rounded_thumbnail_notification);
            ImageView buddy_small_thumbnail = view.findViewById(R.id.id_buddy_rounded_thumbnail);
            TextView txtSenderUserName = view.findViewById(R.id.sender_user_name);
            TextView txtBuddyName = view.findViewById(R.id.buddy_name_message);

            final Notification notification = (Notification) this.getItem(i);

            txtId.setText(notification.getId());
            txtSenderId.setText(notification.getSender_Id());
            txtStatus.setText(notification.getStatus());
            txtProfile_Url.setText(notification.getProfile_Url());

            if(notification.getProfile_Url() != null && notification.getProfile_Url().length() > 0) {
            }

            if(user.getUser_id().equals(notification.getId()))         // your condition to hide dropdown
            {
                user_small_thumbnail.setVisibility(View.VISIBLE);
                Glide
                        .with(ImageCommentActivity.this)
                        .load(user.getUser_profile())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.placeholder_person)
                        .into(user_small_thumbnail);
                txtSenderName.setVisibility(View.VISIBLE);
                txtSenderUserName.setVisibility(View.VISIBLE);
                txtSenderName.setText(notification.getSender_Name());
                txtSenderUserName.setText(notification.getUser_name());
            } else

            {
                buddy_small_thumbnail.setVisibility(View.VISIBLE);

                Glide
                        .with(ImageCommentActivity.this)
                        .load(notification.getProfile_Url())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.placeholder_person)
                        .into(buddy_small_thumbnail);
                txtBuddyMessage.setVisibility(View.VISIBLE);
                txtBuddyName.setVisibility(View.VISIBLE);
                txtBuddyMessage.setText(notification.getSender_Name());
                txtBuddyName.setText(notification.getUser_name());
            }
      return view;
        }
    }

    class DataRetriever
    {
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

            AndroidNetworking.post(AppConstants.COMMENT_GET)
                    .addBodyParameter("post_id" , post_id)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                         //   Toast.makeText(context, String.valueOf(response) , Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject;
                            Notification notification;

                            try
                            {
                                for (int i = 0; i<response.length(); i++)
                                {
                                    jsonObject = response.getJSONObject(i);

                                    String id = jsonObject.getString("sender_id");
                                    String sender_id = jsonObject.getString("post_id");
                                    String sender_name = jsonObject.getString("comments");
                                    String status = jsonObject.getString("profile_url");
                                    String profile_url = jsonObject.getString("profile_url");
                                    String user_name = jsonObject.getString("name");

                                    notification = new Notification(id,sender_id,sender_name,status,profile_url , user_name);
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

    private class UploadAsyncTask extends AsyncTask<Void, Integer, String> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;
        private Exception exception;
      //  private ProgressDialog progressDialog;

        private UploadAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            String responseString = null;

            try {
                HttpPost httpPost = new HttpPost(AppConstants.COMMENT_SEND);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                multipartEntityBuilder.addTextBody("comments", GetCommentString);
                multipartEntityBuilder.addTextBody("user_id" , user.getUser_id());
                multipartEntityBuilder.addTextBody("user_name" , user.getName());
                multipartEntityBuilder.addTextBody("user_profile" , user.getUser_profile());
                multipartEntityBuilder.addTextBody("post_id" , post_id);
                multipartEntityBuilder.addTextBody("post_user_id", post_user_id);

                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        new MyHttpEntity.ProgressListener() {
                            @Override
                            public void transferred(float progress) {
                                publishProgress((int) progress);
                            }
                        };

                // POST
                httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));

                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(httpEntity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
                this.exception = e;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {

            CommentEditText.setText("");

            new DataRetriever(ImageCommentActivity.this).retrieve(myGridView, myDataLoaderProgressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }
    }

    class Notification {
        private String Sender_Id, Sender_Name , Status , Profile_Url , user_name;
        private String id;

        public Notification(String id , String Sender_Id, String Sender_Name , String Status , String Profile_Url , String user_name){
            this.id = id;
            this.Sender_Id = Sender_Id;
            this.Sender_Name = Sender_Name;
            this.Status = Status;
            this.Profile_Url = Profile_Url;
            this.user_name = user_name;
        }
        public String getId() {return id;}
        public String getSender_Id() {return Sender_Id;}
        public String getSender_Name() {return Sender_Name;}
        public String getStatus() {return Status;}
        public String getProfile_Url(){return Profile_Url;}
        public String getUser_name(){return user_name;}
    }
}
