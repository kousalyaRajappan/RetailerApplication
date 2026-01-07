package com.za.toptitup.loginlibrary;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wiseasy.cashier.CashierHelper;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_pos_calculator extends AppCompatActivity {

    private RecyclerView recyclerView;

    BottomNavigationView mBottomNav;
    RealmResults<service_provider_item> service_provider_items;
    TextView textViewheader;
    String prefix = "A", spiver, amount;
    Context mContext;

    private final int REQUEST_CODE = 999;
    private final int RESULT_CODE = 210;


    private final String[] NAME = {""};
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    Realm realm;

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<fin_balance> tiu_fin_balance;
    private CardView cardview;

    private static long back_pressed;
    public static double mGrand_total2;
    public static Double txtQty;
    public static Double txtUnit_price;
    public static String txtdiscount = "0.00";
    private Button bill;
    private Button calc;
    private Button cash;
    private Button clear;
    private Button enter;
    /* access modifiers changed from: private */
    public double mAmount;
    /* access modifiers changed from: private */
    public double mCash;
    /* access modifiers changed from: private */
    public double mChange;
    /* access modifiers changed from: private */
    public double mGrand_total;
    /* access modifiers changed from: private */
    public int mQty;
    public static int tQty;

    private final HashMap params = new HashMap();
    private final String TAG = "invoke--ConsumeActivity";
    String result = "";
    String ret_copy = "";
    String orderno;
    TextView txtGrandTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_cal);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        textViewheader = findViewById(R.id.textViewheader);


        FullscreenCall();

        Realm.init(mContext);
        realm = Realm.getDefaultInstance();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
        spiver = tiu_settings.spi_ver;
        /* TIU HEADER */
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");


        String setting_tv_rand_value = settings.getString("setting_tv_rand_value", "0.00");
        String setting_tv_per_value = settings.getString("setting_tv_per_value", "0");


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi_pos);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);
        bt_close = dialog.findViewById(R.id.bt_close);
        btn_paper_load = dialog.findViewById(R.id.btn_paper_load);
        btn_Paper_ignore_time = dialog.findViewById(R.id.btn_Paper_ignore_time);
      /* WebView webView = (WebView) dialog.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.BLACK);
        webView.loadUrl(Topitup.BASE_URL_SYNC + "message/bankdetails/2/?=" + Topitup.TIU_LICENSE);

        webView.setWebViewClient(new webViewClient());
*/
        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/

        //AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        //recyclerView.setLayoutManager(layoutManager);

        this.mQty = 1;
        this.mAmount = 0.0d;
        tQty = 0;
        final EditText textQty = findViewById(R.id.txtQty);
        final EditText textUnit_price = findViewById(R.id.txtPrice);
        final EditText textCash = findViewById(R.id.txtCash);
        final TextView textCashn = findViewById(R.id.textCash);
        final TextView textChangen = findViewById(R.id.textChange);
        final View vwcash = findViewById(R.id.vwcash);
        final EditText textDiscount = findViewById(R.id.txtDiscount);
        final TextView Grand_total = findViewById(R.id.txtTotal);
        final TextView textChange = findViewById(R.id.txtChange);

        final TextView cashamt = findViewById(R.id.cashamt);

        final TextView txtitems = findViewById(R.id.txtitems);
        txtGrandTotal = findViewById(R.id.txtGrandTotal);

        final TextView textDiscountAmount = findViewById(R.id.txtDiscountAmount);

        final TextView confeertxtDiscountAmount = findViewById(R.id.confeertxtDiscountAmount);
        final TextView confeertxtDiscount = findViewById(R.id.confeertxtDiscount);

        final CheckBox chkconfeertxtDiscountAmount = findViewById(R.id.chkconfeertxtDiscountAmount);
        final CheckBox chkconfeertxtDiscount = findViewById(R.id.chkconfeertxtDiscount);


        confeertxtDiscountAmount.setText(setting_tv_rand_value);
        confeertxtDiscount.setText(setting_tv_per_value);


        final RadioGroup rGroup = findViewById(R.id.radioGroup);
        final RadioButton rb2 = findViewById(R.id.rb2);
        final RadioButton rb1 = findViewById(R.id.rb1);
        textUnit_price.setFilters(new InputFilter[]{
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    final int beforeDecimal = 8;
                    final int afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = textUnit_price.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        } else if (temp.indexOf(".") == -1) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });


        textUnit_price.setFocusableInTouchMode(true);
        textUnit_price.requestFocus();
        // final myDBClass myDb = new myDBClass(this);
        // myDb.getWritableDatabase();
        // myDb.deleteAll(

        textQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activity_pos_calculator.this.mQty = 0;
                    textQty.setText("");
                }
            }
        });


        findViewById(R.id.btnIncrease).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String temp = textQty.getText().toString();
                if (temp.equals(null) || temp.equals("")) {
                    activity_pos_calculator mainPage = activity_pos_calculator.this;
                    mainPage.mQty = mainPage.mQty + 1;
                    textQty.setText(String.valueOf(activity_pos_calculator.this.mQty));
                    return;
                }
                activity_pos_calculator.this.mQty = (int) Double.parseDouble(temp);
                activity_pos_calculator mainPage2 = activity_pos_calculator.this;
                mainPage2.mQty = mainPage2.mQty + 1;

                textQty.setText(String.valueOf(activity_pos_calculator.this.mQty));


                //mainPage2.tQty=mainPage2.tQty +  mainPage2.mQty;
                // txtitems.setText(new StringBuilder(String.valueOf(activity_pos_calculator.this.tQty)).toString());


            }
        });


        findViewById(R.id.btnDecrease).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String temp = textQty.getText().toString();
                if (temp.equals(null) || temp.equals("")) {
                    activity_pos_calculator mainPage = activity_pos_calculator.this;
                    mainPage.mQty = mainPage.mQty - 1;
                    textQty.setText(String.valueOf(activity_pos_calculator.this.mQty));
                    return;
                }
                activity_pos_calculator.this.mQty = (int) Double.parseDouble(temp);
                activity_pos_calculator mainPage2 = activity_pos_calculator.this;
                mainPage2.mQty = mainPage2.mQty - 1;

                textQty.setText(String.valueOf(activity_pos_calculator.this.mQty));


                // mainPage2.tQty=mainPage2.tQty -  mainPage2.mQty;
                //txtitems.setText(new StringBuilder(String.valueOf(activity_pos_calculator.this.tQty)).toString());
            }
        });

        this.cash = findViewById(R.id.btnCash);
        this.cash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textCash.requestFocus();
            }
        });


        Button btnbill = findViewById(R.id.btnBill);
        this.bill = findViewById(R.id.btnBill);
        this.bill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  MainPage.this.startActivity(new Intent(MainPage.this, ViewAll.class));
                if (rb1.isChecked()) {
                    if (textCash.getText().length() == 0) {
                        Toasty.error(mContext, "Enter an amount Paid!!!", 8000, true).show();
                        textCash.findFocus();
                        return;
                    }

                   /* try{
                        Float amntPaid=Float.valueOf(textCash.getText().toString());
                        Float totalgrand=Float.valueOf(txtGrandTotal.getText().toString().substring(2));
                    }

                    catch(ParseException e){
                    e.printStackTrace();

                    }



                  if(Float.valueOf(textCash.getText().toString())<Float.valueOf(txtGrandTotal.getText().toString().substring(2))){
// Float.valueOf(txtGrandTotal.getText().toString())
                        Toasty.error(mContext, "Enter an amount Paid!!!", 8000, true).show();
                        return;
                    }*/


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
/*
                    params.put("third_trans_no",sdf1.format(new Date()));
                    params.put("order_amount",txtGrandTotal.getText().toString());
                    params.put("pay_type", InvokeConstant.PAY_TYPE_BANK_CARD);
                    params.put("note","NA");
                    invokeconsume();
                    */


                    ret_copy = "";
                    ret_copy += "2" + tiu_settings.company_name + "\n";
                    ret_copy += "1\n";
                    // ret_copy += "1***********************\n";
                    ret_copy += "1CUSTOMER RECEIPT\n";
                    ret_copy += "1\n";
                    // ret_copy += "1***********************\n";
                    ret_copy += "1" + currentDateandTime + "\n";
                    ret_copy += "1\n";
                    ret_copy += "1Approved : " + txtGrandTotal.getText() + "\n";
                    ret_copy += "1\n";
                    ret_copy += "1Amount Paid : R" + textCash.getText() + "\n";
                    ret_copy += "1\n";
                    ret_copy += "1Change : " + textChange.getText() + "\n";
                    ret_copy += "1\n";
                    ret_copy += "1Top it Up\n";
                    ret_copy += "1www.topitup.co.za\n";
                    ret_copy += "1\n";
                    ret_copy += "1\n";
                    PrinterTopitup.print_data(ret_copy);


                } else {
                    if (Topitup.DEVICE_TYPE.equals("WPOS")) {
                        String ranval = (txtGrandTotal.getText().toString()).substring(2).replace(".", "");
                        Intent myIntent2 = new Intent(mContext, activity_adpay.class);
                        myIntent2.putExtra("key", ranval);
                        startActivity(myIntent2);
                    } else {
                        startNewActivity(activity_pos_calculator.this,"com.tallorder.pocketpos");
//                        String dataToSend = "Hello from App 1";
//
//                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, dataToSend);
//
////                        String targetPackage = "com.tallorder.pocketpos"; // Replace with your target app's package
////                        sendIntent.setPackage(targetPackage);
//
//                        Intent chooser = Intent.createChooser(sendIntent,"Select the app you want to use");
//                        PackageManager packageManager = getPackageManager();
//                        if (sendIntent.resolveActivity(packageManager) != null) {
//                            // Launch the app directly
//                            startActivityForResult(chooser,REQUEST_CODE);
//                        } else {
//                            // Handle case where no app can handle the intent
//                            Toast.makeText(activity_pos_calculator.this, "App is not installed or cannot handle the intent", Toast.LENGTH_SHORT).show();
//                        }


//                        int sdk = android.os.Build.VERSION.SDK_INT;
//                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
//                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                            clipboard.setText(txtGrandTotal.getText());
//                        } else {
//                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                            android.content.ClipData clip = android.content.ClipData.newPlainText("amount", txtGrandTotal.getText());
//                            clipboard.setPrimaryClip(clip);
//                        }
//                        notificationIntent.putExtra("amoun", txtGrandTotal.getText());


//                        Intent notificationIntent = getPackageManager().getLaunchIntentForPackage("com.tallorder.pocketpos");
//                        //  notificationIntent.setPackage(null); // The golden row !!!
//                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                        int sdk = android.os.Build.VERSION.SDK_INT;
//                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
//                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                            clipboard.setText(txtGrandTotal.getText());
//                        } else {
//                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                            android.content.ClipData clip = android.content.ClipData.newPlainText("amount", txtGrandTotal.getText());
//                            clipboard.setPrimaryClip(clip);
//                        }
//                        notificationIntent.putExtra("amoun", txtGrandTotal.getText());
//                        startActivity(notificationIntent);

                    }

/*
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    String currentDateandTime = sdf.format(new Date());
                    if(Topitup.DEVICE_TYPE.equals("WPOS") && Topitup.enable_realtime_swipe.equals("1")) {
                        getorderno();
                        String ranval = (txtGrandTotal.getText().toString()).substring(2).replace(".", "");
                        amount = (txtGrandTotal.getText().toString()).substring(2).replace(".", "");
                        params.put("third_trans_no", orderno);
                        params.put("order_amount", ranval);
                        params.put("pay_type", InvokeConstant.PAY_TYPE_BANK_CARD);
                        params.put("note", "NA");
                        invokeconsume();
                    }
//prabha
                    //result="{\"code\":0,\"msg\":\"success\",\"data\":{\"third_trans_no\":\"20211104132600\",\"order_amount\":100,\"pay_type\":\"1001\",\"note\":\"NA\",\"trans_type\":1,\"trans_status\":1,\"actual_amount\":0,\"channel_discount_amount\":0,\"bank\":{\"card_no\":1234XXXXXXX3457\"\",\"voucher_no\":\"000010\",\"batch_no\":\"000001\",\"refer_no\":\"000000000008\",\"trans_time\":\"2021-11-05 12:14:49\"}}}";



                    ret_copy += "2"+tiu_settings.company_name+"\n";
                    ret_copy += "1\n";
                    // ret_copy += "1***********************\n";
                    ret_copy += "1MERCHANT RECEIPT\n";
                    ret_copy += "1\n";
                    // ret_copy += "1***********************\n";
                    ret_copy += "1"+currentDateandTime+"\n";
                    ret_copy += "1\n";
                   // ret_copy += "1Approved : "+txtGrandTotal.getText()+"\n";
                   /* ret_copy += "1\n";
                    ret_copy += "1Amount Paid : R"+textCash.getText()+"\n";
                    ret_copy += "1\n";
                    ret_copy += "1Change : "+textChange.getText()+"\n";
                    ret_copy += "1\n";
                    ret_copy += "1Top it Up\n";
                    ret_copy += "1www.topitup.co.za\n";
                    ret_copy += "1\n";
                    ret_copy += "1\n";
                    */

                    // Printer.print_data(ret_copy);

                }

            }
        });
        this.calc = findViewById(R.id.btnCalc);


        this.calc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tempQty = textQty.getText().toString();
                String tempUnit_price = textUnit_price.getText().toString();
                if (tempQty.equals(null) || tempQty.equals("") || tempUnit_price.equals(null) || tempUnit_price.equals("")) {
                    System.out.println("in if");
                    return;
                }
                // System.out.println("in else");
                activity_pos_calculator.this.mAmount = Double.parseDouble(tempQty) * Double.parseDouble(tempUnit_price);


                activity_pos_calculator mainPage = activity_pos_calculator.this;
                mainPage.mGrand_total = mainPage.mGrand_total + activity_pos_calculator.this.mAmount;
                activity_pos_calculator.mGrand_total2 = activity_pos_calculator.this.mGrand_total;
                activity_pos_calculator.txtQty = Double.valueOf(Double.parseDouble(tempQty));

                activity_pos_calculator.tQty = activity_pos_calculator.tQty + Integer.parseInt(tempQty);
                //int ttqty=Integer.parseInt(String.valueOf(activity_pos_calculator.tQty));
                txtitems.setText(String.valueOf(tQty));

                //activity_pos_calculator.this.mGrand_total=activity_pos_calculator.this.mGrand_total+2;

                double per_conv_amount = 0;
                double per_conv_fee = 0;

                if (chkconfeertxtDiscountAmount.isChecked())
                    per_conv_amount = Double.valueOf(confeertxtDiscountAmount.getText().toString());

                if (chkconfeertxtDiscount.isChecked())
                    per_conv_fee = (Double.valueOf(confeertxtDiscount.getText().toString()) * activity_pos_calculator.this.mGrand_total) / 100;

                activity_pos_calculator.txtUnit_price = Double.valueOf(Double.parseDouble(tempUnit_price));
                Grand_total.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total)));
                txtGrandTotal.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total + per_conv_fee + per_conv_amount)));
                if (activity_pos_calculator.this.mGrand_total > 0) {
                    btnbill.setVisibility(View.VISIBLE);
                } else {
                    btnbill.setVisibility(View.INVISIBLE);
                }

                //if (myDb.InsertData(String.format("%.2f", new Object[]{activity_pos_calculator.txtQty}), String.format("%.2f", new Object[]{activity_pos_calculator.txtUnit_price}), String.format("%.2f", new Object[]{Double.valueOf(MainPage.this.mAmount)})) > 0) {
                //Toast.makeText(activity_pos_calculator.this, "Add Successfully", 1).show();
                // } else {
                //  Toast.makeText(MainPage.this, "Add Failed.", 1).show();
                // }
                activity_pos_calculator.this.mQty = 1;
                textQty.setText(String.valueOf(activity_pos_calculator.this.mQty));
                textUnit_price.setText("");
                textUnit_price.requestFocus();
            }
        });


        this.enter = findViewById(R.id.btnEnter);
        this.enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tempCash = textCash.getText().toString();
                if (!tempCash.equals(null) && !tempCash.equals("")) {
                    activity_pos_calculator.this.mCash = Double.parseDouble(tempCash);
                    activity_pos_calculator.this.mChange = activity_pos_calculator.this.mCash - activity_pos_calculator.mGrand_total2;
                    textChange.setText(String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mChange)));
                    //  ((InputMethodManager) activity_pos_calculator.this.getSystemService(INPUT_SERVICE)).hideSoftInputFromWindow(activity_pos_calculator.this.getCurrentFocus().getWindowToken(), 2);
                }
            }
        });
        textDiscount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String a = s.toString();
                if (a.equals(null) || a.equals("")) {
                    a = "0.00";
                }
                activity_pos_calculator.txtdiscount = a;
                System.out.println("Discout=" + a);
                Double discount = Double.valueOf(activity_pos_calculator.this.mGrand_total - ((Double.parseDouble(a) / 100.0d) * activity_pos_calculator.this.mGrand_total));
                activity_pos_calculator.mGrand_total2 = discount.doubleValue();
                Grand_total.setText("R " + String.format("%.2f", discount));
                txtGrandTotal.setText("R " + String.format("%.2f", discount));

            }
        });


        textDiscountAmount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String a = s.toString();
                if (a.equals(null) || a.equals("")) {
                    a = "0.00";
                }

                if (activity_pos_calculator.this.mGrand_total > 0) {
                    Double discount = Double.valueOf(activity_pos_calculator.this.mGrand_total - ((Double.parseDouble(a))));
                    activity_pos_calculator.mGrand_total2 = discount.doubleValue();
                    Grand_total.setText("R " + String.format("%.2f", discount));
                    txtGrandTotal.setText("R " + String.format("%.2f", discount));
                }
            }
        });

        textCash.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempCash = textCash.getText().toString();
                if (!tempCash.equals(null) && !tempCash.equals("")) {
                    activity_pos_calculator.this.mCash = Double.parseDouble(tempCash);
                    activity_pos_calculator.this.mChange = activity_pos_calculator.this.mCash - activity_pos_calculator.mGrand_total2;
                    textChange.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mChange)));
                    //  ((InputMethodManager) activity_pos_calculator.this.getSystemService(INPUT_SERVICE)).hideSoftInputFromWindow(activity_pos_calculator.this.getCurrentFocus().getWindowToken(), 2);
                }

            }
        });


        chkconfeertxtDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                             @Override
                                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                 double per_conv_amount = 0;
                                                                 double per_conv_fee = 0;
                                                                 double totFee = 0;

                                                                 if (chkconfeertxtDiscountAmount.isChecked())
                                                                     per_conv_amount = Double.valueOf(confeertxtDiscountAmount.getText().toString());

                                                                 if (chkconfeertxtDiscount.isChecked())
                                                                     per_conv_fee = (Double.valueOf(confeertxtDiscount.getText().toString()) * activity_pos_calculator.this.mGrand_total) / 100;


                                                                 totFee = per_conv_fee + per_conv_amount;

                                                                 if (isChecked) {
                                                                     txtGrandTotal.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total + totFee)));
                                                                 } else {
                                                                     txtGrandTotal.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total)));

                                                                 }

                                                             }
                                                         }
        );


        chkconfeertxtDiscountAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                   @Override
                                                                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                                       double per_conv_amount = 0;
                                                                       double per_conv_fee = 0;
                                                                       double totFee = 0;

                                                                       if (chkconfeertxtDiscountAmount.isChecked())
                                                                           per_conv_amount = Double.valueOf(confeertxtDiscountAmount.getText().toString());

                                                                       if (chkconfeertxtDiscount.isChecked())
                                                                           per_conv_fee = (Double.valueOf(confeertxtDiscount.getText().toString()) * activity_pos_calculator.this.mGrand_total) / 100;


                                                                       totFee = per_conv_fee + per_conv_amount;

                                                                       if (isChecked) {
                                                                           txtGrandTotal.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total + totFee)));
                                                                       } else {
                                                                           txtGrandTotal.setText("R " + String.format("%.2f", Double.valueOf(activity_pos_calculator.this.mGrand_total)));

                                                                       }

                                                                   }
                                                               }
        );


        this.clear = findViewById(R.id.btnClear);
        final TextView textView = Grand_total;
        final EditText editText = textQty;
        final EditText editText2 = textUnit_price;
        //final myDBClass mydbclass = myDb;


        this.clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainPage.this);
                //alertDialog.setTitle("Confirm Clear...");
                //alertDialog.setMessage("Are you sure you want delete this?");
                //alertDialog.setIcon(C0084R.C0085drawable.ic_action_warning);


                //   final myDBClass mydbclass = mydbclass;


            }
        });


        mBottomNav.setSelectedItemId(R.id.action_dumm1);
        mBottomNav.findViewById(R.id.action_dumm1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        // mBottomNav.findViewById(R.id.action_dumm1).setVisibility(View.INVISIBLE);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              /*  switch (item.getItemId()) {
                    case R.id.action_back:

                        onBackPressed();
                        return true;

                    case R.id.action_clear:
                        recreate();


                        return true;
                    default:
                        return true;
                }*/
                if (item.getItemId() == R.id.action_back) {
                    onBackPressed();
                    return true;

                } else if (item.getItemId() == R.id.action_clear) {
                    recreate();
                    return true;

                } else {
                    return true;
                }

            }

        });


        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    if (checkedRadioButton.getText().equals("Pay by Card")) {
                        textCash.setVisibility(View.INVISIBLE);
                        textCashn.setVisibility(View.INVISIBLE);
                        textChangen.setVisibility(View.INVISIBLE);
                        textChange.setVisibility(View.INVISIBLE);
                        vwcash.setVisibility(View.INVISIBLE);
                        cashamt.setVisibility(View.INVISIBLE);
                    } else {

                        textCash.setVisibility(View.VISIBLE);
                        textCashn.setVisibility(View.VISIBLE);
                        textChangen.setVisibility(View.VISIBLE);
                        textChange.setVisibility(View.VISIBLE);
                        vwcash.setVisibility(View.VISIBLE);
                        cashamt.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        rb2.setEnabled(true);
        rb2.isChecked();
        /*
        Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("com.tallorder.pocketpos");
        if (launchIntentp != null) {
rb2.setEnabled(true);
            rb2.isChecked();
        }else{

            rb2.setEnabled(false);
            rb1.isChecked();
        }
*/


        ImageView imgView = findViewById(R.id.imgview);

        imgView.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                // showCustomDialog("Information", "", true);
                Intent myIntentZHistory = new Intent(mContext, activity_mpos_settle_detail.class);
                startActivity(myIntentZHistory);
            }
        });


        ImageView imgview1 = findViewById(R.id.imgview1);

        imgview1.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                do_show_dlg_conv();
            }
        });


    }


    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        FullscreenCall();
        if (dialog != null)
            dialog.dismiss();
        super.onResume();
    }


    public void onclick_btn_action(View v) {
        EditText textUnitPrice = findViewById(R.id.txtPrice);
        /*switch (v.getId()) {

            case R.id.ten: {

                textUnitPrice.setText("10.00");
                return;
            }
            case R.id.twenty: {
                textUnitPrice.setText("20.00");
                return;
            }
            case R.id.thirty: {
                textUnitPrice.setText("30.00");
                return;
            }
            case R.id.fifty: {
                textUnitPrice.setText("50.00");
                return;
            }
            case R.id.hundred: {
                textUnitPrice.setText("100.00");
                return;
            }
            case R.id.twohundred: {
                textUnitPrice.setText("200.00");
            }
        }*/
        if (v.getId() == R.id.ten) {
            textUnitPrice.setText("10.00");

        } else if (v.getId() == R.id.twenty) {
            textUnitPrice.setText("20.00");

        } else if (v.getId() == R.id.thirty) {
            textUnitPrice.setText("30.00");

        } else if (v.getId() == R.id.fifty) {
            textUnitPrice.setText("50.00");

        } else if (v.getId() == R.id.hundred) {
            textUnitPrice.setText("100.00");

        } else if (v.getId() == R.id.twohundred) {
            textUnitPrice.setText("200.00");
        }

    }


    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;
    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;

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

            bt_close.setVisibility(View.VISIBLE);
            btn_paper_load.setVisibility(View.GONE);

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


    public class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @SuppressLint("ResourceAsColor")
    private void do_show_dlg_conv() {


        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_pos_calc);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();


        String setting_tv_rand_value = settings.getString("setting_tv_rand_value", "0.00");
        String setting_tv_per_value = settings.getString("setting_tv_per_value", "0");
        Button btn_close = dialog.findViewById(R.id.btn_close);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        final TextView tv_rand_value = dialog.findViewById(R.id.tv_rand_value);
        final TextView tv_per_value = dialog.findViewById(R.id.tv_per_value);
        tv_rand_value.setText(setting_tv_rand_value);
        tv_per_value.setText(setting_tv_per_value);

        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();
                FullscreenCall();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editor.putString("setting_tv_rand_value", tv_rand_value.getText().toString());
                editor.putString("setting_tv_per_value", tv_per_value.getText().toString());

                editor.commit();

                dialog.dismiss();
                FullscreenCall();
            }
        });


        dialog.show();


    }


    private void invokeconsume() {


        CashierHelper.consume(mContext, this.params, new CashierHelper.PayCallBack() {
            public void success(@Nullable String data) {
                Log.d(activity_pos_calculator.this.TAG, "消费成功 success :" + data);
                Toasty.success(activity_pos_calculator.this, "Payment Approved", Toast.LENGTH_LONG).show();
                //tv_result_consume.setText(data);
                JSONObject json = null;    // create JSON obj from string
                try {
                    json = new JSONObject(data);
                    Toasty.success(mContext, result, 8000, true).show();
                    String msg = json.getString("msg");
                    String code = json.getString("code");// this will return correct
                    String datas = json.getString("data");

                    if (code.equals("0")) {
                        JSONObject json1 = new JSONObject(datas);
                        String trans_status = "";
                        trans_status = json1.getString("trans_status");

                        String bank = json1.getString("bank");

                        JSONObject json2 = new JSONObject(bank);

                        String card_no = json2.getString("card_no");
                        String voucher_no = json2.getString("voucher_no");
                        String batch_no = json2.getString("batch_no");
                        String refer_no = json2.getString("refer_no");
                        String trans_time = json2.getString("trans_time");
                      /*  Topitup.card_no=card_no;
                        Topitup.voucher_no=voucher_no;
                        Topitup.batch_no=batch_no;
                        Topitup.refer_no=refer_no;
                        Topitup.trans_time=trans_time;
                        Topitup.amount=amount;
                        Topitup.orderno=orderno;
                        Topitup.enable_realtime_swipe="1";*/
                        // calladapyAPI(card_no,voucher_no,batch_no,refer_no,trans_time,amount,"Approved");

                        // Intent myIntent2 = new Intent(mContext, activity_main.class);
                        //  startActivity(myIntent2);


                    } else {


                    }

                    //  Toasty.error(mContext, trans_status, 8000, true).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            public void failed(@Nullable String errInfo) {
                Log.d(activity_pos_calculator.this.TAG, "消费失败 failed :" + errInfo);
                String msg = "Declined";
                Toast.makeText(activity_pos_calculator.this, getString(R.string.consume_fail), Toast.LENGTH_LONG).show();
                // tv_result_consume.setText(errInfo);
                // Printer.print_data(errInfo);
                JSONObject json1 = null;
                try {
                    json1 = new JSONObject(errInfo);
                    msg = json1.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ret_copy += "1" + msg + " : " + txtGrandTotal.getText() + "\n";
                // ret_copy += "1"+msg+": "+amount+"\n";
                   /* ret_copy += "1\n";
                    ret_copy += "1Amount Paid : R"+textCash.getText()+"\n";
                    ret_copy += "1\n";
                    ret_copy += "1Change : "+textChange.getText()+"\n"; */
                ret_copy += "1\n";
                ret_copy += "1Top it Up\n";
                ret_copy += "1www.topitup.co.za\n";
                ret_copy += "1\n";
                ret_copy += "1\n";


                PrinterTopitup.print_data(ret_copy);


                result = errInfo;
            }
        });

    }


    private void getorderno() {
        /*
        1->Real Time Settlement
        2->Daily Settlement
        3->Bank Settlement
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf1.format(new Date());

        orderno = "1" + Topitup.POSUSER_ID + currentDateandTime;

    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle the result
                    Intent data = result.getData();
                    if (data != null) {
                        String response = data.getStringExtra("resultKey");
                        Toast.makeText(this, "Received: " + response, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No result received", Toast.LENGTH_SHORT).show();
                }
            });

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        if (intent == null) {
            // Bring user to the market or let them choose an app, as the package is not installed
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooser = Intent.createChooser(intent,"Select the app you want to use");
        if(intent.resolveActivity(getPackageManager())!=null) {
            intent.putExtra("reference", "rafiq");
            intent.putExtra("amount", txtGrandTotal.getText().toString());
            startActivityForResult(chooser, REQUEST_CODE);
        }

    }
}
