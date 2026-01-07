package com.za.toptitup.loginlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;

import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.service_provider_item;

public class SpiListAdapter extends RealmBaseAdapter<service_provider_item> implements ListAdapter {


        private static class ViewHolder {
            TextView btn_description;
            TextView btn_provider;
            TextView btn_spi_type;
            ImageButton btn_multiple;
        }

//        private boolean inDeletionMode = false;
//        private Set<Integer> countersToDelete = new HashSet<Integer>();

        public SpiListAdapter(OrderedRealmCollection<service_provider_item> realmResults) {

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

                final service_provider_item item = adapterData.get(position);

                viewHolder.btn_provider.setText(item.item_desc);
                viewHolder.btn_description.setText(item.item_btn_desc);

                viewHolder.btn_spi_type.setText("Airtime");
                if (item.item_type == 1 || item.item_type == 3) viewHolder.btn_spi_type.setText("Special");
                if (item.item_type == 2) viewHolder.btn_spi_type.setText("Data");

//                viewHolder.btn_multiple.setOnClickListener(new View.OnClickListener()   {
//                    public void onClick(View v)  {
//                        try {
//
//                            //Timber.i("PLUS CLICK : " + item.service_provider_item_id);
//
//                            Context context;
//                            context = v.getContext();
//
//                            ((activity_spi) context).show_multi_voucher(item.service_provider_item_id);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


            }
            return convertView;
        }
    }


