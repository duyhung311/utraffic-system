package com.hcmut.admin.utraffictest.repository.remote.model.response;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay.BitmapLineData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatusRenderData {
    @SerializedName("segment")
    @Expose
    private long segment;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;
    @SerializedName("velocity")
    @Expose
    private int velocity;
    @SerializedName("createdAt")
    @Expose
    private Date createdAt;
    @SerializedName("street")
    @Expose
    private Street street;

    @NonNull
    @Override
    public String toString() {
        return "polyline: " + polyline.toString() + ", color: " + color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public long getSegment() {
        return segment;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void setSegment(long segment) {
        this.segment = segment;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public Street getStreet() {
        return street;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<LatLng> getLatLngPolyline() {
        if (polyline != null) {
            return polyline.getLatLngPolyline();
        }
        return null;
    }

    public boolean isInLatLngBounds(LatLngBounds bounds) {
        LatLng latLng = polyline.getStartPointLatLng();
        if (latLng != null) {
            return bounds.contains(latLng);
        }
        return false;
    }
    public LatLng getStartPointLatLng() {
        if (polyline != null) {
            return polyline.getStartPointLatLng();
        }
        return null;
    }

    public static List<com.google.android.gms.maps.model.Polyline> renderStatus(final GoogleMap googleMap, List<StatusRenderData> statusRenderDataList) {
        if (googleMap == null || statusRenderDataList == null) return null;
        List<LatLng> line;
        String color;
        final List<com.google.android.gms.maps.model.Polyline>  polylineList = new ArrayList<>();
        Log.e("lis", "" + statusRenderDataList.size());
        Handler handler = new Handler(Looper.getMainLooper());
        for (StatusRenderData statusRenderData : statusRenderDataList) {
            try {
                line = statusRenderData.getLatLngPolyline();
                color = statusRenderData.getColor();
                if (line != null && line.size() == 2 && color != null) {
                    final PolylineOptions polylineOptions = new PolylineOptions()
                            .add(line.get(0), line.get(1))
                            .width(5)
                            .color(Color.parseColor(color));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            polylineList.add(googleMap.addPolyline(polylineOptions));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return polylineList;
    }

    public static List<MarkerOptions> parseMarkerOptionsList(
            List<StatusRenderData> statusRenderDataList, BitmapDescriptor icon) {
        if (statusRenderDataList != null && statusRenderDataList.size() > 0) {
            MarkerOptions markerOptions;
            List<MarkerOptions> markerOptionsList = new ArrayList<>();
            for (StatusRenderData data : statusRenderDataList) {
                try {
                    Date createdDate = data.getCreatedAt();
                    if (createdDate == null) {
                        createdDate = new Date(Calendar.getInstance().getTime().getTime() - 50000);
                    }
                    markerOptions = new MarkerOptions()
                            .position(data.getStartPointLatLng())
                            .title(data.getSegment() + "/" + data.getVelocity() + "/" + data.getColor() + "/" + createdDate.getTime())
                            .icon(icon);
                    markerOptionsList.add(markerOptions);
                } catch (Exception e) {}
            }
            return markerOptionsList;
        }
        return null;
    }

    public static List<BitmapLineData> parseBitmapLineData (List<StatusRenderData> statusRenderDataList) {
        if (statusRenderDataList != null && statusRenderDataList.size() > 0) {
            List<BitmapLineData> lineDataList = new ArrayList<>();
            List<LatLng> line;
            String color;
            try {
                for (StatusRenderData statusRenderData : statusRenderDataList) {
                    line = statusRenderData.getLatLngPolyline();
                    color = statusRenderData.getColor();
                    if (line != null && line.size() == 2 && color != null) {
                        lineDataList.add(new BitmapLineData(line.get(0), line.get(1), color));
                    }
                }
            } catch (Exception e) {}
            return lineDataList;
        }
        return null;
    }

    public static List<PolylineOptions> parsePolylineOption (List<StatusRenderData> statusRenderDataList) {
        if (statusRenderDataList != null && statusRenderDataList.size() > 0) {
            List<PolylineOptions> polylineOptionsList = new ArrayList<>();
            List<LatLng> line;
            String color;
            PolylineOptions polylineOptions;
            try {
                for (StatusRenderData statusRenderData : statusRenderDataList) {
                    line = statusRenderData.getLatLngPolyline();
                    color = statusRenderData.getColor();
                    if (line != null && line.size() == 2 && color != null) {
                        polylineOptions = new PolylineOptions()
                                .width(7)
                                .color(Color.parseColor(color))
                                .addAll(line);
                        polylineOptionsList.add(polylineOptions);
                    }
                }
            } catch (Exception e) {}
            return polylineOptionsList;
        }
        return null;
    }

    public static class Polyline {

        @SerializedName("coordinates")
        @Expose
        private List<List<Double>> coordinates = null;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }

        public List<LatLng> getLatLngPolyline() {
            try {
                List<LatLng> l = new ArrayList<>();
                l.add(new LatLng(coordinates.get(0).get(1), coordinates.get(0).get(0)));
                l.add(new LatLng(coordinates.get(1).get(1), coordinates.get(1).get(0)));
                return l;
            } catch (Exception e) {}
            return null;
        }

        public LatLng getStartPointLatLng() {
            try {
                return new LatLng(coordinates.get(0).get(1), coordinates.get(0).get(0));
            } catch (Exception e) {}
            return null;
        }

        @NonNull
        @Override
        public String toString() {
            try {
                String s = "";
                for (List<Double> coords : coordinates) {
                    s += "{ " + coords.get(0) + ", " + coords.get(1) + "} ";
                }
                return s;
            } catch (Exception e) {
            }
            return "";
        }
    }

    public static class Street {
        @SerializedName("type")
        @Expose
        public String type;

        @NonNull
        @Override
        public String toString() {
            return "streetType: " + type;
        }
    }

}

