package com.za.toptitup.loginlibrary.model;

public class WholesaleResponse {
    private String reference;
    private double amount;
    private int from_customer_id;
    private int to_customer_id;
    private int supplier_id;
    private String status;
    private String message;

    // Getters & Setters
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getFrom_customer_id() {
        return from_customer_id;
    }

    public void setFrom_customer_id(int from_customer_id) {
        this.from_customer_id = from_customer_id;
    }

    public int getTo_customer_id() {
        return to_customer_id;
    }

    public void setTo_customer_id(int to_customer_id) {
        this.to_customer_id = to_customer_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
