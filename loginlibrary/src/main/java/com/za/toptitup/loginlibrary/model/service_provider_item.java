package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class service_provider_item extends RealmObject {

    @PrimaryKey
    public int service_provider_item_id;
    public int service_provider_id;

    public int item_type = 0;
    public int item_position = 0;

    public String item_desc = "";
    public String item_btn_desc = "";
    public String item_print_desc = "";
    public String item_quickprint = "";
    public String item_value = "";
    public String item_barcode = "";
    public String social_type="";
    public String period_label="";
    public double item_value_int = 0;
    public boolean visible_cashier = true;
    public boolean visible_admin = true;
    public boolean item_show_value = false;

    public boolean isVisible_admin() {
        return visible_admin;
    }

    public void setVisible_admin(boolean visible_admin) {
        this.visible_admin = visible_admin;
    }


}

