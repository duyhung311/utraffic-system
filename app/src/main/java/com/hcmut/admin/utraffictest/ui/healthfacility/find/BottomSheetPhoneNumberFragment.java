package com.hcmut.admin.utraffictest.ui.healthfacility.find;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.healthfacility.PhoneNumberAdapter;

import java.util.ArrayList;

public class BottomSheetPhoneNumberFragment extends BottomSheetDialogFragment {
    private Fragment fragment;
    private ArrayList<String> listPhoneNumber;
    private Context mContext;
    private Adapter adapter;

    public BottomSheetPhoneNumberFragment(Fragment fragment, Context mContext, ArrayList<String> listPhoneNumber) {
        this.fragment = fragment;
        this.mContext = mContext;
        this.listPhoneNumber = listPhoneNumber;
    }

    public BottomSheetPhoneNumberFragment(Adapter adapter, Context mContext, ArrayList<String> listPhoneNumber) {
        this.adapter = adapter;
        this.mContext = mContext;
        this.listPhoneNumber = listPhoneNumber;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_phone_number_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvPhoneNumber = view.findViewById(R.id.rcvPhoneNumber);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvPhoneNumber.setLayoutManager(linearLayoutManager);

        PhoneNumberAdapter phoneNumberAdapter = new PhoneNumberAdapter(fragment, mContext, adapter, listPhoneNumber);
        rcvPhoneNumber.setAdapter(phoneNumberAdapter);

        return bottomSheetDialog;
    }
}
