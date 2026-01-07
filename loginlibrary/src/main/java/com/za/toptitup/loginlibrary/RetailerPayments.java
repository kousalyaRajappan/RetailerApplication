package com.za.toptitup.loginlibrary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.MyItemForRetailerPayment;
import com.za.toptitup.loginlibrary.model.RetailerPaymentAdapter;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class RetailerPayments extends BaseAdminActivity {
    Realm realm;
    Context mContext;
    public static RetailerPayments instance;
    TextView date_month, date_display;
    DatePickerDialog datePickerDialog;
    private RecyclerView recyclerView;
    LinearLayoutManager manager;
    ArrayList<MyItemForRetailerPayment> movies = new ArrayList<>();
    private RetailerPaymentAdapter listAdapterRecycle;
    private LinearLayout bottomProgressLayout;
    TextView txt_selected;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    SharedPreferences settings;
    RealmResults<fin_balance> tiu_fin_balance;
    String thread = "readThread", is_admin;

    public String SelectedDate = "", selectedAccount = "";
    SearchView txt_account;
    ListView recycler_search;
    ArrayList<String> arrayList_search;
    ArrayAdapter<String> adapterSearch;
    boolean isLoading = false;
    private int indexId = 0;
    int startId = 0;
    TextView txt_search;
    public String printType = "";
    LinearLayout ll_bottom;
    AppCompatButton bt_close;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;

    Dialog dialogDark;
    private LinearLayout pageLoadingWrapper;
    private TextView pop_title;
    private TextView pop_content;
PrinterTopitup printer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        printer = new PrinterTopitup(this);
        setContentView(R.layout.retailer_payment);

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        date_month = findViewById(R.id.date_month);
        date_display = findViewById(R.id.date_display);
        txt_account = findViewById(R.id.txt_account);
        recycler_search = findViewById(R.id.recycler_search);
        txt_selected = findViewById(R.id.txt_selected);
        recyclerView = findViewById(R.id.recyclerView);
        ll_bottom = findViewById(R.id.ll_bottom);
        ll_bottom.setVisibility(View.GONE);
        activity_login.fromScreen = "retailer_payments";

        txt_search = findViewById(R.id.txt_search);


        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        final TextView tiu_clock = findViewById(R.id.tiu_clock);
//        tiu_batt = (TextView) findViewById(R.id.tiu_batt);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
        }else {
            // Use the same format for lower versions as well
            sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
        }
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");
        update_balance();
        bottomProgressLayout = findViewById(R.id.bottom_progress_layout);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        movies = new ArrayList<>();
        listAdapterRecycle = new RetailerPaymentAdapter(mContext, 0, movies);
        recyclerView.setAdapter(listAdapterRecycle);
        printType = "";
        txt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SelectedDate.equals("")) {
                    PopulateRetailerList(0);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("dx start" + dx, "dy start" + isLoading);
                if (!isLoading) {
                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == movies.size() - 1) {
                        manager.scrollToPosition(movies.size() - 1);
                        indexId++;
                        startId = 10 * indexId;
                        isLoading = true;
                        PopulateRetailerList(startId);
                    }
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                    }
                }
            }
        });
        arrayList_search = new ArrayList<>();

        recycler_search.setVisibility(View.GONE);
        txt_account.setQueryHint("Enter Account # ");
        txt_account.setIconified(false);
        txt_account.clearFocus();

        txt_account.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                recycler_search.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (!SelectedDate.equals("")) {
                    if (s.length() > 2) {
                        recycler_search.setVisibility(View.VISIBLE);
                        adapterSearch.getFilter().filter(s);

                    } else {
                        recycler_search.setVisibility(View.GONE);
                    }
                } else {
                    SelectDialog();

//                    Toast.makeText(RetailerPayments.this,"Please Select the Date",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        recycler_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String name = adapterSearch.getItem(pos);
                recycler_search.setVisibility(View.GONE);
                selectedAccount = name;

                txt_account.setQuery(name, true);
            }
        });
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        tiu_title_outlet.setText(tiu_settings.account_number);
/*
        if (tiu_settings.company_name.length() > 10) {
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
        } else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Admin");

            } else {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", ""));
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

        date_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(RetailerPayments.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                date_display.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                                date_month.setText("Select Month");
                                SelectedDate = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                                txt_selected.setText("Selected Date  " + SelectedDate);
                                callAccountApi("D");
                                txt_account.setQuery("", true);
                                ll_bottom.setVisibility(View.VISIBLE);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate((c.getTimeInMillis()));
            }
        });
        date_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String currentDateFormat = selectedYear + "-" + String.format("%02d", selectedMonth);// + "/" + selectedYear;  //"MM/dd/yyyy"
                        date_month.setText(currentDateFormat);
                        date_display.setText("Select Date");
                        SelectedDate = currentDateFormat;
                        txt_selected.setText("Selected Date  " + SelectedDate);
                        callAccountApi("M");
                        ll_bottom.setVisibility(View.VISIBLE);


                    }
                });
                pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });
    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RetailerPayments.rl_network.setVisibility(View.VISIBLE);
                    RetailerPayments.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RetailerPayments.rl_network.setVisibility(View.INVISIBLE);
                    RetailerPayments.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RetailerPayments.rl_network.setVisibility(View.INVISIBLE);
                    RetailerPayments.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    private void update_balance() {
        settings = getSharedPreferences("TIUPREF", 0);

        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");

        final String setting_balance_admin = settings.getString("setting_balance_admin", "0");
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        final String setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
        final String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
        final String setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");


        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    is_admin = "0";
                    if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";


                    if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {

                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
                        /*if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                    }
                    if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
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
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
                        tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
                    }

                    if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
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
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);

               /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
            }
            if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            }



           /* if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                tiu_title_balance.setText("Standard R " + tiu_fin_balance.get(0).available_balance);

                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }*/

            if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance);
                tiu_title_balance.setVisibility(View.VISIBLE);
            }

            if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {

                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
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
        } catch (Exception ex) {
            //
        }

    }


    public void onclick_btn_action(View v) {

      /*  switch (v.getId()) {
            case R.id.btn_bottom_detail: {

                printType = "D";
                if (!SelectedDate.equals("")) {
                    DisplayDialog();
                } else {
                    SelectDialog();

//                    Toast.makeText(RetailerPayments.this,"Please Select The date",Toast.LENGTH_LONG).show();
                }
//                onBackPressed();
                return;
            }

            case R.id.btn_bottom_summary: {
                printType = "S";
                if (!SelectedDate.equals("")) {

                    DisplayDialog();
                } else {
                    SelectDialog();
//                    Toast.makeText(RetailerPayments.this,"Please Select The date",Toast.LENGTH_LONG).show();
                }


//                Printer.do_last_reprint(mContext);
            }

        }*/
        if (v.getId() == R.id.btn_bottom_detail) {
            printType = "D";
            if (!SelectedDate.equals("")) {
                DisplayDialog();
            } else {
                SelectDialog();
                // Toast.makeText(RetailerPayments.this,"Please Select The date",Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.btn_bottom_summary) {
            printType = "S";
            if (!SelectedDate.equals("")) {
                DisplayDialog();
            } else {
                SelectDialog();
                // Toast.makeText(RetailerPayments.this,"Please Select The date",Toast.LENGTH_LONG).show();
            }
            // Printer.do_last_reprint(mContext);
        }

    }

    private void DisplayDialog() {


        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.display_dialog);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView txt_short = dialog.findViewById(R.id.txt_short);
        TextView txt_long = dialog.findViewById(R.id.txt_long);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txt_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callApiForPrint("S");
            }
        });

        txt_long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callApiForPrint("L");

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void SelectDialog() {

        Dialog dialogSelect = new Dialog(this);
        dialogSelect.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogSelect.setContentView(R.layout.select_dialog);
        dialogSelect.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSelect.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView txt_ok = dialogSelect.findViewById(R.id.txt_ok);
        ImageView img_close = dialogSelect.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelect.dismiss();
            }
        });
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelect.dismiss();
            }
        });
        dialogSelect.show();
        dialogSelect.getWindow().setAttributes(lp);

    }

    public void callApiForPrint(String printLength) {
        final Call<ResponseBody> call;


        call = apiService.get_retailer_payments_print(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, printType, printLength, SelectedDate, selectedAccount);

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

                    Toasty.info(mContext, err_msg, 8000, true).show();

                } else {

                    showCustomDialog("Printing", "Busy printing...");
//                    Topitup.getInstance().printerInit();
                    handler.postDelayed(runnable, 2000);


                    PrinterTopitup.print_data(res);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }
            }
        });

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialogDark.dismiss();
        }
    };

    private void showCustomDialog(String sTitle, String sContent) {

        dialogDark = new Dialog(this);
        dialogDark.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogDark.setContentView(R.layout.dialog_dark);
        dialogDark.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogDark.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialogDark.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialogDark.findViewById(R.id.pop_title);
        pop_content = dialogDark.findViewById(R.id.pop_content);
        if (sTitle.equals("printing")) {

        }

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        bt_close = dialogDark.findViewById(R.id.bt_close);
        bt_close.setVisibility(View.GONE);


        dialogDark.show();
        dialogDark.getWindow().setAttributes(lp);

    }

    private void PopulateRetailerList(int page) {
        if (page == 0) {
            movies.clear();
//            centerProgressLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
//            noItemLayout.setVisibility(View.GONE);
            bottomProgressLayout.setVisibility(View.GONE);
        } else {
            bottomProgressLayout.setVisibility(View.VISIBLE);
        }
//        showCustomDialog();


        // find the radiobutton by returned id

        String is_admin = "0";
        if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";


//        Timber.i("REPRINT: " + Topitup.TIU_LICENSE + ", posuserid: " + Topitup.POSUSER_ID + " ,is_admin: " + is_admin);

        final Call<ResponseBody> call;
        int start = 0, end = 0;
        start = page;
        end = 10;

        call = apiService.get_retailer_payments_list_(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, SelectedDate, selectedAccount, String.valueOf(start), String.valueOf(end));

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

                    Toasty.info(mContext, err_msg, 8000, true).show();
                    bottomProgressLayout.setVisibility(View.GONE);

//                    dialog.dismiss();
                    return;

                }

                String[] toSplit = res.split("\n");
                MyItemForRetailerPayment test;

                for (int i = 0; i < toSplit.length; i++) {
                    test = new MyItemForRetailerPayment();
                    String s = toSplit[i];
                    String[] tokens = s.split("\\^");


                    test.acc_number = tokens[0];
                    test.date = tokens[1];
                    test.amount = tokens[2];
                    test.fee = tokens[3];
                    test.name = tokens[4];
                    movies.add(test);
                }
                bottomProgressLayout.setVisibility(View.GONE);

                if (page == 0 && movies.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                populateTransactionList(movies);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }
            }
        });

    }

    private void populateTransactionList(ArrayList<MyItemForRetailerPayment> movies) {
        isLoading = false;
        listAdapterRecycle.notifyDataSetChanged();
    }

    private void callAccountApi(String format) {
        arrayList_search.clear();
        final Call<ResponseBody> call = apiService.payment_get_retailer(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, format, SelectedDate);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
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

                    Toasty.error(mContext, "API Error" + matcher, 8000, true).show();

                } else {        //Response OK
                    String[] toSplit = res_realtime.split("\n");

                    for (int i = 0; i < toSplit.length; i++) {

                        String s = toSplit[i];
                        String[] tokens = s.split("\\^");
                        Log.e("account numbers", "tokens size......" + tokens.length);
                        Collections.addAll(arrayList_search, tokens);
                    }
                    adapterSearch = new ArrayAdapter<>(RetailerPayments.this, android.R.layout.simple_list_item_1, arrayList_search);
                    recycler_search.setAdapter(adapterSearch);
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

}
