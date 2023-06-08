package com.hcmut.admin.utraffictest.ui.reportdetail.traffic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.ui.reportdetail.traffic.photo.PreViewPhotoActivity;
import com.hcmut.admin.utraffictest.ui.reportdetail.traffic.ratinglist.RatingAdapter;
import com.hcmut.admin.utraffictest.ui.viewReport.ViewReportFragment;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrafficReportDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrafficReportDetailFragment extends Fragment
        implements MapActivity.OnBackPressCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String SEGMENT_DATA = "segment_data";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private boolean isOnlyOne = true;
    private RatingAdapter ratingAdapter;
    private TextView tvVelocity;
    private ImageView imgBack;

    private ViewReportFragment.SegmentData currentSegmentData;

    public TrafficReportDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrafficReportDetailFragment newInstance(String param1, String param2) {
        TrafficReportDetailFragment fragment = new TrafficReportDetailFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traffic_report_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            currentSegmentData = (ViewReportFragment.SegmentData) getArguments().getSerializable(SEGMENT_DATA);
        } catch (Exception e) {
            currentSegmentData = new ViewReportFragment.SegmentData(-1, 0, "", 0);
        }

        addControls(view);
        addEvents();
        initReports();
    }

    private void addControls(View view) {
        tvVelocity = view.findViewById(R.id.tv_speed);
        recyclerView = view.findViewById(R.id.rvRating);
        imgBack = view.findViewById(R.id.imgBack);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ratingAdapter = new RatingAdapter(view.getContext());
        recyclerView.setAdapter(ratingAdapter);
    }

    private void addEvents() {
        ratingAdapter.setOnItemClickedListener(new RatingAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int id, View view, ReportResponse reportData) {
                switch (view.getId()) {
                    case R.id.btn_rating: {
                        showRatingDialog(reportData);
                        break;
                    }
                    case R.id.img_list: {
                        Intent intent = new Intent(getActivity(), PreViewPhotoActivity.class);
                        intent.putStringArrayListExtra("IMAGE", reportData.getImages());
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TrafficReportDetailFragment.this).popBackStack();
            }
        });
    }

    private void initReports() {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getContext(),
                "",
                getString(R.string.loading),
                true);
        RetrofitClient.getApiService().getReportOfTrafficStatus(
                currentSegmentData.createdDate,
                (int) currentSegmentData.segmentId)
                .enqueue(new Callback<BaseResponse<List<ReportResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<ReportResponse>>> call, final Response<BaseResponse<List<ReportResponse>>> response) {
                        Log.e("faf", response.toString());
                        progressDialog.dismiss();
                        if (response.body() != null) {
                            updateData(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<List<ReportResponse>>> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateData(List<ReportResponse> newDatas) {
        if (newDatas != null && newDatas.size() > 0) {
            ratingAdapter.setData(newDatas);
            tvVelocity.setText("Vận tốc trung bình: " + currentSegmentData.speed + " km/h");
        }
    }

    private void showRatingDialog(final ReportResponse reportResponse) {
        Activity activity = getActivity();
        if (activity instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) activity;
            mapActivity.setRatingDialogListener(new RatingDialogListener() {
                @Override
                public void onPositiveButtonClicked(int rate, @NotNull String comment) {
                    reportResponse.performRating(getContext(), rate);
                }

                @Override
                public void onNegativeButtonClicked() {

                }

                @Override
                public void onNeutralButtonClicked() {

                }
            });
            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Gửi")
                    .setNegativeButtonText("Để sau")
//                .setNeutralButtonText("Later")
                    .setNumberOfStars(5)
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(3)
                    .setTitle("Đánh giá")
                    .setDescription("Chọn và gửi phản hồi của bạn")
                    .setCommentInputEnabled(true)
//                .setDefaultComment("This app is pretty cool !")
                    .setStarColor(R.color.yellow)
//                .setNoteDescriptionTextColor(R.color.black)
                    .setTitleTextColor(R.color.black)
                    .setDescriptionTextColor(R.color.text_hint)
                    .setHint("Để lại bình luận của bạn tại đây...")
                    .setHintTextColor(R.color.text_hint)
                    .setCommentTextColor(R.color.black)
                    .setCommentBackgroundColor(R.color.bg_comment_rating)
                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(true)
                    .create(mapActivity)
                    .show();
        }
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(TrafficReportDetailFragment.this).popBackStack();
    }
}
