package com.hcmut.admin.utraffictest.ui.healthfacility.search;

import android.content.res.Configuration;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.healthfacility.ResultSearchHealthFacilitiesAdapter;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.model.HealthFacility;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultHealthFacilitiesFragment extends Fragment implements  MapActivity.OnBackPressCallback {

    Toolbar toolbar;
    RecyclerView listOfSearchedListHF;
    ResultSearchHealthFacilitiesAdapter searchAdapter;
    ArrayList<HealthFacility> listSearchHF;

    AndroidExt androidExt = new AndroidExt();
    private GoogleMap map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_search_hf, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolBarResultSearchHF);
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

        listOfSearchedListHF = view.findViewById(R.id.listOfSearchedListHF);

        Search(getArguments().getString("word"));
    }
    private void Search(String word) {

        listOfSearchedListHF.setHasFixedSize(true);
        listOfSearchedListHF.setLayoutManager(new GridLayoutManager(getContext(), (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 1 : 1));

        listSearchHF = new ArrayList<>();

        getSearchHF(word);

        searchAdapter = new ResultSearchHealthFacilitiesAdapter(listSearchHF,this);
        listOfSearchedListHF.setAdapter(searchAdapter);
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
        NavHostFragment.findNavController(ResultHealthFacilitiesFragment.this).popBackStack();
    }

    private void getSearchHF(final String nameHF){
        RetrofitClient.getAPIHealthFacilities().getSearchHF(nameHF)
                .enqueue(new Callback<List<HealthFacility>>() {
                    @Override
                    public void onResponse(Call<List<HealthFacility>> call, Response<List<HealthFacility>> response) {
                        if (response.body() != null) {
                                List<HealthFacility> hfResponse = response.body();
                            if(hfResponse.size() != 0) {
                                    System.out.println(hfResponse.get(0).getName());
                                    listSearchHF.clear();
                                    listSearchHF.addAll(hfResponse);
                                    searchAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Kết quả không tìm thấy, vui lòng thử lại!");
                                NavHostFragment.findNavController(ResultHealthFacilitiesFragment.this).popBackStack();
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<List<HealthFacility>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
