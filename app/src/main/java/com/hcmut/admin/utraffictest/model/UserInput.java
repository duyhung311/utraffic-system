package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 2/6/2019.
 */

public class UserInput {
    private LatLng curPos,desPos;
    private int velocity;
    private int before;
    private int after;

    public UserInput(){
        velocity=0;
        before=0;
        after=0;
    }

    public LatLng getCurPos() {
        return curPos;
    }

    public LatLng getDesPos() {
        return desPos;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getBefore() {
        return before;
    }

    public int getAfter() {
        return after;
    }

    public void setCurPos(LatLng curPos) {
        this.curPos = curPos;
    }

    public void setDesPos(LatLng desPos) {
        this.desPos = desPos;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public void setAfter(int after) {
        this.after = after;
    }
}
