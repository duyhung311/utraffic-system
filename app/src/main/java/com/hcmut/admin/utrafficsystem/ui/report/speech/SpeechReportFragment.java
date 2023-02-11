package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.constant.MobileConstants;
import com.hcmut.admin.utrafficsystem.model.User;
import com.hcmut.admin.utrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.SpeechReportBody;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.NearSegmentResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.SpeechReportResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private FloatingActionButton playBackAudio;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer myMediaPlayer;
    private boolean wasPlaying = false;
    private int totalTimeOfRecord = 0;
    private boolean isRecording; // true -> in recording, false -> not in recording mode
    File outputFile;
    private final APIService apiService;
    private final String temporarySpeechRecordId = (new ObjectId()).toString();

    private LatLng currLatLng;
    private LatLng nextLatLng;
    private LatLng bkLatLng = new LatLng(10.887792,106.8143736);
    private MarkerOptions marker = new MarkerOptions().icon(null);

    public SpeechReportFragment() {
        // Required empty public constructor
        apiService = RetrofitClient.getApiService();
    }

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
            this.isRecording = false;
            ((MapActivity) view.getContext()).hideBottomNav();
            this.record = view.findViewById(R.id.speechRecordButton);
            this.submit = view.findViewById(R.id.btnSubmitSpeechReport);

            this.seekBar = view.findViewById(R.id.playSpeechRecordSeekBar);
            this.seekBarTimeDisplay = view.findViewById(R.id.playSpeechRecordTime);
            this.totalTimeDisplay = view.findViewById(R.id.totalSpeechRecordTime);
            this.playBackAudio = view.findViewById(R.id.fabPlayingAudio);

            this.record.setEnabled(true);
            this.submit.setEnabled(true);

            this.mapView = view.findViewById(R.id.mapView);
            this.mapView.onCreate(savedInstanceState);
            this.mapView.onResume();
            this.mapView.getMapAsync(this);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //seekBarTimeDisplay.setVisibility(View.VISIBLE);
                    //myMediaPlayer.pause();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                    //seekBarTimeDisplay.setVisibility(View.VISIBLE);
                    int progressInSeconds = (int) Math.ceil(progress / 1000f);

                    if (progressInSeconds > 0 && Objects.nonNull(myMediaPlayer) && !myMediaPlayer.isPlaying()) {
                        clearMediaPlayer();
                        playBackAudio.setImageResource(android.R.drawable.ic_media_play);
                        seekBar.setProgress(0);
                        seekBarTimeDisplay.setText(convertTime(0));
                    }
                    else {
                        seekBarTimeDisplay.setText(convertTime(progress));
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (Objects.nonNull(myMediaPlayer) && myMediaPlayer.isPlaying()) {
                        myMediaPlayer.seekTo(seekBar.getProgress());
                        //myMediaPlayer.start();
                    }
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
                    if (isRecording) {
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
                callServerForEnhanceRecord(outputFile);
            }
        });

        //Playback audio
        this.playBackAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                if (outputFile != null)
                    startPlaying();
            }
        });
    }

    private void callServerForEnhanceRecord(File audioFile) {
        // hard code setting for api callServerForEnhanceRecord()
        List<Integer> listSegments = new ArrayList();
        listSegments.add(16);
        listSegments.add(17);
        SpeechReportBody speechReportBody =  new SpeechReportBody();
        speechReportBody.setSegments(listSegments);
        speechReportBody.setSpeechRecordId(temporarySpeechRecordId);
        speechReportBody.setRecord(audioFile);
        okhttp3.RequestBody requestFile = RequestBody.create(audioFile, MediaType.parse("multipart/form-data"));
        List<okhttp3.RequestBody> segments = new ArrayList<>();
        for(Integer segment : listSegments) {
            segments.add(RequestBody.create(segment.toString(), MediaType.parse("multipart/form-data")));
        }
        okhttp3.RequestBody speechRecordId = RequestBody.create(temporarySpeechRecordId, MediaType.parse("multipart/form-data"));
        String type = "rectangle";
        Double[][] coordinates = new Double[4][2];
        coordinates[0] = new Double[]{1.0, 2.0};
        coordinates[1] = new Double[]{3D, 4D};
        coordinates[2] = new Double[]{5D, 6D};
        coordinates[3] = new Double[]{7D, 8D};
        Integer activeTime = 301;
        Double radius = 10D;
        int option = 0;
        User user = SharedPrefUtils.getUser(getContext());
        try {
            byte[] file = FileUtils.readFileToByteArray(outputFile);
            String encodedAudioFile = Base64.encodeToString(file, 0);
            encodedAudioFile = encodedAudioFile.replaceAll(System.lineSeparator(), "");
            apiService.callServerForEnhanceRecord(
                            segments,
                            speechRecordId,
                            type,
                            coordinates,
                            activeTime,
                            radius,
                            option,
                            encodedAudioFile,
                            user
                    )
                    .enqueue(new Callback<SpeechReportResponse>() {
                        @Override
                        public void onResponse(Call<SpeechReportResponse> call, Response<SpeechReportResponse> response) {
                            Log.i(MobileConstants.INFO_TAGNAME, "Success");
                        }
                        @Override
                        public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                            Log.i(MobileConstants.INFO_TAGNAME, "Fail callServerForEnhanceRecord()");
                            t.printStackTrace();

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecord() throws IOException {
        clearAudioPlaybackUI();
        this.myAudioRecorder = new MediaRecorder();
        // .wav file setting
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setAudioChannels(1);
        myAudioRecorder.setAudioEncodingBitRate(128000);
        myAudioRecorder.setAudioSamplingRate(48000);

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PODCASTS) ;
        if (!path.exists()) {
            path.mkdirs();
        }
        outputFile = new File(path, "/recording" + ".wav" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myAudioRecorder.setOutputFile(outputFile);
        }
        myAudioRecorder.prepare();
        myAudioRecorder.start();
        isRecording = true;
        this.submit.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG/4).show();
    }

    private void stopRecord() {
        this.myAudioRecorder.stop();
        this.myAudioRecorder.reset();
        this.myAudioRecorder.release();
        isRecording = false;
        submit.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG/4).show();
        this.seekBar.setVisibility(View.VISIBLE);
        this.playBackAudio.setVisibility(View.VISIBLE);
    }

    private void startPlaying() {
        try {

//            if (myMediaPlayer != null && myMediaPlayer.getCurrentPosition() == myMediaPlayer.getDuration()) {
//                seekBar.setProgress(0);
//                seekBarTimeDisplay.setText(convertTime(0));
//                wasPlaying = false;
//                clearMediaPlayer();
//            }

            if (Objects.nonNull(myMediaPlayer) && myMediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                playBackAudio.setImageResource(android.R.drawable.ic_media_play);
            }

            if (!wasPlaying) {

                if (Objects.isNull(myMediaPlayer)) {
                    myMediaPlayer = new MediaPlayer();
                    myMediaPlayer.setDataSource(outputFile.getPath());
                    myMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
                    myMediaPlayer.setLooping(false);
                }


                myMediaPlayer.prepare();
                myMediaPlayer.start();
                playBackAudio.setImageResource(android.R.drawable.ic_media_pause);
                totalTimeOfRecord = myMediaPlayer.getDuration();
                seekBar.setMax(totalTimeOfRecord);
                totalTimeDisplay.setText(convertTime(totalTimeOfRecord));
                this.totalTimeDisplay.setVisibility(View.VISIBLE);
                this.seekBarTimeDisplay.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = myMediaPlayer.getCurrentPosition();
                        while (Objects.nonNull(myMediaPlayer) && myMediaPlayer.isPlaying() && currentPosition < totalTimeOfRecord) {
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
                        playBackAudio.setImageResource(android.R.drawable.ic_media_play);
                    }
                }).start();
            }
            wasPlaying = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertTime(int msec) {
        return String.format("%02d", (int)Math.floor(msec/60000)) + ":" + String.format("%02d", (int)Math.ceil(msec/1000));
    }

    private void clearAudioPlaybackUI() {
        seekBar.setVisibility(View.INVISIBLE);
        totalTimeDisplay.setVisibility(View.INVISIBLE);
        seekBarTimeDisplay.setVisibility(View.INVISIBLE);
        playBackAudio.setVisibility(View.INVISIBLE);
        seekBarTimeDisplay.setText(convertTime(0));
        seekBar.setProgress(0);
        if (Objects.nonNull(myMediaPlayer)) {
            clearMediaPlayer();
        }
    }

    private void clearMediaPlayer() {
        myMediaPlayer.stop();
        myMediaPlayer.release();
        myMediaPlayer = null;
        totalTimeOfRecord = 0;
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(SpeechReportFragment.this).popBackStack();
        clearMediaPlayer();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            gMap = googleMap;
            marker.position(bkLatLng);
            gMap.addMarker(marker);
            gMap.setMaxZoomPreference(16);
            gMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(bkLatLng, 16));
        }
    }


    private void getNearestSegment(LatLng latLng) {
        RetrofitClient.getApiService().getNearSegment(latLng.latitude, latLng.longitude)
                .enqueue(new Callback<BaseResponse<List<NearSegmentResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<NearSegmentResponse>>> call, Response<BaseResponse<List<NearSegmentResponse>>> response) {

                        if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                            List<NearSegmentResponse> nearSegmentResponses = response.body().getData();
                            try {
                                currLatLng = nearSegmentResponses.get(0).getStartLatLng();
                                nextLatLng = nearSegmentResponses.get(0).getEndLatLng();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<NearSegmentResponse>>> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAudioPlaybackUI();
        clearMediaPlayer();
    }
}