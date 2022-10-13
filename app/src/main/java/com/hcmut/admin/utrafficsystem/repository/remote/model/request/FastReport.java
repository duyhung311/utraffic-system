package com.hcmut.admin.utrafficsystem.repository.remote.model.request;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.utrafficsystem.util.LocationUtil;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FastReport {
    public PersonSharing personSharing;
    public int speed;
    public String address;
    public double longitude;
    public double latitude;
    public int distance;

    public static class PersonSharing {
        public String id = "5ee78bcb37594b29a04802a1";
    }

    public void postReport(final Activity activity, final AndroidExt androidExt) {
        RetrofitClient.getAPIVOHService().postFastRecord(FastReport.this)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        try {
                            if (response.body().getCode() == 201) {
                                androidExt.showSuccess(
                                        activity,
                                        "Gửi cảnh báo thành công");
                            }
                        } catch (Exception e) {
                            androidExt.showErrorDialog(
                                    activity,
                                    "Gửi cảnh báo Thất bại, Xin kiểm tra lại kết nối mạng");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        androidExt.showErrorDialog(
                                activity,
                                "Gửi cảnh báo Thất bại, Xin kiểm tra lại kết nối mạng");
                    }
                });
    }

    public static void postFastReport(final Activity activity, final AndroidExt androidExt) {
        if (activity == null) {
            return;
        }
        if (MapUtil.checkGPSTurnOn(activity, androidExt)) {
            LocationCollectionManager.getInstance(activity.getApplicationContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            if (location != null) {
                                final String address = LocationUtil.getAddressByLatLng(activity,
                                        new LatLng(location.getLatitude(), location.getLongitude()));
                                if (address.length() < 1) {
                                    androidExt.showErrorDialog(
                                            activity,
                                            "Không thể lấy được địa chỉ người dùng, Vui lòng kiểm tra lại đường truyền!");
                                    return;
                                }
                                androidExt.comfirm(
                                        activity,
                                        "Thực hiện cảnh báo nhanh",
                                        address,
                                        new ClickDialogListener.Yes() {
                                            @Override
                                            public void onCLickYes() {
                                                FastReport fastReport = new FastReport();
                                                fastReport.address = address;
                                                fastReport.distance = 200;
                                                fastReport.personSharing = new FastReport.PersonSharing();
                                                fastReport.speed = 15;
                                                fastReport.latitude = location.getLatitude();
                                                fastReport.longitude = location.getLongitude();
                                                fastReport.postReport(activity, androidExt);
                                            }
                                        });
                            } else {
                                androidExt.showErrorDialog(activity,"Không thể lấy được vị trí người dùng, Vui lòng thử lại!");
                            }
                        }
                    });
        }
    }
}
