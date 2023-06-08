package com.hcmut.admin.utraffictest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.repository.remote.model.response.DealResponse;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    private Context mContext;
    private List<DealResponse> listDeal;
    private DealResponse currentDeal;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
    private DealAdapter.DealAdapterOnClickHandler clickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface DealAdapterOnClickHandler {
        void onClick(DealResponse deal);
    }

    public DealAdapter(Context mContext, List<DealResponse> listDeal,DealAdapter.DealAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        this.listDeal = listDeal;
        this.clickHandler = clickHandler;
    }





    @NonNull
    @Override
    public DealAdapter.DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View dealView =
                inflater.inflate(R.layout.list_deal, parent, false);

        DealAdapter.DealViewHolder viewHolder = new DealAdapter.DealViewHolder(dealView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DealAdapter.DealViewHolder holder, int position) {
        currentDeal = listDeal.get(position);
        // nhận điểm
        if(currentDeal.getType().compareTo("get point")==0){
            holder.nameDeal.setText("+"+currentDeal.getPoint()+" điểm: "+ currentDeal.getContent());
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.getpoint);
        }
        //đổi điểm
        else if(currentDeal.getType().compareTo("get voucher")==0){
            if(SharedPrefUtils.getUser(mContext).getUserId().compareTo(currentDeal.getSender().getId())==0) {
                holder.nameDeal.setText("-" + currentDeal.getPoint() + " điểm: " + currentDeal.getContent());
            }
            else{
                holder.nameDeal.setText("+" + currentDeal.getPoint() + " điểm: " + currentDeal.getContent());
            }
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.covert);
        }
        //chuyển điểm
        else if(currentDeal.getType().compareTo("transfer point")==0){
            holder.nameDeal.setText("-"+currentDeal.getPoint()+" điểm: "+ currentDeal.getContent());
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.transfer);

        }
        //mua điểm
        else if(currentDeal.getType().compareTo("buy point")==0){
            holder.nameDeal.setText("+"+currentDeal.getPoint()+" điểm: "+ currentDeal.getContent());
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.wallet);

        }
        //sử dụng voucher
        else if(currentDeal.getType().compareTo("use voucher")==0){
            holder.nameDeal.setText( currentDeal.getContent());
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.coupon);

        }
        else if(currentDeal.getType().compareTo("get gift")==0){
            holder.nameDeal.setText( currentDeal.getContent());
            holder.timeDeal.setText(formatter.format(currentDeal.getCreatedAt()));
            holder.imageDeal.setImageResource(R.drawable.getpoint);

        }




    }

    @Override
    public int getItemCount() {
        if (listDeal == null) {
            return 0;
        }
        return listDeal.size();
    }

    class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Create view instances
        public TextView nameDeal;
        public TextView timeDeal;
        public ImageView imageDeal;



        public DealViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameDeal = itemView.findViewById(R.id.nameDeal);
            timeDeal = itemView.findViewById(R.id.timeDeal);
            imageDeal = itemView.findViewById(R.id.imageDeal);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Get position of the product
            currentDeal = listDeal.get(position);
            // Send product through click
            clickHandler.onClick(currentDeal);
        }
    }
}
