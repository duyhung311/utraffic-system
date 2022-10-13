package com.hcmut.admin.utrafficsystem.ui.healthfacility.find;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.healthfacility.FindHealthFacilitiesResultAdapter;
import com.hcmut.admin.utrafficsystem.model.HealthFacility;

import java.util.List;

public class BottomSheetFindHealthFacilitiesResult extends BottomSheetDialogFragment {
    private Fragment fragment;
    private List<HealthFacility> healthFacilitiesList;
    private GoogleMap map;
    private Adapter adapter;

    public BottomSheetFindHealthFacilitiesResult(Fragment fragment, List<HealthFacility> healthFacilitiesList, GoogleMap map) {
        this.fragment = fragment;
        this.healthFacilitiesList = healthFacilitiesList;
        this.map = map;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_find_health_facilities_result_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvFindHealthFacilitiesResult = view.findViewById(R.id.rcvFindHealthFacilitiesResult);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvFindHealthFacilitiesResult.setLayoutManager(linearLayoutManager);

        FindHealthFacilitiesResultAdapter findHealthFacilitiesResultAdapter = new FindHealthFacilitiesResultAdapter(getContext(), adapter, fragment, healthFacilitiesList,bottomSheetDialog,map);
        rcvFindHealthFacilitiesResult.setAdapter(findHealthFacilitiesResultAdapter);

        return bottomSheetDialog;
    }
}
