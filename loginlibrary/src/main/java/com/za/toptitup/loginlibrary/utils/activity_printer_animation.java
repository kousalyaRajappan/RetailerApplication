package com.za.toptitup.loginlibrary.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.za.toptitup.loginlibrary.R;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class activity_printer_animation extends Activity {

    private static final String TAG = "PrinterAnimation";

    private TextView tvReceiptContent;
    private LinearLayout contentContainer;
    private FrameLayout printerViewport;
    private ImageView ivBarcode,ivQrCode;
    private String qrData = null;

    private String receiptData, receiptDatacopy;
    private boolean printingStarted = false;
    private String barcodeData = null;
    private String txid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_animation);

        receiptDatacopy = getIntent().getStringExtra("slip_to_print");
        receiptData = getIntent().getStringExtra("slip_to_print");
        txid = getIntent().getStringExtra("txid");


        if (receiptData == null || receiptData.isEmpty()) {
            finish();
            return;
        }

        tvReceiptContent = findViewById(R.id.tvReceiptContent);
        contentContainer = findViewById(R.id.contentContainer);
        printerViewport = findViewById(R.id.printerViewport);
        ivBarcode = findViewById(R.id.ivBarcode);
        ivQrCode = findViewById(R.id.ivQrCode);

        setupReceiptText();

        // Wait 2 seconds while centered, then trigger animation AND print together
        new Handler(Looper.getMainLooper()).postDelayed(this::startPrintingProcess, 2000);
    }

    private void setupReceiptText() {
        try {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            BufferedReader reader = new BufferedReader(new StringReader(receiptData));
            String line;
            boolean firstLineHandled = false;

            while ((line = reader.readLine()) != null) {

                // Check for barcode line
                if (line.length() >= 8 && line.startsWith("BARCODE:")) {
                    if (Topitup.PRINT_BARCODE.equals("1")) {
                        barcodeData = line.replace("BARCODE:", "").trim();
                        Log.d(TAG, "Barcode found: " + barcodeData);
                    }
                    continue;
                }

                // Remove printer control characters
                line = line.replaceAll("^[0-9]", "").trim();

                if (line.isEmpty()) continue;

                boolean isAmountLine = line.matches(".*-\\s*R\\d+.*");
                boolean isPinLine = line.matches("\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}");

                // -------- SPACING RULES --------
                if (!firstLineHandled) {
                    ssb.append("\n"); // Padding for first text
                }

                if (isAmountLine || isPinLine) {
                    ssb.append("\n"); // Space before amount & pin
                }

                int start = ssb.length();
                ssb.append(line).append("\n");
                int end = ssb.length();

                // -------- BOLD RULES --------
                if (!firstLineHandled || isAmountLine || isPinLine) {
                    ssb.setSpan(
                            new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                firstLineHandled = true;
            }

            // -------- FOOTER --------
            int footerStart = ssb.length();
            // ssb.append("\n\nTop it Up\nwww.topitup.co.za");

            ssb.setSpan(
                    new android.text.style.AlignmentSpan.Standard(
                            android.text.Layout.Alignment.ALIGN_CENTER),
                    footerStart,
                    ssb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            tvReceiptContent.setText(ssb);

        } catch (Exception e) {
            tvReceiptContent.setText(receiptData);
        }

        // Generate and display barcode if found
        if (barcodeData != null && !barcodeData.isEmpty()) {
            generateBarcode(barcodeData);
        } else {
            ivBarcode.setVisibility(View.GONE);
        }
        if (txid != null && !txid.isEmpty()) {
            ivQrCode.setVisibility(View.VISIBLE);
            generateQRCode(txid);
        } else {
            ivQrCode.setVisibility(View.GONE);
        }
        // Setup paper size and position based on content length
        contentContainer.post(this::setupPaperSizeAndPosition);
    }
  /*  private void generateQRCode(String data) {
        try {
            int size = 400;
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQrCode.setImageBitmap(bitmap);
            ivQrCode.setVisibility(View.VISIBLE);

            Log.d(TAG, "QR code generated successfully: " + data);

        } catch (Exception e) {
            Log.e(TAG, "Failed to generate QR code: " + e.getMessage());
            ivQrCode.setVisibility(View.GONE);
        }
    }*/
  private void generateQRCode(String data) {
      try {
          int size = 500;

          Map<EncodeHintType, Object> hints = new HashMap<>();
          hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
          hints.put(EncodeHintType.MARGIN, 2);

          MultiFormatWriter writer = new MultiFormatWriter();
          BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);

          Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
          for (int x = 0; x < size; x++) {
              for (int y = 0; y < size; y++) {
                  bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
              }
          }

          ivQrCode.setImageBitmap(bitmap);
          ivQrCode.setScaleType(ImageView.ScaleType.FIT_CENTER);
          ivQrCode.setVisibility(View.VISIBLE);

          Log.d(TAG, "QR code generated successfully: " + data);

      } catch (Exception e) {
          Log.e(TAG, "Failed to generate QR code: " + e.getMessage());
          ivQrCode.setVisibility(View.GONE);
      }
  }
    private void generateBarcode(String data) {
        try {
            // Use EAN13 format (13 digits) or CODE128 for alphanumeric
            BarcodeFormat format = BarcodeFormat.CODE_128;

            // If data is numeric and 13 digits, use EAN13
            if (data.matches("\\d{13}")) {
                format = BarcodeFormat.EAN_13;
            } else if (data.matches("\\d{8}")) {
                format = BarcodeFormat.EAN_8;
            }

            int width = 600;
            int height = 130;

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, format, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ivBarcode.setImageBitmap(bitmap);
            ivBarcode.setVisibility(View.VISIBLE);

            Log.d(TAG, "Barcode generated successfully: " + data);

        } catch (Exception e) {
            Log.e(TAG, "Failed to generate barcode: " + e.getMessage());
            ivBarcode.setVisibility(View.GONE);
        }
    }

    /**
     * Setup paper size based on content length and position it centered
     */
    private void setupPaperSizeAndPosition() {
        // Measure the TextView to get actual content height
        tvReceiptContent.measure(
                View.MeasureSpec.makeMeasureSpec(contentContainer.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
        );

        int textHeight = tvReceiptContent.getMeasuredHeight();
        int paddingTop = contentContainer.getPaddingTop();
        int paddingBottom = contentContainer.getPaddingBottom();

        // Add barcode height if present
        int barcodeHeight = 0;
        if (ivBarcode.getVisibility() == View.VISIBLE) {
            ivBarcode.measure(
                    View.MeasureSpec.makeMeasureSpec(contentContainer.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
            );
            barcodeHeight = ivBarcode.getMeasuredHeight() + 32; // 16dp margin top + bottom
        }
        int qrHeight = 0;
        if (ivQrCode.getVisibility() == View.VISIBLE) {
            int qrDp = 150;
            qrHeight = (int) (qrDp * getResources().getDisplayMetrics().density) + 32; // 32 = margins
        }

        int totalContentHeight = textHeight + paddingTop + paddingBottom + barcodeHeight + qrHeight;

        // Set container height to actual content height
        ViewGroup.LayoutParams params = contentContainer.getLayoutParams();
        params.height = totalContentHeight;
        contentContainer.setLayoutParams(params);

        Log.d(TAG, String.format("Paper height set to: %dpx for %d lines (text: %d, barcode: %d)",
                totalContentHeight, tvReceiptContent.getLineCount(), textHeight, barcodeHeight));

        // Center paper in viewport
        int viewportHeight = printerViewport.getHeight();
        float centerPos = (viewportHeight - totalContentHeight) / 2f;
        contentContainer.setTranslationY(centerPos);

        Log.d(TAG, String.format("Paper centered at: %.1f (viewport: %d, content: %d)",
                centerPos, viewportHeight, totalContentHeight));
    }

    private void startPrintingProcess() {
        // 1. Start the physical/logic print
        triggerActualPrint();

        // 2. Start the visual animation
        startUpwardPrinterAnimation();

        // 3. Start the vibration feedback
        startVibration();
    }

    private void startUpwardPrinterAnimation() {
        contentContainer.post(() -> {
            int contentHeight = contentContainer.getHeight();
            float startY = contentContainer.getTranslationY(); // Current centered position

            // Paper needs to travel from center position to completely exit through slot
            // Subtract ~3 lines worth of pixels to stop exactly at the right point
            int lineHeight = (int) (tvReceiptContent.getTextSize() * 1.15); // Text size * line spacing
            int offsetAdjustment = lineHeight * 3; // 3 lines worth

//            float endY = -(contentHeight + startY+900 );
            float endY = -(contentHeight  );
//


            Log.e("animation","printing height.....");
            int lineCount = tvReceiptContent.getLineCount();

            // Get printer-specific timing
            long msPerLine = getPrinterSpeed();
            long totalDuration = lineCount * msPerLine;

            // Safety bounds: minimum based on device, maximum 10s
            totalDuration = Math.max(getMinDuration(), Math.min(totalDuration, 10000));

            Log.d(TAG, String.format("Animation: from %.1f to %.1f (%d lines, %dms, %.1fms/line, offset: %d)",
                    startY, endY, lineCount, totalDuration, (float)totalDuration / lineCount, offsetAdjustment));

            ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
            animator.setDuration(totalDuration);
            animator.setInterpolator(new LinearInterpolator());

            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                contentContainer.setTranslationY(value);
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    stopVibration();
                    finishAndClose();
                }
            });

            animator.start();
        });
    }
    private long getPrinterSpeed() {
        Log.e("printer animation","animation wpos speed out side"+Topitup.DEVICE_TYPE);

        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            return 150; // Increased from 80
        } else if (Topitup.DEVICE_TYPE.equals("WPOS")) {
            if (android.os.Build.MODEL.equals("P052")) {
                return 120; // Fast WPOS model
            } else {
                Log.e("printer animation","animation wpos speed");
                return 400; // Increased from 300 - Much slower
            }
        } else {
            return 150; // Increased from 80
        }
    }


    private long getMinDuration() {
        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            return 2500; // Increased from 1500
        } else if (Topitup.DEVICE_TYPE.equals("WPOS")) {
            if (android.os.Build.MODEL.equals("P052")) {
                return 3000; // Fast WPOS model
            } else {
                return 5000; // Increased from 4500 - 5 second minimum
            }
        } else {
            return 2500; // Increased from 1500
        }
    }

    /*private long getPrinterSpeed() {
        Log.e("printer animation","animation wpos speed out side"+Topitup.DEVICE_TYPE);

        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            return 80;
        } else if (Topitup.DEVICE_TYPE.equals("WPOS")) {

                Log.e("printer animation","animation wpos speed");
                return 300; // Slow WPOS model

        } else {
            return 80; // Default
        }
    }

    private long getMinDuration() {
        if (Topitup.DEVICE_TYPE.equals("TABLET")) {
            return 1500;
        } else if (Topitup.DEVICE_TYPE.equals("WPOS")) {

                return 4500; // Slow WPOS model

        } else {
            return 1500; // Default
        }
    }*/

    private void triggerActualPrint() {
        if (printingStarted) return;
        printingStarted = true;

        new Thread(() -> {
            try {
                Log.e(TAG, "Print triggered"+txid);
                // This now fires exactly when the paper starts moving upward
                PrinterTopitup.print_data(receiptDatacopy,txid);
            } catch (Exception e) {
                Log.e(TAG, "Print error: " + e.getMessage());
            }
        }).start();
    }

    // ---------------- FEEDBACK & UTILS ----------------

    private void startVibration() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                v.vibrate(new long[]{0, 60, 40}, 0);
            }
        } catch (Exception ignored) {}
    }

    private void stopVibration() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) v.cancel();
        } catch (Exception ignored) {}
    }

    private void finishAndClose() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibration();
    }
}