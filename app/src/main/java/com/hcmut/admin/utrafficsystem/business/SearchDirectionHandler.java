package com.hcmut.admin.utrafficsystem.business;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectRespose;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDirectionHandler {
    public static final String TYPE_TIME = "time";
    public static final String TYPE_DISTANCE = "distance";

    public static void direct(final Context context,
                              final LatLng startPoint,
                              final LatLng endPoint,
                              final boolean isTimeType,
                              final DirectResultCallback listener) {
        direct(context, startPoint, endPoint, (isTimeType) ? TYPE_TIME : TYPE_DISTANCE, listener);
    }

    public static LatLng addressStringToLatLng(Context context, String beginAddressString) {
        Address address = MapUtil.getLatLngByAddressOrPlaceName(context, beginAddressString);
        if (address != null) {
            return new LatLng(address.getLatitude(), address.getLongitude());
        }
        return null;
    }

    private static void direct(Context context, LatLng startPoint, LatLng endPoint, String type, final DirectResultCallback listener) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Đang tìm đường..!", true);

        RetrofitClient.getApiService().getFindDirect(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, type)
                .enqueue(new Callback<BaseResponse<List<DirectRespose>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DirectRespose>>> call, Response<BaseResponse<List<DirectRespose>>> response) {
                        Log.e("fad", response.toString());
                        progressDialog.dismiss();
                        try {
                            DirectRespose directRespose = response.body().getData().get(0);
                            if (directRespose.getCoords().size() > 1) {
                                listener.onSuccess(directRespose);
                                return;
                            }
                        } catch (Exception e) {}
                        listener.onHaveNoData();
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<DirectRespose>>> call, Throwable t) {
                        progressDialog.dismiss();
                        listener.onFail();
                    }
                });
    }

    public interface DirectResultCallback {
        void onSuccess(DirectRespose directRespose);
        void onHaveNoData();
        void onFail();
    }
}
