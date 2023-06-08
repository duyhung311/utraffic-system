package com.hcmut.admin.utraffictest.ui.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuidingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuidingFragment extends Fragment implements MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imgBack;
    private ConstraintLayout clSearchWayGuide;
    private ConstraintLayout clWarningStatusGuide;
    private ConstraintLayout clReportStatusGuide;
    private ConstraintLayout clSettingGuide;

    public GuidingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuidingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuidingFragment newInstance(String param1, String param2) {
        GuidingFragment fragment = new GuidingFragment();
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
        return inflater.inflate(R.layout.fragment_guiding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }

    private void addEvents(View view) {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(GuidingFragment.this).popBackStack();
            }
        });
        clSearchWayGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGuideNavigate(GuidingContentFragment.SEARCH_DIRECTION);
            }
        });
        clWarningStatusGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGuideNavigate(GuidingContentFragment.WARNING_STATUS);
            }
        });
        clReportStatusGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGuideNavigate(GuidingContentFragment.REPORT_STATUS);
            }
        });
        clSettingGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGuideNavigate(GuidingContentFragment.ACCOUNG_SETTING);
            }
        });
    }

    private void addControls(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        clSearchWayGuide = view.findViewById(R.id.clSearchWayGuide);
        clWarningStatusGuide = view.findViewById(R.id.clWarningStatusGuide);
        clReportStatusGuide = view.findViewById(R.id.clReportGuide);
        clSettingGuide = view.findViewById(R.id.clSettingGuide);
    }

    private void createGuideNavigate(final int featureId){
        Bundle bundle = new Bundle();
        bundle.putInt(GuidingContentFragment.GUIDING_FEATURE_TYPE, featureId);
        NavHostFragment.findNavController(GuidingFragment.this)
                .navigate(R.id.action_guidingFragment_to_guidingContentFragment, bundle);
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(GuidingFragment.this).popBackStack();
    }
}
