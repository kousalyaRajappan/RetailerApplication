package com.za.toptitup.loginlibrary.utils;

import android.content.Context;

import io.realm.Realm;

import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;


public class API {

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);


    public void fetch_server_updates(final Context mContext, final Realm realm, Topitup app) {

        //showWaitDialog();

        //EventBus.getDefault().post(new eventSyncDetail("Refreshing Data", 0));


    }









}
