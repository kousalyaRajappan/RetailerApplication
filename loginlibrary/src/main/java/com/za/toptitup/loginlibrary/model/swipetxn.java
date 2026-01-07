package com.za.toptitup.loginlibrary.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class swipetxn extends RealmObject {


  //  private int swipe_id = 0;
    @PrimaryKey
    public String orderno = "";
    public int trans_status;
    public String code;
    public String card_no = "";
    public String voucher_no = "";
    public String batch_no = "";
    public String refer_no = "";
    public String trans_time = "";
    public String amount = "0";
    public String response = "0";
    public String notes = "";
    public String tip = "";
    public String discount = "";
    public Boolean is_txn = false;

    @Override
    public String toString()
    {
        return " [code = "+code+",orderno = "+orderno+",trans_status = "+trans_status+",is_txn = "+is_txn+", card_no = "+card_no+",voucher_no = "+voucher_no+", batch_no = "+batch_no+", refer_no = "+refer_no+", trans_time = "+trans_time+",amount = "+amount+",response = "+response+", notes = "+notes+", tip = "+tip+", discount = "+discount+"]";
    }
//
//    public int getSwipe_id() {
//        return swipe_id;
//    }
//
//    public void setSwipe_id(int swipe_id) {
//        this.swipe_id = swipe_id;
//    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public int getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(int trans_status) {
        this.trans_status = trans_status;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getVoucher_no() {
        return voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getRefer_no() {
        return refer_no;
    }

    public void setRefer_no(String refer_no) {
        this.refer_no = refer_no;
    }

    public String getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Boolean getIs_txn() {
        return is_txn;
    }

    public void setIs_txn(Boolean is_txn) {
        this.is_txn = is_txn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
