package com.hcmut.admin.utrafficsystem.ui.partner.healthfacility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthFacilityPartnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthFacilityPartnerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatImageButton btnBack;
    private TextView tvRegisterHealthFacility;
    private TextView tvMydHealthFacilities;

    public HealthFacilityPartnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HealthFacilityPartnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthFacilityPartnerFragment newInstance(String param1, String param2) {
        HealthFacilityPartnerFragment fragment = new HealthFacilityPartnerFragment();
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
        return inflater.inflate(R.layout.fragment_health_facility_partner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack =(AppCompatImageButton) view.findViewById(R.id.btnBack);
        tvRegisterHealthFacility = view.findViewById(R.id.tvRegisterHealthFacility);
        tvMydHealthFacilities = view.findViewById(R.id.tvMyHealthFacilities);

        addEvent();
    }

    private void addEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HealthFacilityPartnerFragment.this).popBackStack();
            }
        });
        tvRegisterHealthFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HealthFacilityPartnerFragment.this)
                        .navigate(R.id.action_healthFacilityPartnerFragment_to_registerHealthFacilityFragment);
            }
        });

        tvMydHealthFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HealthFacilityPartnerFragment.this)
                        .navigate(R.id.action_healthFacilityPartnerFragment_to_myHealthFacilitiesFragment);
            }
        });
    }
}