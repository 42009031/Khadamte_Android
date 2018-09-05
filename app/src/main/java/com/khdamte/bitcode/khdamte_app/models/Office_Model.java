package com.khdamte.bitcode.khdamte_app.models;

import java.util.Set;

/**
 * Created by Amado on 4/2/2017.
 */

public class Office_Model {

    private String office_id;
    private String office_name ;
    private String office_address;
    private String office_img;
    private String office_desc;
    private String office_email ;
    private String office_phone1 ;
    private String office_phone2 ;
    private String office_phone3 ;
    private Set<Integer> nationnality_maids ;
    private String office_review ;

    public Office_Model(String office_id, String office_name, String office_address, String office_img, String office_desc, String office_email, String office_phone1, String office_phone2, String office_phone3, Set<Integer> nationnality_maids) {
        this.office_id = office_id;
        this.office_name = office_name;
        this.office_address = office_address;
        this.office_desc = office_desc;
        this.office_email = office_email;
        this.office_phone1 = office_phone1;
        this.office_phone2 = office_phone2;
        this.office_phone3 = office_phone3;
        this.nationnality_maids = nationnality_maids;
        this.office_img = office_img ;
    }


    public Office_Model(String office_id, String office_name, String office_img, String office_desc) {
        this.office_id = office_id;
        this.office_name = office_name;
        this.office_img = office_img;
        this.office_desc = office_desc;
    }

    public Office_Model(String office_name, String office_address, String office_desc, String office_email, String office_phone1, String office_phone2, String office_phone3) {
        this.office_name = office_name;
        this.office_address = office_address;
        this.office_desc = office_desc;
        this.office_email = office_email;
        this.office_phone1 = office_phone1;
        this.office_phone2 = office_phone2;
        this.office_phone3 = office_phone3;
    }

    public Office_Model() {

    }

    public String getOffice_img() {
        return office_img;
    }

    public void setOffice_img(String office_img) {
        this.office_img = office_img;
    }

    public String getOffice_id() {
        return office_id;
    }

    public void setOffice_id(String office_id) {
        this.office_id = office_id;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public String getOffice_address() {
        return office_address;
    }

    public void setOffice_address(String office_address) {
        this.office_address = office_address;
    }

    public String getOffice_desc() {
        return office_desc;
    }

    public void setOffice_desc(String office_desc) {
        this.office_desc = office_desc;
    }

    public String getOffice_email() {
        return office_email;
    }

    public void setOffice_email(String office_email) {
        this.office_email = office_email;
    }

    public String getOffice_phone1() {
        return office_phone1;
    }

    public void setOffice_phone1(String office_phone1) {
        this.office_phone1 = office_phone1;
    }

    public String getOffice_phone2() {
        return office_phone2;
    }

    public void setOffice_phone2(String office_phone2) {
        this.office_phone2 = office_phone2;
    }

    public String getOffice_phone3() {
        return office_phone3;
    }

    public void setOffice_phone3(String office_phone3) {
        this.office_phone3 = office_phone3;
    }

    public Set<Integer> getNationnality_maids() {
        return nationnality_maids;
    }

    public void setNationnality_maids(Set<Integer> nationnality_maids) {
        this.nationnality_maids = nationnality_maids;
    }

    public String getOffice_review() {
        return office_review;
    }

    public void setOffice_review(String office_review) {
        this.office_review = office_review;
    }
}
