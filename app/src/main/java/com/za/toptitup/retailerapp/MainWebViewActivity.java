package com.za.toptitup.retailerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.za.toptitup.loginlibrary.activity_login;

public class MainWebViewActivity extends AppCompatActivity {

    private WebView webView;
    ProgressDialog progressDialog;


    private static final String BASE_URL =
            "https://dev.topitup.co.za";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);

        webView = findViewById(R.id.webview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        Intent intent = getIntent();

        String license = intent.getStringExtra("LICENSE");
        String pos_user_id = intent.getStringExtra("POS_USER_ID");

        if (license == null || pos_user_id == null) {
            finish();
            return;
        }

        // WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        // Cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // 🔥 LOGOUT HANDLING HERE
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                String url = request.getUrl().toString();
                if (url.contains("/printslip")) {
                    // String url = "https://dev.topitup.co.za/Retailerscan/printslip/0/395242/20";

// 1. Split the URL by "/"
                    String[] parts = url.split("/");

// 2. Find index of "printslip"
                    int indexPrintslip = -1;
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("printslip")) {
                            indexPrintslip = i;
                            break;
                        }
                    }

// 3. If found, extract the next values
                    if (indexPrintslip != -1 && parts.length > indexPrintslip + 3) {
                        String type = parts[indexPrintslip + 1]; // "0"
                        String txid = parts[indexPrintslip + 2]; // "395242"
                        String amount = parts[indexPrintslip + 3]; // "20"

                        // 4. Show a Toast with the results
                        Toast.makeText(getApplicationContext(),
                                "transaction type: " + type +
                                        "**** transaction id: " + txid +
                                        "**** transaction amount: " + amount,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "URL format unexpected",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                if (url.contains("/logout")) {
                    handleLogout();
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ) {
                if (url.contains("/logout")) {
                    handleLogout();
                    return true;
                }
                return false;
            }
        });

        // Build final URL
        String finalUrl =
                BASE_URL +
                        "/retailerscan/retailer_app_login/" +
                        license + "/" +
                        pos_user_id;

        webView.loadUrl(finalUrl);
    }

    private void handleLogout() {

        // Clear WebView data
        webView.clearCache(true);
        webView.clearHistory();

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Go back to login (library)
        Intent intent = new Intent(
                this,
                activity_login.class
        );
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            handleLogout();
            super.onBackPressed();
        }
    }
}
