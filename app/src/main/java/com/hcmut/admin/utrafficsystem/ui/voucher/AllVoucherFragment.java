package com.hcmut.admin.utrafficsystem.ui.voucher;

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
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.TestVoucherAdapter;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllVoucherFragment extends Fragment implements TestVoucherAdapter.ProductAdapterOnClickHandler, MapActivity.OnBackPressCallback {

    TestVoucherAdapter allVoucherAdapter;
    ArrayList<VoucherResponse> listVoucher;
    RecyclerView listViewAll;
    private Toolbar toolbar;
    Bundle bundle = new Bundle();
    AndroidExt androidExt = new AndroidExt();

    @Override
    public void onClick(VoucherResponse voucher) {
        bundle.putString("idVoucher", voucher.getId());
        NavHostFragment.findNavController(AllVoucherFragment.this)
                .navigate(R.id.action_allVoucherFragment_to_detailVoucherFragment,bundle);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_all_voucher, container, false);
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
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbarall);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Danh sách voucher");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        listVoucher = new ArrayList<>();
        if(getArguments().getInt("type")==0){
            getAllTopVoucher();
        }
        else{
            getAllTrendVoucher();
        }
//        listVoucher.add(new Voucher(1, "Giảm giá 1", 500,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(2, "Giảm giá 2", 600,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(3, "Giảm giá 3", 700,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(4, "Giảm giá 4", 800,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(5, "Giảm giá 5", 900,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        allVoucherAdapter=new TestVoucherAdapter(listVoucher,getContext(),AllVoucherFragment.this,R.layout.voucher_list_item);
        listViewAll = view.findViewById(R.id.allvoucher);
        setUpRecycleView();
        listViewAll.setAdapter(allVoucherAdapter);
    }
    private void setUpRecycleView() {
//        listViewAll.setHasFixedSize(true);
//        listViewAll.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        listViewAll.setItemAnimator(null);
    }


    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(AllVoucherFragment.this).popBackStack();
    }
    private void getAllTrendVoucher(){
        RetrofitClient.getApiService().getAllTrendVoucher()
                .enqueue(new Callback<BaseResponse<List<VoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<VoucherResponse>>> call, Response<BaseResponse<List<VoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                List<VoucherResponse> voucherResponse = response.body().getData();
                                listVoucher.clear();
                                listVoucher.addAll(voucherResponse);
                                allVoucherAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<VoucherResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
    private void getAllTopVoucher(){
        RetrofitClient.getApiService().getAllTopVoucher()
                .enqueue(new Callback<BaseResponse<List<VoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<VoucherResponse>>> call, Response<BaseResponse<List<VoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                List<VoucherResponse> voucherResponse = response.body().getData();
                                listVoucher.clear();
                                listVoucher.addAll(voucherResponse);
                                allVoucherAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<VoucherResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
