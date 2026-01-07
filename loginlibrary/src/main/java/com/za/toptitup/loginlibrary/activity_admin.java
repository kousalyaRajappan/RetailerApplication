package com.za.toptitup.loginlibrary;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import za.co.topitup.activitySplashScreen;

import com.za.toptitup.loginlibrary.admin.Printingw;
import com.za.toptitup.loginlibrary.admin.activity_banktransfer;
import com.za.toptitup.loginlibrary.admin.activity_credit;
import com.za.toptitup.loginlibrary.admin.activity_depositlip;
import com.za.toptitup.loginlibrary.admin.activity_otp;
import com.za.toptitup.loginlibrary.admin.activity_paymenthistory;
import com.za.toptitup.loginlibrary.admin.activity_quick_launch;
import com.za.toptitup.loginlibrary.admin.activity_settings;
import com.za.toptitup.loginlibrary.admin.activity_transfer;
import com.za.toptitup.loginlibrary.admin.activity_users;
import com.za.toptitup.loginlibrary.admin.activity_wallettransfer;
import com.za.toptitup.loginlibrary.admin.activity_zhistory;
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.MyItem;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.serviceProviderQuick;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.supplierCashmx;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UserException;

public class activity_admin extends BaseAdminActivity implements HomeAdapter.ItemListener, HomeAdapterReports.ItemListener {

    // private String[] NAME = {"X-Intraday", "X-Intraday Cashier Detail", "X-Intraday Cashier Summary", "End-of-Day Z", "Z Report History", "Daily Sales Report", "Monthly Sales Report"};
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    public static String setting_printer_bypass;
    private final Integer[] IMAGE3 = new Integer[]{R.drawable.monthly_swipe_detail, R.drawable.dialy_swipe_settlement, R.drawable.monthly_swipe_summary, R.drawable.daily_swipe_summary,};
    private final String[] NAME3 = new String[]{"Monthly Swipe Detail", "Daily Swipe Settlement", "Monthly Swipe Summary", "Daily Swipe Summary"};
    BottomNavigationView mBottomNav;
    RealmResults<service_provider_item> service_provider_items;
    RealmResults<serviceProviderQuick> tiu_serviceProviderQuick;
    TextView textViewheader, txtexit, txtCashReports;
    LinearLayout linSwipes, linSales;
    String prefix = "B", spiver;
    Context mContext;
    List<String> SPList;
//    private String[] NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", "Dialy Commission Statement", "Commission Statement"};
    String enable_rpt_monthly_deposit = "0", enable_rpt_comm_statement = "0", enable_rpt_monthly_sales;
    Realm realm;
    int sales;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    RealmResults<fin_balance> tiu_fin_balance;
    String enable_remote_ext_credit;
    //***
    RealmResults<service_provider_item_settings> service_provider_item_settings;
    int size;
    com.za.toptitup.loginlibrary.model.service_provider_item_settings service_provider_item_setting;
    int fin_opt;
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    ProgressBar progressBar;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
        }
    };
    private RecyclerView recyclerView, recyclerView2, recyclerView3;
    private ArrayList<Item> arrayList, arrayList2, arrayList3;
    private String[] NAMEQ, DENO, BARCODE;
    private int[] SPITEMID;
    /*  private Integer[] IMAGE = {
              R.drawable.x_intraday,
              R.drawable.x_intraday_cashier_detail,
              R.drawable.x_intraday_cashier_summary,
              R.drawable.ic_z_report,
              R.drawable.ic_z_history,
              R.drawable.daily_sales_report,
              R.drawable.monthly_sales_report,

      };*/
    private Integer[] IMAGE2 = new Integer[]{
            R.drawable.daily_sales_report,
            R.drawable.monthly_sales_report,
            R.drawable.dialy_commission_statement,
            R.drawable.commision_statement_,
    };
    private String[] NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", " Daily Commission Statement", "Monthly Commission Statement"};
    private Integer[] IMAGE = new Integer[]{
            //   R.drawable.get_balance,
            R.drawable.view_invoices,
            R.drawable.financial_transactions,
            R.drawable.store_transfer,
            R.drawable.wallet_transfer,
            R.drawable.transfer_to_bank,
            R.drawable.deposit_slip,
            R.drawable.deposit__history,

            R.drawable.temp_credit,
            R.drawable.retailer_payments

    };
    //  private String[] NAME = {"Get Balance", "View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Swipe", "Retailer Payments"};
    private String[] NAME = {"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};
    private CardView cardview;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        Log.e("action", "activity admin oncreate");
        activity_login.fromScreen = "activity_admin";
        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView3 = findViewById(R.id.recyclerView3);
        linSwipes = findViewById(R.id.linswipe);
        linSales = findViewById(R.id.linSales);
        txtCashReports = findViewById(R.id.txtCashReports);
        textViewheader = findViewById(R.id.textViewheader);
        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_financial);
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
        cardview = findViewById(R.id.cardView);
     /* TextView  mIsDemo = (TextView) findViewById(R.id.activity_login_is_demo);
        if (!Topitup.DEBUG) {
            mIsDemo.setVisibility(View.INVISIBLE);
        }else {
            mIsDemo.setVisibility(View.VISIBLE);
        }*/
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();
        final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();


        if (tiu_settings == null) {

            spiver = "0";
        } else {

            spiver = tiu_settings.spi_ver;
        }

        enable_remote_ext_credit = tiu_fin_balance.enable_remote_ext_credit;
        enable_rpt_monthly_deposit = tiu_settings1.enable_rpt_monthly_deposit;
        enable_rpt_comm_statement = tiu_settings1.enable_rpt_comm_statement;
        enable_rpt_monthly_sales = tiu_settings1.enable_rpt_monthly_sales;
        Log.e("enable ", "credit admin...." + enable_remote_ext_credit);
        if (enable_rpt_monthly_deposit == null)
            enable_rpt_monthly_deposit = "0";

        if (enable_rpt_comm_statement == null)
            enable_rpt_comm_statement = "0";

        if (enable_rpt_monthly_sales == null)
            enable_rpt_monthly_sales = "0";
        //Toasty.error(mContext, "enable_rpt_comm_statement="+ enable_rpt_comm_statement, 8000, true).show();

        /* TIU HEADER */
         settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        setting_printer_bypass = settings.getString("setting_print_bypass", "0");
        final String enable_commission = settings.getString("enable_commission", "1");
        String setting_mpos_disclaimer = settings.getString("setting_mpos_disclaimer", "0");

        if (enable_commission.equals("0")) {
            IMAGE2 = new Integer[]{
                    R.drawable.daily_sales_report,
                    R.drawable.monthly_sales_report,
                    R.drawable.commision_statement_,
            };
//    private String[] NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", "Dialy Commission Statement", "Commission Statement"};


            NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", "Monthly Commission Statement"};

        } else {
            IMAGE2 = new Integer[]{
                    R.drawable.daily_sales_report,
                    R.drawable.monthly_sales_report,
                    R.drawable.dialy_commission_statement,

            };
//    private String[] NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", "Dialy Commission Statement", "Commission Statement"};


            NAME2 = new String[]{"Daily Sales Report", "Monthly Sales Report", " Daily Commission Statement"};

        }
        if (setting_mpos_disclaimer.equals("1") || Topitup.DEVICE_TYPE.equals("WPOS")) {
            IMAGE = new Integer[]{
                    // R.drawable.get_balance,
                    R.drawable.view_invoices,
                    R.drawable.financial_transactions,
                    R.drawable.store_transfer,
                    R.drawable.wallet_transfer,
                    R.drawable.transfer_to_bank,
                    R.drawable.deposit_slip,
                    R.drawable.deposit__history,

                    R.drawable.temp_credit,
                    R.drawable.retailer_payments

            };


            //NAME = new String[]{"Get Balance", "View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Swipe", "Retailer Payments"};
            NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};

        } else {
            IMAGE = new Integer[]{
                    //   R.drawable.get_balance,
                    R.drawable.view_invoices,
                    R.drawable.financial_transactions,
                    R.drawable.store_transfer,
                    R.drawable.wallet_transfer,
                    R.drawable.transfer_to_bank,
                    R.drawable.deposit_slip,
                    R.drawable.deposit__history,
                    R.drawable.temp_credit,
                    R.drawable.retailerpayments

            };
            NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};

            // NAME = new String[]{"Get Balance", "View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History",  "Request Credit", "Retailer Payments"};
        }


        SPList = new ArrayList<>((Arrays.asList(NAME)));
        ArrayList IMAGEList = new ArrayList<>((Arrays.asList(IMAGE)));
        Log.e("enable ", "credit admin...." + enable_remote_ext_credit);

        if (enable_remote_ext_credit.equals("0")) {
            int pos = SPList.indexOf("Request Credit");
            SPList.remove(pos);
            IMAGEList.remove(pos);
        }

        if (Topitup.RETAILER_TYPE != 10) {
            int pos = SPList.indexOf("Retailer Payments");
            SPList.remove(pos);
            IMAGEList.remove(pos);
        }

        if (enable_rpt_monthly_deposit.equals("0")) {
            int pos = SPList.indexOf("Monthly Deposit Statement");
            Log.e("monthly deposit", pos + "size........" + SPList.size());

            if (pos > 0) {
                SPList.remove(pos);
                IMAGEList.remove(pos);
            }

        }


        if (enable_rpt_comm_statement.equals("0")) {
            int pos = SPList.indexOf("Commission Statement");
            if (pos > 0) {
                SPList.remove(pos);
                IMAGEList.remove(pos);
            }
        }
        IMAGE = (Integer[]) IMAGEList.toArray(new Integer[SPList.size()]);

        NAME = SPList.toArray(new String[SPList.size()]);
        fin_opt = SPList.size();

        tabs();
        FullscreenCall();

        GridLayoutManager manager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
//        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
//        GridLayoutManager manager2 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        GridLayoutManager manager2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager2 = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            manager2 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
        recyclerView2.setLayoutManager(manager2);
        GridLayoutManager manager3;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager3 = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            manager3 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
//        GridLayoutManager manager3 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(manager3);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {
                    case R.id.action_back:
                        onBackPressed();
                        return true;
                    case R.id.action_report:
                        Log.e("action", "bottom report");
                        linSales.setVisibility(View.VISIBLE);
                        linSwipes.setVisibility(View.VISIBLE);
                        txtCashReports.setVisibility(View.VISIBLE);
                        prefix = "A";
                        IMAGE = new Integer[]{
                                R.drawable.x_intraday,
                                R.drawable.x_intraday_cashier_detail,
                                R.drawable.x_intraday_cashier_summary,
                                R.drawable.ic_z_report,
                                R.drawable.ic_z_history,
                        };


// NAME = new String[]{"X-Intraday","X-Intraday Cashier Detail","X-Intraday Cashier Summary","End-of-Day Z","Z Report History","Monthly Sales Report"};
                        NAME = new String[]{"X-Intraday", "X-Intraday Cashier Detail", "X-Intraday Cashier Summary", "End-of-Day Z", "Z Report History",};

                        tabs();


                        return true;

                    case R.id.action_financial:
                        linSales.setVisibility(View.GONE);
                        linSwipes.setVisibility(View.GONE);
                        txtCashReports.setVisibility(View.GONE);
                        prefix = "B";

                        String setting_mpos_disclaimer = settings.getString("setting_mpos_disclaimer", "0");
                        if (setting_mpos_disclaimer.equals("1") || Topitup.DEVICE_TYPE.equals("WPOS")) {
                            IMAGE = new Integer[]{
                                    //    R.drawable.get_balance,
                                    R.drawable.view_invoices,
                                    R.drawable.financial_transactions,
                                    R.drawable.store_transfer,
                                    R.drawable.wallet_transfer,
                                    R.drawable.transfer_to_bank,
                                    R.drawable.deposit_slip,
                                    R.drawable.deposit__history,

                                    R.drawable.temp_credit,
                                    R.drawable.retailer_payments

                            };


                            //     NAME = new String[]{"Get Balance", "View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Swipe", "Retailer Payments"};
                            NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};

                        } else {
                            IMAGE = new Integer[]{
                                    //  R.drawable.get_balance,
                                    R.drawable.view_invoices,
                                    R.drawable.financial_transactions,
                                    R.drawable.store_transfer,
                                    R.drawable.wallet_transfer,
                                    R.drawable.transfer_to_bank,
                                    R.drawable.deposit_slip,
                                    R.drawable.deposit__history,
                                    R.drawable.temp_credit,
                                    R.drawable.retailerpayments

                            };
                            NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};

                            //   NAME = new String[]{"Get Balance", "View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History",  "Request Credit", "Retailer Payments"};
                        }
                        SPList = new ArrayList<>((Arrays.asList(NAME)));
                        ArrayList IMAGEList = new ArrayList<>((Arrays.asList(IMAGE)));


                        if (enable_remote_ext_credit.equals("0")) {
                            int pos = SPList.indexOf("Request Credit");
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }

                        if (Topitup.RETAILER_TYPE != 10) {
                            int pos = SPList.indexOf("Retailer Payments");
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }

                        if (enable_rpt_monthly_deposit.equals("0")) {
                            int pos = SPList.indexOf("Monthly Deposit Statement");
                            Log.e("monthly deposit", pos + "size........" + SPList.size());

                            if (pos > 0) {
                                SPList.remove(pos);
                                IMAGEList.remove(pos);
                            }
                         *//*   SPList.remove(pos);
                            IMAGEList.remove(pos);*//*

                        }

                        if (enable_rpt_comm_statement.equals("0")) {
                            int pos = SPList.indexOf("Commission Statement");
                            Log.e("Commission Statement", pos + "size........" + SPList.size());

                            if (pos > 0) {
                                SPList.remove(pos);
                                IMAGEList.remove(pos);
                            }
                        *//*    SPList.remove(pos);
                            IMAGEList.remove(pos);*//*

                        }
                        IMAGE = (Integer[]) IMAGEList.toArray(new Integer[SPList.size()]);
                        NAME = SPList.toArray(new String[SPList.size()]);
                        fin_opt = SPList.size();
                        Log.e("enable ext cresit", NAME.length + "status of temp...." + enable_remote_ext_credit);


                        tabs();
                        return true;
                    case R.id.action_users:
                        linSales.setVisibility(View.GONE);
                        linSwipes.setVisibility(View.GONE);
                        txtCashReports.setVisibility(View.GONE);
                        prefix = "C";
                        IMAGE = new Integer[]{R.drawable.users, R.drawable.cashier_settings, R.drawable.custion_products};
                        //      IMAGE = new Integer[]{ R.drawable.admin_user_orig,R.drawable.settings};

                        // NAME = new String[]{"Users","Cashier Settings","Customised Product"};
                        NAME = new String[]{"Users", "Cashier Settings", "Customised Product"};
                        tabs();

                        return true;
                    case R.id.action_setting:
                        linSales.setVisibility(View.GONE);
                        linSwipes.setVisibility(View.GONE);
                        txtCashReports.setVisibility(View.GONE);
                        prefix = "D";
                        if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                            IMAGE = new Integer[]{R.drawable.general_settings, R.drawable.print_and_slip_settings, R.drawable.quick_launch_settings, R.drawable.update_management, R.drawable.ping_tools, R.drawable.restart_app, R.drawable.swipe_settings, R.drawable.otp, R.drawable.test_print, R.drawable.salaah_app_01};
                            NAME = new String[]{"General Settings", "Print&Slip Settings", "Quick Launch Settings", "Update Management", "Ping Tools", "Restart App", "SWIPE Settings", "OTP", "Test Print", "Salaah App"};
                        } else {
                            IMAGE = new Integer[]{R.drawable.general_settings, R.drawable.print_and_slip_settings, R.drawable.quick_launch_settings, R.drawable.update_management, R.drawable.ping_tools, R.drawable.restart_app, R.drawable.swipe_settings, R.drawable.otp, R.drawable.test_print, R.drawable.salaah_app_01};
                            NAME = new String[]{"General Settings", "Print&Slip Settings", "Quick Launch Settings", "Update Management", "Ping Tools", "Restart App", "SWIPE Settings", "OTP", "Test Print", "Salaah App"};
                        }

                        boolean isAppInstalled = appInstalledOrNot("za.co.topitup.salaahapp");

                        if (!isAppInstalled) {
                            List<String> namelist = new ArrayList<String>();
                            List<Integer> imagelist = new ArrayList<Integer>();

                            namelist.addAll(Arrays.asList(NAME));
                            imagelist.addAll(Arrays.asList(IMAGE));
                            int pos = namelist.indexOf("Salaah App");

                            namelist.remove(pos);
                            imagelist.remove(pos);

                            NAME = namelist.toArray(new String[namelist.size()]);
                            IMAGE = imagelist.toArray(new Integer[namelist.size()]);

                        }

                        tabs();


                        return true;
                    default:

                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_report) {
                    Log.e("action", "bottom report");
                    linSales.setVisibility(View.VISIBLE);
                    linSwipes.setVisibility(View.VISIBLE);
                    txtCashReports.setVisibility(View.VISIBLE);
                    prefix = "A";
                    IMAGE = new Integer[]{
                            R.drawable.x_intraday,
                            R.drawable.x_intraday_cashier_detail,
                            R.drawable.x_intraday_cashier_summary,
                            R.drawable.ic_z_report,
                            R.drawable.ic_z_history,
                    };
                    NAME = new String[]{"X-Intraday", "X-Intraday Cashier Detail", "X-Intraday Cashier Summary", "End-of-Day Z", "Z Report History",};
                    tabs();
                    return true;
                } else if (item.getItemId() == R.id.action_financial) {
                    linSales.setVisibility(View.GONE);
                    linSwipes.setVisibility(View.GONE);
                    txtCashReports.setVisibility(View.GONE);
                    prefix = "B";

                    String setting_mpos_disclaimer = settings.getString("setting_mpos_disclaimer", "0");
                    if (setting_mpos_disclaimer.equals("1") || Topitup.DEVICE_TYPE.equals("WPOS")) {
                        IMAGE = new Integer[]{
                                R.drawable.view_invoices,
                                R.drawable.financial_transactions,
                                R.drawable.store_transfer,
                                R.drawable.wallet_transfer,
                                R.drawable.transfer_to_bank,
                                R.drawable.deposit_slip,
                                R.drawable.deposit__history,
                                R.drawable.temp_credit,
                                R.drawable.retailer_payments
                        };
                        NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};
                    } else {
                        IMAGE = new Integer[]{
                                R.drawable.view_invoices,
                                R.drawable.financial_transactions,
                                R.drawable.store_transfer,
                                R.drawable.wallet_transfer,
                                R.drawable.transfer_to_bank,
                                R.drawable.deposit_slip,
                                R.drawable.deposit__history,
                                R.drawable.temp_credit,
                                R.drawable.retailerpayments
                        };
                        NAME = new String[]{"View Invoices", "View Financial Transactions", "Store Transfer", "Wallet Transfer", "Banking Detail", "Deposit Slip", "Deposit History", "Request Credit", "Retailer Payments"};
                    }

                    SPList = new ArrayList<>(Arrays.asList(NAME));
                    ArrayList<Integer> IMAGEList = new ArrayList<>(Arrays.asList(IMAGE));

                    if (enable_remote_ext_credit.equals("0")) {
                        int pos = SPList.indexOf("Request Credit");
                        if (pos >= 0) {
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }
                    }

                    if (Topitup.RETAILER_TYPE != 10) {
                        int pos = SPList.indexOf("Retailer Payments");
                        if (pos >= 0) {
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }
                    }

                    if (enable_rpt_monthly_deposit.equals("0")) {
                        int pos = SPList.indexOf("Monthly Deposit Statement");
                        if (pos >= 0) {
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }
                    }

                    if (enable_rpt_comm_statement.equals("0")) {
                        int pos = SPList.indexOf("Commission Statement");
                        if (pos >= 0) {
                            SPList.remove(pos);
                            IMAGEList.remove(pos);
                        }
                    }

                    IMAGE = IMAGEList.toArray(new Integer[0]);
                    NAME = SPList.toArray(new String[0]);
                    fin_opt = SPList.size();
                    Log.e("enable ext credit", NAME.length + " status of temp...." + enable_remote_ext_credit);

                    tabs();
                    return true;
                } else if (item.getItemId() == R.id.action_users) {
                    linSales.setVisibility(View.GONE);
                    linSwipes.setVisibility(View.GONE);
                    txtCashReports.setVisibility(View.GONE);
                    prefix = "C";
                    IMAGE = new Integer[]{R.drawable.users, R.drawable.cashier_settings, R.drawable.custion_products};
                    NAME = new String[]{"Users", "Cashier Settings", "Customised Product"};
                    tabs();
                    return true;
                } else if (item.getItemId() == R.id.action_setting) {
                    linSales.setVisibility(View.GONE);
                    linSwipes.setVisibility(View.GONE);
                    txtCashReports.setVisibility(View.GONE);
                    prefix = "D";
                    IMAGE = new Integer[]{
                            R.drawable.general_settings,
                            R.drawable.print_and_slip_settings,
                            R.drawable.quick_launch_settings,
                            R.drawable.update_management,
                            R.drawable.ping_tools,
                            R.drawable.restart_app,
                            R.drawable.swipe_settings,
                            R.drawable.otp,
                            R.drawable.test_print,
                            R.drawable.salaah_app_01
                    };
                    NAME = new String[]{
                            "General Settings",
                            "Print&Slip Settings",
                            "Quick Launch Settings",
                            "Update Management",
                            "Ping Tools",
                            "Restart App",
                            "SWIPE Settings",
                            "OTP",
                            "Test Print",
                            "Salaah App"
                    };

                    boolean isAppInstalled = appInstalledOrNot("za.co.topitup.salaahapp");
                    if (!isAppInstalled) {
                        List<String> namelist = new ArrayList<>(Arrays.asList(NAME));
                        List<Integer> imagelist = new ArrayList<>(Arrays.asList(IMAGE));
                        int pos = namelist.indexOf("Salaah App");

                        if (pos >= 0) {
                            namelist.remove(pos);
                            imagelist.remove(pos);
                        }

                        NAME = namelist.toArray(new String[0]);
                        IMAGE = imagelist.toArray(new Integer[0]);
                    }

                    tabs();
                    return true;
                } else {
                    return true;
                }

            }

        });


    }

    private void tabs() {
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        String p = "1";
        if (prefix.equals("A")) {
            p = "2";
            textViewheader.setText("REPORTS");
            SPList = new ArrayList<>((Arrays.asList(NAME)));
            //Toasty.error(mContext, ""+ SPList.contains("cellc"), 8000, true).show();

           /* if (enable_rpt_monthly_sales.equals("0")) {

                int pos = SPList.indexOf("Monthly Sales Report");

                SPList.remove(pos);
            }*/
            NAME = SPList.toArray(new String[SPList.size()]);
        }
        if (prefix.equals("B")) {
            textViewheader.setText("FINANCIAL MANAGEMENT");
            p = "1";
        }
        if (prefix.equals("C")) {
            textViewheader.setText("USER MANAGEMENT");
            p = "3";

        }
        if (prefix.equals("D")) {
            textViewheader.setText("SETTINGS");
            p = "4";
        }
        if (prefix.equals("E")) {
            textViewheader.setText("UPDATE MANAGEMENT");
            p = "4.5";

        }
        if (prefix.equals("F")) {

            textViewheader.setText("SWIPE");
            p = "2." + (fin_opt - 1);

        }

       /* if (Topitup.RETAILER_TYPE != 10) {
            int pos = SPList.indexOf("Retailer Payments");
            SPList.remove(pos);
            IMAGEList.remove(pos);
        }*/


        for (int i = 0; i < NAME.length; i++) {
            arrayList.add(new Item(NAME[i], prefix + i, IMAGE[i], "#FFFFFF", p + "." + i));
        }
        for (int i = 0; i < NAME2.length; i++) {
            arrayList2.add(new Item(NAME2[i], prefix + (NAME.length + i), IMAGE2[i], "#FFFFFF", p + "." + (NAME.length + i)));
        }
        for (int i = 0; i < NAME3.length; i++) {

            arrayList3.add(new Item(NAME3[i], prefix + (NAME.length + NAME2.length + i), IMAGE3[i], "#FFFFFF", p + "." + (NAME.length + NAME2.length + i)));

        }
        if (p == "2") {
            HomeAdapterReports adapter = new HomeAdapterReports(activity_admin.this, arrayList, activity_admin.this);
            recyclerView.setAdapter(adapter);
        } else {
            HomeAdapter adapter = new HomeAdapter(activity_admin.this, arrayList, activity_admin.this);
            recyclerView.setAdapter(adapter);
        }

        HomeAdapterReports adapter2 = new HomeAdapterReports(activity_admin.this, arrayList2, activity_admin.this);
        recyclerView2.setAdapter(adapter2);
        HomeAdapterReports adapter3 = new HomeAdapterReports(activity_admin.this, arrayList3, activity_admin.this);
        recyclerView3.setAdapter(adapter3);

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

    @Override
    public void onItemClick(Item item) {

        Log.e("item", "item position......." + item.pos);
        switch (item.pos) {
            case "A0":
                showCustomDialog("Cashup", "Requesting X Intraday");
                cashup_x_intraday();
                return;

            case "A1":
                showCustomDialog("Cashup", "Requesting X Intraday Cashier Detail");
                cashup_x_intraday_detail();
                return;

            case "A2":

                showCustomDialog("Cashup", "Requesting X Intraday Cashier Summary");
                cashup_x_intraday_summary();
                return;

            case "A3":

                showCustomDialog("Cashup", "Requesting Z End-of-day");
                z_end_of_day();
                return;
            case "A4":

                Intent myIntentZHistory = new Intent(mContext, activity_zhistory.class);
                startActivity(myIntentZHistory);
                return;
            case "A5":
                showCustomDialog("Report", "Daily Sales Report");
                sales = 0;
                Intent myintentsales = new Intent(mContext, activity_reports.class);
                Bundle brepo = new Bundle();
                brepo.putInt("key", 0); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
                return;
            //   sales_report();
            case "A6":
                showCustomDialog("Report", "Monthly Sales Report");
                myintentsales = new Intent(mContext, activity_reports.class);
                Bundle brepo1 = new Bundle();
                brepo1.putInt("key", 1); //Your id
                myintentsales.putExtras(brepo1); //Put your id to your next Intent
                startActivity(myintentsales);

                //sales_report();
                return;
            case "A7":
               /* if (item.text.equals("Monthly Deposit Statement")) {
                    showCustomDialog("Financial", "Requesting Monthly Deposit Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo2 = new Bundle();
                    brepo2.putInt("key", 3); //Your id
                    myintentsales.putExtras(brepo2); //Put your id to your next Intent
                    startActivity(myintentsales);
                }*/
                if (item.text.equals(" Daily Commission Statement")) {
                    showCustomDialog("Financial", "Requesting Commission Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo3 = new Bundle();
                    brepo3.putInt("key", 11); //Your id
                    myintentsales.putExtras(brepo3); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else if (item.text.equals("Monthly Commission Statement")) {
                    showCustomDialog("Financial", "Requesting Commission Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo3 = new Bundle();
                    brepo3.putInt("key", 4); //Your id
                    myintentsales.putExtras(brepo3); //Put your id to your next Intent
                    startActivity(myintentsales);
                }
                return;
            case "A8":
               /* if (item.text.equals("Monthly Commission Statement")) {
                    showCustomDialog("Financial", "Requesting Commission Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo3 = new Bundle();
                    brepo3.putInt("key", 4); //Your id
                    myintentsales.putExtras(brepo3); //Put your id to your next Intent
                    startActivity(myintentsales);
                }*/
                if (item.text.equals("Monthly Swipe Detail")) {
                    showCustomDialog("Financial", "Requesting Monthly Swipe Summary Report");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 5); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                }
                return;
            case "A9":

             /*   showCustomDialog("Financial", "Requesting Monthly Swipe Summary Report");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 5); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                showCustomDialog("Financial", "Requesting Swipe Daily Settlement");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 6); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);

                return;
            case "A10":
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                if (item.text.equals("Monthly Swipe Summary")) {
                    showCustomDialog("Financial", "Requesting Monthly Swipe Summary Report");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 5); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else {
                    showCustomDialog("Financial", "Requesting Swipe Daily Settlement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 6); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);

                }

                return;
            case "A11":
                if (item.text.equals("Daily Swipe Settlement")) {
                    showCustomDialog("Financial", "Requesting Swipe Daily Settlement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 6); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else if (item.text.equals("Daily Swipe Summary")) {
                    showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 9); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else {
                    showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 8); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                }
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                /*showCustomDialog("Financial", "Requesting Bank Card Transfer");
                myintentsales = new Intent(mContext, activity_banktransfer.class);
                brepo = new Bundle();
                brepo.putInt("key", 7); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
*/
                return;
            case "A12":
                if (item.text.equals("Monthly Swipe Summary")) {
                    showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 8); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else {
                    showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 9); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                }


                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                /*showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 8); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                return;

            case "A13":
                if (item.text.equals("Daily Swipe Summary")) {
                    showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    brepo = new Bundle();
                    brepo.putInt("key", 9); //Your id
                    myintentsales.putExtras(brepo); //Put your id to your next Intent
                    startActivity(myintentsales);
                }


                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                /*showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 8); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                return;
           /* case "B0":
                Intent myIntentBalance = new Intent(mContext, activity_transactions.class);
                startActivity(myIntentBalance);

                return;*/
            case "B0":
                //showCustomDialog("Financial","Requesting Invoices");
                Intent myIntentBalanceinvoie = new Intent(mContext, activity_invoice.class);
                Bundle binv = new Bundle();
                binv.putInt("key", 1); //Your id
                myIntentBalanceinvoie.putExtras(binv); //Put your id to your next Intent
                startActivity(myIntentBalanceinvoie);
                return;
            case "B1":
                Intent myIntentfin = new Intent(mContext, activity_invoice.class);
                Bundle bfin = new Bundle();
                bfin.putInt("key", 2); //Your id
                myIntentfin.putExtras(bfin); //Put your id to your next Intent
                startActivity(myIntentfin);
                return;
            case "B2":
                Intent myIntentTransfer = new Intent(mContext, activity_transfer.class);
                startActivity(myIntentTransfer);
                return;

            case "B3":
                Intent myIntentWallet = new Intent(mContext, activity_wallettransfer.class);
                startActivity(myIntentWallet);
                return;
            case "B4":
                Log.e("activity ", "banking");
                myintentsales = new Intent(mContext, activity_banktransfer.class);
                brepo = new Bundle();
                brepo.putInt("key", 7); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);

               /* Intent myIntentBanking = new Intent(mContext, activity_banking_detail.class);
                startActivity(myIntentBanking);*/
                return;
            case "B5":
                Intent depositslip = new Intent(mContext, activity_depositlip.class);
                startActivity(depositslip);
                return;
            case "B6":
                Intent myIntentPaymentHistory = new Intent(mContext, activity_paymenthistory.class);
                startActivity(myIntentPaymentHistory);
                return;
            case "B7":
                if (item.text.equals("Monthly Deposit Statement")) {
                    showCustomDialog("Financial", "Requesting Monthly Deposit Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo2 = new Bundle();
                    brepo2.putInt("key", 3); //Your id
                    myintentsales.putExtras(brepo2); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else if (item.text.equals("Commission Statement")) {
                    showCustomDialog("Financial", "Requesting Commmission Statement");

                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo3 = new Bundle();
                    brepo3.putInt("key", 4); //Your id
                    myintentsales.putExtras(brepo3); //Put your id to your next Intent
                    startActivity(myintentsales);
                } else if (item.text.equals("Request Credit")) {
                    myIntentPaymentHistory = new Intent(mContext, activity_credit.class);
                    startActivity(myIntentPaymentHistory);
                } else if (item.text.equals("Retailer Payments")) {
                    myIntentPaymentHistory = new Intent(mContext, RetailerPayments.class);
                    startActivity(myIntentPaymentHistory);

                }
            /* if (item.text.equals("Swipe") || Topitup.TIU_SERVER.equals("DEMO")) {

                    prefix = "F";
                    IMAGE = new Integer[]{R.drawable.monthly_swipe_detail1, R.drawable.dialy_swipe_settlement1, R.drawable.ic_credit_card_grey, R.drawable.monthly_swipe_summary1, R.drawable.daily_swipe_summary1, R.drawable.late_settlement};
                    NAME = new String[]{"Monthly Swipe Detail", "Daily Swipe Settlement", "Bank Card Transfer", "Monthly Swipe Summary", "Daily Swipe Summary", "Late Settlement"};
                    tabs();
                }*/
                return;
            case "B8":
                if (item.text.equals("Monthly Deposit Statement")) {
                    showCustomDialog("Financial", "Requesting Monthly Deposit Statement");
                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo2 = new Bundle();
                    brepo2.putInt("key", 3); //Your id
                    myintentsales.putExtras(brepo2); //Put your id to your next Intent
                    startActivity(myintentsales);
                }
                if (item.text.equals("Commission Statement")) {
                    showCustomDialog("Financial", "Requesting Commmission Statement");

                    myintentsales = new Intent(mContext, activity_reports.class);
                    Bundle brepo3 = new Bundle();
                    brepo3.putInt("key", 4); //Your id
                    myintentsales.putExtras(brepo3); //Put your id to your next Intent
                    startActivity(myintentsales);
                }

                if (item.text.equals("Request Credit")) {
                    myIntentPaymentHistory = new Intent(mContext, activity_credit.class);
                    startActivity(myIntentPaymentHistory);
                }
                if (item.text.equals("Retailer Payments")) {
                    myIntentPaymentHistory = new Intent(mContext, RetailerPayments.class);
                    startActivity(myIntentPaymentHistory);

                }
                if (item.text.equals("Swipe")) {

                    prefix = "F";
                    IMAGE = new Integer[]{R.drawable.monthly_swipe_detail, R.drawable.dialy_swipe_settlement, R.drawable.monthly_swipe_summary, R.drawable.daily_swipe_summary, R.drawable.late_settlement};
                    NAME = new String[]{"Monthly Swipe Detail", "Daily Swipe Settlement", "Monthly Swipe Summary", "Daily Swipe Summary", "Late Settlement"};

                    tabs();
                }
                return;
            case "B9":
                if (item.text.equals("Swipe")) {

                    prefix = "F";
                    IMAGE = new Integer[]{R.drawable.monthly_swipe_summary1, R.drawable.dialy_swipe_settlement1, R.drawable.monthly_swipe_summary1, R.drawable.daily_swipe_summary1, R.drawable.late_settlement};
                    NAME = new String[]{"Monthly Swipe Detail", "Daily Swipe Settlement", "Monthly Swipe Summary", "Daily Swipe Summary", "Late Settlement"};

                    tabs();
                } else if (item.text.equals("Retailer Payments")) {
                    myIntentPaymentHistory = new Intent(mContext, RetailerPayments.class);
                    startActivity(myIntentPaymentHistory);

                } else {
                    myIntentPaymentHistory = new Intent(mContext, activity_credit.class);
                    startActivity(myIntentPaymentHistory);
                }
                return;
            case "B10":

                if (item.text.equals("Retailer Payments")) {
                    myIntentPaymentHistory = new Intent(mContext, RetailerPayments.class);
                    startActivity(myIntentPaymentHistory);
                } else {
                    prefix = "F";
                    IMAGE = new Integer[]{R.drawable.monthly_swipe_detail1, R.drawable.dialy_swipe_settlement1, R.drawable.monthly_swipe_summary1, R.drawable.daily_swipe_summary1, R.drawable.late_settlement};
                    NAME = new String[]{"Monthly Swipe Detail", "Daily Swipe Settlement", "Monthly Swipe Summary", "Daily Swipe Summary", "Late Settlement"};


                    tabs();
                }
                return;


            case "C0":
                Intent myIntentUsers = new Intent(mContext, activity_users.class);
                startActivity(myIntentUsers);
                return;
            case "C1":
                showCustomDialog("User Management", "Requesting Cashier Settings");

                Intent myIntentActivationSettingsc1 = new Intent(mContext, activity_settings.class);
                myIntentActivationSettingsc1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bc1 = new Bundle();
                bc1.putInt("key", 3); //Your id
                myIntentActivationSettingsc1.putExtras(bc1); //Put your id to your next Intent
                startActivity(myIntentActivationSettingsc1);
                // finish();
                return;
            case "C2":
                // showCustomDialogn("TopitUp","This feature will be added soon",true);


                showCustomDialog("User Management", "Requesting Customised Product");

                Intent myIntentActivationSettingsc2 = new Intent(mContext, activity_product_settings.class);
                myIntentActivationSettingsc2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bc2 = new Bundle();
                bc2.putInt("key", 4); //Your id
                myIntentActivationSettingsc2.putExtras(bc2); //Put your id to your next Intent
                startActivity(myIntentActivationSettingsc2);

                return;

            case "D0":
                showCustomDialog("Wait...", "Requesting General Settings");
                Intent myIntentActivationSettings = new Intent(mContext, activity_settings.class);
                Bundle b = new Bundle();
                b.putInt("key", 1); //Your id
                myIntentActivationSettings.putExtras(b); //Put your id to your next Intent
                startActivity(myIntentActivationSettings);

                return;
            case "D1":
                showCustomDialog("Wait...", "Requesting Print&Slip Settings");
                Intent myIntentActivationSettings1 = new Intent(mContext, activity_settings.class);
                Bundle b1 = new Bundle();
                b1.putInt("key", 2); //Your id
                myIntentActivationSettings1.putExtras(b1); //Put your id to your next Intent
                startActivity(myIntentActivationSettings1);
                return;
            case "D2":
//                Toasty.info(activity_admin.this,"quick",1000).show();
//                  showCustomDialog("Wait...","Requesting Quick Launch Settings");

                startActivity(new Intent(mContext, activity_quick_launch.class));

                return;

            case "D3":
                prefix = "E";
                //    IMAGE = new Integer[]{R.drawable.ic_virtual_store, R.drawable.ic_update_catalogue, R.drawable.ic_refresh_users, R.drawable.ic_update_catalogue, R.drawable.settings, R.drawable.settings};
                //    NAME = new String[]{"Virtual Store", "Update Catalogue \n(" + spiver + ")", "Refresh Users", "Update Cash Mx Catalogue", "Backup", "Restore"};

                IMAGE = new Integer[]{R.drawable.ic_virtual_store, R.drawable.ic_update_catalogue, R.drawable.ic_refresh_users,};
                NAME = new String[]{"Virtual Store", "Update Catalogue \n(" + spiver + ")", "Refresh Users"};
                tabs();
                return;
            case "D4":
                Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("ua.com.streamsoft.pingtoolspro");
                //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntentp != null) {
                    startActivity(launchIntentp);//null pointer check in case package name was not found
                }

                /*Intent myIntentActivation = new Intent(mContext, activity_activation.class);
                startActivity(myIntentActivation);*/
                return;
            case "D6":
                showCustomDialog("Wait...", "Requesting POS Settings");

                Intent myIntentSettings = new Intent(mContext, activity_settings.class);
                Bundle c = new Bundle();
                c.putInt("key", 5); //Your id
                myIntentSettings.putExtras(c); //Put your id to your next Intent
                startActivity(myIntentSettings);
               /* Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("ua.com.streamsoft.pingtoolspro");
                //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntentp != null) {
                    startActivity(launchIntentp);//null pointer check in case package name was not found
                }*/

                return;
            case "D7":
                Intent myIntentBanking = new Intent(mContext, activity_otp.class);
                startActivity(myIntentBanking);
                /*showCustomDialog("Wait...", "Requesting POS Settings");


                Intent myIntentSettings = new Intent(mContext, activity_settings.class);
                Bundle c = new Bundle();
                c.putInt("key", 5); //Your id
                myIntentSettings.putExtras(c); //Put your id to your next Intent
                startActivity(myIntentSettings);*/

                return;

            case "D8":
                if (item.text.equals("Salaah App")) {
                    try {
                        Intent intent = getPackageManager().getLaunchIntentForPackage("za.co.topitup.salaahapp");

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("muteFromVas", "false");

                        startActivity(intent);
                    } catch (Exception e) {

                    }



                   /* Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName("za.co.topitup.salaahapp", "za.co.topitup.salaahapp.MainActivity"));
                    startActivity(intent);*/
                } else {
                    callTestApi();

                    Log.e("test print", "print............");
//                    PrintDemo printDemo = new PrintDemo();
//                    printDemo.SendDataString("test print");

                }

               /* Intent myIntentBanking = new Intent(mContext, activity_otp.class);
                startActivity(myIntentBanking);*/
//                get_swipe_sales();

                return;
            case "D9":
                if (item.text.equals("Salaah App")) {
                    try {
                        Intent intent = getPackageManager().getLaunchIntentForPackage("za.co.topitup.salaahapp");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                    }

                   /* Intent intent = new Intent();
                    intent.setComponent(new ComponentName("za.co.topitup.salaahapp", "za.co.topitup.salaahapp.MainActivity"));
                    startActivity(intent);*/
                }
//                callTestApi();
//                Toast.makeText(activity_admin.this,"test print",Toast.LENGTH_LONG).show();
                return;
            case "E0":

                if (Topitup.DEVICE_TYPE == "QCOM SHOP1") {
                    //check_for_new_app();
                } else {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.manoj.appupdater");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }
                return;
            case "E1":
                get_update_all();
                return;
            case "E2":
                showCustomDialog("Updating", "Refreshing User List");

                get_update_users();
                return;
            case "E3":
                // showCustomDialogn("TopitUp","This feature will be added soon",true);
                update_cashmx_suppliers();
                return;
            case "E4":
                showCustomDialogn("TopitUp", "This feature will be added soon", true);

                return;
            case "D5":
                Intent intent = new Intent(activity_admin.this, activity_login.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
                Intent mStartActivity = new Intent(mContext, za.co.topitup.activitySplashScreen.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
                return;
            case "F0":
                showCustomDialog("Financial", "Requesting Monthly Swipe Summary Report");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 5); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
                return;
            case "F1":
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                showCustomDialog("Financial", "Requesting Swipe Daily Settlement");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 6); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);

                return;
            case "F2":
               /* showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 8); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                showCustomDialog("Financial", "Requesting Bank Card Transfer");
                myintentsales = new Intent(mContext, activity_banktransfer.class);
                brepo = new Bundle();
                brepo.putInt("key", 7); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
                return;
            case "F3":
                showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 8); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
               /* showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 9); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                /*showCustomDialog("Financial", "Requesting Monthly Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 8); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                return;
            case "F4":
                showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 9); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
                /*showCustomDialog("Financial", "Requesting Detailed Late Settlement");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 10); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                /*showCustomDialog("Financial", "Requesting Daily Swipe Summary");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 9); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);*/
                return;
            case "F5":
                /* showCustomDialogn("TopitUp","This feature will be added soon",true);*/
                showCustomDialog("Financial", "Requesting Detailed Late Settlement");
                myintentsales = new Intent(mContext, activity_reports.class);
                brepo = new Bundle();
                brepo.putInt("key", 10); //Your id
                myintentsales.putExtras(brepo); //Put your id to your next Intent
                startActivity(myintentsales);
        }


        // Toast.makeText(getApplicationContext(), item.pos + " is clicked", Toast.LENGTH_SHORT).show();
    }

    private void callTestApi() {


        Call<ResponseBody> call = apiService.get_test_print(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                //Toasty.error(mContext, "Could not log in", 3000, true).show();
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    // printingCustomDialog("Printing", "Busy printing...");
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String selectedPrinter = settings.getString("printer", "inner");

                        if (selectedPrinter.equals("inner")) {
                            if (Build.MODEL.equals("P052")) {
                            }else {
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
//                                    Printer.do_last_reprint(mContext);
                                    }

                                }
                            }
                        }else{
//                            Printer.do_last_reprint(mContext);

                        }

                    }
//                    if(Topitup.DEVICE_TYPE.equals("TABLET")){
//
//                        printData("1234567891011121314151617181920", activity_admin.this);
//                    }else {
                    PrinterTopitup.print_data(res);
//                    }

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toasty.error(mContext, t.getMessage(), 100000, true).show();
                Timber.e("GetUpdateAll error: " + t.getMessage());


            }
        });


    }

    private void update_cashmx_suppliers() {


        Call<supplierCashmx> call = apiService.get_all_supliers(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<supplierCashmx>() {
            @Override
            public void onResponse(Call<supplierCashmx> call, Response<supplierCashmx> response) {

                if(response.isSuccessful()) {
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

                            supplierCashmx result = response.body();


                            realm.beginTransaction();

                            String suppliers_data = "";

                            byte[] bytes = android.util.Base64.decode(result.supplier_data, android.util.Base64.DEFAULT);
                            suppliers_data = new String(bytes);

                            realm.where(supplierCashmx.class).findAll().deleteAllFromRealm();
                            realm.where(supplierCashmx.class).findAll().deleteAllFromRealm();
                            realm.commitTransaction();
                            BufferedReader bufReader = new BufferedReader(new StringReader(suppliers_data));


                            String line = null;


                            while ((line = bufReader.readLine()) != null) {

                                Timber.i(line);


                                String[] myData = line.split("\\^");
                                //for (String s: myData) {

                                //if (s.length() > 0) {


                                Toasty.error(mContext, "supplier_id" + myData[0], 5000, true).show();
                                supplierCashmx sc = new supplierCashmx();
                                sc.supplier_id = Integer.parseInt(myData[0]);


                                if (myData.length > 2) sc.short_name = myData[1];
                                if (myData.length > 3) sc.legal_name = myData[2];
                                if (myData.length > 4) sc.logo_name = myData[3];
                                if (myData.length > 5) sc.sts = Integer.valueOf(myData[4]);
                                if (myData.length > 6) sc.cashms_acc_number = myData[5];
                                if (myData.length > 7) sc.dc_code = myData[6];
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(sc);
                                realm.commitTransaction();


                                //  System.out.println(s);

                                // }

                                // }


                            }


                        }

                    } catch (Exception ex) {
                     /*   if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                    }
                }else{
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }


            }

            @Override
            public void onFailure(Call<supplierCashmx> call, Throwable t) {

                Toasty.error(mContext, t.getMessage(), 100000, true).show();
                Timber.e("GetUpdateAll error: " + t.getMessage());


            }
        });


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

    @Override
    protected void onResume() {
        if (dialog != null)
            dialog.dismiss();
        FullscreenCall();
        super.onResume();
    }

    private void get_update_users() {


        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>() {

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Timber.e(response.body());
                //Toasty.error(mContext, response.toString(), 8000, true).show();

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

                   // res = response.body()();

                } catch (Exception ex) {
                    //
                    response.body().clear();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    private void get_update_all() {

        showCustomDialog("Updating", "Please wait...");

        Call<GetUpdateAll> call = apiService.get_update_all(Topitup.TIU_LICENSE, spiver, "", Topitup.APP_VERSION);
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {


                if(response.isSuccessful()) {
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


                        if (response.body().toString().toLowerCase().contains("customer_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                            //
                        } else {

                            GetUpdateAll result = response.body();

                            realm.beginTransaction();

                            String service_provider_data = "";

                            byte[] bytes = android.util.Base64.decode(result.published_data, android.util.Base64.DEFAULT);
                            service_provider_data = new String(bytes);

                            result.service_provider_data = service_provider_data;
                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();
                            Log.i("catalogue list", service_provider_data);
                            update_spi(service_provider_data);

                            stopCustomDialogclose("Updated", "Product catalogue updated.");

                        }

                    } catch (Exception ex) {
                       /* if (response.raw() != null)
                            response.raw().close();*/
                        stopCustomDialog("Problem", ex.getMessage());
                    }
                }else{
                    dialog.dismiss();

                }


            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {

                dialog.dismiss();

            }
        });


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
                    Log.e("~~~~~~",myData[4]+","+myData[5]+","+myData[6]);
                 /*   if (myData.length == 10) {
                        new_spi.item_barcode = myData[9];
                    } else {
                        if (myData.length == 12) {
                            new_spi.item_barcode = myData[9];
                            new_spi.period_label = myData[10];
                            new_spi.social_type = myData[11];
                        }

                    }*/
                    /*Log.e("TestInfo Service", myData[1] + "," + myData[2] + "," +
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

    public void statement() {
        final Call<ResponseBody> call;

        call = apiService.commission_statement(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "202008");

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

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res;
                    if (res.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stopCustomDialog("Problem", t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    public void z_end_of_day() {

        //final Call<ResponseBody> call = apiService.z_end_of_day_cashier(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        final Call<ResponseBody> call = apiService.z_end_of_day(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                dialog.dismiss();
                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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
                    matcher = matcher.replace("err:", "");
                    try {
//                        matcher = matcher.replace("ERR:", "");

//                        stopCustomDialog("Problem", matcher);
//                        JSONObject jsonObject = new JSONObject(matcher);

                        stopCustomDialog("Problem", matcher);
                    } catch (Exception e) {
                        stopCustomDialog("Problem", matcher);
                        e.printStackTrace();
                    }

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    //  printingCustomDialog("Printing", "Busy printing...");
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String selectedPrinter = settings.getString("printer", "inner");

                        if (selectedPrinter.equals("inner")) {
                            if (Build.MODEL.equals("P052")) {
                                PrinterTopitup.print_data(res);
                                dialog.dismiss();
                            }else{
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
                                    return;
                                } else {
                                    PrinterTopitup.print_data(res);
                                }
                                //  Printer.print_data(res);
                                dialog.dismiss();

                            }
                            }
                        }else{
                            PrinterTopitup.print_data(res);
                            dialog.dismiss();

                        }

                    }else{
                        PrinterTopitup.print_data(res);
                        dialog.dismiss();

                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    public void get_swipe_sales() {

        // final Call<ResponseBody> call = apiService.get_invoices(Topitup.TIU_LICENSE, Topitup.POSUSER_ID,"0","0");
        final Call<ResponseBody> call = apiService.get_swipe_sales(Topitup.TIU_LICENSE, "2021", "10", "14", Topitup.DEVICE_TYPE);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        JSONArray jsonArry = jsonObject.getJSONArray("arr");
                        Toasty.info(mContext, "json" + jsonArry.length(), 8000, true).show();

                        for (int i = 0; i < jsonArry.length(); i++) {
                            MyItem test = new MyItem();
                            JSONObject objin = jsonArry.getJSONObject(i);
                            // Toasty.info(mContext, "json"+objin.getString("tx_id"), 8000, true).show();


                            // movies.add(test);

                        }

                    } catch (JSONException e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }


                    //  printingCustomDialog("Printing","Busy printing...");
                    //  Printer.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    public void get_invoice() {

        // final Call<ResponseBody> call = apiService.get_invoices(Topitup.TIU_LICENSE, Topitup.POSUSER_ID,"0","0");
        final Call<ResponseBody> call = apiService.invoice_print(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "650011");
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    public void cashup_x_intraday_detail() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday_detail(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    // printingCustomDialog("Printing", "Busy printing...");
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String selectedPrinter = settings.getString("printer", "inner");

                        if (selectedPrinter.equals("inner")) {
                            if (Build.MODEL.equals("P052")) {
                                PrinterTopitup.print_data(res);

                            }else{

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
     /*       if(total_vouchers_to_print>1)
            {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }*/
                            if (status != 0 && setting_printer_bypass.equals("0")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);

                            } else {
                                // Printer.print_data(res);
                                if (setting_printer_bypass.equals("1")) {
                                    showCustomDialog("Out of Paper", "Please check paper and try again.", true);

                                } else {
                                    PrinterTopitup.print_data(res);
                                }
                            }
                        }
                        }else{
                            PrinterTopitup.print_data(res);

                        }

                    }else {
                        PrinterTopitup.print_data(res);

                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    public void cashup_x_intraday_summary() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday_summary(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    //  printingCustomDialog("Printing", "Busy printing...");
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String selectedPrinter = settings.getString("printer", "inner");

                        if (selectedPrinter.equals("inner")) {
                            if (Build.MODEL.equals("P052")) {
                                PrinterTopitup.print_data(res);

                            }else{

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
     /*       if(total_vouchers_to_print>1)
            {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }*/
                            if (status != 0 && setting_printer_bypass.equals("0")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);

                            } else {
                                // Printer.print_data(res);
                                if (setting_printer_bypass.equals("1")) {
                                    showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                                } else {
                                    PrinterTopitup.print_data(res);

                                }
                            }
                        }
                        }else {
                            PrinterTopitup.print_data(res);

                        }

                    }else{
                        PrinterTopitup.print_data(res);

                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }
    //  AppCompatButton btn_Paper_ignore_always;

    public void cashup_x_intraday() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    //   printingCustomDialog("Printing", "Busy printing...");
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String selectedPrinter = settings.getString("printer", "inner");

                        if (selectedPrinter.equals("inner")) {
                            if (Build.MODEL.equals("P052")) {
                                PrinterTopitup.print_data(res);

                            }else{

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
                                // Printer.print_data(res);
                                if (setting_printer_bypass.equals("1")) {
                                    showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                                } else {
                                    PrinterTopitup.print_data(res);
                                }
                            }
                        }
                        }else{
                            PrinterTopitup.print_data(res);

                        }

                    }else {
                        PrinterTopitup.print_data(res);

                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    private void showCustomDialog(String sTitle, String sContent) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
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
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void showCustomDialogn(String sTitle, String sContent, Boolean showClose) {

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
                FullscreenCall();
                dialog.dismiss();
            }
        });

        btn_paper_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topitup.getInstance().printerInit();
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
        FullscreenCall();
    }

    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }

    private void stopCustomDialogclose(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity_admin.this, activity_login.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
                Intent mStartActivity = new Intent(mContext, activitySplashScreen.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });

    }

    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

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
        progressBar = dialog.findViewById(R.id.progressBar);
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
}
