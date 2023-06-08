package com.hcmut.admin.utraffictest.ui.atm;

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
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.atm.AtmListAdapter;
import com.hcmut.admin.utraffictest.model.Atm;

import java.util.List;

public class BottomSheetAtmListFragment extends BottomSheetDialogFragment {
    private Fragment fragment;
    private List<Atm> atmList;
    private GoogleMap map;
    private Adapter adapter;

    public BottomSheetAtmListFragment(Fragment fragment, List<Atm> atmList, GoogleMap map) {
        this.fragment = fragment;
        this.atmList = atmList;
        this.map = map;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_atm_list_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvListAtm = view.findViewById(R.id.rcvListAtm);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvListAtm.setLayoutManager(linearLayoutManager);

        AtmListAdapter atmListAdapter = new AtmListAdapter(getContext(), adapter, fragment, atmList, bottomSheetDialog, map);
        rcvListAtm.setAdapter(atmListAdapter);

        return bottomSheetDialog;
    }
}
