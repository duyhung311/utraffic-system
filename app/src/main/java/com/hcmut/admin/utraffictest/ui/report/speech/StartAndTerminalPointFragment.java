package com.hcmut.admin.utraffictest.ui.report.speech;

import android.app.Activity;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.business.trafficmodule.TrafficRenderModule;
import com.hcmut.admin.utraffictest.constant.MobileConstants;
import com.hcmut.admin.utraffictest.dto.InterFragmentDTO;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.utraffictest.util.LocationCollectionManager;

import java.util.Objects;

public class StartAndTerminalPointFragment<MainActivity> extends Fragment implements MapActivity.OnBackPressCallback {
    private MarkerOptions markerOptions = new MarkerOptions().icon(null);
    private Marker marker;
    private GoogleMap map;
    private static GoogleMap backUpGMap;
    private LatLng startLatLng;

    private TextView btnOk;
    private TextView middlePoint;
    private TextView textView;
    private ImageView imgBack;

    public StartAndTerminalPointFragment(){
    }

    public static StartAndTerminalPointFragment newInstance(String param1, String param2) {
        StartAndTerminalPointFragment fragment = new StartAndTerminalPointFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_point_on_map, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle bundle = this.getArguments();
            InterFragmentDTO latlng = (InterFragmentDTO) Objects.requireNonNull(bundle).getSerializable(MobileConstants.ITEM);
            startLatLng = (LatLng) latlng.getMap().get(MobileConstants.LATLNG);
            //isChooseBoth = Objects.isNull(startLatLng);
        } catch (Exception ex) {

        }

    }

    private void addEvents() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGetPointOnMap();
            }
        });

        middlePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControls(view);
        addEvents();
        MapActivity mapActivity = (MapActivity) view.getContext();
        backUpGMap = mapActivity.getGoogleMap();
        this.map = mapActivity.getGoogleMap();

        if (Objects.nonNull(startLatLng)) {
            markerOptions.position(startLatLng);
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(startLatLng, 16));
            markerOptions.position(startLatLng);
            marker = map.addMarker(markerOptions);
        } else {
            LocationCollectionManager.getInstance(getContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(currentLocation, 16));
                        }
                    });
        }
        map.setMaxZoomPreference(TrafficRenderModule.MAX_ZOOM_LEVEL);

    }

    public void addControls(View view ) {
        this.btnOk = view.findViewById(R.id.btnOk);
        this.textView = view.findViewById(R.id.textView);
        if (Objects.nonNull(startLatLng)) {
            this.textView.append(MobileConstants.TERMINAL_POINT);
        } else {
            this.textView.append(MobileConstants.START_POINT);

        }
        this.imgBack = view.findViewById(R.id.imgBack);
        this.middlePoint = view.findViewById(R.id.middlePoint);
//        this.btnToggleRender = view.findViewById(R.id.btnToggleRender);
    }

    @Override
    public void onBackPress() {
        if (Objects.nonNull(marker)) {
            marker.remove();
        }
        NavHostFragment.findNavController(StartAndTerminalPointFragment.this).popBackStack();
    }

    private void handleGetPointOnMap() {
        int [] screenCoord = new int[2];
        Activity activity = getActivity();
        if (activity != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenCoord[0] = displayMetrics.widthPixels / 2;
            screenCoord[1] = displayMetrics.heightPixels / 2;
        } else {
            middlePoint.getLocationOnScreen(screenCoord);
        }
        try {
            LatLng location = map.getProjection().fromScreenLocation(
                    new Point(screenCoord[0], screenCoord[1]));

            int type = getArguments().getInt(SearchPlaceResultHandler.SEARCH_TYPE, -1);

            SearchPlaceResultHandler.getInstance().dispatchSearchPlaceResult(type, null, location);
            onBackPress();

//            if (Objects.nonNull(startLatLng)) { // your location
//                NavHostFragment.findNavController(StartAndTerminalPointFragment.this).popBackStack();
//            } else {
//                Bundle bundle = new Bundle();
//                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_END_SEARCH);
//                InterFragmentDTO dto = new InterFragmentDTO();
//                dto.getMap().put(MobileConstants.LATLNG, startLatLng);
//                bundle.putSerializable(MobileConstants.ITEM, dto);
//                NavHostFragment.findNavController(StartAndTerminalPointFragment.this)
//                        .navigate(R.id.action_speechReportFragment_to_pickOnMapFragmentSpeechReport, bundle);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof MapActivity) {
//            MapActivity mapActivity = (MapActivity) context;
//            this.map = mapActivity.getGoogleMap();
//            if (Objects.nonNull(startLatLng)) {
//                markerOptions.position(startLatLng);
//                map.moveCamera(CameraUpdateFactory
//                        .newLatLngZoom(startLatLng, 16));
//                markerOptions.position(startLatLng);
//                map.addMarker(markerOptions);
//            } else {
//                LocationCollectionManager.getInstance(getContext())
//                    .getCurrentLocation(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                            map.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(currentLocation, 16));
//                        }
//                    });
//            }
//            map.setMaxZoomPreference(TrafficRenderModule.MAX_ZOOM_LEVEL);
//        }
//    }

}
