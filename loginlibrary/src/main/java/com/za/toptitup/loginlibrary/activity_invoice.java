package com.za.toptitup.loginlibrary;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import com.za.toptitup.loginlibrary.model.ReprintList;
import com.za.toptitup.loginlibrary.model.ReprintListAdapter;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_invoice extends BaseAdminActivity {

    Realm realm;
    private BroadcastReceiver mNetworkReceiver;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    EditText txt_input;
    RadioButton rdo_filter_type_0;
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_2;
    RadioGroup grp_radio_layout, grp_radio_wallet;
    ListView lst_reprint;
    int value = -1;
    TextView txtStatus, txtStatus2;
    String alltx = "1";
    RealmResults<fin_balance> tiu_fin_balance;
    RadioButton rdbAll, rdbpayment, rdbbills, rdbinv, rdbcomm;
    EditText date_start, date_end;
    DatePickerDialog datePickerDialog;
    LinearLayout ll_date;
    private boolean flag_loading;
    private ReprintListAdapter listAdapterRecycle;
    private RecyclerView recyclerView;
    private LinearLayout bottomProgressLayout;

    LinearLayoutManager manager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = true;
    private int indexId = 0;
    int startId = 0;
    boolean isLoading = false;
    public static ImageView txt_battery,img_wifi,img_network,img_network2;
    public static RelativeLayout rl_network,rl_server;

    public static activity_invoice instance;
    public static String setting_printer_bypass;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_invoice);
        activity_login.fromScreen = "activity_invoice";
        /* END HEADER */
        grp_radio_wallet = findViewById(R.id.grp_radio_wallet); //grp_radio_layout
        grp_radio_layout = findViewById(R.id.grp_radio_layout); //grp_radio_layout
        /* TIU HEADER */
         settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        setting_printer_bypass = settings.getString("setting_print_bypass", "0");
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

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
       final LinearLayout tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();


        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        ll_date = findViewById(R.id.ll_date);
        date_start = findViewById(R.id.date_start);
        date_start.setShowSoftInputOnFocus(false);
        date_end = findViewById(R.id.date_end);
        date_end.setShowSoftInputOnFocus(false);


        txtStatus = findViewById(R.id.txtStatus);
        txtStatus2 = findViewById(R.id.txtStatus2);

        //   grp_radio_layout2=(RadioGroup) findViewById(R.id.grp_radio_layout2); //grp_radio_layout
        rdbAll = findViewById(R.id.rdo_filter_type_0);
        rdbpayment = findViewById(R.id.rdo_filter_type_2);
        rdbbills = findViewById(R.id.rdo_filter_type_3);
        rdbinv = findViewById(R.id.rdo_filter_type_4);
        // rdbcomm=(RadioButton) findViewById(R.id.rdo_filter_type_5);
        lst_reprint = findViewById(R.id.lst_reprint);

        recyclerView = findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        movies = new ArrayList<>();
        listAdapterRecycle = new ReprintListAdapter(mContext, 0, movies);
        recyclerView.setAdapter(listAdapterRecycle);
        bottomProgressLayout = findViewById(R.id.bottom_progress_layout);

        ((Topitup) getApplication()).checkWifiSimInternet(this);

//        tiu_title_bar_new.setVisibility(View.GONE);
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
       tiu_clock.setText(strDate+" ");

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
        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                date_start.setShowSoftInputOnFocus(false);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(activity_invoice.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date_start.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));

                                if (!date_end.equals("")) {
                                    boolean isGreater = compareDate(date_start.getText().toString(), date_end.getText().toString());
                                    if (!isGreater) {
                                        date_end.setText("");
                                    } else {
                                        movies.clear();
                                        startId = 0;
                                        Log.e("radio 111",".....66666666666666666...");

                                        PopulateLastSales(0);
                                    }

                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate((c.getTimeInMillis()));
            }
        });

        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                date_end.setShowSoftInputOnFocus(false);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(activity_invoice.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                boolean isGreater = compareDate(date_start.getText().toString(), year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));

                                if (isGreater) {
                                    startId = 0;
                                    movies.clear();

                                    Log.e("radio 111",".....77777777777777777...");

                                    PopulateLastSales(0);
                                    date_end.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate((c.getTimeInMillis()));

            }
        });

        tiu_title_outlet.setText(tiu_settings.account_number);

        /*if (tiu_settings.company_name.length() > 10) {
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
        } else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                tiu_user_name_.setText("Admin");

            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "...");
                tiu_user_name_.setText("Cashier");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", "") );
                tiu_user_name_.setText("Cashier");

            }
        }

        try {

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
                     /*   tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);*/
                    }

                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");

                    }
                    if (setting_balance_login_swipe.equals("1") || setting_balance_cashier_swipe.equals("1")) {

                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }
                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance+" ");
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                    tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
            }
            if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
            }
            if (setting_balance_login_commission.equals("1") || setting_balance_cashier_commission.equals("1")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission+"  ");

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


        getCurrentDate();
        getYesterdayDate();




        Bundle b = getIntent().getExtras();

        if (b != null)
            value = b.getInt("key");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("dx start"+dx,"dy start"+isLoading);
                if (!isLoading) {
                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == movies.size() - 1) {
                        manager.scrollToPosition(movies.size() - 1);
                        indexId++;
                        startId = 10 * indexId;
                        isLoading = true;
                        Log.e("radio 111",".....888888888888...");

                        PopulateLastSales(startId);
                    }
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                    }
                }
            }
        });
        if (value == 1) {
            txtStatus.setVisibility(View.GONE);
            txtStatus2.setVisibility(View.GONE);/*
            date_start.setVisibility(View.GONE);
            date_end.setVisibility(View.GONE);*/
            ll_date.setVisibility(View.GONE);

            grp_radio_layout.setVisibility(View.GONE);
            grp_radio_wallet.setVisibility(View.GONE);
        } else {
            txtStatus.setVisibility(View.VISIBLE);
            txtStatus2.setVisibility(View.VISIBLE);/*
            date_start.setVisibility(View.VISIBLE);
            date_end.setVisibility(View.VISIBLE);*/
            ll_date.setVisibility(View.VISIBLE);
            grp_radio_layout.setVisibility(View.VISIBLE);
            grp_radio_wallet.setVisibility(View.VISIBLE);

        }


        grp_radio_wallet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                //  grp_radio_layout2.clearCheck();
                try {
                    movies.clear();

                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                }catch(Exception e){


                }
                int selectedId = grp_radio_layout.getCheckedRadioButtonId();

                if (selectedId == R.id.rdo_filter_type_0) {
                    alltx = "1";
                } else if (selectedId == R.id.rdo_filter_type_2) {

                    alltx = "0";
                } else if (selectedId == R.id.rdo_filter_type_3) {

                    alltx = "2";
                } else if (selectedId == R.id.rdo_filter_type_4) {
                    alltx = "3";

                }

                Log.e("radio 111",".....11111111111...");

                //      grp_radio_layout2.clearCheck();
                PopulateLastSales(0);
                //some code


            }
        });
        grp_radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (checkedId == R.id.rdo_filter_type_0) {//All
                    //  grp_radio_layout2.clearCheck();
                    movies.clear();
                    if(listAdapter!=null) {

                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                    alltx = "1";
                    //      grp_radio_layout2.clearCheck();
                    Log.e("radio 111",".....2222222222222...");

                    PopulateLastSales(0);
                    //some code
                } else if (checkedId == R.id.rdo_filter_type_2) {//Payments
                    // grp_radio_layout2.clearCheck();
                    movies.clear();
                    if(listAdapter!=null) {
                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }

                    alltx = "0";
                    //    grp_radio_layout2.clearCheck();
                    Log.e("radio 111",".....33333333333333...");

                    PopulateLastSales(0);
                    //some code
                } else if (checkedId == R.id.rdo_filter_type_3) {//Bills
                    // grp_radio_layout2.clearCheck();
                    movies.clear();
                    if(listAdapter!=null) {

                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                    alltx = "2";
                    //   grp_radio_layout2.clearCheck();
                    Log.e("radio 111",".....44444444444444...");

                    PopulateLastSales(0);
                    //some code
                } else if (checkedId == R.id.rdo_filter_type_4) {//Commissions
                    // grp_radio_layout2.clearCheck();
                    movies.clear();
                    if(listAdapter!=null) {

                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                    alltx = "3";
                    Log.e("radio 111",".....5555555555555...");

                    PopulateLastSales(0);
                    //some code
                }

            }
        });

        Log.e("radio 111",".....99999999999999999...");

        PopulateLastSales(0);

    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_invoice.rl_network.setVisibility(View.VISIBLE);
                    activity_invoice.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_invoice.rl_network.setVisibility(View.INVISIBLE);
                    activity_invoice.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_invoice.rl_network.setVisibility(View.INVISIBLE);
                    activity_invoice.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public boolean compareDate(String startDate, String endD1ate) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        boolean b = false;
        try {
            //If start date is after the end date
            if (dfDate.parse(startDate).before(dfDate.parse(endD1ate))) {
                b = true;//If start date is before end date
            } else b = dfDate.parse(startDate).equals(dfDate.parse(endD1ate));//If two dates are equal
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public void getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        date_end.setText(mYear + "-" + String.format("%02d", (mMonth + 1)) + "-" + String.format("%02d", mDay));

    }


    public void getYesterdayDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        date_start.setText(mYear + "-" + String.format("%02d", (mMonth + 1)) + "-" + String.format("%02d", mDay - 1));

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

                        if (status != 0 && setting_printer_bypass.equals("0")) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                            return;
                        } else {
                            //  Printer.do_last_reprint(mContext);
                            if (setting_printer_bypass.equals("1")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);

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

            case R.id.btn_top_search: {

                try {
                    movies.clear();
                    listAdapter.clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    //
                }

                PopulateLastSales(0);
            }


        }*/
        if (v.getId() == R.id.btn_bottom_close) {
            onBackPressed();

        } else if (v.getId() == R.id.btn_bottom_reprint_last) {
            if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                if (android.os.Build.MODEL.equals("P052")) {
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
            }

        } else if (v.getId() == R.id.btn_top_search) {
            try {
                movies.clear();
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                //
            }

            Log.e("radio 111",".....1010101010101...");

            PopulateLastSales(0);
        }


    }

    ArrayList<MyItem> movies = new ArrayList<MyItem>();
    ReprintList listAdapter;

    private void PopulateLastSales(int page) {
        if (page == 0) {
//            centerProgressLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
//            noItemLayout.setVisibility(View.GONE);
            bottomProgressLayout.setVisibility(View.GONE);
        } else {
            bottomProgressLayout.setVisibility(View.VISIBLE);
        }
        showCustomDialog("","",false);

        int _filter_type = 0;

        int selectedId = grp_radio_wallet.getCheckedRadioButtonId();

        Log.e("selected id","....id....."+selectedId);
        // find the radiobutton by returned id
            String valueSelected = "0";
        if (selectedId == R.id.radio_all) {
            RadioButton selectedRadioButton = findViewById(selectedId);

            valueSelected = selectedRadioButton.getText().toString();

        } else if (selectedId == R.id.radio_standard) {

            RadioButton selectedRadioButton = findViewById(selectedId);

            valueSelected = selectedRadioButton.getText().toString();
        } else if (selectedId == R.id.radio_bill) {

            RadioButton selectedRadioButton = findViewById(selectedId);

            valueSelected = selectedRadioButton.getText().toString();

        }


        Log.e("selected value",".......value selected......."+valueSelected);
//        RadioButton wallet = findViewById(selectedId);

        String is_admin = "0";
        if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";


//        Timber.i("REPRINT: " + Topitup.TIU_LICENSE + ", posuserid: " + Topitup.POSUSER_ID + " ,is_admin: " + is_admin);

        final Call<ResponseBody> call;
        int start = 0, end = 0;
        start = page;
        end =  10;
        if (value == 1) {

            call = apiService.get_invoices_(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "0", "0", String.valueOf(start), String.valueOf(end));
        } else {
            grp_radio_layout.setVisibility(View.VISIBLE);
            String startDatee = date_start.getText().toString().replace("-", "");
            String endDatee = date_end.getText().toString().replace("-", "");

            call = apiService.get_fintx(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, alltx, valueSelected, startDatee, endDatee, String.valueOf(start), String.valueOf(end));
        }

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
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    bottomProgressLayout.setVisibility(View.GONE);

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
                    bottomProgressLayout.setVisibility(View.GONE);

                    dialog.dismiss();
                    return;

                }

                if (value == 2) {

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        JSONArray jsonArry = jsonObject.getJSONArray("arr");

                        for (int i = 0; i < jsonArry.length(); i++) {
                            MyItem test = new MyItem();
                            JSONObject objin = jsonArry.getJSONObject(i);

                            test.stock_uid = objin.getString("tx_id");
                            test.btn_description = objin.getString("customer_order_no") + " (" + objin.getString("tx_type") + ")";
                            test.btn_date = objin.getString("customer_tx_date");
                            test.btn_user = objin.getString("customer_tx_amount");
                            movies.add(test);

                        }

                    } catch (JSONException e) {
                        bottomProgressLayout.setVisibility(View.GONE);

                        dialog.dismiss();
                        e.printStackTrace();
                    }


                    if (startId == 0) {
                        dialog.dismiss();
//                        centerProgressLayout.setVisibility(View.GONE);
//                        noItemLayout.setVisibility(View.GONE);
                    }
                    bottomProgressLayout.setVisibility(View.GONE);
                    if(page == 0 && movies.size()==0){
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    populateTransactionList(movies);

                    lst_reprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            getReprint(movies.get(position).stock_uid);

                        }
                    });


                } else {
                    if (!res.contains("^")) {
                        bottomProgressLayout.setVisibility(View.GONE);

                        Toasty.info(mContext, "No Results.", 8000, true).show();
                        dialog.dismiss();
                        return;
                    }

                    try {


                        Timber.i("REPRINT RES: " + res);


                        int rowCount = 0;
                        String[] toSplit = res.split("\n");
                        for (int i = 0; i < toSplit.length; i++) {

                            String s = toSplit[i];
                            //rowCount++;
                            String[] tokens = s.split("\\^");

                            MyItem test = new MyItem();

                            String value = tokens[3];

                            Timber.i("REPRINT: " + value);


                            test.stock_uid = tokens[0];
                            try {
                                test.btn_description = tokens[1];
                            } catch (Exception ex) {
                                test.btn_description = tokens[1];
                            }

                            test.btn_date = tokens[2];
                            test.btn_user = tokens[3];

                            movies.add(test);
                            dialog.dismiss();
                            //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());

                        }

                        if (startId == 0) {
                            dialog.dismiss();
//                        centerProgressLayout.setVisibility(View.GONE);
//                        noItemLayout.setVisibility(View.GONE);
                        }
                        bottomProgressLayout.setVisibility(View.GONE);
                        if(page == 0 && movies.size()==0){
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
//                        populateTransactionList(movies);
                        populateTransactionList(movies);

//                        listAdapter = new ReprintList(mContext, 0, movies);
//                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
//                        lst_reprint.setAdapter(listAdapter);

                        lst_reprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                getReprint(movies.get(position).stock_uid);
                                //Toasty.info(mContext, "ID " +  String.valueOf(movies.get(position).stock_uid), 8000, true).show();
                                //Toast.makeText(MainActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

                            }
                        });

                    } catch (Exception ex) {
                        bottomProgressLayout.setVisibility(View.GONE);


                        Toasty.info(mContext, "No Results.", 8000, true).show();
                        dialog.dismiss();
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
//        resultModelList.addAll(result);
        isLoading = false;
        listAdapterRecycle.notifyDataSetChanged();
    }


    private void getReprint(final String stock_uid) {
        final Call<ResponseBody> call;
        if (value == 1) {

            call = apiService.invoice_print(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid);

        } else {
            call = apiService.print_fintx(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid);
        }


        //   final Call<ResponseBody> call = apiService.print_fintx(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid);


        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    // stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    // stopCustomDialog("Problem",matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

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
