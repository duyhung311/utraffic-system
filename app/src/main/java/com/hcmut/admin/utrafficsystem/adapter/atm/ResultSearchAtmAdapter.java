package com.hcmut.admin.utrafficsystem.adapter.atm;

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

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.Atm;

import java.util.ArrayList;

public class ResultSearchAtmAdapter extends RecyclerView.Adapter<ResultSearchAtmAdapter.ViewHolder> {

    final ArrayList<Atm> listAtm;
    private Fragment fragment;

    public ResultSearchAtmAdapter(ArrayList<Atm> listAtm, Fragment fragment) {
        super();
        this.listAtm = listAtm;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View resultSearchAtmView = inflater.inflate(R.layout.item_place_search_atm, parent, false);
        ViewHolder viewHolder = new ViewHolder(resultSearchAtmView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Atm atm = listAtm.get(position);
        holder.tvNameAtm.setText( atm.getName());
        holder.tvAddressAtm.setText( atm.getAddress());

        holder.cardViewResultSearchAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("infoAtm", atm);
                NavHostFragment.findNavController(fragment).navigate(R.id.action_resultAtmFragment_to_atmInforFragment,bundle);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listAtm.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNameAtm;
        public TextView tvAddressAtm;
        public CardView cardViewResultSearchAtm;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameAtm = itemView.findViewById(R.id.tvNameAtm);
            tvAddressAtm = itemView.findViewById(R.id.tvAddressAtm);
            cardViewResultSearchAtm = itemView.findViewById(R.id.cardViewResultSearchAtm);
        }

    }

}
