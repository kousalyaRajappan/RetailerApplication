package com.za.toptitup.loginlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.admin.activity_activation;
import com.za.toptitup.loginlibrary.admin.activity_banking_detail;
import com.za.toptitup.loginlibrary.admin.activity_zhistory;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_cashier extends BaseAdminActivity implements HomeAdapter.ItemListener{

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayList;
    BottomNavigationView mBottomNav;
    RealmResults<service_provider_item> service_provider_items;
    TextView textViewheader;
    String prefix="A",spiver;
    Context mContext;

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    Realm realm;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<fin_balance> tiu_fin_balance;
    private CardView cardview;
    private String[] NAME;
    private  Integer[] IMAGE;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_main);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        textViewheader = findViewById(R.id.textViewheader);
        mBottomNav  = findViewById(R.id.bottom_navigation);






       // mBottomNav.setVisibility(View.GONE);
        cardview = findViewById(R.id.cardView);


        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        spiver=tiu_settings.spi_ver;
        /* TIU HEADER */
       settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name","");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier","0");
        final String setting_balance_login = settings.getString("setting_balance_login","0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen","0");



      IMAGE = new Integer[]{R.drawable.ic_w_report,R.drawable.ic_y_report };



         NAME = new String[]{"Cashup W", "Cashup Y"};
        tabs();
        GridLayoutManager manager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
             manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }
        recyclerView.setLayoutManager(manager);

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

    private void tabs() {
        arrayList = new ArrayList<>();
        if(prefix.equals("A"))
            textViewheader.setText("REPORTS");


        for (int i = 0;i< NAME.length;i++){
            arrayList.add(new Item(NAME[i],prefix+i ,IMAGE[i], "#FFFFFF",""));
        }
        HomeAdapter adapter = new HomeAdapter(activity_cashier.this, arrayList, activity_cashier.this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(Item item) {
        final String setting_chk_run_y_repo = settings.getString("setting_chk_run_y_repo","0");
        final String setting_chk_run_y_his = settings.getString("setting_chk_run_y_his","0");
        final String setting_chk_all_y_repo = settings.getString("setting_chk_all_y_repo","0");
        final String setting_chk_run_w_repo = settings.getString("setting_chk_run_w_repo","0");
        switch(item.pos){
            case "A0":

if(setting_chk_run_w_repo.equals("1")) {
    showCustomDialog("Cashup", "Requesting X Intraday");
    cashup_w_intraday();
}else{
    Toasty.info( mContext,"Disabled By Admin!!!", Toast.LENGTH_SHORT).show();

}
                return;

            case "A1":
                if(setting_chk_run_y_repo.equals("1")) {
                    showCustomDialog("Cashup","Requesting Y Intraday");

                    cashup_y_intraday(setting_chk_all_y_repo);
                }else{
                    Toasty.info( mContext,"Disabled By Admin!!!", Toast.LENGTH_SHORT).show();
                }
                return;

            case "A2":

                Intent myIntentBanking = new Intent(mContext, activity_banking_detail.class);
                startActivity(myIntentBanking);
                return;

            case "A3":

                Intent myIntentActivation = new Intent(mContext, activity_activation.class);
                startActivity(myIntentActivation);
                return;

            case "A4":

                Intent myIntentZHistory = new Intent(mContext, activity_zhistory.class);
                startActivity(myIntentZHistory);
                return;
            case "A5":

        }



        // Toast.makeText(getApplicationContext(), item.pos + " is clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }





    public void cashup_w_intraday(){

        final Call<ResponseBody> call = apiService.cashup_w_intraday(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

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
                } catch (Exception ex)
                {
                    //
                    if (response.body() != null)
                    response.body().close();
                }

                if (res.contains("<error><err>")) {

                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");

                    stopCustomDialog("Problem",matcher);
                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    printingCustomDialog("Printing","Busy printing...");
                    PrinterTopitup.print_data(res);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    stopCustomDialog("No Internet","Please check that your internet connection is working.");
                }
                else {
                    stopCustomDialog("Problem",t.getMessage());
                }

            }

        });




    }




    public void cashup_y_intraday(String isAdmin) {

        final Call<ResponseBody> call = apiService.cashup_y_intraday(Topitup.cashUp,Topitup.TIU_LICENSE, Topitup.POSUSER_ID,isAdmin);

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

                if (res.contains("<error><err>")) {

                    String matcher = StringUtils.substringBetween(res, "<err>", "</err>");

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
        }
    };










}
