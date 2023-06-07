package com.hcmut.admin.utrafficsystem.ui.direction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.MarkerCreating;
import com.hcmut.admin.utrafficsystem.business.SearchDirectionHandler;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.Coord;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectResponse;
import com.hcmut.admin.utrafficsystem.service.AppForegroundService;
import com.hcmut.admin.utrafficsystem.ui.tbt.TbtActivity;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.ui.searchplace.SearchPlaceFragment;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchResultCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectionFragment extends Fragment
        implements SearchResultCallback,
        MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String DESTINATION_ADDRESS = "destination_address";
    public static final String DESTINATION_LATLNG = "destination_latlng";
    public static final String CURRENT_ADDRESS = "current_address";
    public static final String CURRENT_LATLNG = "current_latlng";

    private AppCompatButton btnToggleRender;
    private AppCompatButton btnStartTbt;
    private AutoCompleteTextView txtBeginAddress;
    private AutoCompleteTextView txtEndAddress;
    private AppCompatImageButton btnBack;
    private Button btnDistance;
    private Button btnTime;
    private boolean isTimeDirectionSelected = true;

    private AutocompletePrediction beginSearchPlaceResult;
    private AutocompletePrediction endSearchPlaceResult;
    private LatLng beginSelectedPoint;
    private LatLng endSelectedPoint;
    private boolean isHaveSearchResult = false;
    private LatLng startPoint = null;
    private LatLng endPoint = null;

    private MarkerCreating beginMarkerCreating;
    private MarkerCreating endMarkerCreating;
    private MarkerCreating directInfoMarker;    // for time and distance show
    private GoogleMap map;
    private List<Polyline> directPolylines = new ArrayList<>();

    public DirectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectionFragment newInstance(String param1, String param2) {
        DirectionFragment fragment = new DirectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MapActivity mapActivity = ((MapActivity) getContext());
            mapActivity.hideBottomNav();
            updateRenderStatusOptionBackground(mapActivity.isRenderStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTimeAndDistanceView();
        if (isHaveSearchResult) {
            handleSearchResult();
        }
    }

    private void handleSearchResult() {
        String temp;
        if (beginSearchPlaceResult != null) {
            temp = beginSearchPlaceResult.getSecondaryText(null).toString();
            txtBeginAddress.setText(temp);
            startPoint = SearchDirectionHandler.addressStringToLatLng(getContext(), temp);
        }
        if (endSearchPlaceResult != null) {
            temp = endSearchPlaceResult.getSecondaryText(null).toString();
            txtEndAddress.setText(temp);
            endPoint = SearchDirectionHandler.addressStringToLatLng(getContext(), temp);
        }
        if (beginSelectedPoint != null) {
            txtBeginAddress.setText("Ghim vị trí");
            startPoint = beginSelectedPoint;
        }
        if (endSelectedPoint != null) {
            txtEndAddress.setText("Ghim vị trí");
            endPoint = endSelectedPoint;
        }
        performDirection(startPoint, endPoint);
    }

    private void performDirection(LatLng startPoint, LatLng endPoint) {
        if (startPoint != null && endPoint != null) {
            SearchDirectionHandler.direct(
                    getContext(),
                    startPoint,
                    endPoint,
                    isTimeDirectionSelected,
                    new SearchDirectionHandler.DirectResultCallback() {
                        @Override
                        public void onSuccess(DirectResponse directResponse) {
                            renderDirection(directResponse);
                        }

                        @Override
                        public void onFail() {
                            try {
                                Toast.makeText(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }

                        @Override
                        public void onHaveNoData() {
                            try {
                                Toast.makeText(getContext(), "Đoạn đường không được hỗ trợ hoặc" +
                                        " Không thể tìm thấy đường đến đó, vui lòng thử lại!", Toast.LENGTH_LONG)
                                        .show();
                            } catch (Exception e) {}
                        }
                    });
        }
        isHaveSearchResult = false;
        beginSearchPlaceResult = null;
        endSearchPlaceResult = null;
        beginSelectedPoint = null;
        endSelectedPoint = null;
    }

    private boolean isCouldDirect() {
        return beginSearchPlaceResult != null && endSearchPlaceResult != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBeginAddress = view.findViewById(R.id.txtBeginAddress);
        txtEndAddress = view.findViewById(R.id.txtEndAddress);
        btnBack = view.findViewById(R.id.btnBack);

        btnDistance = view.findViewById(R.id.btnDistance);
        btnTime = view.findViewById(R.id.btnTime);
        btnToggleRender = view.findViewById(R.id.btnToggleRender);
        btnStartTbt = view.findViewById(R.id.btnStartTbt);

        if(getArguments()!=null){
            txtBeginAddress.setText(getArguments().getString(CURRENT_ADDRESS));
            txtEndAddress.setText(getArguments().getString(DESTINATION_ADDRESS));
            startPoint = getArguments().getParcelable(CURRENT_LATLNG);
            endPoint = getArguments().getParcelable(DESTINATION_LATLNG);
            onDistanceButtonClick();
        }

        addEvents();
    }

    private void addEvents() {
        txtBeginAddress.setOnFocusChangeListener((view, b) -> {
            if (b) {
                navigateToSearchFragment(SearchPlaceResultHandler.BEGIN_SEARCH);
            }
        });
        txtEndAddress.setOnFocusChangeListener((view, b) -> {
            if (b) {
                navigateToSearchFragment(SearchPlaceResultHandler.END_SEARCH);
            }
        });
        btnBack.setOnClickListener(view -> {
            AppForegroundService.path_id = null;
            removeMarker();
            removeDirect();
            NavHostFragment.findNavController(DirectionFragment.this).popBackStack();
        });
        btnDistance.setOnClickListener(view -> onDistanceButtonClick());
        btnTime.setOnClickListener(view -> onTimeButtonClick());

        btnToggleRender.setOnClickListener(v -> {
            MapActivity mapActivity = (MapActivity) getContext();
            boolean toggleValue = !mapActivity.isRenderStatus();
            mapActivity.setTrafficEnable(toggleValue);
            updateRenderStatusOptionBackground(toggleValue);
        });

        btnStartTbt.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TbtActivity.class);
            intent.putExtra("startPoint", startPoint);
            intent.putExtra("endPoint", endPoint);
            intent.putExtra("isTimeDirectionSelected", isTimeDirectionSelected);
            startActivity(intent);
        });
    }

    private void updateRenderStatusOptionBackground(boolean isEnable) {
        if (isEnable) {
            btnToggleRender.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.bg_button_active));
        } else {
            btnToggleRender.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.gray_bg_custom));
        }
    }

    private void onTimeButtonClick() {
        if(!isTimeDirectionSelected) {
            isTimeDirectionSelected = true;
            updateTimeAndDistanceView();
            performDirection(startPoint, endPoint);
        }
    }

    private void onDistanceButtonClick() {
        if (isTimeDirectionSelected) {
            isTimeDirectionSelected = false;
            updateTimeAndDistanceView();
            performDirection(startPoint, endPoint);
        }
    }

    private void updateTimeAndDistanceView() {
        Context context = getContext();
        if (context != null) {
            if (isTimeDirectionSelected) {
                btnTime.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.bg_button_active));
                btnTime.setTextColor(Color.BLUE);
                btnDistance.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.gray_bg_custom));
                btnDistance.setTextColor(Color.BLACK);
            } else {
                btnDistance.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.bg_button_active));
                btnDistance.setTextColor(Color.BLUE);
                btnTime.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.gray_bg_custom));
                btnTime.setTextColor(Color.BLACK);
            }
        }
    }

    private void navigateToSearchFragment(int type) {
        SearchPlaceResultHandler.getInstance()
                .addSearchPlaceResultListener(DirectionFragment.this);
        Bundle bundle = new Bundle();
        bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, type);
        bundle.putBoolean(SearchPlaceFragment.CHOOSE_MAP_POINT, true);
        NavHostFragment.findNavController(DirectionFragment.this)
                .navigate(R.id.action_directionFragment_to_searchPlaceFragment, bundle);
    }

    private void createMarker(LatLng beginLatLng, LatLng endLatLng, LatLng directInfoLatLng, String directInfoTitle) {
        removeMarker();
        beginMarkerCreating = new MarkerCreating(beginLatLng);
        endMarkerCreating = new MarkerCreating(endLatLng);
        directInfoMarker = new MarkerCreating(directInfoLatLng);
        beginMarkerCreating.createMarker(getContext(), map, R.drawable.ic_start_location_marker, false, false);
        endMarkerCreating.createMarker(getContext(), map, R.drawable.ic_stop_location_marker, false, false);
        directInfoMarker.createMarker(getContext(), map, R.drawable.ic_dot, false, false, directInfoTitle);
    }

    @SuppressLint("DefaultLocale")
    private void renderDirection(DirectResponse directResponse) {
        List<Coord> directs = directResponse.getCoords();
        AppForegroundService.path_id = directResponse.getPathId();

        // render direct to map
        LatLng beginLatLng = new LatLng(directs.get(0).getLat(), directs.get(0).getLng());
        LatLng endLatLng = new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng());
        LatLng directInfoLatLng = new LatLng(directs.get(directs.size() / 2).getLat(), directs.get(directs.size() / 2).getLng());
        String directInfoTitle = String.format("%d phút (%.1f km)", (int) directResponse.getTime(), (directResponse.getDistance()/1000f));
        createMarker(beginLatLng, endLatLng, directInfoLatLng, directInfoTitle);
        removeDirect();
        LatLng start;
        LatLng end;
        String color;
        for (int i = 0; i < directs.size(); i++) {
            try {
                start = new LatLng(directs.get(i).getLat(), directs.get(i).getLng());
                end = new LatLng(directs.get(i).geteLat(), directs.get(i).geteLng());
                color = directs.get(i).getStatus().color;
                directPolylines.add(map.addPolyline(
                        new PolylineOptions().add(
                                start,
                                end
                        ).width(10).geodesic(true)
                                .clickable(true)
                                .color(Color.parseColor(color))
                ));
            } catch (Exception e) {}
        }
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(directs.get(0).getLat(), directs.get(0).getLng()))
                .include(new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng()))
                .build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        map.animateCamera(cu);
    }

    private void removeDirect() {
        if (directPolylines != null) {
            for (Polyline polyline : directPolylines) {
                polyline.remove();
            }
            directPolylines.clear();
        }
    }

    private void removeMarker() {
        if (beginMarkerCreating != null) {
            beginMarkerCreating.removeMarker();
        }
        if (endMarkerCreating != null) {
            endMarkerCreating.removeMarker();
        }
        if (directInfoMarker != null) {
            directInfoMarker.removeMarker();
        }
    }

    @Override
    public void onSearchResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onBeginSearchPlaceResultReady(AutocompletePrediction result) {
        beginSearchPlaceResult = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onEndSearchPlaceResultReady(AutocompletePrediction result) {
        endSearchPlaceResult = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onSelectedBeginSearchPlaceResultReady(LatLng result) {
        beginSelectedPoint = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {
        endSelectedPoint = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(DirectionFragment.this).popBackStack();
    }
}
