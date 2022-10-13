package com.hcmut.admin.utrafficsystem.ui.healthfacility.find;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities.RatingRequest;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetRatingFragment extends BottomSheetDialogFragment {
    public static String ID_HF = "idHF";
    public static String ID_ATM = "idAtm";

    public static String HF_OBJECT = "HF_Object";
    public static String ATM_OBJECT = "ATM_Object";

    private Bundle bundle;
    private RatingBar ratingBar;
    private String idHF;
    private String idAtm;
    private String ratedObject;

    public BottomSheetRatingFragment(Bundle bundle, String ratedObject) {
        this.bundle = bundle;
        this.ratedObject = ratedObject;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        ratingBar = view.findViewById(R.id.ratingBar);
        addEvent();

        return bottomSheetDialog;
    }

    private void addEvent(){
        if(bundle != null){
            idHF = bundle.getString(ID_HF);
            idAtm = bundle.getString(ID_ATM);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rate, boolean fromUser) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang xử lý..!", true);

                if(ratedObject == HF_OBJECT) {
                    RetrofitClient.getAPIHealthFacilities().ratingHF(new RatingRequest(idHF, rate))
                            .enqueue(new Callback<BaseResponse<Object>>() {
                                @Override
                                public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                                    progressDialog.dismiss();
                                    try {
                                        if (response.body() != null) {
                                            MapActivity.androidExt.showMessageNoAction(
                                                    getContext(),
                                                    "Thông báo",
                                                    "Đánh giá đã được hệ thống ghi nhận! Bạn cần tải lại để xem đánh giá.");
                                        } else {
                                            MapActivity.androidExt.showMessageNoAction(
                                                    getContext(),
                                                    "Thông báo",
                                                    "Không thể đánh giá, vui lòng kiểm tra lại đường truyền");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                                    progressDialog.dismiss();
                                    try {
                                        MapActivity.androidExt.showMessageNoAction(
                                                getContext(), "Thông báo", "Kết nối thất bại, vui lòng thử lại");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }else if(ratedObject == ATM_OBJECT){
                        RetrofitClient.getAPIAtm().ratingAtm(new RatingRequest(idAtm, rate))
                                .enqueue(new Callback<BaseResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                                        progressDialog.dismiss();
                                        try {
                                            if (response.body() != null) {
                                                MapActivity.androidExt.showMessageNoAction(
                                                        getContext(),
                                                        "Thông báo",
                                                        "Đánh giá đã được hệ thống ghi nhận! Bạn cần tải lại để xem đánh giá.");
                                            } else {
                                                MapActivity.androidExt.showMessageNoAction(
                                                        getContext(),
                                                        "Thông báo",
                                                        "Không thể đánh giá, vui lòng kiểm tra lại đường truyền");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                                        progressDialog.dismiss();
                                        try {
                                            MapActivity.androidExt.showMessageNoAction(
                                                    getContext(), "Thông báo", "Kết nối thất bại, vui lòng thử lại");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                }
            }
        });
    }
}
