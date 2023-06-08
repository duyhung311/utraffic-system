package com.hcmut.admin.utraffictest.ui.voucher.buypoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

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
import com.hcmut.admin.utraffictest.repository.remote.model.response.PayMoMoResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.ClickDialogListener;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNamePayment;

//import vn.momo.momo_partner.AppMoMoLib;
//import vn.momo.momo_partner.MoMoParameterNameMap;


public class BuyPointFragment extends Fragment  implements MapActivity.OnBackPressCallback ,View.OnClickListener {
    Toolbar toolbar;
    LinearLayout layoutMOMO;
    LinearLayout layoutATM;
    RadioButton radioMOMO;
    RadioButton radioATM;
    Button payButton;
    EditText amountText;
    AndroidExt androidExt = new AndroidExt();
    //    private String amount = "10000";
////    private String fee = "0";
////    int environment = 0;//developer default
////    private String merchantName = "MoMoTest";
////    private String merchantCode = "MOMOIQA420180417";
////    private String merchantNameLabel = "Nhà cung cấp";
////    private String description = "Thanh toán dịch vụ ABC";
    public String amount = "0";
    private String fee = "0";
    private int check_amount=0;
    int environment = 0;//developer default
    private String merchantName = "UTraffic";
    private String merchantCode = "MOMOUX4U20201123";
    private String partnerCode = "MOMOUX4U20201123";
    private String merchantNameLabel = "UTraffic";
    private String description = "Giao dịch mua điểm";
    public String requestId = UUID.randomUUID().toString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        return inflater.inflate(R.layout.fragment_buy_point, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Mua điểm");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        layoutMOMO = view.findViewById(R.id.layout_momo);
        layoutATM = view.findViewById(R.id.layout_atm);
        radioATM = view.findViewById(R.id.buttonATM);
        radioMOMO = view.findViewById(R.id.buttonMOMO);
        payButton = view.findViewById(R.id.payButton);
        amountText = view.findViewById(R.id.txtChoose);

        layoutATM.setOnClickListener(this);
        layoutMOMO.setOnClickListener(this);
        radioATM.setEnabled(false);
        radioMOMO.setEnabled(false);
        payButton.setOnClickListener(this);
        ((MapActivity) getActivity()).setBuyPointFragment(this);


    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(BuyPointFragment.this).popBackStack();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_momo:
                radioMOMO.setChecked(true);
                radioATM.setChecked(false);
                break;
            case R.id.layout_atm:
                radioMOMO.setChecked(false);
                radioATM.setChecked(true);
                break;
            case R.id.payButton:
                requestPayment();
        }

    }

    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        if (amountText.getText().toString() != null && amountText.getText().toString().trim().length() != 0) {
            check_amount = Integer.parseInt(amountText.getText().toString().trim());
            if(check_amount<20 ){
                androidExt.showNotifyDialog(getContext(), "Số điểm mua tối thiểu là 20", new ClickDialogListener.OK() {
                    @Override
                    public void onCLickOK() {

                    }
                });
                return;
            }
            amount =  Integer.toString(check_amount*50);
        }


        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put(MoMoParameterNamePayment.MERCHANT_NAME, merchantName);
        eventValue.put(MoMoParameterNamePayment.MERCHANT_CODE, merchantCode);
        eventValue.put(MoMoParameterNamePayment.AMOUNT, amount);
        eventValue.put(MoMoParameterNamePayment.DESCRIPTION, description);
        //client Optional
        eventValue.put(MoMoParameterNamePayment.FEE, fee);
        eventValue.put(MoMoParameterNamePayment.MERCHANT_NAME_LABEL, merchantNameLabel);

        eventValue.put(MoMoParameterNamePayment.REQUEST_ID, requestId);
        eventValue.put(MoMoParameterNamePayment.PARTNER_CODE, partnerCode);

        eventValue.put(MoMoParameterNamePayment.REQUEST_TYPE, "payment");
        eventValue.put(MoMoParameterNamePayment.LANGUAGE, "vi");
        eventValue.put(MoMoParameterNamePayment.EXTRA, "");

        //Request momo app
        AppMoMoLib.getInstance().requestMoMoCallBack(getActivity(), eventValue);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {

            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if (env == null) {
                        env = "app";
                    }
                    if (token != null && !token.equals("")) {
                        System.out.println("test1");
                        paymentRequest(token,phoneNumber,requestId, Integer.parseInt(amount),check_amount);


                    } else {
                        androidExt.showNotifyDialog(getContext(), "Giao dịch thất bại, vui lòng thử lại.", new ClickDialogListener.OK() {
                            @Override
                            public void onCLickOK() {

                            }
                        });
                    }

                } else {
                    androidExt.showNotifyDialog(getContext(), "Giao dịch thất bại, vui lòng thử lại.", new ClickDialogListener.OK() {
                        @Override
                        public void onCLickOK() {

                        }
                    });
                }
            } else {
                androidExt.showNotifyDialog(getContext(), "Giao dịch thất bại, vui lòng thử lại.", new ClickDialogListener.OK() {
                    @Override
                    public void onCLickOK() {

                    }
                });
            }
        } else {
            androidExt.showNotifyDialog(getContext(), "Giao dịch thất bại, vui lòng thử lại.", new ClickDialogListener.OK() {
                @Override
                public void onCLickOK() {

                }
            });
        }
    }
    public void paymentRequest(String token,String phone,String order,int amount,int point) {
        RetrofitClient.getApiService().paymentRequest(SharedPrefUtils.getUser(getContext()).getAccessToken(), token, phone, order, amount,point)
                .enqueue(new Callback<BaseResponse<PayMoMoResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<PayMoMoResponse>> call, Response<BaseResponse<PayMoMoResponse>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                PayMoMoResponse result = response.body().getData();
                                if(result.getState()==1){
                                    NavHostFragment.findNavController(BuyPointFragment.this)
                                            .navigate(R.id.action_buyPointFragment_to_PaySuccessFragment);
                                }
                                else{
                                    androidExt.showNotifyDialog(getContext(), "Giao dịch thất bại, vui lòng thử lại.", new ClickDialogListener.OK() {
                                        @Override
                                        public void onCLickOK() {

                                        }
                                    });
                                }

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<PayMoMoResponse>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

}
