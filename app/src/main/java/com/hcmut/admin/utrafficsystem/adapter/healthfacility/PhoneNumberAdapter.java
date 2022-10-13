package com.hcmut.admin.utrafficsystem.adapter.healthfacility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;

import java.util.ArrayList;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.PhoneNumberViewHolder> {
    private Fragment fragment;
    private Context mContext;
    private Adapter adapter;
    private ArrayList<String> listPhoneNumber;

    public PhoneNumberAdapter(Fragment fragment, Context mContext, Adapter adapter, ArrayList<String> listPhoneNumber) {
        this.fragment = fragment;
        this.mContext= mContext;
        this.adapter = adapter;
        this.listPhoneNumber = listPhoneNumber;
    }

    @NonNull
    @Override
    public PhoneNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_number, parent, false);
        return new PhoneNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneNumberViewHolder holder, int position) {
        final String item  = listPhoneNumber.get(position);
        if(item == null){
            return;
        }

        holder.tvPhoneNumber.setText(item);

        holder.tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item)) {
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        String dial = "tel:" + item;
                        mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(fragment.getContext(), "Quyền cuộc gọi điện thoại bị từ chối", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(fragment.getContext(), "Nhập số điện thoại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listPhoneNumber != null){
            return listPhoneNumber.size();
        }
        return 0;
    }

    public class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPhoneNumber;

        public PhoneNumberViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
