package com.hcmut.admin.utrafficsystem.ui.reportdetail.traffic.ratinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponse;

import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingViewHolder> {

    private List<ReportResponse> data;
    private OnItemClickListener itemClickListener;
    private OnItemClickedListener itemClickedListener;
    private Context mContext;

    public RatingAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<ReportResponse> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ReportResponse getItem(int position) {
        try {
            return data.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearData() {
        if (data != null) {
            this.data.clear();
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view, itemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RatingViewHolder holder, final int position) {
        ReportResponse reportData = data.get(position);
        holder.bindData(reportData);
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    @Deprecated
    public interface OnItemClickListener {
        void onItemClick(int id, View view, int position, ArrayList<String> images);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(int viewId, View view, ReportResponse reportData);
    }

    public void setOnItemClickedListener(OnItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }
}