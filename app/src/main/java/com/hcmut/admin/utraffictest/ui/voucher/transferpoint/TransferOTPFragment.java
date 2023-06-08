package com.hcmut.admin.utraffictest.ui.voucher.transferpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferOTPFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Button btnVerify;
    Toolbar toolbar;
    TextView numberPhone;
    Bundle bundle = new Bundle();
    AndroidExt androidExt = new AndroidExt();
    TextView reSend;
    TextView opt1;
    TextView opt2;
    TextView opt3;
    TextView opt4;
    TextView opt5;
    TextView opt6;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                String authen = opt1.getText().toString()+ opt2.getText().toString()+
                opt3.getText().toString()+
                opt4.getText().toString()+
                opt5.getText().toString()+
                opt6.getText().toString();
                if(authen.length()==6){
                    confirmAuthentication(Integer.parseInt(getArguments().getString("point")),getArguments().getString("message"),authen,getArguments().getString("receive"));
                }
                else{
                    Toast.makeText(getContext(), "Vui lòng nhập mã xác thực", Toast.LENGTH_SHORT).show();
                }




                break;
            case R.id.tv_resend:
                getMessageAuthentication();
                break;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_opt_authentication, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnVerify = view.findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(this);

        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chuyển điểm");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        numberPhone= view.findViewById(R.id.textView2);
        numberPhone.setText("được gửi đến "+ SharedPrefUtils.getUser(getContext()).getPhoneNumber());
        reSend = view.findViewById(R.id.tv_resend);
        reSend.setOnClickListener(this);

        opt1 = view.findViewById(R.id.otp_edit_text1);
        opt2 = view.findViewById(R.id.otp_edit_text2);
        opt3 = view.findViewById(R.id.otp_edit_text3);
        opt4 = view.findViewById(R.id.otp_edit_text4);
        opt5 = view.findViewById(R.id.otp_edit_text5);
        opt6 = view.findViewById(R.id.otp_edit_text6);

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(TransferOTPFragment.this).popBackStack();
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
    private void getMessageAuthentication(){
        RetrofitClient.getApiService().getMessageAuthentication(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }
    private void confirmAuthentication(int point,String message,String authen,String receive) {

        RetrofitClient.getApiService().confirmAuthentication(SharedPrefUtils.getUser(getContext()).getAccessToken(),point,message,authen,receive)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                if (response.body().getData() != null) {
                                                    NavHostFragment.findNavController(TransferOTPFragment.this)
                        .navigate(R.id.action_transferOTPFragment_to_successTransferFragment);
                                } else {
                                    androidExt.showErrorDialog(getContext(), "Mã xác thực không chính xác");
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
