package com.skyblue.skybluea.activity.registration2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.databinding.ActivityLogin2Binding;

public class LoginActivity2 extends AppCompatActivity {
    private ActivityLogin2Binding binding;
    private final Context context = this;
    private static final String TAG = "GoogleSignIn";
    SignInButton sibGoogleSignIn;
    CardView cvGoogleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private String mPhoneCode, mCountryName , mobileFullNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        binding.google.setOnClickListener(view -> signIn());
        binding.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMobile = binding.mobile.getText().toString().trim();

                if ("".equals(mMobile) || mMobile.isEmpty()){
                    binding.mobile.setError("Enter valid mobile no");
                    binding.mobile.requestFocus();
                    return;
                }
                mPhoneCode = binding.ccp.getSelectedCountryCodeWithPlus();
                mCountryName = binding.ccp.getSelectedCountryName();
                mobileFullNo =mPhoneCode + mMobile;

                Intent intent = new Intent(context, VerifyPhoneActivity2.class);
                intent.putExtra("mobile", mMobile);
                intent.putExtra("mobile_full", mobileFullNo);
                intent.putExtra("phone_code", mPhoneCode);
                intent.putExtra("country_name", mCountryName);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        Log.e(TAG, "updateUI : account " + account);
        if (account != null) {
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personToken = account.getIdToken();
            String personPhoto = account.getPhotoUrl().toString();

            String googleData =  " personId : " + personId  +"\n\n"+ " personName : " + personName  +"\n\n"+ " personGivenName : " + personGivenName  +"\n\n"+ " personFamilyName : " + personFamilyName  +"\n\n"+ " personEmail : " + personEmail  +"\n\n"+ " personToken : " + personToken  +"\n\n"+ " personPhoto : " + personPhoto;
            Log.e(TAG, "updateUI : data : account : " + googleData);

            Intent intent    =   new Intent(context, HomeActivity.class);
            intent.putExtra("googleData",googleData);
            intent.putExtra("googleProfileImage", personPhoto);
            startActivity(intent);
            signOut();

        } else {
            Log.e(TAG, "Login failed!");
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Google Sign out successfully");
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Revoke access successfully
                        Log.e(TAG, "Google Revoke access successfully");
                    }
                });
    }
}