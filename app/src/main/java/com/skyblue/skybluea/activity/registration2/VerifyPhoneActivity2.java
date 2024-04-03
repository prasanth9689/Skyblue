package com.skyblue.skybluea.activity.registration2;

import static com.skyblue.skybluea.helper.Utils.showMessageInSnackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.LoginActivity;
import com.skyblue.skybluea.activity.registration.PasswordActivity;
import com.skyblue.skybluea.activity.registration.VerifyPhoneActivity;
import com.skyblue.skybluea.databinding.ActivityVerifyPhone2Binding;
import com.skyblue.skybluea.databinding.ActivityVerifyPhoneBinding;
import com.skyblue.skybluea.helper.Utils;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.model.Login;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity2 extends AppCompatActivity {
    private ActivityVerifyPhone2Binding binding;
    private FirebaseAuth mAuth;
    private final Context context = this;
    private String verificationId;
    private ProgressDialog pDialog;
    private Dialog sendingOtpDialog;
    String getotp , firebase_token;
    String mobileNo;
    private Dialog progressbar;
    APIInterface apiInterface;
    private SessionHandler session;
    int time=60;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            // super.onCodeSent(s, forceResendingToken);
            if (s != null){
                Log.e("otp_", "success");
                sendingOtpDialog.dismiss();
                verificationId = s;
            }
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                binding.editTextCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(context, "onVerificationFailed " + e.toString(), Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, "Invalid Request " + e, Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(context, "The SMS quota for the project has been exceeded " + e, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhone2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.textTimer.setText("0:"+checkDigit(time));
                time--;
            }

            public void onFinish() {
           //    binding.textTimer.setText("try again");
                binding.textTimer.setVisibility(View.GONE);
                binding.reSend.setVisibility(View.VISIBLE);
            }

        }.start();

        mobileNo = getIntent().getStringExtra("mobile");
        getotp = getIntent().getStringExtra("s");
        sendVerificationCode( mobileNo);
        setOnClickListener();
        initSendingOtpDialog();
        sendingOtpDialog.show();

        initProgressBar();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void initProgressBar() {
        progressbar = new Dialog(context);
        progressbar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressbar.setContentView(R.layout.m_loading_pls_wait);
        progressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressbar.setCancelable(false);
    }

    private void initSendingOtpDialog() {
        sendingOtpDialog = new Dialog(context);
        sendingOtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sendingOtpDialog.setContentView(R.layout.m_send_otp_wait);
        Objects.requireNonNull(sendingOtpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sendingOtpDialog.setCancelable(false);
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        displayLoader();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUser();
                        /*
                        pDialog.dismiss();
                        Intent i = new Intent(mContext, NameActivity2.class);
                        startActivity(i);
                        finish();
                         */
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUser() {
        progressbar.show();

        //newUser();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<String> call = apiInterface.check_user(mobileNo);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressbar.dismiss();
                if (response.isSuccessful()){
                    if ("1".equals(response.body())){
                        newUser();
                    }else {
                       // showMessageInSnackbar(context, getResources().getString(R.string.already_registered_this_number_please_login));
                        saveSignInSession();
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

    private void saveSignInSession() {
            loginProgressBar();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mobile", mobileNo)
                    .addFormDataPart("token", String.valueOf(firebase_token))
                    .build();

            Call<List<Login>> call=apiInterface.login2(requestBody);
            call.enqueue(new Callback<List<Login>>() {
                @Override
                public void onResponse(@NonNull Call<List<Login>> call, @NonNull Response<List<Login>> response) {
                    if (response.code() == 200){
                        assert response.body() != null;
                        for(Login login: response.body()) {
                            pDialog.dismiss();
                            switch (Integer.parseInt(login.getMessage())) {

                                case 1:
                                    showMessageInSnackbar(context, getString(R.string.check_username_and_password));
                                    break;

                                case 2:
                                    showMessageInSnackbar(context, getString(R.string.account_not_found));
                                    break;

                                case 3:
                                    showMessageInSnackbar(context, getString(R.string.please_enter_empty_field));
                                    break;

                                case 4:
                                    showMessageInSnackbar(context, getString(R.string.success));
                                    session.loginUser(mobileNo,
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
                                    showMessageInSnackbar(context, getString(R.string.firebase_token_updated));
                                    break;

                                case 6:
                                    showMessageInSnackbar(context, getString(R.string.server_error_db));
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
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }


    private void loginProgressBar() {
        pDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage("Sign in please wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void newUser() {
        Intent intent = new Intent(context, NameActivity2.class);
        intent.putExtra("mobile", mobileNo);
        startActivity(intent);
    }

    private void setOnClickListener() {
        binding.verify.setOnClickListener(v -> {
            String code = binding.editTextCode.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                binding.editTextCode.setError(getResources().getString(R.string.enter_code));
                binding.editTextCode.requestFocus();
                return;
            }
            verifyCode(code);

        });
        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.checking_otp_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}