package com.hcmut.admin.utrafficsystem.business;

import android.graphics.Bitmap;

import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrafficReportPhotoUploader extends PhotoUploader {
    private int maxWidth;
    private int maxHeight;

    public TrafficReportPhotoUploader(int maxWidth, int maxHeight, @NotNull PhotoUploadCallback callback) {
        super(callback);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    protected void uploadFile(Bitmap bitmap, final PhotoUploadCallback photoUploadCallback) {
        if (bitmap == null) {
            photoUploadCallback.onUpLoadFail();
            return;
        }

        final Bitmap scaledBitmap = getScaleBitmap(bitmap, maxWidth, maxHeight);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] byteArray = stream.toByteArray();

        RequestBody fileReqBody = RequestBody.create(byteArray, MediaType.parse("image/*"));
        MultipartBody.Part parts = MultipartBody.Part.createFormData(
                "file", String.valueOf(System.currentTimeMillis()), fileReqBody);
        RetrofitClient.getApiService().uploadFile(parts)
                .enqueue(new Callback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<String>> call, final Response<BaseResponse<String>> response) {
                        // TODO: neu thanh cong thi set image vao imageview
                        if (response.body() != null &&
                                response.body().getData() != null &&
                                response.code() == 200) {
                            photoUploadCallback.onUpLoaded(scaledBitmap, response.body().getData());
                        } else {
                            photoUploadCallback.onUpLoadFail();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                        photoUploadCallback.onUpLoadFail();
                    }
                });
    }
}
