package com.hcmut.admin.utraffictest.ui.voucher.myvoucher;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class QRVoucherFragment  extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Button btnReturn;
    ImageView qrImage;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qrReturn:
                onBackPress();
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode, container, false);

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnReturn = view.findViewById(R.id.qrReturn);
        qrImage = view.findViewById(R.id.qrImage);
        btnReturn.setOnClickListener(this);
        generationQRVoucher();

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(QRVoucherFragment.this).popBackStack();
    }
    public void generationQRVoucher(){

        QRGEncoder qrgEncoder = new QRGEncoder(getArguments().getString("code"),null, QRGContents.Type.TEXT,500);
        Bitmap qrBits = qrgEncoder.getBitmap();
        qrImage.setImageBitmap(qrBits);

    }
}
