package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;


import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


//import com.imagpay.SwipeEvent;
//import com.imagpay.SwipeListener;
//import com.imagpay.enums.CardDetected;
//import com.imagpay.enums.EmvStatus;
//import com.imagpay.enums.PrintStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;


public class activity_web_rica extends BaseActivity {

    Context mContext;


    //BottomBar bottomBar;
    Realm realm;



    Dialog dialog;

    TextView txt_last_voucher_info;
    int printerQ1Sts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;


        setContentView(R.layout.activity_web_rica);




        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        WebView web_view = findViewById(R.id.webview);
        web_view.requestFocus();
        WebSettings settings = web_view.getSettings();
        web_view.getSettings().setLightTouchEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setGeolocationEnabled(true);
        web_view.setSoundEffectsEnabled(true);
        web_view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        web_view.getSettings().setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.loadUrl("https://www.ricaonline.co.za");
        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });

     //   web_view.setDomStorageEnabled(true);
      //  myWebView.loadUrl("https://www.ricaonline.co.za");


        BottomNavigationView mBottomNav  = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                /*switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;

                    case R.id.action_clear:



                        return true;
                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    // Your clear logic here (if any)
                    return true;
                } else {
                    return true;
                }

            }

        });


    }


















    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;

    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);
            if(printerQ1Sts==1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(View.VISIBLE);
            }
            else {
                bt_close.setVisibility(View.VISIBLE);
                btn_paper_load.setVisibility(View.GONE);
            }
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.VISIBLE);
        }

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_paper_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        btn_Paper_ignore_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }






























    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }

    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int len = data.length;
            String str = "";
            for (int i = 0; i < len; i++) {
                if ((data[i] & 0xFF) < 16)
                    str = str + "0" + Integer.toHexString(data[i] & 0xFF);
                else
                    str = str + Integer.toHexString(data[i] & 0xFF);
            }
            return str.toUpperCase();
        }
    }

}
