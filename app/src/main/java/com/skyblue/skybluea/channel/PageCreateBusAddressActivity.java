package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.skyblue.skybluea.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PageCreateBusAddressActivity extends AppCompatActivity {
    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";


    Context context = this;

    EditText txtAddress, txtPinCode, txtCityName , txtStateName;
    String addressHolder , pinCodeHolder, CityNameHolder , stateNameHolder;
    Button createPageBtn;
    String dateString , timeString , timeDateString;
    ProgressDialog progressDialog;

    String GetSendString , toServerUnicodeEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_create_bus_address);
        txtAddress = findViewById(R.id.id_addrss);
        txtPinCode = findViewById(R.id.id_pincode);
        txtCityName = findViewById(R.id.id_city_name);
        txtStateName = findViewById(R.id.id_state_name);

        createPageBtn = findViewById(R.id.id_page_create_button);

        createPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = getIntent().getStringExtra("page_name");
                String des = getIntent().getStringExtra("page_des");
                String spinner = getIntent().getStringExtra("page_spinner");

                String phone = getIntent().getStringExtra("page_phone");
                String website = getIntent().getStringExtra("page_website");

            //    Toast.makeText(context, name+des+spinner+phone+website, Toast.LENGTH_SHORT).show();

                dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                timeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                timeDateString  = dateString +" "+timeString;

                GetSendString = Objects.requireNonNull(name);

                dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                timeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                timeDateString  = dateString +" "+timeString;

                //     toServerUnicodeEncoded = org.apache.commons.text.StringEscapeUtils.escapeJava(GetSendString);
                try {
                    byte[] data = name.getBytes("UTF-8");
                    toServerUnicodeEncoded = Base64.encodeToString(data, Base64.DEFAULT);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                CREATEPAGE();
            }
        });



    }

    private void CREATEPAGE() {

        progressDialog = new ProgressDialog(PageCreateBusAddressActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait... its take a some time");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);

      String page_id = getIntent().getStringExtra("page_id");

        final String name = toServerUnicodeEncoded;

        //  String name = getIntent().getStringExtra("page_name");
        String des = getIntent().getStringExtra("page_des");
        String spinner = getIntent().getStringExtra("page_spinner");

        String phone = getIntent().getStringExtra("page_phone");
        String website = getIntent().getStringExtra("page_website");

        String address = txtAddress.getText().toString();
        String pincode = txtPinCode.getText().toString();
        String cityname = txtCityName.getText().toString();
        String statename = txtStateName.getText().toString();

        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/handle/pages/business/create_page.php")
                .addBodyParameter("user_id", userIdHolderShared)
                .addBodyParameter("name", name)
                .addBodyParameter("common_name", spinner)
                .addBodyParameter("common_id", page_id)
                .addBodyParameter("date_added", timeDateString)
                .addBodyParameter("description", des)
                .addBodyParameter("phone", phone)
                .addBodyParameter("website", website)
                .addBodyParameter("address", address)
                .addBodyParameter("pin_code", pincode)
                .addBodyParameter("city_name", cityname)
                .addBodyParameter("state_name", statename)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(PageCreateBusAddressActivity.this,"Page created",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), PageHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(PageCreateBusAddressActivity.this,"Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
}