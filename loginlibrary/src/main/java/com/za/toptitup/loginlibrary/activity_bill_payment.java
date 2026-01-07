package com.za.toptitup.loginlibrary;

import static java.lang.Math.round;

import static com.za.toptitup.loginlibrary.utils.Topitup.getAppContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
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
import com.za.toptitup.loginlibrary.admin.activity_settings;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;


public class activity_bill_payment extends BaseActivity implements HomeAdapterMainBill.ItemListener {

    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static activity_bill_payment instance;
    Context mContext;
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    EditText txtAccountNumber;
    EditText txtPaymentAmount;
    EditText dialogEditText_cent, dialogEditText;
    RelativeLayout rl_amount;
    TextView tv_response;
    TextView tv_value;
    RelativeLayout layoutacc;
    int process_step, bill_payment_type;
    int printerQ1Sts;
    RealmResults<fin_balance> tiu_fin_balance;
    double value = 0.00d;
    String fee = "0", company_name, linkData;
    String tender = "0";
    RadioGroup tendertype;
    LinearLayout tiu_title_bar_new;
    boolean is_busy_with_voucher = false;
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;
    Dialog dialog;
    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            if (settings.getString("setting_time_out", "0").equals("1")) {

                //Toasty.error(mContext, "test", 5000, true).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Toasty.error(mContext, "test2t", 5000, true).show();
                        // Do something after 5s = 5000ms
                        logout();
                    }
                }, 10);
            } else {
                finish();
            }
        }
    };
    boolean pay_at_clicked = true, syntel_clicked, easy_pay_clicked;
    private TextView txt_rand;
    private Button btn_next, btn_easy_pay, btn_syntel;
    private ImageButton btn_pay_at;
    private Button btn_lookup;
    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    LinearLayout ll_provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        bill_payment_type = getIntent().getIntExtra("bill_payment_type", 1);
        Log.e("---kousi", String.valueOf(bill_payment_type));
        setContentView(R.layout.activity_bill_payments_new);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btn_easy_pay = findViewById(R.id.btn_easy_pay);
        btn_pay_at = findViewById(R.id.btn_pay);
        btn_syntel = findViewById(R.id.btn_syntel);
        ll_provider = findViewById(R.id.ll_provider);

        activity_login.fromScreen = "activity_bill_payment";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);

        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        ((Topitup) getApplication()).checkWifiSimInternet(this);

        btn_easy_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easy_pay_clicked = true;
                pay_at_clicked = false;
                syntel_clicked = false;
                btn_syntel.setBackground(getDrawable(R.drawable.btn_white_curved));
                btn_easy_pay.setBackground(getDrawable(R.drawable.btn_service_provider));
                btn_pay_at.setBackground(getDrawable(R.drawable.btn_white_curved));
                refresh_list();
                recyclerView.setVisibility(View.GONE);
            }
        });
        btn_pay_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easy_pay_clicked = false;
                pay_at_clicked = true;
                syntel_clicked = false;
                btn_syntel.setBackground(getDrawable(R.drawable.btn_white_curved));
                btn_easy_pay.setBackground(getDrawable(R.drawable.btn_white_curved));
                btn_pay_at.setBackground(getDrawable(R.drawable.btn_service_provider));
                refresh_list();
                recyclerView.setVisibility(View.VISIBLE);


            }
        });
        btn_syntel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easy_pay_clicked = false;
                pay_at_clicked = false;
                syntel_clicked = true;
                btn_syntel.setBackground(getDrawable(R.drawable.btn_service_provider));
                btn_easy_pay.setBackground(getDrawable(R.drawable.btn_white_curved));
                btn_pay_at.setBackground(getDrawable(R.drawable.btn_white_curved));
                refresh_list();
                recyclerView.setVisibility(View.GONE);

            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        layoutacc = findViewById(R.id.layoutacc);
        refresh_list();
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
       /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
*/






        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);

        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();

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
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "... ");
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

        company_name = tiu_settings.company_name;

        if (Topitup.IS_ADMIN.equals("1")) {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Admin");

            final String setting_chk_over_bill_pay_admin = settings.getString("setting_chk_over_bill_pay_admin", "0");
            if (setting_chk_over_bill_pay_admin.equals("1")) {
                fee = "1";
            } else {
                fee = "0";
            }

        } else {
            tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            tiu_user_name_.setText("Cashier");

            final String setting_chk_over_bill_pay_cashier = settings.getString("setting_chk_over_bill_pay_cashier", "0");
            if (setting_chk_over_bill_pay_cashier.equals("1")) {
                fee = "1";
            } else {
                fee = "0";
            }
        }

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
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

                    }
                }
            });

            if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
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
        /* END HEADER */


        txtAccountNumber = findViewById(R.id.txtAccountNumber);
        //txtAccountNumber.setText("115981111111111");
        txtPaymentAmount = findViewById(R.id.txtPaymentAmount);
        txt_rand = findViewById(R.id.txt_rand);
        dialogEditText = findViewById(R.id.dialogEditText);
        dialogEditText_cent = findViewById(R.id.dialogEditText_cent);
        cent_value_entered = "";
        rand_value_entered = "";
        dialogEditText.addTextChangedListener(new MoneyTextWatcherRand(dialogEditText));
        dialogEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(dialogEditText_cent));
       /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        txtAccountNumber.requestFocus();*/
      /*  if(txtAccountNumber.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }*/
        rl_amount = findViewById(R.id.rl_amount);
        tv_response = findViewById(R.id.tv_response);
        tv_value = findViewById(R.id.tv_value);
        tendertype = findViewById(R.id.tendertype);
        txtPaymentAmount.setVisibility(View.GONE);
        txtAccountNumber.setFocusable(true);
        rl_amount.setVisibility(View.GONE);
        tv_value.setVisibility(View.VISIBLE);


        btn_lookup = findViewById(R.id.btn_lookup);
        btn_lookup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntentBanking = new Intent(mContext, activity_bill_lookup.class);
                startActivity(myIntentBanking);
            }
        });


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dumm2);
        mBottomNav.findViewById(R.id.action_dumm2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        //mBottomNav.setSelectedItemId(R.id.action_logout);
        mBottomNav.getMenu().findItem(R.id.action_dumm1).setEnabled(true);
        mBottomNav.getMenu().findItem(R.id.action_dumm1).setIcon(R.drawable.ic_search);
        mBottomNav.getMenu().findItem(R.id.action_dumm1).setTitle("BILL ISSUER\nLOOKUP");
        //mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              /*  switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;
                    case R.id.action_dumm1:

                        Intent myIntentBanking = new Intent(mContext, activity_bill_lookup.class);
                        startActivity(myIntentBanking);
                        return true;
                    case R.id.action_logout:

                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent2);
                        return true;
                    case R.id.action_clear:

                        // if (process_step == 2) return true;

                        txtAccountNumber.setText("");
                        txtPaymentAmount.setText("");
                        dialogEditText_cent.setText("");
                        dialogEditText.setText("");
                        txtAccountNumber.requestFocus();


                        btn_next.setText("Next");
                        btn_next.setVisibility(View.VISIBLE);
                        btn_next.setEnabled(true);
                        process_step = 0;
                        tv_value.setVisibility(View.INVISIBLE);
                        txtAccountNumber.setEnabled(true);
                        txtPaymentAmount.setEnabled(true);
                        dialogEditText.setEnabled(true);
                        dialogEditText_cent.setEnabled(true);
                        txtPaymentAmount.setVisibility(View.GONE);
                        rl_amount.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        tv_response.setText("");

                        return true;
                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_dumm1) {
                    Intent myIntentBanking = new Intent(mContext, activity_bill_lookup.class);
                    startActivity(myIntentBanking);
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    // Clear fields and reset the UI
                    txtAccountNumber.setText("");
                    txtPaymentAmount.setText("");
                    dialogEditText_cent.setText("");
                    dialogEditText.setText("");
                    txtAccountNumber.requestFocus();

                    btn_next.setText("Next");
                    btn_next.setVisibility(View.VISIBLE);
                    btn_next.setEnabled(true);
                    process_step = 0;
                    tv_value.setVisibility(View.INVISIBLE);
                    txtAccountNumber.setEnabled(true);
                    txtPaymentAmount.setEnabled(true);
                    dialogEditText.setEnabled(true);
                    dialogEditText_cent.setEnabled(true);
                    txtPaymentAmount.setVisibility(View.GONE);
                    rl_amount.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_response.setText("");

                    return true;
                } else {
                    return true;
                }

            }

        });


        process_step = 0;

        btn_next = findViewById(R.id.btn_nexts);
        Button clearButton = findViewById(R.id.btn_clear);

        // Set up the OnClickListener for the button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the text in the EditText
                txtAccountNumber.setText("");
                txtPaymentAmount.setText("");
                dialogEditText_cent.setText("");
                dialogEditText.setText("");
                txtAccountNumber.requestFocus();

                btn_next.setText("Next");
                btn_next.setVisibility(View.VISIBLE);
                btn_next.setEnabled(true);
                process_step = 0;
                tv_value.setVisibility(View.INVISIBLE);
                txtAccountNumber.setEnabled(true);
                txtPaymentAmount.setEnabled(true);
                dialogEditText.setEnabled(true);
                dialogEditText_cent.setEnabled(true);
                txtPaymentAmount.setVisibility(View.GONE);
                rl_amount.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                tv_response.setText("");

            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                if (txtAccountNumber.getText().toString().equals("") || txtAccountNumber.getText().toString() == null) {
                    tv_response.setText("");
                    Toasty.error(mContext, "Enter an Account Number", 5000, true).show();
                    return;
                }
                if (!Topitup.checkConnection(getApplicationContext())) {
                  /*  String htmlslno = "No internet Connection. Please contact Top it Up Customer Services on 0860 111 723 or Whatsapp on 064 121 9970";

                    showCustomDialog("Error", htmlslno, true);*/
                    showNoInternetPopup();

                } else {
                    btn_next.setEnabled(false);
                    txtAccountNumber.setEnabled(false);
                    txtPaymentAmount.setEnabled(false);
                    dialogEditText_cent.setEnabled(false);
                    dialogEditText.setEnabled(false);

                    //    Toasty.error(mContext, "hello"+txtAccountNumber.getText().toString(), 5000, true).show();
                    if (process_step == 0) {

                        get_customer_information(txtAccountNumber.getText().toString(), "10.00");


                        btn_next.setEnabled(true);

                        // alertDialog.hide();

                    }

                    if (process_step == 1) {

                        //double value = 0d;

                        try {
                            String amountEnter = dialogEditText.getText() + "." + dialogEditText_cent.getText().toString();

                            value = Double.parseDouble(amountEnter);
                            value = round(value * 100);
                        } catch (Exception ex) {
                            value = 0;
                        }

                        if (value == 0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Incorret Amount!");
                            alertDialog.setMessage("Amount cannot be R 0.00");
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();


                            dialogEditText.requestFocus();
                            txtAccountNumber.setEnabled(true);
                            dialogEditText.setEnabled(true);
                            dialogEditText_cent.setEnabled(true);

                            btn_next.setEnabled(true);

                            return;
                        }

//                    if (!Printer.check_paper()) {
//                        showCustomDialog("Out of Paper","Please check paper and try again.", true);
//                        return;
//                    }

                        btn_next.setVisibility(View.VISIBLE);
                        btn_next.setEnabled(true);

                        String selectedPrinter = settings.getString("printer", "inner");
                        if (settings.getString("setting_print_to_screen", "0").equals("1")) {
                            btn_next.setVisibility(View.INVISIBLE);
                            do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                        } else {
                            if (selectedPrinter.equals("bluetooth")) {
                                if(activity_settings.isBluetoothConnected) {
                                    if(activity_settings.isBluetoothConnected && BluetoothService.isReallyConnected()) {

                                        if (!activity_settings.checkPrinterStatusWithoutHandler(Topitup.getAppContext())) {
                                            showUsbNotConnectedDialog(activity_bill_payment.this, txtAccountNumber.getText().toString(), String.valueOf(value));


                                        } else {
                                            btn_next.setVisibility(View.INVISIBLE);
                                            do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                        }
                                    }else{
                                        if (activity_settings.mService != null)  {
                                            String lastDeviceAddress = settings.getString("last_device_address", null);
                                            if (lastDeviceAddress != null) {
                                                if (activity_settings.mBluetoothAdapter == null) {
                                                    activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                }
                                                activity_settings.bluetoothMsg = "";
                                                BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                                                activity_settings.mService.connect(device);

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    public void run() {
                                                        if(activity_settings.isBluetoothConnected){

                                                            btn_next.setVisibility(View.INVISIBLE);
                                                            do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                                        }else{
                                                            Toast.makeText(getAppContext(),"Please try again",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }, 3000);


                                            }else{
                                                Toast.makeText(getAppContext(),"bluetooth last Address null",Toast.LENGTH_LONG).show();

                                            }
                                        }else{
                                            Toast.makeText(getAppContext(),"bluetooth Service null",Toast.LENGTH_LONG).show();

                                        }
                                    }

                                }else{
//                                    Toast.makeText(getAppContext(),"bluetooth was not connected",Toast.LENGTH_LONG).show();

                                    if (activity_settings.mService != null)  {
                                        String lastDeviceAddress = settings.getString("last_device_address", null);
                                        if (lastDeviceAddress != null) {
                                            if (activity_settings.mBluetoothAdapter == null) {
                                                activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                            }
                                            activity_settings.bluetoothMsg = "";
                                            BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                                            activity_settings.mService.connect(device);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    if(activity_settings.isBluetoothConnected){

                                                        btn_next.setVisibility(View.INVISIBLE);
                                                        do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                                    }else{
                                                        Toast.makeText(getAppContext(),"Please try again",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }, 3000);


                                        }else{
                                            Toast.makeText(getAppContext(),"bluetooth last Address null",Toast.LENGTH_LONG).show();

                                        }
                                    }else{
                                        Toast.makeText(getAppContext(),"bluetooth Service null",Toast.LENGTH_LONG).show();

                                    }

                                   /* if (activity_settings.mService != null)  {
                                        String lastDeviceAddress = settings.getString("last_device_address", null);
                                        if (lastDeviceAddress != null) {
                                            if (activity_settings.mBluetoothAdapter == null) {
                                                activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                            }
                                            activity_settings.bluetoothMsg = "";
                                            BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                                            activity_settings.mService.connect(device);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {

                                                    if(activity_settings.isBluetoothConnected){
                                                        btn_next.setVisibility(View.INVISIBLE);
                                                        do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                                    }else{
                                                        Toast.makeText(activity_bill_payment.this,"Bluetooth Printer is offline",Toast.LENGTH_LONG).show();

                                                    }
                                                }
                                            }, 3000);


                                        }else{
                                            Toast.makeText(activity_bill_payment.this,"bluetooth last Address null",Toast.LENGTH_LONG).show();

                                        }
                                    }else{
                                        Toast.makeText(activity_bill_payment.this,"bluetooth Service null",Toast.LENGTH_LONG).show();

                                    }*/
                                }

                            } else if (selectedPrinter.equals("usb")) {
                                if (PrinterTopitup.dev != null) {

                                    if (!(PrinterTopitup.usbCtrl.isHasPermission(PrinterTopitup.dev))) {
                                        PrinterTopitup.printmethod(activity_bill_payment.this);
                                    } else {
                                        btn_next.setVisibility(View.INVISIBLE);
                                        do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                    }
                                } else {
                                    PrinterTopitup.printmethod(activity_bill_payment.this);

                                    // Re-check if the device is now connected after calling printmethod
                                    if (PrinterTopitup.dev == null) {
                                        // No USB device is connected, handle this case (e.g., show a dialog)
                                        showUsbNotConnectedDialog(activity_bill_payment.this,txtAccountNumber.getText().toString(),String.valueOf(value));
                                    }


                                }

                            } else {
                                if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("TABLET") ){
                                    Toast.makeText(getAppContext(), "Please connect USB or Bluetooth", Toast.LENGTH_LONG).show();

                                }else{
                                    btn_next.setVisibility(View.INVISIBLE);
                                    do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
                                }

                            }

                        }




                    }

                }
            }
        });


    }


    public  void showUsbNotConnectedDialog(Context conn, String txtAccountNumber, String value) {
        Dialog dialog = new Dialog(conn);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.usb_not_connected);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        LinearLayout ll_buttons = dialog.findViewById(R.id.ll_buttons);
        TextView txt_ok = dialog.findViewById(R.id.txt_ok);

        TextView txt_print_screen = dialog.findViewById(R.id.txt_print_screen);

        TextView txt_reprint = dialog.findViewById(R.id.txt_reprint);
        ll_buttons.setWeightSum(2);

        txt_print_screen.setVisibility(View.VISIBLE);

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txt_print_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();

                editor.putString("setting_print_to_screen", "1");
                editor.commit();
                btn_next.setVisibility(View.INVISIBLE);
                do_payment(txtAccountNumber, value);
                dialog.dismiss();


            }
        });
        txt_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }
    public void refresh_list() {
        arrayList = new ArrayList<>();

        if (bill_payment_type == 1) {

            arrayList.add(new Item("11374", "11374", R.drawable.prov_dstv, "#FFFFFF", ""));
        } else if (bill_payment_type == 2) {
            /*  arrayList.add(new Item("11374", "11374", R.drawable.prov_dstv, "#FFFFFF", ""));*/
            arrayList.add(new Item("11425", "11425", R.drawable.prov_el, "#FFFFFF", ""));
            arrayList.add(new Item("11440", "11440", R.drawable.prov_apm, "#FFFFFF", ""));
            arrayList.add(new Item("11344", "11344", R.drawable.prov_eldo, "#FFFFFF", ""));
            arrayList.add(new Item("11425", "11425", R.drawable.prov_ie, "#FFFFFF", ""));
            arrayList.add(new Item("11333", "11333", R.drawable.prov_intercape, "#FFFFFF", ""));
        } else if (bill_payment_type == 3) {
            arrayList.add(new Item("11380", "11380", R.drawable.prov_mukuru, "#FFFFFF", ""));
            arrayList.add(new Item("11422", "11422", R.drawable.prov_mamamoney, "#FFFFFF", ""));
            arrayList.add(new Item("11420", "11420", R.drawable.prov_sfx, "#FFFFFF", ""));
//            arrayList.add(new Item("11429", "11429", R.drawable.prov_hellopaisa, "#FFFFFF", ""));
            //arrayList.add(new Item("", "", R.drawable.prov_yellowcard, "#FFFFFF", ""));
        } else {
            if (pay_at_clicked) {
                arrayList.add(new Item("11374", "11374", R.drawable.prov_dstv, "#FFFFFF", ""));
                arrayList.add(new Item("11420", "11420", R.drawable.prov_sfx, "#FFFFFF", ""));

                arrayList.add(new Item("11303", "11303", R.drawable.prov_tvl, "#FFFFFF", ""));
                //  arrayList.add(new Item("", "", R.drawable.prov_telkom, "#FFFFFF", ""));
                arrayList.add(new Item("11454", "11454", R.drawable.prov_avon, "#FFFFFF", ""));
                arrayList.add(new Item("", "", R.drawable.prov_homechoice, "#FFFFFF", ""));
                // arrayList.add(new Item("", "", R.drawable.prov_eskcom, "#FFFFFF", ""));

            } else {
                //  arrayList.add(new Item("11374", "11374", R.drawable.prov_dstv, "#FFFFFF", ""));
                //arrayList.add(new Item("11420", "11420", R.drawable.prov_sfx, "#FFFFFF", ""));
                if (easy_pay_clicked || syntel_clicked)
                    arrayList.add(new Item("11303", "11303", R.drawable.prov_tvl, "#FFFFFF", ""));
                arrayList.add(new Item("", "", R.drawable.prov_telkom, "#FFFFFF", ""));
                arrayList.add(new Item("11454", "11454", R.drawable.prov_avon, "#FFFFFF", ""));
                arrayList.add(new Item("", "", R.drawable.prov_homechoice, "#FFFFFF", ""));
                arrayList.add(new Item("", "", R.drawable.prov_eskcom, "#FFFFFF", ""));

            }
        }
//        arrayList.add(new ItemSPI("11374",1, R.drawable.prov_dstv, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("-",1, R.drawable.prov_telkom, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("-",1, R.drawable.prov_hellopaisa, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("11455",1, R.drawable.prov_mukuru, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("-",1, R.drawable.prov_mamamoney, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("11374",1, R.drawable.prov_dstv, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("ma",1, R.drawable.prov_coct, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("11374",1, R.drawable.prov_homechoice, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("11455",1, R.drawable.prov_justine, "#FFFFFF","",""));
//        arrayList.add(new ItemSPI("11361",1, R.drawable.prov_pathcare, "#FFFFFF","",""));
        HomeAdapterMainBill adapter = new HomeAdapterMainBill(activity_bill_payment.this, arrayList, activity_bill_payment.this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
//        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_bill_payment.rl_network.setVisibility(View.VISIBLE);
                    activity_bill_payment.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_bill_payment.rl_network.setVisibility(View.INVISIBLE);
                    activity_bill_payment.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_bill_payment.rl_network.setVisibility(View.INVISIBLE);
                    activity_bill_payment.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void showNoInternetPopup() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
        bt_close = dialog.findViewById(R.id.bt_close);
        bt_close.setOnClickListener(new View.OnClickListener() {
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

    public void get_customer_information(String pass_account_number, String pass_value) {

        is_busy_with_voucher = true;

        showCustomDialog("Bills", "Requesting Customer Information", false);
        int selectedRadioButtonId = tendertype.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        String selectedRbText = selectedRadioButton.getText().toString();




        Log.i("selectedRbText=", selectedRbText);
        if (selectedRbText.equals("Credit Card")) tender = "1";
        else if (selectedRbText.equals("Debit Card")) tender = "2";
        else tender = "0";


        final Call<ResponseBody> call = apiService.bill_get_custinfo(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, pass_account_number, pass_value, tender, "0", fee);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            return;
                        }

                        //Timber.e(response.body());
                        String res = "";
                        try {

                            res = response.body().string();

                        } catch (Exception ex) {

                            if (response.body() != null) {
                                response.body().close();
                            }
                        }

                        if (res.contains("<error><err>")) {

                            String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                            tv_response.setTextColor(Color.parseColor("#ff0000"));
                            tv_response.setText(matcher);

                            txtAccountNumber.setEnabled(true);
                            dialogEditText.setEnabled(true);
                            dialogEditText_cent.setEnabled(true);

                            dialogEditText.requestFocus();


                            //Toasty.info(mContext, matcher, 8000, true).show();

                        } else {        //Response OK

                            tv_response.setTextColor(Color.parseColor("#3c3c3c"));
                            JSONObject json = null;    // create JSON obj from string
                            try {
                                JSONObject obj = new JSONObject(res);
                                if (obj.has("ret")) {
                                    Log.i("bill", obj.getString("ret"));
                                    tv_response.setText(obj.getString("ret"));
                                }
                                if (obj.has("linkData")) {
                                    linkData = obj.getString("linkData");
                                }

                            } catch (JSONException e) {
                                // Toasty.error(mContext, "JSON ERROR", 8000, true).show();
                                tv_response.setText(res);
                            }

                   /* if(res.contains("Link Data :")) {
                        String[] parts = res.split("Link Data :");
                       // Toasty.info(mContext, parts[1], 8000, true).show();
                        linkData=parts[1];
                        res.replace("Link Data :"+linkData,"");

                    }
                    tv_response.setText(res);*/
                            recyclerView.setVisibility(View.GONE);
//                    tv_value.setVisibility(View.VISIBLE);

//                    txtPaymentAmount.setVisibility(View.VISIBLE);
                            rl_amount.setVisibility(View.VISIBLE);
                            ll_provider.setVisibility(View.GONE);
                            dialogEditText_cent.setEnabled(true);
                            dialogEditText.setEnabled(true);

                            dialogEditText.requestFocus();

                            btn_next.setText("Process");
                            process_step = 1;

                        }


                        dialog.dismiss();

                        //Toasty.info(mContext, res, 8000, true).show();
                    } else {
                        stopCustomDialog("Error", "There was a problem retrieving the account details.Please try again later.");

                    }
                } catch (Exception e) {
                    if (response.body() != null)
                        response.body().close();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

                is_busy_with_voucher = false;

                //progressBar.setVisibility(View.GONE);
                t.printStackTrace();

            }

        });


    }

    public void do_payment(String txtAccountNumber, String value) {
        // Log.e("infoo",txtAccountNumber+",,,"+value);

        dialog.dismiss();
        ConfirmationForPayment(txtAccountNumber,value);

    }

    public void doPrePayment(String txtAccountNumber, String value){
        showCustomDialog("Requesting Voucher", "Processing...", false);

        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts = 2;
                showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                return;
            }
        }
        if (!PrinterTopitup.check_paper(mContext)) {
            printerQ1Sts = 1;
            // showCustomDialog("Printer Issue", "Please try again. After Some time", true);
            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
            return;
        }
        is_busy_with_voucher = true;


        showCustomDialog("Bills", "Processing...", false);


        final Call<voucher_response> call = apiService.bill_do_payment(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, txtAccountNumber, value, tender, "1", fee, linkData);

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                if (response.isSuccessful()) {
                    try {
                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            return;
                        }
                        //Timber.e(response.body());
                        process_step = 2;

                        voucher_response res = response.body();

                        byte[] bytes = android.util.Base64.decode(res.print_data, android.util.Base64.DEFAULT);
                        //String stringValueBase64Decoded = Base64.encodeToString(bytes, Base64.NO_WRAP);

                        String stringValueBase64Decoded = new String(bytes);

                        Toasty.info(mContext, stringValueBase64Decoded, 8000, true).show();

                        printingCustomDialog("Printing", "Your slip is busy printing.\nYou have been charged, please check reprint if there is an issue.");

                        //    Printer.store_last_reprint(stringValueBase64Decoded);
                        PrinterTopitup.print_data(stringValueBase64Decoded);
                    } catch (Exception e) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/

                    }


                } else {

                    stopCustomDialog("Connection Issue", "You have been charged, please check reprint if there is an issue.");

                }
                //Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                // Toasty.error(mContext, t.getMessage(), 8000, true).show();


                if (t instanceof SocketTimeoutException) {

                    stopCustomDialog("Connection Issue", "You have been charged, please check reprint if there is an issue.");

                } else {

                    if (t.getMessage().contains("Exception")) {
                        stopCustomDialog("Problem", "please try again after some time");

                    } else {
                        stopCustomDialog("Problem", t.getMessage());

                    }
                    //voucher_response res = t.toString();

                    //checkissue(txtAccountNumber,value);
                    //  stopCustomDialog("Problem","Minimum amount is R 20.00");


                }

                is_busy_with_voucher = false;

                t.printStackTrace();

            }

        });

    }
    private void ConfirmationForPayment(String txtAccountNumber, String value) {

        Dialog dialogConfirm= new Dialog(this);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogConfirm.setContentView(R.layout.confirmation_bill_dialog);
        dialogConfirm.setCancelable(true);
        this.setFinishOnTouchOutside(false);
        TextView txt_clock = dialogConfirm.findViewById(R.id.txt_clock);
        TextView txt_account = dialogConfirm.findViewById(R.id.txt_account);
        TextView txt_app_version = dialogConfirm.findViewById(R.id.txt_app_version);
        TextView txt_user_name = dialogConfirm.findViewById(R.id.txt_user_name);
        TextView txt_acc_type = dialogConfirm.findViewById(R.id.txt_acc_type);
        TextView txt_account_type = dialogConfirm.findViewById(R.id.txt_account_type);
        TextView txt_amount = dialogConfirm.findViewById(R.id.txt_amount);
        TextView txt_meter = dialogConfirm.findViewById(R.id.txt_meter);
        TextView txt_vouchers = dialogConfirm.findViewById(R.id.txt_vouchers);
        TextView txt_card = dialogConfirm.findViewById(R.id.txt_card);



        TextView txt_cancel_amount = dialogConfirm.findViewById(R.id.txt_cancel_amount);
        TextView txt_proceed_amount = dialogConfirm.findViewById(R.id.txt_proceed_amount);

        txt_cancel_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                btn_next.setVisibility(View.VISIBLE);
                btn_next.setEnabled(true);

            }
        });
        txt_proceed_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                doPrePayment(txtAccountNumber,value);
// transanct();
            }
        });



        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        String strDate = sdf.format(c.getTime());
        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(txtAccountNumber);

        if(bill_payment_type==1){
            txt_card.setText("DSTV");

        } else if(bill_payment_type==2){
            txt_card.setText("Pay Bills");

        }else if(bill_payment_type==3){
            txt_card.setText("Money Transfer");

        }
        double result = Double.parseDouble(value) / 100;
        System.out.println(String.valueOf(result));
        setBoldText(txt_meter,"Account Number #:",txtAccountNumber,"");
        setBoldText(txt_amount,"Amount: R ",String.valueOf(result),"");

 /*setBoldText(txt_meter,"Meter #:",pass_meter_number,"");
 setBoldText(txt_amount,"Amount: R ",elecAmount,"");
 setBoldText(txt_vouchers,"Number of Vouchers:",String.valueOf(mqty),"");*/


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
// txt_activation.setText("Product " + providerName);
        dialogConfirm.show();
    }
    public void setBoldText(TextView textView, String prefix, String boldText, String suffix) {
        // Combine all parts of the text
        String fullText = prefix + boldText + suffix;

        // Create a SpannableStringBuilder
        SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);
        int start = prefix.length();
        int end = start + boldText.length();
        // Apply bold style to the boldText part
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                start, // Start index of bold text
                end, // End index of bold text
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new RelativeSizeSpan(1.2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the styled text to the TextView
        textView.setText(spannable);
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
            if (printerQ1Sts == 1) {
                bt_close.setVisibility(View.GONE);
                btn_paper_load.setVisibility(View.VISIBLE);
            } else {
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
                do_payment(txtAccountNumber.getText().toString(), String.valueOf(value));
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

    @Override
    public void onItemClick(Item item) {
        if (item == null) {
            return;
        }
        txtAccountNumber.setText(item.pos);
        txtAccountNumber.setSelection(txtAccountNumber.getText().length());
        recyclerView.setVisibility(View.GONE);
        layoutacc.setVisibility(View.VISIBLE);

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


}