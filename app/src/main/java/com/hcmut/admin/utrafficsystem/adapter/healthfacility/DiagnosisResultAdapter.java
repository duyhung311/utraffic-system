package com.hcmut.admin.utrafficsystem.adapter.healthfacility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.DiagnosisInfo;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.FindHealthFacilityFragment;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisResultAdapter extends RecyclerView.Adapter<DiagnosisResultAdapter.DiagnosisResultViewHolder> {
    private Fragment fragment;
    private List<DiagnosisInfo> diagnosisInfoList;
    private BottomSheetDialog bottomSheetDialog;

    public DiagnosisResultAdapter(Fragment fragment,List<DiagnosisInfo> diagnosisInfoList,BottomSheetDialog bottomSheetDialog) {
        this.fragment = fragment;
        this.diagnosisInfoList = diagnosisInfoList;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public DiagnosisResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diagnosis_result, parent, false);
        return new DiagnosisResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosisResultViewHolder holder, int position) {
        final DiagnosisInfo item  = diagnosisInfoList.get(position);
        if(item == null){
            return;
        }

        holder.tvDiagnosisName.setText(item.getName());
        holder.tvAccuracy.setText(item.getAccuracy().toString()+"%");
        holder.btnShowHealthFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList(FindHealthFacilityFragment.SPECIALISATION_IDS, (ArrayList<Integer>) item.getSpecialisationIds());
                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_findHealthFacilityFragment_to_mapFeatureFragment,bundle);
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(diagnosisInfoList != null){
            return diagnosisInfoList.size();
        }
        return 0;
    }

    public class DiagnosisResultViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDiagnosisName;
        private TextView tvAccuracy;
        private AppCompatButton btnShowHealthFacilities;

        public DiagnosisResultViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDiagnosisName = itemView.findViewById(R.id.tvDiagnosisName);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
            btnShowHealthFacilities = itemView.findViewById(R.id.btnShowHealthFacilities);
        }
    }
}
