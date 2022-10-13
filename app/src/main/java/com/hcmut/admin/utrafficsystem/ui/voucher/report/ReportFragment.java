package com.hcmut.admin.utrafficsystem.ui.voucher.report;

import android.graphics.Color;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponseVoucher;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment implements MapActivity.OnBackPressCallback {
    Toolbar toolbar;
    BarChart barChart;
    AndroidExt androidExt = new AndroidExt();
    String[] days;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");

    ArrayList<BarEntry> barEntriesIn;
    ArrayList<BarEntry> barEntriesOut;

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(ReportFragment.this).popBackStack();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Báo cáo");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        barChart = view.findViewById(R.id.barchart);


        days = new String[]{"1","2","3","4","5","6","7"};
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        getReportVoucher();

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
    private void getReportVoucher(){
        RetrofitClient.getApiService().getReportVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse<List<ReportResponseVoucher>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<ReportResponseVoucher>>> call, Response<BaseResponse<List<ReportResponseVoucher>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                List<ReportResponseVoucher> responseVouchers= response.body().getData();
                                barEntriesIn = new ArrayList<>();
                                barEntriesOut = new ArrayList<>();
                                for(int i=0;i<responseVouchers.size();i++){
                                    System.out.println(responseVouchers.get(i).getDate());
                                    days[i]=formatter.format(responseVouchers.get(i).getDate());
                                    System.out.println(responseVouchers.get(i).getPointOut());

                                    barEntriesIn.add(new BarEntry(i,responseVouchers.get(i).getPointIn()));
                                    barEntriesOut.add(new BarEntry(i,responseVouchers.get(i).getPointOut()));
                                }

                                BarDataSet barDataSet1 = new BarDataSet(barEntriesIn,"Điểm kiếm được");
                                BarDataSet barDataSet2 = new BarDataSet(barEntriesOut,"Điểm sử dụng");
                                barDataSet1.setColor(Color.BLUE);
                                barDataSet2.setColor(Color.RED);
                                BarData barData = new BarData(barDataSet1,barDataSet2);
                                barChart.setData(barData);


                                XAxis xAxis = barChart.getXAxis();
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
                                xAxis.setCenterAxisLabels(true);
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setGranularity(1);
                                xAxis.setGranularityEnabled(true);

                                barChart.setDragEnabled(true);
                                barChart.setVisibleXRangeMaximum(3);
                                float barSpace = 0.0f;
                                float groupSpace =0.4f;
                                barData.setBarWidth(0.3f);
                                barChart.getXAxis().setAxisMinimum(0);
                                barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*7);
                                barChart.getAxisLeft().setAxisMinimum(0);
                                barChart.groupBars(0,groupSpace,barSpace);


                                barChart.invalidate();


                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<ReportResponseVoucher>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }
}
