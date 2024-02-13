package com.skyblue.skybluea.activity.registration;

import static com.skyblue.skybluea.helper.Utils.showMessageInSnackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.skyblue.skybluea.R;

import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.databinding.ActivityRegisterBinding;
import com.skyblue.skybluea.helper.CheckNetwork;
import com.skyblue.skybluea.helper.GlobalVariables;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private SessionHandler session;
    private ActivityRegisterBinding binding;
    private Context context = this;
    private Dialog progressbar;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String PREFE_REG_MOBILE = "mobile";
    private static final String PREFE_REG_MOBILE_WITH_C_CODE = "mobile_cc";
    private static final String PREFE_REG_C_NAME = "country";
    private static final String PREFE_C_CODE = "country_code";
    APIInterface apiInterface;
    private String mobileNo , countryCodeWithPlus , countryName , mobileFullNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.mobileNo.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        CheckNetwork network = new CheckNetwork(getApplicationContext());
        network.registerNetworkCallback();

        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHome();
        }

        initProgressBar();
        initOnClickListener();
    }

    private void initOnClickListener() {
        binding.buttonContinue.setOnClickListener(view -> {
            mobileNo = binding.mobileNo.getText().toString();
            if (mobileNo.isEmpty() || mobileNo.length() < 10) {
                binding.mobileNo.setError(getString(R.string.check_mobile_no));
                binding.mobileNo.requestFocus();
                return;
            }
            if (GlobalVariables.isNetworkConnected){
                checkUser();
            }else{
                binding.error.setText(getString(R.string.check_internet_connection));
            }
        });

        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void checkUser() {
        progressbar.show();
        countryCodeWithPlus = binding.ccp.getSelectedCountryCodeWithPlus();
         countryName = binding.ccp.getSelectedCountryName();
         mobileFullNo =countryCodeWithPlus + mobileNo;
        Log.e("ccp_", "Mobile no " + mobileNo + "Country code " + countryCodeWithPlus +"Country name " + countryName + "Mobile no full " + mobileFullNo);
        //newUser();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<String> call = apiInterface.check_user(mobileFullNo);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
               progressbar.dismiss();
                if (response.isSuccessful()){
                    if ("1".equals(response.body())){
                        newUser();
                    }else {
                        showMessageInSnackbar(context, getResources().getString(R.string.already_registered_this_number_please_login));
                    }
                } else {
                    showMessageInSnackbar(context, getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("retrofit", String.valueOf(t));
                progressbar.dismiss();
            }
        });
    }

    private void newUser() {
        setSharedPreference();
        Intent intent = new Intent(context, VerifyPhoneActivity.class);
        intent.putExtra("mobile", mobileFullNo);
        startActivity(intent);
    }

    private void setSharedPreference() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFE_REG_MOBILE, mobileNo);
        editor.putString(PREFE_REG_MOBILE_WITH_C_CODE, mobileFullNo);
        editor.putString(PREFE_REG_C_NAME, countryName);
        editor.putString(PREFE_C_CODE, countryCodeWithPlus);
        editor.apply();
    }

    private void initProgressBar() {
        progressbar = new Dialog(context);
        progressbar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressbar.setContentView(R.layout.m_loading_pls_wait);
        progressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressbar.setCancelable(false);
    }

    private void loadHome() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

        }
    }
}