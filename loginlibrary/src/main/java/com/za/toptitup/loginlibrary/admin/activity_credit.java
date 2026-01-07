package com.za.toptitup.loginlibrary.admin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.credit_request;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_credit extends BaseAdminActivity {

    Realm realm;
    protected Topitup app;
    Context mContext;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    WebView wv_credit;
    Button btn_exit, btn_request, btn_process;
    EditText txt_request_amount, txt_otp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_credit);

        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        wv_credit = findViewById(R.id.wv_credit);

        txt_request_amount = findViewById(R.id.txt_request_amount);
        txt_request_amount.setVisibility(View.GONE);


        btn_request = findViewById(R.id.btn_request);
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                do_credit_request();

            }
        });


        btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_request.setVisibility(View.GONE);
        btn_exit.setVisibility(View.GONE);



        txt_otp = findViewById(R.id.txt_otp);
        txt_otp.setVisibility(View.GONE);

        btn_process = findViewById(R.id.btn_process);
        btn_process.setVisibility(View.GONE);
        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_otp.setEnabled(false);
                btn_process.setEnabled(false);

               if (txt_otp.getText().toString().trim().equals(otp)) {

                   process_credit();

               } else {

                   btn_exit.setVisibility(View.VISIBLE);

                   Toasty.error(mContext, "Incorrect OTP.", 8000, true).show();

               }

            }
        });




//        txt_terminal_name = (TextView)findViewById(R.id.txt_terminal_name);
//        chk_balance_cashier = (CheckBox)findViewById(R.id.chk_balance_cashier);
//        chk_balance_login = (CheckBox)findViewById(R.id.chk_balance_login);
//        chk_print_to_screen = (CheckBox)findViewById(R.id.chk_print_to_screen);
//        chk_print_barcode = (CheckBox)findViewById(R.id.chk_print_barcode);
//
//        chk_print_to_screen.setVisibility(View.GONE);
//
//        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
//        txt_terminal_name.setText(settings.getString("setting_terminal_name","").trim());
//
//        if (settings.getString("setting_balance_cashier","0").equals("1")) chk_balance_cashier.setChecked(true);
//        if (settings.getString("setting_balance_login","0").equals("1")) chk_balance_login.setChecked(true);
//
//        if (Topitup.DEVICE_TYPE.equals("Q1")) {
//            if (settings.getString("setting_print_barcode","0").equals("1")) chk_print_barcode.setChecked(true);
//        } else {
//            chk_print_barcode.setVisibility(View.GONE);
//        }


        //if (settings.getString("setting_print_to_screen","0").equals("1")) chk_print_to_screen.setChecked(true);

        check_credit_status();

    }





    private void process_credit()
    {

        showCustomDialog();

        final Call<credit_request> call = apiService.credit_request_process(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, request_amount);

        call.enqueue(new Callback<credit_request>(){

            @Override
            public void onResponse(Call<credit_request> call, Response<credit_request> response) {

                if(response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    otp = "";

                    String res = "";
                    try {

                        credit_request result = response.body();

                        txt_otp.setVisibility(View.GONE);
                        btn_process.setVisibility(View.GONE);

                        btn_exit.setVisibility(View.VISIBLE);

                        byte[] data = Base64.decode(result.cr_data, Base64.DEFAULT);
                        String hmtl = new String(data, StandardCharsets.UTF_8);

                        wv_credit.getSettings().setJavaScriptEnabled(true);
                        wv_credit.loadDataWithBaseURL("", hmtl, "text/html", "UTF-8", "");

                        dialog.dismiss();

                    } catch (Exception ex) {

                     /*   if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                        dialog.dismiss();
                    }
                }else {
                    dialog.dismiss();
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }

            }

            @Override
            public void onFailure(Call<credit_request> call, Throwable t) {
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }
                t.printStackTrace();
                dialog.dismiss();
            }

        });


    }










    String request_amount = "";
    String otp = "";

    private void do_credit_request()
    {

        txt_request_amount.setEnabled(false);
        btn_request.setEnabled(false);

        txt_request_amount.setVisibility(View.GONE);
        btn_request.setVisibility(View.GONE);

        request_amount = txt_request_amount.getText().toString().trim();

        if (request_amount.equals("")) {
            Toasty.error(mContext, "Please enter an amount.", 4000, true).show();
            return;
        }

        showCustomDialog();

        final Call<credit_request> call = apiService.credit_request(Topitup.TIU_LICENSE, Topitup.POSUSER_ID, request_amount);

        call.enqueue(new Callback<credit_request>(){

            @Override
            public void onResponse(Call<credit_request> call, Response<credit_request> response) {

                if(response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    //Timber.i("check_credit_status: res- " + response.body().toString());

                    otp = "";

                    String res = "";
                    try {

                        credit_request result = response.body();

                        //globalSettings.credit_req_otp = (string)json["cr_otp"];

                        //Toasty.success(mContext, result.cr_data, 8000, true).show();

//                    if (result.cr_allowed.equals("true")) {
//
//                        btn_request.setVisibility(View.VISIBLE);
//                        txt_request_amount.setVisibility(View.VISIBLE);
//                        txt_request_amount.requestFocus();
//
//                    } else {
//
//                        btn_exit.setVisibility(View.VISIBLE);
//                    }

                        if (result.cr_allowed.equals("true") && result.cr_allowed.equals("true")) {

                            btn_process.setVisibility(View.VISIBLE);
                            txt_otp.setVisibility(View.VISIBLE);
                            txt_otp.requestFocus();

                            otp = result.cr_otp;


                        } else {

                            btn_exit.setVisibility(View.VISIBLE);

                        }

                        byte[] data = Base64.decode(result.cr_data, Base64.DEFAULT);
                        String hmtl = new String(data, StandardCharsets.UTF_8);

                        wv_credit.getSettings().setJavaScriptEnabled(true);
                        wv_credit.loadDataWithBaseURL("", hmtl, "text/html", "UTF-8", "");

                        dialog.dismiss();

                    } catch (Exception ex) {
                    /*    if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                        dialog.dismiss();
                    }
                }else{
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }

            }

            @Override
            public void onFailure(Call<credit_request> call, Throwable t) {
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }
                t.printStackTrace();
                dialog.dismiss();
            }

        });



    }


    private void check_credit_status()
    {


        showCustomDialog();

        final Call<credit_request> call = apiService.credit_request_status(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);

        call.enqueue(new Callback<credit_request>(){

            @Override
            public void onResponse(Call<credit_request> call, Response<credit_request> response) {

                if(response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    //Timber.i("check_credit_status: res- " + response.body().toString());


                    String res = "";
                    try {

                        credit_request result = response.body();

                        //Toasty.success(mContext, result.cr_data, 8000, true).show();

                        if (result.cr_allowed.equals("true")) {

                            btn_request.setVisibility(View.VISIBLE);
                            txt_request_amount.setVisibility(View.VISIBLE);
                            txt_request_amount.requestFocus();

                        } else {

                            btn_exit.setVisibility(View.VISIBLE);
                        }


                        byte[] data = Base64.decode(result.cr_data, Base64.DEFAULT);
                        String hmtl = new String(data, StandardCharsets.UTF_8);

                        wv_credit.getSettings().setJavaScriptEnabled(true);
                        wv_credit.loadDataWithBaseURL("", hmtl, "text/html", "UTF-8", "");


                        dialog.dismiss();

                    } catch (Exception ex) {
                       /* if (response.raw() != null)
                            response.raw().close();*/
                        Toasty.error(mContext, ex.getMessage(), 8000, true).show();
                        dialog.dismiss();
                    }
                }else{
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();

                }

            }

            @Override
            public void onFailure(Call<credit_request> call, Throwable t) {
                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }
                t.printStackTrace();
                dialog.dismiss();
            }

        });



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
    Handler handler  = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();
            finish();
        }
    };






}
