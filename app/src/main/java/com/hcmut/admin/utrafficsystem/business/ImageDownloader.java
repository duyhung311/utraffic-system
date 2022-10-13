package com.hcmut.admin.utrafficsystem.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageViewWeakReference;

    public ImageDownloader(ImageView bmImage) {
        this.imageViewWeakReference = new WeakReference<>(bmImage);
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
        }
        return mIcon11;
    }

    protected void onPostExecute(final Bitmap result) {
        ImageView imageView = imageViewWeakReference.get();
        if (result != null && imageView != null) {
            imageView.setImageBitmap(result);
        }
    }
}
