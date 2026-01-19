package com.za.toptitup.loginlibrary.model;

import com.google.gson.annotations.SerializedName;

public class SupplierData {

        @SerializedName("name")
        private String supplierName;

        @SerializedName("legal_name")
        private String legalName;

        @SerializedName("address_1")
        private String address1;

        @SerializedName("phone_1")
        private String phone;

        @SerializedName("email_1")
        private String email;

        @SerializedName("country")
        private String country;
        @SerializedName("city")
        private String city;
        // Getters
        public String getSupplierName() { return supplierName; }
        public String getLegalName() { return legalName; }
        public String getAddress1() { return address1; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getCountry() { return country; }

        public String getCity() {
                return city;
        }
}
