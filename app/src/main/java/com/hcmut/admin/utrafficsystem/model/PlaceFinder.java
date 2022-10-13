package com.hcmut.admin.utrafficsystem.model;

import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Created by Admin on 10/5/2018.
 */

public class PlaceFinder {
    private PlacesClient mGeoDataClient;
    private PlaceInfo mPlace;


    public PlaceFinder(DirectionFinderListener listener, PlacesClient mGeoDataClient) {
        this.mGeoDataClient = mGeoDataClient;
    }

//    void getPlaceInfoById(String placeId) {
//        mPlace = new PlaceInfo();
//        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    Place place = places.get(0);
//
//                    try {
//                        mPlace.setAddress(place.getAddress().toString());
//                        //mPlace.setAttributions(place.getAttributions().toString());
//                        mPlace.setId(place.getId().toString());
//                        mPlace.setLatLng(place.getLatLng());
//                        mPlace.setName(place.getName().toString());
//                        mPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                        mPlace.setRating(place.getRating());
//                        mPlace.setWebsiteUri(place.getWebsiteUri());
//                    } catch (NullPointerException e) {
//                        Log.e("Error: ", "onResult: NullPointerException: " + e.getMessage());
//                    }
//                    places.release();
//                } else {
//                    Log.e("Error: ", "Place not found.");
//                }
//            }
//        });
//    }
}
