package com.skyblue.skybluea.account;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AccountProfileEditActivity extends AppCompatActivity {
    private SessionHandler session;
    Button dobSaveButton;
    TextView GenderEditButton;
    TextView DobEditButton;
    TextView textPrompt2;
    TextView dateTextView;
    TextView genderText , dobText , mobileText;
    TextView MobileProtected;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetViewDOB;
    BottomSheetDialog bottomSheetDialogDOB;
    RadioButton MaleRadioButton , FemaleRadioButton;
    Spinner spinnerDate , spinnerMonth , spinnerYear;
    String spinnerDateHolder , spinnerMonthHolder , spinnerYearHolder;
    ImageView id_back_arrow_ucp;
    Context context = this;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile_edit);

        GenderEditButton = findViewById(R.id.id_iVEdit_gender);
        DobEditButton = findViewById(R.id.id_iVEdit_dob);
        genderText = findViewById(R.id.id_gender_text);
        dobText = findViewById(R.id.id_dob_text);
        mobileText = findViewById(R.id.id_mobile_text);
        MobileProtected = findViewById(R.id.id_mobile_hidden_star);
        id_back_arrow_ucp = findViewById(R.id.id_back);

        GenderEditButton.setVisibility(View.VISIBLE);
        DobEditButton.setVisibility(View.VISIBLE);

        initGenderBottomSheet();
        initDobBottomSheet();

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        mobileText.setText(user.getMobile());

        GenderEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();

            }
        });

        DobEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogDOB.show();
            }
        });

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
                new BottomSheetBehavior.BottomSheetCallback(){
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        switch (newState){
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                textPrompt2.setText("COLLAPSED");
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                textPrompt2.setText("DRAGGING");
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                textPrompt2.setText("EXPANDED");
                                break;
                            case BottomSheetBehavior.STATE_HIDDEN:
                                textPrompt2.setText("HIDDEN");
                                bottomSheetDialog.dismiss();
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                textPrompt2.setText("SETTLING");
                                break;
                            default:
                                textPrompt2.setText("unknown...");
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                };
    }

    private void initDobBottomSheet() {
        bottomSheetViewDOB = getLayoutInflater().inflate(R.layout.bottomsheetdialog_layout_dob, null);
        bottomSheetDialogDOB = new BottomSheetDialog(AccountProfileEditActivity.this);
        bottomSheetDialogDOB.setContentView(bottomSheetViewDOB);

        dobSaveButton = bottomSheetViewDOB.findViewById(R.id.id_save_dob_button);
        spinnerDate = bottomSheetViewDOB.findViewById(R.id.id_spinner_day);
        spinnerMonth = bottomSheetViewDOB.findViewById(R.id.id_spinner_month);
        spinnerYear = bottomSheetViewDOB.findViewById(R.id.id_spinner_year);
        dateTextView = bottomSheetViewDOB.findViewById(R.id.id_date_view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);

        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.year_array_dob, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        dobSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerDateHolder = spinnerDate.getSelectedItem().toString().trim();
                spinnerMonthHolder = spinnerMonth.getSelectedItem().toString().trim();
                spinnerYearHolder = spinnerYear.getSelectedItem().toString().trim();

                dateTextView.setText(spinnerDateHolder + "/" + spinnerMonthHolder +"/"+spinnerYearHolder);

                String getDate = dateTextView.getText().toString().trim();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.DOB_UPDATE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {
                                dobText.setText(getDate);

                                session.loginUserDobUpdate(getDate);

                                bottomSheetDialogDOB.dismiss();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                // Showing error message if something goes wrong.
                                Toast.makeText(AccountProfileEditActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("user_id", user.getUser_id());
                        params.put("dob", getDate);

                        return params;
                    }
                };
                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(AccountProfileEditActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    private void initGenderBottomSheet() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetdialog_layout_male_female, null);
        bottomSheetDialog = new BottomSheetDialog(AccountProfileEditActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        MaleRadioButton = bottomSheetView.findViewById(R.id.radioButtonMale);
        FemaleRadioButton = bottomSheetView.findViewById(R.id.radioButtonFemale);

        MaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FemaleRadioButton.setVisibility(INVISIBLE);

                String male_id = "1";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.GENDER_UPDATE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {
                                genderText.setText("MALE");
                                MaleRadioButton.setChecked(true);
                                FemaleRadioButton.setChecked(false);
                                FemaleRadioButton.setVisibility(View.VISIBLE);

                                bottomSheetDialog.dismiss();

                                session.loginUserGenderUpdate("MALE");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                // Showing error message if something goes wrong.
                                Toast.makeText(AccountProfileEditActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("user_id", user.getUser_id());
                        params.put("male_id", male_id);

                        return params;
                    }
                };

                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(AccountProfileEditActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }
        });
        FemaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaleRadioButton.setVisibility(INVISIBLE);

                String female_id = "0";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.GENDER_UPDATE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {
                                genderText.setText("FEMALE");
                                FemaleRadioButton.setChecked(true);
                                MaleRadioButton.setChecked(false);
                                MaleRadioButton.setVisibility(View.VISIBLE);

                                bottomSheetDialog.dismiss();

                                session.loginUserGenderUpdate("FEMALE");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                // Showing error message if something goes wrong.
                                Toast.makeText(AccountProfileEditActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("user_id", user.getUser_id());
                        params.put("female_id", female_id);

                        return params;
                    }
                };

                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(AccountProfileEditActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        genderText.setText(user.getUser_gender());
        dobText.setText(user.getUser_dob());
    }

}

