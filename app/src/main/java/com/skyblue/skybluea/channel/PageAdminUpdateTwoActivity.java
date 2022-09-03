package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

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

public class PageAdminUpdateTwoActivity extends AppCompatActivity {

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";


    EmojiEditText nameText;
    Button updateBtn;
    ViewGroup rootView;

    String GetSendString , dateString , timeString , timeDateString;
    String  toServerUnicodeEncoded;

    ProgressDialog progressDialog;

    EditText txtDescription , txtWebsite;
    EditText txtSpinner;

    Context context = this;

    String page_id , common_id , common_name , page_name , page_description , website;

    private static final String SERVER_GET_PAGE = "https://www.skyblue-watch.xyz/web/handle/pages/get_page_details.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());

        setContentView(R.layout.activity_page_admin_update_two);

        nameText = findViewById(R.id.id_name_text);
        updateBtn = findViewById(R.id.id_save_button);
        rootView = findViewById(R.id.main_activity_root_view);
        txtDescription = findViewById(R.id.id_description);
        txtSpinner = findViewById(R.id.id_spinner);
        txtWebsite = findViewById(R.id.id_website);


        page_id = getIntent().getStringExtra("page_id");
        common_id = getIntent().getStringExtra("common_id");


        LoadPage();
/*
        nameText.setText(newStringEmojidecooded);

        txtDescription.setText(page_description);
        txtWebsite.setText(website);

        txtSpinner.setText(common_name);

 */

        txtSpinner.setFocusable(false);


        nameText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(nameText);

        String common_id = getIntent().getStringExtra("common_id");
/*
        switch (Integer.parseInt(common_id)) {

            case 1:
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.array_education, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtSpinner.setAdapter(adapter);

                break;
            case 2:


                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                        R.array.array_celebrity, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtSpinner.setAdapter(adapter1);

                break;

            case 3:
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.array_business, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtSpinner.setAdapter(adapter2);
                break;

            case 4:
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                        R.array.array_entertainment, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtSpinner.setAdapter(adapter3);
                break;

            case 5:
                //       Toast.makeText(HomeProfileMainActivity.this, "Logout" , Toast.LENGTH_SHORT).show();



                break;

            default:
                Toast.makeText(context , "default", Toast.LENGTH_SHORT).show();
                break;
        }


 */
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameText.getText().toString();

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
        String des = txtDescription.getText().toString();
       // String spinner = txtSpinner.getSelectedItem().toString().trim();
        String spinner = txtSpinner.getText().toString();

        String website = txtWebsite.getText().toString();


        String common_id = getIntent().getStringExtra("common_id");

        // Toast.makeText(context, page_id, Toast.LENGTH_SHORT).show();

        AndroidNetworking.post("https://www.skyblue-watch.xyz/web/handle/pages/update/2/create_page.php")
                .addBodyParameter("user_id", userIdHolderShared)
                .addBodyParameter("common_id", common_id)
                .addBodyParameter("page_id", page_id)
                .addBodyParameter("name", name)
                .addBodyParameter("common_name", spinner)
                .addBodyParameter("date_added", timeDateString)
                .addBodyParameter("description", des)
                .addBodyParameter("website", website)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(context,"Page updated",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();


                        Intent intent = new Intent(getApplicationContext(), PageHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);




                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    class PageIn {

        private String id, name , common_name , logo , cover , description ,  website;

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



        public String getWebsite() {
            return website;
        }





        public void setDescription(String description) {
            this.description = description;
        }



        public void setWebsite(String website) {
            this.website = website;
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

                                    Model.setWebsite(dataobjst.getString("website"));




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

                                    nameText.setText(newStringEmojidecooded);
                                    txtSpinner.setText(ArrayList.get(j).getCommon_name());

                                    txtDescription.setText(ArrayList.get(j).getDescription());

                                    txtWebsite.setText(ArrayList.get(j).getWebsite());


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
}