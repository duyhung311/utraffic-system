package com.hcmut.admin.utraffictest.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmut.admin.utraffictest.R;

public class SearchInputView extends NonGestureCardView {
    private ImageView imgClearText;
    private ImageView imgBack;
    private AutoCompleteTextView txtSearchInput;

    private OnClickListener backClickListener;

    // only show back icon, don't show search icon
    private boolean isOnlyBackIcon = false;

    public SearchInputView(@NonNull Context context) {
        super(context);
        initView();
    }

    public SearchInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_search_input, this, false);
        addView(view);

        imgClearText = findViewById(R.id.imgClearText);
        imgBack = findViewById(R.id.imgBack);
        txtSearchInput = findViewById(R.id.txtSearchInput);
    }

    public void updateView() {
        if (txtSearchInput.getText().toString().equals("")) {
            handleBackAndClearView(false);
        } else {
            handleBackAndClearView(true);
        }
    }

    /**
     * Handle Back and Clear button when have or haven't search result
     */
    public void handleBackAndClearView(boolean isHaveSearchResult) {
        if (isHaveSearchResult) {
            imgClearText.setVisibility(View.VISIBLE);
            if (!isOnlyBackIcon) {
                imgBack.setImageResource(R.drawable.ic_arrow_back);
                imgBack.setOnClickListener(backClickListener);
            }
        } else {
            txtSearchInput.setText("");
            imgClearText.setVisibility(View.GONE);
            if (!isOnlyBackIcon) {
                imgBack.setImageResource(R.drawable.ic_search);
                imgBack.setOnClickListener(null);
            }
        }
    }

    public String getSearchInputText() {
        return txtSearchInput.getText().toString();
    }

    public void setImgBackEvent(OnClickListener onClickListener, boolean isOnlyBackIcon) {
        this.backClickListener = onClickListener;
        this.isOnlyBackIcon = isOnlyBackIcon;
        if (isOnlyBackIcon) {
            imgBack.setImageResource(R.drawable.ic_arrow_back);
            imgBack.setOnClickListener(backClickListener);
        }
    }

    public void setImgClearTextEvent(OnClickListener onClickListener) {
        imgClearText.setOnClickListener(onClickListener);
    }

    public void setTxtSearchInputEvent(OnFocusChangeListener onFocusChangeListener) {
        txtSearchInput.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void setTxtSearchInputText(String text) {
        txtSearchInput.setText(text);
    }
}
