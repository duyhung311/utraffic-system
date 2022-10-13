package com.hcmut.admin.utrafficsystem.ui.guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.hcmut.admin.utrafficsystem.R;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int[] titles = {
            R.string.accountSettingTitle,
            R.string.reportTitle,
            R.string.warningStatusTitle,
            R.string.searchWayTitle
    };

    int[] contents = {
            R.string.accountSettingContent,
            R.string.reportContent,
            R.string.warningStatusContent,
            R.string.searchWay
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtGuideSlideTitle);
        TextView txtContent = (TextView) view.findViewById(R.id.txtGuideSlideContent);

        txtTitle.setText(titles[position]);
        txtContent.setText(contents[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
