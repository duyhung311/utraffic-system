package com.hcmut.admin.utrafficsystem.ui.searchplace.callback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.Stack;

public class SearchPlaceResultHandler {
    public static final String SEARCH_TYPE = "SEARCH_TYPE";
    public static final int NORMAL_SEARCH = 0;
    public static final int BEGIN_SEARCH = 1;
    public static final int END_SEARCH = 2;
    public static final int SELECTED_BEGIN_SEARCH = 3;
    public static final int SELECTED_END_SEARCH = 4;


    private Stack<SearchResultCallback> listeners = new Stack<>();

    private SearchPlaceResultHandler() {

    }

    private static SearchPlaceResultHandler handler;
    public static SearchPlaceResultHandler getInstance() {
        if (handler == null) {
            handler = new SearchPlaceResultHandler();
        }
        return handler;
    }

    public void addSearchPlaceResultListener(SearchResultCallback listener) {
        this.listeners.push(listener);
    }

    public void clearListener() {
        listeners.clear();
    }

    public void dispatchSearchPlaceResult(int type, AutocompletePrediction result, LatLng latlng) {
        SearchResultCallback listener = listeners.pop();
        if (listener != null) {
            switch (type) {
                case NORMAL_SEARCH:
                    listener.onSearchResultReady(result);
                    break;
                case BEGIN_SEARCH:
                    listener.onBeginSearchPlaceResultReady(result);
                    break;
                case END_SEARCH:
                    listener.onEndSearchPlaceResultReady(result);
                    break;
                case SELECTED_BEGIN_SEARCH:
                    listener.onSelectedBeginSearchPlaceResultReady(latlng);
                    break;
                case SELECTED_END_SEARCH:
                    listener.onSelectedEndSearchPlaceResultReady(latlng);
                    break;
            }
        }
    }

    public static int convertToSelectedPointSearchType(int type) {
        switch (type){
            case BEGIN_SEARCH:
                return SELECTED_BEGIN_SEARCH;
            case END_SEARCH:
                return SELECTED_END_SEARCH;
            default:
                return type;
        }
    }
}

