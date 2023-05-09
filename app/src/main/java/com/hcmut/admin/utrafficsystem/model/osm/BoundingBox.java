package com.hcmut.admin.utrafficsystem.model.osm;

public class BoundingBox {
    private double topLeftLat;
    private double topLeftLon;
    private double botRghtLat;
    private double botRghtLon;
    public BoundingBox() {
    }
    public BoundingBox(double topLeftLat, double topLeftLon, double botRghtLat, double botRghtLon) {
        this.topLeftLat = topLeftLat;
        this.topLeftLon = topLeftLon;
        this.botRghtLat = botRghtLat;
        this.botRghtLon = botRghtLon;
    }

    public double getTopLeftLat() {
        return topLeftLat;
    }

    public void setTopLeftLat(double topLeftLat) {
        this.topLeftLat = topLeftLat;
    }

    public double getTopLeftLon() {
        return topLeftLon;
    }

    public void setTopLeftLon(double topLeftLon) {
        this.topLeftLon = topLeftLon;
    }

    public double getBotRghtLat() {
        return botRghtLat;
    }

    public void setBotRghtLat(double botRghtLat) {
        this.botRghtLat = botRghtLat;
    }

    public double getBotRghtLon() {
        return botRghtLon;
    }

    public void setBotRghtLon(double botRghtLon) {
        this.botRghtLon = botRghtLon;
    }
}
