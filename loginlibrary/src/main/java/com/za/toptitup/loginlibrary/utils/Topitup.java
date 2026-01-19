package com.za.toptitup.loginlibrary.utils;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static timber.log.Timber.DebugTree;
import static timber.log.Timber.i;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
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
import androidx.lifecycle.ProcessLifecycleOwner;

import java.io.IOException;
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
    public static String POSUSER_NAME = "";
    public static String RICA_REG = "";
    //public static Account myAccount;

    public static final String AUTHORITY = "za.co.topitup.app";
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
   /* public static int getCellSignalStrength(Context context) {
        int strength = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

        } else {
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
            if (cellInfos != null && cellInfos.size() > 0) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

//        Log.e("sim signal ..", "strength" + strength);
        return strength;
    }*/


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

        /*RealmConfiguration config = new RealmConfiguration
                .Builder()
                //.directory(file )
                //.name("default.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);*/

        if (BuildConfig.DEBUG) {
            plant(new DebugTree());
        }

        //if (DEBUG) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        //}


        try {

            PackageInfo pInfo = mInstance.getPackageManager().getPackageInfo(getPackageName(), 0);
            //String version = pInfo.versionName;
            // APP_VERSION = String.valueOf(pInfo.versionCode);
            // APP_VERSION = String.valueOf("Ver"+pInfo.versionName);
            APP_VERSION = String.valueOf(pInfo.versionName);
            APP_BUILD_VERSION_CODE = String.valueOf(pInfo.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }


    /*    batteryReceiver = new BatteryReceiver();
                registerReceiver(batteryReceiver, new IntentFilter(ACTION_BATTERY_CHANGED));*/
        //Timber.e( Environment.getExternalStorageDirectory().toString());


        //String restoredText = prefs.getString("text", null);
        //if (restoredText != null) {
        //TIU_LICENSE  = prefs.getString("TIU_LICENSE", "DEMO762c-a473-11e3-a836-001e679706de");   // vstream
        //TIU_LICENSE = prefs.getString("TIU_LICENSE", "DEMO5f89-1628-11e8-a0d1-001e6779cd30");
        // TIU_LICENSE = prefs.getString("TIU_LICENSE", "fce666f6-1896-11e9-9181-0cc47ac0400e");
        TIU_LICENSE = prefs.getString("TIU_LICENSE", "");
        //}

        PRINT_BARCODE = prefs.getString("setting_print_barcode", "0");
        DEVICE_SLNO = getSerialNumber();

        Log.e("deviec serial Number", "......" + DEVICE_SLNO);
        //String manufacturer = android.os.Build.MANUFACTURER;
        //Timber.i("manufacturer:" + manufacturer);

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
                        mPrinter.setPrintFontType(getAppContext(), "");//fonnts/PraduhhTheGreat.ttf
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if (Build.MANUFACTURER.equals("CHUWI") || Build.MANUFACTURER.equals("Mobicel") || Build.MANUFACTURER.equals("samsung") || Build.MANUFACTURER.equals("wiseasy") || Build.MANUFACTURER.equals("rockchip")) {

            if (Build.MODEL.equals("Hi10 XPro")) {
                DEVICE_TYPE = "TABLET";

            } else {
                DEVICE_TYPE = "MOBILE";
            }

        } else if (Build.ID.equals("MRA58K")) {

            DEVICE_TYPE = "Q1";

            //Timber.i("DEVICE: Q1");


            //绑定服务
            Intent intent = new Intent();
            intent.setPackage("com.iposprinter.iposprinterservice");
            intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
            //startService(intent);
          //  bindService(intent, connectService, Context.BIND_AUTO_CREATE);
            //注册打印机状态接收器
            IntentFilter printerStatusFilter = new IntentFilter();
            printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
            printerStatusFilter.addAction(GET_CUST_PRINTAPP_PACKAGENAME_ACTION);

//            registerReceiver(IPosPrinterStatusListener, printerStatusFilter, Context.RECEIVER_NOT_EXPORTED);

            mInstance.checking_paper_status = 1;

            q1handler = new HandlerUtils.MyHandler(iHandlerIntent);

        /*    callback = new IPosPrinterCallback.Stub() {

                @Override
                public void onRunResult(final boolean isSuccess) throws RemoteException {
                    i("result:" + isSuccess + "\n");
                }

                @Override
                public void onReturnString(final String value) throws RemoteException {
                    i("result:" + value + "\n");
                }
            };*/


//            ThreadPoolManager.getInstance().executeTask(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        mIPosPrinterService.printerInit(callback);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

//            callback = new IPosPrinterCallback.Stub() {
//
//                @Override
//                public void onRunResult(final boolean isSuccess) throws RemoteException {
//                    Log.i(TAG, "result:" + isSuccess + "\n");
//                }
//
//                @Override
//                public void onReturnString(final String value) throws RemoteException {
//                    Log.i(TAG, "result:" + value + "\n");
//                }
//            };


        } else if (Build.ID.equals("NRD90M")) {

            //bindService();

        } else {
            DEVICE_TYPE = "Z91";

            initSDK();

//                DEVICE_TYPE = "Z91";
//
//                initSDK();
//
//                MposHandler.getInstance(this).addSwipeListener(this);
//
//                Topitup.handler.addSwipeListener(new SwipeListener() {
//
//                    @Override
//                    public void onParseData(SwipeEvent event) {
//
//                        //Timber.i("PRINT: zzzz " + event.getValue() );
//                        // sendMessage("onParseData:" + event.getValue());
//                    }
//
//                    @Override
//                    public void onDisconnected(SwipeEvent event) {
//                    }
//
//                    @Override
//                    public void onConnected(SwipeEvent event) {
//                    }
//
//                    @Override
//                    public void onCardDetect(CardDetected type) {
//                    }
//
//                    @Override
//                    public void onPrintStatus(PrintStatus status) {
//
//                        //Timber.i("PRINT: onPrintStatus yy:" + status.toString() );
//
//                        if (status == PrintStatus.IMAGES) {
//
//                        } else if (status == PrintStatus.EXIT) {
//
//                            mInstance.checking_paper_status = 1;
//
//                            // setting.mPosExitPrint();
//                            // new Thread(new Runnable() {
//                            // @Override
//                            // public void run() {
//                            // // TODO Auto-generated method stub
//                            // setting.prnStatus();
//                            // }
//                            // }).start();
//                        } else if (status == PrintStatus.NO_PAPER) {
//
//                            mInstance.checking_paper_status = 0;
//
//                        } else if (status == PrintStatus.LACK_PAPER) {
//
//                            mInstance.checking_paper_status = 0;
//
//                        } else {
//
//                            mInstance.checking_paper_status = 1;
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onEmvStatus(EmvStatus arg0) {
//                    }
//
//                });


        }


    }

    private void plant(DebugTree debugTree) {

    }


    public static Context getAppContext() {
        return Topitup.context;
    }


   /* public void bindService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }*/



/*
    private final ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.e("client", "onServiceDisconnected");
            mIzkcService = null;
            //Toast.makeText(BaseActivity.this, getString(R.string.service_bind_fail), Toast.LENGTH_SHORT).show();
            //发送消息绑定失败 send message to notify bind fail
            //sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_FAIL);

            //Toasty.error(mContext, "SEVICE_BIND_FAIL", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.e("client", "onServiceConnected");
            mIzkcService = IZKCService.Stub.asInterface(service);
            if (mIzkcService != null) {
                try {
                    //Toast.makeText(BaseActivity.this, getString(R.string.service_bind_success), Toast.LENGTH_SHORT).show();
                    //获取产品型号 get product model
                    DEVICE_MODEL = mIzkcService.getDeviceModel();
                    //设置当前模块 set current function module
                    mIzkcService.setModuleFlag(8);

                    mIzkcService.sendRAWData("printer", new byte[]{0x1b, 0x4e, 0x04, 0x01});

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //发送消息绑定成功 send message to notify bind success
                //sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_SUCCESS);

                //Toasty.success(mContext, "SEVICE_BIND_SUCCESS", Toast.LENGTH_LONG).show();

            }
        }
    };
*/


    public static void checkServiceRunning() {
        Log.e("mycontentReceiver", ".......checkservice");

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.e("mycontentReceiver", ".......checkservice" + mAudioManager.isMusicActive());

        if (mAudioManager.isMusicActive()) {
            addValues("stop", "stop");

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                addValues("start", "start");


            }
        }, 10000);
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
            //Key-value object to add value in the database
            ContentValues values = new ContentValues();
            values.put(MyContentProvider.name, productName);
            values.put(MyContentProvider.price, productPrice);
            //insert data using Content URI

            Cursor cursor = context.getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null, null);

//            Toast.makeText(activity_main.this,"Download request send",Toast.LENGTH_LONG).show();
            if (cursor.moveToFirst()) {
                Log.e("update", "........" + name);
                context.getContentResolver().update(MyContentProvider.CONTENT_URI, values, null, null);

            } else {
                Log.e("insert", "........");

                context.getContentResolver().insert(MyContentProvider.CONTENT_URI, values);

            }
//            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }

        // displaying a toast message
//        Toast.makeText(getBaseContext(), "New Record Inserted  "+name, Toast.LENGTH_LONG).show();
    }

    /**** SDK ***/
    private boolean initSDK() {

        try {

            // Init SDK,call singleton function,so that you can keeping on the
            // connect in the whole life cycle
            //this.handler = MposHandler.getInstance(this);
            //this.setting = Settings.getInstance(handler);
            // power on the device when you need to read card or print
            //this.setting.mPosPowerOn();

            // for 90,delay 1S and then connect
            // Thread.sleep(1000);
            // connect device via serial port
//            if (!handler.isConnected()) {
//
//                if (handler.connect()) {
//                    Timber.i("PRINT: POS Handler Connected");
//                } else {
//                    Timber.i("PRINT: POS Handler NOT Connected");
//                }
//
//            } else {
//
//                handler.close();
//
//                if (handler.connect()) {
//                    Timber.i("PRINT: POS Handler Connected");
//                } else {
//                    Timber.i("PRINT: POS Handler NOT Connected");
//                }
//
//                //txt_printer_status.setText(String.valueOf(handler.connect()));
//                //Toasty.normal(mContext, "Printer ReConnect:" + handler.connect(), 500).show();
//            }

            return true;

        } catch (Exception e) {

            //Toasty.error(mContext, e.getMessage(), 3000, true).show();
            return false;

        }

        //handler.setShowLog(true);

    }


    public static void resetPaperStatus() {

        if (DEVICE_TYPE.equals("Z91")) {
            mInstance.checking_paper_status = -1;
        }

        if (DEVICE_TYPE.equals("ZKC")) {
            //
        }

//        if (DEVICE_TYPE.equals("Q1")) {
//            mInstance.checking_paper_status = -1;
//        }


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

/*
    public static int getQ1PrinterSts() {
        int status = 1;
        if (DEVICE_TYPE.equals("Q1")) {
            try {
                //mIzkcService.getPrinterStatus();
                status = mIPosPrinterService.getPrinterStatus();
            } catch (Exception e) {
                //
            }
//|| status == 4 commented
            if (status == 2 || status == 3 || status == 5) {
                return 0;
            } else {
                return 1;
            }

        }

        return 1;
    }
*/

    public static void checkOutOfPaper() {

//        if (DEVICE_TYPE.equals("Z91")) {
//            mInstance.setting.mPosEnterPrint();
//        }


        //   if (DEVICE_TYPE.equals("Q1")) {


//            try {
//                if(mInstance.mIPosPrinterService.getPrinterStatus() == PRINTER_PAPERLESS) {
//                    mInstance.checking_paper_status = 0;
//                } else {
//                    mInstance.checking_paper_status = 1;
//                }
//                Timber.i("getPrinterStatus: " + mInstance.checking_paper_status);
//            } catch (Exception ex){
//                Timber.i("getPrinterStatus: " + ex.getMessage() );
//                mInstance.checking_paper_status = 1;
//            }

//            try {
//                //mInstance.mIPosPrinterService.printerInit(mInstance.callback);
//                Integer ret =  mInstance.mIPosPrinterService.getPrinterStatus();
//
//
//                Timber.i("getPrinterStatus: " + ret.toString());
//
//            } catch (Exception ex) {
//                //
//            }
        //    }

        //if (Topitup.setting.mPosEnterPrint()) {
        //    Timber.i("PRINT: checkOutOfPaper TRUE ");
        //} else {
        //    Timber.i("PRINT: checkOutOfPaper FALSE ");
        //}
//
//          new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (Topitup.setting.mPosEnterPrint()) {
//
//                    Timber.i("PRINT: checkOutOfPaper TRUE ");
//
//
//                } else {
//                    Timber.i("PRINT: checkOutOfPaper FALSE ");
//                }
//            }
//        }).start();

        //   boolean ret = this.handler.mPosEnterPrint();


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    public static synchronized Topitup getInstance() {
        return mInstance;
    }


    private HandlerUtils.MyHandler q1handler;


    private final HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent() {
        @Override
        public void handlerIntent(Message msg) {

            //Timber.i("getPrinterStatus : " + msg.what);

            mInstance.checking_paper_status = 1;

            switch (msg.what) {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:

                    // Timber.i("getPrinterStatus : NORMAL");
/*
                    if (getPrinterStatus() == PRINTER_NORMAL) {
                        // mInstance.checking_paper_status = 1;
                    }*/
                    break;
                case MSG_IS_BUSY:
                    // Toast.makeText(mContext, "printer_is_working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:

                    loopPrintFlag = DEFAULT_LOOP_PRINT;

                    mInstance.checking_paper_status = 0;
//                    Timber.i("PAPER: OUT OF PAPER");


                    // Toast.makeText(mContext,"out_of_paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:

                    //Timber.i("PAPER: GOT PAPER");
                    // mInstance.checking_paper_status = 1;

                    //Toast.makeText(mContext, "exists_paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    //  Toast.makeText(mContext, "printer_high_temp_alarm", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    // Toast.makeText(mContext, "motor_high_temp_alarm", Toast.LENGTH_SHORT).show();
                    // handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                   // printerInit();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
                    //Toast.makeText(mContext, "printer_current_task_print_complete", Toast.LENGTH_SHORT).show();
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
            } else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
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


    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
    static final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;
    /*打印机当前状态*/
    private int printerStatus = 0;

    /*定义状态广播*/
    private final String PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservic e.NORMAL_ACTION";
    private final String PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";
    private final String GET_CUST_PRINTAPP_PACKAGENAME_ACTION = "android.print.action.CUST_PRINTAPP_PACKAGENAME";

    /*定义消息*/
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

    /*循环打印类型*/
    private final int MULTI_THREAD_LOOP_PRINT = 1;
    private final int INPUT_CONTENT_LOOP_PRINT = 2;
    private final int DEMO_LOOP_PRINT = 3;
    private final int PRINT_DRIVER_ERROR_TEST = 4;
    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;
    private final byte loopContent = 0x00;
    private final int printDriverTestCount = 0;


    private static final String TAG = "IPosPrinterTestDemo";






    public void startUserSession() {

        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI", 86400000);
        //Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
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

                        //                    //
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
        // listener.onSessionLogout();

        this.listener = listener;

    }

    private void cancelTimer() {

        //Toast.makeText(Topitup.this, "cancel called"+timer,Toast.LENGTH_SHORT).show();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Log.e("session timer", "cancel timer");

    }

    public void onUserInteracted() {
        //Toast.makeText(Topitup.this, "user interacted",Toast.LENGTH_SHORT).show();
        startUserSession();
    }


//Admin

    public void startUserSessionAdmin() {
        Log.e("session timer", "start user session admin");

    /*    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",86400000);
       Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();*/

        long TIMEOUT_IN_MILLI_ADMIN = 120000;
        cancelTimerAdmin();
//Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI_ADMIN, Toast.LENGTH_LONG).show();
        timerAdmin = new Timer();
        timerAdmin.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {

                        listenerAdmin.onSessionAdminLogout();

                        //                    //
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
        // listener.onSessionLogout();

        this.listenerAdmin = listenerAdmin;

    }

    private void cancelTimerAdmin() {
        Log.e("session timer", "cancel timer admin");


        //Toast.makeText(Topitup.this, "cancel called"+timer,Toast.LENGTH_SHORT).show();
        if (timerAdmin != null) {
            timerAdmin.cancel();
            timerAdmin.purge();
            timerAdmin = null;
        }
    }

    public void onUserInteractedAdmin() {
        //Toast.makeText(Topitup.this, "user interacted",Toast.LENGTH_SHORT).show();
        startUserSessionAdmin();
    }

    public long getScreensavertime() {
        return screensavertime;
    }


    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
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

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))

                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))

                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

/*
    public void checkWifiSimInternet(Context con) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder().build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);

                Log.e("internet availablility",".......available");
                // Check real internet access
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
        });

        *//*try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkRequest networkRequest = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        changeWifi(4, con);
                        changeServer("");
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        changeWifi(0, con);
                        changeServer("network");
                    }
                });
            }
        } catch (Exception e) {

        }*//*
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int wifilevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        changeWifi(wifilevel, con);

        if(wifilevel == 0){
            changeServer("");

        }

       *//* if (!isConnected(con)) {
            changeServer("network");
        } else {

        }*//*
        if (!isAirplaneModeOn(con)) {
            if (con.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                checkNetwork(con);
            }
        } else {

            addSignalSim2(0, con);
            addSignalSim1(0, con);

        }
    }*/




    public void checkWifiSimInternet(Context con) {
        // Unregister any existing callback first
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

        // Rest of your WiFi checking code...
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
        /*try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL("https://clients3.google.com/generate_204").openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500); // 1.5 seconds timeout
            urlConnection.connect();
            return (urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0);
        } catch (IOException e) {
            return false;
        }*/
    }
    public void addSignalSim1(int signalStrength, Context con) {

//        Log.e("signal strength", "...........strength...." + signalStrength);
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

//        Log.e("signal strength", "...........strength...." + signalStrength);
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
//                Log.e("signal strength", "........2...else. 70..." + signalStrength);

                 if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));

                }

            } else if (signalStrength < 70 && signalStrength > 50) {
//                Log.e("signal strength", ".....2......else. 50..." + signalStrength);

                 if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal2));

                }


            } else if (signalStrength < 50) {
//                Log.e("signal strength", "......2.....else. 40..." + signalStrength);

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
                //Call here for next manufacturer's predicted method name if you wish
                e1.printStackTrace();
            }
        }
        getCellSignalStrength(con);
        SubscriptionManager subs = (SubscriptionManager) con.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

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
//                    addSignalSim2(strength2);

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
            // TODO: Consider calling
            //  ActivityCompat#requestPermissions

        } else {
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
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

//        Log.e("sim signal ..", "strength" + strength);
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
               /* activity_login.rl_network.setVisibility(View.VISIBLE);
                activity_login.rl_server.setVisibility(View.INVISIBLE);*/
            }
        } else {
           if (activity_login.fromScreen.equals("activity_login")) {
                activity_login.instance.updateUI("");
            }
        }
    }

/*
    private static class GeminiMethodNotFoundException extends Exception {
        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
*/


}