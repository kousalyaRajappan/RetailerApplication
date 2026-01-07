package com.za.toptitup.loginlibrary.admin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.adapters.UserListAdapter;
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.MyItem;
import com.za.toptitup.loginlibrary.model.MyItemLicense;
import com.za.toptitup.loginlibrary.model.ReprintListAdapterZ;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_zhistory extends BaseAdminActivity implements ReprintListAdapterZ.OnItemClickMain {

    private static final int REQUEST_CAMERARESULT = 201;
    protected Topitup app;
    Realm realm;
    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    ReprintListAdapterZ.OnItemClickMain onItemClickMain;
    ArrayList<MyItemLicense> itemLicence;
    RecyclerView lst_reprint;
    LinearLayoutManager manager;

    RadioButton rdo_report_type0;
    RadioButton rdo_report_type1;
    int startId = 0;
    boolean isLoading = false;
    Spinner spinner_lic;
    String licenceValue = "0";

    String licenseId;
    String licenceFromSpinner;
    ListView listView;
    ArrayList<MyItem> movies = new ArrayList<MyItem>();
    ReprintListAdapterZ listAdapter;
    LinearLayout pageLoadingWrapper;
    PrinterTopitup printer;

    //    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Z Report History");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Tools.setSystemBarColor(this);
//    }
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
    private LinearLayout bottomProgressLayout;
    private int indexId = 0;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        onItemClickMain = this;
        setContentView(R.layout.activity_zhistory);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

         printer = new PrinterTopitup(this);
        //initToolbar();

        lst_reprint = findViewById(R.id.lst_reprint);
        manager = new LinearLayoutManager(this);
        lst_reprint.setLayoutManager(manager);
        lst_reprint.setHasFixedSize(true);
        movies = new ArrayList<>();

        listAdapter = new ReprintListAdapterZ(mContext, 0, movies, onItemClickMain);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
        lst_reprint.setAdapter(listAdapter);
        bottomProgressLayout = findViewById(R.id.bottom_progress_layout);
        final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();

        licenseId = tiu_settings1.license_id;


        rdo_report_type0 = findViewById(R.id.rdo_report_type0);
        rdo_report_type1 = findViewById(R.id.rdo_report_type1);
        spinner_lic = findViewById(R.id.spinner_lic);


        getLicenseList();

        spinner_lic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinner_lic.getSelectedItem().toString().contains(licenseId)) {
//                    Log.e("spinner value", itemLicence.get(i).lic_code + "selected value   if ..." + licenseId);

                    licenceValue = licenseId;
                    licenceFromSpinner = itemLicence.get(i).lic_code;

                } else {

                    licenceValue = itemLicence.get(i - 1).lic_uid;
                    licenceFromSpinner = itemLicence.get(i-1).lic_code;
//                    Log.e("spinner value", itemLicence.get(i - 1).lic_code + "selected value   else ..." +licenceValue);

                }
                Log.e("spinner value", licenceFromSpinner+ "selected value   else ..." +licenceValue);

                get_zhistory(0, licenceValue);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        lst_reprint.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == movies.size() - 1) {
                        manager.scrollToPosition(movies.size() - 1);
                        indexId++;
                        startId = 10 * indexId;
                        isLoading = true;

                        get_zhistory(startId, licenceValue);
                    }
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                    }
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
    }

    private void getLicenseList() {

        final Call<ResponseBody> call = apiService.licence_list(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }
                itemLicence = new ArrayList<MyItemLicense>();

                String res = "";
                try {
                    res = response.body().string();
                } catch (Exception ex) {
                    if (response.body() != null)
                        response.body().close();
                    Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                    bottomProgressLayout.setVisibility(View.GONE);
                    dialog.dismiss();
                    return;
                }

                try {
                    String[] toSplit = res.split("\n");
                    MyItemLicense test;

//                    test.lic_uid=Topitup.
                    for (int i = 0; i < toSplit.length; i++) {
                        test = new MyItemLicense();
                        String s = toSplit[i];
                        String[] tokens = s.split("\\^");


                        test.lic_uid = tokens[0];
                        test.lic_code = tokens[1];
                        test.lic_des = tokens[2];

                        //  Log.e("add values",test.lic_code+"........."+test.lic_uid+"  "+test.lic_des);
                        itemLicence.add(test);

                    }


                /*    Log.e("gowthami","posuser"+Topitup.POSUSER_ID);
                    Log.e("gowthami","posuser name"+Topitup.POSUSER_NAME);
                    Log.e("gowthami","posuser TIU_SERVER"+Topitup.TIU_SERVER);


                    Log.e("gowthami","customer id"+Topitup.CUSTOMER_ID);*/

                    List<String> list = new ArrayList<String>();
                    List<String> finallist = new ArrayList<String>();

                    for (int i = 0; i < itemLicence.size(); i++) {
                        list.add(itemLicence.get(i).lic_des);
                    }
                    Log.e("gowthami", "posuser TIU_SERVER............" + licenseId);

//                    list.add("Top it Up #5128");
                    if (!finallist.contains("Top it Up #" + licenseId)) {
                        finallist.add(0, "Top it Up #" + licenseId + " | Current Device");
                    }

                    for (int i = 0; i < list.size(); i++) {
                        String finaldesc;
                        if (list.get(i).contains(licenseId)) {
                            finaldesc = list.get(i) + " | Current Device";
                        } else {
                            finaldesc = list.get(i) + " | Other Device";
                        }

                        finallist.add(finaldesc);
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, finallist);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner_lic.setAdapter(adapter);
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                } else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

               // dialog.dismiss();

            }

        });
    }

    public void get_zhistory(int page, String licenceValue) {
        if (page == 0) {
//            centerProgressLayout.setVisibility(View.VISIBLE);
            lst_reprint.setVisibility(View.INVISIBLE);
//            noItemLayout.setVisibility(View.GONE);
            bottomProgressLayout.setVisibility(View.GONE);
        } else {
            bottomProgressLayout.setVisibility(View.VISIBLE);
        }
        showCustomDialog();
        int start = 0, end = 0;
        start = page;
        end = 10;
        final Call<ResponseBody> call = apiService.z_report_history(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, licenceValue, "0", "0", String.valueOf(start), String.valueOf(end));

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
                    bottomProgressLayout.setVisibility(View.GONE);

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
                    bottomProgressLayout.setVisibility(View.GONE);

                    dialog.dismiss();
                    return;

                }


                if (!res.contains("\n")) {
                    Toasty.info(mContext, "No Results.", 8000, true).show();
                    bottomProgressLayout.setVisibility(View.GONE);

                    dialog.dismiss();
                    return;
                }
                if (page == 0) {
                    movies.clear();
                }
                try {

                    int rowCount = 0;
                    String[] toSplit = res.split("\n");
                    for (int i = 0; i < toSplit.length; i++) {

                        String s = toSplit[i];
                        //rowCount++;
                        String[] tokens = s.split("\\^");

                        MyItem test = new MyItem();

                        test.stock_uid = tokens[0];

                        try {
                            test.btn_description = "Total Sale    R " + String.format("%.2f", Float.parseFloat(tokens[4]) + Float.parseFloat(tokens[5]));
                           /* test.btn_description = "Airtime R " + String.format("%.2f", Float.parseFloat(tokens[4].toString())) +
                                    ", Electricity R " + String.format("%.2f", Float.parseFloat(tokens[5].toString()));*/
                        } catch (Exception ex) {
                            //test.btn_description = tokens[2].toString();
                        }

                        test.btn_date = tokens[2] + ", " + tokens[3];
                        test.btn_user = tokens[1];

                        movies.add(test);

                        //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());

                    }


                  /*  listAdapter = new ReprintList(mContext, 0, movies);
                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.ding_products.simple_list_item_1, movies);
                    lst_reprint.setAdapter(listAdapter);
*/
                    if (startId == 0) {
                        dialog.dismiss();
//                        centerProgressLayout.setVisibility(View.GONE);
//                        noItemLayout.setVisibility(View.GONE);
                    }
                    bottomProgressLayout.setVisibility(View.GONE);
                    if (page == 0 && movies.size() == 0) {
                        lst_reprint.setVisibility(View.INVISIBLE);
                    } else {
                        lst_reprint.setVisibility(View.VISIBLE);
                    }
                    populateTransactionList(movies);

                   /* lst_reprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            getReprint(movies.get(position).stock_uid);
                            //Toasty.info(mContext, "ID " +  String.valueOf(movies.get(position).stock_uid), 8000, true).show();
                            //Toast.makeText(MainActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

                        }
                    });*/

                } catch (Exception ex) {

                    Toasty.info(mContext, ex.getMessage(), 8000, true).show();
                }
                bottomProgressLayout.setVisibility(View.GONE);

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

    private void populateTransactionList(ArrayList<MyItem> movies) {
        isLoading = false;
        listAdapter.notifyDataSetChanged();
    }

    private void getReprint(final String customer_report_z_id) {


        String ret = "";
        String htmlResponse = "";

        showCustomDialog();

        Log.e("spinner value","........spinners selected"+licenceFromSpinner);
        String by_cashier = "-1";
        if (rdo_report_type1.isChecked()) by_cashier = "1";

        Call<ResponseBody> call = apiService.z_reprint(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID, customer_report_z_id, licenceFromSpinner, Topitup.POSUSER_ID, by_cashier);
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

                    }

                    Timber.i("REPRINT " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {

                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();

                    } else {

                        PrinterTopitup.print_data(res);

                    }


                } catch (Exception ex) {

                    Timber.e("REPRINT " + ex.getMessage());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClickedMain(String sID) {
        getReprint(sID);

    }
}
