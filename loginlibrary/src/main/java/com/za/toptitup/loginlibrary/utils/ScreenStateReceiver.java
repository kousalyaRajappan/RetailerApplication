package com.za.toptitup.loginlibrary.utils;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.za.toptitup.loginlibrary.BaseActivity;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class ScreenStateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //BaseActivity ba = new BaseActivity();
//        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//        if (isCharging){
//            Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
//        }else{
//            //ba.batteryAlert();
//            Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
//
//        }

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.i("check","screen on");
            //code
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.i("check","screen off");
            if(context instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) context;
                activity.logout();
            }
            /*if(context instanceof BaseAdminActivity) {
                BaseAdminActivity activity = (BaseAdminActivity) context;
                activity.logout();
            }*/
            //code
        }
    }

}