package com.hcmut.admin.utrafficsystem.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;
import com.google.maps.model.SpeedLimit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 10/5/2018.
 */
@Deprecated
public class RoadFinder {
    private GeoApiContext mContext;
    private MapActivity mapActivity;
    private TrafficData mTrafficData;
    private LatLng point;
    private float radius;
    private float currentVelocity;
    private int trafficLevel;
    private int duration;
    private boolean isFindPoint = false;

    @SuppressLint("StaticFieldLeak")
    private
    AsyncTask<LatLng, Integer, SnappedPoint> mSnapToRoad = new AsyncTask<LatLng, Integer, SnappedPoint>() {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SnappedPoint doInBackground(LatLng... point) {
            try {
                //Log.d("current latitude",Double.toString(point[0].latitude));
                //Log.d("current longtitude",Double.toString(point[0].longitude));
                return snapToRoad(new LatLng(point[0].latitude, point[0].longitude));
            } catch (final Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SnappedPoint snappedPoint) {
            super.onPostExecute(snappedPoint);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    };
    @SuppressLint("StaticFieldLeak")
    private AsyncTask<String, Integer, Long> mGetSpeedLimit = new AsyncTask<String, Integer, Long>() {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... placeId) {
            try {
                return getSpeedLimit(mContext, placeId[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long speedLimit) {
            super.onPostExecute(speedLimit);
            //mapActivity.addTrafficRoad(new TrafficRoad(mTrafficData,speedLimit));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    };

    public RoadFinder(MapActivity mapActivity, float radius, float currentVelocity, int trafficLevel, int duration) {
        this.mapActivity = mapActivity;
        this.radius = radius;
        this.currentVelocity = currentVelocity;
        this.trafficLevel = trafficLevel;
        this.duration = duration;
        mContext = new GeoApiContext().setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");
    }

    /* GET PLACE ID */

    public RoadFinder(MapActivity mapActivity, TrafficData mTrafficData) {
        this.mapActivity = mapActivity;
        this.mTrafficData = mTrafficData;
        mContext = new GeoApiContext().setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");
    }

    public RoadFinder(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
        mContext = new GeoApiContext().setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");
    }

    public void getPlaceIdByLatLng(LatLng point) {
        this.point = new LatLng(point.latitude, point.longitude);
        Toast.makeText(mapActivity, "My lonngggg:" + Double.toString(point.longitude), Toast.LENGTH_LONG).show();
        mSnapToRoad.execute(point);
    }

    public void getSnappedPoint(LatLng point, boolean isFindPoint) {
        this.point = new LatLng(point.latitude, point.longitude);
        this.isFindPoint = isFindPoint;
        mSnapToRoad.execute(point);
    }

    private SnappedPoint snapToRoad(LatLng point) throws Exception {
        List<com.google.maps.model.LatLng> mCapturedLocations = new ArrayList<>();
        mCapturedLocations.add(new com.google.maps.model.LatLng(point.latitude, point.longitude));
        com.google.maps.model.LatLng[] page = mCapturedLocations.subList(0, 1).toArray(new com.google.maps.model.LatLng[1]);
        SnappedPoint[] snappedPoints = RoadsApi.snapToRoads(mContext, true, page).await();
        return snappedPoints[0];
    }

    /* GET SPEED LIMIT */
    public void getSpeedLimitByPlaceId(String placeId) {
        mGetSpeedLimit.execute(placeId);
    }

    private Long getSpeedLimit(GeoApiContext context, String placeId) throws Exception {
        Map<String, SpeedLimit> placeSpeeds = new HashMap<>();

        placeSpeeds.put(placeId, null);

        String uniquePlaceId = placeSpeeds.keySet().toString();

        SpeedLimit[] speedLimits = RoadsApi.speedLimits(context, placeId).await();

        placeSpeeds.put(speedLimits[0].placeId, speedLimits[0]);

        Log.d("My speeeed:", Long.toString(placeSpeeds.get(placeId).speedLimit));
        return placeSpeeds.get(placeId).speedLimit;
    }
}

