package com.za.toptitup.loginlibrary;

import static android.content.Intent.ACTION_BATTERY_CHANGED;

import android.accessibilityservice.AccessibilityService;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.za.toptitup.loginlibrary.admin.activity_settings;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.utils.BatteryReceiver;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.ScreenStateReceiver;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UsbBroadcastReceiver;

public class BaseActivity extends AppCompatActivity implements LogoutListener{

    private static final int REQUEST_ENABLE_BT = 2;
    private static boolean isFirebaseInitialized = false;

    private Runnable runnable;
    Context mContext;

    private static long TIMEOUT_IN_MILLI;

    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog, dialogp,dialogNotify;

    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    private AccessibilityService context;
    Boolean isRegistered = false;
    private ScreenStateReceiver mReceiver;
    private Handler refreshHandler,refreshHandlerscreensaver;
    BatteryReceiver batteryReceiver;

    private Runnable runnablescreensaver;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    Boolean isInBackground;
    Handler handlerprint = new Handler();
    public static boolean fromVoucherSale = false;
    int bp;

    PrinterTopitup printer;

    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbDevice selectedDevice;
    private static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";
    private PendingIntent permissionIntent;
    private SharedPreferences preferences;
    UsbBroadcastReceiver usbBroadcastReceiver;
    private BluetoothAdapter mBluetoothAdapter = null;
    private String selectedPrinter;
    SharedPreferences settings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isFirebaseInitialized) {
            FirebaseApp.initializeApp(this);
            addDeviceIdToCrashlytics();
            isFirebaseInitialized = true;

            FirebaseCrashlytics.getInstance().log("Crashlytics initialized in BaseActivity");
            FirebaseCrashlytics.getInstance().sendUnsentReports();
        }
        super.onCreate(savedInstanceState);
         settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI_ORI", 86400000);
         printer= new PrinterTopitup(this);

        editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
        editor.commit();

       /* if (!isFirebaseInitialized) {
            FirebaseApp.initializeApp(this);
            isFirebaseInitialized = true;

            // Optional: Add custom keys or logs
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Firebase Crashlytics initialized in BaseActivity");
        }*/
        if(!fromVoucherSale) {
            ((Topitup) getApplication()).registerSessionListener(this);
            ((Topitup) getApplication()).startUserSession();
        }


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReceiver();
        registerReceiver(mReceiver, intentFilter);

        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(ACTION_BATTERY_CHANGED));

        FullscreenCall();
       /*  usbBroadcastReceiver = new UsbBroadcastReceiver();
        IntentFilter permissionFilter = new IntentFilter("com.example.yourapp.USB_PERMISSION");
        registerReceiver(usbBroadcastReceiver, permissionFilter);*/
       /* usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        preferences = getSharedPreferences("DevicePrefs", MODE_PRIVATE);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // Initialize and register the UsbPermissionReceiver
        usbBroadcastReceiver = new UsbBroadcastReceiver();
        usbBroadcastReceiver.initialize(usbManager, this);  // Pass UsbManager and callback
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbBroadcastReceiver, filter);*/

        // Attempt to auto-connect to saved device if available
//        autoConnectToSavedDevice();

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);
        bt_close = dialog.findViewById(R.id.bt_close);
        btn_paper_load = dialog.findViewById(R.id.btn_paper_load);
        btn_Paper_ignore_time = dialog.findViewById(R.id.btn_Paper_ignore_time);

         selectedPrinter = settings.getString("printer", "inner");
        if (selectedPrinter.equals("bluetooth")) {


            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // If the adapter is null, then Bluetooth is not supported
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        refreshHandlerscreensaver = new Handler();
        runnablescreensaver = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                final Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    // Somnabulator is undocumented--may be removed in a future version...
                    intent.setClassName("com.android.systemui",
                            "com.android.systemui.Somnambulator");
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } catch (Exception e) { /* Do nothing */ }




            }
        };
        startHandler();




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
    private void autoConnectToSavedDevice() {
        int savedVendorId = preferences.getInt("VendorId", -1);
        int savedProductId = preferences.getInt("ProductId", -1);

        if (savedVendorId != -1 && savedProductId != -1) {
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                if (device.getVendorId() == savedVendorId && device.getProductId() == savedProductId) {
                    if (!usbManager.hasPermission(device)) {
                        usbManager.requestPermission(device, permissionIntent);
                    } else {
                        connection = usbManager.openDevice(device);
                    }
                    break;
                }
            }
        }
    }
    private void saveDeviceDetails(UsbDevice device) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("VendorId", device.getVendorId());
        editor.putInt("ProductId", device.getProductId());
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();


        //  unregisterReceiver(mReceiver);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        ((Topitup) getApplication()).onUserInteracted();
        stopHandler();//stop first and then start
        startHandler();
        //  batteryAlert();


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

    @Override
    protected void onStart() {
        super.onStart();
//        autoConnectToSavedDevice();
        if (selectedPrinter.equals("bluetooth")) {
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//            registerReceiver(mReceiverbluetooth, filter);

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);


                Toast.makeText(BaseActivity.this,"bluetooth 11111111",Toast.LENGTH_LONG).show();
                if (activity_settings.mService != null && !activity_settings.isBluetoothConnected) {
                    String lastDeviceAddress = settings.getString("last_device_address", null);
                    if (lastDeviceAddress != null) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                        activity_settings.mService.connect(device);
                    }
                }else{
                    Toast.makeText(BaseActivity.this,"bluetooth 222222222",Toast.LENGTH_LONG).show();

                }
                // Otherwise, setup the session
            } else {
                if (activity_settings.mService == null)
                    activity_settings.mService = new BluetoothService(this, activity_settings.mHandler);

            }
        }

    }

    public boolean batteryAlert() {


        getApplication();
        bp = Topitup.getBatteryPercentage(getApplicationContext());
        boolean connected = ((Topitup) getApplication()).isConnected(getApplicationContext());
        Log.i("bp=", "=" + bp);

        if (Topitup.DEVICE_TYPE.equals("MOBILE")) {

        } else if (!Topitup.DEVICE_TYPE.equals("MOBILE") && bp > 10) {


            // checkPrintMPOSslip();
        } else {
            //int bp=10;
            if (bp <= 10) {

                if (bp > 5) {
                    if (!connected) {
                        dialog.setCancelable(true);
                        //showCustomDialog("Please Connect Charger!!!","The Battery is getting low:\nless than "+bp+"% remaning",true);
                        showCustomDialogBattery("Please Connect Charger!!!", "Battery is currently " + bp + "%  Note Device will not print at 5%", true);
                    }
                    return true;

                } else {
                    dialog.setCancelable(false);
                    showCustomDialogBattery("Please Connect Charger!!!", "Low Battery unable to print", true);
                    return false;
                }
            }
        }
        return true;
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
//        if (mReceiverbluetooth != null) {
//
//            unregisterReceiver(mReceiverbluetooth);
//        }

       /* if (connection != null) {
            connection.close();
        }
//        unregisterReceiver(usbPermissionReceiver);
        if (usbBroadcastReceiver != null) {
            unregisterReceiver(usbBroadcastReceiver);
        }*/

    }


    @Override
    protected void onResume() {

        batteryAlert();
        FullscreenCall();
        super.onResume();
        if (selectedPrinter.equals("bluetooth")) {

            if (activity_settings.mService != null) {

                if (activity_settings.mService.getState() == BluetoothService.STATE_NONE) {
                    // Start the Bluetooth services
                    activity_settings.mService.start();
                }
            }
        }

    }



    public void logout() {
        //  Toast.makeText(BaseActivity.this, "in=baseactivity",Toast.LENGTH_LONG).show();
     /*   AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("This is an alert with no consequence");
        dlgAlert.setTitle("App Title");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();*/
//        this.unregisterReceiver(mReceiver);

        hideKeyboard();
           /* if(activity_login.fromScreen.equals("activity_main") ) {

            }else{
                finishAffinity();
                startActivity(new Intent(this, activity_main.class));


            }*/

        Log.e("timer end","...main.....1"+activity_login.fromScreen);
        if(activity_login.fromScreen.equals("activity_login")){
            fromVoucherSale = false;
        } else if (activity_login.fromScreen.equals("activity_main")) {
            fromVoucherSale = true;
            stopHandler();
            Topitup.stopTimers();

            startActivity(new Intent(this, activity_main.class));

        }if(activity_login.fromScreen.equals("supplier")){
        }else{
            fromVoucherSale = true;
            hideKeyboard();
            startActivity(new Intent(this, activity_main.class));

            new Handler(Looper.getMainLooper()).postDelayed(() -> finishAffinity(), 500);

        }

        //  finish();
//        startActivity(new Intent(this,activity_login.class));

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


    @Override
    public void onSessionLogout() {
        // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
        logout();
    }
/*
    @Override
    public void onUsbPermissionGranted(UsbDevice device) {
        selectedDevice = device;
        connection = usbManager.openDevice(device);
        saveDeviceDetails(device);
    }

    @Override
    public void onUsbPermissionDenied(UsbDevice device) {

    }*/


    private class ScreenReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            SharedPreferences.Editor editor = settings.edit();


            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                editor.putString("setting_screen_off", "1");
                editor.commit();
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {


                editor.putString("setting_screen_off", "1");
                editor.commit();
                logout();
            }
        }

    }




    //  AppCompatButton btn_Paper_ignore_always;

    private void showCustomDialogBattery(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);

            bt_close.setVisibility(View.VISIBLE);
            btn_paper_load.setVisibility(View.GONE);

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


    public void stopHandler() {
        refreshHandlerscreensaver.removeCallbacks(runnablescreensaver);
    }

    public void startHandler() {


        refreshHandlerscreensaver.postDelayed(runnablescreensaver, ((Topitup) getApplication()).getScreensavertime()); //for 5 minutes
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


    public void getNotifyUpdate() {
        dialogNotify.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setDimAmount(0);
        dialogNotify.setContentView(R.layout.dialog_deposit_notify);

        dialogNotify.setCancelable(false);
        TextView txt_desc = dialogNotify.findViewById(R.id.txt_desc);

        TextView txt_read = dialogNotify.findViewById(R.id.txt_read);


        txt_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialogNotify.dismiss();
//                finish();
            }
        });
        dialogNotify.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialogNotify.show();
        /*Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                // your code here...
                //getNotify()
               // final Dialog dialog = new Dialog(mContext);
//        Dialog dialog=new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);
                dialogNotify.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setDimAmount(0);
                dialogNotify.setContentView(R.layout.dialog_deposit_notify);

                dialogNotify.setCancelable(false);
                TextView txt_desc = dialogNotify.findViewById(R.id.txt_desc);

                TextView txt_read = dialogNotify.findViewById(R.id.txt_read);


                txt_read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        dialogNotify.dismiss();
//                finish();
                    }
                });
                dialogNotify.show();

            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 0l, 1000 * 60 * 60);*/
    }
  /*  private final BroadcastReceiver mReceiverbluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                String lastDeviceAddress = settings.getString("last_device_address", null);
                if (lastDeviceAddress != null) {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                    activity_settings.mService.connect(device);
                }
            }
        }
    };*/


}