package com.za.toptitup.loginlibrary.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.Item;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.ViewHolder> {

    private final ArrayList mValues;
    private final Context mContext;
    protected ItemListener mListener;

    public MessagingAdapter(Context context, ArrayList values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txt_name;
        private TextView textView1;


        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            txt_name = v.findViewById(R.id.txt_name);

        }

        public void setData(String item,int pos) {
            txt_name.setText(item);
           /* this.item = item;
            textView.setText(item.text);
            textView1.setText(item.bdis);
            imageView.setImageResource(item.drawable);
            relativeLayout.setBackgroundColor(Color.parseColor(item.color));*/
        }

        @Override
        public void onClick(View view) {
           /* if (mListener != null) {
                mListener.onItemClick(item);
            }*/
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position).toString(),position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Item item);
    }
}