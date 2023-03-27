package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.MarkerCreating;
import com.hcmut.admin.utrafficsystem.business.SearchDirectionHandler;
import com.hcmut.admin.utrafficsystem.constant.MobileConstants;
import com.hcmut.admin.utrafficsystem.dto.InterFragmentDTO;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.model.User;
import com.hcmut.admin.utrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.SpeechReportBody;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.Coord;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectRespose;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.NearSegmentResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.SpeechReportResponse;
import com.hcmut.admin.utrafficsystem.service.AppForegroundService;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchResultCallback;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;
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
 * Use the {@link SpeechReportFragment} factory method toSearchResultCallback
 * create an instance of this fragment.
 */
public class SpeechReportFragment<MainActivity> extends Fragment implements MapActivity.OnBackPressCallback, OnMapReadyCallback, SearchResultCallback  {

    // TODO: Rename parameter arguments, choose names that match
    private MapView mapView;
    private GoogleMap gMap;
    private ImageButton record;
    private Button submit;
    private SeekBar seekBar;
    private TextView seekBarTimeDisplay;
    private TextView totalTimeDisplay;
    private TextView btnYourLocation;
    private FloatingActionButton playBackAudio;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer myMediaPlayer;
    private int totalTimeOfRecord = 0;
    public static AndroidExt androidExt;
    private com.hcmut.admin.utrafficsystem.customview.NonGestureConstraintLayout speechReportContainer;
    private boolean isRecording; // true -> in recording, false -> not in recording mode
    private boolean newRecord; // true -> new record that is not init by media player yet
    private File outputFile;
    private final APIService apiService;
    private final String temporarySpeechRecordId = (new ObjectId()).toString();
    private LatLng pickOnMapStartLatLng;
    private LatLng pickOnMapEndLatLng;
    private TextView btnChooseOnMap;
    private List<Polyline> directPolylines = new ArrayList<>();
    private MarkerCreating beginMarkerCreating;
    private MarkerCreating endMarkerCreating;
    private MarkerCreating directInfoMarker;
    public SpeechReportFragment() {
        apiService = RetrofitClient.getApiService();
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
        addEvents(view);
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
            this.btnYourLocation = view.findViewById(R.id.btnYourLocation);
            this.btnChooseOnMap =  view.findViewById(R.id.btnChooseOnMap);
            this.playBackAudio = view.findViewById(R.id.fabPlayingAudio);
            this.record.setEnabled(true);
            this.submit.setEnabled(true);
            this.mapView = view.findViewById(R.id.mapView);
            this.mapView.onCreate(savedInstanceState);
            this.mapView.onResume();
            this.mapView.getMapAsync((OnMapReadyCallback) this);
            this.speechReportContainer =  view.findViewById(R.id.speechReportContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents(View view) {
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
                if (Objects.nonNull(pickOnMapStartLatLng) == Objects.nonNull(pickOnMapEndLatLng)) {
                    callServerForEnhanceRecord(outputFile);
                } // else: thong bao
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

        btnYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickOnMapEndLatLng = null;
                pickOnMapStartLatLng = null;
                speechReportContainer.animate().translationY(speechReportContainer.getHeight());
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                        .getCurrentLocation(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                pickOnMapStartLatLng = latlng;
                                Bundle bundle = new Bundle();
                                SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(SpeechReportFragment.this);
                                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_END_SEARCH);
                                InterFragmentDTO dto = new InterFragmentDTO();
                                dto.getMap().put(MobileConstants.LATLNG, latlng);
                                bundle.putSerializable(MobileConstants.ITEM, dto);
                                NavHostFragment.findNavController(SpeechReportFragment.this)
                                        .navigate(R.id.action_speechReportFragment_to_pickOnMapFragmentSpeechReport, bundle);
                            }
                        });
                }
            }
        });

        btnChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickOnMapEndLatLng = null;
                pickOnMapStartLatLng = null;
                speechReportContainer.animate().translationY(speechReportContainer.getHeight());
                Bundle bundle = new Bundle();
                SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(SpeechReportFragment.this);
                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_BEGIN_SEARCH);
                NavHostFragment.findNavController(SpeechReportFragment.this)
                        .navigate(R.id.action_speechReportFragment_to_pickOnMapFragmentSpeechReport, bundle);
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
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
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
        System.out.println("c@ll onMapReady()");
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            LocationCollectionManager.getInstance(getContext())
                .getCurrentLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        gMap = googleMap;
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        gMap.setMaxZoomPreference(24);
                        gMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(currentLatLng, 16));
                        if (Objects.nonNull(pickOnMapStartLatLng) && Objects.nonNull(pickOnMapEndLatLng)) {
                            performDirection(pickOnMapStartLatLng, pickOnMapEndLatLng);
                        }
                    }
                });
        }

    }

    @Override
    public void onResume() {
        System.out.println("c@ll onResume()");
        super.onResume();
        if (Objects.nonNull(pickOnMapStartLatLng) && Objects.isNull(pickOnMapEndLatLng)) {
            Bundle bundle = new Bundle();
            SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(SpeechReportFragment.this);
            bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_END_SEARCH);
            InterFragmentDTO dto = new InterFragmentDTO();
            dto.getMap().put(MobileConstants.LATLNG, pickOnMapStartLatLng);
            bundle.putSerializable(MobileConstants.ITEM, dto);
            NavHostFragment.findNavController(SpeechReportFragment.this)
                    .navigate(R.id.action_speechReportFragment_to_pickOnMapFragmentSpeechReport, bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        System.out.println("c@ll onAttach()");

        super.onAttach(context);
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    gMap = googleMap;
                    LocationCollectionManager.getInstance(getContext())
                        .getCurrentLocation(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                gMap = googleMap;
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                gMap.setMaxZoomPreference(24);
                                gMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(currentLatLng, 16));
                            }
                        });

                }
            });
        }
    }

    private void performDirection(LatLng startPoint, LatLng endPoint) {
        if (startPoint != null && endPoint != null) {
            SearchDirectionHandler.direct(getContext(), startPoint, endPoint, Boolean.FALSE,
                    new SearchDirectionHandler.DirectResultCallback() {
                        @Override
                        public void onSuccess(DirectRespose directRespose) {
                            renderDirection(directRespose);
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
        pickOnMapEndLatLng = null;
        pickOnMapStartLatLng = null;
    }

    @SuppressLint("DefaultLocale")
    private void renderDirection(DirectRespose directRespose) {
        List<Coord> directs = directRespose.getCoords();
        AppForegroundService.path_id = directRespose.getPathId();

        // render direct to map
        LatLng beginLatLng = new LatLng(directs.get(0).getLat(), directs.get(0).getLng());
        LatLng endLatLng = new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng());
        LatLng directInfoLatLng = new LatLng(directs.get(directs.size() / 2).getLat(), directs.get(directs.size() / 2).getLng());
        String directInfoTitle = String.format("%d phút (%.1f km)", (int) directRespose.getTime(), (directRespose.getDistance()/1000f));
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
                directPolylines.add(gMap.addPolyline(
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
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 36);
        gMap.animateCamera(cu);
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

    private void createMarker(LatLng beginLatLng, LatLng endLatLng, LatLng directInfoLatLng, String directInfoTitle) {
        removeMarker();
        beginMarkerCreating = new MarkerCreating(beginLatLng);
        endMarkerCreating = new MarkerCreating(endLatLng);
        directInfoMarker = new MarkerCreating(directInfoLatLng);
        beginMarkerCreating.createMarker(getContext(), gMap, R.drawable.ic_start_location_marker, false, false);
        endMarkerCreating.createMarker(getContext(), gMap, R.drawable.ic_stop_location_marker, false, false);
        directInfoMarker.createMarker(getContext(), gMap, R.drawable.ic_dot, false, false, directInfoTitle);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAudioPlaybackComponents();
        clearMediaPlayer();
        removeDirect();
        removeMarker();
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
        pickOnMapStartLatLng = result;
    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {
        pickOnMapEndLatLng = result;
    }

}