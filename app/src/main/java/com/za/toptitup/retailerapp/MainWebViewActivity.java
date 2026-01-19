package com.za.toptitup.retailerapp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private static final String BASE_URL =
            "https://dev.topitup.co.za";

    //Live URL
    //https://admin.topitup.co.za/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);

        webView = findViewById(R.id.webview);
        Intent intent = getIntent();

        String license = intent.getStringExtra("LICENSE");
        String pos_user_id = intent.getStringExtra("POS_USER_ID");
        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        // Accept all cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // Ensure links open within WebView
        webView.setWebViewClient(new WebViewClient());
//LiveUrl
        // String url ="https://admin.topitup.co.za/retailerscan/retailer_app_login/{"+license+}"/{"+ pos_user_id+"}"
        // https://dev.topitup.co.za/retailerscan/retailer_app_login/DEMO972e-e247-11f0-9493-ac1f6b9740b4/5248
        // Example: Load your URL
        String url = "https://dev.topitup.co.za/Retailer"; // Replace with your URL
        if (license == null || pos_user_id == null) {
            finish();
            return;
        }

        String finalUrl =
                BASE_URL +
                        "/retailerscan/retailer_app_login/" +
                        license + "/" +
                        pos_user_id;
        webView.loadUrl(finalUrl);
    }

    @Override
    public void onBackPressed() {
        // Allow back navigation inside WebView
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

