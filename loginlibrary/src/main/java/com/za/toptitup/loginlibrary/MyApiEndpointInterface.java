package com.za.toptitup.loginlibrary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.za.toptitup.loginlibrary.model.GetStatusFull;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MessageService;
import com.za.toptitup.loginlibrary.model.MessageServiceNotice;
import com.za.toptitup.loginlibrary.model.credit_request;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_user_current;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.supplierCashmx;
import com.za.toptitup.loginlibrary.model.voucher_response;
import com.za.toptitup.loginlibrary.utils.Topitup;

public interface MyApiEndpointInterface {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Topitup.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(Topitup.client)
            .build();

    Retrofit retrofit_sync = new Retrofit.Builder()
            .baseUrl(Topitup.BASE_URL_SYNC)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(Topitup.client)
            .build();


    Retrofit retrofit1 = new Retrofit.Builder()
            .baseUrl("https://portal.nedsecure.co.za/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(Topitup.client)
            .build();


    @Headers({"Authorisation: Basic"})
    @GET("/api/merchant/authenticate")
    Call<ResponseBody> mpos_auth(
            @Header("usergroup") String usergroup,
            @Header("username") String username,
            @Header("timestamp") String timestamp,
            @Header("token") String token
    );

    @Headers({"Authorisation: Basic"})
    @GET("/api/transactions")
    Call<ResponseBody> mpos_config(
            @Header("usergroup") String usergroup,
            @Header("username") String username,
            @Header("timestamp") String timestamp,
            @Header("token") String token,
            @Query("applicationid") String mode
    );


    /**/


    @Headers({"ignore: 1"})
    @GET("voucherapi/login/login")
    Call<pos_user_current> login(
            @Header("license") String TIU_LICENSE,
            @Query("license_code") String license_code,
            @Query("pin") String login_pin
    );


    @FormUrlEncoded
    @POST("voucherapi/device_information/{technician_id}")
    Call<String> device_information(
            @Header("license") String TIU_LICENSE,
            @Field("app_ver") String app_ver,
            @Field("os_version") String os_version,
            @Field("os_api_level") String os_api_level,
            @Field("device_manufacturer") String device_manufacturer,
            @Field("device_name") String device_name,
            @Field("device_model") String device_model,
            @Field("device_product") String device_product
    );


    @Headers({"device: android"})
    @GET("/payaccountrt/payment/do-payment")
    Call<ResponseBody> bill_get_custinfo(
            //    @Path("payatapirt") String payatapirt,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("accno") String accno,
            @Query("amt") String amt,
            @Query("cc") String cc,
            @Query("p") String p,
            @Query("fee") String fee
    );


    @Headers({"device: android"})
    @GET("/payaccountrt/payment/do-payment")
    Call<voucher_response> bill_do_payment(
            //  @Path("payatapirt") String payatapirt,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("accno") String accno,
            @Query("amt") String amt,
            @Query("cc") String cc,
            @Query("p") String p,
            @Query("fee") String fee,
            @Query("linkData") String linkData
    );


    @Headers({"device: android"})
    @GET("itroncomm/electricity/custInfoReq")
    Call<ResponseBody> elec_get_custinfo(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("lic") String license,
            @Query("m") String meter_number
    );


    @Headers({"device: android"})
    @GET("itroncomm/electricity/vendReq")
    Call<ResponseBody> elec_process(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("lic") String license,
            @Query("v") String value,
            @Query("m") String meter_number,
            @Query("p") String pos_user_id,
            @Query("uid") String stats_ele_busy,
            @Query("last") String stats_ele_lastprinted
    );

    @Headers({"device: android"})
    @GET("itroncomm/electricity/vendConfirmBalance")
    Call<ResponseBody> elec_vend_confirm(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String value,
            @Query("lic") String license
    );


    @Headers({"device: android"})
    @GET("itroncomm/electricity/vendGetReprint")
    Call<ResponseBody> vendGetReprint(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("lic") String license,
            @Query("uid") String uid,
            @Query("s") int status,
            @Query("d") int useDateRange

    );


    @Headers({"device: android"})
    @GET("/itroncomm/electricity/vendReq")
    Call<ResponseBody> requestFreeToken(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("lic") String license,
            @Query("v") String value,
            @Query("m") String meter_number,
            @Query("p") String pos_user_id,
            @Query("uid") String stats_ele_busy,
            @Query("last") String stats_ele_lastprinted
    );


//payaccount

    @Headers({"device: android"})
    @GET("/{payatapirt}/payment/reprint")
    Call<voucher_response> billpayment_reprint(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String uid
    );


    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/x-intraday")
    Call<ResponseBody> cashup_x_intraday(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );


    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/x-intraday-cashier")
    Call<ResponseBody> cashup_x_intraday_detail(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );

    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/x-intraday-cashier?q=1")
    Call<ResponseBody> cashup_x_intraday_summary(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );


    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/cashup-single")
    Call<ResponseBody> cashup_y_intraday(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("a") String a);


    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/x-single")
    Call<ResponseBody> cashup_w_intraday(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-notice")
    Call<MessageService> get_notice(
            @Header("license") String TIU_LICENSE
            // @Header("posuser") String POSUSER_ID
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-notice-new")
    Call<MessageServiceNotice> get_notice_(
            @Header("license") String TIU_LICENSE
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-ads")
    Call<JsonObject> get_adds_login(
            @Header("license") String TIU_LICENSE
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-mesaage")
    Call<JsonObject> get_adds_(
            @Header("license") String TIU_LICENSE
    );

    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/z-end-of-day-cashier")
    Call<ResponseBody> z_end_of_day_cashier(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );

    @Headers({"device: android"})
    @GET("voucherapi/{cashup}/z-end-of-day")
    Call<ResponseBody> z_end_of_day(

            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
               @Path("cashup") String cashup
    );

    @Headers({"device: android"})
    @GET("voucherapi/reports/get-sales-by-month")
    Call<ResponseBody> monthly_sales_report(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymm
    );


    @Headers({"device: android"})
    @GET("voucherapi/reports/get-sales-by-day")
    Call<ResponseBody> daily_sales_report(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymmdd
    );


    @Headers({"device: android"})
    @GET("voucherapi/reports/get-monthly-deposit-statement")
    Call<ResponseBody> deposit_statement(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymm
    );


    @Headers({"device: android"})
    @GET("voucherapi/reports/get-monthly-commission-statement")
    Call<ResponseBody> commission_statement(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymm
    );

    // get-daily-commission-statement
    @Headers({"device: android"})
    @GET("voucherapi/reports/get-daily-commission-statement")
    Call<ResponseBody> daily_commission_statement(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymm
    );

    @Headers({"device: android"})
    @GET("/voucherapi/financial/addpay-print-monthly-detail")
    Call<ResponseBody> wappoint_summary_month(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String yyyymm
    );


    @Headers({"device: android"})
    @GET("/voucherapi/customer/get-invoice-list")
    Call<ResponseBody> get_invoices(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("sdtm") String sdate,
            @Query("edtm") String edate,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit
    );


    @Headers({"device: android"})
    @GET("/voucherapi/customer/get-invoice-list")
    Call<ResponseBody> get_invoices_(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("sdtm") String sdate,
            @Query("edtm") String edate,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit
    );

    @Headers({"device: android"})
    @GET("/voucherapi/customer/get-invoice")
    Call<ResponseBody> invoice_print(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("invoice_id") String invoice_id

    );


    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/customer/get-posuser-list")
    Call<List<pos_users>> get_posuser_list(
            @Header("license") String TIU_LICENSE,
            @Query("json") int json
    );


    @Headers({"ignore: 100", "device: android"})
    @GET("voucherapi/login/get-active-license/")
    Call<JsonObject> get_approve_licence(
//            @Header("license") String TIU_LICENSE,
            @Query("asset_serial") String asset_serial
    );

    @Headers({"ignore: 100", "device: android"})
    @GET("voucherapi/login/get-license-device-id/")
    Call<JsonObject> get_approve_by_deviceID(
            @Query("asset_id") String asset_id
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/update/get-update-all")
    Call<GetUpdateAll> get_update_all(
            @Header("license") String TIU_LICENSE,
            @Query("spi") String spi_version,
            @Query("sp") String sp_version,
            @Query("appver") String app_version
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-status-full")
    Call<GetUpdateAll> get_status_full(
            @Header("license") String TIU_LICENSE,
            @Query("api") String api_version,
            @Query("swv") String software_version,
            @Query("spi") String spi_version,
            @Query("sp") String sp_version
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/general/get-status-full")
    Call<GetStatusFull> get_status_full_flag(
            @Header("license") String TIU_LICENSE,
            @Query("api") String api_version,
            @Query("swv") String software_version,
            @Query("spi") String spi_version,
            @Query("sp") String sp_version
    );

    //cahmx
    @Headers({"device: android"})
    @GET("supplier/cashmx/get-payment-search")
    Call<ResponseBody> get_payment_search(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("sdate") String sdate,
            @Query("edate") String edate,
            @Query("txt") String param


    );


    @Headers({"device: android"})
    @GET("terminal/cashmx/account-payment-reprint")
    Call<ResponseBody> reprint_cashmx(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String uid
    );

    @Headers({"device: android"})
    @GET("/voucherapi/cashmx/get-customer-info")
    Call<ResponseBody> get_customer_info(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );

    @Headers({"device: android"})
    // @GET("/voucherapi/cashmx/account-payment")
    @GET("/penbev/cashmx/account-payment")
    Call<ResponseBody> cash_acct_payment(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("ptype") String ptype,
            @Query("storeno") String storeno,
            @Query("loadno") String loadno,
            @Query("driverno") String driverno,
            @Query("drivercell") String drivercell,
            @Query("amt") String amt,
            @Query("shipmentno") String shipmentno
    );

    @Headers({"device: android"})
    // @GET("/voucherapi/cashmx/account-payment-clover")
    @GET("/penbev/cashmx/account-payment-supplier")
    Call<ResponseBody> cash_acct_payment_supplier(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("ptype") String ptype,
            @Query("storeno") String storeno,
            @Query("loadno") String loadno,
            @Query("driverno") String driverno,
            @Query("drivercell") String drivercell,
            @Query("amt") String amt,
            @Query("shipmentno") String shipmentno
    );

    @Headers({"device: android"})
    @GET("/penbev/cashmx/get-all-supliers")
    Call<supplierCashmx> get_all_supliers(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID

    );


    @Headers({"device: android"})
    @GET("/voucherapi/customer/device-test-print")
    Call<ResponseBody> get_test_print(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID

    );

    @Headers({"device: android"})
    @GET("/voucherapi/customer/update-product-sts")
    Call<ResponseBody> setUn_selected_Items(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/voucher/get-last-voucher-info")
    Call<ResponseBody> get_last_voucher_info(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );


    @Headers({"device: android"})
    @GET("voucherapi/voucher/get-voucher-json")
    Call<voucher_response> get_voucher_json(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("p1") String p1,
            @Query("p2") String p2,
            @Query("voucher_cost") double voucher_cost,
            @Query("item_id") int item_id,
            @Query("last") String last
    );
    @Headers({"device: android"})
    @GET("bluapirt/voucher/get-voucher-json")
    Call<voucher_response> get_voucher_json_ott(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("p1") String p1,
            @Query("p2") String p2,
            @Query("voucher_cost") double voucher_cost,
            @Query("item_id") int item_id,
            @Query("last") String last
    );

    @Headers({"device: android"})
    @GET("voucherapi/voucher/get-voucher-json")
    Call<voucher_response> get_voucher_json_old(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("p1") String p1,
            @Query("p2") String p2,
            @Query("item_id") int item_id,
            @Query("last") String last
    );

    @Headers({"device: android"})
    @GET("bluapirt/voucher/get-voucher-json")
    Call<voucher_response> get_voucher_json_(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("p1") String p1,
            @Query("p2") String p2,
            @Query("item_id") int item_id,
            @Query("list_cost") double list_cost,
            @Query("last") String last
    );


    @Headers({"device: android"})
    @GET("voucherapi/voucher/get-voucher-json-flash")
    Call<voucher_response> get_voucher_json_flash(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("p1") String p1,
            @Query("p2") String p2,
            @Query("item_id") int item_id,
            @Query("last") String last,
            @Query("list_cost") double list_cost,
            @Query("stock_uid") String stock_uid


    );

    @Headers({"device: android"})
    @GET("voucherapi/flash/get-voucher")
    Call<ResponseBody> get_flash_voucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("voucher_cost") double voucher_cost,
            @Query("service_provider_item_id") int service_provider_item_id
    );

    @Headers({"device: android"})
    @GET("bluapirt/voucher/get-voucher")
    Call<ResponseBody> get_voucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("voucher_cost") int voucher_cost,
            @Query("service_provider_item_id") int service_provider_item_id,
            @Query("voucher_type") int voucher_type

    );

    //  {baseurl}/voucherapi/ott/get-voucher?voucher_cost=1000&service_provider_item_id=500
    @Headers({"device: android"})
    @GET("voucherapi/ott/get-voucher")
    Call<ResponseBody> get_OttVoucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("voucher_cost") double voucher_cost,
            @Query("service_provider_item_id") int service_provider_item_id

            );
    @Headers({"device: android"})
    @GET("voucherapi/flexi/get-voucher")
    Call<ResponseBody> get_Flexipin(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("voucher_cost") double voucher_cost,
            @Query("service_provider_item_id") int service_provider_item_id

    );

//    @QueryParam("p1") String requestUID,
//    @DefaultValue("0") @QueryParam("p2") int reqCount,
//    @DefaultValue("0") @QueryParam("p3") int printCount,
//    @QueryParam("item_id") int service_provider_item_id,
//    @DefaultValue("0") @QueryParam("last") String last_requestUID,
//    @DefaultValue("0") @QueryParam("json") int return_json

    @Headers({"ignore: 100", "device: android"})
    @GET("voucherapi/general/get-asset-upgrade")
    Call<ResponseBody> get_asset_upgrade(
            @Query("pin") String pin,
            @Query("asset_id") String asset_id
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("/voucherapi/financial/get-balance")
    Call<fin_balance> get_balance(
            @Header("license") String TIU_LICENSE,
            @Query("lvl") String lvl
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("/voucherapi/financial/get-balance")
    Call<fin_balance> get_balance_new(
            @Header("license") String TIU_LICENSE,
            @Query("lvl") String lvl
    );

    @Headers({"device: android"})
    @GET("/voucherapi/general/get-deposit-slip")
    Call<ResponseBody> get_deposit_slip(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("v") String v
    );

    @Headers({"device: android"})
    @GET("/dingrt/connect/get-vouchers")
    Call<ResponseBody> ding_voucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("phonenum") String phonenum
    );


    @Headers({"device: android"})
    @POST("/dingrt/connect/exchange-estimate-req")
    Call<ResponseBody> ding_exchange_estimate_req(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Body RequestBody body
    );


    @Headers({"device: android"})
    @POST("/dingrt/connect/vend-req")
    Call<ResponseBody> ding_vend_req(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Body RequestBody body
    );


/*
    @Headers({"device: android"})
    @GET("/voucherapi/customer/get-last-sales-filter")
    Call<ResponseBody> get_last_sales_filter(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String q,
            @Query("t") String t,
            @Query("f") String f
    );

    @Headers({"device: android"})
    @GET("voucherapi/voucher/get-voucher-reprint-json")
    Call<voucher_response> airtime_reprint(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String stock_uid
    );

     @Headers({"device: android"})
    @GET("voucherapi/voucher/reprint-voucher")
    Call<ResponseBody> reprint_voucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("t") int reprint_type,
            @Query("s") String param
    );

    */

    @Headers({"device: android"})
    @GET("/addpay/Api/get-last-sales-filter")
    Call<ResponseBody> get_last_sales_filter(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String q,
            @Query("t") String t,
            @Query("f") String f,
            @Query("devicemodel") String devicemodel,
            @Query("dt") String dt,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit
//            @Query("sp") String sp
            );

    @Headers({"device: android"})
    @GET("/addpay/Api/get-last-sales-filter")
    Call<ResponseBody> get_last_sales_filter_sp(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String q,
            @Query("t") String t,
            @Query("f") String f,
            @Query("devicemodel") String devicemodel,
            @Query("dt") String dt,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit,
            @Query("sp") String sp
    );

    @GET("addpay/Api/get-voucher-reprint-json")
    Call<voucher_response> airtime_reprint(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String stock_uid,
            @Query("devicemodel") String devicemodel
    );

    @Headers({"device: android"})
    @GET("addpay/Api/reprint-voucher")
    Call<ResponseBody> reprint_voucher(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("t") int reprint_type,
            @Query("s") String param
    );

    @Headers({"device: android"})
    @GET("addpay/Api/reprint-bill-pay")
    Call<ResponseBody> billpayment_reprint(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("uid") String stock_uid,
            @Query("devicemodel") String devicemodel
    );

    @Headers({"device: android"})
    @GET("addpay/Api/reprint-swipe")
    Call<ResponseBody> reprint_swipe(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("t") int reprint_type,
            @Query("pid") String param,
            @Query("devicemodel") String devicemodel
    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/wallet-transfer-interstore")
    Call<ResponseBody> wallet_transfer_interstore(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("acc") String acc,
            @Query("w") String w,
            @Query("amt") String amt,
            @Query("wdest") String wdest
    );

    @Headers({"device: android","ignore: 0"})
    @GET("/voucherapi/financial/store-transfer-portal")
    Call<ResponseBody> store_transfer(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("acc") String acc,
            @Query("w") String w,
            @Query("amt") String amt,
            @Query("wdest") String wdest
    );

    @Headers({"ignore: 1", "device: android"})

    @GET("/addpay/Api/get-passcode")
    Call<ResponseBody> call_for_retailer_pin(
            @Header("license") String TIU_LICENSE

    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/wallet-transfer-new")
    Call<ResponseBody> wallet_transfer(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("w") String w,
            @Query("amt") String amt
    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/wallet-transfer")
    Call<ResponseBody> wallet_transfer_new(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("from_wallet") String w,
            @Query("to_wallet") String to,
            @Query("amt") String amt
    );

    @Headers({"device: android"})
    @GET("voucherapi/customer/user-update-android")
    Call<ResponseBody> user_update_android(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("pid") Integer posuser_id,
            @Query("fn") String posuser_firstname,
            @Query("ln") String posuser_surname,
            @Query("p") String posuser_pin,
            @Query("a") Integer posuser_isadmin

    );


    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/customer/user-add")
    Call<ResponseBody> user_add(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("fn") String posuser_firstname,
            @Query("ln") String posuser_lastname,
            @Query("p") String posuser_pin,
            @Query("s") String posuser_pin_swipe,
            @Query("e") String posuser_status,
            @Query("a") String posuser_isadmin,
            @Query("idt") String idt,
            @Query("idn") String idn,
            @Query("rt") String rt
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/customer/delete-posuser")
    Call<ResponseBody> delete_posuser(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("pid") String delete_posuser_id
    );


    @Headers({"device: android"})
    @GET("/voucherapi/cashup/z-report-history")
    Call<ResponseBody> z_report_history(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("q") String q,
            @Query("sdt") String sdt,
            @Query("edt") String edt,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit
    );


    @Headers({"device: android"})
    @GET("/voucherapi/general/get-license-list")
    Call<ResponseBody> licence_list(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID

    );

    @Headers({"device: android"})
    @GET("/voucherapi/{cashup}/z-reprint")
    Call<ResponseBody> z_reprint(
            @Path("cashup") String cashup,
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("zid") String customer_report_z_id,
            @Query("license") String license_code,
            @Query("uid") String z_report_uid,
            @Query("c") String by_cashier
    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/credit-request-status")
    Call<credit_request> credit_request_status(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID
    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/credit-request")
    Call<credit_request> credit_request(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("amt") String amt
    );


    @Headers({"device: android"})
    @GET("/voucherapi/financial/credit-request-process")
    Call<credit_request> credit_request_process(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("amt") String amt
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/update/get-app-update-available")
    Call<ResponseBody> get_app_update_available(
            @Header("license") String TIU_LICENSE,
            @Query("appver") String app_version
    );


    @Headers({"device: android"})
    @GET("addpay/Api/get-fintx")
    Call<ResponseBody> get_fintx(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("alltx") String alltx,
            @Query("wallet") String wallet,
            @Query("sdt") String sdt,
            @Query("edt") String edt,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit

    );

    @Headers({"device: android"})
    @GET("addpay/Api/print-fintx")
    Call<ResponseBody> print_fintx(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("tx_id") String tx_id
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/get-random")
    Call<ResponseBody> get_swipe_real(
            @Header("license") String TIU_LICENSE,
            @Query("customer_id") String customer_id
    );

    @Headers({"device: android"})
    @GET("addpay/Api/swipe-monthly-sales")
    Call<ResponseBody> swipe_monthly_sales(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("customer_id") String customer_id,
            @Query("y") String year,
            @Query("m") String month,
            @Query("devicemodel") String device
    );

    @Headers({"device: android"})
    @GET("addpay/Api/swipe-monthly-sales-summary")
    Call<ResponseBody> swipe_monthly_sales_summary(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("customer_id") String customer_id,
            @Query("y") String year,
            @Query("m") String month,
            @Query("devicemodel") String device
    );


    @Headers({"device: android"})
    @GET("addpay/Api/swipe-daily-sales")
    Call<ResponseBody> swipe_daily_sales(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("customer_id") String customer_id,
            @Query("y") String year,
            @Query("m") String month,
            @Query("d") String day,
            @Query("devicemodel") String device
    );

    @Headers({"device: android"})
    @GET("addpay/Api/swipe-daily-sales-summary")
    Call<ResponseBody> swipe_daily_sales_summary(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("customer_id") String customer_id,
            @Query("y") String year,
            @Query("m") String month,
            @Query("d") String day,
            @Query("devicemodel") String device
    );

    @Headers({"device: android"})
    @GET("addpay/Api/swipe-daily-recon")
    Call<ResponseBody> swipe_daily_recon(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("customer_id") String customer_id,
            @Query("y") String year,
            @Query("m") String month,
            @Query("d") String day,
            @Query("devicemodel") String device
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/print-real-time-slip")
    Call<ResponseBody> print_real_time_slip(
            @Header("license") String TIU_LICENSE,
            @Query("customer_id") String customer_id,
            @Query("currDate") String currDate,
            @Query("sec") String sec
    );


    @Headers({"ignore: 2", "device: android"})
    @GET("addpay/Api/get-txns-appid")
    Call<ResponseBody> get_txns_appid(
            @Header("license") String TIU_LICENSE,
            @Query("sdt") String sdt,
            @Query("edt") String edt,
            @Query("ckey") String ckey,
            @Query("appid") String appid
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/get-swipe-sales")
    Call<ResponseBody> get_swipe_sales(
            @Header("license") String TIU_LICENSE,
            @Query("y") String y,
            @Query("m") String m,
            @Query("d") String d,
            @Query("devicemodel") String device
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/get-sts-cust-new")
    Call<ResponseBody> get_swipe_sts(
            @Header("license") String TIU_LICENSE,
            @Query("addpayverName") String versionName,
            @Query("addpayverCode") int versionCode
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("voucherapi/voucher/product-activation-request")
    Call<ResponseBody> activate_product(
            @Header("license") String TIU_LICENSE,
            @Query("serviceprovider_desc") String providerName
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("supplier/cashmx/payment-get-retailer")
    Call<ResponseBody> payment_get_retailer(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,

            @Query("t") String t,
            @Query("dt") String dt
    );

    @Headers({"device: android"})
    @GET("supplier/cashmx/retailer-payments")
    Call<ResponseBody> get_retailer_payments_list_(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("dt") String sdate,
            @Query("acc_no") String edate,
            @Query("slimit") String slimit,
            @Query("elimit") String elimit
    );

    @Headers({"device: android"})
    @GET("supplier/cashmx/retailer-payments-print")
    Call<ResponseBody> get_retailer_payments_print(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("t") String t,
            @Query("p") String p,
            @Query("dt") String dt,
            @Query("acc_no") String acc_no
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/adpay-txn-response")
    Call<ResponseBody> adpay_txn_response(
            @Header("license") String TIU_LICENSE,
            @Query("orderno") String orderno,
            @Query("ret_res") String ret_res

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/adpay-txn")
    Call<ResponseBody> adpay_txn(
            @Header("license") String TIU_LICENSE,
            @Query("orderno") String orderno,
            @Query("card_no") String card_no,
            @Query("voucher_no") String voucher_no,
            @Query("batch_no") String batch_no,
            @Query("refer_no") String refer_no,
            @Query("trans_time") String trans_time,
            @Query("amount") String amount,
            @Query("response") String response,
            @Query("notes") String notes,
            @Query("tip") String tip,
            @Query("discount") String discount
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/setorderno")
    Call<ResponseBody> setorderno(
            @Header("license") String TIU_LICENSE,
            @Query("orderno") String orderno

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/setordernoamnt")
    Call<ResponseBody> setordernoamnt(
            @Header("license") String TIU_LICENSE,
            @Query("orderno") String orderno,
            @Query("amount") String amount

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/updateorder")
    Call<ResponseBody> updateorder(
            @Header("license") String TIU_LICENSE,
            @Query("orderno") String orderno,
            @Query("recon") String recon

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/getunsettledorderno")
    Call<ResponseBody> getunsettledorderno(
            @Header("license") String TIU_LICENSE

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/getswipesalesnew")
    Call<ResponseBody> getswipesalesnew(
            @Header("license") String TIU_LICENSE,
            @Query("dt") String dt,
            @Query("txnsts") String txnsts

    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/getswipesalehistory")
    Call<ResponseBody> getswipesalehistory(
            @Header("license") String TIU_LICENSE,
            @Query("pid") String pid

    );


    @Headers({"device: android","ignore: 0"})
    @GET("addpay/Api/pay-to-bankcard")
    Call<ResponseBody> pay_to_bankcard(
            @Header("license") String TIU_LICENSE,
            @Header("posuser") String POSUSER_ID,
            @Query("amount") String amt
    );

    @Headers({"ignore: 1", "device: android"})
    @GET("addpay/Api/get-balance-cash-card")
    Call<ResponseBody> get_balance_cash_card(
            @Header("license") String TIU_LICENSE,
            @Query("lvl") String lvl
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("/voucherapi/general/get-web-services")
    Call<ResponseBody> get_web_services(
            @Header("license") String TIU_LICENSE
    );


    @Headers({"ignore: 1", "device: android"})
    @GET("/voucherapi/general/get-deposit-notify")
    Call<ResponseBody> get_deposit_notify(
            @Header("license") String TIU_LICENSE


    );

    ///voucherapi/general/update-deposit-notify
    @Headers({"ignore: 1", "device: android"})
    @GET("/voucherapi/general/update-deposit-notify")
    Call<ResponseBody> update_deposit_notify(
            @Header("license") String TIU_LICENSE,
            @Query("deposit_note_id") String deposit_note_id
    );
}
