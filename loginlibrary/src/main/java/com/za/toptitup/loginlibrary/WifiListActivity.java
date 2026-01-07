package com.za.toptitup.loginlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WifiListActivity extends AppCompatActivity implements WifiListener {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    public ScanResult mWifi;
    RecyclerView recyclerView;
    WifiListener wifiListener;
    private WifiManager wifiManager;
    private WifiAdapter wifiAdapter;
    private List<ScanResult> wifiList = new ArrayList<>();
    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        wifiListener = this;
        recyclerView = findViewById(R.id.wifirecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... enabling it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        requestLocationPermission();

    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            } else {
                scanWifiNetworks();
            }
        } else {
            scanWifiNetworks();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifiNetworks();
            } else {
                Toast.makeText(this, "Permission required to scan Wi-Fi networks", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanWifiNetworks() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean success = wifiManager.startScan();
        if (!success) {
            scanFailure();
        }
    }

    @SuppressLint("MissingPermission")
    private void scanSuccess() {
        wifiList = wifiManager.getScanResults();
        if (wifiList != null) {
            wifiAdapter = new WifiAdapter(wifiList, wifiListener);
            recyclerView.setAdapter(wifiAdapter);
        }
        // wifiAdapter.updateWifiList(wifiList);
    }

    @SuppressLint("MissingPermission")
    private void scanFailure() {
        Toast.makeText(getApplicationContext(), "scan wifi error", Toast.LENGTH_LONG).show();
        wifiList = wifiManager.getScanResults();
        if (wifiList != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            wifiAdapter = new WifiAdapter(wifiList, wifiListener);
            recyclerView.setAdapter(wifiAdapter);
        }
        // wifiAdapter.updateWifiList(wifiList);
    }

    private void connectToWifi(ScanResult wifi, String password) {
        String networkSSID = wifi.SSID;
        String networkPass = password;
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPass);

        wifiManager.addNetwork(conf);

        // Enable the network
        @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(String.format("\"%s\"", networkSSID))) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                Toast.makeText(this, "Connecting to Wi-Fi...", Toast.LENGTH_SHORT).show();

                // Wait a bit for the connection to establish
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (isWifiConnected(wifi.SSID)) {
                                    Toast.makeText(WifiListActivity.this, "Connected to " + wifi.SSID, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(WifiListActivity.this, "Failed to connect to " + wifi.SSID, Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        5000
                );
            } else {
                Toast.makeText(this, "Failed to add network configuration", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }


    private boolean isWifiConnected(String ssid) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo != null && networkInfo.isConnected()) {
            String currentSSID = wifiManager.getConnectionInfo().getSSID();
            return currentSSID.equals("\"" + ssid + "\"");
        }
        return false;
    }

    @Override
    public void onWifiCallback(ScanResult wifi) {
        mWifi = wifi;
        showDialog();
        // connectToWifi(wifi);
    }

    public void showDialog() {
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Enter Password")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Find the EditText in the custom layout
                        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

                        // Get the password entered by the user
                        String password = editTextPassword.getText().toString();
                        connectToWifi(mWifi, password);
                        // Handle the password (e.g., authenticate the user)
                        Toast.makeText(getApplicationContext(), "Password: " + password, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


