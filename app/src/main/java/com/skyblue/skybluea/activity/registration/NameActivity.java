package com.skyblue.skybluea.activity.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity;
import com.skyblue.skybluea.activity.PrivacyPolicyActivity;
import com.skyblue.skybluea.databinding.ActivityNameBinding;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.model.Register;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NameActivity extends AppCompatActivity {
    private SessionHandler session;
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String PREFE_REG_MOBILE = "mobile";
    private static final String PREFE_REG_MOBILE_WITH_C_CODE = "mobile_cc";
    private static final String PREFE_REG_C_NAME = "country";
    private static final String PREFE_C_CODE = "country_code";
    private ActivityNameBinding binding;
    private final Context context = this;
    private String firebaseToken;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        session = new SessionHandler(getApplicationContext());

        generateFirebaseToken();
        initDOBArray();
        setOnClickListener();
    }

    private void setOnClickListener() {
        binding.radioBtnFemale.setOnClickListener(view -> {
            binding.radioBtnMale.setChecked(false);
            int i = 0;
            int result = 0;
            binding.genderId.setText(String.valueOf(Integer.parseInt("1") ));
            binding.genderText.setText(getString(R.string.female));
        });

        binding.radioBtnMale.setOnClickListener(view -> {
            binding.radioBtnFemale.setChecked(false);
            binding.genderText.setText("1");
            binding.genderText.setText(getString(R.string.male));
        });

        binding.privacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(NameActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        binding.registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.nameEd.getText().toString();
                if (name.isEmpty() || name.length() < 2) {
                    binding.nameEd.setError(getResources().getString(R.string.please_enter_name));
                    binding.nameEd.requestFocus();
                    return;
                }
                if (!binding.genderText.getText().toString().matches(""))
                {
                    Signup();
                }else
                {
                    showMessageInSnackbar(getResources().getString(R.string.please_select_gender));
                }
            }
        });
    }

    private void Signup() {
        showProgressBar();

        SharedPreferences sh = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String mobile = sh.getString(PREFE_REG_MOBILE, "");
        String mobileWithPlus = sh.getString(PREFE_REG_MOBILE_WITH_C_CODE, "");
        String country = sh.getString(PREFE_REG_C_NAME, "");
        String countryPhoneCode = sh.getString(PREFE_C_CODE, "");

        Log.e("mysh", "Mobile no " + mobile + "\n"
                + "Mobile no with plus " + mobileWithPlus + "\n"
                + "Country name " + country + "\n"
                + "Country code" + countryPhoneCode);

        String password = getIntent().getStringExtra("password");

        // get today date
       String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
       String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
       String currentTimeZone = new SimpleDateFormat("z", Locale.getDefault()).format(new Date());

        // get timezone
        String timeZone = currentDate +" "+ currentTime +" "+ currentTimeZone;

       // get signup user name
        String userName = binding.nameEd.getText().toString().trim();

        // get user date of birth
        String spinnerDate1 = binding.spinnerDate.getSelectedItem().toString().trim();
        String spinnerMonth1 = binding.spinnerMonth.getSelectedItem().toString().trim();
        String spinnerYear1 = binding.spinnerYear.getSelectedItem().toString().trim();

        // final full date of birth
        String dateDob = String.format("%s/%s/%s", spinnerDate1, spinnerMonth1, spinnerYear1);

        // for gender identification
        String genderName = binding.genderText.getText().toString().trim();
        String genderId = binding.genderId.toString().trim();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", userName)
                .addFormDataPart("mobile_with_plus", mobileWithPlus)
                .addFormDataPart("mobile", mobile)
                .addFormDataPart("password", password)
                .addFormDataPart("country", country)
                .addFormDataPart("phone_code", countryPhoneCode)
//                .addFormDataPart("country_name_code", country_name_code)
                .addFormDataPart("dob", dateDob)
                .addFormDataPart("gender", genderName)
                .addFormDataPart("date", currentDate)
                .addFormDataPart("time", currentTime)
                .addFormDataPart("time_zone", currentTimeZone)
                .addFormDataPart("date_time_zone", timeZone)
                .addFormDataPart("firebase_token", "test")
                .build();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Register>> call = apiInterface.register(requestBody);

        call.enqueue(new Callback<List<Register>>() {
            @Override
            public void onResponse(@NonNull Call<List<Register>> call, @NonNull Response<List<Register>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        for (Register register: response.body()){
                            String userId = register.user_id;

                            if (userId != null && !userId.isEmpty()) {
                                session.loginUser(mobile,userName,userId,"","",genderName,dateDob,firebaseToken);
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
                progressDialog.dismiss();
                Toast.makeText(NameActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressBar() {
        this.progressDialog = new ProgressDialog(NameActivity.this, R.style.AppCompatAlertDialogStyle);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getResources().getString(R.string.please_wait___));
        this.progressDialog.show();
    }

    private void initDOBArray() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDate.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMonth.setAdapter(adapterMonth);

        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYear.setAdapter(adapterYear);
    }

    private void generateFirebaseToken() {
//        FirebaseInstanceId.getInstance()
//                .getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        firebaseToken = task.getResult().getToken();
//                        Log.e("firebase_token", firebaseToken);
//                    }
//                });
    }

    private void showMessageInSnackbar(String message)
    {
        Snackbar snack = Snackbar.make((((Activity) context).findViewById(android.R.id.content)), message, Snackbar.LENGTH_SHORT);
        snack.setDuration(Snackbar.LENGTH_SHORT);
        //snack.setAction(actionButton, new View.OnClickListener());//add your own listener
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