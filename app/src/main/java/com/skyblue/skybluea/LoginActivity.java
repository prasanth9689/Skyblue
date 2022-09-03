package com.skyblue.skybluea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.skyblue.skybluea.account.AccountUpdateCoverActivity;
import com.skyblue.skybluea.account.AccountUpdateProfileActivity;
import com.skyblue.skybluea.channel.PageAdminUpdateCoverActivity;
import com.skyblue.skybluea.channel.PageAdminUpdateLogoActivity;
import com.skyblue.skybluea.forget_password.ForgetPasActivity;
import com.skyblue.skybluea.registration.InitImageViewActivity;
import com.skyblue.skybluea.registration.InitProfileActivity;
import com.skyblue.skybluea.registration.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";
    private EditText etMobile;
    private EditText etPassword;
    private String mobile;
    private String password;
    private static final String name = "skyblue";
    private ProgressDialog pDialog;

    private SessionHandler session;
    TextView ForgetPassword;
    TextView tokenTxt;
    String tokenHolder;
    private ProgressDialog progressDialog;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHome();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        Button btnCreateAc = findViewById(R.id.btnCreateAc);
        Button btnLogin = findViewById(R.id.btLogin);
        ForgetPassword = findViewById(R.id.id_forget_text);
        tokenTxt = findViewById(R.id.firebase_token2);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("TOKEN_UPDATE",task.getResult().getToken());
                            tokenTxt.setText(task.getResult().getToken());
                        }
                    }
                });

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPasActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retrieve the data entered in the edit texts
                mobile = etMobile.getText().toString().toLowerCase().trim();
                password = etPassword.getText().toString().trim();
                tokenHolder = tokenTxt.getText().toString().trim();
                if (validateInputs()) {
                    Utils.hideKeyboard(context);
                    login();
                }
            }
        });
        btnCreateAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadHome() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.logging_in_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void displayLoaderRegisterToken() {
        pDialog = new ProgressDialog(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.logging_success_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void login() {
        displayLoader();

        AndroidNetworking.post(AppConstants.LOGIN)
                .addBodyParameter("mobile", mobile)
                .addBodyParameter("password", password)
                .addBodyParameter("token", tokenHolder)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
 //  Utils.showMessage(context, response);
//-----------------------------------------------------------------------------------------
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.optString("status").equals("true"))
                            {
                              //  Utils.showMessage(context, "Step 1");
                                ArrayList<CurrentUser> CurrentUserArrayList = new ArrayList<>();

                                JSONArray jsonArrayst = jsonObject.getJSONArray("data");

                                for (int i = 0; i < jsonArrayst.length(); i++)
                                {
                                    CurrentUser CurrentUserDetails = new CurrentUser();
                                    JSONObject dataobjst = jsonArrayst.getJSONObject(i);

                                    CurrentUserDetails.setId(dataobjst.getString("id"));
                                    CurrentUserDetails.setName(dataobjst.getString("name"));
                                    CurrentUserDetails.setProfile_url(dataobjst.getString("profile_url"));
                                    CurrentUserDetails.setCover_picture_url(dataobjst.getString("cover_url"));
                                    CurrentUserDetails.setToken(dataobjst.getString("token"));
                                    CurrentUserDetails.setMessage(dataobjst.getString("message"));
                                    CurrentUserDetails.setUser_gender(dataobjst.getString("gender"));
                                    CurrentUserDetails.setUser_dob(dataobjst.getString("dob"));

                                    CurrentUserArrayList.add(CurrentUserDetails);
                                }
                                for (int j = 0; j < CurrentUserArrayList.size(); j++) {

                                 //   Utils.showMessage(context, String.valueOf(CurrentUserArrayList.get(j).getMessage()));

                                    switch (Integer.parseInt(CurrentUserArrayList.get(j).getMessage())) {

                                        case 1:
                                            Utils.showMessage(context, "Check Username And Password!");
                                            break;

                                        case 2:
                                            Utils.showMessage(context, "Account not found!");
                                            break;

                                        case 3:
                                            Utils.showMessageInSnackbar(context, "Please Enter Empty Field");
                                            break;

                                        case 4:

                                            Utils.showMessageInSnackbar(context, "success");

                                            String userId = CurrentUserArrayList.get(j).getId();
                                    String userName = CurrentUserArrayList.get(j).getName();
                                    String userProfile = CurrentUserArrayList.get(j).getProfile_url();
                                    String userCover = CurrentUserArrayList.get(j).getCover_picture_url();
                                    String userGender = CurrentUserArrayList.get(j).getUser_gender();
                                    String userDob = CurrentUserArrayList.get(j).getUser_dob();

                                 //   Utils.showMessage(context, userNameHolder);
                                    Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();

                                    session.loginUser(mobile,userName,userId,userProfile,userCover,userGender,userDob,tokenHolder);

//                                    SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sp.edit();
//
//                                    editor.putString(KEY_PREFE_USER_ID, CurrentUserArrayList.get(j).getId());
//                                    editor.putString(KEY_PREFE_USER_NAME, CurrentUserArrayList.get(j).getName());
//                                    editor.putString(KEY_PREFE_USER_PROFILE, CurrentUserArrayList.get(j).getProfile_url());
//                                    editor.putString(KEY_PREFE_USER_COVER, CurrentUserArrayList.get(j).getCover_picture_url());
//                                    editor.apply();

                                   // Utils.showMessage(context, userIdHolder + userNameHolder + userProfileUrlHolder + userCoverUrlHolder + userDeviceToken);

                                            Intent intent = new Intent(context, Home.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            break;

                                        default:
                                            Utils.showMessageInSnackbar(context, "SERVER ERROR!");
                                            break;
                                    }
//                                    String userIdHolder = CurrentUserArrayList.get(j).getId();
//                                    String userNameHolder = CurrentUserArrayList.get(j).getName();
//                                    String userProfileUrlHolder = CurrentUserArrayList.get(j).getProfile_url();
//                                    String userCoverUrlHolder = CurrentUserArrayList.get(j).getCover_picture_url();
//                                    String userDeviceToken = CurrentUserArrayList.get(j).getToken();
//
//                                    Utils.showMessage(context, userNameHolder);
//                                    Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
//
//                                    session.loginUser(mobile,userNameHolder);
//
//                                    SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sp.edit();
//
//                                    editor.putString(KEY_PREFE_USER_ID, CurrentUserArrayList.get(j).getId());
//                                    editor.putString(KEY_PREFE_USER_NAME, CurrentUserArrayList.get(j).getName());
//                                    editor.putString(KEY_PREFE_USER_PROFILE, CurrentUserArrayList.get(j).getProfile_url());
//                                    editor.putString(KEY_PREFE_USER_COVER, CurrentUserArrayList.get(j).getCover_picture_url());
//                                    editor.apply();
//
//                                    Utils.showMessage(context, userIdHolder + userNameHolder + userProfileUrlHolder + userCoverUrlHolder + userDeviceToken);

                                    pDialog.dismiss();

                                }
                            } else {
                                //      Toast.makeText(HomeHomeFragmentActivity.this.getActivity().getApplicationContext(), jsonObjectluser.optString("message") + "", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//-----------------------------------------------------------------------------------------------------------------------------
                    }
                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                    }
                });


//        JSONObject request = new JSONObject();
//        try {
//            //Populate the request parameters
//            request.put(KEY_MOBILE, mobile);
//            request.put(KEY_PASSWORD, password);
//            request.put(KEY_NAME, name);
//            request.put("token", tokenHolder);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
//                (Request.Method.POST, AppConstants.LOGIN, request, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        pDialog.dismiss();
//                        try {
//                            //Check if user got logged in successfully
//                            if (response.getInt(KEY_STATUS) == 0) {
//
//                                session.loginUser(mobile,name);
//                                Utils.showMessage(context, response.getString("name"));
//                            }else{
//                              //  Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
//                            Utils.showMessageInSnackbar(context, response.getString(KEY_MESSAGE));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pDialog.dismiss();
//
//                        //Display error message whenever an error occurs
//                        Toast.makeText(getApplicationContext(),
//                                error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        // Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(mobile)){
            etMobile.setError(getResources().getString(R.string.phone_cannot_be_empty));
            etMobile.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            etPassword.setError(getResources().getString(R.string.password_cannot_be_empty));
            etPassword.requestFocus();
            return false;
        }
        return true;
    }



    public static class CurrentUser
    {
        private String id, name , profile_url , cover_picture_url , user_gender , user_dob ,  token;
        private String message;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getProfile_url(){return profile_url;}

        public String getCover_picture_url() {
            return cover_picture_url;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        public String getUser_gender() {
            return user_gender;
        }

        public String getUser_dob() {
            return user_dob;
        }

        public void setUser_gender(String user_gender) {
            this.user_gender = user_gender;
        }

        public void setUser_dob(String user_dob) {
            this.user_dob = user_dob;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setProfile_url(String profile_url){this.profile_url = profile_url;}

        public void setCover_picture_url(String cover_picture_url) {
            this.cover_picture_url = cover_picture_url;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }


}