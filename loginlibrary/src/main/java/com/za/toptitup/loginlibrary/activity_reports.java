package com.za.toptitup.loginlibrary;


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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.admin.Printingw;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_reports extends BaseAdminActivity {

    Realm realm;
    private BroadcastReceiver mNetworkReceiver;
    RelativeLayout rl_report_date;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    EditText txt_input;
    RadioButton rdo_filter_type_0;
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_2;
    RadioGroup grp_radio_layout;
    ListView lst_reprint;
    int value = -1;
    String alltx = "1";
    RealmResults<fin_balance> tiu_fin_balance;
    int sales;
    DatePicker picker;
    WebView wb_report;
    String printdata;
    Button bt_print;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;
    public static String setting_printer_bypass;
    public static activity_reports  instance ;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_reports);

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
        bt_print = findViewById(R.id.bt_bottom_print);
        wb_report = findViewById(R.id.wb_report);
        picker = findViewById(R.id.datePicker1);
        rl_report_date = findViewById(R.id.rl_report_date);



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
        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);

        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        activity_login.fromScreen = "activity_reports";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);

        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();

        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);
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
        tiu_title_outlet.setText(tiu_settings.account_number);
/*
        if(tiu_settings.company_name.length()>10){
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
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

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
            //
        }
        /* END HEADER */

        grp_radio_layout = findViewById(R.id.grp_radio_layout); //grp_radio_layout


        Bundle b = getIntent().getExtras();

        if (b != null)
            sales = b.getInt("key");
        //  Toasty.success(mContext, "Psal="+sales, Toast.LENGTH_LONG).show();
//if(sales!=0 && sales!=6 && sales!=5) {
        if (sales == 3 || sales == 4 || sales == 5 || sales == 1 || sales == 8) {
            LinearLayout pickerParentLayout =
                    (LinearLayout) picker.getChildAt(0);
            LinearLayout pickerSpinnersHolder = (LinearLayout) pickerParentLayout.getChildAt(0);
            NumberPicker picker = (NumberPicker) pickerSpinnersHolder.getChildAt(0);
            picker.setVisibility(View.GONE);

        }


    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reports.rl_network.setVisibility(View.VISIBLE);
                    activity_reports.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reports.rl_network.setVisibility(View.INVISIBLE);
                    activity_reports.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_reports.rl_network.setVisibility(View.INVISIBLE);
                    activity_reports.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    public void onclick_btn_action(View v) {

       /* switch (v.getId()) {
            case R.id.btn_bottom_close: {
                onBackPressed();
                return;
            }


            case R.id.btn_top_search:

                return;
            case R.id.btn_view:
                // int yyyymm= picker.getYear()+"/"+picker.getMonth() + 1;
                //  Toasty.error(mContext, "Selected Date: "+picker.getYear() +""+ (String.format("%02d",(picker.getMonth()+ 1)) )+""+picker.getDayOfMonth(), Toast.LENGTH_LONG).show();
                sales_report();
                return;

            case R.id.bt_bottom_print:
                // int yyyymm= picker.getYear()+"/"+picker.getMonth() + 1;
                //  Toasty.error(mContext, "Selected Date: "+picker.getYear() +""+ (String.format("%02d",(picker.getMonth()+ 1)) )+""+picker.getDayOfMonth(), Toast.LENGTH_LONG).show();
//sales_report();
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
                        } else {
                            // Printer.print_data(printdata);
                            if (setting_printer_bypass.equals("1")) {
                                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                            } else {
                                Printer.print_data(printdata);
                            }
                        }
                    }else{
                        Printer.print_data(printdata);

                    }

                }else{
                    Printer.print_data(printdata);

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

        } else if (v.getId() == R.id.btn_top_search) {

        } else if (v.getId() == R.id.btn_view) {
            // int yyyymm = picker.getYear() + "/" + picker.getMonth() + 1;
            // Toasty.error(mContext, "Selected Date: " + picker.getYear() + "" + (String.format("%02d", (picker.getMonth() + 1))) + "" + picker.getDayOfMonth(), Toast.LENGTH_LONG).show();
            sales_report();

        } else if (v.getId() == R.id.bt_bottom_print) {
            // int yyyymm = picker.getYear() + "/" + picker.getMonth() + 1;
            // Toasty.error(mContext, "Selected Date: " + picker.getYear() + "" + (String.format("%02d", (picker.getMonth() + 1))) + "" + picker.getDayOfMonth(), Toast.LENGTH_LONG).show();
            // sales_report();

            if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                String selectedPrinter = settings.getString("printer", "inner");

                if (selectedPrinter.equals("inner")) {
                    if (android.os.Build.MODEL.equals("P052")) {
                        PrinterTopitup.print_data(printdata);

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

            /* if(total_vouchers_to_print > 1) {
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            } */

                    if (status != 0 && setting_printer_bypass.equals("0")) {
                        showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                    } else {
                        // Printer.print_data(printdata);
                        if (setting_printer_bypass.equals("1")) {
                            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                        } else {
                            PrinterTopitup.print_data(printdata);
                        }
                    }
                }
                } else {
                    PrinterTopitup.print_data(printdata);
                }

            } else {
                PrinterTopitup.print_data(printdata);
            }

        } else {
            // Handle any additional cases if needed
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

    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            // finish();
        }
    };


    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }

    public void sales_report() {

        final Call<ResponseBody> call;
        if (sales == 0)
            call = apiService.daily_sales_report(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, picker.getYear() + (String.format("%02d", (picker.getMonth() + 1))) + String.format("%02d", picker.getDayOfMonth()));
        else if (sales == 3)
            call = apiService.deposit_statement(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, picker.getYear() + (String.format("%02d", (picker.getMonth() + 1))));
        else if (sales == 4)
            call = apiService.commission_statement(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, picker.getYear() + (String.format("%02d", (picker.getMonth() + 1))));
        else if (sales == 6)
            call = apiService.swipe_daily_sales(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.CUSTOMER_ID, String.format("%04d", picker.getYear()), String.format("%02d", (picker.getMonth() + 1)), String.format("%02d", picker.getDayOfMonth()), Topitup.DEVICE_TYPE);
        else if (sales == 5)
            call = apiService.swipe_monthly_sales(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.CUSTOMER_ID, String.format("%04d", picker.getYear()), String.format("%02d", (picker.getMonth() + 1)), Topitup.DEVICE_TYPE);
        else if (sales == 8)
            call = apiService.swipe_monthly_sales_summary(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.CUSTOMER_ID, String.format("%04d", picker.getYear()), String.format("%02d", (picker.getMonth() + 1)), Topitup.DEVICE_TYPE);
        else if (sales == 9)
            call = apiService.swipe_daily_sales_summary(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.CUSTOMER_ID, String.format("%04d", picker.getYear()), String.format("%02d", (picker.getMonth() + 1)), String.format("%02d", picker.getDayOfMonth()), Topitup.DEVICE_TYPE);
        else if (sales == 10)
            call = apiService.swipe_daily_recon(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, Topitup.CUSTOMER_ID, String.format("%04d", picker.getYear()), String.format("%02d", (picker.getMonth() + 1)), String.format("%02d", picker.getDayOfMonth()), Topitup.DEVICE_TYPE);
        else if (sales == 11)
            call = apiService.daily_commission_statement(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, picker.getYear() + (String.format("%02d", (picker.getMonth() + 1))) + String.format("%02d", picker.getDayOfMonth()));
        else

            call = apiService.monthly_sales_report(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, picker.getYear() + (String.format("%02d", (picker.getMonth() + 1))));

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
                    printdata = res;
                    bt_print.setVisibility(View.VISIBLE);

                    picker.setVisibility(View.GONE);
//                    btn_view
                    rl_report_date.setVisibility(View.GONE);
                    wb_report.setVisibility(View.VISIBLE);
                    wb_report.getSettings().setJavaScriptEnabled(false);

                    Log.e("print data","........."+printdata);
                    String html = "";
                    try {
                        BufferedReader bufReader = new BufferedReader(new StringReader(printdata));
                        String line = null;

                        // Start using a monospaced font for better alignment
                        html += "<div style=\"font-family:monospace; white-space:pre;\">";

                        while ((line = bufReader.readLine()) != null) {
                            String prnt_line = "";
                            String size = "";

                            // Extract the size and text
                            if (line.length() > 1) {
                                size = line.substring(0, 1); // First character as size
                                prnt_line = line.substring(1); // Rest as content
                            }

                            // Preserve alignment and spacing
                            html += prnt_line + "<br/>";
                        }

                        html += "</div>"; // Close the monospaced block
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    wb_report.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stopCustomDialog("Problem", t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }
}
