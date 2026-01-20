package com.za.toptitup.loginlibrary;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

public class CustomQrScannerActivity extends AppCompatActivity {

    private BarcodeView barcodeView;
    private ImageView scanLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_qr_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        scanLine = findViewById(R.id.scanLine);

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    barcodeView.pause();
                    Intent intent = new Intent();
                    intent.putExtra("QR_RESULT", result.getText());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        startScanLineAnimation();
    }

    private void startScanLineAnimation() {
        scanLine.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(
                    scanLine,
                    "translationY",
                    0f,
                    scanLine.getParent() instanceof ImageView
                            ? ((ImageView) scanLine.getParent()).getHeight()
                            : 350f
            );
            animator.setDuration(1800);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
