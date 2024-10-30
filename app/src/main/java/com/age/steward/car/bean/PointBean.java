package com.age.steward.car.bean;

import com.baidu.mapapi.model.LatLng;

public class PointBean {


    LatLng latLng;
    double distance;
    boolean canVisit;

    public PointBean(LatLng latLng) {
        this.latLng = latLng;
    }

    public PointBean(LatLng latLng, double distance, boolean canVisit) {
        this.latLng = latLng;
        this.distance = distance;
        this.canVisit = canVisit;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public boolean isCanVisit() {
        return canVisit;
    }

    public double getDistance() {
        return distance;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setCanVisit(boolean canVisit) {
        this.canVisit = canVisit;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
