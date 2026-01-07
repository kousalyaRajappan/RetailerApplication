package com.za.toptitup.loginlibrary.model;

public class voucher_response {

    public String uid;
    public String dtmt;
    public String sp;
    public String n;
    public String balance;
    public String dtmd;
    public String low_balance;
    public String dtm;
    public String s;
    public String credit_limit;
    public String p;
    public String spi;
    public String balance_cash;
    public String credit_extended;
    public String available_balance;
    public String err = "";
    public String print_data = "";
    public  String commission ;
    public  String swipe ;
    public  String loyalty;




    @Override
    public String toString()
    {
        return "ClassPojo [uid = "+uid+", dtmt = "+dtmt+", sp = "+sp+", n = "+n+", balance = "+balance+", dtmd = "+dtmd+", low_balance = "+low_balance+", dtm = "+dtm+", s = "+s+", credit_limit = "+credit_limit+",commission = "+commission+",swipe = "+swipe+",loyalty = "+loyalty+", p = "+p+", spi = "+spi+", balance_cash = "+balance_cash+", credit_extended = "+credit_extended+", available_balance = "+available_balance+"]";
    }
}
