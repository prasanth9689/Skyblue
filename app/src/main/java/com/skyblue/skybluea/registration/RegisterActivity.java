package com.skyblue.skybluea.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.skyblue.skybluea.AppConstants;
import com.skyblue.skybluea.MainActivity;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.skyblue.skybluea.Utils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private SessionHandler session;

    Button buttonContinue;
    TextView textViewCountryCode, textViewCountryName,textViewCountryNameCode,TextViewNumberWithPlus, TextViewNumberOnly;
    EditText editTextCarrierNumber;
    String mobileNumberHolder;
    TextView TextViewMobileNumberStatus;
    TextView CheckUserExpectationTextView;
    CountryCodePicker ccp;

    // production = correct status code 1    (please change when production time)
    private String TempStatus = "1";
    Context context = this;

    private Dialog progressbarLoadingPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHome();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        buttonContinue = findViewById(R.id.buttonContinue);
        editTextCarrierNumber = (EditText) findViewById(R.id.editTextCarrierNumber);

        textViewCountryCode = findViewById(R.id.textView_countryCode);
        textViewCountryName = findViewById(R.id.textView_countryName);
        textViewCountryNameCode = findViewById(R.id.textView_countryNameCode);
        TextViewNumberWithPlus = findViewById(R.id.id_get_number_with_plus);
        TextViewNumberOnly = findViewById(R.id.id_get_number_only);
        TextViewMobileNumberStatus = findViewById(R.id.mobileStatus);
        CheckUserExpectationTextView = findViewById(R.id.check_user_expectation);

        initCustomProgressBarLoading();

        editTextCarrierNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserExpectationTextView.setText("");
            }
        });
        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               progressbarLoadingPleaseWait.show();

                final String StringEditTextCarrierNumberWithPlus =ccp.getSelectedCountryCodeWithPlus()+editTextCarrierNumber.getText().toString();
                final String StringTextViewNumberOnly =editTextCarrierNumber.getText().toString();

                if (StringEditTextCarrierNumberWithPlus.isEmpty() || StringEditTextCarrierNumberWithPlus.length() < 10) {
                    editTextCarrierNumber.setError(getResources().getString(R.string.valid_number_is_required));
                    editTextCarrierNumber.requestFocus();
                    return;
                }

                  final String phoneNumber = StringEditTextCarrierNumberWithPlus;

                mobileNumberHolder = editTextCarrierNumber.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.CHECK_USER_ALREADY_EXISTS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {

                                TextViewMobileNumberStatus.setText(ServerResponse);

                                final String GetStatusId = TextViewMobileNumberStatus.getText().toString().trim();

                                if (GetStatusId.equals(TempStatus))
                                {
                                    progressbarLoadingPleaseWait.dismiss();
                                    NewUserNext();
                                }else
                                {
                                    progressbarLoadingPleaseWait.dismiss();
                                    String message = getResources().getString(R.string.already_registered_this_number_try_new_number);
                                    Utils.showMessage(RegisterActivity.this, message);
                                   // CheckUserExpectationTextView.setText(getResources().getString(R.string.already_registered_this_number_try_new_number));
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                // Showing error message if something goes wrong.
                                Toast.makeText(RegisterActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("mobile", phoneNumber);
                        return params;
                    }
                };
                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    private void initCustomProgressBarLoading() {
        progressbarLoadingPleaseWait = new Dialog(context);
        progressbarLoadingPleaseWait.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressbarLoadingPleaseWait.setContentView(R.layout.model_dialog_progress_loading_please_wait);
        progressbarLoadingPleaseWait.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressbarLoadingPleaseWait.setCancelable(false);
    }

    private void NewUserNext() {
        final String StringEditTextCarrierNumberWithPlus =ccp.getSelectedCountryCodeWithPlus()+editTextCarrierNumber.getText().toString();
        final String StringTextViewNumberOnly =editTextCarrierNumber.getText().toString();

        if (StringEditTextCarrierNumberWithPlus.isEmpty() || StringEditTextCarrierNumberWithPlus.length() < 10) {
            editTextCarrierNumber.setError("Valid number is required");
            editTextCarrierNumber.requestFocus();
            return;
        }

        String phoneNumber = StringEditTextCarrierNumberWithPlus;
        Intent intent = new Intent(RegisterActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", phoneNumber);
        intent.putExtra("phonenumberonly", StringTextViewNumberOnly);
        intent.putExtra("countryname",ccp.getSelectedCountryName());
        intent.putExtra("countrynamecode",ccp.getSelectedCountryNameCode());
        intent.putExtra("phonecode",ccp.getSelectedCountryCodeWithPlus());
        startActivity(intent);
    }

    private void loadHome() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
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