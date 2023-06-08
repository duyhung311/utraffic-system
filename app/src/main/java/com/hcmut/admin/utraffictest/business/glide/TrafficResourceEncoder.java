package com.hcmut.admin.utraffictest.business.glide;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TrafficResourceEncoder implements ResourceEncoder<Bitmap> {
    @NonNull
    @Override
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.TRANSFORMED;
    }

    @Override
    public boolean encode(@NonNull Resource<Bitmap> data, @NonNull File file, @NonNull Options options) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Bitmap bitmap = data.get();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            bitmap.recycle();
            Log.e("glide", "encode");
            return true;
        } catch (FileNotFoundException e) {}
        return false;
    }
}
