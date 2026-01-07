package com.za.toptitup.loginlibrary;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.ItemSPI;

public class HomeAdapterMainQuick extends RecyclerView.Adapter<HomeAdapterMainQuick.ViewHolder> {

    private final ArrayList<ItemSPI> mValues;
    private final Context mContext;
    protected ItemListener mListener;

    public HomeAdapterMainQuick(Context context, ArrayList<ItemSPI> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private TextView textView1;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        private ItemSPI item;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
          //  textView1 = (TextView) v.findViewById(R.id.textView1);
            imageView = v.findViewById(R.id.imageView);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }

        public void setData(ItemSPI item) {
            this.item = item;
            String deno =item.getDeno();
            Log.e("quick","voucher...ids....."+item.pos);
            Log.e("quick","voucher...deno....."+item.deno);

            try {
                String splitDeno;
                if(deno.contains(".")){
                     splitDeno = deno.substring(0, deno.indexOf('.'));

                }else{
                     splitDeno = deno;
                }

                textView.setText("R " + splitDeno);
            }
            catch (Exception e){

            }
           // textView1.setText(item.pos);
            imageView.setImageResource(item.drawable);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_main_quick, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(ItemSPI item);

        void onItemClick(Item item);
    }
}