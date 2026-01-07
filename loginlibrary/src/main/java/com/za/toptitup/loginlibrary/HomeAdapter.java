package com.za.toptitup.loginlibrary;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.za.toptitup.loginlibrary.model.Item;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final ArrayList<Item> mValues;
    private final Context mContext;
    protected ItemListener mListener;

    public HomeAdapter(Context context, ArrayList<Item> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private final TextView textView1;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        private Item item;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
          textView1 = v.findViewById(R.id.textView1);
            imageView = v.findViewById(R.id.imageView);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }

        public void setData(Item item,int pos) {
            this.item = item;
//            if(pos == 0){
//                textView.setVisibility(View.GONE);
//                textView1.setVisibility(View.GONE);
            /*}else{
                textView.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.VISIBLE);
            }*/


            if(item.text.contains("Update Catalogue")){
                textView.setVisibility(View.VISIBLE);
                textView.setText(item.text.replace("Update Catalogue", ""));

            }
           textView1.setText(item.bdis);
            imageView.setImageResource(item.drawable);
            relativeLayout.setBackgroundColor(Color.parseColor(item.color));
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Item item);
    }
}