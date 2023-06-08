package com.hcmut.admin.utraffictest.ui.viewReport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.business.ViewReportHandler;
import com.hcmut.admin.utraffictest.util.LocationCollectionManager;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.ui.reportdetail.traffic.TrafficReportDetailFragment;
import com.hcmut.admin.utraffictest.util.MapUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String REPORT_RATING = "report_rating";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GoogleMap map;
    private Button btnViewDetail;
    private TextView txtSpeed;
    private TextView txtStatusColor;
    private Button btnRefresh;
    private ConstraintLayout clReview;
    private ImageView imgBack;

    private ViewReportHandler viewReportHandler;
    private ArrayList<Marker> userReportMarker = new ArrayList<>();
    private SegmentData selectedSegment;
    private AppCompatButton btnToggleRender;

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            if (REPORT_RATING.equals(marker.getTag())) {
                View v = getLayoutInflater().inflate(R.layout.layout_marker_info_tile, null);
                TextView tvInfo = v.findViewById(R.id.tvInfo);
                String [] temp = marker.getTitle().split("/");
                try {
                    tvInfo.setText(temp[1] + " km/h");
                } catch (Exception e) {}
                return v;
            }
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };

    public ViewReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportView.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewReportFragment newInstance(String param1, String param2) {
        ViewReportFragment fragment = new ViewReportFragment();
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
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_report, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewReportHandler = new ViewReportHandler(context);

        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
            mapActivity.setUserReportMarkerListener(new OnReportMakerClick() {
                @Override
                public void onClick(Marker marker) {
                    String[] datas = marker.getTitle().split("/");
                    showSelectedUserReport(datas);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }

    private void addControls(View view) {
        btnViewDetail = view.findViewById(R.id.btnViewDetail);
        txtSpeed = view.findViewById(R.id.tv_speed);
        clReview = view.findViewById(R.id.clReview);
        txtStatusColor = view.findViewById(R.id.txtStatusColor);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        imgBack = view.findViewById(R.id.imgBack);
        btnToggleRender = view.findViewById(R.id.btnToggleRender);

        clearSelectedSegment();
    }

    private void addEvents(View view) {
        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSegment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TrafficReportDetailFragment.SEGMENT_DATA, selectedSegment);
                    NavHostFragment.findNavController(ViewReportFragment.this)
                            .navigate(R.id.action_viewReportFragment_to_ratingFragment2, bundle);
                }
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshUserReport();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSelectedSegment();
                removeReportStatus();
                updateUI();
            }
        });
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
            btnToggleRender.setBackground(requireContext().getDrawable(R.drawable.bg_button_active));
        } else {
            btnToggleRender.setBackground(requireContext().getDrawable(R.drawable.gray_bg_custom));
        }
    }

    private void showSelectedUserReport(String [] datas) {
        try {
            setSelectedSegment(new SegmentData(Long.parseLong(datas[0]), Integer.parseInt(datas[1]), datas[2], Long.parseLong(datas[3])));
            txtSpeed.setText("Vận tốc trung bình: " + datas[1] + "km/h");
            txtStatusColor.setBackgroundColor(Color.parseColor(datas[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedSegment(SegmentData selectedSegment) {
        this.selectedSegment = selectedSegment;
        if (!btnViewDetail.isEnabled()) {
            btnViewDetail.setEnabled(true);
        }
    }

    private void clearSelectedSegment() {
        selectedSegment = null;
        if (btnViewDetail.isEnabled()) {
            btnViewDetail.setEnabled(false);
        }
        txtStatusColor.setBackgroundColor(Color.parseColor("#e9e9ef"));
    }

    private void refreshUserReport() {
        clearSelectedSegment();
        removeReportStatus();
        if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
            final ProgressDialog progressDialog = ProgressDialog.show(
                    getContext(),
                    "",
                    getString(R.string.loading),
                    true);
            LocationCollectionManager.getInstance(getContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                loadUserReport(new LatLng(location.getLatitude(), location.getLongitude()),
                                        progressDialog);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),
                                        "Không thể lấy vị trí, vui lòng thử lại",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isHaveReportData() {
        return userReportMarker.size() > 0;
    }

    private void updateUI() {
        if (isHaveReportData()) {
            if (imgBack.getVisibility() != View.VISIBLE) {
                imgBack.setVisibility(View.VISIBLE);
                hideBottomNav();
            }
        } else {
            if (imgBack.getVisibility() == View.VISIBLE) {
                imgBack.setVisibility(View.GONE);
                showBottomNav();
            }
        }

        try {
            MapActivity mapActivity = (MapActivity) getContext();
            updateRenderStatusOptionBackground(mapActivity.isRenderStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideBottomNav() {
        Activity activity = getActivity();
        if (activity instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) activity;
            mapActivity.hideBottomNav();
            clReview.setPadding(0, 10, 0, 10 + mapActivity.getBottomNavHeight());
        }
    }

    private void showBottomNav() {
        Activity activity = getActivity();
        if (activity instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) activity;
            mapActivity.showBottomNav();
            clReview.setPadding(0, 10, 0, 10);
        }
    }

    private void loadUserReport(final LatLng location, final ProgressDialog progressDialog) {
        viewReportHandler.getUserReportStatus(location.latitude, location.longitude, new ViewReportHandler.SegmentResultCallback() {
            @Override
            public void onSuccess(List<MarkerOptions> markerOptionsList) {
                Marker marker;
                for (MarkerOptions markerOptions : markerOptionsList) {
                    marker = map.addMarker(markerOptions);
                    userReportMarker.add(marker);
                    marker.setTag(REPORT_RATING);
                }
                progressDialog.dismiss();
                updateUI();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            }

            @Override
            public void onHaveNotResult() {
                progressDialog.dismiss();
                updateUI();
                Toast.makeText(getContext(), "Không có dữ liệu báo cáo tại thời điểm này", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFaile() {
                progressDialog.dismiss();
                updateUI();
                Toast.makeText(getContext(), "Lỗi, vui lòng kiểm tra lại đường truyền hoặc máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeReportStatus() {
        if (userReportMarker != null) {
            for (Marker marker : userReportMarker) {
                marker.remove();
            }
            userReportMarker.clear();
        }
    }

    public interface OnReportMakerClick {
        void onClick(Marker marker);
    }

    public static class SegmentData implements Serializable {
        private static final int offset = 15000;

        public final int speed;
        public final String color;
        public final long segmentId;
        public final long createdDate;

        public SegmentData(long segmentId, int speed, String color, long createdDate) {
            this.speed = speed;
            this.color = color;
            this.segmentId = segmentId;
            this.createdDate = createdDate - offset;
        }
    }
}
