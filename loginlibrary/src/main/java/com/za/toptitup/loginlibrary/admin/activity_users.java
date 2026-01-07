package com.za.toptitup.loginlibrary.admin;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.adapters.AdapterListBasic;
import com.za.toptitup.loginlibrary.adapters.UserListAdapter;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.utils.Tools;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_users extends BaseAdminActivity {

    private View parent_view;

    Realm realm;

    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;


    private RecyclerView recyclerView;
    private AdapterListBasic mAdapter;

    private FloatingActionButton btn_add_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_users);
        parent_view = findViewById(android.R.id.content);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

       // initToolbar();

        btn_add_user = findViewById(R.id.btn_add_user);
        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent myIntent = new Intent(mContext, activity_users_detail.class);
            myIntent.putExtra("POSUSER_ID", 0);
            startActivityForResult(myIntent, SECOND_ACTIVITY_REQUEST_CODE);
            //startActivity(myIntent);

            }
        });

        get_posusers();
        BottomNavigationView mBottomNav  = findViewById(R.id.bottom_navigation);


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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("POS Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                //String returnString = data.getStringExtra("keyName");

                get_posusers();
            }
        }
    }


    private UserListAdapter adapter;
    ListView listView;


    public void get_posusers(){

        RealmResults<pos_users> pos_users = realm.where(pos_users.class).findAll();
        realm.beginTransaction();
        for (int i = 0; i < pos_users.size(); i++) {
            pos_users.get(i).posuser_status = 2;
        }
        realm.commitTransaction();



        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>(){

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Toasty.error(mContext, response.toString(), 8000, true).show();

                //List<pos_users> rs = response.body();

                //Timber.i(response.body().toString());

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


                    RealmResults<pos_users> pos_users = realm.where(pos_users.class)
                        .equalTo("posuser_status", 1)
                        .findAll();

                    adapter = new UserListAdapter(pos_users);

                    listView = findViewById(R.id.list_users);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Timber.i(" ON PRESS ");

                        pos_users item = adapter.getItem(position);
                        if (item == null) {
                            return;
                        }

                        //Toasty.error(mContext, String.valueOf(item.posuser_id) , 8000, true).show();

                        Intent myIntent = new Intent(mContext, activity_users_detail.class);
                        myIntent.putExtra("POSUSER_ID", item.posuser_id);
                        startActivity(myIntent);

                        }
                    });



                } catch (Exception ex)
                {

                    Timber.i("ERR get_posusers: " + ex.getMessage());

                }


                //Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


            }

        });



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

}
