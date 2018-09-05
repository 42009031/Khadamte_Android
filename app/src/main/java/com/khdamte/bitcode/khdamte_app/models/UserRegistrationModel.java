package com.khdamte.bitcode.khdamte_app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amado on 03-Sep-18.
 */

public class UserRegistrationModel implements Parcelable {


    private String role, fName, lName, email, pwd, phone, gender, state, address;

    public UserRegistrationModel(String role, String fName, String lName, String email, String pwd, String phone, String gender, String state, String address) {
        this.role = role;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.gender = gender;
        this.state = state;
        this.address = address;
    }

    protected UserRegistrationModel(Parcel in) {
        role = in.readString();
        fName = in.readString();
        lName = in.readString();
        email = in.readString();
        pwd = in.readString();
        phone = in.readString();
        gender = in.readString();
        state = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(role);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(email);
        dest.writeString(pwd);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(state);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
