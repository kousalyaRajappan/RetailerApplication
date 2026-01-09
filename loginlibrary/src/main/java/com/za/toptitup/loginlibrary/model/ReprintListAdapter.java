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

import com.za.toptitup.loginlibrary.R;

public class ReprintListAdapter extends RecyclerView.Adapter<ReprintListAdapter.ViewHolder> {
    private final List<MyItem> mItems;
    private final Context          mContext;
    private final LayoutInflater mInflater;

    public ReprintListAdapter (Context context, int resourceId, ArrayList<MyItem> items)
    {
//        super(context, resourceId);

        mContext = context;
        mItems = items;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            btn_description = itemView.findViewById(R.id.btn_description);
            btn_date = itemView.findViewById(R.id.btn_date);
            btn_user = itemView.findViewById(R.id.btn_user);

        }

    }
}
