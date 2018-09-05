package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Ahmed Dawoud on 2/2/2017.
 */

public class Admin_RegistrationRequest_Model {
    private String user_id ;
    private String user_name ;
    private String user_phone ;
    private String user_Emails ;

    public Admin_RegistrationRequest_Model(String user_id, String user_name, String user_phone, String user_Emails) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_Emails = user_Emails;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_Emails() {
        return user_Emails;
    }

    public void setUser_Emails(String user_Emails) {
        this.user_Emails = user_Emails;
    }
}
