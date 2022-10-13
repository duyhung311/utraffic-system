package com.hcmut.admin.utrafficsystem.ui.reportdetail.traffic.photo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.hcmut.admin.utrafficsystem.R;

import java.util.ArrayList;

public class PreViewPhotoActivity extends AppCompatActivity {

    private AppCompatTextView tvPosition;
    private ArrayList<String> images;
    private PreViewPhotoAdapter preViewPhotoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        tvPosition = findViewById(R.id.tvPosition);
        images = getIntent().getStringArrayListExtra("IMAGE");
        setupViewPager();
    }

    @SuppressLint("SetTextI18n")
    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.vpPhotos);
        preViewPhotoAdapter = new PreViewPhotoAdapter(getSupportFragmentManager());
        tvPosition.setText( "1 / " + images.size());
        preViewPhotoAdapter.setPhotos(images);
        viewPager.setAdapter(preViewPhotoAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                int curImage = i + 1;
                tvPosition.setText(curImage + " / " + images.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
}
