package com.za.toptitup.loginlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;

import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.pos_users;

public class UserListAdapter extends RealmBaseAdapter<pos_users> implements ListAdapter {


    private static class ViewHolder {
        TextView btn_description;
        TextView btn_provider;
        TextView btn_spi_type;
        ImageButton btn_multiple;
    }

//        private boolean inDeletionMode = false;
//        private Set<Integer> countersToDelete = new HashSet<Integer>();

    public UserListAdapter(OrderedRealmCollection<pos_users> realmResults) {

        super(realmResults);
    }

//        void enableDeletionMode(boolean enabled) {
//            inDeletionMode = enabled;
//            if (!enabled) {
//                countersToDelete.clear();
//            }
//            notifyDataSetChanged();
//        }

//        Set<Integer> getCountersToDelete() {
//            return countersToDelete;
//        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spi, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.btn_description = convertView.findViewById(R.id.btn_description);
            viewHolder.btn_provider = convertView.findViewById(R.id.btn_provider);
            viewHolder.btn_spi_type = convertView.findViewById(R.id.btn_spi_type);
            //viewHolder.btn_multiple = (ImageButton) convertView.findViewById(R.id.btn_multiple);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {

            final pos_users item = adapterData.get(position);

            viewHolder.btn_provider.setText(item.posuser_firstname);
            viewHolder.btn_description.setText(item.posuser_surname);

            viewHolder.btn_spi_type.setText("Cashier");
            if (item.posuser_isadmin == 1) viewHolder.btn_spi_type.setText("Admin");


        }
        return convertView;
    }
}


