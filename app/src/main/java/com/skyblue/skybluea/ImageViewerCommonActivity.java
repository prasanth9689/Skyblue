package com.skyblue.skybluea;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;

public class ImageViewerCommonActivity extends AppCompatActivity {
    private ZoomageView profileImageView;
    private Uri profileUploadImageUri;
    private ImageView imageViewTick;
    //  private static final String userId = "99";



    ///   private static final String TAG = UploadProfilePictureActivity.class.getSimpleName();
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final int YOUR_PERMISSION_STATIC_CODE_IDENTIFIER=001;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CONTACTS= 111;

    Uri imageUri;
    Bitmap bitmapImage;
    private String received_uri , received_uri_cover;
    String userIdHolder;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";

    ImageView backBtn , coverImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_common);
        profileImageView = (ZoomageView) findViewById(R.id.CropImageView);

        backBtn = findViewById(R.id.backBtn);
        coverImageView = findViewById(R.id.profile_cover_image_view);

        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        final RelativeLayout placeHolderRelativeLayout = findViewById(R.id.relPlaceholder);


        received_uri = (getIntent().getStringExtra("image_url"));

        //profileImageView.setImageURI(Uri.parse(received_uri));


        ImageViewerCommonActivity context = this;
/*
        Glide
                .with( context )
                .load(received_uri)
                //  .apply(RequestOptions.circleCropTransform())
                .placeholder(R.color.image_placeholder_bg)
                .into( profileImageView );

 */

        Glide.with(context)
                .load(received_uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.VISIBLE);
                        placeHolderRelativeLayout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        placeHolderRelativeLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
        .into(profileImageView);




        SharedPreferences sp = getApplicationContext().getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        userIdHolder = sp.getString(KEY_PREFE_ID, null);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
