package com.skyblue.skybluea.activity.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.account.add_mobile.AddMobileNumberActivity;
import com.skyblue.skybluea.databinding.ActivityVerifyPhoneBinding;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    private ActivityVerifyPhoneBinding binding;
    private FirebaseAuth mAuth;
    private final Context mContext = this;
    private String verificationId;
    private ProgressDialog pDialog;
    private Dialog sendingOtpDialog;
    String getotp;

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
            Toast.makeText(mContext, "onVerificationFailed " + e.toString(), Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(mContext, "Invalid Request " + e, Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(mContext, "The SMS quota for the project has been exceeded " + e, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        String mobileNo = getIntent().getStringExtra("mobile");
        getotp = getIntent().getStringExtra("s");
        sendVerificationCode( mobileNo);
        setOnClickListener();
        initSendingOtpDialog();
        sendingOtpDialog.show();
    }

    private void initSendingOtpDialog() {
        sendingOtpDialog = new Dialog(mContext);
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
                        pDialog.dismiss();
                        Intent i = new Intent(mContext, PasswordActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
        pDialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage(getResources().getString(R.string.checking_otp_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}