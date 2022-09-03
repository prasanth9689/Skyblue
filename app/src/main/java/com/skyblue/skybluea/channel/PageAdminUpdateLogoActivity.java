package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class PageAdminUpdateLogoActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private String received_uri;
    private Uri cropImageUri;
    Bitmap croppedBitmap;

    RelativeLayout cropBtnRel;
    ImageView cropBtn , cropUndoBtn;
    Button updateBtn , cropFinishBtn, cropCancelBtn ;

    private static final String SERVER_PATH = "https://www.skyblue-watch.xyz/web/handle/pages/update/logo/logo_update.php";

    File f;

    private String dataOriginal;
    Bitmap bitmapOriginal;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String  KEY_PREFE_PROFILE_TWO_URL = "profile_url_two";
    private static final String KEY_PREFE_ID = "myid";

    Context context = this;

    ProgressDialog dialog;

    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_admin_update_logo);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropBtnRel = findViewById(R.id.id_crop_btn_rel);
        cropBtn = findViewById(R.id.id_crop_btn);
        updateBtn = findViewById(R.id.id_update_btn);
        cropFinishBtn = findViewById(R.id.id_crop_finish_btn);
        cropCancelBtn = findViewById(R.id.id_crop_cancel_btn);
        cropUndoBtn = findViewById(R.id.id_undo_btn);


        PageAdminUpdateLogoActivity context = this;

        received_uri = (getIntent().getStringExtra("img"));
        cropImageUri = Uri.parse(received_uri);
        cropImageView.setImageUriAsync(Uri.parse(received_uri));

        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setShowProgressBar(false);
        cropImageView.setGuidelines(CropImageView.Guidelines.OFF);
        cropImageView.setShowCropOverlay(false);

        /*
        For genuine
         */

        dataOriginal = getIntent().getExtras().getString("img");
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            bitmapOriginal = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(dataOriginal));
            // bitmap = BitmapFactory.decodeFile(String.valueOf(imageUri));

        } catch (IOException e) {
            e.printStackTrace();
        }


        cropBtnRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImageView.setShowProgressBar(true);
                cropImageView.setGuidelines(CropImageView.Guidelines.ON);
                cropImageView.setShowCropOverlay(true);

                updateBtn.setVisibility(View.INVISIBLE);
                cropFinishBtn.setVisibility(View.VISIBLE);
                cropCancelBtn.setVisibility(View.VISIBLE);
            }
        });

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImageView.setShowProgressBar(true);
                cropImageView.setGuidelines(CropImageView.Guidelines.ON);
                cropImageView.setShowCropOverlay(true);

                updateBtn.setVisibility(View.INVISIBLE);
                cropFinishBtn.setVisibility(View.VISIBLE);
                cropCancelBtn.setVisibility(View.VISIBLE);

            }
        });

        cropCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImageView.setAutoZoomEnabled(false);
                cropImageView.setShowProgressBar(false);
                cropImageView.setGuidelines(CropImageView.Guidelines.OFF);
                cropImageView.setShowCropOverlay(false);

                cropCancelBtn.setVisibility(View.INVISIBLE);
                cropFinishBtn.setVisibility(View.INVISIBLE);

                updateBtn.setVisibility(View.VISIBLE);
            }
        });

        cropFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                croppedBitmap = cropImageView.getCroppedImage();

                if (croppedBitmap != null) {
                    cropImageView.setImageBitmap(croppedBitmap);

                    cropImageView.setAutoZoomEnabled(false);
                    cropImageView.setShowProgressBar(false);
                    cropImageView.setGuidelines(CropImageView.Guidelines.OFF);
                    cropImageView.setShowCropOverlay(false);

                    cropCancelBtn.setVisibility(View.INVISIBLE);
                    cropFinishBtn.setVisibility(View.INVISIBLE);

                    updateBtn.setVisibility(View.VISIBLE);
                    cropUndoBtn.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cropUndoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setImageUriAsync(Uri.parse(received_uri));

                cropImageView.setShowProgressBar(true);
                cropImageView.setGuidelines(CropImageView.Guidelines.ON);
                cropImageView.setShowCropOverlay(true);

                updateBtn.setVisibility(View.INVISIBLE);
                cropFinishBtn.setVisibility(View.VISIBLE);
                cropCancelBtn.setVisibility(View.VISIBLE);

                cropUndoBtn.setVisibility(View.INVISIBLE);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(AccountUpdateProfileActivity.this);
                uploadAsyncTask.execute();

 */

                Profile();

            }
        });

    }


    private void Profile(){



        if (croppedBitmap != null) {

            String filename = "hiiii";
            f = new File(context.getCacheDir(), filename);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  File file = new File(String.valueOf(data));



            //Convert bitmap to byte array
            // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fos.write(bitmapdata);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            String filename = "hiiii";
            f = new File(context.getCacheDir(), filename);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  File file = new File(String.valueOf(data));



            //Convert bitmap to byte array
            // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fos.write(bitmapdata);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolder = sp.getString(KEY_PREFE_ID, null);

        String title = "Please wait";

        dialog = new ProgressDialog(PageAdminUpdateLogoActivity.this, R.style.AppCompatAlertDialogStyle);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Please wait");
        dialog.setMax(100);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog
            }
        });
        dialog.show();

        String page_id = getIntent().getStringExtra("common_id");

        AndroidNetworking.upload(SERVER_PATH)
                .addMultipartFile("fileImage",f)
                .addMultipartParameter("page_id",page_id)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        final int progress = (int) (bytesUploaded * 100 / totalBytes);
                        dialog.incrementProgressBy(progress);

                        if (progress == 100)
                        {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    dialog.dismiss();
                                    Toast.makeText(context, "Profile updated success", Toast.LENGTH_SHORT).show();

                                }
                            }, 300);


                        }
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        //Toast.makeText(AccountUpdateProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
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
                HttpPost httpPost = new HttpPost(SERVER_PATH);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

               /*                   Don't delete

                FixBitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                ConvertedImageHolder = Base64.encodeToString(byteArray, Base64.DEFAULT);

                File dir = Environment.getExternalStorageDirectory();
                File file_image = new File(dir, "/Skyblue/thumbnail.png");

                */

                if (croppedBitmap != null) {

                    String filename = "hiiii";
                    f = new File(context.getCacheDir(), filename);
                    f.createNewFile();

                    //  File file = new File(String.valueOf(data));



                    //Convert bitmap to byte array
                    // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);

                } else {
                    String filename = "hiiii";
                    f = new File(context.getCacheDir(), filename);
                    f.createNewFile();

                    //  File file = new File(String.valueOf(data));



                    //Convert bitmap to byte array
                    // Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);

                }
                /*
                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String userIdHolder = sp.getString(KEY_PREFE_ID, null);
                String userNameHolder = sp.getString(KEY_PREFE_NAME, null);

                 */
/*
                session = new SessionHandler(getApplicationContext());
                User user = session.getUserDetails();
                user.getMobile();

 */

                SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String userIdHolder = sp.getString(KEY_PREFE_ID, null);


                multipartEntityBuilder.addPart("fileImage", new FileBody(f));
                multipartEntityBuilder.addTextBody("user_id", userIdHolder);
                /*
                multipartEntityBuilder.addTextBody("image_about", AboutImageEditeTextHolder);
                multipartEntityBuilder.addTextBody("user_id", userIdHolder);
                multipartEntityBuilder.addTextBody("user_name", userNameHolder);
                multipartEntityBuilder.addTextBody("time_date", timeDateString);

                 */




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


}




