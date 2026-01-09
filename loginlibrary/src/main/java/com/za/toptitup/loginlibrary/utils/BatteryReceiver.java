package com.za.toptitup.loginlibrary.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import android.util.Log;

import za.co.topitup.R;

import com.za.toptitup.loginlibrary.activity_login;
import com.za.toptitup.loginlibrary.activity_main;

public class BatteryReceiver extends BroadcastReceiver {
    Context con;



    @Override
    public void onReceive(Context context, Intent intent) {
        con = context;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        Log.e("battery ", "receiver........" + activity_login.fromScreen);
        if (isCharging) {
            if (level > 15) {
                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_login")) {
                   activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_spi_test")) {
                    activity_spi_test.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));


                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));


                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.charging_yellow));
                }


            } else {

                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));
                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));


                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));


                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));


                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));


                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));

                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));


                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_charging));
                }

            }

        }
        else {
            if (level > 75) {
                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));

                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));


                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_full));
                }


            } else if (level > 40 && level < 75) {
                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));

                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));


                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_75));
                }


            } else if (level > 15 && level < 40) {
                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));

                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));


                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_50));
                }


            } else if (level < 15) {
                if (activity_login.fromScreen.equals("activity_main")) {
                    activity_main.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_login")) {
                    activity_login.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_addpay_bills")) {
                    activity_addpay_bills.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));


                } else if (activity_login.fromScreen.equals("activity_reports")) {
                    activity_reports.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));


                } else if (activity_login.fromScreen.equals("activity_adpay")) {
                    activity_adpay.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));


                } else if (activity_login.fromScreen.equals("activity_adpay_new")) {
                    activity_adpay_new.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));


                } else if (activity_login.fromScreen.equals("activity_bill_payment")) {
                    activity_bill_payment.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_cash_management")) {
                    activity_cash_management.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_cashmx")) {
                    activity_cashmx.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_ding")) {
                    activity_ding.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_elec")) {
                    activity_elec.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_spi")) {
                    activity_spi.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_main_old")) {
                    activity_main_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_product_settings")) {
                    activity_product_settings.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_reprint")) {
                    activity_reprint.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("activity_spi_old")) {
                    activity_spi_old.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("retailer_payments")) {
                    RetailerPayments.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));

                } else if (activity_login.fromScreen.equals("transfer_bank")) {
                    activity_banktransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));
                } else if (activity_login.fromScreen.equals("wallet_transfer")) {
                    activity_wallettransfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));
                } else if (activity_login.fromScreen.equals("activity_transfer")) {
                    activity_transfer.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));
                } else if (activity_login.fromScreen.equals("activity_invoice")) {
                    activity_invoice.txt_battery.setImageDrawable(con.getDrawable(R.drawable.battery_empty));
                }
            }
        }

    }
}
