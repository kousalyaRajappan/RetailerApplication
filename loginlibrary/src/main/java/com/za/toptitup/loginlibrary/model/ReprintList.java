package com.za.toptitup.loginlibrary.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import za.co.topitup.R;

public class ReprintList extends ArrayAdapter<MyItem> {

    private final List<MyItem>      mItems;
    private final Context          mContext;
    private final LayoutInflater   mInflater;

    public ReprintList (Context context, int resourceId, ArrayList<MyItem> items)
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
            convertView = mInflater.inflate(R.layout.list_single, null);
            holder = new ViewHolder();

            holder.btn_description = convertView.findViewById(R.id.btn_description);
            holder.btn_date = convertView.findViewById(R.id.btn_date);
            holder.btn_user = convertView.findViewById(R.id.btn_user);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        MyItem item = getItem(position);
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

        return convertView;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position)
    {
        return mItems.get(position);
    }

    public class ViewHolder
    {

        TextView    btn_description;
        TextView    btn_date;
        TextView    btn_user;
        //VideoView   video;
        //ImageButton button;
    }
}
