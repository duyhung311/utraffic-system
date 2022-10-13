package com.hcmut.admin.utrafficsystem.adapter.atm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.ui.direction.DirectionFragment;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.BottomSheetPhoneNumberFragment;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class AtmAdapter extends RecyclerView.Adapter<AtmAdapter.ViewHolder> {

    private Context mContext;
    final ArrayList<Atm> listAtm;
    private AtmAdapterOnClickHandler clickHandler;
    private Fragment AtmAdapterFragment;
    private Adapter AtmAdapter;

    private GoogleMap map;

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    public AtmAdapter(Context mContext, ArrayList<Atm> listAtm, AtmAdapterOnClickHandler clickHandler, Fragment AtmAdapterFragment) {
        super();
        this.listAtm = listAtm;
        this.mContext = mContext;
        this.clickHandler = clickHandler;
        this.AtmAdapterFragment = AtmAdapterFragment;
    }

    public AtmAdapter(Context mContext, ArrayList<Atm> listAtm, AtmAdapterOnClickHandler clickHandler) {
        super();
        this.listAtm = listAtm;
        this.mContext = mContext;
        this.clickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View atmView = inflater.inflate(R.layout.item_atm, parent, false);
        ViewHolder atmViewHolder = new ViewHolder(atmView);
        return atmViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Atm atm = listAtm.get(position);
        holder.nameAtm.setText(atm.getName());
        holder.addressAtm.setText(atm.getAddress());
        holder.numberAtm.setText(atm.getNumber_atm().toString() + " máy");
        holder.workTimeAtm.setText(atm.getWork_time());
        holder.branchAtm.setText(atm.getBranch_atm());
        holder.phoneNumberAtm.setText(atm.getPhone_number().replace("và"," - "));

        holder.tvRating.setText(String.valueOf(atm.getRate()));
        holder.tvNumberOfRating.setText(atm.getNumberOfRate() + " đánh giá");
        holder.ratingAtm.setRating(Float.parseFloat(String.valueOf(atm.getRate())));

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

                        BottomSheetPhoneNumberFragment btsPhoneNumber = new BottomSheetPhoneNumberFragment(AtmAdapter, mContext, listPhoneNumber);
                        btsPhoneNumber.show(AtmAdapterFragment.getFragmentManager(), btsPhoneNumber.getTag());
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


        holder.directAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = (MapActivity) mContext;
                mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;
                    }
                });
                if (MapUtil.checkGPSTurnOn(AtmAdapterFragment.getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(AtmAdapterFragment.getActivity())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        if (map != null) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString(DirectionFragment.CURRENT_ADDRESS, "Vị trí của bạn");
                                            bundle.putParcelable(DirectionFragment.CURRENT_LATLNG, new LatLng(location.getLatitude(), location.getLongitude()));
                                            bundle.putString(DirectionFragment.DESTINATION_ADDRESS, atm.getAddress());
                                            bundle.putParcelable(DirectionFragment.DESTINATION_LATLNG, new LatLng(atm.getLatitude(), atm.getLongitude()));
                                            NavHostFragment.findNavController(AtmAdapterFragment)
                                                    .navigate(R.id.action_homeAtmFragment_to_directionFragment, bundle);
                                        } else {
                                            Toast.makeText(AtmAdapterFragment.getContext(),
                                                    "Bản đồ chưa được tải lên, vui lòng thử lại",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(AtmAdapterFragment.getContext(),
                                                "Không thể lấy vị trí, vui lòng thử lại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAtm.size();
    }

    public interface AtmAdapterOnClickHandler {
        void onClick(Atm atm);
    }

    /**
     * Lớp nắm giữ cấu trúc view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameAtm;
        public TextView addressAtm;
        public TextView numberAtm;
        public TextView workTimeAtm;
        public TextView branchAtm;
        public TextView phoneNumberAtm;
        public TextView tvRating;
        public RatingBar ratingAtm;
        public TextView tvNumberOfRating;
        public ImageView directAtm;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nameAtm = itemView.findViewById(R.id.nameAtm);
            addressAtm = itemView.findViewById(R.id.addressAtm);
            numberAtm = itemView.findViewById(R.id.numberAtm);
            workTimeAtm = itemView.findViewById(R.id.workTimeAtm);
            branchAtm = itemView.findViewById(R.id.branchAtm);
            phoneNumberAtm = itemView.findViewById(R.id.phoneNumberAtm);
            tvRating = itemView.findViewById(R.id.tvRating);
            ratingAtm = itemView.findViewById(R.id.ratingAtm);;
            tvNumberOfRating = itemView.findViewById(R.id.tvNumberOfRating);
            directAtm = itemView.findViewById(R.id.directAtm);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Atm atm = listAtm.get(position);
            switch (view.getId()) {
                case R.id.cardViewAtm: clickHandler.onClick(atm);
            }
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

}
