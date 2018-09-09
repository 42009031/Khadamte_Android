package com.khdamte.bitcode.khdamte_app.models;

/**
 * Created by Amado on 09-Sep-18.
 */

public class MaidsDataModel {

    private String maidId;
    private String userId;
    private String name;
    private String religion;
    private String age;
    private String price;
    private String image;
    private String descrip;
    private String user;
    private String phone1;
    private String phone2;
    private String nationality;

    public MaidsDataModel(){}

    public MaidsDataModel(String maidId, String userId, String name, String religion, String age, String price, String image, String descrip, String user, String phone1, String phone2, String nationality) {
        this.maidId = maidId;
        this.userId = userId;
        this.name = name;
        this.religion = religion;
        this.age = age;
        this.price = price;
        this.image = image;
        this.descrip = descrip;
        this.user = user;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.nationality = nationality;
    }

    public String getMaidId() {
        return maidId;
    }

    public void setMaidId(String maidId) {
        this.maidId = maidId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
