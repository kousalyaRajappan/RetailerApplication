package com.za.toptitup.loginlibrary.admin;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_transactions extends BaseAdminActivity {

    Context mContext;
    fin_balance res;

    Realm realm;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    TextView fin_balance_default, txt_date;
    TextView fin_balance_available;
    TextView fin_balance_cash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        setContentView(R.layout.activity_transactions);

        fin_balance_default = findViewById(R.id.fin_balance_default);
        txt_date = findViewById(R.id.txt_date);
        fin_balance_available = findViewById(R.id.fin_balance_available);
        fin_balance_cash = findViewById(R.id.fin_balance_cash);


        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yy/HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        txt_date.setText(currentDateandTime);
        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                }
                return true;

            }

        });

        showCustomDialog("Financial", "Fetching latest balance...");
        handler.postDelayed(runnable2, 500);

    }

    public void onclick_btn_action(View v) {

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        //  Toast.makeText(activity_transactions.this,"DEMO"+tiu_settings.company_name,Toast.LENGTH_SHORT).show();


        PrinterTopitup.print_data("2" + tiu_settings.company_name + "\n\n" + "1Current Balance Report\n\n\n1Date: " + res.dtmd + "\n1Time: " + res.dtmt + "\n\n\n1Available: R " + res.available_balance + "\n1Balance: R " + res.balance + "\n1Credit: R " + res.credit_limit + "\n1Cash: R " + res.balance_cash + "\n\n\n");
//        Printer.print_data("1Date: "+res.dtmd);
//     Printer.print_data("1Time: "+res.dtmt);
//        Printer.print_data("1Available: R "+res.available_balance);
//        Printer.print_data("1Balance: R "+res.balance);
//        Printer.print_data("1Credit: R "+res.credit_limit);
//        Printer.print_data("1Cash: R "+res.balance_cash);

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

                    //Timber.e(response.body());
//                String res = "";
//                try {
//
//                    res = response.body().string();
//
//                } catch (Exception ex)
//                {
//                    //
//                }


                    try {

                        res = response.body();

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(res);
                        realm.commitTransaction();
                        // realm.close();

                        // Toasty.info(mContext,                 res.toString(), 8000, true).show();


                        //                if (res.contains("<error><err>") || res.contains("ERR:")) {
                        //
                        //                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        //                    tv_response.setTextColor( Color.parseColor("#ff0000"));
                        //                    tv_response.setText(matcher);
                        //
                        //                    txtAccountNumber.setEnabled(true);
                        //                    txtPaymentAmount.setEnabled(true);
                        //                    txtAccountNumberbottomBar .requestFocus();
                        //
                        //                    //Toasty.info(mContext, matcher, 8000, true).show();
                        //
                        //                } else {        //Response OK

                        fin_balance_default.setText(res.balance);
                        fin_balance_available.setText(res.available_balance);
                        fin_balance_cash.setText(res.balance_cash);


                        Button btn_print_balance = findViewById(R.id.btn_print_balance);
                        btn_print_balance.setEnabled(!(res.balance).equals("0.00") || !(res.balance).equals("0.00") || !res.credit_limit.equals("0.00"));


                    } catch (Exception ex) {
                     /*   if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, "Unable to fetch Balance. Check internet connection.", 4000, true).show();

                    }
                }else{
                    Toasty.error(mContext, "Unable to fetch Balance. Check internet connection.", 4000, true).show();

                }


                //}

                dialog.dismiss();

            }


            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

                //Toasty.error(mContext, t.getMessage(), 8000, true).show();
                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

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
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            get_balance();
        }
    };


}
