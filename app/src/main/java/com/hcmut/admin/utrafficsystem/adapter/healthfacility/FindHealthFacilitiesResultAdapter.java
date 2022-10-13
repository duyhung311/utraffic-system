package com.hcmut.admin.utrafficsystem.adapter.healthfacility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.MarkerCreating;
import com.hcmut.admin.utrafficsystem.model.HealthFacility;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.BottomSheetPhoneNumberFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FindHealthFacilitiesResultAdapter extends RecyclerView.Adapter<FindHealthFacilitiesResultAdapter.FindHealthFacilitiesResultViewHolder>{
    private Context mContext;
    private Adapter adapter;
    private Fragment fragment;
    private List<HealthFacility> healthFacilityList;
    private BottomSheetDialog bottomSheetDialog;
    private GoogleMap map;

    private MarkerCreating markerCreating;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    public FindHealthFacilitiesResultAdapter(Context mContext, Adapter adapter, Fragment fragment, List<HealthFacility> healthFacilityList,BottomSheetDialog bottomSheetDialog, GoogleMap map) {
        Collections.sort(healthFacilityList, new Comparator<HealthFacility>() {
            @Override
            public int compare(HealthFacility o1, HealthFacility o2) {
                return o1.getRate() > o2.getRate() ? -1 : (o1.getRate() < o2.getRate()) ? 1 : 0;
            }
        });
        this.mContext = mContext;
        this.adapter = adapter;
        this.fragment = fragment;
        this.healthFacilityList = healthFacilityList;
        this.bottomSheetDialog = bottomSheetDialog;
        this.map = map;
    }

    @NonNull
    @Override
    public FindHealthFacilitiesResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_health_facilities_result, parent, false);
        return new FindHealthFacilitiesResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindHealthFacilitiesResultViewHolder holder, int position) {
        final HealthFacility item  = healthFacilityList.get(position);
        if(item == null){
            return;
        }

        holder.nameHF.setText(item.getName());
        holder.addressHF.setText(item.getAddress());

        if (item.getWork_time().equals("")){
            holder.work_timeHF.setText("Đang cập nhật");
        }else{
            holder.work_timeHF.setText(item.getWork_time());
        }

        if(Arrays.toString(item.getSpecialisation()).equals("")){
            holder.specialisationHF.setText("Đang cập nhật");
        }else{
            String specialisation = Arrays.toString(item.getSpecialisation()).replace("[","").replace("]","");
            handleViewMore(holder.specialisationHF, specialisation);
        }

        if (item.getService().equals("")){
            holder.serviceHF.setText("Đang cập nhật");
        } else{
            handleViewMore(holder.serviceHF, item.getService());
        }

        if (item.getPhone_number().equals("")){
            holder.phone_numberHF.setText("Đang cập nhật");
        }else{
            holder.phone_numberHF.setText(item.getPhone_number());
        }

        holder.phone_numberHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    Objects.requireNonNull(holder).phone_numberHF.setEnabled(true);
                } else {
                    holder.phone_numberHF.setEnabled(false);
                    ActivityCompat.requestPermissions((Activity) mContext.getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }

                Bundle bundle = new Bundle();
                if(bundle != null){

                    String phoneNumber = item.getPhone_number().replace(".","").replace("\n","").replace("và"," - ");
                    if (phoneNumber.length() > 13){
                        ArrayList<String> listPhoneNumber = new ArrayList<String>(Arrays.asList(phoneNumber.split(" - ")));
                        bundle.putStringArrayList("phoneNumber", listPhoneNumber);

                        BottomSheetPhoneNumberFragment btsPhoneNumber = new BottomSheetPhoneNumberFragment(adapter, mContext, listPhoneNumber);
                        btsPhoneNumber.show(fragment.getFragmentManager(), btsPhoneNumber.getTag());
                    }else{
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                                String dial = "tel:" + phoneNumber;
                                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "Quyền cuộc gọi điện thoại bị từ chối", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), "Nhập số điện thoại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        holder.rating.setText(String.valueOf(item.getRate()));
        holder.tvNumberOfRating.setText(item.getNumberOfRate() + " đánh giá");
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(item.getRate())));

        holder.cvFindHealthFacilitiesResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bottomSheetDialog.dismiss();
                showHealthFacilityOnMap(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(healthFacilityList != null){
            return healthFacilityList.size();
        }
        return 0;
    }

    public class FindHealthFacilitiesResultViewHolder extends RecyclerView.ViewHolder{
        private TextView nameHF;
        private TextView addressHF;
        private TextView work_timeHF;
        private TextView specialisationHF;
        private TextView serviceHF;
        private TextView phone_numberHF;
        private TextView rating;
        private RatingBar ratingBar;
        private TextView tvNumberOfRating;
        private CardView cvFindHealthFacilitiesResult;

        public FindHealthFacilitiesResultViewHolder(@NonNull View itemView) {
            super(itemView);

            nameHF = itemView.findViewById(R.id.nameHF);
            addressHF = itemView.findViewById(R.id.addressHF);
            work_timeHF = itemView.findViewById(R.id.work_timeHF);
            specialisationHF = itemView.findViewById(R.id.specialisationHF);
            serviceHF = itemView.findViewById(R.id.serviceHF);
            phone_numberHF = itemView.findViewById(R.id.phone_numberHF);
            rating = itemView.findViewById(R.id.tv_rating);
            ratingBar = itemView.findViewById(R.id.ratingBar);;
            tvNumberOfRating = itemView.findViewById(R.id.tvNumberOfRating);
            cvFindHealthFacilitiesResult = itemView.findViewById(R.id.cvFindHealthFacilitiesResult);
        }
    }

    private void handleViewMore(final TextView textView, final String string){
        if (string.length() > 80) {
            textView.setText(Html.fromHtml(string.substring(0, 80)+"..."+"<font color='blue'> <u>Xem thêm</u></font>"));
        }
        else textView.setText(string);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().toString().endsWith("Xem thêm")) {
                    textView.setText(Html.fromHtml(string +"<font color='blue'> <u>Thu gọn</u></font>"));
                }
                else {
                    if (string.length() > 80) {
                        textView.setText(Html.fromHtml(string.substring(0, 80)+"..."+"<font color='blue'> <u>Xem thêm</u></font>"));
                    }
                    else textView.setText(string);
                }
            }
        });
    }

    private void showHealthFacilityOnMap(final HealthFacility item){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(item.getLatitude(), item.getLongitude()), 17));
        if(markerCreating != null){
            markerCreating.removeMarker();
        }
        markerCreating = new MarkerCreating(item.getLatLng());
        markerCreating.createHealthFacilityMarker(fragment.getContext(), map, R.drawable.ic_chosen_point, false, false,item,1.0f);
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
