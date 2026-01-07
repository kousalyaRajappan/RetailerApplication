package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class activity_cash_management extends BaseActivity  {

    Context mContext;
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    EditText txtAccountNumber,txtDcCode,txtPaymentAmount,txtDriver,txtDriverCell,txtshipmentno;
    TextView tv_response,tv_value;
    private Button btn_next,btn_ret;
    LinearLayout tiu_title_bar_new;
String data;
    int prtype;
    int process_step;
    int printerQ1Sts;
    RealmResults<fin_balance> tiu_fin_balance;
    double value = 0.00d;

    public static ImageView txt_battery,img_wifi,img_network,img_network2;
    public static RelativeLayout rl_network,rl_server;

    public static activity_cash_management instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_cash_managment);


        Bundle b = getIntent().getExtras();

        if(b != null)
            prtype = b.getInt("ptype");

        txtAccountNumber = findViewById(R.id.txtCustomerNumber);
        txtPaymentAmount = findViewById(R.id.txtPaymentAmount);
        txtshipmentno = findViewById(R.id.txtshipmentno);

        txtPaymentAmount.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    final int beforeDecimal = 8;
                    final int afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = txtPaymentAmount.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        }
                        else if (temp.indexOf(".") == -1) {
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
        //txtPaymentAmount.addTextChangedListener(new activity_cash_management.MoneyTextWatcher(txtPaymentAmount));

        txtAccountNumber.setEnabled(false);

        txtDcCode = findViewById(R.id.txtDcCode);
        txtDriver = findViewById(R.id.txtDriver);
        txtDriverCell = findViewById(R.id.txtDriverCell);
        tv_response = findViewById(R.id.tv_response);
        tv_value = findViewById(R.id.tv_value);
        btn_next = findViewById(R.id.btn_next);
        btn_ret = findViewById(R.id.btn_ret);
        btn_next.setVisibility(View.GONE);

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
        activity_login.fromScreen = "activity_cash_management";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
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
        get_customer_information();

       txtDcCode.setEnabled(false);

        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name","");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier","0");
        final String setting_balance_login = settings.getString("setting_balance_login","0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen","0");

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        tiu_title_outlet.setText(tiu_settings.account_number);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

/*
        if(tiu_settings.company_name.length()>10){
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0,10) + "... | " + tiu_settings.account_number);
        }
        else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/
        if(Topitup.POSUSER_NAME.length()>13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0,12) + "...");
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0,12) + "... ");
                tiu_user_name_.setText("Cashier");

            }
        }else{

            if (Topitup.IS_ADMIN.equals("1")) {
                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", "") );
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", "") );
                tiu_user_name_.setText("Cashier");

            }
        }

    BottomNavigationView mBottomNav  = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dumm1);
        mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;

                    case R.id.action_clear:

                        txtPaymentAmount.setText("");
                        txtDcCode.setText("");
                        txtDriver.setText("");
                        txtDriverCell.setText("");

                        txtDcCode.requestFocus();

                       // btn_next.setText("Next");

                        tv_response.setText("");

                        return true;
                     default:
                         return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    txtPaymentAmount.setText("");
                    txtDcCode.setText("");
                    txtDriver.setText("");
                    txtDriverCell.setText("");

                    txtDcCode.requestFocus();
                    tv_response.setText("");

                    return true;
                } else {
                    return true;
                }

            }

        });


        btn_next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
do_payment();
            }
        });
        btn_ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                         String ret_copy = "";
                ret_copy += "1\n";
                ret_copy += "1***********************\n";
                ret_copy += "1                                  RETAILER COPY\n";
                ret_copy += "1***********************\n";
                       PrinterTopitup.print_data(ret_copy+data);

                Toasty.success(mContext, "Complete!", 3000, true).show();
                onBackPressed();
            }
        });

    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cash_management.rl_network.setVisibility(View.VISIBLE);
                    activity_cash_management.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cash_management.rl_network.setVisibility(View.INVISIBLE);
                    activity_cash_management.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cash_management.rl_network.setVisibility(View.INVISIBLE);
                    activity_cash_management.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    boolean is_busy_with_voucher = false;

    public void get_customer_information(){

        is_busy_with_voucher = true;

        showCustomDialog("Cash Managment","Requesting Customer Number", false);

        final Call<ResponseBody> call = apiService.get_customer_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";

                try {

                   res = response.body().string();

                    JSONObject obj = new JSONObject(res);

                    txtAccountNumber.setText(obj.getString("cashms_acc_number"));

                    String dc_code=obj.getString("dc_code");
                    txtDcCode.setText(dc_code+txtAccountNumber.getText());
                    Selection.setSelection(txtDcCode.getText(), txtDcCode.getText().length());

                    txtDcCode.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(!s.toString().startsWith(dc_code)){
                                txtDcCode.setText(dc_code);
                                Selection.setSelection(txtDcCode.getText(), txtDcCode.getText().length());

                            }

                        }
                    });



                } catch (Exception ex)
                {
                    if (response.body() != null)
                    response.body().close();
                }
              //  Toasty.error(mContext, ""+res, 8000, true).show();
                if (res.contains("<error><err>")) {

                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");

                } else {        //Response OK

                    if(txtDcCode.length()>3){
                        btn_next.setVisibility(View.VISIBLE);
                        // return;
                    }else{

                        btn_next.setVisibility(View.GONE);
                        Toasty.error(mContext, "Customer Number not found!!!, please contact Top it Up.", 8000, true).show();
                    }


                }


                dialog.dismiss();

                //Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet","Please check that your internet connection is working.");
                }
                else {
                    stopCustomDialog("Problem",t.getMessage());
                }

                is_busy_with_voucher = false;

                //progressBar.setVisibility(View.GONE);
                t.printStackTrace();

            }

        });



    }


    public void do_payment(){
        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts=2;
                showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                return;
            }
        }
        if (!PrinterTopitup.check_paper(mContext)) {
            printerQ1Sts=1;
            //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
            return;
        }

        if (txtshipmentno.getText().toString().trim().length() < 5)
        {
            Toasty.error(mContext, "Enter Valid Shipment Number!", 3000, true).show();
            txtshipmentno.requestFocus();
            return;
        }
        if (txtDriver.getText().toString().trim().length() < 3)
        {
            Toasty.error(mContext, "Enter Valid Driver Number!", 3000, true).show();
            txtDriver.requestFocus();
            return;
        }
        if (txtDriverCell.getText().toString().trim().length() < 8)
        {
            Toasty.error(mContext, "Enter Valid Driver Cell No.!", 3000, true).show();
            txtDriverCell.requestFocus();
            return;
        }
        if ((txtPaymentAmount.getText().toString().trim()).equals("R 0.00") || (txtPaymentAmount.getText().toString().trim()).equals("")) {
            Toasty.error(mContext, "Enter Valid Amount!", 3000, true).show();
            txtPaymentAmount.requestFocus();
            return;
        }

            is_busy_with_voucher = true;


        showCustomDialog("Cash Managment","Processing...", false);

        String amount = "";

        amount = txtPaymentAmount.getText().toString().trim();
        amount = amount.replace("R","").trim();


        try {
            value = Double.parseDouble(amount);
            value = round(value * 100);
        } catch (Exception ex)
        {
            value = 0;
        }


        {



            final Call<ResponseBody> call;

           // if(prtype==1)
          call = apiService.cash_acct_payment(Topitup.TIU_LICENSE,Topitup.POSUSER_ID,"penbev",txtAccountNumber.getText().toString().trim(),txtDcCode.getText().toString().trim(),txtDriver.getText().toString().trim(),txtDriverCell.getText().toString().trim(), String.valueOf(value),txtshipmentno.getText().toString().trim());

            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    //Timber.i("TRANSFER: res- " + response.body().toString());
                    if(response.code()==500){
                       Toasty.error(mContext, "Unable to connect, please contact Top it Up.", 8000, true).show();

                        return;
                    }

                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    String res = "";
                    try {

                        res = response.body().string();

                    } catch (Exception ex)

                    {
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

                    if (res.contains("<error><err>")) {
                        dialog.dismiss();
                      //  Toasty.error(mContext, "Insufficient funds!!!, please contact Top it Up.", 8000, true).show();
                        Toasty.error(mContext, res, 8000, true).show();
                        //Toasty.info(mContext, matcher, 8000, true).show();

                    }else{


                        try{
                            //  JSONObject obj = new JSONObject(res);

                            //Log.d("My App", obj.toString());
                            //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";
                            String driver_copy = "";
                            driver_copy += "1\n";
                            driver_copy += "1***********************\n";
                            driver_copy += "1                                      DRIVER COPY\n";
                            driver_copy += "1***********************\n";
                            data=res;

                   /*     String driver_copy = "";
                        driver_copy += "1\n";
                        driver_copy += "1***********************\n";
                        driver_copy += "1                                  RETAILER COPY\n";
                        driver_copy += "1***********************\n";
                       Printer.print_data(retailer_copy+res+"1\n\n-----------------\n\n"+driver_copy+res);
                       */
                            PrinterTopitup.print_data(driver_copy+res);

                        } catch (Exception ex) {
                            Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                            dialog.dismiss();
                            return;
                        }


                        dialog.dismiss();

                        btn_next.setVisibility(View.GONE);
                        btn_ret.setVisibility(View.VISIBLE);

                        Toasty.success(mContext, "Complete!", 3000, true).show();

                    }


                }



                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {


                    // Timber.i("REPRINT: " +  t.getMessage());

                    if (t instanceof IOException) {
                        Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                    }
                    else {
                        Toasty.error(mContext, t.getMessage(), 8000, true).show();
                    }


                    //progressBar.setVisibility(View.GONE);
                    //t.printStackTrace();

                    dialog.dismiss();

                }

            });









        }





    }




    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;
    Dialog dialog;

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
            if(printerQ1Sts==1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(View.VISIBLE);
            }
            else {
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
                dialog.dismiss();
                do_payment();
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
            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            if (settings.getString("setting_time_out","0").equals("1")) {

                //Toasty.error(mContext, "test", 5000, true).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Toasty.error(mContext, "test2t", 5000, true).show();
                        // Do something after 5s = 5000ms
                        logout();
                    }
                }, 10);
            }else {
                finish();
            }
        }
    };


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
