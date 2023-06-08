package com.hcmut.admin.utraffictest.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapUtils {
    //BEARING
    public double GetBearing(LatLng from, LatLng to){
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * 180.0 / Math.PI;

        return angle;
    }

    //LATLNG FUNCTIONS
    public double getDistBetween2LatLngs(LatLng pointA,LatLng pointB)
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(pointB.latitude-pointA.latitude);
        double lngDiff = Math.toRadians(pointB.longitude-pointA.longitude);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(pointA.latitude)) * Math.cos(Math.toRadians(pointB.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
        int meterConversion = 1609;
        return distance * meterConversion;
    }

    public LatLng getPntWithDistAndAngle(LatLng origin, double radius, double angle)
    {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        double EARTH_RADIUS = 6378100.0;   //unit: meter

        // Convert to radians.
        double lat = origin.latitude * Math.PI / 180.0;
        double lon = origin.longitude * Math.PI / 180.0;
        double angleInRad = angle * Math.PI/180;

        // y
        double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(angleInRad);
        // x
        double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(angleInRad) / Math.cos(lat);

        return new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);
    }

    public static LatLng getMidPointOf2LatLngs(LatLng pointA, LatLng pointB){
        //lat lon in rad
        double latA = Math.toRadians(pointA.latitude);
        double lonA = Math.toRadians(pointA.longitude);
        double latB = Math.toRadians(pointB.latitude);
        double lonB = Math.toRadians(pointB.longitude);

        double Bx = Math.cos(latB) * Math.cos(lonB - lonA);
        double By = Math.cos(lonB) * Math.sin(lonB - lonA);

        double midLat = Math.toDegrees(Math.atan2(Math.sin(latA) + Math.sin(latB), Math.sqrt((Math.cos(latA) + Bx) * (Math.cos(latA) + Bx) + By * By)));
        double midLon = Math.toDegrees(lonA + Math.atan2(By, Math.cos(latA) + Bx));

        return new LatLng(midLat,midLon);
    }

    public ArrayList<LatLng> getCirclePoints(LatLng centre, double radius)
    {
        ArrayList<LatLng> points = new ArrayList<>();

        double EARTH_RADIUS = 6378100.0;   //unit: meter
        // Convert to radians.
        double lat = centre.latitude * Math.PI / 180.0;
        double lon = centre.longitude * Math.PI / 180.0;

        for (double t = 0; t < Math.PI * 2+0.3; t += 0.3)
        {
            // y
            double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
            // x
            double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

            // saving the location on circle as a LatLng point
            LatLng point =new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);

            // now here note that same point(lat/lng) is used for marker as well as saved in the ArrayList
            points.add(point);
        }
        return points;
    }


    //PLACE ID
    public String getPlaceId(LatLng point){
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");
        GeocodingResult[] results = new GeocodingResult[0];
        try {
            results = GeocodingApi.newRequest(context)
                    .latlng(new com.google.maps.model.LatLng(point.latitude,point.longitude)).await();
            Log.d("Place id: ",results[0].placeId);
            return results[0].placeId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class getPlaceId extends AsyncTask<LatLng, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(LatLng... placeString) {
            try {
                return getPlaceId(placeString[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String placeId) {
            super.onPostExecute(placeId);
            Log.d("Place id: ",placeId);
        }
    }

    //CELL
    boolean isPointsInsideRadius(LatLng checkPoint, LatLng centerPoint, double km) {
        double ky = 40000 / 360;
        double kx = Math.cos(Math.PI * centerPoint.latitude / 180.0) * ky;
        double dx = Math.abs(centerPoint.latitude - checkPoint.longitude) * kx;
        double dy = Math.abs(centerPoint.latitude - checkPoint.longitude) * ky;
        return Math.sqrt(dx * dx + dy * dy) <= km;
    }

    List<Cell> getCellsListInsideCircle(LatLng centre, double radius, List<Cell> cellsList) {
        List<Cell> insideCircleCellsList = new ArrayList<>();
        for(int i=0;i<cellsList.size();i++){
            if(isPointsInsideRadius(cellsList.get(i).getLatLng(),centre,radius)){
                //add cell to list
                insideCircleCellsList.add(cellsList.get(i));
            }
        }
        return insideCircleCellsList;
    }

    //PLACE INFO
    PlaceInfo mPlace;
//    public void getPlaceInfoById(PlacesClient placesClient, String placeId){
//        placesClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    Place place= places.get(0);
//
//                    mPlace = new PlaceInfo();
//
//                    try{
//                        mPlace.setAddress(place.getAddress().toString());
//                        //mPlace.setAttributions(place.getAttributions().toString());
//                        mPlace.setId(place.getId().toString());
//                        mPlace.setLatLng(place.getLatLng());
//                        mPlace.setName(place.getName().toString());
//                        mPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                        mPlace.setRating(place.getRating());
//                        mPlace.setWebsiteUri(place.getWebsiteUri());
//                    }catch (NullPointerException e){
//                        Log.e("Message:", "onResult: NullPointerException: " + e.getMessage() );
//                    }
//                    places.release();
//                } else {
//                    Log.e("Message:", "Place not found.");
//                }
//            }
//        });
//    }

    public Address getLatLngByAddressOrPlaceName(Context context, String placeString){
        String searchString = placeString.toString();

        Geocoder geocoder= new Geocoder(context);

        List<Address> list= new ArrayList<>();
        try{
            //first parameter: search text for finding location, second parameter: maximum number of result get
            list=geocoder.getFromLocationName(searchString,1);

        }catch (IOException e){

        }

        if(list.size() > 0){
            Address address = list.get(0);
            return address;
        }
        return null;
    }

}
