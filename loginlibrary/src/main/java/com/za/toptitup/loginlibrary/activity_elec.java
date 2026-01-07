package com.za.toptitup.loginlibrary;

import static java.lang.Math.round;
import static com.za.toptitup.loginlibrary.utils.Topitup.getAppContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.admin.activity_settings;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;


public class activity_elec extends BaseActivity {

    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static activity_elec instance;
    public boolean isFromScreenPrint = false;
    Context mContext;
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    TextInputEditText txtAccountNumber;
    TextView tv_response;
    TextView tv_value;
    EditText txtPaymentAmount;
    TextInputEditText amntEditText, amntEditText_cent;
    TextView tv_notice;
    LinearLayout ll_amount_wrapper;
    Button freetoken;
    com.makeramen.roundedimageview.RoundedImageView prov_12;
    RealmResults<fin_balance> tiu_fin_balance;
    int process_step;
    int mqty = 1;
    double value = 0.00d;
    int printerQ1Sts;
    //Keyboard mKeyboard;
    //KeyboardView mKeyboardView;
    boolean warning_process = true;
    LinearLayout tiu_title_bar_new;
    boolean is_busy_with_voucher = false;
    Integer current_voucher = 1;
    String _elecVoucher = "";
    String _elecVoucherStockUid = "";
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;
    Dialog dialog;
    String company_name, account_number;

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
            } else {
                finish();
            }
        }
    };
    SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);
    private TextView txt_cent;
    private TextView txt_rand;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    private Button btn_next, btn_clear;
    private String elecAmount;
    private MoneyTextWatcherRand moneyTextWatcher;
    private MoneyTextWatcherCent moneyTextWatcherCent;
    private String is_admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        instance = this;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_elec);

        freetoken = findViewById(R.id.freetoken);


//        // Create the Keyboard
//        mKeyboard= new Keyboard(this, R.xml.qwerty);
//
//        // Lookup the KeyboardView
//        mKeyboardView = (KeyboardView)findViewById(R.id.keyboard);
//        // Attach the keyboard to the view
//        mKeyboardView.setKeyboard( mKeyboard );
//
//        // Do not show the preview balloons
//        //mKeyboardView.setPreviewEnabled(false);
//
//        // Install the key handler
//        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
//
//      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FullscreenCall();


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
        activity_login.fromScreen = "activity_elec";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);

        final TextView tiu_clock = findViewById(R.id.tiu_clock);


        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        ((Topitup) getApplication()).checkWifiSimInternet(this);

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
        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

//        Topitup.POSUSER_ID = null;

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);


        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        account_number = tiu_settings.account_number;
        company_name = tiu_settings.company_name;
        tiu_title_outlet.setText(tiu_settings.account_number);

   /*     if(tiu_settings.company_name.length()>10){
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0,10) + "... | " + tiu_settings.account_number);
        }
        else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "...");
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "... ");
                tiu_user_name_.setText("Cashier");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Cashier");

            }
        }

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

           /* tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                    }

                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        view2.setVisibility(VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

                    }
                    if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                    }
                    if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                        view3.setVisibility(VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }
                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
            } else {
                tiu_title_balance.setVisibility(GONE);
                tiu_title_balance_cash.setVisibility(GONE);
            }
            if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                view2.setVisibility(VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

            }
            if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                view3.setVisibility(VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            }*/
            String setting_balance_admin = settings.getString("setting_balance_admin", "0");

            String setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
            String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
            String  setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");



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

                            view2.setVisibility(View.VISIBLE);
                            tiu_title_balance_cash.setText("Bills  R  " + tiu_fin_balance.get(0).balance_cash);
                        }
                        if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {
                            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                        }
                        if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                            view3.setVisibility(View.VISIBLE);
                            tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                        }

                        if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                            tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
                            tiu_title_balance.setVisibility(View.VISIBLE);
                        }

                        if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                            view2.setVisibility(View.VISIBLE);
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                            tiu_title_balance_cash.setVisibility(View.VISIBLE);
                        }
                        if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                            tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
                        }

                        if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
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
                    tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);

//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
               /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);*/
                    tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
                }


                if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                    view2.setVisibility(View.VISIBLE);
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    tiu_title_balance_cash.setVisibility(View.VISIBLE);


                }
                if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {
                    tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                }
                if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                    view3.setVisibility(View.VISIBLE);
                    tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                }

                if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                    tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance);
                    tiu_title_balance.setVisibility(View.VISIBLE);
                }

                if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {

                    view2.setVisibility(View.VISIBLE);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    tiu_title_balance_cash.setVisibility(View.VISIBLE);
                }
                if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                    view3.setVisibility(View.VISIBLE);
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
        /* END HEADER */


        ll_amount_wrapper = findViewById(R.id.ll_amount_wrapper);

        txtAccountNumber = findViewById(R.id.txtAccountNumber);
        tv_response = findViewById(R.id.tv_response);

       /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        //txtAccountNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        txtAccountNumber.requestFocus();*/

        prov_12 = findViewById(R.id.prov_12);

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(txtAccountNumber, InputMethodManager.SHOW_IMPLICIT);

//txtAccountNumber.setText("06668014423");

        tv_value = findViewById(R.id.tv_value);
        txtPaymentAmount = findViewById(R.id.txtPaymentAmount);

        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);

        txt_rand = findViewById(R.id.txt_rand);
        txt_cent = findViewById(R.id.txt_cent);
        //tv_value.setVisibility(View.GONE);
//        txtPaymentAmount.setVisibility(View.GONE);
        ll_amount_wrapper.setVisibility(View.GONE);
        cent_value_entered = "";
        rand_value_entered = "";
        moneyTextWatcher = new MoneyTextWatcherRand(amntEditText);
        moneyTextWatcherCent = new MoneyTextWatcherCent(amntEditText_cent);

        amntEditText.addTextChangedListener(moneyTextWatcher);
        amntEditText_cent.addTextChangedListener(moneyTextWatcherCent);

        tv_notice = findViewById(R.id.tv_notice);


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dumm1);
        mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

             /*   switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;
                    case R.id.action_logout:

                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent2);
                        return true;

                    case R.id.action_clear:

                        if (process_step == 2) return true;

                        txtAccountNumber.setText("");
                        txtPaymentAmount.setText("");

                        amntEditText.setText("");
                        amntEditText_cent.setText("");

                        txtAccountNumber.requestFocus();
                        freetoken.setVisibility(View.GONE);
                        btn_next.setText("Next");
                        process_step = 0;

                        txtAccountNumber.setEnabled(true);
                        txtPaymentAmount.setEnabled(true);

                        amntEditText.setEnabled(true);
                        amntEditText_cent.setEnabled(true);

                        tv_response.setText("");

                        return true;
                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    if (process_step == 2) return true;

                    txtAccountNumber.setText("");
                    txtPaymentAmount.setText("");

                    amntEditText.setText("");
                    amntEditText_cent.setText("");

                    txtAccountNumber.requestFocus();
                    freetoken.setVisibility(View.GONE);
                    btn_next.setText("Next");
                    process_step = 0;

                    txtAccountNumber.setEnabled(true);
                    txtPaymentAmount.setEnabled(true);

                    amntEditText.setEnabled(true);
                    amntEditText_cent.setEnabled(true);

                    tv_response.setText("");

                    return true;
                } else {
                    return true;
                }

            }
            /*
                            else if (item.getItemId() == R.id.action_clear) {
                    if (process_step == 2) return true;

                    txtAccountNumber.setText("");
                    txtPaymentAmount.setText("");

                    amntEditText.setText("");
                    amntEditText_cent.setText("");

                    txtAccountNumber.requestFocus();
                    freetoken.setVisibility(View.GONE);
                    btn_next.setText("Next");
                    process_step = 0;

                    txtAccountNumber.setEnabled(true);
                    txtPaymentAmount.setEnabled(true);

                    amntEditText.setEnabled(true);
                    amntEditText_cent.setEnabled(true);

                    tv_response.setText("");

                    return true;
                }
             */

        });

        process_step = 0;

        btn_next = findViewById(R.id.btn_next);
        btn_clear = findViewById(R.id.btn_clear);
        btn_next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!Topitup.checkConnection(getApplicationContext())) {
                   /* String htmlslno = "No internet Connection. Please contact Top it Up Customer Services on 0860 111 723 or Whatsapp on 064 121 9970";

                    showCustomDialog("Error", htmlslno, true);
*/
                    showNoInternetPopup();

                } else {
                    btn_next.setEnabled(false);

                    tv_notice.setVisibility(View.GONE);

                    txtAccountNumber.setEnabled(false);
                    txtPaymentAmount.setEnabled(false);

                    amntEditText.setEnabled(false);
                    amntEditText_cent.setEnabled(false);


                    try {


                        String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();


                        value = Double.parseDouble(amountEnter);

                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        elecAmount = decimalFormat.format(value).replace(",", ".");

                        value = round(value * 100);

//                    value = Double.parseDouble(txtPaymentAmount.getText().toString());
                    } catch (Exception ex) {
                        value = 0;
                    }


                    //Toasty.error(mContext, "hello", 5000, true).show();
                    if (process_step == 0) {

                        prov_12.setVisibility(View.GONE);

                        get_customer_information(txtAccountNumber.getText().toString(), String.valueOf((int) value));

                        btn_next.setEnabled(true);


                        // alertDialog.hide();

                    }

                    if (process_step == 1) {


                        if (value == 0) {

                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Incorret Amount!");
                            alertDialog.setMessage("Amount cannot be R 0.00");
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                            txtPaymentAmount.requestFocus();
                            txtAccountNumber.setEnabled(true);
                            txtPaymentAmount.setEnabled(true);

                            amntEditText.setEnabled(true);
                            amntEditText_cent.setEnabled(true);


                            btn_next.setEnabled(true);

                            return;
                        }
//2000>=3000
                        if (Topitup.warning_electra <= value / 100) {
                            if (warning_process) {
                                warning_electra();
                                txtPaymentAmount.requestFocus();
                                txtAccountNumber.setEnabled(true);
                                txtPaymentAmount.setEnabled(true);
                                amntEditText.setEnabled(true);
                                amntEditText_cent.setEnabled(true);


                                btn_next.setEnabled(true);
                                return;
                            }
                        }

                        showCustomDialog("Requesting Voucher", "Processing...", false);

//                    if (!Printer.check_paper()) {
//                        showCustomDialog("Out of Paper","Please check paper and try again.", true);
//                        return;
//                    }

                        btn_next.setVisibility(View.INVISIBLE);
                        current_voucher = 1;

                        if (mqty > 1) {

                            pop_title.setText("Requesting Voucher (" + current_voucher + " of " + mqty + ")");
                            do_payment(txtAccountNumber.getText().toString(), String.valueOf((int) value));

                        } else {

                            do_payment(txtAccountNumber.getText().toString(), String.valueOf((int) value));

                        }


                    }

                }
            }
        });


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (process_step == 2) return true;

                txtAccountNumber.setText("");
                txtPaymentAmount.setText("");

                amntEditText.setText("");
                amntEditText_cent.setText("");

                txtAccountNumber.requestFocus();
                freetoken.setVisibility(View.GONE);
                btn_next.setText("Next");
                process_step = 0;

                txtAccountNumber.setEnabled(true);
                txtPaymentAmount.setEnabled(true);

                amntEditText.setEnabled(true);
                amntEditText_cent.setEnabled(true);

                tv_response.setText("");

//                return true;
            }
        });

    }


    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_elec.rl_network.setVisibility(View.VISIBLE);
                    activity_elec.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_elec.rl_network.setVisibility(View.INVISIBLE);
                    activity_elec.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_elec.rl_network.setVisibility(View.INVISIBLE);
                    activity_elec.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void showNoInternetPopup() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
        bt_close = dialog.findViewById(R.id.bt_close);
        bt_close.setOnClickListener(new View.OnClickListener() {
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

    public void onclick_btn_provider(View v) {


        //Toasty.error(mContext, v.getTag().toString(), 1000, true).show();
        //Printer.print_data(txt, setting, handler, mContext);

        if (v.getTag().toString().equals("prov_unipin")) {
            Intent myIntent = new Intent(mContext, activity_spi_old.class);
            myIntent.putExtra("service_provider_id", 12);
            startActivity(myIntent);

            finish();
        }

    }

    public void getfreetokens(View view) {

        if (Topitup.POSUSER_ID == null || Topitup.POSUSER_ID.trim().isEmpty()) {
            showLoginAlert(activity_elec.this);
            return;
        }

        showCustomDialog("Electricity", "Requesting Free Tokens", false);

        final Call<ResponseBody> call = apiService.requestFreeToken(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.TIU_LICENSE, "0", txtAccountNumber.getText().toString(), Topitup.POSUSER_ID, "", "0");

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }


                //Timber.e(response.body());
                String res = "";
                try {

                    res = response.body().string();
                    //Printer.print_data(res);
                } catch (Exception ex) {
                    //
                    if (response.body() != null)
                        response.body().close();

                }/*  */

                if (res.contains("<error><err>") || res.contains("ERR:")) {

                    String matcher = "";
                    matcher = res;
                    if (res.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    tv_response.setTextColor(Color.parseColor("#ff0000"));
                    tv_response.setText(matcher);

                    txtAccountNumber.setEnabled(true);
                    txtAccountNumber.requestFocus();

                    //txtPaymentAmount.setEnabled(true);
                    //tv_response.setVisibility(View.VISIBLE);

                    dialog.dismiss();

                    //stopCustomDialog("Problem",matcher);

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    try {


                        String[] array = res.split("=");

                        String printdata = array[2].substring(1);


                        PrinterTopitup.print_data(printdata);


                    } catch (Exception ex) {

                        Timber.i(ex.getMessage());

                    }


                }

                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    //  stopCustomDialog("No Internet","Please check that your internet connection is working.");
                    // stopCustomDialog("No Internet","Please check that your internet connection is working.");
                    // stopCustomDialog("Error","Invalid Meter # Please contact your Municipality or Private Utility");
                    stopCustomDialog("Error", "There was a problemrequesting Free Tokens");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }


                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

            }

        });


    }

    public void increaseInteger(View view) {
        mqty = mqty + 1;
        if (mqty > 9) mqty = 9;
        qty_display(mqty);

    }

    public void decreaseInteger(View view) {
        mqty = mqty - 1;
        if (mqty < 1) mqty = 1;
        qty_display(mqty);
    }

    private void qty_display(int number) {
        TextView displayInteger = findViewById(R.id.voucher_qty);
        displayInteger.setText("" + number);
    }

    public void get_customer_information(String pass_meter_number, String pass_value) {

        if (Topitup.POSUSER_ID == null || Topitup.POSUSER_ID.trim().isEmpty()) {
            showLoginAlert(activity_elec.this);
            return;
        }

        is_busy_with_voucher = true;
        current_voucher = 1;

        showCustomDialog("Electricity", "Requesting Customer Information", false);

        final Call<ResponseBody> call = apiService.elec_get_custinfo(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.TIU_LICENSE, pass_meter_number);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 200) {

                        //Timber.e(response.body());
                        String res = "";
                        try {

                            res = response.body().string();

                        } catch (Exception ex) {
                            //
                            if (response.body() != null)
                                response.body().close();
                        }

                        if (res.contains("<error><err>") || res.contains("ERR:")) {

                            String matcher = "";
                            matcher = res;
                            if (res.contains("<err>")) {
                                matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                            }
                            matcher = matcher.replace("ERR:", "");

                            tv_response.setTextColor(Color.parseColor("#ff0000"));
                            tv_response.setText(matcher);

                            txtAccountNumber.setEnabled(true);
                            txtAccountNumber.requestFocus();

                            //txtPaymentAmount.setEnabled(true);
                            //tv_response.setVisibility(View.VISIBLE);

                            dialog.dismiss();

                            //stopCustomDialog("Problem",matcher);

                            //Toasty.info(mContext, matcher, 8000, true).show();

                        } else {        //Response OK
                            if (!response.headers().get("Server").equals("TIU")) {
                                Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                                return;
                            }

                            tv_response.setTextColor(Color.parseColor("#3c3c3c"));

                            //res = res.replace("^","\n");


                            String meter_details = "";
                            try {
                                //char[] del = new char[] { '=', '^' };
                                //String[] tokens = res.Split(del);

                                String strName = "";
                                String strAddress = "";
                                String strUtility = "";
                                String strExtra = "";
                                String strPending = "";
                                String strMaxVend = "";

                                //String[]tokens = pdfName.split("-|\\.");

                                String[] tokens = res.split("\\^");

                                //utility=COCT POS^address=6 ROSE CRT,BLACKBIRD AVE^name=WILLIAMS^p=0^f=0^extra=^max=R 500^tov=-1^fc=FFFFFF^bc=B22222

                                for (int token = 0; token < tokens.length; token++) {

                                    //Timber.i("ELEC: 1 = " + tokens[token]);

                                    String s1 = "";
                                    String s2 = "";
                                    try {
                                        s1 = tokens[token].split("=")[0];
                                        s2 = tokens[token].split("=")[1];
                                    } catch (Exception ex) {
                                        //
                                    }

                                    if (s1.equals("name")) strName = s2;
                                    if (s1.equals("address")) strAddress = s2;
                                    if (s1.equals("utility")) strUtility = s2;
                                    if (s1.equals("extra")) strExtra = s2;
//                            if (s1.equals("p"))          strPending = s2;
//                            if (s1.equals("max"))        strMaxVend = s2;

                                }

                                String result_name = strName.replace("\r\n", " ");
                                String result_addr = strAddress.replace("\r\n", " ");
                                String extra = strExtra.replace("\r\n", " ");
                                result_name = result_name.replace("\n", " ");
                                result_addr = result_addr.replace("\n", " ");
                                extra = extra.replace("\n", " ");

                                //Timber.i("ELEC: " + strUtility);
                                if (extra.length() > 0) {
                                    int index = extra.indexOf("R") + 1;
                                   /* String finalString = extra.substring(0, index);
                                    String afterremove = extra.substring(index);
                                    String[] parts1 = afterremove.split("\\.");
                                    String beforeR = parts1[0];
                                    String afterR = parts1[1];

                                    *//*String beforeR = extra.substring(0, index); // "Outstanding Charges due "
                                    String afterR = extra.substring(index);*//*
//                                    amntEditText.setEnabled(true);
                                    amntEditText.setText(beforeR);
                                    if(afterR.equals("0")){
                                        afterR ="00";
                                    }
                                    amntEditText_cent.setText(afterR);
                                    Log.e("electricity","........rand....."+beforeR);

//                                 */
                                    String[] parts = extra.split("R\\s*"); // Splits at "R " or "R" followed by optional spaces

                                    if (parts.length > 1) {
                                        String numberStr = parts[1].trim(); // Gets "1"

                                        try {

                                            double amount = Double.parseDouble(numberStr);

                                            extra = String.format(Locale.US, parts[0] + "R %.2f", amount);


                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    // meter_details = "Utility: " + strUtility + "\nCustomer: " + result_name + "\nAddress: " + result_addr+ "\nDue: " + extra;
                                    meter_details = "Utility: " + strUtility + "<br>" +
                                            "Customer: " + result_name + "<br>" +
                                            "Address: " + result_addr + "<br>" +
                                            "<font color='red'><big><b>" + extra + "</b></big></font>";
                                    tv_response.setText(Html.fromHtml(meter_details));
                                } else {
                                    amntEditText.setText("");

                                    meter_details = "Utility: " + strUtility + "\nCustomer: " + result_name + "\nAddress: " + result_addr;
                                    tv_response.setText(meter_details);

                                }

                                freetoken.setVisibility(View.VISIBLE);
                                //Timber.i("ELEC: " + meter_details);

                            } catch (Exception ex) {

                                ex.printStackTrace();
                                Timber.i(ex.getMessage());

                            }


                            //tv_response.setText(meter_details);
                            //tv_response.setText(Html.fromHtml(meter_details));

                            btn_next.setText("Process");

                            //tv_value.setVisibility(View.VISIBLE);
                            //txtPaymentAmount.setVisibility(View.VISIBLE);
                            ll_amount_wrapper.setVisibility(View.VISIBLE);

                            txtPaymentAmount.setEnabled(true);
                            amntEditText.setEnabled(true);
                            amntEditText_cent.setEnabled(true);


                            txtPaymentAmount.requestFocus();

                            process_step = 1;

                        }

                        dialog.dismiss();

                        //Toasty.info(mContext, res, 8000, true).show();

                    } else {
                        stopCustomDialog("Error", "There was a problem retrieving the address details.Please try again later.");

                    }
                } catch (Exception e) {
                    if (response.body() != null)
                        response.body().close();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    //  stopCustomDialog("No Internet","Please check that your internet connection is working.");
                    // stopCustomDialog("No Internet","Please check that your internet connection is working.");
                    // stopCustomDialog("Error","Invalid Meter # Please contact your Municipality or Private Utility");
                    stopCustomDialog("Error", "There was a problem retrieving the address details.Please try again later.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

                is_busy_with_voucher = false;

                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

            }

        });


    }

    public void showUsbNotConnectedDialog(Context conn, String meter_number, String pass_value) {
        Dialog dialog = new Dialog(conn);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.usb_not_connected);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        LinearLayout ll_buttons = dialog.findViewById(R.id.ll_buttons);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);

        TextView txt_print_screen = dialog.findViewById(R.id.txt_print_screen);

        TextView txt_reprint = dialog.findViewById(R.id.txt_reprint);
        ll_buttons.setWeightSum(2);

        txt_print_screen.setVisibility(View.VISIBLE);

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txt_print_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();

                editor.putString("setting_print_to_screen", "1");
                editor.commit();
                doPayment(meter_number, pass_value);
                dialog.dismiss();


            }
        });
        txt_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }
    private void showLoginAlert(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Login Required")
                .setMessage("Please login again")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);

//                    logout();
                })
                .show();
    }
    public void doPayment(String pass_meter_number, String pass_value) {

        if (Topitup.POSUSER_ID == null || Topitup.POSUSER_ID.trim().isEmpty()) {
            showLoginAlert(activity_elec.this);
            return;
        }


        showCustomDialog("Requesting Voucher", "Processing...", false);

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


        is_busy_with_voucher = true;

        final Call<ResponseBody> call = apiService.elec_process(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.TIU_LICENSE, pass_value, pass_meter_number, Topitup.POSUSER_ID, "0", "0");

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Timber.e(response.body());


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

                //voucher_response res = response.body();

                if (res.toLowerCase().contains("<error><err>") || res.toLowerCase().contains("err:")) {

                    String matcher = res.replaceAll("(?i)err:", "");
                    stopCustomDialog("Could Not Vend Electricity", matcher);

                    txtPaymentAmount.setEnabled(true);
                    amntEditText.setEnabled(true);
                    amntEditText_cent.setEnabled(true);


                    txtPaymentAmount.requestFocus();
                    btn_next.setVisibility(View.VISIBLE);
                    btn_next.setEnabled(true);


                } else {

                    // Timber.i("ELEC: " + res);

                    if (IsVoucherOk(res)) {

                        process_step = 2;

                        do_confrim_payment();

                    } else {

                        stopCustomDialog("Problem", "Voucher Issue");

                    }


                }

                // Timber.e(res);

                //Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof SocketTimeoutException) {

                    stopCustomDialog("Connection Issue", "You have been charged, please check reprint if there is an issue.");

                } else {

                    stopCustomDialog("Problem", t.getMessage());

                }


                //Toasty.error(mContext, t.getMessage(), 8000, true).show();
                is_busy_with_voucher = false;

                //t.printStackTrace();

            }

        });


    }

    public void do_payment(String pass_meter_number, String pass_value) {

        dialog.dismiss();
        ConfirmationForPayment(pass_meter_number, pass_value, elecAmount);


    }

    public void doPrePayment(String pass_meter_number, String pass_value) {
        String selectedPrinter = settings.getString("printer", "inner");
        if (settings.getString("setting_print_to_screen", "0").equals("1")) {
            isFromScreenPrint = true;
            doPayment(pass_meter_number, pass_value);

        } else {
            isFromScreenPrint = false;
            if (selectedPrinter.equals("bluetooth")) {


                if (activity_settings.isBluetoothConnected) {
                    if(activity_settings.isBluetoothConnected && BluetoothService.isReallyConnected()) {

                        if (!activity_settings.checkPrinterStatusWithoutHandler(Topitup.getAppContext())) {
                            showUsbNotConnectedDialog(activity_elec.this, pass_meter_number, pass_value);


                        } else {
                            doPayment(pass_meter_number, pass_value);
                        }
                    }else{
                        if (activity_settings.mService != null)  {
                            String lastDeviceAddress = settings.getString("last_device_address", null);
                            if (lastDeviceAddress != null) {
                                if (activity_settings.mBluetoothAdapter == null) {
                                    activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                }
                                activity_settings.bluetoothMsg = "";
                                BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                                activity_settings.mService.connect(device);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        if(activity_settings.isBluetoothConnected){
                                            doPayment(pass_meter_number, pass_value);
                                        }else{
                                            Toast.makeText(getAppContext(),"Please try again",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, 3000);


                            }else{
                                Toast.makeText(getAppContext(),"bluetooth last Address null",Toast.LENGTH_LONG).show();

                            }
                        }else{
                            Toast.makeText(getAppContext(),"bluetooth Service null",Toast.LENGTH_LONG).show();

                        }
                    }
                } else {
//                    Toast.makeText(getAppContext(), "bluetooth was not connected", Toast.LENGTH_LONG).show();
                    if (activity_settings.mService != null)  {
                        String lastDeviceAddress = settings.getString("last_device_address", null);
                        if (lastDeviceAddress != null) {
                            if (activity_settings.mBluetoothAdapter == null) {
                                activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            }
                            activity_settings.bluetoothMsg = "";
                            BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                            activity_settings.mService.connect(device);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if(activity_settings.isBluetoothConnected){
                                        doPayment(pass_meter_number, pass_value);
                                    }else{
                                        Toast.makeText(getAppContext(),"Please try again",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, 3000);


                        }else{
                            Toast.makeText(getAppContext(),"bluetooth last Address null",Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(getAppContext(),"bluetooth Service null",Toast.LENGTH_LONG).show();

                    }
                   /* if (activity_settings.mService != null)  {
                        String lastDeviceAddress = settings.getString("last_device_address", null);
                        if (lastDeviceAddress != null) {
                            if (activity_settings.mBluetoothAdapter == null) {
                                activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            }
                            activity_settings.bluetoothMsg = "";
                            BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                            activity_settings.mService.connect(device);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {

                                    if(activity_settings.isBluetoothConnected){
                                        doPayment(pass_meter_number, pass_value);

                                    }else{
                                        Toast.makeText(activity_elec.this,"Bluetooth Printer is offline",Toast.LENGTH_LONG).show();

                                    }
                                }
                            }, 3000);


                        }else{
                            Toast.makeText(activity_elec.this,"bluetooth last Address null",Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(activity_elec.this,"bluetooth Service null",Toast.LENGTH_LONG).show();

                    }*/
                }

            } else if (selectedPrinter.equals("usb")) {
                if (PrinterTopitup.dev != null) {

                    if (!(PrinterTopitup.usbCtrl.isHasPermission(PrinterTopitup.dev))) {
                        dialog.dismiss();
                        PrinterTopitup.printmethod(activity_elec.this);
                    } else {
                        doPayment(pass_meter_number, pass_value);

                    }
                } else {

                    PrinterTopitup.printmethod(activity_elec.this);

                    // Re-check if the device is now connected after calling printmethod
                    if (PrinterTopitup.dev == null) {
                        // No USB device is connected, handle this case (e.g., show a dialog)
                        showUsbNotConnectedDialog(activity_elec.this, pass_meter_number, pass_value);
                    }
//                    dialog.dismiss();
//                    showUsbNotConnectedDialog(activity_elec.this, pass_meter_number, pass_value);
                    //  Toast.makeText(Topitup.getAppContext(), "Usb not connected", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("TABLET")) {
//                    stopCustomDialog("USB not Print", "Please connect Usb or Bluetooth\"");
                    dialog.dismiss();
                    Toast.makeText(getAppContext(), "Please connect USB or Bluetooth", Toast.LENGTH_LONG).show();

                } else {
                    doPayment(pass_meter_number, pass_value);

                }

            }

        }
    }

    private void ConfirmationForPayment(String pass_meter_number, String pass_value, String elecAmount) {

        Dialog dialogConfirm = new Dialog(this);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogConfirm.setContentView(R.layout.confirmation_elec_dialog);
        dialogConfirm.setCancelable(true);
        this.setFinishOnTouchOutside(false);
        TextView txt_clock = dialogConfirm.findViewById(R.id.txt_clock);
        TextView txt_account = dialogConfirm.findViewById(R.id.txt_account);
        TextView txt_app_version = dialogConfirm.findViewById(R.id.txt_app_version);
        TextView txt_user_name = dialogConfirm.findViewById(R.id.txt_user_name);
        TextView txt_acc_type = dialogConfirm.findViewById(R.id.txt_acc_type);
        TextView txt_account_type = dialogConfirm.findViewById(R.id.txt_account_type);
        TextView txt_amount = dialogConfirm.findViewById(R.id.txt_amount);
        TextView txt_meter = dialogConfirm.findViewById(R.id.txt_meter);
        TextView txt_vouchers = dialogConfirm.findViewById(R.id.txt_vouchers);


        TextView txt_cancel_amount = dialogConfirm.findViewById(R.id.txt_cancel_amount);
        TextView txt_proceed_amount = dialogConfirm.findViewById(R.id.txt_proceed_amount);

        txt_cancel_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                btn_next.setVisibility(View.VISIBLE);
                btn_next.setEnabled(true);

            }
        });
        txt_proceed_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                doPrePayment(pass_meter_number, pass_value);
//                transanct();
            }
        });


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);


        setBoldText(txt_meter, "Meter #:", pass_meter_number, "");
        setBoldText(txt_amount, "Amount: R ", elecAmount, "");
        setBoldText(txt_vouchers, "Number of Vouchers:", String.valueOf(mqty), "");


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
        if (company_name != null && !company_name.isEmpty()) {

            if (company_name.length() > 10) {
                txt_account.setText(company_name.substring(0, 10) + "... ");
            } else {

                txt_account.setText(company_name);
            }
        }
//        txt_activation.setText("Product " + providerName);
        dialogConfirm.show();
    }

    public void setBoldText(TextView textView, String prefix, String boldText, String suffix) {
        // Combine all parts of the text
        String fullText = prefix + boldText + suffix;

        // Create a SpannableStringBuilder
        SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);
        int start = prefix.length();
        int end = start + boldText.length();
        // Apply bold style to the boldText part
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                start, // Start index of bold text
                end, // End index of bold text
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new RelativeSizeSpan(1.2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the styled text to the TextView
        textView.setText(spannable);
    }

    private boolean IsVoucherOk(String retWS) {

        _elecVoucherStockUid = "0";
        _elecVoucher = "";

        try {

            int rowCount = 0;
            String[] toSplit = retWS.split("\n");
            for (int i = 0; i < toSplit.length; i++) {

                String s = toSplit[i];
                rowCount++;

                if (rowCount == 1) {

                    // Timber.e("ELEC : split = " + s);

//                    char[] del = new char[] { '=', '^' };
                    String[] tokens = s.split("\\^");
//
                    for (int token = 0; token < tokens.length; token++) {

                        String s1 = "";
                        String s2 = "";

                        try {
                            s1 = tokens[token].split("=")[0];
                            s2 = tokens[token].split("=")[1];
                        } catch (Exception ex) {
                            //
                        }

                        if (s1.equals("uid")) {
                            // AppSettings.lastVoucherUID = s2;
                            _elecVoucherStockUid = s2;
                        }

//                        if (s1 == "l")
//                            if (s2 == "1")
//                                finBalance.low_balance = "1";
//                            else
//                                finBalance.low_balance = "0";

//                        if (s1 == "dtm")
//                        {
//                            // AppSettings.lastVoucherUID = s2;
//                            globalVoucher.tx_date = s2;
//                        }

                    }

                } else {
                    _elecVoucher += "\n" + s;
                }
            }

            return true;

        } catch (Exception ex) {
            Timber.e(ex.getMessage());
        }


        //Timber.e("ELEC : " + _elecVoucherStockUid + " --- " + _elecVoucher);

        return false;

    }

    public void do_confrim_payment() {

        if (!batteryAlert()) {
            //dialog_multi.dismiss();

            return;
        }

        final Call<ResponseBody> call = apiService.elec_vend_confirm(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, _elecVoucherStockUid, Topitup.TIU_LICENSE);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Timber.e("ELEC : " + response.body());
                //process_step = 2;

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

                //voucher_response res = response.body();

                if (res.toLowerCase().contains("ok=1")) {
                    //if (res.toLowerCase().equals("ok=1")) {

                    boolean is_last_voucher = current_voucher >= mqty;

                    printingCustomDialog("Printing", "Your voucher is busy printing.\nYou have been charged, please check reprint if there is an issue.", is_last_voucher);

                    //Printer.store_last_reprint(res.toString());
                    //Printer.print_data(res.toString());
                    //  Printer.store_last_reprint(_elecVoucher);
                    PrinterTopitup.print_data(_elecVoucher);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

                    if (mqty > 1 && current_voucher < mqty) {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                        current_voucher++;

                        pop_title.setText("Requesting Voucher (" + current_voucher + " of " + mqty + ")");
                        do_payment(txtAccountNumber.getText().toString(), String.valueOf((int) value));

                    }


                } else {

                    Toasty.info(mContext, res, 8000, true).show();
                    stopCustomDialog("Could not Print", "Please try a reprint.");

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof SocketTimeoutException) {

                    stopCustomDialog("Connection Issue", "You have been charged, please check reprint if there is an issue.");

                } else {

                    stopCustomDialog("Problem", t.getMessage());

                }

            }

        });


    }

    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        FullscreenCall();
        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);
            if (printerQ1Sts == 1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(View.VISIBLE);
            } else {
                bt_close.setVisibility(View.VISIBLE);
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
                dialog.dismiss();
                do_payment(txtAccountNumber.getText().toString(), String.valueOf((int) value));
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


//    private void showCustomDialog(String sTitle, String sContent) {
//
//
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
//        dialog.setContentView(R.ding_products.dialog_dark);
//        dialog.setCancelable(false);
//        this.setFinishOnTouchOutside(false);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
//        pageLoadingWrapper = ((LinearLayout) dialog.findViewById(R.id.pageLoadingWrapper));
//        pop_title = ((TextView) dialog.findViewById(R.id.pop_title));
//        pop_content = ((TextView) dialog.findViewById(R.id.pop_content));
//
//        pop_title.setText(sTitle);
//        pop_content.setText(sContent);
//
//        bt_close = ((AppCompatButton) dialog.findViewById(R.id.bt_close));
//        bt_close.setVisibility(View.GONE);
//
//        bt_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//
//    }


    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }

    private void printingCustomDialog(String sTitle, String sContent, boolean is_last_voucher) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        if (is_last_voucher) {
            handler.postDelayed(runnable, 2000);
        }

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

    public void warning_electra() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Warning")
                .setMessage("Are you sure, you want to continue ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this,"Selected Option: YES",Toast.LENGTH_SHORT).show();
                        warning_process = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this,"Selected Option: No",Toast.LENGTH_SHORT).show();
                        warning_process = true;
                    }
                });
        //Creating dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
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
                    Log.e("electricity","text watcher cent"+s.toString());

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

}