package com.hcmut.admin.utrafficsystem.adapter.atm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.BottomSheetPhoneNumberFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AtmListAdapter extends RecyclerView.Adapter<AtmListAdapter.AtmListViewHolder>{
    private Context mContext;
    private Fragment AtmListAdapterFragment;
    private List<Atm> atmList;
    private BottomSheetDialog bottomSheetDialog;
    private GoogleMap map;
    private Adapter AtmListAdapter;

    private MarkerCreating markerCreating;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    public AtmListAdapter(Context mContext,Adapter AtmListAdapter, Fragment AtmListAdapterFragment, List<Atm> atmList, BottomSheetDialog bottomSheetDialog, GoogleMap map) {
        Collections.sort(atmList, new Comparator<Atm>() {
            @Override
            public int compare(Atm o1, Atm o2) {
                return o1.getRate() > o2.getRate() ? -1 : (o1.getRate() < o2.getRate()) ? 1 : 0;
            }
        });
        this.mContext = mContext;
        this.AtmListAdapter = AtmListAdapter;
        this.AtmListAdapterFragment = AtmListAdapterFragment;
        this.atmList = atmList;
        this.bottomSheetDialog = bottomSheetDialog;
        this.map = map;
    }

    @NonNull
    @Override
    public AtmListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atm_list, parent, false);
        return new AtmListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AtmListViewHolder holder, int position) {
        final Atm atm  = atmList.get(position);
        if(atm == null){
            return;
        }

        holder.nameAtm.setText(atm.getName());
        holder.addressAtm.setText(atm.getAddress());
        holder.workTimeAtm.setText(atm.getWork_time());

        if (atm.getPhone_number().equals("")){
            holder.phoneNumberAtm.setText("Đang cập nhật");
        }else{
            holder.phoneNumberAtm.setText( atm.getPhone_number().replace("và"," - "));
        }

        holder.phoneNumberAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    holder.phoneNumberAtm.setEnabled(true);
                } else {
                    holder.phoneNumberAtm.setEnabled(false);
                    ActivityCompat.requestPermissions((Activity) mContext.getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }

                Bundle bundle = new Bundle();
                if(bundle != null){

                    String phoneNumber = atm.getPhone_number().replace(".","").replace("\n","").replace("và"," - ");
                    if (phoneNumber.length() > 13){
                        ArrayList<String> listPhoneNumber = new ArrayList<String>(Arrays.asList(phoneNumber.split(" - ")));
                        bundle.putStringArrayList("phoneNumber", listPhoneNumber);

                        BottomSheetPhoneNumberFragment btsPhoneNumber = new BottomSheetPhoneNumberFragment(AtmListAdapter, mContext, listPhoneNumber);
                        btsPhoneNumber.show(AtmListAdapterFragment.getFragmentManager(), btsPhoneNumber.getTag());
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

        holder.tvRating.setText(String.valueOf(atm.getRate()));
        holder.tvNumberOfRating.setText(atm.getNumberOfRate() + " đánh giá");
        holder.ratingAtm.setRating(Float.parseFloat(String.valueOf(atm.getRate())));

        holder.cardViewAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bottomSheetDialog.dismiss();
                showAtmOnMap(atm);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(atmList != null){
            return atmList.size();
        }
        return 0;
    }

    public class AtmListViewHolder extends RecyclerView.ViewHolder{
        public TextView nameAtm;
        public TextView addressAtm;
        public TextView workTimeAtm;
        public TextView phoneNumberAtm;
        public TextView tvRating;
        public RatingBar ratingAtm;
        public TextView tvNumberOfRating;
        public CardView cardViewAtm;

        public AtmListViewHolder(@NonNull View itemView) {
            super(itemView);

            nameAtm = itemView.findViewById(R.id.nameAtm);
            addressAtm = itemView.findViewById(R.id.addressAtm);
            workTimeAtm = itemView.findViewById(R.id.workTimeAtm);
            phoneNumberAtm = itemView.findViewById(R.id.phoneNumberAtm);
            tvRating = itemView.findViewById(R.id.tvRating);
            ratingAtm = itemView.findViewById(R.id.ratingAtm);;
            tvNumberOfRating = itemView.findViewById(R.id.tvNumberOfRating);
            cardViewAtm = itemView.findViewById(R.id.cardViewAtm);
        }
    }

    private void showAtmOnMap(final Atm item){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(item.getLatitude(), item.getLongitude()), 17));
        if(markerCreating != null){
            markerCreating.removeMarker();
        }
        markerCreating = new MarkerCreating(item.getLatLng());
        markerCreating.createAtmMarker(AtmListAdapterFragment.getContext(), map, R.drawable.ic_chosen_point, false, false,item,1.0f);
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
