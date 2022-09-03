      package com.skyblue.skybluea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class HomeVideoActivity extends AppCompatActivity {

    private WebView webView;
    Activity activity;
   // private ProgressDialog progressDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout Nav;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_video);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        Nav = findViewById(R.id.top_nav);

        activity = this;

        final ProgressDialog progressDialog = new ProgressDialog(HomeVideoActivity.this, R.style.AppCompatAlertDialogStyle);
     //   progressDialog = ProgressDialog.show(activity, "Loading", "Please wait", true);
        progressDialog.setMessage("Loading Please wait");
        progressDialog.show();
        progressDialog.setCancelable(true);

        webView = findViewById(R.id.webView);


        webView.loadUrl("https://www.skyblue-watch.xyz/web/web_app/nav_video/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);


        Nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.dismiss();
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // DO NOT CALL SUPER METHOD
                super.onReceivedSslError(view, handler, error);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            
                webView.loadUrl("file:///android_asset/internet_error.html");

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                webView.loadUrl("https://www.skyblue-watch.xyz/web/web_app/nav_video/");
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }
}