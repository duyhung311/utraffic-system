package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeechReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeechReportFragment<MainActivity> extends Fragment implements MapActivity.OnBackPressCallback, OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    private MapView mapView;
    private GoogleMap gMap;
    private ImageButton record;
    private Button submit;
    private SeekBar seekBar;
    private TextView seekBarTimeDisplay;
    private TextView totalTimeDisplay;
    private ImageButton playBackAudio;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer myMediaPlayer;
    private SupportMapFragment mapFragment;
    private boolean recordButtonStatus; // true -> in recording, false -> not in recording mode
    private Integer counter = 0;
    String outputFile;
    String dolbyInputBucketUrl;
    String dolbyEnhanceAudioJobId;
    Boolean sendAudioStatus = false;

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

            this.seekBar = view.findViewById(R.id.playSpeechRecordSeekBar);
            this.seekBarTimeDisplay = view.findViewById(R.id.playSpeechRecordTime);
            this.totalTimeDisplay = view.findViewById(R.id.totalSpeechRecordTime);
            this.playBackAudio = view.findViewById(R.id.playSpeechRecordButton);

            this.record.setEnabled(true);
            this.submit.setEnabled(true);

            this.mapView = view.findViewById(R.id.mapView);
            this.mapView.onCreate(savedInstanceState);
            this.mapView.onResume();
            this.mapView.getMapAsync(this);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                    seekBarTimeDisplay.setVisibility(View.VISIBLE);
                    myMediaPlayer.seekTo(progress);
                    seekBarTimeDisplay.setText(convertTime(progress));
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {

        // Record Audio
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

        // Send audio to server
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //Playback audio
        this.playBackAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                if (outputFile != null)
                    //seekBar.setProgress(0);
                    startPlaying();
            }
        });
    }

    private void startRecord() throws IOException {
        outputFile = Environment.getExternalStorageDirectory().getCanonicalPath() + "/recording_" + (counter++).toString() + ".mp3";
        this.myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
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

        this.seekBar.setVisibility(View.VISIBLE);
        this.totalTimeDisplay.setVisibility(View.VISIBLE);
        this.seekBarTimeDisplay.setVisibility(View.VISIBLE);
        this.playBackAudio.setVisibility(View.VISIBLE);

        getMediaPlayer();
        int totalTime = myMediaPlayer.getDuration();
        this.seekBar.setMax(totalTime);
        this.totalTimeDisplay.setText(convertTime(totalTime));
    }

    private String convertTime(int msec) {
        return String.format("%02d", (int)Math.floor(msec/60000)) + ":" + String.format("%02d", (int)Math.ceil(msec/1000));
    }

    private void getMediaPlayer() {
        myMediaPlayer = new MediaPlayer();
        try {
            myMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            myMediaPlayer.setDataSource(outputFile);
            myMediaPlayer.prepare();
            myMediaPlayer.setLooping(false);
            myMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPlaying() {
        getMediaPlayer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                int total = myMediaPlayer.getDuration();
                while (myMediaPlayer != null && myMediaPlayer.isPlaying() && currentPosition < total) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    seekBar.setProgress(currentPosition);
                }
            }
        }).start();
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