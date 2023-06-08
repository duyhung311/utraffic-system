package com.hcmut.admin.utraffictest.business;

import android.app.Activity;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class TourGuideModule {
    private TourGuide tourGuideHandler;
    private Queue<TourGuideData> dataList = new LinkedList<>();

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tourGuideHandler.cleanUp();
            TourGuideData next = dataList.poll();
            if (next != null) {
                processNextToolTip(next);
            }
        }
    };

    public TourGuideModule(Activity activity) {
        tourGuideHandler = TourGuide.init(activity)
                .with(TourGuide.Technique.CLICK)
                .setPointer(new Pointer())
                .setOverlay(new Overlay()
                        .disableClick(true)
                        .disableClickThroughHole(true)
                        .setOnClickListener(listener));
    }

    private void processNextToolTip(TourGuideData data) {
        tourGuideHandler
                .setToolTip(new ToolTip()
                        .setTitle(data.title)
                        .setDescription(data.description))
                .playOn(data.view);
    }

    public void processToolTip() {
        TourGuideData next = dataList.poll();
        if (next != null) {
            processNextToolTip(next);
        }
    }

    public TourGuideModule add(@NotNull TourGuideData d) {
        dataList.add(d);
        return this;
    }

    public static class TourGuideData {
        protected View view;
        protected String title;
        protected String description;

        public TourGuideData(View view, String title, String description) {
            this.view = view;
            this.title = title;
            this.description = description;
        }
    }
}
