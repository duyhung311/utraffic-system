package com.hcmut.admin.utraffictest.adapter.healthfacility;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.HealthFacility;

import java.util.ArrayList;

public class ResultSearchHealthFacilitiesAdapter extends RecyclerView.Adapter<ResultSearchHealthFacilitiesAdapter.ViewHolder> {

    final ArrayList<HealthFacility> listHF;
    private Fragment fragment;

    public ResultSearchHealthFacilitiesAdapter( ArrayList<HealthFacility> listHF, Fragment fragment){
        super();
        this.listHF = listHF;
        this.fragment = fragment;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View resultSearchHFView = inflater.inflate(R.layout.item_place_search_hf, parent, false);
        ViewHolder viewHolder = new ViewHolder(resultSearchHFView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HealthFacility hf = listHF.get(position);
        holder.tvNameHF.setText( hf.getName());
        holder.tvAddressHF.setText( hf.getAddress());

        holder.cardViewResultSearchHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("infor",  hf);
                NavHostFragment.findNavController(fragment).navigate(R.id.action_resultHFFragment_to_healthFacilityInforFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHF.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvNameHF;
        public TextView tvAddressHF;
        public CardView cardViewResultSearchHF;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameHF = itemView.findViewById(R.id.tvNameHF);
            tvAddressHF = itemView.findViewById(R.id.tvAddressHF);
            cardViewResultSearchHF = itemView.findViewById(R.id.cardViewResultSearchHF);
        }
    }

}
