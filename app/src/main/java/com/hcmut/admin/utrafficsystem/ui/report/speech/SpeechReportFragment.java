package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.google.android.gms.maps.MapView;

import java.io.IOException;
import com.google.android.gms.maps.MapView;

import java.io.IOException;

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
    private boolean recordButtonStatus; // true -> in recording, false -> not in recording mode
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer counter = 0;
    String outputFile;
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
            this.recordButtonStatus = false;
            ((MapActivity) view.getContext()).hideBottomNav();
            this.record = view.findViewById(R.id.speechRecordButton);
            this.submit = view.findViewById(R.id.btnSubmitSpeechReport);

            this.record.setEnabled(true);
            this.submit.setEnabled(true);

            this.mapView = view.findViewById(R.id.mapView);
            this.mapView.onCreate(savedInstanceState);
            this.mapView.onResume();
            this.mapView.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {
        this.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (recordButtonStatus) {
                        stopRecord();
                    } else {
                        startRecord();
                    }
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        });
    }

    private void startRecord() throws IOException {
        outputFile = Environment.getExternalStorageDirectory().getCanonicalPath() + "/recording_" + (counter++).toString() + ".3gp";
        this.myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.prepare();
        myAudioRecorder.start();
        recordButtonStatus = true;
        this.submit.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG/4).show();
    }

    private void stopRecord() {
        this.myAudioRecorder.stop();
        this.myAudioRecorder.reset();
        this.myAudioRecorder.release();
        recordButtonStatus = false;
        System.out.println("stop recording");
        submit.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG/4).show();
        //File file = new File();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
        }
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