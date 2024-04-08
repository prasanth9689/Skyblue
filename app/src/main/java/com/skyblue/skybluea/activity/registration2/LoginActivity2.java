package com.skyblue.skybluea.activity.registration2;

import static com.skyblue.skybluea.helper.Utils.showMessageInSnackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.model.Register;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity2 extends AppCompatActivity {
    private ActivityLogin2Binding binding;
    private SessionHandler session;
    private final Context context = this;
    private static final String TAG = "GoogleSignIn";
    SignInButton sibGoogleSignIn;
    CardView cvGoogleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private String mPhoneCode, mCountryName , mobileFullNo;
    private Dialog progressbar;
    private String personId, personName, personGivenName, personFamilyName, personEmail, personToken, personPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionHandler(getApplicationContext());

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
            personId = account.getId();
            personName = account.getDisplayName();
            personGivenName = account.getGivenName();
            personFamilyName = account.getFamilyName();
            personEmail = account.getEmail();
            personToken = account.getIdToken();
            personPhoto = account.getPhotoUrl().toString();

            String googleData =  " personId : " + personId  +"\n\n"+ " personName : " + personName  +"\n\n"+ " personGivenName : " + personGivenName  +"\n\n"+ " personFamilyName : " + personFamilyName  +"\n\n"+ " personEmail : " + personEmail  +"\n\n"+ " personToken : " + personToken  +"\n\n"+ " personPhoto : " + personPhoto;
            Log.e(TAG, "updateUI : data : account : " + googleData);

            checkAlreadyExists();

        } else {
            Log.e(TAG, "Login failed!");
        }
    }

    private void checkAlreadyExists() {
        progressbar = new Dialog(context);
        progressbar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressbar.setContentView(R.layout.m_loading_pls_wait);
        progressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressbar.setCancelable(false);
        progressbar.show();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<String> call = apiInterface.check_user_email(personId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressbar.dismiss();
                if (response.isSuccessful()){
                    if ("1".equals(response.body())){
                        /*
                           New save user details to server
                           and then local user session
                         */
                        Toast.makeText(context, "New user", Toast.LENGTH_SHORT).show();
                        Log.e("login_", "New user");
                          registerNewUser();
                    }else {

                        /*
                          Already_registered user
                          Save local user session
                         */
                        Toast.makeText(context, "Already registered user", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showMessageInSnackbar(context, getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressbar.dismiss();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerNewUser() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email_person_id", personId)
                .build();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Register>> call = apiInterface.registerEmailSign(requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Register>> call, @NonNull Response<List<Register>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        for (Register register: response.body()){
                            String userId = register.user_id;

                            if (userId != null && !userId.isEmpty()) {
                                session.loginUser("Email signed",personName,userId,"Email signed","Email signed","Email signed","","Email signed");
                                Intent intent = new Intent(context, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Register>> call, @NonNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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