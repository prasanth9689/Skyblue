package com.skyblue.skybluea.forget_password;

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
import android.widget.ImageView;
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
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.Utils;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasActivity extends AppCompatActivity {

    final Context context = this;
    Button buttonContinue;
    TextView textViewCountryCode, textViewCountryName,textViewCountryNameCode,TextViewNumberWithPlus, TextViewNumberOnly;
    EditText editTextCarrierNumber;
    String mobileNumberHolder;
    TextView TextViewMobileNumberStatus;
    CountryCodePicker ccp;
    private String TempStatus = "1";
    ImageView BackBtn;

    private Dialog progressbarLoadingPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_pas);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        buttonContinue = findViewById(R.id.buttonContinue);
        editTextCarrierNumber = (EditText) findViewById(R.id.editTextCarrierNumber);
        textViewCountryCode = findViewById(R.id.textView_countryCode);
        textViewCountryName = findViewById(R.id.textView_countryName);
        textViewCountryNameCode = findViewById(R.id.textView_countryNameCode);
        TextViewNumberWithPlus = findViewById(R.id.id_get_number_with_plus);
        TextViewNumberOnly = findViewById(R.id.id_get_number_only);
        TextViewMobileNumberStatus = findViewById(R.id.mobileStatus);
        BackBtn = findViewById(R.id.backBtn_forget_pass);

        initCustomProgressBarLoading();

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String StringEditTextCarrierNumberWithPlus =ccp.getSelectedCountryCodeWithPlus()+editTextCarrierNumber.getText().toString();
                final String StringTextViewNumberOnly =editTextCarrierNumber.getText().toString();

                if (StringEditTextCarrierNumberWithPlus.isEmpty() || StringEditTextCarrierNumberWithPlus.length() < 10) {
                    editTextCarrierNumber.setError(getResources().getString(R.string.valid_number_is_required));
                    editTextCarrierNumber.requestFocus();
                    return;
                }

                progressbarLoadingPleaseWait.show();

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
                                    Utils.showMessage(context, getResources().getString(R.string.account_not_found));
                                }else
                                {
                                    progressbarLoadingPleaseWait.dismiss();
                                    NewUserNext();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                // Showing error message if something goes wrong.
                                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
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
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    private void NewUserNext() {
        final String StringEditTextCarrierNumberWithPlus =ccp.getSelectedCountryCodeWithPlus()+editTextCarrierNumber.getText().toString();
        final String StringTextViewNumberOnly =editTextCarrierNumber.getText().toString();

        if (StringEditTextCarrierNumberWithPlus.isEmpty() || StringEditTextCarrierNumberWithPlus.length() < 10) {
            editTextCarrierNumber.setError(getResources().getString(R.string.valid_number_is_required));
            editTextCarrierNumber.requestFocus();
            return;
        }

        String phoneNumber = StringEditTextCarrierNumberWithPlus;

        Intent intent = new Intent(context, ForgetPasPhoneVerify.class);
        intent.putExtra("phonenumber", phoneNumber);
        intent.putExtra("phonenumberonly", StringTextViewNumberOnly);
        intent.putExtra("countryname",ccp.getSelectedCountryName());
        intent.putExtra("countrynamecode",ccp.getSelectedCountryNameCode());
        intent.putExtra("phonecode",ccp.getSelectedCountryCodeWithPlus());
        startActivity(intent);
    }

    private void initCustomProgressBarLoading() {
        progressbarLoadingPleaseWait = new Dialog(context);
        progressbarLoadingPleaseWait.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressbarLoadingPleaseWait.setContentView(R.layout.model_dialog_progress_loading_please_wait);
        progressbarLoadingPleaseWait.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressbarLoadingPleaseWait.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Intent intent = new Intent(this, ProfileActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
        }
    }
}