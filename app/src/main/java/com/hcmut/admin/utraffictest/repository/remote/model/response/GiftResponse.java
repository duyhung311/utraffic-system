package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class GiftResponse {

        @SerializedName("_id")
        private String _id;
        @SerializedName("longitude")
        private float longitude;
        @SerializedName("latitude")
        private float latitude;
        @SerializedName("point")
        private int point;
        @SerializedName("amount")
        private int amount;

        public GiftResponse(String _id,float longitude,float latitude,int point,int amount) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.point = point;
            this.amount = amount;
            this._id = _id;
        }
        public String getId() {
        return _id;
    }
        public float getLongitude() {
            return longitude;
        }

        public float getLatitude() {
            return latitude;
        }

        public int getPoint() {
            return point;
        }
        public int getAmount() {
            return amount;
        }




}
