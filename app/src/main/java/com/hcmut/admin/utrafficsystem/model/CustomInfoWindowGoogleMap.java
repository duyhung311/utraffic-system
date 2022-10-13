package com.hcmut.admin.utrafficsystem.model;

/**
 * Created by Admin on 12/2/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcmut.admin.utrafficsystem.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.map_custom_infowindow, null);

        //TextView title = view.findViewById(R.id.title);

        TextView segmentSpeedTxv = view.findViewById(R.id.segment_speed);
        TextView trafficLevel = view.findViewById(R.id.traffic_level);
        ImageView statusImg = view.findViewById(R.id.status_bar);
        TextView roadName = view.findViewById(R.id.road_name);
        TextView roadSpeed = view.findViewById(R.id.road_speed);

        TrafficStatusWindowData infoWindowData = (TrafficStatusWindowData) marker.getTag();
        if (infoWindowData != null) {
            trafficLevel.setText(Integer.toString(infoWindowData.getSegmentStatus()));
            roadName.setText(infoWindowData.getRoadName());
            roadSpeed.setText(Integer.toString(infoWindowData.getRoadSpeed()));

            //set color for color status img
            switch (infoWindowData.getSegmentStatus()) {
                case 1: {
                    int color = context.getResources().getColor(R.color.green);
                    statusImg.setColorFilter(color);
                    break;
                }
                case 2: {
                    int color = context.getResources().getColor(R.color.light_green);
                    statusImg.setColorFilter(color);
                    break;
                }
                case 3: {
                    int color = context.getResources().getColor(R.color.yellow);
                    statusImg.setColorFilter(color);
                    break;
                }
                case 4: {
                    int color = context.getResources().getColor(R.color.strong_yellow);
                    statusImg.setColorFilter(color);
                    break;
                }
                case 5: {
                    int color = context.getResources().getColor(R.color.orange);
                    statusImg.setColorFilter(color);
                    break;
                }
                case 6: {
                    int color = context.getResources().getColor(R.color.red);
                    statusImg.setColorFilter(color);
                    break;
                }
            }
        }

        return view;
    }


}