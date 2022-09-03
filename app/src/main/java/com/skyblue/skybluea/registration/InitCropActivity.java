package com.skyblue.skybluea.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;


public class InitCropActivity extends AppCompatActivity {
    private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_USER_ID = "user_id";

    CropImageView cropImageView;
    Uri resultUri;
    final Context context = this;
    ImageView cropNowBtn ;
    Button croppedImageNextBtn;
    String data ;
    private Uri resultUriMy;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_crop);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropNowBtn = findViewById(R.id.button_crop_now);
        croppedImageNextBtn = findViewById(R.id.button_post_page_cropped);

        data = getIntent().getExtras().getString("img");

        cropImageView.setImageUriAsync(Uri.parse(data));
        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setShowProgressBar(false);
        cropImageView.setGuidelines(CropImageView.Guidelines.OFF);
        cropImageView.setShowCropOverlay(false);

        CropImage.activity(Uri.parse(data))
                .start(this);

        cropNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        croppedImageNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resultUri != null)
                {
                  yesCropped();
                }else
                {
                  noCropped();
                }

                UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(context);
                uploadAsyncTask.execute();

            }
        });

    }

    private void yesCropped() {
        /*  get image uri to bitmap path */
        data = getIntent().getExtras().getString("img");
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(resultUri)));
            // bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void noCropped() {
        /*  get image uri to bitmap path */
        data = getIntent().getExtras().getString("img");
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data));
            // bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));

        } catch (IOException e) {
            e.printStackTrace();
        }
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

               /*                   Don't delete

                FixBitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                ConvertedImageHolder = Base64.encodeToString(byteArray, Base64.DEFAULT);

                File dir = Environment.getExternalStorageDirectory();
                File file_image = new File(dir, "/Skyblue/thumbnail.png");

                */

                String filename = "hiiii";
                File f = new File(context.getCacheDir(), filename);
                f.createNewFile();

                //  File file = new File(String.valueOf(data));

//Convert bitmap to byte array
                // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);


                /*
                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String userIdHolder = sp.getString(KEY_PREFE_ID, null);
                String userNameHolder = sp.getString(KEY_PREFE_NAME, null);

                 */

                session = new SessionHandler(getApplicationContext());
                User user = session.getUserDetails();
                user.getMobile();

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String USER_ID_HOLDER = sp.getString(KEY_PREFE_USER_ID, null);


                multipartEntityBuilder.addPart("file_image", new FileBody(f));
                multipartEntityBuilder.addTextBody("user_id", USER_ID_HOLDER);

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
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            // Close dialog
            this.progressDialog.dismiss();

            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_updated), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(context , Home.class);
            startActivity(intent);
            finish();

            Toast.makeText(getApplicationContext(),
                    result, Toast.LENGTH_LONG).show();
            //  showFileChooser();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                resultUriMy = Uri.parse(String.valueOf(resultUri));
                //   Toast.makeText(CropActivityNew.this, (CharSequence) resultUriMy,Toast.LENGTH_SHORT).show();

                cropImageView.setImageUriAsync(resultUriMy);

                cropImageView.setAutoZoomEnabled(false);
                cropImageView.setShowProgressBar(false);
                cropImageView.setGuidelines(CropImageView.Guidelines.OFF);
                cropImageView.setShowCropOverlay(false);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed () {
        // super.onBackPressed();

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.row_model_discard_dialog_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //     final EditText userInput = (EditText) promptsView
        //          .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.discard),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                finish();
                                //DeletePhoto();

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
