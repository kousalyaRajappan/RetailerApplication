package com.za.toptitup.loginlibrary.model;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import za.co.topitup.R;

public class RetailerPaymentAdapter extends RecyclerView.Adapter<RetailerPaymentAdapter.ViewHolder> {
    private final List<MyItemForRetailerPayment> mItems;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public RetailerPaymentAdapter (Context context, int resourceId, ArrayList<MyItemForRetailerPayment> items)
    {
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_recycler_single, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        MyItemForRetailerPayment item = mItems.get(i);
        if (item != null)
        {
            holder.btn_description.setText(item.acc_number);
            holder.btn_date.setText(item.date);
            holder.btn_amount.setText(item.amount);
            holder.btn_fee.setText(item.fee);
            holder.btn_ref.setText(item.name);

        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView btn_description;
        TextView    btn_amount;
        TextView    btn_date,btn_fee,btn_ref;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            btn_description = itemView.findViewById(R.id.btn_description);
            btn_amount = itemView.findViewById(R.id.btn_amount);
            btn_date = itemView.findViewById(R.id.btn_date);
            btn_fee = itemView.findViewById(R.id.btn_fee);
            btn_ref = itemView.findViewById(R.id.btn_ref);

        }

    }
}
