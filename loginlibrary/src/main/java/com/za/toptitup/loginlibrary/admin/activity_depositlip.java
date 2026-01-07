package com.za.toptitup.loginlibrary.admin;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.deposit_slip;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_depositlip extends BaseAdminActivity {

    Context mContext;
    fin_balance res;
    deposit_slip resb;
    EditText txtDepositAmount, amntEditText, amntEditText_cent;
    Realm realm;
    Integer StWallet = 0, CashWallet = 0, stWalletPaymentpt = 0, CashWallettPaymentpt = 0, nedbank = 0, absa = 0;
    String payat_account_number, payat_account_number_cash, acn1, acn2;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    TextView fin_balance_default, txt_rand, txt_cent;
    TextView fin_balance_available;
    TextView fin_balance_cash;
    RadioGroup rdp_wallet, rdp_payment, rdp_bank;
    Button btn_print_balance;
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
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
    private String rand_value_entered = "";
    private String cent_value_entered = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        setContentView(R.layout.activity_depositslip);
        FullscreenCall();
        fin_balance_default = findViewById(R.id.fin_balance_default);
        fin_balance_available = findViewById(R.id.fin_balance_available);
        fin_balance_cash = findViewById(R.id.fin_balance_cash);
        rdp_wallet = findViewById(R.id.rdp_wallet);
        rdp_payment = findViewById(R.id.rdp_payment);
        rdp_bank = findViewById(R.id.rdp_bank);
        View paypointimage = findViewById(R.id.paypointimage);
        rdp_payment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
              /*  switch (checkedId) {

                    case R.id.payment_point:
                        rdp_bank.setVisibility(View.GONE);
                        paypointimage.setVisibility(View.VISIBLE);
                        break;
                    case R.id.bank:
                        rdp_bank.setVisibility(View.VISIBLE);
                        paypointimage.setVisibility(View.GONE);
                        break;
                }*/
                if (checkedId == R.id.payment_point) {
                    rdp_bank.setVisibility(View.GONE);
                    paypointimage.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.bank) {
                    rdp_bank.setVisibility(View.VISIBLE);
                    paypointimage.setVisibility(View.GONE);
                }

            }

        });

        txtDepositAmount = findViewById(R.id.txtDepositAmount);

        //  txtDepositAmount.addTextChangedListener(new MoneyTextWatcher(txtDepositAmount));


        final GetStatusFull tiu_settings = realm.where(GetStatusFull.class).findFirst();
        payat_account_number = tiu_settings.payat_account_number;
        payat_account_number_cash = tiu_settings.payat_account_number_cash;
        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);

        txt_rand = findViewById(R.id.txt_rand);
        txt_cent = findViewById(R.id.txt_cent);

        cent_value_entered = "";
        rand_value_entered = "";
        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));
        txtDepositAmount.setFilters(new InputFilter[]{
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    final int beforeDecimal = 8;
                    final int afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = txtDepositAmount.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        } else if (temp.indexOf(".") == -1) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dumm1);
        mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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

        //     final Call<ResponseBody> call = apiService.get_deposit_slip(Topitup.TIU_LICENSE,Topitup.POSUSER_ID, "1");
        //    Printer.print_data("tets");
      /*  switch (v.getId()) {


            case R.id.btn_one_thous:
                amntEditText.setText("1000");
                break;
            case R.id.btn_onefive_thous:
                amntEditText.setText("1500");
                break;
            case R.id.btn_two_thous:
                amntEditText.setText("2500");
                break;
            case R.id.btn_twofive_thous:
                amntEditText.setText("2500");
                break;
            case R.id.btn_three_thous:
                amntEditText.setText("3000");
                break;
            case R.id.btn_threefive_thous:
                amntEditText.setText("3500");
                break;
            case R.id.btn_four_thous:
                amntEditText.setText("4000");
                break;
            case R.id.btn_fourfive_thous:
                amntEditText.setText("4500");
                break;
            case R.id.btn_five_thous:
                amntEditText.setText("5000");
                break;
            case R.id.btn_fivefive_thous:
                amntEditText.setText("5500");
                break;
            case R.id.btn_six_thous:
                amntEditText.setText("6000");
                break;
            case R.id.btn_seven_thous:
                amntEditText.setText("7000");
                break;
        }*/
        if (v.getId() == R.id.btn_one_thous) {
            amntEditText.setText("1000");
        } else if (v.getId() == R.id.btn_onefive_thous) {
            amntEditText.setText("1500");
        } else if (v.getId() == R.id.btn_two_thous) {
            amntEditText.setText("2500");
        } else if (v.getId() == R.id.btn_twofive_thous) {
            amntEditText.setText("2500");
        } else if (v.getId() == R.id.btn_three_thous) {
            amntEditText.setText("3000");
        } else if (v.getId() == R.id.btn_threefive_thous) {
            amntEditText.setText("3500");
        } else if (v.getId() == R.id.btn_four_thous) {
            amntEditText.setText("4000");
        } else if (v.getId() == R.id.btn_fourfive_thous) {
            amntEditText.setText("4500");
        } else if (v.getId() == R.id.btn_five_thous) {
            amntEditText.setText("5000");
        } else if (v.getId() == R.id.btn_fivefive_thous) {
            amntEditText.setText("5500");
        } else if (v.getId() == R.id.btn_six_thous) {
            amntEditText.setText("6000");
        } else if (v.getId() == R.id.btn_seven_thous) {
            amntEditText.setText("7000");
        }

        String amount = "";
        String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText();

        amount = amountEnter.trim();
        amount = amount.replace("R", "").trim();

        if (amount.equals("0.00") || amount.equals("")) {
            Toasty.error(mContext, "Enter Valid Amount!", 3000, true).show();
            txtDepositAmount.requestFocus();
            return;
        }

        {

            final Call<ResponseBody> call = apiService.get_deposit_slip(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, amount);
            //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    //Timber.i("TRANSFER: res- " + response.body().toString());
                    try {
                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            return;
                        }
                    } catch (Exception ex) {

                        Toasty.error(mContext, "Unknown Error", 8000, true).show();
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

                    //Toast.makeText(activity_depositlip.this, "v=",Toast.LENGTH_LONG).show();
                    if (res.trim().length() == 0) {

                        String err_msg = "No Results.";

                        Toasty.info(mContext, err_msg, 8000, true).show();
                        dialog.dismiss();
                        return;

                    }

                    try {


                        if (rdp_wallet.getCheckedRadioButtonId() == R.id.stdWallet)
                            StWallet = 1;
                        else
                            CashWallet = 1;


                        if (rdp_payment.getCheckedRadioButtonId() == R.id.payment_point) {
                            if (rdp_wallet.getCheckedRadioButtonId() == R.id.stdWallet) {
                                stWalletPaymentpt = 1;
                                CashWallettPaymentpt = 0;
                            } else if (rdp_wallet.getCheckedRadioButtonId() == R.id.cashWallet) {
                                CashWallettPaymentpt = 1;
                                stWalletPaymentpt = 0;
                            }
                        } else {


                            if (rdp_bank.getCheckedRadioButtonId() == R.id.absa)
                                absa = 1;
                            else
                                nedbank = 1;
                        }
                        //  JSONObject obj = new JSONObject(res);

                        //Log.d("My App", obj.toString());
                        //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";
                        String[] arr = res.split("\\n");
                        // List<String> list = new ArrayList<String>(Arrays.asList(arr));
               /* for(int i=0;i<arr.length;i++){
                    System.out.println((i)+"."+arr[i]);
                }*/
                        String printdata = "";
                        // Toasty.info(mContext, ""+CashWallet, 8000, true).show();
                        if (arr.length == 37) {

                            for (int i = 0; i < arr.length; i++) {
                                if (StWallet == 0 && (i == 7 || i == 8 || i == 9)) {

                                } else if (CashWallet == 0 && (i == 10 || i == 11 || i == 12)) {


                                } else if (stWalletPaymentpt == 0 && (i == 13 || i == 14 || i == 15)) {


                                } else if (CashWallettPaymentpt == 0 && (i == 16 || i == 17 || i == 18)) {


                                } else if ((nedbank == 0 && absa == 0) && (i == 18 || i == 19 || i == 20)) {


                                } else if (nedbank == 0 && (i == 21 || i == 22 || i == 23 || i == 24 || i == 25 || i == 26)) {


                                } else if (absa == 0 && (i == 27 || i == 28 || i == 29 || i == 30 || i == 31 || i == 32)) {


                                } else if ((CashWallettPaymentpt == 0 && stWalletPaymentpt == 0) && (i == 33 || i == 34 || i == 35 || i == 36)) {


                                } else {

                                    printdata += arr[i] + "\n";
                                }
                            }

                            //   printdata=arr[0]+"/n"+arr[1]+"/n"+arr[2]+"/n"+arr[3]+"/n"+arr[4]+"/n"+arr[5]+"/n"+arr[6]+"/n"+arr[7]+"/n"+arr[8]+"/n"+arr[9]+"/n"+arr[10]+"/n"+arr[11]+"/n"+arr[12]+"/n"+arr[13]+"/n"+arr[14]+"/n"+arr[15]+"/n"+arr[16]+"/n"+arr[17];

                        }


                        //Toasty.error(mContext, "Account no:"+acn1, 8000, true).show();
                        //  Toasty.error(mContext, "counter"+printdata, 8000, true).show();

                         Log.d("printdata","msg="+printdata);
                        PrinterTopitup.print_data(printdata);
//

                        // String print_data=arr[0]+"\n"+arr[1]+"\n"+arr[2]+"\n"+arr[3]+"\n"+arr[4]+"\n"+arr[5]+"\n"+arr[6]+"\n";
                        // list.remove(7);
                        //  list.remove(8);
                        //  list.remove(9);
                        //  arr=ArrayUtils.remove(arr, 7);
                        //  arr= ArrayUtils.remove(arr, 8);
                        // arr=ArrayUtils.remove(arr, 9);

                        //  print_data=print_data+arr[7]+"\n"+arr[8]+"\n"+arr[9]+"\n";//Standard Wallet

                        //   print_data=print_data+arr[10]+"\n"+arr[11]+"\n"+arr[12]+"\n";//cash Wallet:

                        //  print_data=print_data+arr[13]+"\n"+arr[14]+"\n"+arr[15]+"\n";//payment point (Default wallet)

                        // print_data=print_data+arr[16]+"\n"+arr[17]+"\n"+arr[18]+"\n";//payment point (cash wallet)
                        // arr=ArrayUtils.remove(arr, 7);
                        // arr=ArrayUtils.remove(arr, 7);
                        // arr=ArrayUtils.remove(arr, 7);


             /*   arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);


                arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);

                arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);
                arr=ArrayUtils.remove(arr, 7);*/


                        //  print_data=print_data+arr[19]+"\n"+arr[20]+"\n"+arr[21]+"\n";//Nedback
                        // String result =  StringUtils.join(list, "\n");

                        //Toasty.info(mContext, result, 25000, true).show();

                        // Printer.print_data(result);

                    } catch (Exception ex) {
                        Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                        dialog.dismiss();
                        return;
                    }


                    dialog.dismiss();
                    Toasty.success(mContext, "Complete!", 3000, true).show();
                    onBackPressed();

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


        //   Toast.makeText(activity_depositlip.this,"DEMO"+ResponseBody,Toast.LENGTH_SHORT).show();

        //final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        //  Toast.makeText(activity_transactions.this,"DEMO"+tiu_settings.company_name,Toast.LENGTH_SHORT).show();


        // Printer.print_data("2"+tiu_settings.company_name+"\n\n"+"1Current Balance Report\n\n\n1Date: "+res.dtmd+"\n1Time: "+res.dtmt+"\n\n\n1Available: R "+res.available_balance+"\n1Balance: R "+res.balance+"\n1Credit: R "+res.credit_limit+"\n1Cash: R "+res.balance_cash+"\n\n\n");
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
                        acn1 = res.acn1;
                        acn2 = res.acn2;
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
                        //  if((res.balance).equals("0.00") && (res.balance).equals("0.00") && res.credit_limit.equals("0.00"))
                        //  btn_print_balance.setEnabled(false);
                        //else
                        btn_print_balance.setEnabled(true);


                    } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, "Unable to fetch Balance. Check internet connection.", 4000, true).show();

                    }
                }else {
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

    public class MoneyTextWatcherCent implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcherCent(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                String str = s.toString();
                if (str.equals("")) {
                    if (rand_value_entered.equals("")) {
                        txt_rand.setText("");
                    } else {
                        cent_value_entered = "";
                        txt_rand.setText(rand_value_entered + " Rand ");
                    }
                } else {
                    final long number = Long.parseLong(s.toString());

                    cent_value_entered = WordsConert.convert(number);
                    if (rand_value_entered.equals("")) {
                        txt_rand.setText(cent_value_entered + " Cent");
                    } else {
                        txt_rand.setText(rand_value_entered + " Rand " + cent_value_entered + " Cent");
                    }
                }
            } catch (NumberFormatException e) {
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public class MoneyTextWatcherRand implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcherRand(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                String str = s.toString();
                if (str.equals("")) {

                    if (cent_value_entered.equals("")) {
                        txt_rand.setText("");

                    } else {
                        txt_rand.setText(" " + cent_value_entered + " Cent");
                    }
                } else {
                    final long number = Long.parseLong(s.toString());
                    rand_value_entered = WordsConert.convert(number);
                    if (cent_value_entered.equals("")) {
                        txt_rand.setText(rand_value_entered + " Rand");

                    } else {
                        txt_rand.setText(rand_value_entered + " Rand " + cent_value_entered + " Cent");
                    }
                }
            } catch (NumberFormatException e) {
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            if (s.isEmpty()) return;
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[R,.]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, RoundingMode.FLOOR).divide(new BigDecimal(100), RoundingMode.FLOOR);

            //final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
            //String formatted = nf.getCurrencyInstance().format(parsed);
            editText.setText(parsed.toString());
            editText.setSelection(parsed.toString().length());
            editText.addTextChangedListener(this);
        }
    }


}
