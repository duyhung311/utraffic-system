package com.hcmut.admin.utrafficsystem.ui.reportdetail.traffic.photo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.ImageDownloader;

public class PhotoFragment extends Fragment {

    private AppCompatImageView imageView;
    private String url;

    public static Fragment newInstance(String photoUrl) {
        Fragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("URL", photoUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString("URL") : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_preview_photo, container, false);
        imageView = view.findViewById(R.id.ivPhoto);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new ImageDownloader(imageView).execute(url);
    }
}
