package com.hcmut.admin.utrafficsystem.ui.searchplace.result;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.utrafficsystem.R;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    TextView tvPlace;
    TextView tvPlaceDetail;
    SearchPlaceAdapter.OnItemClickCallback onItemClickListener;
    private AutocompletePrediction currentData;

    public PlaceViewHolder(@NonNull View itemView, final SearchPlaceAdapter.OnItemClickCallback onItemClickListener) {
        super(itemView);
        tvPlace = itemView.findViewById(R.id.tvPlace);
        tvPlaceDetail = itemView.findViewById(R.id.tvPlaceDetail);
        this.onItemClickListener = onItemClickListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(currentData);
                }
            }
        });
    }

    public void bind(AutocompletePrediction autocompletePrediction) {
        if (autocompletePrediction != null) {
            currentData = autocompletePrediction;
            tvPlace.setText(autocompletePrediction.getPrimaryText(null));
            tvPlaceDetail.setText(autocompletePrediction.getSecondaryText(null));
        }
    }

    private void setOnItemClickListener(View itemView, Object currentItem) {

    }
}
