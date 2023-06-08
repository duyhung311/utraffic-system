package com.hcmut.admin.utraffictest.ui.voucher.myvoucher;

import android.graphics.Color;
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

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.MyVoucherResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMyVoucherFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Toolbar toolbar;
    Button btnUse;
    AndroidExt androidExt = new AndroidExt();
    Bundle bundle = new Bundle();
    TextView orderNumber;
    TextView date;
    TextView name;
    TextView price;
    TextView content;
    TextView status;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    MyVoucherResponse myVoucherResponses;
    ImageView image;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reOrder:
                bundle.putString("code", myVoucherResponses.getCode());
                NavHostFragment.findNavController(DetailMyVoucherFragment.this)
                        .navigate(R.id.action_detailMyVoucherFragment_to_qrVoucherFragment,bundle);

                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_my_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chi tiết");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        btnUse = view.findViewById(R.id.reOrder);
        btnUse.setOnClickListener(this);
        orderNumber = view.findViewById(R.id.orderNumber);
        date = view.findViewById(R.id.orderDate);
        name = view.findViewById(R.id.txtProductName);
        price = view.findViewById(R.id.txtProductPrice);
        content = view.findViewById(R.id.txtContent);
        status = view.findViewById(R.id.orderStatus);
        image = view.findViewById(R.id.imgProductImage);

        getDetailMyVoucher(getArguments().getString("idOffer"));

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(DetailMyVoucherFragment.this).popBackStack();
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
    private void getDetailMyVoucher(String id){
        RetrofitClient.getApiService().getDetailMyVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken(),id)
                .enqueue(new Callback<BaseResponse<MyVoucherResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<MyVoucherResponse>> call, Response<BaseResponse<MyVoucherResponse>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                myVoucherResponses = response.body().getData();
                                orderNumber.setText(myVoucherResponses.getCodeDeal());
                                date.setText(formatter.format(myVoucherResponses.getDate()));
                                name.setText(myVoucherResponses.getVoucherResponse().getName());
                                price.setText(myVoucherResponses.getVoucherResponse().getValue()+" điểm");
                                content.setText(myVoucherResponses.getVoucherResponse().getContent());
                                String[] parts = myVoucherResponses.getCode().split(",");
                                if (parts.length==2){
                                    status.setText("Mã thẻ: "+parts[0]+"\n"+"\n"+"Số serial: "+ parts[1]);
                                    btnUse.setVisibility(View.GONE);
                                }
                                else {
                                    if (myVoucherResponses.getStatus() == 0) {
                                        status.setText("Voucher chưa được sử dụng");

                                    } else {
                                        status.setText("Voucher đã được sử dụng");
                                        btnUse.setEnabled(false);
                                        btnUse.setBackgroundColor(Color.GRAY);
                                    }
                                }
                                if(myVoucherResponses.getVoucherResponse().getImage()!=null){
                                    Picasso.get().load(myVoucherResponses.getVoucherResponse().getImage()).noFade().fit().into(image);
                                }



                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<MyVoucherResponse>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
