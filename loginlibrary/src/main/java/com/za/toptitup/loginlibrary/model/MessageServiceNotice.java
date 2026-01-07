package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageServiceNotice extends RealmObject {

    @PrimaryKey
    public String notice_id;
    public String notice_data;
    public String notice_date;
    public String notice_status;

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_data() {
        return notice_data;
    }

    public void setNotice_data(String notice_data) {
        this.notice_data = notice_data;
    }

    public String getNotice_date() {
        return notice_date;
    }

    public void setNotice_date(String notice_date) {
        this.notice_date = notice_date;
    }

    public String getNotice_status() {
        return notice_status;
    }

    public void setNotice_status(String notice_status) {
        this.notice_status = notice_status;
    }

    @Override
    public String toString()
    {

        return "[notice_id = "+notice_id+", notice_data = "+notice_data+", notice_date = "+notice_date+", notice_date = "+notice_status+"]";

    }


}
