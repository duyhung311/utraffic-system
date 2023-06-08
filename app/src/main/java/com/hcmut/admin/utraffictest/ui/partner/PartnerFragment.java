package com.hcmut.admin.utraffictest.ui.partner;

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

import com.hcmut.admin.utraffictest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartnerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatImageButton btnBack;
    private TextView tvHealthFacilityPartner;

    public PartnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PartnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartnerFragment newInstance(String param1, String param2) {
        PartnerFragment fragment = new PartnerFragment();
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
        return inflater.inflate(R.layout.fragment_partner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btnBack);
        tvHealthFacilityPartner = view.findViewById(R.id.tvHealthFacilityPartner);

        addEvent();
    }

    private void addEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PartnerFragment.this).popBackStack();
            }
        });

        tvHealthFacilityPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PartnerFragment.this)
                        .navigate(R.id.action_partnerFragment_to_healthFacilityPartnerFragment);
            }
        });
    }
}