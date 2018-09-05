package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 11-Jul-18.
 */

public class IndividualMaidsModel {

    private String id, img, name, desc, nationality, price;

    public IndividualMaidsModel() {
    }

    public IndividualMaidsModel(String id, String img, String name, String desc, String nationality, String price) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.desc = desc;
        this.nationality = nationality;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
