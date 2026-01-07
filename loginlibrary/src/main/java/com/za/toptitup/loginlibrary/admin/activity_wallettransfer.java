package com.za.toptitup.loginlibrary.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
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
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_wallettransfer extends BaseAdminActivity {

    public static activity_wallettransfer instance;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    protected Topitup app;
    Realm realm;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    fin_balance res;
    EditText txt_amount;
    TextView txt_total_available_default, txt_total_available_cash, txt_total_available_commission, txt_total_available_swipe, txt_amount_standard;
    RadioButton rdo_filter_type_0;
    RadioButton rdo_filter_type_1, rdo_filter_type_2, rdo_filter_type_3, rdo_filter_type_4;
    RadioButton radio_standars, radio_bills, radio_swipe, radio_achieve, radio_commission;
    Button btn_make_payment, btn_print_balance;
    RadioGroup grp_radio_layout, grp_radio_layout_to;
    //    RealmResults<fin_balance> tiu_fin_balance;
    EditText amntEditText, amntEditText_cent;
    TextView date1;
    String dt;
    RealmResults<fin_balance> tiu_fin_balance;
    ScrollView scoll;
    LinearLayout tiu_title_bar_new;
    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    private TextView txt_rand;
    private String enable_remote_ext_credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        instance = this;
        setContentView(R.layout.activity_wallettransfer);
        activity_login.fromScreen = "wallet_transfer";

        /*getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        scoll = findViewById(R.id.scoll);
        txt_amount = findViewById(R.id.txt_amount);
        txt_amount.addTextChangedListener(new MoneyTextWatcher(txt_amount));
        txt_total_available_default = findViewById(R.id.txt_total_available_default);
        txt_total_available_cash = findViewById(R.id.txt_total_available_cash);
        txt_total_available_swipe = findViewById(R.id.txt_total_available_swipe);
        txt_amount_standard = findViewById(R.id.txt_amount_standard);
        txt_total_available_commission = findViewById(R.id.txt_total_available_commission);
        btn_print_balance = findViewById(R.id.btn_print_balance);
        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);
        txt_rand = findViewById(R.id.txt_rand);
        final fin_balance tiu_fin_balance_ = realm.where(fin_balance.class).findFirst();

        enable_remote_ext_credit = tiu_fin_balance_.enable_remote_ext_credit;


        scoll.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    private Integer systemNavigationBarHeight = null;

                    @Override
                    public void onGlobalLayout() {
                        int heightDifference = getHeightDifference();
                        if (heightDifference > 0) {
                            if (systemNavigationBarHeight == null) {
                                /* Get layout height when the layout was created at first time */
                                systemNavigationBarHeight = heightDifference;
                            }
                        } else {
                            systemNavigationBarHeight = 0;
                        }

                        if (heightDifference > getDefaultNavigationBarHeight()) {
                            /* Keyboard opened */
                            int keyBoardHeight = heightDifference - systemNavigationBarHeight;
                            scoll.scrollTo(0, scoll.getBottom());
//                                                       scoll.addView(home_linear, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                        } else {

                            /* Keyboard closed */
                        }
                    }
                });


/*
        amntEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

//                  amntEditText_cent.requestFocus();
                    hideKeyboard();
                    Log.e("action done",".........");
                }
                return false;
            }
        });

        amntEditText_cent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

//                    amntEditText_cent.requestFocus();
                    hideKeyboard();
                    Log.e("action done",".........");
                }
                return false;
            }
        });*/
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);

        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        rdo_filter_type_0 = findViewById(R.id.rdo_filter_type_0);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);
        rdo_filter_type_2 = findViewById(R.id.rdo_filter_type_2);
        rdo_filter_type_3 = findViewById(R.id.rdo_filter_type_3);
        rdo_filter_type_4 = findViewById(R.id.rdo_filter_type_4);

        radio_standars = findViewById(R.id.radio_standars);
        radio_bills = findViewById(R.id.radio_bills);
        radio_swipe = findViewById(R.id.radio_swipe);
        radio_achieve = findViewById(R.id.radio_achieve);
        radio_commission = findViewById(R.id.radio_commission);

        radio_standars.setVisibility(View.GONE);

        grp_radio_layout = findViewById(R.id.grp_radio_layout);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        grp_radio_layout_to = findViewById(R.id.grp_radio_layout_to);
        btn_make_payment = findViewById(R.id.btn_make_payment);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);

        final String setting_balance_login = settings.getString("setting_balance_login", "0");

        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
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
        final TextView tiu_clock = findViewById(R.id.tiu_clock);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
        }else {
            // Use the same format for lower versions as well
            sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
        }
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
                }else {
                    // Use the same format for lower versions as well
                    sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
                }
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);
        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

//        amntEditText.setFocusable(true);
        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));


        tiu_title_outlet.setText(tiu_settings.account_number);

        /*if (tiu_settings.company_name.length() > 10) {
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
        } else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/

        if (Topitup.IS_ADMIN.equals("1")) {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Admin");
        } else {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Cashier");
        }
       /* SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dt = sdf1.format(new Date());
        date1=(TextView) findViewById(R.id.date1);
        date1.setText(dt);*/

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (enable_remote_ext_credit.equals("1")) {
                        txt_amount_standard.setText("R " + tiu_fin_balance.get(0).balance);
                    }
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
                    }
                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))

                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                        view2.setVisibility(View.VISIBLE);
                    }
                    if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                    }
                    if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }
                }
            });


            if (enable_remote_ext_credit.equals("1")) {
                txt_amount_standard.setText("R " + tiu_fin_balance.get(0).available_balance);
            }

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
            }
            if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
            }
            if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            } else {
                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            //
        }
       /* if (tiu_settings.company_name.length() > 10) {
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
        } else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... | Admin");

            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... | Cashier");
            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME + " | Admin");

            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME + " | Cashier");
            }
        }*/

      /*  try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }
                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
            } else {
                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            //
        }*/

        rdo_filter_type_2.setEnabled(true);

        rdo_filter_type_3.setEnabled(true);

        radio_commission.setEnabled(true);
        radio_swipe.setEnabled(true);
        btn_make_payment.setText("TRANSFER FUNDS");

//        btn_make_payment.setText("TRANSFER TO CASH WALLET");
        grp_radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Toasty.error(mContext, String.valueOf(checkedId), 1000, true).show();

                if (checkedId == R.id.rdo_filter_type_0) {
                    radio_standars.setVisibility(View.GONE);
                    radio_bills.setVisibility(View.VISIBLE);
                    radio_swipe.setVisibility(View.GONE);
                    radio_commission.setVisibility(View.GONE);


                    radio_bills.setChecked(true);
                    btn_make_payment.setText("TRANSFER FUNDS");
                } else if (checkedId == R.id.rdo_filter_type_1) {
                    radio_standars.setVisibility(View.VISIBLE);
                    radio_bills.setVisibility(View.GONE);
                    radio_swipe.setVisibility(View.GONE);
                    radio_commission.setVisibility(View.GONE);

                    radio_standars.setChecked(true);

                    btn_make_payment.setText("TRANSFER FUNDS");
                } else if (checkedId == R.id.rdo_filter_type_2) {
                    radio_standars.setVisibility(View.VISIBLE);
                    radio_bills.setVisibility(View.VISIBLE);
                    radio_swipe.setVisibility(View.GONE);
//                    radio_achieve.setVisibility(View.VISIBLE);
                    radio_commission.setVisibility(View.GONE);

                    radio_standars.setChecked(true);

                    btn_make_payment.setText("TRANSFER FUNDS");
                } else if (checkedId == R.id.rdo_filter_type_3) {
                    radio_standars.setVisibility(View.VISIBLE);
                    radio_bills.setVisibility(View.VISIBLE);
                    radio_swipe.setVisibility(View.GONE);
//                    radio_achieve.setVisibility(View.VISIBLE);
                    radio_commission.setVisibility(View.GONE);

                    radio_standars.setChecked(true);

                    btn_make_payment.setText("TRANSFER FUNDS");
                } else if (checkedId == R.id.rdo_filter_type_4) {
                    radio_standars.setVisibility(View.VISIBLE);
                    radio_bills.setVisibility(View.VISIBLE);
                    radio_swipe.setVisibility(View.VISIBLE);
//                    radio_achieve.setVisibility(View.GONE);
                    radio_commission.setVisibility(View.VISIBLE);

                    radio_standars.setChecked(true);

                    btn_make_payment.setText("TRANSFER FUNDS");
                }

            }
        });

        get_balance();

    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_wallettransfer.rl_network.setVisibility(View.VISIBLE);
                    activity_wallettransfer.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_wallettransfer.rl_network.setVisibility(View.INVISIBLE);
                    activity_wallettransfer.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_wallettransfer.rl_network.setVisibility(View.INVISIBLE);
                    activity_wallettransfer.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private int getHeightDifference() {
        Point screenSize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        } else {
            getWindowManager().getDefaultDisplay().getSize(screenSize);
        }

        Rect rect = new Rect();
        scoll.getWindowVisibleDisplayFrame(rect);
        return screenSize.y - rect.bottom;
    }

    private int getDefaultNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 100;
    }

    public void onclick_btn_action(View v) {

        /*switch (v.getId()) {


            case R.id.btn_bottom_close: {
                onBackPressed();
                return;
            }


            case R.id.btn_make_payment: {

                DoTransfer();
                return;

            }
            case R.id.btn_print_balance: {

                final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();

                Printer.print_data("2" + tiu_settings.company_name + "\n" + "1" + tiu_settings.account_number + "\n\n" + "1Current Balance Report\n\n\n1Date: " + res.dtmd + "\n1Time: " + res.dtmt + "\n\n\n1Available: R " + res.available_balance + "\n1Balance: R " + res.balance + "\n1Credit: R " + res.credit_limit + "\n1Cash: R " + res.balance_cash + "\n1Commission: R " + res.commission + "\n1Swipe: R " + res.swipe + "\n\n\n");
//                     return;

            }


        }*/
        if (v.getId() == R.id.btn_bottom_close) {
            onBackPressed();

        } else if (v.getId() == R.id.btn_make_payment) {
            DoTransfer();

        } else if (v.getId() == R.id.btn_print_balance) {
            final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();

            PrinterTopitup.print_data("2" + tiu_settings.company_name + "\n" +
                    "1" + tiu_settings.account_number + "\n\n" +
                    "1Current Balance Report\n\n\n1Date: " + res.dtmd + "\n" +
                    "1Time: " + res.dtmt + "\n\n\n1Available: R " + res.available_balance + "\n" +
                    "1Balance: R " + res.balance + "\n" +
                    "1Credit: R " + res.credit_limit + "\n" +
                    "1Cash: R " + res.balance_cash + "\n" +
                    "1Commission: R " + res.commission + "\n" +
                    "1Swipe: R " + res.swipe + "\n\n\n");

        }


    }

    private void DoTransfer() {


        String _filter_type = "0";

        if (rdo_filter_type_0.isChecked()) {
            _filter_type = "0";
        } else if (rdo_filter_type_1.isChecked()) {
            _filter_type = "1";
        } else if (rdo_filter_type_2.isChecked()) {
            _filter_type = "2";
        } else if (rdo_filter_type_3.isChecked()) {
            _filter_type = "3";
        } else if (rdo_filter_type_4.isChecked()) {
            _filter_type = "4";
        }


        String to_type = "1";

        if (radio_standars.isChecked()) {
            to_type = "0";
        } else if (radio_bills.isChecked()) {
            to_type = "1";
        } else if (radio_commission.isChecked()) {
            to_type = "2";
        } else if (radio_swipe.isChecked()) {
            to_type = "3";
        }


        // Toasty.error(mContext, "selected!"+rdo_filter_type_1.isSelected(), 3000, true).show();

        String amount = "", cent = "00";
        if (amntEditText_cent.length() > 0) {
            int numberFromCent = Integer.parseInt(amntEditText_cent.getText().toString());
            cent = String.format("%02d", numberFromCent);
        }

        if (amntEditText.getText().length() > 0) {
            amount = convertNumber(amntEditText.getText().toString()) + "." + cent;


        } else {
            amount = "0" + "." + cent;
        }

        if (amntEditText.getText().toString().length() > 0) {
            if (Integer.parseInt(amntEditText.getText().toString()) < 5) {
                Toasty.error(mContext, "amount should be greater than 5", 8000, true).show();
                return;
            }
        } else {
            Toasty.error(mContext, "amount should be greater than 5", 8000, true).show();
            return;
        }

//        amount = amntEditText.getText().toString() + "." + cent;

        amount = amount.replace("R", "").trim();

        if (amount.length() < 2) {
            Toasty.error(mContext, "Please enter a correct value!", 3000, true).show();
            return;
        }

        displayDialog(_filter_type, to_type, amount);

       /* showCustomDialog();

        final Call<ResponseBody> call = apiService.wallet_transfer_new(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, _filter_type,to_type, amount);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;

                String res = "";
                try {

                    res = response.body().string();

                } catch (Exception ex) {
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    dialog.dismiss();
                    return;
                }

                // Toast.makeText(activity_wallettransfer.this, "v=",Toast.LENGTH_LONG).show();
                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String err_msg = "No Results.";

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.contains("ERR:")) {
                        err_msg = res.replace("ERR:", "");
                    }

                    Toasty.error(mContext, err_msg, 10000, true).show();
                    dialog.dismiss();
                    return;

                }

                try {
                    JSONObject obj = new JSONObject(res);

                    //Log.d("My App", obj.toString());
                    //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                    if (obj.getString("ret").equals("ok")) {
                        Printer.print_data(obj.getString("slip"));
                    } else {
                        Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
                        dialog.dismiss();
                        return;
                    }

                } catch (Exception ex) {
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    dialog.dismiss();
                    return;
                }


                dialog.dismiss();
                Toasty.success(mContext, "Transfer Complete!", 3000, true).show();
                onBackPressed();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                // Timber.i("REPRINT: " +  t.getMessage());

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                //progressBar.setVisibility(View.GONE);
                //t.printStackTrace();

                dialog.dismiss();

            }

        });
*/

    }

    private void displayDialog(String from_type, String to_type, String amount) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        String from_acc = "", to_acc = "";
        if (from_type.equals("0")) {
            from_acc = "Standard";
        } else if (from_type.equals("1")) {
            from_acc = "Bills";

        } else if (from_type.equals("2")) {
            from_acc = "Commission";

        } else if (from_type.equals("3")) {
            from_acc = "Swipe Card";

        }


        if (to_type.equals("0")) {

            to_acc = "Standard";

        } else if (to_type.equals("1")) {
            to_acc = "Bills";

        } else if (to_type.equals("2")) {
            to_acc = "Commission";

        } else if (to_type.equals("3")) {
            to_acc = "Swipe Card";

        }
        TextView from_text = dialog.findViewById(R.id.from);
        TextView to_text = dialog.findViewById(R.id.to);
        TextView payamount = dialog.findViewById(R.id.payamount);
        Button bt_close = dialog.findViewById(R.id.bt_close);
        Button bt_process_fin = dialog.findViewById(R.id.bt_process_fin);


        from_text.setText(from_acc);
        to_text.setText(to_acc);
        payamount.setText("R " + amount);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_process_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!Topitup.POSUSER_ID.equals("0")) {
                    transferAmount(from_type, to_type, amount.replace(",", ""));

                } else {
                    Toast.makeText(activity_wallettransfer.this, "Please Try After Some Time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String convertNumber(String number) {

//       String  NumberFormat.getNumberInstance(Locale.getDefault()).format(Integer.parseInt(number))
        int amount = Integer.parseInt(number);
//        Log.e("double ","amount........."+amount);
//        DecimalFormat formatter = new DecimalFormat("#,###");
        String formatted = NumberFormat.getNumberInstance(Locale.US).format(amount);
//        Log.e("double ",formatted+"amount........."+amount);
//        System.out.println(NumberFormat.getNumberInstance(Locale.US).format(amount));

        return formatted;
    }

    private void transferAmount(String _filter_type, String to_type, String amount) {
        showCustomDialog();

        final Call<ResponseBody> call = apiService.wallet_transfer_new(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, _filter_type, to_type, amount);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                JSONObject objNew;
                try {

                    res = response.body().string();
                     objNew = new JSONObject(res);

                } catch (Exception ex) {
                    if (response.body() != null)
                    response.body().close();
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    dialog.dismiss();
                    return;
                }

                // Toast.makeText(activity_wallettransfer.this, "v=",Toast.LENGTH_LONG).show();
                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:")|| res.contains("err:") || res.contains("\"err\"")) {

                    String err_msg = "No Results.";

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.contains("ERR:")) {
                        err_msg = res.replace("ERR:", "");
                    }

                    if (res.contains("err:")) {
                        try {
                            err_msg = objNew.getString("err");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Toasty.error(mContext, err_msg, 10000, true).show();
                    dialog.dismiss();
                    return;

                }else{
                    try {
                        JSONObject obj = new JSONObject(res);

                        //Log.d("My App", obj.toString());
                        //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                        if (obj.getString("ret").equals("ok")) {
                            PrinterTopitup.print_data(obj.getString("slip"));
                        } else {
                            Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
                            dialog.dismiss();
                            return;
                        }

                    } catch (Exception ex) {
                        Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                        dialog.dismiss();
                        return;
                    }
                    Toasty.success(mContext, "Transfer Complete!", 3000, true).show();
                }




                dialog.dismiss();

                onBackPressed();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                // Timber.i("REPRINT: " +  t.getMessage());

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                //progressBar.setVisibility(View.GONE);
                //t.printStackTrace();

                dialog.dismiss();

            }

        });
    }

    private void showCustomDialog() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);

        pop_title.setText("Requesting");
        pop_content.setText("please wait...");

        bt_close = dialog.findViewById(R.id.bt_close);
        bt_close.setVisibility(View.GONE);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public void get_balance() {

        final Call<fin_balance> call = apiService.get_balance_new(Topitup.TIU_LICENSE, "1");

        call.enqueue(new Callback<fin_balance>() {

            @Override
            public void onResponse(Call<fin_balance> call, Response<fin_balance> response) {


                if(response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }


                    try {

                        res = response.body();

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(res);
                        realm.commitTransaction();
                        realm.close();


                        txt_total_available_cash.setText("R " + res.balance_cash);
                        txt_total_available_default.setText("R " + res.available_balance);
                        txt_total_available_swipe.setText("R " + res.swipe);
                        txt_total_available_commission.setText("R " + res.commission);
                  /*  txt_total_available_cash.setText("Available funds to Transfer (Additional Wallet): " + res.balance_cash);
                    txt_total_available_default.setText("Available funds to Transfer (Default Wallet): " + res.balance);*/

                        //fin_balance_default.setText(res.balance);
                        //fin_balance_available.setText(res.available_balance);
                        //fin_balance_cash.setText(res.balance_cash);

                    } catch (Exception ex) {

                        Toasty.error(mContext, "Unable to fetch Available Balance!", 4000, true).show();

                      /*  if (response.raw() != null)
                            response.raw().close();*/

                        txt_total_available_cash.setText("Unknown");
                        txt_total_available_default.setText("Unknown");
                        txt_total_available_swipe.setText("UnKnown");
                        txt_total_available_commission.setText("UnKnown");
                   /* txt_total_available_cash.setText("Available funds to Transfer (Additional Wallet): Unknown");
                    txt_total_available_default.setText("Available funds to Transfer (Default Wallet): Unknown");*/

                    }
                }else{
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }

            }

            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                //progressBar.setVisibility(View.GONE);
                t.printStackTrace();

            }

        });


    }

    public void hideKeyboard() {
        try {

            View view = findViewById(android.R.id.content);
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            /*
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }*/
        } catch (Exception var2) {
        }

       /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            if (s.isEmpty()) return;
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[R,.]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, RoundingMode.FLOOR).divide(new BigDecimal(100), RoundingMode.FLOOR);

            editText.setText(parsed.toString());
            editText.setSelection(parsed.toString().length());
            editText.addTextChangedListener(this);
        }
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

                    cent_value_entered = WordsConert.convert(number);
                    if (rand_value_entered.equals("")) {
                        txt_rand.setText(cent_value_entered + " Cent");
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
                        txt_rand.setText(" " + cent_value_entered + " Cent");
                    }
                } else {
                    final long number = Long.parseLong(s.toString());
                    rand_value_entered = WordsConert.convert(number);
                    if (cent_value_entered.equals("")) {
                        txt_rand.setText(rand_value_entered + " Rand");

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

}