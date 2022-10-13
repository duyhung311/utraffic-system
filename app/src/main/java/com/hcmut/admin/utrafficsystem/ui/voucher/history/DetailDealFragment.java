package com.hcmut.admin.utrafficsystem.ui.voucher.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

public class DetailDealFragment  extends Fragment implements MapActivity.OnBackPressCallback{
    TextView nameDetail;
    TextView pointDetail;
    TextView codeDetail;
    TextView timeDetail;
    TextView nameUser;
    TextView phoneUser;
    TextView pointUser;
    TextView message;
    TextView messageTitle;
    TextView infoAdd;
    LinearLayout layoutInfoAdd;
    LinearLayout layoutMessage;
    TextView userTitle;
    Toolbar toolbar;





    @Override
    public void onBackPress() {

        NavHostFragment.findNavController(DetailDealFragment.this).popBackStack();
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
        nameDetail= view.findViewById(R.id.nameDetailDeal);
        pointDetail= view.findViewById(R.id.namePoint);
        codeDetail= view.findViewById(R.id.codeDeal);
        timeDetail= view.findViewById(R.id.dateDeal);
        nameUser= view.findViewById(R.id.nameSR);
        phoneUser= view.findViewById(R.id.phoneSR);
        pointUser= view.findViewById(R.id.pointSR);
        message= view.findViewById(R.id.contentMessage);
        messageTitle= view.findViewById(R.id.messageTitle);
        infoAdd= view.findViewById(R.id.infoAdd);
        layoutInfoAdd= view.findViewById(R.id.layoutInfoAdd);
        layoutMessage= view.findViewById(R.id.layoutMessage);
        userTitle= view.findViewById(R.id.nameTitle);



        if(getArguments().getString("type").compareTo("get point")==0){
            nameDetail.setText("Nhận điểm");
            pointDetail.setText("+"+getArguments().getInt("point"));
            codeDetail.setText(getArguments().getString("code"));
            timeDetail.setText(getArguments().getString("createdAt"));
            infoAdd.setVisibility(View.INVISIBLE);
            layoutInfoAdd.setVisibility(View.INVISIBLE);
            messageTitle.setVisibility(View.INVISIBLE);
            layoutMessage.setVisibility(View.INVISIBLE);



        }
        //đổi điểm
        else if(getArguments().getString("type").compareTo("get voucher")==0){
            nameDetail.setText("Đổi Voucher");
            pointDetail.setText("-"+getArguments().getInt("point"));
            codeDetail.setText(getArguments().getString("code"));
            timeDetail.setText(getArguments().getString("createdAt"));
            infoAdd.setVisibility(View.INVISIBLE);
            layoutInfoAdd.setVisibility(View.INVISIBLE);
            messageTitle.setVisibility(View.INVISIBLE);
            layoutMessage.setVisibility(View.INVISIBLE);
        }
        //chuyển điểm
        else if(getArguments().getString("type").compareTo("transfer point")==0){
            if(getArguments().getString("sender")==null) {
                nameDetail.setText("Chuyển điểm");
                pointDetail.setText("-"+getArguments().getInt("point"));
                codeDetail.setText(getArguments().getString("code"));
                timeDetail.setText(getArguments().getString("createdAt"));
                nameUser.setText(getArguments().getString("receive"));
                phoneUser.setText(getArguments().getString("phone"));
                pointUser.setText(getArguments().getInt("point")+"");
                message.setText(getArguments().getString("message"));
                userTitle.setText("Người nhận");


            }
            else{
                nameDetail.setText("Chuyển điểm");
                pointDetail.setText("+"+getArguments().getInt("point"));
                codeDetail.setText(getArguments().getString("code"));
                timeDetail.setText(getArguments().getString("createdAt"));
                nameUser.setText(getArguments().getString("receive"));
                phoneUser.setText(getArguments().getString("phone"));
                pointUser.setText(getArguments().getInt("point")+"");
                message.setText(getArguments().getString("message"));
            }


        }
        //mua điểm
        else if(getArguments().getString("type").compareTo("buy point")==0){
            nameDetail.setText("Mua điểm");
            pointDetail.setText("+"+getArguments().getInt("point"));
            codeDetail.setText(getArguments().getString("code"));
            timeDetail.setText(getArguments().getString("createdAt"));
            infoAdd.setVisibility(View.INVISIBLE);
            layoutInfoAdd.setVisibility(View.INVISIBLE);
            messageTitle.setVisibility(View.INVISIBLE);
            layoutMessage.setVisibility(View.INVISIBLE);


        }
        //sử dụng voucher
        else if(getArguments().getString("type").compareTo("use voucher")==0){
            nameDetail.setText("Sử dụng Voucher");
            pointDetail.setText("");
            codeDetail.setText(getArguments().getString("code"));
            timeDetail.setText(getArguments().getString("createdAt"));
            infoAdd.setVisibility(View.INVISIBLE);
            layoutInfoAdd.setVisibility(View.INVISIBLE);
            messageTitle.setVisibility(View.INVISIBLE);
            layoutMessage.setVisibility(View.INVISIBLE);


        }
        else if(getArguments().getString("type").compareTo("get gift")==0){
            nameDetail.setText("Nhận phần thưởng");
            pointDetail.setText("+"+getArguments().getInt("point"));
            codeDetail.setText(getArguments().getString("code"));
            timeDetail.setText(getArguments().getString("createdAt"));
            infoAdd.setVisibility(View.INVISIBLE);
            layoutInfoAdd.setVisibility(View.INVISIBLE);
            messageTitle.setVisibility(View.INVISIBLE);
            layoutMessage.setVisibility(View.INVISIBLE);



        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_deal, container, false);

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
