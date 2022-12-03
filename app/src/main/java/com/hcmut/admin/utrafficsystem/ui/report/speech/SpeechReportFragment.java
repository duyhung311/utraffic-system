package com.hcmut.admin.utrafficsystem.ui.report.speech;

import static com.facebook.FacebookSdk.getApplicationContext;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hcmut.admin.utrafficsystem.util.GiftUtil.androidExt;

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
import com.google.gson.JsonObject;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.Content;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.SpeechReportBody;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.SpeechReportRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.SpeechReportResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.google.android.gms.maps.MapView;

import java.io.File;
import java.io.IOException;
import com.google.android.gms.maps.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

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
    String dolbyInputBucketUrl;
    String dolbyEnhanceAudioJobId;
    Boolean sendAudioStatus = false;

    private String serverUrl = "http://localhost:3000/api/report/speech-report";
    private String temporarySpeechRecordId = "6386b8b0f0113e7528486509";
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

    /*
        1. @POST Create Input Bucket on Dolby
        Receives a Url as a link to the input bucket
     */
    private void createInputBucket(String objectId) {
        String inputUrl = "dlb://".concat(objectId);
        SpeechReportRequest speechReportRequest = new SpeechReportRequest(inputUrl);
        RetrofitClient.getAPIDolby()
                .createDolbyInputBucket(speechReportRequest)
                .enqueue(new Callback<SpeechReportResponse>() {
                    @Override
                    public void onResponse(Call<SpeechReportResponse> call, Response<SpeechReportResponse> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String inputBucketUrl = response.body().getUrl();
                            dolbyInputBucketUrl = inputBucketUrl;
                            System.out.println("Dolby Input Bucket " + inputBucketUrl.length());
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    /*
        2. @PUT Send audio file to Input Bucket
     */
    private void uploadAudioFileToDolby(String url, File audioFile) {
        SpeechReportRequest speechReportRequest = new SpeechReportRequest(audioFile);
        RetrofitClient.getAPIDolby().uploadAudioFileToDolby("audio/mpeg", url, speechReportRequest)
                .enqueue(new Callback<SpeechReportResponse>() {
                    @Override
                    public void onResponse(Call<SpeechReportResponse> call, Response<SpeechReportResponse> response) {
                        if (response.code() == 200) {
                            System.out.println("Upload Audio File Successfully");
                        } else {
                            sendAudioStatus = false;
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                        sendAudioStatus = false;
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    /*
        Generate a random string with size @n
     */
    static String getRandomString(int n)
    {
        String AlphaNumericString = "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder randomString = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            randomString.append(AlphaNumericString.charAt(index));
        }
        return randomString.toString();
    }

    /*
        3. Initialize Enhance Audio on Dolby
     */
    private void initEnhanceAudioDolby(String objectId) throws JSONException {
        Content content = new Content("voice_recording");
        String inputUrl = "dlb://".concat(objectId);
        String outputUrl = "dlb://enhanced".concat(objectId);
        SpeechReportRequest speechReportRequest = new SpeechReportRequest(
                content,
                inputUrl,
                outputUrl
        );
        RetrofitClient.getAPIDolby().initEnhanceAudioDolby(speechReportRequest)
                .enqueue(new Callback<SpeechReportResponse>() {
                    @Override
                    public void onResponse(Call<SpeechReportResponse> call, Response<SpeechReportResponse> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String jobId = response.body().getJobId();
                            dolbyEnhanceAudioJobId = jobId;
                            System.out.println("Dolby Job Id " + jobId);
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    private void triggerServer(File audioFile) {
        List<Integer> listSegments = new ArrayList();
        listSegments.add(16);
        listSegments.add(17);
        SpeechReportBody speechReportBody =  new SpeechReportBody();
        speechReportBody.setSegments(listSegments);
        speechReportBody.setSpeechRecordId(temporarySpeechRecordId);
        speechReportBody.setRecord(audioFile);
        RetrofitClient.getApiService().triggerServer(speechReportBody)
                .enqueue(new Callback<SpeechReportResponse>() {
                    @Override
                    public void onResponse(Call<SpeechReportResponse> call, Response<SpeechReportResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<SpeechReportResponse> call, Throwable t) {
                        //androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
    private void addEvents() {
        /*
            Record Audio
         */
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
        /*
            Send audio to server
         */
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAudioStatus = (outputFile != null);
                String objectId = "6386c6e13c7ef65be8e0778d";
                        // getRandomString(32);
                if (outputFile != null)
                    createInputBucket(objectId);

                if (dolbyInputBucketUrl != null) {
                    File audioFile = new File(outputFile);
                    uploadAudioFileToDolby(dolbyInputBucketUrl, audioFile);
                    triggerServer(audioFile);
                }

                try {
                    initEnhanceAudioDolby(objectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendAudioStatus = (dolbyEnhanceAudioJobId != null);
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