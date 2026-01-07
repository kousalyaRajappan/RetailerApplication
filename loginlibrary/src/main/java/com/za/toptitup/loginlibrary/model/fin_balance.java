package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class fin_balance extends RealmObject {

    @PrimaryKey
    private  int fin_balance_id = 0;

    public Boolean is_balance_available = false;

    public  String balance = "0.00";
    public  String balance_cash = "0.00";
    public  String commission = "0.00";
    public  String swipe = "0.00";
    public  String loyalty = "0.00";




    public  String available_balance = "0.00";
    public  String credit_limit = "0.00";
    public  String credit_extended = "0.00";
    public  String low_balance = "0";


    public  String cash_customer = "0";

    public  String dtmd = "01/01/2015";
    public  String dtmt = "12:00:00";

    public  String w1_desc = "";
    public  String w2_desc = "";

    public  String acn1 = "";
    public  String acn2 = "";

    public  String enable_remote_ext_credit = "0";

    public  String allow_transfer_cash = "0";
    public  String allow_transfer_interstore = "0";

    public String err = "";
    public String new_spi_ver;

    public  String real_time_balance = "0.00";
    @Override
    public String toString()
    {

        return " [balance = "+balance+", available_balance = "+available_balance+",balance_cash = "+balance_cash+",commission = "+commission+",swipe = "+swipe+",loyalty = "+loyalty+", err = "+err+", credit_limit = "+credit_limit+", cash_customer = "+cash_customer+", dtmd = "+dtmd+", dtmt = "+dtmt+", real_time_balance = "+real_time_balance+", new_spi_ver = "+new_spi_ver+"]";

    }
}
