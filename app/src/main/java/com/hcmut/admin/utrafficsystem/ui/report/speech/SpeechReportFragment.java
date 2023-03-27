package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
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
    private int totalTimeOfRecord = 0;
    public static AndroidExt androidExt;
    private boolean isRecording; // true -> in recording, false -> not in recording mode
    private boolean newRecord; // true -> new record that is not init by media player yet
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
                if (outputFile != null) {
                    play();
                }
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
                            // androidExt.showMessageNoAction(getContext(), "Thông báo", "Gửi báo cáo thành công!");
                        }
                        @Override
                        public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                            // androidExt.showMessageNoAction(getContext(), "Thông báo", "Không thể gửi báo cáo. Vui lòng thử lại.");
                            Log.i(MobileConstants.INFO_TAGNAME, "Fail callServerForEnhanceRecord()");
                            t.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecord() throws IOException {
        clearAudioPlaybackComponents();

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

        // Animate recording button
        isRecording = true;
        Animation btnShake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake); // for recording button
        record.startAnimation(btnShake);

        myAudioRecorder.prepare();
        myAudioRecorder.start();
        this.submit.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Bắt đầu ghi âm", Toast.LENGTH_LONG/4).show();
    }

    private void stopRecord() {
        this.myAudioRecorder.stop();
        this.myAudioRecorder.reset();
        this.myAudioRecorder.release();
        isRecording = false;
        submit.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Kết thúc ghi âm", Toast.LENGTH_LONG/4).show();

        // Stop animate recording button
        record.clearAnimation();

        // Get duration of the audio record
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(outputFile.getPath());
        String recordDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        totalTimeOfRecord = Integer.parseInt(recordDuration);
        totalTimeDisplay.setText(convertTime(totalTimeOfRecord));
        seekBar.setMax(totalTimeOfRecord);

        // Set visibility to playback components
        seekBar.setVisibility(View.VISIBLE);
        playBackAudio.setVisibility(View.VISIBLE);
        totalTimeDisplay.setVisibility(View.VISIBLE);
        seekBarTimeDisplay.setVisibility(View.VISIBLE);
        newRecord = true;
    }

    private void initMusicPlayer() {
        if (!Objects.isNull(myMediaPlayer) && myMediaPlayer.isPlaying()) {
            myMediaPlayer.stop();
            myMediaPlayer.reset();
        }

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(outputFile.getPath()));
        playBackAudio.setImageResource(android.R.drawable.ic_media_play);

        myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                playBackAudio.setImageResource(android.R.drawable.ic_media_pause);
                mediaPlayer.start();
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playBackAudio.setImageResource(android.R.drawable.ic_media_play);
                seekBar.setProgress(totalTimeOfRecord);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTimeDisplay.setText(convertTime(progress));
                if (fromUser) {
                    myMediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Objects.nonNull(myMediaPlayer)) {
                    try {
                        if (Objects.nonNull(myMediaPlayer) && myMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = myMediaPlayer.getCurrentPosition();
                            handlerForSeekBar.sendMessage(message);
                            Thread.sleep(50);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerForSeekBar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            seekBar.setProgress(msg.what);
        }
    };

    private void play() {
        if (newRecord) {
            newRecord = false;
            initMusicPlayer();
        }
        else if (Objects.nonNull(myMediaPlayer) && myMediaPlayer.isPlaying()) {
            myMediaPlayer.pause();
            playBackAudio.setImageResource(android.R.drawable.ic_media_play);
        }
        else if (Objects.nonNull(myMediaPlayer)) {
            myMediaPlayer.start();
            playBackAudio.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private String convertTime(int msec) {
        double sec = Math.ceil((float)msec / 1000);
        int minutes = (int)Math.floor((sec%3600)/60);
        int seconds = (int)(sec % 60);
        return String.format("%02d", minutes)+ ":" + String.format("%02d", seconds);
    }

    private void clearAudioPlaybackComponents() {
        seekBar.setVisibility(View.INVISIBLE);
        totalTimeDisplay.setVisibility(View.INVISIBLE);
        seekBarTimeDisplay.setVisibility(View.INVISIBLE);
        playBackAudio.setVisibility(View.INVISIBLE);
        seekBarTimeDisplay.setText(convertTime(0));
        seekBar.setProgress(0);
        if (Objects.nonNull(myMediaPlayer))
            myMediaPlayer.reset();
    }

    private void clearMediaPlayer() {
        if (Objects.nonNull(myMediaPlayer)) {
            if (myMediaPlayer.isPlaying())
                myMediaPlayer.stop();
            myMediaPlayer.reset();
            myMediaPlayer.release();
            myMediaPlayer = null;
            totalTimeOfRecord = 0;
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
        clearAudioPlaybackComponents();
        clearMediaPlayer();
    }
}