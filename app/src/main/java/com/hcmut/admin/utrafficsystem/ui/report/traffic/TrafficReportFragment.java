package com.hcmut.admin.utrafficsystem.ui.report.traffic;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.PhotoUploader;
import com.hcmut.admin.utrafficsystem.business.TrafficReportPhotoUploader;
import com.hcmut.admin.utrafficsystem.business.UserLocation;
import com.hcmut.admin.utrafficsystem.model.MarkerListener;
import com.hcmut.admin.utrafficsystem.business.ReportSendingHandler;
import com.hcmut.admin.utrafficsystem.business.SearchDirectionHandler;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.utrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.customview.SearchInputView;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.utrafficsystem.ui.searchplace.callback.SearchResultCallback;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrafficReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrafficReportFragment extends Fragment implements
        SearchResultCallback,
        MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int MAX_PHOTO_TOTAL = 4;
    public static final double REPORT_RADIUS_LIMIT = 500;   // in meters

    private AutocompletePrediction searchPlaceResult;
    private LatLng selectedMapPoint;
    private LatLng reportLatLng;
    private boolean isHaveSearchResult = false;
    private ReportSendingHandler reportSendingHandler;

    private TextView btnYourLocation;
    private TextView btnChooseOnMap;
    private SearchInputView searchInputView;

    private EditText txtNote;
    private SeekBar sbSpeed;
    private TextView txtSpeed;
    private TextView txtAddImage;
    private LinearLayout llImageContainer;
    private TextView txtReview;
    private Spinner snReason;

    private List<String> images;
    private List<Bitmap> imageBitmaps = new ArrayList<>();
    private PhotoUploader photoUploader;
    private ProgressDialog progressDialog;
    private Circle circleReportLimit;
    private AppCompatButton btnToggleRender;

    private GoogleMap map;

    public TrafficReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportSendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrafficReportFragment newInstance(String param1, String param2) {
        TrafficReportFragment fragment = new TrafficReportFragment();
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
        if (isHaveSearchResult) {
            handleSearchResult();
        } else {
            try {
                MapActivity mapActivity = ((MapActivity) getContext());
                mapActivity.hideBottomNav();
                updateRenderStatusOptionBackground(mapActivity.isRenderStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchInputView.updateView();
        }
    }

    private void handleSearchResult() {
        if (searchPlaceResult != null) {
            String addressString = searchPlaceResult.getSecondaryText(null).toString();
            LatLng latLng = SearchDirectionHandler.addressStringToLatLng(getContext(), addressString);
            if (latLng != null) {
                searchInputView.setTxtSearchInputText(addressString);
                setReportLocation(latLng);
            } else {
                Toast.makeText(getContext(),
                        "Không thể lấy vị trí, vui lòng thử lại",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (selectedMapPoint != null) {
            searchInputView.setTxtSearchInputText("Ghim vị trí");
            setReportLocation(selectedMapPoint);
        }

        selectedMapPoint = null;
        searchPlaceResult = null;
        isHaveSearchResult = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traffic_report, container, false);
    }

    @Override
    public void onDetach() {
        try {
            Objects.requireNonNull((MapActivity) getContext()).unRegisterCameraPhotoHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCircleReportLimit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnToggleRender = view.findViewById(R.id.btnToggleRender);
        btnYourLocation = view.findViewById(R.id.btnYourLocation);
        btnChooseOnMap = view.findViewById(R.id.btnChooseOnMap);
        searchInputView = view.findViewById(R.id.searchInputView);
        reportSendingHandler = new ReportSendingHandler(view.getContext(),
                (ConstraintLayout) view.findViewById(R.id.clCollectLocation),
                (ConstraintLayout) view.findViewById(R.id.clCollectReportData));

        sbSpeed = view.findViewById(R.id.sbSpeed);
        txtNote = view.findViewById(R.id.txtNote);
        txtSpeed = view.findViewById(R.id.txtSpeed);
        txtAddImage = view.findViewById(R.id.txtAddImage);
        llImageContainer = view.findViewById(R.id.llImageContainer);
        txtReview = view.findViewById(R.id.txtReview);
        snReason = view.findViewById(R.id.snReason);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.reason_spinner_item,
                ReportRequest.reasons);
        snReason.setAdapter(adapter);
        initOptionDataView();

        addEvents(view);
    }

    private void addEvents(View view) {
        photoUploader = new TrafficReportPhotoUploader(640, 960, new PhotoUploader.PhotoUploadCallback() {
            @Override
            public void onPreUpload() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                progressDialog = (getActivity() == null) ? null :
                        ProgressDialog.show(getActivity(), "", "Đang tải ảnh lên", true);
            }

            @Override
            public void onUpLoaded(Bitmap bitmap, String url) {
                if (bitmap != null) {
                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(params);
                    llImageContainer.addView(imageView);
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    images.add(url);
                    imageBitmaps.add(bitmap);
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onUpLoadFail() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(),
                        "Không thể tải ảnh lên, vui lòng thử lại",
                        Toast.LENGTH_SHORT).show();
            }
        });
        final Context context = view.getContext();
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    drawCircleReportLimit(map, REPORT_RADIUS_LIMIT);
                }
            });
            mapActivity.setMarkerListener(new MarkerListener() {
                @Override
                public void onClick(Marker marker) {
                    reportSendingHandler.onArrowMarkerClickedNotConfirm(map, marker);
                }
            });
            mapActivity.registerCameraPhotoHandler(photoUploader);
        }
        searchInputView.setTxtSearchInputEvent(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    reportSendingHandler.clear();
                    SearchPlaceResultHandler.getInstance()
                            .addSearchPlaceResultListener(TrafficReportFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.NORMAL_SEARCH);
                    NavHostFragment.findNavController(TrafficReportFragment.this)
                            .navigate(R.id.action_reportSendingFragment_to_searchPlaceFragment2, bundle);
                }
            }
        });
        searchInputView.setImgClearTextEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearReport();
            }
        });
        searchInputView.setImgBackEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearReport();
                NavHostFragment.findNavController(TrafficReportFragment.this).popBackStack();
            }
        }, true);
        btnYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    reportSendingHandler.clear();
                                    if (location != null) {
                                        setReportLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                                        searchInputView.setTxtSearchInputText("Vị trí của bạn");
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Không thể lấy vị trí, vui lòng thử lại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        btnChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportSendingHandler.clear();
                SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(TrafficReportFragment.this);
                Bundle bundle = new Bundle();
                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_BEGIN_SEARCH);
                NavHostFragment.findNavController(TrafficReportFragment.this)
                        .navigate(R.id.action_reportSendingFragment_to_pickPointOnMapFragment2, bundle);
            }
        });

        sbSpeed.setMax(ReportRequest.MAX_VELOCITY);
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
                txtSpeed.setText(String.valueOf(progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        txtAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images == null || images.size() < MAX_PHOTO_TOTAL) {
                    photoUploader.collectPhoto(getActivity());
                } else {
                    Toast.makeText(getContext(),
                            "Bạn chỉ có thể thêm tối đa " + MAX_PHOTO_TOTAL + " hình ảnh",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MapActivity.androidExt.confirm(context,
                        "Thông báo",
                        "Bạn có muốn gửi báo cáo này?",
                        new ClickDialogListener.Yes() {
                            @Override
                            public void onCLickYes() {
                                reportSendingHandler.sendReport(
                                        TrafficReportFragment.this,
                                        searchInputView.getSearchInputText(),
                                        sbSpeed.getProgress(),
                                        Arrays.asList(snReason.getSelectedItem().toString()),
                                        txtNote.getText().toString(),
                                        images);
                            }
                        });
            }
        });



//        CODE Cũ
//
//        txtReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                reportSendingHandler.reviewReport(
//                        TrafficReportFragment.this,
//                        searchInputView.getSearchInputText(),
//                        sbSpeed.getProgress(),
//                        Arrays.asList(snReason.getSelectedItem().toString()),
//                        txtNote.getText().toString(),
//                        images);
//            }
//        });

        btnToggleRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = (MapActivity) getContext();
                boolean toggleValue = !mapActivity.isRenderStatus();
                mapActivity.setTrafficEnable(toggleValue);
                updateRenderStatusOptionBackground(toggleValue);
            }
        });
    }

    private void updateRenderStatusOptionBackground(boolean isEnable) {
        if (isEnable) {
            btnToggleRender.setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.bg_button_active));
        } else {
            btnToggleRender.setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.gray_bg_custom));
        }
    }

    public void drawCircleReportLimit(GoogleMap map, double radius) {
        removeCircleReportLimit();
        LatLng center = UserLocation.parseLatLng(LocationCollectionManager.getInstance(getContext()).getLastUserLocation());
        if (center != null) {
            circleReportLimit = map.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(8));
        }
    }

    public void removeCircleReportLimit() {
        if (circleReportLimit != null) {
            circleReportLimit.remove();
        }
    }

    public boolean isInCircleReportLimit(@NonNull LatLng point) {
        UserLocation userLocation = LocationCollectionManager.getInstance(getContext()).getLastUserLocation();
        UserLocation selectedLocation = new UserLocation(point);
        return userLocation != null
                && userLocation.distanceTo(selectedLocation) <= REPORT_RADIUS_LIMIT;
    }

    public void clearReport() {
        searchInputView.handleBackAndClearView(false);
        reportSendingHandler.clear();
        initOptionDataView();
    }

    private void initOptionDataView() {
        sbSpeed.setProgress(20);
        txtSpeed.setText(String.valueOf(20));
        snReason.setSelection(0);
        llImageContainer.removeAllViews();
        images = null;
        txtNote.setText(null);

        for (Bitmap bitmap : imageBitmaps) {
            try {
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Have location to report
     * hide location collection view
     * show option data collection view
     * @param latLng
     */
    private void setReportLocation(LatLng latLng) {
        reportLatLng = latLng;
        if (isInCircleReportLimit(reportLatLng)) {
            searchInputView.handleBackAndClearView(true);
            reportSendingHandler.handleReportStepByStep(getActivity(), map, latLng);
        } else {
            MapActivity.androidExt.showMessageNoAction(getContext(), "Thông báo", "Điểm bạn chọn không nằm trong bán kính cho phép, vui lòng chọn lại điểm nằm trong đường tròn giới hạn");
        }
    }

    @Override
    public void onSearchResultReady(AutocompletePrediction result) {
        searchPlaceResult = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onBeginSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onEndSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onSelectedBeginSearchPlaceResultReady(LatLng result) {
        selectedMapPoint = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(TrafficReportFragment.this).popBackStack();
    }
}
