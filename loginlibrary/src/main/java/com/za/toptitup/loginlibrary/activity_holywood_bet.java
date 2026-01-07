package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.admin.activity_activation;
import com.za.toptitup.loginlibrary.admin.activity_banking_detail;
import com.za.toptitup.loginlibrary.admin.activity_credit;
import com.za.toptitup.loginlibrary.admin.activity_depositlip;
import com.za.toptitup.loginlibrary.admin.activity_paymenthistory;
import com.za.toptitup.loginlibrary.admin.activity_settings;
import com.za.toptitup.loginlibrary.admin.activity_transfer;
import com.za.toptitup.loginlibrary.admin.activity_users;
import com.za.toptitup.loginlibrary.admin.activity_wallettransfer;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UserException;

public class activity_holywood_bet extends AppCompatActivity implements HomeAdapterHollywood.ItemListener {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    BottomNavigationView mBottomNav;
    RealmResults<service_provider_item> service_provider_items;
    TextView textViewheader;
    String prefix = "A", spiver;
    Context mContext;
    private Integer[] IMAGE = {
            R.drawable.prov_hollywood,
            R.drawable.prov_hollywood,
            R.drawable.prov_hollywood,
            R.drawable.prov_hollywood,
            R.drawable.prov_hollywood,
            R.drawable.prov_hollywood
    };


    private String[] NAME = {"R 5", "R 10", "R 15", "R 20", "R 25", "R 30"};
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    Realm realm;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<fin_balance> tiu_fin_balance;
    private CardView cardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_vas);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        textViewheader = findViewById(R.id.textViewheader);
        mBottomNav = findViewById(R.id.bottom_navigation);

        cardview = findViewById(R.id.cardView);


        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        spiver = tiu_settings.spi_ver;

        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        service_provider_items = realm.where(service_provider_item.class)
                .equalTo("service_provider_id", 1)
                .equalTo("item_type", 0)
                .sort("item_position", Sort.ASCENDING)
                .findAll();
        Log.i("service_provider_items", "" + service_provider_items);
        tabs();
        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/

        //AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        //recyclerView.setLayoutManager(layoutManager);


        /**
         Simple GridLayoutManager that spans two columns
         **/
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {

                    case R.id.action_logout:
                        prefix = "A";
                        IMAGE = new Integer[]{R.drawable.x_intraday,
                                R.drawable.x_intraday_cashier_detail,
                                R.drawable.x_intraday_cashier_summary,
                                R.drawable.end_of_day_z,
                                R.drawable.z_report_history,
                                R.drawable.monthly_sales_report,


                        };


// NAME = new String[]{"X-Intraday","X-Intraday Cashier Detail","X-Intraday Cashier Summary","End-of-Day Z","Z Report History","Monthly Sales Report"};
                        NAME = new String[]{"1.1 X-Intraday", "1.2 X-Intraday Cashier Detail", "1.3 X-Intraday Cashier Summary", "1.4 End-of-Day Z", "1.5 Z Report History", "1.6 Monthly Sales Report"};

                        tabs();

                        return true;

                    case R.id.action_financial:
                        prefix = "B";


                        IMAGE = new Integer[]{R.drawable.loan,
                                R.drawable.billpayment,
                                R.drawable.sendmoney,
                                R.drawable.wallettransfer,
                                R.drawable.sendmoney,
                                R.drawable.wallettransfer,
                                R.drawable.admin_cr,
                                R.drawable.banking,
                                R.drawable.wallettransfer
                        };


                        NAME = new String[]{"2.1 Get Balance", "2.2 View Invoices", "2.3 View Financial Transactions", "2.4 Deposit Slip", "2.5 Store Transfer", "2.6 Wallet Transfer", "2.7 Request Credit", "2.8 Banking Detail", "2.9 Deposit History"};

                        tabs();
                        return true;
                    case R.id.action_users:

                        prefix = "C";
                        IMAGE = new Integer[]{R.drawable.admin_user_orig, R.drawable.general_settings, android.R.drawable.ic_menu_manage};

                        NAME = new String[]{"3.1 Users", "3.2 Cashier Settings", "3.3 Customised Product"};

                        tabs();

                        return true;
                    case R.id.action_setting:
                        prefix = "D";
                        IMAGE = new Integer[]{R.drawable.general_settings, R.drawable.print_and_slip_settings, R.drawable.voucher_plus, R.drawable.update_management, R.drawable.activation_info, R.drawable.ping_tools};
                        NAME = new String[]{"4.1 General Settings", "4.2 Print&Slip  Settings", "4.3 Electricity   Settings", "4.4 Update Management", "4.5 Activation Info", "4.6 Ping Tools"};

                        tabs();


                        return true;
                    default:

                        return true;
                }*/
                if (item.getItemId() == R.id.action_logout) {
                    prefix = "A";
                    IMAGE = new Integer[]{
                            R.drawable.x_intraday,
                            R.drawable.x_intraday_cashier_detail,
                            R.drawable.x_intraday_cashier_summary,
                            R.drawable.end_of_day_z,
                            R.drawable.z_report_history,
                            R.drawable.monthly_sales_report,
                    };

                    NAME = new String[]{
                            "1.1 X-Intraday",
                            "1.2 X-Intraday Cashier Detail",
                            "1.3 X-Intraday Cashier Summary",
                            "1.4 End-of-Day Z",
                            "1.5 Z Report History",
                            "1.6 Monthly Sales Report"
                    };

                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_financial) {
                    prefix = "B";
                    IMAGE = new Integer[]{
                            R.drawable.loan,
                            R.drawable.billpayment,
                            R.drawable.sendmoney,
                            R.drawable.wallettransfer,
                            R.drawable.sendmoney,
                            R.drawable.wallettransfer,
                            R.drawable.admin_cr,
                            R.drawable.banking,
                            R.drawable.wallettransfer
                    };

                    NAME = new String[]{
                            "2.1 Get Balance",
                            "2.2 View Invoices",
                            "2.3 View Financial Transactions",
                            "2.4 Deposit Slip",
                            "2.5 Store Transfer",
                            "2.6 Wallet Transfer",
                            "2.7 Request Credit",
                            "2.8 Banking Detail",
                            "2.9 Deposit History"
                    };

                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_users) {
                    prefix = "C";
                    IMAGE = new Integer[]{
                            R.drawable.admin_user_orig,
                            R.drawable.general_settings,
                            android.R.drawable.ic_menu_manage
                    };

                    NAME = new String[]{
                            "3.1 Users",
                            "3.2 Cashier Settings",
                            "3.3 Customised Product"
                    };

                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_setting) {
                    prefix = "D";
                    IMAGE = new Integer[]{
                            R.drawable.general_settings,
                            R.drawable.print_and_slip_settings,
                            R.drawable.voucher_plus,
                            R.drawable.update_management,
                            R.drawable.activation_info,
                            R.drawable.ping_tools
                    };

                    NAME = new String[]{
                            "4.1 General Settings",
                            "4.2 Print&Slip Settings",
                            "4.3 Electricity Settings",
                            "4.4 Update Management",
                            "4.5 Activation Info",
                            "4.6 Ping Tools"
                    };

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
        if (prefix.equals("A"))
            textViewheader.setText("VIRTUAL VAS");
        if (prefix.equals("B"))
            textViewheader.setText("FINANCIAL MANAGEMENT");
        if (prefix.equals("C"))
            textViewheader.setText("USER MANAGEMENT");
        if (prefix.equals("D"))
            textViewheader.setText("SETTINGS");
        if (prefix.equals("E"))
            textViewheader.setText("UPDATE MANAGEMENT");

        for (int i = 0; i < NAME.length; i++) {

            arrayList.add(new Item(NAME[i], prefix + i, IMAGE[i], "#FFFFFF", ""));

        }
        HomeAdapterHollywood adapter = new HomeAdapterHollywood(activity_holywood_bet.this, arrayList, activity_holywood_bet.this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(Item item) {

        switch (item.pos) {
            case "A0":


                return;

            case "A1":


                return;

            case "A2":


                return;

            case "A3":

                return;
            case "A4":


                return;
            case "A5":


                return;
            case "B0":


                return;
            case "B1":
                //showCustomDialog("Financial","Requesting Invoices");
                Intent myIntentBalanceinvoie = new Intent(mContext, activity_invoice.class);
                Bundle binv = new Bundle();
                binv.putInt("key", 1); //Your id
                myIntentBalanceinvoie.putExtras(binv); //Put your id to your next Intent
                startActivity(myIntentBalanceinvoie);
                return;
            case "B2":
                Intent myIntentfin = new Intent(mContext, activity_invoice.class);
                Bundle bfin = new Bundle();
                bfin.putInt("key", 2); //Your id
                myIntentfin.putExtras(bfin); //Put your id to your next Intent
                startActivity(myIntentfin);
                return;
            case "B3":
                Intent depositslip = new Intent(mContext, activity_depositlip.class);
                startActivity(depositslip);
                return;
            case "B4":
                Intent myIntentTransfer = new Intent(mContext, activity_transfer.class);
                startActivity(myIntentTransfer);
                return;
            case "B5":
                Intent myIntentWallet = new Intent(mContext, activity_wallettransfer.class);
                startActivity(myIntentWallet);
                return;
            case "B6":
                Intent myIntentActivationCredit = new Intent(mContext, activity_credit.class);
                startActivity(myIntentActivationCredit);
                return;
            case "B7":
                Intent myIntentBanking = new Intent(mContext, activity_banking_detail.class);
                startActivity(myIntentBanking);
                return;
            case "B8":
                Intent myIntentPaymentHistory = new Intent(mContext, activity_paymenthistory.class);
                startActivity(myIntentPaymentHistory);
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
            case "C6":

                return;
            case "C7":

                return;
            case "C8":

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
                showCustomDialog("Please Wait", "Requesting Electricity Settings");
                Intent myIntentActivationSettingse = new Intent(mContext, activity_settings.class);
                Bundle b1e = new Bundle();
                b1e.putInt("key", 4); //Your id
                myIntentActivationSettingse.putExtras(b1e); //Put your id to your next Intent
                startActivity(myIntentActivationSettingse);
                return;
            case "D3":
                prefix = "E";
                IMAGE = new Integer[]{R.drawable.auto_update, R.drawable.auto_update, R.drawable.admin_user_orig, R.drawable.settings, R.drawable.settings};
                NAME = new String[]{"Virtual Store", "Update Catalogue \n(" + spiver + ")", "Refresh Users", "Backup", "Restore"};

                tabs();
                return;
            case "D4":

                Intent myIntentActivation = new Intent(mContext, activity_activation.class);
                startActivity(myIntentActivation);
                return;
            case "D5":
                Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("ua.com.streamsoft.pingtoolspro");
                //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntentp != null) {
                    startActivity(launchIntentp);//null pointer check in case package name was not found
                }

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

        }


        // Toast.makeText(getApplicationContext(), item.pos + " is clicked", Toast.LENGTH_SHORT).show();
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

                    //res = response.body().string();

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

        Call<GetUpdateAll> call = apiService.get_update_all(Topitup.TIU_LICENSE, "", "", Topitup.APP_VERSION);
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

                        if (!response.headers().get("server").equals("TIU")) {
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

                            update_spi(service_provider_data);

                            stopCustomDialog("Updated", "Product catalogue updated.");

                        }

                    } catch (Exception ex) {
                     /*   if (response.raw() != null)
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

                String[] myData = line.split("\\^");

                if (myData[0].equals("p")) {

                    service_provider new_sp = new service_provider();
                    new_sp.provider_id = Integer.parseInt(myData[1]);

                    last_sp_id = Integer.parseInt(myData[1]);

                    if (myData.length > 2) new_sp.provider_desc = myData[2];
                    if (myData.length > 3) new_sp.provider_message = myData[3];

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_sp);
                    realm.commitTransaction();

                } else {

                    service_provider_item new_spi = new service_provider_item();
                    new_spi.service_provider_item_id = Integer.parseInt(myData[0]);
                    new_spi.service_provider_id = last_sp_id;


                    boolean item_show_value = Integer.parseInt(myData[7]) == 1;

                    new_spi.item_desc = myData[1];
                    new_spi.item_btn_desc = myData[2];
                    //new_spi.item_print_desc = myData[3];
                    new_spi.item_print_desc = myData[4];
                    new_spi.item_value = myData[5];
                    new_spi.item_type = Integer.parseInt(myData[6]);
                    new_spi.item_show_value = item_show_value;
                    new_spi.item_position = Integer.parseInt(myData[8]);
                    new_spi.item_barcode = myData[9];

                    new_spi.item_value_int = Double.parseDouble(myData[5]);

                    // Timber.i("SPI :  " + String.valueOf( Integer.parseInt(myData[5])));

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_spi);
                    realm.commitTransaction();

                }


            }

        } catch (Exception ex) {

            Timber.i("SPI Error: " + ex.getMessage());

        }


    }


    public void monthly_sales_report() {

        final Call<ResponseBody> call = apiService.monthly_sales_report(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "202008");

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


    public void cashup_x_intraday_summary() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday_summary(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

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

    public void cashup_x_intraday() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

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


    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;

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

    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }

    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }


    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
        }
    };


}
