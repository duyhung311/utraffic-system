package com.hcmut.admin.utrafficsystem.ui.searchplace.callback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

public interface SearchResultCallback {
    void onSearchResultReady(AutocompletePrediction result);
    void onBeginSearchPlaceResultReady(AutocompletePrediction result);
    void onEndSearchPlaceResultReady(AutocompletePrediction result);
    void onSelectedBeginSearchPlaceResultReady(LatLng result);
    void onSelectedEndSearchPlaceResultReady(LatLng result);
}
