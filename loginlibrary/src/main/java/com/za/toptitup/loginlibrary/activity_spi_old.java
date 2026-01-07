package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Handler;

import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


//import com.imagpay.SwipeEvent;
//import com.imagpay.SwipeListener;
//import com.imagpay.enums.CardDetected;
//import com.imagpay.enums.EmvStatus;
//import com.imagpay.enums.PrintStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.adapters.SpiListAdapter;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;


public class activity_spi_old extends BaseActivity {

    Context mContext;
    public static activity_spi_old instance;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    //BottomBar bottomBar;
    Realm realm;

    Integer service_provider_id;
    Integer item_type;

    private SpiListAdapter adapter;
    ListView listView;

    RealmResults<service_provider_item> service_provider_items;
    RealmResults<fin_balance> tiu_fin_balance;

    Dialog dialog;
    String company_addrs, company_addrs1, company_addrs2, print_address = "0";
    TextView txt_last_voucher_info;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;

    int printerQ1Sts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        instance = this;

        setContentView(R.layout.activity_spi);


        service_provider_id = getIntent().getIntExtra("service_provider_id", 0);
        item_type = 0;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_print_address = settings.getString("setting_print_address", "0");
        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        activity_login.fromScreen = "activity_spi_old";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);

        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
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
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        tiu_title_outlet.setText(tiu_settings.account_number);
        if (setting_print_address.equals("1")) {
            print_address = "1";
            company_addrs = tiu_settings.address;
            String[] toSplit = company_addrs.split("\\|");
            company_addrs1 = toSplit[0];
            company_addrs2 = toSplit[1];
        }

        if (Topitup.IS_ADMIN.equals("1")) {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Admin");
        } else {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Cashier");

        }

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);

                    }
                    if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                    }
                    if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }

                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard R " + tiu_fin_balance.get(0).available_balance);
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
            }
            if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

            }
            if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            } else {
                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }

        } catch (Exception ex) {

            Timber.e("ERROR FIN: " + ex.getMessage());

        }
        /* END HEADER */


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
        // btn_Paper_ignore_always = ((AppCompatButton) dialog.findViewById(R.id.btn_Paper_ignore_always));
        //bottomBar = (BottomBar)findViewById(R.id.bottombar);
        //for (int i = 0; i < bottomBar.getTabCount(); i++)
        //{ bottomBar.getTabAtPosition(i).setGravity(Gravity.CENTER_VERTICAL); }


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;

                    case R.id.action_airtime:
                        item_type = 0;
                        service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .equalTo("item_type", item_type)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();
                        adapter = new SpiListAdapter(service_provider_items);
                        listView.setAdapter(adapter);
                        return true;

                    case R.id.action_data:
                        item_type = 2;
                        service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .equalTo("item_type", item_type)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();
                        adapter = new SpiListAdapter(service_provider_items);
                        listView.setAdapter(adapter);
                        return true;

                    case R.id.action_special:
                        item_type = 1;
                        service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .notEqualTo("item_type", 0)
                                .notEqualTo("item_type", 2)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();
                        adapter = new SpiListAdapter(service_provider_items);
                        listView.setAdapter(adapter);
                        return true;

                    default:
                        item_type = 0;
                        service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .equalTo("item_type", item_type)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();
                        adapter = new SpiListAdapter(service_provider_items);
                        listView.setAdapter(adapter);
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;

                } else if (item.getItemId() == R.id.action_airtime) {
                    item_type = 0;
                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id)
                            .equalTo("item_type", item_type)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();
                    adapter = new SpiListAdapter(service_provider_items);
                    listView.setAdapter(adapter);
                    return true;

                } else if (item.getItemId() == R.id.action_data) {
                    item_type = 2;
                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id)
                            .equalTo("item_type", item_type)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();
                    adapter = new SpiListAdapter(service_provider_items);
                    listView.setAdapter(adapter);
                    return true;

                } else if (item.getItemId() == R.id.action_special) {
                    item_type = 1;
                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id)
                            .notEqualTo("item_type", 0)
                            .notEqualTo("item_type", 2)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();
                    adapter = new SpiListAdapter(service_provider_items);
                    listView.setAdapter(adapter);
                    return true;

                } else {
                    // Default case
                    item_type = 0;
                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id)
                            .equalTo("item_type", item_type)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();
                    adapter = new SpiListAdapter(service_provider_items);
                    listView.setAdapter(adapter);
                    return true;
                }

            }

        });

        service_provider_items = realm.where(service_provider_item.class)
                .equalTo("service_provider_id", service_provider_id)
                .equalTo("item_type", item_type)
                .sort("item_position", Sort.ASCENDING)
                .findAll();

        //Timber.e("Total SPI: " + String.valueOf(service_provider_items.size()));

        adapter = new SpiListAdapter(service_provider_items);

        listView = findViewById(R.id.list_spi);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Timber.i(" ON PRESS ");

                service_provider_item item = adapter.getItem(position);
                if (item == null) {
                    return;
                }

                total_vouchers_to_print = 1;
                spi_id = item.service_provider_item_id;
                spi_barcode = item.item_barcode;

                show_multi_voucher(item.service_provider_item_id);

                //final int spi_id = counter.service_provider_item_id;
                //Timber.e(" PRINT SPI : " + spi_id);

                //show_multi_voucher(counter.service_provider_item_id);

                //showCustomDialog("Requesting", "Please wait...", false);

                //total_vouchers_to_print = 1;
                //spi_id = item.service_provider_item_id;
                //get_voucher();


            }
        });


    }


    Integer spi_id = 0;
    String spi_barcode = "";
    Integer total_vouchers_to_print = 1;
    Integer current_voucher = 1;
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_spi_old.rl_network.setVisibility(View.VISIBLE);
                    activity_spi_old.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_spi_old.rl_network.setVisibility(View.INVISIBLE);
                    activity_spi_old.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_spi_old.rl_network.setVisibility(View.INVISIBLE);
                    activity_spi_old.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    public void get_voucher() {


        //Toasty.error(mContext, Topitup.POSUSER_ID, 8000, true).show();

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


        final Call<voucher_response> call = apiService.get_voucher_json_old(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", spi_id, "");

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }


                   try{

                   }catch (Exception e){

                   }
                voucher_response result = response.body();

                //Timber.e(result.toString());

                if (result.err.length() > 0) {

                    //String matcher = StringUtils.substringBetween(result.toString(), "<err>", "</err>");
                    //Toasty.error(mContext, result.err, 8000, true).show();

                    pop_title.setText("Voucher Request Issue");
                    pop_content.setText(result.err);
                    bt_close.setVisibility(View.VISIBLE);
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

               //     Printer.store_last_reprint(slip);

                    Timber.i(slip);

                    PrinterTopitup.print_data(slip);

                    if (current_voucher < total_vouchers_to_print) {
                        current_voucher++;

                        print_handler.postDelayed(print_handler_runnable, 2000);

                    } else {
                        handler.postDelayed(runnable, 2000);
                    }


                    try {
                        fin_balance fb = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findFirst();

                        realm.beginTransaction();
                        fb.balance = result.balance;
                        fb.available_balance = result.available_balance;
                        fb.balance_cash = result.balance_cash;
                        fb.commission = result.commission;
                        fb.swipe = result.swipe;
                        fb.loyalty = result.loyalty;

                        realm.commitTransaction();
                    } catch (Exception ex) {

                        Timber.e("ERROR SPI 2: " + ex.getMessage());

                    }


                }


            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();

                if (t instanceof SocketTimeoutException) {

                    pop_title.setText("Connection Issue");
                    pop_content.setText("You could have been charged, Please reprint or view your sales history when your internet connection returns.");

                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                } else {

                    //t.printStackTrace();

                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(View.VISIBLE);
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

                                stock_uid = obj.getString("stock_uid");

                                txt_last_header.setText(obj.getString("last_header"));
                                txt_last_time.setText(obj.getString("last_time"));

                                txt_last_voucher_info.setText(obj.getString("message"));
                                bt_reprint.setVisibility(View.VISIBLE);

                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        bt_process.setEnabled(true);

                    }

                } catch (Exception ex) {

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


                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                voucher_response result = response.body();

                //Timber.e(result.toString());

                if (result.err.length() > 0) {

                    //

                } else {        //Response OK

                    //Printer.store_last_reprint(result.print_data);

                    PrinterTopitup.print_data(result.print_data);

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


    Dialog dialog_multi;

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

    public void show_multi_voucher(Integer service_provider_id) {

        //Timber.i("PLUS CLICK 2: " + String.valueOf(service_provider_id));
        stock_uid = "";

        total_vouchers_to_print = 1;
        spi_id = service_provider_id;

        dialog_multi = new Dialog(mContext);
        dialog_multi.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_multi.setContentView(R.layout.dialog_multi_voucher);
        dialog_multi.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_multi.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));

//        pop_content = ((TextView) dialog_multi.findViewById(R.id.pop_content));

        //   pop_title.setText("Requesting");
//        pop_content.setText("please wait...");

        //bt_process.setText("Process x" + String.valueOf(total_vouchers_to_print));

        textView2 = dialog_multi.findViewById(R.id.textView2);
        textView3 = dialog_multi.findViewById(R.id.textView3);
        textView4 = dialog_multi.findViewById(R.id.textView4);
        textView5 = dialog_multi.findViewById(R.id.textView5);
        textView6 = dialog_multi.findViewById(R.id.textView6);
        textView7 = dialog_multi.findViewById(R.id.textView7);
        textView8 = dialog_multi.findViewById(R.id.textView8);
        textView9 = dialog_multi.findViewById(R.id.textView9);
        pop_title2 = dialog_multi.findViewById(R.id.pop_title);       // REMOVE "TextView" TEXTVIEW TO STOP ERROR


        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);
        if (settings.getString("setting_print_to_screen", "0").equals("1")) {

            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
            textView5.setVisibility(View.GONE);
            textView6.setVisibility(View.GONE);
            textView7.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
            textView9.setVisibility(View.GONE);

        }


        txt_last_header = dialog_multi.findViewById(R.id.txt_last_header);
        txt_last_time = dialog_multi.findViewById(R.id.txt_last_time);
        txt_last_voucher_info = dialog_multi.findViewById(R.id.txt_last_voucher_info);

        final AppCompatButton bt_close = dialog_multi.findViewById(R.id.bt_close);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        bt_process.setText("Process x" + total_vouchers_to_print);
        pop_title2.setText("Select Quantity (x" + total_vouchers_to_print + ")");
        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomDialog("Requesting", "Please wait...", false);

                get_voucher();

                dialog_multi.dismiss();

            }
        });


        dialog_multi.show();
        dialog_multi.getWindow().setAttributes(lp);

        bt_process.setEnabled(false);
        showCustomDialog("Fetching Last Voucher", "Please wait...", false);

        get_last_voucher_info();

    }


    public void click_spi_multi(View view) {

        int total_vouchers = 1;

        if (view.getTag().equals("2")) total_vouchers = 2;
        if (view.getTag().equals("3")) total_vouchers = 3;
        if (view.getTag().equals("4")) total_vouchers = 4;
        if (view.getTag().equals("5")) total_vouchers = 5;
        if (view.getTag().equals("6")) total_vouchers = 6;
        if (view.getTag().equals("7")) total_vouchers = 7;
        if (view.getTag().equals("8")) total_vouchers = 8;
        if (view.getTag().equals("9")) total_vouchers = 9;

        total_vouchers_to_print = total_vouchers;

        pop_title2.setText("Select Quantity (x" + total_vouchers_to_print + ")");
        bt_process.setText("Process x" + total_vouchers_to_print);

    }


    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;

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

    }

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

    // run after some seconds to wait for printing
    Handler print_handler = new Handler();
    Runnable print_handler_runnable = new Runnable() {
        @Override
        public void run() {
            get_voucher();
        }
    };


    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }

    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int len = data.length;
            String str = "";
            for (int i = 0; i < len; i++) {
                if ((data[i] & 0xFF) < 16)
                    str = str + "0" + Integer.toHexString(data[i] & 0xFF);
                else
                    str = str + Integer.toHexString(data[i] & 0xFF);
            }
            return str.toUpperCase();
        }
    }

}
