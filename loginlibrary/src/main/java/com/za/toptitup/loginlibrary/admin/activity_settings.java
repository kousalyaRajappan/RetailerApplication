package com.za.toptitup.loginlibrary.admin;

import static sdk.PrinterCommand.POS_Set_Cashbox;
import static com.za.toptitup.loginlibrary.utils.Topitup.PRINT_BARCODE;
import static com.za.toptitup.loginlibrary.utils.Topitup.getAppContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.printer.Printer;
import com.zj.usbsdk.UsbController;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import com.za.toptitup.loginlibrary.BaseAdminActivity;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.activity_admin;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import com.za.toptitup.loginlibrary.bluetooth.DeviceListActivity;
import com.za.toptitup.loginlibrary.sdk.PrinterCommand;
import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.MyItem;
import com.za.toptitup.loginlibrary.model.ReprintList;
import com.za.toptitup.loginlibrary.model.swipetxn;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class activity_settings extends BaseAdminActivity implements OnItemSelectedListener {

    public static final int MESSAGE_STATE_CHANGE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final boolean DEBUG = true;
    private static final String CHINESE = "GBK";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    // Member object for the services
    public static BluetoothService mService = null;

    public static String bluetoothMsg = "welcome to\n\n" +
            "topitup Team";
    SharedPreferences settings;
    public static boolean isBluetoothConnected = false;
    @SuppressLint("HandlerLeak")
    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SharedPreferences settings = getAppContext().getSharedPreferences("TIUPREF", 0);

            SharedPreferences.Editor editor = settings.edit();
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i("TAG", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:

                            Toast.makeText(Topitup.getAppContext(), "bluetooth connected", Toast.LENGTH_LONG).show();

                            editor.putString("printer", "bluetooth");
                            editor.commit();
                            isBluetoothConnected = true;


                            if (!bluetoothMsg.equals("")) {

                                SendDataByte(PrinterCommand.POS_Print_Text(bluetoothMsg, CHINESE, 0, 0, 0, 0), getAppContext());
                                SendDataByte(PrinterCommand.POS_Set_Cut(1), getAppContext());
                                SendDataByte(PrinterCommand.POS_Set_PrtInit(), getAppContext());
                            }
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            isBluetoothConnected = false;
                            Toast.makeText(Topitup.getAppContext(), "bluetooth connecting", Toast.LENGTH_LONG).show();

                            break;
                        case BluetoothService.STATE_LISTEN:
                            isBluetoothConnected = false;
//                            Toast.makeText(Topitup.getAppContext(), "bluetooth listen", Toast.LENGTH_LONG).show();
                            break;

                        case BluetoothService.STATE_NONE:
                            isBluetoothConnected = false;
                            /*Toast.makeText(Topitup.getAppContext(), "bluetooth none", Toast.LENGTH_LONG).show();
                            editor.putString("printer", "inner");
                            editor.commit();*/
                            break;
                    }
                    break;


            }
        }
    };
    protected Topitup app;
    Realm realm;
    Context mContext;
    String rg;
    //  private Printing printing;
    boolean isMute = false;
    LinearLayout ll_sallah;
    RealmResults<swipetxn> swipetxns;
    ArrayList<MyItem> movies = new ArrayList<MyItem>();
    ReprintList listAdapter;
    TextView txt_terminal_name, tiu_title_header, textView10, textView101, mpos_user_group, mpos_user_name, mpos_user_pwd;
    CheckBox chk_allow_cashier_cash_drawer, chk_printer_cash_box, chk_print_ele_copy, chk_balance_cashier, chk_balance_cashier_bills, chk_balance_cashier_commission, chk_balance_cashier_swipe, chk_balance_login, chk_balance_bills, chk_balance_commission, chk_balance_swipe, chk_print_to_screen, chk_print_barcode, chk_print_bypass, chk_time_out, chk_add_pay,chk_disclaimer, chk_last_sale, chk_balance_admin, chk_balance_admin_bills, chk_balance_admin_commission, chk_balance_admin_swipe, chk_print_address, chk_bypass_calculator, chk_slip_cancel, chk_slip_logout;
    Button btn_ping_tools, btn_blutooth, btn_blutooth_unpair, btn_q1_test, btn_usb, btn_bluetooth_;
    CheckBox chk_cashier_own_sales, chk_disable_cashier_ele, chk_disable_cashier_bill, chk_disable_cashier_sale_his, chk_allow_cashier_rep_ser, chk_last_vou_admin, chk_last_vou_cashier, chk_over_bill_pay_admin, chk_over_bill_pay_cashier, chk_run_w_repo, chk_run_y_repo, chk_run_y_his, chk_all_y_repo, chk_mpos_disclaimer, chk_auto_mpos;
    LinearLayout gensettings, slipsettings, cashiersettings, elesettings, mpossettings;
    ListView lst_pos_txn;
    CheckBox check_salaha;
    RadioGroup rdog_printer;
    RadioButton rdo_bluetooth, rdo_usb, rdo_inner;
    private RadioGroup mFirstGroup;
    private RadioGroup mSecondGroup, mposfirstgroup;
    private boolean isChecking = true;
    private int mCheckedId;
    private NotificationManager n;
    private AudioManager audioManager;
    public static BluetoothAdapter mBluetoothAdapter = null;
    private Button btnBottomTestPrint;
    private int REQUEST_BLUETOOTH_PERMISSIONS = 121;
    SharedPreferences prefs;
    public static Printer printerp052;

    public static void SendDataString(String data, Context con) {

//      BluetoothService  mServiceNew = new BluetoothService(con, mHandler);

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(con, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void printLogobluetooth(Context con) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {

            return;
        }

        Bitmap bmp = BitmapFactory.decodeResource(con.getResources(), R.drawable.logo_splash);

        try {
            mService.printLogo(bmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SendDataByte(byte[] data, Context con) {
//        BluetoothService  mServiceNew = new BluetoothService(con, mHandler);


        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(con, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        mService.write(data);
    }


    public static void interpretPrinterStatus(byte status) {
        int statusCode = status & 0xFF; // Convert to unsigned int

        // Check the status for paper presence
        boolean paperPresent = (statusCode & 0x60) == 0; // Check bits for paper presence
        boolean paperNearEnd = (statusCode & 0x0C) != 0; // Check bits for near-end status

        if (!paperPresent) {
            Log.d("Printer Status", "Paper is out.");
        } else if (paperNearEnd) {
            Log.d("Printer Status", "Paper is near end.");
        } else {
            Log.d("Printer Status", "Paper is present.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;


        setContentView(R.layout.activity_settings);
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        txt_terminal_name = findViewById(R.id.txt_terminal_name);
        mpos_user_group = findViewById(R.id.mpos_user_group);
        mpos_user_name = findViewById(R.id.mpos_user_name);
        mpos_user_pwd = findViewById(R.id.mpos_user_pwd);
        textView10 = findViewById(R.id.textView10);
        textView101 = findViewById(R.id.textView101);
        gensettings = findViewById(R.id.gensettings);
        slipsettings = findViewById(R.id.slipsettings);
        elesettings = findViewById(R.id.elesettings);
        mpossettings = findViewById(R.id.mpossettings);
        cashiersettings = findViewById(R.id.cashiersettings);
        chk_balance_cashier = findViewById(R.id.chk_balance_cashier);
        chk_balance_cashier_bills = findViewById(R.id.chk_balance_cashier_bills);
        chk_balance_cashier_commission = findViewById(R.id.chk_balance_cashier_commission);
        chk_balance_cashier_swipe = findViewById(R.id.chk_balance_cashier_swipe);
        btnBottomTestPrint = findViewById(R.id.btn_bottom_test_print);
        chk_balance_admin = findViewById(R.id.chk_balance_admin);
        chk_balance_admin_bills = findViewById(R.id.chk_balance_admin_bills);
        chk_balance_admin_commission = findViewById(R.id.chk_balance_admin_commission);
        chk_balance_admin_swipe = findViewById(R.id.chk_balance_admin_swipe);
        chk_print_address = findViewById(R.id.chk_print_address);
        chk_balance_login = findViewById(R.id.chk_balance_login);
        chk_balance_bills = findViewById(R.id.chk_balance_bills);
        chk_balance_commission = findViewById(R.id.chk_balance_commission);
        chk_balance_swipe = findViewById(R.id.chk_balance_swipe);
        chk_last_sale = findViewById(R.id.chk_last_sale);
        chk_print_to_screen = findViewById(R.id.chk_print_to_screen);
        chk_print_barcode = findViewById(R.id.chk_print_barcode);
        chk_print_bypass = findViewById(R.id.chk_print_by_pass);
        chk_add_pay = findViewById(R.id.chk_add_pay);
        chk_time_out = findViewById(R.id.chk_time_out);
        chk_print_ele_copy = findViewById(R.id.chk_print_ele_copy);
        tiu_title_header = findViewById(R.id.tiu_title_header);
        chk_mpos_disclaimer = findViewById(R.id.chk_mpos_disclaimer);
        chk_bypass_calculator = findViewById(R.id.chk_bypass_calculator);
        chk_slip_cancel = findViewById(R.id.chk_slip_cancel);
        chk_slip_logout = findViewById(R.id.chk_slip_logout);
        chk_printer_cash_box = findViewById(R.id.chk_printer_cash_box);
        chk_allow_cashier_cash_drawer = findViewById(R.id.chk_allow_cashier_cash_drawer);
        ll_sallah = findViewById(R.id.ll_sallah);
        ll_sallah.setVisibility(View.GONE);
        check_salaha = findViewById(R.id.check_salaha);
        rdog_printer = findViewById(R.id.rdog_printer);
        rdo_bluetooth = findViewById(R.id.rdo_bluetooth);
        rdo_usb = findViewById(R.id.rdo_usb);
        rdo_inner = findViewById(R.id.rdo_inner);
         settings = getSharedPreferences("TIUPREF", 0);

        onClickShowDetails();


        if (Topitup.DEVICE_TYPE.equals("WPOS")) {
            lst_pos_txn = findViewById(R.id.lst_pos_txn);
            lst_pos_txn.setVisibility(View.VISIBLE);
            TextView textusergroup = findViewById(R.id.textusergroup);
            TextView textusername = findViewById(R.id.textusername);
            TextView textpasswrd = findViewById(R.id.textpasswrd);
            textusergroup.setVisibility(View.GONE);
            mpos_user_group.setVisibility(View.GONE);
            mpos_user_pwd.setVisibility(View.GONE);
            mpos_user_name.setVisibility(View.GONE);
            chk_bypass_calculator.setVisibility(View.GONE);
            chk_bypass_calculator.isChecked();
            chk_mpos_disclaimer.setVisibility(View.GONE);
            textusername.setVisibility(View.GONE);
            textpasswrd.setVisibility(View.GONE);
        }

        if (Topitup.DEVICE_TYPE.equals("WPOS") || Topitup.DEVICE_TYPE.equals("Q1")) {

            rdo_inner.setVisibility(View.VISIBLE);
        } else {
            rdo_inner.setVisibility(View.GONE);
        }

        chk_auto_mpos = findViewById(R.id.chk_auto_mpos);


        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if (b != null)
            value = b.getInt("key");


        mFirstGroup = findViewById(R.id.first_group);
        mSecondGroup = findViewById(R.id.second_group);
        mposfirstgroup = findViewById(R.id.mposfirstgroup);
        chk_disclaimer = findViewById(R.id.chk_disclaimer);

        boolean isAppInstalled = appInstalledOrNot("za.co.topitup.salaahapp");
        if (isAppInstalled) {
            ll_sallah.setVisibility(View.GONE);
        } else {
            ll_sallah.setVisibility(View.GONE);
        }

        final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();
        String licenseId = tiu_settings1.license_id;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 102);

        }
       /* String serialNumber = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serialNumber = Build.getSerial();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d("responseData", String.valueOf(e.getMessage()));
        }*/


        String device_sl_number = getSerialNumber();
        textView10.setText("Terminal Name : TOPITUP #" + licenseId + "_" + device_sl_number);
        textView101.setText("Serial : " + Topitup.POSUSER_ID);
        btn_q1_test = findViewById(R.id.btn_q1_test);
        // Printooth.INSTANCE.init(mContext);
        btn_ping_tools = findViewById(R.id.btn_ping_tools);
        btn_blutooth = findViewById(R.id.btn_blutooth);
        btn_usb = findViewById(R.id.btn_usb);
        btn_bluetooth_ = findViewById(R.id.btn_bluetooth_);

        rdog_printer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = findViewById(checkedId);
                SharedPreferences.Editor editor = settings.edit();

                if (rb.getText().equals("Bluetooth")) {
                    chk_printer_cash_box.setEnabled(true);

                    btn_bluetooth_.setVisibility(View.VISIBLE);
                    btn_usb.setVisibility(View.GONE);
                } else if (rb.getText().equals("USB")) {
                    chk_printer_cash_box.setEnabled(true);

                    if (mService != null)
                        mService.stop();
                    btn_bluetooth_.setVisibility(View.GONE);
                    btn_usb.setVisibility(View.VISIBLE);
                } else if (rb.getText().equals("Internal")) {
                    chk_printer_cash_box.setEnabled(false);
                    btn_bluetooth_.setVisibility(View.GONE);
                    btn_usb.setVisibility(View.GONE);
                    editor.putString("printer", "inner");
                    editor.commit();
                    if (mService != null)
                        mService.stop();

                }
            }
        });

        String selectedPrinter = settings.getString("printer", "inner");

        Log.e("selected ", "printer......" + selectedPrinter);
        if (selectedPrinter.equals("bluetooth")) {
            rdo_bluetooth.setChecked(true);
            chk_printer_cash_box.setEnabled(true);
            chk_allow_cashier_cash_drawer.setEnabled(true);

        } else if (selectedPrinter.equals("usb")) {
            rdo_usb.setChecked(true);
            chk_printer_cash_box.setEnabled(true);
            chk_allow_cashier_cash_drawer.setEnabled(true);


        } else {
            rdo_inner.setChecked(true);
            chk_printer_cash_box.setEnabled(false);
            chk_allow_cashier_cash_drawer.setEnabled(false);
        }

        btn_blutooth_unpair = findViewById(R.id.btn_blutooth_unpair);

        btnBottomTestPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (Printer.dev != null && Printer.usbCtrl != null) {

                    byte[] buffer = PrinterCommand.POS_Set_PrtInit();
                    Printer.usbCtrl.sendByte(buffer, Printer.dev);

                    checkPrinterStatus();
                    Toast.makeText(activity_settings.this,"printer..."+isOutOfPaper(Printer.dev),Toast.LENGTH_SHORT).show();
                }*/
                /*if (Printer.dev != null && Printer.usbCtrl != null) {

                    byte[] buffer = PrinterCommand.POS_Set_PrtInit();
                    Printer.usbCtrl.sendByte(buffer, Printer.dev);

                    openCashDrawer(Printer.usbCtrl, Printer.dev);
                }else{
                    Toast.makeText(activity_settings.this, "Please connect Usb Printer", Toast.LENGTH_SHORT).show();

                }*/

                if (rdo_usb.isChecked()) {
                    if (PrinterTopitup.dev != null && PrinterTopitup.usbCtrl != null) {

                        PrinterTopitup.usbCtrl.sendMsg(" Hi ..........\n Topitup USB Printer \n Connected........", "GBK", PrinterTopitup.dev);
                    } else {
                        Toast.makeText(activity_settings.this, "Please connect USB Printer", Toast.LENGTH_SHORT).show();

                    }
                } else if (rdo_bluetooth.isChecked()) {
//                    Toast.makeText(activity_settings.this, "bluetooth connected", Toast.LENGTH_SHORT).show();

                    SendDataByte(PrinterCommand.POS_Print_Text(" Hi ..........\n Topitup bluetooth Printer \n Connected........", CHINESE, 0, 0, 0, 0), getAppContext());

                } else if (rdo_inner.isChecked()) {

                    if(Build.MODEL.equals("P052")) {
                         printerp052 = WisePosSdk.getInstance().getPrinter();
                        printerp052.initPrinter();
                        PrinterTopitup.print_data(" Hi ..........\n Topitup  Printer \n Connected........");

                    }else {
                        int status;
                        Printingw wpos = new Printingw();
                        wpos.init();
                        wpos.printStatus();
                        try {
                            status = wpos.mPrinter.printPaper(10);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        if (status != 0) {
                            Toasty.error(mContext, "Out of Paper", Toast.LENGTH_LONG).show();
                        } else {
                            PrinterTopitup.print_data(" Hi ..........\n Topitup  Printer \n Connected........");
                        }
                    }

//                    Toast.makeText(activity_settings.this, "inner connected", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity_settings.this, "nothing", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("printer", "usb");
                editor.commit();
               /* Intent intent = new Intent(activity_settings.this,PrintDemo.class);
                startActivity(intent);*/

                PrinterTopitup.printmethod(activity_settings.this);


            }
        });
        btn_bluetooth_.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("printer", "bluetooth");
                editor.commit();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        // Proceed with Bluetooth operations
                        bluetoothOperation();
                    } else {
                        requestBluetoothPermissions();
                    }
                } else {
                    // For older Android versions, directly perform Bluetooth operations
                    bluetoothOperation();
                }


            }
        });


        if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("WPOS")) {

            btn_blutooth.setText("Pair BT printer");
          /*  if (Printooth.INSTANCE.hasPairedPrinter()) {

                btn_blutooth_unpair.setText("Un-pair " + Printooth.INSTANCE.getPairedPrinter().getName());
                btn_blutooth_unpair.setVisibility(View.VISIBLE);
                btn_blutooth.setVisibility(View.GONE);
            } else {
                btn_blutooth_unpair.setVisibility(View.GONE);



                btn_blutooth.setVisibility(View.GONE);
            }*/
        }
        //chk_print_to_screen.setVisibility(View.GONE);
        if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("WPOS") || Topitup.DEVICE_TYPE.equals("Z91")) {
            btn_blutooth.setVisibility(View.GONE);
        } else {

            btn_blutooth.setVisibility(View.GONE);
        }
        if (Topitup.DEVICE_TYPE.equals("Q1")) {
            btn_q1_test.setVisibility(View.GONE);
        } else {
            btn_q1_test.setVisibility(View.GONE);
        }
        txt_terminal_name.setText(settings.getString("setting_terminal_name", "").trim());
        mpos_user_group.setText(settings.getString("setting_mpos_user_group", "").trim());
        mpos_user_name.setText(settings.getString("setting_mpos_user_name", "").trim());
        mpos_user_pwd.setText(settings.getString("setting_mpos_user_pwd", "Wappoint123*").trim());

        String rg = settings.getString("setting_rg", "mSecondGroup");
        // Toast.makeText(this, "r="+rg, Toast.LENGTH_SHORT).show();
        int i = settings.getInt("setting_time_out_val", 2);
        // Toast.makeText(this, "i="+i, Toast.LENGTH_SHORT).show();
        if (i >= 0) {
            mFirstGroup.clearCheck();
            mSecondGroup.clearCheck();
            // ((RadioButton) ((mSecondGroup)findViewById(R.id.type8)).getChildAt(i)).setChecked(true);

            if (rg.equals("mFirstGroup")) {
                // Toast.makeText(this, "if loop", Toast.LENGTH_SHORT).show();
                ((RadioButton) mFirstGroup.getChildAt(i)).setChecked(true);
                mCheckedId = mFirstGroup.getCheckedRadioButtonId();
            } else {
                ((RadioButton) mSecondGroup.getChildAt(i)).setChecked(true);
                mCheckedId = mSecondGroup.getCheckedRadioButtonId();
            }
        }


        if (settings.getString("setting_mpos_disclaimer", "0").equals("1"))
            chk_mpos_disclaimer.setChecked(true);


        chk_slip_cancel.setChecked(settings.getString("setting_slip_cancel", "0").equals("1"));

        if (settings.getString("swipe_sale_logout_flag", "0").equals("1")) {
            chk_slip_logout.setVisibility(View.GONE);
        } else {
            chk_slip_logout.setVisibility(View.VISIBLE);

        }


        chk_slip_logout.setChecked(settings.getString("setting_slip_logout", "0").equals("1"));


        if (settings.getString("setting_bypass_calculator", "0").equals("1"))
            chk_bypass_calculator.setChecked(true);

        if (settings.getString("setting_balance_cashier", "0").equals("1"))
            chk_balance_cashier.setChecked(true);

        if (settings.getString("setting_balance_cashier_bills", "0").equals("1"))
            chk_balance_cashier_bills.setChecked(true);

        if (settings.getString("setting_balance_cashier_commission", "0").equals("1"))
            chk_balance_cashier_commission.setChecked(true);

        if (settings.getString("setting_balance_cashier_swipe", "0").equals("1"))
            chk_balance_cashier_swipe.setChecked(true);

        if (settings.getString("setting_chk_print_ele_copy", "0").equals("1"))
            chk_print_ele_copy.setChecked(true);

        if (settings.getString("setting_balance_admin", "0").equals("1"))
            chk_balance_admin.setChecked(true);

        if (settings.getString("setting_balance_admin_bills", "0").equals("1"))
            chk_balance_admin_bills.setChecked(true);

        if (settings.getString("setting_balance_admin_commission", "0").equals("1"))
            chk_balance_admin_commission.setChecked(true);

        if (settings.getString("setting_balance_admin_swipe", "0").equals("1"))
            chk_balance_admin_swipe.setChecked(true);

        if (settings.getString("setting_balance_login", "0").equals("1")) {

            chk_balance_login.setChecked(true);
            chk_balance_admin.setChecked(true);
            chk_balance_cashier.setChecked(true);
        }

        if (settings.getString("setting_balance_login_bills", "0").equals("1")) {
            chk_balance_bills.setChecked(true);

            chk_balance_admin_bills.setChecked(true);
            chk_balance_cashier_bills.setChecked(true);
        }

        if (settings.getString("setting_balance_login_commission", "0").equals("1")) {
            chk_balance_commission.setChecked(true);
            chk_balance_admin_commission.setChecked(true);
            chk_balance_cashier_commission.setChecked(true);

        }

        if (settings.getString("setting_balance_login_swipe", "0").equals("1")) {
            chk_balance_swipe.setChecked(true);
            chk_balance_admin_swipe.setChecked(true);
            chk_balance_cashier_swipe.setChecked(true);
        }

       /* if (settings.getString("setting_balance_login", "0").equals("1"))
            chk_balance_bills.setChecked(true);*/
        if (settings.getString("setting_print_address", "0").equals("1"))
            chk_print_address.setChecked(true);
        if (settings.getString("setting_print_barcode", "0").equals("1"))
            chk_print_barcode.setChecked(true);
        if (settings.getString("setting_last_sale", "0").equals("1"))
            chk_last_sale.setChecked(true);
        if (settings.getString("setting_print_bypass", "0").equals("1"))
            chk_print_bypass.setChecked(true);

        if (settings.getString("mute_salaha", "0").equals("1"))
            check_salaha.setChecked(true);
     /*   if (Topitup.DEVICE_TYPE.equals("Q1")  || Topitup.DEVICE_TYPE.equals("QCOM SHOP1")) {
            if (settings.getString("setting_print_barcode","0").equals("1")) chk_print_barcode.setChecked(true);
        } else {
            chk_print_barcode.setVisibility(View.GONE);
        }
*/
        //Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",0);
        //Toast.makeText(activity_settings.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();

        if (settings.getString("printer_cash_drawer", "0").equals("1")) {
            chk_printer_cash_box.setChecked(true);

        }

        if (settings.getString("printer_cashier_cash_drawer", "0").equals("1")) {
            Log.e("printer cashier cash drawer", "cash drawer status.....if...");

            chk_allow_cashier_cash_drawer.setChecked(true);

        } else {
            Log.e("printer cashier cash drawer", "cash drawer status.....else...");

        }

        if (settings.getString("setting_print_to_screen", "0").equals("1"))
            chk_print_to_screen.setChecked(true);
        if (settings.getString("setting_time_out", "0").equals("1")) chk_time_out.setChecked(true);

        if (settings.getString("setting_add_pay", "0").equals("1")) chk_add_pay.setChecked(true);


        if (settings.getString("setting_disclaimer", "0").equals("1")) {
            chk_disclaimer.setChecked(true);
            chk_disclaimer.setVisibility(View.VISIBLE);
        } else {

            chk_disclaimer.setVisibility(View.GONE);
        }
        if (settings.getString("setting_chk_auto_mpos", "0").equals("1")) {
            chk_auto_mpos.setChecked(true);
            mposfirstgroup.setVisibility(View.VISIBLE);
        } else {
            mposfirstgroup.setVisibility(View.GONE);
        }

        int MPOSTYPE = settings.getInt("MPOSTYPE", 2);
        if (MPOSTYPE == 1)
            mposfirstgroup.check(R.id.mpostype1);
        else if (MPOSTYPE == 2)
            mposfirstgroup.check(R.id.mpostype2);
        else
            mposfirstgroup.check(R.id.mpostype3);

        chk_auto_mpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_auto_mpos.isChecked())
                    mposfirstgroup.setVisibility(View.VISIBLE);
                else
                    mposfirstgroup.setVisibility(View.GONE);
            }
        });

        if (chk_time_out.isChecked()) {
            mFirstGroup.setVisibility(View.GONE);
            mSecondGroup.setVisibility(View.GONE);
            chk_disclaimer.setVisibility(View.GONE);
        }


        check_salaha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("za.co.topitup.salaahapp");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (isChecked) {
                    isMute = true;
                    intent.putExtra("muteFromVas", "false");
                    startActivity(intent);
                } else {
                    intent.putExtra("muteFromVas", "true");
                    startActivity(intent);
                    isMute = false;
                }
            }
        });

        chk_time_out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chk_disclaimer.setVisibility(View.GONE);

                chk_disclaimer.setChecked(false);
                if (chk_time_out.isChecked()) {
                    mFirstGroup.setVisibility(View.GONE);
                    mSecondGroup.setVisibility(View.GONE);
                } else {
                    mFirstGroup.setVisibility(View.VISIBLE);
                    mSecondGroup.setVisibility(View.VISIBLE);
                }
            }
        });




        mposfirstgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != -1) {
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    if (checkedId == R.id.mpostype1)
                        editor.putInt("MPOSTYPE", 1);
                    else if (checkedId == R.id.mpostype2)
                        editor.putInt("MPOSTYPE", 2);
                    else
                        editor.putInt("MPOSTYPE", 3);
                    editor.commit();

                }

            }
        });


        mFirstGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                chk_disclaimer.setChecked(false);
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    mSecondGroup.clearCheck();
                    mCheckedId = checkedId;
                    chk_disclaimer.setVisibility(View.GONE);
                }
                isChecking = true;
            }
        });

        mSecondGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                chk_disclaimer.setChecked(false);
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    mFirstGroup.clearCheck();
                    mCheckedId = checkedId;
                    if (mCheckedId == R.id.type8) {
                        chk_disclaimer.setVisibility(View.VISIBLE);
                    } else {
                        chk_disclaimer.setChecked(false);
                        chk_disclaimer.setVisibility(View.GONE);

                    }
                }
                isChecking = true;
            }
        });


        /****
         * cashier Settings
         */
        chk_cashier_own_sales = findViewById(R.id.chk_cashier_own_sales);
        chk_disable_cashier_ele = findViewById(R.id.chk_disable_cashier_ele);
        chk_disable_cashier_bill = findViewById(R.id.chk_disable_cashier_bill);
        chk_disable_cashier_sale_his = findViewById(R.id.chk_disable_cashier_sale_his);
        chk_allow_cashier_rep_ser = findViewById(R.id.chk_allow_cashier_rep_ser);
        chk_last_vou_admin = findViewById(R.id.chk_last_vou_admin);
        chk_last_vou_cashier = findViewById(R.id.chk_last_vou_cashier);
        chk_over_bill_pay_admin = findViewById(R.id.chk_over_bill_pay_admin);
        chk_over_bill_pay_cashier = findViewById(R.id.chk_over_bill_pay_cashier);
        chk_run_w_repo = findViewById(R.id.chk_run_w_repo);
        chk_run_y_repo = findViewById(R.id.chk_run_y_repo);
        chk_run_y_his = findViewById(R.id.chk_run_y_his);
        chk_all_y_repo = findViewById(R.id.chk_all_y_repo);

        if (settings.getString("setting_chk_cashier_own_sales", "0").equals("1"))
            chk_cashier_own_sales.setChecked(true);
        if (settings.getString("setting_chk_disable_cashier_ele", "0").equals("1"))
            chk_disable_cashier_ele.setChecked(true);
        if (settings.getString("setting_chk_disable_cashier_bill", "0").equals("1"))
            chk_disable_cashier_bill.setChecked(true);
        if (settings.getString("setting_chk_disable_cashier_sale_his", "0").equals("1"))
            chk_disable_cashier_sale_his.setChecked(true);
        if (settings.getString("setting_chk_allow_cashier_rep_ser", "0").equals("1"))
            chk_allow_cashier_rep_ser.setChecked(true);
        if (settings.getString("setting_chk_last_vou_admin", "0").equals("1"))
            chk_last_vou_admin.setChecked(true);
        if (settings.getString("setting_chk_last_vou_cashier", "0").equals("1"))
            chk_last_vou_cashier.setChecked(true);


        if (settings.getString("setting_chk_over_bill_pay_admin", "0").equals("1"))
            chk_over_bill_pay_admin.setChecked(true);
        if (settings.getString("setting_chk_over_bill_pay_cashier", "0").equals("1"))
            chk_over_bill_pay_cashier.setChecked(true);

        if (settings.getString("setting_chk_run_w_repo", "0").equals("1"))
            chk_run_w_repo.setChecked(true);
        if (settings.getString("setting_chk_run_y_repo", "0").equals("1"))
            chk_run_y_repo.setChecked(true);
        if (settings.getString("setting_chk_run_y_his", "0").equals("1"))
            chk_run_y_his.setChecked(true);
        if (settings.getString("setting_chk_all_y_repo", "0").equals("1"))
            chk_all_y_repo.setChecked(true);


        if (value == 1) {
            tiu_title_header.setText("General Settings");
            gensettings.setVisibility(View.VISIBLE);
        }
        if (value == 2) {
            tiu_title_header.setText("Print&Slip Settings");
            slipsettings.setVisibility(View.VISIBLE);
            btnBottomTestPrint.setVisibility(View.VISIBLE);
        }
        if (value == 3) {
            tiu_title_header.setText("Cashier Settings");
            cashiersettings.setVisibility(View.VISIBLE);

        }
        if (value == 4) {
            tiu_title_header.setText("Electrcity Settings");
            elesettings.setVisibility(View.VISIBLE);

        }
        if (value == 5) {
            tiu_title_header.setText("Swipe Card Settings");
            mpossettings.setVisibility(View.VISIBLE);

        }
//        changeSystemTime("2015","04","06","13","09","30");

    }

    public void openCashDrawer(UsbController usbController, UsbDevice device) {
        int mode = 0;     // Mode to control the cashbox (usually 0 or 1 depending on the printer)
        int time1 = 100;  // Pulse on time in milliseconds (adjust based on your printer requirements)
        int time2 = 100;  // Pulse off time in milliseconds (adjust based on your printer requirements)

        // Generate the command byte array
        byte[] command = POS_Set_Cashbox(mode, time1, time2);

        if (command != null) {
            // Send the command to the printer to open the cash drawer
            usbController.sendByte(command, device);
        } else {
            System.out.println("Invalid parameters for cashbox command");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activity_admin admin = new activity_admin();
        admin.dismiss();
    }
    public static boolean checkPrinterStatusWithoutHandler(Context appContext) {
        boolean isPrinter = false;
        BluetoothService mBluetoothService = new BluetoothService(Topitup.getAppContext(), mHandler);

        isPrinter = mBluetoothService.checkPrinterStatusBeforePrinting();

        return isPrinter;
    }

    public void bluetoothOperation() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity_settings.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
//                    rdo_inner.setChecked(true);

        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null) {
                mService = new BluetoothService(activity_settings.this, mHandler);

            } else {
//                rdo_inner.setChecked(true);

            }

        }

        Intent serverIntent = new Intent(activity_settings.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH_PERMISSIONS
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth operations
                bluetoothOperation();
            } else {
                Toast.makeText(this, "Bluetooth permissions are required for this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DEBUG)
            switch (requestCode) {
                case REQUEST_CONNECT_DEVICE: {

                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        // Get the device MAC address
                        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        // Get the BLuetoothDevice object
                        if (BluetoothAdapter.checkBluetoothAddress(address)) {
                            BluetoothDevice device = mBluetoothAdapter
                                    .getRemoteDevice(address);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("last_device_address", address);
                            editor.commit();
                            if (mService != null) {
                                mService.connect(device);
                            }

                        } else {
//                        Toast.makeText(activity_settings.this,"result if",Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        rdo_inner.setChecked(true);
                        Toast.makeText(activity_settings.this, "Bluetooth Device Not Found", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case REQUEST_ENABLE_BT: {
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {
                        // Bluetooth is now enabled, so set up a session
//                    KeyListenerInit();
                        mService = new BluetoothService(this, mHandler);

                    } else {
                        // Use"r did not enable Bluetooth or an error occured
                        Log.d("TAG", "BT not enabled");
                        Toast.makeText(this, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }

            }
    }

    public synchronized boolean isOutOfPaper(UsbDevice device) {
//        byte[] statusCommand =      byte[] { 0x10, 0x04, 0x02 }; // DLE EOT 2 command
        byte[] statusCommand = new byte[]{0x10, 0x04, 0x04}; // DLE EOT 4 command to check paper sensor

        // Send the command to the printer
        PrinterTopitup.usbCtrl.sendByte(statusCommand, device);

        // Add a short delay to allow the printer to process the command
        try {
            Thread.sleep(100); // 100 milliseconds; adjust as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Read the response from the printer
        byte status = PrinterTopitup.usbCtrl.revByte(device);
        System.out.println("Status byte: " + Integer.toBinaryString(status));
        System.out.println("Paper status (bit 5): " + ((status & 0x20) != 0));
        System.out.println("Alternative check (bit 3): " + ((status & 0x08) != 0));
        System.out.println("Alternative check (bit 2): " + ((status & 0x04) != 0));

        System.out.println("Status byte: " + Integer.toBinaryString(status & 0xFF));

        // Check the response byte for paper status
        // You might need to test different bits or consult the documentation.
        // Bit 5 is commonly used, but if this returns false positives, try other bits.
        return (status & 0x20) != 0; // This checks bit 5
    }

    public void onclick_btn_action(View v) {

        // Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //   TextView spinnertxt = (TextView) findViewById(R.id.spinnetxt);
        if (v.getId() == R.id.btn_ping_tools) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("ua.com.streamsoft.pingtoolspro");
            if (launchIntent != null) {
                startActivity(launchIntent); // null pointer check in case package name was not found
            }

        } else if (v.getId() == R.id.btn_wpos) {
            Intent intentprincon = new Intent(activity_settings.this, Printingw.class);
            startActivity(intentprincon);

        } else if (v.getId() == R.id.btn_q1_test) {
            if (!PrinterTopitup.checkQ1Printer()) {
                Toasty.error(mContext, "Printer Issue", Toast.LENGTH_LONG).show();
                return;
            }
            if (!PrinterTopitup.checkQ1Printer()) {
                Toasty.error(mContext, "Out of Paper", Toast.LENGTH_LONG).show();
                return;
            }

            PrinterTopitup.print_data("2Top it Up\n" +
                    "1Airtime | Data | Easy Airtime | Electricity | Water |\n" +
                    "1Bill Payments | Money Transfers \n" +
                    "1|International Airtime | 1ForYou | OTT\n");

        } else if (v.getId() == R.id.btn_bottom_close) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_settings_save) {

            String str_balance_cashier = "0";
            if (chk_balance_cashier.isChecked()) str_balance_cashier = "1";

            String str_balance_cashier_bills = "0";
            if (chk_balance_cashier_bills.isChecked()) str_balance_cashier_bills = "1";

            String str_balance_cashier_commission = "0";
            if (chk_balance_cashier_commission.isChecked())
                str_balance_cashier_commission = "1";

            String str_balance_cashier_swipe = "0";
            if (chk_balance_cashier_swipe.isChecked()) str_balance_cashier_swipe = "1";

            String str_balance_admin = "0";
            if (chk_balance_admin.isChecked()) str_balance_admin = "1";

            String str_balance_admin_bills = "0";
            if (chk_balance_admin_bills.isChecked()) str_balance_admin_bills = "1";


            String str_balance_admin_commission = "0";
            if (chk_balance_admin_commission.isChecked()) str_balance_admin_commission = "1";

            String str_balance_admin_swipe = "0";
            if (chk_balance_admin_swipe.isChecked()) str_balance_admin_swipe = "1";


            String str_balance_login = "0";
            if (chk_balance_login.isChecked()) {
                str_balance_login = "1";
                str_balance_cashier = "1";
                str_balance_admin = "1";
            }

            String str_balance_login_bills = "0";
            if (chk_balance_bills.isChecked()) {
                str_balance_login_bills = "1";
                str_balance_admin_bills = "1";
                str_balance_cashier_bills = "1";
            }

            String str_balance_login_commission = "0";
            if (chk_balance_commission.isChecked()) {
                str_balance_login_commission = "1";
                str_balance_admin_commission = "1";
                str_balance_cashier_commission = "1";
            }

            String str_balance_login_swipe = "0";
            if (chk_balance_swipe.isChecked()) {
                str_balance_login_swipe = "1";
                str_balance_admin_swipe = "1";
                str_balance_cashier_swipe = "1";
            }

            String muteSalaha = "0";
            if (isMute) {
                muteSalaha = "1";
            } else {
                muteSalaha = "0";

            }
            String str_chk_print_bypass = "0";
            //    if (Topitup.DEVICE_TYPE.equals("Q1") || Topitup.DEVICE_TYPE.equals("QCOM SHOP1")) {
            if (chk_print_bypass.isChecked()) str_chk_print_bypass = "1";
            //   }
            String str_chk_print_barcode = "0";
            //    if (Topitup.DEVICE_TYPE.equals("Q1") || Topitup.DEVICE_TYPE.equals("QCOM SHOP1")) {
            if (chk_print_barcode.isChecked()) str_chk_print_barcode = "1";
            //   }
            String str_chk_time_out = "0";
            if (chk_time_out.isChecked()) str_chk_time_out = "1";

            String setting_add_pay = "0";
            if (chk_add_pay.isChecked()) setting_add_pay = "1";


            String str_print_to_screen = "0";
            if (chk_print_to_screen.isChecked()) str_print_to_screen = "1";

            String str_print_cash_box = "0";
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (chk_printer_cash_box.isChecked()) str_print_cash_box = "1";
//                }
            String str_print_cashier_cash_box = "0";
            if (chk_allow_cashier_cash_drawer.isChecked()) str_print_cashier_cash_box = "1";

            String str_last_sale = "0";
            if (chk_last_sale.isChecked()) str_last_sale = "1";

            String str_disclaimer = "0";
            if (chk_disclaimer.isChecked()) str_disclaimer = "1";

            String str_chk_cashier_own_sales = "0";
            if (chk_cashier_own_sales.isChecked()) str_chk_cashier_own_sales = "1";

            String str_chk_disable_cashier_ele = "0";
            if (chk_disable_cashier_ele.isChecked()) str_chk_disable_cashier_ele = "1";

            String str_chk_disable_cashier_bill = "0";
            if (chk_disable_cashier_bill.isChecked()) str_chk_disable_cashier_bill = "1";

            String str_chk_disable_cashier_sale_his = "0";
            if (chk_disable_cashier_sale_his.isChecked())
                str_chk_disable_cashier_sale_his = "1";

            String str_chk_allow_cashier_rep_ser = "0";
            if (chk_allow_cashier_rep_ser.isChecked()) str_chk_allow_cashier_rep_ser = "1";

            String str_chk_last_vou_admin = "0";
            if (chk_last_vou_admin.isChecked()) str_chk_last_vou_admin = "1";

            String str_chk_last_vou_cashier = "0";
            if (chk_last_vou_cashier.isChecked()) str_chk_last_vou_cashier = "1";

            String str_chk_over_bill_pay_admin = "0";
            if (chk_over_bill_pay_admin.isChecked()) str_chk_over_bill_pay_admin = "1";

            String str_chk_over_bill_pay_cashier = "0";
            if (chk_over_bill_pay_cashier.isChecked()) str_chk_over_bill_pay_cashier = "1";

            String str_chk_run_w_repo = "0";
            if (chk_run_w_repo.isChecked()) str_chk_run_w_repo = "1";

            String str_chk_run_y_repo = "0";
            if (chk_run_y_repo.isChecked()) str_chk_run_y_repo = "1";

            String str_chk_run_y_his = "0";
            if (chk_run_y_his.isChecked()) str_chk_run_y_his = "1";

            String str_chk_all_y_repo = "0";
            if (chk_all_y_repo.isChecked()) str_chk_all_y_repo = "1";

            String str_mpos_disclaimer = "0";
            if (chk_mpos_disclaimer.isChecked()) str_mpos_disclaimer = "1";


            String str_bypass_calculator = "0";
            if (chk_bypass_calculator.isChecked()) str_bypass_calculator = "1";

            String str_slip_cancel = "0";
            if (chk_slip_cancel.isChecked()) str_slip_cancel = "1";

            String str_slip_logout = "0";
            if (chk_slip_logout.isChecked()) str_slip_logout = "1";

            String str_chk_print_ele_copy = "0";
            if (chk_print_ele_copy.isChecked()) str_chk_print_ele_copy = "1";

            String str_chk_auto_mpos = "0";
            if (chk_auto_mpos.isChecked()) str_chk_auto_mpos = "1";

            String str_print_address = "0";
            if (chk_print_address.isChecked()) str_print_address = "1";

              /*  if(check_salaha.isChecked()){
                    final AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                    mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }*/

            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            SharedPreferences.Editor editor = settings.edit();

            //delete

            editor.putString("setting_add_pay",setting_add_pay);
            editor.putString("setting_terminal_name", txt_terminal_name.getText().toString().trim());
            editor.putString("setting_mpos_user_group", mpos_user_group.getText().toString().trim());
            editor.putString("setting_mpos_user_name", mpos_user_name.getText().toString().trim());
            editor.putString("setting_mpos_user_pwd", mpos_user_pwd.getText().toString().trim());

            editor.putString("setting_mpos_disclaimer", str_mpos_disclaimer);
            editor.putString("setting_chk_auto_mpos", str_chk_auto_mpos);
            editor.putString("setting_chk_print_ele_copy", str_chk_print_ele_copy);
            editor.putString("setting_bypass_calculator", str_bypass_calculator);

            editor.putString("setting_balance_cashier", str_balance_cashier);
            editor.putString("setting_balance_cashier_bills", str_balance_cashier_bills);
            editor.putString("setting_balance_cashier_commission", str_balance_cashier_commission);
            editor.putString("setting_balance_cashier_swipe", str_balance_cashier_swipe);


            editor.putString("setting_balance_admin", str_balance_admin);
            editor.putString("setting_balance_admin_bills", str_balance_admin_bills);
            editor.putString("setting_balance_admin_commission", str_balance_admin_commission);
            editor.putString("setting_balance_admin_swipe", str_balance_admin_swipe);

            editor.putString("setting_balance_login", str_balance_login);
            editor.putString("setting_balance_login_bills", str_balance_login_bills);
            editor.putString("setting_balance_login_commission", str_balance_login_commission);
            editor.putString("setting_balance_login_swipe", str_balance_login_swipe);


            editor.putString("setting_print_to_screen", str_print_to_screen);
            editor.putString("printer_cash_drawer", str_print_cash_box);
            editor.putString("printer_cashier_cash_drawer", str_print_cashier_cash_box);
            editor.putString("setting_print_barcode", str_chk_print_barcode);
            editor.putString("setting_print_bypass", str_chk_print_bypass);
            editor.putString("mute_salaha", muteSalaha);
            editor.putString("setting_time_out", str_chk_time_out);
            editor.putString("setting_last_sale", str_last_sale);
            editor.putString("setting_disclaimer", str_disclaimer);
            editor.putString("setting_print_address", str_print_address);
            editor.putString("setting_slip_cancel", str_slip_cancel);
            editor.putString("setting_slip_logout", str_slip_logout);
            editor.putString("setting_chk_cashier_own_sales", str_chk_cashier_own_sales);
            editor.putString("setting_chk_disable_cashier_ele", str_chk_disable_cashier_ele);
            editor.putString("setting_chk_disable_cashier_bill", str_chk_disable_cashier_bill);
            editor.putString("setting_chk_disable_cashier_sale_his", str_chk_disable_cashier_sale_his);
            editor.putString("setting_chk_allow_cashier_rep_ser", str_chk_allow_cashier_rep_ser);
            editor.putString("setting_chk_last_vou_admin", str_chk_last_vou_admin);
            editor.putString("setting_chk_last_vou_cashier", str_chk_last_vou_cashier);
            editor.putString("setting_chk_over_bill_pay_admin", str_chk_over_bill_pay_admin);
            editor.putString("setting_chk_over_bill_pay_cashier", str_chk_over_bill_pay_cashier);
            editor.putString("setting_chk_run_y_repo", str_chk_run_y_repo);
            editor.putString("setting_chk_run_y_his", str_chk_run_y_his);
            editor.putString("setting_chk_all_y_repo", str_chk_all_y_repo);
            editor.putString("setting_chk_run_w_repo", str_chk_run_w_repo);

            //   editor.putString("setting_time_out_val",spinner.getSelectedItem().toString());

            //


//String time_out_val=spinner.getSelectedItem().toString();
            //    Toast.makeText(activity_settings.this, "v="+String.valueOf(mCheckedId),Toast.LENGTH_LONG).show();
            long TIMEOUT_IN_MILLI = 60000;


            if (mCheckedId == R.id.type1) {
                TIMEOUT_IN_MILLI = 15000;
                editor.putInt("setting_time_out_val", mFirstGroup.indexOfChild(findViewById(R.id.type1)));
                rg = "mFirstGroup";
            } else if (mCheckedId == R.id.type2) {
                editor.putInt("setting_time_out_val", mFirstGroup.indexOfChild(findViewById(R.id.type2)));
                TIMEOUT_IN_MILLI = 2 * 15000;
                rg = "mFirstGroup";
            } else if (mCheckedId == R.id.type3) {
                editor.putInt("setting_time_out_val", mFirstGroup.indexOfChild(findViewById(R.id.type3)));
                TIMEOUT_IN_MILLI = 60000;
                rg = "mFirstGroup";
            } else if (mCheckedId == R.id.type4) {
                editor.putInt("setting_time_out_val", mFirstGroup.indexOfChild(findViewById(R.id.type4)));
                TIMEOUT_IN_MILLI = 2 * 60000;
                rg = "mFirstGroup";
            } else if (mCheckedId == R.id.type5) {
                editor.putInt("setting_time_out_val", mSecondGroup.indexOfChild(findViewById(R.id.type5)));
                TIMEOUT_IN_MILLI = 5 * 60000;
                rg = "mSecondGroup";
            } else if (mCheckedId == R.id.type6) {
                editor.putInt("setting_time_out_val", mSecondGroup.indexOfChild(findViewById(R.id.type6)));
                TIMEOUT_IN_MILLI = 10 * 60000;
                rg = "mSecondGroup";
            } else if (mCheckedId == R.id.type7) {
                editor.putInt("setting_time_out_val", mSecondGroup.indexOfChild(findViewById(R.id.type7)));
                TIMEOUT_IN_MILLI = 30 * 60000;
                rg = "mSecondGroup";
            } else {
                editor.putInt("setting_time_out_val", mSecondGroup.indexOfChild(findViewById(R.id.type8)));
                TIMEOUT_IN_MILLI = 365 * 24 * 60000;
                rg = "mSecondGroup";
            }

            editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
            editor.putLong("TIMEOUT_IN_MILLI_ORI", TIMEOUT_IN_MILLI);
            editor.putString("setting_rg", rg);
            if (mCheckedId == R.id.type8 && !chk_time_out.isChecked()) {
                if (!chk_disclaimer.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    //   builder.setTitle("Legal Disclaimer");

                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_simple, viewGroup, false);
                    builder.setView(dialogView);

//                        builder.setCancelable(true);
//                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                chk_disclaimer.setVisibility(View.VISIBLE);
//                                    chk_disclaimer.setChecked(true);
//                            }
//                        });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setButton("I Accept    ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chk_disclaimer.setVisibility(View.VISIBLE);
                            chk_disclaimer.setChecked(true);
                        }
                    });
                    alertDialog.show();


                } else {


                    editor.commit();
                    PRINT_BARCODE = str_chk_print_barcode;

                    Toasty.info(mContext, "Saved!", 5000, true).show();

                }
            } else {
                if (chk_time_out.isChecked()) {

                    editor.putInt("setting_time_out_val", mSecondGroup.indexOfChild(findViewById(R.id.type7)));
                    rg = "mSecondGroup";
                    // TIMEOUT_IN_MILLI= 365*24*60000;
                    TIMEOUT_IN_MILLI = 60000;
                    editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
                    editor.putLong("TIMEOUT_IN_MILLI_ORI", TIMEOUT_IN_MILLI);
                    editor.putString("setting_rg", rg);

                }
                editor.commit();
                PRINT_BARCODE = str_chk_print_barcode;
                // Toast.makeText(this, "otime="+TIMEOUT_IN_MILLI, Toast.LENGTH_SHORT).show();

                Toasty.info(mContext, "Saved!", 5000, true).show();

            }


        }


    }

    public void onClickShowDetails() {
        // inserting complete table details in this text field

        // creating a cursor object of the
        // content URI
        Cursor cursor = getContentResolver().query(Uri.parse("content://za.co.topitup.salaahapp/users"), null, null, null, null);

        // iteration of the cursor
        // to print whole table
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                StringBuilder strBuild = new StringBuilder();

                @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex("name"));
                check_salaha.setChecked(text.equals("false"));
                Log.e("response", "res from cursor" + text);
//            resultView.setText(strBuild);
            } else {
//            resultView.setText("No Records Found");
            }
        }
    }

    public String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))

                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))

                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //Do something
                //  Toast.makeText(this, "Alarm Selected: " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                //Do another thing
                //  Toast.makeText(this, "Option Selected: " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


}
