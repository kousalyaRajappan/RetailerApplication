package com.za.toptitup.loginlibrary.adapters;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.R;
import com.za.toptitup.loginlibrary.model.Item;

public class AutoScrollAdapter extends RecyclerView.Adapter<AutoScrollAdapter.ViewHolder> {
    private final HorizantalrecyclerListener mListener;
    private final ArrayList<Item> mValues;
    private final Context mContext;

    View view;

    public AutoScrollAdapter(Context context, ArrayList<Item> values, HorizantalrecyclerListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(mContext).inflate(R.layout.row_item_scroll, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(mValues.get(i).text);
        // textView1.setText(item.pos);
        viewHolder.imageView.setImageResource(mValues.get(i).drawable);

        viewHolder.relativeLayout.setBackgroundColor(Color.parseColor(mValues.get(i).color));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface HorizantalrecyclerListener {
        void onItemClickHorizantal(Item item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        CardView cardView;
        private TextView textView1;
        private Item item;

        public ViewHolder(@NonNull View v) {
            super(v);
            v.setOnClickListener(this);

            textView = v.findViewById(R.id.textView);
            // textView1 = (TextView) v.findViewById(R.id.textView1);
            imageView = v.findViewById(R.id.imageView);
            cardView = v.findViewById(R.id.cardView);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClickHorizantal(item);
            }
        }

    }
}
