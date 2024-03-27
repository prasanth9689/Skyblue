package com.skyblue.skybluea.helper.session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMPTY = "";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_USER_COVER = "user_cover";
    private static final String KEY_USER_GENDER = "user_gender";
    private static final String KEY_USER_DOB = "user_dob";
    private static final String KEY_FIREBASE_TOKEN = "firebase_token";
    private static final String KEY_PRIMARY_CHANNEL_ID = "primary_channel_id";
    private static final String KEY_PRIMARY_CHANNEL_NAME = "primary_channel_name";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     * Logs in the user by saving user details and setting session
     *
     * @param mobile
     * @param name
     */
    public void loginUser(String mobile, String name , String user_id , String user_profile , String user_cover, String user_gender, String user_dob , String firebase_token) {
        mEditor.putString(KEY_MOBILE, mobile);
        mEditor.putString(KEY_NAME, name);
        mEditor.putString(KEY_USER_ID, user_id);
        mEditor.putString(KEY_USER_PROFILE, user_profile);
        mEditor.putString(KEY_USER_COVER, user_cover);
        mEditor.putString(KEY_USER_GENDER, user_gender);
        mEditor.putString(KEY_USER_DOB, user_dob);
        mEditor.putString(KEY_FIREBASE_TOKEN, firebase_token);
        Date date = new Date();

        //Set user session for next 7 days
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }

    public void saveChannelPrimary(String channel_id, String channel_name){
        mEditor.putString(KEY_PRIMARY_CHANNEL_ID, channel_id);
        mEditor.putString(KEY_PRIMARY_CHANNEL_NAME, channel_name);
        mEditor.commit();
    }

    public void loginUserUpdateProfile( String user_profile) {
        mEditor.putString(KEY_USER_PROFILE, user_profile);
        mEditor.commit();
    }

    public void loginUserAddCover( String user_cover) {
        mEditor.putString(KEY_USER_COVER, user_cover);
        mEditor.commit();
    }

    public void loginUserGenderUpdate( String user_gender) {
        mEditor.putString(KEY_USER_GENDER, user_gender);
        mEditor.commit();
    }

    public void loginUserDobUpdate( String user_dob) {
        mEditor.putString(KEY_USER_DOB, user_dob);
        mEditor.commit();
    }

    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        /* If shared preferences does not have a value
         then user is not logged in
         */
        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        /* Check if session is expired by comparing
        current date and Session expiry date
        */
        return currentDate.before(expiryDate);
    }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    public User getUserDetails() {
        //Check if user is logged in first
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setMobile(mPreferences.getString(KEY_MOBILE, KEY_EMPTY));
        user.setName(mPreferences.getString(KEY_NAME, KEY_EMPTY));
        user.setUser_id(mPreferences.getString(KEY_USER_ID, KEY_EMPTY));
        user.setUser_profile(mPreferences.getString(KEY_USER_PROFILE, KEY_EMPTY));
        user.setUser_cover(mPreferences.getString(KEY_USER_COVER, KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));
        user.setUser_gender(mPreferences.getString(KEY_USER_GENDER, KEY_EMPTY));
        user.setUser_dob(mPreferences.getString(KEY_USER_DOB, KEY_USER_DOB));
        user.setFirebase_token(mPreferences.getString(KEY_FIREBASE_TOKEN, KEY_EMPTY));
        user.setChannel_primary_id(mPreferences.getString(KEY_PRIMARY_CHANNEL_ID, KEY_EMPTY));
        user.setChannel_primary_name(mPreferences.getString(KEY_PRIMARY_CHANNEL_NAME, KEY_EMPTY));

        return user;
    }

    /*
     * Set primary channel
     */

    /*
     * Get primary channel
     */
    public User getPrimaryChannel(){
        User user = new User();
        user.setChannel_primary_id(mPreferences.getString(KEY_PRIMARY_CHANNEL_ID, KEY_EMPTY));
        user.setChannel_primary_name(mPreferences.getString(KEY_PRIMARY_CHANNEL_NAME, KEY_EMPTY));
        return user;
    }

    /**
     * Logs out user by clearing the session
     */
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }


}
