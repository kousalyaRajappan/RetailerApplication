package com.za.toptitup.loginlibrary.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import wangpos.sdk4.libbasebinder.Printer;
import wangpos.sdk4.libbasebinder.Printer.Align;

public class WPosPrinterUtil {

    private Printer mPrinter;
    private Context mContext;

    public WPosPrinterUtil(Context context) {
        this.mContext = context.getApplicationContext();
        initPrinter();
    }

    private void initPrinter() {
        try {
            mPrinter = new Printer(mContext);
            mPrinter.setPrintFontType(null, "");
        } catch (IOException | RemoteException e) {
            Log.e("WPosPrinter", "Initialization failed", e);
        }
    }

    public void printData(String data) {
        if (mPrinter == null) {
            Log.e("WPosPrinter", "Printer not initialized");
            return;
        }

        try {
            int result = mPrinter.printInit();
            mPrinter.clearPrintDataCache();

            int print_line_counter = 0;
            BufferedReader bufReader = new BufferedReader(new StringReader(data));
            String line;

            while ((line = bufReader.readLine()) != null) {
                String prnt_line = "";
                String size = "";

                if (line.length() >= 8 && line.startsWith("BARCODE:")) {
                    if (Topitup.PRINT_BARCODE.equals("1")) {
                        prnt_line = line.replace("BARCODE:", "");
                        try {
                            result = mPrinter.printBarCodeBase(prnt_line,
                                    Printer.BarcodeType.EAN_13,
                                    Printer.BarcodeWidth.LARGE, 65, 20);
                            result = mPrinter.printString(prnt_line,
                                    Printer.Font.DEFAULT, 25, Align.CENTER,
                                    true, false, false);
                        } catch (Exception e) {
                            Log.e("WPosPrinter", "Barcode print failed", e);
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
                            result = mPrinter.printPaper(2);
                        }
                    } else if (size.equals("1")) {
                        print_line_counter = 0;
                        result = mPrinter.printString(prnt_line,
                                Printer.Font.DEFAULT, 25, Align.LEFT,
                                true, false, false);
                    } else if (size.equals("3")) {
                        print_line_counter = 0;
                        result = mPrinter.printString(prnt_line,
                                Printer.Font.DEFAULT_BOLD, 42, Align.LEFT,
                                true, false, false);
                    } else {
                        print_line_counter = 0;
                        result = mPrinter.printString(prnt_line,
                                Printer.Font.DEFAULT_BOLD, 30, Align.LEFT,
                                true, false, false);
                    }
                }
            }

            result = mPrinter.printPaper(100);
            result = mPrinter.printFinish();

            Log.d("WPosPrinter", "Print completed successfully");

        } catch (Exception e) {
            Log.e("WPosPrinter", "Print failed", e);
        }
    }
}