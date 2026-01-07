package com.za.toptitup.loginlibrary.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;
public class UsbBroadcastReceiver extends BroadcastReceiver {
    SharedPreferences preferences ;
    private UsbManager usbManager;
    private PendingIntent permissionIntent;
    private UsbDeviceConnection connection;

    private static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            preferences = context.getSharedPreferences("TIUPREF", 0);

            if (device != null) {
                int targetVendorId = preferences.getInt("TARGET_VENDOR_ID", -1);
                int targetProductId = preferences.getInt("TARGET_PRODUCT_ID", -1);
                if(targetVendorId!= -1 && targetProductId!= -1){
                    // Get Vendor ID and Product ID
                    int vendorId = device.getVendorId();
                    int productId = device.getProductId();

                    // Save to SharedPreferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("TARGET_VENDOR_ID", vendorId);
                    editor.putInt("TARGET_PRODUCT_ID", productId);
                    editor.apply();

                    // Request permission for the device
                    requestUsbPermission(device);
                    // Optional: Show confirmation
                    Toast.makeText(context, "Device info saved: Vendor ID = " + vendorId + ", Product ID = " + productId, Toast.LENGTH_SHORT).show();
                }else{
                    autoConnectToSavedDevice();
                }

            }
        }
    }

    private void requestUsbPermission(UsbDevice device,Context conn) {
        UsbManager usbManager = (UsbManager) conn.getSystemService(Context.USB_SERVICE);
        PendingIntent permissionIntent = PendingIntent.getBroadcast(conn, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // Check if we have permission for the device
        if (!usbManager.hasPermission(device)) {
            // Request permission if not already granted
            usbManager.requestPermission(device, permissionIntent);
        } else {
            // Permission already granted, proceed with communication
//            onUsbDeviceReady(device);
        }
    }
    public void requestUsbPermission(UsbDevice device) {
        if (!usbManager.hasPermission(device)) {
            usbManager.requestPermission(device, permissionIntent);
        } else {
            saveDeviceDetails(device);

            // If permission is already granted, open the connection immediately
//            onUsbPermissionGranted(device);
        }
    }
    private void connectToDevice(Context context, UsbDevice device) {
        // Add code here to initialize communication with the USB printer
    }

    private void autoConnectToSavedDevice() {
        int savedVendorId = preferences.getInt("TARGET_VENDOR_ID", -1);
        int savedProductId = preferences.getInt("TARGET_PRODUCT_ID", -1);

        if (savedVendorId != -1 && savedProductId != -1) {
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                if (device.getVendorId() == savedVendorId && device.getProductId() == savedProductId) {
                    if (!usbManager.hasPermission(device)) {
                        usbManager.requestPermission(device, permissionIntent);
                    } else {
                        connection = usbManager.openDevice(device);
                    }
                    break;
                }
            }
        }
    }
    private void saveDeviceDetails(UsbDevice device) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("TARGET_VENDOR_ID", device.getVendorId());
        editor.putInt("TARGET_PRODUCT_ID", device.getProductId());
        editor.apply();
    }
}
