package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hbb20.CountryCodePicker;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.MyItem;
import com.za.toptitup.loginlibrary.model.ReprintList;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_ding extends BaseActivity implements HomeAdapterMain.ItemListener {

    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    public static activity_ding instance;
    public String finalNumber = "";
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    int printerQ1Sts;
    String tokenType, randamount = "R 0.00", operator, currency, tokenTypeInfo;
    RelativeLayout rl_amount;
    ListView lst_reprint;
    TextView tv_response, tv_phone, tv_response1, tv_value, txt_cent, txt_rand;
    RealmResults<fin_balance> tiu_fin_balance;
    double value = 0.00d, amount = 0.00d;
    LinearLayout linlayout;
    ArrayList<MyItem> movies = new ArrayList<MyItem>();
    ReprintList listAdapter;
    boolean is_busy_with_voucher = false, isDenominated = true;
    RelativeLayout relglobalairtime, relglobalele;
    LinearLayout tiu_title_bar_new;
    // Hide after some seconds
    Handler handler = new Handler();
    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;
    Dialog dialog;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };
    Dialog dialog_multi;
    String stock_uid = "";
    AppCompatButton bt_process;
    AppCompatButton bt_reprint;
    TextView txt_last_header;
    TextView txt_last_time;
    TextView pop_title2;
    private Context mContext;
    private boolean isProgrammaticChange = false;
    private BroadcastReceiver mNetworkReceiver;
    private EditText edtPhoneNumber, PhoneNumber1, isocoutrycode, amntEditText, amntEditText_cent;
    private Button btn_next, btn_non_pay, btn_ret;
    private CountryCodePicker ccp;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ding);
        mContext = this;
        instance = this;

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        ccp = findViewById(R.id.ccp);
        //   ccp.resetToDefaultCountry();

        edtPhoneNumber = findViewById(R.id.PhoneNumber);
        rl_amount = findViewById(R.id.rl_amount);
//        et_amount.addTextChangedListener(new MoneyTextWatcher(et_amount));
        ccp.registerCarrierNumberEditText(edtPhoneNumber);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        amntEditText = findViewById(R.id.dialogEditText);
        amntEditText_cent = findViewById(R.id.dialogEditText_cent);

        txt_rand = findViewById(R.id.txt_rand);
        txt_cent = findViewById(R.id.txt_cent);
        //tv_value.setVisibility(View.GONE);
//        txtPaymentAmount.setVisibility(View.GONE);
        cent_value_entered = "";
        rand_value_entered = "";
        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));

        PhoneNumber1 = findViewById(R.id.PhoneNumber1);
        isocoutrycode = findViewById(R.id.isocoutrycode);
        // ccp.registerPhoneNumberTextView(PhoneNumber1);
        tv_response = findViewById(R.id.tv_response);
        tv_value = findViewById(R.id.tv_value);
        tv_response1 = findViewById(R.id.tv_response1);
        tv_phone = findViewById(R.id.tv_phone);
        linlayout = findViewById(R.id.linlayout);
        PhoneNumber1.requestFocus();
        activity_login.fromScreen = "activity_ding";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        btn_next = findViewById(R.id.btn_next);
        btn_non_pay = findViewById(R.id.btn_non_pay);
        relglobalairtime = findViewById(R.id.relglobalairtime);
        relglobalele = findViewById(R.id.relglobalele);
        btn_ret = findViewById(R.id.btn_ret);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
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

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);
        recyclerView = findViewById(R.id.recyclerView);
        bt_close = dialog.findViewById(R.id.bt_close);
        btn_paper_load = dialog.findViewById(R.id.btn_paper_load);
        btn_Paper_ignore_time = dialog.findViewById(R.id.btn_Paper_ignore_time);
        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");

        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);


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
                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "...");
                tiu_user_name_.setText("Admin");


            } else {

                tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "...");
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
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
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

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
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
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                /*PhoneNumberUtil phoneNumberUtil;
                phoneNumberUtil = PhoneNumberUtil.createInstance((MetadataLoader) getApplicationContext());*/
                Log.e("daaa", ccp.isValidFullNumber() + ",,," + edtPhoneNumber.getText().toString() + ",");
                if (edtPhoneNumber.getText().length() >= 9) {
                    btn_next.setVisibility(View.VISIBLE);
                } else {
                    btn_next.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (edtPhoneNumber.getText().toString().matches("^0")) {
                    // Not allowed
                    // Toast.makeText(mContext, "not allowed", Toast.LENGTH_LONG).show();
                    Toasty.error(mContext, "not allowed", 8000, true).show();
                    edtPhoneNumber.setText("");
                }
                edtPhoneNumber.requestFocus(edtPhoneNumber.getText().length());


//                if(!s.toString().startsWith(ccp.getSelectedCountryCodeWithPlus())){
//                    edtPhoneNumber.setText(ccp.getSelectedCountryCodeWithPlus());
//                    Selection.setSelection(edtPhoneNumber.getText(), edtPhoneNumber.getText().length());
//
//                }


//                if(edtPhoneNumber.getText().length()>10) {
//
//                    ccp.setFullNumber(edtPhoneNumber.getText().toString());
//                    if(ccp.isValid()) {
//                        ccp.registerPhoneNumberTextView(edtPhoneNumber);
//                        edtPhoneNumber.setText(ccp.getPhoneNumber().toString());
//                        PhoneNumber1.setText(ccp.getFullNumber());
//                    }
//                }

            }
        });

        PhoneNumber1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (isProgrammaticChange) return;  // skip during button click
                if (PhoneNumber1.getText().length() >= 3) {
                    ccp.setFullNumber(PhoneNumber1.getText().toString());
                    edtPhoneNumber.setText(ccp.getSelectedCountryCodeWithPlus() + edtPhoneNumber.getText());
                    Log.e("ddddd", edtPhoneNumber.getText().toString());
                    int pos = edtPhoneNumber.getText().length();
                    edtPhoneNumber.setSelection(pos);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (isProgrammaticChange) return;  // skip during button click
                if (PhoneNumber1.getText().toString().matches("^0")) {
                    // Not allowed
                    Toasty.error(mContext, "not allowed, input country code", 8000, true).show();
                    PhoneNumber1.setText("");
                }

//                if(edtPhoneNumber.getText().length()>10) {
//
//                    ccp.setFullNumber(edtPhoneNumber.getText().toString());
//                    if(ccp.isValid()) {
//                        ccp.registerPhoneNumberTextView(edtPhoneNumber);
//                        edtPhoneNumber.setText(ccp.getPhoneNumber().toString());
//                        PhoneNumber1.setText(ccp.getFullNumber());
//                    }
//                }

            }
        });

        lst_reprint = findViewById(R.id.lst_reprint);


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {


            @Override
            public void onCountrySelected() {

                PhoneNumber1.setVisibility(View.GONE);
                edtPhoneNumber.setVisibility(View.VISIBLE);
                edtPhoneNumber.setFocusable(true);
                if (edtPhoneNumber.getText().equals("") || edtPhoneNumber.getText() == null) {
                    edtPhoneNumber.setText("");
                }
                //  isocoutrycode.setVisibility(View.VISIBLE);
                isocoutrycode.setText(ccp.getSelectedCountryCodeWithPlus());
                edtPhoneNumber.setText(ccp.getSelectedCountryCodeWithPlus() + edtPhoneNumber.getText());
                int pos = edtPhoneNumber.getText().length();
                edtPhoneNumber.setSelection(pos);

            }
        });


        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_dumm1);
        mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                /*switch (item.getItemId()) {
                    case R.id.action_back:
                        onBackPressed();
                        return true;
                    case R.id.action_airtime:
                        clearfrm();
                        relglobalairtime.setVisibility(View.VISIBLE);
                        relglobalele.setVisibility(View.GONE);
                        return true;
              *//*      case R.id.action_electrcity:
                        clearfrm();
                        relglobalairtime.setVisibility(View.GONE);
                        relglobalele.setVisibility(View.VISIBLE);
                        return true;*//*
                    case R.id.action_clear:
                        clearfrm();
                        return true;
                    case R.id.action_logout:

                        Intent myIntent2 = new Intent(mContext, activity_login.class);
                        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent2);
                        return true;

                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_airtime) {
                    clearfrm();
                    relglobalairtime.setVisibility(View.VISIBLE);
                    relglobalele.setVisibility(View.GONE);
                    return true;
/*} else if (item.getItemId() == R.id.action_electrcity) {
    clearfrm();
    relglobalairtime.setVisibility(View.GONE);
    relglobalele.setVisibility(View.VISIBLE);
    return true;*/
                } else if (item.getItemId() == R.id.action_clear) {
                    clearfrm();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    Intent myIntent2 = new Intent(mContext, activity_login.class);
                    myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent2);
                    return true;
                } else {
                    return true;
                }

            }

        });

        btn_next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //edtPhoneNumber.setText("9880343423");
                //
                if (!Topitup.checkConnection(getApplicationContext())) {
                   /* String htmlslno = "No internet Connection. Please contact Top it Up Customer Services on 0860 111 723 or Whatsapp on 064 121 9970";

                    showCustomDialog("Error", htmlslno, true);
*/
                    showNoInternetPopup();

                } else {

                    ////changes
                    //  String ccode = ccp.getSelectedCountryCodeWithPlus();
                    // String phno = edtPhoneNumber.getText().toString().replace(ccode, "");
                    //edtPhoneNumber.setText(phno);
                    ///changes

                    ccp.registerCarrierNumberEditText(edtPhoneNumber);

                    //  if (ccp.isValid()) {
                    showCustomDialog("Global Airtime", "Requesting Vouchers Please Wait!!!", false);
                    Log.e("Error numb", ccp.getFullNumber());
                    finalNumber = ccp.getFullNumber();
                    Log.e("Error numb", finalNumber);
                    Log.e("Error numb", edtPhoneNumber.getText().toString());
                    isProgrammaticChange = true;
                    PhoneNumber1.setText("+"+finalNumber);
                    isProgrammaticChange = false;
                    getVouchers();

                    //   }
                       /*else {
                        Toast.makeText(mContext, "number " + ccp.getFullNumber() + " not valid!!!", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });


        btn_non_pay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String amountEnter = amntEditText.getText() + "." + amntEditText_cent.getText().toString();

                if (amountEnter.trim().equals("0.00") || amountEnter.trim().equals("")) {
                    Toasty.error(mContext, "Enter Valid Amount!", 3000, true).show();
                    amntEditText.requestFocus();
                    return;
                }
                amount = Double.parseDouble(amountEnter);
                if (amount < 20) {
                    Toasty.error(mContext, "Amount cannot be less than R 20.00", 3000, true).show();
                    amntEditText.requestFocus();
                    return;
                }
                if (amount > 2001) {
                    Toasty.error(mContext, "Amount cannot be greater than R 2000.00", 3000, true).show();
                    amntEditText.requestFocus();
                    return;
                }


                randamount = "R " + amountEnter;
                //  Toasty.info(mContext, "Rand="+randamount, 8000, true).show();
                //txtAccountNumber.setText(obj.getString("cashms_acc_number"));

                show_multi_voucher(2);
            }
        });
        arrayList = new ArrayList<>();
        arrayList.add(new Item("", "1", R.drawable.prov_ad, "#FFFFFF", ""));
        arrayList.add(new Item("", "1", R.drawable.prov51new, "#FFFFFF", ""));
        HomeAdapterMain adapter = new HomeAdapterMain(activity_ding.this, arrayList, activity_ding.this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
    }

    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_ding.rl_network.setVisibility(View.VISIBLE);
                    activity_ding.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_ding.rl_network.setVisibility(View.INVISIBLE);
                    activity_ding.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_ding.rl_network.setVisibility(View.INVISIBLE);
                    activity_ding.rl_server.setVisibility(View.INVISIBLE);
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

    private void clearfrm() {

        //  btn_next.setVisibility(View.VISIBLE);
        lst_reprint.setVisibility(View.GONE);
        tv_response.setText("");
        rl_amount.setVisibility(View.GONE);
        btn_non_pay.setVisibility(View.GONE);
        tv_value.setVisibility(View.GONE);
        ccp.setCountryForNameCode("ww");
        ccp.setEnabled(true);
        edtPhoneNumber.setVisibility(View.GONE);
        PhoneNumber1.setVisibility(View.VISIBLE);
        PhoneNumber1.setText("");
        edtPhoneNumber.setText("");
        linlayout.setVisibility(View.VISIBLE);
        edtPhoneNumber.setEnabled(true);
        ccp.setClickable(true);
        isocoutrycode.setVisibility(View.GONE);

    }

    private void getVouchers() {

        showCustomDialog("Global Airtime", "Requesting Vouchers Please Wait!!!", false);


        final Call<ResponseBody> call = apiService.ding_voucher(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, finalNumber);

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
                    dialog.dismiss();
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
                    dialog.dismiss();
                    return;

                }


                if (!res.contains("ipayMsg")) {
                    Toasty.info(mContext, "No Vouchers.", 8000, true).show();
                    dialog.dismiss();
                    return;
                }
                JSONObject jsonObj = null;
                try {

                    Log.e("REPRINT RES:", res);
                    Timber.i("REPRINT RES: " + res);

                    jsonObj = XML.toJSONObject(res);
                    Log.e("REPRINT jsonobj:", String.valueOf(jsonObj));
                    JSONObject obj = jsonObj.getJSONObject("ipayMsg");
                    JSONObject obj1 = obj.getJSONObject("cellMsg");
                    JSONObject obj2 = obj1.getJSONObject("custInfoRes");
                    Log.e("REPRINT jsonobj:", obj + "----\n" + obj1 + "-----------\n" + obj2);
                    if (obj2.optJSONObject("networkInfo") == null) {
                        showCustomDialog("Global Airtime", "Invalid Phone Number!!!", true);
                        return;
                    }

                    JSONObject obj3 = obj2.getJSONObject("networkInfo");
                    operator = obj3.getString("network");
                    edtPhoneNumber.setEnabled(false);
                    ccp.setClickable(false);

                    rl_amount.setVisibility(View.GONE);

//tv_response.setText(Html.fromHtml("<h2>Summary</h2><b>Country : </b>"+ccp.getSelectedCountryName()+"<br><b>Number : </b>"+ccp.getFullNumber()+"<br><b>Operator: </b>"+operator));
                    tv_response.setText(Html.fromHtml("<br><b>Operator: </b>" + operator + "<br><<b>Please Choose Voucher</b>"));
//  JSONObject obj5 = obj2.getJSONObject("tokenTypeInfo");
                    dialog.dismiss();

                    /*

                    for non denomintion vouchers

                     */
                    if (obj2.optJSONArray("tokenTypeInfo") == null) {
                        isDenominated = false;
                        JSONObject obj12 = obj2.getJSONObject("tokenTypeInfo");
                        tv_response.setText(Html.fromHtml("<br><b>Operator: </b>" + operator));
                        rl_amount.setVisibility(View.VISIBLE);
                        btn_non_pay.setVisibility(View.VISIBLE);
                        tv_value.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.GONE);
                        String tokenType1 = "\"" + obj12.getString("tokenType");
                        tokenType = obj12.getString("tokenType");
                        String network = "\"" + operator;
                        String currencyCode = "\"" + obj12.getString("currencyCode");
                        String isDenominated = "\"false";

                        //<tokenTypeInfo tokenType="ETB 34.65-4988.98 (BHETET55310)" network="Ethio Telecom Ethiopia - BHET" units="" window="" amt="0" tax="0" available="true" customized="false" popidx="-1" pinless="true" isStock="false" isDenominated="false" amtCalcedAtVend="true" currencyCode="ETB">ETB 34.65-4988.98 (BHETET55310)</tokenTypeInfo
                        tokenTypeInfo = "<tokenTypeInfo tokenType=" + tokenType1 + "\" network=" + network + "\" units=\"\" window=\"\" amt=\"0\" tax=\"0\" available=\"true\" customized=\"false\" popidx=\"-1\" pinless=\"true\" isStock=\"false\" isDenominated=" + isDenominated + "\" amtCalcedAtVend=\"true\" currencyCode=" + currencyCode + "\">" + tokenType + "</tokenTypeInfo>";


                        //Toasty.success(mContext, "Complete!"+obj12.getString("tokenType"), 3000, true).show();

                    } else {
                        isDenominated = true;
                        JSONArray jsonArry = obj2.getJSONArray("tokenTypeInfo");

                        for (int i = 0; i < jsonArry.length(); i++) {
                            MyItem test = new MyItem();

                            JSONObject objin = jsonArry.getJSONObject(i);
                            test.stock_uid = objin.getString("tokenType");
                            String tokeType = objin.getString("tokenType");
                            currency = objin.getString("currencyCode");
                            String[] arr = tokeType.split("\\(");
                            if (arr.length >= 1) {
                                String prodCode = arr[1].replace(")", "");
                                String prodDesc = tokeType.replace(prodCode, "");
                                prodDesc = prodDesc.replace("()", "");
                                test.btn_date = prodCode;
                                test.btn_description = prodDesc;

                            } else {
                                test.btn_date = "-";
                                test.btn_description = objin.getString("tokenType");
                            }
                            test.btn_user = "Voucher #" + (i + 1);
                            movies.add(test);

                        }
                        listAdapter = new ReprintList(mContext, 0, movies);
                        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
                        lst_reprint.setAdapter(listAdapter);
                        lst_reprint.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.GONE);
                        lst_reprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //String xml=XML.toString(item);
                                String tokenType1 = "\"" + movies.get(position).stock_uid;
                                tokenType = movies.get(position).stock_uid;
                                String network = "\"" + operator;
                                String currencyCode = "\"" + currency;
                                String isDenominated = "\"true";


                                tokenTypeInfo = "<tokenTypeInfo tokenType=" + tokenType1 + "\" network=" + network + "\" units=\"\" window=\"\" amt=\"0\" tax=\"0\" available=\"true\" customized=\"false\" popidx=\"-1\" pinless=\"true\" isStock=\"false\" isDenominated=" + isDenominated + "\" amtCalcedAtVend=\"true\" currencyCode=" + currencyCode + "\">" + tokenType + "</tokenTypeInfo>";
                                //   Log.d("token", "" + tokenTypeInfo);
                                //   getExchangeRate(movies.get(position).stock_uid);
                                getExchangeRate();

                            }
                        });

                    }


                } catch (Exception ex) {
                    Toasty.info(mContext, "No Results.2", 8000, true).show();
                }


                dialog.dismiss();

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

    private void getExchangeRate() {
        is_busy_with_voucher = true;
        showCustomDialog("Global Airtime", "Getting estimated price Please Wait!!!", false);
        //  showCustomDialog("Inter","Requesting Exchange Rate", false);
        //XMLBody bd=new XMLBody(body);

        //ResponseBody requestBody = new XMLBody(body);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), tokenTypeInfo);
        final Call<ResponseBody> call = apiService.ding_exchange_estimate_req(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, requestBody);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                //Timber.e(response.body());
                String res = "";


                try {
                    res = response.body().string();

                    randamount = "R " + res;
                    //  Toasty.info(mContext, "Rand="+randamount, 8000, true).show();
                    //txtAccountNumber.setText(obj.getString("cashms_acc_number"));

                    show_multi_voucher(1);

                } catch (Exception ex) {
                    //
                    if (response.body() != null)
                        response.body().close();
                }
                //  Toasty.error(mContext, ""+res, 8000, true).show();
                if (res.contains("<error><err>")) {

                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK


                }


                dialog.dismiss();

                //Toasty.info(mContext, res, 8000, true).show();

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

    private void do_payment() {

        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts = 2;
                showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                return;
            }
        }
        if (!PrinterTopitup.check_paper(mContext)) {
            printerQ1Sts = 1;
            //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
            showCustomDialog("Out of Paper", "Please check paper and try again.", true);
            return;
        }
        showCustomDialog("Requesting", "Please wait...", false);
        is_busy_with_voucher = true;
        String res = "";
        String amount = randamount.substring(2).replace(".", "");
        amount = "\"" + amount + "\"";
        String tokenType1 = "\"" + tokenType;
        String network1 = "\"" + operator;
        String phno = "\"" + ccp.getFullNumber();
        String vendReq;
        if (isDenominated) {
            vendReq = "<reqInfo tokenType=" + tokenType1 + "\" network=" + network1 + "\" payType=\"cash\" phoneNum=" + phno + "\" amt=" + amount + "></reqInfo>";
        } else {
            vendReq = "<reqInfo tokenType=" + tokenType1 + "\" network=" + network1 + "\" payType=\"cash\" phoneNum=" + phno + "\" amt=" + amount + " non_denominated=\"true\"></reqInfo>";

        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), vendReq);
        final Call<ResponseBody> call = apiService.ding_vend_req(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, requestBody);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                //Timber.e(response.body());
                String res = "";

                try {
                    res = response.body().string();
                } catch (IOException e) {
                    if (response.body() != null)
                        response.body().close();
                    e.printStackTrace();
                }

                if (res.contains("<error><err>")) {


                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                    // tv_response.setTextColor( Color.parseColor("#ff0000"));
                    //tv_response.setText(matcher);

                    dialog.dismiss();
                    String[] strParts = matcher.split("\\^");
                    if (strParts.length > 1) {

                        showCustomDialog(strParts[0].toUpperCase(), strParts[2], true);
                        //clearfrm();
                    } else {
                        showCustomDialog("Global Airtime", "Error Please contact Top it up Admin!!!", false);


                    }

                    // Toasty.info(mContext, matcher, 8000, true).show();

                } else if (res.contains("ERR")) {
                    dialog.dismiss();
                    showCustomDialog("Global Airtime", "Error Please contact Top it up Admin!!!", true);
                } else {        //Response OK

                    //printingCustomDialog("Printing", "Your voucher is busy printing.\nYou have been charged, please check reprint if there is an issue.");


                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(res);
                        String amt = obj.getString("real_tx_value");
                        value = Double.parseDouble(amt) / 100;
                        showCustomDialog("Global Airtime", "Payable Amount : R " + value, true);

                        //ccp.getSelectedCountryName();
                        String slip = obj.getString("print_data");
                        slip = slip.replace("1Global Airtime\n" +
                                "1", "1Global Airtime\n" +
                                "1\n1Country : " + ccp.getSelectedCountryName());
                        //Printer.print_data(obj.getString("print_data"));
                        PrinterTopitup.print_data(slip);
                        clearfrm();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                //Toasty.info(mContext, res, 8000, true).show();

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

    public void show_multi_voucher(Integer denominated) {

        //Timber.i("PLUS CLICK 2: " + String.valueOf(service_provider_id));
        stock_uid = "";

        int total_vouchers_to_print = 1;


        dialog_multi = new Dialog(mContext);
        dialog_multi.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_multi.setContentView(R.layout.dialog_voucher_ding);
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


        TextView country = dialog_multi.findViewById(R.id.country);
        TextView amt = dialog_multi.findViewById(R.id.amt);
        TextView phno = dialog_multi.findViewById(R.id.phno);
        TextView voucher = dialog_multi.findViewById(R.id.voucher);
        TextView nt = dialog_multi.findViewById(R.id.network);

        country.setText("Country : " + ccp.getSelectedCountryName());
        if (denominated == 1)
            amt.setText("Estimated Amount : " + randamount + "\n(Actual value may differ.)");
        else
            amt.setText("Payable Amount : " + randamount);


        nt.setText("Network : " + operator);
        phno.setText("Phone Number : " + ccp.getFullNumber());
        voucher.setText("Voucher : " + tokenType);
        pop_title2 = dialog_multi.findViewById(R.id.pop_title);       // REMOVE "TextView" TEXTVIEW TO STOP ERROR


        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);


        txt_last_header = dialog_multi.findViewById(R.id.txt_last_header);
        txt_last_time = dialog_multi.findViewById(R.id.txt_last_time);


        final AppCompatButton bt_close = dialog_multi.findViewById(R.id.bt_close);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_multi.dismiss();
            }
        });

        if (!batteryAlert()) {
            //dialog_multi.dismiss();

            return;
        }

        bt_reprint = dialog_multi.findViewById(R.id.bt_reprint);
        bt_reprint.setVisibility(View.GONE);


        bt_process = dialog_multi.findViewById(R.id.bt_process);
        bt_process.setText("Process");
        pop_title2.setText("Summary");
        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_multi.dismiss();

                do_payment();


            }
        });


        dialog_multi.show();
        dialog_multi.getWindow().setAttributes(lp);

    }

    @Override
    public void onItemClick(Item item) {

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
