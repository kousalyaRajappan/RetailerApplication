package com.za.toptitup.loginlibrary;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.model.Item;

public class HomeAdapterMain extends RecyclerView.Adapter<HomeAdapterMain.ViewHolder> {

    private final ArrayList<Item> mValues;
    private final Context mContext;
    protected ItemListener mListener;

    public HomeAdapterMain(Context context, ArrayList<Item> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private TextView textView1;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        private Item item;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
            // textView1 = (TextView) v.findViewById(R.id.textView1);
            imageView = v.findViewById(R.id.imageView);
//            cardView = v.findViewById(R.id.cardView);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }

        public void setData(Item item, int size, int pos) {
            this.item = item;
            textView.setText(item.text);
            // textView1.setText(item.pos);
        /*    Glide
                    .with(mContext)
                    .load(item.drawable)
                    .into(imageView);*/
            if (item.text.equalsIgnoreCase("busticket")) {
                Glide
                        .with(mContext)
                        .load(item.drawable)
                        .into(imageView);
            } else {
                imageView.setImageResource(item.drawable);
            }

              relativeLayout.setBackgroundColor(Color.parseColor(item.color));
         /*   if(pos ==size-1 || pos == size-2 || pos == size-3){

//                relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.green));
               *//* FrameLayout.LayoutParmas params=new FrameLayout.LayoutParmas(30, 30);//FrameLayout is a parentcreated by anroid frame work
                relativeLayout.setLayoutParmas(params);  ////Here Relative layout is child
*//*

             *//*   ViewGroup.LayoutParams params1 =new FrameLayout.LayoutParams(relativeLayout.getWidth(),relativeLayout.getHeight());

                relativeLayout.setLayoutParams(params1);*//*
//                relativeLayout.requestLayout();
//                relativeLayout.setLayoutParams(layoutParams);
                imageView.setImageResource(item.drawable);

            }else {
                imageView.setImageResource(item.drawable);

                relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }*/
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position), mValues.size(), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Item item);
    }
}