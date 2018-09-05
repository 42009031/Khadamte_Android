package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 7/23/2017.
 */

public class ServicesModel {

    private String serv_id, serv_name, serv_img, serv_phonr1, serv_phone2 ;

    public ServicesModel(String serv_id, String serv_name, String serv_img, String serv_phonr1, String serv_phone2) {
        this.serv_id = serv_id;
        this.serv_name = serv_name;
        this.serv_img = serv_img;
        this.serv_phonr1 = serv_phonr1;
        this.serv_phone2 = serv_phone2;
    }
    public ServicesModel(){}

    public String getServ_id() {
        return serv_id;
    }

    public void setServ_id(String serv_id) {
        this.serv_id = serv_id;
    }

    public String getServ_name() {
        return serv_name;
    }

    public void setServ_name(String serv_name) {
        this.serv_name = serv_name;
    }

    public String getServ_img() {
        return serv_img;
    }

    public void setServ_img(String serv_img) {
        this.serv_img = serv_img;
    }

    public String getServ_phonr1() {
        return serv_phonr1;
    }

    public void setServ_phonr1(String serv_phonr1) {
        this.serv_phonr1 = serv_phonr1;
    }

    public String getServ_phone2() {
        return serv_phone2;
    }

    public void setServ_phone2(String serv_phone2) {
        this.serv_phone2 = serv_phone2;
    }
}
