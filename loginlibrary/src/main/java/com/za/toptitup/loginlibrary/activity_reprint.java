package com.za.toptitup.loginlibrary;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
import com.za.toptitup.loginlibrary.model.MyItem;
import com.za.toptitup.loginlibrary.model.ReprinttAdapter;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;


public class activity_reprint extends BaseActivity implements ReprinttAdapter.OnItemClickMain {
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static activity_reprint instance;
    public static String setting_printer_bypass;
    LinearLayoutManager manager;
    Realm realm;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    EditText txt_input, date;
    RadioButton rdo_filter_type_0, rdo_filter_type_100;
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_2, rdo_filter_type_3;
    RecyclerView lst_reprint;
    int startId = 0;
    String productId = "";
    RealmResults<service_provider_item> service_provider_items;
    RealmResults<fin_balance> tiu_fin_balance;
    String spi_barcode, item_desc, enable_realtime_swipe;
    int size;
    String company_addrs, company_addrs1, company_addrs2, print_address = "0";
    DatePickerDialog datePickerDialog;
    Spinner spinner_hour;
    boolean isLoading = false;
    ReprinttAdapter.OnItemClickMain onItemClickMain;
    int pinok = 0;
    LinearLayout tiu_title_bar_new;
    Spinner spinner_product;
    ArrayList<MyItem> movies = new ArrayList<MyItem>();
    ReprinttAdapter listAdapter;
    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    ProgressBar progressBar;
    SharedPreferences settings;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };
    private BroadcastReceiver mNetworkReceiver;
    private int indexId = 0;
    private LinearLayout bottomProgressLayout;

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
PrinterTopitup printer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
printer= new PrinterTopitup(this);
        setContentView(R.layout.activity_reprint);

        FullscreenCall();


        onItemClickMain = this;
        /* TIU HEADER */
         settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_print_address = settings.getString("setting_print_address", "0");


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");
        setting_printer_bypass = settings.getString("setting_print_bypass", "0");


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

        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);


        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        activity_login.fromScreen = "activity_reprint";


        spinner_hour = findViewById(R.id.spinner_hour);
        spinner_product = findViewById(R.id.spinner_product);

        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        service_provider_items = realm.where(service_provider_item.class).findAll();
        size = service_provider_items.size();
        ArrayList<String> listProduct = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            item_desc = service_provider_items.get(i).item_desc;


            if (item_desc.contains(" ")) {
                String productName = item_desc.substring(0, item_desc.indexOf(" ")).toUpperCase();
                String finalProduct = "";
                if (productName.contains("BLU")) {
                    finalProduct = "BLUE VOUCHER";
                } else if (productName.contains("CELL")) {
                    finalProduct = "CELL C";
                } else if (productName.contains("VIRTUAL")) {
                    finalProduct = "VIRTUAL UNIPIN";
                } else if (productName.contains("TALK")) {
                    finalProduct = "Talk 360";
                } else {
                    finalProduct = productName;
                }
                listProduct.add(finalProduct);
            }


        }
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(listProduct);
        listProduct.clear();
        listProduct.add("Select Product");

        listProduct.addAll(hashSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listProduct);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product.setAdapter(adapter);
        spinner_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int selected_id = getCode(spinner_product.getSelectedItem().toString());
                productId = String.valueOf(selected_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();
            }
        });
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

       /* if(tiu_settings.company_name.length()>10){
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0,10) + "... | " + tiu_settings.account_number);
        }
        else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12));
                tiu_user_name_.setText("Admin");

            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12));
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

        List<String> list = new ArrayList<String>();
        list.add("0:00 - 1:00");
        list.add("1:00 - 2:00");
        list.add("2:00 - 3:00");
        list.add("3:00 - 4:00");
        list.add("4:00 - 5:00");
        list.add("5:00 - 6:00");
        list.add("6:00 - 7:00");
        list.add("7:00 - 8:00");
        list.add("8:00 - 9:00");
        list.add("9:00 - 10:00");
        list.add("10:00 - 11:00");
        list.add("11:00 - 12:00");
        list.add("12:00 - 13:00");
        list.add("13:00 - 14:00");
        list.add("14:00 - 15:00");
        list.add("15:00 - 16:00");
        list.add("16:00 - 17:00");
        list.add("17:00 - 18:00");
        list.add("18:00 - 19:00");
        list.add("19:00 - 20:00");
        list.add("20:00 - 21:00");
        list.add("21:00 - 22:00");
        list.add("22:00 - 23:00");
        list.add("23:00 - 24:00");


        ArrayAdapter<String> adapterProduct = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_hour.setAdapter(adapterProduct);
        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
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
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
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

        txt_input = findViewById(R.id.txt_input);
        rdo_filter_type_0 = findViewById(R.id.rdo_filter_type_0);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);
        rdo_filter_type_2 = findViewById(R.id.rdo_filter_type_2);
        rdo_filter_type_3 = findViewById(R.id.rdo_filter_type_3);
        rdo_filter_type_100 = findViewById(R.id.rdo_filter_type_100);
        RadioGroup rg1 = findViewById(R.id.grp_radio_layout_reprint1);
        RadioGroup rg2 = findViewById(R.id.grp_radio_layout_reprint2);
        RadioGroup rg3 = findViewById(R.id.radio_reprint_2);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    rg2.clearCheck();
                    rg3.clearCheck();
                    rg1.check(checkedId);
                }
                txt_input.setHint("Serial Number / UID");
            }
        });
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != -1) {
                    rg1.clearCheck();
                    rg3.clearCheck();

                    rg2.check(checkedId);
                }

                if (checkedId == R.id.rdo_filter_type_3) {
                    txt_input.setHint("Store No./Driver no./Driver Cell No.");
                } else {
                    txt_input.setHint("Serial Number / UID");
                }
            }
        });

        rg3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != -1) {
                    rg1.clearCheck();
                    rg2.clearCheck();

                    rg3.check(checkedId);
                }

                if (checkedId == R.id.rdo_filter_type_3) {
                    txt_input.setHint("Store No./Driver no./Driver Cell No.");
                } else {
                    txt_input.setHint("Serial Number / UID");
                }
            }
        });

        Button btn_bottom_reprint_last = findViewById(R.id.btn_bottom_reprint_last);

        lst_reprint = findViewById(R.id.lst_reprint);
        bottomProgressLayout = findViewById(R.id.bottom_progress_layout);

        manager = new LinearLayoutManager(this);
        lst_reprint.setLayoutManager(manager);
        lst_reprint.setHasFixedSize(true);

        listAdapter = new ReprinttAdapter(mContext, 0, movies, onItemClickMain);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
        lst_reprint.setAdapter(listAdapter);
        lst_reprint.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == movies.size() - 1) {
                        manager.scrollToPosition(movies.size() - 1);
                        indexId++;
                        startId = 25 * indexId;
                        isLoading = true;
                        if (rdo_filter_type_3.isChecked()) {
                        }
                        else{
                            PopulateLastSales(startId);

                        }
                    }
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                    }
                }
            }
        });
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
        date.setShowSoftInputOnFocus(false);
    /*    final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        // initiate the date picker and a button

        date.setText(mYear + "-"+String.format("%02d", (mMonth + 1) ) + "-" + String.format("%02d", mDay));*/
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                date.setShowSoftInputOnFocus(false);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(activity_reprint.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        PopulateLastSales(0);

    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reprint.rl_network.setVisibility(View.VISIBLE);
                    activity_reprint.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reprint.rl_network.setVisibility(View.INVISIBLE);
                    activity_reprint.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reprint.rl_network.setVisibility(View.INVISIBLE);
                    activity_reprint.rl_server.setVisibility(View.INVISIBLE);
                }
            });
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

    public void onclick_btn_action(View v) {

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
     *//*       if(total_vouchers_to_print>1)
            {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }*//*
                        if (status != 0 && setting_printer_bypass.equals("0")) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                            return;
                        } else {
                            // Printer.do_last_reprint(mContext);
                            if (setting_printer_bypass.equals("1")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                                return;
                            } else {
                                printer.do_last_reprint(mContext);
                            }
                        }
                    }else{
                        printer.do_last_reprint(mContext);

                    }

                }
                return;
            }

            case R.id.btn_top_search: {

               *//* try {
                    movies.clear();
//                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }*//*

                PopulateLastSales(0);
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

            /*if (total_vouchers_to_print > 1) {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }*/

                    if (status != 0 && setting_printer_bypass.equals("0")) {
                        showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                    } else {
                        // Printer.do_last_reprint(mContext);
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

        } else if (v.getId() == R.id.btn_top_search) {
            // Try clearing or updating any lists
    /* try {
        movies.clear();
        // listAdapter.clear();
        listAdapter.notifyDataSetChanged();
    } catch (Exception ex) {
        // Handle exceptions if needed
    } */

            PopulateLastSales(0);

        } else {
            // Add any default action if needed (optional)
        }


    }

    private void PopulateLastSales(int page) {

        if (page == 0) {
            lst_reprint.setVisibility(View.INVISIBLE);
            bottomProgressLayout.setVisibility(View.GONE);
        } else {
            bottomProgressLayout.setVisibility(View.VISIBLE);
        }
        showCustomDialog("", "", false);
        int start = 0, end = 0;
        start = page;
        end = 25;
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

//
        final Call<ResponseBody> call;
        if (rdo_filter_type_3.isChecked()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("GMT"));
            String tddate, threemdate;
            if (date.getText().toString().equals("")) {
                tddate = getDate(cal);

                cal.add(Calendar.MONTH, -6);
                threemdate = getDate(cal);
            } else {
                threemdate = date.getText().toString();
                tddate = getDate(cal);
            }

            call = apiService.get_payment_search(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, threemdate, tddate, search_str);
        } else {
            String finalDate;
            if (date.getText().toString().equals("")) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("GMT"));

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                finalDate = df.format(cal.getTime());
                date.setText(finalDate);

            } else {
                finalDate = date.getText().toString();
            }
            if (productId.equals("") || productId.equals("0")) {
                call = apiService.get_last_sales_filter(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, is_admin, String.valueOf(_filter_type), search_str, Topitup.DEVICE_TYPE, finalDate, String.valueOf(start), String.valueOf(end));

            } else {
//            call = apiService.get_last_sales_filter(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, is_admin, String.valueOf(_filter_type), search_str, Topitup.DEVICE_TYPE, finalDate,"0","25","40");
                call = apiService.get_last_sales_filter_sp(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, is_admin, String.valueOf(_filter_type), search_str, Topitup.DEVICE_TYPE, finalDate, String.valueOf(start), String.valueOf(end), productId);

            }
        }


        if (_filter_type == 100) {
            if (pinok == 0) {
                showpinDialog();
                dialog.dismiss();
                return;
            }
        }
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Timber.i("REPRINT: res- " + response.body().toString());

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                dialog.dismiss();
                String res = "";
                try {

                    res = response.body().string();
                  //  Log.i("------kousi", res + "." + res.trim().length());

                } catch (Exception ex) {

                    Toasty.info(mContext, "No Results.", 8000, true).show();

                    //  Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                 //   Log.i("------111kousi", ex.getMessage());

                    if (response.body() != null)
                        response.body().close();
                    dialog.dismiss();
                    bottomProgressLayout.setVisibility(View.GONE);

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
                    bottomProgressLayout.setVisibility(View.GONE);

                    if(page==0){
                        Toasty.info(mContext, err_msg, 8000, true).show();
                    }
                    dialog.dismiss();
                    return;

                }


                if (!res.contains("^")) {
                    Toasty.info(mContext, "No Results...", 8000, true).show();
                    dialog.dismiss();
                    bottomProgressLayout.setVisibility(View.GONE);

                    return;
                }
                if (page == 0) {
                    movies.clear();
                }
                try {


                    int rowCount = 0;
                    String[] toSplit = res.split("\n");
                    for (int i = 0; i < toSplit.length; i++) {

                        String s = toSplit[i];
                        //rowCount++;
                        String[] tokens = s.split("\\^");
                        Timber.i("tokens" + i + " value" + tokens[3]);
                        MyItem test = new MyItem();


                        test.stock_uid = tokens[0];
                        try {
                            String value = tokens[3];
                            test.btn_description = tokens[2] + " R " + String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(value));
                        } catch (Exception ex) {
                            test.btn_description = tokens[2];
                        }
                        // Timber.i("REPRINT: " + value);
                        test.btn_date = tokens[1];
                        test.btn_user = tokens[5];

                        movies.add(test);

                        //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());

                    }

                    if (startId == 0) {
                        dialog.dismiss();
                    }
                    bottomProgressLayout.setVisibility(View.GONE);
                    if (page == 0 && movies.size() == 0) {
                        lst_reprint.setVisibility(View.INVISIBLE);
                    } else {
                        lst_reprint.setVisibility(View.VISIBLE);
                    }

                        populateTransactionList(movies);


                } catch (Exception ex) {
                    Toasty.info(mContext, "No Results...." + ex, 8000, true).show();
                    if (response.body() != null){
                        response.body().close();

                    }
                }

//                lst_reprint.setOnItemClickListener(new AdapterListBasic.OnItemClickListener()
//                {
//                    // argument position gives the index of item which is clicked
//                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
//                    {
//                        String selectedmovie=movies.get(position);
//                        //Toast.makeText(getApplicationContext(), "Movie Selected : "+selectedmovie,   Toast.LENGTH_LONG).show();
//                    }
//                });

                bottomProgressLayout.setVisibility(View.GONE);

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

    private void populateTransactionList(ArrayList<MyItem> movies) {
        isLoading = false;
        listAdapter.notifyDataSetChanged();
    }

    public int getCode(String SelectedValue) {
        if (SelectedValue.equals("VODACOM")) {
            return 1;

        } else if (SelectedValue.equals("MTN")) {
            return 2;
        } else if (SelectedValue.equals("CELL C")) {
            return 3;

        } else if (SelectedValue.equals("TELKOM")) {
            return 10;
        } else if (SelectedValue.equals("BLUE VOUCHER")) {
            return 29;

        } else if (SelectedValue.equals("1FORYOU")) {
            return 24;

        } else if (SelectedValue.equals("OTT")) {
            return 25;

        } else if (SelectedValue.equals("101DIALER")) {
            return 18;
        } else if (SelectedValue.equals("FLEXIPIN")) {
            return 40;
        } else if (SelectedValue.equals("VIRGIN")) {

            return 4;
        } else if (SelectedValue.equals("TELKOM")) {

            return 10;
        } else if (SelectedValue.equals("TALK 360")) {

            return 17;
        } else if (SelectedValue.equals("1VOUCHER")) {

            return 24;
        } else if (SelectedValue.equals("SPOTIFY")) {

            return 26;
        } else if (SelectedValue.equals("LYCAMOBILE")) {

            return 19;
        } else if (SelectedValue.equals("RINGAS")) {

            return 30;
        } else if (SelectedValue.equals("SIYAVULA")) {

            return 15;
        } else if (SelectedValue.equals("GLOBAL")) {

            return 21;
        } else if (SelectedValue.equals("EASYLOAD")) {

            return 20;
        } else if (SelectedValue.equals("WORLDCALL")) {

            return 7;
        } else if (SelectedValue.equals("IKEJA")) {

            return 23;
        } else if (SelectedValue.equals("UBER")) {

            return 27;
        } else if (SelectedValue.equals("VIRTUAL UNIPIN")) {

            return 12;
        }
        return 0;
    }



    private void getReprint(int type, final String stock_uid) {

        String ret = "";
        String htmlResponse = "";


        showCustomDialog("", "", false);
        Call<ResponseBody> call;
        if (rdo_filter_type_3.isChecked())
            call = apiService.reprint_cashmx(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid);
        else if (rdo_filter_type_100.isChecked()) {

            call = apiService.reprint_swipe(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, 0, stock_uid, Topitup.DEVICE_TYPE);
        } else if (rdo_filter_type_1.isChecked()) {
            call = apiService.billpayment_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid, Topitup.DEVICE_TYPE);
        } else
            call = apiService.reprint_voucher(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, type, stock_uid);
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

                    Timber.i("REPRINT " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {

                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();
                    } else {

                        if (rdo_filter_type_3.isChecked()) {

                            PrinterTopitup.print_data(res);
                            dialog.dismiss();
                        } else if (rdo_filter_type_1.isChecked()) {
                            try {
                                JSONObject reader = new JSONObject(res);
                                byte[] slip = android.util.Base64.decode(reader.getString("print_data"), android.util.Base64.DEFAULT);

                                //Toasty.error(mContext, "slip="+slip, 8000, true).show();
                                //  if (setting_printer_bypass.equals("1")||)
                                PrinterTopitup.print_data(new String(slip));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        } else if (rdo_filter_type_100.isChecked()) {

                            // Printer.print_data(res);
                            try {
                                JSONObject reader = new JSONObject(res);
                                String cslip = reader.getString("print_data");
                                String mslip = reader.getString("print_data1");
                                PrinterTopitup.print_data(cslip);

                                try {
                                    Thread.sleep(5000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                PrinterTopitup.print_data(mslip);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            dialog.dismiss();
                        } else {
                            int rowCount = 0;
                            String[] toSplit = res.split("\\^");
                            for (int i = 0; i < toSplit.length; i++) {

                                String s = toSplit[i];
                                String[] tokens = s.split("=");

                                if (tokens[0].equals("spi")) {

                                    doReprintLogic(tokens[1], stock_uid);

                                    //Timber.i("REPRINT: " + value);
                                    //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());

                                }
                            }


                        }


                    }


//                    char[] del = new char[] { '=', '^' };
//                    string[] tokens = htmlResponse.Split(del);
//
//                    for (int token = 0; token < tokens.Length; token += 2)
//                    {
//                        string s1 = tokens[token];
//                        string s2 = tokens[token + 1];
//
//                        if (s1 == "uid")
//                            globalVoucher.stock_uid = s2;
//                        if (s1 == "p")
//                            globalVoucher.pin_number_encryped = s2;
//                        if (s1 == "s")
//                            globalVoucher.serial_number = s2;
//                        if (s1 == "t")
//                            globalVoucher.reprint_dtm = s2;
//                        if (s1 == "c")
//                            globalVoucher.reprint_count = s2;
//                        if (s1 == "sp")
//                            globalVoucher.service_provider_id = Int32.Parse(s2);
//                        if (s1 == "spi")
//                            globalVoucher.service_provider_item_id = Int32.Parse(s2);
//                        if (s1 == "dtm")
//                            globalVoucher.tx_date = s2;
//
//                        if (s1 == "l")
//                            if (s2 == "1")
//                                finBalance.low_balance = "1";
//                            else
//                                finBalance.low_balance = "0";
//
//                        if (s1 == "b") finBalance.available_balance = s2;
//                    }


                } catch (Exception ex) {


                    Timber.e("REPRINT " + ex.getMessage());

                    Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                }


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

                        //printer.store_last_reprint(res.toString());
                        //printer.print_data(res.toString());
                        //printer.store_last_reprint(_elecVoucher);
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

                            //printer.store_last_reprint(stringValueBase64Decoded);
                            PrinterTopitup.print_data(stringValueBase64Decoded);
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

        } else {


            final Call<voucher_response> call = apiService.airtime_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid, Topitup.DEVICE_TYPE);

            call.enqueue(new Callback<voucher_response>() {

                @Override
                public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {


                    if(response.isSuccessful()) {
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

                                //printer.store_last_reprint(result.print_data);
                                String slip = result.print_data;

                                int spi = parseInt(result.spi);
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

    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }


    public void showpinDialog() {

        pos_users pincheck = realm.where(pos_users.class).equalTo("posuser_id", parseInt(Topitup.POSUSER_ID)).findFirst();

        String pinnov = pincheck.posuser_pin;
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_inputpin, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView
                .findViewById(R.id.user_input);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /** DO THE METHOD HERE WHEN PROCEED IS CLICKED*/
                                String user_text = (userInput.getText()).toString();

                                /** CHECK FOR USER'S INPUT **/
                                if (user_text.equals(pinnov)) {
                                    // Log.d(user_text, "HELLO THIS IS THE MESSAGE CAUGHT :)");
                                    pinok = 1;
                                    PopulateLastSales(0);
                                    //  Search_Tips(user_text);

                                } else {
                                    pinok = 0;
                                    // Log.d(user_text,"string is empty");
                                    String message = "The pin no. you have entered is incorrect." + " \n \n" + "Please try again!";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Error");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            showpinDialog();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }

                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onItemClickedMain(int position) {
        getReprint(4, movies.get(position).stock_uid);

/*
        if (Topitup.DEVICE_TYPE.equals("WPOS")) {

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
     */
/*       if(total_vouchers_to_print>1)
            {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }*//*

            if (status != 0 && (setting_printer_bypass.equals("0"))) {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);

            } else {

                getReprint(4, movies.get(position).stock_uid);

            }

        } else if (Topitup.DEVICE_TYPE.equals("MOBILE")) {
            if (Topitup.getAppContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                getReprint(4, movies.get(position).stock_uid);

            }
        }
*/
    }
}