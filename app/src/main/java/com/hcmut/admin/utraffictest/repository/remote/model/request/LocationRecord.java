package com.hcmut.admin.utraffictest.repository.remote.model.request;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hcmut.admin.utraffictest.business.UserLocation;

public class LocationRecord {
    @SerializedName("prevLatitude")
    @Expose
    private double prevLatitude;

    @SerializedName("prevLongitude")
    @Expose
    private double prevLongitude;

    @SerializedName("currLatitude")
    @Expose
    private double currLatitude;

    @SerializedName("currLongitude")
    @Expose
    private double currLongitude;

    @SerializedName("currTimestamp")
    @Expose
    private String currTimestamp;

    @SerializedName("prevTimestamp")
    @Expose
    private String prevTimestamp;

    public LocationRecord() {
    }

    public LocationRecord(UserLocation prevLocation, UserLocation currLocation) {
        if (prevLocation != null) {
            prevLatitude = prevLocation.getLatitude();
            prevLongitude = prevLocation.getLongitude();
            prevTimestamp = prevLocation.getTimestampString();
        }

        if (currLocation != null) {
            currLatitude = currLocation.getLatitude();
            currLongitude = currLocation.getLongitude();
            currTimestamp = currLocation.getTimestampString();
        }
    }

    public LocationRecord(double prevLatitude, double prevLongitude, String prevTimestamp, double currLatitude, double currLongitude, String currTimestamp) {
        this.prevLatitude = prevLatitude;
        this.prevLongitude = prevLongitude;
        this.prevTimestamp = prevTimestamp;

        this.currLatitude = currLatitude;
        this.currLongitude = currLongitude;
        this.currTimestamp = currTimestamp;
    }

    public double getPrevLatitude() {
        return prevLatitude;
    }

    public void setPrevLatitude(double prevLatitude) {
        this.prevLatitude = prevLatitude;
    }

    public double getPrevLongitude() {
        return prevLongitude;
    }

    public void setPrevLongitude(double prevLongitude) {
        this.prevLongitude = prevLongitude;
    }

    public double getCurrLatitude() {
        return currLatitude;
    }

    public void setCurrLatitude(double currLatitude) {
        this.currLatitude = currLatitude;
    }

    public double getCurrLongitude() {
        return currLongitude;
    }

    public void setCurrLongitude(double currLongitude) {
        this.currLongitude = currLongitude;
    }

    public String getCurrTimestamp() {
        return currTimestamp;
    }

    public void setCurrTimestamp(String currTimestamp) {
        this.currTimestamp = currTimestamp;
    }

    public String getPrevTimestamp() {
        return prevTimestamp;
    }

    public void setPrevTimestamp(String prevTimestamp) {
        this.prevTimestamp = prevTimestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "prev = " + prevLatitude + "; " + prevLongitude + " T = " + prevTimestamp + "... curr = " +  currLatitude + "; " + currLongitude + " T = " + currTimestamp;
    }
}
