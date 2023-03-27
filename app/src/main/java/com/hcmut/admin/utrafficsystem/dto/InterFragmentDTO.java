package com.hcmut.admin.utrafficsystem.dto;

import com.google.android.gms.maps.GoogleMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//use for passing
public class InterFragmentDTO implements Serializable {
    private Map<String, Object> map;

    public InterFragmentDTO() {
        map = new HashMap<>();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
