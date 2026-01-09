package com.za.toptitup.loginlibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import android.util.Log;


import com.za.toptitup.loginlibrary.R;
import com.za.toptitup.loginlibrary.activity_login;

public class BatteryReceiver extends BroadcastReceiver {
    Context con;



    @Override
    public void onReceive(Context context, Intent intent) {
        con = context;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        Log.e("battery ", "receiver........" + activity_login.fromScreen);
        if (isCharging) {
            if (level > 15) {
              if (activity_login.fromScreen.equals("activity_login")) {
                   activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                }


            } else {

               if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));
                }
            }

        }
        else {
            if (level > 75) {
              if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                }
            } else if (level > 40 && level < 75) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                }
            } else if (level > 15 && level < 40) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                }

            } else if (level < 15) {
                 if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                }
            }
        }

    }
}
