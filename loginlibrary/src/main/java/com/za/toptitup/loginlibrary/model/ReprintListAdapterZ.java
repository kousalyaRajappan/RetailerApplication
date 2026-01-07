package com.za.toptitup.loginlibrary.model;

import android.content.Context;

import java.util.ArrayList;

import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import za.co.topitup.R;

public class ReprintListAdapterZ extends RecyclerView.Adapter<ReprintListAdapterZ.ViewHolder> {
    private final OnItemClickMain onItemClick;

    private final List<MyItem> mItems;
    private final Context          mContext;
    private final LayoutInflater mInflater;

    public ReprintListAdapterZ (Context context, int resourceId, ArrayList<MyItem> items,OnItemClickMain onItemClickMain)
    {
//        super(context, resourceId);

        mContext = context;
        mItems = items;
        this.onItemClick = onItemClickMain;

        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_single, viewGroup, false));    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        MyItem item = mItems.get(i);
        if (item != null)
        {
            // This is where you set up the views.
            // This is just an example of what you could do.



            holder.btn_description.setText(item.btn_description);  //+ " - " + String.valueOf(item.stock_uid)
            holder.btn_user.setText(item.btn_user);
            holder.btn_date.setText(item.btn_date);


            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClickedMain(item.stock_uid);

                }
            });
            holder.btn_user.setTypeface(holder.btn_user.getTypeface(), Typeface.BOLD);
//            holder.btn_user.setTextSize(mContext.getResources().getDimension(R.dimen.four_dp));
            holder.btn_user.setTextColor(mContext.getResources().getColor(R.color.red));
            //holder.video.setMediaController(new MediaController(mContext));
            //holder.button.setImageDrawable(item.getImageDrawable());
//            holder.button.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View view)
//                        {
//                            holder.video.setVideoURI(item.getURI());
//                            holder.video.start();
//                        }
//                    }
//            );
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView btn_description;
        TextView    btn_date;
        TextView    btn_user;
        LinearLayout lyt_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            lyt_parent = itemView.findViewById(R.id.lyt_parent);
            btn_description = itemView.findViewById(R.id.btn_description);
            btn_date = itemView.findViewById(R.id.btn_date);
            btn_user = itemView.findViewById(R.id.btn_user);

        }

    }

    public interface OnItemClickMain{
        //  void onItemClickAllIndia(int position,String carid,String name);
        void onItemClickedMain(String sID);
    }
}
