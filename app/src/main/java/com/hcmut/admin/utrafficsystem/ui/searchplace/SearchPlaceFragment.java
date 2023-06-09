package com.hcmut.admin.utrafficsystem.ui.searchplace;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.constant.MobileConstants;
import com.hcmut.admin.utrafficsystem.dto.InterFragmentDTO;
import com.hcmut.admin.utrafficsystem.ui.report.speech.SpeechReportFragment;
import com.hcmut.admin.utrafficsystem.ui.searchplace.result.PlaceAutoCompleteAdapter;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchResultCallback;
import com.hcmut.admin.utrafficsystem.ui.searchplace.result.SearchPlaceAdapter;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPlaceFragment extends Fragment implements
        MapActivity.OnBackPressCallback,
        SearchResultCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String CHOOSE_MAP_POINT = "CHOOSE_MAP_POINT";
    public static final String SEARCH_TYPE = "SEARCH_TYPE";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView txtSearchInput;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private RecyclerView rcSearchPlaceResult;
    private SearchPlaceAdapter searchPlaceAdapter;
    private ImageView imgBack;
    private TextView btnChooseMapPoint;
    private TextView btnChooseCurrentLocation;

    private LatLng selectedLatLngOnScreen;
    private boolean isHaveResult = false;

    private int searchType;

    public SearchPlaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPlaceFragment newInstance(String param1, String param2) {
        SearchPlaceFragment fragment = new SearchPlaceFragment();
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
            ((MapActivity) getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isHaveResult) {
            try {
                int type = getArguments().getInt(SearchPlaceResultHandler.SEARCH_TYPE, -1);
                SearchPlaceResultHandler
                        .getInstance()
                        .dispatchSearchPlaceResult(
                                SearchPlaceResultHandler.convertToSelectedPointSearchType(type),
                                null,
                                selectedLatLngOnScreen);
            } catch (Exception e) {}
            NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
        }
        isHaveResult = false;
        selectedLatLngOnScreen = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgBack = view.findViewById(R.id.imgBack);
        btnChooseMapPoint = view.findViewById(R.id.btnChooseMapPoint);
        btnChooseCurrentLocation = view.findViewById(R.id.btnChooseCurrentLocation);
        Bundle bundle = getArguments();
        if (bundle != null) {
            searchType = bundle.getInt(SearchPlaceResultHandler.SEARCH_TYPE);
            boolean isShowChooseMapPoint = bundle.getBoolean(CHOOSE_MAP_POINT, false);
            btnChooseMapPoint.setVisibility(isShowChooseMapPoint ? View.VISIBLE : View.GONE);
        }

        rcSearchPlaceResult = view.findViewById(R.id.rcSearchPlaceResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcSearchPlaceResult.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                view.getContext(), LinearLayoutManager.VERTICAL);
        rcSearchPlaceResult.addItemDecoration(dividerItemDecoration);
        searchPlaceAdapter = new SearchPlaceAdapter();
        rcSearchPlaceResult.setAdapter(searchPlaceAdapter);
        txtSearchInput = view.findViewById(R.id.search_edt);
        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(
                view.getContext(),
                Places.createClient(view.getContext()),
                searchPlaceAdapter);
        txtSearchInput.setAdapter(placeAutoCompleteAdapter);
        txtSearchInput.requestFocus();

        addEvents(view);
    }

    private void addEvents(final View view){
        searchPlaceAdapter.setOnItemClickedListener(new SearchPlaceAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(AutocompletePrediction itemData) {
                try {
                    int type = getArguments().getInt(SearchPlaceResultHandler.SEARCH_TYPE, -1);
                    SearchPlaceResultHandler.getInstance()
                            .dispatchSearchPlaceResult(type, itemData, null);
                } catch (Exception e) {}

                MapActivity.hideKeyboard(getActivity());
                NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity.hideKeyboard(getActivity());
                NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
            }
        });

        btnChooseMapPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity.hideKeyboard(getActivity());
                SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(SearchPlaceFragment.this);
                Bundle bundle = new Bundle();
                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_BEGIN_SEARCH);
                NavHostFragment.findNavController(SearchPlaceFragment.this)
                        .navigate(R.id.action_searchPlaceFragment_to_pickPointOnMapFragment, bundle);
            }
        });
        btnChooseCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                    SearchPlaceResultHandler.getInstance()
                                            .dispatchSearchPlaceResult(searchType , null, latlng);
                                    NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
    }

    @Override
    public void onSearchResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onBeginSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onEndSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onSelectedBeginSearchPlaceResultReady(LatLng result) {
        selectedLatLngOnScreen = result;
        isHaveResult = true;
    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {

    }
}
