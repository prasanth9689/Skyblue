package com.skyblue.skybluea.registration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.LoginActivity;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.Utils;

import java.io.IOException;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class InitImageViewActivity extends AppCompatActivity {
    private SessionHandler session;

    private ZoomageView demoView;
    ImageView cropNextButton ;
    Button updateProfileBtn , skipBtn;
    String data;
    TextView cameraUriTextView;
    final Context context = this;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_image_view);
        data = getIntent().getExtras().getString("img");

        demoView = findViewById(R.id.myZoomageView);
        updateProfileBtn = findViewById(R.id.post_page_next_button);
        cropNextButton = findViewById(R.id.button_crop_next);
        cameraUriTextView = findViewById(R.id.camera_txt);
        skipBtn = findViewById(R.id.id_skip_button_init);



        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        user.getMobile();

        demoView.setImageURI(Uri.parse(data));

        cameraUriTextView.setText(data);

        /*  get image uri to bitmap path */
        data = getIntent().getExtras().getString("img");
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data));
            // bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));

        } catch (IOException e) {
            e.printStackTrace();
        }

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cropNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
                String get_country_name = getIntent().getStringExtra("countryname");
                String get_country_phone_code = getIntent().getStringExtra("phonecode");

                Uri sendUri = Uri.parse(data);

                Intent intent = new Intent(InitImageViewActivity.this , InitCropActivity.class);
                intent.putExtra("img" , getIntent().getExtras().getString("img"));
                intent.putExtra("cam" , sendUri.toString());


                intent.putExtra("phonenumberonly" , get_mobile_no);
                intent.putExtra("countryname" , get_country_name);
                intent.putExtra("phonecode" , get_country_phone_code);
                startActivity(intent);
                finish();
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(context);
                uploadAsyncTask.execute();
            }
        });
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

            try {
                HttpPost httpPost = new HttpPost(AppConstants.INIT_PROFILE_PICTURE_UPLOAD);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                String filename = "hiiii";
                File f = new File(context.getCacheDir(), filename);
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                session = new SessionHandler(getApplicationContext());
                User user = session.getUserDetails();
                String userId = user.getUser_id();

                multipartEntityBuilder.addPart("file_image", new FileBody(f));
                multipartEntityBuilder.addTextBody("user_id", userId);

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
            this.progressDialog = new ProgressDialog(this.context, R.style.AppCompatAlertDialogStyle);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(true);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            // Close dialog
            this.progressDialog.dismiss();

            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.optString("status").equals("true"))
                {
                    ArrayList<LoginActivity.CurrentUser> CurrentUserArrayList = new ArrayList<>();

                    JSONArray jsonArrayst = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArrayst.length(); i++)
                    {
                        LoginActivity.CurrentUser CurrentUserDetails = new LoginActivity.CurrentUser();
                        JSONObject dataobjst = jsonArrayst.getJSONObject(i);

                        CurrentUserDetails.setProfile_url(dataobjst.getString("profile_url"));
                        CurrentUserDetails.setMessage(dataobjst.getString("message"));
                        CurrentUserArrayList.add(CurrentUserDetails);
                    }
                    for (int j = 0; j < CurrentUserArrayList.size(); j++) {

                        switch (Integer.parseInt(CurrentUserArrayList.get(j).getMessage())) {

                            case 1:
                                Utils.showMessage(context, "Error try again!");
                                break;

                            case 2:
                                Utils.showMessage(context, "File is missing!");
                                break;

                            case 3:
                                Toast.makeText(context, "Profile updated",Toast.LENGTH_SHORT).show();
                                String userProfile = CurrentUserArrayList.get(j).getProfile_url();

                                session.loginUserUpdateProfile(userProfile);

                                Intent intent = new Intent(context, Home.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;

                            default:
                                Utils.showMessageInSnackbar(context, "SERVER ERROR!");
                                break;
                        }
//
                    }
                } else {
                    //      Toast.makeText(HomeHomeFragmentActivity.this.getActivity().getApplicationContext(), jsonObjectluser.optString("message") + "", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]);
        }
    }


}
