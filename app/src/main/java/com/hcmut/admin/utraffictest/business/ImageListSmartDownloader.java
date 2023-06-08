package com.hcmut.admin.utraffictest.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageListSmartDownloader extends AsyncTask<Void, Object, Void> {
    private List<String> urls;
    private WeakReference<LinearLayout> viewWeakReference;

    private ImageListSmartDownloader(WeakReference<LinearLayout> viewWeakReference, List<String> urls) {
        this.urls = urls;
        this.viewWeakReference = viewWeakReference;
    }

    @Override
    protected Void doInBackground(Void ... params) {
        Bitmap bitmap;
        for (String url : urls) {
            try {
                bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                publishProgress(bitmap);
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object ... values) {
        try {
            Bitmap bitmap = (Bitmap) values[0];
            LinearLayout llImageContainer = viewWeakReference.get();
            if (llImageContainer != null) {
                ImageView imageView = new ImageView(llImageContainer.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                imageView.setImageBitmap(bitmap);
                imageView.setLayoutParams(params);
                llImageContainer.addView(imageView);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {}
    }

    public static class Builder {
        private List<String> urls = new ArrayList<>();
        private WeakReference<LinearLayout> viewWeakReference;

        public Builder(LinearLayout parent) {
            viewWeakReference = new WeakReference<>(parent);
        }

        public ImageListSmartDownloader.Builder addImageUrl(String url) {
            urls.add(url);
            return this;
        }

        public ImageListSmartDownloader build () {
            return new ImageListSmartDownloader(viewWeakReference, urls);
        }
    }
}
