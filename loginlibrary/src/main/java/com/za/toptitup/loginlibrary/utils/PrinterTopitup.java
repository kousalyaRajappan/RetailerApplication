package com.za.toptitup.loginlibrary.utils;

import static com.wisepos.smartpos.errorcode.WisePosErrorCode.ERR_SUCCESS;
import static java.lang.Thread.sleep;
import static sdk.PrinterCommand.POS_Set_Cashbox;
import static com.za.toptitup.loginlibrary.bluetooth.BluetoothService.extractPureBlackContent;
import static com.za.toptitup.loginlibrary.utils.Topitup.getAppContext;
import static com.za.toptitup.loginlibrary.utils.Topitup.mIzkcService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wisepos.smartpos.WisePosException;
import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.printer.BarcodeType;
import com.wisepos.smartpos.printer.Printer;
import com.wisepos.smartpos.printer.PrinterListener;
import com.wisepos.smartpos.printer.TextInfo;
import com.zj.usbsdk.UsbController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import print.Print;
import sdk.PrintPicture;
import sdk.PrinterCommand;
import za.co.topitup.R;
import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.activity_main;
import com.za.toptitup.loginlibrary.bluetooth.BluetoothService;
import zj.com.customize.sdk.Other;

//import com.za.toptitup.loginlibrary.admin.Printingw;

//import com.imagpay.Settings;
//import com.imagpay.PrnStrFormat;
//import com.imagpay.Settings;
//import com.imagpay.SwipeEvent;
//import com.imagpay.SwipeListener;
//import com.imagpay.emv.TransListener;
//import com.imagpay.enums.CardDetected;
//import com.imagpay.enums.EmvStatus;
//import com.imagpay.enums.PrintStatus;
//import com.imagpay.mpos.MposHandler;


public final class PrinterTopitup {

    private static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";
    private static boolean is_busy_with_voucher;
    private static Activity mContext;
    private static String last_reprint = "";
    public static UsbController usbCtrl = null;
    public static UsbDevice dev = null;
    private static SharedPreferences preferences;
    private static UsbManager usbManager;
    private static PendingIntent permissionIntent;

    public PrinterTopitup(Activity mContext) {
        PrinterTopitup.mContext = mContext;
    }

    public static void store_last_reprint(final String slip_to_print) {

        last_reprint = slip_to_print;

    }

    public static void do_last_reprint(Context mContext) {

        if (last_reprint.length() == 0) {

            Toasty.error(mContext, "Nothing to reprint", Toast.LENGTH_LONG).show();

        } else {
            print_data(last_reprint);
        }

    }


    public static boolean check_paper(Context mContext) {

        SharedPreferences settings = mContext.getSharedPreferences("TIUPREF", 0);
        String selectedPrinter = settings.getString("printer", "inner");
        int is_out_of_paper = -1;

        if (selectedPrinter.equals("inner")) {


            if (settings.getString("setting_print_to_screen", "0").equals("1")) {
                return true;
            }

            if(android.os.Build.MODEL.equals("P052")) {
                return true;
            }

            Topitup.resetPaperStatus();
            Topitup.checkOutOfPaper();

            try {
                while (is_out_of_paper == -1) {
                    sleep(250);
                    is_out_of_paper = Topitup.getPaperStatus();
                }
            } catch (Exception ex) {
                //
            }


            //Toasty.error(Topitup.getAppContext(), "Out of Paper!", Toast.LENGTH_LONG).show();
            return is_out_of_paper != 0;
        }


        return is_out_of_paper != 0;

    }


    public static void printmethod(Activity mContextt) {
        int[][] u_infor;

        usbCtrl = new UsbController(mContextt, mHandler1);
        u_infor = new int[8][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0416;
        u_infor[4][1] = 0x5011;
        u_infor[5][0] = 0x0416;
        u_infor[5][1] = 0xAABB;
        u_infor[6][0] = 0x1659;
        u_infor[6][1] = 0x8965;
        u_infor[7][0] = 0x0483;
        u_infor[7][1] = 0x5741;


        usbCtrl.close();
        int i = 0;
        for (i = 0; i < 8; i++) {
            dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
            if (dev != null)
                break;
        }

        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        if (dev != null) {
            int vendorId = dev.getVendorId();
            int productId = dev.getProductId();
            SharedPreferences settings = mContext.getSharedPreferences("TIUPREF", 0);

            Log.d("USB_DEVICE", "Vendor ID: " + vendorId + ", Product ID: " + productId);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("vendorId", vendorId);
            editor.putInt("productId", productId);

            editor.commit();
            if (!(usbCtrl.isHasPermission(dev))) {
//                usbCtrl.getPermission(dev);
                PendingIntent permissionIntent = PendingIntent.getBroadcast(
                        mContext, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                usbManager.requestPermission(dev, permissionIntent);
            } else {
                Toast.makeText(mContextt, "obtaining USB device access permissions success",
                        Toast.LENGTH_SHORT).show();

            }

        } else {
            showUsbNotConnectedDialog(mContextt);

//            Toast.makeText(mContextt, "Please connect USB Printer", Toast.LENGTH_SHORT).show();
        }


    }

    public static void resetPrinter(UsbDevice dev) {
        endPrintJob(dev);
        byte[] resetCommand = new byte[]{0x1B, 0x40};
        usbCtrl.sendByte(resetCommand, dev);
    }

    public static void endPrintJob(UsbDevice dev) {
        byte[] endCommand = new byte[]{0x1D, 0x56, 0x01}; // Paper cut command
        usbCtrl.sendByte(endCommand, dev);
    }

    public static void endPrintJobAndReset() {
        try {
            // Step 1: Cut the paper (check printer documentation for proper command)
            byte[] cutPaperCommand = new byte[]{0x1D, 0x56, 0x01}; // Full cut
            activity_settings.SendDataByte(cutPaperCommand, getAppContext());

            // Optional: Add a delay to ensure the cut command is processed
            Thread.sleep(50);

            // Step 2: Reset the printer
            byte[] resetCommand = new byte[]{0x1B, 0x40}; // Printer reset command
            activity_settings.SendDataByte(resetCommand, getAppContext());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void SendDataByte(byte[] data) {
        if (data.length > 0)
            usbCtrl.sendByte(data, dev);
    }

    @SuppressLint("HandlerLeak")
    public static final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SharedPreferences settings = getAppContext().getSharedPreferences("TIUPREF", 0);

            SharedPreferences.Editor editor = settings.edit();

            switch (msg.what) {

                case UsbController.USB_CONNECTED:
                    editor.putString("printer", "usb");
                    editor.commit();
                    Toast.makeText(Topitup.getAppContext(), "usb connected", Toast.LENGTH_LONG).show();
                    break;
                default:
                    editor.putString("printer", "inner");
                    editor.commit();
                    Toast.makeText(Topitup.getAppContext(), "usb not connected..." + msg.what, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public static boolean checkQ1Printer() {

        int is_printer_sts = -1;
        try {
            while (is_printer_sts == -1) {
                sleep(250);
                is_printer_sts = Topitup.getQ1PrinterSts();
            }
        } catch (Exception ex) {
            //
        }

        return is_printer_sts != 0;


    }

    public static void bluetoothDataPrinter(String slip_to_print) {
        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);

        BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
        String line = null;

        try {
            activity_settings.printLogobluetooth(getAppContext());
            while ((line = bufReader.readLine()) != null) {

                String prnt_line = "";
                if (line.length() > 0) {
                    String first = line.substring(0, 1);

                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                        if (Topitup.PRINT_BARCODE.equals("1")) {
                            if (line.contains("BARCODE")) {

                                prnt_line += line.replace("BARCODE:", "");
                                byte[] code = PrinterCommand.getCodeBarCommand(prnt_line, 67, 3, 68, 0, 2);
                                activity_settings.SendDataString("\n", getAppContext());
                                activity_settings.SendDataByte(new byte[]{0x1b, 0x61, 0x00}, getAppContext());
                                activity_settings.SendDataByte(code, getAppContext());

                            }
                        }

                    } else {

                        if (first.equals("1") || first.equals("0")) {
                            // prnt_line = line.substring(1) + "\n";
                            prnt_line = line.substring(1) + "\r\n";

                            if (prnt_line.equals("")) {

                                activity_settings.SendDataString(prnt_line, getAppContext());
                            } else {
                                int maxCharsPerLine = 32; // Adjust this based on printer width

                                // Split text into wrapped lines
                                List<String> wrappedLines = wrapText(prnt_line, maxCharsPerLine);
                                activity_settings.SendDataString(prnt_line, getAppContext());
                                // Print each wrapped line
                                for (String wrappedLine : wrappedLines) {
                                    //   activity_settings.SendDataString(wrappedLine, getAppContext());
                                    //activity_settings.SendDataString(prnt_line , getAppContext());
                                }
                            }

                        } else {

                            prnt_line = line.substring(1) + "\n";

                            byte[] boldOn = {0x1B, 0x45, 0x01}; // ESC E 1
                            activity_settings.SendDataByte(boldOn, getAppContext());

                            byte[] textSizeMedium = {0x1D, 0x21, 0x01}; // GS ! n (n = 0x01 for double-height only)
                            activity_settings.SendDataByte(textSizeMedium, getAppContext());

                            activity_settings.SendDataByte(prnt_line.getBytes(), getAppContext());

                            byte[] textSizeNormal = {0x1D, 0x21, 0x00}; // GS ! 0
                            activity_settings.SendDataByte(textSizeNormal, getAppContext());

                            byte[] boldOff = {0x1B, 0x45, 0x00}; // ESC E 0
                            activity_settings.SendDataByte(boldOff, getAppContext());

                        }


                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        activity_settings.SendDataString("\n \n \n", getAppContext());

        if (settings.getString("printer_cash_drawer", "0").equals("1")) {

            activity_settings.SendDataByte(PrinterCommand.POS_Set_PrtInit(), getAppContext());

            if (activity_login.fromScreen.equals("activity_spi") || activity_login.fromScreen.equals("activity_elec") || activity_login.fromScreen.equals("activity_bill_payment")) {
                openCashDrawerBluetooth();

            }


        }

        endPrintJobAndReset();
    }


    public static void print_data(final String slip_to_print) {

        SharedPreferences settings = Topitup.getAppContext().getSharedPreferences("TIUPREF", 0);
       /* String value  = settings.getString("setting_print_to_screen_temp", "0");
        Log.e("selected printer", "selected........" + value);*/
        if (settings.getString("setting_print_to_screen", "0").equals("1")) {
            Log.e("bluettooth", ",print data,   if, screen");

            Intent intent = new Intent(Topitup.getAppContext(), activity_print_screen.class);
            intent.putExtra("slip_to_print", slip_to_print);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Topitup.getAppContext().startActivity(intent);
            return;

        }



       /* if (settings.getString("setting_print_to_screen_temp", "0").equals("1")) {
            Log.e("bluettooth", ",print data,   if," );


            Intent intent = new Intent(Topitup.getAppContext(), activity_print_screen.class);
            intent.putExtra("slip_to_print", slip_to_print);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Topitup.getAppContext().startActivity(intent);

            return;

        }*/

        int print_line_counter = 0;


        if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("TABLET") || Topitup.DEVICE_TYPE.equals("WPOS") || Topitup.DEVICE_TYPE.equals("Q1") || Topitup.DEVICE_TYPE.equals("po52")) {


            String selectedPrinter = settings.getString("printer", "inner");


            Log.e("selected printer", "selected........" + selectedPrinter);
            if (selectedPrinter.equals("bluetooth")) {
                Log.e("print screen bluetooth", "........print......." + activity_main.printScreen);
                if (activity_main.printScreen) {

                    Intent intent = new Intent(Topitup.getAppContext(), activity_print_screen.class);
                    intent.putExtra("slip_to_print", slip_to_print);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Topitup.getAppContext().startActivity(intent);
                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    bluetoothDataPrinter(slip_to_print);

                } /*else if (activity_login.fromScreen.equals("activity_spi")) {
                    bluetoothDataPrinter(slip_to_print);

                }*/ else {
                    if (activity_settings.mService != null) {
                        new Thread(() -> {
                            boolean isConnected = BluetoothService.isReallyConnected();

                            if (!isConnected) {
                                // Try reconnecting
                                String lastDeviceAddress = settings.getString("last_device_address", null);
                                if (lastDeviceAddress != null) {
                                    if (activity_settings.mBluetoothAdapter == null) {
                                        activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    }
                                    BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                                    activity_settings.mService.connect(device);

                                    // Wait for connection (non-blocking)
                                    int attempts = 0;
                                    while (!BluetoothService.isReallyConnected() && attempts < 20) {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException ignored) {
                                        }
                                        attempts++;
                                    }
                                    isConnected = BluetoothService.isReallyConnected();
                                }
                            }

                            final boolean finalIsConnected = isConnected;
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (finalIsConnected) {
                                    // Check printer status asynchronously
                                    activity_settings.mService.checkPrinterStatusAsync(hasPaper -> {
                                        if (hasPaper) {
                                            bluetoothDataPrinter(slip_to_print);
                                        } else {
                                            showUsbNotConnectedDialog(mContext);

                                        }
                                    });
                                } else {
                                    showUsbNotConnectedDialog(mContext);
                                }
                            });
                        }).start();
                    } else {
                        Toast.makeText(getAppContext(), "Bluetooth Service null", Toast.LENGTH_LONG).show();
                    }
                }
                /*else if(activity_settings.isBluetoothConnected && BluetoothService.isReallyConnected()) {

                    Log.e("bluetooth ","if......1111...."+BluetoothService.isReallyConnected());
                    if (!activity_settings.checkPrinterStatusWithoutHandler(Topitup.getAppContext())) {
                        showUsbNotConnectedDialog(mContext);
                    }else {
                        bluetoothDataPrinter(slip_to_print);

                    }

                }
                else{
                    Log.e("bluetooth ","if......3333. 5454545...");

                    if (activity_settings.mService != null)  {
                        String lastDeviceAddress = settings.getString("last_device_address", null);
                        if (lastDeviceAddress != null) {
                            if (activity_settings.mBluetoothAdapter == null) {
                                activity_settings.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            }
                            activity_settings.bluetoothMsg = "";
                            BluetoothDevice device = activity_settings.mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
                            activity_settings.mService.connect(device);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if(activity_settings.isBluetoothConnected){
                                        bluetoothDataPrinter(slip_to_print);
                                    }else{
                                        if (!BluetoothService.isReallyConnected()) {
                                            showUsbNotConnectedDialog(mContext);

                                        }else {
                                            Toast.makeText(getAppContext(),"Please try again ",Toast.LENGTH_LONG).show();

                                        }

                                    }
                                }
                            }, 3000);


                        }else{
                            Toast.makeText(getAppContext(),"bluetooth last Address null",Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(getAppContext(),"bluetooth Service null",Toast.LENGTH_LONG).show();

                    }

                }*/

            } else if (selectedPrinter.equals("usb")) {

                if (dev != null && usbCtrl != null) {

                    if (dev != null) {

                        if (!(usbCtrl.isHasPermission(dev))) {

//                            initiateUsbPrinter(usbCtrl);
                            printmethod(mContext);
//                            Toast.makeText(getAppContext(),"both null",Toast.LENGTH_LONG).show();


                        } else {
                            initiateUsbPrinter(usbCtrl);

                            if (checkPrinerStatus(dev, mContext)) {
                                Log.e("print image", "image print");
                                printDrawableOverUSB(mContext, dev, usbCtrl);
                                BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                                String line = null;
                                try {
                                    while ((line = bufReader.readLine()) != null) {

                                        String prnt_line = "";
                                        if (line.length() > 0) {
                                            String first = line.substring(0, 1);

                                            if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                                if (Topitup.PRINT_BARCODE.equals("1")) {
                                                    if (line.contains("BARCODE")) {

                                                        prnt_line += line.replace("BARCODE:", "");

                                                        byte[] code = PrinterCommand.getCodeBarCommand(prnt_line, 67, 3, 68, 0, 2);
//

                                                        usbCtrl.sendByte(new byte[]{0x1b, 0x61, 0x00}, dev); // ESC a 0 (left alignment)
                                                        usbCtrl.sendByte(code, dev); // Send barcode data


                                                    }
                                                }

                                            } else {


                                                if (first.equals("1") || first.equals("0")) {

                                                    prnt_line = line.substring(1);

                                                    if (prnt_line.equals("")) {
                                                        usbCtrl.sendMsg(prnt_line + "     ", "GBK", dev);

                                                    } else {
                                                        int maxCharsPerLine = 32; // Adjust this based on printer width

                                                        // Split text into wrapped lines
                                                        List<String> wrappedLines = wrapText(prnt_line, maxCharsPerLine);

                                                        // Print each wrapped line
                                                        for (String wrappedLine : wrappedLines) {
                                                            usbCtrl.sendMsg(wrappedLine, "GBK", dev);
                                                        }

                                                    }


                                                } else {
                                                    prnt_line = line.substring(1);

                                                    byte[] boldOn = {0x1B, 0x45, 0x01}; // ESC E 1
                                                    usbCtrl.sendByte(boldOn, dev);

                                                    byte[] textSize = {0x1D, 0x21, 0x01}; // GS ! n (n = 0x11 for 2x height and width)
                                                    usbCtrl.sendByte(textSize, dev);

                                                    usbCtrl.sendMsg(prnt_line, "GBK", dev);

                                                    byte[] textSizeNormal = {0x1D, 0x21, 0x00}; // GS ! 0
                                                    usbCtrl.sendByte(textSizeNormal, dev);
                                                    byte[] boldOff = {0x1B, 0x45, 0x00}; // ESC E 0
                                                    usbCtrl.sendByte(boldOff, dev);


                                                }
                                            }
                                        }
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                usbCtrl.sendMsg("\n \n \n", "GBK", dev);
                                resetPrinter(dev);
                            } else {
                                showUsbNotConnectedDialog(mContext);
                            }


                        }

                    } else {

                        printmethod(mContext);


                    }
                } else {

                    printmethod(mContext);


                }
                if (settings.getString("printer_cash_drawer", "0").equals("1")) {
                    if (dev != null && usbCtrl != null) {

                        byte[] buffer = PrinterCommand.POS_Set_PrtInit();
                        PrinterTopitup.usbCtrl.sendByte(buffer, dev);

                        if (activity_login.fromScreen.equals("activity_spi") || activity_login.fromScreen.equals("activity_elec") || activity_login.fromScreen.equals("activity_bill_payment")) {
                            openCashDrawer(usbCtrl, dev);

                        }

                    } else {
                        Toast.makeText(mContext, "Please connect USB Printer", Toast.LENGTH_SHORT).show();

                    }
                }


            } else if (selectedPrinter.equals("inner")) {
                if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("TABLET")) {
                    Toast.makeText(getAppContext(), "Please connect USB or Bluetooth", Toast.LENGTH_LONG).show();

                } else if (Topitup.DEVICE_TYPE.equals("WPOS")) {

                    if (android.os.Build.MODEL.equals("P052")) {
                        Toast.makeText(mContext, "p052 selected", Toast.LENGTH_LONG).show();

                        try {
                            Map<String, Object> map;
                            Printer printer = WisePosSdk.getInstance().getPrinter();
                            printer.initPrinter();  //Initializing the printer.
//                            String SANS_SERIF_LIGHT = "sans-serif-light";
                            String SANS_SERIF_LIGHT = "sans-serif";

                            int PRINT_STYLE_CENTER = 0x02;
                            int PRINT_STYLE_LEFT = 0x01;

                            int gray = 5; // Try safe value between 1-5
                            int ret = printer.setGrayLevel(gray);


                            if (ret != ERR_SUCCESS) {
                                Log.e("sdkdemo", "startCaching failed errCode = " + Integer.toHexString(ret));
                                return;
                            } else {
                                Log.e("sdkdemo", " gray value success  ");

                            }

                            map = printer.getPrinterStatus();   //Gets the current status of the printer.
                            if (map == null) {
                                Log.e("sdkdemo", "getStatus failed" + String.format(" errCode = 0x%x\n", 0));
                                return;
                            } else {
                                Log.e("sdkdemo", "getStatus " + String.format(" errCode = 0x%x\n", 0));

                            }
                            //Gets whether the printer is out of paper from the map file.
                            if ((byte) map.get("paper") == 1) {
                                Log.e("sdkdemo", "IsHavePaper = false\n");
                                return;
                            } else {
                                Log.e("sdkdemo", "IsHavePaper = true\n");
                            }

                            //When printing text information, the program needs to set the printing font. The current setting is the default font.
                            Bundle bundle1 = new Bundle();
                            if (android.os.Build.MODEL.equals("P5SE") || android.os.Build.MODEL.equals("P5MAX") || android.os.Build.MODEL.equals("P052")) {
                                bundle1.putString("font", SANS_SERIF_LIGHT);
                            } else {
                                bundle1.putString("font", "DEFAULT");
                            }

                            printer.setPrintFont(bundle1);
                            //When printing text information, the program needs to set the printing font. The current setting is the default font.
                            BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));

                            String line;

                            printer.setLineSpacing(1); // Set global line spacing
                            Bundle fontBundle = new Bundle();
                            if (android.os.Build.MODEL.equals("P5SE") || android.os.Build.MODEL.equals("P5MAX") || android.os.Build.MODEL.equals("P052")) {
                                fontBundle.putString("font", SANS_SERIF_LIGHT);
                            } else {
                                fontBundle.putString("font", "DEFAULT");
                            }
                            printer.setPrintFont(fontBundle);

                            while ((line = bufReader.readLine()) != null) {
                                if (line.length() == 0) continue;

                                String prnt_line = "";
                                String first = line.substring(0, 1);

                                TextInfo textInfo = new TextInfo();
                                textInfo.setFontSize(25); // Default font size
                                textInfo.setBold(false);
                                textInfo.setAlign(PRINT_STYLE_LEFT);

                                if (line.startsWith("BARCODE:")) {
                                    if (Topitup.PRINT_BARCODE.equals("1")) {

                                        // Wiseasy SDK might not support raw ESC commands.
                                        // If barcode printing is supported natively, use printer.printBarcode API.
                                        String barcodeData = line.replace("BARCODE:", "").trim();
                                        // Add Code128 barcode
                                        printer.addBarCode(BarcodeType.BARCODE_TYPE_BARCODE_128, 380, 90, barcodeData);
                                    }
                                    // Add PDF417 barcode
//                                printer.printBarcode(barcodeData, BarcodeFormat.CODE_128, 2, 100); // adjust type/width/height
                                } else if (first.equals("0") || first.equals("1")) {
                                    prnt_line = line.substring(1);

                                    // Optional text wrap logic (you need to implement wrapText() separately)
                                    List<String> wrappedLines = wrapText(prnt_line, 32);
                                    for (String wrappedLine : wrappedLines) {
                                        textInfo.setText(wrappedLine);
                                        textInfo.setBold(true);

                                        printer.addSingleText(textInfo);
                                    }
                                } else {
                                    // Bold & larger size for other lines
                                    prnt_line = line.substring(1);
                                    textInfo.setBold(true);
                                    textInfo.setFontSize(33);
                                    textInfo.setAlign(PRINT_STYLE_CENTER);
                                    textInfo.setText(prnt_line);
                                    printer.addSingleText(textInfo);
                                }
                            }

                            Bundle printerOption = new Bundle();
                            //Start printing
                            printer.startPrinting(printerOption, new PrinterListener() {
                                @Override
                                public void onError(int i) {
                                    Log.e("sdkdemo", "startPrinting failed errCode = " + i);
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("sdkdemo", "print success\n");
                                    try {
                                        //After printing, Feed the paper.
                                        printer.feedPaper(30);
                                    } catch (WisePosException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onReport(int i) {
                                    //The callback method is reserved and does not need to be implemented
                                }
                            });

                            /*TextInfo textInfo = new TextInfo();

                            textInfo.setAlign(PRINT_STYLE_CENTER);
                            textInfo.setFontSize(32);
                            printer.setLineSpacing(1);

                            textInfo.setText("topitup \n welcomes you");
                            printer.addSingleText(textInfo);

                            Bundle printerOption = new Bundle();
                            //Start printing
                            printer.startPrinting(printerOption, new PrinterListener() {
                                @Override
                                public void onError(int i) {
                                    Log.e("sdkdemo","startPrinting failed errCode = " + i);
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("sdkdemo","print success\n");
                                    try {
                                        //After printing, Feed the paper.
                                        printer.feedPaper(30);
                                    } catch (WisePosException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onReport(int i) {
                                    //The callback method is reserved and does not need to be implemented
                                }
                            });*/
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("sdkdemo", "print failed" + e.toString() + "\n");
                        }
                     /*   try {
                            Map<String, Object> map;
//                            Printer printer = activity_settings.printerp052;

                            Printer printer = activity_settings.printerp052;

                            if (printer == null) {
                                printer = WisePosSdk.getInstance().getPrinter();
                                printer.initPrinter();
                                activity_settings.printerp052 = printer;
                            } else {
                                try {
                                    printer.getPrinterStatus(); // Check if it’s still valid
                                } catch (Exception e) {
                                    printer = WisePosSdk.getInstance().getPrinter();
                                    printer.initPrinter();
                                    activity_settings.printerp052 = printer;
                                }
                            }
//                            Printer printer = WisePosSdk.getInstance().getPrinter();
//                            printer.initPrinter(); //Initializing the printer.
//                            String SANS_SERIF_LIGHT = "DEFAULT";
                            String SANS_SERIF_LIGHT = "sans-serif";
//                            String SANS_SERIF_LIGHT = "sans-serif-light";

                            int PRINT_STYLE_CENTER = 0x02;
                            int PRINT_STYLE_LEFT = 0x01;

                            int gray = 5; // Try safe value between 1-5
                            int ret = printer.setGrayLevel(gray);


                            if (ret != ERR_SUCCESS) {
                                Log.e("sdkdemo","startCaching failed errCode = " + Integer.toHexString(ret));
                                return;
                            }else{
                                Log.e("sdkdemo"," gray value success " );
                            }

                            map = printer.getPrinterStatus(); //Gets the current status of the printer.
                            if (map == null) {
                                Log.e("sdkdemo","getStatus failed" + String.format(" errCode = 0x%x\n",0));
                                return;
                            }else{
                                Log.e("sdkdemo","getStatus " + String.format(" errCode = 0x%x\n",0));
                            }

                            //Gets whether the printer is out of paper from the map file.
                            if ((byte) map.get("paper") == 1) {
                                Toast.makeText(mContext,"Out of Paper",Toast.LENGTH_LONG).show();
                                Log.e("sdkdemo","IsHavePaper = false\n");
                                return;
                            } else {
                                Log.e("sdkdemo","IsHavePaper = true\n");
                            }

                            //When printing text information, the program needs to set the printing font. The current setting is the default font.
                            Bundle bundle1 = new Bundle();
                            if (android.os.Build.MODEL.equals("P5SE") ||android.os.Build.MODEL.equals("P5MAX") ||android.os.Build.MODEL.equals("P052")){
                                bundle1.putString("font", SANS_SERIF_LIGHT);
                            } else {
                                bundle1.putString("font", "DEFAULT");
                            }

                            printer.setPrintFont(bundle1);

                            //When printing text information, the program needs to set the printing font. The current setting is the default font.
                            BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));

                            String line;

                            printer.setLineSpacing(1); // Set global line spacing
                            Bundle fontBundle = new Bundle();
                            if (android.os.Build.MODEL.equals("P5SE") || android.os.Build.MODEL.equals("P5MAX") || android.os.Build.MODEL.equals("P052")) {
                                fontBundle.putString("font", SANS_SERIF_LIGHT);
                            } else {
                                fontBundle.putString("font", "DEFAULT");
                            }
                            printer.setPrintFont(fontBundle);

                            while ((line = bufReader.readLine()) != null) {
                                if (line.length() == 0) continue;

                                String prnt_line = "";
                                String first = line.substring(0, 1);

                                TextInfo textInfo = new TextInfo();
                                textInfo.setFontSize(25); // Default font size
                                textInfo.setBold(false);
                                textInfo.setAlign(PRINT_STYLE_LEFT);

                                if (line.startsWith("BARCODE:")) {
                                    // Wiseasy SDK might not support raw ESC commands.
                                    // If barcode printing is supported natively, use printer.printBarcode API.
                                    String barcodeData = line.replace("BARCODE:", "").trim();
                                    // Add Code128 barcode
                                    printer.addBarCode(BarcodeType.BARCODE_TYPE_BARCODE_128, 380, 90, barcodeData);
                                    // Add PDF417 barcode
//                                printer.printBarcode(barcodeData, BarcodeFormat.CODE_128, 2, 100); // adjust type/width/height
                                } else if (first.equals("0") || first.equals("1")) {
                                    prnt_line = line.substring(1);

                                    // Optional text wrap logic (you need to implement wrapText() separately)
                                    List<String> wrappedLines = wrapText(prnt_line, 32);
                                    for (String wrappedLine : wrappedLines) {
                                        textInfo.setText(wrappedLine);
                                        textInfo.setBold(true);

                                        printer.addSingleText(textInfo);
                                    }
                                } else {
                                    // Bold & larger size for other lines
                                    prnt_line = line.substring(1);
                                    textInfo.setBold(true);
                                    textInfo.setFontSize(33);
                                    textInfo.setAlign(PRINT_STYLE_CENTER);
                                    textInfo.setText(prnt_line);
                                    printer.addSingleText(textInfo);
                                }
                            }

                            Bundle printerOption = new Bundle();
                            //Start printing
                            Printer finalPrinter = printer;
                            printer.startPrinting(printerOption, new PrinterListener() {
                                @Override
                                public void onError(int i) {
                                    Log.e("sdkdemo","startPrinting failed errCode = " + i);
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("sdkdemo","print success\n");
                                    try {
                                        //After printing, Feed the paper.
                                        finalPrinter.feedPaper(30);
                                    } catch (WisePosException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onReport(int i) {
                                    //The callback method is reserved and does not need to be implemented
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getAppContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                            Log.e("sdkdemo","print failed" + e.toString() + "\n");
                        }*/
                    } else {
                        if (activity_main.printScreen) {

                            Intent intent = new Intent(Topitup.getAppContext(), activity_print_screen.class);
                            intent.putExtra("slip_to_print", slip_to_print);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Topitup.getAppContext().startActivity(intent);

                        } else {
                            Printingw wpos = new Printingw();
                            wpos.data = slip_to_print;
                            wpos.init();
                            wpos.printStart();
                        }

                    }

                } else if (Topitup.DEVICE_TYPE.equals("Q1")) {

                    ThreadPoolManager.getInstance().executeTask(new Runnable() {
                        @Override
                        public void run() {

                            int print_line_counter = 0;
                            String prnt_all = "";

                            BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                            String line = null;

                            try {

                                Topitup.mIPosPrinterService.printerInit(Topitup.callback);

                                Topitup.mIPosPrinterService.PrintSpecFormatText("\n", "ST", 12, 0, Topitup.callback);

                                while ((line = bufReader.readLine()) != null) {

                                    String prnt_line = "";
                                    String size = "";

                                    //  Timber.e("BARCODE: " + prnt_line);

                                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                        if (Topitup.PRINT_BARCODE.equals("1")) {

                                            size = "1";
                                            if (line.contains("BARCODE")) {

                                                prnt_line = line.replace("BARCODE:", "");
                                            }

                                            Topitup.mIPosPrinterService.setPrinterPrintAlignment(1, Topitup.callback);
                                            Topitup.mIPosPrinterService.printBarCode(prnt_line, 2, 6, 14, 2, Topitup.callback);
                                            //Topitup.mIPosPrinterService.printBarCode(prnt_line, 8, 6, 14, 2, Topitup.callback);
                                            Topitup.mIPosPrinterService.printBlankLines(1, 25, Topitup.callback);
                                            //Topitup.mIPosPrinterService.PrintSpecFormatText(prnt_line + "\n", "ST", 24, 0, Topitup.callback);
                                            //Topitup.mIPosPrinterService.printBlankLines(1, 25, Topitup.callback);

                                        }

                                    } else {

                                        if (line.length() > 1) {
                                            prnt_line = line.substring(1);
                                            size = "" + line.charAt(0);
                                        }

                                        prnt_line = prnt_line.trim();

                                        if (prnt_line.length() == 0) {

                                            print_line_counter++;
                                            if (print_line_counter < 2) {
                                                //prnt_all = prnt_all +  "\n";

                                                Topitup.mIPosPrinterService.printBlankLines(2, 8, Topitup.callback);

                                                //Topitup.mIPosPrinterService.printerPerformPrint(40, Topitup.callback);
                                            }

                                        } else if (size.equals("1")) {

                                            print_line_counter = 0;

                                            //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
//                                    Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 24, Topitup.callback);

                                            //prnt_all = prnt_all + prnt_line + "\n";

                                            //Topitup.mIPosPrinterService.printText(prnt_line + "\n", Topitup.callback);
                                            Topitup.mIPosPrinterService.PrintSpecFormatText(prnt_line + "\n", "ST", 24, 0, Topitup.callback);


                                        } else {

                                            print_line_counter = 0;

                                            //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
                                            //Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 48, Topitup.callback);
                                            //Topitup.mIPosPrinterService.printText(prnt_line,  Topitup.callback);

                                            //System.out.println(prnt_line.substring(0, 16));
                                            //System.out.println(prnt_line.substring(14, 32));

                                            if (prnt_line.length() <= 16) {

                                                Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line + "\n", "ST", 48, Topitup.callback);

                                            } else {
                                                Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(0, 16) + "\n", "ST", 48, Topitup.callback);
                                                Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(16) + "\n", "ST", 48, Topitup.callback);
                                            }

                                            //prnt_all = prnt_all + prnt_line + "\n";


                                        }

                                    }

                                }


//                            Topitup.mIPosPrinterService.printerInit(Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("Top it Up Slip\nDate    User\n2019-10-21     Shaun Tesr\nasdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("123 123 144\n   12323\n", "ST", 48, Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("asdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("     Top it Up\n    www.topitup.co.za", "ST", 32, Topitup.callback);


                                //Topitup.mIPosPrinterService.printText(prnt_all,  Topitup.callback);

                                Topitup.mIPosPrinterService.printerPerformPrint(80, Topitup.callback);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }

            } else {

                if (Topitup.DEVICE_TYPE.equals("MOBILE") || Topitup.DEVICE_TYPE.equals("TABLET")) {
                    Toast.makeText(getAppContext(), "Please connect USB or Bluetooth", Toast.LENGTH_LONG).show();

                } /*else {

                    if (Printooth.INSTANCE.hasPairedPrinter()) {
                        BluetoothPrinter bluetoothPrinter = new BluetoothPrinter();

                        bluetoothPrinter.initView();
                        Timber.i("PRINTER: printing...");
                        try {
                            bluetoothPrinter.printdata = slip_to_print;
                            bluetoothPrinter.printData();
                            bluetoothPrinter.print();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }*/
            }


//            }
            Log.i("Device=", Topitup.DEVICE_TYPE);

        }
        if (Topitup.DEVICE_TYPE.equals("QCOM SHOP1")) {


            QSPrinter QSP = new QSPrinter();
            QSPrinter.print(slip_to_print);


        }


      /*  if (Topitup.DEVICE_TYPE.equals("Q1")) {


            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {


                    int print_line_counter = 0;
                    String prnt_all = "";

                    BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                    String line = null;

                    try {

                        Topitup.mIPosPrinterService.printerInit(Topitup.callback);

                        Topitup.mIPosPrinterService.PrintSpecFormatText("\n", "ST", 12, 0, Topitup.callback);

                        while ((line = bufReader.readLine()) != null) {

                            String prnt_line = "";
                            String size = "";


                            //  Timber.e("BARCODE: " + prnt_line);

                            if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                if (Topitup.PRINT_BARCODE.equals("1")) {

                                    size = "1";
                                    if (line.contains("BARCODE")) {

                                        prnt_line = line.replace("BARCODE:", "");
                                    }

                                    Topitup.mIPosPrinterService.setPrinterPrintAlignment(1, Topitup.callback);
                                    Topitup.mIPosPrinterService.printBarCode(prnt_line, 2, 6, 14, 2, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printBarCode(prnt_line, 8, 6, 14, 2, Topitup.callback);
                                    Topitup.mIPosPrinterService.printBlankLines(1, 25, Topitup.callback);
                                    //Topitup.mIPosPrinterService.PrintSpecFormatText(prnt_line + "\n", "ST", 24, 0, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printBlankLines(1, 25, Topitup.callback);

                                }

                            } else {

                                if (line.length() > 1) {
                                    prnt_line = "" + line.substring(1);
                                    size = "" + line.charAt(0);
                                }

                                prnt_line = prnt_line.trim();

                                if (prnt_line.length() == 0) {

                                    print_line_counter++;
                                    if (print_line_counter < 2) {
                                        //prnt_all = prnt_all +  "\n";

                                        Topitup.mIPosPrinterService.printBlankLines(2, 8, Topitup.callback);

                                        //Topitup.mIPosPrinterService.printerPerformPrint(40, Topitup.callback);
                                    }

                                } else if (size.equals("1")) {

                                    print_line_counter = 0;

                                    //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
//                                    Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 24, Topitup.callback);

                                    //prnt_all = prnt_all + prnt_line + "\n";

                                    //Topitup.mIPosPrinterService.printText(prnt_line + "\n", Topitup.callback);
                                    Topitup.mIPosPrinterService.PrintSpecFormatText(prnt_line + "\n", "ST", 24, 0, Topitup.callback);


                                } else {

                                    print_line_counter = 0;

                                    //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 48, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printText(prnt_line,  Topitup.callback);

                                    //System.out.println(prnt_line.substring(0, 16));
                                    //System.out.println(prnt_line.substring(14, 32));

                                    if (prnt_line.length() <= 16) {

                                        Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line + "\n", "ST", 48, Topitup.callback);

                                    } else {
                                        Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(0, 16) + "\n", "ST", 48, Topitup.callback);
                                        Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(16) + "\n", "ST", 48, Topitup.callback);
                                    }

                                    //prnt_all = prnt_all + prnt_line + "\n";


                                }

                            }

                        }


//                            Topitup.mIPosPrinterService.printerInit(Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("Top it Up Slip\nDate    User\n2019-10-21     Shaun Tesr\nasdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("123 123 144\n   12323\n", "ST", 48, Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("asdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("     Top it Up\n    www.topitup.co.za", "ST", 32, Topitup.callback);


                        //Topitup.mIPosPrinterService.printText(prnt_all,  Topitup.callback);

                        Topitup.mIPosPrinterService.printerPerformPrint(80, Topitup.callback);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }*/


        if (Topitup.DEVICE_TYPE.equals("ZKC")) {

            try {

                mIzkcService.sendRAWData("printer", new byte[]{0x1b, 0x4e, 0x04, 0x01});

                BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                String line = null;
                boolean size_normal = true;

                if (mIzkcService != null && mIzkcService.checkPrinterAvailable()) {

                    while ((line = bufReader.readLine()) != null) {

                        String prnt_line = "";
                        String size = "";

                        if (line.length() > 1) {
                            prnt_line = line.substring(1);
                            size = "" + line.charAt(0);
                        }

                        //Timber.i("LINE: " + line);
                        // Timber.i("SIZE: " + size);

                        if (prnt_line.length() == 0) {

                            //Topitup.setting.mPosPrintLn();

                            if (!size_normal) {
                                mIzkcService.setFontSize(0);
                            }
                            size_normal = true;

                            print_line_counter++;
                            if (print_line_counter < 2) {
                                mIzkcService.printGBKText("\n");
                            }

                        } else if (size.equals("1")) {

                            print_line_counter = 0;

                            if (!size_normal) {
                                mIzkcService.setFontSize(0);
                            }
                            size_normal = true;
                            //Topitup.setting.mPosPrnStr(prnt_line);

                            mIzkcService.printGBKText(prnt_line + "\n");

                        } else {

                            print_line_counter = 0;

                            if (size_normal) {
                                mIzkcService.setFontSize(1);
                            }
                            size_normal = false;

                            mIzkcService.printGBKText(prnt_line + "\n");

                        }

                    }

                    mIzkcService.printGBKText("\n\n\n");

                    is_busy_with_voucher = false;

                } else {

                    //Timber.i("PRINTER: no BB...");

                    Message msg = new Message();
                    msg.what = 500;
                    // mContext.mHandler.sendMessage(msg);

                    is_busy_with_voucher = false;

                }

            } catch (Exception e) {
                Message msg = new Message();
                msg.what = 500;
                // mContext.mHandler.sendMessage(msg);

                is_busy_with_voucher = false;
            }


            try {
                mIzkcService.setFontSize(0);
            } catch (Exception e) {
                //
            }
        }
        //Toasty.error(Topitup.getAppContext(), Topitup.DEVICE_TYPE, Toast.LENGTH_LONG).show();
        if (Topitup.DEVICE_TYPE.equals("SUNMI")) {


          /*  Toasty.error(getAppContext(), "I=" + SunmiPrintHelper.getInstance().getPrinterSerialNo(), 8000, true).show();

            SunmiPrintHelper.getInstance().printText("test", 24, true, true, "test.ttf");
            SunmiPrintHelper.getInstance().feedPaper();*/


            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                @Override
                public void run() {


                    int print_line_counter = 0;
                    String prnt_all = "";

                    BufferedReader bufReader = new BufferedReader(new StringReader(slip_to_print));
                    String line = null;

                    try {

                        // Topitup.mIPosPrinterService.printerInit(Topitup.callback);

                        // Topitup.mIPosPrinterService.PrintSpecFormatText("\n", "ST", 12, 0, Topitup.callback);

                        while ((line = bufReader.readLine()) != null) {

                            String prnt_line = "";
                            String size = "";


                            //  Timber.e("BARCODE: " + prnt_line);

                            if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                                if (Topitup.PRINT_BARCODE.equals("1")) {

                                    size = "1";
                                    if (line.contains("BARCODE")) {

                                        prnt_line = line.replace("BARCODE:", "");
                                    }


                                }

                            } else {

                                if (line.length() > 1) {
                                    prnt_line = line.substring(1);
                                    size = "" + line.charAt(0);
                                }

                                prnt_line = prnt_line.trim();

                                if (prnt_line.length() == 0) {

                                    print_line_counter++;
                                    if (print_line_counter < 2) {
                                        //prnt_all = prnt_all +  "\n";

                                        // Topitup.mIPosPrinterService.printBlankLines(2, 8, Topitup.callback);
                                        //  SunmiPrintHelper.getInstance().printText("test", 24, true, true, "test.ttf");
                                        //Topitup.mIPosPrinterService.printerPerformPrint(40, Topitup.callback);
                                    }

                                } else if (size.equals("1")) {

                                    print_line_counter = 0;

                                    //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
//                                    Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 24, Topitup.callback);

                                    //prnt_all = prnt_all + prnt_line + "\n";

                                    //Topitup.mIPosPrinterService.printText(prnt_line + "\n", Topitup.callback);
                                    // SunmiPrintHelper.getInstance().printText(prnt_line + "\n", 24, false, false, "test.ttf");

                                    //  SunmiPrintHelper.getInstance().feedPaper();


                                } else {

                                    print_line_counter = 0;

                                    //Topitup.mIPosPrinterService.printBlankLines(1, 8, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line, "ST", 48, Topitup.callback);
                                    //Topitup.mIPosPrinterService.printText(prnt_line,  Topitup.callback);

                                    //System.out.println(prnt_line.substring(0, 16));
                                    //System.out.println(prnt_line.substring(14, 32));

                                    if (prnt_line.length() <= 16) {

                                        //Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line + "\n", "ST", 48, Topitup.callback);
                                        //  SunmiPrintHelper.getInstance().printText(prnt_line + "\n", 36, true, false, "test.ttf");

                                    } else {
                                        // SunmiPrintHelper.getInstance().printText(prnt_line.substring(0, 16) + "\n", 36, false, true, "test.ttf");
                                        //SunmiPrintHelper.getInstance().printText(prnt_line.substring(16) + "\n", 36, true, false, "test.ttf");
                                        // Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(0, 16) + "\n", "ST", 48, Topitup.callback);
                                        //Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(16, prnt_line.length()) + "\n", "ST", 48, Topitup.callback);
                                    }

                                    //prnt_all = prnt_all + prnt_line + "\n";


                                }

                            }

                        }


//                            Topitup.mIPosPrinterService.printerInit(Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("Top it Up Slip\nDate    User\n2019-10-21     Shaun Tesr\nasdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("123 123 144\n   12323\n", "ST", 48, Topitup.callback);
//
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("asdf asfd fasfasf asdf\nasdfa sfas fsafas fas fasf\n\n", "ST", 32, Topitup.callback);
//                            Topitup.mIPosPrinterService.printSpecifiedTypeText("     Top it Up\n    www.topitup.co.za", "ST", 32, Topitup.callback);


                        //Topitup.mIPosPrinterService.printText(prnt_all,  Topitup.callback);

                        //   Topitup.mIPosPrinterService.printerPerformPrint(80,  Topitup.callback);
                        // SunmiPrintHelper.getInstance().feedPaper();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
        if (Topitup.DEVICE_TYPE.equals("Z91")) {


        }


    }

    private static boolean checkPrinerStatus(UsbDevice dev, Context mcontext) {
        SharedPreferences settings = mContext.getSharedPreferences("TIUPREF", 0);

        int vendorId = settings.getInt("vendorId", 1046);
        int productId = settings.getInt("productId", 20497);

        PrinterStatusChecker statusChecker = new PrinterStatusChecker(mcontext);
        boolean status = statusChecker.checkPrinterStatus(vendorId, productId);
        Log.e("printer status", "printer...no paper condition....." + status);
        return status;
    }

    public static List<String> wrapText(String text, int maxChars) {
        List<String> result = new ArrayList<>();
        while (text.length() > maxChars) {
            int breakIndex = maxChars;

            // Find a space to break the line
            while (breakIndex > 0 && text.charAt(breakIndex) != ' ') {
                breakIndex--;
            }

            // If no space found, force break at maxChars
            if (breakIndex == 0) breakIndex = maxChars;

            // Add trimmed line to result
            result.add(text.substring(0, breakIndex).trim());

            // Update remaining text
            text = text.substring(breakIndex).trim();
        }

        // Add last remaining part
        if (!text.isEmpty()) {
            result.add(text);
        }

        return result;
    }

    public static void openCashDrawer(UsbController usbController, UsbDevice device) {
        int mode = 0;     // Mode to control the cashbox (usually 0 or 1 depending on the printer)
        int time1 = 100;  // Pulse on time in milliseconds (adjust based on your printer requirements)
        int time2 = 100;  // Pulse off time in milliseconds (adjust based on your printer requirements)

        Log.e("cash drawer open", "open");
        // Generate the command byte array
        byte[] command = POS_Set_Cashbox(mode, time1, time2);

        if (command != null) {
            // Send the command to the printer to open the cash drawer
            usbController.sendByte(command, device);
        } else {
            System.out.println("Invalid parameters for cashbox command");

        }
    }

    public static void printDrawableOverUSB(Context context, UsbDevice dev, UsbController usbCtrl) {
        // Step 1: Get the bitmap from drawable
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_splash);
        int printerWidth = 394; // Full width in pixels for your printer
        int logoWidth = 384;// Resize original bitmap
        Bitmap resized = Other.resizeImage(bmp, logoWidth, bmp.getHeight() * logoWidth / bmp.getWidth());
        // Create a new blank bitmap with full printer width and same height
        Bitmap centeredBitmap = Bitmap.createBitmap(printerWidth, resized.getHeight(), Bitmap.Config.ARGB_8888);

        // Center the resized bitmap onto the new bitmap
        Canvas canvas = new Canvas(centeredBitmap);
        int left = (printerWidth - logoWidth) / 2;
        canvas.drawColor(Color.WHITE); // Background white
        canvas.drawBitmap(resized, left, 0, null);

        // Optional: Remove non-black pixels (if needed)
        Bitmap cleanLogo = extractPureBlackContent(centeredBitmap, 120);  // adjust threshold

        // Step 2: Generate ESC/POS command from bitmap
        byte[] imageCommand = PrintPicture.POS_PrintBMP(cleanLogo, logoWidth, 0); // 0 = alignment

        // Step 3: Send init and image commands via usbCtrl
        usbCtrl.sendByte(PrinterCommand.POS_Set_PrtInit(), dev); // Printer init
        usbCtrl.sendByte(new byte[]{0x1b, 0x61, 0x01}, dev); // Center align (ESC a 1)
        usbCtrl.sendByte(imageCommand, dev); // Send image data
        usbCtrl.sendByte(PrinterCommand.POS_Set_LF(), dev); // Line feed
    }

    public static void openCashDrawerBluetooth() {
        if (activity_settings.isBluetoothConnected) {

            int mode = 0;     // Mode to control the cash drawer (usually 0 or 1 depending on the printer)
            int time1 = 100;  // Pulse on time in milliseconds (adjust based on your printer requirements)
            int time2 = 100;  // Pulse off time in milliseconds (adjust based on your printer requirements)

            Log.e("cash drawer open", "open");
            byte[] command = POS_Set_Cashbox(mode, time1, time2);

            if (command != null) {
                try {
                    // Send the command to the Bluetooth printer to open the cash drawer
                    activity_settings.SendDataByte(command, getAppContext());

                } catch (Exception e) {
                    Log.e("Bluetooth Error", "Failed to send cash drawer command", e);
                }
            } else {
                System.out.println("Invalid parameters for cashbox command");
            }
        } else {
            Toast.makeText(mContext, "Please connect Bluetooth Printer", Toast.LENGTH_SHORT).show();
        }

    }


    public static void initiateUsbPrinter(UsbController usbCtrl) {
        Log.e("initiate ", "usb.....1111111.....");

        int[][] u_infor;

        u_infor = new int[8][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0416;
        u_infor[4][1] = 0x5011;
        u_infor[5][0] = 0x0416;
        u_infor[5][1] = 0xAABB;
        u_infor[6][0] = 0x1659;
        u_infor[6][1] = 0x8965;
        u_infor[7][0] = 0x0483;
        u_infor[7][1] = 0x5741;


        usbCtrl.close();
        int i = 0;
        dev = null;
        for (i = 0; i < 8; i++) {
            dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
            if (dev != null)
                break;
        }
        if (dev != null) {
           /* if (!(usbCtrl.isHasPermission(dev))) {
                //Log.d("usb调试","请求USB设备权限.");
                usbCtrl.getPermission(dev);
            } else {
                Toast.makeText(Topitup.getAppContext(), "permission granted",
                        Toast.LENGTH_SHORT).show();
            }*/
        } else {
            Toast.makeText(Topitup.getAppContext(), "usb not connected",
                    Toast.LENGTH_SHORT).show();
        }

    }


    /* public static void initiateUsbPrinterUSB(){
         Log.e("initiate ","usb.....22222222.....");

         int[][] u_infor;

          activity_settings.usbCtrl = new UsbController((Activity) Topitup.getAppContext(),mHandler1);
          u_infor = new int[8][2];
          u_infor[0][0] = 0x1CBE;
          u_infor[0][1] = 0x0003;
          u_infor[1][0] = 0x1CB0;
          u_infor[1][1] = 0x0003;
          u_infor[2][0] = 0x0483;
          u_infor[2][1] = 0x5740;
          u_infor[3][0] = 0x0493;
          u_infor[3][1] = 0x8760;
          u_infor[4][0] = 0x0416;
          u_infor[4][1] = 0x5011;
          u_infor[5][0] = 0x0416;
          u_infor[5][1] = 0xAABB;
          u_infor[6][0] = 0x1659;
          u_infor[6][1] = 0x8965;
          u_infor[7][0] = 0x0483;
          u_infor[7][1] = 0x5741;


         activity_settings.usbCtrl.close();
          int  i = 0;
          activity_settings.dev = null;
          for( i = 0 ; i < 8 ; i++ ){
              activity_settings.dev = activity_settings.usbCtrl.getDev(u_infor[i][0],u_infor[i][1]);
              if(activity_settings.dev != null)
                  break;
          }
          if( activity_settings.dev != null ) {
              if (!(activity_settings.usbCtrl.isHasPermission(activity_settings.dev))) {
                  activity_settings.usbCtrl.getPermission(activity_settings.dev);
              } else {
                  Toast.makeText(Topitup.getAppContext(), Topitup.getAppContext().getString(R.string.msg_getpermission),
                          Toast.LENGTH_SHORT).show();
              }
          }

      }
      @SuppressLint("HandlerLeak") private static final Handler mHandler1 = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              switch (msg.what) {
                  case UsbController.USB_CONNECTED:


                      Toast.makeText(Topitup.getAppContext(),"usb connected",Toast.LENGTH_LONG).show();

                      break;
                  default:
                      break;
              }
          }
      };*/
    private static class QSPrinter {

        static final String data = null;

        public QSPrinter() {

            String device = getAppContext().getString(R.string.print_device);
            String baudrate = getAppContext().getString(R.string.print_baudrate);
            Toasty.error(getAppContext(), "Device=" + device, Toast.LENGTH_LONG).show();

            try {

                int portOpen = Print.PortOpen(getAppContext(), "Serial," + device + "," + baudrate);
                if (portOpen == 0) {
                    // stopCustomDialog("printer","printer sucess");
                    Toasty.error(getAppContext(), "Printer Success", Toast.LENGTH_LONG).show();


                    // Print.PrintText(slip_to_print, 1, 0, 14);

                }
            } catch (Exception e) {
                Toasty.error(getAppContext(), "Printer Problem", Toast.LENGTH_LONG).show();
                // stopCustomDialog("printer","printer problem");
            }
        }


        public static void print(String data) {
            // Print.PrintText( "\n", 1, 0, 14);


            try {
                String line = null;
                int print_line_counter = 0;
                BufferedReader bufReader = new BufferedReader(new StringReader(data));

                while ((line = bufReader.readLine()) != null) {


                    String prnt_line = "";
                    String size = "";

                    if (line.length() >= 8 && line.startsWith("BARCODE:")) {

                        //

                        size = "1";
                        if (line.contains("BARCODE")) {

                            prnt_line = line.replace("BARCODE:", "");
                        }

                        if (Print.IsOpened()) {
                            if (Topitup.PRINT_BARCODE.equals("1")) {
                                Print.PrintBarCode(Print.BC_EAN13, prnt_line, 3, 80, 2, 1);
                            }
                            //Print.PrintAndFeed(450);

                            //Print.PrintText( "\n\n\n\n\n\n\n\n");
                        }
                        //// Print.CutPaper(Print.PARTIAL_CUT);


                        //  }

                    } else {

                        if (line.length() > 1) {
                            prnt_line = line.substring(1);
                            size = "" + line.charAt(0);
                        }

                        prnt_line = prnt_line.trim();

                        if (prnt_line.length() == 0) {

                            print_line_counter++;
                            if (print_line_counter < 2) {

                                Print.PrintText("\n");
                                //Topitup.mIPosPrinterService.printBlankLines(2, 8, Topitup.callback);

                            }

                        } else if (size.equals("1")) {

                            print_line_counter = 0;
                            // Print.SetLeftMargin(0);
                            Print.PrintText(prnt_line, 0, 0, 0);
                            //Topitup.mIPosPrinterService.PrintSpecFormatText(prnt_line + "\n", "ST", 24, 0, Topitup.callback);


                        } else {

                            print_line_counter = 0;


                            if (prnt_line.length() <= 16) {
                                Print.PrintText(prnt_line, 0, 0, 17);
                                //  Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line + "\n", "ST", 48, Topitup.callback);

                            } else {
                                Print.PrintText(prnt_line.substring(0, 16) + "\n", 0, 0, 17);
                                Print.PrintText(prnt_line.substring(16), 0, 0, 17);
                                // Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(0, 16) + "\n", "ST", 48, Topitup.callback);
                                // Topitup.mIPosPrinterService.printSpecifiedTypeText(prnt_line.substring(16, prnt_line.length()) + "\n", "ST", 48, Topitup.callback);
                            }


                        }

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                Print.PrintText("\n\n\n\n\n\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void showUsbNotConnectedDialog(Context conn) {
        Dialog dialog = new Dialog(conn);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.usb_not_connected);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView txt_ok = dialog.findViewById(R.id.txt_ok);


        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
