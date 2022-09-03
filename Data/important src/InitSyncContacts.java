package com.skyblue.skybluea.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;


public class InitSyncContacts extends AppCompatActivity {
    private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";
    private static final String KEY_PREFE_NAME = "user_name";
    private static final String KEY_PREFE_PROFILE_URL = "profile_url";

    private static final int CONTACT_READ_PERMISSION_CODE = 101;
    ArrayList<Contact> contactList = new ArrayList<>();


    ProgressBar progressBarLoading;
    Button Allow , Skip;
    TextView textView;

    ProgressDialog progressDialog;

    String name, number;
    private String currentDateString;
    HashSet<String> mobileNoSet = new HashSet<String>();
    HashSet<String> NameSet = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_sync_contacts);

        String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
        String get_country_name = getIntent().getStringExtra("countryname");
        String get_country_phone_code = getIntent().getStringExtra("phonecode");

      //  Toast.makeText(InitSyncContacts.this,get_mobile_no+get_country_name+get_country_phone_code,Toast.LENGTH_SHORT).show();




        progressBarLoading = findViewById(R.id.progress_bar_loading);
        Allow = findViewById(R.id.allow);
        textView = findViewById(R.id.text);

        Skip = findViewById(R.id.skip);

        currentDateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idWithoutSync = "2";

                String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
                String get_country_name = getIntent().getStringExtra("countryname");
                String get_country_phone_code = getIntent().getStringExtra("phonecode");

                Intent intent = new Intent(InitSyncContacts.this, InitAddFriendsActivity.class);
                intent.putExtra("id", idWithoutSync);

                intent.putExtra("phonenumberonly" , get_mobile_no);
                intent.putExtra("countryname" , get_country_name);
                intent.putExtra("phonecode" , get_country_phone_code);
                startActivity(intent);
            }
        });

        Allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLoading.setVisibility(View.VISIBLE);
                //rttttttt4rrrrrrrrr  textView.setText("Sync Contact Please Wait..");


                JSONObject JSONcontacts = new JSONObject();

                //Loop through array of contacts and put them to a JSONcontact object
                for (int i = 0; i < contactList.size(); i++) {
                    try {
                        JSONcontacts.put("Count:" + String.valueOf(i + 1), contactList.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject EverythingJSON = new JSONObject();

                try {
                    EverythingJSON.put("contact", JSONcontacts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                //EverythingJSON.put("something", JSONsoemthing);
                //  EverythingJSON.put("else", JSONelse);

                //        https://www.skyblue-watch.xyz/web/handle/test/test.php


                final String URL = "https://www.skyblue-watch.xyz/web/handle/init_sync_contacts.php";

                final ProgressDialog progressDialog = new ProgressDialog(InitSyncContacts.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(true);
                progressDialog.show();


                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            //    Toast.makeText(InitSyncContacts.this, response,Toast.LENGTH_SHORT).show();

                                textView.setText(response);
                //                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();


                                String idWithSync = "1";

                                String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
                                String get_country_name = getIntent().getStringExtra("countryname");
                                String get_country_phone_code = getIntent().getStringExtra("phonecode");

                                Intent intent = new Intent(InitSyncContacts.this, InitAddFriendsActivity.class);
                                intent.putExtra("id", idWithSync);

                                intent.putExtra("phonenumberonly" , get_mobile_no);
                                intent.putExtra("countryname" , get_country_name);
                                intent.putExtra("phonecode" , get_country_phone_code);
                                startActivity(intent);
                                /*
                                Character first = response.charAt(0);

                                switch (Integer.parseInt(String.valueOf(first))) {

                                    // failed sync contact's
                                    case 0:
                                        Toast.makeText(InitSyncContacts.this, "Failed",Toast.LENGTH_SHORT).show();
                                        break;
                                    // successfully sync contact's
                                    case 1:
                                        Toast.makeText(InitSyncContacts.this, "Successfully sync contacts",Toast.LENGTH_SHORT).show();
                                        break;
                                     // No response recived
                                    default:
                                        Toast.makeText(InitSyncContacts.this, "ERROR",Toast.LENGTH_SHORT).show();
                                        break;
                                }

                                 */


                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        progressDialog.dismiss();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();


                        try {
                            JSONObject json = new JSONObject();
                           //  json.put("sender_id", mobileNoSet);
                        //    json.put("sender_name", Arrays.asList("a", "b"));
                            String jsonString = json.toString();

                            JSONObject afterParse = new JSONObject(jsonString);
                            String myString = mobileNoSet.toString();


                            //  Dont erse
                            Gson gson = new Gson();
                            String jsonStringss= gson.toJson(mobileNoSet);
                            //------------------------------------------------

                            //  Dont erse
                       //     Gson gsonName = new Gson();
                        //    String jsonStringssName= gson.toJson(NameSet);
                            //------------------------------------------------

                            SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                            String userIdHolder = sp.getString(KEY_PREFE_ID, null);

                            String get_mobile_no = getIntent().getStringExtra("phonenumberonly");
                            String get_country_name = getIntent().getStringExtra("countryname");
                            String get_country_phone_code = getIntent().getStringExtra("phonecode");

                            params.put("contacts", jsonStringss);
                            params.put("primary_id", userIdHolder);
                            params.put("primary_number", get_mobile_no);
                            params.put("primary_country_name", get_country_name);
                            params.put("primary_country_code", get_country_phone_code);
                            params.put("date_added", currentDateString);

                     //       params.put("names", jsonStringssName);

                           // textView.setText(jsonStringss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Adding All values to Params.

                       // params.put("sender_id","191");


                        return params;
                    }

                };

                RequestQueue requestQueue = Volley.newRequestQueue(InitSyncContacts.this);
                requestQueue.add(stringRequest);
            }
        });



        // if system os is >= marshmallow , request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                            PackageManager.PERMISSION_DENIED) {
                // permission not enabled , request it
                String[] permission = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS};
                //show popup to request permission
                requestPermissions(permission, CONTACT_READ_PERMISSION_CODE);
            } else {
                  getContactList();

                // permission already granded
                //   getContactList();

            }
        }



    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private void getContactList() {

        this.progressDialog = new ProgressDialog(InitSyncContacts.this);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getResources().getString(R.string.please_wait___));
        //   this.progressDialog.show();

        ContentResolver cr = getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {

            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");

                    if (!mobileNoSet.contains(number)) {

                        contactList.add(new Contact(name, number));
                        mobileNoSet.add(number);
                        NameSet.add(name);
                        Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                + " No = " + number);
                    }


                }
            } finally {
                cursor.close();
            }
        }
    }

    public class Contact {
        public String name;
        public String phoneNumber;

        public Contact() {
        }

        public Contact(String name, String phoneNumber ) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

    }

    // Disable back press button
    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}