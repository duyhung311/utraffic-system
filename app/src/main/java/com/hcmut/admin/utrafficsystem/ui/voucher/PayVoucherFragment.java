package com.hcmut.admin.utrafficsystem.ui.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.InfoPaymentResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayVoucherFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Toolbar toolbar;
    Button btnPay;
    TextView name;
    TextView value;
    TextView beforePoint;
    TextView payPoint;
    TextView afterPoint;
    ImageView image;



    AndroidExt androidExt = new AndroidExt();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payButton:
                paymentVoucherRequest(getArguments().getString("idVoucher"));
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Thanh toán");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        btnPay = view.findViewById(R.id.payButton);
        btnPay.setOnClickListener(this);
        name  = view.findViewById(R.id.txtProductName);
        value  = view.findViewById(R.id.txtProductPrice);
        beforePoint  = view.findViewById(R.id.pointCurrentNumber);
        payPoint  = view.findViewById(R.id.pointPayNumber);
        afterPoint  = view.findViewById(R.id.pointRestNumber);
        image = view.findViewById(R.id.imgProductImage);

        getInfoPaymentVoucher(getArguments().getString("idVoucher"));
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
    public void onBackPress() {
        NavHostFragment.findNavController(PayVoucherFragment.this).popBackStack();
    }
    private void getInfoPaymentVoucher(String id){
        RetrofitClient.getApiService().getInfoPaymentVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken(),id)
                .enqueue(new Callback<BaseResponse<InfoPaymentResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<InfoPaymentResponse>> call, Response<BaseResponse<InfoPaymentResponse>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                InfoPaymentResponse infoPayment = response.body().getData();
                                name.setText(infoPayment.getName());
                                value.setText(String.valueOf(infoPayment.getValue()));
                                beforePoint.setText(String.valueOf(infoPayment.getBeforePoint()));
                                payPoint.setText("-"+ infoPayment.getPayPoint());
                                afterPoint.setText(String.valueOf(infoPayment.getAfterPoint()));
                                if(infoPayment.getImage()!=null){
                                    Picasso.get().load(infoPayment.getImage()).noFade().fit().into(image);
                                }
                            } else {
                                androidExt.showNotifyDialog(getContext(), "Sô điểm không đủ để thanh toán hoặc số lượng voucher đã hết", new ClickDialogListener.OK() {
                                    @Override
                                    public void onCLickOK() {
                                        onBackPress();
                                    }
                                });
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<InfoPaymentResponse>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
    private void paymentVoucherRequest(String id) {

        RetrofitClient.getApiService().paymentVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken(),id)
                .enqueue(new Callback<BaseResponse<VoucherResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<VoucherResponse>> call, Response<BaseResponse<VoucherResponse>> response) {

                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                if (response.body().getData() != null) {
                                                    NavHostFragment.findNavController(PayVoucherFragment.this)
                        .navigate(R.id.action_payVoucherFragment_to_PaySuccessVoucherFragment);

                                } else {
                                    androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                                }
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<VoucherResponse>> call, Throwable t) {

                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
