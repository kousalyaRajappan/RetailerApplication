package com.za.toptitup.loginlibrary;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class PrinterStatusChecker {
    private static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";
    private UsbManager usbManager;
    private Context context;
    private UsbDevice printerDevice;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpointIn;
    private UsbEndpoint endpointOut;

    public PrinterStatusChecker(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public boolean checkPrinterStatus(int vendorId,int prouctId) {

        findUsbPrinter(vendorId,prouctId);

        if (printerDevice == null) {
            Log.e("USB", "No printer found");
            return false;
        }

        // Request permission
        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                context, 0, new android.content.Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        );
        usbManager.requestPermission(printerDevice, permissionIntent);

        if (usbManager.hasPermission(printerDevice)) {
            openConnection();
            sendStatusCommand();
            return readPrinterStatus();  // Return printer status

        } else {
            Log.e("USB", "Permission denied for USB printer");
            return false;

        }
    }

    private void findUsbPrinter(int vendorId,int productId) {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            Log.d("USB", "Found device: " + device.getVendorId());

            // Replace with your printer's vendor and product ID
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                printerDevice = device;
                break;
            }
        }
    }

    private void openConnection() {
        UsbInterface usbInterface = printerDevice.getInterface(0);
        connection = usbManager.openDevice(printerDevice);
        connection.claimInterface(usbInterface, true);

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                endpointOut = endpoint;  // For sending data
            } else if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                endpointIn = endpoint;  // For receiving status
            }
        }
    }

    private void sendStatusCommand() {
        // DLE EOT 1 command (Check printer status)
        byte[] command = new byte[]{0x10, 0x04, 0x01};
        connection.bulkTransfer(endpointOut, command, command.length, 1000);
        Log.d("USB", "Status command sent");
    }

   private boolean readPrinterStatus() {
       byte[] buffer = new byte[1];  // Printer status response is 1 byte
       int result = connection.bulkTransfer(endpointIn, buffer, buffer.length, 1000);

       if (result > 0) {
           int statusByte = buffer[0] & 0xFF;  // Convert to unsigned int
           Log.d("USB", "Printer Status Byte: " + statusByte);
           return interpretStatus(statusByte);
       } else {
           Log.e("USB", "Failed to read status or no response from printer");
           return false;  // Consider printer offline if no response
       }
   }
    private boolean interpretStatus(int status) {


        if ((status & 0x08) == 0x08) {
            Log.e("USB", "Printer is OFFLINE");
            return false;
        } else if ((status & 0x40) == 0x40) {
            Log.e("USB", "ERROR: Paper out or other issue");
            return false;
        }else {
            Log.d("USB", "Printer is ONLINE");
            return true;
        }


    }


}
