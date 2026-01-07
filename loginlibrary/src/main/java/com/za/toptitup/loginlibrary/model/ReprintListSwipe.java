package com.za.toptitup.loginlibrary.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import za.co.topitup.R;

public class ReprintListSwipe extends ArrayAdapter<MyItemSwipe> {

    private final List<MyItemSwipe>      mItems;
    private final Context          mContext;
    private final LayoutInflater   mInflater;

    public ReprintListSwipe(Context context, int resourceId, ArrayList<MyItemSwipe> items)
    {
        super(context, resourceId);

        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_swipe, null);
            holder = new ViewHolder();

            holder.btn_description = convertView.findViewById(R.id.btn_description);
            holder.btn_date = convertView.findViewById(R.id.btn_date);
            holder.btn_user = convertView.findViewById(R.id.btn_user);
            holder.uid = convertView.findViewById(R.id.uid);
            holder.imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
            holder.txnamount = convertView.findViewById(R.id.btn_txnamount);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        MyItemSwipe item = getItem(position);
        if (item != null)
        {
            // This is where you set up the views.
            // This is just an example of what you could do.



            holder.btn_description.setText(item.btn_description);  //+ " - " + String.valueOf(item.stock_uid)
            holder.btn_user.setText(item.btn_user);
            holder.btn_date.setText(item.btn_date);
            holder.imageViewFlag.setImageResource(item.imageViewFlag);
            holder.txnamount.setText(item.txnamount);
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

        return convertView;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public MyItemSwipe getItem(int position)
    {
        return mItems.get(position);
    }

    public class ViewHolder
    {

        TextView    btn_description;
        TextView    btn_date;
        TextView    btn_user;
        TextView    uid;
        ImageView   imageViewFlag;
        TextView   txnamount;
        //VideoView   video;
        //ImageButton button;
    }
}
