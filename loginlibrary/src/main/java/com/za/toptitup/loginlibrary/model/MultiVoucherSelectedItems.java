package com.za.toptitup.loginlibrary.model;

public class MultiVoucherSelectedItems  {

    public int pos;

    public String text;
    public String deno;
    public String item_barcode;
    public int drawable;
    public Integer serviceProviderId;

    public MultiVoucherSelectedItems(int pos, String text, String deno, String item_barcode,Integer serviceProviderId,int drawable) {
        this.pos = pos;
        this.text = text;
        this.deno = deno;
        this.item_barcode = item_barcode;
        this.serviceProviderId = serviceProviderId;
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public Integer getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Integer serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getItem_barcode() {
        return item_barcode;
    }

    public void setItem_barcode(String item_barcode) {
        this.item_barcode = item_barcode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDeno() {
        return deno;
    }

    public void setDeno(String deno) {
        this.deno = deno;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
