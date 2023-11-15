package com.skyblue.skybluea.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.skyblue.skybluea.activity.account.AccountActivity;
import com.skyblue.skybluea.activity.channels.ChannelsDashboard;
import com.skyblue.skybluea.adapters.ChannelsAdatpter;
import com.skyblue.skybluea.adapters.PostAdapter;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.database.DatabaseManager;
import com.skyblue.skybluea.databinding.ActivityHomeBinding;
import com.skyblue.skybluea.helper.AppConstants;
import com.skyblue.skybluea.helper.CheckNetwork;
import com.skyblue.skybluea.helper.GlobalVariables;
import com.skyblue.skybluea.helper.LocaleHelper;
import com.skyblue.skybluea.helper.Utils;
import com.skyblue.skybluea.helper.custom_media_picker.MediaPickerActivity;
import com.skyblue.skybluea.model.ChannelCreation;
import com.skyblue.skybluea.model.ChannelsModel;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;
import com.skyblue.skybluea.helper.session.SessionHandler;
import com.skyblue.skybluea.helper.session.User;
import com.skyblue.skybluea.viewmodels.PostListViewModel;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
   private ActivityHomeBinding binding;
    private SessionHandler session;
    public static Context context;
    private User user;
    private List<Post> postList;
    private PostListViewModel postListViewModel;
    private PostAdapter adapter;
    public HomeActivity activity;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int MY_STORAGE_REQUEST_CODE = 2;
    private DatabaseManager databaseManager;
    private ArrayList<ChannelsModel> channelsArrayList;
    private APIInterface apiInterface;
    private boolean isChannelsHave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        CheckNetwork network = new CheckNetwork(getApplicationContext());
        network.registerNetworkCallback();

        if (GlobalVariables.isNetworkConnected){
            // call retrofit api
            postListViewModel.makeApiCall();
            Log.e("home_", "Internet connected");
        }else{
            Log.e("home_", "Internet not connected");
            binding.errorLoadHome.setVisibility(View.GONE);
        }

        session = new SessionHandler(getApplicationContext());
        user = session.getUserDetails();

        context = getApplicationContext();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new PostAdapter(postList);
        binding.recyclerView.setAdapter(adapter);
        // framework provides the ViewModels, a special mechanism is required to create instances of them
        postListViewModel = new ViewModelProvider(this).get(PostListViewModel.class);

        initSetOnClickListener();
        initSession();

             /*
         Attach the Observer object to the LiveData object using the observe() method. The observe() method takes a LifecycleOwner object.
         This subscribes the Observer object to the LiveData object so that it is notified of changes. You usually attach the Observer object
         in a UI controller, such as an activity or fragment.
         */
        postListViewModel.getUserListObserver().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> list) {
                // check user array list empty or null
                if (list!=null){
                    postList = list;
                    adapter.updateUserList(list);
                    binding.errorLoadHome.setVisibility(View.GONE);
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
                if (list == null){
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.errorLoadHome.setVisibility(View.GONE);
                    binding.serverErrorLoad.setVisibility(View.VISIBLE);
                }
            }
        });

        if(session.isLoggedIn()){
            getChannelsFromServer();
            syncChannelList();
        }

    }

    private void getChannelsFromServer() {
        String userId = user.getUser_id();
        String access = AppConstants.ACCESS_CHANNEL_GET;

        RequestBody mUserId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody mAccess = RequestBody.create(MediaType.parse("multipart/form-data"), access);

        Call<ChannelCreation> call = apiInterface.getChannels(mAccess, mUserId);

        call.enqueue(new Callback<ChannelCreation>() {
            @Override
            public void onResponse(Call<ChannelCreation> call, Response<ChannelCreation> response) {
                if (response.isSuccessful()){
                    ChannelCreation channelCreation = response.body();

                    assert channelCreation != null;
                    Log.e("channel_", channelCreation.status);

                    ArrayList<String> channelNameStringArray = new ArrayList<>();
                    ArrayList<String> channelIdStringArray = new ArrayList<>();
                    ArrayList<String> channelCreatedDateStringArray = new ArrayList<>();

                    List<ChannelCreation.Data> dataList = channelCreation.data;
                    for (ChannelCreation.Data data: dataList){
                      //  Log.e("channel_", data.channel_name);
                        channelNameStringArray.add(data.channel_name);
                        channelIdStringArray.add(data.channel_id);
                        channelCreatedDateStringArray.add(data.created_date);

                        Log.e("channel_", "Channel synced success from server ");
                    }
                    databaseManager.open();
                    databaseManager.deleteAllChannel();
                    databaseManager.saveChannelListFromServer(channelNameStringArray,
                            channelIdStringArray,
                            channelCreatedDateStringArray);
                  if (dataList.size() != 0){
                      isChannelsHave = true;
                      checkPrimaryChannel();
                  }
                }
            }

            @Override
            public void onFailure(Call<ChannelCreation> call, Throwable t) {
                Log.e("channel_", "Error : Unable to sync from server");
            }
        });
    }

    private void checkPrimaryChannel() {
        Log.e("channel", "Channels have - not set primary channel to upload");
        String primaryChannelId = user.getChannel_primary_id();

        if (primaryChannelId.equals("")){
            Toast.makeText(context, "Please select channel to upload", Toast.LENGTH_SHORT).show();
            binding.channelPrimarySelectionCon.setVisibility(View.VISIBLE);
            channelsArrayList = new ArrayList<>(databaseManager.loadCahnnels());
            displayChannelsList();
        }
      //  String debug = "";
    }

    private void displayChannelsList() {
        binding.primaryChannelList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.primaryChannelList.setHasFixedSize(true);
        binding.primaryChannelList.setLayoutManager(linearLayoutManager);
        binding.primaryChannelList.setItemViewCacheSize(20);
        ChannelsPrimarySelectAdapater channelsPrimarySelectAdapater = new ChannelsPrimarySelectAdapater(getApplicationContext(), this, channelsArrayList);
        binding.primaryChannelList.setAdapter(channelsPrimarySelectAdapater);

    }

    public class ChannelsPrimarySelectAdapater extends RecyclerView.Adapter<ChannelsPrimarySelectAdapater.ViewHolder> {
        private Context context;
        private Activity activity;
        private ArrayList<ChannelsModel> arrayList;

        public ChannelsPrimarySelectAdapater(Context context, Activity activity, ArrayList<ChannelsModel> arrayList){
            this.context = context;
            this.activity  = activity ;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public ChannelsPrimarySelectAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(context).inflate(R.layout.model_channel_list_primary_sel, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChannelsPrimarySelectAdapater.ViewHolder holder, int position) {

            final ChannelsModel channelsModel = arrayList.get(position);
            holder.channelName.setText(channelsModel.getChannelName());
            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
                    holder.radioButton.setEnabled(true);
                    session.saveChannelPrimary(channelsModel.getChannelId(), channelsModel.getChannelName());
                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView channelName;
            private CardView mainLayout;
            private RadioButton radioButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                channelName = itemView.findViewById(R.id.channel_name);
                mainLayout = itemView.findViewById(R.id.main);
                radioButton = itemView.findViewById(R.id.radio_button);
            }
        }
    }


    private void syncChannelList() {
        Log.e("channel_", "Channel sync started from server");
        channelsArrayList = new ArrayList<>(databaseManager.loadCahnnels());

        if (channelsArrayList.size() == 0){
            Log.e("channel_", "No channels created");
        }else {
            Log.e("channel_", "channels availabe");
            isChannelsHave = true;
        }
    }

    private void initSession() {
        if (session.isLoggedIn()) {
            Glide
                    .with(context)
                    .load(user.getUser_profile())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder_person)
                    .into(binding.loggedUserImage);
        }
    }

    private void initSetOnClickListener() {
        binding.bottomNavHome.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.item_home:
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class)); finish();  overridePendingTransition(0, 0);
                    return true;

                case R.id.item_media:
                    Intent ii2 = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(ii2);
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.item_add:
                    if (session.isLoggedIn()) {
                        if (isChannelsHave){
                            Intent ii = new Intent(HomeActivity.this, MediaPickerActivity.class);
                            startActivity(ii);
                        }else {
                            Intent ii = new Intent(HomeActivity.this, ChannelsDashboard.class);
                            startActivity(ii);
                        }
                    } else {
                       login();
                    }
                    return true;

                case R.id.item_notification:
                    if (session.isLoggedIn()) {
                        Intent ii = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(ii);
                    } else {
                        login();
                    }
                    return true;

                case R.id.item_chat:
                    Intent ii = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(ii);
                    return true;
            }
            return false;
        });

        binding.uploadVideo.setOnClickListener(view -> {
            if (session.isLoggedIn()) {
                if (isChannelsHave){
                    Intent ii = new Intent(HomeActivity.this, MediaPickerActivity.class);
                    startActivity(ii);
                }else {
                    Intent ii = new Intent(HomeActivity.this, ChannelsDashboard.class);
                    startActivity(ii);
                }
            } else {
                login();
            }
        });

        binding.swipeContainer.setOnRefreshListener(() -> {
            if (postList != null){
                postList.clear();
                postListViewModel.makeApiCall();
                binding.swipeContainer.setRefreshing(false);
            }
        });

        binding.userProfileTopRelativeBtn.setOnClickListener(view -> {
            if (session.isLoggedIn()) {
                Intent intent = new Intent(context, AccountActivity.class);
                startActivity(intent);
            } else {
                Intent ii = new Intent(context, LoginActivity.class);
                startActivity(ii);
            }
        });
    }

    private void login() {
        Intent i = new Intent(context, LoginActivity.class);
        startActivity(i);
    }

    private void storagePermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
        }
        else
        {
            //Storage Permission already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_CAMERA_REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    storagePermission();
                }
                else
                {
                    Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            };
            case MY_STORAGE_REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                    Toast.makeText(context, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postList == null){
            postListViewModel.makeApiCall();
        }
    }
}
