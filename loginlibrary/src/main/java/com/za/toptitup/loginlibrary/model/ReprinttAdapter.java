package com.za.toptitup.loginlibrary.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.za.toptitup.loginlibrary.R;

public class ReprinttAdapter extends RecyclerView.Adapter<ReprinttAdapter.ViewHolder> {
    private final List<MyItem> mItems;
    private final Context          mContext;
    private final LayoutInflater mInflater;
    private final OnItemClickMain onItemClickMain;


    public ReprinttAdapter(Context context, int resourceId, ArrayList<MyItem> items, OnItemClickMain onItemClickMain)
    {
//        super(context, resourceId);

        mContext = context;
        mItems = items;
        this.onItemClickMain = onItemClickMain;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_single, viewGroup, false));    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int i) {
        MyItem item = mItems.get(i);
        if (item != null)
        {

            holder.btn_user.setText(item.btn_user);
            holder.btn_date.setText(item.btn_date);

            String fileName = StringUtils.substringAfterLast(item.btn_description, " R ");
            String fileName1 = StringUtils.substringBeforeLast(item.btn_description, " R ");
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                holder.btn_description.setText(fileName1);  //+ " - " + String.valueOf(item.stock_uid)

                holder.btn_value.setText(" R " + fileName);
            }else {
                holder.btn_description.setText(item.btn_description);  //+ " - " + String.valueOf(item.stock_uid)

            }
            Log.e("filename...... "+fileName1,"....."+fileName);

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickMain.onItemClickedMain(i);
                }
            });

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
        TextView    btn_user,btn_value;
        LinearLayout lyt_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            btn_description = itemView.findViewById(R.id.btn_description);
            btn_date = itemView.findViewById(R.id.btn_date);
            btn_user = itemView.findViewById(R.id.btn_user);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
            btn_value = itemView.findViewById(R.id.btn_value);

        }

    }
    public interface OnItemClickMain{
        //  void onItemClickAllIndia(int position,String carid,String name);
        void onItemClickedMain(int  position);
    }
}
