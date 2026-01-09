package com.za.toptitup.loginlibrary.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

import java.util.ArrayList;

import com.za.toptitup.loginlibrary.R;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private final Context context;

    public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object  getItem(int pos) {


        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.prov_item_layout, null);
        }

        //Handle TextView and display string from your list
  //      TextView tvContact= (TextView)view.findViewById(R.id.tvContact);
//        tvContact.setText(list.get(position));


        //Handle buttons and add onClickListeners
        Button callbtn = view.findViewById(R.id.btn);

        callbtn.setText(list.get(position));
        callbtn.setTag("test");

//        callbtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//
//            }
//        });

        return view;
    }
}