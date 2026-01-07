package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.ItemCashmx;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.serviceProviderQuick;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.supplierCashmx;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_cashmx extends BaseActivity implements HomeAdapterCashmx.ItemListener {

    private RecyclerView recyclerView;
    private ArrayList<ItemCashmx> arrayList;
    BottomNavigationView mBottomNav;
    SharedPreferences settings;
    RealmResults<service_provider_item> service_provider_items;
    RealmResults<serviceProviderQuick> tiu_serviceProviderQuick;
    TextView textViewheader, txtexit;
    String prefix = "A", spiver;
    Context mContext;
    private String[] NAMEQ, DENO, BARCODE;
    private String[] NAME = new String[0];
    private Integer[] IMAGE, SUPPLIERID;
    private int[] SPITEMID;
    RealmResults<supplierCashmx> scs;


    List<String> SPList;

    //supplierCashmx sc=supplierCashmx();

    private String[] ACTIVATE;
//2122221

    // private Integer[] IMAGE = IMAGESUP;

    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    String enable_rpt_monthly_deposit = "0", enable_rpt_comm_statement = "0", enable_rpt_monthly_sales;
    Realm realm;
    int sales;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<fin_balance> tiu_fin_balance;
    private CardView cardview;
    String enable_remote_ext_credit;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;

    private String[] NAMESUP;
    //***
    RealmResults<service_provider_item_settings> service_provider_item_settings;
    int size;
    com.za.toptitup.loginlibrary.model.service_provider_item_settings service_provider_item_setting;
    int fin_opt;
    public static activity_cashmx instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashmx);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        instance = this;
        recyclerView = findViewById(R.id.recyclerView);
        // textViewheader = (TextView) findViewById(R.id.textViewheader);
        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setSelectedItemId(R.id.action_report);
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
        cardview = findViewById(R.id.cardView);

        activity_login.fromScreen = "activity_cashmx";

        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        ((Topitup) getApplication()).checkWifiSimInternet(this);

        tiu_title_bar_new = findViewById(R.id.tiu_title_bar_new);

        tiu_title_bar_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                displayDialog();
                Topitup.checkServiceRunning();

            }
        });
        scs = realm.where(supplierCashmx.class).sort("supplier_id", Sort.ASCENDING).findAll();
        get_supplers();
// NAME = new String[]{"Peninsula Beverages", "Clover", "Jumbo Ottery", "1UP Cash n Carry", "British American Tobacco","Blue Ribbon", "Fair Cape Factory Shop", "Kinder", "Simba", "Elite", "Madiba Wholesale & Retailers",
        //        "Africa Cash & Carry", "Buy More", "Kadam Wholesalesrs"};
        //String[] NAME = NAMESUP;

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();
        final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();


        if (tiu_settings == null) {

            spiver = "0";
        } else {

            spiver = tiu_settings.spi_ver;
        }

        enable_remote_ext_credit = tiu_fin_balance.enable_remote_ext_credit;
        enable_rpt_monthly_deposit = tiu_settings1.enable_rpt_monthly_deposit;
        enable_rpt_comm_statement = tiu_settings1.enable_rpt_comm_statement;
        enable_rpt_monthly_sales = tiu_settings1.enable_rpt_monthly_sales;
        if (enable_rpt_monthly_deposit == null)
            enable_rpt_monthly_deposit = "0";

        if (enable_rpt_comm_statement == null)
            enable_rpt_comm_statement = "0";

        if (enable_rpt_monthly_sales == null)
            enable_rpt_monthly_sales = "0";
        //Toasty.error(mContext, "enable_rpt_comm_statement="+ enable_rpt_comm_statement, 8000, true).show();

        /* TIU HEADER */
        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);


        final TextView tiu_clock = findViewById(R.id.tiu_clock);

        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  |  HH:mm");
        String strDate = sdf.format(c.getTime());
        tiu_clock.setText(strDate + " ");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  |  HH:mm");
                String strDate = sdf.format(c.getTime());
                tiu_clock.setText(strDate + " ");
                handler.postDelayed(this, 60000); //now is every 2 minutes
            }
        }, 60000);

        try {


//            if(tiu_settings.equals(null)){
//                Toasty.error(mContext, "Unable to login", 5000, true).show();
//                return;
//            }else {

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

                    mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Admin");
                } else {
                    mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Reports");
                    tiu_user_name.setText(Topitup.POSUSER_NAME.substring(0, 12) + "... ");
                    tiu_user_name_.setText("Cashier");

                }
            } else {

                if (Topitup.IS_ADMIN.equals("1")) {
                    tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                    tiu_user_name_.setText("Admin");

                    mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Admin");
                } else {
                    mBottomNav.getMenu().findItem(R.id.action_admin).setTitle("Reports");
                    tiu_user_name.setText(Topitup.POSUSER_NAME.replaceAll("\\s.*", ""));
                    tiu_user_name_.setText("Cashier");

                }
            }
            //  }


        } catch (Exception ex) {
            //
        }

        update_balance();
        tabs();
        FullscreenCall();
        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/

        //AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        //recyclerView.setLayoutManager(layoutManager);


        /**
         Simple GridLayoutManager that spans two columns
         **/
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* switch (item.getItemId()) {
                    case R.id.action_back:
                        onBackPressed();
                        return true;
                    case R.id.action_report:
                        prefix = "A";
                        get_supplers();

                        tabs();

                        return true;

                    case R.id.action_financial:
                        prefix = "B";


                        IMAGE = new Integer[]{
                                R.drawable.ic_get_balance,

                        };


                        NAME = new String[]{"Get Balance"};


                        SPList = new ArrayList<>((Arrays.asList(NAME)));
                        //Toasty.error(mContext, ""+ SPList.contains("cellc"), 8000, true).show();

                        if (enable_remote_ext_credit.equals("0")) {

                            SPList.remove("Request Credit");
                        }
                        if (enable_rpt_monthly_deposit.equals("0")) {

                            SPList.remove("Monthly Deposit Statement");
                        }
                        if (enable_rpt_comm_statement.equals("0")) {

                            SPList.remove("Commission Statement");
                        }
                        NAME = SPList.toArray(new String[SPList.size()]);
                        fin_opt = SPList.size();


                        tabs();
                        return true;
                    case R.id.action_users:

                        prefix = "C";
                        IMAGE = new Integer[]{R.drawable.admin_user_orig};
                        //      IMAGE = new Integer[]{ R.drawable.admin_user_orig,R.drawable.settings};

                        // NAME = new String[]{"Users","Cashier Settings","Customised Product"};
                        NAME = new String[]{"Report"};
                        tabs();

                        return true;

                    default:

                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_report) {
                    prefix = "A";
                    get_supplers();
                    tabs();
                    return true;
                } else if (item.getItemId() == R.id.action_financial) {
                    prefix = "B";
                    IMAGE = new Integer[]{R.drawable.ic_get_balance};
                    NAME = new String[]{"Get Balance"};

                    SPList = new ArrayList<>((Arrays.asList(NAME)));

                    if (enable_remote_ext_credit.equals("0")) {
                        SPList.remove("Request Credit");
                    }
                    if (enable_rpt_monthly_deposit.equals("0")) {
                        SPList.remove("Monthly Deposit Statement");
                    }
                    if (enable_rpt_comm_statement.equals("0")) {
                        SPList.remove("Commission Statement");
                    }

                    NAME = SPList.toArray(new String[SPList.size()]);
                    fin_opt = SPList.size();
                    tabs();
                    return true;
                } else if (item.getItemId() == R.id.action_users) {
                    prefix = "C";
                    IMAGE = new Integer[]{R.drawable.admin_user_orig};
                    NAME = new String[]{"Report"};
                    tabs();
                    return true;
                } else {
                    return true;
                }

            }

        });


    }
    public void updateUI(String value) {
        // here you can update the UI
        if (value.equalsIgnoreCase("network")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cashmx.rl_network.setVisibility(View.VISIBLE);
                    activity_cashmx.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cashmx.rl_network.setVisibility(View.INVISIBLE);
                    activity_cashmx.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_cashmx.rl_network.setVisibility(View.INVISIBLE);
                    activity_cashmx.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void get_supplers() {
//        Integer[] IMAGESUP = {
//                R.drawable.provcashmanagement,
//                R.drawable.blueribbon,
//                R.drawable.jumbo,
//                R.drawable.cashngrow,
//                R.drawable.btc,
//                R.drawable.clover,
//                R.drawable.faircape,
//                R.drawable.kinder,
//                R.drawable.simba,
//                R.drawable.supplier,
//                R.drawable.supplier,
//                R.drawable.supplier,
//                R.drawable.supplier,
//                R.drawable.supplier,
//        };
        int size = scs.size();
        NAME = new String[size];
        IMAGE = new Integer[size];
        SUPPLIERID = new Integer[size];
        ACTIVATE = new String[size];
        for (int i = 0; i < size; i++) {
            supplierCashmx sc = scs.get(i);
            NAME[i] = sc.legal_name;
            IMAGE[i] = R.drawable.supplier;
            ACTIVATE[i] = String.valueOf(sc.sts);
            if (sc.short_name.equals("")) {

            } else {
                int imid = isDrawableImageExists(sc.short_name.toLowerCase());
                if (imid != 0)
                    IMAGE[i] = imid;
            }


            SUPPLIERID[i] = sc.supplier_id;

            //ACTIVATE[i]="1";
        }

        //Toasty.error(mContext, ""+ imid, 8000, true).show();
    }

    private void tabs() {
        arrayList = new ArrayList<ItemCashmx>();
        String p = "1";
        if (prefix.equals("A")) {
            p = "1";
            // textViewheader.setText("REPORTS");
            SPList = new ArrayList<>((Arrays.asList(NAME)));
            //Toasty.error(mContext, ""+ SPList.contains("cellc"), 8000, true).show();

            if (enable_rpt_monthly_sales.equals("0")) {

                SPList.remove("Monthly Sales Report");
            }
            NAME = SPList.toArray(new String[SPList.size()]);
        }
        if (prefix.equals("B")) {
            // textViewheader.setText("FINANCIAL MANAGEMENT");
            p = "2";
        }
        if (prefix.equals("C")) {
            // textViewheader.setText("USER MANAGEMENT");
            p = "3";

        }
        if (prefix.equals("D")) {
            // textViewheader.setText("SETTINGS");
            p = "4";
        }
        if (prefix.equals("E")) {
            // textViewheader.setText("UPDATE MANAGEMENT");
            p = "4.5";

        }
        if (prefix.equals("F")) {

            // textViewheader.setText("SWIPE");

            p = "2." + (fin_opt - 1);

        }
        for (int i = 0; i < NAME.length; i++) {

            if (ACTIVATE[i].equals("3") || ACTIVATE[i].equals("4") || ACTIVATE[i].equals("0")) {

            } else {
                arrayList.add(new ItemCashmx(NAME[i], SUPPLIERID[i], IMAGE[i], "#FFFFFF", "", ACTIVATE[i]));

                //  arrayList.add(new ItemCashmx(NAME[i], prefix + i, IMAGE[i], "#FFFFFF", "", ACTIVATE[i]));
            }

        }
        HomeAdapterCashmx adapter = new HomeAdapterCashmx(activity_cashmx.this, arrayList, activity_cashmx.this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(ItemCashmx item) {


        if (item.activate.equals("2")) {
            Intent myIntent = new Intent(mContext, activity_cash_management.class);
            myIntent.putExtra("ptype", item.pos);
            startActivity(myIntent);
        } else {

            Toasty.error(mContext, "Supplier not activated", 8000, true).show();
        }


        // Toast.makeText(getApplicationContext(), item.pos + " is clicked", Toast.LENGTH_SHORT).show();
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

    private void get_update_users() {


        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>() {

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Timber.e(response.body());
                //Toasty.error(mContext, response.toString(), 8000, true).show();

                   if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {

                    List<pos_users> result = response.body();

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(result);
                    realm.commitTransaction();

                    //res = response.body().string();

                } catch (Exception ex) {
                    //
                    response.body().clear();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }




    private void update_spi(String service_provider_data) {


        realm.beginTransaction();
        realm.where(service_provider.class).findAll().deleteAllFromRealm();
        realm.where(service_provider_item.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();


        BufferedReader bufReader = new BufferedReader(new StringReader(service_provider_data));

        try {

            String line = null;
            Integer last_sp_id = 0;

            while ((line = bufReader.readLine()) != null) {

                String[] myData = line.split("\\^");
                //  Toasty.error(mContext, "provider_id"+myData[0], 8000, true).show();
                if (myData[0].equals("p")) {
                    //  Toasty.error(mContext, "yData[0]"+myData[0], 8000, true).show();
                    service_provider new_sp = new service_provider();
                    new_sp.provider_id = Integer.parseInt(myData[1]);
                    //  Toasty.error(mContext, "provider_id"+new_sp.provider_id, 8000, true).show();

                    last_sp_id = Integer.parseInt(myData[1]);

                    if (myData.length > 2) new_sp.provider_desc = myData[2];
                    if (myData.length > 3) new_sp.provider_message = myData[3];

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_sp);
                    realm.commitTransaction();

                } else {

                    service_provider_item new_spi = new service_provider_item();
                    new_spi.service_provider_item_id = Integer.parseInt(myData[0]);
                    new_spi.service_provider_id = last_sp_id;


                    boolean item_show_value = Integer.parseInt(myData[7]) == 1;

                    new_spi.item_desc = myData[1];
                    new_spi.item_btn_desc = myData[2];
                    //new_spi.item_print_desc = myData[3];
                    new_spi.item_print_desc = myData[4];
                    new_spi.item_value = myData[5];
                    new_spi.item_type = Integer.parseInt(myData[6]);
                    new_spi.item_show_value = item_show_value;
                    new_spi.item_position = Integer.parseInt(myData[8]);
                    Timber.i("length " + myData.length);
                    if (myData.length == 10)
                        new_spi.item_barcode = myData[9];

                    new_spi.item_value_int = Double.parseDouble(myData[5]);

                    Timber.i("SPI :  " + myData[1]);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_spi);
                    realm.commitTransaction();

                }


            }

        } catch (Exception ex) {

            Timber.i("SPI Error: " + ex.getMessage());

        }


/**
 *
 * Service provider items
 *
 */
    /*    realm.beginTransaction();
        realm.where(service_provider_item_settings.class).findAll().deleteAllFromRealm();
         realm.commitTransaction();*/
        service_provider_items = realm.where(service_provider_item.class).findAll();

        size = service_provider_items.size();

        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("enable", 1).findAll();
        realm.beginTransaction();
        for (int i = 0; i < service_provider_item_settings.size(); i++) {
            service_provider_item_settings.get(i).setEnable(0);
        }

        realm.commitTransaction();
        for (int k = 0; k < size; k++) {
            service_provider_item sp_item = service_provider_items.get(k);
            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", sp_item.service_provider_item_id).findFirst();
            realm.beginTransaction();
            if (service_provider_item_setting == null) {
                //Toasty.info(mContext, "if loop ", 8000, true).show();
                service_provider_item_settings sp_settings = new service_provider_item_settings();
                sp_settings.setService_provider_id(sp_item.service_provider_id);
                sp_settings.setService_provider_item_id(sp_item.service_provider_item_id);
                sp_settings.setItem_desc(sp_item.item_desc);
                sp_settings.setItem_position(sp_item.item_position);
                sp_settings.setVisible_admin(true);
                sp_settings.setVisible_cashier(true);
                realm.insertOrUpdate(sp_settings);

            } else {
                service_provider_item_setting.setEnable(1);
                service_provider_item_setting.setItem_desc(sp_item.item_desc);
            }
            realm.commitTransaction();
        }


    }


    public void statement() {
        final Call<ResponseBody> call;

        call = apiService.commission_statement(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, "202008");

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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stopCustomDialog("Problem", t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }


    public void z_end_of_day() {

        //final Call<ResponseBody> call = apiService.z_end_of_day_cashier(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        final Call<ResponseBody> call = apiService.z_end_of_day(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }


    public void cashup_x_intraday_summary() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday_summary(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

            }

        });


    }

    public void cashup_x_intraday() {

        final Call<ResponseBody> call = apiService.cashup_x_intraday(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
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

                    stopCustomDialog("Problem", matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing", "Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet", "Please check that your internet connection is working.");
                } else {
                    stopCustomDialog("Problem", t.getMessage());
                }

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
        dialog.setCancelable(true);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
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

    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    //  AppCompatButton btn_Paper_ignore_always;

    private void showCustomDialogn(String sTitle, String sContent, Boolean showClose) {

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
                FullscreenCall();
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

    private void update_balance() {
        settings = getSharedPreferences("TIUPREF", 0);

        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");

        final String setting_balance_admin = settings.getString("setting_balance_admin", "0");
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final String setting_balance_admin_bills = settings.getString("setting_balance_admin_bills", "0");
        final String setting_balance_admin_commission = settings.getString("setting_balance_admin_commission", "0");
        final String setting_balance_admin_swipe = settings.getString("setting_balance_admin_swipe", "0");


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    String is_admin = "0";
                    if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";
                    if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

                        /*if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);*/
                    }


                    if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

                    }
                    if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {

                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

                    }

                    if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                        tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);


                    }


                    if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
                    }
                    if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                        tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
                    }

                    if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                        view3.setVisibility(View.VISIBLE);
                        tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
                    }
                }
            });
            String is_admin = "0";
            if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

            tiu_title_balance.setVisibility(View.GONE);
            tiu_title_balance_cash.setVisibility(View.GONE);


            if (setting_balance_cashier.equals("1") && is_admin.equals("0")) {
                tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

               /* if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);*/
                tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if (setting_balance_cashier_bills.equals("1") && is_admin.equals("0")) {

                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);

                view2.setVisibility(View.VISIBLE);
                tiu_title_balance_cash.setVisibility(View.VISIBLE);

            }
            if (setting_balance_cashier_commission.equals("1") && is_admin.equals("0")) {

                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");

            }
            if (setting_balance_cashier_swipe.equals("1") && is_admin.equals("0")) {
                view3.setVisibility(View.VISIBLE);

                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);

            }

            if (setting_balance_admin.equals("1") && is_admin.equals("1")) {
                tiu_title_balance.setText("Standard  R " + tiu_fin_balance.get(0).available_balance + " ");
//                tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
//                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.get(0).balance_cash);
                tiu_title_balance.setVisibility(View.VISIBLE);
//                tiu_title_balance_cash.setVisibility(View.VISIBLE);
            }

            if (setting_balance_admin_bills.equals("1") && is_admin.equals("1")) {
                tiu_title_balance_cash.setVisibility(View.VISIBLE);

                view2.setVisibility(View.VISIBLE);
                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills  R " + tiu_fin_balance.get(0).balance_cash);
            }
            if (setting_balance_admin_commission.equals("1") && is_admin.equals("1")) {
                tiu_title_balance_commision.setText("Commission  R " + tiu_fin_balance.get(0).commission + "  ");
            }

            if (setting_balance_admin_swipe.equals("1") && is_admin.equals("1")) {
                view3.setVisibility(View.VISIBLE);
                tiu_title_balance_swipe.setText("Swipe  R " + tiu_fin_balance.get(0).swipe);
            }
        } catch (Exception ex) {
            //
        }

    }


    private int isDrawableImageExists(String imgName) {
        int id = 0;
        id = getResources().getIdentifier(imgName, "drawable", getPackageName());
        return id;
    }

}
