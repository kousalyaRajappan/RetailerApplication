package com.za.toptitup.retailerapp;


import android.app.Application;
import android.content.Intent;

import com.za.toptitup.loginlibrary.activitySplashScreen;


public class RetailerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, activitySplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

