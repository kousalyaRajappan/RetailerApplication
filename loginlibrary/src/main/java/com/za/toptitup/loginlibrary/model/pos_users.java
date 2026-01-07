package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class pos_users extends RealmObject {

    @PrimaryKey
    public int posuser_id;

    public String posuser_firstname;
    public String posuser_surname;
    public Integer posuser_isadmin;
    public Integer posuser_status;
    public String posuser_pin;
    public String posuser_pin_swipe;
    public String posuser_create_dtm;
    public String posuser_edit_dtm;
    public String rica_training;
    public String rica_registered;
    public String posuser_idnumber_type;
    public String posuser_idnumber;

    @Override
    public String toString()
    {
        return "[posuser_id = "+posuser_id+"]";

    }

}
