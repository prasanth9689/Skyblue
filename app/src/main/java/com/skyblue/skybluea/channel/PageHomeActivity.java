package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageHomeActivity extends AppCompatActivity {
    private SessionHandler session;

    private String SERVER_CHECK_DATA = "https://www.skyblue-watch.xyz/web/handle/pages/check_user_have_pages.php";

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";

    // production = correct status code 1    (please change when production time)
    private String TempStatus = "1";

    RelativeLayout relYouDontHavePageCreatePageCenter , relUnableToLoadUserDataCenter;
    Button createPageBtnTop , createPageBtnCenter;

    Context context = this;

    ProgressDialog progressDialog;

    private RecyclerView vList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Page> pageList;
    private RecyclerView.Adapter adapter;

    private String SERVER_GET_PAGES = "https://www.skyblue-watch.xyz/web/handle/pages/get_user_page_list.php";

    ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_home);

        relYouDontHavePageCreatePageCenter = findViewById(R.id.msg_dont_have_page_create_page_center);
        relUnableToLoadUserDataCenter = findViewById(R.id.rel_unable_to_load_user_data);
        createPageBtnTop = findViewById(R.id.id_create_page_btn_top);
        createPageBtnCenter = findViewById(R.id.id_create_page_btn);
        backButton = findViewById(R.id.id_back);

        vList = findViewById(R.id.recycler_view);
        pageList = new ArrayList<>();

        adapter = new PageAdapter(getApplicationContext(), pageList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(vList.getContext(), linearLayoutManager.getOrientation());

        vList.setHasFixedSize(true);
        vList.setLayoutManager(linearLayoutManager);
        vList.addItemDecoration(new DividerItemDecoration(context, 0));
        vList.setAdapter(adapter);

        CheckUserAlreadyHavePage();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createPageBtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageHomeActivity.this, PageCreateSelectActivity.class);
                startActivity(intent);
            }
        });


      createPageBtnCenter.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PageHomeActivity.this, PageCreateSelectActivity.class);
            startActivity(intent);
        }
    });
}

    private void CheckUserAlreadyHavePage() {


        progressDialog = new ProgressDialog(PageHomeActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.loading_please_wait));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog

                relUnableToLoadUserDataCenter.setVisibility(View.VISIBLE);
                createPageBtnTop.setVisibility(View.VISIBLE);

                /*  Important*/
                relYouDontHavePageCreatePageCenter.setVisibility(View.INVISIBLE);
                createPageBtnCenter.setVisibility(View.INVISIBLE);

            }
        });
        progressDialog.show();

        SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
        String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_CHECK_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                     //   Toast.makeText(context, ServerResponse, Toast.LENGTH_SHORT).show();

                        if (ServerResponse.equals(TempStatus))
                        {
                            //NewUserNext();
                            relYouDontHavePageCreatePageCenter.setVisibility(View.VISIBLE);
                          //  createPageBtnTop.setVisibility(View.VISIBLE);

                            Toast.makeText(PageHomeActivity.this, "No pages availabe", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else
                        {
                           // CheckUserExpectationTextView.setText(getResources().getString(R.string.already_registered_this_number_try_new_number));
                          // Toast.makeText(PageHomeActivity.this, "This user have a pages", Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();

                           createPageBtnTop.setVisibility(View.VISIBLE);

                           pageList.clear();
                           LoadUserPages();
                        }
                        // Showing response message coming from server.
                        //  Toast.makeText(RegisterMobileActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        // Showing error message if something goes wrong.
                        Toast.makeText(PageHomeActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", userIdHolderShared);


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(PageHomeActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);



    }

    private void LoadUserPages() {


            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Loading...");
            progressDialog.show();


            final StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_GET_PAGES,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i<jsonArray.length(); i++ )
                                {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                    Page page = new Page();
                                    page.setId(jsonObject2.getString("id"));
                                    page.setCommon_id(jsonObject2.getString("common_id"));
                                    page.setCommon_name(jsonObject2.getString("common_name"));
                                    page.setName(jsonObject2.getString("name"));
                                    page.setDescription(jsonObject2.getString("description"));
                                    page.setPhone(jsonObject2.getString("phone"));
                                    page.setWebsite(jsonObject2.getString("website_url"));
                                    page.setAddress(jsonObject2.getString("address"));
                                    page.setPincode(jsonObject2.getString("pin_code"));
                                    page.setCover(jsonObject2.getString("cover_url"));
                                    page.setLogo(jsonObject2.getString("logo_url"));
                                    page.setCityname(jsonObject2.getString("city_name"));
                                    page.setStatename(jsonObject2.getString("state_name"));

                                    pageList.add(page);
                                    //       Toast.makeText(SearchActivity.this, jsonObject2.getString("id"), Toast.LENGTH_LONG).show();
                                }

                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                            // adapter.clear();


                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();

                        }

                    }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                    progressDialog.dismiss();

                }
            }){
                @Override
                protected Map<String, String> getParams() {

                    SharedPreferences sp = getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                    String userIdHolderShared = sp.getString(KEY_PREFE_ID, null);

                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("user_id", userIdHolderShared);



                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(PageHomeActivity.this);
            requestQueue.add(stringRequest);



    }

    public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder>{

        private Context context;
        private List<Page> list;


        public PageAdapter(Context context, List<Page> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_model_page_user_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Page page = list.get(position);


            String newStringEmojidecooded = "";

            try {
                byte[] data = Base64.decode(page.getName(), Base64.DEFAULT);
                newStringEmojidecooded = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            holder.textName.setText(newStringEmojidecooded);



          //  Toast.makeText(context, "page id:::" + page.getId() + "common_id:::" + page.getCommon_id() +  "common_name:::" + page.getCommon_name() + "description:::" + page.getDescription() + "phone:::" + page.getPhone() + "website:::" + page.getWebsite() + "address:::" + page.getAddress() + "pincode:::" + page.getPincode() + "cover:::" + page.getCover() + "cityname:::" + page.getCityname() + "statename" + page.getStatename(),Toast.LENGTH_SHORT).show();
       //     Toast.makeText(context, "cityname:::" + page.getCityname() + "statename" + page.getStatename(),Toast.LENGTH_SHORT).show();


                Glide
                        .with( this.context )
                        .load(page.getLogo())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.flag)
                        .into( holder.imageView );

                holder.cardViewContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String page_id = page.getId();
                        String common_id = page.getCommon_id();
                        String common_name = page.getCommon_name();
                        String page_name = page.getName();
                        String page_description = page.getDescription();
                        String phone = page.getPhone();
                        String website = page.getWebsite();
                        String address = page.getAddress();
                        String pin_code = page.getPincode();
                        String cover = page.getCover();
                        String city_name = page.getCityname();
                        String state_name = page.getStatename();

                        String logo = page.getLogo();

                        Intent intent = new Intent(context, PageAdminViewActivity.class);
                        intent.putExtra("page_id", page_id);
                        intent.putExtra("common_id", common_id);
                        intent.putExtra("common_name", common_name);
                        intent.putExtra("name", page_name);
                        intent.putExtra("des", page_description);

                        intent.putExtra("phone", phone);
                        intent.putExtra("website", website);
                        intent.putExtra("address", address);
                        intent.putExtra("pin_code", pin_code);
                        intent.putExtra("cover", cover);
                        intent.putExtra("city_name", city_name);
                        intent.putExtra("state_name", state_name);

                        intent.putExtra("logo", logo);

                        startActivity(intent);
                    }
                });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView textName , textDescription;
            CardView cardViewContainer;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.id_logo);
                textName = itemView.findViewById(R.id.id_page_name);
                textDescription = itemView.findViewById(R.id.id_page_description);
                cardViewContainer = itemView.findViewById(R.id.container_card);
            }
        }
    }

    public class Page
    {
        public String id;
        public String user_id;
        public String common_id;
        public String name;
        public String common_name;
        public String description;
        public String phone;
        public String website;
        public String address;
        public String pincode;
        public String logo;
        public String cover;
        public String cityname;
        public String statename;

        public Page()
        {
            this.id = id;
            this.user_id = user_id;
            this.common_id = common_id;
            this.name = name;
            this.common_name = common_name;
            this.description = description;
            this.phone = phone;
            this.website = website;
            this.address = address;
            this.pincode = pincode;
            this.logo = logo;
            this.cover = cover;
            this.cityname = cityname;
        }

        public String getPhone() {
            return phone;
        }

        public String getCommon_name() {
            return common_name;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        public String getCommon_id() {
            return common_id;
        }

        public String getLogo() {
            return logo;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getName() {
            return name;
        }

        public String getWebsite() {
            return website;
        }

        public String getAddress() {
            return address;
        }

        public String getPincode() {
            return pincode;
        }

        public String getCover() {
            return cover;
        }

        public String getCityname() {
            return cityname;
        }

        public String getStatename() {
            return statename;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCommon_id(String common_id) {
            this.common_id = common_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setCommon_name(String common_name) {
            this.common_name = common_name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public void setName(String name) {
            this.name = name;
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

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public void setStatename(String statename) {
            this.statename = statename;
        }
    }
/*
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here

        CheckUserAlreadyHavePage();

        Toast.makeText(context, "Refreshed" ,Toast.LENGTH_SHORT).show();
    }

 */

    }
