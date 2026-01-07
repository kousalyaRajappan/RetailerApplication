package com.za.toptitup.loginlibrary;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.za.toptitup.loginlibrary.admin.Printingw;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.MyItemSwipe;
import com.za.toptitup.loginlibrary.model.ReprintListSwipe;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_addpay_bills extends AppCompatActivity {

    Realm realm;
    private BroadcastReceiver mNetworkReceiver;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    private final String TAG = "invoke--ConsumeActivity";
    EditText txt_input, date;
    RadioButton rdo_filter_type_0, rdo_filter_type_100;
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_2, rdo_filter_type_3;
    ListView lst_reprint;
    RealmResults<service_provider_item> service_provider_items;
    RealmResults<fin_balance> tiu_fin_balance;
    String spi_barcode, item_desc, enable_realtime_swipe, dt;
    int size;
    String company_addrs, company_addrs1, company_addrs2, print_address = "0";
    DatePickerDialog datePickerDialog;
    activity_adpay_new adpay_new;
    String pid;
    TextView date1, tot_sales, tot_sales_amount, tot_txns_pending, tot_txns_pending_amount, tot_txns_declined_cancelled, tot_txns_declined_cancelled_amount, tot_txns_approved, tot_txns_approved_amount;
    String txnid, txnsts = "100";
    RadioButton tot_txns, tot_approved, tot_declined_cancelled, tot_pending;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;
    public static activity_addpay_bills instance;
    SharedPreferences settings;

    public static String setting_printer_bypass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_addpay_bills);

        FullscreenCall();



        /* TIU HEADER */
         settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_print_address = settings.getString("setting_print_address", "0");
        setting_printer_bypass = settings.getString("setting_print_bypass", "0");

        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        final String setting_chk_allow_cashier_rep_ser = settings.getString("setting_chk_allow_cashier_rep_ser", "0");
        final String str_chk_last_vou_admin = settings.getString("setting_chk_last_vou_admin", "0");
        final String str_chk_last_vou_cashier = settings.getString("setting_chk_last_vou_cashier", "0");
        enable_realtime_swipe = settings.getString("enable_realtime_swipe", "0");
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

        activity_login.fromScreen = "activity_addpay_bills";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        tiu_title_outlet.setText(tiu_settings.account_number);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        if (Topitup.IS_ADMIN.equals("1")) {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Admin");

        } else {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Cashier");

        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dt = sdf1.format(new Date());
        date1 = findViewById(R.id.date1);
        date1.setText(dt);
        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);
    /*    TextView  mIsDemo = (TextView) findViewById(R.id.activity_login_is_demo);
        if (Topitup.DEBUG) {
            mIsDemo.setVisibility(View.INVISIBLE);
        }else {
            mIsDemo.setVisibility(View.VISIBLE);
        }*/

        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c1.getTime());
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
        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
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

                    }
                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
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
        /* END HEADER */
        adpay_new = new activity_adpay_new();


        txt_input = findViewById(R.id.txt_input);
        date1 = findViewById(R.id.date1);
        tot_sales = findViewById(R.id.tot_sales);
        tot_sales_amount = findViewById(R.id.tot_sales_amount);

        rdo_filter_type_0 = findViewById(R.id.rdo_filter_type_0);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);
        rdo_filter_type_2 = findViewById(R.id.rdo_filter_type_2);
        rdo_filter_type_3 = findViewById(R.id.rdo_filter_type_3);
        rdo_filter_type_100 = findViewById(R.id.rdo_filter_type_100);

        tot_txns = findViewById(R.id.tot_txns);
        tot_approved = findViewById(R.id.tot_approved);
        tot_declined_cancelled = findViewById(R.id.tot_declined_cancelled);
        tot_pending = findViewById(R.id.tot_pending);

        tot_txns_pending = findViewById(R.id.tot_txns_pending);
        tot_txns_pending_amount = findViewById(R.id.tot_txns_pending_amount);
        tot_txns_declined_cancelled = findViewById(R.id.tot_txns_declined_cancelled);
        tot_txns_declined_cancelled_amount = findViewById(R.id.tot_txns_declined_cancelled_amount);
        tot_txns_approved = findViewById(R.id.tot_txns_approved);
        tot_txns_approved_amount = findViewById(R.id.tot_txns_approved_amount);


        Button btn_bottom_reprint_last = findViewById(R.id.btn_bottom_reprint_last);

        lst_reprint = findViewById(R.id.lst_reprint);
        btn_bottom_reprint_last.setEnabled(false);
        // Toasty.info( mContext,"str_chk_last_vou_admin="+str_chk_last_vou_admin, Toast.LENGTH_SHORT).show();

        if (Topitup.IS_ADMIN.equals("1") && str_chk_last_vou_admin.equals("1"))
            btn_bottom_reprint_last.setEnabled(true);
        if (Topitup.IS_ADMIN.equals("0") && str_chk_last_vou_cashier.equals("1"))
            btn_bottom_reprint_last.setEnabled(true);
        if (Topitup.IS_ADMIN.equals("0")) {
            txt_input.setEnabled(setting_chk_allow_cashier_rep_ser.equals("1"));
        }
        date = findViewById(R.id.date);
        date.setText(dt);
        PopulateLastSales();
        date.setShowSoftInputOnFocus(false);


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        // initiate the date picker and a button

        date1.setText(mYear + "-" + String.format("%02d", (mMonth + 1)) + "-" + String.format("%02d", mDay));
        // perform click event on edit text
        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                date1.setShowSoftInputOnFocus(false);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(activity_addpay_bills.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date1.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                                dt = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                                try {
                                    movies.clear();
                                    listAdapter.clear();
                                    listAdapter.notifyDataSetChanged();
                                } catch (Exception ex) {
                                    //
                                }
                                PopulateLastSales();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_addpay_bills.rl_network.setVisibility(View.VISIBLE);
                    activity_addpay_bills.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_addpay_bills.rl_network.setVisibility(View.INVISIBLE);
                    activity_addpay_bills.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_addpay_bills.rl_network.setVisibility(View.INVISIBLE);
                    activity_addpay_bills.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    public void onRadioButtonClicked(View v) {

       /* switch (v.getId()) {

            case R.id.tot_txns: {
                tot_txns.setChecked(true);
                tot_approved.setChecked(false);
                tot_declined_cancelled.setChecked(false);
                tot_pending.setChecked(false);
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                txnsts = "100";
                PopulateLastSales();
                return;
            }
            case R.id.tot_approved: {
                tot_txns.setChecked(false);
                tot_approved.setChecked(true);
                tot_declined_cancelled.setChecked(false);
                tot_pending.setChecked(false);
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                txnsts = "1";
                PopulateLastSales();
                return;
            }
            case R.id.tot_declined_cancelled: {
                tot_txns.setChecked(false);
                tot_approved.setChecked(false);
                tot_declined_cancelled.setChecked(true);
                tot_pending.setChecked(false);
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                txnsts = "2";
                PopulateLastSales();
                return;
            }
            case R.id.tot_pending: {
                tot_txns.setChecked(false);
                tot_approved.setChecked(false);
                tot_declined_cancelled.setChecked(false);
                tot_pending.setChecked(true);
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                txnsts = "0";
                PopulateLastSales();
            }

        }*/
        if (v.getId() == R.id.tot_txns) {
            tot_txns.setChecked(true);
            tot_approved.setChecked(false);
            tot_declined_cancelled.setChecked(false);
            tot_pending.setChecked(false);
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            txnsts = "100";
            PopulateLastSales();

        } else if (v.getId() == R.id.tot_approved) {
            tot_txns.setChecked(false);
            tot_approved.setChecked(true);
            tot_declined_cancelled.setChecked(false);
            tot_pending.setChecked(false);
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            txnsts = "1";
            PopulateLastSales();

        } else if (v.getId() == R.id.tot_declined_cancelled) {
            tot_txns.setChecked(false);
            tot_approved.setChecked(false);
            tot_declined_cancelled.setChecked(true);
            tot_pending.setChecked(false);
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            txnsts = "2";
            PopulateLastSales();

        } else if (v.getId() == R.id.tot_pending) {
            tot_txns.setChecked(false);
            tot_approved.setChecked(false);
            tot_declined_cancelled.setChecked(false);
            tot_pending.setChecked(true);
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            txnsts = "0";
            PopulateLastSales();
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


    public void onclick_btn_action(View v) throws ParseException {

      /*  switch (v.getId()) {
            case R.id.btn_bottom_close: {
                onBackPressed();
                return;
            }

            case R.id.btn_bottom_reprint_last: {

                if (Topitup.DEVICE_TYPE.equals("WPOS")) {

                    String selectedPrinter = settings.getString("printer", "inner");

                    if (selectedPrinter.equals("inner")) {
                        int status;
                        Printingw wpos = new Printingw();
                        wpos.init();
                        // wpos.printStart();
                        wpos.printStatus();
                        try {
                            status = wpos.mPrinter.printPaper(10);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        if (status != 0 && setting_printer_bypass.equals("0")) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                            return;
                        } else {
                            if (setting_printer_bypass.equals("1")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                                return;
                            } else {
                                Printer.do_last_reprint(mContext);
                            }


                        }
                    }else{
                        Printer.do_last_reprint(mContext);

                    }

                }

                return;
            }
            case R.id.btnDecrease: {
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                // dt = sdf1.format(new Date());
                // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                String currdt = date1.getText().toString();
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(currdt));
                c.add(Calendar.DATE, -1);
                dt = sdf.format(c.getTime());
                date1.setText(dt);
               *//* tot_txns.setChecked(true);
                tot_approved.setChecked(false);
                tot_declined_cancelled.setChecked(false);
                tot_pending.setChecked(false);*//*
                PopulateLastSales();

             *//*    try {
                    Date curdt = sdf1.parse(date1.getText().toString());
                    Date yesterday = DateUtils.addDays(curdt,-1);;
                    date1.setText(sdf1.format((yesterday).toString()));
                    dt=yesterday.toString();
                    PopulateLastSales();
                }catch (ParseException e) {
                    e.printStackTrace();
                }

               try {
                    Date curdt = format.parse(date1.getText().toString());
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date yesterday = cal.getTime();
                    date1.setText(yesterday.toString());
                    dt=yesterday.toString();
                    PopulateLastSales();
                }catch (ParseException e) {
                    e.printStackTrace();
                }*//*
                return;
            }
            case R.id.btnIncrease: {
                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                // dt = sdf1.format(new Date());
                // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                String currdt = date1.getText().toString();
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(currdt));
                c.add(Calendar.DATE, 1);
                dt = sdf.format(c.getTime());
                date1.setText(dt);
                *//*tot_txns.setChecked(true);
                tot_approved.setChecked(false);
                tot_declined_cancelled.setChecked(false);
                tot_pending.setChecked(false);*//*
                PopulateLastSales();

             *//*    try {
                    Date curdt = sdf1.parse(date1.getText().toString());
                    Date yesterday = DateUtils.addDays(curdt,-1);;
                    date1.setText(sdf1.format((yesterday).toString()));
                    dt=yesterday.toString();
                    PopulateLastSales();
                }catch (ParseException e) {
                    e.printStackTrace();
                }

               try {
                    Date curdt = format.parse(date1.getText().toString());
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date yesterday = cal.getTime();
                    date1.setText(yesterday.toString());
                    dt=yesterday.toString();
                    PopulateLastSales();
                }catch (ParseException e) {
                    e.printStackTrace();
                }*//*
                return;
            }
            case R.id.btn_top_search: {

                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }

                PopulateLastSales();
            }


//            case R.id.btn_bottom_reprint: {
//
//                return;
//            }
            //Printer.do_last_reprint(mContext);
            //return true;


        }*/
        if (v.getId() == R.id.btn_bottom_close) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_bottom_reprint_last) {

            if (Topitup.DEVICE_TYPE.equals("WPOS")) {

                String selectedPrinter = settings.getString("printer", "inner");

                if (selectedPrinter.equals("inner")) {
                    if (Build.MODEL.equals("P052")) {
                        PrinterTopitup.do_last_reprint(mContext);
                    } else {


                    int status;
                    Printingw wpos = new Printingw();
                    wpos.init();
                    // wpos.printStart();
                    wpos.printStatus();
                    try {
                        status = wpos.mPrinter.printPaper(10);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    if (status != 0 && setting_printer_bypass.equals("0")) {
                        showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                    } else {
                        if (setting_printer_bypass.equals("1")) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                        } else {
                            PrinterTopitup.do_last_reprint(mContext);
                        }
                    }
                }
                } else {
                    PrinterTopitup.do_last_reprint(mContext);
                }
            }

        } else if (v.getId() == R.id.btnDecrease) {
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currdt = date1.getText().toString();
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(currdt));
            c.add(Calendar.DATE, -1);
            dt = sdf.format(c.getTime());
            date1.setText(dt);
            PopulateLastSales();

        } else if (v.getId() == R.id.btnIncrease) {
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currdt = date1.getText().toString();
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(currdt));
            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            date1.setText(dt);
            PopulateLastSales();

        } else if (v.getId() == R.id.btn_top_search) {
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }

            PopulateLastSales();
        }


    }


    /**
     * @return current Date from Calendar in dd/MM/yyyy format
     * adding 1 into month because Calendar month starts from zero
     */
    public static String getDate(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" +
                (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
    }

    /**
     * @return current Date from Calendar in HH:mm:SS format
     * <p>
     * adding 1 into month because Calendar month starts from zero
     */
    public static String getTime(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY) + ":" +
                (cal.get(Calendar.MINUTE)) + ":" + cal.get(Calendar.SECOND);
    }


    ArrayList<MyItemSwipe> movies = new ArrayList<MyItemSwipe>();
    ReprintListSwipe listAdapter;

    private void PopulateLastSales() {
        tot_sales.setText("0");
        tot_sales_amount.setText("R 0.00");
        tot_txns_pending.setText("0");
        tot_txns_pending_amount.setText("R 0.00");
        tot_txns_approved.setText("0");
        tot_txns_approved_amount.setText("R 0.00");
        tot_txns_declined_cancelled.setText("0");
        tot_txns_declined_cancelled_amount.setText("R 0.00");

        showCustomDialog("", "", false);

        int _filter_type = 1;

        if (rdo_filter_type_0.isChecked()) _filter_type = 1;
        if (rdo_filter_type_1.isChecked()) _filter_type = 7;
        if (rdo_filter_type_2.isChecked()) _filter_type = 2;
        if (rdo_filter_type_100.isChecked()) {
            _filter_type = 100;
        /*    if(Topitup.DEVICE_TYPE.equals("WPOS") && enable_realtime_swipe.equals("1")) {
                activity_adpay adpay = new activity_adpay();
                adpay.getunsettledtxns();
            }*/
        }


        if (txt_input.getText().toString().trim().length() > 0 && txt_input.getText().toString().trim().length() < 4) {
            Toasty.error(mContext, "Ref# or Serial too short!", 3000, true).show();
            return;
        }

        String search_str = "";
        search_str = txt_input.getText().toString().trim();

        String is_admin = "0";
        if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

//        urlParam += "&t=" + _filter_type;
//
//
//        if (txt_input.getText().toString().trim().Length > 0)
//        {
//            urlParam += "&f=" + txt_input.getText().toString().trim();
//        }

        //Timber.i("REPRINT: " + Topitup.TIU_LICENSE + ", posuserid: " +  Topitup.POSUSER_ID+ " ,is_admin: " + is_admin);
        //  Toasty.info(mContext, String.valueOf(_filter_type), 8000, true).show();
        final Call<ResponseBody> call;
//date.getText().toString()

        call = apiService.getswipesalesnew(Topitup.TIU_LICENSE, dt, "100");

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Timber.i("REPRINT: res- " + response.body().toString());

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
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    dialog.dismiss();
                    return;
                }


                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String err_msg = "No Results.";

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.contains("ERR:")) {
                        err_msg = res.replace("ERR:", "");
                    }

                    Toasty.info(mContext, err_msg, 8000, true).show();
                    dialog.dismiss();
                    return;

                }


                if (!res.contains("^")) {
                    Toasty.info(mContext, "No Results...", 8000, true).show();
                    dialog.dismiss();
                    return;
                }
                int rowCount = 0;
                int txn_tot_approved = 0;
                int txn_tot_pending = 0;
                int txn_tot_declined_cancelled = 0;
                double txn_tot_approved_amount = 0.00;
                double txn_tot_pending_amount = 0.00;
                double txn_tot_declined_cancelled_amount = 0.00;
                double txn_tot_amount = 0.00;

                try {


                    Timber.i("REPRINT RES: " + res);


                    String[] toSplit = res.split("\n");

                    for (int i = 0; i < toSplit.length; i++) {

                        String s = toSplit[i];
                        rowCount++;
                        String[] tokens = s.split("\\^");
                        Timber.i("tokens" + i + " value" + tokens[3]);
                        MyItemSwipe test = new MyItemSwipe();


                        double amount = 0.00;
                        test.stock_uid = tokens[4];
                        try {
                            String value = tokens[3];
                            String val = String.format("%.2f", Float.parseFloat(value));
                            txn_tot_amount += Float.parseFloat(value);
                            amount = Float.parseFloat(value);
                            test.btn_description = tokens[2];
                            test.txnamount = "R " + val.replace(",", ".");
                        } catch (Exception ex) {
                            test.btn_description = tokens[2];
                        }
                        // TextView btn_description = (TextView) findViewById(R.id.btn_description);
                        // Timber.i("REPRINT: " + value);
                        test.btn_date = tokens[1];
                        test.btn_user = tokens[5];
                        test.uid = tokens[6];
                        if (test.uid.equals("3")) {
                            test.imageViewFlag = R.drawable.cancelled;
                            test.btn_user = "Cancelled";
                            txn_tot_declined_cancelled++;
                            txn_tot_declined_cancelled_amount += amount;
                            //btn_description.setPaintFlags(btn_description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }  else if (test.uid.equals("6")) {
                            test.imageViewFlag = R.drawable.declined;
                            test.btn_user = "Approved Not Settled";
                            txn_tot_declined_cancelled++;
                            txn_tot_declined_cancelled_amount += amount;
                        }

                        else if (test.uid.equals("2")) {
                            test.imageViewFlag = R.drawable.declined;
                            test.btn_user = "Declined";
                            txn_tot_declined_cancelled++;
                            txn_tot_declined_cancelled_amount += amount;
                        } else if (test.uid.equals("0")) {
                            test.imageViewFlag = R.drawable.pending;
                            test.btn_user = "Pending";
                            txn_tot_pending_amount += amount;
                            txn_tot_pending++;
                        } else if (test.btn_user.equals("Settled") && test.uid.equals("1")) {
                            test.imageViewFlag = R.drawable.settled;
                            txn_tot_approved++;
                            txn_tot_approved_amount += amount;
                        }
                        //String txnid=tokens[4].toString();
                        // if(TextUtils.isEmpty(txnid))
                        test.uid = tokens[2];
                        //Toasty.info(mContext, "txnsts"+txnsts+" test.btn_user="+ test.btn_user, 8000, true).show();


                        if (txnsts.equals("100"))
                            movies.add(test);
                        else if (txnsts.equals("1") && test.btn_user.equals("Settled"))
                            movies.add(test);
                        else if (txnsts.equals("0") && test.btn_user.equals("Pending"))
                            movies.add(test);
                        else if (txnsts.equals("2") && (test.btn_user.equals("Cancelled") || test.btn_user.equals("Declined")))
                            movies.add(test);
                        //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());

                    }


                    listAdapter = new ReprintListSwipe(mContext, 0, movies);
                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
                    lst_reprint.setAdapter(listAdapter);

                    lst_reprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (movies.get(position).btn_user.equals("Settled")) {
                                getReprint(4, movies.get(position).stock_uid);

                            } else if (movies.get(position).btn_user.equals("Pending")) {
                                pid = movies.get(position).uid;
                                txnid = movies.get(position).stock_uid;
                                Intent intent = new Intent();

                                intent.setPackage("com.wiseasy.cashier");
                                intent.setAction("com.wiseasy.transaction.call");
                                intent.putExtra("version", "A01");
                                intent.putExtra("appId", "wza61f2e0da04ff7f8");
                                intent.putExtra("transType", "QUERY");
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("businessOrderNo", pid);
                                    intent.putExtra("transData", jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivityForResult(intent, 1);


                            }
                            //Toasty.info(mContext, "ID " +  String.valueOf( movies.get(position).stock_uid), 8000, true).show();
                            //Toast.makeText(MainActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (Exception ex) {
                    Toasty.info(mContext, "No Results...." + ex, 8000, true).show();
                }

                tot_sales.setText(String.valueOf(rowCount));
                tot_sales_amount.setText("R " + String.format("%.2f", txn_tot_amount));
                tot_txns_pending.setText(String.valueOf(txn_tot_pending));
                tot_txns_pending_amount.setText("R " + String.format("%.2f", txn_tot_pending_amount));
                tot_txns_approved.setText(String.valueOf(txn_tot_approved));
                tot_txns_approved_amount.setText("R " + String.format("%.2f", txn_tot_approved_amount));
                tot_txns_declined_cancelled.setText(String.valueOf(txn_tot_declined_cancelled));
                tot_txns_declined_cancelled_amount.setText("R " + String.format("%.2f", txn_tot_declined_cancelled_amount));
//                lst_reprint.setOnItemClickListener(new AdapterListBasic.OnItemClickListener()
//                {
//                    // argument position gives the index of item which is clicked
//                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
//                    {
//                        String selectedmovie=movies.get(position);
//                        //Toast.makeText(getApplicationContext(), "Movie Selected : "+selectedmovie,   Toast.LENGTH_LONG).show();
//                    }
//                });


                dialog.dismiss();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String transType = "SALE";
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }
        adpay_new.orderno = pid;
        String result = data.getStringExtra("result");
        // Toasty.error(mContext, ""+result, 1000, true).show();
        String resultMsg = data.getStringExtra("resultMsg");
        transType = data.getStringExtra("transType");
        if (TextUtils.isEmpty(result)) {

            adpay_new.updateordernosts("3");

        } else if (result.equals("M001")) {
            Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.wiseasy.cashier");
            //    notificationIntent.setPackage(null); // The golden row !!!
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (notificationIntent != null) {
                startActivity(notificationIntent);
            } else {
                Toasty.error(mContext, "Cashier App is not Installed!!!", 25000).show();
            }

        } else if (result.equals("M007")) {
            if (resultMsg.equals(""))
                adpay_new.updateordernosts("2");
            else
                adpay_new.updateordernosts("3");
            Intent intent = new Intent(mContext, activity_main.class);
            //intent.putExtra("URL", Topitup.BASE_URL_SYNC + "info/more_concat_details/?l=" + Topitup.TIU_LICENSE);
            startActivity(intent);

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
                        //  String expDate = jsonObject.getString("expDate");
                        // String merchantID = jsonObject.getString("merchantID");
                        adpay_new.refer_no = jsonObject.getString("refNo");
                        //  String terminalID = jsonObject.getString("terminalID");
                        adpay_new.voucher_no = jsonObject.getString("traceNo");
                        String transDate = jsonObject.getString("transDate");
                        String respCode = jsonObject.getString("respCode");
                        adpay_new.trans_time = transDate + " " + jsonObject.getString("transTime");
                        if (respCode.equals("000") || respCode.equals("00")) {
                            adpay_new.callAddpayBackSettle();
                        } else {
                            adpay_new.updateordernosts("2");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        movies.clear();
                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        //
                    }
                    PopulateLastSales();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (adpay_new.txnsuccess) {

                                getReprint(4, txnid);
                            } else {
                                Toasty.error(mContext, "txn is already settled. please contact Top it Up.", 5000, true).show();

                            }
                        }
                    }, 5000);


                } else {
                    adpay_new.updateordernosts("2");
                    try {
                        movies.clear();
                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        //
                    }
                    PopulateLastSales();

                }
            } catch (Exception ex) {

                Toasty.error(mContext, ex.getMessage(), 8000);
            }
        }
        // getReprint(4, pid);
    }


    private void getReprint(int type, final String stock_uid) {

        String ret = "";
        String htmlResponse = "";


        Intent myIntent = new Intent(mContext, activity_addpay_txnview.class);
        Bundle brepo = new Bundle();
        brepo.putString("pid", stock_uid); //Your id
        myIntent.putExtras(brepo); //Put your id to your next Intent
        startActivity(myIntent);


    }


    public void doReprintLogic(String service_provider_item_id, String stock_uid) {

        if (service_provider_item_id.equals("51")) {

            final Call<ResponseBody> call = apiService.vendGetReprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.TIU_LICENSE, stock_uid, 1, 0);

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
                        //
                        if (response.body() != null)
                        response.body().close();
                    }


                    if (res.toLowerCase().contains("<error><err>") || res.toLowerCase().contains("err:")) {


                    } else {

                        String _elecVoucher = "";

                        int rowCount = 0;
                        String[] toSplit = res.split("\n");
                        for (int i = 0; i < toSplit.length; i++) {

                            String s = toSplit[i];
                            rowCount++;

                            if (rowCount > 1) {
                                _elecVoucher += "\n" + s;
                            }
                        }


                        printingCustomDialog("Printing", "Your voucher is busy printing.\nYou have been charged, please check reprint if there is an issue.");

                        //Printer.store_last_reprint(res.toString());
                        //Printer.print_data(res.toString());
                        //Printer.store_last_reprint(_elecVoucher);
                        PrinterTopitup.print_data(_elecVoucher);


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


        } else if (service_provider_item_id.equals("107") || service_provider_item_id.equals("163")) {


            //String htmlResponse = WebResponse.GetURL("/payaccount/payment/reprint?q=" + globalVoucher.stock_uid);

            final Call<voucher_response> call = apiService.billpayment_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid);

            call.enqueue(new Callback<voucher_response>() {

                @Override
                public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                    if(response.isSuccessful()) {
                        try {
                            if (!response.headers().get("Server").equals("TIU")) {
                                Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                                return;
                            }

                            voucher_response res = response.body();

                            //Timber.e("REPRINT " + res.toString());

                            byte[] bytes = android.util.Base64.decode(res.print_data, android.util.Base64.DEFAULT);
                            //String stringValueBase64Decoded = Base64.encodeToString(bytes, Base64.NO_WRAP);

                            String stringValueBase64Decoded = new String(bytes);

                            //Toasty.info(mContext, stringValueBase64Decoded, 8000, true).show();

                            //Printer.store_last_reprint(stringValueBase64Decoded);
                            PrinterTopitup.print_data(stringValueBase64Decoded);

                        } catch (Exception e) {
                           /* if (response.raw() != null)
                                response.raw().close();*/
                        }
                    }else{
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

        } else {


            final Call<voucher_response> call = apiService.airtime_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid, Topitup.DEVICE_TYPE);

            call.enqueue(new Callback<voucher_response>() {

                @Override
                public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {


                   /* if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }
*/
                    if(response.isSuccessful()) {
                        try {
                            voucher_response result = response.body();

                            //Timber.e(result.toString());

                            if (result.err.length() > 0) {

                                //

                            } else {        //Response OK

                                //Printer.store_last_reprint(result.print_data);
                                String slip = result.print_data;

                                int spi = Integer.parseInt(result.spi);
                                //Toasty.info(mContext,"print_address"+print_address, Toast.LENGTH_SHORT).show();

                                if (Topitup.PRINT_BARCODE.equals("1")) {

                                    //  String item_desc=bufReader.read(7);
                                    service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi).findAll();
                                    // Toasty.info(mContext,"item_desc"+spi, Toast.LENGTH_SHORT).show();
                                    size = service_provider_items.size();
                                    // Toasty.info(mContext,"item_desc"+item_desc, Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i < size; i++) {
                                        service_provider_item product = service_provider_items.get(i);
                                        spi_barcode = product.item_barcode;
                                        //     Toasty.info(mContext,"spi_barcode"+spi_barcode, Toast.LENGTH_SHORT).show();
                                        //Timber.i("spi_barcode=" + spi_barcode);
                                    }
                                    // Timber.i("print_line_counter="+print_line_counter);

                                    //  spi_barcode="123451";

                                    if (spi_barcode != null && !spi_barcode.equals("")) {
                                        if (print_address.equals("1"))
                                            slip = slip.replace("1\n1Trx", "1" + company_addrs1 + ", " + company_addrs2 + "\n1\n1Trx");
                                        slip = slip + "BARCODE:" + spi_barcode;
                                    }
                                }
                                //  Timber.i(slip);
                                PrinterTopitup.print_data(slip);


                            }
                        } catch (Exception e) {
                          /*  if (response.raw() != null)
                                response.raw().close();*/
                        }
                    }else{
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

    }


    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
ProgressBar progressBar;
    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

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
        bt_close = dialog.findViewById(R.id.bt_close);
        progressBar=dialog.findViewById(R.id.progressBar);
        if (sTitle.equalsIgnoreCase("")) {
            pop_title.setText("Requesting");
            pop_content.setText("please wait...");
            bt_close.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            pop_title.setText(sTitle);
            pop_content.setText(sContent);
            bt_close.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };


    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }


}
