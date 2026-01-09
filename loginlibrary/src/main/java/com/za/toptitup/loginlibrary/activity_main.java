package com.za.toptitup.loginlibrary;

import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static com.za.toptitup.loginlibrary.utils.PrinterTopitup.openCashDrawer;
import static com.za.toptitup.loginlibrary.utils.PrinterTopitup.openCashDrawerBluetooth;
import static com.za.toptitup.loginlibrary.utils.Topitup.getAppContext;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wisedevice.sdk.IInitDeviceSdkListener;
import com.wisedevice.sdk.WiseDeviceSdk;
import com.wisepos.smartpos.InitPosSdkListener;
import com.wisepos.smartpos.WisePosException;
import com.wisepos.smartpos.WisePosSdk;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sdk.PrinterCommand;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.adapters.AutoScrollAdapter;
import com.za.toptitup.loginlibrary.adapters.MultiVoucherAdapter;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.ItemSPI;
import com.za.toptitup.loginlibrary.model.MultiVoucherSelectedItems;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.service_provider_quick_items;
import com.za.toptitup.loginlibrary.model.service_provider_settings;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.BatteryReceiver;
import com.za.toptitup.loginlibrary.utils.NetworkChangeReceiver;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.ScreenStateReceiver;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UserException;
import com.za.toptitup.loginlibrary.model.LoyaltyStatus;

public class activity_main extends BaseActivity implements HomeAdapterMain.ItemListener, HomeAdapterMainQuick.ItemListener, AutoScrollAdapter.HorizantalrecyclerListener {

    private static final List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
    public static activity_main instance;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static String setting_printer_bypass;
    public static boolean isMultiVoucherSelected = false;
    static String[] voucher_print_footer = {"", "          Top it Up", "      www.itopitup.co.za", ""};
    static String[] voucher_print_dtm_cashier = {"", "Date        Time Cashier", "", ""};
    final int duration = 10;
    final int pixelsToMove = 50;
    private final String TAG = "invoke--ConsumeActivity";
    private final Handler mHandler1 = new Handler(Looper.getMainLooper());
    //PrinterClassSerialPort printerClass = null;
    private final int cutTimes = 1;
    private final String device = "/dev/ttyMT0";
    private final int baudrate = 115200;// 38400
    private final boolean close_printer = true;
    public Dialog progressView;
    public ArrayList<MultiVoucherSelectedItems> selectedMultiItems = new ArrayList<>();
    protected Topitup app;
    service_provider_settings service_provider_setting;
    BottomNavigationView mBottomNav;
    int size;
    Integer service_provider_id;
    public static boolean printScreen = false;
    String company_addrs1, company_addrs2, print_address = "0", setting_bypass_calculator, setting_pockepos_open, setting_chk_auto_mpos;
    TextView txt_last_voucher_info;
    int printerQ1Sts;
    Realm realm;
    String new_spi_ver;
    String manufacturer;
    String customer_id;
    String swipe_sale_logout;
    String thread = "readThread", is_admin;
    long startTimes = 0;
    long endTimes = 0;
    long timeSpace = 0;
    int delay = 5000;
    Context mContext;
    ProgressDialog mProgressDialog;
    int counter = 0;
    TextView textCount;
    TextView printDetail;
    TextView txtBalance;
    RealmResults<fin_balance> tiu_fin_balance;
    RealmResults<service_provider_quick_items> tiu_serviceProviderQuick;
    SharedPreferences settings;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    String prefix = "A", spiver, enable_vas = "0", enable_unipin = "0", enable_airtime = "1", enable_flexipin = "0", enable_bill = "1", enable_ele = "1", enable_mamamoney = "0", enable_easyairtime = "0", enable_cashms = "0", enable_international = "0", enable_oneforu = "0", enable_ringas = "0", enable_bluvoucher = "0", enable_ott = "0",st_status = "0";
    BatteryReceiver batteryReceiver;
    //public MposHandler handler;
    String enable_vodacom = "0";
    List<String> SPList;
    //real time print
    AppCompatButton btn_email, btn_merchant_copy, btn_customer_copy, bt_merchant_customer_copy, bt_closep;
    Handler mHandler = new Handler();
    Handler handlerNew = new Handler();

    //private ProgressBar spinner;
    CountDownTimer cntdwnTimer;
    boolean isRunning = false;
    WebView wb_slip;
    TextView txtload, last_txn_pos, tiu_batt;
    ProgressBar mposprogress;
    LinearLayout lntxwait;
    String asset_serial = "NA";
    String stDate, endDate, enable_realtime_swipe, addpayVer;
    boolean stopRunning = false;
    activity_adpay_new adpay_new;
    String businessOrderNo;
    String expDate = "NA";
    LinearLayoutManager HorizontalLayout;
    service_provider_item_settings service_provider_item_setting;
    Timer timer;
    TextView tiu_title_outlet;
    Dialog dialogactivate;
    ProgressBar progress;
    TextView tiu_user_name, tiu_user_name_;
    AppBarLayout app_bar;
    String setting_chk_quick_launch;
    Integer spi_id = 0;
    String spi_barcode = "";
    Integer total_vouchers_to_print = 1;
    Integer current_voucher = 1;
    LinearLayout pageLoadingWrapper;
    int i = 10;
    //ProgressBar progressBar;
    TextView pop_title, txt_multi_item_count, txt_multi_amount;
    TextView txt_last_time_date;
    TextView txt_last_voucher_infos;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    Dialog dialog_multi, dialog_net;
    String stock_uid = "";
    AppCompatButton bt_process;
    AppCompatButton bt_reprint;
    TextView txt_last_header;
    TextView txt_last_time;
    TextView pop_title2;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    AppCompatButton btn_paper_load;
    LinearLayout ll_back, ll_main_bottom, ll_logout_ring;
    AppCompatButton btn_Paper_ignore_time;
    EditText otp1, otp2, otp3, otp4;
    boolean isFromElse = false;
    RelativeLayout rl_multi_item_view, rl_cancel_print;
    RelativeLayout rl_counter;
    CountDownTimer mCountDownTimer;
    GetUpdateAll tiu_settings;
    double voucher_cost;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();


            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            if (settings.getString("setting_time_out", "0").equals("1")) {

                //Toasty.error(mContext, "test", 5000, true).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Toasty.error(mContext, "test2t", 5000, true).show();
                        // Do something after 5s = 5000ms
                        logout();
                    }
                }, 10);
            }
        }
    };
    // run after some seconds to wait for printing
    Handler print_handler = new Handler();
    Handler print_handler_multi = new Handler();
    LinearLayout ll_logout, ll_admin, ll_reprint, llCashDrawer;
    ImageView img_logout, img_admin, img_reprint, img_cash;
    TextView img_reprint_txt, img_admin_txt, img_logout_txt, img_cash_txt;
    //TextView txt_printer_status;
    service_provider_item service_provider_itemv;
    TextView txt_clock, txt_account, txt_app_version, txt_user_name, txt_acc_type, txt_account_type, txt_printing, txt_desc, txt_close, type_of;
    boolean mIsDialogShown = true;
    int multiVoucherCount = 0;
    TextView txt_date;
    Double totalMultiAmount = 0.0;
    Boolean isClose = false;
    Runnable print_handler_multi_runnable = new Runnable() {
        @Override
        public void run() {
            i = 10;
            printMultiVoucher();
        }
    };
    private ProgressBar mProgressBar;
    private String item_desc = "";
    private int image;
    private RecyclerView recyclerView, recyclerViewq, recycler_horizantal, recyclerVouchers;
    private ArrayList<Item> arrayList;
    private ArrayList<Item> arrayListhor;
    private ArrayList<ItemSPI> arrayListQ;
    private BroadcastReceiver mNetworkReceiver;
    private Thread autoprint_Thread;
    private Button btn_startstop;
    private Button btn_print;
    private ScreenStateReceiver mReceiver;
    private String[] NAME, NAMEQ, DENO, BARCODE, NAMEhor;
    private int[] SPITEMID;
    private boolean[] visibleCashier, visibleAdmin;
    private CardView cardview;
    private String cslip, mslip;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progressdialog;
    private String amount_deno;
    private ProgressBar mLoginProgress;
    private TextView tiu_title_balance, tiu_title_balance_cash, tiu_title_balance_commision, tiu_title_balance_swipe;
    private View view2, view3, view21;
    private String setting_balance_cashier, setting_balance_admin, setting_balance_cashier_bills, setting_balance_cashier_commission, setting_balance_cashier_swipe, setting_balance_admin_bills, setting_balance_admin_commission, setting_balance_admin_swipe;
    private String pinEntered = "";
    private Dialog dialog_active;
    private TextView txt_error, txt_multi_voucher, txt_cancel_multi_voucher, txt_print_multi_voucher;
    private Dialog dialog_busy, dialog_nonet_printing;
    private String accountNumber, companyName;
    private MultiVoucherAdapter multiVoucherAdapter;
    private int sizeOfMulti;

    public static void dialog(boolean value) {

        if (value) {
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

        } else {
            //tv_check_connection.setVisibility(View.VISIBLE);
            //tv_check_connection.setText("Could not Connect to internet");
            //tv_check_connection.setBackgroundColor(Color.RED);
            //tv_check_connection.setTextColor(Color.WHITE);
        }

    }

    private static void startRotatingImage(ImageView rotateImage, Context context) {
        Animation startRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        rotateImage.startAnimation(startRotateAnimation);
    }

    /*****
     *
     *
     * remove
     *
     *
     */

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String transType = "SALE";

        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }

        String result = data.getStringExtra("result");
        // Toasty.error(mContext, ""+result, 1000, true).show();
        String resultMsg = data.getStringExtra("resultMsg");
        transType = data.getStringExtra("transType");
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("buisnessorderno", "");
        editor.commit();
        if (TextUtils.isEmpty(result)) {

            adpay_new.updateordernosts("3");

            return;
        } else if (result.equals("M001")) {
            Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.wiseasy.cashier");
            //    notificationIntent.setPackage(null); // The golden row !!!
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (notificationIntent != null) {
                startActivity(notificationIntent);
            } else {
                Toasty.error(mContext, "Cashier App is not Installed!!!", 25000).show();
            }
            return;

        } else if (result.equals("M007")) {
            if (resultMsg.equals(""))
                adpay_new.updateordernosts("2");
            else
                adpay_new.updateordernosts("3");
            Intent intent = new Intent(mContext, activity_main.class);
            //intent.putExtra("URL", Topitup.BASE_URL_SYNC + "info/more_concat_details/?l=" + Topitup.TIU_LICENSE);
            startActivity(intent);
            return;

        } else {
            try {

                String transData = data.getStringExtra("transData");


                adpay_new.calladapyresponse(transData);


                //   Log.i("transData",transData);
                if (result.equals("0") || result.equals("00")) {

// orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved",notes,tips,discount

                    try {

                        JSONObject jsonObject = new JSONObject(transData);
                        adpay_new.amount = jsonObject.getString("amt");
                        adpay_new.batch_no = jsonObject.getString("batchNo");
                        adpay_new.card_no = jsonObject.getString("cardNo");
                        expDate = jsonObject.getString("expDate");
                        String merchantID = jsonObject.getString("merchantID");
                        adpay_new.refer_no = jsonObject.getString("refNo");
                        String terminalID = jsonObject.getString("terminalID");
                        adpay_new.voucher_no = jsonObject.getString("traceNo");
                        String transDate = jsonObject.getString("transDate");
                        String respCode = jsonObject.getString("respCode");
                        adpay_new.trans_time = transDate + " " + jsonObject.getString("transTime");

                        if (respCode.equals("000")) {
                            adpay_new.callAddpayBackSettle();
                        } else {
                            adpay_new.updateordernosts("2");
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                if (adpay_new.txnsuccess) {
                                    get_balance();
                                    ringtone();
                                } else {
                                    //Toasty.error(mContext, "txn is already settled. please contact Top it Up.", 5000, true).show();

                                }
                            }
                        }, 5000);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    adpay_new.updateordernosts("2");

                }
            } catch (Exception ex) {

                Toasty.error(mContext, ex.getMessage(), 8000);
            }
        }
        // getReprint(4, pid);
    }
*/
    public   WisePosSdk wisePosSdk = null;
    public   WiseDeviceSdk wiseDeviceSdk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        mContext = this;
        instance = this;


        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        /* TIU HEADER */
        settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        setting_chk_quick_launch = settings.getString("setting_chk_quick_launch", "0");
        final String setting_pockepos_open = settings.getString("setting_pockepos_open", "0");

        //Toast.makeText(activity_main.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
        tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        asset_serial = settings.getString("asset_serial", "0");
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        dialog_busy = new Dialog(this, R.style.DialogTheme);

        dialog_busy.setContentView(R.layout.busy_printing);
        dialog_busy.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tiu_clock = findViewById(R.id.tiu_clock);
//        tiu_batt = (TextView) findViewById(R.id.tiu_batt);
        tiu_user_name = findViewById(R.id.tiu_user_name);
    /*    Button crashButton = new Button(this);
        crashButton.setText("Test Crash");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
       */
        /*showLoyaltyDialog(
                LoyaltyStatus.IN_PROGRESS,
                16500,
                30000,
                55
        );*/
        showLoyaltyDialog(
                LoyaltyStatus.WON,
                35000,
                30000,
                100
        );
      /*  showLoyaltyDialog(
                LoyaltyStatus.MISSED,
                17000,
                30000,
                57
        );*/
        tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        txt_multi_voucher = findViewById(R.id.txt_multi_voucher);
        rl_multi_item_view = findViewById(R.id.rl_multi_item_view);
        txt_cancel_multi_voucher = findViewById(R.id.txt_cancel_multi_voucher);
        txt_print_multi_voucher = findViewById(R.id.txt_print_multi_voucher);
        rl_cancel_print = findViewById(R.id.rl_cancel_print);

        ll_reprint = findViewById(R.id.ll_reprint);
        ll_admin = findViewById(R.id.ll_admin);
        ll_logout = findViewById(R.id.ll_logout);
        llCashDrawer = findViewById(R.id.ll_cash_drawer);
        img_cash_txt = findViewById(R.id.img_cash_txt);
        img_cash = findViewById(R.id.img_cash);
        txt_multi_item_count = findViewById(R.id.txt_multi_item_count);
        txt_multi_amount = findViewById(R.id.txt_multi_amount);
        img_logout = findViewById(R.id.img_logout);
        img_admin = findViewById(R.id.img_admin);
        img_reprint = findViewById(R.id.img_reprint);
        view21 = findViewById(R.id.view21);

        img_logout_txt = findViewById(R.id.img_logout_txt);
        img_admin_txt = findViewById(R.id.img_admin_txt);
        img_reprint_txt = findViewById(R.id.img_reprint_txt);

        ll_back = findViewById(R.id.ll_back);
        ll_logout_ring = findViewById(R.id.ll_logoutRing);
        ll_main_bottom = findViewById(R.id.ll_main_bottom);
        wiseDeviceSdk = WiseDeviceSdk.getInstance();
        wisePosSdk = WisePosSdk.getInstance();  //Obtain the WisePsoSdk object.
        Log.d("sdkdemo", "initPosSdk: process！");
//        Toast.makeText(activity_main.this,Topitup.DEVICE_TYPE+"device type....."+android.os.Build.MODEL,Toast.LENGTH_LONG).show();

        if(Build.MODEL.equals("P052")) {


            wisePosSdk.initPosSdk(this, new InitPosSdkListener() {  //Initialize the SDK and bind the service
                @Override
                public void onInitPosSuccess() {
                    Log.d("sdkdemo", "initPosSdk: success!");
                }

                @Override
                public void onInitPosFail(int i) {
                    Log.d("sdkdemo", "initPosSdk: fail!");
                }
            });
            wiseDeviceSdk.initDeviceSdk(this, new IInitDeviceSdkListener() {
                @Override
                public void onInitPosSuccess() {
                    Log.d("sdkdemo", "initDeviceSdk: success!");
                }

                @Override
                public void onInitPosFail(int i) {
                    Log.d("sdkdemo", "initDeviceSdk: fail!");
                }
            });
        }
        /*txt_pos52.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity_main.this, PrinterActivity.class);
                startActivity(intent);
            }
        });*/
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
        } else {
            // Use the same format for lower versions as well
            sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
        }
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");


        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                txt_multi_voucher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recycler_horizantal.setVisibility(View.GONE);
                        isMultiVoucherSelected = true;
                        txt_cancel_multi_voucher.setVisibility(VISIBLE);
                        txt_print_multi_voucher.setVisibility(VISIBLE);
                        txt_multi_voucher.setVisibility(View.GONE);
                        rl_multi_item_view.setVisibility(VISIBLE);
                        rl_cancel_print.setVisibility(VISIBLE);

                    }
                });
                txt_cancel_multi_voucher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rl_cancel_print.setVisibility(View.GONE);

                        recycler_horizantal.setVisibility(VISIBLE);
                        isMultiVoucherSelected = false;

                        txt_cancel_multi_voucher.setVisibility(View.GONE);
                        txt_print_multi_voucher.setVisibility(View.GONE);

                        txt_multi_voucher.setVisibility(VISIBLE);
                        rl_multi_item_view.setVisibility(View.GONE);
                        txt_multi_amount.setText("R");
                        txt_multi_item_count.setText("Total(0)");
                        activity_spi.selectedProviderIds.clear();

                        selectedMultiItems.clear();
                        multiVoucherAdapter.notifyDataSetChanged();

                    }
                });

                txt_print_multi_voucher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String selectedPrinter = settings.getString("printer", "inner");
                        total_vouchers_to_print = sizeOfMulti;

                        if (selectedPrinter.equals("bluetooth")) {
                            printMultiVoucher();

                        } else if (selectedPrinter.equals("usb")) {
                            if (PrinterTopitup.dev != null) {

                                if (!(PrinterTopitup.usbCtrl.isHasPermission(PrinterTopitup.dev))) {
                                    PrinterTopitup.printmethod(activity_main.this);
                                } else {
                                    printMultiVoucher();
                                }
                            } else {
                                PrinterTopitup.printmethod(activity_main.this);

                            }

                        } else {
                            printMultiVoucher();

//                    doPrePrinting(isFrom,sprovider_id,Provider_input_id);

                        }


                    }
                });
            }

        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
                } else {
                    // Use the same format for lower versions as well
                    sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
                }
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);


        //  adpay_new = new activity_adpay_new();

        try {

            tiu_settings = realm.where(GetUpdateAll.class).findFirst();
            spiver = tiu_settings.spi_ver;
            enable_vas = tiu_settings.enable_vas;
            enable_unipin = tiu_settings.enable_unipin;
            enable_cashms = tiu_settings.enable_cashms;
            enable_easyairtime = tiu_settings.enable_easyairtime;
            enable_mamamoney = tiu_settings.enable_mamamoney;
            st_status= tiu_settings.st_status;

            enable_ott = tiu_settings.enable_ott;
            enable_bluvoucher = tiu_settings.enable_bluvoucher;
            enable_ringas = tiu_settings.enable_ringas;
            enable_oneforu = tiu_settings.enable_oneforu;
            accountNumber = tiu_settings.account_number;
            companyName = tiu_settings.company_name;

            if (Topitup.TIU_SERVER.equals("DEMO")) {
                enable_international = "1";
            } else {
                enable_international = tiu_settings.enable_international;
            }
            tiu_title_outlet.setText(tiu_settings.account_number);


            setUserNameAndDesignation();


        } catch (Exception ex) {
            //
        }
        if (enable_vas == null) {
            enable_vas = "0";
        }
        if (enable_bluvoucher == null) {
            enable_bluvoucher = "0";
        }
        if (enable_international == null) {
            enable_international = "0";
        }
        if (enable_unipin == null) {
            enable_vas = "0";
        }
        if (enable_mamamoney == null) {
            enable_mamamoney = "0";
        }
        if (enable_cashms == null) {
            enable_cashms = "0";
        }
        if (enable_easyairtime == null) {
            enable_easyairtime = "1";
        }
        initViews();
        if (BaseActivity.fromVoucherSale) {
            Topitup.stopTimers();
            showActivateDialog();

        }
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseActivity.fromVoucherSale) {

                    showActivateDialog();
                } else {
                    ll_logout.setBackgroundColor(getResources().getColor(R.color.white));
                    ll_admin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ll_reprint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    img_logout.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_admin.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_reprint.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//                img_logout.setImageTintMode();
                    img_logout_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                    img_admin_txt.setTextColor(getResources().getColor(R.color.white));
                    img_reprint_txt.setTextColor(getResources().getColor(R.color.white));

                    try {
                        if (batteryReceiver != null) {
                            unregisterReceiver(batteryReceiver);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);
                }
            }

        });
        llCashDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_cash_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                llCashDrawer.setBackgroundColor(getResources().getColor(R.color.white));
                img_cash.setImageDrawable(getResources().getDrawable(R.drawable.cashier_tab_red));

                String selectedPrinter = settings.getString("printer", "inner");
                if (selectedPrinter.equals("usb")) {
                    if (PrinterTopitup.dev != null && PrinterTopitup.usbCtrl != null) {

                        byte[] buffer = PrinterCommand.POS_Set_PrtInit();
                        PrinterTopitup.usbCtrl.sendByte(buffer, PrinterTopitup.dev);

                        openCashDrawer(PrinterTopitup.usbCtrl, PrinterTopitup.dev);

                    } else {
                        Toast.makeText(mContext, "Please connect Usb Printer", Toast.LENGTH_SHORT).show();
                    }
                    /*img_cash_txt.setTextColor(getResources().getColor(R.color.white));
                    llCashDrawer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));*/
                } else if (selectedPrinter.equals("bluetooth")) {
                    activity_settings.SendDataByte(PrinterCommand.POS_Set_PrtInit(), getAppContext());
                    openCashDrawerBluetooth();


                    if (activity_settings.mService != null) {
                        activity_settings.SendDataByte(PrinterCommand.POS_Set_PrtInit(), getAppContext());
                        openCashDrawerBluetooth();
                    } else {
                        Toast.makeText(mContext, "Bluetooth is not connected", Toast.LENGTH_SHORT).show();
                    }
                }

                handlerNew.postDelayed(new Runnable() {
                    public void run() {
                        img_cash_txt.setTextColor(getResources().getColor(R.color.white));
                        llCashDrawer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        img_cash.setImageDrawable(getResources().getDrawable(R.drawable.cashier_tab_white));

                    }
                }, 1000);
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(mContext, activity_main.class);
                myIntent.putExtra("service_provider_id", 0);
                startActivity(myIntent);
            }
        });
        ll_logout_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent2 = new Intent(mContext, activity_login.class);
                myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent2);
            }
        });
        ll_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseActivity.fromVoucherSale) {

                    showActivateDialog();
                } else {
                    ll_logout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ll_admin.setBackgroundColor(getResources().getColor(R.color.white));
                    ll_reprint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    img_logout.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_admin.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_reprint.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                    img_logout_txt.setTextColor(getResources().getColor(R.color.white));
                    img_admin_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                    img_reprint_txt.setTextColor(getResources().getColor(R.color.white));
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

                }
            }

        });
        ll_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseActivity.fromVoucherSale) {

                    showActivateDialog();
                } else {
                    ll_logout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ll_admin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ll_reprint.setBackgroundColor(getResources().getColor(R.color.white));

                    img_logout.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_admin.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_reprint.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

                    img_logout_txt.setTextColor(getResources().getColor(R.color.white));
                    img_admin_txt.setTextColor(getResources().getColor(R.color.white));
                    img_reprint_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putLong("TIMEOUT_IN_MILLI", 86400000);
                    editor.commit();
                    ((Topitup) getApplication()).startUserSession();
                    Intent myIntentReprint = new Intent(mContext, activity_reprint.class);
                    startActivity(myIntentReprint);
                }
            }

        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                update_balance();
            }
        });


        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {
                    case R.id.action_logout:

                        try {
                            if (batteryReceiver != null) {
                                unregisterReceiver(batteryReceiver);
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent2);


                        return true;

                    case R.id.action_admin:

                        if (BaseActivity.fromVoucherSale) {

                            showActivateDialog();
                        } else {
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

                        }

                        return true;

                    case R.id.action_reprint:
                        if (BaseActivity.fromVoucherSale) {

                            showActivateDialog();
                        } else {

                            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putLong("TIMEOUT_IN_MILLI", 86400000);
                            editor.commit();
                            ((Topitup) getApplication()).startUserSession();
                            Intent myIntentReprint = new Intent(mContext, activity_reprint.class);
                            startActivity(myIntentReprint);

                        }

                        return true;
                    case R.id.action_back:

                        if (BaseActivity.fromVoucherSale) {

                            showActivateDialog();
                        } else {
                            Intent myIntent = new Intent(mContext, activity_main.class);
                            myIntent.putExtra("service_provider_id", 0);
                            startActivity(myIntent);
                        }

                        return true;
                    //Printer.do_last_reprint(mContext);
                    //return true;


                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_logout) {
                    try {
                        if (batteryReceiver != null) {
                            unregisterReceiver(batteryReceiver);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);

                    return true;

                } else if (item.getItemId() == R.id.action_admin) {
                    if (BaseActivity.fromVoucherSale) {
                        showActivateDialog();
                    } else {
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
                    }

                    return true;

                } else if (item.getItemId() == R.id.action_reprint) {
                    if (BaseActivity.fromVoucherSale) {
                        showActivateDialog();
                    } else {
                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putLong("TIMEOUT_IN_MILLI", 86400000);
                        editor.commit();
                        ((Topitup) getApplication()).startUserSession();
                        Intent myIntentReprint = new Intent(mContext, activity_reprint.class);
                        startActivity(myIntentReprint);
                    }

                    return true;

                } else if (item.getItemId() == R.id.action_back) {
                    if (BaseActivity.fromVoucherSale) {
                        showActivateDialog();
                    } else {
                        Intent myIntent = new Intent(mContext, activity_main.class);
                        myIntent.putExtra("service_provider_id", 0);
                        startActivity(myIntent);
                    }

                    return true;

// Uncomment if there's a need for the printer action
//} else if (item.getItemId() == R.id.action_print_last_reprint) {
//    Printer.do_last_reprint(mContext);
//    return true;

                } else {
                    return true;
                }


            }

        });

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, screenStateFilter);
//          tiu_batt.setText(Topitup.getBatteryPercentage(mContext)+" %");
     /*   try {
            getNotifyUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

    }

    private void printMultiVoucher() {
        setting_printer_bypass = settings.getString("setting_print_bypass", "0");
        String selectedPrinter = settings.getString("printer", "inner");

        if (selectedPrinter.equals("inner")) {
            if (Topitup.DEVICE_TYPE.equals("Q1")) {
                if (!PrinterTopitup.checkQ1Printer()) {
                    printerQ1Sts = 2;
                    showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                    return;
                }

                if (!PrinterTopitup.check_paper(mContext)) {
                    printerQ1Sts = 1;
                    //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                    showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                    return;
                }
            }

        }

        if (Topitup.DEVICE_TYPE.equals("WPOS")) {

            if (selectedPrinter.equals("inner")) {
                i = 10;

                if(Build.MODEL.equals("P052")) {
                 /*   try {
                        Map<String, Object> map;

                        map = activity_settings.printerp052.getPrinterStatus(); //Gets the current status of the printer.

                        if (map == null) {
                            Log.e("sdkdemo","getStatus failed" + String.format(" errCode = 0x%x\n",0));
                            return;
                        }else{
                            Log.e("sdkdemo","getStatus " + String.format(" errCode = 0x%x\n",0));
                        }

                        //Gets whether the printer is out of paper from the map file.
                        if ((byte) map.get("paper") == 1) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                            return;
                        } else {

                            Log.e("sdkdemo","IsHavePaper = true\n");
                        }
                    } catch (WisePosException e) {
                        throw new RuntimeException(e);
                    }*/
                }else {
                    int status;
                    Printingw wpos = new Printingw();
                    wpos.init();
                    // wpos.printStart();
                    wpos.printStatus();
                    try {
                        status = wpos.mPrinter.printPaper(1);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    if (status != 0 && (setting_printer_bypass.equals("0"))) {
                        showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                        return;
                    }
                }
            }
        }
        multiVoucherCount = 0;
        //commented for 1 voucher we have revert when we start
//        getVouchersInfo();


    }

    private void getVouchersInfo() {
        int sizeOfMulti = activity_spi.selectedProviderIds.size();
        if (multiVoucherCount < sizeOfMulti) {
            MultiVoucherSelectedItems item = activity_spi.selectedProviderIds.get(multiVoucherCount);
            spi_id = item.pos;
            image = item.drawable;
            spi_barcode = item.item_barcode;
            amount_deno = item.deno;

            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", spi_id).findFirst();
            if (service_provider_item_setting != null) {
                item_desc = service_provider_item_setting.item_desc;


                service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi_id).findFirst();

            }
            getVoucherJson();
        }
    }

    /* private void getVoucherJson() {
         if (Topitup.checkConnection(getApplicationContext())) {
             if (setting_printer_bypass.equals("0")) {
                 busyPrintingDialog();
             }
         } else {
             noInternetBetweenPrinting();
         }
     }*/
    private void getVoucherJson() {
        dialog.dismiss();
        int i = 10;
        if (Topitup.checkConnection(getApplicationContext())) {
            if (setting_printer_bypass.equals("0")) {
                busyPrintingDialog();
            }
        } else {
            noInternetBetweenPrinting();
        }
        final Call<voucher_response> call;

       /* if (service_provider_id == 31) {
            voucher_cost = service_provider_itemv.item_value_int * 100;
            //commented when we start we can remove
//            call = apiService.get_voucher_json_flash(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", spi_id, "", voucher_cost);
        } else*/
        if (service_provider_id == 39) {

            //   voucher_cost = service_provider_itemv.item_value_int * 100;
            int cost = (int) voucher_cost;
            call = apiService.get_voucher_json_(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", spi_id, cost, "");

            //   call = apiService.get_voucher_json_ott(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", voucher_cost, spi_id, "");
        } else {
            voucher_cost = service_provider_itemv.item_value_int * 100;
            call = apiService.get_voucher_json(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", voucher_cost, spi_id, "");
        }
        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {
                        activity_spi.selectedProviderIds.remove(0);

                        selectedMultiItems.remove(0);
                        multiVoucherAdapter.notifyDataSetChanged();
                        totalMultiAmount = totalMultiAmount - (voucher_cost / 100);
                        Log.e("total multi amout", "total amount........" + totalMultiAmount);
                        sizeOfMulti = sizeOfMulti - 1;
                        txt_multi_amount.setText("R " + totalMultiAmount);
                        txt_multi_item_count.setText("Total(" + sizeOfMulti + ")");

                        voucher_response result = response.body();

                        //Timber.e(result.toString());

                        if (result.err.length() > 0) {

                            Log.e("activity", "airtime error11111" + amount_deno);


                            if (result.err.contains("nofunds")) {
                                pop_title.setText("No Funds");
                                txt_printing.setText("Insufficient funds to print voucher. Please contact Top it Up.");
                                txt_desc.setText("Insufficient funds to print voucher. Please contact Top it Up.");
                            } else {
                                pop_title.setText("Voucher Request Issue");
                                txt_printing.setText(result.err);
                                txt_desc.setText(" Voucher is currently unavailable");
                            }
                            pop_content.setText(result.err);

                            bt_close.setVisibility(VISIBLE);
                            pageLoadingWrapper.setVisibility(View.GONE);
                            txt_desc.setVisibility(View.GONE);
                            rl_counter.setVisibility(View.GONE);

                            img_reprint.setVisibility(View.GONE);
                            type_of.setText(amount_deno);
                            txt_close.setVisibility(VISIBLE);
                            isClose = true;

                        } else {
                            isClose = false;

                            if (setting_printer_bypass.equals("0")) {//Response OK
                                txt_close.setVisibility(View.GONE);

                                txt_desc.setVisibility(VISIBLE);
                                rl_counter.setVisibility(VISIBLE);
                                txt_printing.setText("Printing #" + current_voucher);
                                txt_desc.setText("Your voucher is busy printing.\nIf you have an issue, please check Reprint  ");

                                img_reprint.setVisibility(VISIBLE);
                                bt_close.setVisibility(View.GONE);
                                pageLoadingWrapper.setVisibility(View.GONE);
                            }
                            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", spi_id).findFirst();
                            if (service_provider_item_setting != null) {
                                item_desc = service_provider_item_setting.item_desc;

                            }

                            String slip = result.print_data;


                            dialog.dismiss();
                            mCountDownTimer.start();

//                    pop_content.setText("Your voucher is busy printing.\nPlease check reprint if there is an issue.");


                            if (print_address.equals("1"))
                                slip = slip.replace("1\n1Trx", "1" + company_addrs1 + ", " + company_addrs2 + "\n1\n1Trx");
                            if (spi_barcode != null && !spi_barcode.equals("")) {

                                slip = slip + "BARCODE:" + spi_barcode;
                            }


                            if (setting_printer_bypass.equals("1")) {
                                // dialog.dismiss();
                                displayOutOfPaperDialog();
                            } else {
                                // Printer.store_last_reprint(slip);
                                PrinterTopitup.print_data(slip);
                            }


                            fin_balance fb = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findFirst();

                            realm.beginTransaction();

                            fb.balance = result.balance;

                            fb.available_balance = convertNumber(result.available_balance);
                            fb.balance_cash = convertNumber(result.balance_cash);


//                        fb.balance_cash = result.balance_cash;
//                        fb.commission = result.commission;
//                        fb.swipe = result.swipe;
                            fb.loyalty = result.loyalty;

                            realm.commitTransaction();


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (response.raw().body() != null)
                            response.raw().body().close();
                        if (response.raw() != null)
                            response.raw().close();
                    }
                } else {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }


                // Toasty.error(mContext, "done", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();
//                rl_image.setVisibility(View.GONE);

                if (t instanceof SocketTimeoutException) {

                    pop_title.setText("Connection Issue");
                    pop_content.setText("You could have been charged, Please reprint or view your sales history when your internet connection returns.");

                    bt_close.setVisibility(VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                } else {

                    //t.printStackTrace();

                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                }

                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });
    }

    public String convertNumber(String number) {
       /* double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String formatted = formatter.format(amount);*/

        String numberFormated = NumberFormat.getNumberInstance(Locale.UK).format(Double.parseDouble(number));

        String finalNumberFormateed;
        if (numberFormated.contains(".")) {
            finalNumberFormateed = numberFormated;
        } else {
            finalNumberFormateed = numberFormated + ".00";
        }
        return finalNumberFormateed;
    }

    private void displayOutOfPaperDialog() {

        dialog_net = new Dialog(this, R.style.DialogTheme);
        dialog_net.setContentView(R.layout.layout_out_of_paper);
        dialog_net.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txt_last_time = dialog_net.findViewById(R.id.txt_last_time);
        txt_last_time_date = dialog_net.findViewById(R.id.txt_last_time_date);
        txt_last_voucher_infos = dialog_net.findViewById(R.id.txt_last_voucher_info1);
        txt_last_voucher_info = dialog_net.findViewById(R.id.txt_last_voucher_info);
        txt_date = dialog_multi.findViewById(R.id.txt_last_time_date);

        Button btnReprint = dialog_net.findViewById(R.id.bt_reprint);
        dialog_net.show();

        get_last_voucher_info_disp();
        btnReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isDialogone = false;
//                isDialog = false;
                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
                dialog_net.dismiss();
            }
        });


    }

    private void get_last_voucher_info_disp() {

        Call<ResponseBody> call = apiService.get_last_voucher_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {


                    String res = "";
                    try {
                        res = response.body().string();
                    } catch (Exception ex) {
                        if (response.body() != null)
                            response.body().close();
                    }


                    //Timber.e("VOUCHER " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {
                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();
                    } else {

                        String json = res;

                        try {

                            JSONObject obj = new JSONObject(json);

                            //Log.d("My App", obj.toString());
                            //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                            if (obj.getString("result").equals("-1")) {

                                txt_last_header.setVisibility(View.GONE);
                                txt_last_time.setVisibility(View.GONE);
                                txt_last_voucher_info.setVisibility(View.GONE);
                                txt_date.setVisibility(View.GONE);
                                bt_reprint.setVisibility(View.GONE);

                            } else {

//                                if (Integer.parseInt(obj.getString("time_diff")) < 30) {
//                                    Toasty.error(mContext, "Voucher sold " + obj.getString("time_diff") + " seconds ago", 9000, true).show();
//                                }
                                stock_uid = obj.getString("stock_uid");
                                String[] arr = obj.getString("message").split("\\n");
                                String[] arrtime = obj.getString("last_time").split("\\|");
                                //   txt_last_header.setText(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, obj.getString("last_header")) + " : ");
                                // obj.getString("last_header"));
                                txt_last_voucher_info.setText(arr[0]);
                                txt_last_voucher_infos.setText(arr[2].substring(5));
                                txt_last_time.setText(arrtime[1] + "  ");
                                SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
                                Date date = format1.parse(arr[1]);
                                System.out.println(format2.format(date));
                                String strDateTime = format2.format(date);
                                System.out.println("==String date is : " + strDateTime);
                                txt_date.setText(strDateTime.replace("-", " "));

                                final String chk_last_vou;
                                if (Topitup.IS_ADMIN.equals("1")) {
                                    chk_last_vou = settings.getString("setting_chk_last_vou_admin", "0");

                                } else {

                                    chk_last_vou = settings.getString("setting_chk_last_vou_cashier", "0");
                                }

                                if (chk_last_vou.equals("1"))
                                    bt_reprint.setVisibility(VISIBLE);
                                else bt_reprint.setVisibility(View.GONE);

                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        bt_process.setEnabled(true);

                    }

                } catch (Exception ex) {

                    Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }
        });

    }

    private void busyPrintingDialog() {


        txt_clock = dialog_busy.findViewById(R.id.txt_clock);
        txt_account = dialog_busy.findViewById(R.id.txt_account);
        txt_app_version = dialog_busy.findViewById(R.id.txt_app_version);
        txt_user_name = dialog_busy.findViewById(R.id.txt_user_name);
        txt_acc_type = dialog_busy.findViewById(R.id.txt_acc_type);
        txt_account_type = dialog_busy.findViewById(R.id.txt_account_type);
        rl_counter = dialog_busy.findViewById(R.id.rl_counter);
        txt_printing = dialog_busy.findViewById(R.id.txt_printing);
        txt_desc = dialog_busy.findViewById(R.id.txt_desc);
        txt_close = dialog_busy.findViewById(R.id.txt_close);
        img_reprint = dialog_busy.findViewById(R.id.img_reprint);
        TextView txt_count = dialog_busy.findViewById(R.id.txt_count);

        type_of = dialog_busy.findViewById(R.id.type_of);

        TextView txt_printing = dialog_busy.findViewById(R.id.txt_printing);

        ImageView img_product_busy = dialog_busy.findViewById(R.id.img_product);
        TextView txt_product_name = dialog_busy.findViewById(R.id.txt_product_name);
        TextView txt_amount_busy = dialog_busy.findViewById(R.id.txt_amount);
        mProgressBar = dialog_busy.findViewById(R.id.progressBar);
        if (dialog_busy != null && dialog_busy.isShowing()) {

        } else {

            dialog_busy.show();
        }


        mProgressBar.setProgress(i);


        Log.e("busy dialog", selectedMultiItems + "start......." + sizeOfMulti);

        mCountDownTimer = new CountDownTimer(8000, 150) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (i > 0) {
                    mIsDialogShown = false;
                    i--;
                }
                if (i == 0) {
                    if (!txt_printing.getText().toString().equals("Voucher Request Issue") && !txt_printing.getText().toString().contains("Insufficient funds")) {


                        if (current_voucher < total_vouchers_to_print) {
                            Log.e("busy dialog", "if.......");

                            if (Topitup.checkConnection(getApplicationContext())) {

                                current_voucher++;
                                if (settings.getString("setting_chk_print_ele_copy", "0").equals("1"))
//                                    arrIncr++;
                                    mCountDownTimer.cancel();
                                i = 0;
                                print_handler_multi.postDelayed(print_handler_multi_runnable, 1000);
                            } else {

                                mCountDownTimer.cancel();
                                i = 10;
                                noInternetBetweenPrinting();
                            }
                        } else {

                            Log.e("busy dialog", "else......." + sizeOfMulti);
                            mCountDownTimer.cancel();
                            i = 0;

                            if (Topitup.checkConnection(getApplicationContext())) {

                                if (!txt_printing.getText().toString().equals("Voucher Request Issue") && !txt_printing.getText().toString().contains("Insufficient funds")) {

                                    dialog_busy.dismiss();

                                    handler.postDelayed(runnable, 1000);

                                }
                            } else {
                                Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            }
                        }
                    } else {
                        Log.e("busy dialog", "else...1111...." + sizeOfMulti);

                    }

                } else {
//                    dialog_busy.dismiss();
                    Log.e("busy dialog", "else...2222...." + sizeOfMulti);

                }
                mProgressBar.setProgress(i * 100 / (10000 / 600));
                txt_count.setText(String.valueOf(i));
            }

            @Override
            public void onFinish() {
                //Do what you want
                if (i > 0) {
                    i--;
                }
                mProgressBar.setProgress(100);
                //    if(wpos.printStatus()==0) {

                dialog_busy.dismiss();
                mCountDownTimer.cancel();
                //   }
            }
        };


        if (isClose) {
            mCountDownTimer.cancel();

        }

        img_product_busy.setImageResource(image);

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                mCountDownTimer.cancel();

            }
        });
        /*img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
            }
        });*/
        img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 10;
                mCountDownTimer.cancel();
                if (Topitup.checkConnection(getApplicationContext())) {
                    dialog_busy.dismiss();

                    txt_printing.setText("Reprinting ....");

                    busyPrintingDialog();
                    doReprintLogic(stock_uid);
                    FullscreenCall();
                } else {

                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


                }
                //                showCustomDialog("Reprinting", "Please wait...", false);

            }
        });

        String foo = item_desc;
        Log.e("item type", "printing........." + item_desc);
//        if (!isInput.equals("true")) {
        String[] parts = foo.split(" ", 2);
        String product_item = "";

        if (parts[0].equalsIgnoreCase("cell")) {
            product_item = "Cell C";
        } else if (parts[0].equalsIgnoreCase("blue")) {
            product_item = "Blu Voucher";
        } else {
            product_item = parts[0];
        }

        type_of.setText(item_desc);
        txt_product_name.setText(product_item);
        /*} else {
            type_of.setText(item_name_input + " " + amount_deno);
            txt_product_name.setText(item_name_input);
        }*/
       /* if (mItemSPI.social_type.equals("-1") && mItemSPI.period_label.equals("-1")) {
            txt_amount_busy.setVisibility(View.GONE);

        } else {*/
        txt_amount_busy.setVisibility(View.GONE);

//        }

        txt_amount_busy.setText(amount_deno);

        txt_printing.setText("Printing #" + current_voucher + " of " + total_vouchers_to_print);

        txt_app_version.setText(" " + Topitup.APP_VERSION);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        txt_clock.setText(strDate);
        txt_account_type.setText(accountNumber);
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Admin");

            } else {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Cashier");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));

            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            }
        }
        if (companyName != null && !companyName.isEmpty()) {

            if (companyName.length() > 10) {
                txt_account.setText(companyName.substring(0, 10) + "... ");
            } else {

                txt_account.setText(companyName);
            }
        }

    }

    private void noInternetBetweenPrinting() {
        dialog_nonet_printing = new Dialog(this, R.style.DialogTheme);

        dialog_nonet_printing.setContentView(R.layout.busy_no_net_printing);
        dialog_nonet_printing.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_clock = dialog_nonet_printing.findViewById(R.id.txt_clock);
        txt_account = dialog_nonet_printing.findViewById(R.id.txt_account);
        txt_app_version = dialog_nonet_printing.findViewById(R.id.txt_app_version);
        txt_user_name = dialog_nonet_printing.findViewById(R.id.txt_user_name);
        txt_acc_type = dialog_nonet_printing.findViewById(R.id.txt_acc_type);
        txt_account_type = dialog_nonet_printing.findViewById(R.id.txt_account_type);
        rl_counter = dialog_nonet_printing.findViewById(R.id.rl_counter);
        txt_printing = dialog_nonet_printing.findViewById(R.id.txt_printing);
        txt_desc = dialog_nonet_printing.findViewById(R.id.txt_desc);
        txt_close = dialog_nonet_printing.findViewById(R.id.txt_close);
        img_reprint = dialog_nonet_printing.findViewById(R.id.img_reprint);
        TextView txt_count = dialog_nonet_printing.findViewById(R.id.txt_count);

        type_of = dialog_nonet_printing.findViewById(R.id.type_of);

        TextView txt_printing = dialog_nonet_printing.findViewById(R.id.txt_printing);

        ImageView img_product_busy = dialog_nonet_printing.findViewById(R.id.img_product);
        TextView txt_product_name = dialog_nonet_printing.findViewById(R.id.txt_product_name);
        TextView txt_amount_busy = dialog_nonet_printing.findViewById(R.id.txt_amount);
        dialog_nonet_printing.show();
        TextView txt_close = dialog_nonet_printing.findViewById(R.id.txt_close);


        img_product_busy.setImageResource(image);

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_nonet_printing.dismiss();
            }
        });
        /*img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
            }
        });*/
        img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = 10;
                if (Topitup.checkConnection(getApplicationContext())) {
                    dialog_nonet_printing.dismiss();

                    txt_printing.setText("Reprinting ....");

                    busyPrintingDialog();
                    doReprintLogic(stock_uid);
                    FullscreenCall();
                } else {


                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


                }
                //                showCustomDialog("Reprinting", "Please wait...", false);

            }
        });

        String foo = item_desc;
        Log.e("item type", "printing........." + item_desc);
//        if (!isInput.equals("true")) {
        String[] parts = foo.split(" ", 2);
        String product_item = "";

        if (parts[0].equalsIgnoreCase("cell")) {
            product_item = "Cell C";
        } else if (parts[0].equalsIgnoreCase("blue")) {
            product_item = "Blu Voucher";
        } else {
            product_item = parts[0];
        }

        type_of.setText(item_desc);
        txt_product_name.setText(product_item);
//        }
        /* else {
            type_of.setText(item_name_input + " " + amount_deno);
            txt_product_name.setText(item_name_input);
        }*/

        txt_amount_busy.setText(amount_deno);

        txt_printing.setText("Printing #" + current_voucher + " of " + total_vouchers_to_print);

        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(stDate);
        txt_account_type.setText(accountNumber);
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Admin");

            } else {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Cashier");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                txt_acc_type.setText("Admin");
            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                txt_acc_type.setText("Cashier");
            }
        }
       /* if (companyName != null && !companyName.isEmpty()) {

            if (companyName.length() > 10) {
                txt_account.setText(companyName.substring(0, 10) + "... ");
            } else {

                txt_account.setText(companyName);
            }
        }*/
    }

    private void setUserNameAndDesignation() {
        if (BaseActivity.fromVoucherSale) {
            view21.setVisibility(View.GONE);
            tiu_user_name.setVisibility(View.GONE);
            tiu_user_name_.setVisibility(View.GONE);
        } else {
            view21.setVisibility(VISIBLE);

            tiu_user_name.setVisibility(VISIBLE);
            tiu_user_name_.setVisibility(VISIBLE);
        }
        if (Topitup.POSUSER_NAME.length() > 13) {
            Log.e("username", "test......1......" + Topitup.IS_ADMIN);
            if (Topitup.IS_ADMIN.equals("1")) {
                ll_main_bottom.setVisibility(VISIBLE);
                ll_back.setVisibility(View.GONE);

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "").replaceAll("\\s.*", "") + "...");
                tiu_user_name_.setText("Admin");

                img_admin_txt.setText("Admin");
                mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Admin");

            } else {

                ll_main_bottom.setVisibility(VISIBLE);
                ll_back.setVisibility(View.GONE);

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "").replaceAll("\\s.*", "") + "...");
                tiu_user_name_.setText("Cashier");

                img_admin_txt.setText("Reports");
                mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Reports");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                ll_main_bottom.setVisibility(VISIBLE);
                ll_back.setVisibility(View.GONE);
                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Admin");
                img_admin_txt.setText("Admin");

                mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Admin");
            } else {

                ll_main_bottom.setVisibility(VISIBLE);
                ll_back.setVisibility(View.GONE);
                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Cashier");
                img_admin_txt.setText("Reports");

                mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Reports");

            }

        }

    }

    @Override
    protected void onStart() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI_ORI", 86400000);
        setting_bypass_calculator = settings.getString("setting_bypass_calculator", "0");
        setting_bypass_calculator = settings.getString("setting_bypass_calculator", "0");
        setting_pockepos_open = settings.getString("setting_pockepos_open", "0");
        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos", "0");
        enable_realtime_swipe = settings.getString("enable_realtime_swipe", "0");
        editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
        editor.putString("setting_dnp", "0");
        editor.commit();
        get_balance();
        callproducts();

        get_swipe_realtime();
        if (!BaseActivity.fromVoucherSale) {
            ((Topitup) getApplication()).startUserSession();
        } else {
            ((Topitup) getApplication()).stopUserSession();
        }
        final String setting_chk_quick_launch = settings.getString("setting_chk_quick_launch", "0");
        FullscreenCall();
        if (dialog != null) dialog.dismiss();


        stopRunning = false;

        if (service_provider_id == 1001 || service_provider_id == 1002 || service_provider_id == 1003 || service_provider_id == 1004) {
            recycler_horizantal.setVisibility(View.GONE);
        } else if (setting_chk_quick_launch.equals("0")) {
            recycler_horizantal.setVisibility(VISIBLE);
        } else {
            recycler_horizantal.setVisibility(View.GONE);
        }

        if ((Topitup.DEVICE_TYPE.equals("Q1")) && enable_realtime_swipe.equals("1")) {

            if (Topitup.checkConnection(getApplicationContext())) {

                if (Topitup.wasInBackground) {
                    checkPrintMPOSslip(false);
                }

                if (setting_pockepos_open.equals("1")) {
                    showCustomDialog("Loading Swipe Viewer", "Please Wait", false);
                    checkPrintMPOSslip(true);
                }
            } else {

                showCustomDialog("Could Not Connect", "Please check your internet connection and try again", true);

            }
        }
        if (setting_chk_quick_launch.equals("0")) {
            tabsq();
        }
        mBottomNav.getMenu().getItem(1).setChecked(true);

        super.onStart();
    }

    private void initViews() {
        mLoginProgress = findViewById(R.id.login_button_progress);

        mLoginProgress.getIndeterminateDrawable().setColorFilter(0xFFcc0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        recyclerView = findViewById(R.id.recyclerView);
        recycler_horizantal = findViewById(R.id.recycler_horizantal);
        recyclerViewq = findViewById(R.id.recyclerViewq);
        recyclerVouchers = findViewById(R.id.recycler_vouchers);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                recyclerVouchers.setLayoutManager(layoutManager);
            }
        }
        GridLayoutManager manager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }

        GridLayoutManager managerq;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            managerq = new GridLayoutManager(this, 7, GridLayoutManager.VERTICAL, false);
        } else {
            managerq = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
        recyclerView.setLayoutManager(manager);
//        GridLayoutManager manage = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);


        if (setting_chk_quick_launch.equals("0")) {

            recyclerViewq.setLayoutManager(managerq);
        }
        dialogNotify = new Dialog(this);
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
        service_provider_id = getIntent().getIntExtra("service_provider_id", 0);
        mBottomNav = findViewById(R.id.bottom_navigation);
        app_bar = findViewById(R.id.app_bar);

        app_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });

        dialogp = new Dialog(this);
        dialogp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogp.setContentView(R.layout.dialog_dark_realtime_mpos);
        dialogp.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        bt_closep = dialogp.findViewById(R.id.bt_close);
        btn_merchant_copy = dialogp.findViewById(R.id.bt_merchant_copy);
        btn_customer_copy = dialogp.findViewById(R.id.bt_customer_copy);
        btn_email = dialogp.findViewById(R.id.bt_email);
        bt_merchant_customer_copy = dialogp.findViewById(R.id.bt_merchant_customer_copy);
        wb_slip = dialogp.findViewById(R.id.wb_realtime_slip);
        txtload = dialogp.findViewById(R.id.id_loading);
        last_txn_pos = dialogp.findViewById(R.id.id_last_txn_pos);
        mposprogress = dialogp.findViewById(R.id.id_pbar);
        lntxwait = dialogp.findViewById(R.id.lntxwait);

        if (service_provider_id == 1001 || service_provider_id == 1002 || service_provider_id == 1003 || service_provider_id == 1004) {
            ll_main_bottom.setVisibility(View.GONE);
            ll_back.setVisibility(VISIBLE);
            mBottomNav.getMenu().clear(); //clear old inflated items.
            mBottomNav.inflateMenu(R.menu.bottom_nav_admin_back);
            mBottomNav.setSelectedItemId(R.id.action_dumm1);
            mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            recycler_horizantal.setVisibility(View.GONE);

        } else {
            recycler_horizantal.setVisibility(VISIBLE);
            ll_main_bottom.setVisibility(VISIBLE);
            ll_back.setVisibility(View.GONE);
            mBottomNav.getMenu().getItem(1).setChecked(true);
        }


        cardview = findViewById(R.id.cardView);

    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main.rl_network.setVisibility(VISIBLE);
                    activity_main.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main.rl_network.setVisibility(View.INVISIBLE);
                    activity_main.rl_server.setVisibility(VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_main.rl_network.setVisibility(View.INVISIBLE);
                    activity_main.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void displayDialog() {
        checkServiceRunning();
    }

    private void checkServiceRunning() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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

    private void addValues(String name, String price) {
        String productName = name;
        String productPrice = price;

        if (!productName.isEmpty() && !productPrice.isEmpty()) {
            //Key-value object to add value in the database
            ContentValues values = new ContentValues();
            values.put(MyContentProvider.name, productName);
            values.put(MyContentProvider.price, productPrice);
            //insert data using Content URI

            Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null, null);

//            Toast.makeText(activity_main.this,"Download request send",Toast.LENGTH_LONG).show();
            if (cursor.moveToFirst()) {
                Log.e("update", "........");
                getContentResolver().update(MyContentProvider.CONTENT_URI, values, null, null);

            } else {
                Log.e("insert", "........");

                getContentResolver().insert(MyContentProvider.CONTENT_URI, values);

            }
//            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
        Log.e("insert", "....New Record Inserted...." + name);

        // displaying a toast message
//        Toast.makeText(getBaseContext(), "New Record Inserted  "+name, Toast.LENGTH_LONG).show();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void unRegisterBattery() {

        if (isReceiverRegistered(batteryReceiver)) {
            receivers.remove(batteryReceiver);
            unregisterReceiver(batteryReceiver);

        }

    }

    public boolean isReceiverRegistered(BroadcastReceiver receiver) {
        boolean registered = receivers.contains(receiver);
        Log.i(getClass().getSimpleName(), "is receiver " + receiver + " registered? " + registered);
        return registered;
    }

    public void getBattery(Context context) {
        if (batteryReceiver == null) {
            if (!receivers.contains(batteryReceiver)) {
                receivers.add(batteryReceiver);
                batteryReceiver = new BatteryReceiver();
                registerReceiver(batteryReceiver, new IntentFilter(ACTION_BATTERY_CHANGED));
            }
        } else {
            unregisterReceiver(batteryReceiver);
        }
    }

    private void update_balance() {
        settings = getSharedPreferences("TIUPREF", 0);

        setting_balance_cashier = settings.getString("setting_balance_cashier", "0");

        setting_balance_admin = settings.getString("setting_balance_admin", "0");
        tiu_title_balance = findViewById(R.id.tiu_title_balance);
        tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
        setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
        setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");


        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        try {
            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();

            if (!BaseActivity.fromVoucherSale) {

                tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                    @Override
                    public void onChange(RealmResults<fin_balance> results) {
                        Log.e("balance update", setting_balance_cashier + "........1111..update....." + is_admin);

                        is_admin = "0";
                        if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";


                        if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {

                            tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

                        }
                        if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                            view2.setVisibility(VISIBLE);
                            tiu_title_balance_cash.setText("Bills  R  " + tiu_fin_balance.get(0).balance_cash);
                        }
                        if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {
                            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                        }
                        if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                            view3.setVisibility(VISIBLE);
                            tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                        }

                        if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                            tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
                            tiu_title_balance.setVisibility(VISIBLE);
                        }

                        if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                            view2.setVisibility(VISIBLE);
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                            tiu_title_balance_cash.setVisibility(VISIBLE);
                        }
                        if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
                        }

                        if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                            view3.setVisibility(VISIBLE);
                            tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
                        }
                    }
                });
                is_admin = "0";

                if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);


                if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                    tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);

//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
               /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);*/
                    tiu_title_balance.setVisibility(VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
                }


                if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                    view2.setVisibility(VISIBLE);
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    tiu_title_balance_cash.setVisibility(VISIBLE);


                }
                if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {
                    tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                }
                if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                    view3.setVisibility(VISIBLE);
                    tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                }

                if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                    tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance);
                    tiu_title_balance.setVisibility(VISIBLE);
                }

                if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {

                    view2.setVisibility(VISIBLE);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    tiu_title_balance_cash.setVisibility(VISIBLE);
                }
                if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                    view3.setVisibility(VISIBLE);
                    tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
                }
                if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                    tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
                }
            } else {
                tiu_title_balance_swipe.setVisibility(View.GONE);
                tiu_title_balance_commision.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
                tiu_title_balance.setVisibility(View.GONE);
                view2.setVisibility(View.INVISIBLE);
                view3.setVisibility(View.INVISIBLE);
            }

        } catch (Exception ex) {
            //
        }

        /*if(BaseActivity.fromVoucherSale){
            tiu_title_balance_swipe.setVisibility(View.GONE);
            tiu_title_balance_commision.setVisibility(View.GONE);
            tiu_title_balance_cash.setVisibility(View.GONE);
            tiu_title_balance.setVisibility(View.GONE);
            view2.setVisibility(View.INVISIBLE);
            view3.setVisibility(View.INVISIBLE);
            *//*view3.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);*//*

        }else{
            tiu_title_balance_swipe.setVisibility(View.VISIBLE);
            tiu_title_balance_commision.setVisibility(View.VISIBLE);
            tiu_title_balance_cash.setVisibility(View.VISIBLE);
            tiu_title_balance.setVisibility(View.VISIBLE);
            *//*view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);*//*
        }*/

    }

    private void updateBalance(String isAdmin) {

        Log.e("balance update", setting_balance_cashier + "..........update....." + isAdmin);


        is_admin = isAdmin;
        if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

        if (setting_balance_cashier.equals("1") && isAdmin.equals("0")) {
            tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

            tiu_title_balance.setVisibility(VISIBLE);
        }

        if (setting_balance_cashier_bills.equals("1") && isAdmin.equals("0")) {
            view2.setVisibility(VISIBLE);
            tiu_title_balance_cash.setVisibility(VISIBLE);
            tiu_title_balance_cash.setText("Bills  R  " + tiu_fin_balance.get(0).balance_cash);

        }

        if (setting_balance_cashier_swipe.equals("1") && isAdmin.equals("0")) {
            view3.setVisibility(VISIBLE);
            tiu_title_balance_swipe.setVisibility(VISIBLE);
            tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

        }
        if (setting_balance_cashier_commission.equals("1") && isAdmin.equals("0")) {
            tiu_title_balance_commision.setVisibility(VISIBLE);
            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");


        }
        if (setting_balance_admin.equals("1") && isAdmin.equals("1")) {
            tiu_title_balance.setVisibility(VISIBLE);
            tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

        }

        if (setting_balance_admin_bills.equals("1") && isAdmin.equals("1")) {
            view2.setVisibility(VISIBLE);
            tiu_title_balance_cash.setVisibility(VISIBLE);
            tiu_title_balance_cash.setText("Bills  R  " + tiu_fin_balance.get(0).balance_cash);

        }
        if (setting_balance_admin_commission.equals("1") && isAdmin.equals("1")) {
            tiu_title_balance_commision.setVisibility(VISIBLE);
            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");


        }

        if (setting_balance_admin_swipe.equals("1") && isAdmin.equals("1")) {
            view3.setVisibility(VISIBLE);
            tiu_title_balance_swipe.setVisibility(VISIBLE);
            tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

        }
    }

    private void tabs() {
        arrayList = new ArrayList<>();
        arrayListhor = new ArrayList<>();

        for (int i = 0; i < NAME.length; i++) {

            //Toasty.error(mContext, ""+NAME[i], 1000, true).show();

            if (NAME[i].equals("vodacom")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 1).findFirst();
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_voda_dis, "#FFFFFF", ""));
                } else {
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_voda, "#FFFFFF", ""));
                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_voda_dis, "#FFFFFF", ""));
                    }
                }


            } else if (NAME[i].equals("mtn")) {
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mtn_dis, "#FFFFFF", ""));
                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 2).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mtn, "#FFFFFF", ""));
                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mtn_dis, "#FFFFFF", ""));
                    }
                }

            } else if (NAME[i].equals("flexepin")) {
                if (enable_flexipin.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.flexipin, "#FFFFFF", ""));

                } else {
                    if (service_provider_setting != null) {
                        service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 40).findFirst();
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.flexipin, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.flexipin, "#FFFFFF", ""));

                    }
                }
            } else if (NAME[i].equals("cellc")) {
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_cell_dis, "#FFFFFF", ""));
                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 3).findFirst();
                    if (service_provider_setting == null) {
                    } else {
                        if (service_provider_setting.isVisible_admin()) {

                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_cellc, "#FFFFFF", ""));
                        } else {

                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_cell_dis, "#FFFFFF", ""));
                        }
                    }
                }

            } else if (NAME[i].equals("telkom")) {
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_telcom_dis, "#FFFFFF", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 10).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_tel, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_telcom_dis, "#FFFFFF", ""));

                    }
                }

            } else if (NAME[i].equals("Telkom")) {
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_telcom_dis, "#FFFFFF", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 10).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_tel, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_telcom_dis, "#FFFFFF", ""));

                    }
                }

            } else if (NAME[i].equals("airvoip")) {
                if (enable_airtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_air_dis, "#FFFFFF", ""));

                } else {


                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_air, "#FFFFFF", ""));


                }

            } else if (NAME[i].equals("topitupeasi")) {
                if (enable_easyairtime.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.top_it_up_easy, "#FFFFFF", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 20).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.top_it_up_easy, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.top_it_up_easy, "#FFFFFF", ""));

                    }
                }

            } else if (NAME[i].equals("easyload")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 20).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.easyload, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.easyload_disabled, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("ringas")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 30).findFirst();

                if (enable_ringas.equals("0"))
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ringas_dis, "#FFFFFF", ""));
                else {
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ringas, "#FFFFFF", ""));
                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ringas_dis, "#FFFFFF", ""));
                    }
                }


            } else if (NAME[i].equals("worldcall")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 7).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_worldcall, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_worldcall, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("electricity")) {
               /* service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id",1).findFirst();
                if(service_provider_setting.isVisible_admin()){*/
                if (enable_ele.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_electricity_dis, "#FFFFFF", ""));
                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_electricity, "#FFFFFF", ""));
                }

            } else if (NAME[i].equals("water")) {
//                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id",1).findFirst();
//                if(service_provider_setting.isVisible_admin()){
                if (enable_ele.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_water_dis, "#FFFFFF", ""));
                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_water, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("paybills")) {
                /*service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id",1).findFirst();
                if(service_provider_setting.isVisible_admin()){*/
                if (enable_bill.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_pay_dis, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_bill_pay, "#FFFFFF", ""));

                }

               /* }else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_pay_dis, "#FFFFFF",""));

                }*/

            } else if (NAME[i].equals("globalairtime")) {
                {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 21).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        if (enable_international.equals("0"))
                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_global_dis, "#FFFFFF", ""));
                        else
                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_global, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_global_dis, "#FFFFFF", ""));

                    }

                }

            } else if (NAME[i].equals("1o1mobile")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 18).findFirst();

                if (service_provider_setting == null) {
                } else {
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mobile, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mobile_dis, "#FFFFFF", ""));

                    }
                }
            } else if (NAME[i].equals("virginmobile")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 4).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov4, "#FFFFFF", ""));
                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov4, "#FFFFFF", ""));
                }

            } else if (NAME[i].equals("talk360")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 17).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_talk, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_talk_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("siyavla")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 15).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_siyavula, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_siyavula, "#FFFFFF", ""));

                }

            }
            // else if (NAME[i].equals("cashmx"))
            // arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provcashmanagement, "#FFFFFF",""));
            else if (NAME[i].equals("cashmx")) {
                if (enable_cashms.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_suppliers_dis, "#FFFFFF", ""));

                } else {
//                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 21).findFirst();
//                    if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_suppliers, "#FFFFFF", ""));

//                    } else {
//                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_suppliers_dis, "#FFFFFF", ""));
//
//                    }
                }

            } else if (NAME[i].equals("coca")) {
                /*service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id",1).findFirst();
                if(service_provider_setting.isVisible_admin()){*/
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provcashmanagement, "#FFFFFF", ""));


            } else if (NAME[i].equals("nandhni")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.clover, "#FFFFFF", ""));

            } else if (NAME[i].equals("lyca")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 19).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_lyca, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_lyca, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("rica")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_rica, "#FFFFFF", ""));

            } else if (NAME[i].equals("moneytransfer")) {
                if (enable_bill.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_money_dis, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_money, "#FFFFFF", ""));

                }


            } else if (NAME[i].equals("mamamoney")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mamamoney, "#FFFFFF", ""));

            } else if (NAME[i].equals("unipin")) {
                if (enable_unipin.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_unipin_dis, "#FFFFFF", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 12).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_unipin, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_unipin_dis, "#FFFFFF", ""));

                    }
                }
            } else if (NAME[i].equals("virtualvas")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_virtual_vas, "#FFFFFF", ""));

            } else if (NAME[i].equals("ikeja")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 23).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ikea, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ikea_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("DSTV")) {
                if (enable_bill.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_dstv_dis, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_dstv1, "#FFFFFF", ""));
                }


            } else if (NAME[i].equals("busticket")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 29).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    if (enable_bluvoucher.equals("0"))
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_blue_dis, "#FFFFFF", ""));
                    else
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.blue_voucher11, "#FFFFFF", ""));
                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_blue_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equals("moneytransfer")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_money, "#FFFFFF", ""));


            } else if (NAME[i].equals("oneforyou")) {


                if (enable_oneforu.equals("2")) {

                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 24).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_one, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provider_one_dis, "#FFFFFF", ""));
                    }
                } else if (enable_oneforu.equals("3")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provider_one_dis, "#FFFFFF", ""));

                } else if (enable_oneforu.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provider_one_dis, "#FFFFFF", ""));
                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 24).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_one, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provider_one_dis, "#FFFFFF", ""));
                    }
                }


            } else if (NAME[i].equals("ott")) {

                if (enable_ott.equals("3")) {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 39).findFirst();
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott_dis, "#000000", ""));

                } else if (enable_ott.equals("2")) {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 25).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott, "#000000", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott_dis, "#FFFFFF", ""));
                    }
                } else if (enable_ott.equals("1")) {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 39).findFirst();
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott, "#000000", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 25).findFirst();
                    if (enable_ott.equals("0")) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott_dis, "#FFFFFF", ""));

                    } else {
                        if (service_provider_setting.isVisible_admin()) {
                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott, "#000000", ""));

                        } else {
                            arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott_dis, "#FFFFFF", ""));
                        }
                    }
                }
            } else if (NAME[i].equals("netflix")) {
                if (enable_international.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_netflix_dis, "#FFFFFF", ""));
                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 28).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_netflix, "#FFFFFF", ""));
                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_netflix_dis, "#FFFFFF", ""));
                    }
                }
            } else if (NAME[i].equals("uber")) {
                if (enable_international.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_uber_dis, "#FFFFFF", ""));
                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 27).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_uber, "#FFFFFF", ""));
                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_uber_dis, "#FFFFFF", ""));
                    }
                }
            } else if (NAME[i].equals("spotify")) {
                if (enable_international.equals("0")) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_spotify_dis, "#FFFFFF", ""));

                } else {
                    service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 26).findFirst();
                    if (service_provider_setting.isVisible_admin()) {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_spotify, "#FFFFFF", ""));

                    } else {
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_spotify_dis, "#FFFFFF", ""));

                    }
                }

            } else if (NAME[i].equals("cardpay")) {
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_visa, "#FFFFFF", ""));


            } else
                arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_blank, "#FFFFFF", ""));

        }

        HomeAdapterMain adapter = new HomeAdapterMain(activity_main.this, arrayList, activity_main.this);
        recyclerView.setAdapter(adapter);
        this.mLoginProgress.setVisibility(View.GONE);


    }

    private void setRV(ArrayList<ItemSPI> arrayList) {


        if (arrayList.size() == 0) {
            recycler_horizantal.setVisibility(View.GONE);
        } else {
            if (service_provider_id == 1001 || service_provider_id == 1002 || service_provider_id == 1003 || service_provider_id == 1004) {
                recycler_horizantal.setVisibility(View.GONE);

            } else {
                recycler_horizantal.setVisibility(VISIBLE);
            }
        }

            /*if(Topitup.DEVICE_TYPE.equals("TABLET")) {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recycler_horizantal.setLayoutManager(layoutManager);
            }else {*/
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_horizantal.setLayoutManager(layoutManager);
//            }


        HomeAdapterMainQuick adapterhorizantal = new HomeAdapterMainQuick(activity_main.this, arrayList, activity_main.this);
        recycler_horizantal.setAdapter(adapterhorizantal);


        recycler_horizantal.setOnFlingListener(null);

       /* LinearSnapHelper snapHelper = new LinearSnapHelper();

        snapHelper.attachToRecyclerView(recycler_horizantal);
*/
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() < (adapterhorizantal.getItemCount() - 1)) {


                        if (new RecyclerView.State() != null) {
                            layoutManager.smoothScrollToPosition(recycler_horizantal, new RecyclerView.State(), layoutManager.findLastCompletelyVisibleItemPosition() + 1);
                        }
                    } else {

                        layoutManager.smoothScrollToPosition(recycler_horizantal, new RecyclerView.State(), 0);
                    }
                } catch (Exception e) {

                }
            }
        }, 0, 2000);
    }

    private void tabsq() {
        arrayListQ = new ArrayList<>();
        tiu_serviceProviderQuick = realm.where(service_provider_quick_items.class).findAll();
        SPITEMID = new int[8];
        DENO = new String[8];
        BARCODE = new String[8];
        NAMEQ = new String[8];
        size = tiu_serviceProviderQuick.size();

        tiu_serviceProviderQuick = realm.where(service_provider_quick_items.class).findAll();
        size = tiu_serviceProviderQuick.size();
//        for (int i = 0; i < size; i++) {
        if (size > 0) {
            for (int i = 0; i < 8; i++) {
                service_provider_quick_items product = tiu_serviceProviderQuick.get(i);


                if (product.spPos == 0) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 3).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);
                    String namenew = product.spName;
                    Log.e("cellc", "name.........0" + namenew);
                    arrayListQ.add(new ItemSPI((product.spName).replace("CELLC", ""), product.spItemID, R.drawable.prov_cellc, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                } else if (product.spPos == 1) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 2).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("MTN", ""), product.spItemID, R.drawable.prov_mtn, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                } else if (product.spPos == 2) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 1).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("VODACOM", ""), product.spItemID, R.drawable.prov_voda, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                } else if (product.spPos == 3) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 10).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("TELCOM", ""), product.spItemID, R.drawable.prov_tel, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                } else if (product.spPos == 4) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 24).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("1FORYOU", ""), product.spItemID, R.drawable.prov_one, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                } else if (product.spPos == 5) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 29).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("BLUE VOUCHER", ""), product.spItemID, R.drawable.blue_v_new, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));
                    //     arrayListQ.add(new ItemSPI((product.spName).replace("BLUE VOUCHER", ""), product.spItemID, R.drawable.blue_voucher2, "#FFFFFF", "Data", product.spBarcode, true, true, productNew.item_value));

                } else if (product.spPos == 6) {
                    RealmResults<service_provider_item> service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", 25).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();
                    service_provider_item productNew = service_provider_items.get(product.sdPos);

                    arrayListQ.add(new ItemSPI((product.spName).replace("OTT", ""), product.spItemID, R.drawable.prov_ott, "#000000", "Data", product.spBarcode, true, true, productNew.item_value, productNew.social_type, productNew.period_label));

                }

            /*if (sp.contains("voda")) {
                String sp1 = (product.spName).replace("Vodacom", "");
                sp1 = sp1.replace("Virtual Voda", "");
                //Toasty.error(mContext, "sp", Toast.LENGTH_LONG).show();
                // arrayListQ.add(new ItemSPI(sp1, product.spItemID, R.drawable.prov1, "#FFFFFF", "Data", product.spBarcode,true,true,DENO[i]));
                arrayListQ.add(new ItemSPI(sp1, product.spItemID, R.drawable.prov_voda, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
            } else if (sp.contains("mtn"))
                arrayListQ.add(new ItemSPI((product.spName).replace("Virtual Voda", ""), product.spItemID, R.drawable.prov_mtn, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
            else if (sp.contains("cell c"))
                arrayListQ.add(new ItemSPI((product.spName).replace("Cell C", ""), product.spItemID, R.drawable.prov_cellc, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
           else if (sp.contains("telkom"))
                arrayListQ.add(new ItemSPI((product.spName).replace("telcom", ""), product.spItemID, R.drawable.prov_tel, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
            else if (sp.contains("unipin"))
                arrayListQ.add(new ItemSPI((product.spName).replace("unipin", ""), product.spItemID, R.drawable.prov_unipin, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
           else if (sp.contains("ott"))
                arrayListQ.add(new ItemSPI((product.spName).replace("OTT", ""), product.spItemID, R.drawable.prov_ott, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
            else if (sp.contains("1foryou"))
                arrayListQ.add(new ItemSPI((product.spName).replace("1ForYou", ""), product.spItemID, R.drawable.prov_one, "#FFFFFF", "Data", product.spBarcode, true, true, ""));
*/

            }
        }
        //  arrayListQ.add(new ItemSPI("", 1, R.drawable.prov_blank, "#FFFFFF","","12345"));

//        for (int i = 0; i < NAMEQ.length; i++) {
//
//            if (NAMEQ[i].equals("vodacom"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov1, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("mtn"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov2, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("cellc"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov3, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("telkom"))
//                arrayListQ.add(new ItemSPI(DENO[i],1, R.drawable.prov5, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("topitupeasi"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.top_it_up_easy, "#FFFFFF","","12345"));
//
//            else if (NAMEQ[i].equals("1o1mobile"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov_101, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("virginmobile"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov4, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("talk360"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov_talk360, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("siyavla"))
//                arrayListQ.add(new ItemSPI(DENO[i],1, R.drawable.prov_siyavula, "#FFFFFF","","12345"));
//
//            else if (NAMEQ[i].equals("lyca"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov_lyca, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("rica"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov_rica, "#FFFFFF","","12345"));
//            else if (NAMEQ[i].equals("unipin"))
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov12_small, "#FFFFFF","","12345"));
//            else
//                arrayListQ.add(new ItemSPI(DENO[i], 1, R.drawable.prov_blank, "#FFFFFF","","12345"));
//
//
//
//        }

        setRV(arrayListQ);


    }

    public void showProgress() {
        try {
            progressView = new Dialog(mContext, R.style.AppTheme);
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            progressBar.getIndeterminateDrawable().setColorFilter(0xFFcc0000, android.graphics.PorterDuff.Mode.MULTIPLY);

            progressView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressView.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressView.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            progressView.setCancelable(false);

            progressView.setContentView(view);
          /*  ImageView rotateImage = progressView.findViewById(R.id.rotate_image);
            startRotatingImage(rotateImage, mContext);
          */
            progressView.show();
            Log.e("Exception", "toString()");

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    public void hideProgress() {
        try {
            if (progressView != null)
                progressView.dismiss();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public void onItemClick(ItemSPI item) {
//        Log.e("quick", "quick launch" + item.deno);
        if (BaseActivity.fromVoucherSale) {

            showActivateDialog();
        } else {
            showCustomDialog("Requesting", "Please wait...", false);


            if (item == null) {
                return;
            }


            total_vouchers_to_print = 1;
            spi_id = item.pos;
            spi_barcode = item.item_barcode;
            final String setting_chk_voucher_print = settings.getString("setting_chk_voucher_print", "0");
            amount_deno = item.deno;

            if (setting_chk_voucher_print.equals("0"))
                get_voucher();
            else
                show_multi_voucher(item.pos, item.drawable, item.deno);
        }

    }

    // Toast.makeText(getApplicationContext(), item.pos + " is clicked", Toast.LENGTH_SHORT).show();

    @Override
    public void onItemClick(Item item) {
        if (BaseActivity.fromVoucherSale) {

            showActivateDialog();
        } else {
            final String setting_chk_disable_cashier_ele = settings.getString("setting_chk_disable_cashier_ele", "0");
            final String setting_chk_disable_cashier_bill = settings.getString("setting_chk_disable_cashier_bill", "0");

            //set the claculator
            /*
            - create a method for windo dialog calculator ui
            and if user enter pin the particular product will open
             */
//            if(){
//
//            }else{
//
//            }

            Intent myIntent = new Intent();
            switch (item.pos) {

                case "vodacom":


                    // Toasty.info( mContext,"Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                    if (enable_airtime.equals("0")) {
                        showActivateDialog(R.drawable.prov_voda_dis, 1, R.drawable.prov_voda, "fromService", "vodacom");
                    } else {
                        if (checkSettings(1, R.drawable.prov_voda_dis, R.drawable.prov_voda)) {

                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 1);//vodacom
                            startActivity(myIntent);
                        }
                    }


                    hideProgress();
                    return;
                case "mtn":

                    if (enable_airtime.equals("0")) {
                        showActivateDialog(R.drawable.prov_mtn_dis, 1, R.drawable.prov_mtn, "fromService", "mtn");

                    } else {
                        if (checkSettings(2, R.drawable.prov_mtn_dis, R.drawable.prov_mtn)) {

                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 2);//mtn
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "cellc":

                    if (enable_airtime.equals("0")) {
                        showActivateDialog(R.drawable.prov_cell_dis, 1, R.drawable.prov_cellc, "fromService", "cellc");

                    } else {
                        if (checkSettings(3, R.drawable.prov_cell_dis, R.drawable.prov_cellc)) {

                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 3);//cell c
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "worldcall":

                    if (checkSettings(7, R.drawable.prov_worldcall, R.drawable.prov_worldcall)) {

                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 7);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "telkom":

                    if (enable_airtime.equals("0")) {
                        showActivateDialog(R.drawable.prov_telcom_dis, 1, R.drawable.prov_tel, "fromService", "telkom");

                    } else {
                        if (checkSettings(10, R.drawable.prov_telcom_dis, R.drawable.prov_tel)) {

                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 10);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "Telkom":

                    if (enable_airtime.equals("0")) {
                        showActivateDialog(R.drawable.prov_telcom_dis, 1, R.drawable.prov_tel, "fromService", "Telkom");

                    } else {
                        if (checkSettings(10, R.drawable.prov_telcom_dis, R.drawable.prov_tel)) {

                            myIntent = new Intent(mContext, activity_main.class);
                            myIntent.putExtra("service_provider_id", 1002);

//                    myIntent.putExtra("service_provider_id", 1002);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "airvoip":
//                if (enable_airtime.equals("0")) {
//                    showActivateDialog(R.drawable.prov_air_dis, 1, R.drawable.prov_air, "fromService", "airvoip");
//
//                } else {

//                    if (checkSettings(55, R.drawable.easyload, R.drawable.easyload)) {

                    myIntent = new Intent(mContext, activity_spi.class);
                    myIntent.putExtra("service_provider_id", 55);
                    startActivity(myIntent);
//                    }
                   /* myIntent = new Intent(mContext, activity_main.class);
                    myIntent.putExtra("service_provider_id", 1001);
                    startActivity(myIntent);*/
//                }
                    hideProgress();
                    return;
                case "unipin":


                    if (enable_unipin.equals("0")) {
                        showActivateDialog(R.drawable.prov_unipin_dis, 1, R.drawable.prov_unipin, "fromService", "unipin");

                    } else {
                        if (checkSettings(12, R.drawable.prov_unipin_dis, R.drawable.prov_unipin)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 12);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "electricity":

                    if (enable_ele.equals("0")) {
                        showActivateDialog(R.drawable.prov_electricity_dis, 1, R.drawable.prov_electricity, "fromService", "electricity");

                    } else {
                        if (setting_chk_disable_cashier_ele.equals("1") && is_admin.equals("0")) {
                            Toasty.info(mContext, "Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            myIntent = new Intent(activity_main.this, activity_elec.class);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "water":

                    if (enable_ele.equals("0")) {
                        showActivateDialog(R.drawable.prov_water_dis, 1, R.drawable.prov_water, "fromService", "water");

                    } else {
                        if (setting_chk_disable_cashier_ele.equals("1") && is_admin.equals("0")) {
                            Toasty.info(mContext, "Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            myIntent = new Intent(activity_main.this, activity_elec.class);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "topitupeasi":

                    if (enable_easyairtime.equals("0")) {
                        showActivateDialog(R.drawable.top_it_up_easy, 1, R.drawable.top_it_up_easy, "fromService", "topitupeasi");

                    } else {

                        myIntent = new Intent(mContext, activity_main.class);
                        myIntent.putExtra("service_provider_id", 1003);
                        startActivity(myIntent);
                    }

               /*
                if (checkSettings(20)) {
                    myIntent = new Intent(mContext, activity_spi.class);
                    myIntent.putExtra("service_provider_id", 20);
                    startActivity(myIntent);
                }

                */
                    hideProgress();
                    return;
                case "easyload":

                    if (checkSettings(20, R.drawable.easyload, R.drawable.easyload)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 20);
                        startActivity(myIntent);
                    }


                    hideProgress();
                    return;
                case "ringas":

                    if (enable_ringas.equals("0")) {
                        showActivateDialog(R.drawable.ringas_dis, 1, R.drawable.prov_ringas, "fromService", "ringas");

//                    Toasty.error(mContext, "This Product is disabled. Please contact Top it Up to enable the product!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkSettings(30, R.drawable.ringas_dis, R.drawable.prov_ringas)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 30);
                            startActivity(myIntent);
                        }
                    }


                    hideProgress();
                    return;
                case "oneforyou":
                /*if (enable_oneforu.equals("0")) {
                    showActivateDialog(R.drawable.provider_one_dis, 1, R.drawable.prov_one, "fromService", "oneforyou");
//                    Toasty.error(mContext, "This Product is offline. Please contact Top it Up to enable the product!!!", Toast.LENGTH_SHORT).show();
                } else {*/


                    if (checkSettings(24, R.drawable.provider_one_dis, R.drawable.prov_one)) {
                        if (enable_oneforu.equals("2")) {
                            myIntent = new Intent(mContext, activity_spi.class);
//                            myIntent.putExtra("service_provider_id", 31);
                            myIntent.putExtra("service_provider_id", 31);

                            startActivity(myIntent);
                        } else if (enable_oneforu.equals("3")) {
                            Toast.makeText(mContext, "This Product is offline.", Toast.LENGTH_SHORT).show();
//                        Toasty.error(mContext, "This Product is offline.", Toast.LENGTH_SHORT).show();
                        } else if (enable_oneforu.equals("0")) {
                            showActivateDialog(R.drawable.provider_one_dis, 1, R.drawable.prov_one, "fromService", "ott");
                        } else {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 31);
                            startActivity(myIntent);
                        }
                    }

//                }
                    hideProgress();
                    return;

                case "ott":
                    if (enable_ott.equals("3")) {
                        Toast.makeText(mContext, "This Product is offline.", Toast.LENGTH_SHORT).show();
                    } else if (enable_ott.equals("0")) {
                        showActivateDialog(R.drawable.prov_ott_dis, 1, R.drawable.prov_ott, "fromService", "ott");
                    } else {
                        int providerOtt = 25;
                        if (enable_ott.equals("2")) {
//                            providerOtt = 39;
                            providerOtt = 39;

                        } else {
                            providerOtt = 25;

                        }

                        if (checkSettings(providerOtt, R.drawable.prov_ott_dis, R.drawable.prov_ott)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", providerOtt);
                            startActivity(myIntent);
                        }


/*
                if (checkSettings(25)) {
                    myIntent = new Intent(mContext, activity_spi.class);
                    myIntent.putExtra("service_provider_id", 25);
                    startActivity(myIntent);
                }*/
                    }
                    hideProgress();
                    return;
                case "globalairtime":

                    if (enable_international.equals("0")) {
                        showActivateDialog(R.drawable.prov_global_dis, 1, R.drawable.prov_global, "fromService", "globalairtime");
                    } else {
                        if (checkSettings(21, R.drawable.prov_global_dis, R.drawable.prov_global)) {
                            myIntent = new Intent(mContext, activity_ding.class);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "virginmobile":

                    if (checkSettings(4, R.drawable.prov4, R.drawable.prov4)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 4);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "DSTV":

                    if (enable_bill.equals("0")) {
                        showActivateDialog(R.drawable.prov_dstv_dis, 1, R.drawable.prov_dstv, "fromService", "DSTV");

                    } else {
                        if (setting_chk_disable_cashier_bill.equals("1") && is_admin.equals("0")) {
                            Toasty.info(mContext, "Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            myIntent = new Intent(activity_main.this, activity_bill_payment.class);
                            myIntent.putExtra("bill_payment_type", 1);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "paybills":

                    if (enable_bill.equals("0")) {
                        showActivateDialog(R.drawable.prov_pay_dis, 1, R.drawable.prov_pay, "fromService", "paybills");

                    } else {
                        if (setting_chk_disable_cashier_bill.equals("1") && is_admin.equals("0")) {
                            Toasty.info(mContext, "Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            myIntent = new Intent(activity_main.this, activity_bill_payment.class);
                            myIntent.putExtra("bill_payment_type", 4);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "busticket":

                    if (enable_bluvoucher.equals("0")) {
                        showActivateDialog(R.drawable.prov_blue_dis, 1, R.drawable.blue_v_new, "fromService", "busticket");

//                    Toasty.error(mContext, "This Product is disabled. Please contact Top it Up to enable the product!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkSettings(29, R.drawable.prov_blue_dis, R.drawable.blue_v_new)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 29);//cell c
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "moneytransfer":

                    if (enable_bill.equals("0")) {
                        showActivateDialog(R.drawable.prov_money_dis, 1, R.drawable.prov_money, "fromService", "moneytransfer");

                    } else {
                        if (setting_chk_disable_cashier_bill.equals("1") && is_admin.equals("0")) {
                            Toasty.info(mContext, "Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            myIntent = new Intent(activity_main.this, activity_bill_payment.class);
                            myIntent.putExtra("bill_payment_type", 3);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "talk360":

                    if (checkSettings(17, R.drawable.prov_talk360, R.drawable.prov_talk360)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 17);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "1o1mobile":

                    if (checkSettings(18, R.drawable.prov_101, R.drawable.prov_101)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 18);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "flexepin":

                    if (checkSettings(40, R.drawable.flexipin, R.drawable.flexipin)) {
                        if (enable_flexipin.equals("0")) {
                            showActivateDialog(R.drawable.prov_ott_dis, 1, R.drawable.prov_ott, "fromService", "ott");
                        } else {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 40);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "lyca":

                    if (checkSettings(19, R.drawable.prov_lyca, R.drawable.prov_lyca)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 19);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "cashmx":

              /*  myIntent = new Intent(mContext, activity_main.class);
                myIntent.putExtra("service_provider_id", 1004);
                startActivity(myIntent);*/
//                    if (enable_cashms.equals("0")) {


                    if (enable_cashms.equals("0")) {
                        showActivateDialog(R.drawable.cashmxn, 1, R.drawable.cashmxn, "fromService", "cashmx");

//                    Toasty.error(mContext, "This Product is disabled. Please contact Top it Up to enable the product!!!", Toast.LENGTH_SHORT).show();
                    } else {
//                        if (checkSettings(21, R.drawable.cashmxn, R.drawable.cashmxn)) {
                        //myIntent = new Intent(mContext, activity_cashmx.class);
                        try {
                        /*Intent intent = new Intent();
                        intent.setComponent(new ComponentName("za.co.topitup.supplier", "za.co.topitup.suppliers.CashManActivity"));
                        intent.putExtra("licence", Topitup.TIU_LICENSE);
                        intent.putExtra("posUser", Topitup.POSUSER_ID);
                        intent.putExtra("deviceType", "android");
                        intent.putExtra("retailerId", 1110);
                        intent.putExtra("liveEnv", false);
                        startActivity(intent);*/
                            settings = getSharedPreferences("TIUPREF", 0);
                            String selectedPrinter = settings.getString("printer", "inner");

                            Boolean isLive = false;
                            isLive = !Topitup.TIU_SERVER.equals("DEMO");
                            String lastDeviceAddress = settings.getString("last_device_address", null);

                            activity_login.fromScreen = "supplier";
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("za.co.topitup.suppliers", "za.co.topitup.suppliers.CashManActivity"));
                            intent.putExtra("licence", Topitup.TIU_LICENSE);
                            intent.putExtra("posUser", Topitup.POSUSER_ID);
                            intent.putExtra("deviceType", "android");
                            intent.putExtra("retailerId", Topitup.CUSTOMER_ID);
                            intent.putExtra("liveEnv", Topitup.TIU_SERVER);
                            intent.putExtra("testEnv", Topitup.TIU_SERVER);
                            intent.putExtra("connected", selectedPrinter);
                            intent.putExtra("lastDeviceAddress", lastDeviceAddress);
                            startActivity(intent);

                           /* Intent intent = new Intent();
                            intent.setComponent(new ComponentName("za.co.topitup.suppliers", "za.co.topitup.suppliers.CashManActivity"));
                            intent.putExtra("licence", "0c65c164-d9ee-11ed-99c4-0cc47a4f05f0");
                            intent.putExtra("posUser", "22476");
                            intent.putExtra("deviceType", "android");
                            intent.putExtra("retailerId", "51613");
                            intent.putExtra("liveEnv", true);
                            startActivity(intent);*/
/*

                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("za.co.topitup.suppliers", "za.co.topitup.suppliers.CashManActivity"));
                            intent.putExtra("licence", "");
                            intent.putExtra("posUser", "");
                            intent.putExtra("deviceType", "");
                            intent.putExtra("retailerId", "");
                            intent.putExtra("liveEnv", true);
                            startActivity(intent);
*/

                        } catch (ActivityNotFoundException e) {
                            Log.e("TAG", e.getMessage());
                        } catch (NullPointerException e) {
                            Log.e("TAG", e.getMessage());
                        }
                    /*
                    myIntent = new Intent(mContext, activity_cash_management.class);

                    startActivity(myIntent);*/
//                        }
                    }

                    // myIntent = new Intent(mContext, activity_cash_management.class);

                    // startActivity(myIntent);
                    hideProgress();
                    return;
                case "coca":
/*
               myIntent = new Intent(mContext, activity_cash_management.class);
                myIntent.putExtra("ptype", 1);
                startActivity(myIntent);
                return;*/

                case "nandhni":

                    myIntent = new Intent(mContext, activity_cash_management.class);
                    myIntent.putExtra("ptype", 2);
                    startActivity(myIntent);

                    hideProgress();
                    return;
                case "rica":

                    myIntent = new Intent(mContext, activity_web_rica.class);
                    startActivity(myIntent);

                    hideProgress();
                    return;
                case "virtualvas":

                    myIntent = new Intent(activity_main.this, activity_virtual_vas.class);
                    startActivity(myIntent);
                    hideProgress();

                    return;
                case "siyavla":

                    if (checkSettings(15, R.drawable.prov_siyavula, R.drawable.prov_siyavula)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 15);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "ikeja":

                    if (checkSettings(23, R.drawable.prov_ikea_dis, R.drawable.prov_ikea)) {
                        myIntent = new Intent(mContext, activity_spi.class);
                        myIntent.putExtra("service_provider_id", 23);
                        startActivity(myIntent);
                    }

                    hideProgress();
                    return;
                case "netflix":

                    if (enable_international.equals("0")) {
                        showActivateDialog(R.drawable.prov_netflix_dis, 1, R.drawable.prov_netflix, "fromService", "netflix");
                    } else {
                        if (checkSettings(28, R.drawable.prov_netflix_dis, R.drawable.prov_netflix)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 28);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "uber":

                    if (enable_international.equals("0")) {
                        showActivateDialog(R.drawable.prov_uber_dis, 1, R.drawable.prov_uber, "fromService", "uber");
                    } else {
                        if (checkSettings(27, R.drawable.prov_uber_dis, R.drawable.prov_uber)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 27);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "spotify":

                    if (enable_international.equals("0")) {
                        showActivateDialog(R.drawable.prov_spotify_dis, 1, R.drawable.prov_spotify, "fromService", "spotify");
                    } else {
                        if (checkSettings(26, R.drawable.prov_spotify_dis, R.drawable.prov_spotify)) {
                            myIntent = new Intent(mContext, activity_spi.class);
                            myIntent.putExtra("service_provider_id", 26);
                            startActivity(myIntent);
                        }
                    }

                    hideProgress();
                    return;
                case "cardpay":


                    //   Log.i("DEVICESLNO",Topitup.DEVICE_SLNO);
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();

                    //  Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",86400000);

                    editor.putLong("TIMEOUT_IN_MILLI", 86400000);
                    editor.commit();


               /* Intent myIntent22 = new Intent(mContext, activity_adpay_new.class);
                startActivity(myIntent22);*/

                    Log.d("DEVICE_TYPE", Topitup.DEVICE_TYPE);
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String versionCode = "1.0";
                        String versionCodetiu = "1.0";
                        if (enable_realtime_swipe.equals("1") || enable_realtime_swipe.equals("2") || enable_realtime_swipe.equals("3") || enable_realtime_swipe.equals("6")) {
                            try {
                                versionCode = getPackageManager().getPackageInfo("com.wiseasy.cashier", 0).versionName;
                            } catch (PackageManager.NameNotFoundException e) {

                                e.printStackTrace();
                            }
                            try {
                                versionCodetiu = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                            } catch (PackageManager.NameNotFoundException e) {

                                e.printStackTrace();
                            }
                            //addpayVer="1";
                            // showCustomDialog("Top it Up",versionCode,true);

                            String addPayEnabled = settings.getString("setting_add_pay", "0");

                            ///   if (addpayVer.equals("1")) {
                            if (true) {

                                if(addPayEnabled.equals("1")){
                                    Toast.makeText(mContext, "add pay enabled", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();

                                    Log.e("sale amount", "....sale amount......" );
                                    JSONObject jsonObject = new JSONObject();

                                    try {
                                        intent.setPackage("com.wiseasy.cashier");
//                                        intent.setAction("com.wiseasy.transaction.call");
                                        intent.putExtra("version", "A01");
                                        intent.putExtra("appId", "wza61f2e0da04ff7f8");
//                                        intent.putExtra("transType", "SALE");
//                                        jsonObject.put("businessOrderNo", orderno);
//                                        jsonObject.put("paymentScenario", "CARD");
//                                        jsonObject.put("amt", amount);
//                                        jsonObject.put("userID", "01");
//                                        jsonObject.put("note", notes);
                                        intent.putExtra("transData", jsonObject.toString());
                                        startActivityForResult(intent, 1);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Intent myIntent2 = new Intent(mContext, activity_adpay_new.class);
                                    startActivity(myIntent2);

                                }
//                            if (true) {


                            } else {
                                showCustomDialog("Top it Up", "Addpay Version is not supported for this\nTop it Up Version \n\n Please contact Top it Up on 0860 111 723 \n to update to latest Addpay version \n\n Current Software Versions \n Addpay : " + versionCode + " \n Top it Up :" + versionCodetiu, true);
                            }

                        } else {
                            showCustomDialog("Top it Up", "Card Payment Feature is not enabled!!!", true);
                            //Toasty.error(mContext, "Card Payment Feature is not enabled!!!", 25000).show();
                        }

                    } else {
                        if (setting_bypass_calculator.equals("1")) {

                            Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.tallorder.pocketpos");
                            //    notificationIntent.setPackage(null); // The golden row !!!
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            if (notificationIntent != null) {
                                //ringtone();

                                editor.putString("setting_pockepos_open", "1");
                                startActivity(notificationIntent);
                            } else {
                                Toasty.error(mContext, "PocketPOS is not Installed!!!", 25000).show();
                            }
                        } else {
                            myIntent = new Intent(mContext, activity_pos_calculator.class);
                            editor.putString("setting_pockepos_open", "1");
                            startActivity(myIntent);
                        }
                    }
                    editor.commit();


                    hideProgress();
                    //  Intent i = new Intent();
                    // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // i.setAction("android.intent.action.VIEW");
                    // i.setComponent(ComponentName.unflattenFromString("com.tallorder.pocketpos/com.tallorder.pocketpos.MainActivity"));

                    // setClassName("PACKAGE_NAME","SPECIFIC_CLASS");
                    // Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("com.tallorder.pocketpos");
                    // launchIntentp.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //    Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("com.maltaisn.calcdialoglib.demo");
                    //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                    //   if (launchIntentp != null) {
               /*      int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText("40.00");
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("amount","40.00");
                        clipboard.setPrimaryClip(clip);
                    }*/
                    //  startActivity(launchIntentp);//null pointer check in case package name was not found
                    //startActivity(i);

                    //myIntent = new Intent(mContext, activity_pos_calculator.class);
                    // myIntent.putExtra("service_provider_id", 100);
                    //startActivity(myIntent);

//                }
//               else{
//
//                   myIntent = new Intent(mContext, activity_spi.class);
//                   myIntent.putExtra("service_provider_id", 100);
//                   startActivity(myIntent);
//               }
                    // private String[] NAME = {"vodacom","mtn","cellc","telkom","topitupeasi","globalairtime","virginmobile","talk360","lyca",
                    // "1o1mobile","mamamoney","paybills","siyavla","electricity","unipin","cashmx","rica","virtualvas"};
            }

        }
    }


    private void showLoyaltyDialog(
            LoyaltyStatus status,
            int sales,
            int target,
            int percent
    ) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loyalty_awards);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.tvTitle);
        TextView winText = dialog.findViewById(R.id.tvWinText);
        TextView descText = dialog.findViewById(R.id.tvDescText);
        TextView percentText = dialog.findViewById(R.id.tvPercent);
        TextView footer = dialog.findViewById(R.id.tvFooter);
//        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ImageView winnerImg = dialog.findViewById(R.id.imgWinner);
        ImageView close = dialog.findViewById(R.id.btnClose);

//        progressBar.setProgress(percent);
        percentText.setText(percent + "%");

        switch (status) {

            case IN_PROGRESS:
                winText.setText("Win R500");
                descText.setText("If you reach sales > R30,000 on these products");
                footer.setText("Hurry, only 5 days left\nExpires on 30 June 2025");
                winnerImg.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
                percentText.setVisibility(View.VISIBLE);
                break;

            case WON:
                winText.setText("You have WON\nR500 Prize");
                descText.setText("Target Achieved\nSales > R30,000");
                footer.setText("Congratulations");
                winnerImg.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
                percentText.setVisibility(View.GONE);
                break;

            case MISSED:
                winText.setText("R500 Prize\nNot Awarded");
                descText.setText("Target Missed\nSales > R30,000");
                footer.setText("See you next time");
                winnerImg.setVisibility(View.GONE);
//                progressBar.setVisibility(View.GONE);
                percentText.setVisibility(View.GONE);
                break;
        }

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
        dialog.show();
    }
    private void showActivateDialog(int image, int providerId, int prov_image, String fromDisable, String productName) {
        dialogactivate = new Dialog(this);
        dialogactivate.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogactivate.setContentView(R.layout.dialog_dark_new);
        dialogactivate.setCancelable(true);
        this.setFinishOnTouchOutside(false);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialogactivate.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogactivate.show();
        dialogactivate.getWindow().setAttributes(lp);
        ImageView img_disable = dialogactivate.findViewById(R.id.img_disable);
        LinearLayout ll_pin = dialogactivate.findViewById(R.id.ll_pin);

        TextView txt_activate = dialogactivate.findViewById(R.id.txt_activate);
        EditText ed_pin = dialogactivate.findViewById(R.id.ed_pin);
        TextView txt_ok = dialogactivate.findViewById(R.id.txt_ok);
        TextView txt_error = dialogactivate.findViewById(R.id.txt_error);

        // progress = dialogactivate.findViewById(R.id.progress);
        TextView txt_reference = dialogactivate.findViewById(R.id.txt_reference);
        TextView txt_cancel = dialogactivate.findViewById(R.id.txt_cancel);

        img_disable.setImageResource(image);

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogactivate.dismiss();
            }
        });
        txt_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromDisable.equals("fromService")) {
                    showProgressDialog();
                    callProductActivate(productName, txt_reference);
                } else {
                    ll_pin.setVisibility(VISIBLE);
                }
            }
        });
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pinEntered = ed_pin.getText().toString();

                RealmResults<pos_users> pos_users = realm.where(pos_users.class).equalTo("posuser_pin", pinEntered).equalTo("posuser_isadmin", 1).findAll();
                if (pos_users.size() > 0) {
                    txt_error.setVisibility(View.GONE);
                    if (providerId == 31) {
                        service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 24).findFirst();

                    } else if (providerId == 39) {
                        service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 25).findFirst();

                    } else {
                        service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", providerId).findFirst();

                    }


                    realm.beginTransaction();
                    service_provider_setting.setVisible_admin(true);
                    realm.commitTransaction();
                    dialogactivate.dismiss();
//                    Log.e("providerId", "providerId........." + providerId);
                    showSuccessDialog(providerId, prov_image);
                } else {
                    ed_pin.setText("");
                    txt_error.setVisibility(VISIBLE);
                }

            }
        });


    }

    private void showProgressDialog() {

        if (progressdialog == null) {
            progressdialog = new ProgressDialog(activity_main.this);
            progressdialog.setMessage("Requesting ");
            progressdialog.setIndeterminate(false);
            progressdialog.setCancelable(false);
        }
        progressdialog.show();
    }

    private void dismissProgressDialog() {
        if (progressdialog != null && progressdialog.isShowing()) {
            progressdialog.dismiss();
        }
    }

    private void callProductActivate(String productName, TextView txt_reference) {
        final Call<ResponseBody> call = apiService.activate_product(Topitup.TIU_LICENSE, productName);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dismissProgressDialog();
                if (!response.headers().get("Server").equals("TIU")) {
                    // stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
                    return;
                }

                String res_realtime = "";
                try {
                    res_realtime = response.body().string();
                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                }

                if (res_realtime.trim().length() == 0 || res_realtime.contains("<error><err>") || res_realtime.contains("ERR:") || res_realtime.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res_realtime;
                    if (res_realtime.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    // stopCustomDialog("Problem",matcher);
                    Toasty.error(mContext, "API Error" + matcher, 8000, true).show();

                } else {        //Response OK


                    try {
                        JSONObject reader = new JSONObject(res_realtime);
                        String pid = reader.getString("pid");
                        txt_reference.setVisibility(VISIBLE);
                        txt_reference.append(Html.fromHtml("<b>" + pid + "</b>"));

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();

                if (t instanceof IOException) {
                    // stopCustomDialog("No Internet","Please check that your internet connection is working.");
                } else {
                    //   stopCustomDialog("Problem",t.getMessage());
                }

            }

        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    /*  public void showActivateDialog() {
          dialog_active = new Dialog(activity_main.this);
          dialog_active.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
          dialog_active.setContentView(R.layout.activation_login);
          dialog_active.setCancelable(true);
  
          WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
          //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
          lp.copyFrom(dialog_active.getWindow().getAttributes());
          lp.width = WindowManager.LayoutParams.MATCH_PARENT;
          lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
  
          otp1 = dialog_active.findViewById(R.id.otp1);
          otp2 = dialog_active.findViewById(R.id.otp2);
          otp3 = dialog_active.findViewById(R.id.otp3);
          otp4 = dialog_active.findViewById(R.id.otp4);
          TextView txt_ok = dialog_active.findViewById(R.id.txt_ok);
          TextView txt_title = dialog_active.findViewById(R.id.txt_title);
  
  
          otp1.requestFocus();
          otp1.setFocusable(true);
  
          InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
  //        imm.showSoftInput(otp1, InputMethodManager.SHOW_IMPLICIT);
  
          imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
          txt_error = dialog_active.findViewById(R.id.txt_error);
  
          txt_title.setText("Welcome back " + Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
  
          setotpinput();
  
  
          dialog_active.show();
      }*/
    public void showActivateDialog() {
        dialog_active = new Dialog(activity_main.this);
        dialog_active.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_active.setContentView(R.layout.activation_login);
        dialog_active.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_active.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        otp1 = dialog_active.findViewById(R.id.otp1);
        otp2 = dialog_active.findViewById(R.id.otp2);
        otp3 = dialog_active.findViewById(R.id.otp3);
        otp4 = dialog_active.findViewById(R.id.otp4);
        TextView txt_ok = dialog_active.findViewById(R.id.txt_ok);
        TextView txt_title = dialog_active.findViewById(R.id.txt_title);

        txt_error = dialog_active.findViewById(R.id.txt_error);

        txt_title.setText("Welcome back " + Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));

        // Show the dialog first
        dialog_active.show();

        // Now request focus and show keyboard after dialog is visible
        otp1.requestFocus();
        otp2.setFocusable(false);
        otp2.setClickable(false);

        otp3.setFocusable(false);
        otp3.setClickable(false);

        otp4.setFocusable(false);
        otp4.setClickable(false);

        // Delay showing the keyboard to ensure the dialog is open
        otp1.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 200);  // 200ms delay

        setotpinput();
    }


    private void activateDismissDialog() {
        pinEntered = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
        pos_users pos_users = realm.where(pos_users.class).equalTo("posuser_pin", pinEntered).equalTo("posuser_status", 1).findFirst();

        if (null == pos_users) {

            otp1.setText("");
            otp2.setText("");
            otp3.setText("");
            otp4.setText("");

            otp1.requestFocus();
            txt_error.setVisibility(VISIBLE);
            if (txt_error.getVisibility() == VISIBLE) {
                otp1.requestFocus();
                otp2.setFocusable(false);
                otp2.setClickable(false);

                otp3.setFocusable(false);
                otp3.setClickable(false);

                otp4.setFocusable(false);
                otp4.setClickable(false);
            }

        } else {
            txt_error.setVisibility(View.GONE);
            BaseActivity.fromVoucherSale = false;
            ((Topitup) getApplication()).startUserSessionForActive();

            Topitup.POSUSER_ID = String.valueOf(pos_users.posuser_id);
            Topitup.IS_ADMIN = String.valueOf(pos_users.posuser_isadmin);
            Topitup.POSUSER_NAME = pos_users.posuser_firstname + " " + pos_users.posuser_surname;
            is_admin = String.valueOf(pos_users.posuser_isadmin);
            setUserNameAndDesignation();
            updateBalance(String.valueOf(pos_users.posuser_isadmin));
            dialog_active.dismiss();
        }
    }

    private void setotpinput() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp2.setFocusableInTouchMode(true);
                    otp2.requestFocus();
//                    otp2.setEnabled(true);
                    isFromElse = false;
                    otp1.setBackground(getResources().getDrawable(R.drawable.dot_red));
                    pinEntered = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                    if (pinEntered.length() >= 4) {
                        activateDismissDialog();
                    }
                } else {
                    otp1.setBackground(getResources().getDrawable(R.drawable.dot_white));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("otp", "before text");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("otp", "onText text");
                if (!s.toString().trim().isEmpty()) {
                    isFromElse = false;
                    otp3.setFocusableInTouchMode(true);
                    otp3.requestFocus();
//                    otp3.setEnabled(true);
                    otp2.setBackground(getResources().getDrawable(R.drawable.dot_red));
                    pinEntered = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                    if (pinEntered.length() >= 4) {
                        activateDismissDialog();
                    }
                } else {
                    otp2.setBackground(getResources().getDrawable(R.drawable.dot_white));
                    otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("otp", "after text");

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    isFromElse = false;

                    otp3.setBackground(getResources().getDrawable(R.drawable.dot_red));

//                    otp4.setEnabled(true);
                    otp4.setFocusableInTouchMode(true);
                    otp4.requestFocus();
                    pinEntered = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                    if (pinEntered.length() >= 4) {
                        activateDismissDialog();
                    }
                } else {
                    otp3.setBackground(getResources().getDrawable(R.drawable.dot_white));

                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    isFromElse = true;
                    otp4.setBackground(getResources().getDrawable(R.drawable.dot_red));
                    pinEntered = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                    if (pinEntered.length() >= 4) {
                        activateDismissDialog();
                    }

                } else {
                    otp3.requestFocus();
                    otp4.setBackground(getResources().getDrawable(R.drawable.dot_white));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("key event", "otp1..." + isFromElse);
                if (i == KeyEvent.KEYCODE_DEL && i == KeyEvent.ACTION_DOWN) {

                    if (otp1.getText().toString().equals("")) {
                        if (!isFromElse) {

                            otp1.setText("");
                            otp1.requestFocus();
                        } else {
                            isFromElse = false;

                        }
                    } else {

                        otp1.setText("");
                        otp1.requestFocus();
                    }

                }
                return false;
            }
        });
        otp2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("key event", "otp2..." + isFromElse);
                if (i == KeyEvent.KEYCODE_DEL) {

                    if (otp2.getText().toString().equals("")) {
                        if (!isFromElse) {
                            isFromElse = true;
                            otp1.setText("");
                            otp1.requestFocus();
                        } else {
                            isFromElse = false;

                        }
                    } else {

                        otp2.setText("");
                        otp2.requestFocus();
                    }

                }
                return false;
            }
        });
        otp3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("key event", "otp3..." + isFromElse);
                if (i == KeyEvent.KEYCODE_DEL) {

                    if (otp3.getText().toString().equals("")) {
                        if (!isFromElse) {
                            isFromElse = true;
                            otp2.setText("");
                            otp2.requestFocus();
                        } else {
                            isFromElse = false;

                        }
                    } else {
                        isFromElse = false;

                        otp3.setText("");
                        otp3.requestFocus();
                    }

                }
                return false;
            }
        });
        otp4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("key event", "otp4...");
                if (i == KeyEvent.KEYCODE_DEL) {
                    Log.e("key event", "otp4.1111.." + isFromElse);

                    if (otp4.getText().toString().equals("")) {
                        if (!isFromElse) {
                            isFromElse = true;
                            otp3.setText("");
                            otp3.requestFocus();
                        }

                    } else {
                        isFromElse = false;
                        otp4.setText("");
                        otp4.requestFocus();
                    }

                }
                return false;
            }
        });


    }

    private void showSuccessDialog(int providerId, int image) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.success_dialog);
        dialog.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView img_logo = dialog.findViewById(R.id.img_logo);
        TextView txt_activation = dialog.findViewById(R.id.txt_activation);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);
        img_logo.setImageResource(image);
        String providerName = getProductName(providerId);

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tabs();
                dialog.dismiss();
            }
        });

        txt_activation.setText("Product " + providerName);
        dialog.show();
    }

    private String getProductName(int providerId) {
        if (providerId == 1) {
            return "vodacom";
        } else if (providerId == 2) {
            return "mtn";
        } else if (providerId == 3) {
            return "cell c";
        } else if (providerId == 7) {
            return "worldcall";
        } else if (providerId == 10) {
            return "telkom";
        } else if (providerId == 12) {
            return "unipin";
        } else if (providerId == 20) {
            return "easyload";
        } else if (providerId == 30) {
            return "ringas";
        } else if (providerId == 24) {
            return "oneforyou";
        } else if (providerId == 25) {
            return "ott";
        } else if (providerId == 21) {
            return "globalairtime";
        } else if (providerId == 4) {
            return "virginmobile";
        } else if (providerId == 17) {
            return "talk360";
        } else if (providerId == 18) {
            return "1o1mobile";
        } else if (providerId == 19) {
            return "lyca";
        } else if (providerId == 21) {
            return "cashmx";
        } else if (providerId == 15) {
            return "siyavla";
        } else if (providerId == 23) {
            return "ikeja";
        } else if (providerId == 28) {
            return "netflix";
        } else if (providerId == 27) {
            return "uber";
        } else if (providerId == 26) {
            return "spotify";
        } else if (providerId == 29) {
            return "blu voucher";
        }
        return "";
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
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

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        try {
            unregisterNetworkChanges();
// ??? Might cause issues

//Topitup.setting.mPosPowerOff();
//handler.onDestroy();
//Topitup.setting.onDestroy();
        } catch (Exception ex) {
            Timber.e(ex.getMessage());
        }

        try {
            if (batteryReceiver != null) {
                unregisterReceiver(batteryReceiver);
                batteryReceiver = null;
            }
        } catch (Exception e) {

        }
//        unregisterReceiver(batteryReceiver);


        //AAUpdaterController.end();

        super.onDestroy();
        //
    }

    @Override
    protected void onResume() {
        if (dialog_active != null && dialog_active.isShowing()) {
            otp1.setBackground(getResources().getDrawable(R.drawable.dot_white));
            otp2.setBackground(getResources().getDrawable(R.drawable.dot_white));
            otp3.setBackground(getResources().getDrawable(R.drawable.dot_white));
            otp4.setBackground(getResources().getDrawable(R.drawable.dot_white));
            pinEntered="";
            otp1.setFocusable(true);
            otp1.setFocusableInTouchMode(true);
            otp1.requestFocus();
            otp2.setFocusable(false);
            otp3.setFocusable(false);
            otp4.setFocusable(false);

            otp1.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }, 100);
           // setotpinput();
        }

        String selectedPrinter = settings.getString("printer", "inner");
        get_balance();
        update_balance();
        ll_main_bottom.setWeightSum(3);

        if (Topitup.IS_ADMIN.equals("1")) {
            if (selectedPrinter.equals("usb") || selectedPrinter.equals("bluetooth")) {

                if (settings.getString("printer_cash_drawer", "0").equals("1")) {
                    llCashDrawer.setVisibility(VISIBLE);
                    ll_main_bottom.setWeightSum(4);
                } else {
                    llCashDrawer.setVisibility(View.GONE);
                    ll_main_bottom.setWeightSum(3);

                }
            } else {
                ll_main_bottom.setWeightSum(3);

                llCashDrawer.setVisibility(View.GONE);
            }
        } else {

            if (selectedPrinter.equals("usb") || selectedPrinter.equals("bluetooth")) {

                if (settings.getString("printer_cashier_cash_drawer", "0").equals("1")) {
                    llCashDrawer.setVisibility(VISIBLE);
                    ll_main_bottom.setWeightSum(4);
                } else {
                    llCashDrawer.setVisibility(View.GONE);
                    ll_main_bottom.setWeightSum(3);

                }
            } else {
                ll_main_bottom.setWeightSum(3);

                llCashDrawer.setVisibility(View.GONE);
            }
        }


//        final RealmResults<MultiVoucherSelectedItems> multiVoucherSelectedItems = realm.where(MultiVoucherSelectedItems.class).findAll();
        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                sizeOfMulti = activity_spi.selectedProviderIds.size();
                txt_multi_item_count.setText("Total(" + sizeOfMulti + ")");
                totalMultiAmount = 0.0;
                for (int i = 0; i < sizeOfMulti; i++) {

                    Double amount = Double.parseDouble(activity_spi.selectedProviderIds.get(i).text.replace("R", "").trim());
                    totalMultiAmount += amount;
                    Log.e("selected multi vouchers Size", amount + "......multi vouchers count.." + totalMultiAmount);

                }
            /*if(sizeOfMulti>0){
                recycler_horizantal.setVisibility(View.GONE);
                txt_cancel_multi_voucher.setVisibility(View.VISIBLE);
                txt_multi_voucher.setVisibility(View.GONE);
                rl_multi_item_view.setVisibility(View.VISIBLE);
                txt_print_multi_voucher.setVisibility(View.VISIBLE);
            }else{
                recycler_horizantal.setVisibility(View.VISIBLE);
                txt_cancel_multi_voucher.setVisibility(View.GONE);
                txt_print_multi_voucher.setVisibility(View.GONE);

                txt_multi_voucher.setVisibility(View.VISIBLE);
                rl_multi_item_view.setVisibility(View.GONE);
            }*/
                txt_multi_amount.setText("R " + totalMultiAmount);

                selectedMultiItems.clear();
                selectedMultiItems.addAll(activity_spi.selectedProviderIds);

                multiVoucherAdapter = new MultiVoucherAdapter(activity_main.this, selectedMultiItems);
                recyclerVouchers.setAdapter(multiVoucherAdapter);
            }
        }

        activity_login.fromScreen = "activity_main";
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI_ORI", 86400000);
        setting_bypass_calculator = settings.getString("setting_bypass_calculator", "0");
        setting_bypass_calculator = settings.getString("setting_bypass_calculator", "0");
        setting_pockepos_open = settings.getString("setting_pockepos_open", "0");
        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos", "0");
        enable_realtime_swipe = settings.getString("enable_realtime_swipe", "0");
        editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
        editor.putString("setting_dnp", "0");
        editor.commit();
        ll_logout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ll_admin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ll_reprint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        img_logout.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        img_admin.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        img_reprint.setColorFilter(ContextCompat.getColor(activity_main.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//                img_logout.setImageTintMode();
        img_logout_txt.setTextColor(getResources().getColor(R.color.white));
        img_admin_txt.setTextColor(getResources().getColor(R.color.white));
        img_reprint_txt.setTextColor(getResources().getColor(R.color.white));



        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((Topitup) getApplication()).unregisterNetworkCallback();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    /*****
     *
     *
     * remove
     *
     *
     */

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

    public void get_voucher() {


//        Toasty.error(mContext, Topitup.POSUSER_ID, 8000, true).show();

        if (total_vouchers_to_print > 1) {

            //for (int current_voucher = 1; current_voucher <= total_vouchers_to_print; current_voucher++) {

            pop_title.setText("Requesting Voucher (" + current_voucher + " of " + total_vouchers_to_print + ")");
            do_voucher_request();

            //}

        } else {

            pop_title.setText("Requesting Voucher");
            do_voucher_request();

        }


    }

    private void showCustomDialog(String sTitle, String sContent) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        bt_close = dialog.findViewById(R.id.bt_close);
        bt_close.setVisibility(View.GONE);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullscreenCall();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }

    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }
    //  AppCompatButton btn_Paper_ignore_always;

    private void do_voucher_request() {


        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts = 2;
                showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                return;
            }
        }

        if (!PrinterTopitup.check_paper(mContext)) {
            printerQ1Sts = 1;
            //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
            return;
        }
        String splitDeno;
        if (amount_deno.contains(".")) {
            splitDeno = amount_deno.substring(0, amount_deno.indexOf('.'));

        } else {
            splitDeno = amount_deno;
        }
        int cost = Integer.parseInt(splitDeno);
//        Toast.makeText(activity_spi.this,"amount selected....."+amount_deno,Toast.LENGTH_LONG).show();

        int voucher_cost = cost * 100;
        //   final Call<voucher_response> call = apiService.get_voucher_json(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "",voucher_cost, spi_id, "");
        final Call<voucher_response> call = apiService.get_voucher_json(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", voucher_cost, spi_id, "");

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {
                        voucher_response result = response.body();

                        //Timber.e(result.toString());
                        FullscreenCall();
                        if (result.err.length() > 0) {

                            //String matcher = StringUtils.substringBetween(result.toString(), "<err>", "</err>");
                            //Toasty.error(mContext, result.err, 8000, true).show();

                            pop_title.setText("Voucher Request Issue");
                            pop_content.setText(result.err);
                            bt_close.setVisibility(VISIBLE);
                            pageLoadingWrapper.setVisibility(View.GONE);

                        } else {        //Response OK

                            pop_title.setText("Printing #" + current_voucher);
                            pop_content.setText("Your voucher is busy printing.\nPlease check reprint if there is an issue.");
                            bt_close.setVisibility(View.GONE);
                            pageLoadingWrapper.setVisibility(View.GONE);

                            String slip = result.print_data;
                            if (print_address.equals("1"))
                                slip = slip.replace("1\n1Trx", "1" + company_addrs1 + ", " + company_addrs2 + "\n1\n1Trx");

                            if (spi_barcode != null && !spi_barcode.equals("")) {
                                slip = slip + "BARCODE:" + spi_barcode;
                            }

                            //Printer.store_last_reprint(slip);

                            Timber.i(slip);

                            PrinterTopitup.print_data(slip);

                            if (current_voucher < total_vouchers_to_print) {
                                current_voucher++;

                                print_handler.postDelayed(print_handler_runnable, 2000);

                            } else {
                                handler.postDelayed(runnable, 2000);
                            }


                            fin_balance fb = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findFirst();

                            realm.beginTransaction();
                            fb.balance = result.balance;
                            fb.available_balance = result.available_balance;
                            fb.balance_cash = result.balance_cash;
                            fb.commission = result.commission;
                            fb.swipe = result.swipe;
                            fb.loyalty = result.loyalty;

                            realm.commitTransaction();

                        }
                    } catch (Exception e) {
                     /*   if (response.raw() != null)
                            response.raw().close();*/
                    }
                } else {
                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();

                if (t instanceof SocketTimeoutException) {

                    pop_title.setText("Connection Issue");
                    pop_content.setText("You could have been charged, Please reprint or view your sales history when your internet connection returns.");
                    bt_close.setVisibility(VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                } else {

                    //t.printStackTrace();

                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                }

                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });


    }

    private void get_last_voucher_info() {

        Call<ResponseBody> call = apiService.get_last_voucher_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {


                    String res = "";
                    try {
                        res = response.body().string();
                    } catch (Exception ex) {
                        if (response.body() != null)
                            response.body().close();
                    }


                    //Timber.e("VOUCHER " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {
                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();
                    } else {

                        String json = res;

                        try {

                            JSONObject obj = new JSONObject(json);

                            //Log.d("My App", obj.toString());
                            //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                            if (obj.getString("result").equals("-1")) {

                                txt_last_header.setVisibility(View.GONE);
                                txt_last_time.setVisibility(View.GONE);
                                txt_last_voucher_info.setVisibility(View.GONE);

                                bt_reprint.setVisibility(View.GONE);

                            } else {

//                                if (Integer.parseInt(obj.getString("time_diff")) < 30) {
//                                    Toasty.error(mContext, "Voucher sold " + obj.getString("time_diff") + " seconds ago", 9000, true).show();
//                                }
                                txt_last_header.setVisibility(View.GONE);
                                txt_last_time.setVisibility(View.GONE);
                                txt_last_voucher_info.setVisibility(View.GONE);

                                bt_reprint.setVisibility(View.GONE);
                                stock_uid = obj.getString("stock_uid");

                                //    txt_last_header.setText(obj.getString("last_header"));
                                //   txt_last_time.setText(obj.getString("last_time"));

                                //  txt_last_voucher_info.setText(obj.getString("message"));
                                //  bt_reprint.setVisibility(View.VISIBLE);

                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        bt_process.setEnabled(true);

                    }

                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                    //Timber.e("REPRINT " + ex.getMessage());
                    Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }
        });

    }

    public void doReprintLogic(String stock_uid) {

        final Call<voucher_response> call = apiService.airtime_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid, Topitup.DEVICE_TYPE);

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {


                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }
                    try {
                        voucher_response result = response.body();

                        //Timber.e(result.toString());

                        if (result.err.length() > 0) {

                            //

                        } else {        //Response OK

                            //Printer.store_last_reprint(result.print_data);

                            PrinterTopitup.print_data(result.print_data);

                        }
                    } catch (Exception e) {
                       /* if (response.raw() != null)
                            response.raw().close();*/
                    }


                } else {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }

        });


    }

    public void show_multi_voucher(Integer service_provider_id, int drawable, String deno) {
        FullscreenCall();
        //Timber.i("PLUS CLICK 2: " + String.valueOf(service_provider_id));
        stock_uid = "";

        total_vouchers_to_print = 1;
        spi_id = service_provider_id;

        dialog_multi = new Dialog(mContext);
        dialog_multi.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_multi.setContentView(R.layout.dialog_multi_voucher);
        dialog_multi.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(FLAG_FULLSCREEN);
        lp.copyFrom(dialog_multi.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));

//        pop_content = ((TextView) dialog_multi.findViewById(R.id.pop_content));

//           pop_title.setText("Requesting");
//        pop_content.setText("please wait...");

        //bt_process.setText("Process x" + String.valueOf(total_vouchers_to_print));
        ImageView img_product1 = dialog_multi.findViewById(R.id.img_product1);
        TextView txt_value = dialog_multi.findViewById(R.id.txt_value);
        txt_value.setVisibility(VISIBLE);
        txt_value.setText("R " + deno);
        img_product1.setVisibility(VISIBLE);

        textView2 = dialog_multi.findViewById(R.id.textView2);
        textView3 = dialog_multi.findViewById(R.id.textView3);
        textView4 = dialog_multi.findViewById(R.id.textView4);
        textView5 = dialog_multi.findViewById(R.id.textView5);
        textView6 = dialog_multi.findViewById(R.id.textView6);
        textView7 = dialog_multi.findViewById(R.id.textView7);
        textView8 = dialog_multi.findViewById(R.id.textView8);
        textView9 = dialog_multi.findViewById(R.id.textView9);
        pop_title2 = dialog_multi.findViewById(R.id.pop_title);       // REMOVE "TextView" TEXTVIEW TO STOP ERROR
        img_product1.setImageResource(drawable);


        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);
        //if (settings.getString("setting_print_to_screen","0").equals("1")) {

        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        textView5.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        textView7.setVisibility(View.GONE);
        textView8.setVisibility(View.GONE);
        textView9.setVisibility(View.GONE);


        txt_last_header = dialog_multi.findViewById(R.id.txt_last_header);
        txt_last_time = dialog_multi.findViewById(R.id.txt_last_time);
        txt_last_voucher_info = dialog_multi.findViewById(R.id.txt_last_voucher_info);

        final AppCompatButton bt_close = dialog_multi.findViewById(R.id.bt_close);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullscreenCall();
                dialog_multi.dismiss();
            }
        });

        if (!batteryAlert()) {
            //dialog_multi.dismiss();

            return;
        }

        bt_reprint = dialog_multi.findViewById(R.id.bt_reprint);
        bt_reprint.setVisibility(View.GONE);
        bt_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomDialog("Reprinting", "Please wait...", false);

                doReprintLogic(stock_uid);

                dialog_multi.dismiss();

            }
        });


        bt_process = dialog_multi.findViewById(R.id.bt_process);
        //     bt_process.setText("Process x" + String.valueOf(total_vouchers_to_print));
        bt_process.setText("Ok");
        pop_title2.setText(Html.fromHtml("Due to Security reasons, \nwe are unable to refund vouchers"));
        //   pop_title2.setText("Select Quantity (x" + String.valueOf(total_vouchers_to_print) + ")");
        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomDialog("Requesting", "Please wait...", false);
                dialog_multi.dismiss();

                get_voucher();


            }
        });


        dialog_multi.show();
        dialog_multi.getWindow().setAttributes(lp);

        bt_process.setEnabled(false);
        //showCustomDialog("Fetching Last Voucher", "Please wait...", false);

        get_last_voucher_info();

    }

    public void click_spi_multi(View view) {

        int total_vouchers = 1;

        if (view.getTag().equals("2")) {
            textView2.setBackground(getDrawable(R.drawable.view_bg_red));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 2;
        }
        if (view.getTag().equals("3")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.view_bg_red));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 3;
        }
        if (view.getTag().equals("4")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.view_bg_red));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 4;
        }
        if (view.getTag().equals("5")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.view_bg_red));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 5;
        }
        if (view.getTag().equals("6")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.view_bg_red));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 6;
        }
        if (view.getTag().equals("7")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.view_bg_red));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 7;
        }
        if (view.getTag().equals("8")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.view_bg_red));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            total_vouchers = 8;
        }
        if (view.getTag().equals("9")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.view_bg_red));

            total_vouchers = 9;
        }

        total_vouchers_to_print = total_vouchers;

        pop_title2.setText("Select Quantity (x" + total_vouchers_to_print + ")");
        bt_process.setText("Process x" + total_vouchers_to_print);

    }

    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);
            if (printerQ1Sts == 1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(VISIBLE);
            } else {
                bt_close.setVisibility(VISIBLE);
                btn_paper_load.setVisibility(View.GONE);
            }
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
                Topitup.getInstance().printerInit();
                dialog.dismiss();
                get_voucher();
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
        FullscreenCall();
    }

    private void showCustomDialogmposprint(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);
            if (printerQ1Sts == 1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(VISIBLE);
            } else {
                bt_close.setVisibility(VISIBLE);
                btn_paper_load.setVisibility(View.GONE);
            }
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
                Topitup.getInstance().printerInit();
                dialog.dismiss();
                // get_voucher();
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
        FullscreenCall();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        if (ll_back.getVisibility() == VISIBLE) {
            Intent myIntent = new Intent(mContext, activity_main.class);
            myIntent.putExtra("service_provider_id", 0);
            startActivity(myIntent);
        } else {
            Intent myIntent2 = new Intent(mContext, activity_login.class);
            startActivity(myIntent2);
        }
    }

    public boolean checkSettings(int provider_id, int drawable_image_dis, int drawable_image) {
        try {

            if (provider_id == 39) {

                provider_id = 25;
            }

            service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", provider_id).findFirst();

            if (Topitup.IS_ADMIN.equals("1")) {
                if (!service_provider_setting.isVisible_admin()) {
                    showActivateDialog(drawable_image_dis, provider_id, drawable_image, "fromLocal", "");
                    return false;
                }

            } else {
                if (!service_provider_setting.isVisible_admin()) {
                    showActivateDialog(drawable_image_dis, provider_id, drawable_image, "fromLocal", "");
                    return false;
                }
                if (!service_provider_setting.isVisible_cashier()) {
                    showActivateDialog(drawable_image_dis, provider_id, drawable_image, "fromLocal", "");
                    return false;
                }

            }
            return true;
        } catch (Exception ex) {
            Toasty.error(mContext, "Update Customised Product (3.2) under User Management", 5000, true).show();
            //
        }

        return false;
    }

    /***************realtime**********
     *
     *
     */

    private void showCustomDialogmpos() {

        //  pop_title.setText(sTitle);
        //  pop_content.setText(sContent);


        bt_closep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("setting_pockepos_open", "0");
                //editor.putString("setting_dnp", "1");
                editor.commit();
                dialogp.dismiss();
                dialog.dismiss();
                stopRunning = false;
               /* try {
                    if(isRunning)
                    cntdwnTimer.cancel();
                } catch (Exception r) {
                    r.printStackTrace();
                }*/

            }
        });
        btn_customer_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterTopitup.print_data(cslip);
                try {
                    if (isRunning)
                        cntdwnTimer.cancel();
                } catch (Exception r) {
                    r.printStackTrace();
                }
                dialogp.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 5000L);
            }
        });
        bt_merchant_customer_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (isRunning)
                        cntdwnTimer.cancel();
                } catch (Exception r) {
                    r.printStackTrace();
                }
                dialogp.dismiss();
                printslip();

            }


        });
        btn_merchant_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterTopitup.print_data(mslip);
                try {
                    if (isRunning)
                        cntdwnTimer.cancel();
                } catch (Exception r) {
                    r.printStackTrace();
                }
                dialogp.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                }, 5000L);
            }
        });
        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cntdwnTimer.cancel();
                } catch (Exception r) {
                    r.printStackTrace();
                }
                dialogp.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                }, 5000L);
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogp.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogp.show();
        dialogp.getWindow().setAttributes(lp);

    }

    private void printslip() {
        try {
            if (isRunning)
                cntdwnTimer.cancel();
        } catch (Exception r) {
            r.printStackTrace();
        }
        PrinterTopitup.print_data(cslip);

        showCustomDialog("Printing", "Printing Merchant Copy!!!", false);
        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PrinterTopitup.print_data(mslip);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

            }
        }, 8000L);
    }

    public void checkPrintMPOSslip(boolean show) {

        //Toasty.error(mContext, "stopRunning="+stopRunning, 12000, true).show();

        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts = 2;
                showCustomDialogmposprint("Printer Issue", "Please try again. After Some time", true);
                return;
            }

            if (!PrinterTopitup.check_paper(mContext)) {
                printerQ1Sts = 1;
                //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                showCustomDialogmposprint("Out of Paper", "Please check paper and try again.", true);
                return;
            }
        }


        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        endDate = dateformat.format(c.getTime());


        final Call<ResponseBody> call;

        call = apiService.print_real_time_slip(Topitup.TIU_LICENSE, Topitup.CUSTOMER_ID, endDate, "60");

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {
                    res = response.body().string();


                } catch (Exception ex) {

                    if (response.body() != null) {
                        response.body().close();
                    }
                }

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res;
                    if (res.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    //stopCustomDialog("Problem",matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK


                    try {

                        JSONObject reader = new JSONObject(res);
                        String html = "";
                        String slip_to_print = "";
                        // Calendar c = Calendar.getInstance();
                        // SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                        // String datetime = dateformat.format(c.getTime());
                        // Convert Date to Calendar

                        //  Log.d("stDate",stDate);
                        if (show)
                            showCustomDialogmpos();
                        if (reader.getString("pid").equals("0")) {
                            if (!stopRunning) {

                                try {
                                    if (isRunning)
                                        cntdwnTimer.cancel();
                                } catch (Exception r) {
                                    r.printStackTrace();
                                }
                                String lastUID = reader.getString("lastUID");
                                String lastAmnt = reader.getString("lastAmnt");
                                String lastTxdt = reader.getString("lastTxdt");
                                // String ckey=reader.getString("ckey");
                                // String appID=reader.getString("appID");
                                last_txn_pos.setText("Last Transaction\n" + lastTxdt + "        " + lastAmnt + "\nUID:" + lastUID);
                                lntxwait.setVisibility(VISIBLE);
                                wb_slip.setVisibility(View.GONE);
                                bt_merchant_customer_copy.setEnabled(false);
                                btn_customer_copy.setEnabled(false);
                                btn_merchant_copy.setEnabled(false);
                                txtload.setVisibility(VISIBLE);
                                mposprogress.setVisibility(VISIBLE);
                                cntdwnTimer = new CountDownTimer(60000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        isRunning = true;
                                        NumberFormat f = new DecimalFormat("00");

                                        long hour = (millisUntilFinished / 3600000) % 24;

                                        long min = (millisUntilFinished / 60000) % 60;

                                        long sec = (millisUntilFinished / 1000) % 60;

//txtload.setText("   Please Wait....Fetching Receipt in \n                        "+f.format(hour) + ":" + f.format(min) + ":" + f.format(sec)+" secs");
                                        if (Topitup.checkConnection(getApplicationContext())) {
                                            txtload.setText("   Please Wait....Fetching\n       Receipt in " + f.format(sec) + " secs");
                                            // queryWAP(ckey,appID);


                                            if (sec % 6 == 0)
                                                getPrintMPOSslip(sec);
                                        } else {
                                            txtload.setText("  Check Your Internet\nconnection retrying to\nconnect..... " + f.format(sec) + " secs");
                                        }
                                        // txtload.setText("Internet Data Issue, please contact Top it Up.");
                                        // counter++;

                                    }

                                    public void onFinish() {
                                        isRunning = false;
                                        txtload.setText("Unable to Fetch Receipt\nCheck in Reprint Section");

                                        resetMPOSDia();

                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogp != null && dialogp.isShowing()) {
                                                    Activity activity = getActivityFromContext(dialogp.getContext());
                                                    if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                                        dialogp.dismiss();
                                                    }
                                                }
                                            }
                                        }, 6000L);

                                    }
                                }.start();
                                dialog.dismiss();

                            }

                        } else {


                            stopRunning = true;


                            //  Toasty.error(mContext, "else loop first", 8000, true).show();


                            get_balance();

                            lntxwait.setVisibility(View.GONE);
                            txtload.setVisibility(View.GONE);
                            mposprogress.setVisibility(View.GONE);
                            wb_slip.setVisibility(VISIBLE);
                            bt_merchant_customer_copy.setEnabled(true);
                            btn_customer_copy.setEnabled(true);
                            btn_merchant_copy.setEnabled(true);
                            resetMPOSDia();
                            cslip = reader.getString("print_data");
                            mslip = reader.getString("print_data1");
                            ringtone();
                            update_balance();

                            if (setting_chk_auto_mpos.equals("1")) {
                                //Toasty.error(mContext, "Auto print enabled", 8000, true).show();

                                wb_slip.loadDataWithBaseURL("", "Busy Printing Receipt!!!", "text/html", "UTF-8", "");


                                //   if (settings.getString("setting_dnp", "0").equals("0")){

                                if (settings.getInt("MPOSTYPE", 0) == 1)
                                    PrinterTopitup.print_data(cslip);
                                else if (settings.getInt("MPOSTYPE", 0) == 2)
                                    PrinterTopitup.print_data(mslip);
                                else
                                    printslip();
                                // }

                            }
                            try {
                                //    Toasty.error(mContext, "trying to read buffer", 8000, true).show();

                                BufferedReader bufReader = new BufferedReader(new StringReader(reader.getString("print_data")));
                                String line = null;
                                while ((line = bufReader.readLine()) != null) {

                                    String prnt_line = "";
                                    String size = "";

                                    if (line.length() > 1) {
                                        prnt_line = line.substring(1);
                                        size = "" + line.charAt(0);
                                    }

                                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                        if (Topitup.PRINT_BARCODE.equals("1")) {
                                            if (line.contains("BARCODE")) {

                                                html += line.replace("BARCODE:", "");
                                            }
                                        }

                                    } else {


                                        if (size.equals("1")) {

                                            html += prnt_line + "<br/>";

                                        } else {

                                            html += "<span style=\"font-size:1.4em\">";
                                            html += prnt_line + "</span><br/>";

                                        }

                                    }


                                }


                                wb_slip.getSettings().setJavaScriptEnabled(false);
                                wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

                                //showCustomDialog("Printing","Please wait....",false);


                                try {
                                    if (isRunning)
                                        cntdwnTimer.cancel();
                                } catch (Exception r) {
                                    r.printStackTrace();
                                }
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                }, 8000L);
                            } catch (Exception ex) {
                                //
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //dialog.dismiss();
                    //Printer.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stopCustomDialog("Problem", t.getMessage());
                Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }


        });


    }

    private void queryWAP(String ckey, String appID) {
        final Call<ResponseBody> call;

        call = apiService.get_txns_appid(Topitup.TIU_LICENSE, stDate, endDate, ckey, appID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {
                    res = response.body().string();


                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                // stopCustomDialog("Problem",t.getMessage());
//                    Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }
    private Activity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public void get_balance() {

        final Call<fin_balance> call = apiService.get_balance_new(Topitup.TIU_LICENSE, "1");

        call.enqueue(new Callback<fin_balance>() {

            @Override
            public void onResponse(Call<fin_balance> call, Response<fin_balance> response) {


                try {
                    if (response.code() == 500) {
                        Toasty.error(mContext, "Unable to connect TIU servers", 8000, true).show();

                        return;
                    }

                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }
                    fin_balance res = response.body();
                    new_spi_ver = res.new_spi_ver;

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(res);
                    realm.commitTransaction();

                    final String setting_print_barcode = settings.getString("setting_print_barcode", "0");
                    if (setting_print_barcode.equals("0"))//barcode is not enabled
                    {
                        // Toasty.error(mContext,new_spi_ver, 4000, true).show();
                        checkspver();
                    }
                } catch (Exception ex) {

                   /* if (response != null)
                        response.raw().close();*/


                }


            }

            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {


            }

        });


    }

    public void getPrintMPOSslip(Long Secs) {
        final Call<ResponseBody> call;
        call = apiService.print_real_time_slip(Topitup.TIU_LICENSE, Topitup.CUSTOMER_ID, endDate, Secs.toString());

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 500) {
                    txtload.setText("Unable to connect TIU servers");
                    //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    txtload.setText("Internet Data Issue, please contact Top it Up.");
                    //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {
                    res = response.body().string();


                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                }

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res;
                    if (res.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    //stopCustomDialog("Problem",matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    //  printingCustomDialog("Printing","Busy printing...");

                    try {
                        JSONObject reader = new JSONObject(res);
                        String html = "";


                        if (reader.getString("pid").equals("0")) {
                        } else {
                            stopRunning = true;
                            get_balance();
                            try {
                                if (isRunning)
                                    cntdwnTimer.cancel();
                            } catch (Exception r) {
                                r.printStackTrace();
                            }
                            lntxwait.setVisibility(View.GONE);
                            txtload.setVisibility(View.GONE);
                            mposprogress.setVisibility(View.GONE);
                            wb_slip.setVisibility(VISIBLE);
                            bt_merchant_customer_copy.setEnabled(true);
                            btn_customer_copy.setEnabled(true);
                            btn_merchant_copy.setEnabled(true);
                            resetMPOSDia();
                            cslip = reader.getString("print_data");
                            mslip = reader.getString("print_data1");
                            ringtone();
                            update_balance();


                            try {

                                BufferedReader bufReader = new BufferedReader(new StringReader(reader.getString("print_data")));
                                String line = null;
                                while ((line = bufReader.readLine()) != null) {

                                    String prnt_line = "";
                                    String size = "";

                                    if (line.length() > 1) {
                                        prnt_line = line.substring(1);
                                        size = "" + line.charAt(0);
                                    }

                                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                        if (Topitup.PRINT_BARCODE.equals("1")) {
                                            if (line.contains("BARCODE")) {

                                                html += line.replace("BARCODE:", "");
                                            }
                                        }

                                    } else {


                                        if (size.equals("1")) {

                                            html += prnt_line + "<br/>";

                                        } else {

                                            html += "<span style=\"font-size:1.4em\">";
                                            html += prnt_line + "</span><br/>";

                                        }

                                    }


                                }


                                wb_slip.getSettings().setJavaScriptEnabled(false);
                                wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

                                //showCustomDialog("Printing","Please wait....",false);
                                if (setting_chk_auto_mpos.equals("1")) {
                                    // Toasty.error(mContext,"Auto print enabled", 8000, true).show();

                                    wb_slip.loadDataWithBaseURL("", "Busy Printing Receipt!!!", "text/html", "UTF-8", "");


                                    //   if (settings.getString("setting_dnp", "0").equals("0")){

                                    if (settings.getInt("MPOSTYPE", 0) == 1)
                                        PrinterTopitup.print_data(cslip);
                                    else if (settings.getInt("MPOSTYPE", 0) == 2)
                                        PrinterTopitup.print_data(mslip);
                                    else
                                        printslip();
                                    // }

                                }


                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                }, 8000L);
                            } catch (Exception ex) {
                                //
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                // stopCustomDialog("Problem",t.getMessage());
//                    Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    Runnable print_handler_runnable = new Runnable() {
        @Override
        public void run() {
            get_voucher();
        }
    };

    private void resetMPOSDia() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("setting_pockepos_open", "0");
        editor.commit();
        try {
            if (isRunning)
                cntdwnTimer.cancel();
        } catch (Exception r) {
            r.printStackTrace();
        }

    }

    protected void checkspver() {
        Log.i("spiver", spiver);
        Log.i("new_spi_ver", new_spi_ver);

        if (spiver.equals(new_spi_ver)) {


        } else {
            showCustomDialog("Catalogue Update", "Please Wait New Catalog Updating!!!", false);


            get_update_all();
        }
    }

    private void get_update_all() {
        //Timber.i("result-manoj="+"called");


//        RealmResults<GetUpdateAll> result2 = realm.where(GetUpdateAll.class)
//                .equalTo("customer_id", Topitup.CUSTOMER_ID)
//                .findFirst();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();


        if (tiu_settings == null) {

            spiver = "0";
        } else {

            spiver = tiu_settings.spi_ver;
        }
        Call<GetUpdateAll> call = apiService.get_update_all(Topitup.TIU_LICENSE, spiver, "", Topitup.APP_VERSION);
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    //Toasty.error(mContext, "Could not log in", 3000, true).show();
                    try {


                        //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                        if (!response.headers().get("Server").equals("TIU")) {
                            throw new UserException("Please check your internet connection!");
                        }

                        if (response.body().toString().toLowerCase().contains("customer_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                            //

                        } else {

                            GetUpdateAll result = response.body();

                            realm.beginTransaction();

                            String service_provider_data = "";

                            byte[] bytes = android.util.Base64.decode(result.published_data, android.util.Base64.DEFAULT);
                            service_provider_data = new String(bytes);


                            result.service_provider_data = service_provider_data;

                            customer_id = result.customer_id;

                            //Toasty.error(mContext,"enable_rpt_comm_statement="+result.enable_rpt_comm_statement, 12000, true).show();

                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();

                            update_spi(service_provider_data);
                            // activity_product_settings product_settings=new activity_product_settings();
                            //  product_settings.updateSPIsettings();


                        }

                    } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                    }
                } else {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                }


            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {

                Toasty.error(mContext, t.getMessage(), 100000, true).show();
                Timber.e("GetUpdateAll error: " + t.getMessage());


            }
        });
        showCustomDialog("Catalogue Update", "Please Wait New Catalog Updating!!!", false);
        activity_product_settings product_settings = new activity_product_settings();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                product_settings.updateSPIsettings();
                dialog.dismiss();
            }
        }, 5000L);


    }

    private void update_spi(String service_provider_data) {


        realm.beginTransaction();
        realm.where(service_provider.class).findAll().deleteAllFromRealm();
        realm.where(service_provider_item.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();


        BufferedReader bufReader = new BufferedReader(new StringReader(service_provider_data));

        try {

            String line = null;
            Integer last_sp_id = 0;

            while ((line = bufReader.readLine()) != null) {


                // Timber.i(line);

                //System.out.println(line);

                String[] myData = line.split("\\^");
                //for (String s: myData) {

                //if (s.length() > 0) {

                if (myData[0].equals("p")) {

                    service_provider new_sp = new service_provider();
                    new_sp.provider_id = Integer.parseInt(myData[1]);

                    last_sp_id = Integer.parseInt(myData[1]);

                    // Timber.i("SPI : " + line );

                    if (myData.length > 2) new_sp.provider_desc = myData[2];
                    if (myData.length > 3) new_sp.provider_message = myData[3];

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_sp);
                    realm.commitTransaction();

                } else {

                    service_provider_item new_spi = new service_provider_item();
                    new_spi.service_provider_item_id = Integer.parseInt(myData[0]);
                    new_spi.service_provider_id = last_sp_id;

//                        Timber.i("SPI : SPI -  " + myData[0] );
//                        if (last_sp_id == 19) {
//                            Timber.i("SPI :  " + String.valueOf(new_spi.service_provider_item_id));
//                        }
//

                    boolean item_show_value = Integer.parseInt(myData[7]) == 1;

                    new_spi.item_desc = myData[1];
                    new_spi.item_btn_desc = myData[2];
                    //new_spi.item_print_desc = myData[3];
                    new_spi.item_print_desc = myData[4];
                    new_spi.item_value = myData[5];
                    new_spi.item_type = Integer.parseInt(myData[6]);
                      /*  if(Integer.parseInt(myData[6])==1)
                        Log.i("NAMEMANOJ="+myData[4], myData[5]+"size of the data....splash......"+Integer.parseInt(myData[6]));*/
                    new_spi.item_show_value = item_show_value;
                    new_spi.item_position = Integer.parseInt(myData[8]);
                    // new_spi.item_barcode = myData[9];
                  /*  new_spi.item_barcode = myData[9];
                    new_spi.period_label = myData[10];
                    new_spi.social_type = myData[11];*/
                    new_spi.item_barcode = myData[9];
                    new_spi.social_type = myData[10];
                    new_spi.period_label = myData[11];
                 /*   if (myData.length == 10) {
                        new_spi.item_barcode = myData[9];
                    } else {
                        if (myData.length == 12) {
                            new_spi.item_barcode = myData[9];
                            new_spi.period_label = myData[10];
                            new_spi.social_type = myData[11];
                        }

                    }*/
                   /* Log.e("TestInfo Service", myData[1] + "," + myData[2] + "," +
                            myData[3] + "," + myData[4] + "," +
                            myData[5] + "," + myData[6] + "," + myData[7] + "," + myData[8] + "," + myData[9] + "," + myData[10] + "," + myData[11]
                    );*/
                    new_spi.item_value_int = Double.parseDouble(myData[5]);

                    // Timber.i("SPI :  " + String.valueOf( Integer.parseInt(myData[5])));

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_spi);
                    realm.commitTransaction();

                }
                //  System.out.println(s);

                // }

                // }


            }

        } catch (Exception ex) {

            Timber.i("SPI Error: " + ex.getMessage());

        }


/**
 *
 * Service provider items
 *
 */
    /*
        service_provider_items = realm.where(service_provider_item.class).findAll();

        size = service_provider_items.size();

        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("enable", 1).findAll();
        realm.beginTransaction();
        for (int i = 0; i < service_provider_item_settings.size(); i++) {
            service_provider_item_settings.get(i).setEnable(0);
        }

        realm.commitTransaction();
        for (int k = 0;k<size;k++) {
            service_provider_item sp_item = service_provider_items.get(k);
            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", sp_item.service_provider_item_id).findFirst();
            realm.beginTransaction();
            if(service_provider_item_setting==null) {
                //Toasty.info(mContext, "if loop ", 8000, true).show();
                service_provider_item_settings sp_settings=new service_provider_item_settings();
                sp_settings.setService_provider_id(sp_item.service_provider_id);
                sp_settings.setService_provider_item_id(sp_item.service_provider_item_id);
                sp_settings.setItem_desc(sp_item.item_desc);
                sp_settings.setItem_position(sp_item.item_position);
                sp_settings.setVisible_admin(true);
                sp_settings.setVisible_cashier(true);
                realm.insertOrUpdate(sp_settings);

            }else {
                service_provider_item_setting.setEnable(1);
                service_provider_item_setting.setItem_desc(sp_item.item_desc);
            }
            realm.commitTransaction();
        }

*/

//        RealmResults<service_provider_item> service_provider_items;
//        service_provider_items = realm.where(service_provider_item.class)
//                .equalTo("service_provider_id", 19)
//                .findAll();

        //Timber.i("TOTAL STUFF : " + String.valueOf(service_provider_items.size()));


    }

    public void get_swipe_realtime() {
        String versionName = "1.0";
        int versionCode = 1;
        if (Topitup.DEVICE_TYPE.equals("WPOS")) {
            if (enable_realtime_swipe.equals("1") || enable_realtime_swipe.equals("2") || enable_realtime_swipe.equals("3")) {
                try {
                    versionName = getPackageManager().getPackageInfo("com.wiseasy.cashier", 0).versionName;
                    versionCode = getPackageManager().getPackageInfo("com.wiseasy.cashier", 0).versionCode;
                } catch (PackageManager.NameNotFoundException e) {

                    e.printStackTrace();
                }
            }
        }
        final Call<ResponseBody> call = apiService.get_swipe_sts(Topitup.TIU_LICENSE, versionName, versionCode);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    // stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
                    return;
                }

                String res_realtime = "";
                try {
                    res_realtime = response.body().string();
                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                }

                if (res_realtime.trim().length() == 0 || res_realtime.contains("<error><err>") || res_realtime.contains("ERR:") || res_realtime.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res_realtime;
                    if (res_realtime.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    // stopCustomDialog("Problem",matcher);
                    Toasty.error(mContext, "API Error" + matcher, 8000, true).show();

                } else {        //Response OK


                    try {
                        JSONObject reader = new JSONObject(res_realtime);
                        enable_realtime_swipe = reader.getString("realtime_settle");

                        if (!reader.isNull("addpayVer")) {
                            addpayVer = reader.getString("addpayVer");
                        } else {

                            addpayVer = "1.0.4.5-addpay-2122060288";
                        }
                        if (!reader.isNull("swipe_sale_logout")) {
                            swipe_sale_logout = reader.getString("swipe_sale_logout");
                        } else {
                            swipe_sale_logout = "1";
                        }

                        double min_swipe = reader.getDouble("min_swipe");
                        double max_swipe = reader.getDouble("max_swipe");
                        double warning_swipe = reader.getDouble("warning_swipe");
                        int retailer_type = reader.getInt("retailer_type");

                        //Toasty.error(mContext, "API Error"+min_swipe, 8000, true).show();
                        double blue_max_value;
                        if (!reader.isNull("blulbl_max_sales_value")) {
                            blue_max_value = reader.getDouble("blulbl_max_sales_value");
                        } else {
                            blue_max_value = 1000;
                        }

                        double blue_warning_value;
                        if (!reader.isNull("blulbl_warning_sale_value")) {
                            blue_warning_value = reader.getDouble("blulbl_warning_sale_value");
                        } else {
                            blue_warning_value = 500;
                        }


                        double one_max_value;
                        if (!reader.isNull("oneforyou_max_sales_value")) {
                            one_max_value = reader.getDouble("oneforyou_max_sales_value");
                        } else {
                            one_max_value = 800;
                        }

                        double one_warning_value;
                        if (!reader.isNull("oneforyou_warning_sale_value")) {
                            one_warning_value = reader.getDouble("oneforyou_warning_sale_value");
                        } else {
                            one_warning_value = 400;
                        }
                        String cash_up = "cashup";
                        if (!reader.isNull("cashup")) {
                            cash_up = reader.getString("cashup");
                        } else {
                            cash_up = "cashup";
                        }
                        Topitup.ONE_MAX_THRESHOLD = one_max_value;
                        Topitup.ONE_WARNING_THRESHOLD = one_warning_value;
                        Topitup.BLUE_MAX_THRESHOLD = blue_max_value;
                        Topitup.BLUE_WARNING_THRESHOLD = blue_warning_value;
                        Topitup.min_swipe = min_swipe;
                        Topitup.max_swipe = max_swipe;
                        Topitup.warning_swipe = warning_swipe;
                        Topitup.RETAILER_TYPE = retailer_type;
                        Topitup.cashUp = cash_up;
                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();


                        if (swipe_sale_logout.equals("1")) {
                            editor.putString("setting_slip_logout", "1");
                        }
                        editor.putString("swipe_sale_logout_flag", swipe_sale_logout);
                        editor.putString("enable_realtime_swipe", enable_realtime_swipe);
                        if (realm.isInTransaction()) {
                            // A transaction is already in progress
                            Log.d("RealmTransaction", "Transaction is already in progress");
                        } else {
                            // No transaction is currently in progress, you can begin a new one

                            realm.beginTransaction();
                        }
                        enable_bluvoucher = reader.getString("enable_bluvoucher");
                        enable_cashms = reader.getString("enable_cashms");
                        enable_easyairtime = reader.getString("enable_easyairtime");
                        enable_international = reader.getString("enable_international");
                        enable_mamamoney = reader.getString("enable_mamamoney");

                        enable_oneforu = reader.getString("enable_oneforu");
                        enable_flexipin = reader.getString("enable_flexepin");
                        enable_ott = reader.getString("enable_ott");
                        enable_ringas = reader.getString("enable_ringas");
                        enable_unipin = reader.getString("enable_unipin");
                        enable_airtime = reader.getString("enable_airtime");
                        enable_ele = reader.getString("enable_elec");
                        enable_bill = reader.getString("enable_bill");
//                        st_status = reader.getString("st_status");
                        st_status= reader.optString("st_status", "0"); // default value

                        editor.putString("enable_airtime", enable_airtime);
                        editor.putString("enable_oneforu", enable_oneforu);
                        editor.putString("enable_blue", enable_bluvoucher);
                        editor.putString("enable_ott", enable_ott);
                        editor.putString("enable_flexepin", enable_flexipin);
                        editor.putString("st_status", st_status);

                        Log.e("st_status","main screen"+st_status);


                        GetUpdateAll gua = new GetUpdateAll();

                        gua.enable_bluvoucher = enable_bluvoucher;
                        gua.enable_cashms = enable_cashms;
                        gua.enable_easyairtime = enable_easyairtime;
                        gua.enable_international = enable_international;
                        gua.enable_airtime = enable_airtime;
                        gua.enable_mamamoney = enable_mamamoney;
                        gua.enable_oneforu = enable_oneforu;
                        gua.enable_ott = enable_ott;
                        gua.enable_flexipin = enable_flexipin;
                        gua.enable_ringas = enable_ringas;
                        gua.enable_unipin = enable_unipin;
                        gua.st_status = st_status;

                        realm.copyToRealmOrUpdate(gua);
                        realm.commitTransaction();
                        editor.commit();
                    } catch (JSONException e) {
                        Topitup.min_swipe = 1.00;
                        Topitup.max_swipe = 100000.00;
                        Topitup.warning_swipe = 5000.00;
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    // stopCustomDialog("No Internet","Please check that your internet connection is working.");
                } else {
                    //   stopCustomDialog("Problem",t.getMessage());
                }

            }

        });


    }

    public void crashMe() {
        throw new NullPointerException();
    }

    private void callproducts() {
        if (service_provider_id == 1006) {
//            NAME = new String[]{"virginmobile", "lyca", "1o1mobile", "talk360", "siyavla", "ikeja"};
            NAME = new String[]{"easyloadair"};

        } else if (service_provider_id == 1002) {

            NAME = new String[]{"telkom"};
        } else if (service_provider_id == 1003) {

            NAME = new String[]{"ringas", "easyload"};
        } else if (service_provider_id == 1004) {
            //NAME = new String[]{"coca"};
            NAME = new String[]{"coca", "nandhni", "Clover"};
        } else {
            // NAME = new String[]{"cellc", "mtn", "vodacom", "telkom", "airvoip", "topitupeasi", "electricity", "water", "unipin", "oneforyou", "busticket", "ott", "paybills", "DSTV", "moneytransfer", "globalairtime", "cashmx", "cardpay", "worldcall", "netflix", "uber", "spotify", "rica", "ikeja", "talk360", "1o1mobile"};

            NAME = new String[]{"cellc", "mtn", "vodacom", "telkom", "airvoip", "topitupeasi", "electricity", "water", "unipin", "oneforyou", "busticket", "ott", "paybills", "DSTV", "moneytransfer", "globalairtime", "cashmx", "cardpay", "worldcall", "netflix", "uber", "spotify", "rica", "ikeja", "talk360", "flexepin"};

           /* NAME = new String[]{"cellc", "mtn", "vodacom", "Telkom", "airvoip", "easyload",
                    "electricity", "water", "unipin", "oneforyou", "busticket", "ott", "paybills", "DSTV", "moneytransfer"
                    , "globalairtime", "cashmx", "cardpay", "netflix", "uber", "spotify", "rica", "ikeja", "talk360", "1o1mobile"};//,"mamamoney,*/
            SPList = new ArrayList<>((Arrays.asList(NAME)));
            if (enable_flexipin.equals("0")) {
                SPList.remove("flexepin");

            }
            if (enable_airtime.equals("0")) {

                /*int pos = SPList.indexOf("cellc");
                SPList.remove(pos);

                pos = SPList.indexOf("mtn");
                SPList.remove(pos);

                pos = SPList.indexOf("vodacom");
                SPList.remove(pos);

                pos = SPList.indexOf("Telkom");
                SPList.remove(pos);

                pos = SPList.indexOf("airvoip");
                SPList.remove(pos);*/

            }
            if (enable_bill.equals("0")) {
              /*  int pos = SPList.indexOf("paybills");
                SPList.remove(pos);

                pos = SPList.indexOf("DSTV");
                SPList.remove(pos);

                pos = SPList.indexOf("moneytransfer");
                SPList.remove(pos);*/
            }
            if (enable_ele.equals("0")) {
              /*  int pos = SPList.indexOf("electricity");
                SPList.remove(pos);
                pos = SPList.indexOf("water");
                SPList.remove(pos);
*/
            }
            if (enable_ott.equals("0")) {
              /*  int pos = SPList.indexOf("ott");
                SPList.remove(pos);*/

            }

            if (enable_international.equals("0")) {
               /* int pos = SPList.indexOf("netflix");
                SPList.remove(pos);
                pos = SPList.indexOf("uber");
                SPList.remove(pos);
                pos = SPList.indexOf("spotify");
                SPList.remove(pos);*/

            }


            if (enable_oneforu.equals("0")) {
               /* int pos = SPList.indexOf("oneforyou");
                SPList.remove(pos);*/

            }


        /*    if (enable_unipin.equals("0")) {
                int pos = SPList.indexOf("unipin");
                SPList.remove(pos);
            }
            if (enable_easyairtime.equals("0")) {
                int pos = SPList.indexOf("topitupeasi");
                SPList.remove(pos);
            }
            if (enable_cashms.equals("0")) {
                int pos = SPList.indexOf("cashmx");
                SPList.remove(pos);
            }*/
            if (Topitup.RICA_REG.equals("0")) {
                SPList.remove("rica");
            }
            NAME = SPList.toArray(new String[SPList.size()]);
        }

        NAMEhor = new String[]{"mtn", "vodacom", "mtn", "vodacom", "Telkom", "airvoip", "Telkom", "airvoip"};//,"mamamoney,
//        tabs();
        tabs();

       /* new Thread() {
            @Override
            public void run() {
                //If there are stories, add them to the table
                try {
                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tabs();
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                    ex.printStackTrace();
                }
            }
        }.start();*/
//        new BackgroundTask().execute();
       /* Thread thread = new Thread(new Runnable() {
            @Override public void run() {
                // code to run in background thread
                tabs();

            }
        });
        thread.start();*/
    }

    @Override
    public void onItemClickHorizantal(Item item) {
//        timer.cancel();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            // Perform background task
            try {
                tabs();

//                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Task Completed";
        }

        @Override
        protected void onPostExecute(String result) {
            // Update UI with the results
//            resultTextView.setText(result);
        }
    }


}