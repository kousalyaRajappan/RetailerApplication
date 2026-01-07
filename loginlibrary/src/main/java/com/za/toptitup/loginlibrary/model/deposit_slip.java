package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;

public class deposit_slip extends RealmObject {



    public  String date;
    public  String balance_cash ;


    public String err = "";
    @Override
    public String toString()
    {

        return " [date = "+date+", err = "+err+"]";

    }



}
