package com.skyblue.skybluea.forget_password;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ForgetPasChangeActivity extends AppCompatActivity {

    TextView mobileNumber;
    EditText editTextPassword;
    EditText editTextconfirmPassword;
    Button changePassBtn;
    String passwordHolder;
    String confirmPasswordHolder;
    String mobileNumberHolder;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forget_pas_change);
        mobileNumber = findViewById(R.id.id_mobile_no_txtview);
        editTextPassword = findViewById(R.id.id_password);
        editTextconfirmPassword = findViewById(R.id.id_confirm_password);
        changePassBtn = findViewById(R.id.btnChangePass);

        mobileNumber.setText(getIntent().getStringExtra("phonenumberonlyVPA"));

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordHolder = editTextPassword.getText().toString().trim();
                confirmPasswordHolder = editTextconfirmPassword.getText().toString().trim();

                mobileNumberHolder = mobileNumber.getText().toString().trim();

                if (passwordHolder.isEmpty() || passwordHolder.length() < 1) {
                    editTextPassword.setError(getResources().getString(R.string.valid_password_required));
                    editTextPassword.requestFocus();
                    return;
                }

                if (confirmPasswordHolder.isEmpty() || confirmPasswordHolder.length() < 1) {
                    editTextconfirmPassword.setError(getResources().getString(R.string.valid_confirm_password_required));
                    editTextconfirmPassword.requestFocus();
                    return;
                }
                regHttp();
            }

            private void regHttp() {
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
                HttpPost httpPost = new HttpPost(AppConstants.FORGET_PASSWORD_CHANGE);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                StringBody password_string = new StringBody(passwordHolder);
                StringBody mobile_string = new StringBody(mobileNumberHolder);

                multipartEntityBuilder.addPart("mobile_number_only", mobile_string);
                multipartEntityBuilder.addPart("password", password_string);

                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        new MyHttpEntity.ProgressListener() {
                            @Override
                            public void transferred(float progress) {
                                publishProgress((int) progress);
                            }
                        };

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
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setMessage(getResources().getString(R.string.please_wait___));
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            this.progressDialog.dismiss();
            loadSuccess();
            Toast.makeText(getApplicationContext(),
                    result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]);
        }
    }

    private void loadSuccess() {
        Intent intent = new Intent(context, ForgetPasChangeSuccess.class);
        startActivity(intent);
        finish();
    }
}
