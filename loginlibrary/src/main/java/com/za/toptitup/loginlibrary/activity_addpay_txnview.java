package com.za.toptitup.loginlibrary;


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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_addpay_txnview extends AppCompatActivity {

    Context mContext;
    String res;

    Realm realm;
    String company_name,account_number,cslip,mslip;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    TextView fin_balance_default;
    TextView fin_balance_available;
    TextView fin_balance_cash;

    Button btn_print_balance;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_addpay_txn);

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        account_number=tiu_settings.account_number;
        company_name=tiu_settings.company_name;
        Bundle b = getIntent().getExtras();

        if(b != null)
            pid = b.getString("pid");
        BottomNavigationView mBottomNav  = findViewById(R.id.bottom_navigation);
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

        showCustomDialog("Swipe POS","Fetching Txn...");
        handler.postDelayed(runnable2, 500);

    }

    public void onclick_btn_action(View v) {


        //  Toast.makeText(activity_transactions.this,"DEMO"+tiu_settings.company_name,Toast.LENGTH_SHORT).show();
      /*  switch (v.getId()) {
            case R.id.btn_merchant: {

                Printer.print_data(mslip);

                return;
            }
            case R.id.btn_customer: {

                Printer.print_data(cslip);

                return;
            }
            case R.id.btn_cust_merch: {
                Printer.print_data(cslip);


                try {
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Printer.print_data(mslip);
                return;
            }
            case R.id.btn_txn_refund: {

            }
        }*/
        if (v.getId() == R.id.btn_merchant) {
            PrinterTopitup.print_data(mslip);
        } else if (v.getId() == R.id.btn_customer) {
            PrinterTopitup.print_data(cslip);
        } else if (v.getId() == R.id.btn_cust_merch) {
            PrinterTopitup.print_data(cslip);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PrinterTopitup.print_data(mslip);
        } else if (v.getId() == R.id.btn_txn_refund) {
            // Handle txn refund if needed
        }

//        Printer.print_data("1Date: "+res.dtmd);
//     Printer.print_data("1Time: "+res.dtmt);
//        Printer.print_data("1Available: R "+res.available_balance);
//        Printer.print_data("1Balance: R "+res.balance);
//        Printer.print_data("1Credit: R "+res.credit_limit);
//        Printer.print_data("1Cash: R "+res.balance_cash);

    }

    public void get_balance(){
        if (Topitup.checkConnection(getApplicationContext())) {
            //   Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


            final Call<ResponseBody> call = apiService.getswipesalehistory(Topitup.TIU_LICENSE, pid);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                    if (response.code() == 500) {
                        // logresponse();

                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                        return;
                    }

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

                        //stopCustomDialog("Problem",matcher);
                        //Toasty.info(mContext, matcher, 8000, true).show();

                    } else {        //Response OK

                        //  printingCustomDialog("Printing","Busy printing...");

                        try {
                            TextView panno = findViewById(R.id.panno);
                            TextView uid = findViewById(R.id.uid);
                            TextView txn_amount = findViewById(R.id.txn_amount);
                            TextView settled_amount = findViewById(R.id.settled_amount);
                            TextView txn_date = findViewById(R.id.txn_date);
                            TextView ref_no = findViewById(R.id.ref_no);
                            TextView voucher_no = findViewById(R.id.voucher_no);
                            TextView batch_no = findViewById(R.id.batch_no);
                            TextView txn_id = findViewById(R.id.txn_id);
                            TextView trans_sts = findViewById(R.id.trans_sts);
                            TextView ba_sale = findViewById(R.id.ba_sale);
                            TextView bb_sale = findViewById(R.id.bb_sale);
                            LinearLayout ln_balance = findViewById(R.id.ln_balance);

                            JSONObject reader = new JSONObject(res);
                            txn_amount.setText("R "+ reader.getString("tx_amnt"));
                            settled_amount.setText("R "+reader.getString("settled_amnt"));

                            if(!reader.isNull("balance_after_sale"))
                            {


                                if(reader.getString("balance_after_sale").equals("0.00")){
                                    ln_balance.setVisibility(View.GONE);
                                }else {
                                    ln_balance.setVisibility(View.VISIBLE);
                                    ba_sale.setText("R " + reader.getString("balance_after_sale"));
                                    bb_sale.setText("R " + reader.getString("balance_before_sale"));
                                }
                            }
                            else
                            {
                                ln_balance.setVisibility(View.GONE);
                            }



                            uid.setText(reader.getString("uid"));
                            txn_date.setText(reader.getString("dt"));
                            panno.setText(reader.getString("pan"));
                            ref_no.setText(reader.getString("bankreference"));
                            voucher_no.setText(reader.getString("userreference"));
                            batch_no.setText(reader.getString("batchno"));
                            txn_id.setText(reader.getString("customer_tx"));
                            trans_sts.setText("Settled");
                            String tips="0";
                            String discount="0";
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                            String currentDateandTime = sdf1.format(new Date());
                            cslip = "2"+company_name+"\n" +
                                    "2"+account_number+"\n" +
                                    "1\n" +
                                    "1CUSTOMER RECEIPT\n" +
                                    "1\n" +
                                    "1"+currentDateandTime+"\n" +
                                    "1\n" +
                                    "2Approved:"+txn_amount.getText()+"\n" +
                                    "1\n" +
                                    "1Tips :R"+tips+"\n" +
                                    "1\n" +
                                    "1Discount:R"+discount+"\n" +
                                    "1\n" +
                                    "1Tx. Date:"+txn_date.getText()+"\n" +
                                    "1PAN:"+panno.getText()+"\n" +
                                    "1UID:"+uid.getText()+"\n" +
                                    "1Txn. No.:"+txn_id.getText()+"\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1         Top it Up | 0860 111 723\n" +
                                    "1Whatsapp | 064 121 9970\n" +
                                    "1After Hours 23h00-07h00\n" +
                                    "1021 300 0121\n" +
                                    "1     www.itopitup.co.za\n" +
                                    "1";
                            mslip =  "2"+company_name+"\n" +
                                    "2"+account_number+"\n" +
                                    "1\n" +
                                    "1MERCHANT RECEIPT\n" +
                                    "1\n" +
                                    "1"+currentDateandTime+"\n" +
                                    "1\n" +
                                    "2Approved:"+txn_amount.getText()+"\n" +
                                    "1\n" +
                                    "1Tips :R"+tips+"\n" +
                                    "1\n" +
                                    "1Discount:R"+discount+"\n" +
                                    "1\n" +
                                    "1Tx. Date: "+txn_date.getText()+"\n" +
                                    "1PAN:"+panno.getText()+"\n" +
                                    "1UID:"+uid.getText()+"\n" +
                                    "1Txn. No.:"+txn_id.getText()+"\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1         Top it Up | 0860 111 723\n" +
                                    "1Whatsapp | 064 121 9970\n" +
                                    "1After Hours 23h00-07h00\n" +
                                    "1021 300 0121\n" +
                                    "1     www.itopitup.co.za\n" +
                                    "1";





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
dialog.dismiss();
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    /***
                     *
                     * Give error
                     *
                     */
                    // logresponse();
                    //   offlineslip();
                    //Toasty.error(mContext, t.getMessage(), 5000, true).show();
                    //Timber.e(t.getMessage());
                    //t.printStackTrace();
                }

            });
        }else{

            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


        }





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
    Handler handler  = new Handler();
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
