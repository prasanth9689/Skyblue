package com.skyblue.skybluea.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.registration.RegisterActivity;
import com.skyblue.skybluea.databinding.ActivityLoginBinding;
import com.skyblue.skybluea.helper.CheckNetwork;
import com.skyblue.skybluea.helper.GlobalVariables;
import com.skyblue.skybluea.helper.Utils;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.model.Login;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private SessionHandler session;
    Context context = this;
    String mobile, password, firebase_token;
    private APIInterface apiInterface;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CheckNetwork network = new CheckNetwork(getApplicationContext());
        network.registerNetworkCallback();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHome();
        }

        apiInterface = APIClient.getClient().create(APIInterface.class);

//        FirebaseInstanceId.getInstance()
//                .getInstanceId()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful())
//                    {
//                        Log.d("TOKEN_UPDATE",task.getResult().getToken());
//                        firebase_token =  task.getResult().getToken();
//                    }
//                });

        binding.login.setOnClickListener(view1 -> {
            if (validateInputs()) {

                if (GlobalVariables.isNetworkConnected){
                    // call retrofit api
                    login();
                    Log.e("home_", "Internet connected");
                }else{
                    Log.e("home_", "Internet not connected");
                    Utils.showMessageInSnackbar(context, getString(R.string.check_internet_connection));
                }
            }
        });

        binding.newAccount.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        });
        binding.password.setHint(getString(R.string.enter_password));
    }

    private void login() {
        loginProgressBar();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mobile", mobile)
                .addFormDataPart("password", password)
                .addFormDataPart("token", String.valueOf(firebase_token))
                .build();

        Call<List<Login>> call=apiInterface.login(requestBody);
        call.enqueue(new Callback<List<Login>>() {
            @Override
            public void onResponse(@NonNull Call<List<Login>> call, @NonNull Response<List<Login>> response) {
                if (response.code() == 200){
                    assert response.body() != null;
                    for(Login login: response.body()) {
                        pDialog.dismiss();
                        switch (Integer.parseInt(login.getMessage())) {

                            case 1:
                                showMessageInSnackbar(getString(R.string.check_username_and_password));
                                break;

                            case 2:
                                showMessageInSnackbar(getString(R.string.account_not_found));
                                break;

                            case 3:
                                showMessageInSnackbar(getString(R.string.please_enter_empty_field));
                                break;

                            case 4:
                                showMessageInSnackbar(getString(R.string.success));
                                session.loginUser(mobile,
                                        login.getUser_name(),
                                        login.getUser_id(),
                                        login.getProfile_image(),
                                        login.getCover_image(),
                                        login.getGender(),
                                        login.getDob(),
                                        login.getFirebase_token());
                                loadHome();
                                break;

                            case 5:
                                showMessageInSnackbar(getString(R.string.firebase_token_updated));
                                break;

                            case 6:
                                showMessageInSnackbar(getString(R.string.server_error_db));
                                break;

                            default:
                                Utils.showMessageInSnackbar(context, getString(R.string.server_error));
                                break;
                        }
                    }
                }else {
                    pDialog.dismiss();
                    Utils.showMessageInSnackbar(context, getString(R.string.failed));
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Login>> call, @NonNull Throwable t) {
                Log.e("login", String.valueOf(t));
                Utils.showMessageInSnackbar(context, getString(R.string.failed));
                pDialog.dismiss();
            }
        });
    }

    private void loadHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loginProgressBar() {
        pDialog = new ProgressDialog(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.logging_in_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * Validates inputs and shows error if any
     */
    private boolean validateInputs() {
         mobile = binding.mobile.getText().toString();
         password = Objects.requireNonNull(binding.password.getText()).toString();
        if("".equals(mobile)){
            binding.mobile.setError(getResources().getString(R.string.phone_cannot_be_empty));
            binding.mobile.requestFocus();
            return false;
        }
        if("".equals(password)){
            binding.password.setError(getResources().getString(R.string.password_cannot_be_empty));
            binding.password.requestFocus();
            return false;
        }
        return true;
    }

    private void showMessageInSnackbar(String message)
    {
        Snackbar snack = Snackbar.make((((Activity) context).findViewById(android.R.id.content)), message, Snackbar.LENGTH_SHORT);
        snack.setDuration(Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        TextView tvAction = view.findViewById(com.google.android.material.R.id.snackbar_action);
        tvAction.setTextSize(16);
        tvAction.setTextColor(Color.WHITE);
        tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBlue));
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBlue));
        snack.show();
    }
}