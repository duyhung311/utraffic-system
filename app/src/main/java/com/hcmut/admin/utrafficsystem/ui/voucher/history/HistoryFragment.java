package com.hcmut.admin.utrafficsystem.ui.voucher.history;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.DealAdapter;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DealResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements DealAdapter.DealAdapterOnClickHandler, MapActivity.OnBackPressCallback {
    DealAdapter dealAdapter;
    ArrayList<DealResponse> listDeal;
    RecyclerView listViewAll;
    Bundle bundle = new Bundle();
    AndroidExt androidExt = new AndroidExt();
    Toolbar toolbar;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
    @Override
    public void onClick(DealResponse deal) {
        bundle.putInt("point",deal.getPoint());
        bundle.putString("code",deal.getCode());
        bundle.putString("type",deal.getType());
        bundle.putString("createdAt",formatter.format(deal.getCreatedAt()));
        if(deal.getType().compareTo("transfer point")==0){
            if(SharedPrefUtils.getUser(getContext()).getUserId().compareTo(deal.getSender().getName())==0) {
                bundle.putString("sender", deal.getReceive().getName());
                bundle.putString("phone", deal.getReceive().getPhoneNumber());
                bundle.putString("message", deal.getMessage());
            }else{
                bundle.putString("receive", deal.getSender().getName());
                bundle.putString("phone", deal.getSender().getPhoneNumber());
                bundle.putString("message", deal.getMessage());
            }
        }
        NavHostFragment.findNavController(HistoryFragment.this)
                .navigate(R.id.action_historyFragment_to_detailDealFragment,bundle);
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(HistoryFragment.this).popBackStack();

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Lịch sử giao dịch");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        listViewAll = view.findViewById(R.id.listDeal);
        setUpRecycleView();
        getOrders();
    }
    private void setUpRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listViewAll.setLayoutManager(layoutManager);
        listViewAll.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        listViewAll.addItemDecoration(dividerItemDecoration);
    }

    private void getOrders() {
        listDeal = new ArrayList<>();
//        listDeal.add(new Deal(1, 1, "1/1/2020 15:30",500,"Giao dịch nhận điểm"));
//        listDeal.add(new Deal(2, 2, "1/1/2020 15:30",500,"Giao dịch đổi điểm"));
//        listDeal.add(new Deal(3, 3, "1/1/2020 15:30",500,"Giao dịch chuyển điểm"));
//        listDeal.add(new Deal(4, 4, "1/1/2020 15:30",500,"Giao dịch mua điểm"));
//        listDeal.add(new Deal(5, 5, "1/1/2020 15:30",500,"Sử dụng voucher"));
        dealAdapter = new DealAdapter(getContext(), listDeal,this);
        listViewAll.setAdapter(dealAdapter);
        dealAdapter.notifyDataSetChanged();
        getDealVoucher();

    }
    private void getDealVoucher(){
        RetrofitClient.getApiService().getDealVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse<List<DealResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DealResponse>>> call, Response<BaseResponse<List<DealResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
//                                bundle.putString("point",point.getText().toString());
//                                bundle.putString("message",content.getText().toString());
//                                bundle.putString("receive",getArguments().getString("id"));
//                                NavHostFragment.findNavController(TransferPointFragment.this)
//                                        .navigate(R.id.action_transferPointFragment_to_transferOTPFragment,bundle);
                                List<DealResponse> dealResponses = response.body().getData();
                                listDeal.clear();
                                listDeal.addAll(dealResponses);
                                dealAdapter.notifyDataSetChanged();

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<DealResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }
}
