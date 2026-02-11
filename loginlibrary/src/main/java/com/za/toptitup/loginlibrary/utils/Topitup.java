package com.za.toptitup.loginlibrary.utils;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static timber.log.Timber.DebugTree;
import static timber.log.Timber.i;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;


import com.za.toptitup.loginlibrary.BuildConfig;
import com.za.toptitup.loginlibrary.LogoutAdminListener;
import com.za.toptitup.loginlibrary.LogoutListener;
import com.za.toptitup.loginlibrary.MyContentProvider;

import com.za.toptitup.loginlibrary.R;
import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import com.za.toptitup.loginlibrary.bluetooth.DeviceListActivity;
import com.za.toptitup.loginlibrary.command.sdk.PrinterCommand;

import androidx.lifecycle.ProcessLifecycleOwner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Printer;


public class Topitup extends Application implements LifecycleObserver {  // implements SwipeListener

    //public static final String SDCARD_IMAGES_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "topitup";

    public static String TIU_LICENSE = "";
    public static String TIU_SERVER = "";
    public static String API_SERVER = "";
    public static String POSUSER_ID = "0";
    //public static Account myAccount;
    public static String IS_ADMIN = "0";
    public static String CUSTOMER_ID = "0";
    public static String ACCOUNT_NUMBER = "0";
    public static String POSUSER_NAME = "";
    public static String RICA_REG = "";
    //public static Account myAccount;
    private static final String CHINESE = "GBK";
    public static boolean isBluetoothConnected = false;

    public static final String AUTHORITY = "com.za.toptitup.loginlibraryapp";
    public static final String ACCOUNT_TYPE = "za.co.topitup";
    public static String checkServer = "";

    public static String BASE_URL = "http://tx1.topitup.co.za:25812/";
    public static String BASE_URL1 = "";
    public static String BASE_URL_SYNC = "http://sync.topitup.co.za:25815/";
    public static String BASE_URL_UPDATE = "http://topitup.co.za/";

    public static String DEVICE_TYPE = "";
    public static int DEVICE_MODEL = 0;
    public static Double BLUE_MAX_THRESHOLD = 0.0;
    public static Double BLUE_WARNING_THRESHOLD = 0.0;
    public static Double ONE_MAX_THRESHOLD = 0.0;
    public static Double ONE_WARNING_THRESHOLD = 0.0;
    public static Integer RETAILER_TYPE = 0;
    public static String ST_STATUS = "0";
    public static String DISPLAY_BALANCE = "0";
    public static String ENABLE_COMMISSION = "1";


    public static boolean DEBUG;       // true to use demo server

    private static Topitup mInstance;

    private static Context context;

    public static OkHttpClient client;

    public static double min_swipe = 6;
    public static double max_swipe = 10000.0;
    public static double warning_swipe = 5000.0;

    public static double warning_electra = 2000;

    public static String DEVICE_SLNO = "0";
    public static String MERCHANT_ID = "0";
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothService mService;

//   public static Settings setting;
//   public static MposHandler handler;

    private int checking_paper_status = -1;

    public static String PRINT_BARCODE = "0";

    public static String APP_VERSION = "";
    public static String APP_BUILD_VERSION_CODE = "";


    private LogoutListener listener;
    private LogoutAdminListener listenerAdmin;

    private static Timer timer;
    private static Timer timerAdmin;
    private final long screensavertime = 24 * 60000;//6 min


    final Handler mHandler = new Handler();
    private Thread mUiThread;
    //for WPOS
    private BankCard mCore;
    private Printer mPrinter;

    //for Z91
    // public static Settings setting;
// public static MposHandler handler;
    public static boolean wasInBackground;

    //wpos3
    public static String orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, response;
    public static String payatapirt = "payaccountrt", terminalapinew = "terminalnew", terminalapi = "terminal",cashUp="cashup" ;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;

    // Bluetooth related variables
    private static String bluetoothMsg = "";
    private static final int MESSAGE_STATE_CHANGE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 3;

    // Bluetooth Message Handler
    public static final Handler mBluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SharedPreferences settings = getAppContext().getSharedPreferences("TIUPREF", 0);
            SharedPreferences.Editor editor = settings.edit();

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i("TAG", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            PrinterTopitup.print_data("welcome to retailer app\n\n\n\n");

                            Toast.makeText(Topitup.getAppContext(), "bluetooth connected", Toast.LENGTH_LONG).show();
                            editor.putString("printer", "bluetooth");
                            editor.commit();
                            isBluetoothConnected = true;

                            if (!bluetoothMsg.equals("")) {
                                SendDataByte(PrinterCommand.POS_Print_Text(bluetoothMsg, CHINESE, 0, 0, 0, 0), getAppContext());
                                SendDataByte(PrinterCommand.POS_Set_Cut(1), getAppContext());
                                SendDataByte(PrinterCommand.POS_Set_PrtInit(), getAppContext());
                            }
                            break;

                        case BluetoothService.STATE_CONNECTING:
                            isBluetoothConnected = false;

                            Toast.makeText(Topitup.getAppContext(), "bluetooth connecting", Toast.LENGTH_LONG).show();
                            break;

                        case BluetoothService.STATE_LISTEN:
                            isBluetoothConnected = false;
                            // Toast.makeText(Topitup.getAppContext(), "bluetooth listen", Toast.LENGTH_LONG).show();
                            break;

                        case BluetoothService.STATE_NONE:
                            isBluetoothConnected = false;
                            /*Toast.makeText(Topitup.getAppContext(), "bluetooth none", Toast.LENGTH_LONG).show();
                            editor.putString("printer", "inner");
                            editor.commit();*/
                            break;
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Topitup.context = getApplicationContext();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        SharedPreferences prefs = getSharedPreferences("TIUPREF", MODE_PRIVATE);
        TIU_SERVER = prefs.getString("TIU_SERVER", "LIVE");
        API_SERVER = prefs.getString("API_SERVER", "");

        DEBUG = !TIU_SERVER.contains("LIVE");

        Log.e("selection", API_SERVER + " ........server............" + DEBUG);
        if (DEBUG) {
            if (API_SERVER.equalsIgnoreCase("")) {
//            BASE_URL = "http://tx.topitup.co.za:25812/";
                BASE_URL = "http://demo.topitup.co.za:25812/";
                BASE_URL_SYNC = "http://demo.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://demo.topitup.co.za/";
            } else if (API_SERVER.equalsIgnoreCase("http://dev.topitup.co.za:25812/")) {
//            BASE_URL = "http://tx.topitup.co.za:25812/";
                BASE_URL = "http://dev.topitup.co.za:25812/";
                BASE_URL_SYNC = "http://demo.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://demo.topitup.co.za/";

            } else {
                BASE_URL = "http://dev.topitup.co.za:25812/";
                BASE_URL_SYNC = "http://demo.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://demo.topitup.co.za/";
            }
        } else {
            if (Topitup.TIU_SERVER.equalsIgnoreCase("SERVER3LIVE")) {
                BASE_URL = "http://tx2.topitup.co.za:25812/";
                // BASE_URL1 = "http://41.203.10.186:25812/";
                BASE_URL_SYNC = "http://sync.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://topitup.co.za/";
            } else if (Topitup.TIU_SERVER.equalsIgnoreCase("SERVER2LIVE")) {
                BASE_URL = "http://tx1.topitup.co.za:25812/";
                // BASE_URL1 = "http://41.203.10.186:25812/";
                BASE_URL_SYNC = "http://sync.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://topitup.co.za/";
            } else {
//                BASE_URL = "http://tx1.topitup.co.za:25812/";
                if(! Build.ID.equals("MRA58K")){
                    Log.e("~~~~~~","one"+ Build.ID);
                    BASE_URL = "https://tx2.topitup.co.za/";

                } else {
                    Log.e("~~~~~~","two"+ Build.ID);
                    BASE_URL = "http://tx1.topitup.co.za:25812/";
                }
                // BASE_URL1 = "http://41.203.10.186:25812/";
                BASE_URL_SYNC = "http://sync.topitup.co.za:25815/";
                BASE_URL_UPDATE = "http://topitup.co.za/";

            }
        }
        checkServer = BASE_URL + "voucherapi/test/mytest";

        Log.e("selection", BASE_URL + " ........server.....11111......." + DEBUG);

        //File file = new File("/storage/emulated/legacy/DCIM/Camera/");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .deleteRealmIfMigrationNeeded() // TEMP for dev
                .build();

        Realm.setDefaultConfiguration(config);

        if (BuildConfig.DEBUG) {
            plant(new DebugTree());
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        try {
            PackageInfo pInfo = mInstance.getPackageManager().getPackageInfo(getPackageName(), 0);
            APP_VERSION = String.valueOf(pInfo.versionName);
            APP_BUILD_VERSION_CODE = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        TIU_LICENSE = prefs.getString("TIU_LICENSE", "");
        PRINT_BARCODE = prefs.getString("setting_print_barcode", "0");
        DEVICE_SLNO = getSerialNumber();

        Log.e("deviec serial Number", "......" + DEVICE_SLNO);

        i("android.os.Build.BRAND :" + Build.BRAND);
        i("android.os.Build.MODEL :" + Build.MODEL);
        i("android.os.Build.ID :" + Build.ID);
        i("android.os.Build.BRAND :" + Build.MANUFACTURER);

        if (Build.MODEL.equals("SHOP1") && Build.ID.equals("N2G47H-76")) {
            DEVICE_TYPE = "QCOM SHOP1";
        } else if (Build.MODEL.equals("WPOS-3") || Build.MANUFACTURER.equals("Wiseasy") || Build.MANUFACTURER.equals("wiseasy")) {
            DEVICE_TYPE = "WPOS";
            new Thread() {
                @Override
                public void run() {
                    mCore = new BankCard(getApplicationContext());
                    mPrinter = new Printer(getApplicationContext());
                    try {
                        mPrinter.setPrintFontType(getAppContext(), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (Build.MANUFACTURER.equals("CHUWI") || Build.MANUFACTURER.equals("Mobicel") ||
                Build.MANUFACTURER.equals("samsung") || Build.MANUFACTURER.equals("wiseasy") ||
                Build.MANUFACTURER.equals("rockchip")) {
            if (Build.MODEL.equals("Hi10 XPro")) {
                DEVICE_TYPE = "TABLET";
            } else {
                DEVICE_TYPE = "MOBILE";
            }
        } else if (Build.ID.equals("MRA58K")) {
            DEVICE_TYPE = "Q1";
            Intent intent = new Intent();
            intent.setPackage("com.iposprinter.iposprinterservice");
            intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");

            IntentFilter printerStatusFilter = new IntentFilter();
            printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
            printerStatusFilter.addAction(GET_CUST_PRINTAPP_PACKAGENAME_ACTION);

            mInstance.checking_paper_status = 1;
            q1handler = new HandlerUtils.MyHandler(iHandlerIntent);
        } else if (Build.ID.equals("NRD90M")) {
            //bindService();
        } else {
            DEVICE_TYPE = "Z91";
            initSDK();
        }
    }

    private void plant(DebugTree debugTree) {
    }

    public static Context getAppContext() {
        return Topitup.context;
    }

    public static void connectBluetooth(Activity activity) {
        bluetoothOperation(activity);
    }
    public static void bluetoothOperation(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
Log.e("bluetooth","if..........");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if needed
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1001);
                }
                return;
            }
            if (context instanceof Activity) {
                Toast.makeText(context, "Bluetooth is  available", Toast.LENGTH_LONG).show();

                ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.e("bluetooth","else..........");

            if (mService == null) {
                mService = new BluetoothService(context, mBluetoothHandler);
            }
        }

        if (context instanceof Activity) {
            Log.e("bluetooth","device list..........");

            Intent serverIntent = new Intent(context, DeviceListActivity.class);
            ((Activity) context).startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }

/*    public static void bluetoothOperation(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ((Activity)context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null) {
                mService = new BluetoothService(context, mBluetoothHandler);
            }
        }

        Intent serverIntent = new Intent(context, DeviceListActivity.class);
        ((Activity)context).startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }*/


    // SendDataByte method - you'll need to implement this based on your PrinterCommand class
   /* private static void SendDataByte(byte[] data, Context context) {
        if (mService != null && isBluetoothConnected) {
            mService.write(data);
        }
    }*/
    public static void SendDataByte(byte[] data, Context con) {
//        BluetoothService  mServiceNew = new BluetoothService(con, mHandler);
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(con, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }
    public static void SendDataString(String data, Context con) {

//      BluetoothService  mServiceNew = new BluetoothService(con, mHandler);

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(con, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void checkServiceRunning(Context con) {
        Log.e("mycontentReceiver", ".......checkservice");

        AudioManager mAudioManager = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);
        Log.e("mycontentReceiver", ".......checkservice" + mAudioManager.isMusicActive());

        if (mAudioManager.isMusicActive()) {
            addValues("stop", "stop");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                addValues("start", "start");
            }
        }, 30000);
    }

    private static void addValues(String name, String price) {
        String productName = name;
        String productPrice = price;

        Log.e("mycontentReceiver", ".......addValues00");
        if (!productName.isEmpty() && !productPrice.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MyContentProvider.name, productName);
            values.put(MyContentProvider.price, productPrice);

            Cursor cursor = context.getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null, null);

            if (cursor.moveToFirst()) {
                Log.e("update", "........" + name);
                context.getContentResolver().update(MyContentProvider.CONTENT_URI, values, null, null);
            } else {
                Log.e("insert", "........");
                context.getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
            }
        }
    }

    private boolean initSDK() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void resetPaperStatus() {
        if (DEVICE_TYPE.equals("Z91")) {
            mInstance.checking_paper_status = -1;
        }
        if (DEVICE_TYPE.equals("ZKC")) {
            //
        }
    }

    public static int getBatteryPercentage(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            IntentFilter iFilter = new IntentFilter(ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;
            return (int) (batteryPct * 100);
        }
    }

    public boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public static void checkOutOfPaper() {
        // Implementation as per original
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized Topitup getInstance() {
        return mInstance;
    }

    private HandlerUtils.MyHandler q1handler;

    private final HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent() {
        @Override
        public void handlerIntent(Message msg) {
            mInstance.checking_paper_status = 1;

            switch (msg.what) {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:
                    break;
                case MSG_IS_BUSY:
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    mInstance.checking_paper_status = 0;
                    break;
                case MSG_PAPER_EXISTS:
                    break;
                case MSG_THP_HIGH_TEMP:
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
                    break;
                default:
                    break;
            }
        }
    };

    private final BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.d(TAG, "IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG, "IPosPrinterStatusListener action = " + action);
            if (action.equals(PRINTER_NORMAL_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0);
            } else if (action.equals(PRINTER_PAPERLESS_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0);
            } else if (action.equals(PRINTER_BUSY_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_IS_BUSY, 0);
            } else if (action.equals(PRINTER_PAPEREXISTS_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0);
            } else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0);
            } else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0);
            } else if (action.equals(GET_CUST_PRINTAPP_PACKAGENAME_ACTION)) {
                String mPackageName = intent.getPackage();
                Log.d(TAG, "*******GET_CUST_PRINTAPP_PACKAGENAME_ACTION：" + action + "*****mPackageName:" + mPackageName);
            } else {
                q1handler.sendEmptyMessageDelayed(MSG_TEST, 0);
            }
        }
    };

    // Printer status constants
    private final int PRINTER_NORMAL = 0;
    static final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;
    private int printerStatus = 0;

    // Printer action constants
    private final String PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservic e.NORMAL_ACTION";
    private final String PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";
    private final String GET_CUST_PRINTAPP_PACKAGENAME_ACTION = "android.print.action.CUST_PRINTAPP_PACKAGENAME";

    // Message constants
    private final int MSG_TEST = 1;
    private final int MSG_IS_NORMAL = 2;
    private final int MSG_IS_BUSY = 3;
    private final int MSG_PAPER_LESS = 4;
    private final int MSG_PAPER_EXISTS = 5;
    private final int MSG_THP_HIGH_TEMP = 6;
    private final int MSG_THP_TEMP_NORMAL = 7;
    private final int MSG_MOTOR_HIGH_TEMP = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE = 10;

    // Loop print constants
    private final int MULTI_THREAD_LOOP_PRINT = 1;
    private final int INPUT_CONTENT_LOOP_PRINT = 2;
    private final int DEMO_LOOP_PRINT = 3;
    private final int PRINT_DRIVER_ERROR_TEST = 4;
    private final int DEFAULT_LOOP_PRINT = 0;

    private int loopPrintFlag = DEFAULT_LOOP_PRINT;
    private final byte loopContent = 0x00;
    private final int printDriverTestCount = 0;

    private static final String TAG = "IPosPrinterTestDemo";

    // Session management methods
    public void startUserSession() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI", 86400000);
        Log.e("session timer", "timer...start..." + TIMEOUT_IN_MILLI);
        cancelTimer();
        cancelTimerAdmin();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        listener.onSessionLogout();
                    }
                });
            }
        }, TIMEOUT_IN_MILLI);
    }

    public void startUserSessionForActive() {
        Log.e("session timer", "start user session timer");
        cancelTimer();
        cancelTimerAdmin();
        startUserSession();
    }

    public void stopUserSession() {
        Log.e("session timer", "start user session");
        cancelTimer();
        cancelTimerAdmin();
    }

    private void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }

    public void registerSessionListener(LogoutListener listener) {
        this.listener = listener;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Log.e("session timer", "cancel timer");
    }

    public void onUserInteracted() {
        startUserSession();
    }

    public void startUserSessionAdmin() {
        Log.e("session timer", "start user session admin");
        long TIMEOUT_IN_MILLI_ADMIN = 120000;
        cancelTimerAdmin();
        timerAdmin = new Timer();
        timerAdmin.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        listenerAdmin.onSessionAdminLogout();
                    }
                });
            }
        }, TIMEOUT_IN_MILLI_ADMIN);
    }

    public static void stopTimers() {
        Log.e("session timer", "stop timers");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timerAdmin != null) {
            timerAdmin.cancel();
            timerAdmin.purge();
            timerAdmin = null;
        }
    }

    public void registerSessionListenerAdmin(LogoutAdminListener listenerAdmin) {
        this.listenerAdmin = listenerAdmin;
    }

    private void cancelTimerAdmin() {
        Log.e("session timer", "cancel timer admin");
        if (timerAdmin != null) {
            timerAdmin.cancel();
            timerAdmin.purge();
            timerAdmin = null;
        }
    }

    public void onUserInteractedAdmin() {
        startUserSessionAdmin();
    }

    public long getScreensavertime() {
        return screensavertime;
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void created() {
        Log.d(getClass().getSimpleName(), "ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void started() {
        Log.d(getClass().getSimpleName(), "ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumed() {
        Log.d(getClass().getSimpleName(), "ON_RESUME");
        wasInBackground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void paused() {
        Log.d(getClass().getSimpleName(), "ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopped() {
        Log.d(getClass().getSimpleName(), "ON_STOP");
        wasInBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyed() {
        Log.d(getClass().getSimpleName(), "ON_DESTROY");
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

    public void checkWifiSimInternet(Context con) {
        unregisterNetworkCallback();

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.e("internet availablility",".......available");

                Executors.newSingleThreadExecutor().execute(() -> {
                    if (checkConnection(getApplicationContext())) {
                        Log.e("internet availablility","....if...available");
                        changeWifi(4, con);
                        changeServer("");
                    } else {
                        Log.e("internet availablility","...else....available");
                        changeWifi(0, con);
                        changeServer("network");
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Log.e("internet availablility","..not.....available");
                changeWifi(0, con);
                changeServer("network");
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int wifilevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        changeWifi(wifilevel, con);

        if(wifilevel == 0){
            changeServer("");
        }

        if (!isAirplaneModeOn(con)) {
            if (con.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                checkNetwork(con);
            }
        } else {
            addSignalSim2(0, con);
            addSignalSim1(0, con);
        }
    }

    public void unregisterNetworkCallback() {
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (IllegalArgumentException e) {
                // Callback was already unregistered
            }
            networkCallback = null;
        }
    }

    public static boolean isInternetAvailable() {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
            int status = p.waitFor();
            return (status == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void addSignalSim1(int signalStrength, Context con) {
        if (signalStrength == 0) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.no_signal));
            }
        } else {
            if (signalStrength > 90) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal4));
                }
            } else if (signalStrength > 70 && signalStrength < 90) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));
                }
            } else if (signalStrength < 70 && signalStrength > 50) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal2));
                }
            } else if (signalStrength < 50) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal1));
                }
            }
        }
    }

    public void addSignalSim2(int signalStrength, Context con) {
        if (signalStrength == 0) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.no_signal));
            }
        } else {
            if (signalStrength > 90) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal4));
                }
            } else if (signalStrength > 70 && signalStrength < 90) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));
                }
            } else if (signalStrength < 70 && signalStrength > 50) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal2));
                }
            } else if (signalStrength < 50) {
                if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal1));
                }
            }
        }
    }

    private boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public void changeWifi(int wifilevel, Context con) {
        if (wifilevel == 4) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_wifi.setImageDrawable(con.getDrawable(R.drawable.wifi_3_bars));
            }
        } else if (wifilevel == 3) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_wifi.setImageDrawable(con.getDrawable(R.drawable.wifi_2_bars));
            }
        } else if (wifilevel == 2) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_wifi.setImageDrawable(con.getDrawable(R.drawable.wifi_1_bar));
            }
        } else if (wifilevel == 1) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.img_wifi.setImageDrawable(con.getDrawable(R.drawable.wifi_1_bar));
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void checkNetwork(Context con) {
        String imeiSIM1, imeiSIM2;
        boolean isSIM1Ready;
        boolean isSIM2Ready;
        TelephonyManager telephonyManager = ((TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE));
        isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        isSIM2Ready = false;
        try {
            isSIM1Ready = getSIMStateBySlot(con, "getSimState", 0);
            isSIM2Ready = getSIMStateBySlot(con, "getSimState", 1);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
            try {
                isSIM1Ready = getSIMStateBySlot(con, "getSimState", 0);
                isSIM2Ready = getSIMStateBySlot(con, "getSimState", 1);
            } catch (GeminiMethodNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        getCellSignalStrength(con);
        SubscriptionManager subs = (SubscriptionManager) con.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions
            } else {
                List<CellInfo> allCellinfo = telephonyManager.getAllCellInfo();
                List<SubscriptionInfo> activeSubscriptionInfoList = subs.getActiveSubscriptionInfoList();
                if (activeSubscriptionInfoList != null) {
                    List<CellInfo> regCellInfo = getRegisteredCellInfo(allCellinfo);

                    if (regCellInfo.size() == 1) {
                        int strength111 = 0;
                        CellInfo info1 = regCellInfo.get(0);
                        if (info1 instanceof CellInfoLte) {
                            strength111 = ((CellInfoLte) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoGsm) {
                            strength111 = ((CellInfoGsm) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoCdma) {
                            strength111 = ((CellInfoCdma) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoWcdma) {
                            strength111 = ((CellInfoWcdma) info1).getCellSignalStrength().getDbm();
                        } else {
                            strength111 = 0;
                        }
                        i("subs " + strength111);
                        String strength = String.valueOf(strength111).substring(1);
                        i("sim1   " + strength111 + "  " + Integer.parseInt(strength));
                        addSignalSim1(Integer.parseInt(strength), con);
                        addSignalSim2(0, con);
                    } else {
                        int strength1 = 0;
                        if (isSIM1Ready) {
                            if (regCellInfo.size() > 0) {
                                CellInfo info1 = regCellInfo.get(0);
                                if (info1 instanceof CellInfoLte) {
                                    strength1 = ((CellInfoLte) info1).getCellSignalStrength().getDbm();
                                } else if (info1 instanceof CellInfoGsm) {
                                    strength1 = ((CellInfoGsm) info1).getCellSignalStrength().getDbm();
                                } else if (info1 instanceof CellInfoCdma) {
                                    strength1 = ((CellInfoCdma) info1).getCellSignalStrength().getDbm();
                                } else if (info1 instanceof CellInfoWcdma) {
                                    strength1 = ((CellInfoWcdma) info1).getCellSignalStrength().getDbm();
                                } else {
                                    strength1 = 0;
                                }
                                i("subs " + strength1);
                                String strength = String.valueOf(strength1).substring(1);
                                i("sim1   " + strength + "  " + Integer.parseInt(strength));
                                addSignalSim1(Integer.parseInt(strength), con);
                            } else {
                                addSignalSim1(91, con);
                            }
                        } else {
                            addSignalSim1(0, con);
                        }

                        if (isSIM2Ready) {
                            if (regCellInfo.size() > 0) {
                                CellInfo info2 = regCellInfo.get(1);
                                int strength2;
                                if (info2 instanceof CellInfoLte) {
                                    strength2 = ((CellInfoLte) info2).getCellSignalStrength().getDbm();
                                } else if (info2 instanceof CellInfoGsm) {
                                    strength2 = ((CellInfoGsm) info2).getCellSignalStrength().getDbm();
                                } else if (info2 instanceof CellInfoCdma) {
                                    strength2 = ((CellInfoCdma) info2).getCellSignalStrength().getDbm();
                                } else if (info2 instanceof CellInfoWcdma) {
                                    strength2 = ((CellInfoWcdma) info2).getCellSignalStrength().getDbm();
                                } else {
                                    strength2 = 0;
                                }
                                i("subs " + subs);
                                String strength11 = String.valueOf(strength2).substring(1);
                                i("sim2   " + strength11 + "  " + Integer.parseInt(strength11));
                                addSignalSim2(Integer.parseInt(strength11), con);
                            } else {
                                addSignalSim2(91, con);
                            }
                        } else {
                            addSignalSim2(0, con);
                        }
                    }
                } else {
                    addSignalSim1(0, con);
                    addSignalSim2(0, con);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<CellInfo> getRegisteredCellInfo(List<CellInfo> cellInfos) {
        ArrayList<CellInfo> registeredCellInfos = new ArrayList<>();
        if (!cellInfos.isEmpty()) {
            for (int i = 0; i < cellInfos.size(); i++) {
                if (cellInfos.get(i).isRegistered()) {
                    registeredCellInfos.add(cellInfos.get(i));
                }
            }
        }
        return registeredCellInfos;
    }

    public static int getCellSignalStrength(Context context) {
        int strength = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
        } else {
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            if (cellInfos != null && cellInfos.size() > 0) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            strength = cellSignalStrengthWcdma.getDbm();
                        }
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        @SuppressLint("MissingPermission") CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = cellSignalStrengthGsm.getDbm();
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        @SuppressLint("MissingPermission") CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = cellSignalStrengthLte.getDbm();
                        break;
                    }
                }
                return strength;
            }
        }
        return strength;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        boolean isReady = false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class telephonyClass = Class.forName(telephony.getClass().getName());
            Class[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return isReady;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class telephonyClass = Class.forName(telephony.getClass().getName());
            Class[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if (ob_phone != null) {
                imei = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return imei;
    }

    private static class GeminiMethodNotFoundException extends Exception {
        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

    public void changeServer(String type) {
        Log.e("network change", "receiver.............");
        if (type.equals("server")) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.instance.updateUI("server");
            }
        } else if (type.equals("network")) {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.instance.updateUI("network");
            }
        } else {
            if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.instance.updateUI("");
            }
        }
    }
}