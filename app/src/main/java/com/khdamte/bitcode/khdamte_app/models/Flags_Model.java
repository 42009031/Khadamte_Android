package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 4/1/2017.
 */

public class Flags_Model {

    private String flag_name, flag_id, flag_img;

    public Flags_Model(String flag_name, String flag_id, String flag_img) {
        this.flag_name = flag_name;
        this.flag_id = flag_id;
        this.flag_img = flag_img;
    }

    public String getFlag_name() {
        return flag_name;
    }

    public void setFlag_name(String flag_name) {
        this.flag_name = flag_name;
    }

    public String getFlag_id() {
        return flag_id;
    }

    public void setFlag_id(String flag_id) {
        this.flag_id = flag_id;
    }

    public String getFlag_img() {
        return flag_img;
    }

    public void setFlag_img(String flag_img) {
        this.flag_img = flag_img;
    }
}
