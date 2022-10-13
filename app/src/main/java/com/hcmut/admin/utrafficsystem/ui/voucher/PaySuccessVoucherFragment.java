package com.hcmut.admin.utrafficsystem.ui.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

public class PaySuccessVoucherFragment extends Fragment implements  View.OnClickListener {
    Button btnReturn;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnButton:
                NavHostFragment.findNavController(PaySuccessVoucherFragment.this)
                        .popBackStack(R.id.voucherFragment,false);
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pay_success, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnReturn = view.findViewById(R.id.returnButton);
        btnReturn.setOnClickListener(this);

    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
