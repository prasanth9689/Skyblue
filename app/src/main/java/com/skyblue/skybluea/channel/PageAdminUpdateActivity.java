package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.skyblue.skybluea.R;
import com.vanniktech.emoji.EmojiEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PageAdminUpdateActivity extends AppCompatActivity {

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";

    Context context = this;

    EmojiEditText nameTxt;
    EditText  descriptionTxt , phoneTxt , websiteTxt;
    EditText addressTxt, pinCodeTxt, cityNameTxt , stateNameTxt;
    EditText spinnerTxt;

    ViewGroup rootView;

    Button saveBtn;

    String page_id, common_id, common_name , page_name, page_description , phone , website , address;
    String pin_code , cover , city_name , state_name , logo;

    String GetSendString , toServerUnicodeEncoded;
    String dateString , timeString , timeDateString;

    ProgressDialog progressDialog;
    private static final String SERVER_GET_PAGE = "https://www.skyblue-watch.xyz/web/handle/pages/get_page_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_admin_update);

        rootView = findViewById(R.id.main_activity_root_view);

        nameTxt = findViewById(R.id.id_name_text);
        descriptionTxt = findViewById(R.id.id_description);
        spinnerTxt = findViewById(R.id.id_spinner);

        phoneTxt = findViewById(R.id.id_phone);
        websiteTxt = findViewById(R.id.id_website);

        addressTxt = findViewById(R.id.id_addrss);
        pinCodeTxt = findViewById(R.id.id_pincode);
        cityNameTxt = findViewById(R.id.id_city_name);
        stateNameTxt = findViewById(R.id.id_state_name);

        saveBtn = findViewById(R.id.id_save_button);

        page_id = getIntent().getStringExtra("page_id");
        common_id = getIntent().getStringExtra("common_id");

        // Toast.makeText(context, page_id+common_id+common_name+page_name+page_description,Toast.LENGTH_SHORT).show();



        //Toast.makeText(context, phone+website+address+pin_code+cover+city_name+state_name,Toast.LENGTH_LONG).show();

        LoadPage();


       /*
        descriptionTxt.setText(common_name);
        phoneTxt.setText(phone);
        websiteTxt.setText(website);
        addressTxt.setText(address);
        pinCodeTxt.setText(pin_code);
        cityNameTxt.setText(city_name);
        stateNameTxt.setText(state_name);
        spinnerTxt.setText(common_name);

        */

        spinnerTxt.setFocusable(false);

        /*
        switch (Integer.parseInt(common_id)) {

            case 1:
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.array_education, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTxt.setAdapter(adapter);

                break;
            case 2:
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                        R.array.array_celebrity, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTxt.setAdapter(adapter1);

                break;

            case 3:
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.array_business, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTxt.setAdapter(adapter2);
                break;

            case 4:
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                        R.array.array_entertainment, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTxt.setAdapter(adapter3);
                break;

            case 5:
                //       Toast.makeText(HomeProfileMainActivity.this, "Logout" , Toast.LENGTH_SHORT).show();



                break;

            default:
                Toast.makeText(context , "default", Toast.LENGTH_SHORT).show();
                break;
        }

         */


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameTxt.getText().toString();

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

                UPDATEPAGE();
            }
        });
    }
    class PageIn {

        private String id, name , common_name , logo , cover , description , phone , website;
        private String address, pin_code , city_name , state_name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCommon_name() {
            return common_name;
        }

        public String getLogo() {
            return logo;
        }

        public String getCover() {
            return cover;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCommon_name(String common_name) {
            this.common_name = common_name;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDescription() {
            return description;
        }

        public String getPhone() {
            return phone;
        }

        public String getWebsite() {
            return website;
        }

        public String getAddress() {
            return address;
        }

        public String getCity_name() {
            return city_name;
        }

        public String getPin_code() {
            return pin_code;
        }

        public String getState_name() {
            return state_name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public void setPin_code(String pin_code) {
            this.pin_code = pin_code;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }
    }
    private void LoadPage() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_GET_PAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       Toast.makeText(HomeHomeFragmentActivity.this.getActivity().getApplicationContext(), response+ "", Toast.LENGTH_SHORT).show();
                        // UserIdTempTxt.setText("TextView Json Response Total Likes: " + response);
                        //progressDialog.dismiss();

                        //getting whole json object from response
                        try {

                            JSONObject jsonObjectluser = new JSONObject(response);

                            if (jsonObjectluser.optString("status").equals("true"))
                            {
                                ArrayList<PageIn> ArrayList = new ArrayList<>();
                                JSONArray jsonArrayst = jsonObjectluser.getJSONArray("data");
                                for (int i = 0; i < jsonArrayst.length(); i++) {
                                    PageIn Model = new PageIn();
                                    JSONObject dataobjst = jsonArrayst.getJSONObject(i);

                                    Model.setId(dataobjst.getString("id"));
                                    Model.setName(dataobjst.getString("name"));
                                    Model.setCommon_name(dataobjst.getString("common_name"));

                                    Model.setDescription(dataobjst.getString("des"));
                                    Model.setPhone(dataobjst.getString("phone"));
                                    Model.setWebsite(dataobjst.getString("website"));

                                    Model.setAddress(dataobjst.getString("address"));
                                    Model.setPin_code(dataobjst.getString("pin_code"));
                                    Model.setCity_name(dataobjst.getString("city_name"));
                                    Model.setState_name(dataobjst.getString("state_name"));



                                    ArrayList.add(Model);

                                }
                                for (int j = 0; j < ArrayList.size(); j++) {




                                    String newStringEmojidecooded = "";

                                    String nameString = ArrayList.get(j).getName();

                                    try {
                                        byte[] data = Base64.decode(nameString, Base64.DEFAULT);
                                        newStringEmojidecooded = new String(data, "UTF-8");

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    nameTxt.setText(newStringEmojidecooded);
                                    spinnerTxt.setText(ArrayList.get(j).getCommon_name());

                                    descriptionTxt.setText(ArrayList.get(j).getDescription());
                                    phoneTxt.setText(ArrayList.get(j).getPhone());
                                    websiteTxt.setText(ArrayList.get(j).getWebsite());

                                    addressTxt.setText(ArrayList.get(j).getAddress());
                                    pinCodeTxt.setText(ArrayList.get(j).getPin_code());
                                    cityNameTxt.setText(ArrayList.get(j).getCity_name());
                                    stateNameTxt.setText(ArrayList.get(j).getState_name());
                                }

                            } else {
                                //      Toast.makeText(HomeHomeFragmentActivity.this.getActivity().getApplicationContext(), jsonObjectluser.optString("message") + "", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        // textViewJs.setText("Error Getting Json Data");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // textViewJsonResponse.setText("Error");
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){




            @Override
            protected Map<String, String> getParams() {


                String page_id = getIntent().getStringExtra("page_id");

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("page_id", page_id);



                return params;
            }

        };

        // Creating requestquue
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void UPDATEPAGE() {
        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait... its take a some time");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);

        String page_id = getIntent().getStringExtra("page_id");


        final String name = toServerUnicodeEncoded;

        //  String name = getIntent().getStringExtra("page_name");
        String des = descriptionTxt.getText().toString();
       // String spinner = spinnerTxt.getSelectedItem().toString().trim();
        String spinner = spinnerTxt.getText().toString();

        String phone = phoneTxt.getText().toString();
        String website = websiteTxt.getText().toString();

        String address = addressTxt.getText().toString();
        String pincode = pinCodeTxt.getText().toString();
        String cityname = cityNameTxt.getText().toString();
        String statename = stateNameTxt.getText().toString();

        String common_id = getIntent().getStringExtra("common_id");

       // Toast.makeText(context, page_id, Toast.LENGTH_SHORT).show();

        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/handle/pages/update/1/create_page.php")
                .addBodyParameter("user_id", userIdHolderShared)
                .addBodyParameter("common_id", common_id)
                .addBodyParameter("page_id", page_id)
                .addBodyParameter("name", name)
                .addBodyParameter("common_name", spinner)
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

                        Toast.makeText(context,"Page updated",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        /*
                        Intent intent = new Intent(getApplicationContext(), PageHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                         */


                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }


}