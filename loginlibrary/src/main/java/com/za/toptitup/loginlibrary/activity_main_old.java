package com.za.toptitup.loginlibrary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.NetworkChangeReceiver;
import com.za.toptitup.loginlibrary.utils.ScreenStateReceiver;
import com.za.toptitup.loginlibrary.utils.Topitup;

import static org.apache.commons.lang3.StringUtils.join;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.mobapphome.androidappupdater.tools.AAUpdaterController;


//NavigationView.OnNavigationItemSelectedListener
public class activity_main_old extends BaseActivity implements View.OnClickListener {


    public static final String PREF_FILE = "TIUPREF";
    public static final String KEY_SP_LAST_INTERACTION_TIME = "KEY_SP_LAST_INTERACTION_TIME";
    private static long TIMEOUT_IN_MILLI;
    private Handler refreshHandler;
    private Runnable runnable;

    Realm realm;
    protected Topitup app;

    private BroadcastReceiver mNetworkReceiver;
    String manufacturer;

    static String[]  voucher_print_footer =  {"", "          Top it Up", "      www.itopitup.co.za", "" };
    static String[]  voucher_print_dtm_cashier = { "", "Date        Time Cashier", "", "" };


    //PrinterClassSerialPort printerClass = null;
    private final int cutTimes = 1;

    private Thread autoprint_Thread;
    boolean isPrint = true;
    int times = 1500;// Automatic print time interval
    String thread = "readThread",is_admin;

    private final String device = "/dev/ttyMT0";
    private final int baudrate = 115200;// 38400

    long startTimes =  0;
    long endTimes = 0;
    long timeSpace = 0;

    int delay = 5000;

    private final boolean close_printer = true;

    Context mContext;

    ProgressDialog mProgressDialog;

    private Button btn_startstop;
    private Button btn_print;

    int counter = 0;
    TextView textCount;
    TextView printDetail;
    TextView txtBalance;
    //TextView txt_printer_status;
    private ScreenStateReceiver mReceiver;
    RealmResults<fin_balance> tiu_fin_balance;
    SharedPreferences settings;
    public static RelativeLayout rl_network,rl_server;
    LinearLayout tiu_title_bar_new;

    public static ImageView txt_battery,img_wifi,img_network,img_network2;

    //public MposHandler handler;

    //private ProgressBar spinner;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);



//    public static int module_flag = 8;
//    DetectPrinterThread mDetectPrinterThread;
//    //线程运行标志 the running flag of thread
//    private boolean runFlag = true;
//    //打印机检测标志 the detect flag of printer
//    private boolean detectFlag = true;
//    //打印机连接超时时间 link timeout of printer
//    private float PINTER_LINK_TIMEOUT_MAX = 30 * 1000L;

    public static activity_main_old instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        instance = this;

//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));   if (getIntent().getBooleanExtra("crash", false)) {
//            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
//        }






//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////        }

        setContentView(R.layout.activity_main);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();



        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("");

//        spinner=(ProgressBar)findViewById(R.id.progressBar);
//        spinner.setVisibility(View.GONE);
//


        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        //txt_printer_status = (TextView)findViewById(R.id.txt_printer_status);

       // btn_startstop = findViewById(R.id.btn_startstop);
        btn_print = findViewById(R.id.btn_print);
       // printDetail = findViewById(R.id.printDetail);
      //  txtBalance = findViewById(R.id.txtBalance);
     //   textCount = findViewById(R.id.changingText);
        activity_login.fromScreen = "activity_main_old";


        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);

        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);

        ((Topitup) getApplication()).checkWifiSimInternet(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
       tiu_clock.setText(strDate+" ");

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
                String strDate = sdf.format(c.getTime());
               tiu_clock.setText(strDate+" ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);
        if (Topitup.POSUSER_NAME.equals("")) {

            Intent intent = new Intent(this, activity_login.class);
            startActivity(intent);
            finish();

        }



        /* TIU HEADER */
        settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name","");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier","0");
        final String setting_balance_login = settings.getString("setting_balance_login","0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen","0");
        final String setting_balance_admin = settings.getString("setting_balance_admin","0");
        final String str_balance_admin_bills = settings.getString("setting_balance_admin_bills","0");
        final String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission","0");
        final String setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe","0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");





        //Toast.makeText(activity_main.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);


        final View view2 = findViewById(R.id.view2);        final View view3 = findViewById(R.id.view3);

        try {

            final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
//            if(tiu_settings.equals(null)){
//                Toasty.error(mContext, "Unable to login", 5000, true).show();
//                return;
//            }else {

            tiu_title_outlet.setText(tiu_settings.account_number);
/*
            if(tiu_settings.company_name.length()>10){
                tiu_title_outlet.setText(tiu_settings.company_name.substring(0,10) + "... | " + tiu_settings.account_number);
            }
            else {

                tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
            }*/
            if(Topitup.POSUSER_NAME.length()>13) {
                if (Topitup.IS_ADMIN.equals("1")) {
                    tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0,12) + "...");
                    tiu_user_name_.setText("Admin");


                } else {

                    tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0,12) + "...");
                    tiu_user_name_.setText("Cashier");

                }
            }else{

                if (Topitup.IS_ADMIN.equals("1")) {
                    tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", "") );
                    tiu_user_name_.setText("Admin");


                } else {

                    tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                    tiu_user_name_.setText("Cashier");

                }
            }
          //  }


        }

        catch (Exception ex) {
            //
        }








        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    is_admin = "0";
                    if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";
                    if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }

                    if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);

                    }
                    if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");

                    }
                    if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }


                    if (setting_balance_admin.equals("1")  && is_admin.equals("1")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }

                    if(str_balance_admin_bills.equals("1") && is_admin.equals("1")){
                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }

                    if(setting_balance_admin_commission.equals("1") && is_admin.equals("1")){
                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");

                    }

                    if(setting_balance_admin_swipe.equals("1") && is_admin.equals("1")){
                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }
                }
            });
            is_admin = "0";
            if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

            tiu_title_balance.setVisibility(View.GONE);
            tiu_title_balance_cash.setVisibility(View.GONE);


            if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance+" ");


//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

                tiu_title_balance_cash.setVisibility(View.VISIBLE);

            }
            if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");

            }
            if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            }


            if (setting_balance_admin.equals("1")  && is_admin.equals("1")) {
                tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if(str_balance_admin_bills.equals("1") && is_admin.equals("1")){
                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }
            if(setting_balance_admin_commission.equals("1") && is_admin.equals("1")){
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");
            }

            if(setting_balance_admin_swipe.equals("1") && is_admin.equals("1")){
                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
            }
        } catch (Exception ex) {
            //
        }
        /* END HEADER */



//        RelativeLayout pageLoading = findViewById(R.id.pageLoading);
//        pageLoading.setVisibility(View.VISIBLE);

        //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_loading, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        //alertDialog.show();


        //final RealmResults<service_provider_item> puppies = realm.where(service_provider_item.class).findAll();
        //Timber.e("Total SPI: " + String.valueOf(puppies.size()));


        BottomNavigationView mBottomNav  = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              /*  switch (item.getItemId()) {
                    case R.id.action_logout:

                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        startActivity(myIntent2);

                        return true;

                    case R.id.action_admin:

                        if (Topitup.IS_ADMIN.equals("1")) {
                            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                            SharedPreferences.Editor editor = settings.edit();
                          //  Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",86400000);

                            editor.putLong("TIMEOUT_IN_MILLI",86400000);
                            editor.commit();
                           ( (Topitup) getApplication()).startUserSession();

                            Intent myIntent = new Intent(mContext, activity_admin.class);

                            startActivity(myIntent);
                        } else {
                            Intent myIntent3 = new Intent(mContext, activity_cashier.class);
                            startActivity(myIntent3);
                        }

                        return true;

                    case R.id.action_reprint:

                        Intent myIntentReprint = new Intent(mContext, activity_reprint.class);
                        startActivity(myIntentReprint);

                        return true;

                        //Printer.do_last_reprint(mContext);
                        //return true;



                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_logout) {
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    startActivity(myIntent2);
                    return true;

                } else if (item.getItemId() == R.id.action_admin) {
                    if (Topitup.IS_ADMIN.equals("1")) {
                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putLong("TIMEOUT_IN_MILLI", 86400000);
                        editor.commit();
                        ((Topitup) getApplication()).startUserSession();

                        Intent myIntent = new Intent(mContext, activity_admin.class);
                        startActivity(myIntent);
                    } else {
                        Intent myIntent3 = new Intent(mContext, activity_cashier.class);
                        startActivity(myIntent3);
                    }
                    return true;

                } else if (item.getItemId() == R.id.action_reprint) {
                    Intent myIntentReprint = new Intent(mContext, activity_reprint.class);
                    startActivity(myIntentReprint);
                    return true;

                } else {
                    return true;
                }

            }

        });


        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

            //manufacturer = android.os.Build.MANUFACTURER;

        //Timber.e("ID + PRODUCT = " + android.os.Build.ID + " - " + android.os.Build.MODEL);
            //Toasty.error(mContext, "MODEL + PRODUCT = " + android.os.Build.ID + " - " + android.os.Build.MODEL, 5000, true).show();

//        if  (android.os.Build.ID.equals("NRD90M")) {
//
//            mDetectPrinterThread = new DetectPrinterThread();
//            mDetectPrinterThread.start();
//
//        } else {
//            if (initSDK()) {
//                initPrinter();
//            }
//        }


//        mDetectPrinterThread = new DetectPrinterThread();
//        mDetectPrinterThread.start();
//
        //Printer.check_paper();


        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver,screenStateFilter);



    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main_old.rl_network.setVisibility(View.VISIBLE);
                    activity_main_old.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main_old.rl_network.setVisibility(View.INVISIBLE);
                    activity_main_old.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main_old.rl_network.setVisibility(View.INVISIBLE);
                    activity_main_old.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


//    public boolean isValidLogin() {
//
////        long last_edit_time = getSharedPreference().getLong(KEY_SP_LAST_INTERACTION_TIME, 0);
////        Toast.makeText(activity_main.this, "valid login",Toast.LENGTH_SHORT).show();
////        return last_edit_time == 0 || System.currentTimeMillis() - last_edit_time < TIMEOUT_IN_MILLI;
//    }




    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Timber.i(String.valueOf(id));

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);

        return true;

    }












    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        switch (msg.what) {
            case 101:
                Toasty.info(mContext,"Printing...", Toast.LENGTH_LONG).show();
                break;
            case 500:
                Toasty.error(mContext,"Skipped print!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
    };



//    @Override
//    public void onPrintStatus(PrintStatus arg0) {
//        Timber.e("TEST: printStatus = " + arg0.toString());
//    }
//



    boolean b_do_print = false;

    public void onclick_btn_provider(View v) {

        final String setting_chk_disable_cashier_ele = settings.getString("setting_chk_disable_cashier_ele","0");
        final String setting_chk_disable_cashier_bill = settings.getString("setting_chk_disable_cashier_bill","0");
        //Toasty.error(mContext, v.getTag().toString(), 1000, true).show();
        //Printer.print_data(txt, setting, handler, mContext);

        if (v.getTag().toString().equals("prov1")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 1);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov2")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 2);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov3")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 3);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov4")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 4);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov5")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 5);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov10")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 10);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov11")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 11);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_lyca")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 19);
            startActivity(myIntent);
        }
        if (v.getTag().toString().equals("prov_101dialer")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 18);
            startActivity(myIntent);
        }
        if (v.getTag().toString().equals("prov_talk360")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 17);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_siyavula")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 15);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_tiu_easy")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 20);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_rica")) {
            Intent myIntent = new Intent(mContext, activity_web_rica.class);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_cash_management")) {
            Intent myIntent = new Intent(mContext, activity_cash_management.class);
            myIntent.putExtra("service_provider_id", 21);
            startActivity(myIntent);
        }

        if (v.getTag().toString().equals("prov_ding")) {

        Intent myIntent = new Intent(mContext, activity_ding.class);
        myIntent.putExtra("service_provider_id", 22);
        startActivity(myIntent);
        }


//        if (v.getTag().toString().equals("prov255")) {
//
//            Toasty.error(mContext, v.getTag().toString(), 1000, true).show();
//            //get_voucher();
//
//        }

        if (v.getTag().toString().equals("prov51")) {
            if(setting_chk_disable_cashier_ele.equals("1")  && is_admin.equals("0")){
                Toasty.info( mContext,"Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
            }else {
                Intent myIntent = new Intent(activity_main_old.this, activity_elec.class);
                this.startActivity(myIntent);
            }
        }

        if (v.getTag().toString().equals("prov107")) {
            if(setting_chk_disable_cashier_bill.equals("1")  && is_admin.equals("0")){
                Toasty.info( mContext,"Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent myIntent = new Intent(activity_main_old.this, activity_bill_payment.class);
                this.startActivity(myIntent);
            }
        }



        if (v.getTag().toString().equals("prov_virtual_vas")) {
                Intent myIntent = new Intent(activity_main_old.this, activity_virtual_vas.class);
                this.startActivity(myIntent);
        }

        //String serial = txt_serial.getText().toString();

         //do_voucher();

    }





    public static String[] str_break(String wrapText, int length)
    {
        String[] ret = new String[(wrapText.length() + length - 1) / length];

        for (int i = 0; i < ret.length - 1; i++)
        {
            ret[i] = wrapText.substring(i * length, length);
        }
        ret[ret.length - 1] = wrapText.substring((ret.length - 1) * length);
        return ret;
    }

    public static String[] str_format_pin(String wrapPin)
    {

        String[] PrintString;

        if (wrapPin.length() == 17)
            PrintString = str_break(wrapPin, 9);
        else
            PrintString = str_break(wrapPin, 8);

        for (int j = 0; j < PrintString.length; j++)
        {
            if (PrintString[j].length() == 9) {
                String tmp = new StringBuilder(PrintString[j]).insert(6, " ").toString();
                tmp = new StringBuilder(tmp).insert(3, " ").toString();
                PrintString[j] = "  " + tmp;
                //PrintString[j] = "  " + PrintString[j].Insert(6, " ").Insert(3, " ");
            } else if (PrintString[j].length() > 5) {
                String tmp = new StringBuilder(PrintString[j]).insert(4, " ").toString();
                PrintString[j] = "   " + tmp;
                //PrintString[j] = "   " + PrintString[j].Insert(4, " ");
            } else {
                PrintString[j] = "      " + PrintString[j];
            }
        }

        return PrintString;

    }








    @Override
    public void onClick(View vIn) {

    }




    public static void dialog(boolean value){

        if(value){
            //tv_check_connection.setText("We are back !!!");
            //tv_check_connection.setBackgroundColor(Color.GREEN);
            //tv_check_connection.setTextColor(Color.WHITE);

//            Handler handler_dialogue = new Handler();
//            Runnable delayrunnable = new Runnable() {
//                @Override
//                public void run() {
//
//                    Toasty.info( mContext,"Printing...", Toast.LENGTH_LONG).show();
//
//                    //tv_check_connection.setVisibility(View.GONE);
//                }
//            };
//            handler_dialogue.postDelayed(delayrunnable, 3000);

        }else {
            //tv_check_connection.setVisibility(View.VISIBLE);
            //tv_check_connection.setText("Could not Connect to internet");
            //tv_check_connection.setBackgroundColor(Color.RED);
            //tv_check_connection.setTextColor(Color.WHITE);
        }

    }


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {


            try{

// ??? Might cause issues

//Topitup.setting.mPosPowerOff();
//handler.onDestroy();
//Topitup.setting.onDestroy();
            } catch (Exception ex)
            {
                Timber.e(ex.getMessage());
            }

        unregisterNetworkChanges();

        //AAUpdaterController.end();

        super.onDestroy();
       //
    }

    @Override
    protected void onResume() {
        //开始检测打印机 begin to detect printer
        //detectFlag = true;
        //enableOrDisEnableKey(false);

        super.onResume();
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI_ORI",86400000);

        editor.putLong("TIMEOUT_IN_MILLI",TIMEOUT_IN_MILLI);
        editor.commit();
        ( (Topitup) getApplication()).startUserSession();

     //   Toast.makeText(activity_main.this, "onreume",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");

        Intent myIntent2 = new Intent(mContext, activity_login.class);
        startActivity(myIntent2);


//        Intent setIntent = new Intent(Intent.ACTION_MAIN);
//        setIntent.addCategory(Intent.CATEGORY_HOME);
//        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(setIntent);
    }





}
