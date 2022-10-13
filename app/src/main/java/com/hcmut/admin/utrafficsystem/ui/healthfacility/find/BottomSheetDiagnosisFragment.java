package com.hcmut.admin.utrafficsystem.ui.healthfacility.find;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.healthfacility.DiagnosisResultAdapter;
import com.hcmut.admin.utrafficsystem.model.DiagnosisInfo;

import java.util.List;

public class BottomSheetDiagnosisFragment extends BottomSheetDialogFragment {
    private Fragment fragment;
    private List<DiagnosisInfo> diagnosisInfoList;

    public BottomSheetDiagnosisFragment(Fragment fragment, List<DiagnosisInfo> diagnosisInfoList) {
        this.fragment = fragment;
        this.diagnosisInfoList = diagnosisInfoList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_diagnosis_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvDiagnosisResult = view.findViewById(R.id.rcvDiagnosisResult);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvDiagnosisResult.setLayoutManager(linearLayoutManager);

        DiagnosisResultAdapter diagnosisResultAdapter = new DiagnosisResultAdapter(fragment,diagnosisInfoList,bottomSheetDialog);
        rcvDiagnosisResult.setAdapter(diagnosisResultAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        rcvDiagnosisResult.addItemDecoration(itemDecoration);

        return bottomSheetDialog;
    }
}
