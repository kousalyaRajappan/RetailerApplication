package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageService extends RealmObject {

    @PrimaryKey
    public String notice_id;
    public String notice_data;
    public String notice_date;
    @Override
    public String toString()
    {

        return "[notice_id = "+notice_id+", notice_data = "+notice_data+", notice_date = "+notice_date+"]";

    }


}
