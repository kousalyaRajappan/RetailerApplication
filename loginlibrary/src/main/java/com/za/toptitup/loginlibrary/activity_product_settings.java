package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.service_provider_settings;
import com.za.toptitup.loginlibrary.utils.MultiSelectionSpinner;
import com.za.toptitup.loginlibrary.utils.Topitup;


public class activity_product_settings extends BaseAdminActivity {

    Realm realm;
    private BroadcastReceiver mNetworkReceiver;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    EditText txt_input;
    RadioButton rdo_filter_type_0;
    RadioButton rdo_filter_type_1;
    RadioButton rdo_filter_type_2;
    public static String filterSelectedItem = "product";
    ListView lst_reprint;
    RealmResults<service_provider_item> service_provider_items;
    RealmResults<service_provider_item> service_provider_itemsd;
    RealmResults<service_provider> service_provider;
    RealmResults<service_provider_settings> service_provider_settings;
    com.za.toptitup.loginlibrary.model.service_provider_settings service_provider_setting;
    com.za.toptitup.loginlibrary.model.service_provider_item_settings service_provider_item_setting;

    RealmResults<service_provider_item_settings> service_provider_item_settings;
    RealmResults<fin_balance> tiu_fin_balance;
    String spi_barcode, item_desc;
    int size, sized;
    String[] products, productssel;
    int isadmin = 1, isproduct = 1;
    String company_addrs, company_addrs1, company_addrs2, print_address = "0";
    MultiSelectionSpinner spinner;
    public static ImageView txt_battery, img_wifi, img_network, img_network2;
    public static RelativeLayout rl_network, rl_server;
    LinearLayout tiu_title_bar_new;
    public static activity_product_settings instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        instance = this;
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_product_settings);
        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        FullscreenCall();
/**
 *
 * Service Provider Update
 *
 *
 */

        service_provider = realm.where(service_provider.class).findAll();
        size = service_provider.size();
        filterSelectedItem = "product";
        service_provider_settings = realm.where(service_provider_settings.class).equalTo("enable", 1).findAll();
        realm.beginTransaction();
        Log.e("size of ", "service_provider_settings ...........1" + service_provider_settings.size());
        for (int i = 0; i < service_provider_settings.size(); i++) {
            service_provider_settings.get(i).setEnable(0);
        }

        realm.commitTransaction();
        for (int k = 0; k < size; k++) {
            service_provider sp = service_provider.get(k);
            service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", sp.provider_id).findFirst();
            realm.beginTransaction();
            if (service_provider_setting == null) {
                //  Toasty.info(mContext, "if loop ", 8000, true).show();
                service_provider_settings sp_settings = new service_provider_settings();
                sp_settings.setProvider_desc(sp.provider_desc);
                sp_settings.setProvider_id(sp.provider_id);
                sp_settings.setVisible_admin(true);
                sp_settings.setVisible_cashier(true);
                realm.insertOrUpdate(sp_settings);
            } else {
                service_provider_setting.setEnable(1);
                service_provider_setting.setProvider_desc(sp.provider_desc);
            }
            realm.commitTransaction();
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
//                Toasty.info(mContext, "if loop "+sp_item.service_provider_id, 8000, true).show();
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


        activity_login.fromScreen = "activity_product_settings";


        txt_battery = findViewById(R.id.txt_battery);
        img_wifi = findViewById(R.id.img_wifi);
        img_network = findViewById(R.id.img_network);
        img_network2 = findViewById(R.id.img_network2);
        rl_server = findViewById(R.id.rl_server);
        rl_network = findViewById(R.id.rl_network);

        ((Topitup) getApplication()).checkWifiSimInternet(this);

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
        spinner = findViewById(R.id.spinnerproduct);

        spinner.setisAdmin(isadmin);
        spinner.setisProduct(1);
        service_provider_settings = realm.where(service_provider_settings.class).findAll();

        size = service_provider_settings.size();
        // products=new String[size-8];
        products = new String[size - 11];

        for (int j = 0; j < products.length; j++) {
            service_provider_settings product = service_provider_settings.get(j);
            if (product.provider_id <= 30 || product.provider_id > 39) {
                products[j] = product.provider_desc;
            }
        }
        spinner.setItems(products);
        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();


        size = service_provider_settings.size();
        productssel = new String[size];
        for (int j = 0; j < size; j++) {
            service_provider_settings sp = service_provider_settings.get(j);
            productssel[j] = sp.provider_desc;
        }

        spinner.setSelection(productssel);

        RadioGroup grp_radio_layout = findViewById(R.id.grp_radio_layout);
        RadioGroup grp_radio_product = findViewById(R.id.grp_radio_product);

        grp_radio_layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // get selected radio button from radioGroup
                spinner.clearSelection();
              /*  switch (checkedId) {

                    case R.id.rdo_filter_type_0:
                        spinner.setisAdmin(1);
                        //  grp_radio_product.setOnCheckedChangeListener(this::onCheckedChanged);
                        if (isproduct == 1) {
                            service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();
                            size = service_provider_settings.size();
                            productssel = new String[size];
                            for (int j = 0; j < size; j++) {
                                service_provider_settings sp = service_provider_settings.get(j);
                                productssel[j] = sp.provider_desc;
                            }

                        } else {
                            service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_admin", true).findAll();
                            size = service_provider_item_settings.size();
                            productssel = new String[size];
                            for (int j = 0; j < size; j++) {
                                service_provider_item_settings sp = service_provider_item_settings.get(j);
                                productssel[j] = sp.item_desc;
                            }
                        }
                        spinner.setSelection(productssel);
                        break;
                    case R.id.rdo_filter_type_1:
                        spinner.setisAdmin(0);
                        if (isproduct == 1) {
                            service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_cashier", true).findAll();

                            size = service_provider_settings.size();
                            productssel = new String[size];
                            for (int j = 0; j < size; j++) {
                                service_provider_settings sp = service_provider_settings.get(j);
                                // Toasty.info(mContext ,"Desc" +  sp.provider_desc, 8000, true).show();
                                productssel[j] = sp.provider_desc;
                            }
                        } else {

                            service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_cashier", true).findAll();

                            size = service_provider_item_settings.size();
                            productssel = new String[size];
                            for (int j = 0; j < size; j++) {
                                service_provider_item_settings sp = service_provider_item_settings.get(j);
                                // Toasty.info(mContext ,"Desc" +  sp.provider_desc, 8000, true).show();
                                productssel[j] = sp.item_desc;
                            }
                        }
                        spinner.setSelection(productssel);
                        break;
                }*/
                if (checkedId == R.id.rdo_filter_type_0) {
                    spinner.setisAdmin(1);

                    if (isproduct == 1) {
                        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();

                        int size = service_provider_settings.size();
                        productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_settings sp = service_provider_settings.get(j);
                            productssel[j] = sp.provider_desc;
                        }
                    } else {
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_admin", true).findAll();

                        int size = service_provider_item_settings.size();
                        productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_item_settings sp = service_provider_item_settings.get(j);
                            productssel[j] = sp.item_desc;
                        }
                    }
                    spinner.setSelection(productssel);
                } else if (checkedId == R.id.rdo_filter_type_1) {
                    spinner.setisAdmin(0);

                    if (isproduct == 1) {
                        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_cashier", true).findAll();

                        int size = service_provider_settings.size();
                        productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_settings sp = service_provider_settings.get(j);
                            productssel[j] = sp.provider_desc;
                        }
                    } else {
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_cashier", true).findAll();

                        int size = service_provider_item_settings.size();
                        productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_item_settings sp = service_provider_item_settings.get(j);
                            productssel[j] = sp.item_desc;
                        }
                    }
                    spinner.setSelection(productssel);
                }

            }
        });

        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();
        size = service_provider_settings.size();
        productssel = new String[size];
        for (int j = 0; j < size; j++) {
            service_provider_settings sp = service_provider_settings.get(j);
            // products[j]=sp.provider_desc+"("+sp.provider_id+")";
            productssel[j] = sp.provider_desc;
        }

        spinner.setSelection(productssel);

        grp_radio_product.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // get selected radio button from radioGroup
              /*  switch (checkedId) {
                    case R.id.rdo_filter_product_0:
                        filterSelectedItem = "product";
                        isproduct = 1;
                        spinner.setisProduct(1);
                        service_provider_settings = realm.where(service_provider_settings.class).findAll();
                        size = service_provider_settings.size();
                        products = new String[size - 7];
                        for (int j = 0; j < products.length; j++) {
                            service_provider_settings product = service_provider_settings.get(j);

                            if (product.provider_id <= 31 || product.provider_id > 39) {


                                products[j] = product.provider_desc;
                            }
                        }
                        spinner.setItems(products);


                        if (isadmin == 1)
                            service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();
                        else
                            service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_cashier", true).findAll();

                        size = service_provider_settings.size();
                        productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_settings sp = service_provider_settings.get(j);
                            // products[j]=sp.provider_desc+"("+sp.provider_id+")";
                            productssel[j] = sp.provider_desc;
                        }

                        spinner.setSelection(productssel);


                        break;
                    case R.id.rdo_filter_product_1:
                        filterSelectedItem = "denomination";

                        isproduct = 0;
                        spinner.setisProduct(0);
                        service_provider_item_settings = realm.where(service_provider_item_settings.class)
                                .sort("item_position", Sort.ASCENDING)
                                .findAll();
                        size = service_provider_item_settings.size();
                        products = new String[size];
                        for (int j = 0; j < size; j++) {

                            service_provider_item_settings product = service_provider_item_settings.get(j);
                            products[j] = product.item_desc;
                        }
                        spinner.setItems(products);

                        if (isadmin == 1)
                            service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_admin", true).findAll();
                        else
                            service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_cashier", true).findAll();

                        size = service_provider_item_settings.size();
                        String[] productssel = new String[size];
                        for (int j = 0; j < size; j++) {
                            service_provider_item_settings pro = service_provider_item_settings.get(j);
                            // products[j]=sp.provider_desc+"("+sp.provider_id+")";
                            productssel[j] = pro.item_desc;
                        }

                        spinner.setSelection(productssel);
                        break;

                }*/
                if (checkedId == R.id.rdo_filter_product_0) {
                    filterSelectedItem = "product";
                    isproduct = 1;
                    spinner.setisProduct(1);

                    service_provider_settings = realm.where(service_provider_settings.class).findAll();
                    int size = service_provider_settings.size();
                    products = new String[size - 7];

                    int productIndex = 0; // Track index for products array
                    for (int j = 0; j < size; j++) {
                        service_provider_settings product = service_provider_settings.get(j);
                        if (product.provider_id <= 31 || product.provider_id > 39) {
                            products[productIndex] = product.provider_desc;
                            productIndex++;
                        }
                    }
                    spinner.setItems(products);

                    if (isadmin == 1) {
                        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_admin", true).findAll();
                    } else {
                        service_provider_settings = realm.where(service_provider_settings.class).equalTo("visible_cashier", true).findAll();
                    }

                    size = service_provider_settings.size();
                    productssel = new String[size];
                    for (int j = 0; j < size; j++) {
                        service_provider_settings sp = service_provider_settings.get(j);
                        productssel[j] = sp.provider_desc;
                    }

                    spinner.setSelection(productssel);
                } else if (checkedId == R.id.rdo_filter_product_1) {
                    filterSelectedItem = "denomination";
                    isproduct = 0;
                    spinner.setisProduct(0);

                    service_provider_item_settings = realm.where(service_provider_item_settings.class)
                            .sort("item_position", Sort.ASCENDING)
                            .findAll();
                    size = service_provider_item_settings.size();
                    products = new String[size];

                    for (int j = 0; j < size; j++) {
                        service_provider_item_settings product = service_provider_item_settings.get(j);
                        products[j] = product.item_desc;
                    }
                    spinner.setItems(products);

                    if (isadmin == 1) {
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_admin", true).findAll();
                    } else {
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("visible_cashier", true).findAll();
                    }

                    size = service_provider_item_settings.size();
                    productssel = new String[size];
                    for (int j = 0; j < size; j++) {
                        service_provider_item_settings pro = service_provider_item_settings.get(j);
                        productssel[j] = pro.item_desc;
                    }

                    spinner.setSelection(productssel);
                }

            }
        });


// Multi spinner

        // Creating array adapter for spinner
        // ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, products);

        // Drop down style will be listview with radio button
        // dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // create spinner list elements


        // attaching data adapter to spinner
        //spinner.setAdapter(dataAdapter);

        // String[] productssel = new String[]{"Cell C", "MTN"};







        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_print_address = settings.getString("setting_print_address", "0");


        final String setting_balance_login_bills = settings.getString("setting_balance_login_bills", "0");
        final String setting_balance_login_commission = settings.getString("setting_balance_login_commission", "0");
        final String setting_balance_login_swipe = settings.getString("setting_balance_login_swipe", "0");


        final String setting_balance_cashier_bills = settings.getString("setting_balance_cashier_bills", "0");
        final String setting_balance_cashier_commission = settings.getString("setting_balance_cashier_commission", "0");
        final String setting_balance_cashier_swipe = settings.getString("setting_balance_cashier_swipe", "0");


        final String setting_chk_allow_cashier_rep_ser = settings.getString("setting_chk_allow_cashier_rep_ser", "0");
        final String str_chk_last_vou_admin = settings.getString("setting_chk_last_vou_admin", "0");
        final String str_chk_last_vou_cashier = settings.getString("setting_chk_last_vou_cashier", "0");

        final TextView tiu_title_outlet = findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = findViewById(R.id.tiu_title_balance);
        final TextView tiu_user_name = findViewById(R.id.tiu_user_name);
        final TextView tiu_user_name_ = findViewById(R.id.tiu_user_name_);
        TextView txt_app_version = findViewById(R.id.txt_app_version);
        txt_app_version.setVisibility(View.VISIBLE);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        final TextView tiu_title_balance_cash = findViewById(R.id.tiu_title_balance_cash);

        final TextView tiu_title_balance_commision = findViewById(R.id.tiu_title_balance_commision);
        final TextView tiu_title_balance_swipe = findViewById(R.id.tiu_title_balance_swipe);

        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();

        tiu_title_outlet.setText(tiu_settings.account_number);

        /*if(tiu_settings.company_name.length()>10){
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

        try {
            //final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();

            tiu_fin_balance = realm.where(fin_balance.class).equalTo("fin_balance_id", 0).findAll();
            tiu_fin_balance.addChangeListener(new RealmChangeListener<RealmResults<fin_balance>>() {
                @Override
                public void onChange(RealmResults<fin_balance> results) {
                    if (setting_balance_login.equals("1") || setting_balance_cashier.equals("1")) {
                        tiu_title_balance.setText("R " + tiu_fin_balance.get(0).available_balance);
//                        tiu_title_balance_commision.setText("Commission R " + tiu_fin_balance.get(0).commission);
//                        tiu_title_balance_swipe.setText("Swipe R " + tiu_fin_balance.get(0).swipe);

//                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);
                    }


                    if (setting_balance_login_bills.equals("1") || setting_balance_cashier_bills.equals("1")) {

                        view2.setVisibility(View.VISIBLE);
                        if (!tiu_fin_balance.get(0).balance_cash.equals("0.00"))
                            tiu_title_balance_cash.setText("R " + tiu_fin_balance.get(0).balance_cash);

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

//                if (!tiu_fin_balance.get(0).balance_cash.equals("0.00")) tiu_title_balance_cash.setText("CASH R " + tiu_fin_balance.get(0).balance_cash);
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


        txt_input = findViewById(R.id.txt_input);
        rdo_filter_type_0 = findViewById(R.id.rdo_filter_type_0);
        rdo_filter_type_1 = findViewById(R.id.rdo_filter_type_1);
        rdo_filter_type_2 = findViewById(R.id.rdo_filter_type_2);


        mBottomNav.getMenu().clear(); //clear old inflated items.
        mBottomNav.inflateMenu(R.menu.bottom_nav_admin_back);
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


                        return true;
                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    // Handle clear action here (if needed)
                    return true;
                } else {
                    return true; // Handle any other cases (if needed)
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
                    activity_product_settings.rl_network.setVisibility(View.VISIBLE);
                    activity_product_settings.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        } else if (value.equalsIgnoreCase("server")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_product_settings.rl_network.setVisibility(View.INVISIBLE);
                    activity_product_settings.rl_server.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity_product_settings.rl_network.setVisibility(View.INVISIBLE);
                    activity_product_settings.rl_server.setVisibility(View.INVISIBLE);
                }
            });
        }
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


    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;

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

    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };


    private void printingCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.GONE);
        pageLoadingWrapper.setVisibility(View.GONE);
        handler.postDelayed(runnable, 2000);

    }


    protected void updateSPIsettings() {

        /**
         *
         * Service Provider Update
         *
         *
         */

        try {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
            service_provider = realm.where(service_provider.class).findAll();
            size = service_provider.size();

            service_provider_settings = realm.where(service_provider_settings.class).equalTo("enable", 1).findAll();
            realm.beginTransaction();
            for (int i = 0; i < service_provider_settings.size(); i++) {
                service_provider_settings.get(i).setEnable(0);
            }

            realm.commitTransaction();
            for (int k = 0; k < size; k++) {
                service_provider sp = service_provider.get(k);
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", sp.provider_id).findFirst();
                realm.beginTransaction();
                if (service_provider_setting == null) {
                    //  Toasty.info(mContext, "if loop ", 8000, true).show();
                    service_provider_settings sp_settings = new service_provider_settings();
                    sp_settings.setProvider_desc(sp.provider_desc);
                    sp_settings.setProvider_id(sp.provider_id);
                    sp_settings.setVisible_admin(true);
                    sp_settings.setVisible_cashier(true);

                    realm.insertOrUpdate(sp_settings);

                } else {
                    service_provider_setting.setEnable(1);
                    service_provider_setting.setProvider_desc(sp.provider_desc);
                }
                realm.commitTransaction();
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
            //Timber.i("Done");
//        Toasty.info(mContext, "Hurray it is working", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }

    }


}
