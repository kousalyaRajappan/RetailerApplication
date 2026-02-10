package com.za.toptitup.loginlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;


import androidx.appcompat.widget.AppCompatButton;

import java.io.BufferedReader;
import java.io.StringReader;

import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_print_screen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.dialog_print_screen);

        this.setFinishOnTouchOutside(false);

       //s activity_main.printScreen = false;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);


        AppCompatButton bt_close = this.findViewById(R.id.bt_close);

        Intent intent = getIntent();
        String slip_to_print = intent.getExtras().getString("slip_to_print");

        String html = "";

        try {

            BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
            String line = null;
            while ((line = bufReader.readLine()) != null) {

                String prnt_line = "";
                String size = "";

                if (line.length() > 1) {
                    prnt_line = line.substring(1);
                    size = "" + line.charAt(0);
                }

                if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                    if (Topitup.PRINT_BARCODE.equals("1")) {
                        if(line.contains("BARCODE")) {
                            html += line.replace("BARCODE:", "");
                        }
                    }

                } else {


                    if (size.equals("1")) {

                        html += prnt_line + "<br/>";

                    } else {

                        html += "<span style=\"font-size:1.4em\">";
                        html += prnt_line + "</span><br/>";

                    }

                }



            }

        } catch (Exception ex)
        {
            //
        }


        WebView wb_slip = this.findViewById(R.id.wb_slip);
        wb_slip.getSettings().setJavaScriptEnabled(false);
        wb_slip.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }




}
