package com.hcmut.admin.utrafficsystem.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

public class NonGestureConstraintLayout extends ConstraintLayout {
    public NonGestureConstraintLayout(Context context) {
        super(context);
    }

    public NonGestureConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonGestureConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
