package com.za.toptitup.loginlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.ItemSPI;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.WordsConert;

public class activity_spi_test extends BaseActivity implements HomeAdapterServiceProvider.ItemListener, HomeAdapterSocialServiceProvider.ItemListener, View.OnClickListener, HomeAdapterDataServiceProvider.ItemListener {
    int i = 10;
    CountDownTimer mCountDownTimer;
    private static final CountDownTime sCountDownTime = new CountDownTime(16000, 800);

    private RecyclerView recyclerView, recyclerViewSocial, recyclerViewMonthly, recyclerViewHourly, recyclerViewWeekly;
    private ArrayList<ItemSPI> arrayList;
    private ArrayList<ItemSPI> arrayList1;
    BottomNavigationView mBottomNav;
    RealmResults<service_provider_item> service_provider_items;
    service_provider_item_settings service_provider_item_setting;
    TextView textViewheader, textViewcat, textviewTitle, textviewTitleHourly, textviewTitleDaily, textviewTitleWeekly, textViewTitleMonthly;
    String prefix = "Airtime", spiver, is_admin, company_name;
    Context mContext;
    int size;
    Integer service_provider_id;
    Integer item_type;
    String company_addrs, company_addrs1, company_addrs2, print_address = "0";
    TextView txt_last_voucher_info, txt_last_voucher_info1;
    int printerQ1Sts;
    private String[] NAME, BARCODE, DENO, SOCIAL_TYPE, PERIOD_LABEL;
    private int[] SPITEMID;
    private boolean[] visibleCashier, visibleAdmin;
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    Realm realm;
    SharedPreferences settings;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    RealmResults<fin_balance> tiu_fin_balance;
    Dialog dialog, dialog1;
    String voucherInfo;
    String[] voucherArr1, voucherArr2, voucherArr3, voucherArr4;
    Integer arrIncr = 0;
    double item_value_int_mul;
    service_provider_item service_provider_itemv;
    RelativeLayout lnrsp;
    double voucher_cost;
    private int image;
    String desc;
    String amount_deno, amount_text;
    String enable_airtime = "", enable_blue = "", enable_one_for = "", enable_ott = "";
    LinearLayout linSocial;
    Button btnStandard, btnMonthly, btnDaily, btnWeekly, btnSocial, btnHourly, btnAirtime, btnLogo;
    RelativeLayout btnback, btnlogout;
    LinearLayout linType, linSocialbtn;
    public static ImageView txt_battery, img_wifi, img_network;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;

    private TextView txt_cent;
    private TextView txt_rand;
    private String rand_value_entered = "";
    private String cent_value_entered = "";
    private boolean addOneVoucherId = false;
    String strDate;
    GetUpdateAll tiu_settings;
    private String companyName, account_number;
    private String item_desc = "";

    String item_name_input = "";
    String isInput = "";
    Dialog dialog_busy, dialog_net, dialog_nonet_printing;
    TextView txt_clock, txt_account, txt_app_version, txt_user_name, txt_acc_type, txt_account_type, txt_printing, txt_desc, txt_close, type_of;
    RelativeLayout rl_counter;
    ImageView img_reprint;
    Boolean isClose = false;
    private ProgressBar mProgressBar;
    public boolean isDialog = false, isDialogone = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service_provider_id = getIntent().getIntExtra("service_provider_id", 0);
        //  if(service_provider_id==100) //card payment
        //   setContentView(R.layout.activity_pos_cal);
        //  else
        setContentView(R.layout.activity_spi_new_test);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewSocial = findViewById(R.id.recyclerViewSocial);
        recyclerViewMonthly = findViewById(R.id.recyclerViewMonthly);
        recyclerViewHourly = findViewById(R.id.recyclerViewHourly);
        lnrsp = findViewById(R.id.lnrsp);
        textViewheader = findViewById(R.id.textViewheader);
        textviewTitle = findViewById(R.id.txtStandardBundles);
        textviewTitleDaily = findViewById(R.id.txtDailyBundles);
        textviewTitleHourly = findViewById(R.id.txtHourlyBundles);
        textViewTitleMonthly = findViewById(R.id.txtMonthlyBundles);
        mBottomNav = findViewById(R.id.bottom_navigation);
        textViewcat = findViewById(R.id.textViewcat);
        activity_login.fromScreen = "activity_spi_test";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);

        ((Topitup) getApplication()).checkWifiSimInternet(this);

        mBottomNav.getMenu().getItem(1).setChecked(true);
        linSocial = findViewById(R.id.linsocial);
        btnDaily = findViewById(R.id.txDaily);
        btnMonthly = findViewById(R.id.txMonthly);
        btnWeekly = findViewById(R.id.txtWeekly);
        btnStandard = findViewById(R.id.txtStd);
        btnSocial = findViewById(R.id.txSpecial);
        btnAirtime = findViewById(R.id.txtAirtime);
        btnHourly = findViewById(R.id.txHourly);
        btnLogo = findViewById(R.id.bnlogo);
        btnDaily.setOnClickListener(this);
        btnStandard.setOnClickListener(this);
        btnWeekly.setOnClickListener(this);
        btnMonthly.setOnClickListener(this);
        btnHourly.setOnClickListener(this);
        btnAirtime.setOnClickListener(this);
        btnSocial.setOnClickListener(this);
        btnback = findViewById(R.id.idFrameback);
        btnlogout = findViewById(R.id.idFrameLogout);
        btnback.setOnClickListener(this);
        btnlogout.setOnClickListener(this);
        linType = findViewById(R.id.lintype);
        linSocialbtn = findViewById(R.id.linsocial);
        item_type = 0;
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        tiu_settings = realm.where(GetUpdateAll.class).findFirst();

        /* TIU HEADER */
        settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_balance_admin = settings.getString("setting_balance_admin", "0");

        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        final String setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
        final String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
        final String setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");


        enable_airtime = settings.getString("enable_airtime", "0");
        enable_one_for = settings.getString("enable_oneforu", "0");
        enable_blue = settings.getString("enable_blue", "0");
        enable_ott = settings.getString("enable_ott", "0");

        //Toast.makeText(activity_main.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        try {

//            if(tiu_settings.equals(null)){
//                Toasty.error(mContext, "Unable to login", 5000, true).show();
//                return;
//            }else {

            companyName = tiu_settings.company_name;
            account_number = tiu_settings.account_number;
            tiu_title_outlet.setText(tiu_settings.account_number);
           /* if (tiu_settings.company_name.length() > 10) {
                tiu_title_outlet.setText(tiu_settings.company_name.substring(0, 10) + "... | " + tiu_settings.account_number);
            } else {

                tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
            }*/
            if (Topitup.POSUSER_NAME.length() > 13) {
                if (Topitup.IS_ADMIN.equals("1")) {
                    tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");

                    tiu_user_name_.setText("Admin");
                } else {

                    tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12).replaceAll("\\s.*", "") + "... ");
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
            //  }


        } catch (Exception ex) {
            //
        }


        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    is_admin = "0";
                    if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";
                    if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                        Log.e("balance available_balance", "2222222" + tiu_fin_balance.get(0).available_balance);

                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                    }

                    if (setting_balance_cashier_bills.equals("1") && is_admin.equals("1")) {
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                        view2.setVisibility(View.VISIBLE);
                    }
                  /*  if(setting_balance_cashier_commission.equals("1") && is_admin.equals("1")){
                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
                    }

                    if(setting_balance_cashier_swipe.equals("1") && is_admin.equals("1")){
                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
                    }
*/

                    if (setting_balance_admin.equals("1") && is_admin.equals("1")) {

                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

                      /*  if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                    }


                    if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    }
                  /*  if(setting_balance_admin_commission.equals("1") && is_admin.equals("1")){
                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
                    }

                    if(setting_balance_admin_swipe.equals("1") && is_admin.equals("1")){
                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
                    }*/
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
//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }
            if (setting_balance_cashier_bills.equals("1") && is_admin.equals("1")) {
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                view2.setVisibility(View.VISIBLE);
            }
            if (setting_balance_cashier_commission.equals("1") && is_admin.equals("1")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
            }

            if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("1")) {
                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
            }


           /* if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                tiu_title_balance.setText("Standard R " + tiu_fin_balance.get(0).available_balance);

                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }*/


            if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                Log.e("balance available_balance", "111111" + tiu_fin_balance.get(0).available_balance);

                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
                tiu_title_balance.setVisibility(View.VISIBLE);

//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

                      /*  if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
            }


            if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);

            }
            if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + " ");
            }
            if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);
            }
        } catch (Exception ex) {
            //
        }
        /* END HEADER */
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        img_product = dialog.findViewById(R.id.img_product);
        rl_image = dialog.findViewById(R.id.rl_image);
        txt_amount = dialog.findViewById(R.id.txt_amount);

        pop_content = dialog.findViewById(R.id.pop_content);
        bt_close = dialog.findViewById(R.id.bt_close);
        btn_paper_load = dialog.findViewById(R.id.btn_paper_load);
        btn_Paper_ignore_time = dialog.findViewById(R.id.btn_Paper_ignore_time);


        final TextView tiu_clock = findViewById(R.id.tiu_clock);
        //tiu_batt = (TextView) findViewById(R.id.tiu_batt);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
        strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy @ HH:mm");
                strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate);
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);
        if (service_provider_id == 55) {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", 30)
                    .equalTo("item_type", 0)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        } else {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", service_provider_id)
                    .equalTo("item_type", 0)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        }
        size = service_provider_items.size();
        SPITEMID = new int[size];
        BARCODE = new String[size];
        NAME = new String[size];
        DENO = new String[size];
        visibleCashier = new boolean[size];
        visibleAdmin = new boolean[size];
        SOCIAL_TYPE = new String[size];
        PERIOD_LABEL = new String[size];

        if (service_provider_id == 7 || service_provider_id == 12 || service_provider_id == 15 || service_provider_id == 17 || service_provider_id == 20 || service_provider_id == 23 || service_provider_id == 24 || service_provider_id == 25 || service_provider_id == 100 || service_provider_id == 26 || service_provider_id == 27 || service_provider_id == 28 || service_provider_id == 29 || service_provider_id == 30 || service_provider_id == 31 || service_provider_id == 55 || service_provider_id == 39 || service_provider_id == 18) {
            mBottomNav.getMenu().clear(); //clear old inflated items.
            mBottomNav.inflateMenu(R.menu.bottom_nav_admin_back);
            mBottomNav.setSelectedItemId(R.id.action_dumm1);
            mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (service_provider_id == 24 || service_provider_id == 25 || service_provider_id == 26 || service_provider_id == 27 || service_provider_id == 28 || service_provider_id == 29 || service_provider_id == 31 || service_provider_id == 39)
                prefix = "Voucher";
            else if (service_provider_id == 7)
                prefix = "Airtime";
            else if (service_provider_id == 12)
                prefix = "Electricity";
            else
                prefix = "Internet";
        } else
            prefix = "Airtime";
        for (int i = 0; i < size; i++) {
            service_provider_item product = service_provider_items.get(i);
            String prodCat = product.item_desc.replace("Cell C", "");
            prodCat = prodCat.replace("MTN", "");
            prodCat = prodCat.replace("Vodacom", "");
            prodCat = prodCat.replace("Telkom Mobile", "");
            prodCat = prodCat.replace("Virgin", "");
            prodCat = prodCat.replace("EASYLOAD", "");
            prodCat = prodCat.replace("Airtime", "");
            prodCat = prodCat.replace("Siyavula", "");
            prodCat = prodCat.replace("Talk 360 -", "");
            prodCat = prodCat.replace("101Dialer -", "");
            prodCat = prodCat.replace("Virtual UniPIN", "");
            prodCat = prodCat.replace("WorldCall", "");
            prodCat = prodCat.replace("1ForYou", "");
            prodCat = prodCat.replace("OTT", "");
            prodCat = prodCat.replace("Lycamobile", "");
            prodCat = prodCat.replace("Spotify", "");
            prodCat = prodCat.replace("Netflix", "");
            prodCat = prodCat.replace("Uber", "");
            SPITEMID[i] = product.service_provider_item_id;

            if (prodCat.contains("Ikeja")) {

                prefix = "IKEJA";
                prodCat = prodCat.replace("Ikeja", "");
                NAME[i] = prodCat + " " + product.item_btn_desc;
            } else {

                NAME[i] = "R " + String.format("%.0f", product.item_value_int);
            }
            BARCODE[i] = product.item_barcode;
            SOCIAL_TYPE[i] = product.social_type;
            PERIOD_LABEL[i] = product.period_label;

            //checkSettings(product.service_provider_item_id);
            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
            // DENO[i]="R "+String.format("%.0f",product.item_value_int);
            DENO[i] = "";
        }

        // NAME=new String[]{"R 10", "R 20", "R 25", "R 30", "R 60", "R 180", "R 240", "R 520"};
        Log.i("NAMEMANOJ=", "size of the data....tab1......" + NAME.length);

        tabs();
        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/

        //AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        //recyclerView.setLayoutManager(layoutManager);


        /**
         Simple GridLayoutManager that spans two columns
         **/

        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(manager);


        GridLayoutManager manager2 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewMonthly.setMotionEventSplittingEnabled(false);
        recyclerViewMonthly.setLayoutManager(manager2);
        GridLayoutManager manager3 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewSocial.setMotionEventSplittingEnabled(false);
        recyclerViewSocial.setLayoutManager(manager3);
        GridLayoutManager manager4 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewHourly.setMotionEventSplittingEnabled(false);
        recyclerViewHourly.setLayoutManager(manager4);


        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {

                    case R.id.action_airtime:
                        linSocial.setVisibility(View.GONE);

                        if (service_provider_id == 12)
                            prefix = "Electricity";
                        if (service_provider_id == 24 || service_provider_id == 25 || service_provider_id == 39 || service_provider_id == 26 || service_provider_id == 27 || service_provider_id == 28 || service_provider_id == 29)
                            prefix = "Voucher";
                        else
                            prefix = "Airtime";
                        item_type = 0;
                        //textDeno.setBackgroundResource(R.color.white);
                        if (service_provider_id == 55) {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", 30)
                                    .equalTo("item_type", item_type)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        } else {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", service_provider_id)
                                    .equalTo("item_type", item_type)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        }

                        //  Toasty.info( mContext,"service_provider_id="+service_provider_id, Toast.LENGTH_SHORT).show();
                        size = service_provider_items.size();
                        NAME = new String[size];
                        BARCODE = new String[size];
                        DENO = new String[size];
                        SPITEMID = new int[size];
                        visibleCashier = new boolean[size];
                        visibleAdmin = new boolean[size];
                        DENO = new String[size];
                        SOCIAL_TYPE = new String[size];
                        PERIOD_LABEL = new String[size];
                        for (int i = 0; i < size; i++) {
                            service_provider_item product = service_provider_items.get(i);
                            // Toasty.info( mContext,"val="+product.item_desc, Toast.LENGTH_SHORT).show();
                            String prodCat = product.item_desc.replace("Cell C", "");
                            prodCat = prodCat.replace("MT", "");
                            prodCat = prodCat.replace("Vodacom", "");
                            prodCat = prodCat.replace("Talk 360 -", "");
                            prodCat = prodCat.replace("101Dialer -", "");
                            prodCat = prodCat.replace("Virtual UniPIN", "");
                            prodCat = prodCat.replace("Telkom Mobile", "");
                            prodCat = prodCat.replace("Virgin", "");
                            prodCat = prodCat.replace("Spotify", "");
                            prodCat = prodCat.replace("Netflix", "");
                            prodCat = prodCat.replace("Uber", "");
                            prodCat = prodCat.replace("WorldCall", "");
                            prodCat = prodCat.replace("EASYLOAD", "");
                            prodCat = prodCat.replace("Lycamobile", "");
                            DENO[i] = "";
                            if (prodCat.contains("Ikeja")) {

                                prodCat = prodCat.replace("Ikeja", "");
                                NAME[i] = prodCat + " " + product.item_btn_desc;

                            } else {

                                NAME[i] = "R " + String.format("%.0f", product.item_value_int);
                            }
                            SPITEMID[i] = product.service_provider_item_id;
                            BARCODE[i] = product.item_barcode;
                            SOCIAL_TYPE[i] = product.social_type;
                            PERIOD_LABEL[i] = product.period_label;
                            //  DENO[i]="R "+String.format("%.0f",product.item_value_int);

                            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                        }
                        Log.i("NAMEMANOJ=", "size of the data....tab2......" + NAME.length);

                        tabs();
                        return true;

                    case R.id.action_data:
                        //  textViewcat.setText("Data");
                        linSocial.setVisibility(View.GONE);
                        prefix = "Data";
                        item_type = 2;
                        if (service_provider_id == 55) {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", 30)
                                    .equalTo("item_type", item_type)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        } else {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", service_provider_id)
                                    .equalTo("item_type", item_type)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        }
                       *//* service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .equalTo("item_type", item_type)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();*//*
                        size = service_provider_items.size();
                        SPITEMID = new int[size];
                        BARCODE = new String[size];
                        NAME = new String[size];
                        DENO = new String[size];
                        visibleCashier = new boolean[size];
                        visibleAdmin = new boolean[size];
                        SOCIAL_TYPE = new String[size];
                        PERIOD_LABEL = new String[size];
                        for (int i = 0; i < size; i++) {
                            service_provider_item product = service_provider_items.get(i);
                            Toasty.info(mContext, product.social_type + " ,", Toast.LENGTH_SHORT).show();
                            String prodCat = product.item_print_desc.replace("Data", "");
                            prodCat = prodCat.replace("SmartData", "");

                            NAME[i] = prodCat;
                            SPITEMID[i] = product.service_provider_item_id;
                            BARCODE[i] = product.item_barcode;
                            SOCIAL_TYPE[i] = product.social_type;
                            PERIOD_LABEL[i] = product.period_label;
                            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                            DENO[i] = "R " + String.format("%.0f", product.item_value_int);
                        }

                        tabs();
                        return true;
                    case R.id.action_special:
                        linSocial.setVisibility(View.VISIBLE);

                        prefix = "Social+";
                        item_type = 1;
                        if (service_provider_id == 55) {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", 30)
                                    .notEqualTo("item_type", 0)
                                    .notEqualTo("item_type", 2)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        } else {
                            service_provider_items = realm.where(service_provider_item.class)
                                    .equalTo("service_provider_id", service_provider_id)
                                    .notEqualTo("item_type", 0)
                                    .notEqualTo("item_type", 2)
                                    .sort("item_position", Sort.ASCENDING)
                                    .findAll();
                        }


                      *//*  service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .notEqualTo("item_type", 0)
                                .notEqualTo("item_type", 2)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();*//*
                        Log.i("NAMEMANOJ=" + item_type, service_provider_id + "size of the data....tab4......" + service_provider_items.size());

                        size = service_provider_items.size();
                        SPITEMID = new int[size];
                        BARCODE = new String[size];
                        DENO = new String[size];
                        NAME = new String[size];
                        visibleCashier = new boolean[size];
                        visibleAdmin = new boolean[size];
                        SOCIAL_TYPE = new String[size];
                        PERIOD_LABEL = new String[size];
                        for (int i = 0; i < size; i++) {
                            service_provider_item product = service_provider_items.get(i);
                            Log.i("NAMEMANOJ=", "size of the data....tab4......" + product.item_desc);

                            // Toasty.info( mContext,"val="+contact.item_value, Toast.LENGTH_SHORT).show();
                            String prodCat = product.item_desc.replace("Cell C Data", "");
                            prodCat = prodCat.replace("Telkom Mobile", "");
                            prodCat = prodCat.replace("Cell C", "");
                            prodCat = prodCat.replace("MTN", "");
                            prodCat = prodCat.replace("Vodacom", "");
                            prodCat = prodCat.replace("Talk 360 -", "");
                            prodCat = prodCat.replace("101Dialer -", "");
                            prodCat = prodCat.replace("Lycamobile", "");

                            NAME[i] = product.item_print_desc;
                            SPITEMID[i] = product.service_provider_item_id;
                            BARCODE[i] = product.item_barcode;
                            SOCIAL_TYPE[i] = product.social_type;
                            PERIOD_LABEL[i] = product.period_label;
                            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                            DENO[i] = "R " + String.format("%.0f", product.item_value_int);
                        }
                        Log.i("NAMEMANOJ=", "size of the data....tab4......" + NAME.length);

                        tabs();
                        return true;
                    case R.id.action_setting:
                        linSocial.setVisibility(View.GONE);
                        prefix = "D";
                        NAME = new String[]{"3GB R229"};
                        Log.i("NAMEMANOJ=", "size of the data....tab5......" + NAME.length);

                        tabs();


                        return true;
                    case R.id.action_back:
                        linSocial.setVisibility(View.GONE);

                        onBackPressed();
                        return true;
                    default:

                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    linSocial.setVisibility(View.GONE);
                    onBackPressed();
                    return true;

                } else if (item.getItemId() == R.id.action_airtime) {
                    linSocial.setVisibility(View.GONE);

                    if (service_provider_id == 12) prefix = "Electricity";
                    if (service_provider_id == 24 || service_provider_id == 25 || service_provider_id == 39 || service_provider_id == 26 || service_provider_id == 27 || service_provider_id == 28 || service_provider_id == 29) {
                        prefix = "Voucher";
                    } else {
                        prefix = "Airtime";
                    }

                    item_type = 0;
                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id == 55 ? 30 : service_provider_id)
                            .equalTo("item_type", item_type)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();

                    size = service_provider_items.size();
                    NAME = new String[size];
                    BARCODE = new String[size];
                    DENO = new String[size];
                    SPITEMID = new int[size];
                    visibleCashier = new boolean[size];
                    visibleAdmin = new boolean[size];
                    SOCIAL_TYPE = new String[size];
                    PERIOD_LABEL = new String[size];

                    for (int i = 0; i < size; i++) {
                        service_provider_item product = service_provider_items.get(i);
                        String prodCat = product.item_desc.replace("Cell C", "")
                                .replace("MT", "")
                                .replace("Vodacom", "")
                                .replace("Talk 360 -", "")
                                .replace("101Dialer -", "")
                                .replace("Virtual UniPIN", "")
                                .replace("Telkom Mobile", "")
                                .replace("Virgin", "")
                                .replace("Spotify", "")
                                .replace("Netflix", "")
                                .replace("Uber", "")
                                .replace("WorldCall", "")
                                .replace("EASYLOAD", "")
                                .replace("Lycamobile", "");

                        DENO[i] = "";
                        if (prodCat.contains("Ikeja")) {
                            prodCat = prodCat.replace("Ikeja", "");
                            NAME[i] = prodCat + " " + product.item_btn_desc;
                        } else {
                            NAME[i] = "R " + String.format("%.0f", product.item_value_int);
                        }

                        SPITEMID[i] = product.service_provider_item_id;
                        BARCODE[i] = product.item_barcode;
                        SOCIAL_TYPE[i] = product.social_type;
                        PERIOD_LABEL[i] = product.period_label;

                        visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                        visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                    }
                    Log.i("NAMEMANOJ=", "size of the data....tab2......" + NAME.length);
                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_data) {
                    linSocial.setVisibility(View.GONE);
                    prefix = "Data";
                    item_type = 2;

                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id == 55 ? 30 : service_provider_id)
                            .equalTo("item_type", item_type)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();

                    size = service_provider_items.size();
                    SPITEMID = new int[size];
                    BARCODE = new String[size];
                    NAME = new String[size];
                    DENO = new String[size];
                    visibleCashier = new boolean[size];
                    visibleAdmin = new boolean[size];
                    SOCIAL_TYPE = new String[size];
                    PERIOD_LABEL = new String[size];

                    for (int i = 0; i < size; i++) {
                        service_provider_item product = service_provider_items.get(i);
                        Toasty.info(mContext, product.social_type + " ,", Toast.LENGTH_SHORT).show();

                        String prodCat = product.item_print_desc.replace("Data", "")
                                .replace("SmartData", "");
                        NAME[i] = prodCat;
                        SPITEMID[i] = product.service_provider_item_id;
                        BARCODE[i] = product.item_barcode;
                        SOCIAL_TYPE[i] = product.social_type;
                        PERIOD_LABEL[i] = product.period_label;
                        visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                        visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                        DENO[i] = "R " + String.format("%.0f", product.item_value_int);
                    }
                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_special) {
                    linSocial.setVisibility(View.VISIBLE);
                    prefix = "Social+";
                    item_type = 1;

                    service_provider_items = realm.where(service_provider_item.class)
                            .equalTo("service_provider_id", service_provider_id == 55 ? 30 : service_provider_id)
                            .notEqualTo("item_type", 0)
                            .notEqualTo("item_type", 2)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();

                    size = service_provider_items.size();
                    SPITEMID = new int[size];
                    BARCODE = new String[size];
                    DENO = new String[size];
                    NAME = new String[size];
                    visibleCashier = new boolean[size];
                    visibleAdmin = new boolean[size];
                    SOCIAL_TYPE = new String[size];
                    PERIOD_LABEL = new String[size];

                    for (int i = 0; i < size; i++) {
                        service_provider_item product = service_provider_items.get(i);
                        String prodCat = product.item_desc.replace("Cell C Data", "")
                                .replace("Telkom Mobile", "")
                                .replace("Cell C", "")
                                .replace("MTN", "")
                                .replace("Vodacom", "")
                                .replace("Talk 360 -", "")
                                .replace("101Dialer -", "")
                                .replace("Lycamobile", "");

                        NAME[i] = product.item_print_desc;
                        SPITEMID[i] = product.service_provider_item_id;
                        BARCODE[i] = product.item_barcode;
                        SOCIAL_TYPE[i] = product.social_type;
                        PERIOD_LABEL[i] = product.period_label;
                        visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
                        visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
                        DENO[i] = "R " + String.format("%.0f", product.item_value_int);
                    }
                    Log.i("NAMEMANOJ=", "size of the data....tab4......" + NAME.length);
                    tabs();
                    return true;

                } else if (item.getItemId() == R.id.action_setting) {
                    linSocial.setVisibility(View.GONE);
                    prefix = "D";
                    NAME = new String[]{"3GB R229"};
                    Log.i("NAMEMANOJ=", "size of the data....tab5......" + NAME.length);
                    tabs();
                    return true;

                } else {
                    return true;
                }

            }
        });
    }

    private void tabs() {

        arrayList = new ArrayList<>();
        arrayList1 = new ArrayList<>();
        if (service_provider_id == (23)) {
            linSocialbtn.setVisibility(View.GONE);
            btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_ikea));
        }
        if (service_provider_id == 18) {
            btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_101));
            linSocialbtn.setVisibility(View.GONE);
        }
        for (int i = 0; i < NAME.length; i++) {

            Log.i("NAMEMANOJ=", "size of the data.........." + NAME.length);
            //Log.i("DENOMANOJ=",DENO[i]);
            if (service_provider_id.equals(1)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov1));
                linSocialbtn.setVisibility(View.VISIBLE);
//                if (NAME[i].equals("R 2")) {

                if (prefix.equals("Airtime")) {
                    if (enable_airtime.equals("2")) {
                        NAME[0] = "INPUT AMOUNT";
                    }
                }
//                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov1, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            }
            if (service_provider_id.equals(2)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov2));
                linSocialbtn.setVisibility(View.VISIBLE);
//                if (NAME[i].equals("R 2")) {
                if (prefix.equals("Airtime")) {

                    if (enable_airtime.equals("2")) {
                        NAME[0] = "INPUT AMOUNT";

                    }
                }
//                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov2, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));

            } else if (service_provider_id.equals(3)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov3));
                linSocialbtn.setVisibility(View.VISIBLE);
                if (prefix.equals("Airtime")) {
                    if (enable_airtime.equals("2")) {
                        NAME[0] = "INPUT AMOUNT";

                    }
                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov3, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(4)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov4));
                linSocialbtn.setVisibility(View.GONE);
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov4, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));

            } else if (service_provider_id.equals(7)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_worldcall));
                linSocialbtn.setVisibility(View.GONE);
                if (prefix.equals("Airtime")) {

                    /*if (enable_airtime.equals("2")) {
                        NAME[0] = "INPUT AMOUNT";

                    }*/
                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_worldcall, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));

            } else if (service_provider_id.equals(10)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov10));
                linSocialbtn.setVisibility(View.VISIBLE);
                if (prefix.equals("Airtime")) {

                    if (enable_airtime.equals("2")) {
                        NAME[0] = "INPUT AMOUNT";

                    }
                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov10, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(12)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov12_small));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov12_small, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(15)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_siyavula));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_siyavula, "#FFFFFF", "", BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));//siyavula
            } else if (service_provider_id.equals(17)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_talk360));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_talk360, "#FFFFFF", "", BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));//talk 360

            } else if (service_provider_id == 18) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_101));
                linSocialbtn.setVisibility(View.GONE);
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_101, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(19)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_lyca));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_lyca, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(20)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.easyload));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.easyload, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id == (23)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_ikea));
                if (NAME[i].indexOf("Hotspot R5") != -1)
                    arrayList.add(new ItemSPI("", SPITEMID[i], R.drawable.ikejafive, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
                else if (NAME[i].indexOf("Hotspot R30") != -1)
                    arrayList.add(new ItemSPI("", SPITEMID[i], R.drawable.ikejathirty, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
                else if (NAME[i].indexOf("Hotspot R120") != -1)
                    arrayList.add(new ItemSPI("", SPITEMID[i], R.drawable.ikejaonetwozero, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
                else if (NAME[i].indexOf("Prime R350") != -1)
                    arrayList.add(new ItemSPI("", SPITEMID[i], R.drawable.ikejathreefivezero, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
                else if (NAME[i].indexOf("Prime R500") != -1)
                    arrayList.add(new ItemSPI("", SPITEMID[i], R.drawable.ikejafivezero, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
                else
                    arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_blank, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(24)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_oneforyou));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_oneforyou, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(25)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_ott));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_ott, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(39)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_ott));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_ott, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(26)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_spotify));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_spotify, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(27)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_uber));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_uber, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(28)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_netflix));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_netflix, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(29)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_blue));
//                if (NAME[i].equals("R 10")) {
//                if(prefix.equals("Airtime")) {

                if (enable_blue.equals("2")) {

                    NAME[0] = "INPUT AMOUNT";
                }
//                }
//                }
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_blue, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));

            } else if (service_provider_id.equals(30)) {
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_ringas));
                linSocialbtn.setVisibility(View.GONE);
//                if(prefix.equals("Airtime")) {

                if (enable_airtime.equals("2")) {
                    NAME[0] = "INPUT AMOUNT";

                }
//                }

                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_ringas, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(55)) {

//                if(prefix.equals("Airtime")) {

//                service_provider_id=30;
                /*if (enable_airtime.equals("2")) {
                    NAME[0] = "INPUT AMOUNT";

                }*/
//                }
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_air));
                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_air, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            } else if (service_provider_id.equals(31)) {
                linSocialbtn.setVisibility(View.GONE);
                btnLogo.setBackground(getResources().getDrawable(R.drawable.prov_oneforyou));
//                if (NAME[i].equals("R 1")) {
//                if(prefix.equals("Airtime")) {
                if (enable_one_for.equals("2")) {
                    NAME[0] = "INPUT AMOUNT";

                }
//                }

                arrayList.add(new ItemSPI(NAME[i], SPITEMID[i], R.drawable.prov_oneforyou, "#FFFFFF", prefix, BARCODE[i], visibleAdmin[i], visibleCashier[i], DENO[i], SOCIAL_TYPE[i], PERIOD_LABEL[i]));
            }
            // else
            //   arrayList.add(new Item(NAME[i], prefix + i, R.drawable.prov_blank, "#FFFFFF"));

        }
        if (prefix.equals("Social+")) {
            HomeAdapterSocialServiceProvider adapter1 = new HomeAdapterSocialServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerView.setAdapter(adapter1);
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);

        } else if (prefix.equals("Data")) {
            recyclerViewSocial.setVisibility(View.VISIBLE);
            recyclerViewMonthly.setVisibility(View.VISIBLE);
            recyclerViewHourly.setVisibility(View.VISIBLE);
            textviewTitleDaily.setVisibility(View.VISIBLE);
            textViewTitleMonthly.setVisibility(View.VISIBLE);
            textviewTitleHourly.setVisibility(View.VISIBLE);
            textviewTitle.setVisibility(View.VISIBLE);
            HomeAdapterDataServiceProvider homeAdapterDataServiceProvider = new HomeAdapterDataServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerView.setAdapter(homeAdapterDataServiceProvider);
            HomeAdapterDataServiceProvider homeAdapterDataServiceProvider1 = new HomeAdapterDataServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerViewSocial.setAdapter(homeAdapterDataServiceProvider1);
            HomeAdapterDataServiceProvider homeAdapterDataServiceProvider2 = new HomeAdapterDataServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerViewMonthly.setAdapter(homeAdapterDataServiceProvider2);
            HomeAdapterDataServiceProvider homeAdapterDataServiceProvider13 = new HomeAdapterDataServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerViewHourly.setAdapter(homeAdapterDataServiceProvider13);

        } else {
            HomeAdapterServiceProvider adapter = new HomeAdapterServiceProvider(activity_spi_test.this, arrayList, activity_spi_test.this);
            recyclerView.setAdapter(adapter);
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
        }

    }


    @Override
    public void onItemClick(ItemSPI item,Boolean isEnable) {

        if (item == null) {
            return;
        }


        total_vouchers_to_print = 1;
        spi_id = item.pos;
        image = item.drawable;
        spi_barcode = item.item_barcode;
        amount_deno = item.deno;
        if (service_provider_id.equals("39")) {

        } else {
            if (item.text.equals("INPUT AMOUNT")) {
                isInput = "true";
                if (!isDialogone) {
                    isDialogone = true;
                    if (Topitup.checkConnection(getApplicationContext())) {
                        if(!isEnable) {
                            showoneforyoudialog(mContext);
                        }
                    } else {

                        displayNoInternetDialog();
                    }
                }
            } else {

                if (!isDialog) {
                    isInput = "false";
                    isDialog = true;
                    if (Topitup.checkConnection(getApplicationContext())) {
                        amount_deno = item.text + " " + item.deno;
                        show_multi_voucher(item.pos, "", 0);
                    } else {
                        displayNoInternetDialog();
                    }

                }
            }
        }

    }

    private void displayNoInternetDialog() {

        dialog_net = new Dialog(this, R.style.DialogTheme);

        dialog_net.setContentView(R.layout.dialog_no_net);
        dialog_net.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_clock = dialog_net.findViewById(R.id.txt_clock);
        txt_account = dialog_net.findViewById(R.id.txt_account);
        txt_app_version = dialog_net.findViewById(R.id.txt_app_version);
        txt_user_name = dialog_net.findViewById(R.id.txt_user_name);
        txt_acc_type = dialog_net.findViewById(R.id.txt_acc_type);
        txt_account_type = dialog_net.findViewById(R.id.txt_account_type);

        TextView txt_close = dialog_net.findViewById(R.id.txt_close);

        dialog_net.show();


        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialogone = false;

                isDialog = false;
                dialog_net.dismiss();
            }
        });
        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);
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
            } else {
                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            }
        }

        if (companyName.length() > 10) {
            txt_account.setText(companyName.substring(0, 10) + "... ");
        } else {
            txt_account.setText(companyName);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        if (dialog != null)
            dialog.dismiss();
        FullscreenCall();
        super.onResume();
    }


    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title, txt_amount;
    ImageView img_product, img_product1;
    RelativeLayout rl_image;
    TextView pop_content;
    AppCompatButton bt_close;


    private void stopCustomDialog(String sTitle, String sContent) {
        rl_image.setVisibility(View.GONE);

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }


    Integer spi_id = 0;
    String spi_barcode = "";
    Integer total_vouchers_to_print = 1;
    Integer current_voucher = 1;

    public void get_voucher() {


        rl_image.setVisibility(View.GONE);

        if (total_vouchers_to_print > 1) {

            //for (int current_voucher = 1; current_voucher <= total_vouchers_to_print; current_voucher++) {

            pop_title.setText("Requesting Voucher (" + current_voucher + " of " + total_vouchers_to_print + ")");
            do_voucher_request();

            //}

        } else {


            pop_title.setText("Requesting Voucher");
            do_voucher_request();

        }


    }


    private void do_voucher_request() {


        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            if (!PrinterTopitup.checkQ1Printer()) {
                printerQ1Sts = 2;
                showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                return;
            }

            if (!PrinterTopitup.check_paper(mContext)) {
                printerQ1Sts = 1;
                //   showCustomDialog("Printer Issue", "Please try again. After Some time", true);
                showCustomDialog("Out of Paper", "Please check paper and try again.", true);
                return;
            }

        }


        if (service_provider_id == 31) {
            if (voucher_cost == 0) {
                service_provider_item service_provider_item_id = realm.where(service_provider_item.class)
                        .equalTo("service_provider_item_id", spi_id).findFirst();
                voucher_cost = service_provider_item_id.item_value_int * 100;
            }
            requestflashvoucher();
        } else {
            get_voucher_json();
        }


    }

    private void get_voucher_json_amount(int voucher_cost, int Provider_input_id) {
        final Call<voucher_response> call;
        dialog.dismiss();

//        dialog_busy.dismiss();
//        sCountDownTime.start(this);

        if (Topitup.checkConnection(getApplicationContext())) {

            busyPrintingDialog();
        } else {
            noInternetBetweenPrinting();
//            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


        }

       /* if(service_provider_id==31)
            call = apiService.get_voucher_json_flash(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1","", spi_id, "",voucher_cost);
        else*/
        call = apiService.get_voucher_json_(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", Provider_input_id, voucher_cost, "");

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                voucher_response result = response.body();

                if (result.err.length() > 0) {

//                    dialog_busy.dismiss();
                    rl_image.setVisibility(View.VISIBLE);
                    pop_title.setText("Voucher Request Issue");
                    pop_content.setText(result.err);
                    img_product.setImageResource(image);
                    txt_amount.setText(amount_deno);

                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);
                    txt_desc.setVisibility(View.VISIBLE);
                    rl_counter.setVisibility(View.GONE);
                    txt_printing.setText("Voucher Request Issue");
                    txt_desc.setText("Voucher is currently unavailable");
                    type_of.setText(amount_deno);
                    txt_amount.setVisibility(View.GONE);

                    img_reprint.setVisibility(View.GONE);
                    txt_close.setVisibility(View.VISIBLE);
                    isClose = true;

                } else {
                    isClose = false;

                    //Response OK
                    txt_close.setVisibility(View.GONE);
                    txt_desc.setVisibility(View.VISIBLE);
                    rl_counter.setVisibility(View.VISIBLE);
                    img_reprint.setVisibility(View.VISIBLE);

//                    txt_printing.setText("Printing");
                    img_reprint.setVisibility(View.VISIBLE);
                    txt_amount.setVisibility(View.VISIBLE);

                    txt_desc.setText("Your voucher is busy printing.\nIf you have an issue, please check Reprint  ");

//                    Toast.makeText(activity_spi_test.this, "from 2nd call", Toast.LENGTH_LONG).show();
                    String slip = result.print_data;

                    dialog.dismiss();

                    rl_image.setVisibility(View.GONE);


                    bt_close.setVisibility(View.GONE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                    if (print_address.equals("1"))
                        slip = slip.replace("1\n1Trx", "1" + company_addrs1 + ", " + company_addrs2 + "\n1\n1Trx");

                    if (spi_barcode != null && !spi_barcode.equals("")) {
                        slip = slip + "BARCODE:" + spi_barcode;
                    }
                  //  Printer.store_last_reprint(slip);
                    PrinterTopitup.print_data(slip);


                    if (settings.getString("setting_chk_print_ele_copy", "0").equals("1")) {

                        try {

                            String[] tempArr = slip.split("\\n");
                            // Toasty.error(mContext, "arrIncr=" + arrIncr + "tempArr=" + tempArr[2], Toast.LENGTH_LONG).show();
                            if (tempArr.length > 0) {
                                voucherInfo = tempArr[7].substring(1);
                                voucherArr1[arrIncr] = tempArr[2];
                                voucherArr2[arrIncr] = tempArr[3];
                                voucherArr3[arrIncr] = tempArr[4];
                                voucherArr4[arrIncr] = tempArr[5];

                            }
                        } catch (Exception ex) {


                        }
                    }


                  /*  if (current_voucher < total_vouchers_to_print) {
                        current_voucher++;
                        if (settings.getString("setting_chk_print_ele_copy", "0").equals("1"))
                            arrIncr++;
//                        print_handler.postDelayed(print_handler_runnable, 2000);

                    } else {
//                        handler.postDelayed(runnable, 2000);
                    }
*/

                    try {
                        fin_balance fb = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findFirst();

                        realm.beginTransaction();

                        fb.balance = result.balance;

                        fb.available_balance = convertNumber(result.available_balance);
                        fb.balance_cash = convertNumber(result.balance_cash);

                        fb.loyalty = result.loyalty;

                        realm.commitTransaction();
                    } catch (Exception ex) {

                        Timber.e("ERROR SPI 2: " + ex.getMessage());

                    }


                }

                // Toasty.error(mContext, "done", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();
                rl_image.setVisibility(View.GONE);

                if (t instanceof SocketTimeoutException) {

                    pop_title.setText("Connection Issue");
                    pop_content.setText("You could have been charged, Please reprint or view your sales history when your internet connection returns.");

                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                } else {

                    //t.printStackTrace();

                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                }

                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });
    }


    private void get_voucher_json() {
        dialog.dismiss();

//        dialog_busy.dismiss();
//        sCountDownTime.start(this);

        if (Topitup.checkConnection(getApplicationContext())) {

            busyPrintingDialog();
        } else {
            noInternetBetweenPrinting();

//            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


        }
        final Call<voucher_response> call;
        voucher_cost = service_provider_itemv.item_value_int * 100;
        if (service_provider_id == 31)
            call = apiService.get_voucher_json_flash(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", spi_id, "", voucher_cost,stock_uid);
        else
            call = apiService.get_voucher_json(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "1", "", voucher_cost, spi_id, "");

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }


                voucher_response result = response.body();

                //Timber.e(result.toString());

                if (result.err.length() > 0) {

                    Log.e("activity", "airtime error11111" + amount_deno);

//                    dialog_busy.dismiss();
                    //String matcher = StringUtils.substringBetween(result.toString(), "<err>", "</err>");
                    //Toasty.error(mContext, result.err, 8000, true).show();
                    rl_image.setVisibility(View.VISIBLE);
                    pop_title.setText("Voucher Request Issue");
                    pop_content.setText(result.err);
                    img_product.setImageResource(image);
                    txt_amount.setText(amount_deno);

                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);
                    txt_desc.setVisibility(View.GONE);
                    rl_counter.setVisibility(View.GONE);
                    txt_printing.setText("Voucher Request Issue");
                    txt_desc.setText("Voucher is currently unavailable");
                    img_reprint.setVisibility(View.GONE);
                    txt_amount.setVisibility(View.GONE);
                    type_of.setText(amount_deno);
                    txt_close.setVisibility(View.VISIBLE);
                    isClose = true;
                } else {        //Response OK
                    isClose = false;
                    txt_close.setVisibility(View.GONE);
                    txt_amount.setVisibility(View.VISIBLE);

                    txt_desc.setVisibility(View.VISIBLE);
                    rl_counter.setVisibility(View.VISIBLE);
                    txt_printing.setText("Printing #" + current_voucher);
                    txt_desc.setText("Your voucher is busy printing.\nIf you have an issue, please check Reprint  ");

                    img_reprint.setVisibility(View.VISIBLE);
                    rl_image.setVisibility(View.VISIBLE);

//                    pop_title.setText("Printing #" + String.valueOf(current_voucher));
                    service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", spi_id).findFirst();

                    item_desc = service_provider_item_setting.item_desc;
//                    Toast.makeText(activity_spi.this,"from 1 st call",Toast.LENGTH_LONG).show();

                    String slip = result.print_data;


//                    dialog.dismiss();

//                    pop_content.setText("Your voucher is busy printing.\nPlease check reprint if there is an issue.");
                    bt_close.setVisibility(View.GONE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                    if (print_address.equals("1"))
                        slip = slip.replace("1\n1Trx", "1" + company_addrs1 + ", " + company_addrs2 + "\n1\n1Trx");

                    if (spi_barcode != null && !spi_barcode.equals("")) {
                        slip = slip + "BARCODE:" + spi_barcode;
                    }

                   // Printer.store_last_reprint(slip);

                    //  Timber.i(slip);


                    PrinterTopitup.print_data(slip);

                    if (settings.getString("setting_chk_print_ele_copy", "0").equals("1")) {

                        try {

                            String[] tempArr = slip.split("\\n");
                            // Toasty.error(mContext, "arrIncr=" + arrIncr + "tempArr=" + tempArr[2], Toast.LENGTH_LONG).show();
                            if (tempArr.length > 0) {
                                voucherInfo = tempArr[7].substring(1);
                                voucherArr1[arrIncr] = tempArr[2];
                                voucherArr2[arrIncr] = tempArr[3];
                                voucherArr3[arrIncr] = tempArr[4];
                                voucherArr4[arrIncr] = tempArr[5];

                            }
                        } catch (Exception ex) {


                        }
                    }


                   /* if (current_voucher < total_vouchers_to_print) {
                        current_voucher++;
                        if (settings.getString("setting_chk_print_ele_copy", "0").equals("1"))
                            arrIncr++;
//                        print_handler.postDelayed(print_handler_runnable, 2000);
                        handler.postDelayed(runnable, 2000);


                    } else {
//                        print_handler.postDelayed(print_handler_runnable, 1000);

                        handler.postDelayed(runnable, 2000);
                    }
*/

                    try {
                        fin_balance fb = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findFirst();

                        realm.beginTransaction();

                        fb.balance = result.balance;

                        fb.available_balance = convertNumber(result.available_balance);
                        fb.balance_cash = convertNumber(result.balance_cash);


//                        fb.balance_cash = result.balance_cash;
//                        fb.commission = result.commission;
//                        fb.swipe = result.swipe;
                        fb.loyalty = result.loyalty;

                        realm.commitTransaction();
                    } catch (Exception ex) {

                        Timber.e("ERROR SPI 2: " + ex.getMessage());

                    }


                }

                // Toasty.error(mContext, "done", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 5000, true).show();
                rl_image.setVisibility(View.GONE);

                if (t instanceof SocketTimeoutException) {

                    pop_title.setText("Connection Issue");
                    pop_content.setText("You could have been charged, Please reprint or view your sales history when your internet connection returns.");

                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                } else {

                    //t.printStackTrace();

                    pop_title.setText("Error");
                    pop_content.setText("Voucher currently not available.");
                    bt_close.setVisibility(View.VISIBLE);
                    pageLoadingWrapper.setVisibility(View.GONE);

                }

                //Timber.e(t.getMessage());
                //t.printStackTrace();
            }

        });
    }


    public String convertNumber(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String formatted = formatter.format(amount);
        return formatted;
    }

    private void get_last_voucher_info() {

        Call<ResponseBody> call = apiService.get_last_voucher_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {


                    String res = "";
                    try {
                        res = response.body().string();
                    } catch (Exception ex) {
                        if (response.body() != null)
                        response.body().close();
                    }


                    //Timber.e("VOUCHER " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {
                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();
                    } else {

                        String json = res;

                        try {

                            JSONObject obj = new JSONObject(json);

                            //Log.d("My App", obj.toString());
                            //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                            if (obj.getString("result").equals("-1")) {

                                txt_last_header.setVisibility(View.GONE);
                                txt_last_time.setVisibility(View.GONE);
                                txt_last_voucher_info.setVisibility(View.GONE);

                                bt_reprint.setVisibility(View.GONE);

                            } else {

//                                if (Integer.parseInt(obj.getString("time_diff")) < 30) {
//                                    Toasty.error(mContext, "Voucher sold " + obj.getString("time_diff") + " seconds ago", 9000, true).show();
//                                }

                                stock_uid = obj.getString("stock_uid");
                                String[] arr = obj.getString("message").split("\\n");
                                txt_last_header.setText(obj.getString("last_header"));
                                txt_last_time.setText(obj.getString("last_time"));
                                txt_last_voucher_info.setText(arr[0] + "\n" + "  " + arr[1]);
                                txt_last_voucher_info1.setText(arr[2]);
                                final String chk_last_vou;
                                if (Topitup.IS_ADMIN.equals("1")) {
                                    chk_last_vou = settings.getString("setting_chk_last_vou_admin", "0");

                                } else {

                                    chk_last_vou = settings.getString("setting_chk_last_vou_cashier", "0");
                                }

                                if (chk_last_vou.equals("1"))
                                    bt_reprint.setVisibility(View.VISIBLE);
                                else
                                    bt_reprint.setVisibility(View.GONE);

                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        bt_process.setEnabled(true);

                    }

                } catch (Exception ex) {

                    Toasty.error(mContext, "ERR : " + ex.getMessage(), 3000, true).show();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }
        });

    }


    public void doReprintLogic(String stock_uid) {

        final Call<voucher_response> call = apiService.airtime_reprint(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, stock_uid, Topitup.DEVICE_TYPE);

        call.enqueue(new Callback<voucher_response>() {

            @Override
            public void onResponse(Call<voucher_response> call, Response<voucher_response> response) {


                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                voucher_response result = response.body();

                //Timber.e(result.toString());

                if (result.err.length() > 0) {

                    //

                } else {        //Response OK

                    //Printer.store_last_reprint(result.print_data);

                    PrinterTopitup.print_data(result.print_data);

                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<voucher_response> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }

        });


    }


    Dialog dialog_multi;

    String stock_uid = "";
    AppCompatButton bt_process;
    AppCompatButton bt_reprint;


    TextView txt_last_header;
    TextView txt_last_time;

    TextView pop_title2;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;

    public void show_multi_voucher(Integer service_provider_id, String isFrom, int Provider_input_id) {

        stock_uid = "";

        total_vouchers_to_print = 1;
        int sprovider_id;
//        if (service_provider_id == 55) {
//            spi_id = 30;
//
//            service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", 30).findFirst();
//             sprovider_id = service_provider_itemv.service_provider_id;
//        }else{
//            spi_id = service_provider_id;
//
//            service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi_id).findFirst();
//             sprovider_id = service_provider_itemv.service_provider_id;
//        }

        spi_id = service_provider_id;

        service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi_id).findFirst();
        sprovider_id = service_provider_itemv.service_provider_id;


        dialog_multi = new Dialog(mContext);
        dialog_multi.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog_multi.setContentView(R.layout.dialog_multi_voucher);
        dialog_multi.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_multi.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        textView2 = dialog_multi.findViewById(R.id.textView2);
        textView3 = dialog_multi.findViewById(R.id.textView3);
        textView4 = dialog_multi.findViewById(R.id.textView4);
        textView5 = dialog_multi.findViewById(R.id.textView5);
        textView6 = dialog_multi.findViewById(R.id.textView6);
        textView7 = dialog_multi.findViewById(R.id.textView7);
        textView8 = dialog_multi.findViewById(R.id.textView8);
        textView9 = dialog_multi.findViewById(R.id.textView9);
        img_product1 = dialog_multi.findViewById(R.id.img_product1);
        img_product1.setImageResource(image);

        pop_title2 = dialog_multi.findViewById(R.id.pop_title);       // REMOVE "TextView" TEXTVIEW TO STOP ERROR


        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);
        if (settings.getString("setting_print_to_screen", "0").equals("1") || sprovider_id == 31 || isFrom.equals("input")) {

            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
            textView5.setVisibility(View.GONE);
            textView6.setVisibility(View.GONE);
            textView7.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
            textView9.setVisibility(View.GONE);

        }

        txt_last_header = dialog_multi.findViewById(R.id.txt_last_header);
        txt_last_time = dialog_multi.findViewById(R.id.txt_last_time);
        txt_last_voucher_info = dialog_multi.findViewById(R.id.txt_last_voucher_info);
        txt_last_voucher_info1 = dialog_multi.findViewById(R.id.txt_last_voucher_info1);
        final AppCompatButton bt_close = dialog_multi.findViewById(R.id.bt_close);


        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullscreenCall();
                voucher_cost = 0;
                dialog_multi.dismiss();
                isDialog = false;
            }
        });

        if (!batteryAlert()) {
            //dialog_multi.dismiss();

            return;
        }

        bt_reprint = dialog_multi.findViewById(R.id.bt_reprint);
        bt_reprint.setVisibility(View.GONE);
        bt_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialog = false;

                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
                dialog_multi.dismiss();

            }
        });


        bt_process = dialog_multi.findViewById(R.id.bt_process);
        if (sprovider_id == 31 || isFrom.equals("input")) {

            addOneVoucherId = !isFrom.equals("input");
//        if(sprovider_id==31 || sprovider_id==1){
            if (voucher_cost == 0) {
                service_provider_item service_provider_item_id = realm.where(service_provider_item.class)
                        .equalTo("service_provider_item_id", spi_id).findFirst();
                voucher_cost = service_provider_item_id.item_value_int * 100;
            }

            bt_process.setText("Process");
            DecimalFormat formatter = new DecimalFormat("#0.00");
            pop_title2.setText("Confirm R " + formatter.format(voucher_cost / 100));
        } else {
            bt_process.setText("Process x" + total_vouchers_to_print);
            pop_title2.setText("Select Quantity (x" + total_vouchers_to_print + ")");
        }


        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialog = false;

                showCustomDialog("Requesting", "Please wait...", false);

                if (isFrom.equals("input")) {
                    if (sprovider_id == 31) {
                        get_voucher();

                    } else {
                        callBlueLabelApi((int) voucher_cost, Provider_input_id, 0);
                    }

                } else if (enable_ott.equals("4")) {
                    service_provider_item service_provider_item_id = realm.where(service_provider_item.class)
                            .equalTo("service_provider_item_id", spi_id).findFirst();
                    voucher_cost = service_provider_item_id.item_value_int * 100;
                    callBlueLabelApi((int) voucher_cost, spi_id, 1);

                } else {

                    service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi_id).findFirst();
                    item_value_int_mul = service_provider_itemv.item_value_int;

                    if (settings.getString("setting_chk_print_ele_copy", "0").equals("1")) {
                        voucherArr1 = new String[total_vouchers_to_print];
                        voucherArr2 = new String[total_vouchers_to_print];
                        voucherArr3 = new String[total_vouchers_to_print];
                        voucherArr4 = new String[total_vouchers_to_print];
                    }
                    showCustomDialog("Requesting", "Please wait...", false);
                    get_voucher();

                    FullscreenCall();

                }
                dialog_multi.dismiss();
            }
        });


        dialog_multi.show();
        dialog_multi.getWindow().setAttributes(lp);

        bt_process.setEnabled(false);
        showCustomDialog("Fetching Last Voucher", "Please wait...", false);

        get_last_voucher_info();

    }


    private void callBlueLabelApi(int voucher_cost, int Provider_input_id, int voucherType) {

        final Call<ResponseBody> call;

        call = apiService.get_voucher(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, voucher_cost, Provider_input_id, voucherType);

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
                    try {
                        JSONObject obj = new JSONObject(res);
                        if (obj.getInt("stock_uid") > 0) {
                            get_voucher_json_amount(voucher_cost, Provider_input_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stopCustomDialog("Problem", t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });
    }


    public void click_spi_multi(View view) {

        int total_vouchers = 1;
//        view.setBackgroundColor(getColor(R.color.color_blue));

        if (view.getTag().equals("2")) {

            changeBackground("2");

            total_vouchers = 2;
        }
        if (view.getTag().equals("3")) {

            changeBackground("3");

            total_vouchers = 3;
        }
        if (view.getTag().equals("4")) {
            changeBackground("4");

            total_vouchers = 4;
        }
        if (view.getTag().equals("5")) {
            changeBackground("5");

            total_vouchers = 5;
        }
        if (view.getTag().equals("6")) {
            changeBackground("6");

            total_vouchers = 6;
        }
        if (view.getTag().equals("7")) {
            changeBackground("7");
            total_vouchers = 7;
        }
        if (view.getTag().equals("8")) {

            changeBackground("8");
            total_vouchers = 8;
        }
        if (view.getTag().equals("9")) {

            changeBackground("9");
            total_vouchers = 9;
        }

        total_vouchers_to_print = total_vouchers;

        pop_title2.setText("Select Quantity (x" + total_vouchers_to_print + ")");
        bt_process.setText("Process x" + total_vouchers_to_print);

    }


    public void changeBackground(String view) {

        if (view.equals("2")) {

            textView2.setBackground(getDrawable(R.drawable.view_bg_red));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.white));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));

        } else if (view.equals("3")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.view_bg_red));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.white));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));
        } else if (view.equals("4")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.view_bg_red));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.white));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));
        } else if (view.equals("5")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.view_bg_red));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.white));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));
        } else if (view.equals("6")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.view_bg_red));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.white));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));

        } else if (view.equals("7")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.view_bg_red));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.white));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.black));

        } else if (view.equals("8")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.view_bg_red));
            textView9.setBackground(getDrawable(R.drawable.btn_spi_multi));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.white));
            textView9.setTextColor(getColor(R.color.black));

        } else if (view.equals("9")) {
            textView2.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView3.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView4.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView5.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView6.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView7.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView8.setBackground(getDrawable(R.drawable.btn_spi_multi));
            textView9.setBackground(getDrawable(R.drawable.view_bg_red));

            textView2.setTextColor(getColor(R.color.black));
            textView3.setTextColor(getColor(R.color.black));
            textView4.setTextColor(getColor(R.color.black));
            textView5.setTextColor(getColor(R.color.black));
            textView6.setTextColor(getColor(R.color.black));
            textView7.setTextColor(getColor(R.color.black));
            textView8.setTextColor(getColor(R.color.black));
            textView9.setTextColor(getColor(R.color.white));
        }

    }

    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;

    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

        if (sTitle.contains("Requesting")) {
            rl_image.setVisibility(View.VISIBLE);

            img_product.setImageResource(image);
            txt_amount.setText(amount_deno);
        } else {
            rl_image.setVisibility(View.GONE);
        }


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
                FullscreenCall();
                dialog.dismiss();
            }
        });

        btn_paper_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topitup.getInstance().printerInit();
                dialog.dismiss();
                get_voucher();
            }
        });

        btn_Paper_ignore_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FullscreenCall();
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

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
                       /* if (dialog_busy != null && dialog_busy.isShowing()) {
                            dialog_busy.dismiss();
                        }*/


                        logout();
                    }
                }, 10);
            } else {

                if (settings.getString("setting_chk_print_ele_copy", "0").equals("1")) {

                    do_show_dlg_conv();
                    return;

                }


                finish();
            }
        }
    };

    // run after some seconds to wait for printing
    Handler print_handler = new Handler();
    Runnable print_handler_runnable = new Runnable() {
        @Override
        public void run() {
            get_voucher();
        }
    };


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


    public boolean checkSettings(int service_provider_item_id, int admin) {
        //   try {
        service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", service_provider_item_id).findFirst();
        if (admin == 1) {

            //Toasty.error( mContext,"Product is Disabled under Customised Settings!!!", 25000).show();
            return service_provider_item_setting.isVisible_admin();

        } else {

            //  Toasty.error( mContext,"Product is Disabled By Your Admin!!!", 25000).show();
            return service_provider_item_setting.isVisible_cashier();

        }
        //  } catch (Exception ex) {

        //  Toasty.error( mContext,"Product is Disabled By Your Admin!!!", 25000).show();
        //  return false;
        // }
    }


    private void do_show_dlg_conv() {


        final Dialog dialog1 = new Dialog(this, R.style.DialogTheme);

        dialog1.setContentView(R.layout.dialog_merchant_copy);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn_close = dialog1.findViewById(R.id.btn_close);
        Button btn_save = dialog1.findViewById(R.id.btn_save);


        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                dialog1.dismiss();
                FullscreenCall();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String slip = "1Merchant Copy\n1\n1" + company_name + "\n1\n2" + voucherInfo + "\n\n";


                if (voucherArr1.length > 1) {

                    for (int k = 0; k < voucherArr1.length; k++) {

                        slip += "1Voucher # " + (k + 1) + "\n" + voucherArr1[k] + "\n" + voucherArr2[k] + "\n" + voucherArr3[k] + "\n" + voucherArr4[k] + "\n\n";
                    }
                } else {

                    slip += voucherArr1[0] + "\n" + voucherArr2[0] + "\n" + voucherArr3[0] + "\n" + voucherArr4[0] + "\n\n";

                }

                slip += "\n1Total Qty " + "          Total Amount";
                slip += "\n1" + voucherArr1.length + "                      R" + (item_value_int_mul * (voucherArr1.length)) + "\n\n1Top it Up" + "\n" + "1www.itopitup.co.za" + "\n\n";

                if (spi_barcode != null && !spi_barcode.equals("")) {
                    slip = slip + "BARCODE:" + spi_barcode;
                }
                // Toasty.error(mContext, voucherArr1[0]+"1="+voucherArr1[1], Toast.LENGTH_LONG).show();
                PrinterTopitup.print_data(slip);
                dialog1.dismiss();
                FullscreenCall();
                finish();
            }
        });


        dialog1.show();


    }

    private void busyPrintingDialog() {


        dialog_busy = new Dialog(this, R.style.DialogTheme);

        dialog_busy.setContentView(R.layout.busy_printing);
        dialog_busy.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_clock = dialog_busy.findViewById(R.id.txt_clock);
        txt_account = dialog_busy.findViewById(R.id.txt_account);
        txt_app_version = dialog_busy.findViewById(R.id.txt_app_version);
        txt_user_name = dialog_busy.findViewById(R.id.txt_user_name);
        txt_acc_type = dialog_busy.findViewById(R.id.txt_acc_type);
        txt_account_type = dialog_busy.findViewById(R.id.txt_account_type);
        rl_counter = dialog_busy.findViewById(R.id.rl_counter);
        txt_printing = dialog_busy.findViewById(R.id.txt_printing);
        txt_desc = dialog_busy.findViewById(R.id.txt_desc);
        txt_close = dialog_busy.findViewById(R.id.txt_close);
        img_reprint = dialog_busy.findViewById(R.id.img_reprint);
        TextView txt_count = dialog_busy.findViewById(R.id.txt_count);

        type_of = dialog_busy.findViewById(R.id.type_of);

        TextView txt_printing = dialog_busy.findViewById(R.id.txt_printing);

        ImageView img_product_busy = dialog_busy.findViewById(R.id.img_product);
        TextView txt_product_name = dialog_busy.findViewById(R.id.txt_product_name);
        TextView txt_amount_busy = dialog_busy.findViewById(R.id.txt_amount);
        mProgressBar = dialog_busy.findViewById(R.id.progressBar);

        dialog_busy.show();


        mProgressBar.setProgress(i);

        mCountDownTimer = new CountDownTimer(8000, 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                i--;
                if (i == 0) {
                    dialog_busy.dismiss();

                    if (current_voucher < total_vouchers_to_print) {
                        if (Topitup.checkConnection(getApplicationContext())) {

                            current_voucher++;
                            if (settings.getString("setting_chk_print_ele_copy", "0").equals("1"))
                                arrIncr++;
                            mCountDownTimer.cancel();
                            i = 10;
                            print_handler.postDelayed(print_handler_runnable, 1000);
                        } else {
                            mCountDownTimer.cancel();
                            i = 10;
                            noInternetBetweenPrinting();
                        }
                    } else {
                        i = 0;

                        if (Topitup.checkConnection(getApplicationContext())) {
                            handler.postDelayed(runnable, 1000);
                        } else {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        }
                    }

                } else {
//                    dialog_busy.dismiss();

                }
                mProgressBar.setProgress(i * 100 / (10000 / 600));
                txt_count.setText(String.valueOf(i));
            }

            @Override
            public void onFinish() {
                //Do what you want
                i--;
                mProgressBar.setProgress(100);
            }
        };
        mCountDownTimer.start();

        if (isClose) {
            mCountDownTimer.cancel();
        }

        img_product_busy.setImageResource(image);

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                mCountDownTimer.cancel();
            }
        });
        /*img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
            }
        });*/
        img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 10;
                mCountDownTimer.cancel();
                if (Topitup.checkConnection(getApplicationContext())) {
                    dialog_busy.dismiss();

                    txt_printing.setText("Reprinting ....");

                    busyPrintingDialog();
                    doReprintLogic(stock_uid);
                    FullscreenCall();
                } else {

                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


                }
                //                showCustomDialog("Reprinting", "Please wait...", false);

            }
        });

        String foo = item_desc;
        Log.e("item type", "printing........." + item_desc);
        if (!isInput.equals("true")) {
            String[] parts = foo.split(" ", 2);
            String product_item = "";

            if (parts[0].equalsIgnoreCase("cell")) {
                product_item = "Cell C";
            } else if (parts[0].equalsIgnoreCase("blue")) {
                product_item = "Blu Voucher";
            } else {
                product_item = parts[0];
            }

            type_of.setText(item_desc);
            txt_product_name.setText(product_item);
        } else {
            type_of.setText(item_name_input + " " + amount_deno);
            txt_product_name.setText(item_name_input);
        }

        txt_amount_busy.setText(amount_deno);

        txt_printing.setText("Printing #" + current_voucher + " of " + total_vouchers_to_print);

        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);
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

            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            }
        }

        if (companyName.length() > 10) {
            txt_account.setText(companyName.substring(0, 10) + "... ");
        } else {

            txt_account.setText(companyName);
        }

    }

    private void noInternetBetweenPrinting() {
        dialog_nonet_printing = new Dialog(this, R.style.DialogTheme);

        dialog_nonet_printing.setContentView(R.layout.busy_no_net_printing);
        dialog_nonet_printing.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_clock = dialog_nonet_printing.findViewById(R.id.txt_clock);
        txt_account = dialog_nonet_printing.findViewById(R.id.txt_account);
        txt_app_version = dialog_nonet_printing.findViewById(R.id.txt_app_version);
        txt_user_name = dialog_nonet_printing.findViewById(R.id.txt_user_name);
        txt_acc_type = dialog_nonet_printing.findViewById(R.id.txt_acc_type);
        txt_account_type = dialog_nonet_printing.findViewById(R.id.txt_account_type);
        rl_counter = dialog_nonet_printing.findViewById(R.id.rl_counter);
        txt_printing = dialog_nonet_printing.findViewById(R.id.txt_printing);
        txt_desc = dialog_nonet_printing.findViewById(R.id.txt_desc);
        txt_close = dialog_nonet_printing.findViewById(R.id.txt_close);
        img_reprint = dialog_nonet_printing.findViewById(R.id.img_reprint);
        TextView txt_count = dialog_nonet_printing.findViewById(R.id.txt_count);

        type_of = dialog_nonet_printing.findViewById(R.id.type_of);

        TextView txt_printing = dialog_nonet_printing.findViewById(R.id.txt_printing);

        ImageView img_product_busy = dialog_nonet_printing.findViewById(R.id.img_product);
        TextView txt_product_name = dialog_nonet_printing.findViewById(R.id.txt_product_name);
        TextView txt_amount_busy = dialog_nonet_printing.findViewById(R.id.txt_amount);
        dialog_nonet_printing.show();
        TextView txt_close = dialog_nonet_printing.findViewById(R.id.txt_close);


        img_product_busy.setImageResource(image);

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_nonet_printing.dismiss();
            }
        });
        /*img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_busy.dismiss();
                showCustomDialog("Reprinting", "Please wait...", false);
                doReprintLogic(stock_uid);
                FullscreenCall();
            }
        });*/
        img_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = 10;
                if (Topitup.checkConnection(getApplicationContext())) {
                    dialog_nonet_printing.dismiss();

                    txt_printing.setText("Reprinting ....");

                    busyPrintingDialog();
                    doReprintLogic(stock_uid);
                    FullscreenCall();
                } else {


                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();


                }
                //                showCustomDialog("Reprinting", "Please wait...", false);

            }
        });

        String foo = item_desc;
        Log.e("item type", "printing........." + item_desc);
        if (!isInput.equals("true")) {
            String[] parts = foo.split(" ", 2);
            String product_item = "";

            if (parts[0].equalsIgnoreCase("cell")) {
                product_item = "Cell C";
            } else if (parts[0].equalsIgnoreCase("blue")) {
                product_item = "Blu Voucher";
            } else {
                product_item = parts[0];
            }

            type_of.setText(item_desc);
            txt_product_name.setText(product_item);
        } else {
            type_of.setText(item_name_input + " " + amount_deno);
            txt_product_name.setText(item_name_input);
        }

        txt_amount_busy.setText(amount_deno);

        txt_printing.setText("Printing #" + current_voucher + " of " + total_vouchers_to_print);

        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);
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

            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            }
        }

        if (companyName.length() > 10) {
            txt_account.setText(companyName.substring(0, 10) + "... ");
        } else {

            txt_account.setText(companyName);
        }

    }

    private void requestflashvoucher() {


        final Call<ResponseBody> call;

        if (addOneVoucherId) {
            spi_id = 454;
        }
        call = apiService.get_flash_voucher(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, voucher_cost, spi_id);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                try {
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

                    } else {        //Response OK
                        try {
                            if (res.contains("stock_uid")) {
                                JSONObject obj = new JSONObject(res);
                                if (obj.getInt("stock_uid") > 0) {
                                    stock_uid = obj.getString("stock_uid");

                                    get_voucher_json();
                                }
                            } else {
                                Toasty.error(mContext, "Product is Offline.", 8000, true).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    stopCustomDialog("", "");

                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stopCustomDialog("Problem", t.getMessage());
            }
        });

    }

    private void showoneforyoudialog(Context c) {

        LayoutInflater inflater = LayoutInflater.from(activity_spi_test.this);
        View subView = inflater.inflate(R.layout.flash_dialog_layout, null);
        final EditText amntEditText = subView.findViewById(R.id.dialogEditText);
        EditText amntEditText_cent = subView.findViewById(R.id.dialogEditText_cent);
        final ImageView subImageView = subView.findViewById(R.id.image);
        txt_rand = subView.findViewById(R.id.txt_rand);
        txt_cent = subView.findViewById(R.id.txt_cent);
        Drawable drawable = null;
        int providerIdInput = 0;
        cent_value_entered = "";
        rand_value_entered = "";
        AlertDialog dialogamount = new AlertDialog.Builder(c)
                .setView(subView)
                .create();
        /*
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);*/
        dialogamount.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        dialogamount.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialogamount.setCancelable(false);
        amntEditText.requestFocus();/*
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(amntEditText, InputMethodManager.SHOW_IMPLICIT);*/
        amntEditText.addTextChangedListener(new MoneyTextWatcherRand(amntEditText));
        amntEditText_cent.addTextChangedListener(new MoneyTextWatcherCent(amntEditText_cent));


        switch (service_provider_id) {

            case 1:
                providerIdInput = 485;
                item_name_input = "Vodacom";
                drawable = getResources().getDrawable(R.drawable.prov1);
                break;

            case 3:
                providerIdInput = 487;
                item_name_input = "Cell C";

                drawable = getResources().getDrawable(R.drawable.prov3);
                break;
            case 10:
                providerIdInput = 488;
                item_name_input = "Telkom";

                drawable = getResources().getDrawable(R.drawable.prov10);

                break;
            case 29:
                providerIdInput = 490;
                item_name_input = "Blue Voucher";

                drawable = getResources().getDrawable(R.drawable.prov_bluvoucher);

                break;
            case 19:
                providerIdInput = 489;
                item_name_input = "Lyca";

                drawable = getResources().getDrawable(R.drawable.prov_lyca);
                break;
            case 30:
                providerIdInput = 491;
                item_name_input = "Ringas";

                drawable = getResources().getDrawable(R.drawable.prov_ringas);
                break;

            case 55:
                providerIdInput = 491;
                item_name_input = "Easy Airtime";

                drawable = getResources().getDrawable(R.drawable.prov_air);
                break;
            case 2:
                providerIdInput = 486;
                item_name_input = "MTN";

                drawable = getResources().getDrawable(R.drawable.prov2);
                break;
        }
        subImageView.setImageDrawable(drawable);


        dialogamount.show();
        final AppCompatButton bt_close = dialogamount.findViewById(R.id.bt_close);
        final AppCompatButton bt_process = dialogamount.findViewById(R.id.bt_process);
        final AppCompatButton bt_clear = dialogamount.findViewById(R.id.bt_clear);


        int finalProviderIdInput = providerIdInput;

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amntEditText.setText("");
                amntEditText_cent.setText("");
                txt_rand.setText("");
            }
        });


        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                isDialogone = false;

                String cent = "";
                if (amntEditText_cent.length() > 0) {
                    int numberFromCent = Integer.parseInt(amntEditText_cent.getText().toString());
                    cent = String.format("%02d", numberFromCent);
                }
                String amountEnter;

                if (!cent.equals("")) {
                    amountEnter = amntEditText.getText() + "." + cent;
                } else {
                    amountEnter = amntEditText.getText().toString();

                }

                Log.e("amount ", "entered......" + amountEnter);
                if (!amountEnter.trim().equalsIgnoreCase("")) {

                    if (Double.parseDouble(amountEnter) < 2) {
                        Toasty.error(mContext, "amount should be greater than 2", 8000, true).show();
                        return;
                    }
                } else {

                    Toasty.error(mContext, "amount should be greater than 2", 8000, true).show();
                    return;

                }

                voucher_cost = Double.parseDouble(amountEnter) * 100;
                service_provider_itemv = realm.where(service_provider_item.class).equalTo("service_provider_item_id", spi_id).findFirst();
                int sprovider_id = service_provider_itemv.service_provider_id;

                if (sprovider_id == 31) {
                    if (Double.parseDouble(amountEnter) >= Topitup.ONE_MAX_THRESHOLD) {
                        dialogamount.dismiss();

                        showAmountRestrictDialog(amountEnter, finalProviderIdInput, spi_id, true, Topitup.ONE_MAX_THRESHOLD);
                    } else if (Double.parseDouble(amountEnter) >= Topitup.ONE_WARNING_THRESHOLD) {
                        dialogamount.dismiss();

                        showAmountRestrictDialog(amountEnter, finalProviderIdInput, spi_id, false, Topitup.ONE_MAX_THRESHOLD);
                    } else {
                        amount_deno = "R " + amountEnter;
                        show_multi_voucher(spi_id, "input", finalProviderIdInput);
                        dialogamount.dismiss();
                    }
                } else {
                    if (Double.parseDouble(amountEnter) >= Topitup.BLUE_MAX_THRESHOLD) {
                        dialogamount.dismiss();

                        showAmountRestrictDialog(amountEnter, finalProviderIdInput, spi_id, true, Topitup.BLUE_MAX_THRESHOLD);
                    } else if (Double.parseDouble(amountEnter) >= Topitup.BLUE_WARNING_THRESHOLD) {
                        dialogamount.dismiss();

                        showAmountRestrictDialog(amountEnter, finalProviderIdInput, spi_id, false, Topitup.BLUE_MAX_THRESHOLD);
                    } else {
                        amount_deno = "R " + amountEnter;
                        show_multi_voucher(spi_id, "input", finalProviderIdInput);
                        dialogamount.dismiss();
                    }


                }


            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogone = false;

                hideKeyboard();
                FullscreenCall();
                dialogamount.dismiss();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view == btnDaily) {

            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Daily Data Bundles");
            btnDaily.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnDaily.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));

        } else if (view == btnStandard) {
            onDataClick();
            recyclerViewSocial.setVisibility(View.VISIBLE);
            recyclerViewMonthly.setVisibility(View.VISIBLE);
            recyclerViewHourly.setVisibility(View.VISIBLE);
            textviewTitleDaily.setVisibility(View.VISIBLE);
            textViewTitleMonthly.setVisibility(View.VISIBLE);
            textviewTitleHourly.setVisibility(View.VISIBLE);
            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Standard Data Bundles");
            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));

        } else if (view == btnWeekly) {
            onDataClick();
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Weekly Data Bundles");
            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setTextColor(getResources().getColor(R.color.white));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));
        } else if (view == btnMonthly) {
            onDataClick();
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
                       textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Monthly Data Bundles");
            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));
        } else if (view == btnHourly) {
            onDataClick();
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);

            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Hourly Data Bundles");

            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));
        } else if (view == btnAirtime) {
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
            textviewTitle.setVisibility(View.GONE);
            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            btnSocial.setTextColor(getResources().getColor(R.color.black));
            onAirtimeClick();
        } else if (view == btnSocial) {
            recyclerViewSocial.setVisibility(View.GONE);
            recyclerViewMonthly.setVisibility(View.GONE);
            recyclerViewHourly.setVisibility(View.GONE);
            textviewTitleDaily.setVisibility(View.GONE);
            textViewTitleMonthly.setVisibility(View.GONE);
            textviewTitleHourly.setVisibility(View.GONE);
         //   textviewTitle.setVisibility(View.GONE);

            textviewTitle.setVisibility(View.VISIBLE);
            textviewTitle.setText("Social Data Bundles");
            btnDaily.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnWeekly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnStandard.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnMonthly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnHourly.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnAirtime.setBackground(getResources().getDrawable(R.drawable.txt_border));
            btnSocial.setBackground(getResources().getDrawable(R.drawable.btnselector));
            btnSocial.setTextColor(getResources().getColor(R.color.white));
            btnWeekly.setTextColor(getResources().getColor(R.color.black));
            btnStandard.setTextColor(getResources().getColor(R.color.black));
            btnMonthly.setTextColor(getResources().getColor(R.color.black));
            btnHourly.setTextColor(getResources().getColor(R.color.black));
            btnAirtime.setTextColor(getResources().getColor(R.color.black));
            btnDaily.setTextColor(getResources().getColor(R.color.black));
            onSocialClick();
        } else if (view == btnback) {
            onBackPressed();
            linSocial.setVisibility(View.GONE);

        } else if (view == btnlogout) {
           /* if(batteryReceiver!=null) {
                unregisterReceiver(batteryReceiver);
            }*/
            Intent myIntent2 = new Intent(mContext, activity_login.class);
            myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent2);

        }

    }

    public void onAirtimeClick() {
        // linSocial.setVisibility(View.GONE);

        if (service_provider_id == 12)
            prefix = "Electricity";
        if (service_provider_id == 24 || service_provider_id == 25 || service_provider_id == 39 || service_provider_id == 26 || service_provider_id == 27 || service_provider_id == 28 || service_provider_id == 29)
            prefix = "Voucher";
        else
            prefix = "Airtime";
        item_type = 0;
        //textDeno.setBackgroundResource(R.color.white);
        if (service_provider_id == 55) {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", 30)
                    .equalTo("item_type", item_type)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        } else {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", service_provider_id)
                    .equalTo("item_type", item_type)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        }

        //  Toasty.info( mContext,"service_provider_id="+service_provider_id, Toast.LENGTH_SHORT).show();
        size = service_provider_items.size();
        NAME = new String[size];
        BARCODE = new String[size];
        DENO = new String[size];
        SPITEMID = new int[size];
        visibleCashier = new boolean[size];
        visibleAdmin = new boolean[size];
        SOCIAL_TYPE = new String[size];
        PERIOD_LABEL = new String[size];
        DENO = new String[size];
        for (int i = 0; i < size; i++) {
            service_provider_item product = service_provider_items.get(i);
            // Toasty.info( mContext,"val="+product.item_desc, Toast.LENGTH_SHORT).show();
            String prodCat = product.item_desc.replace("Cell C", "");
            prodCat = prodCat.replace("MT", "");
            prodCat = prodCat.replace("Vodacom", "");
            prodCat = prodCat.replace("Talk 360 -", "");
            prodCat = prodCat.replace("101Dialer -", "");
            prodCat = prodCat.replace("Virtual UniPIN", "");
            prodCat = prodCat.replace("Telkom Mobile", "");
            prodCat = prodCat.replace("Virgin", "");
            prodCat = prodCat.replace("Spotify", "");
            prodCat = prodCat.replace("Netflix", "");
            prodCat = prodCat.replace("Uber", "");
            prodCat = prodCat.replace("WorldCall", "");
            prodCat = prodCat.replace("EASYLOAD", "");
            prodCat = prodCat.replace("Lycamobile", "");
            DENO[i] = "";
            if (prodCat.contains("Ikeja")) {

                prodCat = prodCat.replace("Ikeja", "");
                NAME[i] = prodCat + " " + product.item_btn_desc;

            } else {

                NAME[i] = "R " + String.format("%.0f", product.item_value_int);
            }
            SPITEMID[i] = product.service_provider_item_id;
            BARCODE[i] = product.item_barcode;
            SOCIAL_TYPE[i] = product.social_type;
            PERIOD_LABEL[i] = product.period_label;
            //  DENO[i]="R "+String.format("%.0f",product.item_value_int);

            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
        }
        Log.i("NAMEMANOJ=", "size of the data....tab2......" + NAME.length);

        tabs();
    }

    public void onDataClick() {
        //  linSocial.setVisibility(View.GONE);
        prefix = "Data";
        item_type = 2;
        if (service_provider_id == 55) {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", 30)
                    .equalTo("item_type", item_type)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        } else {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", service_provider_id)
                    .equalTo("item_type", item_type)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        }
                       /* service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .equalTo("item_type", item_type)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();*/
        size = service_provider_items.size();
        SPITEMID = new int[size];
        BARCODE = new String[size];
        NAME = new String[size];
        DENO = new String[size];
        visibleCashier = new boolean[size];
        visibleAdmin = new boolean[size];
        SOCIAL_TYPE = new String[size];
        PERIOD_LABEL = new String[size];
        for (int i = 0; i < size; i++) {
            service_provider_item product = service_provider_items.get(i);
            // Toasty.info( mContext,"val="+contact.item_value, Toast.LENGTH_SHORT).show();
            String prodCat = product.item_print_desc.replace("Data", "");
            prodCat = prodCat.replace("SmartData", "");

            NAME[i] = prodCat;
            SPITEMID[i] = product.service_provider_item_id;
            BARCODE[i] = product.item_barcode;
            SOCIAL_TYPE[i] = product.social_type;
            PERIOD_LABEL[i] = product.period_label;
            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
            DENO[i] = "R " + String.format("%.0f", product.item_value_int);
        }

        tabs();

    }

    public void onSocialClick() {
        linSocial.setVisibility(View.VISIBLE);

        prefix = "Social+";
        item_type = 1;
        if (service_provider_id == 55) {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", 30)
                    .notEqualTo("item_type", 0)
                    .notEqualTo("item_type", 2)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        } else {
            service_provider_items = realm.where(service_provider_item.class)
                    .equalTo("service_provider_id", service_provider_id)
                    .notEqualTo("item_type", 0)
                    .notEqualTo("item_type", 2)
                    .sort("item_position", Sort.ASCENDING)
                    .findAll();
        }


                      /*  service_provider_items = realm.where(service_provider_item.class)
                                .equalTo("service_provider_id", service_provider_id)
                                .notEqualTo("item_type", 0)
                                .notEqualTo("item_type", 2)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();*/
        Log.i("NAMEMANOJ=" + item_type, service_provider_id + "size of the data....tab4......" + service_provider_items.size());

        size = service_provider_items.size();
        SPITEMID = new int[size];
        BARCODE = new String[size];
        DENO = new String[size];
        NAME = new String[size];
        visibleCashier = new boolean[size];
        visibleAdmin = new boolean[size];
        SOCIAL_TYPE = new String[size];
        PERIOD_LABEL = new String[size];
        for (int i = 0; i < size; i++) {
            service_provider_item product = service_provider_items.get(i);
            Log.i("NAMEMANOJkousi=", "size of the data....tab4......" + product.item_desc + product.social_type + "," + product.period_label);

            // Toasty.info( mContext,"val="+contact.item_value, Toast.LENGTH_SHORT).show();
            String prodCat = product.item_desc.replace("Cell C Data", "");
            prodCat = prodCat.replace("Telkom Mobile", "");
            prodCat = prodCat.replace("Cell C", "");
            prodCat = prodCat.replace("MTN", "");
            prodCat = prodCat.replace("Vodacom", "");
            prodCat = prodCat.replace("Talk 360 -", "");
            prodCat = prodCat.replace("101Dialer -", "");
            prodCat = prodCat.replace("Lycamobile", "");

            NAME[i] = product.item_print_desc;
            SPITEMID[i] = product.service_provider_item_id;
            BARCODE[i] = product.item_barcode;
            SOCIAL_TYPE[i] = product.social_type;
            PERIOD_LABEL[i] = product.period_label;
            visibleAdmin[i] = checkSettings(product.service_provider_item_id, 1);
            visibleCashier[i] = checkSettings(product.service_provider_item_id, 0);
            DENO[i] = "R " + String.format("%.0f", product.item_value_int);
        }
        Log.i("NAMEMANOJ=", "size of the data....tab4......" + NAME.length);

        tabs();

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

                    Log.e("..............", "cent number........" + number);
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

    private void showAmountRestrictDialog(String amntEditText, int finalProviderIdInput, Integer spi_idnew, Boolean isMax, Double maxValue) {

        LayoutInflater inflater = LayoutInflater.from(activity_spi_test.this);
        View subView = inflater.inflate(R.layout.amount_alert, null);
        AlertDialog dialogAlert = new AlertDialog.Builder(activity_spi_test.this)
                .setView(subView)
                .create();
        dialogAlert.show();

        TextView txt_title = dialogAlert.findViewById(R.id.pop_title);
        TextView txt_cancel_amount = dialogAlert.findViewById(R.id.txt_cancel_amount);
        TextView txt_proceed_amount = dialogAlert.findViewById(R.id.txt_proceed_amount);
        TextView txt_clock = dialogAlert.findViewById(R.id.txt_clock);
        TextView txt_account = dialogAlert.findViewById(R.id.txt_account);
        TextView txt_app_version = dialogAlert.findViewById(R.id.txt_app_version);
        TextView txt_user_name = dialogAlert.findViewById(R.id.txt_user_name);
        TextView txt_acc_type = dialogAlert.findViewById(R.id.txt_acc_type);
        TextView txt_account_type = dialogAlert.findViewById(R.id.txt_account_type);
        ImageView img_product_busy = dialogAlert.findViewById(R.id.img_product);
        TextView txt_product_name = dialogAlert.findViewById(R.id.txt_product_name);
        TextView type_of = dialogAlert.findViewById(R.id.type_of);

        img_product_busy.setImageResource(image);
        String foo = item_desc;

        type_of.setText("Input Amount: R" + amntEditText);
        txt_product_name.setText(item_name_input);


        txt_app_version.setText(" " + Topitup.APP_VERSION);
        txt_clock.setText(strDate);
        txt_account_type.setText(account_number);


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

            } else {

                txt_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
            }
        }

        if (companyName.length() > 10) {
            txt_account.setText(companyName.substring(0, 10) + "... ");
        } else {

            txt_account.setText(companyName);
        }
        if (isMax) {
            txt_title.setText("Max Amount Exceeded\n\nMaximum Amount Allowed = " + maxValue);
            txt_proceed_amount.setVisibility(View.GONE);
        } else {
            txt_title.setText("You are processing a large Amount.\n\nPlease make sure to Continue ");
            txt_proceed_amount.setVisibility(View.VISIBLE);
        }
        txt_cancel_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert.dismiss();
            }
        });
        txt_proceed_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert.dismiss();
                amount_deno = "R " + amntEditText;
                show_multi_voucher(spi_idnew, "input", finalProviderIdInput);
            }
        });

    }

    public static class CountDownTime extends CountDownTimer {

        private WeakReference<Activity> mActivityRef;
        private String mCurrentTime;
        private boolean mStarted;

        public CountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void start(Activity activity) {
            mActivityRef = new WeakReference<Activity>(activity);
            if (!mStarted) {
                mStarted = true;
                start();
            } else {
                updateTextView();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            mCurrentTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            updateTextView();
        }

        @Override
        public void onFinish() {
            Log.e("current Time ", "timer...finish()...");

            /*Activity activity = mActivityRef.get();
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                activity.finish();
            }*/
            mStarted = false;
        }

        private void updateTextView() {
            Log.e("current Time", "timer......" + mCurrentTime);
            /*Activity activity = mActivityRef.get();
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                TextView textView = (TextView) activity.findViewById(R.id.text_view);
                textView.setText(mCurrentTime);
            }*/
        }
    }


    public void hideKeyboard() {
        try {


            View view = findViewById(android.R.id.content);
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            /*
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }*/
        } catch (Exception var2) {
        }

       /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }
}
