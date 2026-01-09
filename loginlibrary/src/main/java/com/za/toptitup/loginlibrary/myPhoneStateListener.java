package com.za.toptitup.loginlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class myPhoneStateListener extends PhoneStateListener {

    public int signalSupport = 0;
    Context con;
    private String imeiSIM1;
    private String imeiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public myPhoneStateListener(Context ctx) {

        super();
        this.con = ctx;
    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//todo request permission
            }
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        TelephonyManager telephonyManager = ((TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE));


        imeiSIM1 = getDeviceId(con);
        imeiSIM2 = null;
        try {
            imeiSIM1 = getDeviceIdBySlot(con, "getDeviceId", 0);
            imeiSIM2 = getDeviceIdBySlot(con, "getDeviceId", 1);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
            try {
                imeiSIM1 = getDeviceIdBySlot(con, "getDeviceId", 0);
                imeiSIM2 = getDeviceIdBySlot(con, "getDeviceId", 1);
            } catch (GeminiMethodNotFoundException e1) {
                //Call here for next manufacturer's predicted method name if you wish
                e1.printStackTrace();
            }
        }
        isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        isSIM2Ready = false;
        try {
            isSIM1Ready = getSIMStateBySlot(con, "getSimState", 0);
            isSIM2Ready = getSIMStateBySlot(con, "getSimState", 1);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
            try {
                isSIM1Ready = getSIMStateBySlot(con, "getSimState", 0);
                isSIM2Ready = getSIMStateBySlot(con, "getSimState", 1);
            } catch (GeminiMethodNotFoundException e1) {
                //Call here for next manufacturer's predicted method name if you wish
                e1.printStackTrace();
            }
        }


        Log.e("dual sim",isSIM2Ready+"..dual...."+isSIM1Ready );


//        ArrayList cellInfos = (ArrayList) telephonyManager.getAllCellInfo();
        SubscriptionManager subs = (SubscriptionManager) con.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        try {
            List<CellInfo> allCellinfo = telephonyManager.getAllCellInfo();
            List<SubscriptionInfo> activeSubscriptionInfoList = subs.getActiveSubscriptionInfoList();
            if(activeSubscriptionInfoList != null) {
                List<CellInfo> regCellInfo = getRegisteredCellInfo(allCellinfo);

                Log.e("subscriptionInfo", "...signal......" + regCellInfo.size());


                if(regCellInfo.size() ==1){
                    int strength111 = 0;
                    CellInfo info1 = regCellInfo.get(0);
                    if (info1 instanceof CellInfoLte) {

                        strength111 = ((CellInfoLte) info1).getCellSignalStrength().getDbm();
                    } else if (info1 instanceof CellInfoGsm) {
                        strength111 = ((CellInfoGsm) info1).getCellSignalStrength().getDbm();
                    } else if (info1 instanceof CellInfoCdma) {
                        strength111 = ((CellInfoCdma) info1).getCellSignalStrength().getDbm();
                    } else if (info1 instanceof CellInfoWcdma) {
                        strength111 = ((CellInfoWcdma) info1).getCellSignalStrength().getDbm();
                    } else {
                        strength111 = 0;
                    }
                    Timber.i("subs " + strength111);

                    String strength =  String.valueOf(strength111).substring(1);

                    Timber.i("sim1   " +strength111 + "  " + Integer.parseInt(strength));
                    addSignalSim1(Integer.parseInt(strength));
                    addSignalSim2(0);

                }else{
                    int strength1 = 0;
                    if (isSIM1Ready) {

                        CellInfo info1 = regCellInfo.get(0);
                        if (info1 instanceof CellInfoLte) {

                            strength1 = ((CellInfoLte) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoGsm) {
                            strength1 = ((CellInfoGsm) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoCdma) {
                            strength1 = ((CellInfoCdma) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoWcdma) {
                            strength1 = ((CellInfoWcdma) info1).getCellSignalStrength().getDbm();
                        } else {
                            strength1 = 0;
                        }
                        Timber.i("subs " + strength1);

                        String strength =  String.valueOf(strength1).substring(1);

                        Timber.i("sim1   " +strength + "  " + Integer.parseInt(strength));
                        addSignalSim1(Integer.parseInt(strength));

                    } else {
                        addSignalSim1(0);

                    }

                    if (isSIM2Ready) {
                        CellInfo info2 = regCellInfo.get(1);
                        int strength2;
                        if (info2 instanceof CellInfoLte) {
                            strength2 = ((CellInfoLte) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoGsm) {
                            strength2 = ((CellInfoGsm) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoCdma) {
                            strength2 = ((CellInfoCdma) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoWcdma) {
                            strength2 = ((CellInfoWcdma) info2).getCellSignalStrength().getDbm();
                        } else {
                            strength2 = 0;
                        }
                        Timber.i("subs " + subs);

                        String strength11 =  String.valueOf(strength2).substring(1);

                        Timber.i("sim2   " +strength11 + "  " + Integer.parseInt(strength11));
                        addSignalSim2(Integer.parseInt(strength11));
//                    addSignalSim2(strength2);

                    } else {
                        addSignalSim2(0);

                    }

                }



/*
                if(activeSubscriptionInfoList.size()>=2) {
                    if (regCellInfo.size() >= 2) {
                        */
/*CellInfo info1 = regCellInfo.get(0);
                        int strength1;
                        if (info1 instanceof CellInfoLte) {
                            strength1 = ((CellInfoLte) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoGsm) {
                            strength1 = ((CellInfoGsm) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoCdma) {
                            strength1 = ((CellInfoCdma) info1).getCellSignalStrength().getDbm();
                        } else if (info1 instanceof CellInfoWcdma) {
                            strength1 = ((CellInfoWcdma) info1).getCellSignalStrength().getDbm();
                        } else {
                            strength1 = 0;
                        }*//*

                 */
/* Timber.i("subs " + subs);
                        Timber.i("sim1   " + strength1 +  "  " + strength1);*//*




                 */
/*CellInfo info2 = regCellInfo.get(1);
                        int strength2;
                        if (info2 instanceof CellInfoLte) {
                            strength2 = ((CellInfoLte) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoGsm) {
                            strength2 = ((CellInfoGsm) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoCdma) {
                            strength2 = ((CellInfoCdma) info2).getCellSignalStrength().getDbm();
                        } else if (info2 instanceof CellInfoWcdma) {
                            strength2 = ((CellInfoWcdma) info2).getCellSignalStrength().getDbm();
                        } else {
                            strength2 = 0;
                        }
                        Timber.i("subs " + subs);
                        Timber.i("sim2   " + strength2 +  "  " + strength2);*//*


                    }

                }
*/
            }else {
                addSignalSim1(0);
                addSignalSim2(0);

            }


        }
        catch (Exception e){
            e.printStackTrace();

        }




    }



        public static ArrayList<CellInfo> getRegisteredCellInfo(List<CellInfo> cellInfos) {
            ArrayList<CellInfo> registeredCellInfos = new ArrayList<>();
            if (!cellInfos.isEmpty()) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i).isRegistered()) {
                        registeredCellInfos.add(cellInfos.get(i));
                    }
                }
            }
            return registeredCellInfos;
        }


    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{

            Class telephonyClass = Class.forName(telephony.getClass().getName());
            Class[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if(ob_phone != null){
                imei = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return imei;
    }
    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        boolean isReady = false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            Class telephonyClass = Class.forName(telephony.getClass().getName());
            Class[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return isReady;
    }
    private static class GeminiMethodNotFoundException extends Exception {
        private static final long serialVersionUID = -996812356902545308L;
        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }


    public void addSignalSim1(int signalStrength){

        Log.e("signal strength","...........strength...."+signalStrength);
        if (signalStrength == 0) {
            if(activity_login.fromScreen.equals("activity_main") ){
                activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal1));

            }else  if(activity_login.fromScreen.equals("activity_login") ){
                activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal1));

            }
        }else{
            if (signalStrength > 90) {
                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal4));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal4));

                }

            } else if (signalStrength > 70 && signalStrength < 90) {
                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));

                }


            }
            else if (signalStrength < 70 && signalStrength > 50) {
                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal2));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal2));

                }


            }
            else if (signalStrength < 50) {
                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal1));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal1));

                }

            }
        }

    }
    public void addSignalSim2(int signalStrength){

        Log.e("signal strength","...........strength...."+signalStrength);
        if (signalStrength == 0) {
            if(activity_login.fromScreen.equals("activity_main") ){
                activity_main.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal1));

            }else  if(activity_login.fromScreen.equals("activity_login") ){
                activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal1));

            }
        }else{
            Log.e("signal strength","...........else...."+signalStrength);

            if (signalStrength > 90) {
                Log.e("signal strength",".......2....else. 90..."+signalStrength);

                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal4));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal4));

                }

            }
            else if (signalStrength > 70 && signalStrength < 90) {
                Log.e("signal strength","........2...else. 70..."+signalStrength);

                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network.setImageDrawable(con.getDrawable(R.drawable.signal3));

                }


            }
            else if (signalStrength < 70 && signalStrength > 50) {
                Log.e("signal strength",".....2......else. 50..."+signalStrength);

                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal2));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal2));

                }


            }
            else if (signalStrength < 50) {
                Log.e("signal strength","......2.....else. 40..."+signalStrength);

                if(activity_login.fromScreen.equals("activity_main") ){
                    activity_main.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal1));

                }else  if(activity_login.fromScreen.equals("activity_login") ){
                    activity_login.img_network2.setImageDrawable(con.getDrawable(R.drawable.signal1));

                }
            }
        }

    }

}