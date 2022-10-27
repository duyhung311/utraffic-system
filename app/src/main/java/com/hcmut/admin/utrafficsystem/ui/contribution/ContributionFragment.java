package com.hcmut.admin.utrafficsystem.ui.contribution;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.CallPhone;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.FastReport;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContributionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContributionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtReportTraffic;
    private TextView txtReportInfrastructure;
    private TextView txtFastReport;
    private TextView txtCallVOH;
    private TextView txtSpeechReport;

    public ContributionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContributionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContributionFragment newInstance(String param1, String param2) {
        ContributionFragment fragment = new ContributionFragment();
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
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getContext()).showBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contribution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents();
    }

    private void addControls(View view) {
        txtReportTraffic = view.findViewById(R.id.txtReportTraffic);
        txtReportInfrastructure = view.findViewById(R.id.txtReportInfrastructure);
        txtFastReport = view.findViewById(R.id.txtFastReport);
        txtCallVOH = view.findViewById(R.id.txtCallVOH);
        txtSpeechReport = view.findViewById(R.id.txtSpeechReport);
    }

    private void addEvents() {
        txtReportTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        NavHostFragment.findNavController(ContributionFragment.this)
                                                .navigate(R.id.action_contributionFragment_to_reportSendingFragment);
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Không thể lấy vị trí, vui lòng thử lại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        txtReportInfrastructure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ContributionFragment.this)
                        .navigate(R.id.action_contributionFragment_to_infrastructureReportFragment);
            }
        });
        txtFastReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastReport.postFastReport(getActivity(), MapActivity.androidExt);
            }
        });
        txtCallVOH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CallPhone callPhone = new CallPhone((MapActivity) getContext());
                    callPhone.checkCallPhonePermisstion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        txtSpeechReport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NavHostFragment.findNavController(ContributionFragment.this)
                               .navigate(R.id.action_contributionFragment_to_speechReportFragment);
            }
        });
    }
}
