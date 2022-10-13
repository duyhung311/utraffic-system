package com.hcmut.admin.utrafficsystem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Atm implements Parcelable {
    private String _id;
    private String name;
    private String address;
    private Integer number_atm;
    private String work_time;
    private String phone_number;
    private String branch_atm;
    private float latitude;
    private float longitude;
    private float rate;
    private int numberOfRate;


    protected Atm(Parcel in) {
        _id = in.readString();
        name = in.readString();
        address = in.readString();
        number_atm = in.readInt();
        work_time = in.readString();
        phone_number = in.readString();
        branch_atm = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        rate = in.readFloat();
        numberOfRate = in.readInt();
    }

    public static final Creator<Atm> CREATOR = new Creator<Atm>() {
        @Override
        public Atm createFromParcel(Parcel in) {
            return new Atm(in);
        }

        @Override
        public Atm[] newArray(int size) {
            return new Atm[size];
        }


    };

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNumber_atm() {
        return number_atm;
    }

    public void setNumber_atm(Integer number_atm) {
        this.number_atm = number_atm;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBranch_atm() {
        return branch_atm;
    }

    public void setBranch_atm(String branch_atm) {
        this.branch_atm = branch_atm;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getNumberOfRate() {
        return numberOfRate;
    }

    public void setNumberOfRate(int numberOfRate) {
        this.numberOfRate = numberOfRate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeInt(number_atm);
        dest.writeString(work_time);
        dest.writeString(phone_number);
        dest.writeString(branch_atm);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeFloat(rate);
        dest.writeInt(numberOfRate);
    }
}