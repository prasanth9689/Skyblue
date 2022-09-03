package com.skyblue.skybluea.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.MyHttpEntity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;
import com.skyblue.skybluea.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NameActivity extends AppCompatActivity {
   private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_USER_ID = "user_id";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final String KEY_PREFE_PROFILE_URL = "profile_url";
    private static final String KEY_PREFE_USER_GENDER = "user_gender";
    private static final String KEY_PREFE_USER_DOB = "user_dob";

    private String name;
    private String spinnerDateHolder , spinnerMonthHolder , spinnerYearHolder , getDate;
    private String dateHolder , timeHolder, timeZoneHolder, dateTimeZoneHolder;
    private String firebaseTokenHolder;
    private String currentDateString , currentTimeString , currentTimeZoneString;
    private TextView   dateTextView , GenderIdTextView , Gender , TermsAndData;
    private TextView dateText , timeText , timeZoneText , firebaseToken;
    private EditText editTextName;
    Spinner spinnerDate , spinnerMonth , spinnerYear;
    RadioButton MaleRadioButton , FemaleRadioButton;
    RelativeLayout relativeLayoutPrivacy;
    Button register;
    private ProgressDialog progressDialog;
    Context context = this;
    private String GenderIdStringHolder , GenderStringHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_name);

        editTextName = findViewById(R.id.ideditTextName);

        register = findViewById(R.id.buttonNameNext3);
        spinnerDate =  findViewById(R.id.id_spinner_day);
        spinnerMonth =  findViewById(R.id.id_spinner_month);
        spinnerYear =  findViewById(R.id.id_spinner_year);
        dateTextView =  findViewById(R.id.id_date_view);
        MaleRadioButton = findViewById(R.id.radioButtonMale);
        FemaleRadioButton = findViewById(R.id.radioButtonFemale);
        GenderIdTextView = findViewById(R.id.id_gender_id);
        Gender = findViewById(R.id.id_gender_text_view);
        relativeLayoutPrivacy = findViewById(R.id.relativeLayoutPrivacy);
        TermsAndData = findViewById(R.id.text_two);
        dateText = findViewById(R.id.id_date_txt_view);
        timeText = findViewById(R.id.id_time_txt_view);
        timeZoneText = findViewById(R.id.id_time_zone_txt_view);
        firebaseToken = findViewById(R.id.firebase_token);

        currentDateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTimeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        currentTimeZoneString = new SimpleDateFormat("z", Locale.getDefault()).format(new Date());

        dateText.setText(currentDateString);
        timeText.setText(currentTimeString);
        timeZoneText.setText(currentTimeZoneString);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("TOKEN_UPDATE", Objects.requireNonNull(task.getResult()).getToken());
                            firebaseToken.setText(task.getResult().getToken());
                        }
                    }
                });

        relativeLayoutPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);

        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        MaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FemaleRadioButton.setChecked(false);
                GenderIdTextView.setText("1");
                Gender.setText("MALE");
            }
        });

        FemaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaleRadioButton.setChecked(false);
                int i = 0;
                int result = 0;
                GenderIdTextView.setText(String.valueOf(Integer.parseInt("1") ));
                Gender.setText("FEMALE");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                name = editTextName.getText().toString().trim();
                spinnerDateHolder = spinnerDate.getSelectedItem().toString().trim();
                spinnerMonthHolder = spinnerMonth.getSelectedItem().toString().trim();
                spinnerYearHolder = spinnerYear.getSelectedItem().toString().trim();

                dateTextView.setText(String.format("%s/%s/%s", spinnerDateHolder, spinnerMonthHolder, spinnerYearHolder));
                getDate = dateTextView.getText().toString().trim();

                GenderStringHolder = Gender.getText().toString().trim();
                GenderIdStringHolder = GenderIdTextView.toString().trim();

                dateHolder = dateText.getText().toString().trim();
                timeHolder = timeText.getText().toString().trim();
                timeZoneHolder = timeZoneText.getText().toString().trim();
                dateTimeZoneHolder = dateHolder +" "+ timeHolder +" "+ timeZoneHolder;

                firebaseTokenHolder = firebaseToken.getText().toString().trim();

                if (name.isEmpty() || name.length() < 2) {
                    editTextName.setError(getResources().getString(R.string.please_enter_name));
                    editTextName.requestFocus();
                    return;
                }
                if (!GenderIdTextView.getText().toString().matches(""))
                {
                    Signup();
                }else
                {
                   Utils.showMessage(context, getResources().getString(R.string.please_select_gender));
                }
            }
        });
    }

    private void Signup() {

        this.progressDialog = new ProgressDialog(NameActivity.this, R.style.AppCompatAlertDialogStyle);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getResources().getString(R.string.please_wait___));
        this.progressDialog.show();

        String nameHolder = editTextName.getText().toString().trim();
        String mobileHolder = getIntent().getStringExtra("phonenumber");
        String mobileNumberOnlyHolder = getIntent().getStringExtra("phonenumberonly");
        String countryNameHolder = getIntent().getStringExtra("countryname");
        String countryNameCodeHolder = getIntent().getStringExtra("countrynamecode");
        String phoneCodeHolder = getIntent().getStringExtra("phonecode");
        String passwordHolder =  getIntent().getStringExtra("passwordkey");

        String dobHolder =   String.format("%s/%s/%s", spinnerDateHolder, spinnerMonthHolder, spinnerYearHolder);
        String genderHolder = Gender.getText().toString();

        AndroidNetworking.post(AppConstants.REGISTER)
                .addBodyParameter("name", nameHolder)
                .addBodyParameter("mobile", mobileHolder)
                .addBodyParameter("password", passwordHolder)
                .addBodyParameter("mobile_number_only", mobileNumberOnlyHolder)
                .addBodyParameter("country", countryNameHolder)
                .addBodyParameter("country_name_code", countryNameCodeHolder)
                .addBodyParameter("phone_code", phoneCodeHolder)
                .addBodyParameter("dob", dobHolder)
                .addBodyParameter("gender", genderHolder)
                .addBodyParameter("date", dateHolder)
                .addBodyParameter("time", timeHolder)
                .addBodyParameter("time_zone", timeZoneHolder)
                .addBodyParameter("date_time_zone", dateTimeZoneHolder)
                .addBodyParameter("token", firebaseTokenHolder)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        Toast.makeText(NameActivity.this,"success",Toast.LENGTH_SHORT).show();

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.optString("status").equals("true"))
                            {
                                ArrayList<CurrentUser> CurrentUserArrayList = new ArrayList<>();

                                JSONArray jsonArrayst = jsonObject.getJSONArray("data");

                                for (int i = 0; i < jsonArrayst.length(); i++)
                                {
                                    CurrentUser CurrentUserDetails = new CurrentUser();
                                    JSONObject dataobjst = jsonArrayst.getJSONObject(i);

                                    CurrentUserDetails.setId(dataobjst.getString("id"));

                                    CurrentUserArrayList.add(CurrentUserDetails);
                                }
                                for (int j = 0; j < CurrentUserArrayList.size(); j++) {

                                    String user_id = CurrentUserArrayList.get(j).getId();
                                    String user_profile = "";
                                    String user_cover = "";

                                    session.loginUser(mobileHolder,nameHolder,user_id,user_profile,user_cover,genderHolder,dobHolder,firebaseTokenHolder);

//                                    SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sp.edit();
//
//                                    editor.putString(KEY_PREFE_USER_ID, CurrentUserArrayList.get(j).getId());
//                                    editor.putString(KEY_PREFE_USER_GENDER, genderHolder);
//                                    editor.putString(KEY_PREFE_USER_DOB, dobHolder);
//                                    editor.apply();

                                    progressDialog.dismiss();

                                    Intent i = new Intent(NameActivity.this, InitProfileActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                //      Toast.makeText(HomeHomeFragmentActivity.this.getActivity().getApplicationContext(), jsonObjectluser.optString("message") + "", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(NameActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class CurrentUser
    {
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

    }
}
