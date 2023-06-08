package com.hcmut.admin.utraffictest.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 10/2/2018.
 */

public class PlaceInfo {
    private String address;
    private String attributions;
    private String id;
    private LatLng latLng;
    private String name;
    private String phoneNumber;
    private float rating;
    private Uri websiteUri;

    public PlaceInfo(String address, String attributions, String id, LatLng latLng, String name, String phoneNumber, float rating, Uri websiteUri) {
        this.address = address;
        this.attributions = attributions;
        this.id = id;
        this.latLng = latLng;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.websiteUri = websiteUri;
    }

    public PlaceInfo() {
        name = "aaaaa";
    }

    public String getAddress() {
        return address;
    }

    public String getAttributions() {
        return attributions;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "address='" + address + '\'' +
                ", attributions='" + attributions + '\'' +
                ", id='" + id + '\'' +
                ", latLng=" + latLng +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                ", websiteUri=" + websiteUri +
                '}';
    }
}
