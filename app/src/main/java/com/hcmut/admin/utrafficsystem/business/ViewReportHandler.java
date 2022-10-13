package com.hcmut.admin.utrafficsystem.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmut.admin.utrafficsystem.R;

import com.hcmut.admin.utrafficsystem.repository.StatusRepositoryService;
import com.hcmut.admin.utrafficsystem.repository.StatusRemoteRepository;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.StatusResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.StatusRenderData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportHandler {

    private Context context;
    private StatusRepositoryService statusRepository;
    private BitmapDescriptor icon;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ViewReportHandler(Context context) {
        this.context = context;
        statusRepository = new StatusRemoteRepository();
    }

    public void getUserReportStatus(double lat, double lng, @NotNull final SegmentResultCallback callback) {
        RetrofitClient.getApiService()
                .getTrafficStatusIncludeUserReport(lat, lng, 1000)
                .enqueue(new Callback<StatusResponse<List<StatusRenderData>>>() {
                    @Override
                    public void onResponse(Call<StatusResponse<List<StatusRenderData>>> call, Response<StatusResponse<List<StatusRenderData>>> response) {
                        Log.e("fasa", response.toString());
                        if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                            if (icon == null) {
                                icon = bitmapDescriptorFromVector(context, R.drawable.ic_position_rating);
                            }
                            final List<MarkerOptions> markerOptionsList = StatusRenderData
                                    .parseMarkerOptionsList(response.body().getData(), icon);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (markerOptionsList != null) {
                                        callback.onSuccess(markerOptionsList);
                                    } else {
                                        callback.onHaveNotResult();
                                    }
                                }
                            });
                        } else {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onHaveNotResult();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse<List<StatusRenderData>>> call, Throwable t) {
                        t.printStackTrace();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFaile();
                            }
                        });
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int bgDrawableId) {
        Drawable background = ContextCompat.getDrawable(context, bgDrawableId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public interface SegmentResultCallback {
        void onSuccess(List<MarkerOptions> markerOptionsList);
        void onHaveNotResult();
        void onFaile();
    }
}
