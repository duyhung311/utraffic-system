package com.hcmut.admin.utraffictest.business;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hcmut.admin.utraffictest.R;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.hcmut.admin.utraffictest.util.ImageUtils.getGalleryIntents;

public abstract class PhotoUploader {
    public static final int IMAGE_PERMISSION_CODE = 111;
    public static final int IMAGE_REQUEST = 112;

    private PhotoUploader.PhotoUploadCallback photoUploadCallback;

    public PhotoUploader(@NotNull PhotoUploadCallback callback) {
        photoUploadCallback = callback;
    }

    public void collectPhoto(Activity activity) {
        if (activity == null) return;
        Context context = activity.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);
            } else {
                getPhotoIntent(activity);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
        if (requestCode == IMAGE_PERMISSION_CODE) {
            Log.e("test", "length" + grantResults.length);
            if ((grantResults.length > 2) && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                getPhotoIntent(activity);
            }
        }
    }

    public void onActivityResult(Context context, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            photoUploadCallback.onPreUpload();
            handleImageResult(context, data);
        }
    }

    private void handleImageResult(Context context, Intent data) {
        Bitmap bitmap = null;
        try {
            if (data.getData() == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {}
        uploadFile(bitmap, photoUploadCallback);
    }

    protected abstract void uploadFile(final Bitmap bitmap, PhotoUploadCallback photoUploadCallback);

    /**
     * Scale bitmap if its size greater than max size
     */
    protected Bitmap getScaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > maxWidth || height > maxHeight) {
            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                    bitmap, Math.min(width, maxWidth), Math.min(height, maxHeight), false);
            try {
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return scaledBitmap;
        }
        return bitmap;
    }

    private void getPhotoIntent(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) return;

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        // collect all camera intents if Camera permission is available
        allIntents.add(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

        List<Intent> galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK);
        }
        allIntents.addAll(galleryIntents);

        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, context.getString(R.string.title_take_photo));

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        activity.startActivityForResult(chooserIntent, IMAGE_REQUEST);
    }

    public interface PhotoUploadCallback {
        void onPreUpload();
        void onUpLoaded(Bitmap bitmap, String url);
        void onUpLoadFail();
    }
}
