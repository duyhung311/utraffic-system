package com.hcmut.admin.utraffictest.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.MarkerListener;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.utraffictest.repository.remote.model.response.NearSegmentResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.ui.report.traffic.TrafficReportFragment;
import com.hcmut.admin.utraffictest.util.ClickDialogListener;
import com.hcmut.admin.utraffictest.util.MapUtil;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportSendingHandler {
    private Context context;
    private GeoApiContext geoApiContext = new GeoApiContext()
            .setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");

    private Marker arrowMarker;
    private MarkerCreating reportMarker;
    private Handler handler = new Handler(Looper.getMainLooper());

    private LatLng currLatLng;
    private LatLng nextLatLng;

    private ConstraintLayout clCollectLocation;
    private ConstraintLayout clCollectReportData;
    private ProgressDialog progressDialog;

    public ReportSendingHandler(Context context,
                                ConstraintLayout clCollectLocation,
                                ConstraintLayout clCollectReportData) {
        this.context = context;
        this.clCollectLocation = clCollectLocation;
        this.clCollectReportData = clCollectReportData;
    }

    public void handleReportStepByStep(Activity activity, final GoogleMap map, final LatLng latLng) {
        progressDialog = (activity == null) ? null :
                ProgressDialog.show(activity, "", context.getString(R.string.loading), true);
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                LatLng optimizeLatLng = optimizeReportPosition(latLng);
                if (optimizeLatLng != null) {
                    showReportMarkerAndOrientation(map, optimizeLatLng);
                } else {
                    showReportMarkerAndOrientation(map, latLng);
                }
            }
        });
    }

    private void showReportMarkerAndOrientation(final GoogleMap map, final LatLng optimizeLatLng) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(optimizeLatLng, 17));
                reportMarker = new MarkerCreating(optimizeLatLng);
                reportMarker.createMarker(
                        context, map, R.drawable.ic_position_rating, false, false);
                showOrientation(map, optimizeLatLng);
            }
        });
    }

    public void onArrowMarkerClickedNotConfirm(GoogleMap map, Marker marker) {
        if (Objects.requireNonNull(marker.getTag()).equals(MarkerListener.REPORT_ARROW)) {
            LatLng temp = currLatLng;
            currLatLng = nextLatLng;
            nextLatLng = temp;
            if (arrowMarker != null) {
                arrowMarker.remove();
            }
            drawOrientationArrow(map, reportMarker.getLocation(), nextLatLng);
        }
    }

    public void onArrowMarkerClicked(Context context, final GoogleMap map, Marker marker) {
        if (Objects.requireNonNull(marker.getTag()).equals(MarkerListener.REPORT_ARROW)) {
            MapActivity.androidExt.comfirm(context,
                    "Tùy chọn",
                    "Bạn có muốn thay đổi hướng đi?",
                    new ClickDialogListener.Yes() {
                @Override
                public void onCLickYes() {
                    LatLng temp = currLatLng;
                    currLatLng = nextLatLng;
                    nextLatLng = temp;
                    if (arrowMarker != null) {
                        arrowMarker.remove();
                    }
                    drawOrientationArrow(map, reportMarker.getLocation(), nextLatLng);
                }
            });
        }
    }

    public void sendReport(TrafficReportFragment fragment, String address, int velocity, List<String> causes, String note, List<String> images) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setCurrentLatLng(currLatLng);
        reportRequest.setNextLatLng(nextLatLng);
        reportRequest.setVelocity(velocity);
        reportRequest.setCauses(causes);
        reportRequest.setDescription(note);
        reportRequest.setImages(images);
        reportRequest.setAddress(address);

        if (reportRequest.checkValidData(context)) {
            reportRequest.sendReport(fragment);
        }
    }

    /**
     *
     * @param velocity
     * @param causes
     * @param note
     * @param images
     *
     */
    @Deprecated
    public void reviewReport(TrafficReportFragment fragment, String address, int velocity, List<String> causes, String note, List<String> images) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setCurrentLatLng(currLatLng);
        reportRequest.setNextLatLng(nextLatLng);
        reportRequest.setVelocity(velocity);
        reportRequest.setCauses(causes);
        reportRequest.setDescription(note);
        reportRequest.setImages(images);
        reportRequest.setAddress(address);

        if (reportRequest.checkValidData(context)) {
            showReportInfoDialog(fragment, reportRequest);
        }
    }

    private void showOrientation(final GoogleMap map, final LatLng latLng) {
        RetrofitClient.getApiService().getNearSegment(latLng.latitude, latLng.longitude)
                .enqueue(new Callback<BaseResponse<List<NearSegmentResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<NearSegmentResponse>>> call, Response<BaseResponse<List<NearSegmentResponse>>> response) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                            List<NearSegmentResponse> nearSegmentResponses = response.body().getData();
                            try {
                                currLatLng = nearSegmentResponses.get(0).getStartLatLng();
                                nextLatLng = nearSegmentResponses.get(0).getEndLatLng();
                                drawOrientationArrow(map, latLng, nextLatLng);
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (context instanceof MapActivity) {
                            MapActivity.androidExt.showErrorDialog((MapActivity) context, "Điểm đã chọn không thuộc con đường hoặc" +
                                    " đoạn đường chưa được hổ trợ, vui lòng thử lại");
                        } else {
                            Toast.makeText(context, "Điểm đã chọn không thuộc con đường hoặc" +
                                    " đoạn đường chưa được hổ trợ, vui lòng thử lại", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }

                    @Override
                    public void onFailure(Call<BaseResponse<List<NearSegmentResponse>>> call, Throwable t) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(context, "Không thể xác định hướng di chuyển, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void drawOrientationArrow(GoogleMap map, LatLng start, LatLng end) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 240, 180, false);
            double bearing = MapUtil.GetBearing(start, end);
            float cameraRotation = map.getCameraPosition().bearing;
            float rotation = (float) bearing + 90 - cameraRotation;
            arrowMarker = map.addMarker(new MarkerOptions()
                    .position(start)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .anchor(0.9f, 0.5f)
                    .alpha(3)
                    .rotation(rotation)
                    .title("Chọn vào mũi tên để thay đổi chiều")
            );
            arrowMarker.showInfoWindow();
            arrowMarker.setTag(MarkerListener.REPORT_ARROW);
            clCollectLocation.setVisibility(View.GONE);
            clCollectReportData.setVisibility(View.VISIBLE);
            bitmap.recycle();
            smallMarker.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        if (arrowMarker != null) {
            arrowMarker.remove();
        }
        if (reportMarker != null) {
            reportMarker.removeMarker();
        }
        clCollectLocation.setVisibility(View.VISIBLE);
        clCollectReportData.setVisibility(View.GONE);
        currLatLng = null;
        nextLatLng = null;
    }

    private LatLng optimizeReportPosition(LatLng latLng) {
        return MapUtil.snapToRoad(geoApiContext, latLng);
    }

    private void showReportInfoDialog(final TrafficReportFragment fragment, final ReportRequest reportRequest) {
        Activity activity = fragment.getActivity();
        if (activity == null) return;

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View reportInfoDialogView = LayoutInflater
                .from(activity.getApplicationContext())
                .inflate(R.layout.layout_report_info_review, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(reportInfoDialogView);
        final AlertDialog dialog = builder.create();

        TextView txtLocation = reportInfoDialogView.findViewById(R.id.txtLocation);
        TextView txtSpeed = reportInfoDialogView.findViewById(R.id.txtSpeed);
        TextView txtReason = reportInfoDialogView.findViewById(R.id.txtReason);
        TextView txtNote = reportInfoDialogView.findViewById(R.id.txtNote);
        TextView txtSendReport = reportInfoDialogView.findViewById(R.id.txtSendReport);

        txtLocation.setText("Vị trí: " + reportRequest.getAddress());
        txtSpeed.setText("Vận tốc: " + reportRequest.getVelocity() + " km/h");
        try {
            txtReason.setText("Nguyên nhân: " + reportRequest.getCauses().get(0));
        } catch (Exception e) {
            txtReason.setText("Nguyên nhân: /");
        }
        txtNote.setText(reportRequest.getDescription() == null ? "/" : reportRequest.getDescription());
        txtSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportRequest.sendReport(fragment);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
