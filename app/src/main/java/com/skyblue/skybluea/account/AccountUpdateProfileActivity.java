package com.skyblue.skybluea.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.LoginActivity;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.Utils;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AccountUpdateProfileActivity extends AppCompatActivity {
    private SessionHandler session;

    private CropImageView cropImageView;
    private String received_uri;
    private Uri cropImageUri;
    Bitmap croppedBitmap;

    RelativeLayout cropBtnRel;
    ImageView cropBtn , cropUndoBtn;
    Button updateBtn , cropFinishBtn, cropCancelBtn ;
    File f;

    private String dataOriginal;
    Bitmap bitmapOriginal;
    Context context = this;
    ProgressDialog dialog;
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update_profile);

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropBtnRel = findViewById(R.id.id_crop_btn_rel);
        cropBtn = findViewById(R.id.id_crop_btn);
        updateBtn = findViewById(R.id.id_update_btn);
        cropFinishBtn = findViewById(R.id.id_crop_finish_btn);
        cropCancelBtn = findViewById(R.id.id_crop_cancel_btn);
        cropUndoBtn = findViewById(R.id.id_undo_btn);


        AccountUpdateProfileActivity context = this;

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

        String title = "Please wait";

        dialog = new ProgressDialog(AccountUpdateProfileActivity.this, R.style.AppCompatAlertDialogStyle);
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

        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        String userId = user.getUser_id();

        AndroidNetworking.upload(AppConstants.INIT_PROFILE_PICTURE_UPLOAD)
                .addMultipartFile("file_image",f)
                .addMultipartParameter("user_id",userId)
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

                        //  Toast.makeText(AccountUpdateProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        try {

                            //  JSONObject jsonObject = new JSONObject(response);

                            if (response.optString("status").equals("true"))
                            {
                                ArrayList<LoginActivity.CurrentUser> CurrentUserArrayList = new ArrayList<>();

                                JSONArray jsonArrayst = response.getJSONArray("data");

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
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


}




