package com.skyblue.skybluea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final  String KEY_PREFE_PROFILE_URL = "profile_url";

    private String message_send_url = "https://www.skyblue-watch.xyz/web/handle/send_message.php";
    private static final String GET_MESSAGE_RECYCLER = "https://www.skyblue-watch.xyz/web/handle/get_messages_recycler.php";

    EmojiEditText MessageTextBox;
    ImageButton MessageSendButton;

    ImageView emojiIcon , emojiKeyBod , cmaeraIcon;
    ViewGroup rootView;

    String BuddyId;
    String  toServerUnicodeEncoded;
    String GetSendString;
    String newStringEmojidecooded;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Message> messageList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_message);

        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();

        SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolder = sp.getString(KEY_PREFE_ID, null);
        String userNameHolder = sp.getString(KEY_PREFE_NAME, null);

        MessageTextBox = findViewById(R.id.message_text_box);
        MessageSendButton = findViewById(R.id.message_send_button);
        emojiIcon = findViewById(R.id.emoji);
        emojiKeyBod = findViewById(R.id.emoji_keyboard);
        rootView = findViewById(R.id.main_activity_root_view);
        cmaeraIcon = findViewById(R.id.camera);
        BuddyId = getIntent().getStringExtra("buddy_id_key");

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(MessageTextBox);

        mList = findViewById(R.id.message_list);
        mList.setItemViewCacheSize(20);
        messageList = new ArrayList<>();

        adapter = new MessageAdap(getApplicationContext(),messageList);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //       dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        //     mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);
        linearLayoutManager.setReverseLayout(true);
        mList.setLayoutManager(linearLayoutManager);

        messageList.clear();
        getData();







        cmaeraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, MessageSendPhotoActivity.class);
                intent.putExtra("buddy_id", getIntent().getStringExtra("buddy_id_key"));
                intent.putExtra("buddy_profile", getIntent().getStringExtra("profile_id_key"));
                startActivity(intent);
                finish();
            }
        });
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
        MessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSendString = Objects.requireNonNull(MessageTextBox.getText()).toString().trim();

                if (GetSendString.isEmpty() || GetSendString.length() < 1) {
                    Toast.makeText(MessageActivity.this, "write message",Toast.LENGTH_SHORT).show();
                    return;
                }else {


                    //     toServerUnicodeEncoded = org.apache.commons.text.StringEscapeUtils.escapeJava(GetSendString);
                    try {
                        byte[] data = MessageTextBox.getText().toString().getBytes("UTF-8");
                        toServerUnicodeEncoded = Base64.encodeToString(data, Base64.DEFAULT);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(MessageActivity.this);
                    uploadAsyncTask.execute();
                }
            }
        });
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        final StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_MESSAGE_RECYCLER,
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


                                Message message = new Message();
                                message.setMessage(jsonObject2.getString("message"));
                                message.setImageUrl(jsonObject2.getString("image"));
                                message.setSender_id(jsonObject2.getString("sender_id"));




                                messageList.add(message);
                                //       Toast.makeText(SearchActivity.this, jsonObject2.getString("id"), Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        // adapter.clear();


                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    }

                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String userIdHolder = sp.getString(KEY_PREFE_ID, null);

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("sender_id", userIdHolder);
                params.put("buddy_id", getIntent().getStringExtra("buddy_id_key"));



                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);
        requestQueue.add(stringRequest);

    }


    private class UploadAsyncTask extends AsyncTask<Void, Integer, String> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;
        private Exception exception;
        private ProgressDialog progressDialog;

        private UploadAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            String responseString = null;

            // Get video name user enterd



            try {
                HttpPost httpPost = new HttpPost(message_send_url);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();


                SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String userIdHolder = sp.getString(KEY_PREFE_ID, null);
                String userNameHolder = sp.getString(KEY_PREFE_NAME, null);
                String userProfileImageHolder = sp.getString(KEY_PREFE_PROFILE_URL, null);

                /*

                     Work process hidden reuse
                                   BuddyId = getIntent().getStringExtra("buddy_id_key");
               */




                // Declare string body
                // StringBody video_name = new StringBody(videoNameHolder);
                StringBody message_string = new StringBody(toServerUnicodeEncoded);
                StringBody sender_id_string = new StringBody(Objects.requireNonNull(userIdHolder));
                StringBody buddy_id_string = new StringBody(BuddyId);
                StringBody sender_name_string = new StringBody(Objects.requireNonNull(userNameHolder));
                StringBody sender_profilr_url_string = new StringBody(Objects.requireNonNull(userProfileImageHolder));


                // Add the file to be uploaded

                multipartEntityBuilder.addPart("message", message_string);
                multipartEntityBuilder.addPart("sender_id", sender_id_string);
                multipartEntityBuilder.addPart("buddy_id", buddy_id_string);
                multipartEntityBuilder.addPart("sender_name", sender_name_string);
                multipartEntityBuilder.addPart("send_profile_url", sender_profilr_url_string);


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

            // Init and show dialog
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setMessage("Please wait...");
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            // Close dialog
            this.progressDialog.dismiss();
            //Set the user session

            messageList.clear();
            getData();
            MessageTextBox.setText("");


            //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]);
        }
    }

    public class MessageAdap extends RecyclerView.Adapter<MessageAdap.ViewHolder>{
        private static final String SHARED_PREFE_ID = "mypref";
        private static final String KEY_PREFE_ID = "myid";
        private static final String KEY_PREFE_NAME = "user_name";
        private static final  String KEY_PREFE_PROFILE_URL = "profile_url";

        private String newStringEmojidecooded;

        Context context;

        private List <Message> list;
        View v;

        public MessageAdap (Context context, List<Message>list)
        {
            this.context = context;
            this.list = list;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            v = LayoutInflater.from(context).inflate(R.layout.row_model_messages,parent,false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Message message = list.get(position);


            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
            String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);
            String userProfileUrlHolder = sp.getString(KEY_PREFE_PROFILE_URL, null);

            if (userIdHolderShared.equals(message.getSender_id()))
            {


                if (message.getMessage().matches(""))
                {
                    holder.senderImageView.setVisibility(View.VISIBLE);


                    holder.user_small_thumbnail.setVisibility(View.VISIBLE);
                    final float scale = v.getContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (190 * scale + 0.5f);

                    holder.rel.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    //  holder.senderImageView.getLayoutParams().height = pixels;
                    holder.senderImageView.getLayoutParams().width = pixels;

                    Glide
                            .with(context)
                            .load(userProfileUrlHolder)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.user_small_thumbnail);
                    Glide
                            .with(context)
                            .load(message.getImageUrl())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.senderImageView);

                    holder.senderImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MessagePhotoViewActivity.class);
                            intent.putExtra("image", message.getImageUrl());
                            startActivity(intent);
                        }
                    });
                }else
                {

                    holder.txtSenderId.setVisibility(View.INVISIBLE);
                    holder.user_small_thumbnail.setVisibility(View.VISIBLE);
                    holder.txtSenderMessage.setVisibility(View.VISIBLE);

                    holder.txtSenderId.setText(message.getSender_id());
                    try {
                        byte [] data = Base64.decode(message.getMessage(), Base64.DEFAULT);
                        newStringEmojidecooded = new String(data, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    holder.txtSenderMessage.setText(newStringEmojidecooded);
                    Glide
                            .with(context)
                            .load(userProfileUrlHolder)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.user_small_thumbnail);
                }


            }else   /////              main else                           //////////////////////////
            {


                if (message.getMessage().matches(""))
                {

                    holder.buddyImageView.setVisibility(View.VISIBLE);
                    holder.buddy_small_thumbnail.setVisibility(View.VISIBLE);
                    holder.txtBuddyMessage.setVisibility(View.INVISIBLE);
                    holder.txtBuddyId.setVisibility(View.INVISIBLE);
                    final float scale = v.getContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (190 * scale + 0.5f);


                    holder.rel.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //   holder.buddyImageView.getLayoutParams().height = pixels;
                    holder.buddyImageView.getLayoutParams().width = pixels;


                    Glide
                            .with(context)
                            .load(message.getImageUrl())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.buddyImageView);

                    Glide
                            .with(context)
                            .load(getIntent().getStringExtra("profile_id_key"))
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.buddy_small_thumbnail);

                    holder.buddyImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MessagePhotoViewActivity.class);
                            intent.putExtra("image", message.getImageUrl());
                            context.startActivity(intent);
                        }
                    });
                }else
                {




                    holder.txtBuddyId.setVisibility(View.INVISIBLE);
                    holder.buddy_small_thumbnail.setVisibility(View.VISIBLE);
                    holder.txtBuddyMessage.setVisibility(View.VISIBLE);

                    holder.txtBuddyId.setText(message.getSender_id());
                    try {
                        byte [] data = Base64.decode(message.getMessage(), Base64.DEFAULT);
                        newStringEmojidecooded = new String(data, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    holder.txtBuddyMessage.setText(newStringEmojidecooded);
                    Glide
                            .with(context)
                            .load(getIntent().getStringExtra("profile_id_key"))
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.placeholder_person)
                            .into(holder.buddy_small_thumbnail);

                }
            }



        }

        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public int getItemViewType(int position){
            return position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView txtMessageId , txtSenderId , txtStatus , txtProfile_Url  , txtBuddyId;
            EmojiTextView txtSenderMessage , txtBuddyMessage;
            ImageView senderImageView , user_small_thumbnail , buddy_small_thumbnail , buddyImageView;
            RelativeLayout rel;

            public ViewHolder(final View itemView)
            {
                super(itemView);

                EmojiManager.install(new IosEmojiProvider());
                txtMessageId = itemView.findViewById(R.id.id_model_notification);
                txtSenderId = itemView.findViewById(R.id.sender_id);
                txtBuddyId = itemView.findViewById(R.id.buddy_id);
                txtSenderMessage = itemView.findViewById(R.id.sender_message);
                txtStatus = itemView.findViewById(R.id.status_model_notification);
                txtProfile_Url = itemView.findViewById(R.id.profile_url_model_notification);
                txtBuddyMessage = itemView.findViewById(R.id.buddy_message);
                senderImageView = itemView.findViewById(R.id.sender_image);
                buddyImageView = itemView.findViewById(R.id.buddy_image);
                user_small_thumbnail = itemView.findViewById(R.id.id_user_rounded_thumbnail_notification);
                buddy_small_thumbnail = itemView.findViewById(R.id.id_buddy_rounded_thumbnail);
                rel = itemView.findViewById(R.id.relMessageContainer);
            }
        }
    }
}
