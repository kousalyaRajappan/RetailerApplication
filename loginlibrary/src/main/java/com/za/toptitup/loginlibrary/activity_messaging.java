package com.za.toptitup.loginlibrary;

import android.os.Bundle;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_messaging extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<String> arrayList;

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //setContentView(R.ding_products.activity_banking_detail);



        mWebView = new WebView(this);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl(Topitup.BASE_URL_SYNC + "message/msg/?l=" + Topitup.TIU_LICENSE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        this.setContentView(mWebView);
    }
}
