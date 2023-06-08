package com.hcmut.admin.utraffictest.repository.remote.model.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utraffictest.business.UserLocation;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.service.AppForegroundService;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.ui.report.traffic.TrafficReportFragment;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRequest {
    public static final int MAX_VELOCITY = 80;
    public static final String [] reasons = {
            "Tắc đường", "Ngập lụt", "Có vật cản", "Tai nạn", "Công an", "Đường cấm"
    };

    private int velocity;
    private Double currentLat = null;
    private Double currentLng = null;
    private Double nextLat = null;
    private Double nextLng = null;
    private List<String> causes;
    private String description;
    private List<String> images;
    private String type = "user";
    private String token;
    private String active;
    private String path_id;
    private String is_update_current_location;
    private String address;

    @NonNull
    @Override
    public String toString() {
        return "type: " + type + "; active: " + active + "; path_id: "+ path_id + "; is_update: " + is_update_current_location;
    }

    public ReportRequest() {
        if (velocity < 1) {
            velocity = 1;
        }
    }

    /**
     * Contructor for post GPS data
     * @param prevLocation
     * @param currentLocation
     */
    public ReportRequest(@NotNull UserLocation prevLocation, @NotNull UserLocation currentLocation) {
        this.currentLat = prevLocation.getLatitude();
        this.currentLng = prevLocation.getLongitude();
        this.nextLat = currentLocation.getLatitude();
        this.nextLng = currentLocation.getLongitude();
        this.velocity = Math.round(UserLocation.calculateSpeed(prevLocation, currentLocation));
        this.type = "system";
        if (velocity < 1) {
            velocity = 1;
        }
    }

    public ReportRequest(int velocity, double currentLat, double currentLng, double nextLat, double nextLng, List<String> causes, String description, List<String> images) {
        this.velocity = velocity;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.nextLat = nextLat;
        this.nextLng = nextLng;
        this.causes = causes;
        this.description = description;
        this.images = images;
        if (this.velocity < 1) {
            this.velocity = 1;
        }
    }

    /**
     * Model for stop notification
     * @param userLocation
     */
    private ReportRequest(@NotNull UserLocation userLocation, Context context) {
        this.currentLat = userLocation.getLatitude();
        this.currentLng = userLocation.getLongitude();
        this.nextLat = userLocation.getLatitude();
        this.nextLng = userLocation.getLongitude();
        this.velocity = 50;
        this.type = "system";
        token = SharedPrefUtils.getNotiToken(context);
        active = "false";
        path_id = AppForegroundService.path_id;
        is_update_current_location = "true";
    }

    public static ReportRequest getStopNotificationModel(@NotNull UserLocation userLocation, Context context) {
        return new ReportRequest(userLocation, context);
    }

    public static ReportRequest getStartReportNotificationModel(@NotNull UserLocation userLocation, Context context) {
        ReportRequest reportRequest = new ReportRequest(userLocation, context);
        reportRequest.active = "true";
        reportRequest.path_id = null;
        return  reportRequest;
    }

    public void checkUpdateCurrentLocation(Context context) {
        if (AppForegroundService.isUpdateCurrentLocation()) {
            token = SharedPrefUtils.getNotiToken(context);
            active = "true";
            path_id = AppForegroundService.path_id;
            is_update_current_location = "true";
        } else {
            is_update_current_location = "false";
        }
    }

    public boolean checkValidData(Context context) {
        if (currentLat == null || currentLng == null || nextLat == null || nextLng == null) {
            Toast.makeText(context,
                    "Vị trí cảnh báo chưa được chọn, vui lòng thử lại",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (velocity < 0 || velocity > MAX_VELOCITY) {
            Toast.makeText(context,
                    "Vận tốc phải lớn hơn 0 và nhỏ hơn " + MAX_VELOCITY,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void sendReport(final TrafficReportFragment fragment) {
        final Activity activity = fragment.getActivity();
        if (activity == null) return;

        final ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Đang xử lý..!", true);
        RetrofitClient.getApiService().postTrafficReport(MapActivity.currentUser.getAccessToken(), this)
                .enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200 && response.body() != null && response.body().getCode() == 200) {
                            Log.e("ggg", response.body().getMessage());
                            MapActivity.androidExt.showSuccess(activity, "Gửi cảnh báo thành công");
                            fragment.clearReport();
                        } else {
                            MapActivity.androidExt.showErrorDialog(activity, "Gửi cảnh báo thất bại, vui lòng thử lại");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        progressDialog.dismiss();
                        MapActivity.androidExt.showErrorDialog(activity, "Gửi cảnh báo thất bại, vui lòng thử lại");
                    }
                });
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        if (velocity > 0) {
            this.velocity = velocity;
        } else {
            this.velocity = 1;
        }
    }

    public void setCurrentLatLng(LatLng latLng) {
        if (latLng != null) {
            currentLat = latLng.latitude;
            currentLng = latLng.longitude;
        }
    }

    public void setNextLatLng(LatLng latLng) {
        if (latLng != null) {
            nextLat = latLng.latitude;
            nextLng = latLng.longitude;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getNextLat() {
        return nextLat;
    }

    public void setNextLat(double nextLat) {
        this.nextLat = nextLat;
    }

    public double getNextLng() {
        return nextLng;
    }

    public void setNextLng(double nextLng) {
        this.nextLng = nextLng;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPath_id() {
        return path_id;
    }

    public void setPath_id(String path_id) {
        this.path_id = path_id;
    }

    public String getIs_update_current_location() {
        return is_update_current_location;
    }

    public void setIs_update_current_location(String is_update_current_location) {
        this.is_update_current_location = is_update_current_location;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        if (causes != null && causes.size() > 0) {
            this.causes = causes;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 0) {
            this.description = description;
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        if (images != null && images.size() > 0) {
            this.images = images;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
