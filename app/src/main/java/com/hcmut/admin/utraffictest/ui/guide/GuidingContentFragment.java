package com.hcmut.admin.utraffictest.ui.guide;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuidingContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuidingContentFragment extends Fragment implements MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String GUIDING_FEATURE_TYPE = "guiding_feature_type";
    public static final int SEARCH_DIRECTION = 3;
    public static final int WARNING_STATUS = 2;
    public static final int REPORT_STATUS = 1;
    public static final int ACCOUNG_SETTING = 0;

    private ImageView imgBack;
    private TextView txtTitle;
    //private TextView txtContent;
    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mdots;
    private Button btnBackSlide;
    private Button btnNextSlide;
    private int currentSlideItem;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GuidingContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuidingContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuidingContentFragment newInstance(String param1, String param2) {
        GuidingContentFragment fragment = new GuidingContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guiding_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControls(view);

        addEvents();

        Bundle bundle = getArguments();
        if (bundle != null) {
            int guidingType = bundle.getInt(GUIDING_FEATURE_TYPE, 0);
            Log.e("guiding type", "" + guidingType);

            createSliderGuide(view,  guidingType);
        }


    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(GuidingContentFragment.this).popBackStack();
            }
        });
        btnNextSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(++currentSlideItem);
            }
        });
        btnBackSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(--currentSlideItem);
            }
        });
    }

    private void createSliderGuide(View view, int poisition){
        viewPager = (ViewPager) view.findViewById(R.id.slideViewPager);
        sliderAdapter = new SliderAdapter(getContext());
        viewPager.setAdapter(sliderAdapter);
        viewPager.setCurrentItem(poisition);
        addDotIndicator(poisition);
        viewPager.addOnPageChangeListener(viewListener);
        currentSlideItem = poisition;
        changeBackNextStatus(currentSlideItem);
    }

    private void addControls(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        txtTitle = view.findViewById(R.id.txtGuideTitle);
        dotLayout = (LinearLayout) view.findViewById(R.id.dotLayout);
        btnBackSlide = view.findViewById(R.id.btnBackSlideGuide);
        btnNextSlide = view.findViewById(R.id.btnNextSlideGuide);

    }

    private void addDotIndicator(int poisition){
        mdots = new TextView[4];
        dotLayout.removeAllViews();
        for(int i = 0; i < mdots.length; i++){
            mdots[i] = new TextView(getContext());
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(Color.parseColor("#A0A0A0"));
            dotLayout.addView(mdots[i]);
        }
        if(mdots.length > 0){
            mdots[poisition].setTextColor(Color.parseColor("#484848"));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotIndicator(position);
            currentSlideItem = position;
            changeBackNextStatus(currentSlideItem);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void changeBackNextStatus(int currentSlideItem) {
        if(currentSlideItem == 0){
            btnBackSlide.setEnabled(false);
            btnNextSlide.setEnabled(true);
            btnNextSlide.setVisibility(View.VISIBLE);
            btnBackSlide.setVisibility(View.INVISIBLE);
        }else if(currentSlideItem == (mdots.length -1)){
            btnNextSlide.setEnabled(false);
            btnBackSlide.setEnabled(true);
            btnNextSlide.setVisibility(View.INVISIBLE);
            btnBackSlide.setVisibility(View.VISIBLE);
        }else{
            btnNextSlide.setEnabled(true);
            btnBackSlide.setEnabled(true);
            btnNextSlide.setVisibility(View.VISIBLE);
            btnBackSlide.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(GuidingContentFragment.this).popBackStack();
    }
}
