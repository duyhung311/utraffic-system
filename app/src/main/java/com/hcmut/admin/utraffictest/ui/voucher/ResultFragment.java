package com.hcmut.admin.utraffictest.ui.voucher;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.TestVoucherAdapter;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultFragment extends Fragment implements  MapActivity.OnBackPressCallback {
    RecyclerView listOfSearchedList;
    TestVoucherAdapter searchAdapter;
    ArrayList<VoucherResponse> listSearch;
    Toolbar toolbar;
    AndroidExt androidExt = new AndroidExt();
    Bundle bundle = new Bundle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listOfSearchedList= view.findViewById(R.id.listOfSearchedList);

        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kết quả tìm kiếm");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        Search(getArguments().getString("word"));
    }
    private void Search(String word) {

      listOfSearchedList.setHasFixedSize(true);
      listOfSearchedList.setLayoutManager(new GridLayoutManager(getContext(), (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));

        listSearch = new ArrayList<>();
//        listSearch.add(new Voucher(1, "Giảm giá 1", 500,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listSearch.add(new Voucher(2, "Giảm giá 2", 600,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listSearch.add(new Voucher(3, "Giảm giá 3", 700,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listSearch.add(new Voucher(4, "Giảm giá 4", 800,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listSearch.add(new Voucher(5, "Giảm giá 5", 900,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));

      searchAdapter = new TestVoucherAdapter( listSearch,
              getContext(),
              new TestVoucherAdapter.ProductAdapterOnClickHandler() {

                  @Override
                  public void onClick(VoucherResponse voucher) {
                      bundle.putString("idVoucher", voucher.getId());
              NavHostFragment.findNavController(ResultFragment.this)
                      .navigate(R.id.action_resultFragment_to_detailVoucherFragment,bundle);

                  };


              },R.layout.voucher_list_item);
      listOfSearchedList.setAdapter(searchAdapter);
      getSearchVoucher(word);
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
        NavHostFragment.findNavController(ResultFragment.this).popBackStack();

    }
    private void getSearchVoucher(String word){
        RetrofitClient.getApiService().getSearchVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken(),word)
                .enqueue(new Callback<BaseResponse<List<VoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<VoucherResponse>>> call, Response<BaseResponse<List<VoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {

                                List<VoucherResponse> voucherResponse = response.body().getData();

                                if(voucherResponse.size()!=0) {
                                    System.out.println(voucherResponse.get(0).getName());
                                    listSearch.clear();
                                    listSearch.addAll(voucherResponse);
                                    searchAdapter.notifyDataSetChanged();

                                }else{
                                    Toast.makeText(getContext(), "No Result", Toast.LENGTH_SHORT).show();
                                }
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
