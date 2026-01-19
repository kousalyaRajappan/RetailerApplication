package com.za.toptitup.loginlibrary;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.ACTION_BATTERY_CHANGED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.za.toptitup.loginlibrary.admin.activity_activation;
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MessageService;
import com.za.toptitup.loginlibrary.model.MessageServiceNotice;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.SupplierData;
import com.za.toptitup.loginlibrary.model.SupplierResponse;
import com.za.toptitup.loginlibrary.model.WholesaleResponse;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_user_current;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.utils.BatteryReceiver;
import com.za.toptitup.loginlibrary.utils.MyExceptionHandler;
//import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UserException;
import com.za.toptitup.loginlibrary.utils.WordsConert;


//SwipeListener
public class activity_login extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static final int USER_PIN_MAX_CHAR = 4;

    //public static final String PREFS_NAME = "AssetForcePrefsFile";
    private static final int REQUEST_EXTERNALRESULT = 100;
    public static boolean stopAlarm = false;
    public static String fromScreen = "activity_login";
    public static activity_login instance;
    public static RelativeLayout rl_network, rl_server;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    // Animations
    private final OvershootInterpolator mAnimationSlideInterpolator = new OvershootInterpolator(1.0f);
    public String accessCode = "";
    public boolean wasScreenOn;

    protected Topitup app;
    Realm realm;
    Context mContext;
    String userEntered;
    boolean keyPadLockedFlag = false;
    TextView[] pinBoxArray;
    String spiver;
    TextView statusView;
    ImageView img_one, img_two, img_three, img_four, clearButton;
    TextView btn_show_more_contact;
    TextView txt_app_version;
    String lastSaleData, setting_pockepos_open, setting_screen_off, setting_chk_auto_mpos;
    String notice_data;
    boolean is_admin_mode = false;
    boolean cell_incorrect = true;
    //PubNub pubnub;
    boolean isRegistered = false;
    pos_users user;
    String noticeid;
    ImageView newImageView;
    private TextView txt_rand;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    private MoneyTextWatcherRand moneyTextWatcher;
    private MoneyTextWatcherCent moneyTextWatcherCent;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    Dialog dialogp;
    Button btn_merchant_copy, btn_customer_copy, btn_email, bt_merchant_customer_copy;
    Handler mHandler = new Handler();
    WebView wb_slip;
    TextView txtload, last_txn_pos;
    ProgressBar mposprogress;
    LinearLayout lntxwait;
    CountDownTimer cntdwnTimer;
    String stDate, endDate;
    SharedPreferences settings;
    boolean isRunning = false;
    boolean stopRunning = false;
    String enable_realtime_swipe = "0";
    String asset_serial = "NA";
    String merchant_no = "NA";
    String terminal_no = "NA";
    ImageView activity_login_admin, img_gif_local;
    WebView img_gif;
    View view21;
    LinearLayout ll_message, ll_banking_detail, ll_activation, ll_calculator;
    HorizontalScrollView horizantal_scroll;
    Runnable myRunnable;
    Handler handlerSlide;
    Boolean isSlide = false;
    String advertId = "";
    String get_adv_data = "";
    BatteryReceiver batteryReceiver;
    LinearLayout tiu_title_bar_new;
    RelativeLayout txt_invalid;
    ImageView img_close;
    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close, bt_closep;
    Dialog dialog;
    // Hide after some seconds
    Handler handler = new Handler();
    ImageView img_gif_;
    private Animation mAnimSlideIn;
    private Animation mAnimSlideOut;
    //private boolean mDeleteIsShowing = false;
    private boolean mFailedLogin = false;
    //private EditText mUserAccessCode;
    private RelativeLayout mUserAccessCode;
    private ProgressBar mLoginProgress;
    private TextView mOneButton;
    private TextView mTwoButton;
    private TextView mThreeButton;
    private TextView mFourButton;
    private TextView mFiveButton;
    private TextView mSixButton;
    private TextView mSevenButton;
    private TextView mEightButton;
    private TextView mNineButton;
    private String enteredPinQR;
    private AlertDialog loadingDialog;
    //private TextView mZeroButton;
    // private TextView mDeleteButton;
    private TextView mIsDemo, textView3, textLastSale, text_store_name;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            is_admin_mode = false;
            //      SharedPreferences prefs = getSharedPreferences("TIUPREF", MODE_PRIVATE);
            //  String  TIU_SERVER = prefs.getString("TIU_SERVER", "DEMO");
            if (Topitup.DEBUG) {

                mIsDemo.setText("DEMO");
            } else {
                mIsDemo.setVisibility(View.INVISIBLE);
            }


        }


    };
    //  WebView webView;
    private RadioGroup radioserver;
    private RadioButton radiolve;
    private RadioButton radiodemo;
    //NFCEmvHandler nfc;
    private String SERVER;
    private Handler refreshHandler, refreshHandlerscreensaver;
    private Runnable runnablescreensaver;
    private String cslip, mslip;
    private BroadcastReceiver mReceiver = null;

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    private static boolean isEditTextEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        instance = this;
        userEntered = "";

        Log.e("login", "oncreate login");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.ding_products.activity_pin_entry_view);
        setContentView(R.layout.activity_login_pad);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Keep the screen on and bright while this kiosk activity is running.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        app = (Topitup) this.getApplication();
        FullscreenCall();
        hideKeyboard(activity_login.this);

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
        }

        BaseActivity.fromVoucherSale = false;
        // add
        //nfc = NFCEmvHandler.getInstance(this);
        //nfc.addTransListener(mContext);


//        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);


        //  btn_show_more_contact = (TextView) findViewById(R.id.btn_show_more_contact);
        textView3 = findViewById(R.id.textView3);
        newImageView = findViewById(R.id.newImageView);
        ll_message = findViewById(R.id.ll_message);
        ll_banking_detail = findViewById(R.id.ll_banking_detail);
        ll_activation = findViewById(R.id.ll_activation);
        ll_calculator = findViewById(R.id.ll_calculator);
        textLastSale = findViewById(R.id.textLastSale);
        text_store_name = findViewById(R.id.text_store_name);
        activity_login_admin = findViewById(R.id.activity_login_admin);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        txt_invalid = findViewById(R.id.txt_invalid);
        img_close = findViewById(R.id.img_close);
        img_gif_ = findViewById(R.id.img_gif_);
        img_gif_local = findViewById(R.id.img_gif_local);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Log.e("mycontentReceiver", ".......login");

                Topitup.checkServiceRunning();

            }
        });

        stopAlarm = true;
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_invalid.setVisibility(View.GONE);
            }
        });
        fromScreen = "activity_login";
        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);

        ((Topitup) getApplication()).checkWifiSimInternet(this);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


////////**************for realtime diallog

        dialogp = new Dialog(this);
        dialogp.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogp.setContentView(R.layout.dialog_dark_realtime_mpos);
        dialogp.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        // initialize receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        btn_merchant_copy = dialogp.findViewById(R.id.bt_merchant_copy);
        btn_customer_copy = dialogp.findViewById(R.id.bt_customer_copy);
        bt_closep = dialogp.findViewById(R.id.bt_close);
        btn_email = dialogp.findViewById(R.id.bt_email);
        bt_merchant_customer_copy = dialogp.findViewById(R.id.bt_merchant_customer_copy);
        wb_slip = dialogp.findViewById(R.id.wb_realtime_slip);
        txtload = dialogp.findViewById(R.id.id_loading);
        mposprogress = dialogp.findViewById(R.id.id_pbar);
        lntxwait = dialogp.findViewById(R.id.lntxwait);
        last_txn_pos = dialogp.findViewById(R.id.id_last_txn_pos);

        /* TIU HEADER */
        settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        advertId = settings.getString("advert_id", "");

        get_adv_data = settings.getString("get_advs", "");

        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_last_sale = settings.getString("setting_last_sale", "0");


//        final TextView tiu_title_outlet = (TextView) findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
      /*  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(180, 0, 0, 0);
        tiu_title_balance.setLayoutParams(params);*/
//        tiu_title_balance.setLayoutParams();
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        img_gif = findViewById(R.id.img_gif);

        update_balance();
        /* END HEADER */

        configureViews();
        getBattery(activity_login.this);
      /*  if (AlarmReciver.fromAlarm) {
            AlarmReciver.fromAlarm = false;
            displayAddDialog();
        }*/


        txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        String mydate = dateFormat.format(buildDate);
        txt_app_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog("TopitUp", "AppVersion: " + Topitup.APP_VERSION + "\n" +
                        "AppBuildVersionCode: " + Topitup.APP_BUILD_VERSION_CODE + "\n" +
                        "BuildCode: " + mydate.replace("-", "") + "\n" +
                        "AppVersion Name:Hypernova", true);
            }
        });


        if (!Topitup.TIU_LICENSE.equals("")) {
            if (checkPermission()) {
                ///method to get Images
                //  getAddslogin();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    img_gif_.setImageDrawable(getDrawable(R.drawable.login_advertisement));

                } else {
                    if (get_adv_data.equals("local")) {
                        img_gif.setVisibility(View.GONE);
                        img_gif_local.setVisibility(View.VISIBLE);
                        img_gif_local.setImageDrawable(getDrawable(R.drawable.global_1687363966_st));

                       /* String imagePath = "file:///android_res/drawable/global_1687363966_st.gif";
                        String html = "<html><body style='margin:0; padding:0;'><img src=\"" + imagePath + "\" style='width:100; height:200;'/></body></html>";
                        img_gif.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);*/
                    } else {
                        img_gif.setVisibility(View.VISIBLE);
                        img_gif_local.setVisibility(View.GONE);
                        img_gif.loadDataWithBaseURL("file:///android_asset/", get_adv_data, "text/html", "UTF-8", null);
                        img_gif.setBackgroundColor(Color.TRANSPARENT);
                    }

                }

            } /*else {
                requestPermission();
            }*/

         /*   if (this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, REQUEST_EXTERNALRESULT);
            }*/


        }
        //  Print.Initialize();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageView scan_pay = findViewById(R.id.scan_pay);
        if (Topitup.TIU_LICENSE != "") {
            scan_pay.setVisibility(View.VISIBLE);
        } else {
            scan_pay.setVisibility(View.GONE);

        }
        scan_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPinDialog();
            }
        });
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
        if (setting_last_sale.equals("1")) {
            get_last_sale();

        }
        if (Topitup.TIU_LICENSE != "") {
            get_status_full_setup();
            getNotice_login();
        }


        if (!Topitup.DEBUG) {
            if (Topitup.TIU_LICENSE != "") {
                get_message();
            }
        }

       /* ll_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Topitup.TIU_LICENSE.equals("")) {
                    Intent myIntentBanking = new Intent(mContext, activity_messaging.class);
                    startActivity(myIntentBanking);
                } else {
                    Toast.makeText(mContext, "Please activate Top it Up", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_banking_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Topitup.TIU_LICENSE.equals("")) {
                    Intent myIntentBanking = new Intent(mContext, activity_banking_detail.class);
                    startActivity(myIntentBanking);
                } else {
                    Toast.makeText(mContext, "Please activate Top it Up", Toast.LENGTH_SHORT).show();
                }
            }
        });
      */
        ll_activation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Topitup.TIU_LICENSE.equals("")) {
                    Intent myIntentActivation = new Intent(mContext, activity_activation.class);
                    startActivity(myIntentActivation);
                } else {
                    Toast.makeText(mContext, "Please activate Top it Up", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.example.new_sample");
                //    notificationIntent.setPackage(null); // The golden row !!!

                if (notificationIntent != null) {
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(notificationIntent);
                } else {
                    Toast.makeText(activity_login.this, "Calculator app Was Not Installed in your device", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void showPinDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_pin);
        dialog.setCancelable(false);

        EditText etPin = dialog.findViewById(R.id.etPin);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(v -> {
            enteredPinQR = etPin.getText().toString().trim();

            if (enteredPinQR.length() != 4) {
                etPin.setError("Enter 4-digit PIN");
                return;
            }

            if (isPinCorrect(enteredPinQR)) {
                dialog.dismiss();
                openQrScanner();
            } else {
                etPin.setError("Invalid PIN");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());


        // ✅ Focus on PIN field
        etPin.requestFocus();
        dialog.show();
    }

    private void openQrScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String qrData = result.getContents();
            Log.e("data response", "data..qr..." + qrData);

//            String qrData = result.getContents();

            // Extract last path segment
            Uri uri = Uri.parse(qrData);
            String lastSegment = uri.getLastPathSegment();

            if (lastSegment != null && lastSegment.contains("_")) {

                String[] parts = lastSegment.split("_");

                String supplierId = parts[0];   // "6"
                getSupplierDetails(supplierId);
            }
//            Toast.makeText(this, "QR: " + qrData, Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getSupplierDetails(String supplierId) {

        Call<SupplierResponse> call = apiService.getSupplierDetails(supplierId, Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<SupplierResponse>() {
            @Override
            public void onResponse(Call<SupplierResponse> call, Response<SupplierResponse> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {
                        if (!response.headers().get("Server").equals("TIU")) {
                            throw new UserException("Please check your internet connection!");
                        }

                        SupplierResponse apiResponse = response.body();

                        Log.e("log response", "response........" + apiResponse.getStatus());
                        if (!"success".equalsIgnoreCase(apiResponse.getStatus())) {

                            Toasty.error(
                                    mContext,
                                    apiResponse.getMessage() != null
                                            ? apiResponse.getMessage()
                                            : "Supplier not found",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }
                        SupplierData supplier = response.body().getSupplier();
                        Log.e("log response", "response........" + supplier.getAddress1());

//                        if (supplier != null) {


                            showWholesalerPaymentDialog(activity_login.this, supplier, supplierId);

//                        }else{
//                            Toast.makeText(activity_login.this,"invalid supplier",Toast.LENGTH_SHORT).show();
//                        }

                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<SupplierResponse> call, Throwable t) {
            }
        });

    }

    private void showLoading(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) return;

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(progressBar);
        builder.setCancelable(false);

        loadingDialog = builder.create();

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
/*    public void showWholesalerPaymentDialog(Activity activity,
                                            SupplierData supplier,
                                            String supplierId) {

//        if (activity.isFinishing() || activity.isDestroyed()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_wholesaler_payment, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }

        // findViews
        EditText etAccountNo = view.findViewById(R.id.etAccountNo);
        AppCompatEditText amntEditText = view.findViewById(R.id.dialogEditText);
        AppCompatEditText amntEditText_cent = view.findViewById(R.id.dialogEditText_cent);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        ImageView imgClose = view.findViewById(R.id.imgClose);

        // set data
        ((TextView) view.findViewById(R.id.tvSupplierName))
                .setText(supplier.getSupplierName());

        imgClose.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String accountNo = etAccountNo.getText().toString().trim();

            if (accountNo.isEmpty()) {
                etAccountNo.setError("Enter account number");
                return;
            }

            String rand = amntEditText.getText().toString();
            String cent = amntEditText_cent.getText().toString();

            if (rand.isEmpty()) {
                amntEditText.setError("Enter amount");
                return;
            }

            if (cent.isEmpty()) cent = "00";

            double value = Double.parseDouble(rand + "." + cent);

            DecimalFormat df = new DecimalFormat("0.00");
            String wholeSaleAmount = df.format(value).replace(",", ".");

            dialog.dismiss();
            showLoading(activity);
            doPayment(wholeSaleAmount, accountNo, supplierId);
        });

        dialog.show();

        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }*/

    public void showWholesalerPaymentDialog(Context context, SupplierData supplier, String supplierId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_wholesaler_payment, null);


        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Transparent background (rounded corners visible)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvSupplierName = view.findViewById(R.id.tvSupplierName);
        TextView tvLegalName = view.findViewById(R.id.tvLegalName);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        txt_rand = view.findViewById(R.id.txt_rand);

        // Initialize Views
        EditText etAccountNo = view.findViewById(R.id.etAccountNo);
//        EditText etAmount = view.findViewById(R.id.etAmount);
//        EditText etPaise = view.findViewById(R.id.etPaise);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        ImageView imgClose = view.findViewById(R.id.imgClose);
        ImageView clearRand_Cents = view.findViewById(R.id.clearRand_Cents);
        EditText amntEditText = view.findViewById(R.id.dialogEditText);
        EditText amntEditText_cent = view.findViewById(R.id.dialogEditText_cent);
        // Set API data
        tvSupplierName.setText(supplier.getSupplierName());
        tvLegalName.setText("Legal Name: " + supplier.getLegalName());
        tvAddress.setText("Address: " + supplier.getAddress1() + "," + supplier.getCity());
        tvPhone.setText("Phone: " + supplier.getPhone());
        moneyTextWatcher = new MoneyTextWatcherRand(amntEditText);
        moneyTextWatcherCent = new MoneyTextWatcherCent(amntEditText_cent);
        cent_value_entered = "";
        rand_value_entered = "";
        amntEditText.addTextChangedListener(moneyTextWatcher);
        amntEditText_cent.addTextChangedListener(moneyTextWatcherCent);

        clearRand_Cents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!amntEditText.getText().toString().isEmpty() || !amntEditText_cent.getText().toString().isEmpty()) {
                    amntEditText.setText("");
                    amntEditText_cent.setText("");
                    amntEditText_cent.clearFocus();
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            String accountNo = etAccountNo.getText().toString().trim();

            if (accountNo.isEmpty()) {
                etAccountNo.setError("Enter account number");
                return;
            }

            String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();


            double value = Double.parseDouble(amountEnter);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String wholeSaleAmount = decimalFormat.format(value).replace(",", ".");

//            value = round(value * 100);

            Log.e("whole sale amount", value + "...value......." + wholeSaleAmount);
            showLoading(activity_login.this);
            doPayment(wholeSaleAmount, accountNo, supplierId);
            // TODO: Payment logic here


        });

        dialog.show();

        // Optional: Set dialog width
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private void doPayment(String wholeSaleAmount, String accountNo, String supplierId) {
        Call<WholesaleResponse> call = apiService.wholesalePayment(supplierId, "1", accountNo, wholeSaleAmount, Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<WholesaleResponse>() {
            @Override
            public void onResponse(Call<WholesaleResponse> call, Response<WholesaleResponse> response) {

                if (response.isSuccessful()) {

                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {
                        if (!response.headers().get("Server").equals("TIU")) {
                            throw new UserException("Please check your internet connection!");
                        }

                        WholesaleResponse apiResponse = response.body();

                        Log.e("log response", "response........" + apiResponse.getStatus());
                        if (!"COMPLETED".equalsIgnoreCase(apiResponse.getStatus())) {

                            Toasty.error(
                                    mContext,
                                    apiResponse.getMessage() != null
                                            ? apiResponse.getMessage()
                                            : "Transaction Failed",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        } else {
                            Toast.makeText(
                                    mContext,
                                    apiResponse.getMessage() != null
                                            ? apiResponse.getMessage()
                                            : "Transaction Successfull",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        hideLoading();
                    } catch (Exception ex) {
                        hideLoading();
                    }
                } else {
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<WholesaleResponse> call, Throwable t) {
                hideLoading();
            }
        });
    }

    public class MoneyTextWatcherCent implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcherCent(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                String str = s.toString();
                if (str.equals("")) {
                    if (rand_value_entered.equals("")) {
                        txt_rand.setText("");
                    } else {
                        cent_value_entered = "";
                        txt_rand.setText(rand_value_entered + " Rand ");
                    }
                } else {
                    final long number = Long.parseLong(s.toString());
                    Log.e("electricity", "text watcher cent" + s.toString());

                    cent_value_entered = WordsConert.convert(number);
                    if (rand_value_entered.equals("")) {
                        txt_rand.setText(cent_value_entered + " Cent2");
                    } else {
                        txt_rand.setText(rand_value_entered + " Rand " + cent_value_entered + " Cent");
                    }
                }
            } catch (NumberFormatException e) {
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public class MoneyTextWatcherRand implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcherRand(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                String str = s.toString();
                if (str.equals("")) {

                    if (cent_value_entered.equals("")) {
                        txt_rand.setText("");

                    } else {
                        txt_rand.setText(" " + cent_value_entered + " Cent1");
                    }
                } else {
                    String trimmed = s.toString().trim();
                    long number = 0;
                    if (!trimmed.isEmpty()) {
                        try {
                            number = Long.parseLong(trimmed);
                            // Use the number safely
                        } catch (NumberFormatException e) {
                            Log.e("electricity", "Invalid number: " + trimmed);
                        }
                    }
                    rand_value_entered = WordsConert.convert(number);
                    if (cent_value_entered.equals("")) {
                        txt_rand.setText(rand_value_entered + " Rand");

                    } else {
                        txt_rand.setText(rand_value_entered + " Rand " + cent_value_entered + " Cent");
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private boolean isPinCorrect(String pin) {
//        SharedPreferences prefs = getSharedPreferences("SECURITY", MODE_PRIVATE);
//        String savedPin = prefs.getString("USER_PIN", "");
        pos_users userqr = realm.where(pos_users.class).equalTo("posuser_pin", pin).equalTo("posuser_status", 1).findFirst();

        if (null == userqr) {

            return false;
        } else {
            Topitup.POSUSER_ID = String.valueOf(userqr.posuser_id);

            return true;
            //            mLoginProgress.setVisibility(View.VISIBLE);
//            Topitup.IS_ADMIN = String.valueOf(userqr.posuser_isadmin);
//            Topitup.POSUSER_NAME = user.posuser_firstname + " " + user.posuser_surname;
//            Topitup.RICA_REG = user.rica_registered;
//            keyPadLockedFlag = false;
//            get_swipe_realtime();

        }


    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_login.rl_network.setVisibility(View.VISIBLE);
                    activity_login.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_login.rl_network.setVisibility(View.INVISIBLE);
                    activity_login.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_login.rl_network.setVisibility(View.INVISIBLE);
                    activity_login.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

   /* public void printmethod(View view) {
        Intent intent = new Intent(activity_login.this, PrintDemo.class);
        startActivity(intent);
        Toast.makeText(activity_login.this, "print clicked", Toast.LENGTH_SHORT).show();
    }*/

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(this, CAMERA);



        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    //To request permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, CAMERA}, REQUEST_EXTERNALRESULT);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNALRESULT) {
            if (grantResults.length > 0) {
                try {
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storage_write = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storage && storage_write) {
                        if (ActivityCompat.checkSelfPermission(activity_login.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(activity_login.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            return;
                        }
                        //  getAddslogin();

                        if (get_adv_data.equals("local")) {
                            img_gif.setVisibility(View.GONE);
                            img_gif_local.setVisibility(View.VISIBLE);
                            img_gif_local.setImageDrawable(getDrawable(R.drawable.global_1687363966_st));
                          /*  String imagePath = "file:///android_res/drawable/global_1687363966_st.gif";
                            String html = "<html><body style='margin:0; padding:0;'><img src=\"" + imagePath + "\" style='width:100%; height:auto;'/></body></html>";
                            img_gif.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);*/
                        } else {
                            img_gif.setVisibility(View.VISIBLE);
                            img_gif_local.setVisibility(View.GONE);

                            img_gif.loadDataWithBaseURL("file:///android_asset/", get_adv_data, "text/html", "UTF-8", null);
                            img_gif.setBackgroundColor(Color.TRANSPARENT);
                        }

                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getBattery(Context context) {

        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new
                IntentFilter(ACTION_BATTERY_CHANGED));


    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    private void getNotice_login() {

        Call<MessageServiceNotice> call = apiService.get_notice_(Topitup.TIU_LICENSE);
        call.enqueue(new Callback<MessageServiceNotice>() {
            @Override
            public void onResponse(Call<MessageServiceNotice> call, Response<MessageServiceNotice> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        dialog.dismiss();
                        return;
                    }

                    try {
                        if (!response.headers().get("Server").equals("TIU")) {
                            dialog.dismiss();
                            throw new UserException("Please check your internet connection!");
                        }

                        if (response.body().toString().toLowerCase().contains("notice_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {

                        } else {

                            RealmResults<MessageServiceNotice> userNotices = realm.where(MessageServiceNotice.class).findAll();

                            if (userNotices.size() == 0) {
                                displayDialog(response.body());
                            }

                            for (int i = 0; i < userNotices.size(); i++) {

//                            if (!response.body().notice_id.equals(userNotices.get(i).notice_id) ) {
                                if (!userNotices.get(i).notice_status.equals("true")) {
                                    displayDialog(response.body());
                                }
//                            }
                            }
                        }


                    } catch (Exception ex) {

                      /*  if (response.raw() != null)
                            response.raw().close();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageServiceNotice> call, Throwable t) {
            }
        });
    }

    private void get_status_full_setup() {

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();


        if (tiu_settings == null) {

            spiver = "0";
        } else {

            spiver = tiu_settings.spi_ver;
        }

        Call<GetStatusFull> call = apiService.get_status_full_flag(Topitup.TIU_LICENSE, "", Topitup.APP_VERSION, spiver, "");
        call.enqueue(new Callback<GetStatusFull>() {
            @Override
            public void onResponse(Call<GetStatusFull> call, Response<GetStatusFull> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {
                        realm.beginTransaction();
                        realm.where(GetStatusFull.class).findAll().deleteAllFromRealm();
                        realm.commitTransaction();
                        GetStatusFull res = response.body();

                        if (res != null) {
                            res.customer_id = Topitup.CUSTOMER_ID;
                        }

                        //res.enable_realtime_swipe=enable_realtime_swipe;
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(res);
                        realm.commitTransaction();

                        //      final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();
                        //   Toasty.error(mContext,"enable_rpt_comm_statement="+tiu_settings1.enable_rpt_monthly_deposit, 12000, true).show();
                    } catch (Exception ex) {
                        //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();
                  /*  if (response.raw() != null)
                        response.raw().close();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<GetStatusFull> call, Throwable t) {
            }
        });


    }

    private void update_balance() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");

        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_last_sale = settings.getString("setting_last_sale", "0");

        SharedPreferences.Editor editor = settings.edit();

        if (setting_balance_login.equals("1")) {
            editor.putString("setting_balance_cashier", "1");
            editor.putString("setting_balance_admin", "1");
            editor.commit();


        }

        if (setting_balance_login_bills.equals("1")) {
            editor.putString("setting_balance_cashier_bills", "1");
            editor.putString("setting_balance_admin_bills", "1");
            editor.commit();

        }

        if (setting_balance_login_commission.equals("1")) {
            editor.putString("setting_balance_cashier_commission", "1");
            editor.putString("setting_balance_admin_commission", "1");
            editor.commit();

        }

        if (setting_balance_login_swipe.equals("1")) {
            editor.putString("setting_balance_cashier_swipe", "1");
            editor.putString("setting_balance_admin_swipe", "1");
            editor.commit();
        }

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final View view21 = findViewById(R.id.view21);
        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        view21.setVisibility(View.GONE);
        try {
            tiu_title_outlet.setSelected(true);

            final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
            tiu_title_outlet.setText(tiu_settings.account_number);
            text_store_name.setText(tiu_settings.company_name);

            noticeid = tiu_settings.noticeid;
            Topitup.CUSTOMER_ID = tiu_settings.customer_id;
            final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            if (setting_balance_login.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.available_balance + " ");
            } else {
                tiu_title_balance.setVisibility(View.GONE);
            }

            if (setting_balance_login_bills.equals("1")) {

                view2.setVisibility(View.VISIBLE);
//                if (!tiu_fin_balance.balance_cash.equals("0.00"))
                tiu_title_balance_cash.setText("Bills R  " + tiu_fin_balance.balance_cash);
            } else {
                view2.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }

            if (setting_balance_login_commission.equals("1")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.commission + " ");

            } else {
                tiu_title_balance_commision.setVisibility(View.GONE);
            }

            if (setting_balance_login_swipe.equals("1")) {
                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.swipe);

            } else {
                view3.setVisibility(View.GONE);
                tiu_title_balance_swipe.setVisibility(View.GONE);
            }

           /* if (setting_balance_login.equals("1")) {
                tiu_title_balance.setText("Standard R " + tiu_fin_balance.available_balance);
                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.commission);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.swipe);

                if (!tiu_fin_balance.balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.balance_cash);
                // tiu_title_balance.setText("Standard R " + tiu_fin_balance.available_balance+" | "+"Bills R " + tiu_fin_balance.balance_cash);
            } else {
                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }*/
        } catch (Exception ex) {
        }

    }

    private Animation inFromRightAnimation() {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(6000);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void get_last_sale() {

        lastSaleData = "";
        Call<ResponseBody> call = apiService.get_last_voucher_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.headers().get("Server") == null) {
                    return;
                }

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


                            } else {
                                // stock_uid = obj.getString("stock_uid");

                                //   txt_last_header.setText(obj.getString("last_header"));
                                //   txt_last_time.setText(obj.getString("last_time"));
                                String[] toSplit1 = obj.getString("last_time").split("\\|");
                                //  txt_last_voucher_info.setText(obj.getString("message"));
                                String[] toSplit = obj.getString("message").split("\n");
                                // lastSaleData = "<u>Last Sale</u><br>Voucher : <b>"+toSplit[0];
                                lastSaleData = "Last Sale : <b>" + toSplit[1] + " " + toSplit1[1] + "</b><br>Voucher : <b>" + toSplit[0] + "</b> <br> Cashier: <b>" + toSplit[2].replace("User", "") + "</b>";

                                //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    textLastSale.setText(Html.fromHtml(lastSaleData, Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    textLastSale.setText(Html.fromHtml(lastSaleData));
                                }


                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }


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

    private void get_message() {
        //Toasty.error(mContext, "date", 18000, true).show();

        try {

            final MessageService mSrvice = realm.where(MessageService.class).findFirst();

            if (noticeid.equals(mSrvice.notice_id)) {
                notice_data = mSrvice.notice_data;
                Date date2 = new Date();


                WebView wb_notice = findViewById(R.id.wb_notice);
                wb_notice.getSettings().setJavaScriptEnabled(false);

                wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");


            } else {

                realm.beginTransaction();
                realm.where(MessageService.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();

                Call<MessageService> call = apiService.get_notice(Topitup.TIU_LICENSE);
                call.enqueue(new Callback<MessageService>() {
                    @Override
                    public void onResponse(Call<MessageService> call, Response<MessageService> response) {

                        if (response.isSuccessful()) {

                            if (!response.headers().get("Server").equals("TIU")) {
                                Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                                dialog.dismiss();
                                return;
                            }

                            //Toasty.error(mContext, "Could not log in", 3000, true).show();
                            try {


                                //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                                if (!response.headers().get("Server").equals("TIU")) {
                                    dialog.dismiss();
                                    throw new UserException("Please check your internet connection!");
                                }


                                if (response.body().toString().toLowerCase().contains("notice_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                                    //
                                } else {


                                    MessageService result = response.body();

                                    realm.beginTransaction();

                                    byte[] bytes = android.util.Base64.decode(result.notice_data, android.util.Base64.DEFAULT);
                                    notice_data = new String(bytes);


                                    WebView wb_notice = findViewById(R.id.wb_notice);
                                    wb_notice.getSettings().setJavaScriptEnabled(false);
                                    wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");
                                    //  webView.loadData(notice_data, "text/html; charset=utf-8",null);

                                    //
                                    //  Log.i("notice_data",notice_data);
                                    result.notice_data = HtmlCompat.fromHtml(notice_data, 0).toString();
                                    result.notice_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    //  Toasty.error(mContext, "Unable to login"+result.notice_date, 18000, true).show();
                                    realm.copyToRealmOrUpdate(result);
                                    realm.commitTransaction();

                                    // update_spi(service_provider_data);

                                    // stopCustomDialog("Updated","Product catalogue updated.");

                                }

                            } catch (Exception ex) {
                               /* if (response.raw() != null)
                                    response.raw().close();*/
                            }
                        } else {
                            dialog.dismiss();

                        }


                    }

                    @Override
                    public void onFailure(Call<MessageService> call, Throwable t) {


                    }
                });


            }


        } catch (Exception ex) {
          /*  {

                realm.beginTransaction();
                realm.where(MessageService.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();

                Call<MessageService> call = apiService.get_notice(Topitup.TIU_LICENSE);
                call.enqueue(new Callback<MessageService>() {
                    @Override
                    public void onResponse(Call<MessageService> call, Response<MessageService> response) {


                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            dialog.dismiss();
                            return;
                        }
                        //Toasty.error(mContext, "Could not log in", 3000, true).show();
                        try {


                            //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                            if (!response.headers().get("server").equals("TIU")) {
                                dialog.dismiss();
                                throw new UserException("Please check your internet connection!");
                            }


                            if (response.body().toString().toLowerCase().contains("notice_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                                //
                            } else {


                                MessageService result = response.body();

                                realm.beginTransaction();

                                // realm.where(MessageService.class).findAll().deleteAllFromRealm();
                                //realm.commitTransaction();


                                byte[] bytes = android.util.Base64.decode(result.notice_data, android.util.Base64.DEFAULT);
                                notice_data = new String(bytes);

                                //notice_data=notice_data.replace("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title>N</title><style>html, body { overflow: hidden;cursor:none;} body { margin: 0; padding: 0px; } .notice { margin:10px;padding-left:10px; border-left:2px solid #efefef; height:55px;font-family:Tahoma, Geneva, sans-serif; font-size:16px; color:#6c6c6c;} </style>","");
                                // //  Toasty.error(mContext, "Unable to login"+HtmlCompat.fromHtml(notice_data,0), 18000, true).show();
                                // notice_data=notice_data.replace("Top it Up:","");
                                //   notice_data=notice_data.replace("</head><body><div class=\"notice\">","");
                                //    notice_data=notice_data.replace("</div></body></html>","");
                                //   textView3.setText(HtmlCompat.fromHtml(notice_data,0));
                                //newImageView.setVisibility(View.VISIBLE);
                                //  webView.loadData(notice_data, "text/html; charset=utf-8",null);


                                WebView wb_notice = findViewById(R.id.wb_notice);
                                wb_notice.getSettings().setJavaScriptEnabled(false);
                                wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");


                                //
                                //Log.i("notice_data",notice_data);
                                result.notice_data = HtmlCompat.fromHtml(notice_data, 0).toString();
                                result.notice_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                //  Toasty.error(mContext, "Unable to login"+result.notice_date, 18000, true).show();
                                realm.copyToRealmOrUpdate(result);
                                realm.commitTransaction();

                                // update_spi(service_provider_data);

                                // stopCustomDialog("Updated","Product catalogue updated.");

                            }

                        } catch (Exception ex) {

                        }


                    }

                    @Override
                    public void onFailure(Call<MessageService> call, Throwable t) {


                    }
                });


            }*/
        }


    }

    public void stopHandler() {
        refreshHandlerscreensaver.removeCallbacks(runnablescreensaver);
    }


/*
    private void setEditTextListener() {
        this.mUserAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (activity_login.this.mUserAccessCode.getText().length() == USER_PIN_MAX_CHAR) {
                    animateLoginButtonInOut(true);
                }
            }
        });
    }
*/

    public void startHandler() {
        // Toast.makeText(activity_login.this, "user is inactive from last 5 minutes"+((Topitup) getApplication()).getScreensavertime(),Toast.LENGTH_SHORT).show();

        refreshHandlerscreensaver.postDelayed(runnablescreensaver, ((Topitup) getApplication()).getScreensavertime()); //for 5 minutes
    }

    public void click_show_more(View v) {

        //Toasty.error(mContext, v.toString(), 3000, true).show();

//        Intent intent = new Intent(mContext, activity_web.class);
//        intent.putExtra("URL", Topitup.BASE_URL_SYNC + "info/more_concat_details/?l=" + Topitup.TIU_LICENSE);
//        startActivity(intent);

    }

    private void showCustomDialog(String sTitle, String sContent, Boolean bCloseVisible) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pageLoadingWrapper.setVisibility(View.GONE);

        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);


        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        bt_close = dialog.findViewById(R.id.bt_close);
        if (!bCloseVisible) bt_close.setVisibility(View.GONE);

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

    private void configureViews() {

        mUserAccessCode = findViewById(R.id.activity_login_access_code_value);
        img_one = findViewById(R.id.img_one);
        img_two = findViewById(R.id.img_two);
        img_three = findViewById(R.id.img_three);
        img_four = findViewById(R.id.img_four);
//        mUserAccessCode.setTransformationMethod(new HiddenPassTransformationMethod());

        mLoginProgress = findViewById(R.id.login_button_progress);

        mLoginProgress.getIndeterminateDrawable().setColorFilter(0xFFcc0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        clearButton = findViewById(R.id.simpleImageView);
        mOneButton = findViewById(R.id.one_button);
        mTwoButton = findViewById(R.id.two_button);
        mThreeButton = findViewById(R.id.three_button);
        mFourButton = findViewById(R.id.four_button);
        mFiveButton = findViewById(R.id.five_button);
        mSixButton = findViewById(R.id.six_button);
        mSevenButton = findViewById(R.id.seven_button);
        mEightButton = findViewById(R.id.eight_button);
        mNineButton = findViewById(R.id.nine_button);
        //mZeroButton = (TextView)findViewById(R.id.zero_button);
        // mDeleteButton = (TextView)findViewById(R.id.activity_login_access_code_delete);

        mIsDemo = findViewById(R.id.activity_login_is_demo);
        mIsDemo.setVisibility(View.VISIBLE);

        LinearLayout ll_demo = findViewById(R.id.ll_demo);

        ll_demo.setVisibility(View.VISIBLE);

        if (!Topitup.DEBUG) mIsDemo.setVisibility(View.INVISIBLE);


        ImageView mLogo = findViewById(R.id.activity_login_admin);
        mLogo.setOnClickListener(this);

        this.mLoginProgress.setVisibility(View.GONE);

        this.mOneButton.setOnClickListener(this);
        this.mOneButton.setOnTouchListener(this);
        this.mTwoButton.setOnClickListener(this);
        this.mTwoButton.setOnTouchListener(this);
        this.mThreeButton.setOnClickListener(this);
        this.mThreeButton.setOnTouchListener(this);
        this.mFourButton.setOnClickListener(this);
        this.mFourButton.setOnTouchListener(this);
        this.mFiveButton.setOnClickListener(this);
        this.mFiveButton.setOnTouchListener(this);
        this.mSixButton.setOnClickListener(this);
        this.mSixButton.setOnTouchListener(this);
        this.mSevenButton.setOnClickListener(this);
        this.mSevenButton.setOnTouchListener(this);
        this.mEightButton.setOnClickListener(this);
        this.mEightButton.setOnTouchListener(this);
        this.mNineButton.setOnClickListener(this);
        this.mNineButton.setOnTouchListener(this);
        //this.mZeroButton.setOnClickListener(this);
        //this.mZeroButton.setOnTouchListener(this);
        // this.mDeleteButton.setVisibility(this);
        //this.mDeleteButton.setOnClickListener(this);

/*
        mUserAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.e("gowthami"+i,i1+"text changing"+i2);
                if(i == 0){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()>0){
                    clearButton.setVisibility(View.VISIBLE);
                }else {
                    clearButton.setVisibility(View.GONE);

                }

            }
        });
*/
    }

    private void displayDialog(MessageServiceNotice response) {

        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_login);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();


        byte[] bytes = android.util.Base64.decode(response.notice_data, android.util.Base64.DEFAULT);
        notice_data = new String(bytes);

       /* WebView wb_notice = (WebView) dialog.findViewById(R.id.wb_notice);
//        wb_notice.getSettings().setJavaScriptEnabled(false);
        wb_notice.loadData(notice_data, "text/html", "UTF-8");*/
//        wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");

        TextView txt_desc = dialog.findViewById(R.id.txt_desc);
        TextView txt_read = dialog.findViewById(R.id.txt_read);

        txt_desc.setText(notice_data);

        txt_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageServiceNotice result = response;

                realm.beginTransaction();

                result.setNotice_status("true");


                realm.copyToRealmOrUpdate(result);
                realm.commitTransaction();
                dialog.dismiss();
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void do_show_enter_setup() {


        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_setup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;*/
        final TextView tv_cell_number = dialog.findViewById(R.id.tv_cell_number);
        final TextView tv_device_id = dialog.findViewById(R.id.tv_device_id);
        RadioGroup radioserver = dialog.findViewById(R.id.rdpserver);
        Spinner spinner = dialog.findViewById(R.id.spinnerServer);
        spinner.setOnTouchListener((v, event) -> true); // Prevents touch events
        spinner.setOnItemSelectedListener(null);
        spinner.setClickable(false);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();

        LinearLayout device_demo = dialog.findViewById(R.id.device_demo);
        LinearLayout device_live = dialog.findViewById(R.id.device_live);
        TextView txt_license = dialog.findViewById(R.id.tv_please_enter);
        EditText txt_license_e = dialog.findViewById(R.id.tv_cell_number);
        RadioButton radiodemo = dialog.findViewById(R.id.radio_demo);
        RadioButton radiolve = dialog.findViewById(R.id.radio_demo);

        Button btn_activate = dialog.findViewById(R.id.btn_activate);
        Button btn_deactivate = dialog.findViewById(R.id.btn_deactivate);
//        String DAYDREAM = settings.getString("DAYDREAM", "NO");
        TextView header1 = dialog.findViewById(R.id.header1);
        Button btndaydream = dialog.findViewById(R.id.btn_daydream);
        Button btnTIULocker = dialog.findViewById(R.id.btnTIULocker);
        //  final String selectedServer = settings.getString("TIU_SERVER", "LIVE");
        //   Log.e("spinner selection", "........selection....." + selectedServer);


/*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equalsIgnoreCase("Colossus (Glassfish 4)")) {
                    SERVER = "LIVE";

                    editor.putString("TIU_SERVER", "SERVER1LIVE");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                } else if (item.equals("Intel (Payara 5)")) {
                    SERVER = "LIVE";

                    editor.putString("TIU_SERVER", "SERVER2LIVE");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                } else {
                    SERVER = "LIVE";

                    editor.putString("TIU_SERVER", "SERVER3LIVE");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                }

                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Colossus (Glassfish 4)");
        categories.add("Intel (Payara 5)");
        categories.add("Celeron (Payara 5)");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        String apiServer = settings.getString("API_SERVER", "");
        Toast.makeText(getApplicationContext(), apiServer, Toast.LENGTH_SHORT).show();
        // setupSpinnerData();
        String selectedServer = "";
        if (apiServer.contains("http://dev.topitup.co.za:25812")) {
            selectedServer = "DEMO";
            //     Topitup.BASE_URL = "http: //dev.topitup.co.za:25812";
        } else if (apiServer.contains("http://tx1.topitup.co.za:25812")) {
            selectedServer = "SERVER2LIVE";
            //   Topitup.BASE_URL = "http: //tx1.topitup.co.za:25812";
        } else if (apiServer.contains("http://tx2.topitup.co.za:25812")) {
            selectedServer = "SERVER3LIVE";
            //   Topitup.BASE_URL = "http: //tx2.topitup.co.za:25812";
        } else {
            selectedServer = "SERVERLIVE";
            //   Topitup.BASE_URL = "http: //tx.topitup.co.za:25812";
        }
        Log.e("spinner selection", "........selection....." + selectedServer);

        if (selectedServer.equals("SERVER2LIVE")) {
            spinner.setSelection(1);
        } else if (selectedServer.equals("SERVER3LIVE")) {
            spinner.setSelection(2);

        } else {
            spinner.setSelection(0);

        }


        String license_pin = settings.getString("device_licence_pin", "");
        String device_id = settings.getString("device_id", "");
        final String SERVERSHARED = settings.getString("TIU_SERVER", "LIVE");
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SERVERSHARED = settings.getString("TIU_SERVER", "LIVE");

                //  get_setup_information("4677217", "24559", SERVERSHARED);
                get_approved_licence(SERVERSHARED);
            }
        });
        btn_deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("device_licence_pin", "");
                editor.putString("device_id", "");
                editor.putString("TIU_SERVER", "");
                editor.putString("TIU_LICENSE", "");
                // editor.putString("TIU_SERVER", "DEMO");
                editor.commit();

                Realm realm = Realm.getDefaultInstance();

                try {
                    realm.executeTransactionAsync(
                            new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(pos_users.class);
                                    realm.delete(GetUpdateAll.class);
                                }
                            },
                            new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Toasty.success(mContext, "Database cleared successfully", 3000, true).show();
                                }
                            },
                            new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                                }
                            }
                    );

                } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                    ex.printStackTrace();

                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                }
                Intent intent = new Intent(mContext, activity_login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
            }

        });
        Button btntimezone = dialog.findViewById(R.id.btnTimezone);
        String TIMEZONE = settings.getString("TIMEZONE", "NO");
        if (TIMEZONE.equals("YES"))
            btntimezone.setBackgroundColor(R.color.color_green);
        btntimezone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                if (launchIntent != null) {
                    editor.putString("TIMEZONE", "YES");
                    editor.commit();
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });

        Button btnkeyboard = dialog.findViewById(R.id.btnKeyboard);
        String KEYBOARD = settings.getString("KEYBOARD", "NO");
        if (KEYBOARD.equals("YES"))
            btnkeyboard.setBackgroundColor(R.color.color_green);
        btnkeyboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
                editor.putString("KEYBOARD", "YES");
                editor.commit();
            }
        });


        btnTIULocker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("com.example.simpleapplocker");
                //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntentp != null) {
                    startActivity(launchIntentp);//null pointer check in case package name was not found
                }
            }
        });


        if (SERVERSHARED.equals("LIVE") || SERVERSHARED.equals("SERVER1LIVE") || SERVERSHARED.equals("SERVER2LIVE") || SERVERSHARED.equals("SERVER3LIVE")) {
            System.out.println("------" + Topitup.DEVICE_TYPE);
          /*  if (Topitup.DEVICE_TYPE.equals("TABLET")) {

                device_live.setVisibility(View.GONE);
                device_demo.setVisibility(View.VISIBLE);
                txt_license.setVisibility(View.VISIBLE);
                txt_license_e.setVisibility(View.VISIBLE);

            } else {*/
            if (Topitup.DEVICE_TYPE.equals("WPOS") || Topitup.DEVICE_TYPE.equals("Q1")) {
                device_live.setVisibility(View.VISIBLE);
                device_demo.setVisibility(View.GONE);
            } else {
                device_live.setVisibility(View.GONE);
                device_demo.setVisibility(View.VISIBLE);
            }

            txt_license.setVisibility(View.VISIBLE);
            txt_license_e.setVisibility(View.VISIBLE);
//            }


            radioserver.check(R.id.radio_live);
         /*   spinner.setVisibility(View.VISIBLE);
            header1.setVisibility(View.VISIBLE);*/
        } else {
            radioserver.check(R.id.radio_demo);
//            header1.setVisibility(View.GONE);
            device_live.setVisibility(View.GONE);
            device_demo.setVisibility(View.VISIBLE);
//            spinner.setVisibility(View.GONE);
        }
        tv_cell_number.setText(license_pin);
        tv_device_id.setText(device_id);


        radioserver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected


                if (checkedId == R.id.radio_demo) {
                    SERVER = "DEMO";


                    editor.putString("TIU_SERVER", "DEMO");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                    // Toast.makeText(activity_login.this,"DEMO",Toast.LENGTH_SHORT).show();
                } else {
                    SERVER = "LIVE";

                    editor.putString("TIU_SERVER", "LIVE");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                    // Toast.makeText(activity_login.this,"LIVE",Toast.LENGTH_SHORT).show();
                }
              /*  moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);*/

//                ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
//                Intent intent = new Intent(activity_login.this, activitySplashScreen.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
                Realm realm = Realm.getDefaultInstance();

                try {
                    realm.executeTransactionAsync(
                            new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(pos_users.class);
                                    realm.delete(GetUpdateAll.class);
                                }
                            },
                            new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Toasty.success(mContext, "Database cleared successfully", 3000, true).show();
                                }
                            },
                            new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                                }
                            }
                    );

                } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                    ex.printStackTrace();

                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                }
                Intent intent = new Intent(mContext, activity_login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
                Intent mStartActivity = new Intent(activity_login.this, com.za.toptitup.loginlibrary.activitySplashScreen.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(activity_login.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager) activity_login.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(1);

            }
        });


        Button btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });


        Button btnSaveNumber = dialog.findViewById(R.id.btnSaveNumber);
     /*   btnSaveNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
*//*
                if (tv_cell_number.getText().toString().length() == 0) {
                    Toasty.error(mContext, "Please enter a license upgrade pin!", 3000, true).show();
                    return;
                }*//*

                if (tv_device_id.getText().toString().length() == 0) {
                    Toasty.error(mContext, "Please enter the device ID!", 3000, true).show();
                    return;
                } else {
                    String SERVERSHARED = settings.getString("TIU_SERVER", "LIVE");

                    get_approved_by_Device_id(tv_device_id.getText().toString(), SERVERSHARED);
                }


            }
        });*/

        btnSaveNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Topitup.DEVICE_TYPE.equals("TABLET")) {
                    if (tv_device_id.getText().toString().length() == 0) {
                        Toasty.error(mContext, "Please enter the device ID!", 3000, true).show();
                    } else {
                        String SERVERSHARED = settings.getString("TIU_SERVER", "LIVE");

                        get_approved_by_Device_id(tv_device_id.getText().toString(), SERVERSHARED);
//                        get_setup_information(tv_cell_number.getText().toString(), tv_device_id.getText().toString(), SERVERSHARED);

                    }

                } else {


                    if (tv_cell_number.getText().toString().length() == 0) {
                        Toasty.error(mContext, "Please enter a license upgrade pin!", 3000, true).show();
                        return;
                    }

                    if (tv_device_id.getText().toString().length() == 0) {
                        Toasty.error(mContext, "Please enter the device ID!", 3000, true).show();
                        return;
                    }


                    //Clear old info
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_SERVER", "");
                    editor.putString("TIU_LICENSE", "");
                    // editor.putString("TIU_SERVER", "DEMO");
                    editor.commit();

                    Realm realm = Realm.getDefaultInstance();

                    try {
                        realm.executeTransactionAsync(
                                new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.delete(pos_users.class);
                                        realm.delete(GetUpdateAll.class);
                                    }
                                },
                                new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Toasty.success(mContext, "Database cleared successfully", 3000, true).show();
                                    }
                                },
                                new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        error.printStackTrace();
                                        Toasty.error(mContext, "Could not clear database", 3000, true).show();
                                    }
                                }
                        );

                    } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                        ex.printStackTrace();

                        Toasty.error(mContext, "Could not clear database", 3000, true).show();
                    }

                    get_setup_information(tv_cell_number.getText().toString(), tv_device_id.getText().toString(), SERVERSHARED);

                    //tech_cell_number = tv_cell_number.getText().toString();

                    dialog.dismiss();

                }
            }
        });

        Button btnCancelJC = dialog.findViewById(R.id.btnCancelJC);
        btnCancelJC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();


    }

  /*  public void setupSpinnerData() {

        String selectedServer="";
        if (apiServer.equalsIgnoreCase("http: //dev.topitup.co.za:25812")) {
            selectedServer = "DEMO";
        } else if (apiServer.equalsIgnoreCase("http: //tx1.topitup.co.za:25812")) {
            selectedServer = "SERVER2LIVE";
        } else if (apiServer.equalsIgnoreCase("http: //tx2.topitup.co.za:25812")) {
            selectedServer = "SERVER3LIVE";
        }else {
            selectedServer = "SERVERLIVE";
        }
        Log.e("spinner selection", "........selection....." + selectedServer);


    }*/

    private void get_approved_by_Device_id(String deviceId, String SERVERSHARED) {
      /*  String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
//        final Call<JsonObject> call = apiService.get_approve_licence("DEMO99c4-1999-11e9-84ad-001e6779cd30", "PP35272137000044");
        final Call<JsonObject> call = apiService.get_approve_by_deviceID(deviceId);
//        final Call<JsonObject> call = apiService.get_approve_licence("PP35272250000568");
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if (response.isSuccessful()) {
                    String device_id = response.body().get("device_id").toString().replace("\"", "");
                    String licence = response.body().get("secret_pin").toString().replace("\"", "");
                    if (!device_id.equalsIgnoreCase("0") && !licence.equalsIgnoreCase("0")) {
                        //Clear old info
                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("device_licence_pin", "");
                        editor.putString("device_id", "");
                        editor.putString("TIU_SERVER", "");
                        editor.putString("TIU_LICENSE", "");
                        // editor.putString("TIU_SERVER", "DEMO");
                        editor.commit();

                        Realm realm = Realm.getDefaultInstance();

                        try {
                            realm.executeTransactionAsync(
                                    new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.delete(pos_users.class);
                                            realm.delete(GetUpdateAll.class);
                                        }
                                    },
                                    new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            Toasty.success(mContext, "Database cleared successfully", 3000, true).show();
                                        }
                                    },
                                    new Realm.Transaction.OnError() {
                                        @Override
                                        public void onError(Throwable error) {
                                            error.printStackTrace();
                                            Toasty.error(mContext, "Could not clear database", 3000, true).show();
                                        }
                                    }
                            );

                        } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                            ex.printStackTrace();

                            Toasty.error(mContext, "Could not clear database", 3000, true).show();
                        }

                        get_setup_information(licence, device_id, SERVERSHARED);

                        //tech_cell_number = tv_cell_number.getText().toString();

                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        showCustomDialog("TopitUp", response.body().get("res_desc").toString().replace("\"", ""), true);

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    public void crashMe(View v) {
        throw new NullPointerException();
    }


    public void get_setup_information(final String pin, final String device_id, final String SERVERSHARED) {

        final Call<ResponseBody> call = apiService.get_asset_upgrade(pin, device_id);

        call.enqueue(new Callback<ResponseBody>() {

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    //Timber.e(response.body());

                    String res = "";
                    try {

                        res = response.body().string();

                    } catch (Exception ex) {
                        //
                        if (response.body() != null)
                            response.body().close();
                    }

                    if (res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                        //String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        String error_message = "";

                        if (res.contains("xml")) {
                            JSONObject jsonObj = null;
                            try {
                                //  jsonObj = XML.toJSONObject(res);
                                jsonObj = new JSONObject(res);

                                if (res.contains("error")) {
                                    error_message = jsonObj.getJSONObject("error").getString("err");

                                } else {
                                    error_message = jsonObj.getString("err");

                                }

                            } catch (JSONException e) {
                                Log.e("JSON exception", e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(res);
                                error_message = obj.getString("err");

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        showCustomDialog("Could Not Log In", error_message, true);
                        //Toasty.error(mContext, res, 3000, true).show();

                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("device_licence_pin", "");
                        editor.putString("device_id", "");
                        editor.putString("TIU_LICENSE", "");
                        editor.putString("API_SERVER", "");
                        //   editor.putString("TIU_SERVER", "DEMO");
                        editor.commit();

                    } else {        //Response OK

                        // Timber.e(res);

                        Toasty.info(mContext, "ID & License PIN saved.", 3000, true).show();
                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("device_licence_pin", pin);
                        editor.putString("device_id", device_id);
                        editor.putString("TIU_SERVER", SERVERSHARED);
                        editor.putString("API_SERVER", "");

                        //String SERVERSHARED = settings.getString("TIU_SERVER","DEMO");


                        // Toasty.info(mContext, "s=."+SERVERSHARED, 3000, true).show();

                        String[] parts = res.split("\\^");
                        String url;
                        String cleanedUrl;
                        if (parts.length <= 6) {
                            url = "http: //tx.topitup.co.za:25812";
                            cleanedUrl = url.replace(" ", ""); // Removes all spaces
                            System.out.println(cleanedUrl);
                            if (!cleanedUrl.trim().endsWith("/")) {
                                cleanedUrl += "/";
                            }
                            System.out.println(cleanedUrl);
                        } else {
                            url = parts[6];
                            cleanedUrl = url.replace(" ", ""); // Removes all spaces
                            System.out.println(cleanedUrl);
                            if (!cleanedUrl.trim().endsWith("/")) {
                                cleanedUrl += "/";
                            }
                            System.out.println(cleanedUrl);
                        }

                        editor.putString("TIU_LICENSE", parts[3]);
                        editor.putString("setting_screen_off", "0");
                        editor.putString("setting_last_sale", "1");
                        editor.putString("setting_balance_login", "1");
                        editor.putString("setting_balance_login_bills", "1");
                        editor.putString("setting_balance_login_commission", "1");
                        editor.putString("setting_balance_login_swipe", "1");
                        editor.putString("API_SERVER", cleanedUrl);
                        editor.putString("setting_time_out", "1");
                        editor.commit();
                        String apiServer = settings.getString("API_SERVER", "");
                        Toast.makeText(getApplicationContext(), apiServer, Toast.LENGTH_SHORT).show();
                        // setupSpinnerData();
                      /*  String selectedServer = "";
                        if (apiServer.equalsIgnoreCase("http: //dev.topitup.co.za:25812")) {
                            selectedServer = "DEMO";
                       //     Topitup.BASE_URL = "http: //dev.topitup.co.za:25812";
                        } else if (apiServer.equalsIgnoreCase("http: //tx1.topitup.co.za:25812")) {
                            selectedServer = "SERVER2LIVE";
                            Topitup.BASE_URL = "http: //tx1.topitup.co.za:25812";
                        } else if (apiServer.equalsIgnoreCase("http: //tx2.topitup.co.za:25812")) {
                            selectedServer = "SERVER3LIVE";
                            Topitup.BASE_URL = "http: //tx2.topitup.co.za:25812";
                        } else {
                org.gradle.api.InvalidUserDataException: Invalid catalog definition:
  - Problem: In version catalog libs, alias 'extensions' is not a valid alias.

* Try:
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

* Exception is:
java.lang.RuntimeException: org.gradle.api.InvalidUserDataException: Invalid catalog definition:
  - Problem: In version catalog libs, alias 'extensions' is not a valid alias.

    Reason: Alias 'extensions' is a reserved name in Gradle which prevents generation of accessors.

    Possible solution: Use a different alias which doesn't contain any of 'convention' or 'extensions'.

    For more information, please refer to https://docs.gradle.org/8.11.1/userguide/version_catalog_problems.html#reserved_alias_name in the Gradle documentation.
	at org.gradle.api.internal.catalog.DefaultDependenciesAccessors.generateAccessors(DefaultDependenciesAccessors.java:164)
	at org.gradle.configuration.BuildTreePreparingProjectsPreparer.generateDependenciesAccessorsAndAssignPluginVersions(BuildTreePreparingProjectsPreparer.java:92)
	at org.gradle.configuration.BuildTreePreparingProjectsPreparer.prepareProjects(BuildTreePreparingProjectsPreparer.java:55)
	at org.gradle.configuration.BuildOperationFiringProjectsPreparer$ConfigureBuild.run(BuildOperationFiringProjectsPreparer.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:166)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)
	at org.gradle.configuration.BuildOperationFiringProjectsPreparer.prepareProjects(BuildOperationFiringProjectsPreparer.java:40)
	at org.gradle.initialization.VintageBuildModelController.lambda$prepareProjects$2(VintageBuildModelController.java:84)
	at org.gradle.internal.model.StateTransitionController.lambda$doTransition$14(StateTransitionController.java:255)
	at org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:266)
	at org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:254)
	at org.gradle.internal.model.StateTransitionController.lambda$transitionIfNotPreviously$11(StateTransitionController.java:213)
	at org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:36)
	at org.gradle.internal.model.StateTransitionController.transitionIfNotPreviously(StateTransitionController.java:209)
	at org.gradle.initialization.VintageBuildModelController.prepareProjects(VintageBuildModelController.java:84)
	at org.gradle.initialization.VintageBuildModelController.getConfiguredModel(VintageBuildModelController.java:64)
	at org.gradle.internal.build.DefaultBuildLifecycleController.lambda$withProjectsConfigured$1(DefaultBuildLifecycleController.java:133)
	at org.gradle.internal.model.StateTransitionController.lambda$notInState$3(StateTransitionController.java:132)
	at org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:46)
	at org.gradle.internal.model.StateTransitionController.notInState(StateTransitionController.java:128)
	at org.gradle.internal.build.DefaultBuildLifecycleController.withProjectsConfigured(DefaultBuildLifecycleController.java:133)
	at org.gradle.internal.build.DefaultBuildToolingModelController.locateBuilderForTarget(DefaultBuildToolingModelController.java:58)
	at org.gradle.internal.buildtree.DefaultBuildTreeModelCreator$DefaultBuildTreeModelController.lambda$locateBuilderForTarget$0(DefaultBuildTreeModelCreator.java:64)
	at org.gradle.internal.build.DefaultBuildLifecycleController.withToolingModels(DefaultBuildLifecycleController.java:327)
	at org.gradle.internal.build.AbstractBuildState.withToolingModels(AbstractBuildState.java:160)
	at org.gradle.internal.buildtree.DefaultBuildTreeModelCreator$DefaultBuildTreeModelController.locateBuilderForTarget(DefaultBuildTreeModelCreator.java:64)
	at org.gradle.internal.buildtree.DefaultBuildTreeModelCreator$DefaultBuildTreeModelController.locateBuilderForDefaultTarget(DefaultBuildTreeModelCreator.java:59)
	at org.gradle.tooling.internal.provider.runner.DefaultBuildController.getTarget(DefaultBuildController.java:140)
	at org.gradle.tooling.internal.provider.runner.DefaultBuildController.getModel(DefaultBuildController.java:111)
	at org.gradle.tooling.internal.consumer.connection.ParameterAwareBuildControllerAdapter.getModel(ParameterAwareBuildControllerAdapter.java:40)
	at org.gradle.tooling.internal.consumer.connection.UnparameterizedBuildController.getModel(UnparameterizedBuildController.java:116)
	at org.gradle.tooling.internal.consumer.connection.NestedActionAwareBuildControllerAdapter.getModel(NestedActionAwareBuildControllerAdapter.java:32)
	at org.gradle.tooling.internal.consumer.connection.UnparameterizedBuildController.getModel(UnparameterizedBuildController.java:79)
	at org.gradle.tooling.internal.consumer.connection.NestedActionAwareBuildControllerAdapter.getModel(NestedActionAwareBuildControllerAdapter.java:32)
	at org.gradle.tooling.internal.consumer.connection.UnparameterizedBuildController.getModel(UnparameterizedBuildController.java:64)
	at org.gradle.tooling.internal.consumer.connection.NestedActionAwareBuildControllerAdapter.getModel(NestedActionAwareBuildControllerAdapter.java:32)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.lambda$initAction$6(GradleModelFetchAction.java:185)
	at com.intellij.gradle.toolingExtension.impl.telemetry.GradleOpenTelemetry.callWithSpan(GradleOpenTelemetry.java:74)
	at com.intellij.gradle.toolingExtension.impl.telemetry.GradleOpenTelemetry.callWithSpan(GradleOpenTelemetry.java:62)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.initAction(GradleModelFetchAction.java:184)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.doExecute(GradleModelFetchAction.java:139)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.lambda$execute$1(GradleModelFetchAction.java:104)
	at com.intellij.gradle.toolingExtension.impl.telemetry.GradleOpenTelemetry.callWithSpan(GradleOpenTelemetry.java:74)
	at com.intellij.gradle.toolingExtension.impl.telemetry.GradleOpenTelemetry.callWithSpan(GradleOpenTelemetry.java:62)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.lambda$execute$2(GradleModelFetchAction.java:103)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.withOpenTelemetry(GradleModelFetchAction.java:114)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.lambda$execute$3(GradleModelFetchAction.java:102)
	at com.intellij.gradle.toolingExtension.impl.util.GradleExecutorServiceUtil.withSingleThreadExecutor(GradleExecutorServiceUtil.java:18)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.execute(GradleModelFetchAction.java:101)
	at com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction.execute(GradleModelFetchAction.java:37)
	at org.gradle.tooling.internal.consumer.connection.InternalBuildActionAdapter.execute(InternalBuildActionAdapter.java:65)
	at org.gradle.tooling.internal.provider.runner.AbstractClientProvidedBuildActionRunner$ActionAdapter.executeAction(AbstractClientProvidedBuildActionRunner.java:109)
	at org.gradle.tooling.internal.provider.runner.AbstractClientProvidedBuildActionRunner$ActionAdapter.runAction(AbstractClientProvidedBuildActionRunner.java:97)
	at org.gradle.tooling.internal.provider.runner.AbstractClientProvidedBuildActionRunner$ActionAdapter.beforeTasks(AbstractClientProvidedBuildActionRunner.java:81)
	at org.gradle.internal.buildtree.DefaultBuildTreeModelCreator.beforeTasks(DefaultBuildTreeModelCreator.java:43)
	at org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.lambda$fromBuildModel$2(DefaultBuildTreeLifecycleController.java:83)
	at org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.lambda$runBuild$4(DefaultBuildTreeLifecycleController.java:120)
	at org.gradle.internal.model.StateTransitionController.lambda$transition$6(StateTransitionController.java:169)
	at org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:266)
	at org.gradle.internal.model.StateTransitionController.lambda$transition$7(StateTransitionController.java:169)
	at org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:46)
	at org.gradle.internal.model.StateTransitionController.transition(StateTransitionController.java:169)
	at org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.runBuild(DefaultBuildTreeLifecycleController.java:117)
	at org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.fromBuildModel(DefaultBuildTreeLifecycleController.java:82)
	at org.gradle.tooling.internal.provider.runner.AbstractClientProvidedBuildActionRunner.runClientAction(AbstractClientProvidedBuildActionRunner.java:43)
	at org.gradle.tooling.internal.provider.runner.ClientProvidedPhasedActionRunner.run(ClientProvidedPhasedActionRunner.java:59)
	at org.gradle.launcher.exec.ChainingBuildActionRunner.run(ChainingBuildActionRunner.java:35)
	at org.gradle.internal.buildtree.ProblemReportingBuildActionRunner.run(ProblemReportingBuildActionRunner.java:49)
	at org.gradle.launcher.exec.BuildOutcomeReportingBuildActionRunner.run(BuildOutcomeReportingBuildActionRunner.java:66)
	at org.gradle.tooling.internal.provider.FileSystemWatchingBuildActionRunner.run(FileSystemWatchingBuildActionRunner.java:140)
	at org.gradle.launcher.exec.BuildCompletionNotifyingBuildActionRunner.run(BuildCompletionNotifyingBuildActionRunner.java:41)
	at org.gradle.launcher.exec.RootBuildLifecycleBuildActionExecutor.lambda$execute$0(RootBuildLifecycleBuildActionExecutor.java:54)
	at org.gradle.composite.internal.DefaultRootBuildState.run(DefaultRootBuildState.java:130)
	at org.gradle.launcher.exec.RootBuildLifecycleBuildActionExecutor.execute(RootBuildLifecycleBuildActionExecutor.java:54)
	at org.gradle.internal.buildtree.InitDeprecationLoggingActionExecutor.execute(InitDeprecationLoggingActionExecutor.java:62)
	at org.gradle.internal.buildtree.InitProblems.execute(InitProblems.java:36)
	at org.gradle.internal.buildtree.DefaultBuildTreeContext.execute(DefaultBuildTreeContext.java:40)
	at org.gradle.launcher.exec.BuildTreeLifecycleBuildActionExecutor.lambda$execute$0(BuildTreeLifecycleBuildActionExecutor.java:71)
	at org.gradle.internal.buildtree.BuildTreeState.run(BuildTreeState.java:60)
	at org.gradle.launcher.exec.BuildTreeLifecycleBuildActionExecutor.execute(BuildTreeLifecycleBuildActionExecutor.java:71)
	at org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor$3.call(RunAsBuildOperationBuildActionExecutor.java:61)
	at org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor$3.call(RunAsBuildOperationBuildActionExecutor.java:57)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:209)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:166)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor.execute(RunAsBuildOperationBuildActionExecutor.java:57)
	at org.gradle.launcher.exec.RunAsWorkerThreadBuildActionExecutor.lambda$execute$0(RunAsWorkerThreadBuildActionExecutor.java:36)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withLocks(DefaultWorkerLeaseService.java:263)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:127)
	at org.gradle.launcher.exec.RunAsWorkerThreadBuildActionExecutor.execute(RunAsWorkerThreadBuildActionExecutor.java:36)
	at org.gradle.tooling.internal.provider.continuous.ContinuousBuildActionExecutor.execute(ContinuousBuildActionExecutor.java:110)
	at org.gradle.tooling.internal.provider.SubscribableBuildActionExecutor.execute(SubscribableBuildActionExecutor.java:64)
	at org.gradle.internal.session.DefaultBuildSessionContext.execute(DefaultBuildSessionContext.java:46)
	at org.gradle.internal.buildprocess.execution.BuildSessionLifecycleBuildActionExecutor$ActionImpl.apply(BuildSessionLifecycleBuildActionExecutor.java:92)
	at org.gradle.internal.buildprocess.execution.BuildSessionLifecycleBuildActionExecutor$ActionImpl.apply(BuildSessionLifecycleBuildActionExecutor.java:80)
	at org.gradle.internal.session.BuildSessionState.run(BuildSessionState.java:71)
	at org.gradle.internal.buildprocess.execution.BuildSessionLifecycleBuildActionExecutor.execute(BuildSessionLifecycleBuildActionExecutor.java:62)
	at org.gradle.internal.buildprocess.execution.BuildSessionLifecycleBuildActionExecutor.execute(BuildSessionLifecycleBuildActionExecutor.java:41)
	at org.gradle.internal.buildprocess.execution.StartParamsValidatingActionExecutor.execute(StartParamsValidatingActionExecutor.java:64)
	at org.gradle.internal.buildprocess.execution.StartParamsValidatingActionExecutor.execute(StartParamsValidatingActionExecutor.java:32)
	at org.gradle.internal.buildprocess.execution.SessionFailureReportingActionExecutor.execute(SessionFailureReportingActionExecutor.java:51)
	at org.gradle.internal.buildprocess.execution.SessionFailureReportingActionExecutor.execute(SessionFailureReportingActionExecutor.java:39)
	at org.gradle.internal.buildprocess.execution.SetupLoggingActionExecutor.execute(SetupLoggingActionExecutor.java:47)
	at org.gradle.internal.buildprocess.execution.SetupLoggingActionExecutor.execute(SetupLoggingActionExecutor.java:31)
	at org.gradle.launcher.daemon.server.exec.ExecuteBuild.doBuild(ExecuteBuild.java:70)
	at org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.WatchForDisconnection.execute(WatchForDisconnection.java:39)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.ResetDeprecationLogger.execute(ResetDeprecationLogger.java:29)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.RequestStopIfSingleUsedDaemon.execute(RequestStopIfSingleUsedDaemon.java:35)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.ForwardClientInput.lambda$execute$0(ForwardClientInput.java:40)
	at org.gradle.internal.daemon.clientinput.ClientInputForwarder.forwardInput(ClientInputForwarder.java:80)
	at org.gradle.launcher.daemon.server.exec.ForwardClientInput.execute(ForwardClientInput.java:37)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.LogAndCheckHealth.execute(LogAndCheckHealth.java:64)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.LogToClient.doBuild(LogToClient.java:63)
	at org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.EstablishBuildEnvironment.doBuild(EstablishBuildEnvironment.java:84)
	at org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)
	at org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)
	at org.gradle.launcher.daemon.server.exec.StartBuildOrRespondWithBusy$1.run(StartBuildOrRespondWithBusy.java:52)
	at org.gradle.launcher.daemon.server.DaemonStateCoordinator.lambda$runCommand$0(DaemonStateCoordinator.java:321)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:48)
Caused by: org.gradle.api.InvalidUserDataException: Invalid catalog definition:
  - Problem: In version catalog libs, alias 'extensions' is not a valid alias.

    Reason: Alias 'extensions' is a reserved name in Gradle which prevents generation of accessors.

    Possible solution: Use a different alias which doesn't contain any of 'convention' or 'extensions'.

    For more information, please refer to https://docs.gradle.org/8.11.1/userguide/version_catalog_problems.html#reserved_alias_name in the Gradle documentation.
	at org.gradle.api.internal.catalog.problems.DefaultCatalogProblemBuilder.throwError(DefaultCatalogProblemBuilder.java:55)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.throwVersionCatalogProblemException(DefaultVersionCatalogBuilder.java:225)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.throwAliasCatalogException(DefaultVersionCatalogBuilder.java:413)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.validateNormalizedAlias(DefaultVersionCatalogBuilder.java:401)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.normalizeAndValidateAlias(DefaultVersionCatalogBuilder.java:381)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.library(DefaultVersionCatalogBuilder.java:352)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.registerDependency(TomlCatalogFileParser.java:535)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.parseLibrary(TomlCatalogFileParser.java:394)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.lambda$parseLibraries$7(TomlCatalogFileParser.java:236)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.parseLibraries(TomlCatalogFileParser.java:236)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.parse(TomlCatalogFileParser.java:142)
	at org.gradle.api.internal.catalog.parser.TomlCatalogFileParser.parse(TomlCatalogFileParser.java:121)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.importCatalogFromFile(DefaultVersionCatalogBuilder.java:311)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.lambda$maybeImportCatalogs$3(DefaultVersionCatalogBuilder.java:254)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.withContext(DefaultVersionCatalogBuilder.java:175)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.maybeImportCatalogs(DefaultVersionCatalogBuilder.java:254)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.doBuild(DefaultVersionCatalogBuilder.java:182)
	at org.gradle.internal.lazy.UnsafeLazy.get(UnsafeLazy.java:35)
	at org.gradle.api.internal.catalog.DefaultVersionCatalogBuilder.build(DefaultVersionCatalogBuilder.java:167)
	at org.gradle.api.internal.catalog.DefaultDependenciesAccessors.generateAccessors(DefaultDependenciesAccessors.java:149)
	... 137 more            selectedServer = "SERVERLIVE";
                            Topitup.BASE_URL = "http: //tx.topitup.co.za:25812";
                        }*/
                        Intent intent = new Intent(mContext, activity_login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();
                        Intent mStartActivity = new Intent(mContext, activitySplashScreen.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);

                        //tv_response.setTextColor( Color.parseColor("#3c3c3c"));

                    }
                }

                //  Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                if (t instanceof IOException) {
                    showCustomDialog("No Internet", "Please check that your internet connection is working.", true);
                } else {
                    showCustomDialog("Could Not Log In", t.getMessage(), true);
                }


                //Toasty.error(mContext, t.getMessage(), 8000, true).show();
                //progressBar.setVisibility(View.GONE);
                //t.printStackTrace();

            }

        });


    }

    @Override
    public void onUserInteraction() {
        stopHandler();//stop first and then start
        startHandler();
        super.onUserInteraction();

    }

    @Override
    public void onClick(View vIn) {

        if (keyPadLockedFlag) return;


        if (vIn.getId() == R.id.activity_login_admin) {
            is_admin_mode = true;
            mIsDemo.setText("ADMIN");
            mIsDemo.setVisibility(View.VISIBLE);
            handler.postDelayed(runnable, 4000);
            resetPinPad();
            //do_show_enter_setup();
            return;
        }

      /*  switch (vIn.getId()) {
            case  com.za.toptitup.loginlibrary.R.id.one_button:

                Log.e("access code ", "length....." + this.accessCode.length());
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mOneButton.getText();
//                    this.accessCode.append(this.mOneButton.getText());
                }


                break;
            case  com.za.toptitup.loginlibrary.R.id.two_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mTwoButton.getText();

//                    this.mUserAccessCode.append(this.mTwoButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.three_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mThreeButton.getText();

//                    this.mUserAccessCode.append(this.mThreeButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.four_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mFourButton.getText();

//                    this.mUserAccessCode.append(this.mFourButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.five_button:

                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mFiveButton.getText();

//                    this.mUserAccessCode.append(this.mFiveButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.six_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mSixButton.getText();

//                    this.mUserAccessCode.append(this.mSixButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.seven_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mSevenButton.getText();

//                    this.mUserAccessCode.append(this.mSevenButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.eight_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mEightButton.getText();

//                    this.mUserAccessCode.append(this.mEightButton.getText());
                }
                break;
            case  com.za.toptitup.loginlibrary.R.id.nine_button:
                if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                    accessCode += this.mNineButton.getText();

//                    this.mUserAccessCode.append(this.mNineButton.getText());
                }
                break;
        }
*/
        if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.one_button) {
            Log.e("access code ", "length....." + this.accessCode.length());
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mOneButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.two_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mTwoButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.three_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mThreeButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.four_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mFourButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.five_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mFiveButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.six_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mSixButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.seven_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mSevenButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.eight_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mEightButton.getText();
            }
        } else if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.nine_button) {
            if (this.accessCode.length() < USER_PIN_MAX_CHAR) {
                accessCode += this.mNineButton.getText();
            }
        }

        Log.e("access code", "access.........." + accessCode);
        if (accessCode.length() == 1) {
            txt_invalid.setVisibility(View.GONE);
            clearButton.setVisibility(View.VISIBLE);
            img_one.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_two.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
            img_three.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
            img_four.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
            img_one.setVisibility(View.VISIBLE);
            img_two.setVisibility(View.VISIBLE);
            img_three.setVisibility(View.VISIBLE);
            img_four.setVisibility(View.VISIBLE);


        } else if (accessCode.length() == 2) {
            img_one.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_two.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_three.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
            img_four.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));

            img_one.setVisibility(View.VISIBLE);
            img_two.setVisibility(View.VISIBLE);
            img_three.setVisibility(View.VISIBLE);
            img_four.setVisibility(View.VISIBLE);

        } else if (accessCode.length() == 3) {
            img_one.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_two.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_three.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_four.setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
            img_one.setVisibility(View.VISIBLE);
            img_two.setVisibility(View.VISIBLE);
            img_three.setVisibility(View.VISIBLE);
            img_four.setVisibility(View.VISIBLE);

        } else if (accessCode.length() == 4) {
            img_one.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_two.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_three.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_four.setImageDrawable(getResources().getDrawable(R.drawable.dot_red));
            img_one.setVisibility(View.VISIBLE);
            img_two.setVisibility(View.VISIBLE);
            img_three.setVisibility(View.VISIBLE);
            img_four.setVisibility(View.VISIBLE);

        }

        if (is_admin_mode && this.accessCode.length() == USER_PIN_MAX_CHAR) {

            Calendar cal = Calendar.getInstance();
            String m = StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 3), 2, "0");
            String d = StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH) + 2), 2, "0");
            m = m.replace("0", "1");
            d = d.replace("0", "1");


            if (this.accessCode.trim().equals("7777")) {

                resetPinPad();

                showCustomDialog("Updating", "Refreshing User List", false);

                get_update_users();

            } else if (this.accessCode.equals(m + d)) {

                resetPinPad();
                do_show_enter_setup();

            } else {

                resetPinPad();
                Toasty.error(mContext, "Incorrect Admin PIN", 2000, true).show();

                is_admin_mode = false;
                if (Topitup.DEBUG) {
                    mIsDemo.setText("DEMO");
                } else {
                    mIsDemo.setVisibility(View.INVISIBLE);
                }
            }


        } else if (this.accessCode.length() == USER_PIN_MAX_CHAR) {
            user = realm.where(pos_users.class).equalTo("posuser_pin", accessCode).equalTo("posuser_status", 1).findFirst();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("loginPin", accessCode);
            editor.commit();
            Log.e("access code", user + "access.........." + accessCode);

            checkLogin(this.accessCode);

        }
    }

    private void crossFade(int animTimeIn, TextView textViewIn, String valueStringIn) {

        textViewIn.setText(valueStringIn);
        textViewIn.setAlpha(0f);
        textViewIn.setVisibility(View.VISIBLE);

        textViewIn.animate().alpha(1f).setDuration(animTimeIn)
                .setListener(null);
    }

    @Override
    public boolean onTouch(View vIn, MotionEvent eventIn) {
       /* switch (vIn.getId()) {
            case  com.za.toptitup.loginlibrary.R.id.one_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.two_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.three_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.four_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.five_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.six_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.seven_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.eight_button:
                toggleNumberColor(vIn, eventIn);
                break;
            case  com.za.toptitup.loginlibrary.R.id.nine_button:
                toggleNumberColor(vIn, eventIn);
                break;
//            case  com.za.toptitup.loginlibrary.R.id.zero_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
        }*/
        if (vIn.getId() == com.za.toptitup.loginlibrary.R.id.one_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.two_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.three_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.four_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.five_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.six_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.seven_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.eight_button ||
                vIn.getId() == com.za.toptitup.loginlibrary.R.id.nine_button) {
            toggleNumberColor(vIn, eventIn);
        }

        return false;
    }

    private void toggleNumberColor(View viewIn, MotionEvent eventIn) {
        textLastSale.setVisibility(View.GONE);
        if (eventIn.getAction() == MotionEvent.ACTION_DOWN) {
            ((TextView) viewIn).setTextColor(getResources().getColor(R.color.white));
        } else if (eventIn.getAction() == MotionEvent.ACTION_UP) {
            ((TextView) viewIn).setTextColor(Color.parseColor("#811d1d"));
        }
    }


    private void animateLoginButtonInOut(boolean animateIn) {
        if (animateIn) {
            //this.mLoginButton.setVisibility(View.VISIBLE);
            //this.mLoginButton.startAnimation(this.mAnimSlideIn);
        } else {
            this.mLoginProgress.setVisibility(View.GONE);
            //this.mLoginButton.startAnimation(this.mAnimSlideOut);
            //this.mLoginButton.setVisibility(View.GONE);
            //this.mLoginButton.setText("Login");
        }
    }


    private void checkLogin(String password) {
//        this.accessCode = "0";
        keyPadLockedFlag = true;
        //RealmResults<pos_users> pos_users;

        if (null == user) {
            txt_invalid.setVisibility(View.VISIBLE);
            resetPinPad();
        } else {
         /*   try {
                if (batteryReceiver != null) {
                    unregisterReceiver(batteryReceiver);
                    batteryReceiver = null;
                }
            } catch (Exception e) {
            }*/
            mLoginProgress.setVisibility(View.VISIBLE);
            Topitup.POSUSER_ID = String.valueOf(user.posuser_id);
            Topitup.IS_ADMIN = String.valueOf(user.posuser_isadmin);
            Topitup.POSUSER_NAME = user.posuser_firstname + " " + user.posuser_surname;
            Topitup.RICA_REG = user.rica_registered;
            keyPadLockedFlag = false;
            get_swipe_realtime();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoginProgress.setVisibility(View.GONE);
                }
            }, 500);
            openAppWebView(activity_login.this);

            //

        }
    }


    private void get_update_users() {


        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>() {

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Timber.e(response.body());
                //Toasty.error(mContext, response.toString(), 8000, true).show();

                if (response.isSuccessful()) {

                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    String res = "";
                    try {

                        List<pos_users> result = response.body();

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(result);
                        realm.commitTransaction();

                        //res = response.body().string();

                    } catch (Exception ex) {

                        //
                        response.body().clear();
                    }

                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                Log.e("error", "............" + t.getMessage());
                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    private void get_approved_licence(String SERVERSHARED) {
      /*  String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
//        final Call<JsonObject> call = apiService.get_approve_licence("DEMO99c4-1999-11e9-84ad-001e6779cd30", "PP35272137000044");
        final Call<JsonObject> call = apiService.get_approve_licence(Topitup.DEVICE_SLNO);
//        final Call<JsonObject> call = apiService.get_approve_licence("PP35272250000568");
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if (response.isSuccessful()) {
                    String device_id = response.body().get("device_id").toString().replace("\"", "");
                    String licence = response.body().get("secret_pin").toString().replace("\"", "");

                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_SERVER", "");
                    editor.putString("TIU_LICENSE", "");
                    // editor.putString("TIU_SERVER", "DEMO");
                    editor.commit();
                    Realm realm = Realm.getDefaultInstance();

                    try {
                        realm.executeTransactionAsync(
                                new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.delete(pos_users.class);
                                        realm.delete(GetUpdateAll.class);
                                    }
                                },
                                new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Toasty.success(mContext, "Database cleared successfully", 3000, true).show();
                                    }
                                },
                                new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        error.printStackTrace();
                                        Toasty.error(mContext, "Could not clear database", 3000, true).show();
                                    }
                                }
                        );

                    } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                        ex.printStackTrace();

                        Toasty.error(mContext, "Could not clear database", 3000, true).show();
                    }


                    get_setup_information(licence, device_id, SERVERSHARED);
                }


                //tech_cell_number = tv_cell_number.getText().toString();

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    private void checkLoginLive(String password) {

        keyPadLockedFlag = true;


        Call<pos_user_current> call = apiService.login(Topitup.TIU_LICENSE, Topitup.TIU_LICENSE, password);
        call.enqueue(new Callback<pos_user_current>() {

            @Override
            public void onResponse(Call<pos_user_current> call, Response<pos_user_current> response) {


                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }


                    try {

                        pos_user_current pu = response.body(); // new Gson().fromJson(res, pos_user_current.class);


                        if (pu.error_code.equals("1")) {

                            Toasty.error(mContext, pu.error_desc, 3000, true).show();
                            resetPinPad();

                        } else if (pu.posuser_id.equals("0")) {
                            txt_invalid.setVisibility(View.VISIBLE);

//                        Toasty.error(mContext, "Invalid PIN #", 3000, true).show();
                            resetPinPad();

                        } else {
                            try {
                                if (batteryReceiver != null) {
                                    unregisterReceiver(batteryReceiver);
                                    batteryReceiver = null;

                                }
                            } catch (Exception e) {

                            }
                            Topitup.POSUSER_ID = pu.posuser_id;

                            Topitup.IS_ADMIN = pu.is_admin;
                            Topitup.POSUSER_NAME = pu.posuser_name;

                         /*   Intent i = new Intent(activity_login.this, activity_main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);*/
                            //  finish();

                            keyPadLockedFlag = false;

                        }


                    } catch (Exception ex) {

                      /*  if (response.raw() != null)
                            response.raw().close();*/
                        showCustomDialog("Could Not Log In", ex.getMessage(), true);
                        resetPinPad();

                    }
                } else {
                    showCustomDialog("Connection Issue", "Timeout, please check internet connection.", true);
                    resetPinPad();
                }


            }

            @Override
            public void onFailure(Call<pos_user_current> call, Throwable t) {

                if (t instanceof SocketTimeoutException) {
                    showCustomDialog("Connection Issue", "Timeout, please check internet connection.", true);
                } else {

                    showCustomDialog("Could Not Log In", t.getMessage(), true);
                }

                //Toasty.error(mContext, "Problem loggin in.", 3000, true).show();
                //Timber.e("Login error: " + t.getMessage());
                //"Invalid pin #"

                resetPinPad();
            }
        });


    }


    private void resetPinPad() {
        mFailedLogin = true;
        animateLoginButtonInOut(false);
        accessCode = "";

        imagesInvisible();
        keyPadLockedFlag = false;
        new LockKeyPadOperation().execute("");
    }

    public void imagesInvisible() {
        clearButton.setVisibility(View.GONE);

        img_one.setVisibility(View.GONE);
        img_two.setVisibility(View.GONE);
        img_three.setVisibility(View.GONE);
        img_four.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_pin_entry_view, menu);
        return true;
    }

    @Override
    protected void onPause() {
      /*  try {
            if (batteryReceiver != null) {
                unregisterReceiver(batteryReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }*/
        // mLoginProgress.setVisibility(View.GONE);
   /*     // when the screen is about to turn off
        Log.e("MYAPP", "onpause="+wasScreenOn);
        if (wasScreenOn) {
           // wasScreenOn=false;
            // this is the case when onPause() is called by the system due to a screen state change
            Log.e("MYAPP", "SCREEN TURNED OFF");
        } else {
            // this is when onPause() is called when the screen state has not changed
        }*/
        if (isSlide) {
            handlerSlide.removeCallbacks(myRunnable);
        }
        super.onPause();
    }


//
//
//
//    @Override
//    public void onConnected(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//        //setting.mposWriteSN("82320180810001");
//
//        Timber.i("PRINT: onConnected SN:" + arg0.toString() );
//
//    }
//
//    @Override
//    public void onDisconnected(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//
//    }

    /// /
    /// /
    /// /
//    @Override
//    public void onParseData(SwipeEvent event) {
//
//        Timber.i("PRINT: onParseData " + event.getValue() );
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onCardDetect(CardDetected type) {
//        //
//    }
//
//
//
//    @Override
//    public void onPrintStatus(PrintStatus arg0) {
//
//        Timber.i("PRINT: onPrintStatus SN:" + arg0.toString() );
//
//        if (arg0 == PrintStatus.IMAGES) {
//
//        } else if (arg0 == PrintStatus.EXIT) {
//            // setting.mPosExitPrint();
//            // new Thread(new Runnable() {
//            // @Override
//            // public void run() {
//            // // TODO Auto-generated method stub
//            // setting.prnStatus();
//            // }
//            // }).start();
//        } else if (arg0 == PrintStatus.NO_PAPER) {
//
//        } else if (arg0 == PrintStatus.LACK_PAPER) {
//
//        }
//
//    }
//
//
//
//    @Override
//    public void onEmvStatus(EmvStatus arg0) {
//    }


//    public void printerInit() {
//        ThreadPoolManager.getInstance().executeTask(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mIPosPrinterService.printerInit(callback);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
    @Override
    protected void onResume() {
        //Log.d(TAG, "activity onResume");
        if (!Topitup.TIU_LICENSE.equalsIgnoreCase("")) {
            get_balance();
        } else {
            update_balance();
        }
        FullscreenCall();
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        setting_pockepos_open = settings.getString("setting_pockepos_open", "0");
        setting_screen_off = settings.getString("setting_screen_off", "0");
        editor.putString("setting_dnp", "0");
        editor.commit();

     /*   Animation translatebu= AnimationUtils.loadAnimation(this, R.anim.animscroll);
//        tv.setText("Some text view.");
        translatebu.setRepeatCount(Animation.INFINITE);
        translatebu.setRepeatMode(Animation.RESTART);

        horizantal_scroll.startAnimation(translatebu);*/


      /*  handlerSlide = new Handler();


        final int delay = 6500; // 1000 milliseconds == 1 second

        handlerSlide.postDelayed( myRunnable = new Runnable() {
            public void run() {
                horizantal_scroll.startAnimation(inFromRightAnimation());
                isSlide = true;
                handlerSlide.postDelayed(myRunnable, delay);
            }
        }, delay);*/

        //Log.e("MYAPP", "="+wasScreenOn);
     /*   setting_chk_auto_mpos=settings.getString("setting_chk_auto_mpos", "0");
            // this is when onResume() is called due to a screen state change
           // Log.e("MYAPP", "SCREEN TURNED ON");
        stopRunning=false;
        if(Topitup.getInstance().checkConnection(getApplicationContext())) {

            Log.e("MYAPP", "onresume="+wasScreenOn);
            if (!wasScreenOn) {
                // this is when onResume() is called due to a screen state change
                Log.e("MYAPP", "SCREEN TURNED ON");

                if(Topitup.wasInBackground == true){
                    checkPrintMPOSslip(false);
                }

                if (Topitup.TIU_LICENSE != "" && (setting_pockepos_open.equals("1"))) {
                    showCustomDialog("Loading Swipe Viewer", "Please Wait",false);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog!=null)
                                dialog.dismiss();
                        }
                    },5000L);
                    checkPrintMPOSslip(true);
                }
            } else {
                // this is when onResume() is called when the screen state has not changed


            }

        }*/
        // else{

        //  showCustomDialog("Could Not Connect","Please check your internet connection and try again",true);

        //}
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }


        try {
            if (batteryReceiver != null) {
                unregisterReceiver(batteryReceiver);
                batteryReceiver = null;

            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.

//        loopPrintFlag = DEFAULT_LOOP_PRINT;
//        unregisterReceiver(IPosPrinterStatusListener);
//        unbindService(connectService);

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
                editor.putString("setting_dnp", "1");
                editor.commit();
                dialogp.dismiss();
                dialog.dismiss();


            }
        });
        btn_customer_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  PrinterTopitup.print_data(cslip);
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
                //   PrinterTopitup.print_data(mslip);
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

        // PrinterTopitup.print_data(cslip);

        showCustomDialog("Printing", "Printing Merchant Copy!!!", false);
        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //PrinterTopitup.print_data(mslip);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

            }
        }, 8000L);
    }


    public void get_balance() {

        final Call<fin_balance> call = apiService.get_balance_new(Topitup.TIU_LICENSE, "1");

        call.enqueue(new Callback<fin_balance>() {

            @Override
            public void onResponse(Call<fin_balance> call, Response<fin_balance> response) {


                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {

                        fin_balance res = response.body();

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(res);
                        realm.commitTransaction();
                        update_balance();
                    } catch (Exception ex) {

                        //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();
                     /*   if (response.raw() != null)
                            response.raw().close();*/
                    }
                }


            }

            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {


            }

        });


    }


    private void resetMPOSDia() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("setting_pockepos_open", "0");
        try {
            if (isRunning)
                cntdwnTimer.cancel();
        } catch (Exception r) {
            r.printStackTrace();
        }
        editor.commit();
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

    public void clear(View v) {
        if (v.getId() == R.id.simpleImageView) {
            resetPinPad();
        }

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
                    // response.body().close();
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
                        asset_serial = reader.getString("asset_serial");
                        merchant_no = reader.getString("merchant_no");
                        terminal_no = reader.getString("terminal_no");
                        double min_swipe = reader.getDouble("min_swipe");
                        double max_swipe = reader.getDouble("max_swipe");
                        double warning_swipe = reader.getDouble("warning_swipe");
                        int retailer_type = reader.getInt("retailer_type");
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


                        String enable_commission = "1";
                        if (!reader.isNull("enable_commission")) {
                            enable_commission = reader.getString("enable_commission");
                        } else {
                            enable_commission = "1";
                        }
                        String cash_up = "cashup";
                        if (!reader.isNull("cashup")) {
                            cash_up = reader.getString("cashup");
                        } else {
                            cash_up = "cashup";
                        }
                        String st_status = "1";
                        if (!reader.isNull("st_status")) {
                            st_status = reader.getString("st_status");
                        } else {
                            st_status = "0";
                        }

                        Log.e("st_status", "login screen" + st_status);

                        Topitup.ENABLE_COMMISSION = enable_commission;
                        Topitup.ONE_MAX_THRESHOLD = one_max_value;
                        Topitup.ONE_WARNING_THRESHOLD = one_warning_value;
                        Topitup.RETAILER_TYPE = retailer_type;
                        Topitup.cashUp = cash_up;
                        Topitup.ST_STATUS = st_status;


                        Topitup.BLUE_MAX_THRESHOLD = blue_max_value;
                        Topitup.BLUE_WARNING_THRESHOLD = blue_warning_value;


                        Topitup.min_swipe = min_swipe;
                        Topitup.max_swipe = max_swipe;
                        Topitup.warning_swipe = warning_swipe;


                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();


                        // enable_realtime_swipe = res_realtime;

                        editor.putString("enable_realtime_swipe", enable_realtime_swipe);
                        editor.putString("asset_serial", asset_serial);
                        editor.putString("merchant_no", merchant_no);
                        editor.putString("terminal_no", terminal_no);
                        editor.putString("enable_commission", enable_commission);

                        editor.commit();
                        double warning_electra = reader.getDouble("warning_electra");
                        Topitup.warning_electra = warning_electra;
                    } catch (JSONException e) {
                        Topitup.min_swipe = 1.00;
                        Topitup.max_swipe = 100000.00;
                        Topitup.warning_swipe = 5000.00;
                        Topitup.warning_electra = 2000.00;
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

    private class HiddenPassTransformationMethod implements TransformationMethod {

        private final char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i, final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }

    private class LockKeyPadOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            keyPadLockedFlag = false;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class ScreenReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // do whatever you need to do here
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // and do whatever you need to do here
                wasScreenOn = true;
            }
        }

    }

    /*
        public void hideKeyboard() {
            try {


                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            }
            catch (Exception var2) {
            }

           */
/* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*//*

    }
*/
    private void openAppWebView(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(
                    new ComponentName(
                            "com.za.toptitup.retailerapp",
                            "com.za.toptitup.retailerapp.MainWebViewActivity"
                    )
            );
            // ✅ ADD EXTRAS
            intent.putExtra("LICENSE", Topitup.TIU_LICENSE); // leave empty, app decides URL
            intent.putExtra("POS_USER_ID", Topitup.POSUSER_ID);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}