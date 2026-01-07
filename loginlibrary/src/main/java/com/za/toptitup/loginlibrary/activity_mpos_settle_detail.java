package com.za.toptitup.loginlibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.za.toptitup.loginlibrary.utils.Topitup;


public class activity_mpos_settle_detail extends AppCompatActivity {

    Context mContext;

    WebView mWebView;

    private static final int REQUEST_CAMERARESULT = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        //setContentView(R.ding_products.activity_banking_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( this.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                ///method to get Images

            }else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(this,"Your Permission is needed to get access the camera",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CAMERARESULT);
            }
        }else{
            //
        }


        mWebView = new WebView(this);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

       // mWebView.loadUrl(Topitup.BASE_URL_SYNC + "message/bankdetails/2/?l=" + Topitup.TIU_LICENSE);
        mWebView.loadUrl(Topitup.BASE_URL_SYNC + "message/mpossettlementdetails/?l=" + Topitup.TIU_LICENSE);
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
