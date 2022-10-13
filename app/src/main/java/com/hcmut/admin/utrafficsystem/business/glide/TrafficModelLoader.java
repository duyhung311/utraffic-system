package com.hcmut.admin.utrafficsystem.business.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

public final class TrafficModelLoader implements ModelLoader<BitmapGlideModel, Bitmap> {

    @Override
    public LoadData<Bitmap> buildLoadData(@NonNull BitmapGlideModel bitmapGlideModel, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(bitmapGlideModel), new TrafficDataFetcher(bitmapGlideModel));
    }

    @Override
    public boolean handles(@NonNull BitmapGlideModel bitmapGlideModel) {
        return true;
    }

    public static class TrafficModelLoaderFactory implements ModelLoaderFactory<BitmapGlideModel, Bitmap> {
        @NonNull
        @Override
        public ModelLoader<BitmapGlideModel, Bitmap> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new TrafficModelLoader();
        }

        @Override
        public void teardown() {

        }
    }
}
