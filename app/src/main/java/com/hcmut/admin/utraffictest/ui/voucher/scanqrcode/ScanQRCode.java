package com.hcmut.admin.utraffictest.ui.voucher.scanqrcode;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.ClickDialogListener;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRCode extends Fragment implements  MapActivity.OnBackPressCallback {

    AndroidExt androidExt = new AndroidExt();
    Toolbar toolbar;
    public ScanQRCode() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    private CodeScanner mCodeScanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_barcode, container, false);

        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirmQRCode(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Quét QR Code");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(ScanQRCode.this).popBackStack();
    }
    private void confirmQRCode(String code) {

        RetrofitClient.getApiService().confirmQRCode(SharedPrefUtils.getUser(getContext()).getAccessToken(),code)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                if (response.body().getData() != null) {
                                    androidExt.confirmQRScan(getContext(), "Sử dụng Voucher thành công", new ClickDialogListener.Yes() {
                                        @Override
                                        public void onCLickYes() {
                                            mCodeScanner.startPreview();
                                            return;
                                        }
                                    }, new ClickDialogListener.No() {
                                        @Override
                                        public void onClickNo() {
                                            onBackPress();
                                        }
                                    });
                                }
                                 else {
                                    androidExt.confirmQRScan(getContext(), "Voucher không tồn tại hoặc đã được sử dụng", new ClickDialogListener.Yes() {
                                        @Override
                                        public void onCLickYes() {
                                            mCodeScanner.startPreview();
                                            return;
                                        }
                                    }, new ClickDialogListener.No() {
                                        @Override
                                        public void onClickNo() {
                                                onBackPress();
                                        }
                                    });
                                }
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
