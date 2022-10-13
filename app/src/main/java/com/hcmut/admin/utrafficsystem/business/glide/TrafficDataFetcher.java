package com.hcmut.admin.utrafficsystem.business.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

public class TrafficDataFetcher implements DataFetcher<Bitmap> {

    private BitmapGlideModel model;

    public TrafficDataFetcher(BitmapGlideModel model) {
        this.model = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
        if (model.getBitmap() != null) {
            callback.onDataReady(model.getBitmap());
        } else {
            callback.onLoadFailed(new Exception());
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<Bitmap> getDataClass() {
        return Bitmap.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
