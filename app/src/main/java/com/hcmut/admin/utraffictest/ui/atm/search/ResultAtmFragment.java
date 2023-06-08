package com.hcmut.admin.utraffictest.ui.atm.search;

import android.content.res.Configuration;
import android.location.Location;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.atm.ResultSearchAtmAdapter;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.model.Atm;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.LocationCollectionManager;
import com.hcmut.admin.utraffictest.util.MapUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultAtmFragment extends Fragment implements  MapActivity.OnBackPressCallback {

    Toolbar toolbar;
    RecyclerView listOfSearchedListAtm;
    ResultSearchAtmAdapter searchAdapter;
    ArrayList<Atm> listSearchAtm;
    /*boolean isSearchByTime = true;
    TreeMap<Double,Atm> sortedAtmMap;
    ArrayList<Atm> sortedSearchAtmList;*/

    AndroidExt androidExt = new AndroidExt();
    private GoogleMap map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_search_atm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolBarResultSearchAtm);
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

        listOfSearchedListAtm = view.findViewById(R.id.listOfSearchedListAtm);

        Search(getArguments().getString("word"));
    }

    private void Search(final String word) {

        listOfSearchedListAtm.setHasFixedSize(true);
        listOfSearchedListAtm.setLayoutManager(new GridLayoutManager(getContext(), (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 1 : 1));

        listSearchAtm = new ArrayList<>();

        if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
            LocationCollectionManager.getInstance(getContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            getSearchAtm(word,location.getLongitude(),location.getLatitude());
                        }
                    });
        }

        if(listSearchAtm != null){
            searchAdapter = new ResultSearchAtmAdapter(listSearchAtm,this);
            listOfSearchedListAtm.setAdapter(searchAdapter);
        }
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
        NavHostFragment.findNavController(ResultAtmFragment.this).popBackStack();
    }

    private void getSearchAtm(final String nameAtm, final Double longitude , final Double latitude ){
        RetrofitClient.getAPIAtm().getSearchAtm(nameAtm,longitude,latitude)
                .enqueue(new Callback<List<Atm>>() {
                    @Override
                    public void onResponse(Call<List<Atm>> call, Response<List<Atm>> response) {
                        if (response.body() != null) {
                            final List<Atm> atmResponse = response.body();
                            if(atmResponse.size() != 0) {
                                //System.out.println(atmResponse.get(0).getName());
                                listSearchAtm.clear();
                                listSearchAtm.addAll(atmResponse);
                                searchAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Kết quả không tìm thấy, vui lòng thử lại!");
                                NavHostFragment.findNavController(ResultAtmFragment.this).popBackStack();
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Atm>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    /*private void sortSearchAtmList(final LatLng startPoint, List<Atm> listAtm, final boolean isSearchByTime) {
        Log.d("listAtm",listAtm.toString());
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang tìm các ATM", true);
        final Iterator<Atm> iterator = listAtm.iterator();
        while (iterator.hasNext()) {
            final Atm atm = iterator.next();
            LatLng endPoint = new LatLng(atm.getLatitude(), atm.getLongitude());
            SearchDirectionHandler.direct(
                    getContext(),
                    startPoint,
                    endPoint,
                    isSearchByTime,
                    new SearchDirectionHandler.DirectResultCallback() {
                        @Override
                        public void onSuccess(DirectRespose directRespose) {
                            if(isSearchByTime){
                                sortedAtmMap.put(directRespose.getTime(),atm);
                            }else{
                                sortedAtmMap.put((double) directRespose.getDistance(),atm );
                            }
                            if (!iterator.hasNext()) {
                                Log.d("sortedAtmMap",sortedAtmMap.toString());
                                listSearchAtm.clear();
                                for(Map.Entry<Double,Atm> entry : sortedAtmMap.entrySet()) {
                                    listSearchAtm.add(entry.getValue());
                                }
                                Log.d("listSearchAtm",listSearchAtm.toString());
                                searchAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFail() {
                            try {
                                Toast.makeText(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }

                        @Override
                        public void onHaveNoData() {

                        }
                    });

        }
        progressDialog.dismiss();
    }*/
}
