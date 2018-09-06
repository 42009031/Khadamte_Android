package com.khdamte.bitcode.khdamte_app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amado on 03-Sep-18.
 */

public class UserRegistrationModel implements Parcelable {


    private String role, userId, fName, lName, email, pwd, phone, gender, state, countryId, address;

    public UserRegistrationModel(String role, String userId, String fName, String lName, String email, String pwd, String phone, String gender, String state, String countryId, String address) {
        this.role = role;
        this.userId = userId;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.gender = gender;
        this.state = state;
        this.countryId = countryId ;
        this.address = address;
    }

    protected UserRegistrationModel(Parcel in) {
        role = in.readString();
        userId = in.readString();
        fName = in.readString();
        lName = in.readString();
        email = in.readString();
        pwd = in.readString();
        phone = in.readString();
        gender = in.readString();
        state = in.readString();
        countryId = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(role);
        dest.writeString(userId);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(email);
        dest.writeString(pwd);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(state);
        dest.writeString(countryId);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRegistrationModel> CREATOR = new Creator<UserRegistrationModel>() {
        @Override
        public UserRegistrationModel createFromParcel(Parcel in) {
            return new UserRegistrationModel(in);
        }

        @Override
        public UserRegistrationModel[] newArray(int size) {
            return new UserRegistrationModel[size];
        }
    };

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
