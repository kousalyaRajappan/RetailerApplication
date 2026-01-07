package com.za.toptitup.loginlibrary;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.swipetxn;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_adpay_new extends AppCompatActivity {


    Realm realm;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<fin_balance> tiu_fin_balance;
    private final String TAG = "invoke--ConsumeActivity";
    private Context mContext;
    private final HashMap params = new HashMap();
    EditText et_amount_consume, et_note;
    String asset_serial = "NA";
    String merchant_no = "NA";
    String terminal_no = "NA";

    EditText txt_account_number,amntEditText, amntEditText_cent;
    EditText txt_sale_amount, txt_perc_tip, txt_rand_tip, txt_perc_disc, txt_rand_disc, txt_total, txt_notes;
    TextView tv_result_consume,txt_account_number_;
    Button btn_consume;
    String ret_copy = "", setting_chk_auto_mpos, cslip, mslip, enable_realtime_swipe, setting_slip_cancel;
    String orderno, transorderno, refNo, originTransDate, amount, tips = "0.00", discount = "0.00";
    RelativeLayout reltxnscreen, relpayscreen, lo_tip, lo_disc, lo_total;
    WebView wb_slip;
    String html = "";
    int MPOSTYPE;
    swipetxn postxn;
    String postxnresponse;
    private static int MAX_CHARACTERS = 10;
    int nextId;
    String company_name, account_number;
    String card_no = "000000XXXXXXXX00";
    String voucher_no = "";
    String batch_no = "";
    String refer_no = "";
    String trans_time = "";
    String amountd;
    String notes, is_admin;
    String expDate = "00/00";
    String paymentMethod = "";
    String code;
    String retresponse;
    CountDownTimer cntdwnTimer;
    Boolean isRunning;
    int totcnt = 50;
    boolean ordersuccess = false;
    boolean txnsuccess = false;
    boolean callback = false;
    boolean apiCall = false;

    private String retailerPin= "123456";
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_to_1;
    CheckBox chk_discount, chk_tips;
    BottomNavigationView mBottomNav;
    SharedPreferences settings;
    TableLayout tab_layout;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;
    public static activity_adpay_new instance;
    private TextView txt_rand;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    EditText dialogEditText_cent, dialogEditText;
    Button btn_clearr, btn_merchant, btn_customer, btn_merchant_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        instance = this;
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FullscreenCall();

        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy @ HH:mm");
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);

        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos", "0");

        enable_realtime_swipe = settings.getString("enable_realtime_swipe", "0");
        asset_serial = settings.getString("asset_serial", "0");
        merchant_no = settings.getString("merchant_no", "0");
        terminal_no = settings.getString("terminal_no", "0");
        btn_merchant = findViewById(R.id.btn_merchant);
        btn_merchant_customer = findViewById(R.id.btn_merchant_customer);
        btn_customer = findViewById(R.id.btn_customer);
        txt_rand = findViewById(R.id.txt_rand);
        btn_clearr = findViewById(R.id.btn_clearr);
        dialogEditText = findViewById(R.id.dialogEditText);
        dialogEditText_cent = findViewById(R.id.dialogEditText_cent);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);
        txt_account_number_ = findViewById(R.id.txt_account_number_);
        txt_account_number = findViewById(R.id.txt_account_number);
        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);

        cent_value_entered = "";
        rand_value_entered = "";

        String receivedValue = getIntent().getStringExtra("TOTAL_CARD_VALUE");
        if (receivedValue != null) {
            if (!receivedValue.isEmpty()) {
                if (!receivedValue.toString().contentEquals(".")) {
                    String totalVal = receivedValue.replace("R", "").replace(",", "");
                    String[] parts = totalVal.split("\\.");
                    int wholePart = Integer.parseInt(parts[0]);
                    int decimalPart = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;
                    dialogEditText.setText(String.valueOf(wholePart));
                    dialogEditText_cent.setText(String.valueOf(decimalPart));
                    dialogEditText.clearFocus();
                    dialogEditText_cent.clearFocus();
                } else {
                    dialogEditText.setText(receivedValue);
                    dialogEditText_cent.setText("");
                    dialogEditText.clearFocus();
                    dialogEditText_cent.clearFocus();
                }
            }
        } else {
            dialogEditText.addTextChangedListener(new MoneyTextWatcherRand(dialogEditText));
            dialogEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(dialogEditText_cent));
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(dialogEditText, InputMethodManager.SHOW_IMPLICIT);
            dialogEditText.requestFocus();
        }

        setting_slip_cancel = settings.getString("setting_slip_cancel", "0");
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);
//        tab_layout =  findViewById(R.id.tab_layout);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        Log.e("enable real time", ".....card anable...." + enable_realtime_swipe);
        if (enable_realtime_swipe.equals("6")) {
            showDialogAlert();
        }

        activity_login.fromScreen = "activity_adpay_new";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        MPOSTYPE = settings.getInt("MPOSTYPE", 0);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        account_number = tiu_settings.account_number;
        company_name = tiu_settings.company_name;
        tiu_title_outlet.setText(tiu_settings.account_number);


        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        /*
        if (tiu_settings.company_name.length() > 10) {
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

                tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                tiu_user_name_.setText("Cashier");

            }
        }


        // tv_result_consume = (TextView) findViewById(R.id.tv_response);
        reltxnscreen = findViewById(R.id.reltxnscreen);
        relpayscreen = findViewById(R.id.relpayscreen);
        lo_tip = findViewById(R.id.lo_tip);
        lo_disc = findViewById(R.id.lo_disc);
        lo_total = findViewById(R.id.lo_total);

        chk_tips = findViewById(R.id.chk_tips);
        chk_discount = findViewById(R.id.chk_discount);
        btn_clearr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditText.setText("");
                dialogEditText_cent.setText("");
                btn_consume.setEnabled(true);
                btn_consume.setVisibility(VISIBLE);
            }
        });
        getorderno();
        TextView txt_orderno = this.findViewById(R.id.txt_orderno);
        if (orderno.length() == 20) {
            String s1 = orderno.substring(0, 4);
            String s2 = orderno.substring(4, 8);
            String s3 = orderno.substring(8, 12);
            String s4 = orderno.substring(12, 16);
            String s5 = orderno.substring(16, 20);
            String dashedorderno = s1 + "-" + s2 + "-" + s3 + "-" + s4 + "-" + s5;

            txt_orderno.setText(dashedorderno);
        } else {
            txt_orderno.setText(orderno);
        }

        wb_slip = this.findViewById(R.id.wb_slip);
        String setting_tv_rand_value = settings.getString("setting_tv_rand_value", "0.00");
        String setting_tv_per_value = settings.getString("setting_tv_per_value", "0");
        update_balance();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("buisnessorderno", "");
        editor.commit();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // beforeg
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
//        txt_rand_tip.setFocusable(false);
//        txt_rand_disc.setFocusable(false);

        et_amount_consume = findViewById(R.id.txt_total);

        txt_rand_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsonClick(view);
            }
        });

        txt_rand_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discountonCLick(view);
            }
        });

      /*  txt_account_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.e("account number ","length"+s.length());
                if (s.length() == 6) {  // when input complete
                    amntEditText.requestFocus();
                    // show your custom keyboard for dialogEditText
//                    customKeyboard.showForEditText(amntEditText);
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });*/

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


        btn_consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                //  EditText txtsaleamount = (EditText) findViewById(R.id.txtsaleamount);

               /* amount = (txt_total.getText().toString()).replace(".", "");
                amountd = txt_total.getText().toString();
                et_note = findViewById(R.id.txt_notes);
                notes = et_note.getText().toString();
                if (amount.equals("000") || amount.equals("")) {
                    Toasty.error(mContext, "Sale Amount cannot be empty", 8000, true).show();

                    return;
                }
                double saleamount = Double.parseDouble((txt_sale_amount.getText().toString()));*/
                if (dialogEditText.getText().equals("000") || dialogEditText.getText().equals("")) {
                    Toasty.error(mContext, "Sale Amount cannot be empty", 8000, true).show();
                    return;
                }
                String amountEnter;

                String cent = "";
                if (dialogEditText_cent.length() > 0) {
                    int numberFromCent = Integer.parseInt(dialogEditText_cent.getText().toString());
                    cent = String.format("%02d", numberFromCent);
                }
                if (!cent.equals("")) {
                    amountEnter = dialogEditText.getText() + "." + dialogEditText_cent.getText().toString();
                } else {
                    amountEnter = dialogEditText.getText().toString();
                }
                double amount_ = 0;
                if (amountEnter.equalsIgnoreCase("000") || amountEnter.equals("") || amountEnter.isEmpty()) {
                    Toasty.error(mContext, "Sale Amount cannot be empty", 8000, true).show();
                    amount_ = 0;
                    return;
                } else {
                    amount_ = Double.parseDouble(amountEnter);
                }

                // Format the double to two decimal places
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String formattedAmount = decimalFormat.format(amount_).replace(",", ".");

                amount = formattedAmount.replace(".", "");

                amountd = formattedAmount;


                et_note = findViewById(R.id.txt_notes);
                notes = et_note.getText().toString();
                if (amount.equals("000") || amount.equals("")) {
                    Toasty.error(mContext, "Sale Amount cannot be empty", 8000, true).show();

                    return;
                }
                double saleamount = Double.parseDouble(amountd);
                // Toasty.error(mContext, "Minimum Swipe"+Topitup.min_swipe, 8000, true).show();

                if (saleamount < Topitup.min_swipe) {
                    Toasty.error(mContext, "Minimum Sale Amount should be R " + Topitup.min_swipe, 8000, true).show();

                } else if (saleamount > Topitup.max_swipe) {
                    Toasty.error(mContext, "Maximum Sale Amount should be R " + Topitup.max_swipe, 8000, true).show();

                }
                if (saleamount < Topitup.warning_swipe) {
                    Log.e("-----","salemount"+ saleamount);
                    alertWindowCardORScan(String.valueOf(saleamount));

                } else {
                    Log.e("-----","salemount"+ saleamount);
                    confirmBox();
                }


            }


        });


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dummy);
        mBottomNav.findViewById(R.id.action_dummy).setBackgroundColor(getResources().getColor(R.color.white));
        String htmlslno;
        WebView wb_view = findViewById(R.id.wb_view);
//        Log.d("DEVICE_SLNO",Topitup.DEVICE_SLNO);
//        Log.d("asset_serial",asset_serial);
        //Topitup.DEVICE_SLNO
        htmlslno = "<div align='center'><font color='red'><b>Top it Up cannot be held liable for any losses or damages as a result of Card Fraud</b></font>.<br><br>Check that transactions are 'Successfully Approved' before handing over goods.<br> Cardholders can dispute transactions up to 6 months after sale. Please keep proof-of-sale (merchant copy)<br> Multiple transactions with same card & high sale value is not allowed within the same day.</div>";

    /*    String dummyserialnum = "7002I230118145";
        if (dummyserialnum.equals(asset_serial)) {
            htmlslno = "<div align='center'><font color='red'><b>Top it Up cannot be held liable for any losses or damages as a result of Card Fraud</b></font>.<br><br>Check that transactions are 'Successfully Approved' before handing over goods.<br> Cardholders can dispute transactions up to 6 months after sale. Please keep proof-of-sale (merchant copy)<br> Multiple transactions with same card & high sale value is not allowed within the same day.</div>";
        } else {
            btn_consume.setVisibility(View.GONE);
            mBottomNav.findViewById(R.id.action_billtxn).setVisibility(View.INVISIBLE);
            htmlslno = "<div align='center'><font color='red'><b>Your Device Serial # " + Topitup.DEVICE_SLNO + "  is not active</b></font>.<br><br>Please contact Top it Up to activate:<br><br>Customer Services on 0860 111 723<br>Whatsapp on 064 121 9970</div>";
        }*/
        btn_merchant.setEnabled(true);
        btn_customer.setEnabled(true);
        btn_merchant_customer.setEnabled(true);
        wb_view.getSettings().setJavaScriptEnabled(false);
        wb_view.loadDataWithBaseURL("", htmlslno, "text/html", "UTF-8", "");
        wb_slip.getSettings().setJavaScriptEnabled(false);
        wb_slip.loadDataWithBaseURL("", "<br><b><font color='red'>Please Wait....Loading Receipt</font></b>", "text/html", "UTF-8", "");

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_billtxn) {
                    Intent myIntentReprint = new Intent(mContext, activity_addpay_bills.class);
                    startActivity(myIntentReprint);
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    // Add any specific action for the clear case if needed
                    return true;
                } else {
                    return true; // This can handle any unrecognized item
                }
            }

        });
    }

//    private void ConfirmDialog() {
////        final String retailerPin = "123456"; // Example stored pin
//
//        Dialog dialogConfirm = new Dialog(this);
//        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogConfirm.setContentView(R.layout.confirm_pin);
//        dialogConfirm.setCancelable(true);
//        dialogConfirm.show();
//
//        EditText etPasscode = dialogConfirm.findViewById(R.id.et_passcode);
//        TextView tvError = dialogConfirm.findViewById(R.id.tv_error);
//        TextView btnConfirm = dialogConfirm.findViewById(R.id.btn_confirm);
//        TextView btnCancel = dialogConfirm.findViewById(R.id.btn_cancel);
//
//        btnConfirm.setOnClickListener(v -> {
//            String enteredPasscode = etPasscode.getText().toString().trim();
//
//            // Hide previous error
//            tvError.setVisibility(GONE);
//
//            if (enteredPasscode.isEmpty()) {
//                tvError.setText("Please enter your OTP");
//                tvError.setVisibility(VISIBLE);
//            } else if (enteredPasscode.equals(retailerPin)) {
//                dialogConfirm.dismiss();
//                MakePayment();
//            } else {
//                tvError.setText("Incorrect passcode ❌");
//                tvError.setVisibility(VISIBLE);
//                etPasscode.setText("");
//            }
//        });
//
//        btnCancel.setOnClickListener(v -> dialogConfirm.dismiss());
//    }

    private void ConfirmationForPayment(String amountd) {

        Dialog dialogConfirm = new Dialog(this);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogConfirm.setContentView(R.layout.confirmation_dialog);
        dialogConfirm.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        TextView txt_card = dialogConfirm.findViewById(R.id.txt_card);
        TextView txt_clock = dialogConfirm.findViewById(R.id.txt_clock);
        TextView txt_account = dialogConfirm.findViewById(R.id.txt_account);
        TextView txt_app_version = dialogConfirm.findViewById(R.id.txt_app_version);
        TextView txt_user_name = dialogConfirm.findViewById(R.id.txt_user_name);
        TextView txt_acc_type = dialogConfirm.findViewById(R.id.txt_acc_type);
        TextView txt_account_type = dialogConfirm.findViewById(R.id.txt_account_type);
        TextView txt_amount = dialogConfirm.findViewById(R.id.txt_amount);
        TextView txt_cancel_amount = dialogConfirm.findViewById(R.id.txt_cancel_amount);
        TextView txt_proceed_amount = dialogConfirm.findViewById(R.id.txt_proceed_amount);

        if (paymentMethod.equalsIgnoreCase("CardPay")) {
            txt_card.setText("Card Payment");
        } else {
            txt_card.setText("QR Payment");
        }

        txt_cancel_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
            }
        });
        txt_proceed_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();

                transanct();
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);
        Log.e("amount ", "amount enter cofirmation..........." + amountd);
        double amountEnter = Double.parseDouble(amountd);
        String formattedAmount = String.format("%.2f", amountEnter).replace(",", ".");

        txt_amount.setText("Amount R " + formattedAmount);
        if (Topitup.POSUSER_NAME.length() > 13) {
            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Admin");

            } else {
                txt_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
                txt_acc_type.setText("Cashier");

            }
        } else {

            if (Topitup.IS_ADMIN.equals("1")) {
                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                txt_acc_type.setText("Admin");
            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                txt_acc_type.setText("Cashier");
            }
        }
        if (company_name != null && !company_name.isEmpty()) {

            if (company_name.length() > 10) {
                txt_account.setText(company_name.substring(0, 10) + "... ");
            } else {

                txt_account.setText(company_name);
            }
        }
//        txt_activation.setText("Product " + providerName);
        dialogConfirm.show();
    }

//    private void CallApiForPin(){
//        final Call<ResponseBody> call = apiService.call_for_retailer_pin(Topitup.TIU_LICENSE);
//
//        call.enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                if (!response.headers().get("Server").equals("TIU")) {
//                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
//                    return;
//                }
//
//
//                String res = "";
//                try {
//                    res = response.body().string();
//                } catch (Exception ex) {
//                    if (response.body() != null)
//                        response.body().close();
//                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
//                    return;
//                }
//
//
//                //Timber.i("TRANSFER: " +  res);
//
//
//                if (res.trim().length() == 0 || res.contains("<error><err>") || res.toLowerCase().contains("ERR:") || res.contains("\"err\"")) {
//
//                    //Timber.i("TRANSFER: res- " + res);
//
//                    String err_msg = res;
//
//                    if (res.contains("<error><err>")) {
//                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
//                    }
//
//                    if (res.toLowerCase().contains("\"err\":")) {
//                        err_msg = res.replace("{\"err\":\"", "");
//                        err_msg = err_msg.replace("\"}", "");
//                    }
//
//                    Toasty.error(mContext, err_msg, 8000, true).show();
//                    return;
//
//                }
//
//
////                if (!res.contains("\\^")) {
////                    Toasty.info(mContext, "No Results.", 8000, true).show();
////                    dialog.dismiss();
////                    return;
////                }
//
//                try {
//                    JSONObject obj = new JSONObject(res);
//
//                    retailerPin = obj.getString("passcode");
//                    Log.e("passcode of retailer","passcode..."+retailerPin);
//                    /*if (obj.getString("pin_number").equals("ok")) {
//                        PrinterTopitup.print_data(obj.getString("slip"));
//                    } else {
////                        Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
////                        dialog.dismiss();
//                        return;
//                    }*/
//
//                } catch (Exception ex) {
////                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
//                    return;
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//
//                if (t instanceof IOException) {
//                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
//                } else {
//                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
//                }
//
//
//                //progressBar.setVisibility(View.GONE);
//                //t.printStackTrace();
//
//
//            }
//
//        });
//
//    }



//    private void MakePayment() {
//
//
//        int _filter_type = 0;
//        int _filter_type_to = 0;
//        if (rdo_filter_type_1.isChecked()) _filter_type = 1;
//
//        if (rdo_filter_type_to_1.isChecked()) _filter_type_to = 1;
//
//        if (txt_account_number.getText().toString().trim().length() < 6) {
//            Toasty.error(mContext, "Account Number too Short!", 3000, true).show();
//            return;
//        }
//
//
//        String amount = "";
//
//        String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();
//
//
//        amount = amountEnter.trim();
//        amount = amount.replace("R", "").trim();
//
//        if (amount.length() < 2) {
//            Toasty.error(mContext, "Please enter a correct value!", 3000, true).show();
//            return;
//        }
//
//
//        showCustomDialog("Processing", "Please wait...", false);
//
//        final Call<ResponseBody> call = apiService.wallet_transfer_interstore(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, txt_account_number.getText().toString().trim(), String.valueOf(_filter_type), amount, String.valueOf(_filter_type_to));
//
//        call.enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                if (!response.headers().get("Server").equals("TIU")) {
//                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
//                    return;
//                }
//
//                //Timber.i("TRANSFER: res- " + response.body().toString());
//
//                String res = "";
//                try {
//                    res = response.body().string();
//                } catch (Exception ex) {
//                    if (response.body() != null)
//                        response.body().close();
//                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
//                    dialog.dismiss();
//                    return;
//                }
//
//
//                //Timber.i("TRANSFER: " +  res);
//
//
//                if (res.trim().length() == 0 || res.contains("<error><err>") || res.toLowerCase().contains("ERR:") || res.contains("\"err\"")) {
//
//                    //Timber.i("TRANSFER: res- " + res);
//
//                    String err_msg = res;
//
//                    if (res.contains("<error><err>")) {
//                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
//                    }
//
//                    if (res.toLowerCase().contains("\"err\":")) {
//                        err_msg = res.replace("{\"err\":\"", "");
//                        err_msg = err_msg.replace("\"}", "");
//                    }
//
//                    Toasty.error(mContext, err_msg, 8000, true).show();
//                    dialog.dismiss();
//                    return;
//
//                }
//
//
////                if (!res.contains("\\^")) {
////                    Toasty.info(mContext, "No Results.", 8000, true).show();
////                    dialog.dismiss();
////                    return;
////                }
//
//                try {
//                    JSONObject obj = new JSONObject(res);
//
//                    //Log.d("My App", obj.toString());
//                    //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";
//
//                    if (obj.getString("ret").equals("ok")) {
//                        Printer.print_data(obj.getString("slip"));
//                    } else {
//                        Toasty.info(mContext, "Could not complete the transfer.", 8000, true).show();
//                        dialog.dismiss();
//                        return;
//                    }
//
//                } catch (Exception ex) {
//                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
//                    dialog.dismiss();
//                    return;
//                }
//
//
//                dialog.dismiss();
//                Toasty.success(mContext, "Payment Complete!", 3000, true).show();
//                onBackPressed();
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//
//                if (t instanceof IOException) {
//                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
//                } else {
//                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
//                }
//
//
//                //progressBar.setVisibility(View.GONE);
//                //t.printStackTrace();
//
//                dialog.dismiss();
//
//            }
//
//        });
//
//
//    }


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

    private void alertWindowCardORScan(String amountVal) {

        Dialog dialogConfirm = new Dialog(this);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogConfirm.setContentView(R.layout.alert_card_or_scan);
        dialogConfirm.setCancelable(true);
        this.setFinishOnTouchOutside(false);
        TextView txt_amount = dialogConfirm.findViewById(R.id.total_amount);
        ImageView cardPay = dialogConfirm.findViewById(R.id.card_pay);
        ImageView scanPay = dialogConfirm.findViewById(R.id.scan_pay);
        Log.e("amount ", "amount enter cofirmation..........." + amountd);
        double amountEnter = Double.parseDouble(amountVal);
        String formattedAmount = String.format("%.2f", amountEnter).replace(",", ".");
        txt_amount.setText("TOTAL AMOUNT : R" + formattedAmount);
        cardPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPay.setBackground(activity_adpay_new.this.getDrawable(R.drawable.hover_red));
                scanPay.setBackground(activity_adpay_new.this.getDrawable(R.drawable.hover_lyt));
                paymentMethod = "CardPay";
                Log.e("paymethod ", paymentMethod);
                dialogConfirm.dismiss();
                ConfirmationForPayment(amountd);
                // Toast.makeText(activity_adpay_new.this, paymentMethod, Toast.LENGTH_SHORT).show();

            }
        });
        scanPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanPay.setBackground(activity_adpay_new.this.getDrawable(R.drawable.hover_red));
                cardPay.setBackground(activity_adpay_new.this.getDrawable(R.drawable.hover_lyt));
                paymentMethod = "ScanPay";
                Log.e("paymethod ", paymentMethod);
                dialogConfirm.dismiss();
                ConfirmationForPayment(amountd);
                // Toast.makeText(activity_adpay_new.this, paymentMethod, Toast.LENGTH_SHORT).show();
            }
        });
        dialogConfirm.show();
    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay_new.rl_network.setVisibility(VISIBLE);
                    activity_adpay_new.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay_new.rl_network.setVisibility(View.INVISIBLE);
                    activity_adpay_new.rl_server.setVisibility(VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_adpay_new.rl_network.setVisibility(View.INVISIBLE);
                    activity_adpay_new.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void showDialogAlert() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.card_alert);
        dialog.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);

       /* ImageView img_logo = dialog.findViewById(R.id.img_logo);
        TextView txt_activation = dialog.findViewById(R.id.txt_activation);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);
*/
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

//        txt_activation.setText("Product " + providerName);
        dialog.show();
    }


    public void tipsonClick(View v) {

        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_tips);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();


//        Button btn_close = (Button) dialog.findViewById(R.id.btn_close);
//        Button btn_save = (Button) dialog.findViewById(R.id.btn_save);
        final EditText txt_rand_tip_dia = dialog.findViewById(R.id.txt_rand_tip_dia);
        final TextView tv_clear_tip = dialog.findViewById(R.id.tv_clear_tip);

        final EditText txt_perc_tip = dialog.findViewById(R.id.txt_perc_tip);

//        tab_layout.setVisibility(View.GONE);
        lo_tip.setVisibility(VISIBLE);
        lo_total.setVisibility(VISIBLE);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);
        txt_rand_tip_dia.addTextChangedListener(new MoneyTextWatcher(txt_rand_tip_dia));
//        txt_perc_tip.addTextChangedListener(new MoneyTextWatcher(txt_perc_tip));

        tv_clear_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txt_perc_tip.setText("");
                txt_rand_tip.setText("");

            }
        });
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        /*tv_rand_value.setText(setting_tv_rand_value);
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
        });*/


        dialog.show();
    }

    public void discountonCLick(View view) {

        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_discount);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();


//        Button btn_close = (Button) dialog.findViewById(R.id.btn_close);
//        Button btn_save = (Button) dialog.findViewById(R.id.btn_save);
        final EditText txt_rand_disc_dia = dialog.findViewById(R.id.txt_rand_disc_dia);


//        tab_layout.setVisibility(View.GONE);
        lo_disc.setVisibility(VISIBLE);
        lo_total.setVisibility(VISIBLE);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);
        txt_rand_disc_dia.addTextChangedListener(new MoneyTextWatcher(txt_rand_disc_dia));
//        txt_perc_tip.addTextChangedListener(new MoneyTextWatcher(txt_perc_tip));

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        /*tv_rand_value.setText(setting_tv_rand_value);
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
        });*/


        dialog.show();
    }

    public void oneRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        RadioButton radio_sale = findViewById(R.id.radio_sale);
        RadioButton radio_cash = findViewById(R.id.radio_cash);
        RadioButton radio_wholesale = findViewById(R.id.radio_wholesale);
        RadioButton radio_owner = findViewById(R.id.radio_owner);
        RadioButton radio_loan = findViewById(R.id.radio_loan);

      /*  switch (view.getId()) {
            case R.id.radio_sale:
                if (checked) {
                    radio_cash.setChecked(false);
                    radio_wholesale.setChecked(false);
                    radio_owner.setChecked(false);
                    radio_loan.setChecked(false);

                }
                break;
            case R.id.radio_cash:
                if (checked) {
                    radio_sale.setChecked(false);
                    radio_wholesale.setChecked(false);
                    radio_owner.setChecked(false);
                    radio_loan.setChecked(false);
                }
                break;
            case R.id.radio_wholesale:
                if (checked) {
                    radio_cash.setChecked(false);
                    radio_sale.setChecked(false);
                    radio_owner.setChecked(false);
                    radio_loan.setChecked(false);
                }
                break;
            case R.id.radio_owner:
                if (checked) {
                    radio_cash.setChecked(false);
                    radio_wholesale.setChecked(false);
                    radio_sale.setChecked(false);
                    radio_loan.setChecked(false);
                }
                break;
            case R.id.radio_loan:
                if (checked) {
                    radio_cash.setChecked(false);
                    radio_wholesale.setChecked(false);
                    radio_sale.setChecked(false);
                    radio_owner.setChecked(false);
                }
                break;

        }*/
        if (checked) {
            if (view.getId() == R.id.radio_sale) {
                radio_cash.setChecked(false);
                radio_wholesale.setChecked(false);
                radio_owner.setChecked(false);
                radio_loan.setChecked(false);
            } else if (view.getId() == R.id.radio_cash) {
                radio_sale.setChecked(false);
                radio_wholesale.setChecked(false);
                radio_owner.setChecked(false);
                radio_loan.setChecked(false);
            } else if (view.getId() == R.id.radio_wholesale) {
                radio_cash.setChecked(false);
                radio_sale.setChecked(false);
                radio_owner.setChecked(false);
                radio_loan.setChecked(false);
            } else if (view.getId() == R.id.radio_owner) {
                radio_cash.setChecked(false);
                radio_wholesale.setChecked(false);
                radio_sale.setChecked(false);
                radio_loan.setChecked(false);
            } else if (view.getId() == R.id.radio_loan) {
                radio_cash.setChecked(false);
                radio_wholesale.setChecked(false);
                radio_sale.setChecked(false);
                radio_owner.setChecked(false);
            }
        }

    }

    public void click_spi_multi(View view) {
        if (view.getId() == R.id.text_10) {
            txt_sale_amount.setText("10.00");
        } else if (view.getId() == R.id.text_15) {
            txt_sale_amount.setText("15.00");
        } else if (view.getId() == R.id.text_20) {
            txt_sale_amount.setText("20.00");
        } else if (view.getId() == R.id.text_25) {
            txt_sale_amount.setText("25.00");
        } else if (view.getId() == R.id.text_30) {
            txt_sale_amount.setText("30.00");
        } else if (view.getId() == R.id.text_50) {
            txt_sale_amount.setText("50.00");
        } else if (view.getId() == R.id.text_60) {
            txt_sale_amount.setText("60.00");
        } else if (view.getId() == R.id.text_75) {
            txt_sale_amount.setText("75.00");
        } else if (view.getId() == R.id.text_100) {
            txt_sale_amount.setText("100.00");
        }
    }

   /* public void click_spi_multi(View view) {
        switch (view.getId()) {
            case R.id.text_10:
                txt_sale_amount.setText("10.00");
                break;

            case R.id.text_15:
                txt_sale_amount.setText("15.00");
                break;

            case R.id.text_20:
                txt_sale_amount.setText("20.00");
                break;

            case R.id.text_25:
                txt_sale_amount.setText("25.00");
                break;

            case R.id.text_30:
                txt_sale_amount.setText("30.00");
                break;

            case R.id.text_50:
                txt_sale_amount.setText("50.00");
                break;

            case R.id.text_60:
                txt_sale_amount.setText("60.00");
                break;
            case R.id.text_75:
                txt_sale_amount.setText("75.00");
                break;
            case R.id.text_100:
                txt_sale_amount.setText("100.00");
                break;
        }
    }*/

    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        if (chk_tips.isChecked() || chk_discount.isChecked()) {
            lo_total.setVisibility(VISIBLE);
        } else {

            lo_total.setVisibility(View.GONE);
        }

        if (chk_tips.isChecked()) {

            lo_tip.setVisibility(VISIBLE);

        } else {
            lo_tip.setVisibility(View.GONE);

        }
        if (chk_discount.isChecked()) {

            lo_disc.setVisibility(VISIBLE);

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
        //mBottomNav.findViewById(R.id.action_back).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setting_chk_auto_mpos = settings.getString("setting_chk_auto_mpos",
                "0");


        if (dialog != null)
            dialog.dismiss();
        Log.e("status of the passcode","status......"+Topitup.ST_STATUS);
//        if(Topitup.ST_STATUS .equals("0")){
////            rl_main.setVisibility(GONE);
////            rl_qr_code.setVisibility(VISIBLE);
//        }else {
////            rl_main.setVisibility(VISIBLE);
////            rl_qr_code.setVisibility(GONE);
//            txt_account_number.requestFocus();
//            CallApiForPin();
//        }
        super.onResume();
    }


    public void onclick_btn_action(View v) {

        if (v.getId() == R.id.btn_consume) {
        }

    }


    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;

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

            bt_close.setVisibility(VISIBLE);
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


    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
        }
    };


    public class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
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
    private void sale() {
        // Intent myIntent2 = new Intent(getApplicationContext(), activity_adpay_new.class);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("buisnessorderno", orderno);
        editor.commit();
        Intent intent = new Intent();
      /*  intent.setPackage("com.wiseasy.cashier");
        intent.setAction("com.wiseasy.transaction.call");
        intent.putExtra("version", "A01");
        intent.putExtra("appId", "wza61f2e0da04ff7f8");
        intent.putExtra("transType", "SALE");*/

        Log.e("sale amount", "....sale amount......" + amount);
        JSONObject jsonObject = new JSONObject();
        if (paymentMethod.equalsIgnoreCase("CardPay")) {
            try {
                intent.setPackage("com.wiseasy.cashier");
                intent.setAction("com.wiseasy.transaction.call");
                intent.putExtra("version", "A01");
                intent.putExtra("appId", "wza61f2e0da04ff7f8");
                intent.putExtra("transType", "SALE");
                jsonObject.put("businessOrderNo", orderno);
                jsonObject.put("paymentScenario", "CARD");
                jsonObject.put("amt", amount);
                jsonObject.put("userID", "01");
                jsonObject.put("note", notes);
                intent.putExtra("transData", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                intent.setPackage("com.wiseasy.cashier");
                intent.setAction("com.wiseasy.transaction.call");
                intent.putExtra("version", "A01");
                intent.putExtra("appId", "wzc2b74124b4796a73");
                intent.putExtra("transType", "SALE");
                jsonObject.put("businessOrderNo", orderno);
                jsonObject.put("paymentScenario", "SCANQR");//SCANQR;
                jsonObject.put("paymentMethod", "ScanToPay");//Scan to pay
                jsonObject.put("amt", amount);
                jsonObject.put("userID", "01");
                jsonObject.put("note", notes);

                intent.putExtra("transData", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            dialog.dismiss();
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            dialog.dismiss();
            Toasty.error(mContext, "Contact Top it Up,  to Upgrade Your Cashier App!!!", 25000).show();

        }

    }

    private void refund() {
        Intent intent = new Intent();

        intent.setPackage("com.wiseasy.cashier");
        intent.setAction("com.wiseasy.transaction.call");
        intent.putExtra("version", "1.3");
        intent.putExtra("appId", "wza61f2e0da04ff7f8");
        intent.putExtra("transType", "REFUND");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("originBusinessOrderNo", transorderno);
            jsonObject.put("businessOrderNo", orderno);
            jsonObject.put("amt", amount);
            jsonObject.put("paymentScenario", "CARD");
            jsonObject.put("refNo", refNo);
            jsonObject.put("originTransDate", originTransDate);

            intent.putExtra("transData", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String transType = "SALE";
        String result = "";
        String resultMsg = "";
        //  Log.i("data",""+data.getExtras());

        if (data == null) {
            Toasty.error(mContext, "Contact Top it Up,  to Upgrade Your Cashier App!!!", 25000).show();
            return;

        }
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }
        result = data.getStringExtra("result");
        // Toasty.error(mContext, ""+result, 1000, true).show();
        Log.e("result", "result response from card..........." + result);
        resultMsg = "";
        //String resultMsg = data.getStringExtra("resultMsg");
        //  Log.d("result=",result);
        // Log.d("resultMsg=",resultMsg);
      //  transType = data.getStringExtra("transType");
       /* if (resultCode != -1) {
            result = data.getStringExtra("result");
            // Toasty.error(mContext, ""+result, 1000, true).show();
            Log.e("result", "result response from card..........." + result);
            resultMsg = "";
            //String resultMsg = data.getStringExtra("resultMsg");
            //  Log.d("result=",result);
            // Log.d("resultMsg=",resultMsg);
            transType = data.getStringExtra("transType");
        }*/ /*else if (resultCode == -1) {
            result = "00";
            transType = "SALE";
        }*/
      //  Log.d("!!!resultMsg=", transType);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        if (TextUtils.isEmpty(result)) {

            updateordernostsadpay("3");

        }
        else if (result.equals("M001")) {
            updateordernostsadpay("3");
            Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.wiseasy.cashier");
            //    notificationIntent.setPackage(null); // The golden row !!!
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (notificationIntent != null) {
                startActivity(notificationIntent);
            } else {
                Toasty.error(mContext, "Cashier App is not Installed!!!", 25000).show();
            }

        }
        else if (result.equals("M007")) {
            if (resultMsg.equals(""))
                updateordernostsadpay("2");
            else
                updateordernostsadpay("3");
            Intent intent = new Intent(mContext, activity_main.class);
            //intent.putExtra("URL", Topitup.BASE_URL_SYNC + "info/more_concat_details/?l=" + Topitup.TIU_LICENSE);
            startActivity(intent);

        }
        else {
            try {

                String transData = data.getStringExtra("transData");
                reltxnscreen.setVisibility(View.GONE);
                relpayscreen.setVisibility(VISIBLE);
                if (paymentMethod.equalsIgnoreCase("ScanPay")||paymentMethod.equalsIgnoreCase("CardPay")) {
                   // calladapyresponse(transData);


                    Log.i("transData", result + "......result.........." + transData);
                    if (result.equals("0") || result.equals("00")) {
                        Log.e("````````", result+"callbacl");
                        // if(transType.equals("SALE"))
                        //putorderno();
// orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved",notes,tips,discount
                        try {

                            JSONObject jsonObject = new JSONObject(transData);
                            refer_no = jsonObject.getString("refNo");

                            batch_no = jsonObject.getString("batchNo");

                            amount = jsonObject.getString("amt");

                            card_no = jsonObject.getString("cardNo");
                            if (jsonObject.has("expDate")) {
                                expDate = jsonObject.getString("expDate");
                            }
                            // String merchantID = jsonObject.getString("merchantID");

                            // String terminalID = jsonObject.getString("terminalID");
                            //  if (jsonObject.has("businessOrderNo"))
                            //    voucher_no = jsonObject.getString("businessOrderNo");
                            //   else
                            voucher_no = jsonObject.getString("traceNo");


                            String transDate = jsonObject.getString("transDate");
                            trans_time = transDate + " " + jsonObject.getString("transTime");
                            // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
                            //  trans_time = df.format(trans_time);
                            //  Calendar  calendar = Calendar.getInstance();
                            //   SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            // trans_time = simpleDateFormat.format(trans_time).toString();

                            Log.d("!!!!!!enable_realtime_swipe", enable_realtime_swipe);
                            if (enable_realtime_swipe.equals("1")) {
                                if (transType.equals("SALE")) {
                                    showCustomDialog("Processing", "Please wait...", false);
                                    html = company_name + "<br>" +
                                            account_number + "<br>" +
                                            "<br>" +
                                            "<br>" +
                                            trans_time + "<br>" +
                                            "<br>" +
                                            "Approved:R" + amountd + "\n" +
                                            "<br>" +
                                            "Tips :R" + tips + "<br>" +
                                            "<br>" +
                                            "Discount:R" + discount + "<br>" +
                                            "<br>" +
                                            "Tx. Date:" + trans_time + "<br>" +
                                            "PAN:" + card_no + "<br>" +
                                            "UID:" + orderno + "<br>" +
                                            "<br>" +
                                            "<br>" +
                                            "        Top it Up | 0860 111 723<br>" +
                                            "Whatsapp | 064 121 9970<br>" +
                                            "After Hours 23h00-07h00<br>" +
                                            "021 300 0121<br>" +
                                            "     www.topitup.co.za";
                                    wb_slip.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

                                    wb_slip.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                                    if (Build.VERSION.SDK_INT >= 19) {
                                        wb_slip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                                    } else {
                                        wb_slip.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                    }
                                    wb_slip.getSettings().setJavaScriptEnabled(false);

                                    wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

                                    callRealtimeSettleAPI();
                                } else if (transType.equals("QUERY"))
                                    callAddpayBackSettle();

                            } else {
                                bankslip();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {

                        Log.i("transData","else of reslt code"+result);
                        String slip_cancel = settings.getString("setting_slip_cancel", "0");
                        Log.i("transData", slip_cancel + "......slip_cancel.........." + transData);

                        if (slip_cancel.equals("1")) {

                            cslip = "2" + company_name + "\n" +
                                    "2" + account_number + "\n" +
                                    "1\n" +
                                    "1CUSTOMER RECEIPT\n" +
                                    "1\n" +
                                    "1" + trans_time + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "3Declined:R " + amountd + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1Tx. Date:" + trans_time + "\n" +
                                    "1UID:" + orderno + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1         Top it Up | 0860 111 723\n" +
                                    "1Whatsapp | 064 121 9970\n" +
                                    "1After Hours 23h00-07h00\n" +
                                    "1021 300 0121\n" +
                                    "1     www.topitup.co.za\n" +
                                    "1";
                            mslip = "2" + company_name + "\n" +
                                    "2" + account_number + "\n" +
                                    "1\n" +
                                    "1MERCHANT RECEIPT\n" +
                                    "1\n" +
                                    "1" + trans_time + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "3Declined:R " + amountd + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1Tx. Date: " + trans_time + "\n" +
                                    "1UID:" + orderno + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1         Top it Up | 0860 111 723\n" +
                                    "1Whatsapp | 064 121 9970\n" +
                                    "1After Hours 23h00-07h00\n" +
                                    "1021 300 0121\n" +
                                    "1     www.topitup.co.za\n" +
                                    "1";


                            if (MPOSTYPE == 1)
                                PrinterTopitup.print_data(cslip);
                            else if (MPOSTYPE == 2)
                                PrinterTopitup.print_data(mslip);
                            else
                                printslip();


                        } else {

                            html = "<font color='red'><b>" + company_name + "<br><br>" +
                                    account_number + "</b><br>" +
                                    "<br>" +
                                    trans_time + "<br>" +
                                    "<br>" +
                                    "Declined:  <b>R" + amountd + "</b><br><br>" +
                                    "Tx. Date:<b> " + trans_time + "</b><br><br>" +

                                    "UID:<b>" + orderno + "</b><br>" +
                                    "<br>" +
                                    "<br>" +
                                    "        Top it Up | 0860 111 723<br>" +
                                    "Whatsapp | 064 121 9970<br>" +
                                    "     www.topitup.co.za  </font>";
                            wb_slip.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

                            wb_slip.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                            if (Build.VERSION.SDK_INT >= 19) {
                                wb_slip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                            } else {
                                wb_slip.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            }
                            wb_slip.getSettings().setJavaScriptEnabled(false);

                            wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
                            btn_merchant.setEnabled(false);
                            btn_customer.setEnabled(false);
                            btn_merchant_customer.setEnabled(false);
                        }

                        updateordernostsadpay("2");


                    }

                }
                else {
                    Log.e("````````","final else");
                    cslip =
                            "1Top it Up Live Test\n\n" +
                                    "1TIU221 |Customer Copy\n\n" +
                                    "1\n" +
                                    "1" + trans_time + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "2Approved:R " + amountd + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1Tx. Date:" + trans_time + "\n" +
                                    "1Order No.:" + orderno + "\n" +
                                    "1\n" +
                                    "1\n" +
                                    "1         Top it Up | 0860 111 723\n" +
                                    "1Whatsapp | 064 121 9970\n" +
                                    "1After Hours 23h00-07h00\n" +
                                    "1021 300 0121\n" +
                                    "1     www.topitup.co.za\n" +
                                    "1";
                    mslip = "1Top it Up Live Test\n\n" +
                            "1TIU221 |Customer Copy\n\n" +
                            "1\n" +
                            "1" + trans_time + "\n" +
                            "1\n" +
                            "1\n" +
                            "2Approved:R " + amountd + "\n" +
                            "1\n" +
                            "1\n" +
                            "1Tx. Date:" + trans_time + "\n" +
                            "1Order No.:" + orderno + "\n" +
                            "1\n" +
                            "1\n" +
                            "1         Top it Up | 0860 111 723\n" +
                            "1Whatsapp | 064 121 9970\n" +
                            "1After Hours 23h00-07h00\n" +
                            "1021 300 0121\n" +
                            "1     www.topitup.co.za\n" +
                            "1";
                    printslip();
                }

            } catch (Exception ex) {

                Toasty.error(mContext, ex.getMessage(), 8000);
            }
        }

    }


    protected void updateordernostsadpay(String recon) {

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

                    autologout();

                    //  printingCustomDialog("Printing","Busy printing...");


                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                autologout();

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


    protected void updateordernosts(String recon) {

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

    protected void putorderno() {
        if (Topitup.checkConnection(getApplicationContext())) {
            //   Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

//            double amountd_final = Double.parseDouble(amountd);

            final Call<ResponseBody> call = apiService.setordernoamnt(Topitup.TIU_LICENSE, orderno, amountd);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                    if (response.code() == 500) {
                        // logresponse();

                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        if (dialog.isShowing())
                            dialog.dismiss();

                        return;
                    }

                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        if (dialog.isShowing())
                            dialog.dismiss();
                        return;
                    }


                    String res = "";
                    try {
                        res = response.body().string();


                    } catch (Exception ex) {
                        //
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
                        if (dialog.isShowing())
                            dialog.dismiss();

                        //stopCustomDialog("Problem",matcher);
                        //Toasty.info(mContext, matcher, 8000, true).show();

                    } else {        //Response OK

                        //  printingCustomDialog("Printing","Busy printing...");

                        try {
                            JSONObject reader = new JSONObject(res);
                            String html = "";
                            if (reader.getString("upid").equals("1")) {

                                sale();
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
        2->Bank Settlement
        3->Daily Settlement
        4->Admin Locked
         */
        // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf1.format(new Date());
        String posuserid = Topitup.POSUSER_ID;
        String customerid = Topitup.CUSTOMER_ID;
        String posusershort;
        String customershort;
        if (posuserid.length() > 2) {
            posusershort = posuserid.substring(posuserid.length() - 2);
        } else {
            posusershort = posuserid;
        }
        /*if(customerid.length()>2){
            customershort=customerid.substring(customerid.length()-2);
        }else{
            customershort=posuserid;
        }*/
        customershort = String.format("%05d", Integer.parseInt(customerid));
        orderno = enable_realtime_swipe + customershort + posusershort + currentDateandTime;

    }

    private void update_balance() {
        settings = getSharedPreferences("TIUPREF", 0);

        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");

        final String setting_balance_admin = settings.getString("setting_balance_admin", "0");
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        final String setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
        final String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
        final String setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

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

                      /*  if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                    }

                    if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                        view2.setVisibility(VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R  " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                    }
                    if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                        view3.setVisibility(VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }


                    if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

                       /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                    }


                    if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                        view2.setVisibility(VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
                    }

                    if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                        view3.setVisibility(VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
                    }
                }
            });
            is_admin = "0";
            if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

            tiu_title_balance.setVisibility(View.GONE);
            tiu_title_balance_cash.setVisibility(View.GONE);


            if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

             /*   if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                tiu_title_balance.setVisibility(VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

                view2.setVisibility(VISIBLE);
                tiu_title_balance_cash.setVisibility(VISIBLE);

            }
            if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                view3.setVisibility(VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            }

            if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

               /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                tiu_title_balance.setVisibility(VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

                view2.setVisibility(VISIBLE);
                tiu_title_balance_cash.setVisibility(VISIBLE);

            }
            if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
            }

            if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                view3.setVisibility(VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
            }

        } catch (Exception ex) {
            //
        }

    }

    protected void callRealtimeSettleAPI() {

        final Call<ResponseBody> call = apiService.adpay_txn(Topitup.TIU_LICENSE, orderno, card_no, voucher_no, batch_no, refer_no, trans_time, amount, "Approved", notes, tips, discount);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.code() == 500) {
                    // logresponse();
                    offlineslip();
                    if (dialog != null)
                        dialog.dismiss();

                    autologout();
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
                    //
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
                                txnsuccess = false;
                            }

                        } else {
                            //postxn=realm.where(swipetxn.class).equalTo("orderno",orderno).findFirst();

                            ringtone();
                            txnsuccess = true;
                            cslip = reader.getString("print_data");
                            mslip = reader.getString("print_data1");
                            showslip();
                            updateordernostsadpay("1");
                            // Intent myIntent2 = new Intent(mContext, activity_main.class);
                            // startActivity(myIntent2);
                          /*  try {

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
                                if (dialog != null)
                                    dialog.dismiss();
                            } catch (Exception ex)
                            {
                                //
                            }
*/


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


    protected void callAddpayBackSettle() {

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
                    //
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
                                //alredy settled
                                txnsuccess = false;

                                updateordernosts("1");

                            }


                        } else {
                            txnsuccess = true;

                            updateordernosts("1");

                        }
                        callback = true;
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


    protected void addpayquery() {


        Intent intent = new Intent();

        intent.setPackage("com.wiseasy.cashier");
        intent.setAction("com.wiseasy.transaction.call");
        intent.putExtra("version", "A01");
        intent.putExtra("appId", "wza61f2e0da04ff7f8");
        intent.putExtra("transType", "QUERY");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("businessOrderNo", orderno);
            intent.putExtra("transData", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, 1);


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
                    //
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
            String stringID = editText.getResources().getResourceName(editText.getId());

            if (stringID.contains("txt_rand_tip_dia")) {
                txt_rand_tip.setText(editText.getText());
            }
            if (stringID.contains("txt_rand_disc_dia")) {
                txt_rand_disc.setText(editText.getText());
            }


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

    public void onNumClick(View view) {
        if (view.getId() == R.id.btn_one) {
            selectTextViewToAppend("1");
        } else if (view.getId() == R.id.btn_two) {
            selectTextViewToAppend("2");
        } else if (view.getId() == R.id.btn_three) {
            selectTextViewToAppend("3");
        } else if (view.getId() == R.id.btn_four) {
            selectTextViewToAppend("4");
        } else if (view.getId() == R.id.btn_five) {
            selectTextViewToAppend("5");
        } else if (view.getId() == R.id.btn_six) {
            selectTextViewToAppend("6");
        } else if (view.getId() == R.id.btn_seven) {
            selectTextViewToAppend("7");
        } else if (view.getId() == R.id.btn_eight) {
            selectTextViewToAppend("8");
        } else if (view.getId() == R.id.btn_nine) {
            selectTextViewToAppend("9");
        } else if (view.getId() == R.id.btn_zero) {
            selectTextViewToAppend("0");
        } else if (view.getId() == R.id.btn_point) {
            selectTextViewToAppend(".");
        } else if (view.getId() == R.id.btn_clear) {
            selectTextViewToAppend(".");
        } else if (view.getId() == R.id.btn_merchant) {
            PrinterTopitup.print_data(mslip);
        } else if (view.getId() == R.id.btn_customer) {
            PrinterTopitup.print_data(cslip);
        } else if (view.getId() == R.id.btn_merchant_customer) {
            printslip();
        } else if (view.getId() == R.id.tv_clear_sale_amount) {
            txt_sale_amount.setText("");
            txt_total.setText("0.00");
        } else if (view.getId() == R.id.tv_clear_tip) {
            txt_perc_tip.setText("");
            txt_rand_tip.setText("");
        } else if (view.getId() == R.id.tv_clear_disc) {
            txt_rand_disc.setText("");
            txt_perc_disc.setText("");
        } else if (view.getId() == R.id.btn_cancel) {
            onBackPressed();
        }
    }


/*    public void onNumClick(View view) {

        switch (view.getId()) {
            case R.id.btn_one:
                selectTextViewToAppend("1");
                break;
            case R.id.btn_two:
                selectTextViewToAppend("2");
                break;
            case R.id.btn_three:
                selectTextViewToAppend("3");
                break;
            case R.id.btn_four:
                selectTextViewToAppend("4");
                break;
            case R.id.btn_five:
                selectTextViewToAppend("5");
                break;
            case R.id.btn_six:
                selectTextViewToAppend("6");
                break;
            case R.id.btn_seven:
                selectTextViewToAppend("7");
                break;
            case R.id.btn_eight:
                selectTextViewToAppend("8");
                break;
            case R.id.btn_nine:
                selectTextViewToAppend("9");
                break;
            case R.id.btn_zero:
                selectTextViewToAppend("0");
                break;
            case R.id.btn_point:
                selectTextViewToAppend(".");
                break;
            case R.id.btn_clear:
                selectTextViewToAppend(".");
                break;
            case R.id.btn_merchant://
                Printer.print_data(mslip);
                break;
            case R.id.btn_customer:
                Printer.print_data(cslip);
                break;
            case R.id.btn_merchant_customer:
                printslip();
                break;
            case R.id.tv_clear_sale_amount:
                txt_sale_amount.setText("");
                txt_total.setText("0.00");
                break;
            case R.id.tv_clear_tip:
                txt_perc_tip.setText("");
                txt_rand_tip.setText("");
                break;
            case R.id.tv_clear_disc:
                txt_rand_disc.setText("");
                txt_perc_disc.setText("");
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            default:
                break;

        }
    }*/

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
        reltxnscreen.setVisibility(View.GONE);
        relpayscreen.setVisibility(VISIBLE);
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
                "1     www.topitup.co.za\n" +
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
                "1     www.topitup.co.za\n" +
                "1";
        showslip();
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
                "1     www.topitup.co.za\n" +
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
                "1     www.topitup.co.za\n" +
                "1";
        if (MPOSTYPE == 1)
            PrinterTopitup.print_data(cslip);
        else if (MPOSTYPE == 2)
            PrinterTopitup.print_data(mslip);
        else
            printslip();
    }


    public void showslip() {


        reltxnscreen.setVisibility(View.GONE);
        relpayscreen.setVisibility(VISIBLE);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


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


    protected void confirmBox() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity_adpay_new.this);
        alert.setTitle("Confirm");
        alert.setMessage("Confirmation of large sale amount of          R " + amountd + " is correct?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alertWindowCardORScan(amountd);
               // transanct();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alert.show();
    }


    private void transanct() {
        if (Topitup.checkConnection(getApplicationContext())) {

            showCustomDialog("Card Payment Order No" + orderno, "Please Wait....", false);

            btn_consume.setEnabled(false);
            btn_consume.setVisibility(View.INVISIBLE);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            trans_time = dateformat.format(c.getTime());
            putorderno();

        } else {
            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
        }
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void autologout() {
        String setting_slip_logout = settings.getString("setting_slip_logout", "0");
        if (setting_slip_logout.equals("1")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    startActivity(myIntent2);
                }
            }, 500);

        }


    }

}