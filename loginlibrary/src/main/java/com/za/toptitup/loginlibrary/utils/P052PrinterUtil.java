package com.za.toptitup.loginlibrary.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.printer.BarcodeType;
import com.wisepos.smartpos.printer.Printer;
import com.wisepos.smartpos.printer.PrinterListener;
import com.wisepos.smartpos.printer.TextInfo;
import com.wisepos.smartpos.WisePosException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wisepos.smartpos.errorcode.WisePosErrorCode.ERR_SUCCESS;

public class P052PrinterUtil {

    private static final String TAG = "P052Printer";
    private static final String SANS_SERIF_LIGHT = "sans-serif";
    private static final int PRINT_STYLE_LEFT = 0x01;
    private static final int GRAY_LEVEL = 5;
    private static final int MAX_CHARS_PER_LINE = 32;

    private Printer mPrinter;
    private Context mContext;

    public P052PrinterUtil(Context context) {
        this.mContext = context.getApplicationContext();
        initPrinter();
    }

    private void initPrinter() {
        try {
            mPrinter = WisePosSdk.getInstance().getPrinter();
        } catch (Exception e) {
            Log.e(TAG, "Printer initialization failed", e);
        }
    }

    public void printData(String data) {
        if (mPrinter == null) {
            Log.e(TAG, "Printer not initialized");
            showToast("Printer not available");
            return;
        }

        try {
            // Initialize printer
            mPrinter.initPrinter();

            // Set gray level
            int ret = mPrinter.setGrayLevel(GRAY_LEVEL);
            if (ret != ERR_SUCCESS) {
                Log.e(TAG, "Failed to set gray level, errCode = " + Integer.toHexString(ret));
                return;
            }

            // Check printer status
            Map<String, Object> statusMap = mPrinter.getPrinterStatus();
            if (statusMap == null) {
                Log.e(TAG, "Failed to get printer status");
                showToast("Printer status check failed");
                return;
            }

            // Check for paper
            if ((byte) statusMap.get("paper") == 1) {
                Log.e(TAG, "Printer out of paper");
                showToast("Printer out of paper");
                return;
            }

            // Set font
            Bundle fontBundle = new Bundle();
            fontBundle.putString("font", SANS_SERIF_LIGHT);
            mPrinter.setPrintFont(fontBundle);
            mPrinter.setLineSpacing(1);

            // Process and print data
            BufferedReader bufReader = new BufferedReader(new StringReader(data));
            String line;

            while ((line = bufReader.readLine()) != null) {
                if (line.isEmpty()) continue;

                processLine(line);
            }

            // Start printing
            Bundle printerOption = new Bundle();
            mPrinter.startPrinting(printerOption, new PrinterListener() {
                @Override
                public void onError(int errorCode) {
                    Log.e(TAG, "Print failed, errCode = " + errorCode);
                    showToast("Print failed: " + errorCode);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Print completed successfully");
                    try {
                        mPrinter.feedPaper(30);
                    } catch (WisePosException e) {
                        Log.e(TAG, "Paper feed failed", e);
                    }
                }

                @Override
                public void onReport(int i) {
                    // Reserved callback
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Print operation failed", e);
            showToast("Print failed: " + e.getMessage());
        }
    }

    private void processLine(String line) {
        try {
            String first = line.substring(0, 1);

            // Handle barcode
            if (line.startsWith("BARCODE:")) {
                if (Topitup.PRINT_BARCODE.equals("1")) {
                    String barcodeData = line.replace("BARCODE:", "").trim();
                    mPrinter.addBarCode(BarcodeType.BARCODE_TYPE_BARCODE_128, 380, 90, barcodeData);
                }
                return;
            }

            // Handle regular text
            String prnt_line = line.substring(1).trim();
            TextInfo textInfo = new TextInfo();
            textInfo.setAlign(PRINT_STYLE_LEFT);

            if (first.equals("0") || first.equals("1")) {
                // Normal text
                textInfo.setFontSize(25);
                textInfo.setBold(true);

                List<String> wrappedLines = wrapText(prnt_line, MAX_CHARS_PER_LINE);
                for (String wrappedLine : wrappedLines) {
                    textInfo.setText(wrappedLine);
                    mPrinter.addSingleText(textInfo);
                }
            } else {
                // Bold/larger text
                textInfo.setBold(true);
                textInfo.setFontSize(33);
                textInfo.setText(prnt_line);
                mPrinter.addSingleText(textInfo);
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to process line: " + line, e);
        }
    }

    private List<String> wrapText(String text, int maxChars) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        while (text.length() > maxChars) {
            int breakIndex = maxChars;

            // Find a space to break the line
            while (breakIndex > 0 && text.charAt(breakIndex) != ' ') {
                breakIndex--;
            }

            // If no space found, force break at maxChars
            if (breakIndex == 0) {
                breakIndex = maxChars;
            }

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

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
        );
    }
}