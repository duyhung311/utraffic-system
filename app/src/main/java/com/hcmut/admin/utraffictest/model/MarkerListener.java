package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.Marker;

public interface MarkerListener {
    String REPORT_ARROW = "REPORT_ARROW";
    void onClick(Marker marker);
}
