package com.hcmut.admin.utrafficsystem.business;

import android.content.Context;
import android.location.Address;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

public class SearchPlaceHandler {

    private Context context;

    public SearchPlaceHandler(Context context) {
        this.context = context;
    }

    public static LatLng getLatLngFromAddressTextInput(Context context, String searchText) {
        Address address = MapUtil.getLatLngByAddressOrPlaceName(context, searchText);
        if (address != null) {
            return new LatLng(address.getLatitude(), address.getLongitude());
        }
        return null;
    }
}
