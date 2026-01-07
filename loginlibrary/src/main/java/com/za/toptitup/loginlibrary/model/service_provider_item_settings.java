package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class service_provider_item_settings extends RealmObject {

    @PrimaryKey
    public int service_provider_item_id;
    public int service_provider_id;

    public String item_desc = "";
    public int item_position = 0;
    public boolean visible_cashier = true;
    public boolean visible_admin = true;
    public int enable=1;

    public int getService_provider_item_id() {
        return service_provider_item_id;
    }

    public void setService_provider_item_id(int service_provider_item_id) {
        this.service_provider_item_id = service_provider_item_id;
    }

    public int getService_provider_id() {
        return service_provider_id;
    }

    public void setService_provider_id(int service_provider_id) {
        this.service_provider_id = service_provider_id;
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

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public void setItem_desc(String item_desc) {
        this.item_desc = item_desc;
    }

    public int getItem_position() {
        return item_position;
    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }
}

