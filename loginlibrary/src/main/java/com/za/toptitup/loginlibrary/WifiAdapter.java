package com.za.toptitup.loginlibrary;


import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {
    WifiListener wifiListener;
    private List<ScanResult> wifiList;

    public WifiAdapter(List<ScanResult> wifiList, WifiListener wifiListener) {
        this.wifiList = wifiList;
        this.wifiListener = wifiListener;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, parent, false);
        return new WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        ScanResult wifi = wifiList.get(position);
        holder.ssidTextView.setText(wifi.SSID);
        holder.capabilitiesTextView.setText("Signal Strength: " + WifiSignalUtils.dBmToPercentage(wifi.level) + "%");
        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiListener.onWifiCallback(wifi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public void updateWifiList(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
        notifyDataSetChanged();
    }

    static class WifiViewHolder extends RecyclerView.ViewHolder {
        TextView ssidTextView;
        TextView capabilitiesTextView;
        Button btnConnect;

        WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            ssidTextView = itemView.findViewById(R.id.ssidTextView);
            capabilitiesTextView = itemView.findViewById(R.id.capabilitiesTextView);
            btnConnect = itemView.findViewById(R.id.connect);
        }
    }
}

