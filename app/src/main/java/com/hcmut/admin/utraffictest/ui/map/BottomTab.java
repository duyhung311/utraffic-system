package com.hcmut.admin.utraffictest.ui.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class BottomTab {
    private Map<Integer, View> tabs;
    private int currentTabId;

    private BottomTab(Map<Integer, View> tabs, int currentTabId) {
        this.tabs = tabs;
        this.currentTabId =currentTabId;
    }

    public void showTab(int id) {
        final View currentTab = tabs.get(currentTabId);
        if (currentTab != null) {   // hide current tab
            currentTab.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            currentTab.setVisibility(View.GONE);
                        }
                    });
        }
        View showTab = tabs.get(id);
        if (showTab != null) {  // show tab
            showTab.setAlpha(0f);
            showTab.setVisibility(View.VISIBLE);
            showTab.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(null);
        }
        currentTabId = id;
    }

    public static class Builder {
        private Map<Integer, View> tabs = new HashMap<>();
        private int currentTabId;

        public Builder(int currentTabId) {
            this.currentTabId = currentTabId;
        }

        public Builder addTab(int id, View tab) {
            tabs.put(id, tab);
            return this;
        }

        public BottomTab build() {
            BottomTab bottomTab = new BottomTab(tabs, currentTabId);
            bottomTab.showTab(currentTabId);
            return bottomTab;
        }
    }
}
