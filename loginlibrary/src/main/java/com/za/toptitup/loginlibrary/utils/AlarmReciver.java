package com.za.toptitup.loginlibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.za.toptitup.loginlibrary.activity_login;

public class AlarmReciver extends BroadcastReceiver {
    Context con;

    public static boolean fromAlarm = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        con= context;
//        Toast.makeText(context,"message receiver broadcast",Toast.LENGTH_LONG).show();
        // show your dialog here
        Log.e("alarm status","..alarmmmmmmm...."+activity_login.stopAlarm);
//        if(!activity_login.stopAlarm){
            fromAlarm =true;
            Intent intent1 = new Intent(con, activity_login.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            con.startActivity(intent1);
//        }
        //        displayDialog(con);
    }


}