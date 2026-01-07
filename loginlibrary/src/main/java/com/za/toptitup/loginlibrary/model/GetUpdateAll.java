package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class GetUpdateAll extends RealmObject {

    @PrimaryKey
    public String customer_id;

    public String company_name;
    public String account_number;
    public String address;
    public String customer_accept;
    public String enable_unipin;
    public String enable_mamamoney;
    public String enable_cashms;
    public String enable_easyairtime;
    public String sp_ver;
    public String products;
    public String spi_ver;
    public String published_data;
    public String enable_vas;
    public String enable_international;

    public String enable_airtime;

    public String enable_rpt_monthly_deposit;
    public String enable_rpt_comm_statement;

    public String enable_ott;
    public String enable_flexipin;
    public String enable_bluvoucher;
    public String enable_ringas;
    public String enable_oneforu;

    public String st_status;
    public String noticeid;
    @Ignore

    public String service_provider_data;

    @Override
    public String toString()
    {

        return "[customer_id = "+customer_id+", account_number = "+account_number+", address = "+address+", company_name = "+company_name+", customer_accept = "+customer_accept+", enable_mamamoney = "+enable_cashms+", enable_cashms = "+enable_mamamoney+", sp_ver = "+sp_ver+", products = "+products+", spi_ver = "+spi_ver+", published_data = "+published_data+", enable_vas = "+enable_vas+", noticeid = "+noticeid+", enable_airtime = "+enable_airtime+", enable_rpt_monthly_deposit = "+enable_rpt_monthly_deposit+", enable_rpt_comm_statement = "+enable_rpt_comm_statement+", enable_international = "+enable_international+", enable_bluvoucher = "+enable_bluvoucher+", enable_ringas = "+enable_ringas+", enable_ott = "+enable_ott+", enable_oneforu = "+enable_oneforu+", st_status = "+st_status+"]";

    }


}
