package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class serviceProviderQuick extends RealmObject {

    @PrimaryKey
    public int spqid;
    public  String spName;
    public  String spDena;
    public String spBarcode="";
    public int spItemID;
    public int spPos;
    public boolean visible_admin;
    public boolean visible_cashier;
}
