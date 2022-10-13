package com.hcmut.admin.utrafficsystem.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageListDownloader extends AsyncTask<Void, Object, Void> {
    private HashMap<String, WeakReference<ImageView>> imageList;

    private ImageListDownloader(HashMap<String, WeakReference<ImageView>> imageList) {
        this.imageList = imageList;
    }

    @Override
    protected Void doInBackground(Void ... urls) {
        Bitmap bitmap;
        String url;
        for (Map.Entry<String, WeakReference<ImageView>> entry : imageList.entrySet()) {
            try {
                url = entry.getKey();
                bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                publishProgress(bitmap, entry.getValue());
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object ... values) {
        try {
            Bitmap bitmap = (Bitmap) values[0];
            WeakReference<ImageView> imageViewWeakReference = (WeakReference<ImageView>) values[1];
            ImageView imageView = imageViewWeakReference.get();
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {}
    }

    public static class Builder {
        private HashMap<String, WeakReference<ImageView>> imageList = new HashMap<>();
        public Builder addImage (String url, ImageView img) {
            if (url != null && img != null) {
                imageList.put(url, new WeakReference<ImageView>(img));
            }
            return this;
        }

        public ImageListDownloader build () {
            return new ImageListDownloader(imageList);
        }
    }
}
