package com.za.toptitup.loginlibrary;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wiseasy.cashier.CashierHelper;
import com.wiseasy.cashier.InvokeConstant;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import com.za.toptitup.loginlibrary.model.swipetxn;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_adpay extends BaseActivity {


    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static activity_adpay instance;
    private static int MAX_CHARACTERS = 10;
    private final String TAG = "invoke--ConsumeActivity";
    private final HashMap params = new HashMap();
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    RealmResults<fin_balance> tiu_fin_balance;
    EditText et_amount_consume, et_note;
    EditText txt_sale_amount, txt_perc_tip, txt_rand_tip, txt_perc_disc, txt_rand_disc, txt_total, txt_notes;
    TextView tv_result_consume;
    Button btn_consume;
    String ret_copy = "", setting_chk_auto_mpos, cslip, mslip, enable_realtime_swipe, setting_slip_cancel;
    String orderno, amount, tips = "0.00", discount = "0.00";
    RelativeLayout reltxnscreen, relpayscreen, lo_tip, lo_disc;
    WebView wb_slip;
    String html = "";
    int MPOSTYPE;
    swipetxn postxn;
    String postxnresponse;
    int nextId;
    String company_name, account_number;
    String card_no = "000000XXXXXXXX00";
    String voucher_no = "";
    String batch_no = "";
    String refer_no = "";
    String trans_time = "";
    String amountd;
    String notes;
    String code;
    String retresponse;
    CountDownTimer cntdwnTimer;
    Boolean isRunning;
    int totcnt = 50;
    boolean ordersuccess = false;
    boolean apiCall = false;
    CheckBox chk_discount, chk_tips;
    LinearLayout tiu_title_bar_new;
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
        }
    };
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume_new);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        instance = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FullscreenCall();

        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos", "0");
        enable_realtime_swipe = settings.getString("enable_realtime_swipe", "0");
        setting_slip_cancel = settings.getString("setting_slip_cancel", "0");
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
        MPOSTYPE = settings.getInt("MPOSTYPE", 0);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        account_number = tiu_settings.account_number;
        company_name = tiu_settings.company_name;
        activity_login.fromScreen = "activity_adpay";
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);


        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");
    /*    TextView  mIsDemo = (TextView) findViewById(R.id.activity_login_is_demo);
        if (Topitup.DEBUG) {
            mIsDemo.setVisibility(View.INVISIBLE);
        }else {
            mIsDemo.setVisibility(View.VISIBLE);
        }*/
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

      /*  if(tiu_settings.company_name.length()>10){
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


        // tv_result_consume = (TextView) findViewById(R.id.tv_response);
        reltxnscreen = findViewById(R.id.reltxnscreen);
        relpayscreen = findViewById(R.id.relpayscreen);
        lo_tip = findViewById(R.id.lo_tip);
        lo_disc = findViewById(R.id.lo_disc);

        chk_tips = findViewById(R.id.chk_tips);
        chk_discount = findViewById(R.id.chk_discount);


        wb_slip = this.findViewById(R.id.wb_slip);
        String setting_tv_rand_value = settings.getString("setting_tv_rand_value", "0.00");
        String setting_tv_per_value = settings.getString("setting_tv_per_value", "0");


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi_pos);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);
        bt_close = dialog.findViewById(R.id.bt_close);
        btn_paper_load = dialog.findViewById(R.id.btn_paper_load);
        btn_Paper_ignore_time = dialog.findViewById(R.id.btn_Paper_ignore_time);

        btn_consume = findViewById(R.id.btn_consume);
        txt_sale_amount = findViewById(R.id.txt_sale_amount);
        txt_perc_tip = findViewById(R.id.txt_perc_tip);
        txt_rand_tip = findViewById(R.id.txt_rand_tip);
        txt_perc_disc = findViewById(R.id.txt_perc_disc);
        txt_rand_disc = findViewById(R.id.txt_rand_disc);
        txt_total = findViewById(R.id.txt_total);
        txt_notes = findViewById(R.id.txt_notes);
        TextView tv_clear_disc = findViewById(R.id.tv_clear_disc);


        txt_sale_amount.addTextChangedListener(new MoneyTextWatcher(txt_sale_amount));
        txt_perc_tip.addTextChangedListener(new MoneyTextWatcher(txt_perc_tip));
        txt_rand_tip.addTextChangedListener(new MoneyTextWatcher(txt_rand_tip));
        txt_perc_disc.addTextChangedListener(new MoneyTextWatcher(txt_perc_disc));
        txt_rand_disc.addTextChangedListener(new MoneyTextWatcher(txt_rand_disc));


        txt_total.setEnabled(false);

        et_amount_consume = findViewById(R.id.txt_total);
        //et_amount_consume=txt_sale_amount.toString();
/*
        String cslip="";



        cslip = "2Colby Co\n" +
             "2PTG999\n" +
             "1\n" +
             "1CUSTOMER RECEIPT\n" +
             "1\n" +
             "116/11/2021 16:31:19\n" +
             "1\n" +
             "2Approved:R0.01\n" +
             "1\n" +
             "1Tx. Date:16-11-2021 16:31\n" +
             "1PAN:\n" +
             "1UID:1512820211116163110\n" +
             "1Txn. No.:389154\n" +
             "1\n" +
             "1\n" +
             "1         Top it Up\n" +
             "1     0860 111 723\n" +
             "1Whatsapp 064 121 9970\n" +
             "1     www.itopitup.co.za\n" +
             "1";

        try {

            BufferedReader bufReader = new BufferedReader(new StringReader(cslip));
            String line = null;
            while ((line = bufReader.readLine()) != null) {

                String prnt_line = "";
                String size = "";

                if (line.length() > 1) {
                    prnt_line = "" + line.substring(1);
                    size = "" + line.substring(0, 1);
                }

                if (line.length() >= 8 && line.substring(0, 8).equals("BARCODE:")) {

                    if (Topitup.PRINT_BARCODE.equals("1")) {
                        html += line.replace("BARCODE:", "");
                    }

                } else {


                    if (size.equals("1")) {

                        html += prnt_line + "<br/>";

                    } else {

                        html += "<span style=\"font-size:1.4em\">";
                        html += prnt_line + "</span><br/>";

                    }

                }



            }

        } catch (Exception ex)
        {
            //
        }



        wb_slip.getSettings().setJavaScriptEnabled(false);
        wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

*/


        getunsettledtxns();


        btn_consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                //  EditText txtsaleamount = (EditText) findViewById(R.id.txtsaleamount);


                //  et_amount_consume=txtsaleamount;
                amount = (txt_total.getText().toString()).replace(".", "");
                et_note = findViewById(R.id.txt_notes);
                notes = et_note.getText().toString();
                if (amount.equals("000") || amount.equals("")) {
                    Toasty.error(mContext, "Sale Amount cannot be empty", 8000, true).show();

                    return;
                }
                double saleamount = Double.parseDouble((txt_sale_amount.getText().toString()));
                // Toasty.error(mContext, "Minimum Swipe"+Topitup.min_swipe, 8000, true).show();

                if (saleamount < Topitup.min_swipe) {
                    Toasty.error(mContext, "Minimum Sale Amount should be R " + Topitup.min_swipe, 8000, true).show();

                    return;
                }
                if (saleamount > Topitup.max_swipe) {
                    Toasty.error(mContext, "Maximum Sale Amount should be R " + Topitup.max_swipe, 8000, true).show();

                    return;
                }


                // If there are no rows, currentId is null, so the next id must be 1
                // If currentId is not null, increment it by 1


                if (Topitup.checkConnection(getApplicationContext())) {
                    getorderno();
                    // tv_result_consume.setText("");
                    params.put("third_trans_no", orderno);
                    params.put("order_amount", amount);
                    params.put("pay_type", InvokeConstant.PAY_TYPE_BANK_CARD);
                    params.put("note", notes);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
                    trans_time = dateformat.format(c.getTime());
                    putorderno();
                } else {

                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

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
                } else if (item.getItemId() == R.id.action_clear) {
                    // Action for clear
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);
                    return true;
                } else {
                    return true;
                }

/*
                switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;

                    case R.id.action_clear:


                        return true;
                    case R.id.action_logout:

                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent2);
                        return true;

                    default:
                        return true;
                }*/

            }

        });


    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay.rl_network.setVisibility(View.VISIBLE);
                    activity_adpay.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay.rl_network.setVisibility(View.INVISIBLE);
                    activity_adpay.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay.rl_network.setVisibility(View.INVISIBLE);
                    activity_adpay.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void itemClicked(View v) {
        //code to check if this checkbox is checked!

        if (chk_tips.isChecked()) {

            lo_tip.setVisibility(View.VISIBLE);

        } else {
            lo_tip.setVisibility(View.GONE);

        }
        if (chk_discount.isChecked()) {

            lo_disc.setVisibility(View.VISIBLE);

        } else {
            lo_disc.setVisibility(View.GONE);

        }

    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        FullscreenCall();
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos", "0");

        realm.beginTransaction();
        //realm.where(swipetxn.class).findAll().deleteAllFromRealm();
        realm.where(swipetxn.class).equalTo("is_txn", true).findAll().deleteAllFromRealm();
        realm.commitTransaction();


        if (dialog != null)
            dialog.dismiss();
        super.onResume();
    }

    public void onclick_btn_action(View v) {

        if (v.getId() == R.id.btn_consume) {
        }

    }

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

            bt_close.setVisibility(View.VISIBLE);
            btn_paper_load.setVisibility(View.GONE);

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
        FullscreenCall();
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

    @SuppressLint("ResourceAsColor")
    private void do_show_dlg_conv() {


        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_pos_calc);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();


        String setting_tv_rand_value = settings.getString("setting_tv_rand_value", "0.00");
        String setting_tv_per_value = settings.getString("setting_tv_per_value", "0");
        Button btn_close = dialog.findViewById(R.id.btn_close);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        final TextView tv_rand_value = dialog.findViewById(R.id.tv_rand_value);
        final TextView tv_per_value = dialog.findViewById(R.id.tv_per_value);

        tv_rand_value.setText(setting_tv_rand_value);
        tv_per_value.setText(setting_tv_per_value);

        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();
                FullscreenCall();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editor.putString("setting_tv_rand_value", tv_rand_value.getText().toString());
                editor.putString("setting_tv_per_value", tv_per_value.getText().toString());

                editor.commit();

                dialog.dismiss();
                FullscreenCall();
            }
        });


        dialog.show();


    }

    private void invokeconsume() {

        amountd = txt_total.getText().toString();
        CashierHelper.consume(mContext, this.params, new CashierHelper.PayCallBack() {
            public void success(@Nullable String data) {
                //Log.d(activity_adpay.this.TAG, "消费成功 success :" + data);
                // Toasty.success(mContext, "Payment Approved!!!", Toast.LENGTH_LONG).show();
                // tv_result_consume.setText(data);
                //Toasty.success(mContext, data, 8000, true).show();
                if (Topitup.checkConnection(getApplicationContext())) {
                    calladapyresponse(data);
                }
                Realm realm = Realm.getDefaultInstance();


                /*Number maxId = realm.where(swipetxn.class).max("swipe_id");

                nextId = (maxId == null) ? 1 : maxId.intValue() + 1;


                if(nextId>totcnt){
                    realm.beginTransaction();
                    realm.where(swipetxn.class).findAll().deleteAllFromRealm();
                    realm.commitTransaction();
                    nextId=1;
                }*/


                if (enable_realtime_swipe.equals("1")) {
                    swipetxn stxn = new swipetxn();
                    //  stxn.setSwipe_id(nextId);
                    stxn.setOrderno(orderno);
                    stxn.setCode("2");
                    stxn.setIs_txn(false);
                    stxn.setAmount(txt_total.getText().toString());
                    stxn.setNotes("");
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(stxn);
                    realm.commitTransaction();


                    retresponse = data;


                    realm.beginTransaction();
                    postxn = realm.where(swipetxn.class).equalTo("orderno", orderno).findFirst();

                    postxn.response = data;

                    JSONObject json = null;    // create JSON obj from string
                    try {
                        json = new JSONObject(data);

                        String msg = json.getString("msg");
                        code = json.getString("code");// this will return correct
                        String datas = json.getString("data");


                        postxn.setCode(code);
                        postxn.setIs_txn(false);


                        if (code.equals("0")) {
                            JSONObject json1 = new JSONObject(datas);
                            int trans_status;
                            trans_status = json1.getInt("trans_status");

                            postxn.setTrans_status(trans_status);
                            //trans_status=0;// for testing
                            if (trans_status == 1) {

                                String bank = json1.getString("bank");

                                JSONObject json2 = new JSONObject(bank);

                                card_no = json2.getString("card_no");
                                voucher_no = json2.getString("voucher_no");
                                batch_no = json2.getString("batch_no");
                                refer_no = json2.getString("refer_no");
                                trans_time = json2.getString("trans_time");
                      /*  Topitup.card_no=card_no;
                        Topitup.voucher_no=voucher_no;
                        Topitup.batch_no=batch_no;
                        Topitup.refer_no=refer_no;
                        Topitup.trans_time=trans_time;
                        Topitup.amount=amount;
                        Topitup.orderno=orderno;
                        Topitup.enable_realtime_swipe="1";*/
                                //Toasty.success(mContext, "Calling API", 8000, true).show();
                                if (enable_realtime_swipe.equals("1")) {
                                    postxn.batch_no = batch_no;
                                    postxn.card_no = card_no;
                                    postxn.voucher_no = voucher_no;
                                    postxn.refer_no = refer_no;
                                    postxn.trans_time = trans_time;
                                    postxn.is_txn = false;
                                    postxn.tip = tips;
                                    postxn.discount = discount;


                                    //  calladapyAPI(card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved",txt_notes.getText().toString(),tips,discount);
                                    calladapyAPI();

                                }
                            } else if (trans_status == 0) {//trading


                                cntdwnTimer = new CountDownTimer(60000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        isRunning = true;
                                        NumberFormat f = new DecimalFormat("00");

                                        long hour = (millisUntilFinished / 3600000) % 24;

                                        long min = (millisUntilFinished / 60000) % 60;

                                        long sec = (millisUntilFinished / 1000) % 60;

//txtload.setText("   Please Wait....Fetching Receipt in \n                        "+f.format(hour) + ":" + f.format(min) + ":" + f.format(sec)+" secs");
                                        // if (Topitup.getInstance().checkConnection(getApplicationContext())) {


                                        if (sec % 5 == 0)
                                            addpayquery();
//                                    } else {
//
//                                    }
                                        // txtload.setText("Internet Data Issue, please contact Top it Up.");
                                        // counter++;

                                    }

                                    public void onFinish() {
                                        isRunning = false;

                                    }
                                }.start();


                            } else {


                            }
                            // Intent myIntent2 = new Intent(mContext, activity_main.class);
                            //  startActivity(myIntent2);


                        } else {


                        }
                        realm.insertOrUpdate(postxn);
                        realm.commitTransaction();
                        //  Toasty.error(mContext, trans_status, 8000, true).show();


                    } catch (JSONException e) {
                        Toasty.error(mContext, "JSON ERROR", 8000, true).show();
                    }
                }//end of realtime check
                else {
                    bankslip();
                    onBackPressed();
                }

            }

            public void failed(@Nullable String errInfo) {
                Log.d(activity_adpay.this.TAG, "消费失败 failed :" + errInfo);
                String msg = "Declined";
                if (Topitup.checkConnection(getApplicationContext())) {
                    calladapyresponse(errInfo);
                }
                updateordernosts("2");
                //   Toast.makeText(activity_adpay.this, getString(R.string.consume_fail), Toast.LENGTH_LONG).show();
                // tv_result_consume.setText(errInfo);
                // Printer.print_data(errInfo);
                JSONObject json1 = null;
                try {
                    json1 = new JSONObject(errInfo);
                    msg = json1.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                String currentDateandTime = sdf.format(new Date());
                ret_copy += "2" + company_name + "\n";
                ret_copy += "1\n";
                // ret_copy += "1***********************\n";
                ret_copy += "1MERCHANT RECEIPT\n";
                ret_copy += "1\n";
                // ret_copy += "1***********************\n";
                ret_copy += "1" + currentDateandTime + "\n";
                ret_copy += "1\n";

                ret_copy += "1" + msg + " : R " + amountd + "\n";
                ret_copy += "1UID:" + orderno + "\n";

                //  ret_copy += "1" + msg + "\n";
                // ret_copy += "1"+msg+": "+amount+"\n";
                ret_copy += "1\n";
                ret_copy += "1\n";
                ret_copy += "1         Top it Up | 0860 111 723\n";
                ret_copy += "1Whatsapp | 064 121 9970\n";
                ret_copy += "1After Hours 23h00-07h00\n";
                ret_copy += "1021 300 0121\n";
                ret_copy += "1     www.itopitup.co.za\n";
                ret_copy += "1";


                if (enable_realtime_swipe.equals("1") && setting_slip_cancel.equals("1")) {
                    PrinterTopitup.print_data(ret_copy);
                }
                onBackPressed();

            }
        });

    }

/*
private void invokeconsume(){


        CashierHelper.consume(mContext, this.params, (CashierHelper.PayCallBack)(new CashierHelper.PayCallBack() {
            public void success(@Nullable String data) {
                Log.d(activity_adpay.this.TAG, "消费成功 success :" + data);
                Toast.makeText(activity_adpay.this, getString(R.string.consume_success),Toast.LENGTH_LONG).show();
                tv_result_consume.setText(data);
                et_note.setText(data);
        }

            public void failed(@Nullable String errInfo) {
                Log.d(activity_adpay.this.TAG, "消费失败 failed :" + errInfo);
                Toast.makeText(activity_adpay.this, getString(R.string.consume_success),Toast.LENGTH_LONG).show();
                tv_result_consume.setText(errInfo);
                et_note.setText(errInfo);
            }
        }));


    }*/

    private void updateordernosts(String recon) {

        final Call<ResponseBody> call = apiService.updateorder(Topitup.TIU_LICENSE, orderno, recon);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.code() == 500) {
                    // logresponse();

                    //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
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


                }

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


    }

    private void putorderno() {
        if (Topitup.checkConnection(getApplicationContext())) {
            //   Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


            final Call<ResponseBody> call = apiService.setorderno(Topitup.TIU_LICENSE, orderno);

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
                            JSONObject reader = new JSONObject(res);
                            String html = "";
                            if (reader.getString("upid").equals("1")) {

                                invokeconsume();
                                ordersuccess = true;
                            } else if (reader.getString("upid").equals("0")) {

                                ordersuccess = false;
                                Toasty.error(mContext, "Problem with Getting Order No., please contact Top it Up.", 8000, true).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

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
        } else {

            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


        }

    }

    private void getorderno() {
        /*
        1->Real Time Settlement
        2->Daily Settlement
        3->Bank Settlement
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf1.format(new Date());

        orderno = enable_realtime_swipe + Topitup.POSUSER_ID + currentDateandTime;

    }

    public void calladapyAPI() {

        final Call<ResponseBody> call = apiService.adpay_txn(Topitup.TIU_LICENSE, orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved", notes, tips, discount);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.code() == 500) {
                    // logresponse();
                    offlineslip();
                    //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
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
                        JSONObject reader = new JSONObject(res);
                        String html = "";
                        if (reader.getString("pid").equals("0")) {
                            if (reader.getString("settled").equals("1")) {
                                Realm realm = Realm.getDefaultInstance();


                                realm.beginTransaction();
//                            postxn=realm.where(swipetxn.class).equalTo("orderno",orderno).findFirst();
//                         postxn.setIs_txn(true);
//                             realm.insertOrUpdate(postxn);
                                realm.where(swipetxn.class).equalTo("orderno", orderno).findAll().deleteAllFromRealm();
                                realm.commitTransaction();
                            }

                        } else {
                            //postxn=realm.where(swipetxn.class).equalTo("orderno",orderno).findFirst();


                            realm.beginTransaction();
                            postxn.setIs_txn(true);
                            realm.insertOrUpdate(postxn);

                            realm.where(swipetxn.class).equalTo("orderno", orderno).findAll().deleteAllFromRealm();


                            realm.commitTransaction();
                            cslip = reader.getString("print_data");
                            mslip = reader.getString("print_data1");
                            // Intent myIntent2 = new Intent(mContext, activity_main.class);
                            // startActivity(myIntent2);
                            printlip();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // logresponse();
                offlineslip();
                //Toasty.error(mContext, t.getMessage(), 5000, true).show();
                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });


    }

    public void calladapyAPIBackSettle() {

        final Call<ResponseBody> call = apiService.adpay_txn(Topitup.TIU_LICENSE, orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved", notes, tips, discount);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 500) {

                    //Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
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
                        JSONObject reader = new JSONObject(res);
                        String html = "";
                        if (reader.getString("pid").equals("0")) {

                            if (reader.getString("settled").equals("1")) {
                                Realm realm = Realm.getDefaultInstance();
                                updateordernosts("1");
                                ordersuccess = true;
                                realm.beginTransaction();
//                            postxn=realm.where(swipetxn.class).equalTo("orderno",orderno).findFirst();
//                         postxn.setIs_txn(true);
//                             realm.insertOrUpdate(postxn);
                                realm.where(swipetxn.class).equalTo("orderno", orderno).findAll().deleteAllFromRealm();
                                realm.commitTransaction();
                            }


                        } else {

                            Realm realm = Realm.getDefaultInstance();
                            ordersuccess = true;

                            realm.beginTransaction();
//                            postxn=realm.where(swipetxn.class).equalTo("orderno",orderno).findFirst();
//                         postxn.setIs_txn(true);
//                             realm.insertOrUpdate(postxn);
                            realm.where(swipetxn.class).equalTo("orderno", orderno).findAll().deleteAllFromRealm();
                            realm.commitTransaction();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();
                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });


    }

    public void calladapyresponse(String response) {

        final Call<ResponseBody> call = apiService.adpay_txn_response(Topitup.TIU_LICENSE, orderno, response);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 500) {
//logresponse();
                    // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    //  Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
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


                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //logresponse();
//                Toasty.error(mContext, t.getMessage(), 5000, true).show();
                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });


    }

    public void setText(View v) {
        // String newText = txt_sale_amount.getText().toString();
        // textViewResult.setText(newText);

        closeKeyboard();
        txt_sale_amount.setText("");
    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    private void selectTextViewToAppend(String number) {

        if (txt_sale_amount.getText().toString().contains(".")) {
            MAX_CHARACTERS++;
        }
        if (txt_sale_amount.getText().length() < MAX_CHARACTERS) {
            txt_sale_amount.append(number);
            MAX_CHARACTERS = 10;
        } else {
            MAX_CHARACTERS = 10;
            Toasty.error(this, "Cannot have more than 10 numbers", 8000).show();
        }


    }
/*
    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;
        private final Locale locale;

        public MoneyTextWatcher(EditText editText, Locale locale) {
            this.editTextWeakReference = new WeakReference<EditText>(editText);
            this.locale = locale != null ? locale : Locale.getDefault();
        }

        public MoneyTextWatcher(EditText editText) {
            this.editTextWeakReference = new WeakReference<EditText>(editText);
            this.locale = Locale.getDefault();
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
            editText.removeTextChangedListener(this);

            BigDecimal parsed = parseToBigDecimal(editable.toString(), locale);
            String formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);
          //  NumberFormat.getNumberInstance(locale).format(parsed); // sem o simbolo de moeda

            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }

        private BigDecimal parseToBigDecimal(String value, Locale locale) {
            String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(locale).getCurrency().getSymbol());
            String cleanString = value.replaceAll(replaceable, "");
            cleanString = cleanString.replaceAll(",", ".");
            return new BigDecimal(cleanString).setScale(
                    2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR
            );
        }
    }*/

    public void onNumClick(View view) {


        int id = view.getId();

        if (id == R.id.btn_one) {
            selectTextViewToAppend("1");
        } else if (id == R.id.btn_two) {
            selectTextViewToAppend("2");
        } else if (id == R.id.btn_three) {
            selectTextViewToAppend("3");
        } else if (id == R.id.btn_four) {
            selectTextViewToAppend("4");
        } else if (id == R.id.btn_five) {
            selectTextViewToAppend("5");
        } else if (id == R.id.btn_six) {
            selectTextViewToAppend("6");
        } else if (id == R.id.btn_seven) {
            selectTextViewToAppend("7");
        } else if (id == R.id.btn_eight) {
            selectTextViewToAppend("8");
        } else if (id == R.id.btn_nine) {
            selectTextViewToAppend("9");
        } else if (id == R.id.btn_zero) {
            selectTextViewToAppend("0");
        } else if (id == R.id.btn_point) {
            selectTextViewToAppend(".");
        } else if (id == R.id.btn_clear) {
            selectTextViewToAppend(".");
        } else if (id == R.id.btn_merchant) {
            PrinterTopitup.print_data(mslip);
        } else if (id == R.id.btn_customer) {
            PrinterTopitup.print_data(cslip);
        } else if (id == R.id.btn_merchant_customer) {
            printslip();
        } else if (id == R.id.tv_clear_sale_amount) {
            txt_sale_amount.setText("");
            txt_total.setText("0.00");
        } else if (id == R.id.tv_clear_tip) {
            txt_perc_tip.setText("");
            txt_rand_tip.setText("");
        } else if (id == R.id.tv_clear_disc) {
            txt_rand_disc.setText("");
            txt_perc_disc.setText("");
        } else if (id == R.id.btn_cancel) {
            onBackPressed();
        } else {
            // Default case
        }

    }

    private void printslip() {

        PrinterTopitup.print_data(cslip);


        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PrinterTopitup.print_data(mslip);

    }

    private void calculate() {
        double per_tip = 0.00;
        double rand_tip = 0.00;
        double rand_disc = 0.00;
        double perc_disc = 0.00;
        double saleamount = 0.00;
        double totalamount = 0.00;
        Double tipamount1 = 0.00, tipamount2 = 0.00, disamount1 = 0.00, disamount2 = 0.00;
        if (txt_sale_amount.getText().length() > 3)
            saleamount = Double.parseDouble((txt_sale_amount.getText().toString()));
        if (txt_perc_tip.getText().length() > 1)
            per_tip = Double.parseDouble((txt_perc_tip.getText().toString()));
        if (txt_rand_tip.getText().length() > 2)
            rand_tip = Double.parseDouble((txt_rand_tip.getText().toString()));
        if (txt_rand_disc.getText().length() > 1)
            rand_disc = Double.parseDouble((txt_rand_disc.getText().toString()));
        if (txt_perc_disc.getText().length() > 2)
            perc_disc = Double.parseDouble((txt_perc_disc.getText().toString()));

        if (per_tip > 0) {
            tipamount1 = (saleamount * per_tip) / 100;

        }
        if (rand_tip > 0) {
            tipamount2 = rand_tip;
        }

        if (perc_disc > 0) {

            disamount1 = (saleamount * perc_disc) / 100;
        }
        if (rand_disc > 0) {

            disamount2 = rand_disc;
        }
        tips = String.format("%.2f", (tipamount1 + tipamount2)).replace(",", ".");
        discount = String.format("%.2f", (disamount1 + disamount2)).replace(",", ".");
        totalamount = saleamount + (tipamount1 + tipamount2) - (disamount1 + disamount2);
        String totalsale = String.format("%.2f", totalamount);
        txt_total.setText(totalsale.replace(",", "."));


    }

    public void offlineslip() {

        cslip = "2" + company_name + "\n" +
                "2" + account_number + "\n" +
                "1             (offline slip)\n" +
                "1\n" +
                "1CUSTOMER RECEIPT\n" +
                "1\n" +
                "1" + trans_time + "\n" +
                "1\n" +
                "2Approved:R" + amountd + "\n" +
                "1\n" +
                "1Tips :R" + tips + "\n" +
                "1\n" +
                "1Discount:R" + discount + "\n" +
                "1\n" +
                "1Tx. Date:" + trans_time + "\n" +
                "1PAN:" + card_no + "\n" +
                "1UID:" + orderno + "\n" +
                "1\n" +
                "1\n" +
                "1         Top it Up | 0860 111 723\n" +
                "1Whatsapp | 064 121 9970\n" +
                "1After Hours 23h00-07h00\n" +
                "1021 300 0121\n" +
                "1     www.itopitup.co.za\n" +
                "1";
        mslip = "2" + company_name + "\n" +
                "2" + account_number + "\n" +
                "1             (offline slip)\n" +
                "1\n" +
                "1MERCHANT RECEIPT\n" +
                "1\n" +
                "1" + trans_time + "\n" +
                "1\n" +
                "2Approved:R" + amountd + "\n" +
                "1\n" +
                "1Tips :R" + tips + "\n" +
                "1\n" +
                "1Discount:R" + discount + "\n" +
                "1\n" +
                "1Tx. Date: " + trans_time + "\n" +
                "1PAN:" + card_no + "\n" +
                "1UID:" + orderno + "\n" +
                "1\n" +
                "1\n" +
                "1         Top it Up | 0860 111 723\n" +
                "1Whatsapp | 064 121 9970\n" +
                "1After Hours 23h00-07h00\n" +
                "1021 300 0121\n" +
                "1     www.itopitup.co.za\n" +
                "1";
        printlip();
    }

    public void bankslip() {

        cslip = "2" + company_name + "\n" +
                "2" + account_number + "\n" +
                "1             POS Slip\n" +
                "1\n" +
                "1CUSTOMER RECEIPT\n" +
                "1\n" +
                "1" + trans_time + "\n" +
                "1\n" +
                "2Approved:R" + amountd + "\n" +
                "1\n" +
                "1Tips :R" + tips + "\n" +
                "1\n" +
                "1Discount:R" + discount + "\n" +
                "1\n" +
                "1Tx. Date:15-12-2021 16:15\n" +
                "1PAN:" + card_no + "\n" +
                "1UID:" + orderno + "\n" +
                "1\n" +
                "1\n" +
                "1         Top it Up | 0860 111 723\n" +
                "1Whatsapp | 064 121 9970\n" +
                "1After Hours 23h00-07h00\n" +
                "1021 300 0121\n" +
                "1     www.itopitup.co.za\n" +
                "1";
        mslip = "2" + company_name + "\n" +
                "2" + account_number + "\n" +
                "1             POS Slip\n" +
                "1\n" +
                "1MERCHANT RECEIPT\n" +
                "1\n" +
                "1" + trans_time + "\n" +
                "1\n" +
                "2Approved:R" + amountd + "\n" +
                "1\n" +
                "1Tips :R" + tips + "\n" +
                "1\n" +
                "1Discount:R" + discount + "\n" +
                "1\n" +
                "1Tx. Date:15-12-2021 16:15\n" +
                "1PAN:" + card_no + "\n" +
                "1UID:" + orderno + "\n" +
                "1\n" +
                "1\n" +
                "1         Top it Up | 0860 111 723\n" +
                "1Whatsapp | 064 121 9970\n" +
                "1After Hours 23h00-07h00\n" +
                "1021 300 0121\n" +
                "1     www.itopitup.co.za\n" +
                "1";
        if (MPOSTYPE == 1)
            PrinterTopitup.print_data(cslip);
        else if (MPOSTYPE == 2)
            PrinterTopitup.print_data(mslip);
        else
            printslip();
    }

    public void printlip() {

        reltxnscreen.setVisibility(View.GONE);
        relpayscreen.setVisibility(View.VISIBLE);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {

            BufferedReader bufReader = new BufferedReader(new StringReader(cslip));
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

                        html += "<span style=\"font-size:1.4em\">";
                        html += prnt_line + "</span><br/>";

                    }

                }


            }

        } catch (Exception ex) {
            //
        }


        wb_slip.getSettings().setJavaScriptEnabled(false);
        wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        // Printer.print_data("1Success");
        if (setting_chk_auto_mpos.equals("1")) {


            if (MPOSTYPE == 1)
                PrinterTopitup.print_data(cslip);
            else if (MPOSTYPE == 2)
                PrinterTopitup.print_data(mslip);
            else
                printslip();

        }

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void addpayquery() {
        CashierHelper.query(mContext, this.params, new CashierHelper.PayCallBack() {
            public void success(@Nullable String data) {
                Log.d(activity_adpay.this.TAG, "addpay query success :" + data);

                int trans_status = 0;


                if (Topitup.checkConnection(getApplicationContext())) {
                    calladapyresponse(data);
                }
                realm.beginTransaction();
                postxn = realm.where(swipetxn.class).equalTo("orderno", orderno).findFirst();

                JSONObject authorJsonObj = null;
                try {
                    authorJsonObj = new JSONObject(data);
                    code = "" + authorJsonObj.get("code");

                    String msg = (String) authorJsonObj.get("msg");
                    JSONObject dataJsonObj = (JSONObject) authorJsonObj.get("data");//440
                    String total_num = "" + dataJsonObj.get("total_num");

                    JSONArray jsonArray = dataJsonObj.getJSONArray("list");

                    JSONObject listJsonObj = new JSONObject(jsonArray.get(0).toString());//440

                    trans_status = listJsonObj.getInt("trans_status");
                    String channel_discount_amount = "" + listJsonObj.get("channel_discount_amount");
                    String note = (String) listJsonObj.get("note");
                    String order_amount = "" + listJsonObj.get("order_amount");
                    String pay_type = "" + listJsonObj.get("pay_type");
                    String third_trans_no = "" + listJsonObj.get("third_trans_no");

                    String trans_type = "" + listJsonObj.get("trans_type");
                    JSONObject bankJsonObj = (JSONObject) listJsonObj.get("bank");//440
                    batch_no = "" + bankJsonObj.get("batch_no");
                    card_no = (String) bankJsonObj.get("card_no");
                    refer_no = "" + bankJsonObj.get("refer_no");
                    trans_time = (String) bankJsonObj.get("trans_time");
                    voucher_no = "" + bankJsonObj.get("voucher_no");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (code.equals("0")) {


                    if (trans_status == 1) {
                        cntdwnTimer.cancel();


                        postxn.batch_no = batch_no;
                        postxn.card_no = card_no;
                        postxn.voucher_no = voucher_no;
                        postxn.refer_no = refer_no;
                        postxn.trans_time = trans_time;
                        postxn.is_txn = false;
                        postxn.tip = tips;
                        postxn.discount = discount;
                        calladapyAPI();


                    } else if (trans_status == 0) {//trading


                    } else {
                        cntdwnTimer.cancel();

                    }


                } else {

                    updateordernosts("2");
                }
                realm.insertOrUpdate(postxn);
                realm.commitTransaction();
                //  Toasty.error(mContext, trans_status, 8000, true).show();


            }

            public void failed(@Nullable String errInfo) {
                // updateordernosts("2");
                Log.d(activity_adpay.this.TAG, "消费失败 failed :" + errInfo);

            }
        });


    }

    private void logresponse() {
        Realm realm = Realm.getDefaultInstance();

        swipetxn postxnb = realm.where(swipetxn.class).equalTo("orderno", orderno).findFirst();
        realm.beginTransaction();
        postxnb.response = retresponse;
        realm.insertOrUpdate(postxnb);
        realm.commitTransaction();

    }

    private boolean addpayqueryrecon() {

        //Log.d("Called","Called");

        CashierHelper.query(mContext, this.params, new CashierHelper.PayCallBack() {
            public void success(@Nullable String data) {
                Log.d(activity_adpay.this.TAG, "addpay query success :" + data);

                int trans_status = 0;


                if (Topitup.checkConnection(getApplicationContext())) {
                    calladapyresponse(data);
                }


                JSONObject authorJsonObj = null;
                try {
                    authorJsonObj = new JSONObject(data);
                    code = "" + authorJsonObj.get("code");
                    apiCall = true;
                    String msg = (String) authorJsonObj.get("msg");
                    JSONObject dataJsonObj = (JSONObject) authorJsonObj.get("data");//440
                    String total_num = "" + dataJsonObj.get("total_num");

                    JSONArray jsonArray = dataJsonObj.getJSONArray("list");

                    JSONObject listJsonObj = new JSONObject(jsonArray.get(0).toString());//440

                    trans_status = listJsonObj.getInt("trans_status");
                    discount = "" + listJsonObj.get("channel_discount_amount");
                    notes = (String) listJsonObj.get("note");
                    String order_amount = "" + listJsonObj.get("order_amount");
                    double str1 = Double.parseDouble(order_amount);
                    //  amount=String.valueOf(str1/100);
                    amount = order_amount;
                    String pay_type = "" + listJsonObj.get("pay_type");
                    String third_trans_no = "" + listJsonObj.get("third_trans_no");

                    String trans_type = "" + listJsonObj.get("trans_type");
                    JSONObject bankJsonObj = (JSONObject) listJsonObj.get("bank");//440
                    batch_no = "" + bankJsonObj.get("batch_no");
                    card_no = (String) bankJsonObj.get("card_no");
                    refer_no = "" + bankJsonObj.get("refer_no");
                    trans_time = (String) bankJsonObj.get("trans_time");
                    voucher_no = "" + bankJsonObj.get("voucher_no");

                    //  apiService.adpay_txn(Topitup.TIU_LICENSE, orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved",notes,tips,discount);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (code.equals("0")) {


                    if (trans_status == 1) {


                        calladapyAPIBackSettle();


                    } else if (trans_status == 0) {//trading


                    } else {


                    }


                } else {
                    Log.d("updateordernosts", orderno);
                    updateordernosts("2");
                }


            }

            public void failed(@Nullable String errInfo) {
                apiCall = true;
                if (Topitup.checkConnection(getApplicationContext())) {
                    calladapyresponse(errInfo);
                }
                updateordernosts("3");
                Log.d(activity_adpay.this.TAG, "消费失败 failed :" + errInfo);

            }
        });

        return apiCall;

    }

    public void getunsettledtxns() {
        if (Topitup.checkConnection(getApplicationContext())) {
            //   Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


            final Call<ResponseBody> call = apiService.getunsettledorderno(Topitup.TIU_LICENSE);

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
                            JSONObject reader = new JSONObject(res);
                            //  Toasty.info(mContext, reader.getString("cnt"), 8000, true).show();
                            if (reader.getString("cnt").equals("0")) {

                            } else {
                                String ordernos = reader.getString("ordernos");
                                //  Toasty.info(mContext, ordernos, 8000, true).show();

                                String[] parts = ordernos.split(",");
                                // Toasty.info(mContext, "part len="+parts.length, 8000, true).show();
                                if (parts.length > 1) {
                                    for (int j = 0; j < parts.length; j++) {
                                        // Toasty.info(mContext, parts[j], 8000, true).show();

                                        params.put("third_trans_no", parts[j]);
                                        orderno = parts[j];


                                        if (addpayqueryrecon()) {

                                            Log.d("orderno=", orderno);
                                        } else {


                                        }


                                    }
                                } else {
                                    params.put("third_trans_no", ordernos);
                                    orderno = ordernos;
                                    addpayqueryrecon();
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

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
        } else {

            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


        }

    }

    public class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // closeKeyboard();
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
            if (s.equals(".")) return;
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[R,.]", "");

            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, RoundingMode.FLOOR).divide(new BigDecimal(100), RoundingMode.FLOOR);

            //final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
            //String formatted = nf.getCurrencyInstance().format(parsed);
            editText.setText(parsed.toString());
            calculate();
            editText.setSelection(parsed.toString().length());
            editText.addTextChangedListener(this);
        }
    }

    public class MoneyTextWatcher1 implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher1(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // closeKeyboard();
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
            String cleanString = s.replaceAll("[,.]", "");
            BigDecimal parsed = new BigDecimal(cleanString);

            //final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
            //String formatted = nf.getCurrencyInstance().format(parsed);
            editText.setText("% " + parsed);
            // calculate();
            editText.setSelection(parsed.toString().length());
            editText.addTextChangedListener(this);
        }
    }


}