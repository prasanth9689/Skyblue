package com.skyblue.skybluea.account;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.Home;
import com.skyblue.skybluea.MainActivity;
import com.skyblue.skybluea.PhotosGridViewActivity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.settings.SettingsActivity;
import com.skyblue.skybluea.utils.ImageConstants;


public class AccountActivity extends AppCompatActivity {
    private SessionHandler session;

    final Context context = this;
    private static final String KEY_EMPTY = "";
    private static final String KEY_NULL_EMPTY = "null";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    TextView NameTextView;
    ImageView UserProfileImageView;
    ListView listView;
    String[] listitem;
    ImageView ImageAddButtonOpenGallery;
    private static final String SERVER_PATH = "http://103.50.162.147/~skyblqxl/web/uprofile_picture/profile_picture_upload.php";
    private static final String GET_PROFILE_COVER = "https://www.skyblue-watch.xyz/web/uprofile_picture/get_profile_cover_picture_by_id.php";
    ImageView profileCoverImageView ;
    RelativeLayout addCoverImageViewButton;

//    int[] images = {R.drawable.account_circle, R.drawable.account_video_library, R.drawable.account_page_flag, R.drawable.account_settings, R.drawable.account_lock_out};
//
//    String[] version = {"My Profile" , " My Videos" , "Channel" , "Settings" , "Logout"};
//    String[] details = {"Edit your profile" , "Manage uploaded videos" , "Create page. manage page" , "Language , privacy and policy " , "Click to logout now"};
//    String[] versionNumber = {"1", "2", "3", "4" , "5"};
//
//    ListAdapter lAdapter;

    ImageView backButton ;
    RelativeLayout AddProfileCameraBtn;
    TextView skyblueUserId;
    RelativeLayout relTotalFriendsLayout;
    TextView totalFriends;
    String profilePictureStringHolder , profileCoverPictureStringHolder;
    RelativeLayout relUpdateCover;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initVariable();
        initSetOnClickListener();
        // initLoadAccountPage();

    }

//    private void initLoadAccountPage() {
//        lAdapter = new HomeProfileMenuAdapter(context, version, versionNumber, images , details );
//        listView.setAdapter(lAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                switch (Integer.parseInt(versionNumber[i] )) {
//
//                    case 1:
//                        Intent intent = new Intent(context, AccountProfileEditActivity.class);
//                        startActivity(intent);
//                        break;
//
//                    case 2:
//                        Intent inte = new Intent(context, VideosGridViewActivity.class);
//                        startActivity(inte);
//                        break;
//
//                    case 3:
//                        Intent intent2 = new Intent(AccountActivity.this, PageHomeActivity.class);
//                        startActivity(intent2);
//                        finish();
//                        break;
//
//                    case 4:
//                        Intent intent3 = new Intent(AccountActivity.this, SettingsActivity.class);
//                        startActivity(intent3);
//                        finish();
//                        break;
//
//                    case 5:
//                        session = new SessionHandler(getApplicationContext());
//                        User user = session.getUserDetails();
//
//                        session.logoutUser();
//
//                        Intent intent33 = new Intent(context, Home.class);
//                        intent33.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent33);
//                        break;
//
//                    default:
//                        Toast.makeText(context , "default", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        });
//    }

    private void initSetOnClickListener() {
        relTotalFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(context, MesBuddyListActivity.class);
                startActivity(intent);
                 */
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(KEY_EMPTY.equals(profilePictureStringHolder) | "null".equals(profilePictureStringHolder) | "".equals(profilePictureStringHolder)){

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("user_profile", user.getUser_profile());
                    i.putExtra("image_id", ImageConstants.PROFILE_PICTURE_UPLOAD);
                    startActivity(i);
                    finish();

                }else {
                    Intent i = new Intent(context, AccountProfileImageView.class);
                    i.putExtra("user_profile", user.getUser_profile());
                    i.putExtra("picture_title", ImageConstants.PROFILE_IMAGE_VIEW_TITLE);
                    i.putExtra("image_r_id", ImageConstants.PROFILE_IMAGE_VIEW);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        });
        AddProfileCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(AccountActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AccountActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //                showMessageInSnackbar("Permission already granded");

                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("user_profile", user.getUser_profile());
                        i.putExtra("image_id", ImageConstants.PROFILE_PICTURE_UPLOAD);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

        ImageAddButtonOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(AccountActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AccountActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //                showMessageInSnackbar("Permission already granded");

                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("user_profile", user.getUser_profile());
                        i.putExtra("image_id", ImageConstants.PROFILE_PICTURE_UPLOAD);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

        addCoverImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(AccountActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AccountActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //                showMessageInSnackbar("Permission already granded");

                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("user_profile", user.getUser_cover());
                        i.putExtra("image_id", ImageConstants.COVER_PICTURE_UPLOAD);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

        profileCoverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KEY_EMPTY.equals(profileCoverPictureStringHolder) || KEY_NULL_EMPTY.equals(profileCoverPictureStringHolder)){
                    if(ContextCompat.checkSelfPermission(AccountActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(AccountActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                                checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            //                showMessageInSnackbar("Permission already granded");

                            Intent i = new Intent(context, MainActivity.class);
                            i.putExtra("user_profile", user.getUser_cover());
                            i.putExtra("image_id", ImageConstants.COVER_PICTURE_UPLOAD);
                            startActivity(i);
                            finish();
                        }
                    }

                }else{
                                           /*

               Intent putExtra : image_r_id ( contains )
               -> Remove profile picture and Cover picture indicates by : image_r_id

                 -> image_r_id = 11 ( Profile picture )
                 -> image_r_id = 22 ( Cover picture )

         */
                    String image_r_id_Cover = "22";

                    String userProfileImage = "Cover picture";

                    Intent i = new Intent(context, AccountProfileImageView.class);
                    i.putExtra("user_profile", user.getUser_cover());
                    i.putExtra("picture_title", userProfileImage);
                    i.putExtra("image_r_id", image_r_id_Cover);
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
            }
        });

        relUpdateCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(AccountActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AccountActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                if (SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //                showMessageInSnackbar("Permission already granded");

                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("user_profile", user.getUser_cover());
                        i.putExtra("image_id", ImageConstants.COVER_PICTURE_UPLOAD);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

    }

    public void onClickMyProfile(View view){
        Intent intent = new Intent(context, AccountProfileEditActivity.class);
        startActivity(intent);
    }

    public void onClickMyVideos(View view){
        Intent inte = new Intent(context, AccountVideoListActivity.class);
        startActivity(inte);
    }

    public void onClickMyPhotos(View view){
        Intent inte = new Intent(context, AccountImageListActivity.class);
        startActivity(inte);
    }

    public void onClickSettings(View view){
        Intent intent3 = new Intent(AccountActivity.this, SettingsActivity.class);
        startActivity(intent3);
    }

    public void onClickLogout(View view){
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();

        session.logoutUser();

        Intent intent33 = new Intent(context, Home.class);
        intent33.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent33);
    }

    private void initVariable() {
        NameTextView = findViewById(R.id.user_name_pfview);
        UserProfileImageView = findViewById(R.id.id_image_view_up);
        //    listView = findViewById(R.id.id_list_view);
        listitem = getResources().getStringArray(R.array.array_profile_home);
        ImageAddButtonOpenGallery = findViewById(R.id.id_image_add_button_open_gallery);
        profileCoverImageView = findViewById(R.id.profile_cover_image_view);
        addCoverImageViewButton = findViewById(R.id.id_add_cover_image_button_bg);
        backButton = findViewById(R.id.id_back);
        totalFriends = findViewById(R.id.id_total_friends);
        AddProfileCameraBtn = findViewById(R.id.change_profile);
        skyblueUserId = findViewById(R.id.skyblue_id_text);
        relTotalFriendsLayout = findViewById(R.id.id_rel_total_friends_container);
        relUpdateCover = findViewById(R.id.Rel_update_cover);
    }

    class UserIn {

        private String id, name , profile_url , cover_picture_url;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getProfile_url(){return profile_url;}

        public String getCover_picture_url() {
            return cover_picture_url;
        }

        public void setId(String id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setProfile_url(String profile_url){this.profile_url = profile_url;}

        public void setCover_picture_url(String cover_picture_url) {
            this.cover_picture_url = cover_picture_url;
        }
    }



    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        //Toast.makeText(context, "Refreshed" ,Toast.LENGTH_SHORT).show();
        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        skyblueUserId.setText(user.getUser_id());
        NameTextView.setText(user.getName());

        Glide
                .with(getApplicationContext())
                .load(user.getUser_profile())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder_person)
                .into(UserProfileImageView);

        Glide
                .with(getApplicationContext())
                .load(user.getUser_cover())
                .placeholder(R.drawable.account_cover_gradient)
                .into(profileCoverImageView);
    }
}