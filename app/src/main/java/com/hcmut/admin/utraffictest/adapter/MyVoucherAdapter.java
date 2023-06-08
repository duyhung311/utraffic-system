package com.hcmut.admin.utraffictest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.repository.remote.model.response.MyVoucherResponse;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyVoucherAdapter extends RecyclerView.Adapter<MyVoucherAdapter.MyVoucherViewHolder>{
    private Context mContext;
    private List<MyVoucherResponse> orderList;
    private MyVoucherResponse currentOrder;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private MyVoucherAdapter.OrderAdapterOnClickHandler clickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface OrderAdapterOnClickHandler {
        void onClick(MyVoucherResponse order);
    }

    public MyVoucherAdapter(Context mContext, List<MyVoucherResponse> orderList, MyVoucherAdapter.OrderAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        this.orderList = orderList;
        this.clickHandler = clickHandler;
    }





    @NonNull
    @Override
    public MyVoucherAdapter.MyVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View voucherView =
                inflater.inflate(R.layout.list_my_voucher, parent, false);

        MyVoucherAdapter.MyVoucherViewHolder viewHolder = new MyVoucherAdapter.MyVoucherViewHolder(voucherView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVoucherAdapter.MyVoucherViewHolder holder, int position) {
        currentOrder = orderList.get(position);

//        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        String formattedPrice = formatter.format(currentOrder.getProductPrice());
        holder.nameMyVoucher.setText(currentOrder.getVoucherResponse().getName());
        holder.priceMyVoucher.setText(currentOrder.getVoucherResponse().getValue() + " điểm");


        holder.dateMyVoucher.setText(formatter.format(currentOrder.getDate()));

    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        }
        return orderList.size();
    }

    class MyVoucherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Create view instances
        public TextView nameMyVoucher;
        public TextView priceMyVoucher;
        public TextView dateMyVoucher;



        public MyVoucherViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameMyVoucher = itemView.findViewById(R.id.nameMyVoucher);
            priceMyVoucher = itemView.findViewById(R.id.priceMyVoucher);
            dateMyVoucher = itemView.findViewById(R.id.dateMyvoucher);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Get position of the product
            currentOrder = orderList.get(position);
            // Send product through click
            clickHandler.onClick(currentOrder);
        }
    }
}
