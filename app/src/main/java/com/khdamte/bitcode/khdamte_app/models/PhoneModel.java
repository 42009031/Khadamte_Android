package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 4/7/2017.
 */

public class PhoneModel {


    private String phone_name ;
    private String phone_number ;

    public PhoneModel(String phone_name, String phone_number) {
        this.phone_name = phone_name;
        this.phone_number = phone_number;
    }

    public String getPhone_name() {
        return phone_name;
    }

    public void setPhone_name(String phone_name) {
        this.phone_name = phone_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
