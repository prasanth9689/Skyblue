package com.skyblue.skybluea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class MessageSendPhotoActivity extends AppCompatActivity {
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private int GALLERY = 1, CAMERA = 2;
    Bitmap FixBitmap;
    ImageView ShowSelectedImagePrimary;
    CropImageView ShowSelectedImageView;

    ImageView UploadImageOnServerButton;
    ImageView backBtn;

    Bitmap cropped;
    ImageView cropIcon;
    Button cropBtn;
    TextView UserIdTextView;
    RelativeLayout topOneRltL;
    ImageView addImageCenterIcon;
    ImageView GetImageFromGalleryButton;
    ByteArrayOutputStream byteArrayOutputStream ;

    private static final String SERVER_PATH = "https://www.skyblue-watch.xyz/web/zmessage/upload.php";


    String BuddyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send_photo);

        CheckStoragePermission();
        choosePhotoFromGallary();

        GetImageFromGalleryButton = (ImageView)findViewById(R.id.id_button_select);
        UploadImageOnServerButton = (ImageView)findViewById(R.id.id_button_upload);
        ShowSelectedImageView = (CropImageView) findViewById(R.id.id_selected_image_view);
        ShowSelectedImagePrimary = findViewById(R.id.id_selected_image_view_primary);

        UserIdTextView = findViewById(R.id.id_text_view_user_id);
        topOneRltL = findViewById(R.id.id_relative_layout_top_one);
        backBtn = findViewById(R.id.id_back_arrow);
        cropIcon = findViewById(R.id.id_crop_icon);
        cropBtn = findViewById(R.id.id_crop_button);
        addImageCenterIcon = findViewById(R.id.id_add_image_center);

        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolder = sp.getString(KEY_PREFE_ID, null);
        String userNameHolder = sp.getString(KEY_PREFE_NAME, null);

        UserIdTextView.setText(userIdHolder);

        byteArrayOutputStream = new ByteArrayOutputStream();



        addImageCenterIcon.setVisibility(View.VISIBLE);

        addImageCenterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choosePhotoFromGallary();

            }
        });

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropped = ShowSelectedImageView.getCroppedImage();

                ShowSelectedImageView.setImageBitmap(cropped);
                //  Toast.makeText(getApplicationContext(), "Success" + result, Toast.LENGTH_SHORT).show();
                ShowSelectedImageView.setAutoZoomEnabled(false);
                ShowSelectedImageView.setShowProgressBar(false);
                ShowSelectedImageView.setGuidelines(CropImageView.Guidelines.OFF);
                ShowSelectedImageView.setShowCropOverlay(false);

                UploadImageOnServerButton.setVisibility(View.VISIBLE);

                cropIcon.setVisibility(View.VISIBLE);
                cropBtn.setVisibility(View.INVISIBLE);


                ShowSelectedImageView.setVisibility(View.INVISIBLE);

                ShowSelectedImagePrimary.setVisibility(View.VISIBLE);
                ShowSelectedImagePrimary.setImageBitmap(cropped);
            }
        });

        cropIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSelectedImageView.setAutoZoomEnabled(true);
                ShowSelectedImageView.setShowProgressBar(true);
                ShowSelectedImageView.setGuidelines(CropImageView.Guidelines.ON);
                ShowSelectedImageView.setShowCropOverlay(true);

                cropIcon.setVisibility(View.INVISIBLE);
                cropBtn.setVisibility(View.VISIBLE);
                UploadImageOnServerButton.setVisibility(View.INVISIBLE);




                ShowSelectedImagePrimary.setVisibility(View.INVISIBLE);
                Bitmap tempImage = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                ShowSelectedImageView.setImageBitmap(tempImage);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePhotoFromGallary();

            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                UploadImageToServerTwo();

                //  Intent i = new Intent(Upload.this, DashboardActivity.class);
                // startActivity(i);

            }
        });

        if (ContextCompat.checkSelfPermission(MessageSendPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        5);
            }
        }

    }



    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    //   ShowSelectedImageView.setImageBitmap(FixBitmap);

                    ShowSelectedImagePrimary.setImageBitmap(FixBitmap);

                    ShowSelectedImageView.setAutoZoomEnabled(false);
                    ShowSelectedImageView.setShowProgressBar(false);
                    ShowSelectedImageView.setGuidelines(CropImageView.Guidelines.OFF);
                    ShowSelectedImageView.setShowCropOverlay(false);

                    addImageCenterIcon.setVisibility(View.INVISIBLE);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);
                    cropIcon.setVisibility(View.VISIBLE);




                    //     topOneRltL.setBackgroundColor(Color.parseColor("#FF000000"));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageSendPhotoActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImageView.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public void UploadImageToServerTwo (){
        UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(MessageSendPhotoActivity.this);
        uploadAsyncTask.execute();
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
            //  videoNameHolder = videoName.getText().toString().trim();


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



                String filename = "hiiii";
                File f = new File(context.getCacheDir(), filename);
                f.createNewFile();


                //   cropped = ShowSelectedImageView.saveCroppedImageAsync();





//Convert bitmap to byte array
                Bitmap bitmap = ((BitmapDrawable)ShowSelectedImagePrimary.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);

                SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);

                String userIdHolder = sp.getString(KEY_PREFE_ID, null);
                String userNameHolder = sp.getString(KEY_PREFE_NAME, null);




                                   BuddyId = getIntent().getStringExtra("buddy_id");





                StringBody sender_id_string = new StringBody(Objects.requireNonNull(userIdHolder));
                StringBody buddy_id_string = new StringBody(BuddyId);

                // Add the file to be uploaded

                multipartEntityBuilder.addPart("file_image", new FileBody(f));

                multipartEntityBuilder.addPart("sender_id", sender_id_string);
                multipartEntityBuilder.addPart("buddy_id", buddy_id_string);




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



            Intent intent = new Intent(MessageSendPhotoActivity.this , MessageActivity.class);
            intent.putExtra("buddy_id_key", getIntent().getStringExtra("buddy_id"));
            intent.putExtra("profile_id_key" , getIntent().getStringExtra("buddy_profile"));
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
    public void onBackPressed(){
        super.onBackPressed();

        Intent i  = new Intent(this, MessageActivity.class);
        i.putExtra("buddy_id_key", getIntent().getStringExtra("buddy_id"));
        i.putExtra("profile_id_key" , getIntent().getStringExtra("buddy_profile"));
        startActivityForResult(i ,1);
        finish();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
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
        if (ContextCompat.checkSelfPermission(MessageSendPhotoActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MessageSendPhotoActivity.this,
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
                Toast.makeText(MessageSendPhotoActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MessageSendPhotoActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MessageSendPhotoActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();

                CheckCameraPermission();
            } else {
                Toast.makeText(MessageSendPhotoActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

                CheckCameraPermission();
            }

        }
    }
}
