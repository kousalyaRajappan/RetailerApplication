package com.za.toptitup.loginlibrary.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_users_detail extends BaseAdminActivity {

    Realm realm;

    Context mContext;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    EditText te_user_firstname;
    EditText te_user_lastname;
    EditText te_user_pin;

    Switch sw_is_admin;

    Button btn_save;
    Button btn_delete;

    Integer posuser_id = 0;

    pos_users pu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_users_detail);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        posuser_id = intent.getIntExtra("POSUSER_ID",0);



        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


       //Toasty.error(mContext, posuser_id, 8000, true).show();

        te_user_firstname = findViewById(R.id.te_user_firstname);
        te_user_lastname = findViewById(R.id.te_user_lastname);
        te_user_pin = findViewById(R.id.te_user_pin);
        sw_is_admin = findViewById(R.id.sw_is_admin);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //btn_save.setEnabled(false);
                //Toasty.error(mContext, "test", 8000, true).show();

                if (posuser_id == 0) {
                    add_new_user();
                } else {
                    save_user_detail();
                }

            }
        });



        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                btn_delete.setEnabled(false);
                //Toasty.error(mContext, "test", 8000, true).show();

                delete_user();

            }
        });

        if (posuser_id == 0) {
            btn_delete.setVisibility(View.GONE);
        }


        if (posuser_id == 0) {

             btn_save.setText("Add User");

        } else {

            pu = realm.where(pos_users.class).equalTo("posuser_id", posuser_id).findFirst();

            te_user_firstname.setText(pu.posuser_firstname);
            te_user_lastname.setText(pu.posuser_surname);
            te_user_pin.setText(pu.posuser_pin);
            sw_is_admin.setChecked( 1 == pu.posuser_isadmin);

        }
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



    public void delete_user(){

        final Call<ResponseBody> call = apiService.delete_posuser(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, String.valueOf(posuser_id));

        call.enqueue(new Callback<ResponseBody>(){

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

                } catch (Exception ex)
                {
                    if (response.body() != null)
                    response.body().close();
                }

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {
                    String err_msg = res;
                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }
                    if (res.toLowerCase().contains("err")) {
                        err_msg = res.replace("err:", "");
                    } else if (res.toLowerCase().contains("err:")) {
                        err_msg = res.replace("ERR:", "");
                    }
                    Toasty.error(mContext, err_msg, 8000, true).show();
                    return;
                }


                Toasty.success(mContext, "User Removed.", 6000, true).show();

                RealmResults<pos_users>  pos_users = realm.where(pos_users.class).equalTo("posuser_id", posuser_id).findAll();
                realm.beginTransaction();
                for (int i = 0; i < pos_users.size(); i++) {
                    pos_users.get(i).posuser_status = 2;
                }
                realm.commitTransaction();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                // stopCustomDialog("Problem",t.getMessage());
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

            }

        });


    }



    public void add_new_user(){


        if (te_user_firstname.getText().toString().trim().equals("")) {
            Toasty.error(mContext, "Please enter a name.", 4000, true).show();
            return;
        }
        if (te_user_lastname.getText().toString().trim().equals("")) {
            Toasty.error(mContext, "Please enter a name.", 4000, true).show();
            return;
        }
        if (te_user_pin.getText().toString().trim().equals("")) {
            Toasty.error(mContext, "Please enter a PIN.", 4000, true).show();
            return;
        }


        String posuser_isadmin = "0";
        if (sw_is_admin.isChecked()) posuser_isadmin = "1";

        final Call<ResponseBody> call = apiService.user_add(Topitup.TIU_LICENSE, Topitup.POSUSER_ID,
                te_user_firstname.getText().toString().trim(),
                te_user_lastname.getText().toString().trim(),
                te_user_pin.getText().toString().trim(),
                 "","1",
                posuser_isadmin,
                "","",""
                );

        call.enqueue(new Callback<ResponseBody>(){

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

                } catch (Exception ex)
                {
                    if (response.body() != null)
                    response.body().close();
                }

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {
                    String err_msg = res;
                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }
                    if (res.toLowerCase().contains("err")) {
                        err_msg = res.replace("err:", "");
                    } else if (res.toLowerCase().contains("err:")) {
                        err_msg = res.replace("ERR:", "");
                    }
                    Toasty.error(mContext, err_msg, 8000, true).show();
                    return;
                }



                Toasty.success(mContext, "User Added.", 6000, true).show();

                Intent intent = new Intent();
                //intent.putExtra("keyName", "Ok");
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                // stopCustomDialog("Problem",t.getMessage());

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

            }

        });


    }


    Integer posuser_isadmin = 0;
    public void save_user_detail(){

        //showCustomDialog("POS User","Saving User Details", false);

        if (sw_is_admin.isChecked()) posuser_isadmin = 1;

        final Call<ResponseBody> call = apiService.user_update_android(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, posuser_id, te_user_firstname.getText().toString(), te_user_lastname.getText().toString() , te_user_pin.getText().toString(), posuser_isadmin);

        call.enqueue(new Callback<ResponseBody>(){

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

                } catch (Exception ex)
                {
                    if (response.body() != null)
                    response.body().close();
                }

                //Toasty.info(mContext, res, 8000, true).show();

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String err_msg = "No Results.";

                    if (res.contains("<error><err>")) {
                        err_msg = StringUtils.substringBetween(res, "<error><err>", "</err></error>");
                    }

                    if (res.contains("ERR:")) {
                        err_msg = res.replace("ERR:","");
                    }

                    Toasty.error(mContext, err_msg, 8000, true).show();

                    return;

                }


                realm.beginTransaction();
                pu.posuser_firstname = te_user_firstname.getText().toString();
                pu.posuser_surname  = te_user_lastname.getText().toString();
                pu.posuser_pin = te_user_pin.getText().toString();
                pu.posuser_isadmin = posuser_isadmin;
                realm.commitTransaction();

                Toasty.success(mContext, "User Updated.", 8000, true).show();
                finish();


               // dialog.dismiss();

                //Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }


                // stopCustomDialog("Problem",t.getMessage());

                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

                //progressBar.setVisibility(View.GONE);
                // t.printStackTrace();

            }

        });



    }




    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;

    private void showCustomDialog(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);
            bt_close.setVisibility(View.VISIBLE);
        }

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


    private void stopCustomDialog(String sTitle, String sContent) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);
        bt_close.setVisibility(View.VISIBLE);
        pageLoadingWrapper.setVisibility(View.GONE);

    }


    // Hide after some seconds
    Handler handler  = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };



}
