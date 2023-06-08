package com.hcmut.admin.utraffictest.ui.partner.healthfacility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.adapter.healthfacility.MyHealthFacilitiesAdapter;
import com.hcmut.admin.utraffictest.model.HealthFacility;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHealthFaciliesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHealthFaciliesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatImageButton btnBack;
    private RecyclerView rcvMyHealthFacilities;
    private MyHealthFacilitiesAdapter adapter;
    private List<HealthFacility> myHealthFacilities;

    public MyHealthFaciliesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisteredHealthFaciliesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyHealthFaciliesFragment newInstance(String param1, String param2) {
        MyHealthFaciliesFragment fragment = new MyHealthFaciliesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_health_facilies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack =(AppCompatImageButton) view.findViewById(R.id.btnBack);
        rcvMyHealthFacilities = view.findViewById(R.id.rcvMyHealthFacilities);

        setRecyclerView();

        addEvent();
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvMyHealthFacilities.setLayoutManager(linearLayoutManager);
        String partnerId= SharedPrefUtils.getUser(getContext()).getUserId();

        RetrofitClient.getAPIHealthFacilities().getHealthFacilityByPartnerId(partnerId).enqueue(new Callback<List<HealthFacility>>() {
            @Override
            public void onResponse(Call<List<HealthFacility>> call, Response<List<HealthFacility>> response) {
                myHealthFacilities = response.body();
                adapter = new MyHealthFacilitiesAdapter(myHealthFacilities,getContext());
                rcvMyHealthFacilities.setAdapter(adapter);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
                rcvMyHealthFacilities.addItemDecoration(itemDecoration);
            }

            @Override
            public void onFailure(Call<List<HealthFacility>> call, Throwable t) {
                Toast.makeText(getContext(), "Bạn không có cơ sở y tế nào.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MyHealthFaciliesFragment.this).popBackStack();
            }
        });
    }



}