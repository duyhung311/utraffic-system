package com.hcmut.admin.utrafficsystem.ui.report.speech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.google.android.gms.maps.MapView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeechReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeechReportFragment extends Fragment implements MapActivity.OnBackPressCallback, OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MapView mapView;
    private GoogleMap gMap;
    private ImageButton record;
    private Button submit;
    private MediaRecorder myAudioRecorder;
    private SupportMapFragment mapFragment;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpeechReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeechReport.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeechReportFragment newInstance(String param1, String param2) {
        SpeechReportFragment fragment = new SpeechReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speech_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view, savedInstanceState);
        addEvents();
    }

    private void addControls(View view, Bundle savedInstanceState) {
        try {
            ((MapActivity) view.getContext()).hideBottomNav();
            record = view.findViewById(R.id.speechRecordButton);
            submit = view.findViewById(R.id.btnSubmitSpeechReport);
            mapView = view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {

    }

    private void record() {

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(SpeechReportFragment.this).popBackStack();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            gMap = googleMap;
        }
    }

}