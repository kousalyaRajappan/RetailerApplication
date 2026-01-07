package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class supplierCashmx extends RealmObject {

    @PrimaryKey
    public int supplier_id;

    public String short_name;
    public String legal_name;
    public String logo_name;
    public Integer sts;
    public int itemcount;
    public String cashms_acc_number;
    public String dc_code;
    @Ignore
    public String supplier_data;

    @Override
    public String toString()
    {

        return "[supplier_id = "+supplier_id+"]";

    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    public String getLogo_name() {
        return logo_name;
    }

    public void setLogo_name(String logo_name) {
        this.logo_name = logo_name;
    }

    public Integer getSts() {
        return sts;
    }

    public void setSts(Integer sts) {
        this.sts = sts;
    }

    public int getItemcount() {
        return itemcount;
    }

    public void setItemcount(int itemcount) {
        this.itemcount = itemcount;
    }
}
