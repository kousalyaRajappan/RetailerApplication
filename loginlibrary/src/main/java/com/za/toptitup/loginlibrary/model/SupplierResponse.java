package com.za.toptitup.loginlibrary.model;

import com.google.gson.annotations.SerializedName;

public class SupplierResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;
    @SerializedName("supplier")
    private SupplierData supplier;

    public SupplierData getSupplier() {
        return supplier;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}