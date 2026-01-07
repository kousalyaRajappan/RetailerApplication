package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GetStatusFull extends RealmObject {

    @PrimaryKey
    public String customer_id;
    public String enable_vas;
    public String enable_rpt_monthly_deposit;
    public String enable_rpt_comm_statement;
    public String noticeid;
    public String license_id;

    public String enable_rpt_monthly_sales;
    public String payat_account_number;
    public String payat_account_number_cash;
    public String enable_realtime_swipe;
    @Override
    public String toString()
    {

        return "[customer_id = "+customer_id+", enable_vas = "+enable_vas+",license_id = "+license_id+", noticeid = "+noticeid+", enable_rpt_monthly_deposit = "+enable_rpt_monthly_deposit+", enable_rpt_comm_statement = "+enable_rpt_comm_statement+", enable_rpt_monthly_sales = "+enable_rpt_monthly_sales+", payat_account_number = "+payat_account_number+", payat_account_number_cash = "+payat_account_number_cash+", enable_realtime_swipe = "+enable_realtime_swipe+"]";

    }


}
