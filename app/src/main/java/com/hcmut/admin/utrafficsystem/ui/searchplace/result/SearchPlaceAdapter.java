package com.hcmut.admin.utrafficsystem.ui.searchplace.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.utrafficsystem.R;

import java.util.List;

public class SearchPlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private List<AutocompletePrediction> dataSet;
    private OnItemClickCallback onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickCallback onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public SearchPlaceAdapter() {
//        super(context, R.layout.item_place_autocomplete, R.id.tvPlace);
    }

    public void setDataSet(List<AutocompletePrediction> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place_autocomplete, parent, false);
        return new PlaceViewHolder(v, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return (dataSet == null) ? 0 : dataSet.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(AutocompletePrediction itemData);
    }
}