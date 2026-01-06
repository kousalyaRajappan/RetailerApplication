package com.za.toptitup.retailerapp;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainWebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);

        webView = findViewById(R.id.webview);

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

        // Example: Load your URL
        String url = "https://dev.topitup.co.za/Retailer"; // Replace with your URL
        webView.loadUrl(url);
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

