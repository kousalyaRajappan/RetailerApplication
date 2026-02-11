package com.za.toptitup.retailerapp;

import static com.za.toptitup.loginlibrary.utils.Topitup.connectBluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.utils.PrinterTopitup;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class MainWebViewActivity extends AppCompatActivity {

    private WebView webView;
    ProgressDialog progressDialog;
    private int REQUEST_BLUETOOTH_PERMISSIONS = 121;

    private static final String BASE_URL =
            "https://dev.topitup.co.za";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);

        webView = findViewById(R.id.webview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        Intent intent = getIntent();

        String license = intent.getStringExtra("LICENSE");
        String pos_user_id = intent.getStringExtra("POS_USER_ID");

        if (license == null || pos_user_id == null) {
            finish();
            return;
        }

        // WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        // Cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // 🔥 LOGOUT HANDLING HERE
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                String url = request.getUrl().toString();
                if (url.contains("/printslip")) {
                    // String url = "https://dev.topitup.co.za/Retailerscan/printslip/0/395242/20";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            // Proceed with Bluetooth operations
                            connectBluetooth(MainWebViewActivity.this);
                           // bluetoothOperation(MainWebViewActivity.this);
                        } else {
                            requestBluetoothPermissions();
                        }
                    } else {
                        // For older Android versions, directly perform Bluetooth operations
                       // bluetoothOperation(MainWebViewActivity.this);
                        connectBluetooth(MainWebViewActivity.this);
                    }
// 1. Split the URL by "/"
                    String[] parts = url.split("/");

// 2. Find index of "printslip"
                    int indexPrintslip = -1;
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("printslip")) {
                            indexPrintslip = i;
                            break;
                        }
                    }

// 3. If found, extract the next values
                    if (indexPrintslip != -1 && parts.length > indexPrintslip + 3) {
                        String type = parts[indexPrintslip + 1]; // "0"
                        String txid = parts[indexPrintslip + 2]; // "395242"
                        String amount = parts[indexPrintslip + 3]; // "20"
String slip="transaction type: \" + type +\n" +
        "                                        \"**** transaction id: \" + txid +\n" +
        "                                        \"**** transaction amount: \" + amount,";
                        // 4. Show a Toast with the results
                        Toast.makeText(getApplicationContext(),
                                "transaction type: " + type +
                                        "**** transaction id: " + txid +
                                        "**** transaction amount: " + amount,
                                Toast.LENGTH_LONG).show();
                        PrinterTopitup.print_data("transaction type: " + type +
                                "**** transaction id: " + txid +
                                "**** transaction amount: " + amount);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "URL format unexpected",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                if (url.contains("/logout")) {
                   handleLogout();
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ) {
                if (url.contains("/logout")) {
                    handleLogout();
                    return true;
                }
                return false;
            }
        });

        // Build final URL
        String finalUrl =
                BASE_URL +
                        "/retailerscan/retailer_app_login/" +
                        license + "/" +
                        pos_user_id;

        webView.loadUrl(finalUrl);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth connection
                connectBluetooth(MainWebViewActivity.this);
            } else {
                Toast.makeText(this, "Bluetooth permissions are required for printing", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MainWebViewActivity", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        switch (requestCode) {
            case 3: // REQUEST_CONNECT_DEVICE
                if (resultCode == RESULT_OK && data != null) {
                    // Get the device MAC address
                    String address = data.getExtras().getString("device_address"); // DeviceListActivity.EXTRA_DEVICE_ADDRESS

                    if (address != null && BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                        // Save the last connected device
                        SharedPreferences settings = getSharedPreferences("TIUPREF", MODE_PRIVATE);
                        settings.edit()
                                .putString("last_device_address", address)
                                .putString("printer", "bluetooth")
                                .apply();

                        // Connect to the device using Topitup's Bluetooth service
                        if (Topitup.mService != null) {
                            Topitup.mService.connect(device);
                            Toast.makeText(this, "Connecting to printer...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Bluetooth service not initialized", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid Bluetooth address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No Bluetooth device selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2: // REQUEST_ENABLE_BT
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now enabled
                    Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();

                    // Initialize Bluetooth service if needed
                    if (Topitup.mService == null) {
                        Topitup.mService = new com.za.toptitup.loginlibrary.bluetooth.BluetoothService(
                                this,
                                Topitup.mBluetoothHandler
                        );
                    }

                    // Now show device list
                    Intent serverIntent = new Intent(this, com.za.toptitup.loginlibrary.bluetooth.DeviceListActivity.class);
                    startActivityForResult(serverIntent, 3); // REQUEST_CONNECT_DEVICE
                } else {
                    // User did not enable Bluetooth
                    Toast.makeText(this, "Bluetooth is required for printing", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void handleLogout() {

        if (Topitup.isBluetoothConnected) {
            PrinterTopitup.print_data("2welcome to Retailer app\n\n\n\n");
            Toast.makeText(this, "Printing...", Toast.LENGTH_SHORT).show();
        } else {
            Topitup.connectBluetooth(MainWebViewActivity.this);
        }
        // Clear WebView data
        /*webView.clearCache(true);
        webView.clearHistory();

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Go back to login (library)
        Intent intent = new Intent(
                this,
                activity_login.class
        );
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();*/
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            handleLogout();
            super.onBackPressed();
        }
    }
    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.BLUETOOTH_CONNECT,
                                android.Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH_PERMISSIONS
                );
            }
        }
    }

}
