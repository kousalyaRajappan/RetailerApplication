package com.za.toptitup.loginlibrary;

import static android.content.Intent.ACTION_BATTERY_CHANGED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.za.toptitup.loginlibrary.utils.BatteryReceiver;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.ScreenStateReceiver;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class BaseAdminActivity extends AppCompatActivity implements LogoutAdminListener {
    private static boolean isFirebaseInitialized = false;

    private Handler refreshHandler;
    private Runnable runnable;
    Context mContext;
    private ScreenStateReceiver mReceiver;
    boolean isRegistered = false;

    BatteryReceiver batteryReceiver;
    PrinterTopitup printer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isFirebaseInitialized) {
            FirebaseApp.initializeApp(this);
            isFirebaseInitialized = true;
            addDeviceIdToCrashlytics();
            FirebaseCrashlytics.getInstance().log("Crashlytics initialized in BaseAdminActivity");
            FirebaseCrashlytics.getInstance().sendUnsentReports();
        }
        super.onCreate(savedInstanceState);

        ((Topitup) getApplication()).registerSessionListenerAdmin(this);
        ((Topitup) getApplication()).startUserSessionAdmin();
        FullscreenCall();
        printer = new PrinterTopitup(this);


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReceiver();
        registerReceiver(mReceiver, intentFilter);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(ACTION_BATTERY_CHANGED));
    }

    private void addDeviceIdToCrashlytics() {

        // 1️⃣ Get unique Android device ID
        String deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // 2️⃣ Push device ID to Crashlytics as a custom key
        FirebaseCrashlytics.getInstance().setCustomKey("device_id", deviceId);

        // (Optional but recommended) also set as User ID
        FirebaseCrashlytics.getInstance().setUserId(deviceId);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        ((Topitup) getApplication()).onUserInteractedAdmin();

//      if (isValidLogin()) {
//
//          getSharedPreference().edit().putLong(KEY_SP_LAST_INTERACTION_TIME, System.currentTimeMillis()).apply();
//      }
//  else {
//          AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
//          dlgAlert.setMessage("This is an alert with no consequence");
//          dlgAlert.setTitle("App Title");
//          dlgAlert.setPositiveButton("OK", null);
//          dlgAlert.setCancelable(true);
//          dlgAlert.create().show();
//      logout();
//      }
        // Toast.makeText(BaseActivity.this, "t="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
    }

    public void logout() {

     /*   AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("This is an alert with no consequence");
        dlgAlert.setTitle("App Title");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();*/
//        this.unregisterReceiver(mReceiver);
        hideKeyboard();

        BaseActivity.fromVoucherSale = true;
        hideKeyboard();
        finishAffinity();
        startActivity(new Intent(this, activity_main.class));

        /*if(activity_login.fromScreen.equals("activity_main") ) {

        }else{
            finishAffinity();
            startActivity(new Intent(this, activity_main.class));


        }*/
        /*finishAffinity();
        startActivity(new Intent(this, activity_login.class));*/

        //   getSharedPreference().edit().remove(KEY_SP_LAST_INTERACTION_TIME).apply();
        //  Toast.makeText(activity_main.this, "logout",Toast.LENGTH_SHORT).show();
        // Intent myIntent2 = new Intent(mContext, activity_login.class);
        //   myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //  myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //   finish();

        //   startActivity(myIntent2);
        // Toast.makeText(this, "lst", Toast.LENGTH_SHORT).show();
        // make shared preference null.
    }

    @Override
    protected void onResume() {
        FullscreenCall();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }

    @Override
    public void onSessionAdminLogout() {
        logout();
    }

    protected void onStop() {

        super.onStop();

    }

    public void hideKeyboard() {

//        public static void hideKeyboard(Activity activity) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
//        }
       /* try {
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception var2) {
        }*/
       /* try {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception var2) {
        }

        */

    }


    private void FullscreenCall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                logout();
            }
        }
    }
}