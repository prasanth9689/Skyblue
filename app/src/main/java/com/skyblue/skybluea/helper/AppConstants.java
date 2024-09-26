package com.skyblue.skybluea.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppConstants {


    private static final String BASE_URL = "http://sh002.hostgator.tempwebhost.net/";

    public static final String GET_VIDEO_DETAILS = "get_video_details";
    public static final String LOGIN = BASE_URL + "login.php";
    public static final String CHECK_USER_ALREADY_EXISTS = BASE_URL + "check_user.php";
    public static final String REGISTER = BASE_URL + "register.php";
    public static final String INIT_PROFILE_PICTURE_UPLOAD = BASE_URL + "init_profile_picture_upload.php";
    public static final String INIT_PRIVACY_POLICY = BASE_URL + "privacy_policy.html";
    public static final String FORGET_PASSWORD_CHANGE = BASE_URL + "forget_password_change.php";
    public static final String COVER_PICTURE_UPLOAD = BASE_URL + "cover_picture_upload.php";
    public static final String PROFILE_PICTURE_DELETE = BASE_URL + "profile_picture_delete.php";
    public static final String COVER_PICTURE_DELETE = BASE_URL + "cover_picture_delete.php";
    public static final String GENDER_UPDATE = BASE_URL + "gender_update.php";
    public static final String DOB_UPDATE = BASE_URL + "zdob_update.php";
    public static final String POST_IMAGE_UPLOAD = BASE_URL + "post_image_upload.php";
    public static final String POST_VIDEO_UPLOAD = BASE_URL + "post_video_upload.php";
    public static final String HOME_GET_COMMON_DATA = BASE_URL + "get_common_data.php";
    public static final String AC_GET_MEDIA_DATA = BASE_URL + "get_ac_user_media.php";
    public static final String POST_LIKED = BASE_URL + "post_liked.php";
    public static final String POST_UNLIKED = BASE_URL + "post_unliked.php";
    public static final String COMMENT_GET = BASE_URL + "comments_get.php";
    public static final String COMMENT_SEND = BASE_URL + "comment_send.php";
    public static final String HOME_GET_POST_DATA = BASE_URL + "init_seller_apply.php";
    public static final String HOME_SEARCH = "http://sh021.hostgator.tempwebhost.net/~skybl4b37w/skyblue/search.php";
    public static final String PRF_GET_COMMON_MEDIA_DATA = BASE_URL + "get_profile_common_media.php";
    public static final String CHECK_APP_VERSION = "";
    public static final String ACCESS_CHANNEL_CREATION = "0";
    public static final String ACCESS_CHANNEL_GET = "1";
    public static final String ACCESS_SEARCH_GET = "11";

    /* private Constructor to avoid instanciating this class */
    private AppConstants() {}
}
