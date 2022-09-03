package com.skyblue.skybluea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostTagFriendsActivity extends AppCompatActivity {
    private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";

    private static final String GET_RECYCLER = "https://www.skyblue-watch.xyz/web/handle/friend/get_tag_friends.php";

    ProgressBar progressDialog;
    EditText searchEditText;
    ImageView backBtn;

    private RecyclerView fList;
    private LinearLayoutManager linearLayoutManager;
    private List<Friends> friendsList;
    private RecyclerView.Adapter adapter;

    private Context context = this;

    String QueryHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tag_friends);

        backBtn = findViewById(R.id.backBtn);
        fList = findViewById(R.id.recycler_view);
        progressDialog = findViewById(R.id.progressBar);
        searchEditText = (EditText) findViewById(R.id.editTextSearch);


        friendsList = new ArrayList<>();

        adapter = new FriendsAdapter(context.getApplicationContext(), friendsList);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        fList.setHasFixedSize(true);
        fList.setItemViewCacheSize(20);
        fList.setLayoutManager(linearLayoutManager);
        fList.setAdapter(adapter);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();
    }

    private void getData() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_RECYCLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                Friends friends = new Friends();

                                friends.setFriend_id(jsonObject2.getString("id"));
                                friends.setFriend_profile(jsonObject2.getString("profile_url"));
                                friends.setFriend_name(jsonObject2.getString("name"));
                                friendsList.add(friends);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.setVisibility(View.INVISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.setVisibility(View.INVISIBLE);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.setVisibility(View.INVISIBLE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                SharedPreferences sp = context.getSharedPreferences(SHARED_PREFE_ID, MODE_PRIVATE);
                String user_id_holder_in = sp.getString(KEY_PREFE_ID, String.valueOf(1));

                QueryHolder = searchEditText.getText().toString();
                String cases = "%";
                String casess = "%";
                String query = cases + QueryHolder + casess;

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", user_id_holder_in);
                params.put("query", query);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public class Friends {

        public String friend_id;
        public String friend_name;
        public String friend_profile;

        public Friends() {
            this.friend_id = friend_id;
            this.friend_name = friend_name;
            this.friend_profile = friend_profile;

        }

        public String getFriend_id() {
            return friend_id;
        }

        public String getFriend_name() {
            return friend_name;
        }

        public String getFriend_profile() {
            return friend_profile;
        }

        public void setFriend_id(String friend_id) {
            this.friend_id = friend_id;
        }

        public void setFriend_name(String friend_name) {
            this.friend_name = friend_name;
        }

        public void setFriend_profile(String friend_profile) {
            this.friend_profile = friend_profile;
        }


    }

    public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
        Context context;
        private List<Friends> list;
        View v;

        public FriendsAdapter(Context context, List<Friends> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            v = LayoutInflater.from(context).inflate(R.layout.model_row_tag_friends_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
            final Friends friends = list.get(position);

            holder.name.setText(friends.getFriend_name());
            Glide
                    .with(context)
                    .load(friends.getFriend_profile())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(holder.imageProfile);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SwitchCompat switch_compact;
            ImageView imageProfile;
            TextView name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                switch_compact = itemView.findViewById(R.id.switch_compact);
                imageProfile = itemView.findViewById(R.id.thumbnail_view);
                name = itemView.findViewById(R.id.text_name);

            }
        }
    }

}