package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class service_provider extends RealmObject {

    @PrimaryKey
    public int provider_id;

    public String provider_desc;
    public String provider_message;
    public String provider_print;
    public boolean visible_cashier=true;
    public boolean visible_admin=true;
    public int itemcount;


    @Override
    public String toString()
    {
        return "[provider_id = "+provider_id+"]";
    }

    public boolean isVisible_cashier() {
        return visible_cashier;
    }

    public void setVisible_cashier(boolean visible_cashier) {
        this.visible_cashier = visible_cashier;
    }

    public boolean isVisible_admin() {
        return visible_admin;
    }

    public void setVisible_admin(boolean visible_admin) {
        this.visible_admin = visible_admin;
    }
}
