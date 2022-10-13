package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchWayData {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("distance")
    @Expose
    private int distance;

    @SerializedName("coords")
    @Expose
    private List<Coord> coords;

    /**
     * No args constructor for use in serialization
     *
     */
    public SearchWayData() {
    }

    /**
     *
     * @param distance
     * @param coords
     */
    public SearchWayData(int distance, List<Coord> coords) {
        super();
        this.distance = distance;
        this.coords = coords;
    }

    public static PolylineOptions parsePolylineOptions(SearchWayData searchWayData) {
        if (searchWayData == null || searchWayData.getCoords() == null) return null;
        int count = 0;
        PolylineOptions wayPolylineOptions = new PolylineOptions()
                .width(7)
                .geodesic(true)
                .clickable(true)
                .color(Color.BLUE);
        for (SearchWayData.Coord coord : searchWayData.getCoords()) {
            try {
                wayPolylineOptions.add(
                        new LatLng(coord.getLat(), coord.getLng()),
                        new LatLng(coord.getLat(), coord.getLng()));
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count > 1 ? wayPolylineOptions : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void setCoords(List<Coord> coords) {
        this.coords = coords;
    }

    @Override
    @NonNull
    public String toString() {
        String coordsString = "";
        if (coords != null) {
            StringBuilder builder = new StringBuilder();
            for (Coord coord : coords) {
                builder.append(" {");
                builder.append(coord.getLat());
                builder.append(",");
                builder.append(coord.getLng());
                builder.append("}");
            }
            coordsString = builder.toString();
        }
        return "Distance = " + distance + "; id = " + id + "; coord:" + coordsString;
    }

    public static class Coord {

        @SerializedName("lat")
        @Expose
        private double lat;

        @SerializedName("lng")
        @Expose
        private double lng;
//
//        @SerializedName("segment_id")
//        @Expose
//        private int segmentId;

        /**
         * No args constructor for use in serialization
         *
         */
        public Coord() {
        }

        /**
         *
         * @param lng
         * @param lat
         */
        public Coord(double lat, double lng) {
            super();
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
