package com.za.toptitup.loginlibrary.adapters;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import com.za.toptitup.loginlibrary.R;
import com.za.toptitup.loginlibrary.model.MultiVoucherSelectedItems;

public class MultiVoucherAdapter extends RecyclerView.Adapter<MultiVoucherAdapter.ViewHolder> {
    private final ArrayList<MultiVoucherSelectedItems> mMultiValues;
    private final Context mContext;

    public MultiVoucherAdapter(Context context, ArrayList<MultiVoucherSelectedItems> selectedMulti) {
        mMultiValues = selectedMulti;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_multi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final MultiVoucherSelectedItems item = mMultiValues.get(i);

        viewHolder.txtValue.setText(item.getText());
        Log.e("item position","position......"+item.pos);
        viewHolder.txtName.setText(selectedAirtime(item.getServiceProviderId()));

    }

    @Override
    public int getItemCount() {
        return mMultiValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtValue;
        private final TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtValue = itemView.findViewById(R.id.txt_Value_multi_row);
            txtName = itemView.findViewById(R.id.txt_name_multi_row);

        }
    }
    public String selectedAirtime(int service_provider_id){
        String item_name_input = "";
        switch (service_provider_id) {

            case 1:
//                providerIdInput = 485;
                item_name_input = "Vodacom";
//                drawable = getResources().getDrawable(R.drawable.prov1);
                break;

            case 3:
//                providerIdInput = 487;
                item_name_input = "Cell C";

//                drawable = getResources().getDrawable(R.drawable.prov3);
                break;
            case 10:
//                providerIdInput = 488;
                item_name_input = "Telkom";

//                drawable = getResources().getDrawable(R.drawable.prov10);

                break;
            case 29:
//                providerIdInput = 490;
                item_name_input = "Blue Voucher";

//                drawable = getResources().getDrawable(R.drawable.prov_bluvoucher);

                break;
            case 19:
//                providerIdInput = 489;
                item_name_input = "Lyca";

//                drawable = getResources().getDrawable(R.drawable.prov_lyca);
                break;
            case 30:
//                providerIdInput = 491;
                item_name_input = "Ringas";

//                drawable = getResources().getDrawable(R.drawable.prov_ringas);
                break;

            case 55:
//                providerIdInput = 491;
                item_name_input = "Easy Airtime";

//                drawable = getResources().getDrawable(R.drawable.prov_air);
                break;
            case 2:
//                providerIdInput = 486;
                item_name_input = "MTN";

//                drawable = getResources().getDrawable(R.drawable.prov2);
                break;


            case 25:
//                providerIdInput = 500;
                item_name_input = "OTT";
//                drawable = getResources().getDrawable(R.drawable.prov_ott);
                break;

        }
        return item_name_input;
    }

}
