package com.hcmut.admin.utrafficsystem.ui.reportdetail.traffic.photo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PreViewPhotoAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> photos;

    PreViewPhotoAdapter(FragmentManager fm) {
        super(fm);
    }

    void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int position) {
        String url = photos.get(position);
        return PhotoFragment.newInstance(url);
    }

    @Override
    public int getCount() {
        return photos.size();
    }
}
