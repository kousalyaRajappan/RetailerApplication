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

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.model.ItemCashmx;

public class HomeAdapterCashmx extends RecyclerView.Adapter<HomeAdapterCashmx.ViewHolder> {

    private final ArrayList<ItemCashmx> mValues;
    private final Context mContext;
    protected ItemListener mListener;

    public HomeAdapterCashmx(Context context, ArrayList<ItemCashmx> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private final TextView textView1;
        private final TextView textView2;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        private ItemCashmx item;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
          textView1 = v.findViewById(R.id.textView1);
            textView2 = v.findViewById(R.id.textView2);
            imageView = v.findViewById(R.id.imageView);

           // imageView.setImageURI(path);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }

        public void setData(ItemCashmx item) {
            this.item = item;
            textView.setText(item.text);
           textView1.setText(item.bdis);
           if(item.activate.equals("1")){
               textView2.setText("ACTIVATION PENDING");
           }else{
               textView2.setVisibility(View.GONE);
               textView2.setText("");
           }

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_cashmx, parent, false);
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
        void onItemClick(ItemCashmx item);
    }
}