package com.za.toptitup.loginlibrary.admin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_banktransfer extends BaseAdminActivity {

    public static activity_banktransfer instance;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    protected Topitup app;
    private String retailerPin= "";

    Realm realm;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    RealmResults<fin_balance> tiu_fin_balance;
    EditText txt_amount, amntEditText, amntEditText_cent;
    TextView txt_total_available_default, txt_total_available_cash, txt_total_available_cash_to_tansfer, txt_no_txns, txt_rand, txt_cent;
    RadioButton rdo_filter_type_0;
    RadioButton rdo_filter_type_1;
    Button btn_make_payment;
    RelativeLayout rl_qr_code,rl_main;

    RadioGroup grp_radio_layout;
    Double cash_to_tansfer = 0.0;
    int no_txns;
    CheckBox chk_acc_bank;
    Button bt_process;
    String bankdet;
    LinearLayout tiu_title_bar_new;
    Dialog dialog_multi;
    TextView pop_title2;
    LinearLayout pageLoadingWrapper;
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
            finish();
        }
    };
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    ImageView img_sample_qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        instance = this;
        setContentView(R.layout.activity_banktransfer);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        txt_amount = findViewById(R.id.txt_amount);
        txt_amount.addTextChangedListener(new MoneyTextWatcher(txt_amount));
        activity_login.fromScreen = "transfer_bank";
        rl_qr_code = findViewById(R.id.rl_qr_code);
        rl_main = findViewById(R.id.rl_main);
        img_sample_qr = findViewById(R.id.img_sample_qr);
        txt_total_available_default = findViewById(R.id.txt_total_available_default);
        txt_total_available_cash = findViewById(R.id.txt_total_available_cash);
        txt_total_available_cash_to_tansfer = findViewById(R.id.txt_total_available_cash_to_tansfer);
        txt_no_txns = findViewById(R.id.txt_no_txns);
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        rdo_filter_type_0 = findViewById(R.id.rdo_filter_type_0);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);

        grp_radio_layout = findViewById(R.id.grp_radio_layout);

        btn_make_payment = findViewById(R.id.btn_make_payment);

        btn_make_payment.setText("TRANSFER");
        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        txt_rand = findViewById(R.id.txt_rand);
        txt_cent = findViewById(R.id.txt_cent);

        cent_value_entered = "";
        rand_value_entered = "";
        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));
        grp_radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Toasty.error(mContext, String.valueOf(checkedId), 1000, true).show();

                btn_make_payment.setText("TRANSFER");


            }
        });
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        get_balance();

        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);

        final String setting_balance_login = settings.getString("setting_balance_login", "0");

        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);
        final TextView tiu_clock = findViewById(R.id.tiu_clock);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
        } else {
            // Use the same format for lower versions as well
            sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
        }
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("dd MMM YY  @  HH:mm");
                } else {
                    // Use the same format for lower versions as well
                    sdf = new SimpleDateFormat("dd MMM yy  @  HH:mm", Locale.getDefault());
                }
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);
        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        tiu_title_outlet.setText(tiu_settings.account_number);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);


        /*if (tiu_settings.company_name.length() > 10) {
            tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
        } else {

            tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
        }*/

        if (Topitup.IS_ADMIN.equals("1")) {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Admin");
        } else {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Cashier");
        }
       /* SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dt = sdf1.format(new Date());
        date1=(TextView) findViewById(R.id.date1);
        date1.setText(dt);*/

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
                    }
                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))

                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                        view2.setVisibility(View.VISIBLE);
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

    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_banktransfer.rl_network.setVisibility(View.VISIBLE);
                    activity_banktransfer.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_banktransfer.rl_network.setVisibility(View.INVISIBLE);
                    activity_banktransfer.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_banktransfer.rl_network.setVisibility(View.INVISIBLE);
                    activity_banktransfer.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void onclick_btn_action(View v) {

        /*switch (v.getId()) {


            case R.id.btn_bottom_close:
                onBackPressed();
                return;



            case R.id.btn_make_payment:
                show_confirm();
                //DoTransfer();
                return;


            case R.id.btn_pay_terms:

                Intent myIntentBanking = new Intent(mContext, activity_paycard_terms.class);
                startActivity(myIntentBanking);

            default:



        }*/
        if (v.getId() == R.id.btn_bottom_close) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_make_payment) {
            if (Topitup.ST_STATUS.equals("0")){
                DoTransfer();
            }else{
                ConfirmDialog();

            }

            //
        } else if (v.getId() == R.id.btn_pay_terms) {
            Intent myIntentBanking = new Intent(mContext, activity_paycard_terms.class);
            startActivity(myIntentBanking);
        } else {
            // Default case logic (if any)
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("status of the passcode","status......"+Topitup.ST_STATUS);
        if(Topitup.ST_STATUS .equals("2")){
            String url = "https://admin.topitup.co.za";

            Bitmap qrBitmap = generateQRCode(url);
            img_sample_qr.setImageBitmap(qrBitmap);
            rl_main.setVisibility(GONE);
            rl_qr_code.setVisibility(VISIBLE);
        }else if (Topitup.ST_STATUS.equals("1")) {
            rl_main.setVisibility(VISIBLE);
            rl_qr_code.setVisibility(GONE);
            CallApiForPin();
        }

    }
    public Bitmap generateQRCode(String url) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 512, 512);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void ConfirmDialog() {
//        final String retailerPin = "123456"; // Example stored pin

        Dialog dialogConfirm = new Dialog(this);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirm.setContentView(R.layout.confirm_pin);
        dialogConfirm.setCancelable(true);
        dialogConfirm.show();

        EditText etPasscode = dialogConfirm.findViewById(R.id.et_passcode);
        TextView tvError = dialogConfirm.findViewById(R.id.tv_error);
        TextView btnConfirm = dialogConfirm.findViewById(R.id.btn_confirm);
        TextView btnCancel = dialogConfirm.findViewById(R.id.btn_cancel);

        btnConfirm.setOnClickListener(v -> {
            String enteredPasscode = etPasscode.getText().toString().trim();

            // Hide previous error
            tvError.setVisibility(GONE);

            if (enteredPasscode.isEmpty()) {
                tvError.setText("Please enter your OTP");
                tvError.setVisibility(VISIBLE);
            } else if (enteredPasscode.equals(retailerPin)) {
                dialogConfirm.dismiss();
                show_confirm();
            } else {
                tvError.setText("Incorrect passcode ❌");
                tvError.setVisibility(VISIBLE);
                etPasscode.setText("");
            }
        });

        btnCancel.setOnClickListener(v -> dialogConfirm.dismiss());
    }

    private void CallApiForPin(){
        final Call<ResponseBody> call = apiService.call_for_retailer_pin(Topitup.TIU_LICENSE);

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
                    return;
                }


                //Timber.i("TRANSFER: " +  res);


                if (res.trim().length() == 0 || res.contains("<error><err>") || res.toLowerCase().contains("ERR:") || res.contains("\"err\"")) {

                    //Timber.i("TRANSFER: res- " + res);

                    String err_msg = res;

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.toLowerCase().contains("\"err\":")) {
                        err_msg = res.replace("{\"err\":\"", "");
                        err_msg = err_msg.replace("\"}", "");
                    }

                    Toasty.error(mContext, err_msg, 8000, true).show();
                    return;

                }


//                if (!res.contains("\\^")) {
//                    Toasty.info(mContext, "No Results.", 8000, true).show();
//                    dialog.dismiss();
//                    return;
//                }

                try {
                    JSONObject obj = new JSONObject(res);

                    retailerPin = obj.getString("passcode");
                    Log.e("passcode of retailer","passcode..."+retailerPin);
                    /*if (obj.getString("pin_number").equals("ok")) {
                        PrinterTopitup.print_data(obj.getString("slip"));
                    } else {
//                        Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
//                        dialog.dismiss();
                        return;
                    }*/

                } catch (Exception ex) {
//                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    return;
                }




            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                //progressBar.setVisibility(View.GONE);
                //t.printStackTrace();


            }

        });

    }



    private void DoTransfer() {


        String _filter_type = "0";

        if (rdo_filter_type_1.isChecked()) _filter_type = "1";


        // Toasty.error(mContext, "selected!"+rdo_filter_type_1.isSelected(), 3000, true).show();


        String amount = "";
        String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();

        amount = amountEnter.trim();
        amount = amount.replace("R", "").trim();

        if (amount.length() < 2) {
            Toasty.error(mContext, "Please enter a correct value!", 3000, true).show();
            return;
        }
        Double amt = Double.parseDouble(amount);
        if (cash_to_tansfer <= amt) {
            Toasty.error(mContext, "Amount cannot exceed to Available Transfer Amount!", 5000, true).show();
            return;

        }


        showCustomDialog();

        final Call<ResponseBody> call = apiService.pay_to_bankcard(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, amount);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Timber.i("TRANSFER: res- " + response.body().toString());

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
                    dialog.dismiss();
                    return;
                }

                // Toast.makeText(activity_wallettransfer.this, "v=",Toast.LENGTH_LONG).show();
                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String err_msg = "Could not complete the transfer.";

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.contains("ERR:")) {
                        err_msg = res.replace("ERR:", "");
                    }

                    Toasty.error(mContext, err_msg, 8000, true).show();
                    dialog.dismiss();
                    return;

                }

                try {
                    JSONObject obj = new JSONObject(res);

                    //Log.d("My App", obj.toString());
                    //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";
                    if (obj.has("err")) {
                        String err_msg = obj.getString("err");
                        err_msg = res.replace("err", "");
                        Toasty.info(mContext, err_msg, 8000, true).show();
                        dialog.dismiss();
                        return;
                    }
                    if (obj.getString("ret").equals("ok")) {
                        PrinterTopitup.print_data(obj.getString("slip"));
                    } else {
                        Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
                        dialog.dismiss();
                        return;
                    }

                } catch (Exception ex) {
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    dialog.dismiss();
                    return;
                }


                dialog.dismiss();
                Toasty.success(mContext, "Transfer Complete!", 3000, true).show();
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

    public void show_confirm() {

        //Timber.i("PLUS CLICK 2: " + String.valueOf(service_provider_id));


        int total_vouchers_to_print = 1;


        dialog_multi = new Dialog(mContext);
        dialog_multi.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_multi.setContentView(R.layout.dialog_banktransfer);
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


        /*TextView country= dialog_multi.findViewById(R.id.country);
        TextView amt= dialog_multi.findViewById(R.id.amt);
        TextView phno= dialog_multi.findViewById(R.id.phno);
        TextView voucher= dialog_multi.findViewById(R.id.voucher);*/
        final AppCompatButton bt_close = dialog_multi.findViewById(R.id.bt_close);
        final AppCompatButton bt_process_fin = dialog_multi.findViewById(R.id.bt_process_fin);
        bt_process_fin.setVisibility(View.INVISIBLE);
        TextView bankdetails = dialog_multi.findViewById(R.id.bankdetails);
        chk_acc_bank = dialog_multi.findViewById(R.id.chk_acc_bank_cinfirm);
        chk_acc_bank.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    bt_process_fin.setVisibility(View.VISIBLE);
                } else {
                    bt_process_fin.setVisibility(View.INVISIBLE);

                }

            }
        });
        bankdetails.setText(bankdet);
        TextView payamount = dialog_multi.findViewById(R.id.payamount);
        String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();

        String amount = amountEnter.trim();

        payamount.setText("Paying Amount: R" + amount);

        pop_title2 = dialog_multi.findViewById(R.id.pop_title);


        bt_process_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_multi.dismiss();
                DoTransfer();
            }
        });


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_multi.dismiss();
            }
        });


        dialog_multi.show();
        dialog_multi.getWindow().setAttributes(lp);

    }

    private void showCustomDialog() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        lp.copyFrom(dialog_multi.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);

        pop_title.setText("Requesting");
        pop_content.setText("please wait...");

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

    public void get_balance() {

        if (Topitup.checkConnection(getApplicationContext())) {
            //   Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


            final Call<ResponseBody> call = apiService.get_balance_cash_card(Topitup.TIU_LICENSE, "1");

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
                            JSONObject obj = new JSONObject(res);

                            //TextView tv_paymentterms=(TextView)findViewById(R.id.tv_paymentterms);

                            txt_total_available_default.setText("R " + obj.getString("balance"));
                            txt_total_available_cash.setText("R " + obj.getString("balance_cash"));
                            txt_total_available_cash_to_tansfer.setText("R " + obj.getString("available_cash_to_tansfer"));
                            // tv_paymentterms.setText(obj.getString("tv_paymentterms"));
                            WebView wb_slip = findViewById(R.id.wb_slip);
                            String slip_to_print = obj.getString("tv_paymentterms");

                            String html = "";

                            try {
                                BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                                String line = null;
                                while ((line = bufReader.readLine()) != null) {
                                    String prnt_line = "";
                                    String size = "";

                                    if (line.length() > 1) {
                                        prnt_line = line.substring(1);
                                        size = "" + line.charAt(0);
                                    }

                                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {
                                        if (Topitup.PRINT_BARCODE.equals("1")) {
                                            if (line.contains("BARCODE")) {

                                                html += line.replace("BARCODE:", "");
                                            }
                                        }
                                    } else {
                                        if (size.equals("1")) {

                                            html += prnt_line + "<br/>";

                                        } else {

                                            html += "<span style=\"font-size:1.2em\">";
                                            html += prnt_line + "</span><br/>";

                                        }
                                    }
                                }

                            } catch (Exception ex) {
                                //
                            }
                            String htmlContent = "<html>" +
                                    "<head>" +
                                    "<style type='text/css'>" +
                                    "body { line-height: 1.0; margin: 0; padding: 0; }" +
                                    "br { line-height: 0.8; margin: 0; padding: 0; }" +

                                    "span { line-height: 1.0; }" +
                                    "</style>" +
                                    "</head>" +
                                    "<body>" + html + "</body>" +
                                    "</html>";
                            wb_slip.getSettings().setJavaScriptEnabled(false);
                            wb_slip.loadDataWithBaseURL("", htmlContent, "text/html", "UTF-8", "");
                            //obj.getString("allowd_txns");
                            String savailable_cash_to_tansfer = obj.getString("available_cash_to_tansfer");
                            savailable_cash_to_tansfer = savailable_cash_to_tansfer.replace(",", "");
                            txt_no_txns.setText(obj.getString("no_txns"));
                            no_txns = Integer.parseInt(obj.getString("no_txns"));
                            Integer allowd_txns = Integer.parseInt(obj.getString("allowd_txns"));
                            btn_make_payment.setVisibility(View.VISIBLE);
                            Integer enable_cash_card = Integer.parseInt(obj.getString("enable_cash_card"));
                            if (enable_cash_card == 0) {
                                btn_make_payment.setVisibility(View.GONE);

                            } else {

                                btn_make_payment.setVisibility(View.VISIBLE);
                            }

                            if (allowd_txns <= 0) {
                                btn_make_payment.setVisibility(View.GONE);
                            }
                            bankdet = obj.getString("bankdetails");
                            String cleanedNumber = savailable_cash_to_tansfer.replaceAll("\\s", "");

                            Log.e("before card","card.........."+cleanedNumber);
                            cash_to_tansfer = Double.parseDouble(cleanedNumber);
                            Log.e("before card",cash_to_tansfer+"card.........."+cleanedNumber);

                        } catch (Exception ex) {
//                            Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                            // dialog.dismiss();
                        }

                    }

                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }

            });
        } else {

            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


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