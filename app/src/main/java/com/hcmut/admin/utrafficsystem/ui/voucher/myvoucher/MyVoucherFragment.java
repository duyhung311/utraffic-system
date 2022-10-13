package com.hcmut.admin.utrafficsystem.ui.voucher.myvoucher;

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
import com.hcmut.admin.utrafficsystem.adapter.MyVoucherAdapter;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.MyVoucherResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVoucherFragment extends Fragment implements MyVoucherAdapter.OrderAdapterOnClickHandler, MapActivity.OnBackPressCallback {
    MyVoucherAdapter myVoucherAdapter;
    ArrayList<MyVoucherResponse> listVoucher;
    RecyclerView listViewAll;
    Toolbar toolbar;
    AndroidExt androidExt = new AndroidExt();
    Bundle bundle = new Bundle();

    @Override
    public void onClick(MyVoucherResponse order) {
        bundle.putString("idOffer", order.getId());
        NavHostFragment.findNavController(MyVoucherFragment.this)
                .navigate(R.id.action_myVoucherFragment_to_detailMyVoucherFragment,bundle);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Voucher");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        listViewAll = view.findViewById(R.id.myVoucherList);

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
        listVoucher = new ArrayList<>();

//        listVoucher.add(new MyVoucher(1, "Giảm giá 1", "1234567","1/1/2020",500,"1/1/2020","thanh"));
//        listVoucher.add(new MyVoucher(2, "Giảm giá 2", "9876543","1/1/2020",600,"1/1/2020","thanh"));
//        listVoucher.add(new MyVoucher(3, "Giảm giá 3", "1234789","1/1/2020",700,"1/1/2020","thanh"));
//        listVoucher.add(new MyVoucher(4, "Giảm giá 4", "4564564","1/1/2020",800,"1/1/2020","thanh"));
//        listVoucher.add(new MyVoucher(5, "Giảm giá 5", "2342343","1/1/2020",900,"1/1/2020","thanh"));
            myVoucherAdapter = new MyVoucherAdapter(getContext(), listVoucher,this);
            listViewAll.setAdapter(myVoucherAdapter);
        getMyVoucher();


    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(MyVoucherFragment.this).popBackStack();
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
    private void getMyVoucher(){
        RetrofitClient.getApiService().getMyVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse<List<MyVoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<MyVoucherResponse>>> call, Response<BaseResponse<List<MyVoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {

                                List<MyVoucherResponse> myVoucherResponses = response.body().getData();
                                listVoucher.addAll(myVoucherResponses);
                                myVoucherAdapter.notifyDataSetChanged();


                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<MyVoucherResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }


}
