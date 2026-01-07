package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class service_provider_settings extends RealmObject {

    @PrimaryKey
    public int provider_id;

    public String provider_desc;

    public boolean visible_cashier=true;
    public boolean visible_admin=true;
    public int enable=1;

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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public String getProvider_desc() {
        return provider_desc;
    }

    public void setProvider_desc(String provider_desc) {
        this.provider_desc = provider_desc;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
