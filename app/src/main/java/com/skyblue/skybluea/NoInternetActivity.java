package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class NoInternetActivity extends AppCompatActivity {
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);


        checkInternetConnecton();
    }

    private void checkInternetConnecton() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            Intent intent = new Intent(NoInternetActivity.this, Home.class);
            startActivity(intent);
            finish();
        } else{
            connected = false;

            handler=new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkInternetConnecton();
                }
            }, 700);

        }
    }
}
