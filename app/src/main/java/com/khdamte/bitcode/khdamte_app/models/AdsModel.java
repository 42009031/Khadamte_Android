package com.khdamte.bitcode.khdamte_app.models;

import java.io.Serializable;

/**
 * Created by Amado on 7/18/2017.
 */

public class AdsModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ads_img;
    private String office_id ;
    private String office_name;

    public AdsModel(String ads_img, String office_id, String office_name) {
        this.ads_img = ads_img;
        this.office_id = office_id;
        this.office_name = office_name;
    }

    public String getAds_img() {
        return ads_img;
    }

    public void setAds_img(String ads_img) {
        this.ads_img = ads_img;
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
}
