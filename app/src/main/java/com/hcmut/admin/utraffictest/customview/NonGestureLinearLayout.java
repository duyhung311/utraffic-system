package com.hcmut.admin.utraffictest.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class NonGestureLinearLayout extends LinearLayout {
    public NonGestureLinearLayout(Context context) {
        super(context);
    }

    public NonGestureLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonGestureLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NonGestureLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        super.dispatchDragEvent(event);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return true;
    }
}
